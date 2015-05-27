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
from iaasgw.log.log import IaasLogger
import traceback
import time

if __name__ == "__main__":

    logger = IaasLogger()

    argc = len(sys.argv)
    if (argc != 7):
        logger.error('deleteOSandDataDisk.py: Usage: python %s subscription certificateFilePath storageAccount accessKey mediaLink osHardDiskName' % sys.argv[0])
        sys.exit()

    subscription = sys.argv[1]
    certPath = sys.argv[2]
    storageAccount = sys.argv[3]
    accessKey = sys.argv[4]
    mediaLink = sys.argv[5]
    osHardDiskName = sys.argv[6]
  
    #--------------
    # Azureサービスオブジェクトを作成
    sms = ServiceManagementService(subscription, certPath)
    blobService = BlobService(storageAccount, accessKey)

    #--------------
    # コンテナとBlobオブジェクトを取得
    # mediaLinkからBlobオブジェクトを得る
    logger.debug("deleteOSandDataDisk.py: Container and Blob object get mediaLink...(%s)" % mediaLink)

    # # 消すべきBlobの存在チェック

    # # コンテナ一覧を取得
    containerList = blobService.list_containers()
    targetBlob = None

    for container in containerList:
        # # コンテナに含まれるBlob一覧を取得
        blobList = blobService.list_blobs(container.name)
        for blob in blobList:
            # # URIから、先頭のhttp*://を取り除いた文字列を比較
            blobname = blob.url.split('://')[1]
            if blobname == mediaLink.split('://')[1]:
                logger.debug('deleteOSandDataDisk.py: find target blobname: ' + blobname)
                targetBlob = blob
                targetContainer = container

    # # 見つからなければエラー終了
    if (targetBlob is None):
        logger.error('deleteOSandDataDisk.py: target blob(%s) is not found.' % mediaLink.split('://')[1])
        sys.exit()

    #-----------------
    # lease開始
    logger.debug("deleteOSandDataDisk.py: Blob mediaLink of OS or Data Disk leasing...(%s-->%s)" % (targetContainer.name, targetBlob.name))

    prop = targetBlob.properties
        
    # Lease StatusがlockedだったらBlobのリース解放を試みる
    if prop.lease_status == 'locked':
        # unlockedの時に実行すると、 azure.WindowsAzureConflictError
        res = blobService.lease_blob(container_name=targetContainer.name, blob_name=targetBlob.name, x_ms_lease_action='break')
        # (成功すると？){}が返ってくる
        updatedBlob = blobService.get_blob_properties(container_name=targetContainer.name, blob_name=targetBlob.name)

    logger.debug("deleteOSandDataDisk.py: Blob mediaLink of OS or Data Disk leased.(%s-->%s)" % (targetContainer.name, targetBlob.name))

    logger.debug("sleeping... 120sec")
    time.sleep(120)

    #-----------------
    # osHardDiskをOSディスクリストから削除 
#    if ( osHardDiskName is not None ):
    logger.debug("deleteOSandDataDisk.py: OS Disk deleting...(%s)" % osHardDiskName)

    try:
        systemProperty = sms.delete_disk(osHardDiskName, True)
    except WindowsAzureMissingResourceError as e:
        # クラウドサービス/デプロイメントが存在しない(_ERROR_NOT_FOUND)
        logger.error("deleteOSandDataDisk.py: OS Disk deleted.(%s)" % osHardDiskName)
        logger.error(traceback.format_exc())
        sys.exit()
    except WindowsAzureError as e:
        logger.error("deleteOSandDataDisk.py: OS Disk locked.(%s)" % osHardDiskName)
        logger.error(traceback.format_exc())
        sys.exit()
    except Exception:
        # 予期せぬ例外
        logger.error("deleteOSandDataDisk.py: OS Disk unknown exception error.(%s)" % osHardDiskName)
        logger.error(traceback.format_exc())
        sys.exit()

    logger.debug("deleteOSandDataDisk.py: OS Disk deleted.(%s)" % osHardDiskName)
#    else:
#        logger.debug("osHardDiskName is None. OS disk deleting is skip.")

    logger.debug("sleeping... 60sec")
    time.sleep(60)

    #------------------
    # Blobの削除
    logger.debug("deleteOSandDataDisk.py: Blob deleting...(%s-->%s)" % (targetContainer.name, targetBlob.name))

    rs = blobService.delete_blob(container_name=targetContainer.name, blob_name=targetBlob.name)

    logger.debug("deleteOSandDataDisk.py: Blob deleted.(%s-->%s)" % (targetContainer.name, targetBlob.name))

