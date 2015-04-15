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
from iaasgw.utils.stringUtils import isEmpty, isNotEmpty, startsWithIgnoreCase
from iaasgw.controller.azure.azureVolumeController import \
    azureVolumeController
from iaasgw.common.passwordEncryptor import PasswordEncryptor
import traceback
import commands

class azureInstanceController(object):

    logger = IaasLogger()

    client = None
    conn = None
    platforminfo = None
    volumecontroller = None

    def __init__(self, platforminfo, azureiaasclient, conn):
        self.client = azureiaasclient
        self.conn = conn
        self.platforminfo = platforminfo
        self.volumecontroller = azureVolumeController(self.platforminfo, self.client, self.conn)
        self.passwordencryptor = PasswordEncryptor()

    def _generateOsHardDisk(self, storageAccountName, instanceName, imageName):
        # OS用ハードディスクの設定を生成する
        # URLを生成
        mediaLink = 'https://%s.blob.core.windows.net/vhds/%s.vhd' % \
            (storageAccountName, instanceName)
        return self.client.generateOSHardDiskConfig(mediaLink, imageName)

    def _generateConfigurationSet(self, osType, instanceNo):
        # 仮想マシン作成時に指定する設定を生成する
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        # ホスト名
        hostName = instanceInfo['INSTANCE_NAME']

        farmNo = instanceInfo['FARM_NO']
        farmTable = self.conn.getTable("FARM")
        farmInfo = self.conn.selectOne(farmTable.select\
            (farmTable.c.FARM_NO==farmNo))
        userNo = farmInfo['USER_NO']
        userTable = self.conn.getTable("USER")
        userInfo = self.conn.selectOne(userTable.select\
            (userTable.c.USER_NO==userNo))

        azureInstanceTable = self.conn.getTable("AZURE_INSTANCE")
        azureInstanceInfo = self.conn.selectOne(azureInstanceTable.select\
            (azureInstanceTable.c.INSTANCE_NO==instanceNo))

        platformNo = instanceInfo['PLATFORM_NO']
        azureCertificateTable = self.conn.getTable("AZURE_CERTIFICATE")
        azureCertificateInfo = self.conn.selectOne(azureCertificateTable.select\
            (azureCertificateTable.c.USER_NO==userNo and azureCertificateTable.c.PLATFORM_NO==platformNo))
        keyPublic = azureCertificateInfo['KEY_PUBLIC']

        # カスタムデータ
        userData = self.createUserData(instanceNo, instanceInfo, azureInstanceInfo, keyPublic)
        userData = self.makeUserData(userData)
        customData = userData
        self.logger.debug("userData:"+userData)

        # Salt情報
        pccSystemInfoTable = self.conn.getTable("PCC_SYSTEM_INFO")
        pccSystemInfo = self.conn.selectOne(pccSystemInfoTable.select())

        # パスワード復号化
        userPassword = self.passwordencryptor.decrypt(userInfo['PASSWORD'], pccSystemInfo['SECRET_KEY'])

        if osType == 'Linux':
            # XXX: rootのsshキー認証が設定できるのが望ましい
            # Linuxのユーザ名
            userName = userInfo['USERNAME']

            return self.client.generateLinuxConfig(hostName, userName, userPassword, customData)
        elif osType == 'Windows':
            # Windowsのユーザ名
            userName = userInfo['USERNAME']

            return self.client.generateWindowsConfig(hostName, userName, userPassword, customData)
        else:
            return None

    def _generateVirtualMachineName(self, instanceNo):
        # インスタンスNoとサーバ名を組み合わせてAzureの仮想マシン名を生成する
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        # サーバ名
        instanceName = instanceInfo['INSTANCE_NAME']

        # "インスタンスNo"_"サーバ名"をAzureの仮想マシン名とする
        return '%s_%s' % (instanceNo, instanceName)

    def _createInstance(self, platformNo, instanceNo):
        # 作成パラメータの取得
        platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
        platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
            (platformAzureTable.c.PLATFORM_NO==platformNo))

        # クラウドサービス名
        cloudService = platformAzureInfo['CLOUD_SERVICE_NAME']
        self.logger.debug('      Cloud Serivce: %s' % (cloudService))
        # ストレージアカウント名
        storageAccount = platformAzureInfo['STORAGE_ACCOUNT_NAME']
        self.logger.debug('      Storage Account: %s' % (storageAccount))
        # ネットワーク名
        networkName = platformAzureInfo['NETWORK_NAME']
        self.logger.debug('      Network: %s' % (networkName))

        # インスタンス名
        instanceName = self._generateVirtualMachineName(instanceNo)
        self.logger.debug('      Virtual Machine Name: %s' % (instanceName))

        # インスタンスタイプ
        azureInstanceTable = self.conn.getTable("AZURE_INSTANCE")
        azureInstanceInfo = self.conn.selectOne(azureInstanceTable.select\
            (azureInstanceTable.c.INSTANCE_NO==instanceNo))
        instanceType = azureInstanceInfo['INSTANCE_TYPE']
        self.logger.debug('      Instance Type: %s' % (instanceType))

        # 可用性セット
        availabilitySet = azureInstanceInfo['AVAILABILITY_SET']
        self.logger.debug('      Availability Set: %s' % (availabilitySet))

        # サブネットID
        subnetID = azureInstanceInfo['SUBNET_ID']
        self.logger.debug('      Subnet ID: %s' % (subnetID))
        networkConfig = self.client.generateNetworkConfig(subnetID)

        # イメージ名
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        imageNo = instanceInfo['IMAGE_NO']
        imageAzureTable = self.conn.getTable("IMAGE_AZURE")
        imageAzureInfo = self.conn.selectOne(imageAzureTable.select\
            (imageAzureTable.c.IMAGE_NO==imageNo))
        imageName = imageAzureInfo['IMAGE_NAME']
        self.logger.debug('      Image: %s' % (imageName))

        # OSタイプ(Azureから取得)
        osType = self.client.identifyImageOsType(imageName)
        self.logger.debug('      OS: %s' % (osType))

        # ホスト名、パスワード等のOSにまつわる設定
        osConfig = self._generateConfigurationSet(osType, instanceNo)
        osHardDisk = self._generateOsHardDisk(storageAccount, instanceName, imageName)

        # VM作成
        # XXX: AZURE_INSTANCEテーブルには存在していないが、Azureに存在する場合はどうするか？
        status = self.client.createVirtualMachine(cloudService, instanceName, \
            osConfig, osHardDisk, instanceType, networkConfig, networkName, availabilitySet)
        # XXX: プライベートIPを取得する前に、VMの作成成功を確認する
        privateIp = self.client.getVirtualMachineIpAddress(cloudService, instanceName)
        self.logger.debug('      Instance: %s, Status: %s, Private IP Address: %s' \
            % (instanceName, status, privateIp))

        # 異常系テストコード
        #status = 'None'
        # XXX: 非同期操作のステータスを見るべき？
        if status != 'ReadyRole':
            # 新規作成時、VMが起動成功しなかったら、データベースを更新せずに終了
            # XXX: status ReadyRoleになる前にタイムアウトした場合と、最終的にReadyRoleにならない場合に分けて検討する必要あり。
            raise IaasException("EPROCESS-000918", [instanceName, status])

        # データベース更新
        table = self.conn.getTable("AZURE_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["INSTANCE_NAME"] = instanceName
        updateDict["STATUS"] = status
        updateDict["PRIVATE_IP_ADDRESS"] = privateIp
        sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)

    def startInstance(self, instanceNo):
        self.logger.info('      StartInstance: %s' % (instanceNo))

        # プラットフォーム番号の取得
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        platformNo = instanceInfo['PLATFORM_NO']

        # 仮想マシンの起動実績確認
        azureInstanceTable = self.conn.getTable("AZURE_INSTANCE")
        azureInstanceInfo = self.conn.selectOne(azureInstanceTable.select\
            (azureInstanceTable.c.INSTANCE_NO==instanceNo))
        # インスタンス名
        instanceName = azureInstanceInfo['INSTANCE_NAME']
        if isEmpty(instanceName):
            # 仮想マシンの新規作成
            self._createInstance(platformNo, instanceNo)
            return
        else:
            # クラウドサービス名
            platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
            platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
                (platformAzureTable.c.PLATFORM_NO==platformNo))
            cloudService = platformAzureInfo['CLOUD_SERVICE_NAME']

            # 既存仮想マシンの起動
            # Azure上に仮想マシンが存在するか確認
            status = self.client.getVirtualMachineStatus(cloudService, instanceName)
            self.logger.info('      Instance: %s, Status: %s' % (instanceName, status))
            if status is None:
                # インスタンスが存在しないまたは、状態取得に失敗した場合は、処理を終了する
                # ステータスは変更しない
                raise IaasException("EPROCESS-000914", [instanceName])
            elif status == 'ReadyRole':
                self.logger.info('      Instance %s is already running' % (instanceName))
                # インスタンスタイプをチェックし、食い違う場合は警告をログに出力する
                # インスタンスタイプ
                instanceType = azureInstanceInfo['INSTANCE_TYPE']
                currentInstanceType = self.client.getVirtualMachineType(cloudService, instanceName)
                if currentInstanceType != instanceType:
                    raise IaasException("EPROCESS-000913", [instanceName])
                # Azure上では起動している場合、DBを更新して終了。
                pass
            else:
                # ReadyRole以外の場合、起動を試みる
                # インスタンスタイプをチェックし、食い違う場合は警告をログに出力する
                # インスタンスタイプ
                instanceType = azureInstanceInfo['INSTANCE_TYPE']
                currentInstanceType = self.client.getVirtualMachineType(cloudService, instanceName)
                if currentInstanceType != instanceType:
                    self.logger.info('      Changing its instance type to %s' % (instanceType))

                    # サブネット
                    subnetID = azureInstanceInfo['SUBNET_ID']
                    self.logger.info('      Subnet ID: %s' % (subnetID))
                    networkConfig = self.client.generateNetworkConfig(subnetID)
                    # 可用性セット
                    availabilitySet = azureInstanceInfo['AVAILABILITY_SET']
                    self.logger.info('      Availability Set: %s' % (availabilitySet))

                    # インスタンスタイプの変更を反映
                    newInstanceType = self.client.updateVirtualMachineType(cloudService, instanceName, instanceType, \
                                                                           networkConfig, availabilitySet)
                    if isEmpty(newInstanceType):
                        # インスタンスタイプの変更に失敗した場合は、起動も行わない
                        raise IaasException("EPROCESS-000913", [instanceName])
                    self.logger.info('      Instance type for %s has been updated: %s' % (instanceName, newInstanceType))

                # インスタンス起動
                status = self.client.startVirtualMachine(cloudService, instanceName)

            # 異常系テストコード
            #status = 'None'
            if status != 'ReadyRole':
                # VMが起動成功しなかったら、データベースを更新せずに終了
                raise IaasException("EPROCESS-000906", [instanceName, status])

            # データベース更新
            table = self.conn.getTable("AZURE_INSTANCE")
            updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
            updateDict["STATUS"] = status
            sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
            self.conn.execute(sql)
            return

    def stopInstance(self, instanceNo):
        # インスタンス名の取得
        azureInstanceTable = self.conn.getTable("AZURE_INSTANCE")
        azureInstanceInfo = self.conn.selectOne(azureInstanceTable.select\
            (azureInstanceTable.c.INSTANCE_NO==instanceNo))
        instanceName = azureInstanceInfo['INSTANCE_NAME']

        #1度も起動されていない
        if (isEmpty(instanceName)) :
            return

        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        platformNo = instanceInfo['PLATFORM_NO']

        platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
        platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
            (platformAzureTable.c.PLATFORM_NO==platformNo))

        # クラウドサービス名の取得
        cloudService = platformAzureInfo['CLOUD_SERVICE_NAME']


        status = self.client.getVirtualMachineStatus(cloudService, instanceName)
        self.logger.info('      Instance: %s, Status: %s' % (instanceName, status))

        if status != 'ReadyRole' and status != 'RoleStateUnknown':
            if status == 'StoppedVM':
                self.logger.info('      Instance: %s is already stopped' % (instanceName))
            else:
                # 停止できる状態ではない。
                raise IaasException("EPROCESS-000908", [instanceName, status])
        else:
            status = self.client.stopVirtualMachine(cloudService, instanceName)

        # データベース更新
        table = self.conn.getTable("AZURE_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["STATUS"] = status
        sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)

        # ボリュームに関する処理
        azureDiskTable = self.conn.getTable("AZURE_DISK")
        disks = self.conn.select(azureDiskTable.select(azureDiskTable.c.INSTANCE_NO==instanceNo))

        for azureDisk in disks:
            self.volumecontroller.stopVolume(instanceNo, azureDisk["DISK_NO"])

        return

    def terminateInstance(self, instanceNo):
        instanceTable = self.conn.getTable("INSTANCE")
        instanceInfo = self.conn.selectOne(instanceTable.select\
            (instanceTable.c.INSTANCE_NO==instanceNo))
        platformNo = instanceInfo['PLATFORM_NO']

        platformAzureTable = self.conn.getTable("PLATFORM_AZURE")
        platformAzureInfo = self.conn.selectOne(platformAzureTable.select\
            (platformAzureTable.c.PLATFORM_NO==platformNo))

        # クラウドサービス名の取得
        cloudService = platformAzureInfo['CLOUD_SERVICE_NAME']
        # ストレージアカウント名の取得
        storageAccount = platformAzureInfo['STORAGE_ACCOUNT_NAME']

        # インスタンス名の取得
        azureInstanceTable = self.conn.getTable("AZURE_INSTANCE")
        azureInstanceInfo = self.conn.selectOne(azureInstanceTable.select\
            (azureInstanceTable.c.INSTANCE_NO==instanceNo))
        instanceName = azureInstanceInfo['INSTANCE_NAME']

        status = self.client.getVirtualMachineStatus(cloudService, instanceName)
        self.logger.info('      Instance: %s, Status: %s' % (instanceName, status))

        if status is None:
            # 既に削除されているので、何もしない
            self.logger.info('      Instance: %s does not exist, Status: %s' % (instanceName, status))
            self.conn.info(instanceInfo["FARM_NO"], None, None, instanceNo, \
                           instanceInfo["INSTANCE_NAME"], "AzureInstanceNotExist",["AZURE", instanceName, status])
        elif status == 'StoppedVM':
            self.client.deleteVirtualMachine(cloudService, instanceName, storageAccount, \
                                             instanceNo, instanceInfo["FARM_NO"], instanceInfo["INSTANCE_NAME"])
            self.logger.info('      Terminated %s' % (instanceName))
        else:
            # 削除を許さない。
            self.logger.error('      Instance: %s cannot be terminated because the status is not StoppedVM (%s)' % (instanceName, status))
            self.conn.error(instanceInfo["FARM_NO"], None, None, instanceNo, \
                            instanceInfo["INSTANCE_NAME"], "AzureInstanceDeleteFail",["AZURE", instanceName, status])
            raise IaasException("EPROCESS-000904", [instanceName])

        # データベース更新
        # XXX: テスト目的で、terminate後のDB状態でstart(作成)できるようにしている
        table = self.conn.getTable("AZURE_INSTANCE")
        updateDict = self.conn.selectOne(table.select(table.c.INSTANCE_NO==instanceNo))
        updateDict["INSTANCE_NAME"] = None
        updateDict["STATUS"] = None
        updateDict["SUBNET_ID"] = None
        updateDict["PRIVATE_IP_ADDRESS"] = None
        sql = table.update(table.c.INSTANCE_NO == updateDict["INSTANCE_NO"], values=updateDict)
        self.conn.execute(sql)
        return

    def createUserData(self, instanceNo, pccInstance, azureInstance, keyPublic):

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
        # Publicキー
        userData.update({"sshpubkey": keyPublic})

        #DNS情報
        userData.update(self.createDnsUserData(instanceNo))

        # Puppet情報
        userData.update(self.createPuppetUserData())

        # VPN情報
        # XXX: VPNについては別途検討とする
        #internal = self.platforminfo["internal"]
        #if (internal == 0):
        #    userData.update(self.createVpnUserData(pccInstance))

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

