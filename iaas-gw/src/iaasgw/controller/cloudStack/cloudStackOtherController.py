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
from iaasgw.log.log import IaasLogger

############################
#
# 現在未使用の為作成のみ
#
############################

class CloudStackOtherController(object):
    logger = IaasLogger()
    client = None
    conn = None
    platforminfo = None


    def __init__(self, platforminfo, cs2iaasclient, conn):
        self.client = cs2iaasclient
        self.conn = conn
        self.platforminfo = platforminfo


    def describeSnapshot(self, snapshotId):
        snapshot = self.client.describeSnapshot(snapshotId)
        return snapshot

    def describeSnapshots(self):
        snapshots = self.client.describeSnapshots()
        return snapshots

    def createSnapshot(self, volumeNo):
        tableCSVOL = self.conn.getTable("CLOUDSTACK_VOLUME")
        csVolumes = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))
        volumeId = csVolumes['VOLUME_ID']

        snapshot = self.client.createSnapshot(volumeId)

        tableCSSNP = self.conn.getTable("CLOUDSTACK_SNAPSHOT")
        sql = tableCSSNP.insert({"SNAPSHOT_NO":None,
                                "SNAPSHOT_ID":snapshot["id"],
                                "FARM_NO":csVolumes["FARM_NO"],
                                "PLATFORM_NO":csVolumes["PLATFORM_NO"],
                                "CREATE_DATE":snapshot["created"],
                                "VOLUMEID":volumeId,
                                })

        self.conn.execute(sql)
        return snapshot["id"]

    def deleteSnapshot(self, snapshotNo):
        tableCSSNP = self.conn.getTable("CLOUDSTACK_SNAPSHOT")
        csSnap = self.conn.selectOne(tableCSSNP.select(tableCSSNP.c.SNAPSHOT_NO==snapshotNo))

        self.client.deleteSnapshot(csSnap["SNAPSHOT_ID"])


    def getPasswordData(self, instanceNo):
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))
        passwd = self.client.getPasswordData(csInstance["INSTANCE_ID"])
        #改行コードを抜く為改行コードでスプリット
        passwdArray = passwd.splitlines()
        rtPassword = ""
        for passline in passwdArray:
            rtPassword = rtPassword + passline

        return rtPassword


