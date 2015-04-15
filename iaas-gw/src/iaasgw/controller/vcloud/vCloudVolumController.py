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
from iaasgw.utils.stringUtils import isNotEmpty, isEmpty, isBit
import time
import traceback

class VCloudVolumController(object):
    logger = IaasLogger()
    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, vciaasclient, conn):
        self.client = vciaasclient
        self.conn = conn
        self.platforminfo = platforminfo

    def startVolumes(self, instanceNo) :
        # ボリューム情報の取得
        table = self.conn.getTable("VCLOUD_DISK")
        disks = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))

        for disk in disks :
            self.startVolume(instanceNo, disk["DISK_NO"])

    def startVolume(self, instanceNo, diskNo) :
        table = self.conn.getTable("VCLOUD_DISK")
        disk = self.conn.selectOne(table.select(table.c.DISK_NO==diskNo))

        #VCloud_INSTANCE 取得
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.INSTANCE_NO==instanceNo))

        # 取得できない場合はスキップ
        if (disk is None):
            return

        # ボリュームのアタッチ
        self.attachVolume(vcInstance, disk)

        #起動スクリプトを待つ
        time.sleep(10)

    def stopVolumes(self, instanceNo) :
        # ボリューム情報の取得
        disks = self.getVCDisk(instanceNo)

        for disk in disks :
            self.stopVolume(instanceNo, disk["DISK_NO"])

    def stopVolume(self, instanceNo, diskNo):
        table = self.conn.getTable("VCLOUD_DISK")
        disk = self.conn.selectOne(table.select(table.c.DISK_NO==diskNo))
        #VCloud_INSTANCE 取得
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.INSTANCE_NO==instanceNo))

        # DISKNAMEがない場合はスキップ
        if (isEmpty(disk["DISK_ID"])):
            return

        try :
            # ボリュームのデタッチ
            self.detachVolume(vcInstance, disk)

        except Exception, e:
            self.logger.error(traceback.format_exc())
            # 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
            self.logger.warn(e.massage);

            updateDict = self.conn.selectOne(table.select(table.c.DISK_NO==diskNo))
            updateDict["DISK_ID"] = None
            updateDict["ATTACHED"] = False
            updateDict["UNIT_NO"] = None
            sql = table.update(table.c.DISK_NO ==updateDict["DISK_NO"], values=updateDict)
            self.conn.execute(sql)


    def getVCDisk(self, instanceNo) :
        table = self.conn.getTable("VCLOUD_DISK")
        disks = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))

        if (disks or len(disks) < 1) :
            return disks;

        # Platformのチェック
        retDisks = []
        for disk in disks:
            # PlatformNoが異なる場合、データ不整合なので警告ログを出す
            if (self.client.getPlatformNo() != disk["PLATFORM_NO"]) :
                self.logger.warn(None, "EPROCESS-000725",[disk["DISK_ID"], disk["PLATFORM_NO"], self.client.getPlatformNo()])
            else :
                retDisks.append(disk)

        return retDisks;

    def attachVolume(self, vcInstance, disk):
        #FARM 取得
        tableFarm = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==disk["FARM_NO"]))

        #組織
        vdc = self.client.getUseVdc()
        #マイクラウド
        vApp = self.client.describeMyCloud(vdc, farm["FARM_NAME"])
        #既存VM検索
        vm = self.client.describeInstance(vApp, vcInstance["VM_NAME"])
        # DISKNAMEがある場合は編集確認
        if (isNotEmpty(disk["DISK_ID"])):
            oldDisk = self.client.describeVolume(vm, disk["DISK_ID"])
            #ディスクの実態が確認できない（矛盾発生）場合はそのまま返す
            if oldDisk is None:
                self.logger.info("VOLUME IS NONE")
                return

            #何らかの影響でクラウド側の容量の方が大きい場合はDBを更新し合わせる
            if int(oldDisk.size) > (int(disk["SIZE"]) * 1024) :
                table = self.conn.getTable("VCLOUD_DISK")
                updateDict = self.conn.selectOne(table.select(table.c.DISK_NO==disk["DISK_NO"]))
                updateDict["SIZE"] = int(oldDisk.size)/1024
                sql = table.update(table.c.DISK_NO ==updateDict["DISK_NO"], values=updateDict)
                self.conn.execute(sql)

            #サイズ増
            if (int(disk["SIZE"]) * 1024) > int(oldDisk.size):
                #変更が有れば更新
                self.client.editVolume(vm, disk)
            #いずれにしても処理終了
            return

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==disk["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==vcInstance["INSTANCE_NO"]))
        self.conn.debug(instance["FARM_NO"], disk["COMPONENT_NO"], componentName, vcInstance["INSTANCE_NO"], instance["INSTANCE_NAME"],
                         "VCloudDiskAttach",[instance["INSTANCE_NAME"], disk["DISK_ID"], disk["SIZE"]])

        # ボリュームのアタッチ
        pccdisk = self.client.attachVolume(vm, disk)

        #イベントログ出力
        self.conn.debug(instance["FARM_NO"], disk["COMPONENT_NO"], componentName, vcInstance["INSTANCE_NO"], instance["INSTANCE_NAME"],
                         "VCloudDiskAttachFinish",[instance["INSTANCE_NAME"], disk["DISK_ID"], disk["SIZE"]])

        # データベースの更新
        table = self.conn.getTable("VCLOUD_DISK")
        updateDict = self.conn.selectOne(table.select(table.c.DISK_NO==disk["DISK_NO"]))
        updateDict["DISK_ID"] = pccdisk.name
        updateDict["ATTACHED"] = True
        updateDict["UNIT_NO"] = pccdisk.unitNo
        sql = table.update(table.c.DISK_NO ==updateDict["DISK_NO"], values=updateDict)
        self.conn.execute(sql)


    def detachVolume(self, vcInstance, disk) :
        #FARM 取得
        tableFarm = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==disk["FARM_NO"]))

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==disk["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==vcInstance["INSTANCE_NO"]))
        self.conn.debug(instance["FARM_NO"], disk["COMPONENT_NO"], componentName, vcInstance["INSTANCE_NO"], instance["INSTANCE_NAME"],
                         "VCloudDiskDetach",[instance["INSTANCE_NAME"], disk["DISK_NO"], disk["SIZE"]])

        #組織
        vdc = self.client.getUseVdc()
        #マイクラウド
        self.logger.info(farm["FARM_NAME"])
        vApp = self.client.describeMyCloud(vdc, farm["FARM_NAME"])
        #既存VM検索
        vm = self.client.describeInstance(vApp, vcInstance["VM_NAME"])

        # ボリュームのデタッチ
        self.client.detachVolume(vm, disk["DISK_ID"]);

        #イベントログ出力
        self.conn.debug(instance["FARM_NO"], disk["COMPONENT_NO"], componentName, vcInstance["INSTANCE_NO"], instance["INSTANCE_NAME"],
                         "VCloudDiskDetachFinish",[instance["INSTANCE_NAME"], disk["DISK_ID"], disk["SIZE"]])

        # データベースの更新
        table = self.conn.getTable("VCLOUD_DISK")
        updateDict = self.conn.selectOne(table.select(table.c.DISK_NO==disk["DISK_NO"]))
        updateDict["DISK_ID"] = None
        updateDict["ATTACHED"] = False
        updateDict["UNIT_NO"] = None
        sql = table.update(table.c.DISK_NO ==updateDict["DISK_NO"], values=updateDict)
        self.conn.execute(sql)


