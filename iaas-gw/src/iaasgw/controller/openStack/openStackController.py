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


from iaasgw.client.openStackiaasclient import OpenStackIaasClient
from iaasgw.controller.openStack.openStackInstanceController import \
    OpenStackInstanceController
from iaasgw.controller.openStack.openStackOtherController import \
    OpenStackOtherController
from iaasgw.controller.openStack.openStackVolumeController import \
    OpenStackVolumeController
from iaasgw.controller.iaascontroller import IaasController
from iaasgw.log.log import IaasLogger
from iaasgw.utils.stringUtils import isNotEmpty
import traceback


class OpenStackController(IaasController):

    logger = IaasLogger()

    conn = None
    accessInfo = None

    client = None
    instancecontroller = None
    volumecontroller = None
    othercontroller = None

    def __init__(self, conn, accessInfo, platforminfo, isLb = False):
        self.conn = conn
        self.accessInfo = accessInfo
        username = accessInfo['OS_ACCESS_ID']
        password = accessInfo['OS_SECRET_KEY']
        self.client = OpenStackIaasClient(platforminfo, username, password)

        #コントローラ作成
        self.instancecontroller = OpenStackInstanceController(platforminfo, self.client, self.conn)
        self.volumecontroller   = OpenStackVolumeController(platforminfo, self.client, self.conn)
        self.othercontroller    = OpenStackOtherController(platforminfo, self.client, self.conn)

    def __del__(self):
        self.conn.rollback()
        self.conn.close()

    def startInstance(self, instanceNo):
        try:
            self.instancecontroller.startInstance(instanceNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        # ボリュームに関する処理
        table = self.conn.getTable("OPENSTACK_VOLUME")
        volumes = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))
        for volume in volumes:
            if isNotEmpty(volume["COMPONENT_NO"]):
                # コンポーネント番号がある場合はスキップ
                continue
            #Volumeスタート
            self.volumecontroller.startVolume(instanceNo, volume["VOLUME_NO"])

        self.conn.commit()
        return True

    def stopInstance(self, instanceNo):
        try:
            self.instancecontroller.stopInstance(instanceNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        try :
            # ボリュームに関する処理
            tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
            volumes = self.conn.select(tableOSVOL.select(tableOSVOL.c.INSTANCE_NO==instanceNo))

            for volume in volumes:
                self.volumecontroller.stopVolume(instanceNo, volume["VOLUME_NO"])

        except Exception:
            self.logger.error(traceback.format_exc())

        self.conn.commit()
        return True

    def terminateInstance(self, instanceNo):
        try:
            self.instancecontroller.terminateInstance(instanceNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        self.conn.commit()
        return True

    def startVolume(self, instanceNo, volumeNo):
        try:
            self.volumecontroller.startVolume(instanceNo, volumeNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        self.conn.commit()
        return True

    def stopVolume(self, instanceNo, volumeNo):
        try:
            self.volumecontroller.stopVolume(instanceNo, volumeNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        self.conn.commit()
        return True

    def deleteVolume(self, volumeId):
        try:
            self.volumecontroller.deleteVolume(volumeId)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        self.conn.commit()
        return True

    def describeSecurityGroups(self, vpcid=None):
        securityGroupList = self.client.describeSecurityGroups()
        rtString = ''
        for securityGroup in securityGroupList:
            if rtString != '':
                rtString = rtString + "##"
            rtString = rtString + securityGroup.name + "#" + securityGroup.id

        return "RESULT:" + rtString

    def describeAvailabilityZones(self):
        zoneList = self.client.describeAvailabilityZones()
        rtString = ''
        for zone in zoneList:
            if rtString != '':
                rtString = rtString + "##"
            #必要な情報のみ返却。IDに相当するパラメータがないためNONEをセット
            rtString = rtString + zone.zoneName + "#NONE"

        return "RESULT:" + rtString

    def describeNetwork(self):
        networkList = self.client.describeNetworks()
        rtString = ''
        for nw in networkList:
            if rtString != '':
                rtString = rtString + "##"
            rtString = rtString + nw.id + "#" + nw.label

        return "RESULT:" + rtString

    def describeFlavors(self, flavorIds):
        flavorIdList = flavorIds.split(',')
        flavorNameList = []
        flvs = self.client.describeFlavors()
        rtString = ''
        for flavorId in flavorIdList:
            for flv in flvs:
                if flv.id != flavorId:
                    continue
                if rtString != '':
                    rtString = rtString + "##"
                rtString = rtString + flv.id + "#" + flv.name
                break
            else:
                pass

        return "RESULT:" + rtString

    def describeKeyPairs(self):
        keypairs = self.client.describeKeyPairs()
        rtString = ''
        for keypair in keypairs:
            if rtString != '':
                rtString = rtString + "##"

            #とりあえず必要な情報のみ返します
            rtString = rtString + keypair.name

        self.conn.commit()
        return "RESULT:" + rtString

    def importKeyPair(self, keyName, publicKeyMaterial):
        self.client.createKeyPair(keyName, publicKeyMaterial)
        self.conn.commit()
        return "RESULT:" + ''

