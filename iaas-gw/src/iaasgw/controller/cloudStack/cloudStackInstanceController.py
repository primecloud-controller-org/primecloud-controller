 # coding: UTF-8
 #
 # Copyright 2014 by SCSK Corporation.
 # 
 # This file is part of PrimeCloud Controller(TM).
 # 
 # PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 2 of the License, or
 # (at your option) any later version.
 # 
 # PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 # 
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertyUtil import getImage, getScriptProperty, getDnsProperty, getPuppetProperty, getVpnProperty
from iaasgw.utils.stringUtils import isEmpty, isNotEmpty, startsWithIgnoreCase
class CloudStackInstanceController(object):

    client = None
    conn = None
    logger = IaasLogger()
    platforminfo = None

    def __init__(self, platforminfo, ec2iaasclient, conn):
        self.client = ec2iaasclient
        self.conn = conn
        self.platforminfo = platforminfo


    def startInstance(self, instanceNo):
        #AWS_INSTANCE 取得
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))

        #PCC_INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))

        #イメージの取得  再考の余地あり
        image = getImage(pccInstance["IMAGE_NO"])

        #
        if isEmpty(csInstance["INSTANCE_ID"]):
            #インスタンスの作成
            self.run(instanceNo, csInstance, pccInstance, image)

            #winodowsなら
            if (startsWithIgnoreCase(image["os"], "windows")):
                #INSTANCE_ID取得の為、CLOUDSTACK_INSTANCE 再取得
                csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))
                self.client.getPasswordData(csInstance["INSTANCE_ID"])

        else:
            # インスタンスが停止中でない場合はスキップ
            if (csInstance["STATE"] != "Stopped"):
                return;
            # インスタンスの起動
            self.start(instanceNo, csInstance, pccInstance)


    def stopInstance(self, instanceNo):
        #AWS_INSTANCE 取得
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))
        #PCC_INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))

        # インスタンスIDがない場合は確認する
        if (isEmpty(csInstance["INSTANCE_ID"])):
            #起動ミス対策
            nodes = self.client.describeInstances(name = pccInstance["INSTANCE_NAME"])
            if not nodes or len(nodes) == 0:
                #インスタンスが存在しない場合
                return;

            if len(nodes) >= 1:
                #FQDNを比較する
                for node in nodes:
                    if pccInstance["FQDN"] == node.extra["displayname"]:
                        #起動をミスったインスタンスを発見した場合
                        #ID情報を更新
                        csInstance["INSTANCE_ID"] = node.id
                        sql = tableCSINS.update(tableCSINS.c.INSTANCE_NO ==csInstance["INSTANCE_NO"], values=csInstance)
                        self.conn.execute(sql)

        # インスタンスの停止
        self.stop(instanceNo, csInstance, pccInstance)



