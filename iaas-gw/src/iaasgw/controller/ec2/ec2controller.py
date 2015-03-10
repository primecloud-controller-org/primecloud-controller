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


from iaasgw.client.ec2iaasclient import EC2IaasClient
from iaasgw.client.ec2iaasclientLB import EC2IaasClientLB
from iaasgw.controller.ec2.ec2AddressController import ec2AddressController
from iaasgw.controller.ec2.ec2InstanceController import ec2InstanceController
from iaasgw.controller.ec2.ec2LoadBalancercontroller import \
    ec2LoadBalancercontroller
from iaasgw.controller.ec2.ec2OtherController import ec2OtherController
from iaasgw.controller.ec2.ec2VolumController import ec2VolumController
from iaasgw.controller.iaascontroller import IaasController
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertyUtil import getImage
from iaasgw.utils.stringUtils import isNotEmpty, isBit
import traceback


class EC2Controller(IaasController):

    logger = IaasLogger()

    conn = None
    accessInfo = None

    client = None
    clientLb = None
    instancecontroller = None
    volumecontroller = None
    addresscontroller = None
    loadBalancercontroller = None
    othercontroller = None

    def __init__(self, conn, accessInfo, platforminfo, isLb = False):
        self.conn = conn
        self.accessInfo = accessInfo
        self.client = EC2IaasClient(platforminfo, accessInfo["USER_NAME"], accessInfo["ACCESS_ID"], accessInfo["SECRET_KEY"])

        #コントローラ作成
        self.instancecontroller = ec2InstanceController(platforminfo, self.client, self.conn)
        self.volumecontroller   = ec2VolumController(platforminfo, self.client, self.conn)
        self.addresscontroller  = ec2AddressController(platforminfo, self.client, self.conn)
        self.othercontroller    = ec2OtherController(platforminfo, self.client, self.conn)

        if isLb:
            #self.clientLb = ec2iaasclientLB(userInfo.getAccessId(), userInfo.getSecretKey)
            self.clientLb = EC2IaasClientLB(platforminfo, accessInfo["USER_NAME"], accessInfo["ACCESS_ID"], accessInfo["SECRET_KEY"])
            #コントローラ作成
            self.loadBalancercontroller = ec2LoadBalancercontroller(platforminfo, self.clientLb, self.conn)

    def __del__(self):
        self.conn.rollback()
        self.conn.close()

    def startInstance(self, instanceNo):

        # インスタンスに関する処理   TODO タイムアウトリトライは未実装
        try:
            self.instancecontroller.startInstance(instanceNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        # ボリュームに関する処理
        table = self.conn.getTable("AWS_VOLUME")
        volumes = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))
        for volume in volumes:
            if isNotEmpty(volume["COMPONENT_NO"]):
                # コンポーネント番号がある場合はスキップ
                continue
            #Volumeスタート
            self.volumecontroller.startVolume(instanceNo, volume["VOLUME_NO"])

        # アドレスに関する処理
        self.addresscontroller.startAddress(instanceNo)

        self.conn.commit()
        return "RESULT:TRUE"


    def stopInstance(self, instanceNo):

        try :
            # アドレスに関する処理
            self.addresscontroller.stopAddress(instanceNo)
        except Exception:
            self.logger.error(traceback.format_exc())

        try :
            # インスタンスに関する処理
            self.instancecontroller.stopInstance(instanceNo);
        except Exception:
            self.logger.error(traceback.format_exc())

        try :
            # ボリュームに関する処理
            tableAWSVOL = self.conn.getTable("AWS_VOLUME")
            volumes = self.conn.select(tableAWSVOL.select(tableAWSVOL.c.INSTANCE_NO==instanceNo))

            #PCC_INSTANCE 取得
            tableINS = self.conn.getTable("INSTANCE")
            pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))

            #イメージの取得  再考の余地あり
            image = getImage(pccInstance["IMAGE_NO"])

            for awsVolume in volumes:
                if (image["ebsImage"]=="true"):
                    self.volumecontroller.stopVolume(instanceNo, awsVolume["VOLUME_NO"])
                else:
                    if (isNotEmpty(awsVolume["VOLUME_ID"]) and isNotEmpty(awsVolume["INSTANCE_ID"])):
                        updateDict = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==awsVolume["VOLUME_NO"]))
                        updateDict["STATUS"] = None
                        updateDict["INSTANCE_ID"] = None
                        sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
                        self.conn.execute(sql)
        except Exception:
            self.logger.error(traceback.format_exc())

        self.conn.commit()
        return "RESULT:TRUE"


    def terminateInstance(self, instanceId):

        #1度も起動されていない
        if instanceId is None:
            return

        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_ID==instanceId))

        # インスタンスの停止
        change = self.client.terminateInstance(instanceId);

        # データベース更新
        awsInstance["STATUS"] = change["name"]
        sql = tableAWSINS.update(tableAWSINS.c.INSTANCE_NO ==awsInstance["INSTANCE_NO"], values=awsInstance)
        self.conn.execute(sql)

        self.conn.commit()
        return "RESULT:TRUE"

    def startVolume(self, instanceNo, volumeNo):
        self.volumecontroller.startVolume(instanceNo, volumeNo)
        self.conn.commit()
        return "RESULT:TRUE"

    def stopVolume(self, instanceNo, volumeNo):
        self.volumecontroller.stopVolume(instanceNo, volumeNo)
        self.conn.commit()
        return "RESULT:TRUE"

    def deleteVolume(self, volumeId):
        self.client.deleteVolume(volumeId)
        self.conn.commit()
        return "RESULT:TRUE"

    def startLoadBalancer(self, loadBalancerNo):
        tableLB = self.conn.getTable("LOAD_BALANCER")
        loadBalancer = self.conn.selectOne(tableLB.select(tableLB.c.LOAD_BALANCER_NO==loadBalancerNo))


        # ゾーン情報の取得
        zones = self.client.describeAvailabilityZones()

        # サブネットID
        subnets = self.client.describeSubnets()

        # セキュリティグループ
        groups = self.client.describeSecurityGroups()
        groupmap = {}
        for group in groups:
            if group.vpcId is not None:
                key = group.groupName+group.vpcId
                groupmap.update({key:group.groupId})

        # ロードバランサの作成
        self.loadBalancercontroller.createLoadBalancer(loadBalancer["FARM_NO"], loadBalancerNo, zones, subnets, groupmap)

        # DNSサーバへの追加    ここは未定
        #self.loadBalancercontroller.addDns(loadBalancerNo)
        self.conn.commit()
        return "RESULT:TRUE"

    def stopLoadBalancer(self, loadBalancerNo):
        tableLB = self.conn.getTable("LOAD_BALANCER")
        loadBalancer = self.conn.selectOne(tableLB.select(tableLB.c.LOAD_BALANCER_NO==loadBalancerNo))

        # DNSサーバからの削除
        #self.loadBalancercontroller.deleteDns(loadBalancerNo);

        # ロードバランサの削除
        self.loadBalancercontroller.deleteLoadBalancer(loadBalancer["FARM_NO"],loadBalancerNo)
        self.conn.commit()
        return "RESULT:TRUE"

    def configureLoadBalancer(self, loadBalancerNo):
        table = self.conn.getTable("LOAD_BALANCER")
        loadBalancer = self.conn.selectOne(table.select(table.c.LOAD_BALANCER_NO==loadBalancerNo))

        # リスナーの設定
        try :
            self.loadBalancercontroller.configureListeners(loadBalancer["FARM_NO"], loadBalancerNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            # ロードバランサが無効な場合は例外を握りつぶす
            if isBit(loadBalancer["ENABLED"]):
                raise

        # ヘルスチェックの設定
        try :
            self.loadBalancercontroller.configureHealthCheck(loadBalancer["FARM_NO"], loadBalancerNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            # ロードバランサが無効な場合は例外を握りつぶす
            if isBit(loadBalancer["ENABLED"]):
                raise

        # セキュリティグループの設定
        try :
            # サブネットID
            subnets = self.client.describeSubnets()

            # セキュリティグループ
            groups = self.client.describeSecurityGroups()
            groupmap = {}
            for group in groups:
                if group.vpcId is not None:
                    key = group.groupName+group.vpcId
                    groupmap.update({key:group.groupId})

            self.loadBalancercontroller.applySecurityGroupsToLoadBalancer(loadBalancer["FARM_NO"], loadBalancerNo, groupmap, subnets)
        except Exception:
            self.logger.error(traceback.format_exc())
            # ロードバランサが無効な場合は例外を握りつぶす
            if isBit(loadBalancer["ENABLED"]):
                raise

        # インスタンスの振り分け設定
        try :
            self.loadBalancercontroller.configureInstances(loadBalancer["FARM_NO"], loadBalancerNo);
        except Exception:
            self.logger.error(traceback.format_exc())
            # ロードバランサが無効な場合は例外を握りつぶす
            if isBit(loadBalancer["ENABLED"]):
                raise

        self.logger.info(None, "IPROCESS-200106", [loadBalancerNo, loadBalancer["LOAD_BALANCER_NAME"]])

        self.conn.commit()
        return "RESULT:TRUE"


    def allocateAddress(self, farmNo):
        publicIp = None
        platformNo = self.client.getPlatformNo()

        tablePLAWS = self.conn.getTable("PLATFORM_AWS")
        awsPlatform = self.conn.selectOne(tablePLAWS.select(tablePLAWS.c.PLATFORM_NO==platformNo))

        if awsPlatform["VPC"] == 1:
            #VPC用のElasticIP発行処理呼び出し
            publicIp = self.client.allocateVpcAddress()
        else:
            #ElasticIP発行処理呼び出し
            publicIp = self.client.allocateAddress()

        #イベントログ出力
        self.conn.debug(farmNo, None, None, None, None, "AwsElasticIpAllocate", ["EC2", publicIp])

        #DBへ登録
        table = self.conn.getTable("AWS_ADDRESS")
        sql = table.insert({"ADDRESS_NO":None,
                            "USER_NO":self.accessInfo["USER"],
                            "PLATFORM_NO":platformNo,
                            "PUBLIC_IP":publicIp,
                            "COMMENT":None,
                            "INSTANCE_NO":None,
                            "INSTANCE_ID":None})
        self.conn.execute(sql)

        newAddress = self.conn.selectOne(table.select(table.c.PUBLIC_IP==publicIp))

        self.conn.commit()
        return "RESULT:" + str(newAddress["ADDRESS_NO"])

    def releaseAddress(self, addressNo, farmNo):
        platformNo = self.client.getPlatformNo()

        tablePLAWS = self.conn.getTable("PLATFORM_AWS")
        awsPlatform = self.conn.selectOne(tablePLAWS.select(tablePLAWS.c.PLATFORM_NO==platformNo))

        table = self.conn.getTable("AWS_ADDRESS")
        address = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))

        if not address:
            return

        ipaddress = address["PUBLIC_IP"]
        instanceId = address["INSTANCE_ID"]
        instanceNo = address["INSTANCE_NO"]

        if awsPlatform["VPC"] == 1:
            #アドレス情報取得
            address = self.client.describeAddress(ipaddress)
            # アドレスのステータスチェック
            self.addresscontroller.checkAssociatedAddress(instanceNo, addressNo)
            #VPC用のElasticIP解放処理呼び出し
            self.client.releaseVpcAddress(ipaddress, address.allocationId)
        else:
            #ElasticIP解放処理呼び出し
            self.client.releaseAddress(ipaddress)

        #イベントログ
        self.conn.debug(farmNo, None, None, None, None, "AwsElasticIpRelease", ["EC2", ipaddress])

        #DBから削除
        table.delete(table.c.ADDRESS_NO==addressNo).execute()

        self.conn.commit()
        return "RESULT:TRUE"

    def describeSnapshot(self, snapshotId):
        snapshots = self.othercontroller.describeSnapshot(snapshotId)
        rtString = ''
        for snapshot in snapshots:
            if rtString != '':
                rtString = rtString + "##"

            #とりあえず全部
            rtString = rtString  + snapshot.snapshotId + '#' \
                                 + snapshot.volumeId + '#' \
                                 + snapshot.status + '#' \
                                 + snapshot.startTime + '#' \
                                 + snapshot.progress + '#' \
                                 + snapshot.ownerId + '#' \
                                 + snapshot.volumeSize + '#' \
                                 + snapshot.description + '#' \
                                 + snapshot.tagSet

        self.conn.commit()
        return "RESULT:" + rtString


    def createSnapshot(self, volumeNo):
        self.othercontroller.createSnapshot(volumeNo)
        self.conn.commit()
        return "RESULT:TRUE"

    def deleteSnapshot(self, snapshotNo):
        self.othercontroller.deleteSnapshot(snapshotNo)
        self.conn.commit()
        return "RESULT:TRUE"

    def getPasswordData(self, instanceNo):
        passwordData = self.othercontroller.getPasswordData(instanceNo)
        self.conn.commit()
        return "RESULT:" + passwordData

    def describeKeyPairs(self):
        keypairs = self.client.describeKeyPairs()
        rtString = ''
        for keypair in keypairs:
            if rtString != '':
                rtString = rtString + "##"

            #とりあえず必要な情報のみ返します
            rtString = rtString +  keypair.keyName

        self.conn.commit()
        return "RESULT:" + rtString

    def createKeyPair(self, keyName):
        self.client.createKeyPair(keyName)
        self.conn.commit()
        return "RESULT:TRUE"

    def deleteKeyPair(self, keyName):
        self.client.deleteKeyPair(keyName)
        self.conn.commit()
        return "RESULT:TRUE"

    def importKeyPair(self, keyName, publicKeyMaterial):
        keyPair = self.client.importKeyPair(keyName, publicKeyMaterial)

        keyFingerprint = keyPair.keyFingerprint
        self.conn.commit()
        return "RESULT:" + keyFingerprint

    def describeSecurityGroups(self, vpcid = None):
        groups = self.client.describeSecurityGroups()
        rtString = ''
        for group in groups:
            #VPCIDが一致する物以外は除外
            if vpcid is not None:
                if vpcid != group.vpcId:
                    continue
            #VPCID未入力時はVPCIDが設定されていない物のみ使用
            else:
                if group.vpcId is not None:
                    continue

            if rtString != '':
                rtString = rtString + "##"

            #とりあえず必要な情報のみ返します
            rtString = rtString + group.groupName

        self.conn.commit()
        return "RESULT:" + rtString

    def describeAvailabilityZones(self):
        zones = self.client.describeAvailabilityZones()

        #available
        rtString = ''
        for zone in zones:
            #有効な物のみ利用する
            if zone.zone_state != "available":
                continue

            if rtString != '':
                rtString = rtString + "##"

            #とりあえず必要な情報のみ返します IDに相当するパラメータが無い為NONEを入れておく
            rtString = rtString  + zone.name + "#NONE"
        self.conn.commit()
        #出力として返す
        return "RESULT:" + rtString


    def describeSubnets(self, vpcid = None):
        subnets = self.client.describeSubnets()
        rtString = ''
        for subnet in subnets:
            if vpcid is not None:
                if vpcid != subnet.vpcId:
                    continue

            if rtString != '':
                rtString = rtString + "##"

            #とりあえず必要な情報のみ返します
            rtString = rtString + subnet.subnetId + '#' + subnet.availabilityZone+ '#' + subnet.cidrBlock
        self.conn.commit()
        return "RESULT:" + rtString
