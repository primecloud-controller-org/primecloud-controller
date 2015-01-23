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
from iaasgw.module.ec2.ec2module import Listener, HealthCheck, \
    LoadBalancerDescription
from iaasgw.utils.stringUtils import isNotEmpty, isBit
from sqlalchemy.sql.expression import and_
import traceback

class ec2LoadBalancercontroller(object):

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


    def createLoadBalancer(self, farmNo, loadBalancerNo, availabilityZones, subnets, groupmap) :
        tableAWSLB = self.conn.getTable("AWS_LOAD_BALANCER")
        awsLoadBalancer = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO==loadBalancerNo))

        # ロードバランサ作成情報
        loadBalancerName = awsLoadBalancer["NAME"]

        # デフォルトゾーンの特定 デフォルト1件目
        availabilityZone = None
        for zone in availabilityZones:
            availabilityZone = zone.name

        #セキュリティグループ
        securityGroups = []
        if (isNotEmpty(awsLoadBalancer["SECURITY_GROUPS"])):
            securityGroups = awsLoadBalancer["SECURITY_GROUPS"].split(",")

        #サブネットID
        subnetIds = []
        if (isNotEmpty(awsLoadBalancer["SUBNET_ID"])):
            subnetIds = awsLoadBalancer["SUBNET_ID"].split(",")

        # サブネット(VPC)との関係からセキュリティグループIDを取得
        securityGroupIds = []
        if len(subnetIds) != 0:
            for subnet in subnets:
                if subnetIds[0] == subnet.subnetId:
                    #セキュリティグループID
                    for group in securityGroups:
                        key = group+subnet.vpcId
                        securityGroupIds.append(groupmap[key])


        # ダミーのリスナーの設定  instancePort, instanceProtocol, loadBalancerPort, protocol, sslCertificateId
        listener = Listener("65535", None, "65535","TCP",None)
        listeners = [listener]

        # ロードバランサの作成
        dnsName = self.client.createLoadBalancer(availabilityZone, listeners, loadBalancerName, subnetIds, securityGroupIds)

        #実行ログ
        self.logger.info(None ,"IPROCESS-200111", [awsLoadBalancer["NAME"],])

        # イベントログ出力
        self.conn.debug(farmNo, None, None, None, None, "AwsElbCreate", ["EC2", loadBalancerName] )

        # ダミーのリスナーの削除
        self.client.deleteLoadBalancerListeners(["65535",], loadBalancerName)

        # データベース更新
        updateDict = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        updateDict["DNS_NAME"] = dnsName
        sql = tableAWSLB.update(tableAWSLB.c.LOAD_BALANCER_NO ==updateDict["LOAD_BALANCER_NO"], values=updateDict)
        self.conn.execute(sql)

        return dnsName;


    def deleteLoadBalancer(self, farmNo, loadBalancerNo) :
        tableAWSLB = self.conn.getTable("AWS_LOAD_BALANCER")
        awsLoadBalancer = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO==loadBalancerNo))

        # ロードバランサ名
        loadBalancerName = awsLoadBalancer["NAME"]
        try :
            self.client.deleteLoadBalancer(loadBalancerName);

            #実行ログ
            self.logger.info(None ,"IPROCESS-200112", [awsLoadBalancer["NAME"],])

            # イベントログ出力
            self.conn.debug(farmNo, None, None, None, None, "AwsElbDelete", ["EC2", loadBalancerName] )

        except Exception:
            self.logger.error(traceback.format_exc())

        # データベース更新
        updateDict = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        updateDict["DNS_NAME"] = None
        sql = tableAWSLB.update(tableAWSLB.c.LOAD_BALANCER_NO ==updateDict["LOAD_BALANCER_NO"], values=updateDict)
        self.conn.execute(sql)


    def configureListeners(self, farmNo, loadBalancerNo) :
        table = self.conn.getTable("LOAD_BALANCER_LISTENER")
        listeners = self.conn.select(table.select(table.c.LOAD_BALANCER_NO==loadBalancerNo))

        # リスナーの起動・停止処理
        for listener in listeners :
            status = self.getStatusString(listener["STATUS"])
            if isBit(listener["ENABLED"]):
                if status == self.STOPPED :
                    # 有効で停止しているリスナーは処理対象
                    self.startListener(farmNo, loadBalancerNo, listener["LOAD_BALANCER_PORT"])
                elif status == self.RUNNING:
                    # 有効で起動しているリスナーの場合、処理を行わずにフラグを変更する
                    if isBit(listener["CONFIGURE"]):
                        listener["CONFIGURE"] = "0"
                        sql = table.update(and_(table.c.LOAD_BALANCER_NO ==listener["LOAD_BALANCER_NO"], table.c.LOAD_BALANCER_PORT == listener["LOAD_BALANCER_PORT"]), values=listener)
                        self.conn.execute(sql)

            else :
                if (status == self.RUNNING or status == self.WARNING) :
                    # 無効で起動または異常なリスナーは処理対象
                    self.stopListener(farmNo, loadBalancerNo, listener["LOAD_BALANCER_PORT"])
                elif (status == self.STOPPED) :
                    # 無効で停止しているリスナーの場合、処理を行わずにフラグを変更する
                    if isBit(listener["CONFIGURE"]):
                        listener["CONFIGURE"] = "0"
                        sql = table.update(and_(table.c.LOAD_BALANCER_NO ==loadBalancerNo, table.c.LOAD_BALANCER_PORT == listener["LOAD_BALANCER_PORT"]), values=listener)
                        self.conn.execute(sql)

    def startListener(self, farmNo, loadBalancerNo, loadBalancerPort) :
        table = self.conn.getTable("LOAD_BALANCER_LISTENER")
        listener = self.conn.selectOne(table.select(and_(table.c.LOAD_BALANCER_NO==loadBalancerNo, table.c.LOAD_BALANCER_PORT ==loadBalancerPort)))

        try :
            # リスナー作成情報
            tableAWSLB = self.conn.getTable("AWS_LOAD_BALANCER")
            awsLoadBalancer = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO==loadBalancerNo))

            sslKey = None

            if (isNotEmpty(listener["SSL_KEY_NO"])):
                # リスナー作成情報
                tableAWSSSL = self.conn.getTable("AWS_SSL_KEY")
                awsSslKey = self.conn.selectOne(tableAWSSSL.select(tableAWSSSL.c.KEY_NO==listener["SSL_KEY_NO"]))
                sslKey = awsSslKey["SSLCERTIFICATEID"]

            # ロードバランサ名
            loadBalancerName = awsLoadBalancer["NAME"]

            # リスナーの設定  instancePort, instanceProtocol, loadBalancerPort, protocol, sslCertificateId
            listeners = [ Listener(listener["SERVICE_PORT"], None, listener["LOAD_BALANCER_PORT"], listener["PROTOCOL"], sslKey),]

            # リスナーの作成
            self.client.createLoadBalancerListeners(listeners, loadBalancerName)

            #実行ログ
            self.logger.info(None ,"IPROCESS-200121", [awsLoadBalancer["NAME"], listener["LOAD_BALANCER_PORT"]])

            # イベントログ出力
            self.conn.debug(farmNo, None, None, None, None, "AwsElbListenerCreate", ["EC2", loadBalancerName, listener["LOAD_BALANCER_PORT"]] )

        except Exception:
            self.logger.error(traceback.format_exc())
            # ステータスを更新
            tableLBL = self.conn.getTable("LOAD_BALANCER_LISTENER")
            updateDict = self.conn.selectOne(tableLBL.select(and_(tableLBL.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBL.c.LOAD_BALANCER_PORT ==loadBalancerPort)))
            updateDict["STATUS"] = self.WARNING
            sql = tableLBL.update(and_(tableLBL.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBL.c.LOAD_BALANCER_PORT ==loadBalancerPort), values=updateDict)
            self.conn.execute(sql)
            raise


        # ステータスを更新
        tableLBL = self.conn.getTable("LOAD_BALANCER_LISTENER")
        updateDict = self.conn.selectOne(table.select(and_(tableLBL.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBL.c.LOAD_BALANCER_PORT ==loadBalancerPort)))
        updateDict["STATUS"] = self.RUNNING
        sql = tableLBL.update(and_(tableLBL.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBL.c.LOAD_BALANCER_PORT ==loadBalancerPort), values=updateDict)
        self.conn.execute(sql)


    def stopListener(self, farmNo, loadBalancerNo, loadBalancerPort) :
        tableLBL = self.conn.getTable("LOAD_BALANCER_LISTENER")
        listener = self.conn.selectOne(tableLBL.select(and_(tableLBL.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBL.c.LOAD_BALANCER_PORT ==loadBalancerPort)))

        try :
            # リスナー削除情報
            table = self.conn.getTable("AWS_LOAD_BALANCER")
            awsLoadBalancer = self.conn.selectOne(table.select(table.c.LOAD_BALANCER_NO==loadBalancerNo))
            # ロードバランサ名
            loadBalancerName = awsLoadBalancer["NAME"]
            # ロードバランサポート
            loadBalancerPort = listener["LOAD_BALANCER_PORT"]
            loadBalancerPorts = [loadBalancerPort,]

            # リスナーの削除
            self.client.deleteLoadBalancerListeners(loadBalancerPorts, loadBalancerName);

            #実行ログ
            self.logger.info(None ,"IPROCESS-200122", [awsLoadBalancer["NAME"], listener["LOAD_BALANCER_PORT"]])

            # イベントログ出力
            self.conn.debug(farmNo, None, None, None, None, "AwsElbListenerDelete", ["EC2", loadBalancerName, listener["LOAD_BALANCER_PORT"]] )

        except Exception, e:
            self.logger.error(traceback.format_exc())
            self.logger.warn(e.getMessage())


        # ステータスを更新
        updateDict = self.conn.selectOne(tableLBL.select(and_(tableLBL.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBL.c.LOAD_BALANCER_PORT ==loadBalancerPort)))
        updateDict["STATUS"] = self.STOPPED
        sql = tableLBL.update(and_(tableLBL.c.LOAD_BALANCER_NO==loadBalancerNo, tableLBL.c.LOAD_BALANCER_PORT ==loadBalancerPort), values=updateDict)
        self.conn.execute(sql)



    def configureHealthCheck(self, farmNo, loadBalancerNo) :
        tableLBHC = self.conn.getTable("LOAD_BALANCER_HEALTH_CHECK")
        healthCheck = self.conn.selectOne(tableLBHC.select(tableLBHC.c.LOAD_BALANCER_NO==loadBalancerNo))

        # ヘルスチェック情報がない場合はスキップ
        if not healthCheck :
            return

        # 現在のヘルスチェック設定を取得
        tableAWSLB = self.conn.getTable("AWS_LOAD_BALANCER")
        awsLoadBalancer = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        # ロードバランサ名
        loadBalancerName = awsLoadBalancer["NAME"]

        #loadBalancerDescriptions = self.client.describeLoadBalancer(loadBalancerName)
        #description = loadBalancerDescriptions[0]

        description =LoadBalancerDescription(None, None , None, None, None, None, HealthCheck(None, 1, 2, 3, 4), None, None, None, None, None )

        # ヘルスチェック設定を作成
        target = str(healthCheck["CHECK_PROTOCOL"]) + ":" + str(healthCheck["CHECK_PORT"])
        if (isNotEmpty(healthCheck["CHECK_PATH"])) :
            if healthCheck["CHECK_PATH"].startswith('/') == False:
                target = target + "/"

            target = target + healthCheck["CHECK_PATH"]

        healthCheck2 = HealthCheck(
                    healthCheck["HEALTHY_THRESHOLD"],
                    healthCheck["CHECK_INTERVAL"],
                    target,
                    healthCheck["CHECK_TIMEOUT"],
                    healthCheck["UNHEALTHY_THRESHOLD"])

        # ヘルスチェック設定に変更がない場合はスキップ
        if ((healthCheck2.target == description.healthCheck.target)
            and (healthCheck2.timeout == description.healthCheck.timeout)
            and (healthCheck2.interval == description.healthCheck.interval)
            and (healthCheck2.healthyThreshold == description.healthCheck.healthyThreshold)
            and (healthCheck2.unhealthyThreshold == description.healthCheck.unhealthyThreshold)) :
            return

        # ヘルスチェック設定を変更
        self.client.configureHealthCheck(healthCheck2, loadBalancerName);

        #実行ログ
        self.logger.info(None ,"IPROCESS-200131", [awsLoadBalancer["NAME"],])

        # イベントログ出力
        self.conn.debug(farmNo, None, None, None, None, "AwsElbHealthCheckConfig", ["EC2", loadBalancerName,] )

    def applySecurityGroupsToLoadBalancer(self, farmNo, loadBalancerNo, groupmap, subnets) :
        tableAWSLB = self.conn.getTable("AWS_LOAD_BALANCER")
        awsLoadBalancer = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO==loadBalancerNo))
        # ロードバランサ名
        loadBalancerName = awsLoadBalancer["NAME"]

        # サブネットIDが設定されていなければリターン
        subnetIds = []
        if (isNotEmpty(awsLoadBalancer["SUBNET_ID"])):
            subnetIds = awsLoadBalancer["SUBNET_ID"].split(",")
        else:
            return

        #セキュリティグループ
        securityGroups = []
        if (isNotEmpty(awsLoadBalancer["SECURITY_GROUPS"])):
            securityGroups = awsLoadBalancer["SECURITY_GROUPS"].split(",")
        #IDへ変換
        securityGroupIds = []
        for subnet in subnets:
            if subnetIds[0] == subnet.subnetId:
                #セキュリティグループID
                for group in securityGroups:
                    key = group+subnet.vpcId
                    securityGroupIds.append(groupmap[key])

        # セキュリティグループ設定を変更
        self.client.applySecurityGroupsToLoadBalancer(securityGroupIds, loadBalancerName);

        #実行ログ
        self.logger.info(None ,"IPROCESS-200225", [awsLoadBalancer["NAME"],])

        # イベントログ出力
        self.conn.debug(farmNo, None, None, None, None, "AwsElbSecurityGroupsConfig", ["EC2", loadBalancerName,] )

    def configureInstances(self, farmNo, loadBalancerNo) :
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
        self.registerInstances(farmNo, loadBalancerNo, enabledInstances, loadBalancerInstances)
        # 振り分けを解除する
        self.unregisterInstances(farmNo, loadBalancerNo, disabledInstances, loadBalancerInstances)


    def registerInstances(self, farmNo, loadBalancerNo, instances, loadBalancerInstances) :
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
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        for instance in instances:
            awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO == instance["INSTANCE_NO"]))
            instanceIds.append(awsInstance["INSTANCE_ID"])

        try :
            # 振り分け登録
            tableAWSLB = self.conn.getTable("AWS_LOAD_BALANCER")
            awsLoadBalancer = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO == loadBalancerNo))
            loadBalancerName = awsLoadBalancer["NAME"]

            self.client.registerInstancesWithLoadBalancer(instanceIds, loadBalancerName)

            for instanceid in instanceIds:
                #実行ログ
                self.logger.info(None ,"IPROCESS-200141", [awsLoadBalancer["NAME"], instanceid])
                # イベントログ出力
                self.conn.debug(farmNo, None, None, None, None, "AwsElbInstancesRegist", ["EC2", loadBalancerName, instanceid] )

        except Exception:
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
            sql = tableLBINS.update(and_(tableLBINS.c.LOAD_BALANCER_NO==loadBalancerInstance["LOAD_BALANCER_NO"], tableLBINS.c.INSTANCE_NO ==loadBalancerInstance["INSTANCE_NO"]), values=loadBalancerInstance)
            self.conn.execute(sql)




    def unregisterInstances(self, farmNo, loadBalancerNo, instances, loadBalancerInstances) :
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
        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        for instance in instances:
            awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO == instance["INSTANCE_NO"]))
            instanceIds.append(awsInstance["INSTANCE_ID"])

        try :
            # 振り分け解除
            tableAWSLB = self.conn.getTable("AWS_LOAD_BALANCER")
            awsLoadBalancer = self.conn.selectOne(tableAWSLB.select(tableAWSLB.c.LOAD_BALANCER_NO == loadBalancerNo))
            loadBalancerName = awsLoadBalancer["NAME"]

            self.client.deregisterInstancesFromLoadBalancer(instanceIds, loadBalancerName)

            for instanceid in instanceIds:
                #実行ログ
                self.logger.info(None ,"IPROCESS-200142", [awsLoadBalancer["NAME"], instanceid])
                # イベントログ出力
                self.conn.debug(farmNo, None, None, None, None, "AwsElbInstancesDeregist", ["EC2", loadBalancerName, instanceid] )


        except Exception:
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


