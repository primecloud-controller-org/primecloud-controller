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
from iaasgw.utils.stringUtils import isBit
from sqlalchemy.sql.expression import and_
import traceback

class CloudStackLoadBalancercontroller(object):

    logger = IaasLogger()
    client = None
    conn = None
    platforminfo = None


    STOPPED = "STOPPED"
    STARTING = "STARTING"
    RUNNING = "RUNNING"
    STOPPING = "STOPPING"
    CONFIGURING = "CONFIGURING"
    WARNING = "WARNING"

    STATUS={
        STOPPED:STOPPED,
        RUNNING:RUNNING,
        STOPPING:STOPPING,
        CONFIGURING:CONFIGURING,
        WARNING:WARNING,
    }



    def __init__(self, platforminfo, ec2iaasclientLb, conn):
        self.client = ec2iaasclientLb
        self.conn = conn
        self.platforminfo = platforminfo


    def getStatusString(self, key):
        if not key:
            return "STOPPED"

        value = self.STATUS[key]
        if value != None:
            return value
        return "STOPPED"


    def createLoadBalancer(self, loadBalancerNo) :
        tableCSLB = self.conn.getTable("CLOUDSTACK_LOAD_BALANCER")
        csLoadBalancer = self.conn.selectOne(tableCSLB.select(tableCSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        tableLB = self.conn.getTable("LOAD_BALANCER")
        loadBalancer = self.conn.selectOne(tableLB.select(tableLB.c.LOAD_BALANCER_NO==loadBalancerNo))

        # ロードバランサ作成情報
        loadBalancerName = csLoadBalancer["NAME"]
        #アルゴリズム
        algorithm = csLoadBalancer["ALGORITHM"]
        # ゾーン
        zoneid = csLoadBalancer["ZONEID"]
        # パブリックポート
        port = csLoadBalancer["PUBLICPORT"]
        # プライベートポート
        private_port = csLoadBalancer["PRIVATEPORT"]

        # ロードバランサの作成
        balancer = self.client.createLoadBalancer(loadBalancerName, algorithm, port, private_port, zoneid)

        #実行ログ
        self.logger.info(None ,"IPROCESS-200621", [loadBalancerName,])

        # イベントログ出力
        self.conn.debug(loadBalancer["FARM_NO"], None, None, None, None, "CloudStackLBCreate", ["CLOUDSTACK", loadBalancerName] )

        # データベース更新
        updateDict = self.conn.selectOne(tableCSLB.select(tableCSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        updateDict["LOAD_BALANCER_ID"] = balancer.id
        updateDict["PUBLICIP"] = balancer.ip
        updateDict["STATE"] = balancer.state
        updateDict["ADDRESS_ID"] = balancer.ex_public_ip_id
        sql = tableCSLB.update(tableCSLB.c.LOAD_BALANCER_NO ==updateDict["LOAD_BALANCER_NO"], values=updateDict)
        self.conn.execute(sql)



    def deleteLoadBalancer(self, loadBalancerNo) :
        tableCSLB = self.conn.getTable("CLOUDSTACK_LOAD_BALANCER")
        csLoadBalancer = self.conn.selectOne(tableCSLB.select(tableCSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        tableLB = self.conn.getTable("LOAD_BALANCER")
        loadBalancer = self.conn.selectOne(tableLB.select(tableLB.c.LOAD_BALANCER_NO==loadBalancerNo))

        # ロードバランサ
        loadBalancer = csLoadBalancer["LOAD_BALANCER_ID"]
        try :
            self.client.deleteLoadBalancer(loadBalancer);

            #実行ログ
            self.logger.info(None ,"IPROCESS-200622", [csLoadBalancer["NAME"],])

            # イベントログ出力
            self.conn.debug(loadBalancer["FARM_NO"], None, None, None, None, "CloudStackLBDelete", ["CLOUDSTACK", csLoadBalancer["NAME"]] )

        except Exception:
            self.logger.error(traceback.format_exc())

        # データベース更新
        updateDict = self.conn.selectOne(tableCSLB.select(tableCSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        updateDict["LOAD_BALANCER_ID"] = None
        updateDict["PUBLICIP"] = None
        updateDict["STATE"] = None
        updateDict["ADDRESS_ID"] = None
        sql = tableCSLB.update(tableCSLB.c.LOAD_BALANCER_NO ==updateDict["LOAD_BALANCER_NO"], values=updateDict)
        self.conn.execute(sql)



    def configureInstances(self, loadBalancerNo) :
        tableLBINS = self.conn.getTable("LOAD_BALANCER_INSTANCE")
        loadBalancerInstances = self.conn.select(tableLBINS.select(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerNo))

        # 振り分け設定するインスタンスがない場合はスキップ
        if not loadBalancerInstances or len(loadBalancerInstances) == 0:
            return

        tableLB = self.conn.getTable("LOAD_BALANCER")
        loadBalancer = self.conn.selectOne(tableLB.select(tableLB.c.LOAD_BALANCER_NO==loadBalancerNo))

        # 振り分けを登録・解除するインスタンスを仕分けする
        enabledInstances = []
        disabledInstances = []

        # 振り分けするインスタンス情報を取得
        instanceMap = {}
        for loadBalancerInstance in loadBalancerInstances :
            table = self.conn.getTable("INSTANCE")
            instanceNo = loadBalancerInstance["INSTANCE_NO"]

            #インスタンス獲得
            instance = self.conn.selectOne(table.select(table.c.INSTANCE_NO == instanceNo))
            instanceMap.update({instanceNo:instance})

            # ロードバランサが無効の場合は振り分けを解除する
            if not isBit(loadBalancer["ENABLED"]):
                disabledInstances.append(instance)
                continue;
            # インスタンスが無効の場合は振り分けを解除する
            if not isBit(instance["ENABLED"]):
                disabledInstances.append(instance);
                continue;

            if isBit(loadBalancerInstance["ENABLED"]):
                enabledInstances.append(instance)
            else :
                disabledInstances.append(instance)

        # 振り分けを登録する
        self.registerInstances(loadBalancerNo, loadBalancer["FARM_NO"], enabledInstances, loadBalancerInstances)
        # 振り分けを解除する
        self.unregisterInstances(loadBalancerNo, loadBalancer["FARM_NO"], disabledInstances, loadBalancerInstances)


    def registerInstances(self, loadBalancerNo, farmNo, instances, loadBalancerInstances) :
        if not instances or len(instances) == 0:
            # 振り分け登録するインスタンスがない場合はスキップ
            return

        # 振り分けされていないインスタンス番号を抽出
        tmpInstances = []
        for loadBalancerInstance in loadBalancerInstances:
            for instance in instances:
                if instance["INSTANCE_NO"] == loadBalancerInstance["INSTANCE_NO"] :
                    status = self.getStatusString(loadBalancerInstance["STATUS"])
                    if status == self.STOPPED :
                        tmpInstances.append(instance)

        instances = tmpInstances
        # 振り分けされていないインスタンスがない場合はスキップ
        if not instances or len(instances) == 0:
            return

        # 起動しているインスタンス番号を抽出
        tmpInstanceNos = []
        for instance in instances:
            status = self.getStatusString(instance["STATUS"])
            if status == self.RUNNING:
                tmpInstanceNos.append(instance)

        instances = tmpInstanceNos;

        if not instances or len(instances) == 0:
            # 起動しているインスタンスがない場合はスキップ
            return;

        # AWSインスタンスのIDを取得
        instanceIds = []
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        for instance in instances:
            csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO == instance["INSTANCE_NO"]))
            instanceIds.append(csInstance["INSTANCE_ID"])

        try :
            # 振り分け登録
            tableCSLB = self.conn.getTable("CLOUDSTACK_LOAD_BALANCER")
            csLoadBalancer = self.conn.selectOne(tableCSLB.select(tableCSLB.c.LOAD_BALANCER_NO == loadBalancerNo))
            loadBalancerid = csLoadBalancer["LOAD_BALANCER_ID"]

            self.client.attach_members(loadBalancerid, instanceIds)

            for instanceid in instanceIds:
                #実行ログ
                self.logger.info(None ,"IPROCESS-200623", [csLoadBalancer["NAME"], instanceid])
                # イベントログ出力
                self.conn.debug(farmNo, None, None, None, None, "CloudStackLBInstancesRegist", ["CLOUDSTACK", csLoadBalancer["NAME"], instanceid] )

        except Exception, e:
            self.logger.error(traceback.format_exc())
            # ステータスの更新
            tableLBINS = self.conn.getTable("LOAD_BALANCER_INSTANCE")
            for instance in instances:
                loadBalancerInstance = self.conn.selectOne(tableLBINS.select(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBINS.c.INSTANCE_NO ==instance["INSTANCE_NO"])))
                loadBalancerInstance["STATUS"] = self.WARNING
                sql = tableLBINS.update(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerInstance["LOAD_BALANCER_NO"], tableLBINS.c.INSTANCE_NO ==loadBalancerInstance["INSTANCE_NO"]), values=loadBalancerInstance)
                self.conn.execute(sql)

            raise

        # ステータスの更新
        tableLBINS = self.conn.getTable("LOAD_BALANCER_INSTANCE")
        for instance in instances:
            loadBalancerInstance = self.conn.selectOne(tableLBINS.select(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBINS.c.INSTANCE_NO ==instance["INSTANCE_NO"])))
            loadBalancerInstance["STATUS"] = self.RUNNING
            sql = tableLBINS.update(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerInstance["LOAD_BALANCER_NO"] ,tableLBINS.c.INSTANCE_NO ==loadBalancerInstance["INSTANCE_NO"]), values=loadBalancerInstance)
            self.conn.execute(sql)


    def unregisterInstances(self, loadBalancerNo, farmNo, instances, loadBalancerInstances) :
        if not instances or len(instances) == 0:
            # 振り分け登録するインスタンスがない場合はスキップ
            return
        # 振り分けされているインスタンス番号を抽出
        tmpInstances = []
        for loadBalancerInstance in loadBalancerInstances:
            for instance in instances:
                if instance["INSTANCE_NO"] == loadBalancerInstance["INSTANCE_NO"] :
                    status = self.getStatusString(loadBalancerInstance["STATUS"])
                    if status == self.RUNNING :
                        tmpInstances.append(instance)

        instances = tmpInstances
        if not instances or len(instances) == 0:
            # 振り分けされているインスタンスがない場合はスキップ
            return

        # 起動しているインスタンス番号を抽出
        tmpInstanceNos = []
        for instance in instances:
            status = self.getStatusString(instance["STATUS"])
            if status == self.RUNNING:
                tmpInstanceNos.append(instance)

        instances = tmpInstanceNos;
        if not instances or len(instances) == 0:
            # 起動しているインスタンスがない場合はスキップ
            return;

        # AWSインスタンスのIDを取得
        instanceIds = []
        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        for instance in instances:
            csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO == instance["INSTANCE_NO"]))
            instanceIds.append(csInstance["INSTANCE_ID"])

        try :
            # 振り分け解除
            tableCSLB = self.conn.getTable("CLOUDSTACK_LOAD_BALANCER")
            csLoadBalancer = self.conn.selectOne(tableCSLB.select(tableCSLB.c.LOAD_BALANCER_NO == loadBalancerNo))
            loadBalancerid = csLoadBalancer["LOAD_BALANCER_ID"]

            self.client.detach_members(loadBalancerid, instanceIds)

            for instanceid in instanceIds:
                #実行ログ
                self.logger.info(None ,"IPROCESS-200624", [csLoadBalancer["NAME"], instanceid])
                # イベントログ出力
                self.conn.debug(farmNo, None, None, None, None, "CloudStackLBInstancesDeregist", ["CLOUDSTACK", csLoadBalancer["NAME"], instanceid] )


        except Exception, e:
            self.logger.error(traceback.format_exc())
            # ステータスの更新
            tableLBINS = self.conn.getTable("LOAD_BALANCER_INSTANCE")
            for instance in instances:
                loadBalancerInstance = self.conn.selectOne(tableLBINS.select(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBINS.c.INSTANCE_NO ==instance["INSTANCE_NO"])))
                loadBalancerInstance["STATUS"] = self.WARNING
                sql = tableLBINS.update(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerInstance["LOAD_BALANCER_NO"], tableLBINS.c.INSTANCE_NO ==loadBalancerInstance["INSTANCE_NO"]), values=loadBalancerInstance)
                self.conn.execute(sql)

            raise

        # ステータスの更新
        tableLBINS = self.conn.getTable("LOAD_BALANCER_INSTANCE")
        for instance in instances:
            loadBalancerInstance = self.conn.selectOne(tableLBINS.select(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBINS.c.INSTANCE_NO ==instance["INSTANCE_NO"])))
            loadBalancerInstance["STATUS"] = self.STOPPED
            sql = tableLBINS.update(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerInstance["LOAD_BALANCER_NO"], tableLBINS.c.INSTANCE_NO ==loadBalancerInstance["INSTANCE_NO"]), values=loadBalancerInstance)
            self.conn.execute(sql)


