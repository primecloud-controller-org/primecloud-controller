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
import traceback


class OpenStackVolumeController(object):

    logger = IaasLogger()

    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, openstackiaasclient, conn):
        self.client = openstackiaasclient
        self.conn = conn
        self.platforminfo = platforminfo

    def startVolumes(self, instanceNo) :
        # ボリューム情報の取得
        table = self.conn.getTable("OPENSTACK_VOLUME")
        volumes = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))

        for volume in volumes :
            self.startVolume(instanceNo, volume["VOLUME_NO"])

    def startVolume(self, instanceNo, volumeNo) :
        table = self.conn.getTable("OPENSTACK_VOLUME")
        volume = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))

        # インスタンスIDがある場合はスキップ
        if (isNotEmpty(volume["INSTANCE_ID"])) :
            return

        if (isEmpty(volume["VOLUME_ID"])) :
            # ボリュームIDがない場合は新規作成
            self.createVolume(instanceNo, volumeNo)

            # ボリュームの作成待ち
            self.waitCreateVolume(instanceNo, volumeNo)

        # ボリュームのアタッチ
        self.attachVolume(instanceNo, volumeNo)

        # ボリュームのアタッチ待ち
        self.waitAttachVolume(instanceNo, volumeNo)

    def stopVolumes(self, instanceNo) :
        # ボリューム情報の取得
        osVolumes = self.getAwsVolumes(instanceNo)

        for volume in osVolumes :
            self.stopVolume(instanceNo, volume["VOLUME_NO"])

    def stopVolume(self, instanceNo, volumeNo):
        table = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))

        # ボリュームIDがない場合はスキップ
        if (isEmpty(osVolume["VOLUME_ID"])):
            return

        # インスタンスIDがない場合はスキップ
        if (isEmpty(osVolume["INSTANCE_ID"])) :
            return;

        try :
            # ボリュームのデタッチ
            self.detachVolume(instanceNo, volumeNo)

            # ボリュームのデタッチ待ち
            self.waitDetachVolume(instanceNo, volumeNo)
        except Exception, e:
            self.logger.error(traceback.format_exc())
            # 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
            self.logger.warn(e.massage);

            table = self.conn.getTable("OPENSTACK_VOLUME")
            updateDict = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))
            updateDict["STATUS"] = "error"
            updateDict["INSTANCE_ID"] = None
            sql = table.update(table.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)

    def waitVolume(self, volumeId) :
        # スナップショットの処理待ち
        volume = None
        while (True):
            volume = self.client.describeVolume(volumeId);
            status = volume.status

            if status == "available" or status == "in-use" or status == "error":
                break

            if status != "creating" and status != "deleting" and status != "attaching" and status !="detaching":
                #予期しないステータス
                raise IaasException("EPROCESS-001012", [volumeId, status,])
        return volume;

    def createVolume(self, instanceNo, volumeNo) :
        tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))

        # ボリュームの作成
        volume = self.client.createVolume(osVolume["VOLUME_NAME"], osVolume["AVAILABILITY_ZONE"], osVolume["SIZE"], osVolume["SNAPSHOT_ID"])

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==osVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], osVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"], "OpenStackVolumeCreate",["OPENSTACK",])

        # データベース更新
        updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        updateDict["VOLUME_ID"] = volume.id
        updateDict["STATUS"] = volume.status
        sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)

    def waitCreateVolume(self, instanceNo, volumeNo) :
        tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        volumeId = osVolume["VOLUME_ID"]

        # ボリュームの作成待ち
        volume = None
        try :
            volume = self.waitVolume(volumeId)

            if volume.status != "available":
                #ボリューム作成失敗時
                raise IaasException("EPROCESS-001013", [volumeId, volume.status,])

            # ログ出力
            self.logger.info(None, "IPROCESS-100822", [volumeId,])

        except Exception:
            self.logger.error(traceback.format_exc())
            # ボリューム作成失敗時
            osVolume["VOLUME_ID"] = None
            osVolume["STATUS"] = None
            sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==osVolume["VOLUME_NO"], values=osVolume)
            self.conn.execute(sql)
            raise


        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==osVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], osVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "OpenStackVolumeCreateFinish",["OPENSTACK", osVolume["VOLUME_ID"], osVolume["SIZE"]])

        # データベース更新
        updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        updateDict["STATUS"] = volume.status
        sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)

    def deleteVolume(self, volumeId) :
        tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_ID==volumeId))
        volumeNo = osVolume["VOLUME_NO"]

        self.client.deleteVolume(osVolume["VOLUME_ID"])

        # データベース更新
        updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        updateDict["VOLUME_ID"] = None
        updateDict["STATUS"] = None
        sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)

    def attachVolume(self, instanceNo, volumeNo) :
        #OPENSTACK_INSTANCE 取得
        tableOSINS = self.conn.getTable("OPENSTACK_INSTANCE")
        instance = self.conn.selectOne(tableOSINS.select(tableOSINS.c.INSTANCE_NO==instanceNo))
        instanceId = instance["INSTANCE_ID"]

        tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        volumeId = osVolume["VOLUME_ID"]
        device = None

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==osVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], osVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "OpenStackVolumeAttach",[instance["INSTANCE_NAME"], osVolume["VOLUME_ID"], device])

        # ボリュームのアタッチ：デバイスの値はNoneとし、自動で割り当てる
        self.client.attachVolume(volumeId, instanceId, device)

        # データベースの更新
        osVolume["INSTANCE_ID"] = instanceId
        sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==osVolume["VOLUME_NO"], values=osVolume)
        self.conn.execute(sql)

    def waitAttachVolume(self, instanceNo, volumeNo) :
        tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        volumeId = osVolume["VOLUME_ID"]
        instanceId = osVolume["INSTANCE_ID"]

        volume = None
        device = None
        
        try :
            # TODO: アタッチ情報がすぐに更新されない問題に暫定的に対応
            for i in range(0, 10):
                volume = self.waitVolume(volumeId)
                if volume.status == "in-use":
                    break
                else:
                    time.sleep(10)

            #タイムアウト後判定
            if  volume.status != "in-use":
                # アタッチに失敗した場合
                raise IaasException("EPROCESS-001015", [instanceId, volumeId, volume.status,])

            # ログ出力
            self.logger.info(None, "IPROCESS-100824", [volumeId, instanceId,])

        except Exception:
            self.logger.error(traceback.format_exc())
            # アタッチに失敗した場合
            updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
            updateDict["STATUS"] = "error"
            updateDict["INSTANCE_ID"] = None
            sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)
            raise

        try:
            #デバイス取得
            for attachment in volume._info["attachments"]:
                device = attachment[u'device']
            #デバイスが取得出来ない場合
            if device == None:
                raise IaasException("EPROCESS-001014", [volumeId,])
        except Exception:
            self.logger.error(traceback.format_exc())
            # アタッチに失敗した場合
            updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
            updateDict["STATUS"] = "error"
            sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)
            raise
        
        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==osVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], osVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "OpenStackVolumeAttachFinish",[instance["INSTANCE_NAME"], osVolume["VOLUME_ID"], device])

        # データベースの更新
        updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        updateDict["STATUS"] = volume.status
        updateDict["DEVICE"] = device
        sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)

    def detachVolume(self, instanceNo, volumeNo) :
        tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        volumeId = osVolume["VOLUME_ID"]
        instanceId = osVolume["INSTANCE_ID"]
        device = osVolume["DEVICE"]

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==osVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], osVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "OpenStackVolumeDetach",[instance["INSTANCE_NAME"], osVolume["VOLUME_ID"], osVolume["DEVICE"]])

        # ボリュームのデタッチ
        self.client.detachVolume(instanceId, volumeId);

    def waitDetachVolume(self, instanceNo, volumeNo) :
        tableOSVOL = self.conn.getTable("OPENSTACK_VOLUME")
        osVolume = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        volumeId = osVolume["VOLUME_ID"]
        instanceId = osVolume["INSTANCE_ID"]

        volume = None
        try :
            volume = self.waitVolume(volumeId)
            # TODO: デタッチ情報がすぐに更新されない問題に暫定的に対応
            for i in range(0, 10):
                volume = self.waitVolume(volumeId)
                if volume.status == "available":
                    break
                else:
                    time.sleep(10)


            #タイムアウト後判定
            if  volume.status != "available":
                # デタッチに失敗した場合
                raise IaasException("EPROCESS-001016", [instanceId, volumeId, volume.status,])

            # ログ出力
            self.logger.info(None, "IPROCESS-100826", [volumeId, instanceId,])


        except Exception:
            self.logger.error(traceback.format_exc())
            # デタッチに失敗した場合
            updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
            updateDict["STATUS"] = "error"
            updateDict["INSTANCE_ID"] = None
            sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)
            raise


        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==osVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], osVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "OpenStackVolumeDetachFinish",[instance["INSTANCE_NAME"], osVolume["VOLUME_ID"], osVolume["DEVICE"]])

        # データベースの更新
        updateDict = self.conn.selectOne(tableOSVOL.select(tableOSVOL.c.VOLUME_NO==volumeNo))
        updateDict["STATUS"] = volume.status
        updateDict["INSTANCE_ID"] = None
        updateDict["DEVICE"] = None
        sql = tableOSVOL.update(tableOSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)