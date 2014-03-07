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
#設定ファイル読み込み
from iaasgw.db.mysqlConnector import MysqlConnector
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertiesReader import PropertiesReader

reader = PropertiesReader()
logger = IaasLogger()

def getProxy():
    conn = MysqlConnector()
    proxyTable = conn.getTable("PROXY")
    proxyinfo = conn.selectOne(proxyTable.select())
    dict = {}
    if proxyinfo is not None:
        dict.update({"host": proxyinfo["HOST"]})
        dict.update({"port": proxyinfo["PORT"]})
        dict.update({"user": proxyinfo["USER"]})
        dict.update({"pass": proxyinfo["PASSWORD"]})

    return  dict

def getImage(imageNo):
    dict = {}
    conn = MysqlConnector()
    imageTable = conn.getTable("IMAGE")
    image = conn.selectOne(imageTable.select(imageTable.c.IMAGE_NO==imageNo))
    if image is None:
        return dict

    platformTable = conn.getTable("PLATFORM")
    platform = conn.selectOne(platformTable.select(platformTable.c.PLATFORM_NO == image["PLATFORM_NO"]))

    dict = {}
    dict.update({"platformNo": image["PLATFORM_NO"]})
    dict.update({"os": image["OS"]})
    dict.update({"type": platform["PLATFORM_TYPE"]})
    if dict["type"] == "aws":
        awsImageTable = conn.getTable("IMAGE_AWS")
        awsImage = conn.selectOne(awsImageTable.select(awsImageTable.c.IMAGE_NO==imageNo))
        dict.update({"imageId": awsImage["IMAGE_ID"]})
        dict.update({"kernelId": awsImage["KERNEL_ID"]})
        dict.update({"ramdiskId": awsImage["RAMDISK_ID"]})
        dict.update({"instanceTypes": awsImage["INSTANCE_TYPES"]})
        if awsImage["EBS_IMAGE"] == 0 :
            dict.update({"ebsImage": "false"})
        else:
            dict.update({"ebsImage": "true"})

    elif dict["type"] == "vmware":
        vmwImageTable = conn.getTable("IMAGE_VMEARE")
        vmwImage = conn.selectOne(vmwImageTable.select(vmwImageTable.c.IMAGE_NO==imageNo))
        dict.update({"templateName": vmwImage["TEMPLATE_NAME"]})
        dict.update({"instanceTypes": vmwImage["INSTANCE_TYPES"]})
    elif dict["type"] == "nifty":
        niftyImageTable = conn.getTable("IMAGE_NIFTY")
        niftyImage = conn.selectOne(niftyImageTable.select(niftyImageTable.c.IMAGE_NO==imageNo))
        dict.update({"imageId": niftyImage["IMAGE_ID"]})
        dict.update({"instanceTypes": niftyImage["INSTANCE_TYPES"]})
    elif dict["type"] == "cloudstack":
        csImageTable = conn.getTable("IMAGE_CLOUDSTACK")
        csImage = conn.selectOne(csImageTable.select(csImageTable.c.IMAGE_NO==imageNo))
        dict.update({"templateId": csImage["TEMPLATE_ID"]})
        dict.update({"instanceTypes":  csImage["INSTANCE_TYPES"]})

    return dict


def getPlatformProperty(pltfmNo):
    dict = {}
    conn = MysqlConnector()
    platformTable = conn.getTable("PLATFORM")
    platform = conn.selectOne(platformTable.select(platformTable.c.PLATFORM_NO == pltfmNo))
    if platform is not None:
        dict.update({"platformNo": str(platform["PLATFORM_NO"])})
        dict.update({"platformName": platform["PLATFORM_NAME"]})
        dict.update({"platformNameDisp": platform["PLATFORM_NAME_DISP"]})
        dict.update({"platformSimplenameDip": platform["PLATFORM_SIMPLENAME_DISP"]})
        dict.update({"internal": platform["INTERNAL"]})
        dict.update({"proxy": platform["PROXY"]})
        dict.update({"platformType": platform["PLATFORM_TYPE"]})
    return  dict


def getAwsInfo(pltfmNo):
    dict = {}
    conn = MysqlConnector()
    platformawsTable = conn.getTable("PLATFORM_AWS")
    platformaws = conn.selectOne(platformawsTable.select(platformawsTable.c.PLATFORM_NO == pltfmNo))
    if platformaws is not None:
        dict.update({"host": platformaws["HOST"]})
        dict.update({"port": platformaws["PORT"]})
        dict.update({"secure": platformaws["SECURE"]})
        if platformaws["EUCA"] == 0 :
            dict.update({"euca": "false"})
        else:
            dict.update({"euca": "true"})

        if platformaws["VPC"] == 0 :
            dict.update({"vpc": "false"})
        else:
            dict.update({"vpc": "true"})
        dict.update({"zone": platformaws["AVAILABILITY_ZONE"]})
        dict.update({"region": platformaws["REGION"]})
    return dict


def getCloudStackInfo(pltfmNo):
    dict = {}
    conn = MysqlConnector()
    platformcsTable = conn.getTable("PLATFORM_CLOUDSTACK")
    platformcs = conn.selectOne(platformcsTable.select(platformcsTable.c.PLATFORM_NO == pltfmNo))
    if platformcs is not None:
        dict.update({"host": platformcs["HOST"]})
        dict.update({"path": platformcs["PATH"]})
        dict.update({"port": str(platformcs["PORT"])})
        dict.update({"secure": platformcs["SECURE"]})
        dict.update({"timeout": platformcs["TIMEOUT"]})
        dict.update({"device": platformcs["DEVICE_TYPE"]})
        dict.update({"hostid": platformcs["HOST_ID"]})

    return dict

def getVCloudInfo(pltfmNo):
    dict = {}
    conn = MysqlConnector()
    platformcsTable = conn.getTable("PLATFORM_VCLOUD")
    platformcs = conn.selectOne(platformcsTable.select(platformcsTable.c.PLATFORM_NO == pltfmNo))
    if platformcs is not None:
        dict.update({"host": platformcs["HOST"]})
        dict.update({"path": platformcs["PATH"]})
        dict.update({"port": str(platformcs["PORT"])})
        dict.update({"secure": platformcs["SECURE"]})

    return dict

def getScriptProperty(keyStr):
    if keyStr in reader:
        return reader[keyStr]
    else:
        None

def getDnsProperty(keyStr):
    if keyStr in reader:
        return reader[keyStr]
    else:
        None

def getPuppetProperty(keyStr):
    if keyStr in reader:
        return reader[keyStr]
    else:
        None

def getVpnProperty(keyStr):
    if keyStr in reader:
        return reader[keyStr]
    else:
        None