####################################################################################
#---------------------ローカル------------------------------------------------------
####################################################################################

    def run(self, instanceNo, csInstance, pccInstance, image):

        #serviceoffering名称をIDへ変換
        serviceofferings = self.client.describeServiceOfferings()
        #デフォルトは最初にHitするID
        serviceofferingid = serviceofferings[0]["id"]
        for serviceoffering in serviceofferings:
            if csInstance["INSTANCE_TYPE"] == serviceoffering["name"]:
                serviceofferingid = serviceoffering["id"]


        availabilityZone = None
        if (isNotEmpty(csInstance["ZONEID"])):
            availabilityZone = csInstance["ZONEID"]

        #任意設定はここから  必要な分増やす
        extra_args = {}
        if (isNotEmpty(csInstance["NETWORKID"])):
            extra_args["network_id"] = csInstance["NETWORKID"]

        #SecurityGroup
        securityGroups = []
        if (isNotEmpty(csInstance["SECURITYGROUP"])):
            securityGroups.append(csInstance["SECURITYGROUP"].split(","))
            extra_args["securitygroupnames"] = securityGroups

        if (isNotEmpty(csInstance["KEY_NAME"])):
            extra_args["keypair"] = csInstance["KEY_NAME"]

        #UserDataを作成
        userData = self.createUserData(instanceNo, pccInstance, csInstance)
        userData = self.makeUserData(userData)
        extra_args["userdata"] = userData
        self.logger.info("userData:"+userData)


        #イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackInstanceCreate",["CLOUDSTACK"])

        #インスタンスの作成
        node = self.client.runInstances(pccInstance["INSTANCE_NAME"],
                                             pccInstance["FQDN"],
                                             serviceofferingid,
                                             image["templateId"],
                                             availabilityZone,
                                             **extra_args)



        if node["state"] != "Running":
            # インスタンス作成失敗時
            raise IaasException("EPROCESS-000716", [node["id"], node["state"]])

        # ログ出力
        self.logger.info(None, "IPROCESS-100603", [node["id"],])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackInstanceCreateFinish",["CLOUDSTACK", node["id"]])

        # データベース更新
        table = self.conn.getTable("CLOUDSTACK_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["INSTANCE_ID"] = node["id"]
        updateDict["ZONEID"] = node["zoneid"]
        updateDict["STATE"] = node["state"]
        updateDict["DISPLAYNAME"] = node["displayname"]
        updateDict["IPADDRESS"] = node["nic"][0]["ipaddress"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def start(self, instanceNo, csInstance, pccInstance):
        instanceId = csInstance["INSTANCE_ID"]

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackInstanceStart",["CLOUDSTACK", instanceId])

        #serviceoffering名称をIDへ変換
        serviceofferings = self.client.describeServiceOfferings()
        #デフォルトは最初にHitするID
        serviceofferingid = serviceofferings[0]["id"]
        for serviceoffering in serviceofferings:
            if csInstance["INSTANCE_TYPE"] == serviceoffering["name"]:
                serviceofferingid = serviceoffering["id"]

        #serviceofferingの変更有無を確認
        node = self.client.describeInstance(instanceId)
        if  node.extra["serviceofferingid"] != serviceofferingid:
            # serviceofferingの変更
            node = self.client.changeInstance(instanceId, serviceofferingid);

        # インスタンスの起動
        node = self.client.startInstance(instanceId);

        if  node["state"] != "Running":
            # インスタンス作成失敗時
            raise IaasException("EPROCESS-000716", [instanceId, node["state"]])

        # ログ出力
        self.logger.info(None, "IPROCESS-100601", [instanceId,])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackInstanceStartFinish",["CLOUDSTACK", instanceId])

        # データベース更新
        table = self.conn.getTable("CLOUDSTACK_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["ZONEID"] = node["zoneid"]
        updateDict["STATE"] = node["state"]
        updateDict["DISPLAYNAME"] = node["displayname"]
        updateDict["IPADDRESS"] = node["nic"][0]["ipaddress"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def stop(self, instanceNo, csInstance, pccInstance):
        instanceId = csInstance["INSTANCE_ID"]

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackInstanceStop",["CLOUDSTACK", instanceId])

        # インスタンスの停止
        node = self.client.stopInstance(instanceId);


        if node["state"] != "Stopped":
            # インスタンス作成失敗時
            raise IaasException("EPROCESS-000718", [instanceId, node["state"]])

        # ログ出力
        self.logger.info(None, "IPROCESS-100602", [instanceId,])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackInstanceStopFinish",["CLOUDSTACK", instanceId])

        # データベース更新
        table = self.conn.getTable("CLOUDSTACK_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["ZONEID"] = node["zoneid"]
        updateDict["STATE"] = node["state"]
        updateDict["DISPLAYNAME"] = node["displayname"]
        updateDict["IPADDRESS"] = node["nic"][0]["ipaddress"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)



    def terminate(self, instanceId):
        #CLOUDSTACK_INSTANCE 取得
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_ID==instanceId))

        if isEmpty(instanceId):
            #IDが指定されていない場合はそのまま返す
            return

        # インスタンスの停止
        node = self.client.terminateInstance(instanceId)

        # ログ出力
        self.logger.info(None, "IPROCESS-100604", [instanceId,])

        # データベース更新
        csInstance["ZONEID"] = None
        csInstance["STATE"] = node["state"]
        csInstance["DISPLAYNAME"] = None
        csInstance["IPADDRESS"] = None
        sql = tableCSINS.update(tableCSINS.c.INSTANCE_NO ==csInstance["INSTANCE_NO"], values=csInstance)
        self.conn.execute(sql)


    def createUserData(self, instanceNo, pccInstance, csInstance):

        table = self.conn.getTable("FARM")
        fram = self.conn.selectOne(table.select(table.c.FARM_NO==pccInstance["FARM_NO"]))

        #UserDataを作成
        userData = {}
        #DB情報
        userData.update({"instanceName": pccInstance["INSTANCE_NAME"]})
        userData.update({"farmName": fram["FARM_NAME"]})
        # FQDN
        userData.update({"hostname": pccInstance["FQDN"]})
        #初期スクリプト情報
        userData.update({"scriptserver": getScriptProperty("script.server")})

        #DNS情報
        userData.update(self.createDnsUserData(instanceNo))

        # Puppet情報
        userData.update(self.createPuppetUserData())

        # VPN情報
        internal = self.platforminfo["internal"]
        if (internal == 0):
            userData.update(self.createVpnUserData(pccInstance))

        return userData;

    def  createDnsUserData(self,instanceNo):
        # UserDataを作成
        userData = {}
        # Primary DNSサーバ
        userData.update({"dns": getDnsProperty("dns.server")})

        # Secondry DNSサーバ
        dns2 = getDnsProperty("dns.server2")
        if (isNotEmpty(dns2)):
            userData.update({"dns2": dns2})

        # DNSドメイン
        userData.update({"dnsdomain": getDnsProperty("dns.domain")})

        return userData;

    def createPuppetUserData(self):
        # UserDataを作成
        userData = {}
        # PuppetMaster情報
        userData.update({"puppetmaster": getPuppetProperty("puppet.masterHost")})
        return userData;

    def createVpnUserData(self, pccInstance):
        # UserDataを作成
        userData = {}

        #VPN情報のユーザとパスワードをセットする
        userData.update({"vpnuser": pccInstance["FQDN"]})
        userData.update({"vpnuserpass": pccInstance["INSTANCE_CODE"]})

        # VPNサーバ情報
        userData.update({"vpnserver": getVpnProperty("vpn.server")})
        userData.update({"vpnport": getVpnProperty("vpn.port")})
        # userData.update({"vpnuser": getVpnProperty("vpn.user")})
        # userData.update({"vpnuserpass": getVpnProperty("vpn.userpass")})

        # ZIPパスワード
        userData.update({"vpnzippass": getVpnProperty("vpn.zippass")})

        # クライアント証明書ダウンロードURL
        userData.update({"vpnclienturl": getVpnProperty("vpn.clienturl")})

        return userData;

    def makeUserData(self, map):
        if not map or len(map) == 0:
            return None

        userdata = ''
        for key in map.keys():
            value = map[key]
            if isNotEmpty(value):
                if userdata != '':
                    userdata = userdata + ';'

                userdata = userdata + key + "=" + value

        return userdata

