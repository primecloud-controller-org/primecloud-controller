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
from iaasgw.client.vCloudiaasclient import VCloudIaasClient
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.module.vcloud.vCloudModule import PccVMNetwork, VCloudMetadataSet
from iaasgw.utils.propertyUtil import getImage, getScriptProperty, \
    getDnsProperty, getPuppetProperty, getVpnProperty, getOtherProperty
from iaasgw.utils.stringUtils import isNotEmpty
from sqlalchemy.sql.expression import and_

class VCloudInstanceController(object):

    client = None
    conn = None
    logger = IaasLogger()
    platforminfo = None

    def __init__(self, platforminfo, vciaasclient, conn):
        self.client = vciaasclient
        self.conn = conn
        self.platforminfo = platforminfo

    def startInstance(self, instanceNo):
        #AWS_INSTANCE 取得
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.INSTANCE_NO==instanceNo))

        #PCC_INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))

        #FARM 取得
        tableFarm = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==pccInstance["FARM_NO"]))

        #組織
        vdc = self.client.getUseVdc()
        #マイクラウド
        vApp = self.client.describeMyCloud(vdc, farm["FARM_NAME"])

        #イメージの取得  再考の余地あり
        image = getImage(pccInstance["IMAGE_NO"])

        #既存VM検索
        vm = self.client.describeInstance(vApp, vcInstance["VM_NAME"])

        #VMが既に存在する場合はStart なければClone
        if vm is None:
            #インスタンスの作成
            vm = self.cloneVM(vdc, vApp, image, vcInstance, pccInstance)

            #最新情報を取得
            vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.INSTANCE_NO==instanceNo))

            #インスタンスの起動
            self.start(vdc, vApp, vm, vcInstance, pccInstance)

            #winodowsなら
            #if (startsWithIgnoreCase(image["os"], "windows")):
            #    self.client.getPasswordData(vcInstance["INSTANCE_ID"])

        else:
            # インスタンスが停止中でない場合はスキップ
            if (vcInstance["STATUS"] != VCloudIaasClient.STOPPED):
                return;

            # インスタンスの起動
            self.start(vdc, vApp, vm, vcInstance, pccInstance)


    def stopInstance(self, instanceNo):
        #AWS_INSTANCE 取得
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.INSTANCE_NO==instanceNo))
        #PCC_INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        #FARM 取得
        tableFarm = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==pccInstance["FARM_NO"]))

        #組織
        vdc = self.client.getUseVdc()
        #マイクラウド
        vApp = self.client.describeMyCloud(vdc, farm["FARM_NAME"])
        #既存VM検索
        vm = self.client.describeInstance(vApp, vcInstance["VM_NAME"])
        # インスタンスが存在しない場合は何もしない
        if vm is None:
            return;

        # インスタンスの停止
        self.stop(vApp, vm, vcInstance, pccInstance)



