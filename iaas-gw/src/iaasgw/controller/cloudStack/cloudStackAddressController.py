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


class CloudStackAddressController(object):
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
        csAddress = self.getCsAddress(instanceNo);
        if not csAddress:
            # アドレス情報がない場合は終了
            return

        if isNotEmpty(csAddress["INSTANCE_ID"]) :
            # インスタンスIDがある場合はスキップ
            return

        addressNo = csAddress["ADDRESS_NO"]

        # アドレスのステータスチェック
        self.checkAvailableAddress(addressNo)

        # アドレスの関連付け
        self.associateAddress(instanceNo, addressNo)


    def stopAddress(self, instanceNo) :
        # アドレス情報の取得
        csAddress = self.getCsAddress(instanceNo);

        # アドレス情報がない場合は終了
        if not csAddress:
            return

        # インスタンスIDがない場合はスキップ
        if isEmpty(csAddress["INSTANCE_ID"]):
            return;

        addressNo = csAddress["ADDRESS_NO"]

        try :
            # アドレスのステータスチェック
            self.checkAssociatedAddress(addressNo)

            # アドレスの切り離し
            self.disassociateAddress(instanceNo, addressNo)

        except Exception, e:
            # 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
            self.logger.warn(e.__repr__());
            table = self.conn.getTable("CLOUDSTACK_ADDRESS")
            updateDict = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))
            updateDict["INSTANCE_ID"] = None
            sql = table.update(table.c.ADDRESS_NO ==updateDict["ADDRESS_NO"], values=updateDict)
            self.conn.execute(sql)

    def getCsAddress(self, instanceNo) :
        table = self.conn.getTable("CLOUDSTACK_ADDRESS")
        csAddresses = self.conn.select(table.select(table.c.INSTANCE_NO==instanceNo))
        if not csAddresses or len(csAddresses) == 0 :
            return None;
        elif len(csAddresses) > 1 :
            # アドレス情報が複数ある場合
            raise IaasException("EPROCESS-000722", [instanceNo,])


        csAddress = csAddresses[0]

        # Platformのチェック
        platformNo = csAddress["PLATFORM_NO"]
        if self.client.getPlatformNo() != str(platformNo):
            # PlatformNoが異なる場合、データ不整合なので警告ログを出す
            self.logger.warn(None, "EPROCESS-000723", [csAddress["IPADDRESS"], csAddress["PLATFORM_NO"], self.client.getPlatformNo()])
            return None

        return csAddress;


    def checkAvailableAddress(self, addressNo) :
        table = self.conn.getTable("CLOUDSTACK_ADDRESS")
        csAddress = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))
        address_id = csAddress["ADDRESS_ID"]

        address = self.client.describePublicIpAddress(address_id);
        if "virtualmachineid" in address:
            # アドレスが何らかのインスタンスに関連付けられている場合
            raise IaasException("EPROCESS-000710", [address["ipaddress"], address["virtualmachineid"],])


    def associateAddress(self, instanceNo, addressNo) :
        # アドレスの関連付け
        tableCSADD = self.conn.getTable("CLOUDSTACK_ADDRESS")
        csAddress = self.conn.selectOne(tableCSADD.select(tableCSADD.c.ADDRESS_NO==addressNo))
        address_id = csAddress["ADDRESS_ID"]
        publicIp = csAddress["IPADDRESS"]

        tableCSINS = self.conn.getTable("CLOUDSTACK_INSTANCE")
        csInstance = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))
        instanceId = csInstance["INSTANCE_ID"]


        self.client.enableStaticNat(address_id, instanceId)

        #イベントログ出力
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackIpAssociate", [instanceId, publicIp] )

        # データベースの更新
        updateDict = self.conn.selectOne(tableCSADD.select(tableCSADD.c.ADDRESS_NO==addressNo))
        updateDict["INSTANCE_ID"] = instanceId
        sql = tableCSADD.update(tableCSADD.c.ADDRESS_NO ==updateDict["ADDRESS_NO"], values=updateDict)
        self.conn.execute(sql)

        # データベースの更新
        updateDict = self.conn.selectOne(tableCSINS.select(tableCSINS.c.INSTANCE_NO==instanceNo))
        updateDict["IPADDRESS"] = publicIp
        sql = tableCSINS.update(tableCSINS.c.INSTANCE_NO ==updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)


    def checkAssociatedAddress(self, addressNo) :
        # アドレスが関連付けられているかどうかのチェック
        table = self.conn.getTable("CLOUDSTACK_ADDRESS")
        csAddress = self.conn.selectOne(table.select(table.c.ADDRESS_NO==addressNo))
        address_id = csAddress["ADDRESS_ID"]
        instanceId = csAddress["INSTANCE_ID"]

        address = self.client.describePublicIpAddress(address_id);
        if "virtualmachineid" not in address:
            # アドレスがどのインスタンスにも関連付けられていない場合
            raise IaasException("EPROCESS-000711", [address["ipaddress"],  address["virtualmachineid"],])
        elif instanceId != str(address["virtualmachineid"]):
            # アドレスが他インスタンスに関連付けられている場合
            raise IaasException("EPROCESS-000712", [address["ipaddress"],  address["virtualmachineid"],])


    def disassociateAddress(self, instanceNo, addressNo) :
        # アドレスの取り外し
        tableCSADD = self.conn.getTable("CLOUDSTACK_ADDRESS")
        csAddress = self.conn.selectOne(tableCSADD.select(tableCSADD.c.ADDRESS_NO==addressNo))
        address_id = csAddress["ADDRESS_ID"]
        publicIp = csAddress["IPADDRESS"]
        instanceId = csAddress["INSTANCE_ID"]

        self.client.disableStaticNat(address_id)

        #イベントログ出力
        tableINS = self.conn.getTable("INSTANCE")
        pccInstance = self.conn.selectOne(tableINS.select(tableINS.c.INSTANCE_NO==instanceNo))
        self.conn.debug(pccInstance["FARM_NO"], None, None, instanceNo, pccInstance["INSTANCE_NAME"], "CloudStackIpDisassociate", [instanceId, publicIp] )

        # データベースの更新
        csAddress["INSTANCE_ID"] = None
        sql = tableCSADD.update(tableCSADD.c.ADDRESS_NO ==csAddress["ADDRESS_NO"], values=csAddress)
        self.conn.execute(sql)

