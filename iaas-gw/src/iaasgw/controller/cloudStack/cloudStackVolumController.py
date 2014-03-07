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
from iaasgw.utils.readIniFile import getDeviceProperty
from iaasgw.utils.stringUtils import isNotEmpty, isEmpty
import time
import traceback

class CloudStackVolumController(object):
    logger = IaasLogger()
    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, cs2iaasclient, conn):
        self.client = cs2iaasclient
        self.conn = conn
        self.platforminfo = platforminfo



    def startVolumes(self, instanceNo) :
        # ボリューム情報の取得
        table = self.conn.getTable("CLOUDSTACK_VOLUME")
        volumes = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))

        for volume in volumes :
            self.startVolume(instanceNo, volume["VOLUME_NO"])


    def startVolume(self, instanceNo, volumeNo) :
        table = self.conn.getTable("CLOUDSTACK_VOLUME")
        volume = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))

        # インスタンスIDがある場合はスキップ
        if (isNotEmpty(volume["INSTANCE_ID"])) :
            return

        if (isEmpty(volume["VOLUME_ID"])) :
            # ボリュームIDがない場合は新規作成
            self.createVolume(instanceNo, volumeNo)

        # ボリュームのアタッチ
        self.attachVolume(instanceNo, volumeNo)
        #ディスク認識の為リブート
        ##self.rebootInstance(instanceNo)
        #起動スクリプトを待つ
        time.sleep(10)


    def stopVolumes(self, instanceNo) :
        # ボリューム情報の取得
        csVolumes = self.geCsVolumes(instanceNo)

        for volume in csVolumes :
            self.stopVolume(instanceNo, volume["VOLUME_NO"])


    def stopVolume(self, instanceNo, volumeNo):
        tableCSVOL = self.conn.getTable("CLOUDSTACK_VOLUME")
        csVolumes = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))

        # ボリュームIDがない場合はスキップ
        if (isEmpty(csVolumes["VOLUME_ID"])):
            return

        # インスタンスIDがない場合はスキップ
        if (isEmpty(csVolumes["INSTANCE_ID"])) :
            return;

        try :
            # ボリュームのデタッチ
            self.detachVolume(instanceNo, volumeNo)

        except Exception, e:
            self.logger.error(traceback.format_exc())
            # 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
            self.logger.warn(e.massage);

            updateDict = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))
            updateDict["STATE"] = "error"
            updateDict["INSTANCE_ID"] = None
            sql = tableCSVOL.update(tableCSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)


    def getCsVolumess(self, instanceNo) :
        tableCSVOL = self.conn.getTable("CLOUDSTACK_VOLUME")
        csVolumess = self.conn.select(tableCSVOL.select(tableCSVOL.c.INSTANCE_NO==instanceNo))

        if (csVolumess or len(csVolumess) < 1) :
            return csVolumess;

        # Platformのチェック
        retVolumes = []
        for csVolumes in csVolumess:
            # PlatformNoが異なる場合、データ不整合なので警告ログを出す
            if (self.client.getPlatformNo() != csVolumes["PLATFORM_NO"]) :
                self.logger.warn(None, "EPROCESS-000725",[csVolumes["NAME"], csVolumes["PLATFORM_NO"], self.client.getPlatformNo()])
            else :
                retVolumes.append(csVolumes)

        return retVolumes;


    def createVolume(self, instanceNo, volumeNo) :
        tableCSVOL = self.conn.getTable("CLOUDSTACK_VOLUME")
        csVolumes = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))


        size = csVolumes["SIZE"]
        snap = csVolumes["SNAPSHOTID"]
        disk = csVolumes["DISKOFFERINGID"]
        iscustomized = True

        # DISKOFFERINGIDを取得
        diskOfferings = self.client.describeDiskOfferings()
        diskOfferings.sort(cmp=lambda x,y: cmp(x["disksize"], y["disksize"]))

        if snap is not None and not snap.isspace():
            # スナップ利用の場合はDiskとサイズは空に
            disk = None
            size = None
        elif disk is not None and not disk.isspace():
            # DISKOFFERINGIDが入力されている場合
            for diskOffering in diskOfferings:
                # DISKOFFERINGIDを確認
                if diskOffering['id'] == disk:
                    iscustomized = diskOffering["iscustomized"]
                    # カスタムサイズが許可されていなければDISKOFFERINGIDの内容に合わせる
                    if not iscustomized:
                        size = diskOffering['disksize']

        elif size is not None or size != 0:
            bestDisk = None
            fixSize = size
            # サイズのみ指定されている場合
            for diskOffering in diskOfferings:
                iscustomized = diskOffering["iscustomized"]
                # カスタムサイズが許可されている物を探す
                if iscustomized:
                    bestDisk = diskOffering['id']
                    fixSize = size
                    break
                else:
                    #最適なサイズを探す
                    if size <= diskOffering["disksize"]:
                        bestDisk = diskOffering['id']
                        fixSize = diskOffering["disksize"]
                        break;

            # 適切な物が見つからない
            if bestDisk == None:
                raise IaasException("Useful Diskoffering is not found")
            else:
                disk = bestDisk
                size = fixSize


        # ボリュームの作成
        volume = self.client.createVolume(csVolumes["NAME"], csVolumes["ZONEID"], size, snap, disk, iscustomized)


        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==csVolumes["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], csVolumes["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                        "CloudStackVolumeCreate",["CLOUDSTACK",])

        # ログ出力
        self.logger.info(None, "IPROCESS-100611", [volume["id"],])

        #イベントログ出力
        self.conn.debug(instance["FARM_NO"], csVolumes["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "CloudStackVolumeCreateFinish",["CLOUDSTACK", csVolumes["VOLUME_ID"], csVolumes["SIZE"]])

        # データベース更新
        updateDict = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))
        updateDict["VOLUME_ID"] = volume["id"]
        updateDict["STATE"] = volume["state"]
        updateDict["SIZE"] = size
        updateDict["SNAPSHOTID"] = snap
        updateDict["DISKOFFERINGID"] = disk

        sql = tableCSVOL.update(tableCSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)

    def attachVolume(self, instanceNo, volumeNo) :
        #CLOUDSTACK_INSTANCE 取得
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))
        instanceId = csInstance["INSTANCE_ID"]

        tableCSVOL = self.conn.getTable("CLOUDSTACK_VOLUME")
        csVolumes = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))
        volumeId = csVolumes["VOLUME_ID"]

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==csVolumes["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], csVolumes["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "CloudStackVolumeAttach",[instance["INSTANCE_NAME"], csVolumes["VOLUME_ID"], csVolumes["DISKOFFERINGID"]])

        # ハイパーバイザ取得
        hypervisor = self.client.getHypervisor(csVolumes["ZONEID"])
        dviceList = getDeviceProperty(hypervisor["name"])
        #以前アタッチされたデバイス
        #preAttachedDev = None
        #if csVolumes["DEVICEID"] is not None:
        #    for key, value in dviceList:
        #        if value == csVolumes["DEVICEID"]:
        #            preAttachedDev = key


        # ボリュームのアタッチ
        #volume = self.client.attachVolume(volumeId, instanceId, preAttachedDev)
        volume = self.client.attachVolume(volumeId, instanceId)

        #アタッチされたデバイス
        attachedDev = None
        for key, value in dviceList:
            if key == str(volume["deviceid"]):
                    attachedDev = value


        # ログ出力
        self.logger.info(None, "IPROCESS-100612", [volumeId, instanceId,])

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==csVolumes["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], csVolumes["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "CloudStackVolumeAttachFinish",[instance["INSTANCE_NAME"], csVolumes["VOLUME_ID"], csVolumes["DISKOFFERINGID"]])

        # データベースの更新
        updateDict = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))
        updateDict["INSTANCE_ID"] = instanceId
        updateDict["STATE"] = volume["state"]
        updateDict["DEVICEID"] = attachedDev
        updateDict["HYPERVISOR"] = hypervisor["name"]
        sql = tableCSVOL.update(tableCSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)


    def detachVolume(self, instanceNo, volumeNo) :
        tableCSVOL = self.conn.getTable("CLOUDSTACK_VOLUME")
        csVolumes = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))
        volumeId = csVolumes["VOLUME_ID"]
        instanceId = csVolumes["INSTANCE_ID"]

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==csVolumes["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], csVolumes["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "CloudStackVolumeDetach",[instance["INSTANCE_NAME"], csVolumes["VOLUME_ID"], csVolumes["DISKOFFERINGID"]])

        # ボリュームのデタッチ
        volume = self.client.detachVolume(volumeId);

        # ログ出力
        self.logger.info(None, "IPROCESS-100613", [volumeId, instanceId,])

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==csVolumes["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], csVolumes["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "CloudStackVolumeDetachFinish",[instance["INSTANCE_NAME"], csVolumes["VOLUME_ID"], csVolumes["DEVICEID"]])

        # データベースの更新
        updateDict = self.conn.selectOne(tableCSVOL.select(tableCSVOL.c.VOLUME_NO==volumeNo))
        updateDict["STATE"] = volume["state"]
        updateDict["INSTANCE_ID"] = None
        updateDict["DEVICEID"] = None
        sql = tableCSVOL.update(tableCSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)

    def rebootInstance(self, instanceNo) :
        #CLOUDSTACK_INSTANCE 取得
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))
        instanceId = csInstance["INSTANCE_ID"]
        # ボリュームのアタッチ
        self.client.rebootInstance(instanceId)
