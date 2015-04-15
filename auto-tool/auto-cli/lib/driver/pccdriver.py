# -*- coding: utf-8 -*-
import json
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))) )
import common.CommonUtils as CommonUtils
from app.PlatformManager import PlatformManager
from app.ImageManager import ImageManager
from app.ServiceManager import ServiceManager
from app.RepositoryManager import RepositoryManager

if __name__ == '__main__':
    command = sys.argv[1]
    jsonData = sys.argv[2]
    paramDict = json.loads(jsonData)
    #DB接続チェック
    checkResult = CommonUtils.checkDbConnection()
    #DB接続エラーの場合、処理終了
    if checkResult == True:
        if "addPlatform" == command:
            pm = PlatformManager()
            retDict = pm.addPlatform(paramDict)
        elif "updatePlatform" == command:
            pm = PlatformManager()
            retDict = pm.updatePlatform(paramDict)
        elif "deletePlatform" == command:
            pm = PlatformManager()
            retDict = pm.deletePlatform(paramDict)
        elif "enablePlatform" == command:
            pm = PlatformManager()
            retDict = pm.enablePlatform(paramDict)
        elif "disablePlatform" == command:
            pm = PlatformManager()
            retDict = pm.disablePlatform(paramDict)
        elif "listPlatform" == command:
            pm = PlatformManager()
            retDict = pm.listPlatform()
        elif "showPlatform" == command:
            pm = PlatformManager()
            retDict = pm.showPlatform(paramDict)
        elif "addInstanceType" == command:
            pm = PlatformManager()
            retDict = pm.addInstanceType(paramDict)
        elif "updateInstanceType" == command:
            pm = PlatformManager()
            retDict = pm.updateInstanceType(paramDict)
        elif "deleteInstanceType" == command:
            pm = PlatformManager()
            retDict = pm.deleteInstanceType(paramDict)
        elif "listInstanceType" == command:
            pm = PlatformManager()
            retDict = pm.listInstanceType(paramDict)
        elif "addStorageType" == command:
            pm = PlatformManager()
            retDict = pm.addStorageType(paramDict)
        elif "updateStorageType" == command:
            pm = PlatformManager()
            retDict = pm.updateStorageType(paramDict)
        elif "deleteStorageType" == command:
            pm = PlatformManager()
            retDict = pm.deleteStorageType(paramDict)
        elif "listStorageType" == command:
            pm = PlatformManager()
            retDict = pm.listStorageType(paramDict)
        elif "listIaas" == command:
            pm = PlatformManager()
            retDict = pm.listIaas()
        elif "showIaas" == command:
            pm = PlatformManager()
            retDict = pm.showIaas(paramDict)
        elif "addImage" == command:
            im = ImageManager()
            retDict = im.addImage(paramDict)
        elif "updateImage" == command:
            im = ImageManager()
            retDict = im.updateImage(paramDict)
        elif "deleteImage" == command:
            im = ImageManager()
            retDict = im.deleteImage(paramDict)
        elif "enableImage" == command:
            im = ImageManager()
            retDict = im.enableImage(paramDict)
        elif "disableImage" == command:
            im = ImageManager()
            retDict = im.disableImage(paramDict)
        elif "listImage" == command:
            im = ImageManager()
            retDict = im.listImage()
        elif "showImage" == command:
            im = ImageManager()
            retDict = im.showImage(paramDict)
        elif "addService" == command:
            sm = ServiceManager()
            retDict = sm.addService(paramDict)
        elif "updateService" == command:
            sm = ServiceManager()
            retDict = sm.updateService(paramDict)
        elif "deleteService" == command:
            sm = ServiceManager()
            retDict = sm.deleteService(paramDict)
        elif "enableService" == command:
            sm = ServiceManager()
            retDict = sm.enableService(paramDict)
        elif "disableService" == command:
            sm = ServiceManager()
            retDict = sm.disableService(paramDict)
        elif "listService" == command:
            sm = ServiceManager()
            retDict = sm.listService()
        elif "showService" == command:
            sm = ServiceManager()
            retDict = sm.showService(paramDict)
        elif "validateService" == command:
            sm = ServiceManager()
            retDict = sm.validateService(paramDict)
        elif "revokeService" == command:
            sm = ServiceManager()
            retDict = sm.revokeService(paramDict)
        elif "installModule" == command:
            rm = RepositoryManager()
            retDict = rm.installModule(paramDict)
        elif "removeModule" == command:
            rm = RepositoryManager()
            retDict = rm.removeModule(paramDict)
        elif "updateModule" == command:
            rm = RepositoryManager()
            retDict = rm.updateModule(paramDict)
        elif "listModule" == command:
            rm = RepositoryManager()
            retDict = rm.listModule(paramDict)
        elif "showModule" == command:
            rm = RepositoryManager()
            retDict = rm.showModule(paramDict)
        
    else:
        retDict = checkResult

    retJson = json.dumps(retDict, ensure_ascii=False)
    
    print(retJson)
