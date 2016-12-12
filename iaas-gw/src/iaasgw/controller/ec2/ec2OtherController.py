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

class ec2OtherController(object):
    logger = IaasLogger()
    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, ec2iaasclient, conn):
        self.platforminfo = platforminfo
        self.client = ec2iaasclient
        self.conn = conn


    def describeSnapshot(self, snapshotId):
        snapshot = self.client.describeSnapshot(snapshotId)
        return snapshot

    def describeSnapshots(self):
        snapshots = self.client.describeSnapshots()
        return snapshots

    def createSnapshot(self, volumeNo):

        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolumes = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        volumeId = awsVolumes['VOLUME_ID']

        snapshot = self.client.createSnapshot(volumeId)

        tableCSSNP = self.conn.getTable("CLOUDSTACK_SNAPSHOT")
        sql = tableCSSNP.insert({"SNAPSHOT_NO":None,
                                "FARM_NO":awsVolumes["FARM_NO"],
                                "PLATFORM_NO":awsVolumes["PLATFORM_NO"],
                                "VOLUME_NO":volumeNo,
                                "SNAPSHOT_ID":snapshot.snapshotId,
                                "CREATE_DATE":snapshot.startTime})

        self.conn.execute(sql)
        return snapshot

    def deleteSnapshot(self, snapshotNo):
        tableAWSSNP = self.conn.getTable("CLOUDSTACK_SNAPSHOT")
        awsSnap = self.conn.selectOne(tableAWSSNP.select(tableAWSSNP.c.SNAPSHOT_NO==snapshotNo))

        self.client.deleteSnapshot(awsSnap["SNAPSHOT_ID"])


    def getPasswordData(self, instanceNo):
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))

        passwd = self.client.getPasswordData(awsInstance["INSTANCE_ID"])
        #前後の改行コードを抜く為改行コードでスプリット
        passwdArray = passwd.splitlines()
        #1つ目が空白になるので2つめを使う
        if passwdArray[0] == '':
            passwd = passwdArray[1]
        else:
            passwd = passwdArray[0]
        return passwd



