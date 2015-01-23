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
from iaasgw.utils.propertyUtil import getImage, getScriptProperty, getDnsProperty, getPuppetProperty, getVpnProperty
from sqlalchemy.sql.expression import and_
from iaasgw.utils.stringUtils import isEmpty, isNotEmpty, startsWithIgnoreCase
import traceback
#
import re

class OpenStackInstanceController(object):

    logger = IaasLogger()

    client = None
    conn = None
    platforminfo = None
    
    def __init__(self, platforminfo, openstackiaasclient, conn):
        self.client = openstackiaasclient
        self.conn = conn
        self.platforminfo = platforminfo

        return

    def _createVirtualMachine(self, instanceNo, instanceName, instanceInfo, openstackInstanceInfo):
        # プラットフォーム番号の取得
        platformNo = instanceInfo['PLATFORM_NO']
        # VM作成に必要なパラメータを取得
        # キーペア名
        keyName = openstackInstanceInfo['KEY_NAME']
        if isEmpty(keyName):
            self.logger.debug('      No Keipair specified. Using default.')
            # ファーム番号の取得
            farmNo = instanceInfo['FARM_NO']
            farmTable = self.conn.getTable("FARM")
            farmInfo = self.conn.selectOne(farmTable.select\
                (farmTable.c.FARM_NO==farmNo))
            userNo = farmInfo['USER_NO']
            openstackCertificateTable = self.conn.getTable("OPENSTACK_CERTIFICATE")
            certificateInfo = self.conn.selectOne(openstackCertificateTable.select\
                (and_(openstackCertificateTable.c.USER_NO==userNo, \
                openstackCertificateTable.c.PLATFORM_NO==platformNo)))
            keyName = certificateInfo['DEF_KEYPAIR']
        self.logger.debug('      Keypair: %s' % (keyName))
        # フレーバーID
        instanceType = openstackInstanceInfo['INSTANCE_TYPE']
        self.logger.debug('      Instance Type: %s' % (instanceType))
        # セキュリティグループID
        securityGroup = openstackInstanceInfo['SECURITY_GROUPS']
        self.logger.debug('      Security Group: %s' % (securityGroup))
        # ゾーンID
        availabilityZone = openstackInstanceInfo['AVAILABILITY_ZONE']
        if isEmpty(availabilityZone):
            self.logger.debug('      No Availability Zone specified. Using default.')
            platformOpenStackTable = self.conn.getTable("PLATFORM_OPENSTACK")
            platformOpenStackInfo = self.conn.selectOne(platformOpenStackTable.select\
                (platformOpenStackTable.c.PLATFORM_NO==platformNo))
            availabilityZone = platformOpenStackInfo['AVAILABILITY_ZONE']
        self.logger.debug('      Availability Zone: %s' % (availabilityZone))
        # ネットワークID
        networkId = openstackInstanceInfo['NETWORK_ID']
        if isEmpty(networkId):
            self.logger.debug('      No Network ID specified. Using default.')
            platformOpenStackTable = self.conn.getTable("PLATFORM_OPENSTACK")
            platformOpenStackInfo = self.conn.selectOne(platformOpenStackTable.select\
                (platformOpenStackTable.c.PLATFORM_NO==platformNo))
            networkId = platformOpenStackInfo['NETWORK_ID']
        self.logger.debug('      Network: %s' % (networkId))
        # イメージID
        imageNo = instanceInfo['IMAGE_NO']
        imageOpenstackTable = self.conn.getTable("IMAGE_OPENSTACK")
        imageOpenstackInfo = self.conn.selectOne(imageOpenstackTable.select\
            (imageOpenstackTable.c.IMAGE_NO==imageNo))
        imageId = imageOpenstackInfo['IMAGE_ID']
        self.logger.debug('      Image: %s' % (imageId))
        # ユーザデータ
        userData = self.createUserData(instanceNo, instanceInfo, openstackInstanceInfo)
        userData = self.makeUserData(userData)
        self.logger.debug('      User Data: %s' %(userData))

        try:
            instanceObj = self.client.createVirtualMachine(instanceName, instanceType, \
            imageId, availabilityZone, networkId, userData, securityGroup, keyName)
        except:
            raise IaasException("EPROCESS-001002", [instanceName])

        status = instanceObj.status
        if status != 'ACTIVE':
            raise IaasException("EPROCESS-001003", [instanceName, status])

        openstackInstanceId = instanceObj.id
        connectedNetworks = instanceObj.addresses.keys()
        # IPは1つのみであることを仮定
        connectedNw = connectedNetworks[0]
        try:
            ipAddr = instanceObj.addresses[connectedNw][0]['addr']
            self.logger.debug('      IP Address: %s' %(ipAddr))
        except:
            ipAddr = None

        # データベース更新
        table = self.conn.getTable("OPENSTACK_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["STATUS"] = status
        updateDict["INSTANCE_ID"] = openstackInstanceId
        updateDict["PRIVATE_IP_ADDRESS"] = ipAddr
        updateDict["NETWORK_ID"] = networkId
        updateDict["KEY_NAME"] = keyName
        updateDict["AVAILABILITY_ZONE"] = availabilityZone
        sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)
        return True

    def startInstance(self, instanceNo):
        self.logger.info('      StartInstance: %s' % (instanceNo))

        # インスタンスIDの取得
        openstackInstanceTable = self.conn.getTable("OPENSTACK_INSTANCE")
        openstackInstanceInfo = self.conn.selectOne(openstackInstanceTable.select\
            (openstackInstanceTable.c.INSTANCE_NO==instanceNo))
        openstackInstanceId = openstackInstanceInfo['INSTANCE_ID']

        # インスタンス名の取得
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        instanceName = instanceInfo['FQDN']

        if isEmpty(openstackInstanceId):
            # 新規作成
            self.logger.info('      Creating Instance: %s' % (instanceName))
            return self._createVirtualMachine(instanceNo, instanceName, instanceInfo, openstackInstanceInfo)
        else:
            # 既存インスタンス
            # ステータスを確認する
            try:
                status = self.client.getVirtualMachineStatus(openstackInstanceId)
            except:
                raise IaasException("EPROCESS-001008", [instanceName])
            if status == 'ACTIVE':
                # 既にインスタンスは起動しているため、statusを更新して終了する。
                self.logger.info('      Instance: %s is already ACTIVE' % (instanceName))
                # データベース更新
                table = self.conn.getTable("OPENSTACK_INSTANCE")
                updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
                updateDict["STATUS"] = status
                sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
                self.conn.execute(sql)
                return True
            elif status == 'ERROR':
                # インスタンスの状態がERRORならば、起動は意味がないのでエラーにする。
                self.logger.info('      Instance: %s is in ERROR status' % (instanceName))
                raise IaasException("EPROCESS-001003", [instanceName, status])
            else:
                # 既に作成され、停止しているインスタンスを起動する。
                # SHUTOFF 状態のインスタンスに対して起動するのが通常の処理だが、
                # SHUTOFF 状態以外の場合でも、起動を試みて結果を返す。
                self.logger.info('      Starting Instance: %s' % (instanceName))
                try:
                    instanceObj = self.client.startVirtualMachine(openstackInstanceId)
                except:
                    raise IaasException("EPROCESS-001004", [instanceName])
                status = instanceObj.status

                self.logger.info('      StartInstance finished. Status: %s' % (status))
                if status != 'ACTIVE':
                    raise IaasException("EPROCESS-001003", [instanceName, status])
                # データベース更新
                table = self.conn.getTable("OPENSTACK_INSTANCE")
                updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
                updateDict["STATUS"] = status
                sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
                self.conn.execute(sql)
                return True

    def stopInstance(self, instanceNo):
        self.logger.info('      StopInstance: %s' % (instanceNo))

        # インスタンスIDの取得
        openstackInstanceTable = self.conn.getTable("OPENSTACK_INSTANCE")
        openstackInstanceInfo = self.conn.selectOne(openstackInstanceTable.select\
            (openstackInstanceTable.c.INSTANCE_NO==instanceNo))
        openstackInstanceId = openstackInstanceInfo['INSTANCE_ID']
        # インスタンス名の取得
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        instanceName = instanceInfo['FQDN']

        if isEmpty(openstackInstanceId):
            # 一度もStartしたことがない
            self.logger.info('      Nonexistent Instance: %s' % (instanceName))
            raise IaasException("EPROCESS-001006", [instanceName])

        # ステータスを確認する
        try:
            status = self.client.getVirtualMachineStatus(openstackInstanceId)
        except:
            raise IaasException("EPROCESS-001008", [instanceName])
        if status == 'SHUTOFF':
            # 既にインスタンスは起動しているため、statusを更新して終了する。
            self.logger.info('      Instance: %s is already SHUTOFF' % (instanceName))
            # データベース更新
            table = self.conn.getTable("OPENSTACK_INSTANCE")
            updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
            updateDict["STATUS"] = status
            sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
            self.conn.execute(sql)
            return True

        self.logger.info('      Stopping Instance: %s' % (instanceName))
        try:
            instanceObj = self.client.stopVirtualMachine(openstackInstanceId)
        except:
            raise IaasException("EPROCESS-001006", [instanceName])

        status = instanceObj.status
        if status != 'SHUTOFF':
            raise IaasException("EPROCESS-001007", [instanceName, status])

        self.logger.info('      StopInstance finished. Status: %s' % (status))

        # データベース更新
        table = self.conn.getTable("OPENSTACK_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["STATUS"] = status
        sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)

        return True

    def terminateInstance(self, instanceNo):
        self.logger.info('      TerminateInstance: %s' % (instanceNo))

        # インスタンスIDの取得
        openstackInstanceTable = self.conn.getTable("OPENSTACK_INSTANCE")
        openstackInstanceInfo = self.conn.selectOne(openstackInstanceTable.select\
            (openstackInstanceTable.c.INSTANCE_NO==instanceNo))
        openstackInstanceId = openstackInstanceInfo['INSTANCE_ID']
        # インスタンス名の取得
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        instanceName = instanceInfo['FQDN']
        if isEmpty(openstackInstanceId):
            # 一度もStartしたことがない
            raise IaasException("EPROCESS-001009", [instanceName])

        # ステータスを確認する
        try:
            status = self.client.getVirtualMachineStatus(openstackInstanceId)
        except:
            raise IaasException("EPROCESS-001008", [instanceName])
        self.logger.info('      Instance: %s, Status: %s' % (instanceName, status))

        if status != 'SHUTOFF':
            # 削除を許さない。
            self.logger.error('      Instance: %s cannot be terminated because the status is not SHUTOFF (%s)' % (instanceName, status))
            raise IaasException("EPROCESS-001010", [instanceName])
        else:
            self.logger.info('      Terminating Instance: %s' % (instanceName))
            try:
                self.client.deleteVirtualMachine(openstackInstanceId)
            except:
                raise IaasException("EPROCESS-001010", [instanceName])
            # ステータスは確認しない
            self.logger.info('      Terminated %s' % (instanceName))

        return True

    def createUserData(self, instanceNo, pccInstance, azureInstance):

        table = self.conn.getTable("FARM")
        farm = self.conn.selectOne(table.select(table.c.FARM_NO==pccInstance["FARM_NO"]))

        #UserDataを作成
        userData = {}
        #DB情報
        userData.update({"instanceName": pccInstance["INSTANCE_NAME"]})
        userData.update({"farmName": farm["FARM_NAME"]})
        # FQDN
        userData.update({"hostname": pccInstance["FQDN"]})
        #初期スクリプト情報
        userData.update({"scriptserver": getScriptProperty("script.server")})

        #DNS情報
        userData.update(self.createDnsUserData(instanceNo))

        # Puppet情報
        userData.update(self.createPuppetUserData())

        # VPN情報
        internal = self.platforminfo["internal"]
        if (internal == 0):
            userData.update(self.createVpnUserData(pccInstance))

        return userData;

    def  createDnsUserData(self,instanceNo):
        # UserDataを作成
        userData = {}
        # Primary DNSサーバ
        userData.update({"dns": getDnsProperty("dns.server")})

        # Secondry DNSサーバ
        dns2 = getDnsProperty("dns.server2")
        if (isNotEmpty(dns2)):
            userData.update({"dns2": dns2})

        # DNSドメイン
        userData.update({"dnsdomain": getDnsProperty("dns.domain")})

        return userData;

    def createPuppetUserData(self):
        # UserDataを作成
        userData = {}
        # PuppetMaster情報
        userData.update({"puppetmaster": getPuppetProperty("puppet.masterHost")})
        return userData;

    def createVpnUserData(self, pccInstance):
        # UserDataを作成
        userData = {}

        #VPN情報のユーザとパスワードをセットする
        userData.update({"vpnuser": pccInstance["FQDN"]})
        userData.update({"vpnuserpass": pccInstance["INSTANCE_CODE"]})

        # VPNサーバ情報
        userData.update({"vpnserver": getVpnProperty("vpn.server")})
        userData.update({"vpnport": getVpnProperty("vpn.port")})
        # userData.update({"vpnuser": getVpnProperty("vpn.user")})
        # userData.update({"vpnuserpass": getVpnProperty("vpn.userpass")})

        # ZIPパスワード
        userData.update({"vpnzippass": getVpnProperty("vpn.zippass")})

        # クライアント証明書ダウンロードURL
        userData.update({"vpnclienturl": getVpnProperty("vpn.clienturl")})
        
        return userData;

    def makeUserData(self, map):
        if not map or len(map) == 0:
            return None

        userdata = ''
        for key in map.keys():
            value = map[key]
            if isNotEmpty(value):
                if userdata != '':
                    userdata = userdata + ';'

                userdata = userdata + key + "=" + value

        return userdata

