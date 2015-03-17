# -*- coding: utf-8 -*-
import subprocess
import sys
import re
import os
import common.CommonUtils as CommonUtils
import glob
import json
import shutil
from db.MysqlConnector import MysqlConnector

class ImageManager:

    conn = None

    def __init__(self):
        #DBコネクション取得
        self.conn = MysqlConnector()

    def addImage(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        iaasNameList = []
        retDict = None
        platformList = None
        platformNoList = []
        imageNoList = []
        
        try:
            #platformListが指定された場合
            if "platformList" in paramDict:
                #platformListをカンマ区切りで分ける
                platformList = paramDict['platformList'].split(',')
            #platformListが指定されなかった場合PLATFORMテーブルからSELECTABLEが1のPLATFORM_NAME一覧取得
            else:
                platformList = CommonUtils.getSelectablePlatformNameList()
            #AWSプラットフォームが指定された場合、専用のチェックメソッドを指定
            for platformName in platformList:
                iaasName = CommonUtils.getPlatformTypeByName(platformName)
                if iaasName is None:
                    return {'result':'1','message':"指定されたプラットフォーム名:" + platformName + "は存在しません。登録対象を確認して下さい。"}
                if "aws" == iaasName:
                    method = "addAwsImage"
        except Exception as e:
            return {'result':'1','message':"プラットフォーム情報の取得に失敗したためOSイメージの登録を中止します。管理者に連絡を行って下さい。"}

        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        #引数から変数へセット
        imageName = paramDict['imageName']
        imageNameDisp = paramDict['imageNameDisp']
        osName = paramDict['osName']
        osNameDisp = paramDict['osNameDisp']
        instanceTypeList = paramDict['instanceTypeList']
        imageId = paramDict['imageId']
        zabbixTemplate = paramDict['zabbixTemplate']
        serviceList = None
        serviceNoList = 0
        kernelId = None
        ramdiskId = None
        ebsImageFlg = 0
        icon = None
        if "serviceList" in paramDict:
            serviceList = paramDict['serviceList']
        if "kernelId" in paramDict:
            kernelId = paramDict['kernelId']
        if "ramdiskId" in paramDict:
            ramdiskId = paramDict['ramdiskId']
        if "ebsImageFlg" in paramDict:
            ebsImageFlg = paramDict['ebsImageFlg']
        if "icon" in paramDict:
            icon = paramDict['icon']

        #serviceListの値が存在する場合、名称をNoに変換
        if serviceList != None:
            serviceList = serviceList.split(',')
            serviceNoList = []
            for serviceName in serviceList:
                try:
                    result =  CommonUtils.getComponentTypeNoByName(serviceName)
                    if result is None:
                        return {'result':'1','message':"指定されたサービス名称:" + serviceName + "が存在しません。登録対象を確認して下さい。"}
                    else:
                        serviceNoList.append(result)
                except AttributeError as e:
                    return {'result':'1','message':"サービス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #リストをカンマで結合
            serviceNoList = ",".join(serviceNoList)

        #プラットフォーム存在チェック
        for platformName in platformList:
            try:
                plData = CommonUtils.getPlatformDataByName(platformName)
            except Exception as e:
                return {'result':'1','message':"プラットフォーム情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            
            platformNoList.append(str(plData["PLATFORM_NO"]))
            iaasNameList.append(plData["PLATFORM_TYPE"])

        #同一プラットフォーム・同一名のイメージデータ存在チェック
        for platformNo in platformNoList:
            try:
                checkImageData = CommonUtils.getImageDataByNameAndPlatformNo(imageName, platformNo)
                #指定されたプラットフォームに既に同名のイメージが存在する場合
                if checkImageData != None:
                    return {'result':'1','message':"指定されたプラットフォーム:" + str(platformNo) +"には既に同じ名称のイメージ:" + imageName + "が存在します。名称を指定し直して下さい。"}
            except Exception as e:
                return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        #アイコンファイル存在チェック
        if icon != None:
            if os.path.isfile(icon) == False:
                return {'result':'1','message':"指定されたアイコンファイルが見つからないため処理を中止します。登録対象を確認して下さい。"}
        
        #zabbixTemplateの存在チェックと、存在しない場合の追加処理
        #zabbixAPI認証コード発行
        try:
            #getConnectZabbixApi呼び出し
            auth = CommonUtils.getConnectZabbixApi()
        except Exception as e:
            return {'result':'1','message':"zabbixAPI認証コード取得に失敗したため処理を終了します。管理者に連絡を行って下さい。"}
        if auth == None:
            return {'result':'1','message':"zabbixAPI認証コードが発行されなかったため処理を終了します。"}
        
        #zabbixテンプレート存在チェック
        try:
            #getZabbixTemplate呼び出し
            zabbixTemplateData = CommonUtils.getZabbixTemplate(auth, zabbixTemplate)
        except Exception as e:
            return {'result':'1','message':"zabbixテンプレート情報の取得に失敗したため処理を終了します。管理者に連絡を行って下さい。"}

        #テンプレート情報が未登録の場合登録処理実行
        createTemp = True
        if zabbixTemplateData == False:
            #createZabbixTemplate呼び出し
            try:
                createTemp = CommonUtils.createZabbixTemplate(auth, zabbixTemplate)
            except Exception as e:
                return {'result':'1','message':"zabbixテンプレートの追加に失敗したため処理を終了します。管理者に連絡を行って下さい。"}
        if createTemp != True:
            return {'result':'1','message':createTemp}
        
        cnt = 0

        #IMAGEテーブルと各種PLATFORM_TYPE毎のIMAGE系テーブルへのデータ登録
        for platformNo in platformNoList:
            try:
                #IMAGEテーブル登録処理実行
                tableImage = self.conn.getTable("IMAGE")
                sql = tableImage.insert({"PLATFORM_NO":platformNo,
                        "IMAGE_NAME":imageName,
                        "IMAGE_NAME_DISP":imageNameDisp,
                        "OS":osName,
                        "OS_DISP":osNameDisp,
                        "SELECTABLE":"1",
                        "COMPONENT_TYPE_NOS":serviceNoList,
                        "ZABBIX_TEMPLATE":zabbixTemplate
                        })
                #return {'result':'1','message':str(sql)}
                self.conn.execute(sql)
                #ここでコミットしないと子テーブルのPLATFROM_NOが取得出来ない
                self.conn.commit()
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGEテーブルへの登録に失敗したため処理を中止します。イメージ名:" + imageName}

            imageData = CommonUtils.getImageDataByNameAndPlatformNo(imageName, platformNo)
            imageNo = imageData["IMAGE_NO"]
            imageNoList.append(str(imageNo))

            #IaaS毎のテーブル登録
            if "aws" == iaasNameList[cnt]:
                try:
                    #IMAGE_AWSテーブル登録処理実行
                    tableImageAws = self.conn.getTable("IMAGE_AWS")
                    sql = tableImageAws.insert({"IMAGE_NO":imageNo,
                            "IMAGE_ID":imageId,
                            "KERNEL_ID":kernelId,
                            "RAMDISK_ID":ramdiskId,
                            "INSTANCE_TYPES":instanceTypeList,
                            "EBS_IMAGE":ebsImageFlg
                            })
                    self.conn.execute(sql)
                    self.conn.commit()
                except Exception as e:
                    self.conn.rollback()
                    retDict = {'result':'1','message':"IMAGE_AWSテーブルへのデータ登録に失敗したため処理を中止します。"}
            
            elif "vmware" == iaasNameList[cnt]:
                try:
                    #IMAGE_VMWAREテーブル登録処理実行
                    tableImageVmware = self.conn.getTable("IMAGE_VMWARE")
                    sql = tableImageVmware.insert({"IMAGE_NO":imageNo,
                            "TEMPLATE_NAME":imageId,
                            "INSTANCE_TYPES":instanceTypeList
                            })
                    self.conn.execute(sql)
                    self.conn.commit()
                except Exception as e:
                    self.conn.rollback()
                    retDict = {'result':'1','message':"IMAGE_VMWAREテーブルへのデータ登録に失敗したため処理を中止します。"}
            
            elif "cloudstack" == iaasNameList[cnt]:
                try:
                    #IMAGE_CLOUDSTACKテーブル登録処理実行
                    tableImageCloudstack = self.conn.getTable("IMAGE_CLOUDSTACK")
                    sql = tableImageCloudstack.insert({"IMAGE_NO":imageNo,
                            "TEMPLATE_ID":imageId,
                            "INSTANCE_TYPES":instanceTypeList
                            })
                    self.conn.execute(sql)
                    self.conn.commit()
                except Exception as e:
                    self.conn.rollback()
                    retDict = {'result':'1','message':"IMAGE_CLOUDSTACKテーブルへのデータ登録に失敗したため処理を中止します。"}
            
            elif "vcloud" == iaasNameList[cnt]:
                try:
                    #IMAGE_VCLOUDテーブル登録処理実行
                    tableImageVcloud = self.conn.getTable("IMAGE_VCLOUD")
                    sql = tableImageVcloud.insert({"IMAGE_NO":imageNo,
                            "TEMPLATE_NAME":imageId,
                            "INSTANCE_TYPES":instanceTypeList
                            })
                    self.conn.execute(sql)
                    self.conn.commit()
                except Exception as e:
                    self.conn.rollback()
                    retDict = {'result':'1','message':"IMAGE_VCLOUDテーブルへのデータ登録に失敗したため処理を中止します。"}

            elif "openstack" == iaasNameList[cnt]:
                try:
                    #IMAGE_OPENSTACKテーブル登録処理実行
                    tableImageOpenstack = self.conn.getTable("IMAGE_OPENSTACK")
                    sql = tableImageOpenstack.insert({"IMAGE_NO":imageNo,
                            "IMAGE_ID":imageId,
                            "INSTANCE_TYPES":instanceTypeList
                            })
                    self.conn.execute(sql)
                    self.conn.commit()
                except Exception as e:
                    self.conn.rollback()
                    retDict = {'result':'1','message':"IMAGE_OPENSTACKテーブルへのデータ登録に失敗したため処理を中止します。"}
            
            elif "azure" == iaasNameList[cnt]:
                try:
                    #IMAGE_AZUREテーブル登録処理実行
                    tableImageAzure = self.conn.getTable("IMAGE_AZURE")
                    sql = tableImageAzure.insert({"IMAGE_NO":imageNo,
                            "IMAGE_NAME":imageId,
                            "INSTANCE_TYPES":instanceTypeList
                            })
                    self.conn.execute(sql)
                    self.conn.commit()
                except Exception as e:
                    self.conn.rollback()
                    retDict = {'result':'1','message':"IMAGE_AZUREテーブルへのデータ登録に失敗したため処理を中止します。"}
            
            elif "nifty" == iaasNameList[cnt]:
                try:
                    #IMAGE_NIFTYテーブル登録処理実行
                    tableImageNifty = self.conn.getTable("IMAGE_NIFTY")
                    sql = tableImageNifty.insert({"IMAGE_NO":imageNo,
                            "IMAGE_ID":imageId,
                            "INSTANCE_TYPES":instanceTypeList
                            })
                    self.conn.execute(sql)
                    self.conn.commit()
                except Exception as e:
                    self.conn.rollback()
                    retDict = {'result':'1','message':"IMAGE_NIFTYテーブルへのデータ登録に失敗したため処理を中止します。"}

            if retDict != None and retDict['result'] == "1":
                image = self.conn.getTable("IMAGE")
                #エラー終了時、登録したイメージデータを削除
                image.delete(image.c.IMAGE_NO == imageNo).execute()
            
            cnt = cnt + 1
            
        if icon != None:
            path, ext = os.path.splitext(icon)
            #iconの指定がある場合iconのパスに存在する画像を/opt/adc/app/auto-web/VAADIN/themes/classy/iconsに(引数.imageName)pngの形式にリネームしてコピーする。
            copyName = "/opt/adc/app/auto-web/VAADIN/themes/classy/icons/" + imageName + ext
            shutil.copyfile(icon, copyName)
            
        imageNoList = ",".join(imageNoList)
        retDict = {'result':'0','message':"OSイメージNo:" + imageNoList + "/" + imageName + "の登録が完了しました。"}

        return retDict

    def updateImage(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']

        #イメージNo存在チェック
        try:
            if "imageNo" in paramDict:
                imageNo = paramDict['imageNo']
                imageData = CommonUtils.getImageDataByNo(imageNo)
                if imageData is None:
                    return {'result':'1','message':"イメージNo:"+ imageNo + "が存在しません。更新対象を確認して下さい。"}
                else:
                    platformNo = imageData["PLATFORM_NO"]
                    iaasName = CommonUtils.getPlatformTypeByNo(platformNo)
                    if iaasName is None:
                        return {'result':'1','message':"指定されたプラットフォーム名:" + paramDict['platformName'] + "は存在しません。登録対象を確認して下さい。"}
                    elif "aws" == iaasName:
                        method = "updateAwsImage"
        except Exception as e:
            return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}

        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #引数から変数へセット
        imageNo = paramDict['imageNo']
        imageName = None
        imageNameDisp = None
        osName = None
        osNameDisp = None
        serviceList = None
        serviceNoList = 0
        instanceTypeList = None
        zabbixTemplate = None
        kernelId = None
        ramdiskId = None
        icon = None
        
        if "imageName" in paramDict:
            imageName = paramDict['imageName']
        if "imageNameDisp" in paramDict:
            imageNameDisp = paramDict['imageNameDisp']
        if "osName" in paramDict:
            osName = paramDict['osName']
        if "osNameDisp" in paramDict:
            osNameDisp = paramDict['osNameDisp']
        if "serviceList" in paramDict:
            serviceList = paramDict['serviceList']
        if "instanceTypeList" in paramDict:
            instanceTypeList = paramDict['instanceTypeList']
        if "zabbixTemplate" in paramDict:
            zabbixTemplate = paramDict['zabbixTemplate']
        if "kernelId" in paramDict:
            kernelId = paramDict['kernelId']
        if "ramdiskId" in paramDict:
            ramdiskId = paramDict['ramdiskId']
        if "icon" in paramDict:
            icon = paramDict['icon']
       
        #serviceListの値が存在する場合、名称をNoに変換
        if serviceList != None:
            serviceList = serviceList.split(',')
            serviceNoList = []
            for serviceName in serviceList:
                try:
                    result =  CommonUtils.getComponentTypeNoByName(serviceName)
                    if result is None:
                        return {'result':'1','message':"サービス名称:" + serviceName + "が存在しません。更新対象を確認して下さい。"}
                    else:
                        serviceNoList.append(result)
                except AttributeError as e:
                    return {'result':'1','message':"サービス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #リストをカンマで結合
            serviceNoList = ",".join(serviceNoList)
        
        #同一プラットフォーム・同一名のイメージデータ存在チェック
        try:
            platformNo = imageData["PLATFORM_NO"]
            plData = CommonUtils.getPlatformDataByNo(platformNo)
            checkImageData = CommonUtils.getImageDataByNameAndPlatformNo(imageName, platformNo)
             #指定されたイメージ名が既に存在する場合
            if checkImageData != None and imageNo != checkImageData["IMAGE_NO"]:
                return {'result':'1','message':"更新対象のOSイメージが使用するプラットフォーム:" + plData["PLATFORM_NAME"] +"には既に同じ名称のイメージ:" + imageName + "が存在します。名称を指定し直して下さい。"}
        except Exception as e:
            return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        #アイコンファイル存在チェック
        if icon != None:
            if os.path.isfile(icon) == False:
                return {'result':'1','message':"指定されたアイコンファイルが見つからないため処理を中止します。対象を確認して下さい。"}
        
        iaasName = plData["PLATFORM_TYPE"]
        
        #更新データ作成
        if imageName != None:
            imageData["IMAGE_NAME"] = imageName
        if imageNameDisp != None:
            imageData["IMAGE_NAME_DISP"] = imageNameDisp
        if osName != None:
            imageData["OS"] = osName
        if osNameDisp != None:
            imageData["OS_DISP"] = osName
        if serviceNoList != 0:
            imageData["COMPONENT_TYPE_NOS"] = serviceNoList
        if zabbixTemplate != None:
            imageData["ZABBIX_TEMPLATE"] = zabbixTemplate
        
        try:
            #IMAGEテーブルのデータ更新
            try:
                image = self.conn.getTable("IMAGE")
            except Exception as e:
                return {'result':'1','message':"IMAGEテーブルが存在しません。管理者に連絡を行って下さい。"}
            sql = image.update(image.c.IMAGE_NO == imageData["IMAGE_NO"], values = imageData)
            self.conn.execute(sql)
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"IMAGEテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}
        
        #IaaS毎のテーブル更新
        if "aws" == iaasName:
            #更新データ作成
            try:
                tableImageAws = self.conn.getTable("IMAGE_AWS")
            except Exception as e:
                return {'result':'1','message':"IMAGE_AWSテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageAwsData = self.conn.selectOne(tableImageAws.select(tableImageAws.c.IMAGE_NO==imageNo))
            if kernelId != None:
                imageAwsData["KERNEL_ID"] = kernelId
            if ramdiskId != None:
                imageAwsData["RAMDISK_ID"] = ramdiskId
            if instanceTypeList != None:
                imageAwsData["INSTANCE_TYPES"] = instanceTypeList
            try:
                #IMAGE_AWSテーブルのデータ更新
                sql = tableImageAws.update(tableImageAws.c.IMAGE_NO == imageAwsData["IMAGE_NO"], values = imageAwsData)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGE_AWSテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}
        
        elif "vmware" == iaasName:
            #更新データ作成
            try:
                tableImageVm = self.conn.getTable("IMAGE_VMWARE")
            except Exception as e:
                return {'result':'1','message':"IMAGE_VMWAREテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageVmData = self.conn.selectOne(tableImageVm.select(tableImageVm.c.IMAGE_NO==imageNo))
            if instanceTypeList != None:
                imageVmData["INSTANCE_TYPES"] = instanceTypeList
            try:
                #IMAGE_VMWAREテーブルのデータ更新
                sql = tableImageVm.update(tableImageVm.c.IMAGE_NO == imageVmData["IMAGE_NO"], values = imageVmData)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGE_VMWAREテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}
        
        elif "cloudstack" == iaasName:
            #更新データ作成
            try:
                tableImageCs = self.conn.getTable("IMAGE_CLOUDSTACK")
            except Exception as e:
                return {'result':'1','message':"IMAGE_CLOUDSTACKテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageCsData = self.conn.selectOne(tableImageCs.select(tableImageCs.c.IMAGE_NO==imageNo))
            if instanceTypeList != None:
                imageCsData["INSTANCE_TYPES"] = instanceTypeList
            try:
                #IMAGE_CLOUDSTACKテーブルのデータ更新
                sql = tableImageCs.update(tableImageCs.c.IMAGE_NO == imageCsData["IMAGE_NO"], values = imageCsData)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGE_CLOUDSTACKテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}
        
        elif "vcloud" == iaasName:
            #更新データ作成
            try:
                tableImageVc = self.conn.getTable("IMAGE_VCLOUD")
            except Exception as e:
                return {'result':'1','message':"IMAGE_VCLOUDテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageVcData = self.conn.selectOne(tableImageVc.select(tableImageVc.c.IMAGE_NO==imageNo))
            if instanceTypeList != None:
                imageVcData["INSTANCE_TYPES"] = instanceTypeList
            try:
                #IMAGE_VCLOUDテーブルのデータ更新
                sql = tableImageVc.update(tableImageVc.c.IMAGE_NO == imageVcData["IMAGE_NO"], values = imageVcData)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGE_VCLOUDテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}
        
        elif "openstack" == iaasName:
            #更新データ作成
            try:
                tableImageOs = self.conn.getTable("IMAGE_OPENSTACK")
            except Exception as e:
                return {'result':'1','message':"IMAGE_OPENSTACKテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageOsData = self.conn.selectOne(tableImageOs.select(tableImageOs.c.IMAGE_NO==imageNo))
            if instanceTypeList != None:
                imageOsData["INSTANCE_TYPES"] = instanceTypeList
            try:
                #IMAGE_OPENSTACKテーブルのデータ更新
                sql = tableImageOs.update(tableImageOs.c.IMAGE_NO == imageOsData["IMAGE_NO"], values = imageOsData)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGE_OPENSTACKテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}
        
        elif "azure" == iaasName:
            #更新データ作成
            try:
                tableImageAzure = self.conn.getTable("IMAGE_AZURE")
            except Exception as e:
                return {'result':'1','message':"IMAGE_AZUREテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageAzureData = self.conn.selectOne(tableImageAzure.select(tableImageAzure.c.IMAGE_NO==imageNo))
            if instanceTypeList != None:
                imageAzureData["INSTANCE_TYPES"] = instanceTypeList
            try:
                #IMAGE_AZUREテーブルのデータ更新
                sql = tableImageAzure.update(tableImageAzure.c.IMAGE_NO == imageAzureData["IMAGE_NO"], values = imageAzureData)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGE_AZUREテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}
        
        elif "nifty" == iaasName:
            #更新データ作成
            try:
                tableImageNifty = self.conn.getTable("IMAGE_NIFTY")
            except Exception as e:
                return {'result':'1','message':"IMAGE_NIFTYテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageNiftyData = self.conn.selectOne(tableImageNifty.select(tableImageNifty.c.IMAGE_NO==imageNo))
            if instanceTypeList != None:
                imageNiftyData["INSTANCE_TYPES"] = instanceTypeList
            try:
                #IMAGE_NIFTYテーブルのデータ更新
                sql = tableImageNifty.update(tableImageNifty.c.IMAGE_NO == imageNiftyData["IMAGE_NO"], values = imageNiftyData)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGE_NIFTYテーブルの更新に失敗したため処理を中止します。イメージNo:" + imageNo}

        if icon != None:
            path, ext = os.path.splitext(icon)
            #iconの指定がある場合iconのパスに存在する画像を/opt/adc/app/auto-web/VAADIN/themes/classy/iconsに(引数.imageName)pngの形式にリネームしてコピーする。
            copyName = "/opt/adc/app/auto-web/VAADIN/themes/classy/icons/" + imageName + ext
            shutil.copyfile(icon, copyName)

        self.conn.commit()
        return {'result':'0','message':"OSイメージNo:" + imageNo + "/" + imageName + "の更新が完了しました。"}

    def deleteImage(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数チェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数定義
        platformList = None
        imageName = paramDict['imageName']
        if "platformList" in paramDict:
            platformList = paramDict['platformList']
        iconDeleteFlg = False
        imageNoList = []
        imageDataList = []
        
        #イメージデータ存在チェック
        try:
            imageData = CommonUtils.getImageDataByName(imageName)
            if imageData is None:
                return {'result':'1','message':"イメージ:"+ imageName + "が存在しません。削除対象を確認して下さい。"}
        except Exception as e:
            return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        #削除対象imageNo一覧取得
        #platformListが指定されなかった場合
        if platformList is None:
            for image in imageData:
                imageNoList.append(image["IMAGE_NO"])
        else:
            platformList = platformList.split(",")
            for platformName in platformList:
                try:
                    platformNo = CommonUtils.getPlatformDataByName(platformName)["PLATFORM_NO"]
                except Exception as e:
                    return {'result':'1','message':"プラットフォーム情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
                try:
                    imageData = CommonUtils.getImageDataByNameAndPlatformNo(imageName, platformNo)
                    if imageData != None:
                        imageNoList.append(imageData["IMAGE_NO"])
                except Exception as e:
                    return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}

        for imageNo in imageNoList:
            #削除対象のOSイメージを使用しているインスタンス存在チェック
            try:
                table = self.conn.getTable("INSTANCE")
            except Exception as e:
                return {'result':'1','message':"INSTANCEテーブルが存在しません。管理者に連絡を行って下さい。"}
            instanceDatas = self.conn.select(table.select(table.c.IMAGE_NO==imageNo))
            if len(instanceDatas) > 0:
                return {'result':'1','message':"OSイメージNo:" + str(imageNo) + "は現在作成済みのインスタンスで使用されているため削除できません。処理を中止します。"}

            #削除対象のOSイメージを使用しているmyCloud用インスタンステンプレートチェック
            try:
                table = self.conn.getTable("TEMPLATE_INSTANCE")
            except Exception as e:
                return {'result':'1','message':"TEMPLATE_INSTANCEテーブルが存在しません。管理者に連絡を行って下さい。"}
            tmpInstDatas = self.conn.select(table.select(table.c.IMAGE_NO==imageNo))
            if len(tmpInstDatas) > 0:
                return {'result':'1','message':"OSイメージNo:" + str(imageNo) + "は現在インスタンステンプレートで使用されているため削除できません。処理を中止します。"}

        #imageNameの同名存在チェック
        try:
            table = self.conn.getTable("IMAGE")
        except Exception as e:
            return {'result':'1','message':"IMAGEテーブルが存在しません。管理者に連絡を行って下さい。"}
        checkImageData = self.conn.select(table.select(table.c.IMAGE_NAME==imageName))

        if len(checkImageData) == len(imageNoList):
            iconDeleteFlg = True
        
        for imageNo in imageNoList:
            #プラットフォームデータ取得
            try:
                platformNo = CommonUtils.getImageDataByNo(imageNo)["PLATFORM_NO"]
            except Exception as e:
                return {'result':'1','message':"イメージ情報の取得に失敗したため処理を終了します。"}
            try:
                plData = CommonUtils.getPlatformDataByNo(platformNo)
            except Exception as e:
                return {'result':'1','message':"プラットフォーム情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            
            iaasName = plData["PLATFORM_TYPE"]
            #IaaS毎のイメージテーブルのデータ削除
            if "aws" == iaasName:
                try:
                    tableImageAws = self.conn.getTable("IMAGE_AWS")
                    sql = tableImageAws.delete(tableImageAws.c.IMAGE_NO == imageNo)
                    self.conn.execute(sql)
                except Exception as e:
                    self.conn.rollback()
                    return {'result':'1','message':"IMAGE_AWSテーブルのデータ削除に失敗したため処理を中止します。"}

            elif "vmware" == iaasName:
                try:
                    tableImageVm = self.conn.getTable("IMAGE_VMWARE")
                    sql = tableImageVm.delete(tableImageVm.c.IMAGE_NO == imageNo)
                    self.conn.execute(sql)
                except Exception as e:
                    self.conn.rollback()
                    return {'result':'1','message':"IMAGE_VMWAREテーブルのデータ削除に失敗したため処理を中止します。"}
                    
            elif "cloudstack" == iaasName:
                try:
                    tableImageCs = self.conn.getTable("IMAGE_CLOUDSTACK")
                    sql = tableImageCs.delete(tableImageCs.c.IMAGE_NO == imageNo)
                    self.conn.execute(sql)
                except Exception as e:
                    self.conn.rollback()
                    return {'result':'1','message':"IMAGE_CLOUDSTACKテーブルのデータ削除に失敗したため処理を中止します。"}
                    
            elif "vcloud" == iaasName:
                try:
                    tableImageVc = self.conn.getTable("IMAGE_VCLOUD")
                    sql = tableImageVc.delete(tableImageVc.c.IMAGE_NO == imageNo)
                    self.conn.execute(sql)
                except Exception as e:
                    self.conn.rollback()
                    return {'result':'1','message':"IMAGE_VCLOUDテーブルのデータ削除に失敗したため処理を中止します。"}

            elif "openstack" == iaasName:
                try:
                    tableImageOs = self.conn.getTable("IMAGE_OPENSTACK")
                    sql = tableImageOs.delete(tableImageOs.c.IMAGE_NO == imageNo)
                    self.conn.execute(sql)
                except Exception as e:
                    self.conn.rollback()
                    return {'result':'1','message':"IMAGE_OPENSTACKテーブルのデータ削除に失敗したため処理を中止します。"}

            elif "azure" == iaasName:
                try:
                    tableImageAz = self.conn.getTable("IMAGE_AZURE")
                    sql = tableImageAz.delete(tableImageAz.c.IMAGE_NO == imageNo)
                    self.conn.execute(sql)
                except Exception as e:
                    self.conn.rollback()
                    return {'result':'1','message':"IMAGE_AZUREテーブルのデータ削除に失敗したため処理を中止します。"}

            elif "nifty" == iaasName:
                try:
                    tableImageNif = self.conn.getTable("IMAGE_NIFTY")
                    sql = tableImageNif.delete(tableImageNif.c.IMAGE_NO == imageNo)
                    self.conn.execute(sql)
                except Exception as e:
                    self.conn.rollback()
                    return {'result':'1','message':"IMAGE_NIFTYテーブルのデータ削除に失敗したため処理を中止します。"}

            #IMAGEテーブルのデータ削除
            try:
                tableImage = self.conn.getTable("IMAGE")
                sql = tableImage.delete(tableImage.c.IMAGE_NO == imageNo)
                self.conn.execute(sql)
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"IMAGEテーブルのデータ削除に失敗したため処理を中止します。"}
        
        self.conn.commit()
        
        imageNos = ",".join(str(imageNo) for imageNo in imageNoList)
        
        if iconDeleteFlg:
            #アイコン画像削除処理
            filePath = glob.glob('/opt/adc/app/auto-web/VAADIN/themes/classy/icons/' + imageName + '.*')
            if len(filePath) > 0:
                if os.path.isfile(filePath[0]):
                    os.remove(filePath[0])

        return {'result':'0','message':"OSイメージNo:" + imageNos + "の削除が完了しました。"}

    def enableImage(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数チェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数定義
        imageNo = paramDict['imageNo']

        #イメージデータ存在チェック
        try:
            imageData = CommonUtils.getImageDataByNo(imageNo)
            if imageData is None:
                return {'result':'1','message':"イメージNo:"+ imageNo + "が存在しません。有効化対象を確認して下さい。"}
        except Exception as e:
            return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        if "1" == str(imageData["SELECTABLE"]):
            return {'result':'1','message':"OSイメージNo:" + imageNo + "/" + str(imageData["IMAGE_NAME"]) + "は既に有効になっています。"}
        
        #更新データ作成
        imageData["SELECTABLE"] = 1
        
        try:
            #IMAGEテーブルのデータ更新
            image = self.conn.getTable("IMAGE")
            sql = image.update(image.c.IMAGE_NO == imageData["IMAGE_NO"], values = imageData)
            self.conn.execute(sql)
            self.conn.commit()
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"IMAGEテーブルの更新に失敗したため処理を中止します。"}
        
        return {'result':'0','message':"OSイメージNo:" + imageNo + "/" + str(imageData["IMAGE_NAME"]) + "の有効化が完了しました。"}

    def disableImage(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数チェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数定義
        imageNo = paramDict['imageNo']

        #イメージデータ存在チェック
        try:
            imageData = CommonUtils.getImageDataByNo(imageNo)
            if imageData is None:
                return {'result':'1','message':"イメージNo:"+ imageNo + "が存在しません。無効化対象を確認して下さい。"}
        except Exception as e:
            return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        if "0" == str(imageData["SELECTABLE"]):
            return {'result':'1','message':"OSイメージNo:" + imageNo + "/" + str(imageData["IMAGE_NAME"]) + "は既に無効になっています。"}
        
        #更新データ作成
        imageData["SELECTABLE"] = 0
        
        try:
            #IMAGEテーブルのデータ更新
            image = self.conn.getTable("IMAGE")
            sql = image.update(image.c.IMAGE_NO == imageData["IMAGE_NO"], values = imageData)
            self.conn.execute(sql)
            self.conn.commit()
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"IMAGEテーブルの更新に失敗したため処理を中止します。"}
        
        return {'result':'0','message':"OSイメージNo:" + imageNo + "/" + str(imageData["IMAGE_NAME"]) + "の無効化が完了しました。"}
        
    def listImage(self):
        #データの取得
        try:
            table = self.conn.getTable("IMAGE")
        except Exception as e:
            return {'result':'1','message':"IMAGEテーブルが存在しません。管理者に連絡を行って下さい。"}
        imageDataList = self.conn.select(table.select())
        strImageDataList = []
        status = None
        
        if len(imageDataList) == 0:
            return  {'result':'1','message':"OSイメージが登録されていません。"}
        
        for imageData in imageDataList:
            #ステータスの取得
            try:
                status = CommonUtils.getSelectableStatus(imageData["SELECTABLE"])
            except Exception as e:
                return {'result':'1','message':"ステータス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            
            #SELECTABLEの値をenable/disableに変換
            imageData["SELECTABLE"] = status
            #JSON形式に変換し、リストに追加
            strImageDataList.append(json.dumps(imageData, ensure_ascii=False))

        #リストを"&&"で結合
        retData = "&&".join(strImageDataList)
        
        return {'result':'0', 'message':"suucess", 'data':retData}

    def showImage(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        strServiceName = None
        #引数チェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数定義
        imageNo = paramDict['imageNo']
        imageIaasData = None
        
        #イメージデータ存在チェック
        try:
            imageData = CommonUtils.getImageDataByNo(imageNo)
            if imageData is None:
                return {'result':'1','message':"イメージNo:"+ imageNo + "が存在しません。対象を確認して下さい。"}
        except Exception as e:
            return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        strServiceNo = imageData["COMPONENT_TYPE_NOS"]
        #serviceListの値が存在する場合、Noを名称に変換
        if strServiceNo != None:
            serviceList = strServiceNo.split(',')
            serviceNameList = []
            for serviceNo in serviceList:
                try:
                    result =  CommonUtils.getComponentTypeNameByNo(serviceNo)
                    if result is None:
                        return {'result':'1','message':"サービス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
                    else:
                        serviceNameList.append(result)
                except AttributeError as e:
                    return {'result':'1','message':"サービス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #リストをカンマで結合
            strServiceName = ",".join(serviceNameList)
            
        imageData.update({"SERVICE_NAME_LIST":strServiceName})

        #ステータスの取得
        try:
            status = CommonUtils.getSelectableStatus(imageData["SELECTABLE"])
        except Exception as e:
            return {'result':'1','message':"ステータス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            
        #SELECTABLEの値をenable/disableに変換
        imageData["SELECTABLE"] = status
        
        #プラットフォーム存在チェック
        try:
            plData = CommonUtils.getPlatformDataByNo(imageData["PLATFORM_NO"])
            #指定されたプラットフォームが存在しない場合
            if plData is None:
                return {'result':'1','message':"プラットフォーム名:" + platformName + "が存在しません。管理者に連絡を行って下さい。"}
        except Exception as e:
            return {'result':'1','message':"プラットフォーム情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
        
        platformName = plData["PLATFORM_NAME"]
        iaasName = plData["PLATFORM_TYPE"]
        
        if "aws" == iaasName:
            try:
                tableImageAws = self.conn.getTable("IMAGE_AWS")
            except Exception as e:
                return {'result':'1','message':"IMAGE_AWSテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageIaasData = self.conn.selectOne(tableImageAws.select(tableImageAws.c.IMAGE_NO==imageNo))
        elif "vmware" == iaasName:
            try:
                tableImageVm = self.conn.getTable("IMAGE_VMWARE")
            except Exception as e:
                return {'result':'1','message':"IMAGE_VMWAREテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageIaasData = self.conn.selectOne(tableImageVm.select(tableImageVm.c.IMAGE_NO==imageNo))
            imageIaasData.update({"IMAGE_ID":imageIaasData["TEMPLATE_NAME"]})
        elif "cloudstack" == iaasName:
            try:
                tableImageCs = self.conn.getTable("IMAGE_CLOUDSTACK")
            except Exception as e:  
                return {'result':'1','message':"IMAGE_CLOUDSTACKテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageIaasData = self.conn.selectOne(tableImageCs.select(tableImageCs.c.IMAGE_NO==imageNo))
            imageIaasData.update({"IMAGE_ID":imageIaasData["TEMPLATE_ID"]})
        elif "vcloud" == iaasName:
            try:
                tableImageVc = self.conn.getTable("IMAGE_VCLOUD")
            except Exception as e:
                return {'result':'1','message':"IMAGE_VCLOUDテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageIaasData = self.conn.selectOne(tableImageVc.select(tableImageVc.c.IMAGE_NO==imageNo))
            imageIaasData.update({"IMAGE_ID":imageIaasData["TEMPLATE_NAME"]})
        elif "openstack" == iaasName:
            try:
                tableImageOs = self.conn.getTable("IMAGE_OPENSTACK")
            except Exception as e:  
                return {'result':'1','message':"IMAGE_OPENSTACKテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageIaasData = self.conn.selectOne(tableImageOs.select(tableImageOs.c.IMAGE_NO==imageNo))
        elif "azure" == iaasName:
            try:
                tableImageAz = self.conn.getTable("IMAGE_AZURE")
            except Exception as e:
                return {'result':'1','message':"IMAGE_AZUREテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageIaasData = self.conn.selectOne(tableImageAz.select(tableImageAz.c.IMAGE_NO==imageNo))
            imageIaasData.update({"IMAGE_ID":imageIaasData["IMAGE_NAME"]})
        elif "nifty" == iaasName:
            try:
                tableImageNif = self.conn.getTable("IMAGE_NIFTY")
            except Exception as e:
                return {'result':'1','message':"IMAGE_NIFTYテーブルが存在しません。管理者に連絡を行って下さい。"}
            imageIaasData = self.conn.selectOne(tableImageNif.select(tableImageNif.c.IMAGE_NO==imageNo))
            
        imageData.update(imageIaasData)
        imageData.update({u"PLATFORM_NAME":platformName})
        retData = json.dumps(imageData, ensure_ascii=False)
        
        return {'result':'0', 'message':"suucess", 'data':retData}
        