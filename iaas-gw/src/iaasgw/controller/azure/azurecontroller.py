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


from iaasgw.client.azureiaasclient import AzureIaasClient
from iaasgw.controller.azure.azureInstanceController import \
    azureInstanceController
from iaasgw.controller.azure.azureOtherController import \
    azureOtherController
from iaasgw.controller.azure.azureVolumeController import \
    azureVolumeController
from iaasgw.controller.iaascontroller import IaasController
from iaasgw.log.log import IaasLogger
import traceback


class AzureController(IaasController):

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
        self.client = AzureIaasClient(platforminfo, accessInfo["USER_NAME"], accessInfo["SUBSCRIPTION_ID"], accessInfo["CERTIFICATE"], self.conn)

        #コントローラ作成
        self.instancecontroller = azureInstanceController(platforminfo, self.client, self.conn)
        self.volumecontroller   = azureVolumeController(platforminfo, self.client, self.conn)
        self.othercontroller    = azureOtherController(platforminfo, self.client, self.conn)

    def __del__(self):
        self.conn.rollback()
        self.conn.close()

    def startInstance(self, instanceNo):
        try:
            self.instancecontroller.startInstance(instanceNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        self.conn.commit()
        return True

    def stopInstance(self, instanceNo):
        try:
            self.instancecontroller.stopInstance(instanceNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

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

    def deleteVolume(self, volumeNo):
        try:
            self.volumecontroller.deleteVolume(volumeNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise
        return True


    # ユーティリティ
    def generateEnvironment(self, platformNo):
        try:
            self.othercontroller.createCloudServiceFor(platformNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise
        try:
            self.othercontroller.createStorageAccountFor(platformNo)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise

        self.conn.commit()
        return True

    def describeAzureSubnets(self, networkName):
        networks = self.client.listVirtualNetworkSites()
        rtString = ''
        for network in networks:
            if network.name != networkName:
                continue

            for subnet in network.subnets:
                if rtString != '':
                    rtString = rtString + "##"

                #とりあえず必要な情報のみ返します
                rtString = rtString + subnet.name + '#' + subnet.address_prefix

        self.conn.commit()
        return "RESULT:" + rtString
