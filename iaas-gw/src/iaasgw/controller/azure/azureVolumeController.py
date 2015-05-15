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
from iaasgw.utils.stringUtils import isNotEmpty, isEmpty
from iaasgw.utils.readIniFile import getAzureDeviceProperty
from ConfigParser import NoSectionError
import traceback

class azureVolumeController(object):

    logger = IaasLogger()

    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, azureiaasclient, conn):
        self.client = azureiaasclient
        self.conn = conn
        self.platforminfo = platforminfo

    def _getUnusedLun(self, instanceName):
        azureDiskTable = self.conn.getTable('AZURE_DISK')
        instanceList = self.conn.select(azureDiskTable.select(azureDiskTable.c.INSTANCE_NAME==instanceName))

        lunList = map(lambda i: i['LUN'], instanceList)
        for lun in range(0, 16):
            if lun not in lunList:
                self.logger.debug('      LUN %s is unused' % (lun))
                return lun
        return None

    def _createAttachVolume(self, instanceNo, volumeNo):
        azureDiskTable = self.conn.getTable('AZURE_DISK')
        volume = self.conn.selectOne(azureDiskTable.select(azureDiskTable.c.DISK_NO==volumeNo))
        # ボリュームサイズ
        size = volume['SIZE']
        platformNo = volume['PLATFORM_NO']

        platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
        platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
            (platformAzureTable.c.PLATFORM_NO==platformNo))
        # クラウドサービス
        cloudService = platformAzureInfo['CLOUD_SERVICE_NAME']
        # ストレージアカウント
        storageAccountName = platformAzureInfo['STORAGE_ACCOUNT_NAME']
        mediaLink = 'https://%s.blob.core.windows.net/vhds/%s.vhd' % \
            (storageAccountName, volumeNo)

        azureInstanceTable = self.conn.getTable('AZURE_INSTANCE')
        azureInstance = self.conn.selectOne(azureInstanceTable.select(azureInstanceTable.c.INSTANCE_NO==instanceNo))
        # ロール
        instanceName = azureInstance['INSTANCE_NAME']

        # 未使用LUN取得
        lun = self._getUnusedLun(instanceName)
        if lun is None:
            self.logger.info('      No Unused LUN')
            return lun

        # イベントログ用のデータを取得
        componentTable = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(componentTable.select(componentTable.c.COMPONENT_NO==volume['COMPONENT_NO']))
        instanceTable = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(instanceTable.select(instanceTable.c.INSTANCE_NO==instanceNo))
        # イメージ取得
        imageTable = self.conn.getTable("IMAGE")
        image = self.conn.selectOne(imageTable.select(imageTable.c.IMAGE_NO==instance['IMAGE_NO']))
        imageOs = image['OS']
        
        #アタッチされたデバイスを取得
        attachedDev = self._getAttachedDevice(imageOs, lun)

        
        # デバイスの取得に失敗した場合、エラー
        if attachedDev is None:
            self.conn.error(instance["FARM_NO"], component['COMPONENT_NO'], component['COMPONENT_NAME'], instanceNo, \
                            instance["INSTANCE_NAME"], "AzureDeviceGetFailure",["AZURE"])
            raise IaasException("EPROCESS-000915", [instanceName])

        # 空のデータボリュームを作成してアタッチ
        status = self.client.createAddDataDisk(cloudService, instanceName, mediaLink, size, lun)
        self.logger.info('      Attached DISK_NO: %s to %s, Job Status: %s' % (volumeNo, instanceName, status))

        # データベース更新
        table = self.conn.getTable("AZURE_DISK")
        updateDict = self.conn.selectOne(table.select(table.c.DISK_NO==volumeNo))
        updateDict["DISK_NAME"] = self.client.getDiskNameAttachedTo(cloudService, instanceName, lun)
        updateDict["INSTANCE_NAME"] = instanceName
        updateDict["LUN"] = lun
        updateDict["DEVICE"] = attachedDev
        sql = table.update(table.c.DISK_NO == updateDict["DISK_NO"], values=updateDict)
        self.conn.execute(sql)
        return

    def _attachVolume(self, instanceNo, volumeNo):
        azureDiskTable = self.conn.getTable('AZURE_DISK')
        volume = self.conn.selectOne(azureDiskTable.select(azureDiskTable.c.DISK_NO==volumeNo))
        diskName = volume['DISK_NAME']
        platformNo = volume['PLATFORM_NO']

        platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
        platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
            (platformAzureTable.c.PLATFORM_NO==platformNo))
        # クラウドサービス
        cloudService = platformAzureInfo['CLOUD_SERVICE_NAME']

        azureInstanceTable = self.conn.getTable('AZURE_INSTANCE')
        azureInstance = self.conn.selectOne(azureInstanceTable.select(azureInstanceTable.c.INSTANCE_NO==instanceNo))
        # ロール
        instanceName = azureInstance['INSTANCE_NAME']

        # 未使用LUN取得
        lun = self._getUnusedLun(instanceName)
        if lun is None:
            self.logger.info('      No Unused LUN')
            return lun

        # イベントログ用のデータを取得
        componentTable = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(componentTable.select(componentTable.c.COMPONENT_NO==volume['COMPONENT_NO']))
        instanceTable = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(instanceTable.select(instanceTable.c.INSTANCE_NO==instanceNo))

        # イメージ取得
        imageTable = self.conn.getTable("IMAGE")
        image = self.conn.selectOne(imageTable.select(imageTable.c.IMAGE_NO==instance['IMAGE_NO']))
        imageOs = image['OS']

        #アタッチされたデバイスを取得
        attachedDev = self._getAttachedDevice(imageOs, lun)
        
        # デバイスの取得に失敗した場合、エラー
        if attachedDev is None:
            self.conn.error(instance["FARM_NO"], component['COMPONENT_NO'], component['COMPONENT_NAME'], instanceNo, \
                            instance["INSTANCE_NAME"], "AzureDeviceGetFailure",["AZURE"])
            raise IaasException("EPROCESS-000910", [instanceName, diskName])

        # 既存のデータボリュームをアタッチ
        status = self.client.addDataDisk(cloudService, instanceName, diskName, lun)
        self.logger.info('      Attached DISK_NO: %s to %s, Job Status: %s' % (volumeNo, instanceName, status))

        # データベース更新
        table = self.conn.getTable("AZURE_DISK")
        updateDict = self.conn.selectOne(table.select(table.c.DISK_NO==volumeNo))
        updateDict["INSTANCE_NAME"] = instanceName
        updateDict["LUN"] = lun
        updateDict["DEVICE"] = attachedDev
        sql = table.update(table.c.DISK_NO == updateDict["DISK_NO"], values=updateDict)
        self.conn.execute(sql)
        return

    def _getAttachedDevice(self, imageOs, lun) :
        # イメージOS名とセクションが完全にマッチするデバイスパスを取得
        try:
            dviceList = getAzureDeviceProperty(imageOs)
        except NoSectionError:
            try:
                # セクションに完全にマッチするものが無い場合、イメージOS名の後ろ1桁を削る
                editImageOs = imageOs[:-1]
                self.logger.info(editImageOs)
                # セクションとマッチするデバイスパスを取得する
                dviceList = getAzureDeviceProperty(editImageOs)
            except NoSectionError:
                dviceList = []
        #アタッチされたデバイス
        attachedDev = None
        for key, value in dviceList:
            if key == str(lun):
                    attachedDev = value
                    break
        return attachedDev

    def startVolume(self, instanceNo, volumeNo) :
        table = self.conn.getTable('AZURE_DISK')
        volume = self.conn.selectOne(table.select(table.c.DISK_NO==volumeNo))

        # インスタンス名がある場合はスキップ
        if (isNotEmpty(volume['INSTANCE_NAME'])) :
            return

        if (isEmpty(volume['DISK_NAME'])) :
            # ボリューム名がない場合は新規作成
            self._createAttachVolume(instanceNo, volumeNo)
        else:
            # ボリューム名がある場合は既存のデータディスクをアタッチ
            self._attachVolume(instanceNo, volumeNo)

        return

    def detachVolume(self, instanceNo, volumeNo):
        azureDiskTable = self.conn.getTable('AZURE_DISK')
        volume = self.conn.selectOne(azureDiskTable.select(azureDiskTable.c.DISK_NO==volumeNo))
        diskName = volume['DISK_NAME']
        platformNo = volume['PLATFORM_NO']
        # ロール、LUN
        instanceName = volume['INSTANCE_NAME']
        lun = volume['LUN']

        platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
        platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
            (platformAzureTable.c.PLATFORM_NO==platformNo))
        # クラウドサービス
        cloudService = platformAzureInfo['CLOUD_SERVICE_NAME']

        # データボリュームをデタッチ
        status = self.client.deleteDataDisk(cloudService, instanceName, diskName, lun)
        self.logger.info('      Detached DISK_NO: %s from %s, Job Status: %s' % (volumeNo, instanceName, status))

        # データベース更新
        table = self.conn.getTable("AZURE_DISK")
        updateDict = self.conn.selectOne(table.select(table.c.DISK_NO==volumeNo))
        updateDict["INSTANCE_NAME"] = None
        updateDict["LUN"] = None
        sql = table.update(table.c.DISK_NO == updateDict["DISK_NO"], values=updateDict)
        self.conn.execute(sql)
        return


    def stopVolume(self, instanceNo, volumeNo) :
        table = self.conn.getTable('AZURE_DISK')
        volume = self.conn.selectOne(table.select(table.c.DISK_NO==volumeNo))

        # ボリューム名がない場合はスキップ
        if (isEmpty(volume['DISK_NAME'])) :
            return

        # インスタンス名がある場合はスキップ
        if (isEmpty(volume['INSTANCE_NAME'])) :
            return

        self.detachVolume(instanceNo, volumeNo)

        return

    def deleteVolume(self, volumeNo) :
        table = self.conn.getTable('AZURE_DISK')
        volume = self.conn.selectOne(table.select(table.c.DISK_NO==volumeNo))

        # StrageAccountを得る
#        azureDiskTable = self.conn.getTable('AZURE_DISK')
#        volume = self.conn.selectOne(azureDiskTable.select(azureDiskTable.c.DISK_NO==volumeNo))
        platformNo = volume['PLATFORM_NO']

        platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
        platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
            (platformAzureTable.c.PLATFORM_NO==platformNo))

        # ストレージアカウント
        storageAccountName = platformAzureInfo['STORAGE_ACCOUNT_NAME']

        # disk nameを得る
        diskName = volume['DISK_NAME']

        # mediaLinkを得る
        mediaLink = 'https://%s.blob.core.windows.net/vhds/%s.vhd' % \
            (storageAccountName, volumeNo)

        self.logger.debug('      volumeNo:%s, diskName:%s, SA:%s, BlobURL:%s,' % (volumeNo,diskName,storageAccountName,mediaLink))

        # データボリュームを削除
        # 例外が発生しない限りは、削除に失敗していても成功を返す
#        status = self.client.deleteDataVolume(storageAccountName, mediaLink)
        status = self.client._deleteDisk(diskName)
        self.logger.info('      Delete data volume DISK_NO: %s, DISK_NAME: %s, Job Status: %s' % (volumeNo, diskName, status))

        return

