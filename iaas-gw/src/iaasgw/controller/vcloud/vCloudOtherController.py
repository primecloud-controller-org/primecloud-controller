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
from iaasgw.client.vCloudiaasclient import VCloudIaasClient
from iaasgw.log.log import IaasLogger
from iaasgw.module.vcloud.vCloudModule import PccVAppNetwork
from sqlalchemy.sql.expression import and_

############################
#
# 現在未使用の為作成のみ
#
############################

class VCloudOtherController(object):
    logger = IaasLogger()
    client = None
    conn = None
    platforminfo = None


    def __init__(self, platforminfo, vciaasclient, conn):
        self.client = vciaasclient
        self.conn = conn
        self.platforminfo = platforminfo

    def synchronizeCloud(self, platformNo):

        # イベントログ出力
        self.conn.debug(None, None, None, None,
                        None, "SynchronizeVCloud",[platformNo,])

        #組織 情報取得
        vdc = self.client.getUseVdc()
        #進行ログ
        self.logger.info(None, "IPROCESS-300001", [vdc.name,])

        ####################################
        #
        # ストレージの同期
        #
        ####################################
        storageprofiles = self.client.getVdcStorageprofiles(vdc)
        #PLATFORM_VCLOUD_STORAGE_TYPE 取得
        tableStorage = self.conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
        storages = self.conn.select(tableStorage.select(tableStorage.c.PLATFORM_NO==platformNo))

        for storage in storages:
            if storage["STORAGE_TYPE_NAME"] in storageprofiles:
                del storageprofiles[storage["STORAGE_TYPE_NAME"]]
            else:
                #進行ログ
                self.logger.info(None, "IPROCESS-300002", [storage["STORAGE_TYPE_NAME"],])
                #クラウドに存在しなければDBから消す
                tableStorage.delete(tableStorage.c.STORAGE_TYPE_NO==storage["STORAGE_TYPE_NO"]).execute()

        #未登録のプロファイルがあれば追加
        for key in storageprofiles:
            storageprofile = storageprofiles[key]
            #進行ログ
            self.logger.info(None, "IPROCESS-300003", [storageprofile.name,])
            sql = tableStorage.insert({"STORAGE_TYPE_NO":None,
                          "PLATFORM_NO":platformNo,
                          "STORAGE_TYPE_NAME":storageprofile.name,
                          })
            self.conn.execute(sql)

        #ストレージ情報更新
        storages = self.conn.select(tableStorage.select(tableStorage.c.PLATFORM_NO==platformNo))
        print storages
        #ストレージMAP
        storageMap = {}
        for storage in storages:
            storageMap[storage["STORAGE_TYPE_NAME"]] = storage["STORAGE_TYPE_NO"]


        #vAppリスト
        vappList = self.client.describeVdcMyCloud(vdc)
        ####################################
        #
        # VMの同期
        #
        ####################################
        for vapp in vappList:
            vms = vapp.extra["vms"]
            vdc = vapp.extra["vdc"]


            #VAPP名からVDC名を除去した物がFARM名
            nameprefix = vdc + "-"
            farmname = vapp.name.replace(nameprefix, "")
            #FARM 取得
            tableFarm = self.conn.getTable("FARM")
            farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NAME==farmname))
            #Farmが取得できない（PCC管理外のVAPPは無視する）
            if farm is None:
                #ファームが管理外なのでパス！
                continue

            #進行ログ
            self.logger.info(None, "IPROCESS-300004", [vapp.name,])

            #INSTANCE 取得
            tablePccIns = self.conn.getTable("INSTANCE")
            pccInstances = self.conn.select(tablePccIns.select(tablePccIns.c.FARM_NO==farm["FARM_NO"]))
            for pccInstance in pccInstances:

                #VCLOUD_INSTANCE 取得
                tableVcIns = self.conn.getTable("VCLOUD_INSTANCE")
                vcInstance = self.conn.selectOne(tableVcIns.select(tableVcIns.c.INSTANCE_NO==pccInstance["INSTANCE_NO"]))
                if vcInstance is None:
                    #ｖCloudプラットフォームのインスタンスでない
                    continue

                #PCC_INSTANCEの状態チェック
                if pccInstance["STATUS"] != "STOPPED" and pccInstance["STATUS"] != "RUNNING":
                    #PCC処理中なので同期しない
                    #進行ログ
                    self.logger.info(None, "IPROCESS-300005", [pccInstance["INSTANCE_NAME"],])
                    #ステータスが安定状態で無い場合はスキップ
                    continue

                for vm in vms:
                    if vm["name"] == vcInstance["VM_NAME"]:
                        #進行ログ
                        self.logger.info(None, "IPROCESS-300006", [pccInstance["INSTANCE_NAME"],])
                        ####################################
                        #
                        # ステータス
                        #
                        ####################################
                        if vm["state"] == VCloudIaasClient.STOPPED:
                            #INSTANCE 更新
                            pccInstance["ENABLED"] = 0
                            pccInstance["STATUS"] = "STOPPED"
                            pccInstance["COODINATE_STATUS"] = "UN_COODINATED"
                            #VCLOUD_INSTANCE 更新
                            vcInstance["STORAGE_TYPE_NO"] = storageMap[vm["storageprofile"]]
                            vcInstance["STATUS"] = "Stopped"
                            if vm["public_ips"] is not None and len(vm["public_ips"]) >0:
                                vcInstance["IP_ADDRESS"] = vm["public_ips"][0]
                                pccInstance["PUBLIC_IP"] = None
                            if vm["private_ips"] is not None and len(vm["private_ips"]) >0:
                                vcInstance["PRIVATE_IP_ADDRESS"] = vm["private_ips"][0]
                                pccInstance["PRIVATE_IP"] = None
                        elif vm["state"] == VCloudIaasClient.RUNNING:
                            #INSTANCE 更新
                            pccInstance["ENABLED"] = 1
                            pccInstance["STATUS"] = "RUNNING"
                            pccInstance["COODINATE_STATUS"] = "COODINATED"
                            #VCLOUD_INSTANCE 更新
                            vcInstance["STORAGE_TYPE_NO"] = storageMap[vm["storageprofile"]]
                            vcInstance["STATUS"] = "Running"
                            if vm["public_ips"] is not None and len(vm["public_ips"]) >0:
                                vcInstance["IP_ADDRESS"] = vm["public_ips"][0]
                                pccInstance["PUBLIC_IP"] = vm["public_ips"][0]
                            if vm["private_ips"] is not None and len(vm["private_ips"]) >0:
                                vcInstance["PRIVATE_IP_ADDRESS"] = vm["private_ips"][0]
                                pccInstance["PRIVATE_IP"] = vm["private_ips"][0]

                        #INSTANCE 更新 実行
                        sql = tablePccIns.update(tablePccIns.c.INSTANCE_NO ==pccInstance["INSTANCE_NO"], values=pccInstance)
                        self.conn.execute(sql)
                        #VCLOUD_INSTANCE 更新 実行
                        sql = tableVcIns.update(tableVcIns.c.INSTANCE_NO ==vcInstance["INSTANCE_NO"], values=vcInstance)
                        self.conn.execute(sql)

                        ####################################
                        #
                        # ネットワーク
                        #
                        ####################################
                        vmnetworks = self.client.describeVMNetwork(vm)
                        #MAP化
                        vmNetoworkMap = {}
                        for vmnetwork in vmnetworks:
                            vmNetoworkMap[str(vmnetwork.index)] = vmnetwork

                        #VCLOUD_NETWORK 取得
                        tableNW = self.conn.getTable("VCLOUD_INSTANCE_NETWORK")
                        dbnetworks = self.conn.select(tableNW.select(tableNW.c.INSTANCE_NO==pccInstance["INSTANCE_NO"]))
                        for dbnetwork in dbnetworks:
                            if str(dbnetwork["NETWORK_INDEX"]) in vmNetoworkMap:
                                #処理予定の物をリストから削除
                                network =  vmNetoworkMap.pop(str(dbnetwork["NETWORK_INDEX"]))
                                dbnetwork["NETWORK_NAME"] = network.name
                                dbnetwork["IP_MODE"] = network.ipMode
                                dbnetwork["IP_ADDRESS"] = network.ipAddress
                                if network.isPrimary:
                                    dbnetwork["IS_PRIMARY"] = 1
                                else:
                                    dbnetwork["IS_PRIMARY"] = 0

                                sql = tableNW.update(tableNW.c.NETWORK_NO ==dbnetwork["NETWORK_NO"], values=dbnetwork)
                                self.conn.execute(sql)

                            else:
                                #進行ログ
                                self.logger.info(None, "IPROCESS-300007", [dbnetwork["NETWORK_NAME"],])
                                #クラウドに存在しなければDBから消す
                                tableNW.delete(tableNW.c.NETWORK_NO==dbnetwork["NETWORK_NO"]).execute()

                        #未登録のネットワークがあれば追加
                        for key in vmNetoworkMap:
                            vmnetwork = vmNetoworkMap[key]
                            #進行ログ
                            self.logger.info(None, "IPROCESS-300008", [vmnetwork.name,])
                            #プライマリ
                            isPrimary = 0
                            if vmnetwork.isPrimary:
                                isPrimary = 1
                            else:
                                isPrimary = 0

                            sql = tableNW.insert({"NETWORK_NO":None,
                                          "PLATFORM_NO":platformNo,
                                          "INSTANCE_NO":pccInstance["INSTANCE_NO"],
                                          "FARM_NO":pccInstance["FARM_NO"],
                                          "NETWORK_NAME":vmnetwork.name,
                                          "NETWORK_INDEX":int(vmnetwork.index),
                                          "IP_MODE":vmnetwork.ipMode,
                                          "IP_ADDRESS":vmnetwork.ipAddress,
                                          "IS_PRIMARY":isPrimary,
                                          })
                            self.conn.execute(sql)

                        ####################################
                        #
                        # ディスク
                        #
                        ####################################
                        vmdisks = self.client.describeVolumes(vm)
                        #MAP化
                        vmdiskMap = {}
                        for vmdisk in vmdisks:
                            vmdiskMap[str(vmdisk.unitNo)] = vmdisk

                        #VCLOUD_DISK 取得
                        tableDisk = self.conn.getTable("VCLOUD_DISK")
                        dbdisks = self.conn.select(tableDisk.select(tableDisk.c.INSTANCE_NO==pccInstance["INSTANCE_NO"]))
                        for dbdisk in dbdisks:
                            if str(dbdisk["UNIT_NO"]) in vmdiskMap:
                                #処理予定の物をリストから削除
                                disk =  vmdiskMap.pop(str(dbdisk["UNIT_NO"]))
                                dbdisk["DISK_ID"] = disk.name
                                dbdisk["SIZE"] = int(disk.size)/1024
                                sql = tableDisk.update(tableDisk.c.DISK_NO ==dbdisk["DISK_NO"], values=dbdisk)
                                self.conn.execute(sql)

                            else:
                                #進行ログ
                                self.logger.info(None, "IPROCESS-300009", [dbdisk["UNIT_NO"],])
                                #クラウドに存在しなければDBから消す
                                tableDisk.delete(tableDisk.c.DISK_NO==dbdisk["DISK_NO"]).execute()

                        #未登録のディスクがあれば追加
                        for key in vmdiskMap:
                            vmdisk = vmdiskMap[key]
                            if int(vmdisk.unitNo) == 0:
                                #ルートディスクは対象外
                                continue
                            #進行ログ
                            self.logger.info(None, "IPROCESS-300010", [vmdisk.unitNo,])

                            print u"追加！==>",vmdisk.name, vmdisk.unitNo
                            sql = tableDisk.insert({"DISK_NO":None,
                                          "DISK_ID":vmdisk.name,
                                          "FARM_NO":pccInstance["FARM_NO"],
                                          "PLATFORM_NO":platformNo,
                                          "COMPONENT_NO":None,
                                          "INSTANCE_NO":pccInstance["INSTANCE_NO"],
                                          "SIZE":int(vmdisk.size)/1024,
                                          "UNIT_NO":int(vmdisk.unitNo),
                                          "ATTACHED":1,
                                          "DATA_DISK":1,
                                          })
                            self.conn.execute(sql)
                        #進行ログ
                        self.logger.info(None, "IPROCESS-300011", [pccInstance["INSTANCE_NAME"],])

                    else:
                        #管理していないVMは無視する
                        continue

            #進行ログ
            self.logger.info(None, "IPROCESS-300012", [vapp.name,])
        # イベントログ出力
        self.conn.debug(None, None, None, None,
                        None, "SynchronizeVCloudFinish",[platformNo,])


    def createMyCloud(self, platformNo, farmName):
        # イベントログ出力
        self.conn.debug(None, None, None, None,
                        None, "VCloudVAppCreate",[farmName,])


        vdc = self.client.getUseVdc()
        #vappの存在チェック 同じ名前のVAPPが有ったらパス
        vApp = self.client.describeMyCloud(vdc, farmName)
        if vApp is not None:
            return

        #組織に紐付くネットワークの検索
        vdcnetworks = self.client.describeVdcNetwork()

        vdc = self.client.getUseVdc()
        node = self.client.createMyCloud(vdc, farmName, vdcnetworks)

        # イベントログ出力
        self.conn.debug(None, None, None, None,
                        None, "VCloudVAppCreateFinish",[farmName,])

        return node

    def terminateMyCloud(self, platformNo, farmNo):
        tableFarm = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==farmNo))

        # イベントログ出力
        self.conn.debug(farmNo, None, None, None,
                        None, "VCloudVAppDelete",[farm["FARM_NAME"],])


        vdc = self.client.getUseVdc()
        vApp = self.client.describeMyCloud(vdc, farm["FARM_NAME"])

        self.client.terminateMyCloud(vApp)

        # イベントログ出力
        self.conn.debug(farmNo, None, None, None,
                        None, "VCloudVAppDeleteFinish",[farm["FARM_NAME"],])


    ##以降未使用########################################################

    def describeSnapshot(self, snapshotId):
        pass

    def describeSnapshots(self):
        pass

    def createSnapshot(self, volumeNo):
        pass

    def deleteSnapshot(self, snapshotNo):
        pass

    def getPasswordData(self, instanceNo):
        pass

