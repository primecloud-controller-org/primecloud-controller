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
from iaasgw.controller.iaascontroller import IaasController
from iaasgw.controller.vcloud.vCloudInstanceController import \
    VCloudInstanceController
from iaasgw.controller.vcloud.vCloudOtherController import VCloudOtherController
from iaasgw.controller.vcloud.vCloudVolumController import VCloudVolumController
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertyUtil import getTrueFalseProperty
from iaasgw.utils.stringUtils import isNotEmpty, isBit
import datetime
import os
import time
import traceback

class VCloudController(IaasController):

    logger = IaasLogger()

    conn = None
    client = None
    accessInfo = None

    instancecontroller = None
    volumecontroller = None
    addresscontroller = None
    loadBalancercontroller = None
    othercontroller = None

    def __init__(self, conn, accessInfo, platforminfo):
        self.conn = conn
        self.accessInfo = accessInfo
        self.client = VCloudIaasClient(platforminfo, accessInfo["USER_NAME"], accessInfo["ACCESS_ID"], accessInfo["SECRET_KEY"])

        #コントローラ作成
        self.othercontroller         = VCloudOtherController(platforminfo, self.client, self.conn)
        self.instancecontroller      = VCloudInstanceController(platforminfo, self.client, self.conn)
        self.volumecontroller        = VCloudVolumController(platforminfo, self.client, self.conn)


    def __del__(self):
        #保険
        self.conn.rollback()

        #エラー時に残るタスク登録を消す
        taskTable = self.conn.getTable("VCLOUD_TASK")
        sql = taskTable.delete().where(taskTable.c.P_ID == os.getpid())
        self.conn.execute(sql)
        self.conn.commit()
        #セッションクローズ
        self.conn.close()

    def doWaitingExecution(self, farm_name):
        vdc = self.client.getUseVdc()
        vappname = vdc.name + "-" + farm_name
        #タスク情報を登録し1度コミット
        taskTable = self.conn.getTable("VCLOUD_TASK")
        sql = taskTable.insert({"ADDRESS_NO":None,
                            "P_ID":os.getpid(),
                            "REGIST_TIME":datetime.datetime.today(),
                            "VAPP":vappname})
        self.conn.execute(sql)
        self.conn.commit()

        #同タイム対策スリープ
        time.sleep(3)

        #自分のPIDの順番まで待つ
        status = "stop"
        prePid = "0"
        roopCount = 0
        while status != 'go':
            #登録時間の早いタスクを取得(常に最新のDB状況を参照する)
            self.conn.remakeSession()
            task = self.conn.selectOne(taskTable.select()
                                       .where(taskTable.c.VAPP == vappname)
                                       .order_by(taskTable.c.REGIST_TIME, taskTable.c.P_ID))
            self.logger.info(os.getpid())
            self.logger.info(task)
            if os.getpid() == task["P_ID"]:
                vapp = self.client.describeMyCloud(vdc, farm_name)
                #VAPPが実行中タスクを持っている場合は待つ
                if vapp.extra["task"]:
                    roopCount = roopCount +1
                    time.sleep(10)
                else:
                    status = 'go'
            else:
                #前回PIDが同じ場合ループカウントを増やす
                if prePid == task["P_ID"]:
                    roopCount = roopCount +1
                else:
                    #新しいPIDの場合は0に戻す
                    roopCount = 0
                    prePid = task["P_ID"]
                time.sleep(10)

            #タイムアウト
            if roopCount > 100:
                raise IaasException("EPROCESS-000812", [os.getpid(),])

    def doWaitingExecutionEnd(self):
        taskTable = self.conn.getTable("VCLOUD_TASK")
        sql = taskTable.delete().where(taskTable.c.P_ID == os.getpid())
        self.conn.execute(sql)


    def getVappName(self, farmNo=None, instanceNo=None, vm_name=None):

        farmNo = farmNo
        instanceNo = instanceNo
        vm_name = vm_name

        if vm_name is not None:
            #vCloud_INSTANCE 取得
            tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
            vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.VM_NAME==vm_name))
            instanceNo = vcInstance["INSTANCE_NO"]

        if instanceNo is not None:
            #PCC_INSTANCE 取得
            tableINS = self.conn.getTable("INSTANCE")
            pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
            farmNo = pccInstance["FARM_NO"]

        if farmNo is not None:
            #FARM 取得
            tableFarm = self.conn.getTable("FARM")
            farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==farmNo))
            return farm["FARM_NAME"]

        else:
            return None

    def synchronizeCloud(self, platformNo):
        self.othercontroller.synchronizeCloud(platformNo)
        self.conn.commit()
        return True

    def describeNetwork(self):
        networks = self.client.describeVdcNetwork()
        rtString = ''
        for network in networks:
            if rtString != '':
                rtString = rtString + "##"

            name = network.name
            value = "GATEWAY="+str(network.gateway)
            value = value + ",NETMASK=" + str(network.netmask)
            value = value + ",DNS1=" + str(network.dns1)
            value = value + ",DNS2=" + str(network.dns2)
            value = value + ",RANGEF=" + str(network.rangeF)
            value = value + ",RANGET=" + str(network.rangeT)
            value = value + ",PRIMARY=" + str(network.primary)

            #とりあえず必要な情報のみ返します
            rtString = rtString + name + '#' + value

        self.conn.commit()
        return "RESULT:" + rtString

    def createMyCloud(self, platformNo, farmName):

        self.othercontroller.createMyCloud(platformNo, farmName)
        self.conn.commit()
        return True

    def terminateMyCloud(self, platformNo, farmNo):
        #VAPPロック
        self.doWaitingExecution(self.getVappName(farmNo=farmNo))

        self.othercontroller.terminateMyCloud(platformNo, farmNo)

        #VAPPロック解除
        self.doWaitingExecutionEnd()
        self.conn.commit()
        return True


    def startInstance(self, instanceNo):
        #VAPPロック
        self.doWaitingExecution(self.getVappName(instanceNo=instanceNo))

        #仮想マシンを(作成)起動
        self.instancecontroller.startInstance(instanceNo)

        # ボリュームに関する処理
        table = self.conn.getTable("VCLOUD_DISK")
        volumes = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))
        for disk in volumes:
            if isNotEmpty(disk["COMPONENT_NO"]):
                # コンポーネント番号がある場合はスキップ
                continue

            if isNotEmpty(disk["DISK_ID"]):
                # アタッチされている場合はスキップ
                continue

            #Volumeスタート
            self.volumecontroller.startVolume(instanceNo, disk["DISK_NO"])

        #VAPPロック解除
        self.doWaitingExecutionEnd()
        self.conn.commit()
        return True


    def stopInstance(self, instanceNo):
        #VAPPロック
        self.doWaitingExecution(self.getVappName(instanceNo=instanceNo))

        #仮想マシンを停止
        self.instancecontroller.stopInstance(instanceNo)

        # ボリュームに関する処理
        tableDsk = self.conn.getTable("VCLOUD_DISK")
        volumes = self.conn.select(tableDsk.select(tableDsk.c.INSTANCE_NO==instanceNo))
        for disk in volumes:
            #アンデタッチの場合抜ける
            if getTrueFalseProperty("unDetachVolume"):
                break

            # filestorage は外さない
            if isBit(disk["DATA_DISK"]):
                continue

            #Volumのストップ
            self.volumecontroller.stopVolume(instanceNo, disk["DISK_NO"])

        #VAPPロック解除
        self.doWaitingExecutionEnd()
        self.conn.commit()
        return True


    def terminateInstance(self, vm_name):
        #vCloud_INSTANCE 取得
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.VM_NAME==vm_name))
        if vcInstance is None:
            #1度も起動されて無い場合はそのまま返す
            return

        #VAPPロック
        self.doWaitingExecution(self.getVappName(vm_name=vm_name))

        #仮想マシンを削除
        self.instancecontroller.terminate(vm_name)

        #VAPPロック解除
        self.doWaitingExecutionEnd()
        self.conn.commit()
        return True

    def startVolume(self, instanceNo, volumeNo):
        #VAPPロック
        self.doWaitingExecution(self.getVappName(instanceNo=instanceNo))

        self.volumecontroller.startVolume(instanceNo, volumeNo)
        #VAPPロック解除
        self.doWaitingExecutionEnd()
        self.conn.commit()
        return True

    def stopVolume(self, instanceNo, volumeNo):
        #VAPPロック
        self.doWaitingExecution(self.getVappName(instanceNo=instanceNo))

        self.volumecontroller.stopVolume(instanceNo, volumeNo)
        #VAPPロック解除
        self.doWaitingExecutionEnd()
        self.conn.commit()
        return True

    def deleteVolume(self, disk_no):

        tableVCDISK = self.conn.getTable("VCLOUD_DISK")
        vcdisk = self.conn.selectOne(tableVCDISK.select(tableVCDISK.c.DISK_NO==disk_no))
        #VCLOUD_INSTANCE 取得
        tableVCINS = self.conn.getTable("VCLOUD_INSTANCE")
        vcInstance = self.conn.selectOne(tableVCINS.select(tableVCINS.c.INSTANCE_NO==vcdisk["INSTANCE_NO"]))
        #FARM 取得
        tableFarm = self.conn.getTable("FARM")
        farm = self.conn.selectOne(tableFarm.select(tableFarm.c.FARM_NO==vcdisk["FARM_NO"]))

        #VAPPロック
        self.doWaitingExecution(farm["FARM_NAME"])

        #組織
        vdc = self.client.getUseVdc()
        #マイクラウド
        vApp = self.client.describeMyCloud(vdc, farm["FARM_NAME"])
        #既存VM検索
        vm = self.client.describeInstance(vApp, vcInstance["VM_NAME"])

        self.client.detachVolume(vm, vcdisk["DISK_ID"])

        #VAPPロック解除
        self.doWaitingExecutionEnd()
        self.conn.commit()
        return True

    def startLoadBalancer(self, loadBalancerNo):
        pass

    def stopLoadBalancer(self, loadBalancerNo):
        pass

    def configureLoadBalancer(self, loadBalancerNo):
        pass


    #TODO  後々はゾーンを取得するようにしなければいけない
    def allocateAddress(self, farmNo):
        pass

    def releaseAddress(self, addressNo, farmNo):
        pass

    def createSnapshot(self, volumeNo):
        pass

    def deleteSnapshot(self, snapshotNo):
        pass

    def getPasswordData(self, instanceNo):
        pass

    def describeKeyPairs(self):
        pass

    def createKeyPair(self, keyName):
        pass

    def deleteKeyPair(self, keyName):
        pass

    def importKeyPair(self, keyName, publicKeyMaterial):
        pass

    def describeSecurityGroups(self, vpcid = None):
        pass

    def describeAvailabilityZones(self):
        pass


