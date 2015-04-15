# -*- coding: utf-8 -*-
import subprocess
import sys
import re
import os
import json
import common.CommonUtils as CommonUtils
from db.MysqlConnector import MysqlConnector

class ServiceManager:

    conn = None
    
    def __init__(self):
        #DBコネクション取得
        self.conn = MysqlConnector()

    def addService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']

        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}

        #変数設定
        serviceName = paramDict['serviceName']
        serviceNameDisp = paramDict['serviceNameDisp']
        layer = paramDict['layer']
        layerNameDisp = paramDict['layerNameDisp']
        runOrder = paramDict['runOrder']
        zabbixTemplate = paramDict['zabbixTemplate']
        addressUrl = None
        imageNoList = None
        if "addressUrl" in paramDict:
            addressUrl = paramDict['addressUrl']
        if "imageNoList" in paramDict:
            imageNoList = paramDict['imageNoList']

        #getComponentTypeNoByName呼び出し
        try:
            compNo = CommonUtils.getComponentTypeNoByName(serviceName)
        except Exception as e:
            return {'result':'1','message':"サービス情報の取得に失敗したため登録処理を中止します。管理者に連絡を行って下さい。"}
        if compNo != None:
            return {'result':'1','message':"指定されたサービス名称は既に使用されています。他の名称を設定して下さい。"}

        #imageNoListが指定された場合imageNoListの存在チェック
        if imageNoList is not None:
            imageNoList = imageNoList.split(",")
            for imageNo in imageNoList:
                try:
                    imageData = CommonUtils.getImageDataByNo(imageNo)
                except Exception as e:
                    return {'result':'1','message':"イメージ情報の取得に失敗したため登録処理を中止します。管理者に連絡を行って下さい。"}
                
                if imageData is None:
                    return {'result':'1','message':"指定されたイメージNo:" + imageNo + "は存在しません。イメージ情報を確認して下さい。"}
        
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
        
        # サービスを登録
        try:
            componetType = self.conn.getTable("COMPONENT_TYPE")
            sql = componetType.insert({"COMPONENT_TYPE_NO":None,
                    "COMPONENT_TYPE_NAME":serviceName,
                    "COMPONENT_TYPE_NAME_DISP":serviceNameDisp,
                    "LAYER":layer,
                    "LAYER_DISP":layerNameDisp,
                    "RUN_ORDER":runOrder,
                    "SELECTABLE":1,
                    "ZABBIX_TEMPLATE":zabbixTemplate,
                    "ADDRESS_URL":addressUrl})
            self.conn.execute(sql)
            self.conn.commit()
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"COMPONENT_TYPEテーブルへの登録に失敗したため処理を中止します。"}
        
        #imageNoListが指定されなかった場合有効状態の全てのイメージをリストに追加
        if imageNoList is None:
            try:
                imageNoList = CommonUtils.getSelectableImageNoList()
            except Exception as e:
                return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。"}
        
        #imageNoListで指定されたイメージに対してサービス情報を追加
        for imageNo in imageNoList:
            #引数用JSONデータ作成
            json_data = '{"method":"validateService","imageNo":"' + imageNo + '","serviceList":"' + serviceName + '"}'
            jsondic = json.loads(json_data)
            #validateService呼び出し
            try:
                result = self.validateService(jsondic)
                if "1" == result['result']:
                    return result
            except Exception as e:
                return {'result':'1','message':"イメージへのサービス情報追加処理呼び出しに失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            
        imageNoList = ",".join(imageNoList)
        return {'result':'0','message':"サービスモジュール:" + serviceName + "の登録が完了しました。イメージNo:" + imageNoList + "上で利用可能になりました。"}

    def updateService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数設定
        serviceName = paramDict['serviceName']
        serviceNameDisp = None
        layer = None
        layerNameDisp = None
        runOrder = None
        zabbixTemplate = None
        addressUrl = None
        if "serviceNameDisp" in paramDict:
            serviceNameDisp = paramDict['serviceNameDisp']
        if "layer" in paramDict:
            layer = paramDict['layer']
        if "layerNameDisp" in paramDict:
            layerNameDisp = paramDict['layerNameDisp']
        if "runOrder" in paramDict:
            runOrder = paramDict['runOrder']
        if "zabbixTemplate" in paramDict:
            zabbixTemplate = paramDict['zabbixTemplate']
        if "addressUrl" in paramDict:
            addressUrl = paramDict['addressUrl']
        
        #getComponentTypeNoByName呼び出し
        try:
            compNo = CommonUtils.getComponentTypeNoByName(serviceName)
        except Exception as e:
            return {'result':'1','message':"サービス情報の取得に失敗したため更新処理を中止します。管理者に連絡を行って下さい。"}
        if compNo == None:
            return {'result':'1','message':"指定されたサービス名称が存在しません。更新対象を確認して下さい。"}

        # サービスを更新
        try:
            componentType = self.conn.getTable("COMPONENT_TYPE")
            compTypeData = self.conn.selectOne(componentType.select(componentType.c.COMPONENT_TYPE_NAME == serviceName))
            if serviceNameDisp is not None:
                compTypeData['COMPONENT_TYPE_NAME_DISP'] = serviceNameDisp
            if layer is not None:
                compTypeData['LAYER'] = layer
            if layerNameDisp is not None:
                compTypeData['LAYER_DISP'] = layerNameDisp
            if runOrder is not None:
                compTypeData['RUN_ORDER'] = runOrder
            if zabbixTemplate is not None:
                compTypeData['ZABBIX_TEMPLATE'] = zabbixTemplate
            if addressUrl is not None:
                compTypeData['ADDRESS_URL'] = addressUrl
            sql = componentType.update(componentType.c.COMPONENT_TYPE_NAME == serviceName, values = compTypeData)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"サービスモジュール:" + serviceName + "の更新が完了しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"COMPONENT_TYPEテーブルへの更新に失敗したため処理を中止します。"}

    def deleteService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数設定
        imageNoList = []
        imageNoListAll = []
        serviceDelFlg = False
        serviceName = paramDict['serviceName']
        if "imageNoList" in paramDict:
            imageNoList = paramDict['imageNoList'].split(",")
        componentTypeNo = None
        componentNoList = []
        instanceNoList = []
        instanceImageNoList = []

        #getComponentTypeNoByNameの呼び出し
        try:
            componentTypeNo = CommonUtils.getComponentTypeNoByName(serviceName)
        except Exception as e:
            return {'result':'1','message':"サービス情報の取得に失敗したため削除処理を中止します。管理者に連絡を行って下さい。"}
        if componentTypeNo == None:
            return {'result':'1','message':"指定されたサービス名称が存在しません。削除対象を確認して下さい。"}

        #imageNoList存在チェック
        if len(imageNoList) != 0:
            for imageNo in imageNoList[:]:
                try:
                    imageData = CommonUtils.getImageDataByNo(imageNo)
                except Exception as e:
                    return {'result':'1','message':"イメージ情報の取得に失敗したため削除処理を中止します。管理者に連絡を行って下さい。"}
                if imageData is None:
                    return {'result':'1','message':"イメージNo:" + imageNo + "は存在しません。削除対象を確認して下さい。"}
                instanceTypeNos = imageData["COMPONENT_TYPE_NOS"].split(",")
                if componentTypeNo not in instanceTypeNos:
                    imageNoList.remove(imageNo)

        #サービス登録済みのイメージ一覧所得
        try:
            tableImage = self.conn.getTable("IMAGE")
        except Exception as e:
            return {'result':'1','message':"IMAGEテーブルが存在しません。管理者に連絡を行って下さい。"}
        #IMAGEテーブル一覧取得
        imageData = self.conn.select(tableImage.select())
        #指定したサービスが登録されているimageNo一覧を取得
        for image in imageData:
            serviceNos = image['COMPONENT_TYPE_NOS'].split(",")
            for service in serviceNos:
                if service == componentTypeNo:
                    imageNoListAll.append(image['IMAGE_NO'])
        if len(imageNoList) == 0 or len(imageNoList) == len(imageNoListAll):
            imageNoList = imageNoListAll
            serviceDelFlg = True

        #削除対象のサービス定義を使用しているインスタンスチェック
        #COMPONENTテーブル情報取得
        try:
            tableComponent = self.conn.getTable("COMPONENT")
            componentData = self.conn.select(tableComponent.select(tableComponent.c.COMPONENT_TYPE_NO == componentTypeNo))
        except Exception as e:
            return {'result':'1','message':"COMPONENTテーブル情報取得に失敗しました。処理を中止します。"}
        #現在作成中のサービスが存在する場合
        if len(componentData) > 0:
            return {'result':'1','message':"サービスモジュール:" + serviceName + "は現在使用されているため処理を中止します。"}
        
        if serviceDelFlg:
            #削除対象のサービスを使用しているmyCloud用サービステンプレートチェック
            try:
                templateComponent = self.conn.getTable("TEMPLATE_COMPONENT")
            except Exception as e:
                return {'result':'1','message':"TEMPLATE_COMPONENTテーブルが存在しません。管理者に連絡を行って下さい。"}
            tempCompData = self.conn.selectOne(templateComponent.select(templateComponent.c.COMPONENT_TYPE_NO == componentTypeNo))
            if tempCompData != None:
                return {'result':'1','message':"サービスモジュール:" + serviceName + "は現在サービステンプレートで使用されているため削除できません。"}

        #各イメージからサービス情報を削除
        for imageNo in imageNoList:
            json_data = '{"method":"revokeService","imageNo":"' + str(imageNo) + '","serviceList":"' + serviceName + '"}'
            jsondic = json.loads(json_data)
            try:
                ret = self.revokeService(jsondic)
            except Exception as e:
                return {'result':'1','message':"イメージへのサービス情報削除処理呼び出しに失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            if "1" == ret['result']:
                return ret

        if serviceDelFlg:
            #サービスを削除
            try:
                componentType = self.conn.getTable("COMPONENT_TYPE")
                sql = componentType.delete(componentType.c.COMPONENT_TYPE_NAME == serviceName)
                self.conn.execute(sql)
                self.conn.commit()
                return {'result':'0','message':"サービスモジュール:" + serviceName + "の削除が完了しました。"}
            except Exception as e:
                self.conn.rollback()
                return {'result':'1','message':"COMPONENT_TYPEテーブルデータの削除に失敗したため処理を中止します。"}
        imageNoList = ",".join(imageNoList)
        
        return {'result':'0','message':"イメージNo:" + imageNoList + "からサービス" + serviceName + "の削除が完了しました。"}

    def enableService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}

        #変数設定
        serviceName = paramDict['serviceName']
        
        #getComponentTypeDataByName呼び出し
        try:
            serviceData = CommonUtils.getComponentTypeDataByName(serviceName)
        except Exception as e:
            return {'result':'1','message':"サービス情報の取得に失敗したため有効化処理を中止します。管理者に連絡を行って下さい。"}
        if serviceData == None:
            return {'result':'1','message':"指定されたサービス名称が存在しません。有効化対象を確認して下さい。"}
        if serviceData['SELECTABLE'] == 1:
            return {'result':'1','message':"指定されたサービスは既に有効状態です。処理を中止します。"}

        #サービスの有効化
        try:
            componentType = self.conn.getTable("COMPONENT_TYPE")
            compTypeData = self.conn.selectOne(componentType.select(componentType.c.COMPONENT_TYPE_NAME == serviceName))
            compTypeData['SELECTABLE'] = 1
            sql = componentType.update(componentType.c.COMPONENT_TYPE_NAME == serviceName, values = compTypeData)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"サービス名：" + serviceName + "を有効化しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"COMPONENT_TYPEテーブルの有効化に失敗したため処理を中止します。サービス名：" + serviceName}

    def disableService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}

        #変数設定
        serviceName = paramDict['serviceName']
        
        #getComponentTypeDataByName呼び出し
        try:
            serviceData = CommonUtils.getComponentTypeDataByName(serviceName)
        except Exception as e:
            return {'result':'1','message':"サービス情報の取得に失敗したため無効化処理を中止します。管理者に連絡を行って下さい。"}
        if serviceData == None:
            return {'result':'1','message':"指定されたサービス名称が存在しません。無効化対象を確認して下さい。"}
        if serviceData['SELECTABLE'] == 0:
            return {'result':'1','message':"指定されたサービスは既に無効状態です。処理を中止します。"}

        #サービスの無効化
        try:
            componentType = self.conn.getTable("COMPONENT_TYPE")
            compTypeData = self.conn.selectOne(componentType.select(componentType.c.COMPONENT_TYPE_NAME == serviceName))
            compTypeData['SELECTABLE'] = 0
            sql = componentType.update(componentType.c.COMPONENT_TYPE_NAME == serviceName, values = compTypeData)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"サービス名：" + serviceName + "を無効化しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"COMPONENT_TYPEテーブルの無効化に失敗したため処理を中止します。サービス名：" + serviceName}

    def listService(self):
        try:
            #サービスリストの一覧取得
            table = self.conn.getTable("COMPONENT_TYPE")
            compTypeDataList = self.conn.select(table.select())
        except Exception as e:
            return {'result':'1','message':"サービスリストの登録に失敗したため処理を中止します。"}
        strCompTypeDataList = []
        status = None
        
        if len(compTypeDataList) == 0:
            return {'result':'1','message':"サービスデータが登録されていません。"}
        
        for compTypeData in compTypeDataList:
            try:
                status = CommonUtils.getSelectableStatus(compTypeData['SELECTABLE'])
            except Exception as e:
                return {'result':'1','message':"ステータス名称の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #SELECTABLEの値をenable/disableに変換
            compTypeData['SELECTABLE'] = status
            #JSON形式に変換し、リストに追加
            strCompTypeData = json.dumps(compTypeData, ensure_ascii=False)
            strCompTypeDataList.append(strCompTypeData)
        
        #リストを"&&"で結合
        retData = "&&".join(strCompTypeDataList)
        
        return {'result':'0', 'message':"success", 'data':retData}

    def showService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}

        #変数設定
        serviceName = paramDict['serviceName']
        
        #サービス名称存在チェック
        try:
            compTypeNo = CommonUtils.getComponentTypeNoByName(serviceName)
        except Exception as e:
            return {'result':'1','message':"サービス名称の取得に失敗したためサービス情報表示を中止します。管理者に連絡を行って下さい。"}
        if compTypeNo == None:
            return {'result':'1','message':"指定されたサービス名称は存在しません。サービス情報表示対象を確認して下さい。"}

        #サービスデータ取得
        try:
            componentType = self.conn.getTable("COMPONENT_TYPE")
            compTypeData = self.conn.selectOne(componentType.select(componentType.c.COMPONENT_TYPE_NAME == serviceName))
        except Exception as e:
            return {'result':'1','message':"サービスデータの取得に失敗したためサービス情報表示を中止します。管理者に連絡を行って下さい。"}
        #getSeteclatbeStatus呼び出し
        try:
            result = CommonUtils.getSelectableStatus(compTypeData['SELECTABLE'])
        except Exception as e:
            return {'result':'1','message':"ステータス名称の取得に失敗したためサービス情報表示を中止します。管理者に連絡を行って下さい。"}
        compTypeData['SELECTABLE'] = result
        
        #返却用データの編集
        retData = json.dumps(compTypeData, ensure_ascii=False)
        
        return {'result':'0','message':"success",'data':retData}

    def validateService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数設定
        imageNo = paramDict['imageNo']
        serviceList = paramDict['serviceList']
        
        #イメージ存在チェック
        try:
            imageData = CommonUtils.getImageDataByNo(imageNo)
        except Exception as e:
            return {'reslut':'1','message':"イメージ情報の呼び出しに失敗したためサービス情報の追加を中止します。管理者に連絡を行って下さい。"}
        if imageData == None:
            return {'result':'1','message':"imageNo：" + imageNo + "が存在しません。サービス情報の追加を中止します。"}
        
        #serviceList存在チェック
        serviceNameList = serviceList.split(",")
        serviceNoList = []
        for serviceName in serviceNameList:
            try:
                result =  CommonUtils.getComponentTypeNoByName(serviceName)
                if result == None:
                    return {'result':'1','message':"サービス名称：" + serviceName + "が存在しません。サービス名称を確認して下さい。"}
                else:
                    serviceNoList.append(result)
            except Exception as e:
                return {'result':'1','message':"サービス情報の取得に失敗したためサービス情報の追加を中止します。管理者に連絡を行って下さい。"}

        #登録用データ作成
        compTypeNoList = imageData['COMPONENT_TYPE_NOS'].split(",")
        serviceNoList.extend(compTypeNoList)
        serviceNoList = list(set(serviceNoList))
        if "0" in serviceNoList:
            serviceNoList.remove("0")
        serviceNoList.sort()
        serviceNoList = ",".join(serviceNoList)

        #サービス情報追加
        try:
            image = self.conn.getTable("IMAGE")
            imageData = self.conn.selectOne(image.select(image.c.IMAGE_NO == imageNo))
            imageData['COMPONENT_TYPE_NOS'] = serviceNoList
            sql = image.update(image.c.IMAGE_NO == imageNo, values = imageData)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"イメージNo：" + imageNo + "上で利用可能なサービス情報：" + serviceList + "の追加に成功しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"利用可能サービス情報：" + serviceList + "の追加に失敗しました。処理を終了します。"}

    def revokeService(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #変数設定
        imageNo = paramDict['imageNo']
        serviceList = paramDict['serviceList']
        componentNoList = []
        instanceNoList = []
        
        #イメージ存在チェック
        try:
            imageData = CommonUtils.getImageDataByNo(imageNo)
        except Exception as e:
            return {'result':'1','message':"イメージ情報の取得に失敗したためサービス情報の削除を中止します。管理者に連絡を行って下さい。"}
        if imageData == None:
            return {'result':'1','message':"imageNo：" + imageNo + "が存在しません。サービス情報の削除を中止します。"}

        #serviceList存在チェック
        serviceNameList = serviceList.split(",")
        serviceNoList = []
        for serviceName in serviceNameList:
            try:
                result =  CommonUtils.getComponentTypeNoByName(serviceName)
                if result == None:
                    return {'result':'1','message':"サービス名称：" + serviceName + "が存在しません。サービス名称を確認して下さい。"}
                else:
                    serviceNoList.append(result)
            except Exception as e:
                return {'result':'1','message':"サービス情報の取得に失敗したためサービス情報の削除を中止します。管理者に連絡を行って下さい。"}

        #インスタンス作成中のサービスチェック
        for componentTypeNo in serviceNoList:
            #COMPONENTテーブル情報取得
            try:
                tableComponent = self.conn.getTable("COMPONENT")
                componentData = self.conn.select(tableComponent.select(tableComponent.c.COMPONENT_TYPE_NO == componentTypeNo))
            except Exception as e:
                return {'result':'1','message':"COMPONENTテーブル情報取得に失敗しました。処理を中止します。"}
            if len(componentData) > 0:
                return {'result':'1','message':"指定されたサービスは現在使用されているため処理を中止します。"}

        #登録用データ作成
        imageServiceDataList = imageData['COMPONENT_TYPE_NOS'].split(",")
        for service in serviceNoList:
            errFlg = False
            for image in imageServiceDataList[:]:
                if image == service:
                    imageServiceDataList.remove(image)
                    errFlg = True
            if errFlg == False:
                try:
                    serviceName = CommonUtils.getComponentTypeNameByNo(service)
                except Exception as e:
                    return {'result':'1','message':"サービス情報の取得に失敗したためサービス削除処理を中止します。管理者に連絡を行って下さい。"}
                return {'result':'1','message':"サービス名称：" + serviceName + "はイメージNo：" + imageNo + "で利用可能なサービス情報に存在しません。削除対象を確認して下さい。"}
        #サービス情報の作成
        imageServiceDataList.sort()
        imageService = ",".join(imageServiceDataList)
        if "" == imageService:
            imageService = 0

        #サービス情報削除
        try:
            image = self.conn.getTable("IMAGE")
            imageData = self.conn.selectOne(image.select(image.c.IMAGE_NO == imageNo))
            imageData['COMPONENT_TYPE_NOS'] = imageService
            sql = image.update(image.c.IMAGE_NO == imageNo, values = imageData)
            self.conn.execute(sql)
            self.conn.commit()
            return {'result':'0','message':"イメージNo：" + imageNo + "上で利用可能なサービス情報：" + serviceList + "の削除に成功しました。"}
        except Exception as e:
            self.conn.rollback()
            return {'result':'1','message':"利用可能サービス情報：" + serviceList + "の削除に失敗しました。処理を終了します。"}
