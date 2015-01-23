# -*- coding: utf-8 -*-
import subprocess
import sys
import re
import os
import json
import glob
import socket
from urlparse import urlparse
from ArgumentManager import ArgumentManager
from db.MysqlConnector import MysqlConnector
from ast import literal_eval
from sqlalchemy.sql.expression import and_

def checkDbConnection():
    #DB接続テスト
    conn = MysqlConnector()
    try:
       sql = "SELECT 1 FROM dual"
       result = conn.selectOne(sql)
       return True
    except Exception as e:
       return {'result':'1','message':"PCCデータベースへの接続に失敗しました。処理を終了します。"}

def getPlatformTypeByName(platformName):

    conn = MysqlConnector()
    try:
        table = conn.getTable("PLATFORM")
    except Exception as e:
        return None
    plData = conn.selectOne(table.select(table.c.PLATFORM_NAME==platformName))
    
    if plData != None:
        return plData["PLATFORM_TYPE"]
    else:
        return None

def getPlatformTypeByNo(platformNo):

    conn = MysqlConnector()
    try:
        table = conn.getTable("PLATFORM")
    except Exception as e:
        return None
    plData = conn.selectOne(table.select(table.c.PLATFORM_NO==platformNo))
    
    if plData != None:
        return plData["PLATFORM_TYPE"]
    else:
        return None

def getPlatformDataByName(platformName):

    conn = MysqlConnector()
    try:
        table = conn.getTable("PLATFORM")
    except Exception as e:
        return None
    plData = conn.selectOne(table.select(table.c.PLATFORM_NAME==platformName))
    
    if plData != None:
        return plData
    else:
        return None

def getPlatformDataByNo(platformNo):

    conn = MysqlConnector()
    try:
        table = conn.getTable("PLATFORM")
    except Exception as e:
        return None
    plData = conn.selectOne(table.select(table.c.PLATFORM_NO==platformNo))
    
    if plData != None:
        return plData
    else:
        return None

def getPlatformDataByIaas(iaasName):

    conn = MysqlConnector()
    try:
        table = conn.getTable("PLATFORM")
    except Exception as e:
        return None
    plData = conn.select(table.select(table.c.PLATFORM_TYPE==iaasName))
    
    if plData != None:
        return plData
    else:
        return None

def getImageDataByNo(imageNo):

    conn = MysqlConnector()
    try:
        table = conn.getTable("IMAGE")
    except Exception as e:
        return None
    imageData = conn.selectOne(table.select(table.c.IMAGE_NO==imageNo))
    
    if imageData != None:
        return imageData
    else:
        return None

def getImageDataByName(imageName):

    conn = MysqlConnector()
    try:
        table = conn.getTable("IMAGE")
    except Exception as e:
        return None
    imageData = conn.select(table.select(table.c.IMAGE_NAME==imageName))
    
    if len(imageData) != 0:
        return imageData
    else:
        return None

def getImageDataByNameAndPlatformNo(imageName, platformNo):

    conn = MysqlConnector()
    try:
        table = conn.getTable("IMAGE")
    except Exception as e:
        return None
    imageData = conn.selectOne(table.select(and_(table.c.IMAGE_NAME==imageName, table.c.PLATFORM_NO==platformNo)))
    
    if imageData != None:
        return imageData
    else:
        return None

def getComponentTypeNoByName(serviceName):
    conn = MysqlConnector()
    try:
        table = conn.getTable("COMPONENT_TYPE")
    except Exception as e:
        return None
    compTypeData = conn.selectOne(table.select(table.c.COMPONENT_TYPE_NAME==serviceName))

    if compTypeData != None:
        return str(compTypeData["COMPONENT_TYPE_NO"])
    else:
        return None

def getComponentTypeNameByNo(serviceNo):
    conn = MysqlConnector()
    try:
        table = conn.getTable("COMPONENT_TYPE")
    except Exception as e:
        return None
    compTypeData = conn.selectOne(table.select(table.c.COMPONENT_TYPE_NO==serviceNo))

    if compTypeData != None:
        return compTypeData["COMPONENT_TYPE_NAME"]
    else:
        return None

def getComponentTypeDataByName(serviceName):
    conn = MysqlConnector()
    try:
        table = conn.getTable("COMPONENT_TYPE")
    except Exception as e:
        return None
    compTypeData = conn.selectOne(table.select(table.c.COMPONENT_TYPE_NAME==serviceName))

    if compTypeData != None:
        return compTypeData
    else:
        return None

def getSelectablePlatformNameList():
    conn = MysqlConnector()
    tablePlatform = conn.getTable("PLATFORM")
    platformData = conn.select(tablePlatform.select(tablePlatform.c.SELECTABLE=="1"))
    #platformNameList作成
    platformNameList = []
    for i in range(len(platformData)):
        platformNameList.append(str(platformData[i]["PLATFORM_NAME"]))
    return platformNameList

