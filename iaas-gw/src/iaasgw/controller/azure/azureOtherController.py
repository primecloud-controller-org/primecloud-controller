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
import traceback


class azureOtherController(object):
    logger = IaasLogger()

    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, azureiaasclient, conn):
        self.client = azureiaasclient
        self.conn = conn
        self.platforminfo = platforminfo

    def createCloudServiceFor(self, platformNo):
        platformTable = self.conn.getTable("PLATFORM_AZURE")
        platformInfo = self.conn.selectOne(platformTable.select\
            (platformTable.c.PLATFORM_NO==platformNo))

        # クラウドサービスの存在確認
        cloudService = platformInfo['CLOUD_SERVICE_NAME']
        status = self.client.getCloudServiceStatus(cloudService)
        self.logger.info('      Cloud Service: %s, Status: %s' % (cloudService, status))

        # クラウドサービスが存在しない場合、作成する
        if status is None:
            affinityGroup = platformInfo['AFFINITY_GROUP_NAME']
            self.logger.debug('      Trying to create Cloud Service: %s' % (cloudService))
            status = self.client.createCloudService(cloudService, affinityGroup)
            self.logger.info('      Cloud Service: %s, Status: %s' % (cloudService, status))
        else:
            # XXX: ステータスが作成完了以外の場合の処理は要検討
            pass

    def createStorageAccountFor(self, platformNo):
        platformTable = self.conn.getTable("PLATFORM_AZURE")
        platformInfo = self.conn.selectOne(platformTable.select\
            (platformTable.c.PLATFORM_NO==platformNo))

        # ストレージアカウントの存在確認
        # XXX: 名前が要件を満たしているか確認必要
        # Storage account names must be between 3 and 24 characters in length and use numbers and lower-case letters only.
        storageAccount = platformInfo['STORAGE_ACCOUNT_NAME']
        self._verifyStorageAccountName(storageAccount)
        status = self.client.getStorageAccountStatus(storageAccount)
        self.logger.info('      Storage Account: %s, Status: %s' % (storageAccount, status))

        # ストレージアカウントが存在しない場合、作成する
        if status is None:
            affinityGroup = platformInfo['AFFINITY_GROUP_NAME']
            self.logger.debug('      Trying to create Storage Account: %s' % (storageAccount))
            status = self.client.createStorageAccount(storageAccount, affinityGroup)
            self.logger.info('      Storage Account: %s, Status: %s' % (storageAccount, status))
        else:
            # XXX: ステータスが作成完了以外の場合の処理は要検討
            pass

    def _verifyStorageAccountName(self, storageAccount):
        # XXX:
        pass
