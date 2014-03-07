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
from iaasgw.utils.stringUtils import isNotEmpty, getCurrentTimeMillis, isEmpty


class ec2AddressController(object):
    client = None
    conn = None
    logger = IaasLogger()
    platforminfo = None

    def __init__(self, platforminfo, ec2iaasclient, conn):
        self.client = ec2iaasclient
        self.conn = conn
        self.platforminfo = platforminfo


    def startAddress(self, instanceNo) :
        # アドレス情報の取得
        awsAddress = self.getAwsAddress(instanceNo);
        if not awsAddress:
            # アドレス情報がない場合は終了
            return

        if isNotEmpty(awsAddress["INSTANCE_ID"]) :
            # インスタンスIDがある場合はスキップ
            return

        addressNo = awsAddress["ADDRESS_NO"]

        # アドレスのステータスチェック
        self.checkAvailableAddress(instanceNo, addressNo)

        # アドレスの関連付け
        self.associateAddress(instanceNo, addressNo)


    def stopAddress(self, instanceNo) :
        # アドレス情報の取得
        awsAddress = self.getAwsAddress(instanceNo);

        # アドレス情報がない場合は終了
        if not awsAddress:
            return

        # インスタンスIDがない場合はスキップ
        if isEmpty(awsAddress["INSTANCE_ID"]):
            return;

        addressNo = awsAddress["ADDRESS_NO"]

        try :
            # アドレスのステータスチェック
            self.checkAssociatedAddress(instanceNo, addressNo)

            # アドレスの切り離し
            self.disassociateAddress(instanceNo, addressNo)

        except Exception, e:
            # 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
            self.logger.warn(e.__repr__());
            table = self.conn.getTable("AWS_ADDRESS")
            updateDict = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))
            updateDict["INSTANCE_ID"] = None
            sql = table.update(table.c.ADDRESS_NO ==updateDict["ADDRESS_NO"], values=updateDict)
            self.conn.execute(sql)

    def getAwsAddress(self, instanceNo) :
        table = self.conn.getTable("AWS_ADDRESS")
        awsAddresses = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))
        if not awsAddresses or len(awsAddresses) == 0 :
            return None;
        elif len(awsAddresses) > 1 :
            # アドレス情報が複数ある場合
            raise IaasException("EPROCESS-000202", [instanceNo,])


        awsAddress = awsAddresses[0]

        # Platformのチェック
        platformNo = awsAddress["PLATFORM_NO"]
        if self.client.getPlatformNo() != str(platformNo):
            # PlatformNoが異なる場合、データ不整合なので警告ログを出す
            self.logger.warn(None, "EPROCESS-000203", [awsAddress["PUBLIC_IP"], awsAddress["PLATFORM_NO"], self.client.getPlatformNo()])
            return None

        return awsAddress;


    def checkAvailableAddress(self, instanceNo, addressNo) :
        table = self.conn.getTable("AWS_ADDRESS")
        awsAddress = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))
        publicIp = awsAddress["PUBLIC_IP"]

        address = self.client.describeAddress(publicIp);
        if isNotEmpty(address.instanceId):
            # アドレスが何らかのインスタンスに関連付けられている場合
            raise IaasException("EPROCESS-000119", [publicIp, address.instanceId,])


    def associateAddress(self, instanceNo, addressNo) :
        # アドレスの関連付け
        tableAWSADD = self.conn.getTable("AWS_ADDRESS")
        awsAddress = self.conn.selectOne(tableAWSADD.select(tableAWSADD.c.ADDRESS_NO==addressNo))
        publicIp = awsAddress["PUBLIC_IP"]

        tableAWSINS = self.conn.getTable("AWS_INSTANCE")
        awsInstance = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        instanceId = awsInstance["INSTANCE_ID"]


        self.client.associateAddress(publicIp, instanceId)

        #イベントログ出力
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsElasticIpAssociate", [instanceId, publicIp] )

        # データベースの更新
        updateDict = self.conn.selectOne(tableAWSADD.select(tableAWSADD.c.ADDRESS_NO==addressNo))
        updateDict["INSTANCE_ID"] = instanceId
        sql = tableAWSADD.update(tableAWSADD.c.ADDRESS_NO ==updateDict["ADDRESS_NO"], values=updateDict)
        self.conn.execute(sql)


        # 最新のインスタンスを取得
        instance = None
        timeout = 180 * 1000L
        startTime = getCurrentTimeMillis()
        while (True) :
            instance = self.client.describeInstance(instanceId);

            if (instance.extra["dns_name"] != awsInstance["DNS_NAME"]) and (instance.extra["dns_name"] != instance.extra["private_dns"]):
                # DnsNameが変更されており、かつPrivateDnsNameと違っていれば最新インスタンス情報を取得できたとみなす
                break;

            if (getCurrentTimeMillis() - startTime > timeout) :
                # タイムアウト発生時
                raise IaasException("EPROCESS-000204", [instanceId,])

        # データベースの更新
        updateDict = self.conn.selectOne(tableAWSINS.select(tableAWSINS.c.INSTANCE_NO==instanceNo))
        updateDict["DNS_NAME"] = instance.extra["dns_name"]
        updateDict["PRIVATE_DNS_NAME"] = instance.extra["private_dns"]
        if len(instance.public_ip) > 0:
            updateDict["IP_ADDRESS"] = instance.public_ip[0]
        if len(instance.private_ip) > 0:
            updateDict["PRIVATE_IP_ADDRESS"] = instance.private_ip[0]
        sql = tableAWSINS.update(tableAWSINS.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def checkAssociatedAddress(self, instanceNo, addressNo) :
        # アドレスが関連付けられているかどうかのチェック
        table = self.conn.getTable("AWS_ADDRESS")
        awsAddress = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))
        publicIp = awsAddress["PUBLIC_IP"]
        instanceId = awsAddress["INSTANCE_ID"]

        address = self.client.describeAddress(publicIp)

        if isEmpty(address.instanceId):
            # アドレスがどのインスタンスにも関連付けられていない場合
            raise IaasException("EPROCESS-000120", [publicIp, instanceId,])
        elif instanceId != address.instanceId:
            # アドレスが他インスタンスに関連付けられている場合
            raise IaasException("EPROCESS-000121", [publicIp, instanceId, address.instanceId,])


    def disassociateAddress(self, instanceNo, addressNo) :
        # アドレスの取り外し
        tableAWSADD = self.conn.getTable("AWS_ADDRESS")
        awsAddress = self.conn.selectOne(tableAWSADD.select(tableAWSADD.c.ADDRESS_NO==addressNo))
        publicIp = awsAddress["PUBLIC_IP"]
        instanceId = awsAddress["INSTANCE_ID"]

        self.client.disassociateAddress(publicIp, instanceId)

        #イベントログ出力
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "AwsElasticIpDisassociate", [instanceId, publicIp] )

        # データベースの更新
        awsAddress["INSTANCE_ID"] = None
        sql = tableAWSADD.update(tableAWSADD.c.ADDRESS_NO ==awsAddress["ADDRESS_NO"], values=awsAddress)
        self.conn.execute(sql)

