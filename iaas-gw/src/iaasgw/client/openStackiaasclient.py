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
from novaclient.v1_1 import Client as nclient
from cinderclient.v1 import Client as cclient
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertyUtil import getOpenStackInfo
import traceback
import time
import tempfile
import base64
import os
import signal
import time

class OpenStackIaasClient:

    logger = IaasLogger()
    platformNo = None
    username = None
    tenant = None
    tenant_id = None
    auth_url = None
    nova = None

    timeout = False
    TIMEOUT = 600

    def __init__(self, platforminfo, username, password):

        self.username = username
        self.platformNo = platforminfo["platformNo"]
        openstackInfo = getOpenStackInfo(self.platformNo)
        self.tenant = openstackInfo['TENANT_NM']
        self.tenant_id = openstackInfo['TENANT_ID']
        self.auth_url = openstackInfo['URL']

        self.logger.debug('username=%s' % (self.username))
        self.logger.debug('password=%s' % (password))
        self.logger.debug('tenant=%s' % (self.tenant))
        self.logger.debug('auth_url=%s' % (self.auth_url))
        self.logger.debug('tenant_id=%s' % (self.tenant_id))
        # NOVAマネージャ作成
        self.nova = nclient(self.username, password, self.tenant, self.auth_url, tenant_id=self.tenant_id)
        # CINDERマネージャ作成
        self.cinder = cclient(self.username, password, self.tenant, self.auth_url, tenant_id=self.tenant_id)

        signal.signal(signal.SIGALRM, self._taskTimeout)

    def _taskTimeout(self, signum, frame):
        self.timeout = True
        return

    def _setTimeout(self, TIMEOUT):
        self.timeout = False
        signal.alarm(TIMEOUT)

    def _resetTimeout(self):
        self.timeout = False

    def _isTimeout(self):
        return self.timeout

    def _findServerById(self, instanceId):
        try:
            instanceObj = self.nova.servers.get(instanceId)
        except:
            self.logger.error(traceback.format_exc())
            raise
        else:
            return instanceObj

    def _getVirtualMachine(self, instanceId):
        try:
            instanceObj = self._findServerById(instanceId)
        except:
            self.logger.error(traceback.format_exc())
            raise
        else:
            return instanceObj

    def _waitServerStatus(self, serverId, waitStatus, timeout=TIMEOUT):
        SLEEP = 10
        self._setTimeout(timeout)
        while True:
            time.sleep(SLEEP)
            instanceObj = self._getVirtualMachine(serverId)
            status = instanceObj.status
            self.logger.debug('      Status: %s' % (status))
            if status == waitStatus:
                return instanceObj
            elif status == 'ERROR':
                return instanceObj
            if self._isTimeout() is True:
                self._resetTimeout()
                return instanceObj

    def _findFlavorById(self, flavorId):
        try:
            flavorList = self.nova.flavors.list()
        except:
            self.logger.error(traceback.format_exc())
        for flavorObject in flavorList:
            fId = flavorObject.id
            if fId == flavorId:
                flavorName = flavorObject.name
                self.logger.debug('      Flavor Name: %s' % (flavorName))
                return flavorObject
        else:
            return None

    def _findFlavorByName(self, flavorName):
        try:
            flavorList = self.nova.flavors.list()
        except:
            self.logger.error(traceback.format_exc())
        for flavorObject in flavorList:
            fNm = flavorObject.name
            if fNm == flavorName:
                flavorId = flavorObject.id
                self.logger.debug('      Flavor Name: %s' % (flavorName))
                return flavorObject
        else:
            return None

    def _findImageById(self, imageId):
        try:
            imageList = self.nova.images.list()
        except:
            self.logger.error(traceback.format_exc())
        for imageObject in imageList:
            iId = imageObject.id
            if iId == imageId:
                imageName = imageObject.name
                self.logger.debug('      Image Name: %s' % (imageName))
                return imageObject
        else:
            return None

    def getVirtualMachineStatus(self, instanceId):
        try:
            instanceObj = self._getVirtualMachine(instanceId)
        except:
            # NotFound or any other exception
            self.logger.error(traceback.format_exc())
            raise
        status = instanceObj.status
        self.logger.debug('      Instance Status: %s' % (status))
        return status

    def deleteVirtualMachine(self, serverId):
        instanceObj = self._findServerById(serverId)
        # VM削除
        try:
            novaResponse = self.nova.servers.delete(instanceObj)
        except:
            self.logger.error(traceback.format_exc())
            raise

        return None

    def stopVirtualMachine(self, serverId):
        instanceObj = self._findServerById(serverId)
        # VM停止
        try:
            novaResponse = self.nova.servers.stop(instanceObj)
        except:
            # BadRequest: Instance xxx in vm_state stopped. Cannot stop while the instance is in this state.
            #             (HTTP 400) (Request-ID: xxx)
            self.logger.error(traceback.format_exc())
            raise

        # 停止完了まで待つ
        instanceObj = self._waitServerStatus(serverId, 'SHUTOFF')
        status = instanceObj.status
        if status != 'SHUTOFF' and status != 'ERROR':
            # タイムアウト
            self.logger.error('The operation timed out.')
            # 呼び出し側で例外を補足して、異なる例外を送出しているため、下記例外は失われる。
            raise IaasException("EPROCESS-001011")

        return instanceObj

    def startVirtualMachine(self, serverId):
        instanceObj = self._findServerById(serverId)
        # VM起動
        try:
            novaResponse = self.nova.servers.start(instanceObj)
        except:
            self.logger.error(traceback.format_exc())
            raise

        # 起動完了まで待つ
        instanceObj = self._waitServerStatus(serverId, 'ACTIVE')
        status = instanceObj.status
        if status != 'ACTIVE' and status != 'ERROR':
            # タイムアウト
            self.logger.error('The operation timed out.')
            # 呼び出し側で例外を補足して、異なる例外を送出しているため、下記例外は失われる。
            raise IaasException("EPROCESS-001011")

        return instanceObj

    def createVirtualMachine(self, serverName, flavorNm, imageId, availabilityZoneId, networkId, userData, securityGroupId, keyName):
        # フレーバーIDからフレーバーオブジェクトへの変換
        flavorObject = self._findFlavorByName(flavorNm)
        if flavorObject is None:
            self.logger.error('      Flavor Not Found: %s' % (flavorId))
            raise IaasException("EPROCESS-001001")

        # イメージIDからイメージオブジェクトへの変換
        imageObject = self._findImageById(imageId)
        if imageObject is None:
            self.logger.error('      Image Not Found: %s' % (imageId))
            raise IaasException("EPROCESS-001001")

        # NICリストの作成
        nics = [{"net-id": networkId, "v4-fixed-ip": "", "port-id": ""}]

        # セキュリティグループリストの作成
        securityGroupList = [securityGroupId]

        # VM作成
        try:
            novaResponse = self.nova.servers.create(serverName, flavor=flavorObject, image=imageObject, availability_zone=availabilityZoneId, nics=nics, userdata=userData, security_groups=securityGroupList, key_name=keyName)
        except:
            self.logger.error(traceback.format_exc())
            raise

        # 作成完了まで待つ
        serverId = novaResponse.id
        instanceObj = self._waitServerStatus(serverId, 'ACTIVE')
        status = instanceObj.status
        if status != 'ACTIVE' and status != 'ERROR':
            # タイムアウト
            self.logger.error('The operation timed out.')
            # 呼び出し側で例外を補足して、異なる例外を送出しているため、下記例外は失われる。
            raise IaasException("EPROCESS-001011")

        return instanceObj

    def createVolume(self, name, availabilityZone, size=None, snapshotId=None):
        #volume = self.nova.volumes.create(size=1, display_name='test02')
        volume = self.cinder.volumes.create(size=size, display_name=name)
        return volume

    def attachVolume(self, volumeId, instanceId, device):
        #self.cinder.volumes.attach(volumeId, instanceId, device)
        self.nova.volumes.create_server_volume(instanceId, volumeId, device)

    def detachVolume(self, instanceId, volumeId):
        #self.cinder.volumes.detach(volumeId)
        self.nova.volumes.delete_server_volume(instanceId, volumeId)
        
    def deleteVolume(self, volumeId):
        self.cinder.volumes.delete(volumeId)

    def describeVolume(self, volumeId):
        volume = self.cinder.volumes.get(volumeId)
        return volume

    def describeSecurityGroups(self, vpcid=None):
        secgroups = self.nova.security_groups.list()
        return secgroups

    def describeAvailabilityZones(self):
        zones = self.nova.availability_zones.list(detailed=False)
        return zones

    def describeNetworks(self):
        networks = self.nova.networks.list()
        return networks

    def describeFlavors(self):
        flavorList = self.nova.flavors.list()
        return  flavorList

    def describeKeyPairs(self):
        keypairList = self.nova.keypairs.list()
        return  keypairList

    def createKeyPair(self, keyName, publicKeyMaterial):
        self.nova.keypairs.create(name = keyName, public_key = publicKeyMaterial)
        return