####################################################################################
    def cloneVM(self, vdc, vApp, image, vcInstance, pccInstance):

        #イベントログ出力
        platformTable = self.conn.getTable("PLATFORM")
        platform = self.conn.selectOne(platformTable.select(platformTable.c.PLATFORM_NO == pccInstance["PLATFORM_NO"]))
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"], pccInstance["INSTANCE_NAME"],
                        "VCloudInstanceCreate",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])

        tableIN = self.conn.getTable("VCLOUD_INSTANCE_NETWORK")
        instanceNW = self.conn.select(tableIN.select(tableIN.c.INSTANCE_NO==vcInstance["INSTANCE_NO"]))

        tableStorage = self.conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
        storage = self.conn.selectOne(tableStorage.select(tableStorage.c.STORAGE_TYPE_NO==vcInstance["STORAGE_TYPE_NO"]))

        #イメージリンク
        imageHarf = self.client.describeImageHref(image["templateName"])

        #VAPPネットワークのアップデート確認
        self.client.checkNetwork(vApp, instanceNW)
        #ネットワーク設定 INDEX「0」にはPCCネットワークが入る為ダミーを設定
        useNetworks = [PccVMNetwork("","","", 0,0),]
        for net in instanceNW:
            #PCCネットワーク
            if  getOtherProperty("vCloud.PCCNetwork") == net["NETWORK_NAME"]:
                useNetworks[0] = PccVMNetwork(net["NETWORK_NAME"], net["IP_ADDRESS"], net["IP_MODE"], 0, net["IS_PRIMARY"])
            else:
                useNetworks.append(PccVMNetwork(net["NETWORK_NAME"], net["IP_ADDRESS"], net["IP_MODE"], net["NETWORK_INDEX"], net["IS_PRIMARY"]))

        #ストレージ
        storageProfile = self.client.describeStorageProfile(vdc, storage["STORAGE_TYPE_NAME"])

        #パラメータ作成
        info = {"image":imageHarf,
                "vm_name":pccInstance["INSTANCE_NAME"],
                "vm_storage":storageProfile,
                "vm_networks":useNetworks,
                "fqdn":pccInstance["FQDN"]}

        #仮想マシンを作成
        node = self.client.createInstances(vApp, **info);
        vm = self.client.describeInstance(node, pccInstance["INSTANCE_NAME"])
        self.logger.info(vm)
        #イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"], pccInstance["INSTANCE_NAME"],
                        "VCloudInstanceCreateFinish",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])

        #データベース更新
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        updateDict = self.conn.selectOne(tableVCINS.select(tableVCINS.c.INSTANCE_NO==pccInstance["INSTANCE_NO"]))
        updateDict["VM_NAME"] = vm["name"]
        updateDict["STATUS"] = vm["state"]
        publicIp = vm["public_ips"]
        privateIp = vm["private_ips"]
        if len(publicIp) > 0:
            updateDict["IP_ADDRESS"] = publicIp[0]
        if len(privateIp) > 0:
            updateDict["PRIVATE_IP_ADDRESS"] = privateIp[0]
        else :
            updateDict["PRIVATE_IP_ADDRESS"] = updateDict["IP_ADDRESS"]

        sql = tableVCINS.update(tableVCINS.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)

        # TODO ここでネットーワークインデックスを更新
        self.reflectVMNetwork(vm, pccInstance["INSTANCE_NO"])

        return vm

    def start(self, vdc, vApp, vm, vcInstance, pccInstance):
        #プラットフォーム
        platformTable = self.conn.getTable("PLATFORM")
        platform = self.conn.selectOne(platformTable.select(platformTable.c.PLATFORM_NO == pccInstance["PLATFORM_NO"]))

        tableIN = self.conn.getTable("VCLOUD_INSTANCE_NETWORK")
        instanceNw = self.conn.select(tableIN.select(tableIN.c.INSTANCE_NO==vcInstance["INSTANCE_NO"]))

        tableStorage = self.conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
        storage = self.conn.selectOne(tableStorage.select(tableStorage.c.STORAGE_TYPE_NO==vcInstance["STORAGE_TYPE_NO"]))

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"], pccInstance["INSTANCE_NAME"],
                        "VCloudInstanceStart",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])

        #インスタンスタイプの取得
        tableType = self.conn.getTable("PLATFORM_VCLOUD_INSTANCE_TYPE")
        instype = self.conn.selectOne(
                                tableType.select(
                                    and_(tableType.c.INSTANCE_TYPE_NAME==vcInstance["INSTANCE_TYPE"],
                                         tableType.c.PLATFORM_NO==pccInstance["PLATFORM_NO"])
                                )
                    )
        #現状を取得
        setCpu = self.client.describeCPU(vm)
        setMemory = self.client.describeMemory(vm)

        #変更が有れば通知
        if str(instype["CPU"]) != str(setCpu):
            self.client._change_vm_cpu(vm["id"], instype["CPU"])
        if str(instype["MEMORY"]) != str(setMemory):
            self.client._change_vm_memory(vm["id"], instype["MEMORY"])

        #ストレージ変更
        if storage["STORAGE_TYPE_NAME"] != vm["storageprofile"]:
            #変更後のストレージプロファイル
            storageProfile = self.client.describeStorageProfile(vdc, storage["STORAGE_TYPE_NAME"])
            self.client.editInstance(vm, **{"storageProfile":storageProfile})

        #VAPPネットワークのアップデート確認
        self.client.checkNetwork(vApp, instanceNw)
        #IPアドレスの再設定
        self.client._change_vm_nw(vm["id"], instanceNw)

        #UserDataを作成
        userData = self.createUserData(pccInstance["INSTANCE_NO"], pccInstance, vcInstance)
        #PCCネットワークGWを設定
        vappnw = self.client.describeVappNetwork(vApp)
        for net in vappnw:
            if net.name == getOtherProperty("vCloud.PCCNetwork"):
                userData.update({"pccgateway": net.gateway})
                #ルーティングIP
                userData.update(self.createUserNetwork())

        #UserDataを整形
        userData = self.makeUserData(userData)

        #UserDataのキー とりあえず固定(VMWareと同一の物)
        key = "guestinfo.userdata"

        metadata = VCloudMetadataSet(key = key, value = userData)
        metadatas = [metadata,]
        #ユーザーデータ登録
        self.client.setProductSections(vm, metadatas)


        # VCLOUD上のステータスを確認インスタンスが停止中
        startvm = None
        if (vm["state"] == VCloudIaasClient.STOPPED):
            # インスタンスの起動
            node = self.client.startInstance(vApp, vm);
            startvm = self.client.describeInstance(node, vcInstance["VM_NAME"])
        else:
            #既にスタートしている
            startvm = vm

        #イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"], pccInstance["INSTANCE_NAME"],
                        "VCloudInstanceStartFinish",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])

        # データベース更新
        table = self.conn.getTable("VCLOUD_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==pccInstance["INSTANCE_NO"]))
        updateDict["STATUS"] = startvm["state"]
        publicIp = startvm["public_ips"]
        privateIp = startvm["private_ips"]
        if len(publicIp) > 0:
            updateDict["IP_ADDRESS"] = publicIp[0]
        if len(privateIp) > 0:
            updateDict["PRIVATE_IP_ADDRESS"] = privateIp[0]
        else :
            updateDict["PRIVATE_IP_ADDRESS"] = updateDict["IP_ADDRESS"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)

        # TODO ここでネットーワークインデックス等を更新
        self.reflectVMNetwork(vm, pccInstance["INSTANCE_NO"])


    def stop(self, vApp, vm, vcInstance, pccInstance):
        #プラットフォーム
        platformTable = self.conn.getTable("PLATFORM")
        platform = self.conn.selectOne(platformTable.select(platformTable.c.PLATFORM_NO == pccInstance["PLATFORM_NO"]))

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"],
                        pccInstance["INSTANCE_NAME"], "VCloudInstanceStop",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])

        # VCLOUD上のステータスを確認インスタンスが実行中
        stopvm = None
        if (vm["state"] == VCloudIaasClient.RUNNING):
            # インスタンスの停止
            node = self.client.stopInstance(vApp, vm);
            stopvm = self.client.describeInstance(node, vcInstance["VM_NAME"])
        else:
            stopvm = vm


        if stopvm["state"] != VCloudIaasClient.STOPPED:
            # インスタンス停止失敗時
            raise IaasException("EPROCESS-000718", [vcInstance["VM_NAME"], stopvm["state"]])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"],
                        pccInstance["INSTANCE_NAME"], "VCloudInstanceStopFinish",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])

        # データベース更新
        table = self.conn.getTable("VCLOUD_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==pccInstance["INSTANCE_NO"]))
        updateDict["STATUS"] = stopvm["state"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def terminate(self, vm_name):
        #vCloud_INSTANCE 取得
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.VM_NAME==vm_name))
        #PCC_INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==vcInstance["INSTANCE_NO"]))
        #FARM 取得
        tableFarm = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==pccInstance["FARM_NO"]))
        #プラットフォーム
        platformTable = self.conn.getTable("PLATFORM")
        platform = self.conn.selectOne(platformTable.select(platformTable.c.PLATFORM_NO == pccInstance["PLATFORM_NO"]))

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"],
                        pccInstance["INSTANCE_NAME"], "VCloudInstanceDelete",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])

        #組織
        vdc = self.client.getUseVdc()
        #マイクラウド
        vApp = self.client.describeMyCloud(vdc, farm["FARM_NAME"])
        #既存VM検索
        vm = self.client.describeInstance(vApp, vcInstance["VM_NAME"])

        if vm is None:
            #存在しない場合はそのまま返す
            return

        # インスタンスの削除
        self.client.terminateInstance(vApp, vcInstance["VM_NAME"])

        # データベース更新は不要？

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, pccInstance["INSTANCE_NO"],
                        pccInstance["INSTANCE_NAME"], "VCloudInstanceDeleteFinish",[platform["PLATFORM_NAME"], pccInstance["INSTANCE_NAME"]])


    def reflectVMNetwork(self, vm, instanceNo):
        tableIN = self.conn.getTable("VCLOUD_INSTANCE_NETWORK")
        instanceNW = self.conn.select(tableIN.select(tableIN.c.INSTANCE_NO==instanceNo))

        vmNetworks = self.client.describeVMNetwork(vm)

        #既存ネットワークの更新
        for net in instanceNW:
            for vmNet in vmNetworks:
                if vmNet.name == net["NETWORK_NAME"] and vmNet.index == str(net["NETWORK_INDEX"]):
                    net["IP_MODE"] = vmNet.ipMode
                    net["IP_ADDRESS"] = vmNet.ipAddress
                    if vmNet.isPrimary:
                        net["IS_PRIMARY"] = 1
                    else:
                        net["IS_PRIMARY"] = 0
                    sql = tableIN.update(tableIN.c.NETWORK_NO ==net["NETWORK_NO"], values=net)
                    self.conn.execute(sql)
                    vmNetworks.remove(vmNet)
                    break;

        #新規ネットワークの更新 上記で残ったネットワークを使う
        for net in instanceNW:
            if net["NETWORK_INDEX"] is None:
                for vmNet in vmNetworks:
                    if vmNet.name == net["NETWORK_NAME"]:
                        net["IP_MODE"] = vmNet.ipMode
                        net["IP_ADDRESS"] = vmNet.ipAddress
                        net["NETWORK_INDEX"] = vmNet.index
                        if vmNet.isPrimary:
                            net["IS_PRIMARY"] = 1
                        else:
                            net["IS_PRIMARY"] = 0
                        sql = tableIN.update(tableIN.c.NETWORK_NO ==net["NETWORK_NO"], values=net)
                        self.conn.execute(sql)
                        vmNetworks.remove(vmNet)
                        break

    def createUserNetwork(self):
        # UserDataを作成
        userData = {}
        zabbixserver = getOtherProperty("zabbix.server")
        if (isNotEmpty(zabbixserver)):
            #Zabbixサーバー（ルーティング用）
            userData.update({"zabbixserver": zabbixserver})

        routeAddserver = getOtherProperty("vCloud.routeAddserver")
        if (isNotEmpty(routeAddserver)):
            #ユーザー指定ルーティングサーバ
            userData.update({"routeAddserver": routeAddserver})

        return userData;


    def createUserData(self, instanceNo, pccInstance, vcInstance):

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

        # SSHキー
        userData.update(self.createSshUserData(vcInstance))

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

    def createSshUserData(self, vcInstance):
        table = self.conn.getTable("VCLOUD_KEY_PAIR")
        sshkey = self.conn.selectOne(table.select(table.c.KEY_NO==vcInstance["KEY_PAIR_NO"]))
        # UserDataを作成
        userData = {}
        # ZIPパスワード
        userData.update({"sshpubkey": sshkey["KEY_PUBLIC"]})

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