def getSelectableImageNoList():
    conn = MysqlConnector()
    tableImage = conn.getTable("IMAGE")
    imageData = conn.select(tableImage.select(tableImage.c.SELECTABLE=="1"))
    #imageNameList作成
    imageNameList = []
    for i in range(len(imageData)):
        imageNameList.append(str(imageData[i]["IMAGE_NO"]))
    return imageNameList

def getSelectableStatus(selectable):
    if 0 == selectable:
        return "Disable"
    elif 1 == selectable:
        return "Enable"
    else:
        return False

def getMif(moduleName):
    #パス設定
    mifPath = "/opt/adc/pccrepo/" + moduleName + "/" + moduleName + ".json"
    #ファイル存在チェック
    if False == os.path.exists(mifPath):
        return None
    else:
        mifJsonFile = open(mifPath, 'r')
        mif = mifJsonFile.read()
        return json.loads(mif)

def checkArguments(method, paramDict):
    result = checkRequiredArgs(method, paramDict)
    if result != True:
        return result + "が指定されていません。正しい値を指定して下さい。"

    result = checkSupportedArgs(method, paramDict)
    if result != True:
        return "コマンドがサポートしない引数:" + result + " が指定されています。正しい値を指定して下さい。"

    
    result = checkLengthArgs(paramDict)
    if result != True:
        return result[0] + "は" + str(result[1]) + "桁以内で入力して下さい。"

    result = checkFormatArgs(paramDict)
    if result != True:
        if "halfAlpha" == result[1]:
            return result[0] + "は半角英数記号で入力して下さい。"
        elif "number" == result[1]:
            return result[0] + "は数字で入力して下さい。"
        elif "url" == result[1]:
            return result[0] + "はURL形式(http://host:port/path または https://host:port/path)で入力して下さい。"
        elif "boolean" == result[1]:
            return result[0] + "は0または1で入力して下さい。"
    
    return True

def checkRequiredArgs(method, paramDict):
    argObjList = ArgumentManager.PlatformArgsList
    argDict = None

    for argObj in argObjList:
        if method == argObj["method"]:
            argDict = argObj
            break

    reqList = argDict["required"]
    
    for reqObj in reqList:
        if reqObj not in paramDict:
            return reqObj
        elif isBlank(paramDict[reqObj]):
            return reqObj
    return True

def checkSupportedArgs(method, paramDict):
    argObjList = ArgumentManager.PlatformArgsList
    argDict = None

    for argObj in argObjList:
        if method == argObj["method"]:
            argDict = argObj
            break
    
    reqList = argDict["required"]
    optList = argDict["optional"]
    
    for key in paramDict.keys():
        if key not in reqList:
            if key not in optList:
                return key
    return True

def checkLengthArgs(paramDict):
    argFormatList = ArgumentManager.PlatformArgsFormat

    for key in paramDict.keys():
        for argFormatDict in argFormatList:
            if key == argFormatDict["argument"]:
                value = paramDict[key]
                length = argFormatDict["length"]
                if not isBlank(value) and length != None :
                    if len(unicode(value)) > argFormatDict["length"]:
                        return [key, argFormatDict["length"]]
    return True

def checkFormatArgs(paramDict):
    argFormatList = ArgumentManager.PlatformArgsFormat
    
    for key in paramDict.keys():
        for argFormatDict in argFormatList:
            if key == argFormatDict["argument"]:
                value = paramDict[key]
                format = argFormatDict["format"]
                if not isBlank(value) and format != None :
                    if "halfAlpha" == format:
                        if not isHalfAlpha(value):
                            return [key, "halfAlpha"]
                    elif "number" == format:
                        if not isNumber(value):
                            return [key, "number"]
                    elif "url" == format:
                        if not isUrl(value):
                            return [key, "url"]
                    elif "boolean" == format:
                        if not isBoolean(value):
                            return [key, "boolean"]
    return True

def checkIaasName(paramDict):
    if "iaasName" not in paramDict:
        return "iaasNameが指定されていません。正しい値を指定して下さい。"
    
    iaasName = paramDict['iaasName']
    
    if isBlank(iaasName):
        return "iaasNameが指定されていません。正しい値を指定して下さい。"
    elif not isSupportedIaas(iaasName):
        return "iaasName:" + iaasName + "はPCCのサポート対象外です。"
    else:
        return True

def isBlank(value):
    if value == None:
        return True
    elif len(value.strip()) == 0:
        return True
    else:
        return False

def isHalfAlpha(value):
    regexp = re.compile(r'^[a-zA-Z0-9!-/:-@\[-`{-~]+$')

    if regexp.search(value) == None:
        return False
    elif range != None:
        return True
    
    return resultDic

def isNumber(value):
    regexp = re.compile(r'^[0-9]+$')
    if regexp.search(value) == None:
        return False
    else:
        return True

def isUrl(value):
    url = urlparse(value)
    if isBlank(url.scheme):
        return False
    elif "http" != url.scheme and "https" != url.scheme:
        return False
    elif isBlank(url.netloc):
        return False
    else:
        return True

def isBoolean(value):
    if "0" == value or "1" == value:
        return True
    else:
        return False

