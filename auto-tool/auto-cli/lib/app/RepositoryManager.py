# -*- coding: utf-8 -*-
import subprocess
import sys
import re
import os
import json
import common.CommonUtils as CommonUtils
from db.MysqlConnector import MysqlConnector

class RepositoryManager:

    def removeModule(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        #辞書からmifDictを受け取えい削除
        mifDict = paramDict['mifDict']
        del paramDict['mifDict']
        
        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        
        if result != True:
            return {'result':'1','message':result}
        
        #引数から変数へセット
        moduleName = paramDict['moduleName']

        #削除対象がイメージの場合
        if mifDict['moduleInformation'].has_key('TemplateModule'):
            imageName = mifDict['moduleInformation']['TemplateModule']['templateModuleInformation']['moduleName']
            #DB上にイメージが登録されているか確認
            try:
                ret = CommonUtils.getImageDataByName(imageName)
            except Exception as e:
                return {'result':'1','message':"イメージ情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            if ret is not None:
                return {'result':'1','message':"モジュール:" + moduleName + "は現在PCCに登録されています。pcc del imageコマンドを使用してPCCから削除した後、pccrepo removeコマンドを実行して下さい。"}
        
        #削除対象がサービスの場合
        elif mifDict['moduleInformation'].has_key('ServiceModule'):
            serviceName = mifDict['moduleInformation']['ServiceModule']['ServiceModuleInformation']['moduleName']
            #DB上にサービスが登録されているか確認
            try:
                ret = CommonUtils.getComponentTypeNoByName(serviceName)
            except Exception as e:
                return {'result':'1','message':"サービス情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            if ret is not None:
                return {'result':'1','message':"モジュール:" + moduleName + "は現在PCCに登録されています。pcc del serviceコマンドを使用してPCCから削除した後、pccrepo removeコマンドを実行して下さい。"}
        else:
            return {'result':'1','message':"JSONファイルが壊れているため処理を中止します。"}
        #正常終了
        return {'result':'0','message':"success"}

    def updateModule(self, paramDict):
        #処理メソッド取得
        method = paramDict['method']
        #辞書からメソッドキーと値を削除
        del paramDict['method']
        #辞書からmifDictを受け取り削除
        mifDict = paramDict['mifDict']
        del paramDict['mifDict']

        #引数のチェック
        result = CommonUtils.checkArguments(method, paramDict)
        if result != True:
            return {'result':'1','message':result}
        
        #引数から変数へセット
        moduleName = paramDict['moduleName']
        componentNos = []
        instanceNos = []
        status = []

        #更新対象がイメージの場合
        if mifDict['moduleInformation'].has_key('TemplateModule'):
            #MIF中にテンプレートIDが存在するか確認
            #if jsonMif['moduleInformation']['TemplateModude']['templateModuleInformation'].has_key('uploadedTemplateID'):
            a = "hoge"
            #############################################################
            #IaaSへの登録機能実装時に実装する
            #############################################################

        #更新対象がサービスの場合
        elif mifDict['moduleInformation'].has_key('ServiceModule'):
            serviceName = mifDict['moduleInformation']['ServiceModule']['ServiceModuleInformation']['moduleName']
            #COMPONENT_TYPEテーブルからモジュールのCOMPONENT_TYPE_NO取得
            try:
                componentTypeNo = CommonUtils.getComponentTypeNoByName(serviceName)
            except Exception as e:
                return {'result':'1','message':"サービス情報の取得に失敗したため処理を中止します。管理者に連絡を行って下さい。"}
            #PCCテーブルに登録されている場合使用中確認を行う
            if componentTypeNo is not None:
                conn = MysqlConnector()
                #COMPONENTテーブル情報取得
                try:
                    tableComponent = conn.getTable("COMPONENT")
                    componentData = conn.select(tableComponent.select(tableComponent.c.COMPONENT_TYPE_NO == componentTypeNo))
                except Exception as e:
                    return {'result':'1','message':"COMPONENTテーブル情報取得に失敗しました。処理を中止します。"}
                if len(componentData) > 0:
                    for conponent in componentData:
                        componentNos.append(conponent['COMPONENT_NO'])
                #COMPONENT_INSTANCEテーブル情報取得
                try:
                    tableComponentInstance = conn.getTable("COMPONENT_INSTANCE")
                except Exception as e:
                    return {'result':'1','message':"COMPONENT_INSTANCEテーブル情報取得に失敗しました。処理を中止します。"}
                for conponentNo in componentNos:
                    instanceNos.append(conn.selectOne(tableComponentInstance.select(tableComponentInstance.c.COMPONENT_NO == conponentNo))['INSTANCE_NO'])
                #INSTANCEテーブル情報取得
                try:
                    tableInstance = conn.getTable("INSTANCE")
                except Exception as e:
                    return {'result':'1','message':"INSTANCEテーブル情報取得に失敗しました。処理を中止します。"}
                for instanceNo in instanceNos:
                    status.append(conn.selectOne(tableInstance.select(tableInstance.c.INSTANCE_NO == instanceNo))['STATUS'])
                #起動中のサービスが存在する場合処理終了
                if "RUNNING" in status:
                    return {'result':'1','message':"起動中のサービスが存在するため処理を中止します。"}
        else:
            return {'result':'1','message':"JSONファイルが壊れているため処理を中止します。"}
        
        #正常終了
        return {'result':'0','message':"success"}
