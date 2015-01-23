# -*- coding: utf-8 -*-
import subprocess
import sys
import re
import os
import json
import common.CommonUtils as CommonUtils
from db.MysqlConnector import MysqlConnector
from ast import literal_eval

class PlatformManager:

    conn = None

    def __init__(self):
        #DBコネクション取得
        self.conn = MysqlConnector()

    def addPlatform(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']

        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #引数から変数へセット
        iaasName = paramDict['iaasName']
        platformName = paramDict['platformName']
        platformNameDisp = paramDict['platformNameDisp']
        platformSimpleDisp = paramDict['platformSimpleDisp']
        internal = 0
        proxy = 0
        if "internal" in paramDict:
            internal = paramDict['internal']
        if "proxy" in paramDict:
            proxy = paramDict['proxy']
        
        try:
            #プラットフォームの同名存在チェック
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        #既に同名のプラットフォームが存在する場合
        if plData != None:
            return {'result':'1','message':"指定されたプラットフォーム名称は既に使用されています。他の名称を設定して下さい。"}
        
        try:
            #PLATFORMテーブルへのデータ登録
            platform = self.conn.getTable("PLATFORM")
            sql = platform.insert({"PLATFORM_NAME":platformName,
                    "PLATFORM_NAME_DISP":platformNameDisp,
                    "PLATFORM_SIMPLENAME_DISP":platformSimpleDisp,
                    "INTERNAL":internal,
                    "PROXY":proxy,
                    "PLATFORM_TYPE":iaasName,
                    "SELECTABLE":"1"
                    })
            self.conn.execute(sql)
            #ここでコミットしないと子テーブルのPLATFROM_NOが取得出来ない
            self.conn.commit()
        except Exception as e:
            print(e)
            self.conn.rollback()
            return {'result':'1','message':"PLATFORMテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}

        platformNo = CommonUtils.getPlatformDataByName(platformName)["PLATFORM_NO"]
        
        #iaas毎の登録処理呼び出し
        #AWSプラットフォームの場合
        if "aws" == iaasName:
            #aws用引数を変数にセット
            endpoint = paramDict['endpoint']
            euca = 0
            region = None
            availabilityZone = None
            vpcId = None
            if "euca" in paramDict:
                euca = paramDict['euca']
            if "region" in paramDict:
                region = paramDict['region']
            if "availabilityZone" in paramDict:
                availabilityZone = paramDict['availabilityZone']
            if "vpcId" in paramDict:
                vpcId = paramDict['vpcId']
            try:
                retDict = self.addAwsPlatform(platformNo, platformName, endpoint, euca, region, availabilityZone, vpcId)
            except Exception as e:
                retDict = {'result':'1','message':"AWSプラットフォームの登録に失敗しました。管理者に連絡を行って下さい。"}
        
        #VMwareプラットフォームの場合
        elif "vmware" == iaasName:
            #VMware用引数を変数にセット
            endpoint = paramDict['endpoint']
            userName = paramDict['userName']
            password = paramDict['password']
            datacenter = paramDict['datacenter']
            publicNetwork = paramDict['publicNetwork']
            privateNetwork = paramDict['privateNetwork']
            computeResource = paramDict['computeResource']
            instanceTypeName = paramDict['instanceTypeName']
            cpu = paramDict['cpu']
            memory  = paramDict['memory']
            
            try:
                retDict = self.addVmwarePlatform(platformNo, platformName, endpoint, userName, password, datacenter, publicNetwork, privateNetwork, computeResource, instanceTypeName, cpu, memory)
            except Exception as e:
                retDict = {'result':'1','message':"VMwareプラットフォームの登録に失敗しました。管理者に連絡を行って下さい。"}
        
        #CoudStackプラットフォームの場合
        elif "cloudstack" == iaasName:
            #CloudStack用引数を変数にセット
            endpoint = paramDict['endpoint']
            zoneId = paramDict['zoneId']
            networkId = paramDict['networkId']
            deviceType = paramDict['deviceType']
            timeout = 3000
            hostId = None
            if "timeout" in paramDict:
                timeout = paramDict['timeout']
            if "hostId" in paramDict:
                hostId = paramDict['hostId']
                
            try:
                retDict = self.addCloudstackPlatform(platformNo, platformName, endpoint, zoneId, networkId, timeout, deviceType, hostId)
            except Exception as e:
                retDict = {'result':'1','message':"CloudStackプラットフォームの登録に失敗しました。管理者に連絡を行って下さい。"}
        
        #vCloudプラットフォームの場合
        elif "vcloud" == iaasName:
            #vCloud用引数を変数にセット
            endpoint = paramDict['endpoint']
            orgName = paramDict['orgName']
            vdcName = paramDict['vdcName']
            instanceTypeName = paramDict['instanceTypeName']
            cpu = paramDict['cpu']
            memory  = paramDict['memory']
            storageTypeName = paramDict['storageTypeName']
            timeout = 3000
            defNetwork = None
            if "timeout" in paramDict:
                timeout = paramDict['timeout']
            if "defNetwork" in paramDict:
                defNetwork = paramDict['defNetwork']
                
            try:
                retDict = self.addVcloudPlatform(platformNo, platformName, endpoint, orgName, vdcName, timeout, defNetwork, instanceTypeName, cpu, memory, storageTypeName)
            except Exception as e:
                retDict = {'result':'1','message':"vCloudプラットフォームの登録に失敗しました。管理者に連絡を行って下さい。"}
        
        #OpenStackプラットフォームの場合
        elif "openstack" == iaasName:
            #OpenStack用引数を変数にセット
            endpoint = paramDict['endpoint']
            networkId = paramDict['networkId']
            tenantId = paramDict['tenantId']
            tenantName = None
            availabilityZone = None
            if "tenantName" in paramDict:
                tenantName = paramDict['tenantName']
            if "availabilityZone" in paramDict:
                availabilityZone = paramDict['availabilityZone']
                
            try:
                retDict = self.addOpenstackPlatform(platformNo, platformName, endpoint, networkId, tenantId, tenantName, availabilityZone)
            except Exception as e:
                retDict = {'result':'1','message':"OpenStackプラットフォームの登録に失敗しました。管理者に連絡を行って下さい。"}

        #Azureプラットフォームの場合
        elif "azure" == iaasName:
            #Azure用引数を変数にセット
            region = paramDict['region']
            affinityGroupName = paramDict['affinityGroupName']
            cloudServiceName = paramDict['cloudServiceName']
            storageAccountName = paramDict['storageAccountName']
            networkName = paramDict['networkName']
            availabilitySets = None
            if "availabilitySets" in paramDict:
                availabilitySets = paramDict['availabilitySets']
                
            try:
                retDict = self.addAzurePlatform(platformNo, platformName, region, affinityGroupName, cloudServiceName, storageAccountName, networkName, availabilitySets)
            except Exception as e:
                retDict = {'result':'1','message':"Azureプラットフォームの登録に失敗しました。管理者に連絡を行って下さい。"}

        #Niftyプラットフォームの場合
        elif "nifty" == iaasName:
            #Nifty用引数を変数にセット(未決)
                
            try:
                retDict = self.addNiftyPlatform(platformNo, platformName)
            except Exception as e:
                retDict = {'result':'1','message':"Niftyプラットフォームの登録に失敗しました。管理者に連絡を行って下さい。"}
        
        if retDict['result'] == "1":
            #エラー終了時、登録したプラットフォームデータを削除
            platform.delete(platform.c.PLATFORM_NO == platformNo).execute()
        
        #戻り値を返却して終了
        return retDict

    def addAwsPlatform(self, platformNo, platformName, endpoint, euca, region, availabilityZone, vpcId):
        #endopointを分解
        urlDict = CommonUtils.getSplittedUrl(endpoint)
        scheme = urlDict['scheme']
        host = urlDict['host']
        port = urlDict['port']
        vpc = 0
        secure = 0
        if vpcId is not None:
            vpc = 1
        if "https" == scheme:
            secure = 1
            if port is None:
                port = 443
        elif "http" == scheme:
            if port is None:
                port = 80
        else:
            return {'result':'1','message':"endpointはURL形式(http://host:port/path または https://host:port/path)で入力して下さい。"}
        try:
            #PLATFORM_AWSテーブル登録処理実行
            platformAws = self.conn.getTable("PLATFORM_AWS")
            sql = platformAws.insert({"PLATFORM_NO":platformNo,
                    "HOST":host,
                    "PORT":port,
                    "SECURE":secure,
                    "EUCA":euca,
                    "VPC":vpc,
                    "REGION":region,
                    "AVAILABILITY_ZONE":availabilityZone,
                    "VPC_ID":vpcId
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_AWSテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        self.conn.commit()
        return {'result':'0','message':"AWSプラットフォーム:" + platformName + "の登録が完了しました。"}

    def addVmwarePlatform(self, platformNo, platformName, endpoint, userName, password, datacenter, publicNetwork, privateNetwork, computeResource, instanceTypeName, cpu, memory):
        try:
            #PLATFORM_VMWAREテーブル登録処理実行
            platformVmware = self.conn.getTable("PLATFORM_VMWARE")
            sql = platformVmware.insert({"PLATFORM_NO":platformNo,
                    "URL":endpoint,
                    "USERNAME":userName,
                    "PASSWORD":password,
                    "DATACENTER":datacenter,
                    "PUBLIC_NETWORK":publicNetwork,
                    "PRIVATE_NETWORK":privateNetwork,
                    "COMPUTE_RESOURCE":computeResource
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWAREテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}

        try:
            #PLATFORM_VMWARE_INSTANCE_TYPEテーブル登録処理実行
            platformVmwareInstanceType = self.conn.getTable("PLATFORM_VMWARE_INSTANCE_TYPE")
            sql = platformVmwareInstanceType.insert({"PLATFORM_NO":platformNo,
                    "INSTANCE_TYPE_NAME":instanceTypeName,
                    "CPU":cpu,
                    "MEMORY":memory
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWARE_INSTANCE_TYPEテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        self.conn.commit()
        return {'result':'0','message':"VMwareプラットフォーム:" + platformName + "の登録が完了しました。"}

    def addCloudstackPlatform(self, platformNo, platformName, endpoint,zoneId, networkId, timeout, deviceType, hostId):
        #endopointを分解
        urlDict = CommonUtils.getSplittedUrl(endpoint)
        scheme = urlDict['scheme']
        host = urlDict['host']
        port = urlDict['port']
        path = urlDict['path']
        secure = 0
        if "https" == scheme:
            secure = 1
            if port is None:
                port = 443
        elif "http" == scheme:
            if port is None:
                port = 80
        else:
            return {'result':'1','message':"endpointはURL形式(http://host:port/path または https://host:port/path)で入力して下さい。"}
        try:
            #PLATFORM_CLOUDSTACKテーブル登録処理実行
            platformCloudstack = self.conn.getTable("PLATFORM_CLOUDSTACK")
            sql = platformCloudstack.insert({"PLATFORM_NO":platformNo,
                    "HOST":host,
                    "PATH":path,
                    "PORT":port,
                    "SECURE":secure,
                    "ZONE_ID":zoneId,
                    "NETWORK_ID":networkId,
                    "TIMEOUT":timeout,
                    "DEVICE_TYPE":deviceType,
                    "HOST_ID":hostId
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_CLOUDSTACKテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        self.conn.commit()
        return {'result':'0','message':"CloudStackプラットフォーム:" + platformName + "の登録が完了しました。"}

    def addVcloudPlatform(self, platformNo, platformName, endpoint, orgName, vdcName, timeout, defNetwork, instanceTypeName, cpu, memory, storageTypeName):
        #endopointを分解
        urlDict = CommonUtils.getSplittedUrl(endpoint)
        scheme = urlDict['scheme']
        host = urlDict['host']
        secure = 0
        if "https" == scheme:
            secure = 1

        try:
            #PLATFORM_VCLOUDテーブル登録処理実行
            platformVcloud = self.conn.getTable("PLATFORM_VCLOUD")
            sql = platformVcloud.insert({"PLATFORM_NO":platformNo,
                    "URL":host,
                    "ORG_NAME":orgName,
                    "VDC_NAME":vdcName,
                    "SECURE":secure,
                    "TIMEOUT":timeout,
                    "DEF_NETWORK":defNetwork
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUDテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #PLATFORM_VCLOUD_INSTANCE_TYPEテーブル登録処理実行
            platformVcloudInstanceType = self.conn.getTable("PLATFORM_VCLOUD_INSTANCE_TYPE")
            sql = platformVcloudInstanceType.insert({"PLATFORM_NO":platformNo,
                    "INSTANCE_TYPE_NAME":instanceTypeName,
                    "CPU":cpu,
                    "MEMORY":memory
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_INSTANCE_TYPEテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #PLATFORM_VCLOUD_STORAGE_TYPEテーブル登録処理実行
            platformVcloudStorageType = self.conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
            sql = platformVcloudStorageType.insert({"PLATFORM_NO":platformNo,
                    "STORAGE_TYPE_NAME":storageTypeName
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_STORAGE_TYPEテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}

        self.conn.commit()
        return {'result':'0','message':"vCloudプラットフォーム:" + platformName + "の登録が完了しました。"}

    def addOpenstackPlatform(self, platformNo, platformName, endpoint, networkId, tenantId, tenantName, availabilityZone):
        try:
            #PLATFORM_OPENSTACKテーブル登録処理実行
            tablePlOpenStack = self.conn.getTable("PLATFORM_OPENSTACK")
            sql = tablePlOpenStack.insert({"PLATFORM_NO":platformNo,
                    "URL":endpoint,
                    "NETWORK_ID":networkId,
                    "TENANT_ID":tenantId,
                    "TENANT_NM":tenantName,
                    "AVAILABILITY_ZONE":availabilityZone
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_OPENSTACKテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        self.conn.commit()
        return {'result':'0','message':"OpenStackプラットフォーム:" + platformName + "の登録が完了しました。"}

    def addAzurePlatform(self, platformNo, platformName, region, affinityGroupName, cloudServiceName, storageAccountName, networkName, availabilitySets):
        try:
            #PLATFORM_AZUREテーブル登録処理実行
            platformAzure = self.conn.getTable("PLATFORM_AZURE")
            sql = platformAzure.insert({"PLATFORM_NO":platformNo,
                    "LOCATION_NAME":region,
                    "AFFINITY_GROUP_NAME":affinityGroupName,
                    "CLOUD_SERVICE_NAME":cloudServiceName,
                    "STORAGE_ACCOUNT_NAME":storageAccountName,
                    "NETWORK_NAME":networkName,
                    "AVAILABILITY_SETS":availabilitySets
                    })
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_AZUREテーブルへの登録に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        self.conn.commit()
        return {'result':'0','message':"Azureプラットフォーム:" + platformName + "の登録が完了しました。"}

    def addNiftyPlatform(self, platformNo, platformName):
        return {'result':'0','message':"addNiftyはスタブです。"}

    def updatePlatform(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']

        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #引数から変数へセット
        platformNo = paramDict['platformNo']
        
        try:
        #プラットフォームデータ取得
            plData = CommonUtils.getPlatformDataByNo(platformNo)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム情報の取得に失敗したため登録処理を終了します。管理者に連絡を行って下さい。"}
        #プラットフォームデータが存在しない場合、エラー
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォームNoは存在しません。更新対象を確認して下さい。"}
        
        if "platformName" in paramDict:
            try:
                #プラットフォームの同名存在チェック
                platformName = paramDict["platformName"]
                checkPlData = CommonUtils.getPlatformDataByName(platformName)
            except Exception as e:
                return {'result':'1','message':"プラットフォーム情報の取得に失敗したため登録処理を終了します。管理者に連絡を行って下さい。"}
            
            #既に同名のプラットフォームが更新対象レコード以外に存在する場合
            if platformName != plData["PLATFORM_NAME"] and checkPlData != None:
                return {'result':'1','message':"指定されたプラットフォーム名称は既に使用されています。他の名称を設定して下さい。"}
        
        #更新データ作成
        if "platformName" in paramDict:
            plData["PLATFORM_NAME"] = paramDict['platformName']
        if "platformNameDisp" in paramDict:
            plData["PLATFORM_NAME_DISP"] = paramDict['platformNameDisp']
        if "platformSimpleDisp" in paramDict:
            plData["PLATFORM_SIMPLENAME_DISP"] = paramDict['platformSimpleDisp']
        
        try:
            #PLATFORMテーブルのデータ更新
            platform = self.conn.getTable("PLATFORM")
            sql = platform.update(platform.c.PLATFORM_NO == plData["PLATFORM_NO"], values = plData)
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORMテーブルの更新に失敗したため処理を中止します。プラットフォームNo:" + platformNo}

        #iaas毎の登録処理呼び出し
        iaasName = plData["PLATFORM_TYPE"]
        #AWSプラットフォームの場合
        if "aws" == iaasName:
            #aws用引数を変数にセット
            endpoint = None
            if "endpoint" in paramDict:
                endpoint = paramDict['endpoint']
            try:
                retDict = self.updateAwsPlatform(platformNo, endpoint)
            except Exception as e:
                retDict = {'result':'1','message':"AWSプラットフォームの更新に失敗しました。管理者に連絡を行って下さい。"}
        
        #VMwareプラットフォームの場合
        elif "vmware" == iaasName:
            #VMware用引数を変数にセット
            endpoint = None
            userName = None
            password = None
            publicNetwork = None
            privateNetwork = None
            if "endpoint" in paramDict:
                endpoint = paramDict['endpoint']
            if "userName" in paramDict:
                userName = paramDict['userName']
            if "password" in paramDict:
                password = paramDict['password']
            if "publicNetwork" in paramDict:
                publicNetwork = paramDict['publicNetwork']
            if "privateNetwork" in paramDict:
                privateNetwork = paramDict['privateNetwork']
            try:
                retDict = self.updateVmwarePlatform(platformNo, endpoint, userName, password, publicNetwork, privateNetwork)
            except Exception as e:
                retDict = {'result':'1','message':"VMwareプラットフォームの更新に失敗しました。管理者に連絡を行って下さい。"}
        
        #Cloudstackプラットフォームの場合
        elif "cloudstack" == iaasName:
            #Cloudstack用引数を変数にセット
            endpoint = None
            timeout = None
            if "endpoint" in paramDict:
                endpoint = paramDict['endpoint']
            if "timeout" in paramDict:
                timeout = paramDict['timeout']
            try:
                retDict = self.updateCloudstackPlatform(platformNo, endpoint, timeout)
            except Exception as e:
                retDict = {'result':'1','message':"Cloudstackプラットフォームの更新に失敗しました。管理者に連絡を行って下さい。"}
            
        #vCloudプラットフォームの場合
        elif "vcloud" == iaasName:
            #vCloud用引数を変数にセット
            endpoint = None
            timeout = None
            defNetwork = None
            if "endpoint" in paramDict:
                endpoint = paramDict['endpoint']
            if "timeout" in paramDict:
                timeout = paramDict['timeout']
            if "defNetwork" in paramDict:
                defNetwork = paramDict['defNetwork']
            try:
                retDict = self.updateVcloudPlatform(platformNo, endpoint, timeout, defNetwork)
            except Exception  as e:
                retDict = {'result':'1','message':"vCloudプラットフォームの更新に失敗しました。管理者に連絡を行って下さい。"}
        
        #Openstackプラットフォームの場合
        elif "openstack" == iaasName:
            #Openstack用引数を変数にセット
            endpoint = None
            if "endpoint" in paramDict:
                endpoint = paramDict['endpoint']
            try:
                retDict = self.updateOpenstackPlatform(platformNo, endpoint)
            except Exception as e:
                retDict = {'result':'1','message':"Openstackプラットフォームの更新に失敗しました。管理者に連絡を行って下さい。"}
        
        #正常終了時、コミット
        self.conn.commit()
        #戻り値を返却して終了
        return retDict

    def updateAwsPlatform(self, platformNo, endpoint):
        try:
            #PLATFORM_AWSテーブルのデータ更新
            platformAws = self.conn.getTable("PLATFORM_AWS")
            plAwsData = self.conn.selectOne(platformAws.select(platformAws.c.PLATFORM_NO == platformNo))
            #endpointが存在する場合
            if endpoint is not None:
                #endopointを分解
                urlDict = CommonUtils.getSplittedUrl(endpoint)
                plAwsData["HOST"] = urlDict['host']
                plAwsData["PORT"] = urlDict['port']
            sql = platformAws.update(platformAws.c.PLATFORM_NO == platformNo, values = plAwsData)
            self.conn.execute(sql)
            self.conn.commit()
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_AWSテーブルの更新に失敗したため処理を中止します。プラットフォームNo:" + platformNo}
        
        return {'result':'0','message':"AWSプラットフォームNo:" + platformNo + "の更新が完了しました。"}

    def updateVmwarePlatform(self, platformNo, endpoint, userName, password, publicNetwork, privateNetwork):
        try:
            #PLATFORM_VMWAREテーブルのデータ更新
            platformVmware = self.conn.getTable("PLATFORM_VMWARE")
            plVmwareData = self.conn.selectOne(platformVmware.select(platformVmware.c.PLATFORM_NO == platformNo))
            #各引数が存在する場合
            if endpoint is not None:
                plVmwareData["URL"] = endpoint
            if userName is not None:
                plVmwareData["USERNAME"] = userName
            if password is not None:
                plVmwareData["PASSWORD"] = password
            if publicNetwork is not None:
                plVmwareData["PUBLIC_NETWORK"] = publicNetwork
            if privateNetwork is not None:
                plVmwareData["PRIVATE_NETWORK"] = privateNetwork
            sql = platformVmware.update(platformVmware.c.PLATFORM_NO == platformNo, values = plVmwareData)
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWAREテーブルの更新に失敗したため処理を中止します。プラットフォームNo:" + platformNo}
        
        return {'result':'0','message':"VMwareプラットフォームNo:" + platformNo + "の更新が完了しました。"}

    def updateCloudstackPlatform(self, platformNo, endpoint, timeout):
        try:
            #PLATFORM_CLOUDSTACKテーブルのデータ更新
            platformCloudstack = self.conn.getTable("PLATFORM_CLOUDSTACK")
            plCloudstackData = self.conn.selectOne(platformCloudstack.select(platformCloudstack.c.PLATFORM_NO == platformNo))
            #endpointが存在する場合
            if endpoint is not None:
                #endopointを分解
                urlDict = CommonUtils.getSplittedUrl(endpoint)
                plCloudstackData["HOST"] = urlDict['host']
                plCloudstackData["PATH"] = urlDict['path']
                plCloudstackData["PORT"] = urlDict['port']
            #timeoutが存在する場合
            if timeout is not None:
                plCloudstackData["TIMEOUT"] = timeout
            sql = platformCloudstack.update(platformCloudstack.c.PLATFORM_NO == platformNo, values = plCloudstackData)
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_CLOUDSTACKテーブルの更新に失敗したため処理を中止します。プラットフォームNo:" + platformNo}
        
        return {'result':'0','message':"CloudstackプラットフォームNo:" + platformNo + "の更新が完了しました。"}

    def updateVcloudPlatform(self, platformNo, endpoint, timeout, defNetwork):
        try:
            #PLATFORM_VCLOUDテーブルのデータ更新
            platformVcloud = self.conn.getTable("PLATFORM_VCLOUD")
            plVcloudData = self.conn.selectOne(platformVcloud.select(platformVcloud.c.PLATFORM_NO == platformNo))
            #各引数が存在する場合
            if endpoint is not None:
                plVcloudData["URL"] = endpoint
            if timeout is not None:
                plVcloudData["TIMEOUT"] = timeout
            if defNetwork is not None:
                plVcloudData["DEF_NETWORK"] = defNetwork
            sql = platformVcloud.update(platformVcloud.c.PLATFORM_NO == platformNo, values = plVcloudData)
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUDテーブルの更新に失敗したため処理を中止します。プラットフォームNo:" + platformNo}
        
        return {'result':'0','message':"vCloudプラットフォームNo:" + platformNo + "の更新が完了しました。"}

    def updateOpenstackPlatform(self, platformNo, endpoint):
        try:
            #PLATFORM_OPENSTACKテーブルのデータ更新
            platformOpenstack = self.conn.getTable("PLATFORM_OPENSTACK")
            plOpenstackData = self.conn.selectOne(platformOpenstack.select(platformOpenstack.c.PLATFORM_NO == platformNo))
            #endpointが存在する場合
            if endpoint is not None:
                plOpenstackData["URL"] = endpoint
            sql = platformOpenstack.update(platformOpenstack.c.PLATFORM_NO == platformNo, values = plOpenstackData)
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_OPENSTACKテーブルの更新に失敗したため処理を中止します。プラットフォームNo:" + platformNo}
        
        return {'result':'0','message':"OpenstackプラットフォームNo:" + platformNo + "の更新が完了しました。"}

    def deletePlatform(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        platformName = paramDict["platformName"]
        #getPlatformDataByName呼び出し
        try:
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム情報の取得に失敗したため削除処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォーム名:" + platformName + "は存在しません。更新対象を確認して下さい。"}
        
        platformNo = plData["PLATFORM_NO"]
        platformType = plData["PLATFORM_TYPE"]
        
        #削除対象のプラットフォームを使用しているインスタンス存在チェック
        try:
            instacne = self.conn.getTable("INSTANCE")
        except Exception as e:
            return {'result':'1','message':"INSTANCEテーブルが存在しません。管理者に連絡を行って下さい。"}
        instanceData = self.conn.select(instacne.select(instacne.c.PLATFORM_NO == platformNo))
        if len(instanceData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在インスタンスで使用されているため削除できません。該当インスタンスを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているOSイメージ存在チェック
        try:
            image = self.conn.getTable("IMAGE")
        except Exception as e:
            return {'result':'1','message':"IMAGEテーブルが存在しません。管理者に連絡を行って下さい。"}
        imageData = self.conn.select(image.select(image.c.PLATFORM_NO == platformNo))
        if len(imageData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在OSイメージで使用されているため削除できません。該当イメージを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているAWSボリュームチェック
        try:
            awsVolume = self.conn.getTable("AWS_VOLUME")
        except Exception as e:
            return {'result':'1','message':"AWS_VOLUMEテーブルが存在しません。管理者に連絡を行って下さい。"}
        awsVolData = self.conn.select(awsVolume.select(awsVolume.c.PLATFORM_NO == platformNo))
        if len(awsVolData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在AWSのディスクボリュームで使用されているため削除できません。該当ボリュームを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているVMwareボリュームチェック
        try:
            vmwareDisk = self.conn.getTable("VMWARE_DISK")
        except Exception as e:
            return {'result':'1','message':"VMWARE_DISKテーブルが存在しません。管理者に連絡を行って下さい。"}
        vmDiskData = self.conn.select(vmwareDisk.select(vmwareDisk.c.PLATFORM_NO == platformNo))
        if len(vmDiskData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在VMwareのディスクボリュームで使用されているため削除できません。該当ボリュームを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているCloudstackボリュームチェック
        try:
            cloudstackVolume = self.conn.getTable("CLOUDSTACK_VOLUME")
        except Exception as e:
            return {'result':'1','message':"CLOUDSTACK_VOLUMEテーブルが存在しません。管理者に連絡を行って下さい。"}
        csVolData = self.conn.select(cloudstackVolume.select(cloudstackVolume.c.PLATFORM_NO == platformNo))
        if len(csVolData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在Cloudstackのディスクボリュームで使用されているため削除できません。該当ボリュームを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているvCloudボリュームチェック
        try:
            vcloudDisk = self.conn.getTable("VCLOUD_DISK")
        except Exception as e:
            return {'result':'1','message':"VCLOUD_DISKテーブルが存在しません。管理者に連絡を行って下さい。"}
        vcDiskData = self.conn.select(vcloudDisk.select(vcloudDisk.c.PLATFORM_NO == platformNo))
        if len(vcDiskData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在vCloudのディスクボリュームで使用されているため削除できません。該当ボリュームを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対対象のプラットフォームを使用しているOpenstackボリュームチェック
        try:
            openstackVolume = self.conn.getTable("OPENSTACK_VOLUME")
        except Exception as e:
            return {'result':'1','message':"OPENSTACK_VOLUMEテーブルが存在しません。管理者に連絡を行って下さい。"}
        osVolData = self.conn.select(openstackVolume.select(openstackVolume.c.PLATFORM_NO == platformNo))
        if len(osVolData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在Openstackのディスクボリュームで使用されているため削除できません。該当ボリュームを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているAzureボーリュームチェック
        try:
            azureDisk = self.conn.getTable("AZURE_DISK")
        except Exception as e:
            return {'result':'1','message':"AZURE_DISKテーブルが存在しません。管理者に連絡を行って下さい。"}
        azDiskData = self.conn.select(azureDisk.select(azureDisk.c.PLATFORM_NO == platformNo))
        if len(azDiskData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在Azureのディスクボリュームで使用されているため削除できません。該当ボリュームを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているNiftyボリュームチェック
        try:
            niftyVolume = self.conn.getTable("NIFTY_VOLUME")
        except Exception as e:
            return {'result':'1','message':"NIFTY_VOLUMEテーブルが存在しません。管理者に連絡を行って下さい。"}
        nifVolData = self.conn.select(niftyVolume.select(niftyVolume.c.PLATFORM_NO == platformNo))
        if len(nifVolData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在Niftyのディスクボリュームで使用されているため削除できません。該当ボリュームを削除してから再度プラットフォーム削除を行って下さい。"}
        #削除対象のプラットフォームを使用しているmyCloud用インスタンステンプレートチェック
        try:
            templateInstance = self.conn.getTable("TEMPLATE_INSTANCE")
        except Exception as e:
            return {'result':'1','message':"TEMPLATE_INSTANCEテーブルが存在しません。管理者に連絡を行って下さい。"}
        tmpInsData = self.conn.select(templateInstance.select(templateInstance.c.PLATFORM_NO == platformNo))
        if len(tmpInsData) > 0:
            return {'result':'1','message':"プラットフォームNo:" + str(platformNo) + "は現在インスタンステンプレートで使用されているため削除できません。プラットフォームの削除を行う場合は、管理者に連絡を行って下さい。"}
        #PlatformType毎の削除処理呼び出し
        if "aws" == platformType:
            try:
                result = self.deleteAwsPlatform(platformNo, platformName, self.conn)
            except Exception as e:
                return {'result':'1','message':"AWSプラットフォームの削除に失敗しました。管理者に連絡を行って下さい。"}
        elif "vmware" == platformType:
            try:
                result = self.deleteVmwarePlatform(platformNo, platformName, self.conn)
            except Exception as e:
                return {'result':'1','message':"VMwareプラットフォームの削除に失敗しました。管理者に連絡を行って下さい。"}
        elif "cloudstack" == platformType:
            try:
                result = self.deleteCloudstackPlatform(platformNo, platformName, self.conn)
            except Exception as e:
                return {'result':'1','message':"Cloudstackプラットフォームの削除に失敗しました。管理者に連絡を行って下さい。"}
        elif "vcloud" == platformType:
            try:
                result = self.deleteVcloudPlatform(platformNo, platformName, self.conn)
            except Exception as e:
                return {'result':'1','message':"vCloudプラットフォームの削除に失敗しました。管理者に連絡を行って下さい。"}
        elif "openstack" == platformType:
            try:
                result = self.deleteOpenstackPlatform(platformNo, platformName, self.conn)
            except Exception as e:
                return {'result':'1','message':"Openstackプラットフォームの削除に失敗しました。管理者に連絡を行って下さい。"}
        elif "azure" == platformType:
            try:
                result = self.deleteAzurePlatform(platformNo, platformName, self.conn)
            except Exception as e:
                return {'result':'1','message':"Azureプラットフォームの削除に失敗しました。管理者に連絡を行って下さい。"}
        elif "nifty" == platformType:
            try:
                result = self.deleteNiftyPlatform(platformNo, platformName, self.conn)
            except Exception as e:
                return {'result':'1','message':"Niftyプラットフォームの削除に失敗しました。管理者に連絡を行って下さい。"}
        if "1" == result['result']:
            return result
        try:
            #PLATFORMテーブルデータ削除
            tablePlatform = self.conn.getTable("PLATFORM")
            sql = tablePlatform.delete(tablePlatform.c.PLATFORM_NO == platformNo)
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORMテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        self.conn.commit()
        return {'result':'0','message':"プラットフォーム名:" + platformName + "の削除が完了しました。"}

    def deleteAwsPlatform(self, platformNo, platformName, conn):
        try:
            #PLATFORM_AWSテーブルデータ削除
            tablePlatformAws = conn.getTable("PLATFORM_AWS")
            sql = tablePlatformAws.delete(tablePlatformAws.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_AWSテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #AWS_CERTIFICATEテーブルデータの削除
            tableAwsCertificate = conn.getTable("AWS_CERTIFICATE")
            sql = tableAwsCertificate.delete(tableAwsCertificate.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"AWS_CERTIFICATEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #AWS_ADDRESSテーブルデータ削除
            tableAwsAddress = conn.getTable("AWS_ADDRESS")
            sql = tableAwsAddress.delete(tableAwsAddress.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"AWS_ADDRESSテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #AWS_SSL_KEYテーブルデータ削除
            tableAwsSslKey =conn.getTable("AWS_SSL_KEY")
            sql = tableAwsSslKey.delete(tableAwsSslKey.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"AWS_SSL_KEYテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        return {'result':'0','message':"success"}

    def deleteVmwarePlatform(self, platformNo, platformName, conn):
        try:
            #PLATFORM_VMWARE_INSTANCE_TYPEテーブルデータ削除
            tableplVmInsType = conn.getTable("PLATFORM_VMWARE_INSTANCE_TYPE")
            sql = tableplVmInsType.delete(tableplVmInsType.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWARE_INSTANCE_TYPEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #PLATFORM_VMWAREテーブルのデータ削除
            tablePlatformVmware = conn.getTable("PLATFORM_VMWARE")
            sql = tablePlatformVmware.delete(tablePlatformVmware.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWAREテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #VMWARE_KEY_PAIRテーブルデータ削除
            tableVmwareKeyPair = conn.getTable("VMWARE_KEY_PAIR")
            sql = tableVmwareKeyPair.delete(tableVmwareKeyPair.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"VMWARE_KEY_PAIRテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #VMWARE_ADDRESSテーブルデータ削除
            tableVmwareAddress = conn.getTable("VMWARE_ADDRESS")
            sql = tableVmwareAddress.delete(tableVmwareAddress.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"VMWARE_ADDRESSテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #VMWARE_NETWORKテーブルデータ削除
            tableVmwareNetwork = conn.getTable("VMWARE_NETWORK")
            sql = tableVmwareNetwork.delete(tableVmwareNetwork.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"VMWARE_NETWORKテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        return {'result':'0','message':"success"}

    def deleteCloudstackPlatform(self, platformNo, platformName, conn):
        try:
            #PLATFORM_CLOUDSTACKテーブルデータ削除
            tablePlatformCloudstack = conn.getTable("PLATFORM_CLOUDSTACK")
            sql = tablePlatformCloudstack.delete(tablePlatformCloudstack.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_CLOUDSTACKテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #CLOUDSTACK_CERTIFICATEテーブルデータ削除
            tableCloudstackCertificate = conn.getTable("CLOUDSTACK_CERTIFICATE")
            sql = tableCloudstackCertificate.delete(tableCloudstackCertificate.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"CLOUDSTACK_CERTIFICATEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #CLOUDSTACK_ADDRESSテーブルデータ削除
            tableCloudstackAddress = conn.getTable("CLOUDSTACK_ADDRESS")
            sql = tableCloudstackAddress.delete(tableCloudstackAddress.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"CLOUDSTACK_ADDRESSテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        return {'result':'0','message':"success"}

    def deleteVcloudPlatform(self, platformNo, platformName, conn):
        try:
            #PLATFORM_VCLOUD_INSTANCE_TYPEテーブルデータ削除
            tableplVcInsType = conn.getTable("PLATFORM_VCLOUD_INSTANCE_TYPE")
            sql = tableplVcInsType.delete(tableplVcInsType.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_INSTANCE_TYPEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #PLATFORM_VCLOUD_STORAGE_TYPEテーブルデータ削除
            tablePlVcloudStorageType = conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
            sql = tablePlVcloudStorageType.delete(tablePlVcloudStorageType.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_STORAGE_TYPEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #PLATFORM_VCLOUDテーブルデータ削除
            tablePlatformVcloud = conn.getTable("PLATFORM_VCLOUD")
            sql = tablePlatformVcloud.delete(tablePlatformVcloud.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUDテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #VCLOUD_CERTIFICATEテーブルデータ削除
            tableVcloudCertificate = conn.getTable("VCLOUD_CERTIFICATE")
            sql = tableVcloudCertificate.delete(tableVcloudCertificate.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"VCLOUD_CERTIFICATEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #VCLOUD_KEY_PAIRテーブルデータ削除
            tableVcloudKeyPair = conn.getTable("VCLOUD_KEY_PAIR")
            sql = tableVcloudKeyPair.delete(tableVcloudKeyPair.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"VCLOUD_KEY_PAIRテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #VCLOUD_NETWORKテーブルデータ削除
            tableVcloudNetwork = conn.getTable("VCLOUD_NETWORK")
            sql = tableVcloudNetwork.delete(tableVcloudNetwork.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"VCLOUD_NETWORKテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        return {'result':'0','message':"success"}

    def deleteOpenstackPlatform(self, platformNo, platformName, conn):
        try:
            #PLATFORM_OPENSTACKテーブルデータ削除
            tablePlatformOpenstack = conn.getTable("PLATFORM_OPENSTACK")
            sql = tablePlatformOpenstack.delete(tablePlatformOpenstack.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_OPENSTACKテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #OPENSTACK_CERTIFICATEテーブルデータ削除
            tableOpenstackCertificate = conn.getTable("OPENSTACK_CERTIFICATE")
            sql = tableOpenstackCertificate.delete(tableOpenstackCertificate.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"OPENSTACK_CERTIFICATEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #OPENSTACK_SSL_KEYテーブルデータ削除
            tableOpenstackSslKey = conn.getTable("OPENSTACK_SSL_KEY")
            sql = tableOpenstackSslKey.delete(tableOpenstackSslKey.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"OPENSTACK_SSL_KEYテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        return {'result':'0','message':"success"}

    def deleteAzurePlatform(self, platformNo, platformName, conn):
        try:
            #PLATFORM_AZUREテーブルデータ削除
            tableAzurePlatform = conn.getTable("PLATFORM_AZURE")
            sql = tableAzurePlatform.delete(tableAzurePlatform.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_AZUREテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #AZURE_CERTIFICATEテーブルデータ削除
            tableAzureCertificate = conn.getTable("AZURE_CERTIFICATE")
            sql = tableAzureCertificate.delete(tableAzureCertificate.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"AZURE_CERTIFICATEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        return {'result':'0','message':"success"}

    def deleteNiftyPlatform(self, platformNo, platformName, conn):
        try:
            #PLATFORM_NIFTYテーブルデータ削除
            tablePlatformNifty = conn.getTable("PLATFORM_NIFTY")
            sql = tablePlatformNifty.delete(tablePlatformNifty.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"PLATFORM_NIFTYテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        try:
            #NIFTY_CERTIFICATEテーブルデータ削除
            tableNiftyCertificate = conn.getTable("NIFTY_CERTIFICATE")
            sql = tableNiftyCertificate.delete(tableNiftyCertificate.c.PLATFORM_NO == platformNo)
            conn.execute(sql)
        except Exception as e:
            conn.rollback()
            return {'result':'1','message':"NIFTY_CERTIFICATEテーブルデータの削除に失敗したため処理を中止します。プラットフォーム名:" + platformName}
        return {'result':'0','message':"success"}

    def enablePlatform(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        platformName = paramDict['platformName']
        #getPlatformDataByName呼び出し
        try:
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォーム名は存在しません。有効化対象を確認して下さい。"}
        if 1 == plData["SELECTABLE"]:
            return {'result':'1','message':"指定されたプラットフォームは既に有効状態です。処理を中止します。"}
        #PLATFORMの有効化
        try:
            platform = self.conn.getTable("PLATFORM")
            updateData = self.conn.selectOne(platform.select(platform.c.PLATFORM_NO == plData["PLATFORM_NO"]))
            updateData["SELECTABLE"] = 1
            sql = platform.update(platform.c.PLATFORM_NO == plData["PLATFORM_NO"], values = updateData)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"プラットフォーム名:" + platformName + "を有効化しました。"}
        except Exception as e:
            return {'result':'1','message':"プラットフォームの有効化に失敗しました。"}

    def disablePlatform(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        platformName = paramDict['platformName']
        #getPlatformDataByName呼び出し
        try:
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォーム名は存在しません。無効化対象を確認して下さい。"}
        if 0 == plData["SELECTABLE"]:
            return {'result':'1','message':"指定されたプラットフォームは既に無効状態です。処理を中止します。"}
        #PLATFORMの無効化
        try:
            platform = self.conn.getTable("PLATFORM")
            updateData = self.conn.selectOne(platform.select(platform.c.PLATFORM_NO == plData["PLATFORM_NO"]))
            updateData["SELECTABLE"] = 0
            sql = platform.update(platform.c.PLATFORM_NO == plData["PLATFORM_NO"], values = updateData)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"プラットフォーム名:" + platformName + "を無効化しました。"}
        except Exception as e:
            return {'result':'1','message':"プラットフォームの無効化に失敗しました。"}

    def listPlatform(self):
        #データの取得
        try:
            table = self.conn.getTable("PLATFORM")
        except Exception as e:
            return {'result':'1','message':"PLATFORMテーブルが存在しません。管理者に連絡を行って下さい。"}
        plDataList = self.conn.select(table.select())
        strPlDataList = []
        status = None
        
        if len(plDataList) == 0:
            return  {'result':'1','message':"プラットフォームデータが登録されていません。"}
        
        for plData in plDataList:
            try:
                status = CommonUtils.getSelectableStatus(plData["SELECTABLE"])
            except Exception as e:
                return {'result':'1','message':"ステータス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #SELECTABLEの値をenable/disableに変換
            plData["SELECTABLE"] = status
            #JSON形式に変換し、リストに追加
            strPlData = json.dumps(plData, ensure_ascii=False)
            strPlDataList.append(strPlData)

        #リストを"&&"で結合
        retData = "&&".join(strPlDataList)
        
        return {'result':'0', 'message':"suucess", 'data':retData}

    def showPlatform(self, paramDict):
        #必須引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        platformName = paramDict['platformName']
        #getPlatformDataByName
        try:
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return{'result':'1','message':"指定されたプラットフォーム名は存在しません。参照対象を確認して下さい。"}        
        platformNo = plData["PLATFORM_NO"]
        iaasName = plData["PLATFORM_TYPE"]
        #PLATFORM_TYPE毎のプラットフォームデータ取得
        if "aws" == iaasName:
            try:
                tablePlAws = self.conn.getTable("PLATFORM_AWS")
            except Exception as e:
                return {'result':'1','message':"PLATFORM_AWSテーブルが存在しません。管理者に連絡を行って下さい。"}
            iaasPlData = self.conn.selectOne(tablePlAws.select(tablePlAws.c.PLATFORM_NO==platformNo))
        if "vmware" == iaasName:
            try:
                tablePlVm = self.conn.getTable("PLATFORM_VMWARE")
            except Exception as e:
                return {'result':'1','message':"PLATFORM_VMWAREテーブルが存在しません。管理者に連絡を行って下さい。"}
            iaasPlData = self.conn.selectOne(tablePlVm.select(tablePlVm.c.PLATFORM_NO==platformNo))
        if "cloudstack" == iaasName:
            try:
                tablePlCs = self.conn.getTable("PLATFORM_CLOUDSTACK")
            except Exception as e:
                return {'result':'1','message':"PLATFORM_CLOUDSTACKテーブルが存在しません。管理者に連絡を行って下さい。"}
            iaasPlData = self.conn.selectOne(tablePlCs.select(tablePlCs.c.PLATFORM_NO==platformNo))
        if "vcloud" == iaasName:
            try:
                tablePlVc = self.conn.getTable("PLATFORM_VCLOUD")
            except Exception as e:
                return {'result':'1','message':"PLATFORM_VCLOUDテーブルが存在しません。管理者に連絡を行って下さい。"}
            iaasPlData = self.conn.selectOne(tablePlVc.select(tablePlVc.c.PLATFORM_NO==platformNo))
        if "openstack" == iaasName:
            try:
                tablePlOs = self.conn.getTable("PLATFORM_OPENSTACK")
            except Exception as e:
                return {'result':'1','message':"PLATFORM_OPENSTACKテーブルが存在しません。管理者に連絡を行って下さい。"}
            iaasPlData = self.conn.selectOne(tablePlOs.select(tablePlOs.c.PLATFORM_NO==platformNo))
        if "azure" == iaasName:
            try:
                tablePlAz = self.conn.getTable("PLATFORM_AZURE")
            except Exception as e:
                return {'result':'1','message':"PLATFORM_AZUREテーブルが存在しません。管理者に連絡を行って下さい。"}
            iaasPlData = self.conn.selectOne(tablePlAz.select(tablePlAz.c.PLATFORM_NO==platformNo))
        if "nifty" == iaasName:
            try:
                tablePlNif = self.conn.getTable("PLATFORM_NIFTY")
            except Exception as e:
                return {'result':'1','message':"PLATFORM_NIFTYテーブルが存在しません。管理者に連絡を行って下さい。"}
            iaasPlData = self.conn.selectOne(tablePlNif.select(tablePlNif.c.PLATFORM_NO==platformNo))
        
        #プラットフォームデータにIaaSごとのデータを結合
        plData.update(iaasPlData)
        
        #SELECTABLEの値を変換
        try:
            status = CommonUtils.getSelectableStatus(plData["SELECTABLE"])
        except Exception as e:
            return {'result':'1','message':"ステータス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}

        plData["SELECTABLE"] = status

        #返却用データの編集
        retData = json.dumps(plData, ensure_ascii=False)
        
        return {'result':'0','message':"success",'data':retData}

    def listIaas(self):
        #IAAS_INFOテーブル一覧取得
        try:
            iaasInfo = self.conn.getTable("IAAS_INFO")
        except Exception as e:
            return {'result':'1','message':"IAAS_INFOテーブルが存在しません。管理者に連絡を行って下さい。"}
        sql = iaasInfo.select()
        iaasDataList = self.conn.select(sql)
        strIaasDataList = []
        #IaaSデータが未登録の場合
        if len(iaasDataList) == 0:
            return  {'result':'1','message':"IaaSデータが登録されていません。"}
        for iaasData in iaasDataList:
            #JSON形式に変換し、リストに追加
            strIaasDataList.append(json.dumps(iaasData, ensure_ascii=False))

        #リストを"&&"で結合
        retData = "&&".join(strIaasDataList)
        
        return {'result':'0', 'message':"suucess", 'data':retData}

    def showIaas(self, paramDict):
        #必須引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        iaasName = paramDict['iaasName']
        try:
            iaasInfo = self.conn.getTable("IAAS_INFO")
        except Exception as e:
            return {'result':'1','message':"IAAS_INFOテーブルが存在しません。管理者に連絡を行って下さい。"}
        iaasInfoData = self.conn.selectOne(iaasInfo.select(iaasInfo.c.IAAS_NAME == iaasName))
        if iaasInfoData is None:
            return {'result':'1','message':"指定されたIaaS名は存在しません。参照対象を確認して下さい。"}
        else:
            retData = json.dumps(iaasInfoData, ensure_ascii=False)
            return {'result':'0','message':"success",'data':retData}

    def addInstanceType(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        platformName = paramDict['platformName']
        instanceTypeName = paramDict['instanceTypeName']
        cpu = paramDict['cpu']
        memory = paramDict['memory']
        #getPlatformDataByNameの呼び出し
        try:
            platformData = CommonUtils.getPlatformDataByName(platformName)
            if platformData is None:
                return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。登録対象を確認して下さい。"}
            platformType = platformData["PLATFORM_TYPE"]
            if "vcloud" != platformType and "vmware" != platformType:
                return {'result':'1','message':"指定されたプラットフォーム名称にはインスタンスタイプが指定できません。登録対象を確認して下さい。"}
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したためインスタンスタイプの登録を中止します。管理者に連絡を行って下さい。"}
        #platformTypeがvcloudの場合のデータ登録処理
        if "vcloud" == platformType:
            return self.addVcloudInstanceType(platformData["PLATFORM_NO"], instanceTypeName, cpu, memory)
        if "vmware" == platformType:
            return self.addVmwareInstanceType(platformData["PLATFORM_NO"], instanceTypeName, cpu, memory)

    def addVmwareInstanceType(self, platformNo, instanceTypeName, cpu, memory):
        try:
            #PLATFORM_VMWARE_INSTANCE_TYPEテーブルへの登録処理
            tablePlVmInsType = self.conn.getTable("PLATFORM_VMWARE_INSTANCE_TYPE")
            sql = tablePlVmInsType.insert({"PLATFORM_NO":platformNo,
                    "INSTANCE_TYPE_NAME":instanceTypeName,
                    "CPU":cpu,
                    "MEMORY":memory
                    })
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'1','message':"VMWAREインスタンスタイプ:" + instanceTypeName + "の登録が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWARE_INSTANCE_TYPEテーブルへの登録に失敗したため処理を中止します。"}

    def addVcloudInstanceType(self, platformNo, instanceTypeName, cpu, memory):
        try:
            #PLATFORM_VCLOUD_INSTANCE_TYPEテーブルへの登録処理
            tablePlVcInsType = self.conn.getTable("PLATFORM_VCLOUD_INSTANCE_TYPE")
            sql = tablePlVcInsType.insert({"PLATFORM_NO":platformNo,
                    "INSTANCE_TYPE_NAME":instanceTypeName,
                    "CPU":cpu,
                    "MEMORY":memory
                    })
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'1','message':"VCLOUDインスタンスタイプ:" + instanceTypeName + "の登録が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_INSTANCE_TYPEテーブルへの登録に失敗したため処理を中止します。"}

    def updateInstanceType(self, paramDict):
        instanceTypeName = None
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        instanceTypeNo = paramDict['instanceTypeNo']
        platformName = paramDict['platformName']
        instanceTypeName = paramDict['instanceTypeName']
        cpu = paramDict['cpu']
        memory = paramDict['memory']
        #getPlatformDataByName呼び出し
        try:
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。登録対象を確認して下さい。"}
        platformType = plData["PLATFORM_TYPE"]
        if "vcloud" != platformType and "vmware" != platformType:
            return {'result':'1','message':"指定されたプラットフォーム名称にはインスタンスタイプが指定できません。更新対象を確認して下さい。"}
        #データ更新
        if "vcloud" == platformType:
            return self.updateVcloudInstanceType(instanceTypeNo, instanceTypeName, cpu, memory)
        if "vmware" == platformType:
            return self.updateVmwareInstanceType(instanceTypeNo, instanceTypeName, cpu, memory)

    def updateVmwareInstanceType(self, instanceTypeNo, instanceTypeName, cpu, memory):
        try:
            #インスタンスタイプ存在チェック
            tablePlVmInsType = self.conn.getTable("PLATFORM_VMWARE_INSTANCE_TYPE")
            plVmInsType = self.conn.selectOne(tablePlVmInsType.select(tablePlVmInsType.c.INSTANCE_TYPE_NO == instanceTypeNo))
            if plVmInsType is None:
                return {'result':'1','message':"指定されたインスタンスタイプNoは存在しません。更新対象を確認して下さい。"}
            
            #各引数が存在する場合
            if instanceTypeName is not None:
                plVmInsType["INSTANCE_TYPE_NAME"] = instanceTypeName
            if cpu is not None:
                plVmInsType["CPU"] = cpu
            if memory is not None:
                plVmInsType["MEMORY"] = memory
            sql = tablePlVmInsType.update(tablePlVmInsType.c.INSTANCE_TYPE_NO == instanceTypeNo, values = plVmInsType)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"VMwareインスタンスタイプNo:" + instanceTypeNo + "の更新が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWARE_INSTANCE_TYPEテーブルの更新に失敗したため処理を中止します。"}

    def updateVcloudInstanceType(self, instanceTypeNo, instanceTypeName, cpu, memory):
        try:
            #インスタンスタイプ存在チェック
            tablePlVcInsType = self.conn.getTable("PLATFORM_VCLOUD_INSTANCE_TYPE")
            plVcInsType = self.conn.selectOne(tablePlVcInsType.select(tablePlVcInsType.c.INSTANCE_TYPE_NO == instanceTypeNo))
            if plVcInsType is None:
                return {'result':'1','message':"指定されたインスタンスタイプNoは存在しません。更新対象を確認して下さい。"}
                
            #各引数が存在する場合
            if instanceTypeName is not None:
                plVcInsType["INSTANCE_TYPE_NAME"] = instanceTypeName
            if cpu is not None:
                plVcInsType["CPU"] = cpu
            if memory is not None:
                plVcInsType["MEMORY"] = memory
            sql = tablePlVcInsType.update(tablePlVcInsType.c.INSTANCE_TYPE_NO == instanceTypeNo, values = plVcInsType)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"vCloudインスタンスタイプNo:" + instanceTypeNo + "の更新が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_INSTANCE_TYPEテーブルデータの更新に失敗したため処理を中止します。"}

    def deleteInstanceType(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        instanceTypeNo = paramDict['instanceTypeNo']
        platformName = paramDict['platformName']
        try:
            #getPlatformDataByNameを呼び出す
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'resukt':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。登録対象を確認して下さい。"}
        if "vcloud" != plData["PLATFORM_TYPE"] and "vmware" != plData["PLATFORM_TYPE"]:
            return {'result':'1','message':"指定されたプラットフォーム名称にはインスタンスタイプが指定できません。更新対象を確認して下さい。"}
        #PLATFORM_TYPE毎のデータ削除
        if "vcloud" == plData["PLATFORM_TYPE"]:
            return self.deleteVcloudInstanceType(instanceTypeNo)
        if "vmware" == plData["PLATFORM_TYPE"]:
            return self.deleteVmwareInstanceType(instanceTypeNo)

    def deleteVmwareInstanceType(self, instanceTypeNo):
        #インスタンスタイプ存在チェック
        try:
            tablePlVmInsType = self.conn.getTable("PLATFORM_VMWARE_INSTANCE_TYPE")
        except Exception as e:
            return {'result':'1','message':"PLATFORM_VMWARE_INSTANCE_TYPEテーブルが存在しません。管理者に連絡を行って下さい。"}
        plVmInsType = self.conn.selectOne(tablePlVmInsType.select(tablePlVmInsType.c.INSTANCE_TYPE_NO == instanceTypeNo))
        if plVmInsType is None:
            return {'result':'1','message':"指定されたインスタンスタイプNoは存在しません。削除対象を確認して下さい。"}
        try:
            #PLATFORM_VMWARE_INSTANCE_TYPEテーブルのデータ削除
            sql = tablePlVmInsType.delete(tablePlVmInsType.c.INSTANCE_TYPE_NO == instanceTypeNo)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"VMwareインスタンスタイプNo:" + instanceTypeNo + "の削除が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VMWARE_INSTANCE_TYPEテーブルデータの削除に失敗したため処理を中止します。"}

    def deleteVcloudInstanceType(self, instanceTypeNo):
        #インスタンスタイプ存在チェック
        try:
            tablePlVcInsType = self.conn.getTable("PLATFORM_VCLOUD_INSTANCE_TYPE")
        except Exception as e:
            return {'result':'1','message':"PLATFORM_VCLOUD_INSTANCE_TYPEテーブルが存在しません。管理者に連絡を行って下さい。"}
        plVcInsType = self.conn.selectOne(tablePlVcInsType.select(tablePlVcInsType.c.INSTANCE_TYPE_NO == instanceTypeNo))
        if plVcInsType is None:
            return {'result':'1','message':"指定されたインスタンスタイプNoは存在しません。削除対象を確認して下さい。"}
        try:
            #PLATFORM_VCLOUD_INSTANCE_TYPEテーブルのデータ削除
            sql = tablePlVcInsType.delete(tablePlVcInsType.c.INSTANCE_TYPE_NO == instanceTypeNo)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"vCloudインスタンスタイプNo:" + instanceTypeNo + "の削除が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_INSTANCE_TYPEテーブルデータの削除に失敗したため処理を中止します。"}

    def listInstanceType(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        platformName = None
        insTypeDataList = None
        strInsTypeDataList = []
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        if "platformName" in paramDict:
            platformName = paramDict['platformName']
        
        #プラットフォーム名が指定された場合
        if platformName != None:
            #プラットフォームデータ取得
            try:
                plData = CommonUtils.getPlatformDataByName(platformName)
            except Exception as e:
                return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #プラットフォームデータが存在しない場合はエラー
            if plData is None:
                return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。参照対象を確認して下さい。"}
            #IaaSがVMwareの場合
            elif "vmware" == plData["PLATFORM_TYPE"]:
                sql = "SELECT V.INSTANCE_TYPE_NO,V.INSTANCE_TYPE_NAME,V.CPU,V.MEMORY,P.PLATFORM_NAME,P.PLATFORM_TYPE FROM PLATFORM AS P JOIN PLATFORM_VMWARE_INSTANCE_TYPE AS V ON P.PLATFORM_NO = V.PLATFORM_NO WHERE P.PLATFORM_NO = " + str(plData["PLATFORM_NO"]) + " ORDER BY V.INSTANCE_TYPE_NO"
                insTypeDataList = self.conn.select(sql)
            #IaaSがvCloudの場合
            elif "vcloud" == plData["PLATFORM_TYPE"]:
                sql = "SELECT V.INSTANCE_TYPE_NO,V.INSTANCE_TYPE_NAME,V.CPU,V.MEMORY,P.PLATFORM_NAME,P.PLATFORM_TYPE FROM PLATFORM AS P JOIN PLATFORM_VCLOUD_INSTANCE_TYPE AS V ON P.PLATFORM_NO = V.PLATFORM_NO WHERE P.PLATFORM_NO = " + str(plData["PLATFORM_NO"]) + " ORDER BY V.INSTANCE_TYPE_NO"
                insTypeDataList = self.conn.select(sql)
            #IaaSがvCloudとVMware以外の場合はエラー
            else:
                return {'result':'1','message':"指定されたプラットフォーム名称にはインスタンスタイプが存在しません。参照対象を確認して下さい。"}
        #プラットフォーム名が指定されなかった場合、全インスタンスタイプを取得
        else:
            sql = "SELECT V.INSTANCE_TYPE_NO,V.INSTANCE_TYPE_NAME,V.CPU,V.MEMORY,P.PLATFORM_NAME,P.PLATFORM_TYPE FROM PLATFORM AS P JOIN PLATFORM_VMWARE_INSTANCE_TYPE AS V ON P.PLATFORM_NO = V.PLATFORM_NO UNION ALL SELECT V.INSTANCE_TYPE_NO,V.INSTANCE_TYPE_NAME,V.CPU,V.MEMORY,P.PLATFORM_NAME,P.PLATFORM_TYPE FROM PLATFORM AS P JOIN PLATFORM_VCLOUD_INSTANCE_TYPE AS V ON P.PLATFORM_NO = V.PLATFORM_NO ORDER BY INSTANCE_TYPE_NO"
            insTypeDataList = self.conn.select(sql)
        
        for insTypeData in insTypeDataList:
            #JSON形式に変換し、リストに追加
            strInsTypeDataList.append(json.dumps(insTypeData, ensure_ascii=False))

        #リストを"&&"で結合
        retData = "&&".join(strInsTypeDataList)
        
        return {'result':'0', 'message':"suucess", 'data':retData}

    def addStorageType(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        platformName = paramDict['platformName']
        storageTypeName = paramDict['storageTypeName']
        plData = []
        try:
            #getPlatformDataByNameの呼び出し
            plData = CommonUtils.getPlatformDataByName(platformName)
            if plData is None:
                return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。登録対象を確認して下さい。"}
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したためストレージタイプの登録を中止します。管理者に連絡を行って下さい。"}
        if "vcloud" != plData["PLATFORM_TYPE"]:
            return {'result':'1','message':"指定されたプラットフォームにはストレージタイプの指定が出来ません。登録対象を確認して下さい。"}
        #addVcloudStorageTypeメソッドの呼び出し
        return self.addVcloudStorageType(plData["PLATFORM_NO"], storageTypeName)

    def addVcloudStorageType(self, platformNo, storageTypeName):
        try:
            #PLATFORM_VCLOUD_STORAGE_TYPE
            tablePlatformVcloudStorageType = self.conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
            sql = tablePlatformVcloudStorageType.insert({"PLATFORM_NO":platformNo,
                    "STORAGE_TYPE_NAME":storageTypeName
                    })
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"vCloudストレージタイプ:" + storageTypeName + "の登録が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_STORAGE_TYPEテーブルへの登録に失敗したため処理を中止します。"}

    def updateStorageType(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        storageTypeNo = paramDict['storageTypeNo']
        platformName = paramDict['platformName']
        storageTypeName = paramDict['storageTypeName']
        #getPlatformDataByName呼び出し
        try:
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。更新対象を確認して下さい。"}
        platformType = plData["PLATFORM_TYPE"]
        if "vcloud" != platformType:
            return {'result':'1','message':"指定されたプラットフォームにはストレージタイプが存在しません。更新対象を確認して下さい。"}
        #データ更新
        if "vcloud" == platformType:
            return self.updateVcloudStorageType(storageTypeNo, storageTypeName)

    def updateVcloudStorageType(self, storageTypeNo, storageTypeName):

        try:
            #ストレージタイプ存在チェック
            tablePlatformVcloudStorageType = self.conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
            plVcloudStorageType = self.conn.selectOne(tablePlatformVcloudStorageType.select(tablePlatformVcloudStorageType.c.STORAGE_TYPE_NO == storageTypeNo))
            if plVcloudStorageType is None:
                return {'result':'1','message':"指定されたストレージタイプNoは存在しません。更新対象を確認して下さい。"}
            plVcloudStorageType["STORAGE_TYPE_NAME"] = storageTypeName
            sql = tablePlatformVcloudStorageType.update(tablePlatformVcloudStorageType.c.STORAGE_TYPE_NO == storageTypeNo, values = plVcloudStorageType)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"vCloudストレージタイプNo:" + storageTypeNo + "の更新が完了しました。"}
        except Exception as e:
            return {'result':'1','message':"PLATFORM_VCLOUD_STORAGE_TYPEテーブルデータの更新に失敗したため処理を中止します。"}

    def deleteStorageType(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        storageTypeNo = paramDict['storageTypeNo']
        platformName = paramDict['platformName']
        try:
            #getPlatformDataByNameの呼び出し
            plData = CommonUtils.getPlatformDataByName(platformName)
        except Exception as e:
            return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        if plData is None:
            return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。更新対象を確認して下さい。"}
        if "vcloud" != plData["PLATFORM_TYPE"]:
            return {'result':'1','message':"指定されたプラットフォームにはストレージタイプが存在しません。更新対象を確認して下さい。"}
        return self.deleteVcloudStorageType(storageTypeNo)

    def deleteVcloudStorageType(self, storageTypeNo):

        #ストレージタイプチェック
        try:
            tableVcloudStorageType = self.conn.getTable("PLATFORM_VCLOUD_STORAGE_TYPE")
        except Exception as e:
            return {'result':'1','message':"PLATFORM_VCLOUD_STORAGE_TYPEテーブルが存在しません。管理者に連絡を行って下さい。"}
        plVcloudStorageType = self.conn.selectOne(tableVcloudStorageType.select(tableVcloudStorageType.c.STORAGE_TYPE_NO == storageTypeNo))
        if plVcloudStorageType is None:
            return {'result':'1','message':"指定されたストレージタイプは存在しません。更新対象を確認して下さい。"}
        try:
            #PLATFORM_VCLOUD_STORAGE_TYPEテーブルのデータ削除
            sql = tableVcloudStorageType.delete(tableVcloudStorageType.c.STORAGE_TYPE_NO == storageTypeNo)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"vCloudストレージタイプNo:" + storageTypeNo + "の削除が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"PLATFORM_VCLOUD_STORAGE_TYPEテーブルデータの削除に失敗したため処理を中止します。"}

    def listStorageType(self, paramDict):
        #引数チェック
        method = paramDict['method']
        del paramDict['method']
        platformName = None
        storageTypeDataList = None
        strStorageTypeDataList = []
        #checkArguments呼び出し
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        if "platformName" in paramDict:
            platformName = paramDict['platformName']
        
        #プラットフォーム名が指定された場合
        if platformName != None:
            #プラットフォームデータ取得
            try:
                plData = CommonUtils.getPlatformDataByName(platformName)
            except Exception as e:
                return {'result':'1','message':"プラットフォーム名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #プラットフォームデータが存在しない場合はエラー
            if plData is None:
                return {'result':'1','message':"指定されたプラットフォーム名称は存在しません。参照対象を確認して下さい。"}
            #IaaSがvCloudの場合
            elif "vcloud" == plData["PLATFORM_TYPE"]:
                sql = "SELECT V.STORAGE_TYPE_NO, V.STORAGE_TYPE_NAME, P.PLATFORM_NAME, P.PLATFORM_TYPE FROM PLATFORM_VCLOUD_STORAGE_TYPE AS V JOIN PLATFORM AS P ON V.PLATFORM_NO = P.PLATFORM_NO WHERE P.PLATFORM_NO = " + str(plData["PLATFORM_NO"]) + " ORDER BY V.STORAGE_TYPE_NO"
                storageTypeDataList = self.conn.select(sql)
            #IaaSがvCloudとVMware以外の場合はエラー
            else:
                return {'result':'1','message':"指定されたプラットフォーム名称にはストレージタイプが存在しません。参照対象を確認して下さい。"}
        #プラットフォーム名が指定されなかった場合、全インスタンスタイプを取得(現在はvCloudのみ)
        else:
            sql = "SELECT V.STORAGE_TYPE_NO, V.STORAGE_TYPE_NAME, P.PLATFORM_NAME, P.PLATFORM_TYPE FROM PLATFORM_VCLOUD_STORAGE_TYPE AS V JOIN PLATFORM AS P ON V.PLATFORM_NO = P.PLATFORM_NO ORDER BY V.STORAGE_TYPE_NO"
            storageTypeDataList = self.conn.select(sql)
        
        for storageTypeData in storageTypeDataList:
            #JSON形式に変換し、リストに追加
            strStorageTypeDataList.append(json.dumps(storageTypeData, ensure_ascii=False))

        #リストを"&&"で結合
        retData = "&&".join(strStorageTypeDataList)
        
        return {'result':'0', 'message':"suucess", 'data':retData}