def isSupportedIaas(value):
    conn = MysqlConnector()
    try:
        table = conn.getTable("IAAS_INFO")
    except Exception as e:
        return False
    iaasNameData = conn.selectOne(table.select(table.c.IAAS_NAME==value))

    if iaasNameData != None:
        return True
    else:
        return False

def getSplittedUrl(value):
    url = urlparse(value)
    scheme = url.scheme
    host = url.netloc.strip(':' + str(url.port))
    port = url.port
    path = url.path
    return {'scheme':scheme, 'host':host, 'port':port, 'path':path}

def getConnector():
    befFile = open("/opt/adc/conf/config.properties", "r")
    user = None
    pswd = None
    url = None
    dbname = None
    lines = befFile.readlines()
    for line in lines:
        if "db.url" in line:
            connText = line.split("=")[1].strip().rstrip("\n").replace("jdbc:", "")
            url = urlparse(connText)
            scheme = url.scheme
            host = url.netloc.strip(':' + str(url.port))
            port = url.port
            path = url.path

def getConnectZabbixApi():
    url = readZabbixProperties("zaburl")
    user = readZabbixProperties("zabuser")
    pswd = readZabbixProperties("zabpswd")

    #取得に失敗した場合はNoneを返却
    if url is None or user is None or pswd is None:
        return None

    #curlコマンド発行
    param = '{"jsonrpc":"2.0", "method":"user.login", "params":{"user":"' + user + '", "password":"' + pswd + '"}, "auth":null, "id":0}'
    ret = subprocess.Popen('curl -s -H "Accept: application/json" -H "Content-type: application/json" ' + url + "api_jsonrpc.php -i -X POST -d '" + param + "'", stdout=subprocess.PIPE, shell=True)
    retJson = None
    while True:
        line = ret.stdout.readline()
        if not line:
            break
        elif (line.startswith('{') and line.endswith('}')) or (line.startswith('[') and line.endswith(']')):
            retJson = line.rstrip()
            break
    
    if retJson is None:
        return None
    retDic = json.loads(retJson)
    if isinstance(retDic,list):
        retDic = retDic[0]

    if retDic.has_key("error"):
        return None
    elif retDic.has_key("result"):
        return retDic["result"]
    else:
        return None

def getZabbixTemplate(auth, templateName):
    #zabbix URL取得
    url = readZabbixProperties("zaburl")

    #curlコマンド発行
    param = '{"jsonrpc": "2.0","method": "template.get","params": {"output": "extend","filter": {"host": "' + templateName + '"}},"auth": "' + auth + '","id": 2}'
    ret =  subprocess.Popen('curl -s -H "Accept: application/json" -H "Content-type: application/json" ' + url + "api_jsonrpc.php -i -X POST -d '" + param + "'", stdout=subprocess.PIPE, shell=True)
    retJson = None
    while True:
        line = ret.stdout.readline()
        if not line:
            break
        elif (line.startswith('{') and line.endswith('}')) or (line.startswith('[') and line.endswith(']')):
            retJson = line.rstrip()
            break
    
    if retJson is None:
        return None
    retDic = json.loads(retJson)
    if isinstance(retDic,list):
        retDic = retDic[0]

    if retDic.has_key("error"):
        return False
    elif retDic.has_key("result") and len(retDic["result"]) > 0:
        return True
    else:
        return False

def createZabbixTemplate(auth, templateName):
    #zabbix URL取得
    url = readZabbixProperties("zaburl")
    #curlコマンド発行
    param = '{"jsonrpc": "2.0","method": "template.create","params": {"host": "' + templateName + '","groups": [{"groupid": "1"}]},"auth": "' + auth + '","id": 1}'
    ret =  subprocess.Popen('curl -s -H "Accept: application/json" -H "Content-type: application/json" ' + url + "api_jsonrpc.php -i -X POST -d '" + param + "'", stdout=subprocess.PIPE, shell=True)
    retJson = None
    while True:
        line = ret.stdout.readline()
        if not line:
            break
        elif (line.startswith('{') and line.endswith('}')) or (line.startswith('[') and line.endswith(']')):
            retJson = line.rstrip()
            break
    
    if retJson is None:
        return None
    retDic = json.loads(retJson)
    if isinstance(retDic,list):
        retDic = retDic[0]

    if retDic.has_key("error"):
        return "zabbixテンプレート:" + templateName + "の登録に失敗しました。"
    elif retDic.has_key("result"):
        return True
    else:
        return False

def readZabbixProperties(name):
    confFile = open("/opt/adc/conf/config.properties", "r")
    lines = confFile.readlines()
    for line in lines:
        if "zaburl" == name and "zabbix.url" in line:
            return line.split("=")[1].strip().rstrip("\n")
        elif "zabuser" == name and "zabbix.username" in line:
            return line.split("=")[1].strip().rstrip("\n")
        elif "zabpswd" == name and "zabbix.password" in line:
            return line.split("=")[1].strip().rstrip("\n")
    return None

if __name__ == '__main__':
    print(checkDbConnection())
