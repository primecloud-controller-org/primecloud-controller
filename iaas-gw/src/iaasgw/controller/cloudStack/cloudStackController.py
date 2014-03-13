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
from iaasgw.client.cloudStackiaasclient import CloudStackIaasClient
from iaasgw.controller.cloudStack.cloudStackAddressController import \
    CloudStackAddressController
from iaasgw.controller.cloudStack.cloudStackInstanceController import \
    CloudStackInstanceController
from iaasgw.controller.cloudStack.cloudStackLoadBalancercontroller import \
    CloudStackLoadBalancercontroller
from iaasgw.controller.cloudStack.cloudStackOtherController import \
    CloudStackOtherController
from iaasgw.controller.cloudStack.cloudStackVolumController import \
    CloudStackVolumController
from iaasgw.controller.iaascontroller import IaasController
from iaasgw.log.log import IaasLogger
from iaasgw.utils.stringUtils import isNotEmpty, isBit
import traceback

class CloudStackController(IaasController):

    logger = IaasLogger()

    conn = None
    client = None
    accessInfo = None

    instancecontroller = None
    volumecontroller = None
    addresscontroller = None
    loadBalancercontroller = None
    othercontroller = None

    def __init__(self, conn, accessInfo, platforminfo):
        self.conn = conn
        self.accessInfo = accessInfo

        self.client = CloudStackIaasClient(platforminfo, accessInfo["USER_NAME"], accessInfo["ACCESS_ID"], accessInfo["SECRET_KEY"])
        #コントローラ作成
        self.instancecontroller      = CloudStackInstanceController(platforminfo, self.client, self.conn)
        self.volumecontroller        = CloudStackVolumController(platforminfo, self.client, self.conn)
        self.addresscontroller       = CloudStackAddressController(platforminfo, self.client, self.conn)
        self.loadBalancercontroller  = CloudStackLoadBalancercontroller(platforminfo, self.client, self.conn)
        self.othercontroller         = CloudStackOtherController(platforminfo, self.client, self.conn)

    def __del__(self):
        self.conn.rollback()
        self.conn.close()

    def describeDiskOfferings(self):
        return self.client.describeDiskOfferings()

    def describePublicIpAddresses(self, id):
        return self.client.describePublicIpAddresses(id)

    def describeOnlyInstances(self):
        return self.client.describeOnlyInstances()

    def describeServiceOfferings(self):
        return self.client.describeServiceOfferings()


    def describeImages(self):
        return self.client.describeImages()

    def describeInstances(self):
        return self.client.describeInstances()


    def startInstance(self, instanceNo):

        # インスタンスに関する処理   TODO タイムアウトリトライは未実装
        try:
            self.instancecontroller.startInstance(instanceNo)
        except Exception, e:
            self.logger.error(traceback.format_exc())
            raise

        # ボリュームに関する処理
        table = self.conn.getTable("CLOUDSTACK_VOLUME")
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
        return True


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
            tableCSVOL = self.conn.getTable("CLOUDSTACK_VOLUME")
            volumes = self.conn.select(tableCSVOL.select(tableCSVOL.c.INSTANCE_NO==instanceNo))

            for volume in volumes:
                self.volumecontroller.stopVolume(instanceNo, volume["VOLUME_NO"])

        except Exception:
            self.logger.error(traceback.format_exc())

        self.conn.commit()
        return True

    def terminateInstance(self, instanceId):

        #1度も起動されていない
        if instanceId is None:
            return

        # インスタンスの停止
        self.instancecontroller.terminate(instanceId);
        self.conn.commit()
        return True


    def startVolume(self, instanceNo, volumeNo):
        self.volumecontroller.startVolume(instanceNo, volumeNo)
        self.conn.commit()
        return True


    def stopVolume(self, instanceNo, volumeNo):
        self.volumecontroller.stopVolume(instanceNo, volumeNo)
        self.conn.commit()
        return True

    def deleteVolume(self, volumeId):
        self.client.deleteVolume(volumeId)
        self.conn.commit()
        return True

    def startLoadBalancer(self, loadBalancerNo):
        # ロードバランサの作成
        self.loadBalancercontroller.createLoadBalancer(loadBalancerNo)

        self.conn.commit()
        return True

    def stopLoadBalancer(self, loadBalancerNo):

        # ロードバランサの削除
        self.loadBalancercontroller.deleteLoadBalancer(loadBalancerNo)
        self.conn.commit()
        return True

    def configureLoadBalancer(self, loadBalancerNo):
        table = self.conn.getTable("LOAD_BALANCER")
        loadBalancer = self.conn.selectOne(table.select(table.c.LOAD_BALANCER_NO==loadBalancerNo))
        # インスタンスの振り分け設定
        try :
            self.loadBalancercontroller.configureInstances(loadBalancerNo);
        except Exception, e:
            self.logger.error(traceback.format_exc())
            # ロードバランサが無効な場合は例外を握りつぶす
            if not isBit(loadBalancer["ENABLED"]):
                raise

        self.logger.info(None, "IPROCESS-200625", [loadBalancerNo, loadBalancer["LOAD_BALANCER_NAME"]])

        self.conn.commit()
        return True


    #TODO  後々はゾーンを取得するようにしなければいけない
    def allocateAddress(self, farmNo):
        addressid = self.client.associateAddress()
        addressinfo = self.client.describePublicIpAddress(addressid)

        ipaddress = addressinfo["ipaddress"]
        #イベントログ出力
        self.conn.debug(farmNo, None, None, None, None, "CloudStackIpAllocate", ["CLOUDSTACK", ipaddress])

        #DBへ登録
        table = self.conn.getTable("CLOUDSTACK_ADDRESS")
        sql = table.insert([None,
                      self.accessInfo["USER"],
                      self.client.getPlatformNo(),
                      None,
                      None,
                      addressid,
                      ipaddress,
                      addressinfo["networkid"],
                      addressinfo["state"],
                      addressinfo["zoneid"],
                      ])

        self.conn.execute(sql)

        newAddress = self.conn.selectOne(table.select(table.c.IPADDRESS==ipaddress))
        print newAddress
        self.conn.commit()
        return "RESULT:" + str(newAddress["ADDRESS_NO"])


    def releaseAddress(self, addressNo, farmNo):
        table = self.conn.getTable("CLOUDSTACK_ADDRESS")
        address = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))

        if not address:
            return

        addressid = address["ADDRESS_ID"]
        self.client.disassociateAddress(addressid)

        #イベントログ
        self.conn.debug(farmNo, None, None, None, None, "CloudStackIpRelease", ["CLOUDSTACK", address["IPADDRESS"]])

        #DBから削除
        table.delete(table.c.ADDRESS_NO==addressNo).execute()

        self.conn.commit()
        return True



    def createSnapshot(self, volumeNo):
        self.othercontroller.createSnapshot(volumeNo)
        self.conn.commit()
        return True

    def deleteSnapshot(self, snapshotNo):
        self.othercontroller.deleteSnapshot(snapshotNo)
        self.conn.commit()
        return True

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
            rtString = rtString +  keypair['name']

        self.conn.commit()
        return "RESULT:" + rtString

    def createKeyPair(self, keyName):
        self.client.createKeyPair(keyName)
        self.conn.commit()
        return True

    def deleteKeyPair(self, keyName):
        self.client.deleteKeyPair(keyName)
        self.conn.commit()
        return True

    def importKeyPair(self, keyName, publicKeyMaterial):
        keyFingerprint = self.client.registerSSHKeyPair(keyName, publicKeyMaterial)
        self.conn.commit()
        return "RESULT:" + keyFingerprint


    def describeSecurityGroups(self, vpcid = None):
        groups = self.client.describeSecurityGroups()
        rtString = ''
        for group in groups:
            if rtString != '':
                rtString = rtString + "##"

            #とりあえず必要な情報のみ返します
            rtString = rtString + group['name']
        self.conn.commit()
        return "RESULT:" + rtString

    def describeAvailabilityZones(self):
        zones = self.client.describeAvailabilityZones()

        rtString = ''
        for zone in zones:
            if rtString != '':
                rtString = rtString + "##"

            #とりあえず必要な情報のみ返します
            rtString = rtString  + zone["name"] + "#" + str(zone["id"])
        self.conn.commit()
        #出力として返す
        return "RESULT:" + rtString


