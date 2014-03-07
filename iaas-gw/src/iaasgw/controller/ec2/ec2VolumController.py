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
from iaasgw.module.ec2.ec2module import TagSet
from iaasgw.utils.stringUtils import isNotEmpty, isEmpty
import time
import traceback


class ec2VolumController(object):
    logger = IaasLogger()
    client = None
    conn = None
    platforminfo = None

    def __init__(self, platforminfo, ec2iaasclient, conn):
        self.client = ec2iaasclient
        self.conn = conn
        self.platforminfo = platforminfo


    def startVolumes(self, instanceNo) :
        # ボリューム情報の取得
        table = self.conn.getTable("AWS_VOLUME")
        volumes = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))

        for volume in volumes :
            self.startVolume(instanceNo, volume["VOLUME_NO"])


    def startVolume(self, instanceNo, volumeNo) :
        table = self.conn.getTable("AWS_VOLUME")
        volume = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))

        # インスタンスIDがある場合はスキップ
        if (isNotEmpty(volume["INSTANCE_ID"])) :
            return

        if (isEmpty(volume["VOLUME_ID"])) :
            # ボリュームIDがない場合は新規作成
            self.createVolume(instanceNo, volumeNo)

            # ボリュームの作成待ち
            self.waitCreateVolume(instanceNo, volumeNo)

            # ボリュームにタグを付ける
            self.createTag(volumeNo)

        # ボリュームのアタッチ
        self.attachVolume(instanceNo, volumeNo)

        # ボリュームのアタッチ待ち
        self.waitAttachVolume(instanceNo, volumeNo)


    def stopVolumes(self, instanceNo) :
        # ボリューム情報の取得
        awsVolumes = self.getAwsVolumes(instanceNo)

        for volume in awsVolumes :
            self.stopVolume(instanceNo, volume["VOLUME_NO"])


    def stopVolume(self, instanceNo, volumeNo):
        table = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))

        # ボリュームIDがない場合はスキップ
        if (isEmpty(awsVolume["VOLUME_ID"])):
            return

        # インスタンスIDがない場合はスキップ
        if (isEmpty(awsVolume["INSTANCE_ID"])) :
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

            table = self.conn.getTable("AWS_VOLUME")
            updateDict = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))
            updateDict["STATUS"] = "error"
            updateDict["INSTANCE_ID"] = None
            sql = table.update(table.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)


    def getAwsVolumes(self, instanceNo) :
        table = self.conn.getTable("AWS_VOLUME")
        awsVolumes = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))

        if (awsVolumes or len(awsVolumes) < 1) :
            return awsVolumes;

        # Platformのチェック
        retVolumes = []
        for awsVolume in awsVolumes:
            # PlatformNoが異なる場合、データ不整合なので警告ログを出す
            if (self.client.getPlatformNo() != awsVolume["PLATFORM_NO"]) :
                self.logger.warn(None, "EPROCESS-000201",[awsVolume["VOLUME_NAME"], awsVolume["PLATFORM_NO"], self.client.getPlatformNo()])
            else :
                retVolumes.append(awsVolume)

        return retVolumes;

    def waitVolume(self, volumeId) :
        # スナップショットの処理待ち
        volume = None
        while (True):
            volume = self.client.describeVolume(volumeId);
            status = volume.status

            if status == "available" or status == "in-use" or status == "error":
                break

            if status != "creating" and status != "deleting" :
                #予期しないステータス
                raise IaasException("EPROCESS-000112", [volumeId, status,])
        return volume;

    def createVolume(self, instanceNo, volumeNo) :
        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))

        # ボリュームの作成
        volume = self.client.createVolume(awsVolume["AVAILABILITY_ZONE"], awsVolume["SIZE"], awsVolume["SNAPSHOT_ID"])

        # イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==awsVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], awsVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"], "AwsEbsCreate",["EC2",])

        # データベース更新
        updateDict = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        updateDict["VOLUME_ID"] = volume.volumeId
        updateDict["STATUS"] = volume.status
        sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)



    def waitCreateVolume(self, instanceNo, volumeNo) :
        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        volumeId = awsVolume["VOLUME_ID"]

        # ボリュームの作成待ち
        volume = None
        try :
            volume = self.waitVolume(volumeId)

            if volume.status != "available":
                #ボリューム作成失敗時
                raise IaasException("EPROCESS-000113", [volumeId, volume.status,])

            # ログ出力
            self.logger.info(None, "IPROCESS-100122", [volumeId,])

        except Exception:
            self.logger.error(traceback.format_exc())
            # ボリューム作成失敗時
            awsVolume["VOLUME_ID"] = None
            awsVolume["STATUS"] = None
            sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==awsVolume["VOLUME_NO"], values=awsVolume)
            self.conn.execute(sql)
            raise


        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==awsVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], awsVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "AwsEbsCreateFinish",["EC2", awsVolume["VOLUME_ID"], awsVolume["SIZE"]])

        # データベース更新
        updateDict = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        updateDict["STATUS"] = volume.status
        sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)


    def checkAvailableVolume(self, instanceNo, volumeNo) :
        table = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(table.select(table.c.VOLUME_NO==volumeNo))
        volumeId = awsVolume["VOLUME_ID"]

        # ボリュームが利用可能かどうかのチェック
        volume = self.client.describeVolume(volumeId);

        if volume.status != "available":
            # ボリュームがavailableでない時
            raise IaasException("EPROCESS-000114", [volumeId, volume.status,])


    def attachVolume(self, instanceNo, volumeNo) :
        #AWS_INSTANCE 取得
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        instanceId = awsInstance["INSTANCE_ID"]

        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        volumeId = awsVolume["VOLUME_ID"]

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==awsVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], awsVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "AwsEbsAttach",[instance["INSTANCE_NAME"], awsVolume["VOLUME_ID"], awsVolume["DEVICE"]])

        # ボリュームのアタッチ
        self.client.attachVolume(volumeId, instanceId, awsVolume["DEVICE"])

        # データベースの更新
        awsVolume["INSTANCE_ID"] = instanceId
        sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==awsVolume["VOLUME_NO"], values=awsVolume)
        self.conn.execute(sql)


    def waitAttachVolume(self, instanceNo, volumeNo) :
        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        volumeId = awsVolume["VOLUME_ID"]
        instanceId = awsVolume["INSTANCE_ID"]

        volume = None
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
                raise IaasException("EPROCESS-000115", [instanceId, volumeId, volume.status,])

            # ログ出力
            self.logger.info(None, "IPROCESS-100124", [volumeId, instanceId,])

        except Exception:
            self.logger.error(traceback.format_exc())
            # アタッチに失敗した場合
            updateDict = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
            updateDict["STATUS"] = "error"
            updateDict["INSTANCE_ID"] = None
            sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)
            raise


        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==awsVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], awsVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "AwsEbsAttachFinish",[instance["INSTANCE_NAME"], awsVolume["VOLUME_ID"], awsVolume["DEVICE"]])

        # データベースの更新
        updateDict = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        updateDict["STATUS"] = volume.status
        sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)


    def detachVolume(self, instanceNo, volumeNo) :
        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        volumeId = awsVolume["VOLUME_ID"]
        instanceId = awsVolume["INSTANCE_ID"]
        device = awsVolume["DEVICE"]

        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==awsVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], awsVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "AwsEbsDetach",[instance["INSTANCE_NAME"], awsVolume["VOLUME_ID"], awsVolume["DEVICE"]])

        # ボリュームのデタッチ
        self.client.detachVolume(volumeId, instanceId, device);


    def waitDetachVolume(self, instanceNo, volumeNo) :
        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        volumeId = awsVolume["VOLUME_ID"]
        instanceId = awsVolume["INSTANCE_ID"]

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
                raise IaasException("EPROCESS-000116", [instanceId, volumeId, volume.status,])

            # ログ出力
            self.logger.info(None, "IPROCESS-100126", [volumeId, instanceId,])


        except Exception:
            self.logger.error(traceback.format_exc())
            # デタッチに失敗した場合
            updateDict = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
            updateDict["STATUS"] = "error"
            updateDict["INSTANCE_ID"] = None
            sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
            self.conn.execute(sql)
            raise


        #イベントログ出力
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==awsVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(instance["FARM_NO"], awsVolume["COMPONENT_NO"], componentName, instanceNo, instance["INSTANCE_NAME"],
                         "AwsEbsDetachFinish",[instance["INSTANCE_NAME"], awsVolume["VOLUME_ID"], awsVolume["DEVICE"]])

        # データベースの更新
        updateDict = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        updateDict["STATUS"] = volume.status
        updateDict["INSTANCE_ID"] = None
        sql = tableAWSVOL.update(tableAWSVOL.c.VOLUME_NO ==updateDict["VOLUME_NO"], values=updateDict)
        self.conn.execute(sql)


    def createTag(self, volumeNo) :
        # Eucalyptusの場合はタグを付けない
        platform = self.platforminfo["platformName"]
        if (platform == "eucalyptus"):
            return

        tableAWSVOL = self.conn.getTable("AWS_VOLUME")
        awsVolume = self.conn.selectOne(tableAWSVOL.select(tableAWSVOL.c.VOLUME_NO==volumeNo))
        tableCPNT = self.conn.getTable("COMPONENT")
        component = self.conn.selectOne(tableCPNT.select(tableCPNT.c.COMPONENT_NO==awsVolume["COMPONENT_NO"]))
        componentName = None
        if component:
            componentName = component["COMPONENT_NAME"]
        tableINS = self.conn.getTable("INSTANCE")
        instance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==awsVolume["INSTANCE_NO"]))

        # 表示用の文字列を作成する
        tagValue = str(instance["FQDN"]) + "_" + str(componentName)
        tags = []
        tags.append(TagSet(None, None, "Name", tagValue))
        tags.append(TagSet(None, None, "ServiceName", componentName))
        tags.append(TagSet(None, None, "UserName", self.client.getUsername()))


        # Nameタグを追加する
        self.client.createTags(awsVolume["VOLUME_ID"], tags);
