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
from iaasgw.module.ec2.ec2module import TagSet
from iaasgw.utils.propertyUtil import getImage, getScriptProperty, getDnsProperty, getPuppetProperty, getVpnProperty
from iaasgw.utils.stringUtils import startsWithIgnoreCase, isNotEmpty, isEmpty
import time
import traceback


class ec2InstanceController(object):

    logger = IaasLogger()

    ERROR_RETRY_COUNT = 3

    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, ec2iaasclient, conn):
        self.client = ec2iaasclient
        self.conn = conn
        self.platforminfo = platforminfo

    def startInstance(self, instanceNo):
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))

        #PCC_INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))

        #イメージの取得  再考の余地あり
        image = getImage(pccInstance["IMAGE_NO"])

        # インスタンスイメージの場合や、EBSイメージでインスタンスIDがない場合
        if(image["ebsImage"] == "false" or isEmpty(awsInstance["INSTANCE_ID"])):

            #インスタンスIDがある場合はスキップ
            if (isEmpty(awsInstance["INSTANCE_ID"])==False):
                return;

            #インスタンスの作成
            self.run(instanceNo, awsInstance, pccInstance, image)

            #インスタンスの作成待ち
            self.waitRun(instanceNo, pccInstance)

            #インスタンスにタグをつける
            self.createTag(instanceNo, pccInstance)

            #winodowsなら
            if (startsWithIgnoreCase(image["os"], "windows")):
                self.waitGetPasswordData(instanceNo, awsInstance)

        # EBSイメージでインスタンスIDがある場合
        else:
            # インスタンスが停止中でない場合はスキップ
            if (awsInstance["STATUS"] != "stopped"):
                return;
            # インスタンスの起動
            self.start(instanceNo, awsInstance, pccInstance)

            # インスタンスの起動待ち
            self.waitStart(instanceNo, pccInstance)


    def stopInstance(self, instanceNo):
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))

        # インスタンスIDがない場合はスキップ
        if (isEmpty(awsInstance["INSTANCE_ID"])):
            return;

        #PCC_INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))

        #イメージの取得  再考の余地あり
        image = getImage(pccInstance["IMAGE_NO"])

        # インスタンスイメージの場合
        if image["ebsImage"]=="false":
            try :
                # インスタンスの削除
                self.terminate(instanceNo, awsInstance, pccInstance)

                # インスタンスの削除待ち
                self.waitTerminate(instanceNo, pccInstance)

            except Exception, e:
                self.logger.error(traceback.format_exc())
                # 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
                self.logger.warn(e.massage);

                updateDict = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
                updateDict["INSTANCE_ID"] = None
                updateDict["STATUS"] = None
                updateDict["DNS_NAME"] = None
                updateDict["PRIVATE_DNS_NAME"] = None
                updateDict["IP_ADDRESS"] = None
                updateDict["PRIVATE_IP_ADDRESS"] = None
                sql = tableAWSINS.update(tableAWSINS.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
                self.conn.execute(sql)

        # EBSイメージの場合
        else :
            # インスタンスの停止
            self.stop(instanceNo, awsInstance, pccInstance)

            # インスタンスの停止待ち
            self.waitStop(instanceNo, pccInstance)



####################################################################################
#---------------------ローカル------------------------------------------------------
####################################################################################

    def run(self, instanceNo, awsInstance, pccInstance, image):
        #UserDataを作成
        userData = self.createUserData(instanceNo, pccInstance, awsInstance)
        userData = self.makeUserData(userData)

        self.logger.info("userData:"+userData)

        #サブネット
        subnet = None
        if (isNotEmpty(awsInstance["SUBNET_ID"])):
            subnet = self.client.describeSubnets(awsInstance["SUBNET_ID"])[0]

        groupmap = {}
        groups = self.client.describeSecurityGroups()
        for group in groups:
            #サブネット入力時はVPCIDが一致する物をマッピング
            if subnet is not None:
                if subnet.vpcId == group.vpcId:
                    groupmap.update({group.groupName:group.groupId})

        #SecurityGroup
        securityGroups = []
        if (isNotEmpty(awsInstance["SECURITY_GROUPS"])):
            securityGroups = awsInstance["SECURITY_GROUPS"].split(",")

        availabilityZone = None
        if (isNotEmpty(awsInstance["AVAILABILITY_ZONE"])):
            availabilityZone = awsInstance["AVAILABILITY_ZONE"]

        blockDeviceMappings = self.createBlockDeviceMappings(image["imageId"], awsInstance["INSTANCE_TYPE"])

        #イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceCreate",["EC2"])

        #インスタンスの作成
        instance2 = self.client.runInstances(groupmap = groupmap,
                                            imageId = image["imageId"],
                                            kernelId = image["kernelId"],
                                            ramdiskId = image["ramdiskId"],
                                            keyName = awsInstance["KEY_NAME"],
                                            instanceType =awsInstance["INSTANCE_TYPE"],
                                            securityGroup = securityGroups,
                                            location = availabilityZone,
                                            userData = userData,
                                            subnetId = awsInstance["SUBNET_ID"],
                                            privateIpAddress = awsInstance["PRIVATE_IP_ADDRESS"],
                                            blockDeviceMapping = blockDeviceMappings)

        #データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["INSTANCE_ID"] = instance2.id
        updateDict["STATUS"] = instance2.extra['status']
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


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

    def createBlockDeviceMappings(self, imageId, instanceType):
        # イメージの取得
        image = self.client.describeImage(imageId)
#        if not images:
#            return None

#        for tmpImage in images:
#            if imageId == tmpImage.id:
#                image = tmpImage
#                break

        # イメージ情報を取得できない場合はBlockDeviceMappingsを設定しない
        if not image:
            return None

        # EBSイメージでなければBlockDeviceMappingsを設定しない
        if "ebs" != image.extra["rootDeviceType"]:
            return None

        if "windows" == image.extra["platform"]:
            # Windowsの場合
            # 利用者の混乱を防ぐために、BlockDeviceMappingsを設定しない
            return self.createWindowsBlockDeviceMappings(instanceType)
        else:
            # Linuxの場合
            return self.createLinuxBlockDeviceMappings(instanceType)

    def createLinuxBlockDeviceMappings(self, instanceType):
        mappings = []

        # /dev/sda2
        types = ["m1.small", "c1.medium"]
        if instanceType in types:
            mapping ={"DeviceName":"/dev/sda2", "VirtualName":"ephemeral0"}
            mappings.append(mapping)

        # /dev/sdb
        types = ["m1.large", "m1.xlarge", "c1.xlarge", "cc1.4xlarge", "m2.xlarge", "m2.2xlarge", "m2.4xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"/dev/sdb","VirtualName":"ephemeral0"}
            mappings.append(mapping)

        # /dev/sdc
        types = ["m1.large", "m1.xlarge", "cc1.4xlarge", "c1.xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"/dev/sdc","VirtualName":"ephemeral1"}
            mappings.append(mapping)

        # /dev/sdd
        types = ["m1.xlarge", "c1.xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"/dev/sdd","VirtualName":"ephemeral2"}
            mappings.append(mapping)

        # /dev/sde
        types = ["m1.xlarge", "c1.xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"/dev/sde","VirtualName":"ephemeral3"}
            mappings.append(mapping)

        return mappings


    def createWindowsBlockDeviceMappings(self, instanceType):
        mappings = []

        # xvdb
        types = ["m1.small", "c1.medium"]
        if instanceType in types:
            mapping ={"DeviceName":"xvdb","VirtualName":"ephemeral0"}
            mappings.append(mapping)

        # xvdb
        types = ["m1.large", "m1.xlarge", "c1.xlarge", "m2.xlarge", "m2.2xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"xvdb","VirtualName":"ephemeral0"}
            mappings.append(mapping)

        # xvdc
        types = ["m1.large", "m1.xlarge", "c1.xlarge", "m2.4xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"xvdc","VirtualName":"ephemeral1"}
            mappings.append(mapping)

        # xvdd
        types = ["m1.xlarge", "c1.xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"xvdd","VirtualName":"ephemeral2"}
            mappings.append(mapping)

        # xvde
        types = ["m1.xlarge", "c1.xlarge"]
        if instanceType in types:
            mapping ={"DeviceName":"xvde","VirtualName":"ephemeral3"}
            mappings.append(mapping)

        return mappings



    def waitRun(self, instanceNo, pccInstance):
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        instanceId = awsInstance["INSTANCE_ID"]

        # インスタンスの作成待ち
        instance2 = self.waitInstance(instanceId);
        state = instance2.extra['status']

        if state != "running":
            # インスタンス作成失敗時
            raise IaasException("EPROCESS-000106", [instanceId, state])

        # ログ出力
        self.logger.info(None, "IPROCESS-100116", [instanceId,])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceCreateFinish",["EC2", instanceId])

        # データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["AVAILABILITY_ZONE"] = instance2.extra['availability']
        updateDict["STATUS"] = state
        updateDict["DNS_NAME"] = instance2.extra['dns_name']
        updateDict["PRIVATE_DNS_NAME"] = instance2.extra['private_dns']
        if len(instance2.public_ip) > 0:
            updateDict["IP_ADDRESS"] = instance2.public_ip[0]
        if len(instance2.private_ip) > 0:
            updateDict["PRIVATE_IP_ADDRESS"] = instance2.private_ip[0]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)

    def start(self, instanceNo, awsInstance, pccInstance):
        instanceId = awsInstance["INSTANCE_ID"]

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceStart",["EC2", instanceId])

        #インスタンスの確認
        node = self.client.describeInstance(instanceId);

        #インスタンスタイプの変更確認 変更されていればAWSへ通知
        if awsInstance["INSTANCE_TYPE"] != node.extra["instancetype"] :
            params = {'InstanceType': awsInstance["INSTANCE_TYPE"] }
            self.client.modifyInstanceAttribute(instanceId, **params)

        # インスタンスの起動
        change = self.client.startInstance(instanceId);

        # データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["STATUS"] = change["name"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def waitStart(self, instanceNo, pccInstance):
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        instanceId = awsInstance["INSTANCE_ID"]

        # インスタンスの起動待ち
        instance2 = self.waitInstance(instanceId);
        state = instance2.extra['status']

        if state != "running":
            # インスタンス作成失敗時
            raise IaasException("EPROCESS-000126", [instanceId, state])

        # ログ出力
        self.logger.info(None, "IPROCESS-100112", [instanceId,])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceStartFinish",["EC2", instanceId])

        # データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["AVAILABILITY_ZONE"] = instance2.extra['availability']
        updateDict["STATUS"] = state
        updateDict["DNS_NAME"] = instance2.extra['dns_name']
        updateDict["PRIVATE_DNS_NAME"] = instance2.extra['private_dns']
        if len(instance2.public_ip) > 0:
            updateDict["IP_ADDRESS"] = instance2.public_ip[0]
        if len(instance2.private_ip) > 0:
            updateDict["PRIVATE_IP_ADDRESS"] = instance2.private_ip[0]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def createTag(self, instanceNo, pccInstance):
        #INSTANCE 取得
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        #FARM 取得
        tableFARM = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFARM.select(tableFARM.c.FARM_NO==instance["FARM_NO"]))
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))


        # Eucalyptusの場合はタグを付けない
        platform = self.platforminfo["platformName"]
        if (platform == "eucalyptus"):
            return

        # Nameタグを追加する
        tags = []
        tags.append(TagSet(None, None, "Name", pccInstance["FQDN"]))
        tags.append(TagSet(None, None, "UserName", self.client.getUsername()))
        tags.append(TagSet(None, None, "CloudName", farm["DOMAIN_NAME"]))
        tags.append(TagSet(None, None, "ServerName", pccInstance["FQDN"]))

        self.client.createTags(awsInstance["INSTANCE_ID"], tags)

        # EBSイメージでなければ終わり
        image = getImage(pccInstance["IMAGE_NO"])
        if (isEmpty(image["ebsImage"])==False):
            return

        instance2 = self.client.describeInstance(awsInstance["INSTANCE_ID"])

        #この部分の情報をLibCloudが取得していない
        #for (InstanceBlockDeviceMapping blockDeviceMapping : instance2.getBlockDeviceMappings()) {
        #    if (blockDeviceMapping.getDeviceName().equals(instance2.getRootDeviceName())) {
        #        String volumeId = blockDeviceMapping.getEbs().getVolumeId();
        #
        #        // EBSイメージにNameタグを追加する
        #        awsProcessClient.createTag(volumeId, "Name", instance.getFqdn());
        #        break;

    def waitGetPasswordData(self, instanceNo, awsInstance):
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        instanceId = awsInstance["INSTANCE_ID"]
        passwordData = ""

        count = 0
        while (True):
            try:
                passwordData = self.client.getPasswordData(instanceId)
                break
            except Exception:
                #取得できるまで待つ
                time.sleep(60)

            count = count +1
            if count == 100:
                break
        return passwordData



    def terminate(self, instanceNo, awsInstance, pccInstance):
        instanceId = awsInstance["INSTANCE_ID"]

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceDelete",["EC2", instanceId])

        # インスタンスの停止
        change = self.client.terminateInstance(instanceId)

        # データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["STATUS"] = change["name"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def waitTerminate(self, instanceNo, pccInstance):
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        instanceId = awsInstance["INSTANCE_ID"]

        instance2 = None
        try:
            # インスタンスの停止待ち
            instance2 = self.waitInstance(instanceId)
            state = instance2.extra['status']

            if state != "terminated":
                # インスタンス削除失敗時
                raise IaasException("EPROCESS-000109", [instanceId, state])

        except IaasException, e:
            self.logger.error(traceback.format_exc())
            #インスタンス情報の取得失敗はTerminateにより存在が消えたと判断
            if e.getMassageid() == "EPROCESS-000101":
                instance2 = None
            else:
                raise

        # ログ出力
        self.logger.info(None, "IPROCESS-100118", [instanceId,])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceDeleteFinish",["EC2", instanceId])

        status = None
        if instance2 :
            status = instance2.extra['status']

        # データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["INSTANCE_ID"] = None
        updateDict["STATUS"] = status
        updateDict["DNS_NAME"] = None
        updateDict["PRIVATE_DNS_NAME"] = None
        updateDict["IP_ADDRESS"] = None
        #updateDict["PRIVATE_IP_ADDRESS"] = None
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def stop(self, instanceNo, awsInstance, pccInstance):
        instanceId = awsInstance["INSTANCE_ID"]

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceStop",["EC2", instanceId])

        # インスタンスの停止
        change = self.client.stopInstance(instanceId);

        # データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["STATUS"] = change["name"]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def waitStop(self, instanceNo, pccInstance):
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        instanceId = awsInstance["INSTANCE_ID"]

        # インスタンスの停止待ち
        instance2 = self.waitInstance(instanceId);
        state = instance2.extra['status']

        if state != "stopped":
            # インスタンス作成失敗時
            raise IaasException("EPROCESS-000129", [instanceId, state])

        # ログ出力
        self.logger.info(None, "IPROCESS-100114", [instanceId,])

        # イベントログ出力
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsInstanceStopFinish",["EC2", instanceId])

        # データベース更新
        table = self.conn.getTable("AWS_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["AVAILABILITY_ZONE"] = instance2.extra['availability']
        updateDict["STATUS"] = state
        updateDict["DNS_NAME"] = instance2.extra['dns_name']
        updateDict["PRIVATE_DNS_NAME"] = instance2.extra['private_dns']
        if len(instance2.public_ip) > 0:
            updateDict["IP_ADDRESS"] = instance2.public_ip[0]
        if len(instance2.private_ip) > 0:
            updateDict["PRIVATE_IP_ADDRESS"] = instance2.private_ip[0]
        sql = table.update(table.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def waitInstance(self, instanceId):
        # インスタンスの処理待ち
        instance = None
        retryCount = 0
        while (True):
            time.sleep(3)
            try:
                instance = self.client.describeInstance(instanceId)
            except Exception:
                #起動時にIDが行方不明になる事があるので3回までリトライする
                self.logger.info("===============RETRY ID:%s ==========================" %str(instanceId))
                if self.ERROR_RETRY_COUNT < retryCount:
                    raise
                else:
                    continue

            status = instance.extra['status']

            if status == "stopped" or status == "running" or status == "terminated":
                break

            if status != "stopping" and status != "pending" and status != "shutting-down":
                #予期しないステータス
                raise IaasException("EPROCESS-000104", [instanceId, status,])

        return instance


    def createUserData(self, instanceNo, pccInstance, awsInstance):

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




