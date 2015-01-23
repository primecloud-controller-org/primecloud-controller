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
from azure import *
from azure.servicemanagement import *
from azure.storage import BlobService
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
import traceback
import time
import tempfile
import base64
import os
import commands

class AzureIaasClient:

    logger = IaasLogger()
    platformNo = None
    username = None

    sms = None

    def __init__(self, platforminfo, username, subscription, certificate, conn):

        self.platformNo = platforminfo["platformNo"]
        self.username = username
        # XXX: proxyのコード

        # 証明書ファイルを作成
        # XXX: テンポラリファイルの削除をどうするか要検討
        fd = tempfile.NamedTemporaryFile(prefix='pccazurecert',delete=False)
        fd.writelines(certificate)
        fd.close()

        # 接続用オブジェクトの生成
        self.certPath = fd.name
        self.subscription = subscription
        self.sms = ServiceManagementService(subscription, self.certPath)
        self.conn = conn

    def waitAsyncOperationFinished(self, asyncOperation, flg=None):
        requestId = asyncOperation.request_id
        while True:
            # XXX: socket.error: [Errno 104] Connection reset by peer が発生したことがあるので、対処必要
            try:
                operation = self.sms.get_operation_status(requestId)
                self.logger.debug('      Asynchronous Request ID: %s, Status: %s' % \
                    (operation.id, operation.status))
                if operation.status == 'InProgress':
                    if flg == 'del':
                        time.sleep(5)
                    else:
                        time.sleep(10)
                    continue
                elif operation.status == 'Succeeded':
                    break
                else:
                    self.logger.error('%s' % (operation.error.code))
                    self.logger.error('%s' % (operation.error.message))
                    break
            except:
                self.logger.error(traceback.format_exc())
                return

        return operation.status

    def generateOSHardDiskConfig(self, mediaLink, imageName):
        return OSVirtualHardDisk(imageName, mediaLink)

    def generateLinuxConfig(self, hostName, userName, userPassword, customData):
        disableSshPasswordAuthentication = False
        return LinuxConfigurationSet(hostName, userName, userPassword, \
            disableSshPasswordAuthentication, custom_data=base64.b64encode(customData))

    def generateWindowsConfig(self, hostName, userName, userPassword, customData):
        resetPasswordOnFirstLogon = False
        enableAutomaticUpdates = False
        timeZone = 'Tokyo Standard Time'
        windowsConfig = WindowsConfigurationSet(hostName, userName, userPassword, \
            resetPasswordOnFirstLogon, enableAutomaticUpdates, timeZone, custom_data=base64.b64encode(customData))
        windowsConfig.domain_join = None
        return windowsConfig

    def generateNetworkConfig(self, subnetID):
        network = ConfigurationSet()
        network.configuration_set_type = 'NetworkConfiguration'
        network.subnet_names.append(subnetID)
        return network

    def getOsImage(self, imageName):
        # XXX: なぜか、WindowsAzureConflictErrorが発生することがある
        osImages = self.sms.list_os_images()
        for osImage in osImages:
            # XXX: カテゴリが"User"の場合、ストレージアカウントの考慮も必要
            if osImage.name == imageName:
                return osImage
        # 名前の一致するイメージが存在しない
        return None

    def identifyImageOsType(self, imageName):
        osImage = self.getOsImage(imageName)
        if osImage is None:
            # 名前の一致するイメージが存在しない
            return None
        return osImage.os

    def _getStorageAccountKey(self, storageAccount):
        # ストレージアカウントのキーを取得
        res = self.sms.get_storage_account_keys(storageAccount)
        return res.storage_service_keys.primary

    def _getBlobFromMediaLink(self, blobService, mediaLink):
        # コンテナ一覧を取得
        containerList = blobService.list_containers()

        for container in containerList:
            # コンテナに含まれるBlob一覧を取得
            blobList = blobService.list_blobs(container.name)
            for blob in blobList:
                # URIから、先頭のhttp*://を取り除いた文字列を比較
                if blob.url.split('://')[1] == mediaLink.split('://')[1]:
                    return (container, blob)
            else:
                return (None, None)

    def _deleteBlob(self, storageAccount, mediaLink):
        primary = self._getStorageAccountKey(storageAccount)
        # BlobServiceオブジェクトを作成
        blobService = BlobService(storageAccount, primary)

        (container, blob) = self._getBlobFromMediaLink(blobService, mediaLink)

        rs = blobService.delete_blob(container_name=container.name, blob_name=blob.name)
        try:
            updatedBlob = blobService.get_blob_properties(container_name=container.name, blob_name=blob.name)
        except WindowsAzureMissingResourceError as e:
            return True
        return False

    def _leaseBlob(self, storageAccount, mediaLink):
        primary = self._getStorageAccountKey(storageAccount)
        # BlobServiceオブジェクトを作成
        blobService = BlobService(storageAccount, primary)

        (container, blob) = self._getBlobFromMediaLink(blobService, mediaLink)

        prop = blob.properties

        # Lease StatusがlockedだったらBlobのリース解放を試みる
        if prop.lease_status == 'locked':
            # unlockedの時に実行すると、 azure.WindowsAzureConflictError
            res = blobService.lease_blob(container_name=container.name, blob_name=blob.name, x_ms_lease_action='break')
            # (成功すると？){}が返ってくる
            updatedBlob = blobService.get_blob_properties(container_name=container.name, blob_name=blob.name)

    def _existDisk(self, osHardDiskName):
        diskList = self.sms.list_disks()
        for disk in diskList:
            if disk.name == osHardDiskName:
                return True
        else:
            return False

    def _deleteDisk(self, osHardDiskName):
        self.logger.debug("Blob of OS Disk deleting...(%s)" % osHardDiskName)
        try:
            # 指定されたデータまたはオペレーティング システム ディスクをイメージ リポジトリから削除
            # ディスクに関連付けられた BLOB も削除
            asyncOperation = self.sms.delete_disk(osHardDiskName, True)
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000916", [osHardDiskName])
        except WindowsAzureError as e:
            # XXX: Blobがロックされている場合を想定しているが、他にもこのエラーにかかる可能性あり
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000916", [osHardDiskName])
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000916", [osHardDiskName])
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation, flg='del')

        if operationStatus != 'Succeeded':
            raise IaasException("EPROCESS-000916", [osHardDiskName])

        self.logger.debug("Blob of OS Disk deleted.(%s)" % osHardDiskName)
        return operationStatus

    def getOsHardDisk(self, cloudService, roleName):
        deploymentName = cloudService
        # OS Virtual Hard Diskの情報は、get_deployment_by_slotでは取得できない。get_roleを使用する。
        try:
            systemProperty = self.sms.get_role(cloudService, deploymentName, roleName)
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            return systemProperty.os_virtual_hard_disk.disk_name

    def getMediaLink(self, cloudService, roleName):
        deploymentName = cloudService
        # OS Virtual Hard Diskの情報は、get_deployment_by_slotでは取得できない。get_roleを使用する。
        try:
            systemProperty = self.sms.get_role(cloudService, deploymentName, roleName)
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            return systemProperty.os_virtual_hard_disk.media_link

    def getDiskNameAttachedTo(self, cloudService, roleName, lun):
        deploymentName = cloudService
        try:
            systemProperty = self.sms.get_data_disk(cloudService, deploymentName, roleName, lun)
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            return systemProperty.disk_name

    def createAddDataDisk(self, cloudService, roleName, mediaLink, size, lun):
        deploymentName = cloudService
        # TODO: アタッチコンフリクト問題に暫定的に対応
        for i in range(0, 6):
            try:
                asyncOperation = self.sms.add_data_disk(service_name = cloudService, \
                deployment_name = deploymentName, role_name = roleName, \
                lun = lun, host_caching = 'ReadOnly', media_link = mediaLink, \
                disk_label = None, disk_name = None, logical_disk_size_in_gb = size, \
                #disk_label = '%sL' % (diskName), disk_name = '%sN' % (diskName), logical_disk_size_in_gb = size, \
                source_media_link = None)
            except WindowsAzureMissingResourceError as e:
                # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
                self.logger.error(traceback.format_exc())
                raise IaasException("EPROCESS-000915", [roleName])
            except WindowsAzureConflictError as e:
                # TODO: アタッチコンフリクト問題に暫定的に対応
                if i == 5:
                    # 既に存在する
                    self.logger.error(traceback.format_exc())
                    raise IaasException("EPROCESS-000915", [roleName])
                else:
                    time.sleep(30)
                    continue
            except Exception:
                # 予期せぬ例外
                self.logger.error(traceback.format_exc())
                raise IaasException("EPROCESS-000915", [roleName])
            else:
                operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000915", [roleName])

            return operationStatus

    def addDataDisk(self, cloudService, roleName, diskName, lun):
        deploymentName = cloudService
        # TODO: アタッチコンフリクト問題に暫定的に対応
        for i in range(0, 6):
            try:
                asyncOperation = self.sms.add_data_disk(service_name = cloudService, \
                deployment_name = deploymentName, role_name = roleName, \
                lun = lun, host_caching = 'ReadOnly', media_link = None, \
                disk_label = None, disk_name = diskName, logical_disk_size_in_gb = None, \
                #disk_label = '%sL' % (diskName), disk_name = '%sN' % (diskName), logical_disk_size_in_gb = size, \
                source_media_link = None)
            except WindowsAzureMissingResourceError as e:
                # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
                self.logger.error(traceback.format_exc())
                raise IaasException("EPROCESS-000910", [roleName, diskName])
            except WindowsAzureConflictError as e:
                # TODO: アタッチコンフリクト問題に暫定的に対応
                if i == 5:
                    # 既に存在する
                    self.logger.error(traceback.format_exc())
                    raise IaasException("EPROCESS-000910", [roleName, diskName])
                else:
                    time.sleep(30)
                    continue
            except Exception:
                # 予期せぬ例外
                self.logger.error(traceback.format_exc())
                raise IaasException("EPROCESS-000910", [roleName, diskName])
            else:
                operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000910", [roleName, diskName])

            return operationStatus

    def deleteDataDisk(self, cloudService, roleName, diskName, lun):
        deploymentName = cloudService
        # TODO: デタッチコンフリクト問題に暫定的に対応
        for i in range(0, 6):
            try:
                asyncOperation = self.sms.delete_data_disk(service_name = cloudService, \
                deployment_name = deploymentName, role_name = roleName, \
                lun = lun)
            except WindowsAzureMissingResourceError as e:
                # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
                self.logger.error(traceback.format_exc())
                raise IaasException("EPROCESS-000911", [roleName, diskName])
            except WindowsAzureConflictError as e:
                # TODO: アタッチコンフリクト問題に暫定的に対応
                if i == 5:
                    # 既に存在する
                    self.logger.error(traceback.format_exc())
                    raise IaasException("EPROCESS-000911", [roleName, diskName])
                else:
                    time.sleep(30)
                    continue
            except Exception:
                # 予期せぬ例外
                self.logger.error(traceback.format_exc())
                raise IaasException("EPROCESS-000911", [roleName, diskName])
            else:
                operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000911", [roleName, diskName])

            return operationStatus

    def getVirtualMachineType(self, cloudService, roleName):
        try:
            systemProperty = self.sms.get_deployment_by_slot(cloudService, 'production')
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            for role in systemProperty.role_instance_list:
                if role.role_name == roleName:
                    # インスタンスが見つかったらインスタンスサイズを返す
                    return role.instance_size
            # インスタンスが見つからない場合はNoneを返す
            return None

    def getVirtualMachineIpAddress(self, cloudService, roleName):
        try:
            systemProperty = self.sms.get_deployment_by_slot(cloudService, 'production')
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            for role in systemProperty.role_instance_list:
                if role.role_name == roleName:
                    # インスタンスが見つかったらステータスを返す
                    return role.ip_address
            # インスタンスが見つからない場合はNoneを返す
            return None

    def updateVirtualMachineType(self, cloudService, roleName, roleSize, networkConfig, \
                                 availabilitySet):
        deploymentName = cloudService
        try:
          asyncOperation = self.sms.update_role( \
          service_name=cloudService, deployment_name=deploymentName, \
          role_name=roleName, role_size=roleSize, network_config=networkConfig, \
          availability_set_name=availabilitySet)
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            return None
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            # 異常系テストコード
            #operationStatus = 'Failed'
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000913", [roleName])

        return roleSize

    def getVirtualMachineStatus(self, cloudService, roleName):
        # ステータスの取得に失敗した場合はNoneを返す。
        try:
            systemProperty = self.sms.get_deployment_by_slot(cloudService, 'production')
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        # XXX: socket.error: [Errno 104] Connection reset by peer が発生したことがあるので、対処必要
        else:
            for role in systemProperty.role_instance_list:
                if role.role_name == roleName:
                    # インスタンスが見つかったらステータスを返す
                    return role.instance_status
            # インスタンスが見つからない場合はNoneを返す
            return None

    def waitVirtualMachineStatus(self, cloudService, roleName, expectedStatus):
        # XXX: タイムアウト実装必要
        while True:
            status = self.getVirtualMachineStatus(cloudService, roleName)
            self.logger.debug('      Virtual machine: %s, Status: %s' % (roleName, status))
            if status == expectedStatus:
                return status
            elif status == 'ProvisioningFailed':
                # 失敗
                return status
            elif status is None:
                return status
            else:
                time.sleep(10)
                continue

    def waitVirtualMachineStatusStoppedVM(self, cloudService, roleName):
            return self.waitVirtualMachineStatus(cloudService, roleName, 'StoppedVM')

    def waitVirtualMachineStatusReadyRole(self, cloudService, roleName):
            return self.waitVirtualMachineStatus(cloudService, roleName, 'ReadyRole')

    def listVirtualMachines(self, cloudService):
        try:
            systemProperty = self.sms.get_deployment_by_slot(cloudService, 'production')
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            return systemProperty.role_instance_list

    def getCloudServiceStatus(self, cloudService):
        try:
            systemProperty = self.sms.get_hosted_service_properties(cloudService)
        except WindowsAzureMissingResourceError as e:
            # クラウドサービスが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            # クラウドサービスが存在する場合はstatusを返す
            return systemProperty.hosted_service_properties.status

    def waitCloudServiceStatusCreated(self, cloudService):
        while True:
            status = self.getCloudServiceStatus(cloudService)
            if status == 'Creating':
                time.sleep(10)
                continue
            elif status is None:
                return None
            else:
                return status

    def createCloudService(self, cloudService, affinityGroup=None):
        cloudServiceLabel = cloudService
        cloudServiceDesc = cloudService
        try:
            # クラウドサービス作成
            res = self.sms.create_hosted_service(service_name=cloudService, \
            label=cloudServiceLabel, description=cloudServiceDesc, \
            affinity_group=affinityGroup)
            if res is not None:
                # エラー(その他)
                self.logger.error(traceback.format_exc())
                raise
        except WindowsAzureConflictError as e:
            # 既に存在する
            self.logger.error(traceback.format_exc())
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            raise
        return self.waitCloudServiceStatusCreated(cloudService)

    def getStorageAccountStatus(self, storageAccount):
        try:
            systemProperty = self.sms.get_storage_account_properties(storageAccount)
        except WindowsAzureMissingResourceError as e:
            # ストレージアカウントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            # ストレージアカウントが存在する場合はstatusを返す
            return systemProperty.storage_service_properties.status

    def createStorageAccount(self, storageAccount, affinityGroup=None):
        storageAccountLabel = storageAccount
        storageAccountDesc = storageAccount
        try:
            # ストレージアカウント作成
            asyncOperation = self.sms.create_storage_account(service_name=storageAccount, \
            description=storageAccountDesc, label=storageAccountLabel, \
            affinity_group=affinityGroup)
        except WindowsAzureConflictError as e:
            # 既に存在する
            self.logger.error(traceback.format_exc())
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            raise
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            # 異常系テストコード
            #operationStatus = 'Failed'
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000917", [storageAccount])

        return self.getStorageAccountStatus(storageAccount)

    def getProductionDeploymentStatus(self, cloudService):
        try:
            systemProperty = self.sms.get_deployment_by_slot(cloudService, 'production')
        except WindowsAzureMissingResourceError as e:
            # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            return systemProperty.status

    def createVirtualMachineDeployment(self, cloudService, instanceName, osConfig, osHardDisk, \
        instanceType, networkConfig, networkName, availabilitySet):
        deploymentName = cloudService
        deploymentLabel = deploymentName
        try:
          asyncOperation = self.sms.create_virtual_machine_deployment( \
          service_name=cloudService, deployment_name=deploymentName, \
          deployment_slot='production', label=deploymentLabel, \
          role_name=instanceName, system_config=osConfig, \
          os_virtual_hard_disk=osHardDisk, role_size=instanceType, \
          network_config=networkConfig, availability_set_name=availabilitySet, \
          virtual_network_name=networkName)
        except WindowsAzureConflictError as e:
            # 既に存在する
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000903", [instanceName])
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000903", [instanceName])
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            # 異常系テストコード
            #operationStatus = 'Failed'
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000903", [instanceName])

        return self.waitVirtualMachineStatusReadyRole(cloudService, instanceName)
        #return self.getVirtualMachineStatus(cloudService, instanceName)

    def addRole(self, cloudService, instanceName, osConfig, osHardDisk, \
        instanceType, networkConfig, networkName, availabilitySet):
        # XXX: ネットワーク名が既存デプロイメントのネットワークと一致することを確認すべきか。
        deploymentName = cloudService
        try:
              asyncOperation = self.sms.add_role( \
              service_name=cloudService, deployment_name=deploymentName, \
              role_name=instanceName, system_config=osConfig, \
              os_virtual_hard_disk=osHardDisk, role_size=instanceType, \
              network_config=networkConfig, availability_set_name=availabilitySet)
        except WindowsAzureConflictError as e:
            # 既に存在する
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000905", [instanceName])
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000905", [instanceName])
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            # 異常系テストコード
            #operationStatus = 'Failed'
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000905", [instanceName])

        return self.waitVirtualMachineStatusReadyRole(cloudService, instanceName)
        #return self.getVirtualMachineStatus(cloudService, instanceName)

    def startVirtualMachine(self, cloudService, roleName):
        # XXX: クラウドサービス名＝デプロイメント名を前提
        deploymentName = cloudService
        try:
          asyncOperation = self.sms.start_role( \
          service_name=cloudService, deployment_name=deploymentName, \
          role_name=roleName)
        #except WindowsAzureConflictError as e:
        #    # 既に存在する
        #    self.logger.error(traceback.format_exc())
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000905", [roleName])
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            # 異常系テストコード
            #operationStatus = 'Failed'
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000905", [roleName])

        return self.waitVirtualMachineStatusReadyRole(cloudService, roleName)

    def stopVirtualMachine(self, cloudService, roleName):
        # XXX: クラウドサービス名＝デプロイメント名を前提
        deploymentName = cloudService
        try:
          asyncOperation = self.sms.shutdown_role( \
          service_name=cloudService, deployment_name=deploymentName, \
          role_name=roleName)
        #except WindowsAzureConflictError as e:
        #    # 既に存在する
        #    self.logger.error(traceback.format_exc())
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000907", [roleName])
        else:
            # XXX: 非同期操作が Failed したことがあった。Failしたあと、VMの状態がStoppedVMになるまで待つのは不適切。
            # XXX: 非同期操作は Failed になったのに、最終的にVMは停止したことがあった。ただし、しばらくの間、VMのstatusはReadyRoleだった。
            operationStatus = self.waitAsyncOperationFinished(asyncOperation)
            # 異常系テストコード
            #operationStatus = 'Failed'
            if operationStatus != 'Succeeded':
                raise IaasException("EPROCESS-000907", [roleName])

        return self.waitVirtualMachineStatusStoppedVM(cloudService, roleName)

    def _deleteDeployment(self, cloudService, roleName, \
                          instanceNo, farmNo, instanceInfoInstanceName):
        deploymentName = cloudService
        try:
            # 仮想マシン、オペレーティング システム ディスク、
            # 接続されたデータ ディスク、およびディスクの元の BLOB もストレージから削除
            asyncOperation = self.sms.delete_deployment( \
            service_name=cloudService, deployment_name=deploymentName)
            # 異常系テストコード
            #raise Exception
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            self.conn.error(farmNo, None, None, instanceNo, \
                            instanceInfoInstanceName, "AzureInstanceDeleteFail",["AZURE", roleName, ''])
            raise IaasException("EPROCESS-000904", [roleName])
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation, flg='del')

        return operationStatus

    def _deleteRole(self, cloudService, roleName, \
                    instanceNo, farmNo, instanceInfoInstanceName):
        deploymentName = cloudService
        try:
            # 仮想マシン、オペレーティング システム ディスク、
            # 接続されたデータ ディスク、およびディスクの元の BLOB もストレージから削除
            asyncOperation = self.sms.delete_role( \
            service_name=cloudService, deployment_name=deploymentName, \
            role_name=roleName)
            # 異常系テストコード
            #raise Exception
        except Exception:
            # 予期せぬ例外
            self.logger.error(traceback.format_exc())
            self.conn.error(farmNo, None, None, instanceNo, \
                            instanceInfoInstanceName, "AzureInstanceDeleteFail",["AZURE", roleName, ''])
            raise IaasException("EPROCESS-000904", [roleName])
        else:
            operationStatus = self.waitAsyncOperationFinished(asyncOperation, flg='del')

        return operationStatus

    def deleteVirtualMachine(self, cloudService, instanceName, storageAccount, \
                             instanceNo, farmNo, instanceInfoInstanceName):
        mediaLink = self.getMediaLink(cloudService, instanceName)
        self.logger.debug('      Virtual Machine(role or deployment) %s is going to be deleted' % (instanceName))
        self.logger.debug('      Media Link: %s' % (mediaLink))

        roleList = self.listVirtualMachines(cloudService)
        if roleList is None:
            return None
        if len(roleList) == 1:
            # 最後のVMなので、Delete Deploymentを使う必要あり
            status = self._deleteDeployment(cloudService, instanceName, \
                                            instanceNo, farmNo, instanceInfoInstanceName)
        else:
            status = self._deleteRole(cloudService, instanceName, \
                                      instanceNo, farmNo, instanceInfoInstanceName)
        # 異常系テストコード
        #status = 'Failed'
        if status != 'Succeeded':
            self.logger.debug('      Async job did not succeed. Status: %s' % (status))
            self.conn.error(farmNo, None, None, instanceNo, \
                            instanceInfoInstanceName, "AzureInstanceDeleteFail",["AZURE", instanceName, status])
            raise IaasException("EPROCESS-000904", [instanceName])

        return status

    def createVirtualMachine(self, cloudService, instanceName, osConfig, osHardDisk, \
        instanceType, networkConfig, networkName, availabilitySet):

        deploymentStatus = self.getProductionDeploymentStatus(cloudService)
        if deploymentStatus is None:
            return self.createVirtualMachineDeployment(cloudService, instanceName, osConfig, osHardDisk, instanceType, networkConfig, networkName, availabilitySet)
        else:
            # XXX: Running以外の時の扱いを決める必要あり
            return self.addRole(cloudService, instanceName, osConfig, osHardDisk, instanceType, networkConfig, networkName, availabilitySet)

    def deleteDataVolume(self, storageAccount, mediaLink):
        self._leaseBlob(storageAccount, mediaLink)
        time.sleep(120)
        #XXX: 下記エラーへの対処
        #WindowsAzureError: Unknown error (Bad Request)
        #<Error xmlns="http://schemas.microsoft.com/windowsazure" xmlns:i="http://www.w3.org/2001/XMLSchema-instance"><Code>BadRequest</Code><Message>A disk with name cloud-pccdev10-azure-dev10-user01_AZURE-J02-0-201311181315360171 is currently in use by virtual machine azure-dev10-user01_AZURE-J02 running within hosted service cloud-pccdev10, deployment cloud-pccdev10.</Message></Error>

        self._deleteBlob(storageAccount, mediaLink)

        return

    def deleteOSandDataDisk(self, osHardDiskName, storageAccount, mediaLink):
        accessKeys = self.sms.get_storage_account_keys(storageAccount)
        accessKey  = accessKeys.storage_service_keys.primary

        if (osHardDiskName == None):
            parameter = self.subscription +' '+ self.certPath +' '+ storageAccount +' '+ accessKey +' '+ mediaLink +' '
        else:
            parameter = self.subscription +' '+ self.certPath +' '+ storageAccount +' '+ accessKey +' '+ mediaLink +' '+ osHardDiskName
        dir = os.path.dirname(__file__)
        self.logger.info('called deleteOSandDataDisk process. python %s/azuredeleteOSandDataDisk.py %s &' % (dir, parameter) )
        os.system('python %s/azuredeleteOSandDataDisk.py %s &' % (dir, parameter) )

        return

    def listVirtualNetworkSites(self):
        try:
            networks = self.sms.list_virtual_network_sites()
        except WindowsAzureMissingResourceError as e:
            # ネットワーク情報が存在しない(_ERROR_NOT_FOUND)
            return None
        else:
            return networks
