# -*- coding: utf-8 -*-
class ArgumentManager:

    PlatformArgsList=[
        {
            "method":"addPlatform",
            "required":["iaasName", "platformName", "platformNameDisp", "platformSimpleDisp"],
            "optional":["internal", "proxy"]
        },
        {
            "method":"addAwsPlatform",
            "required":["iaasName", "platformName", "platformNameDisp", "platformSimpleDisp", "endpoint"],
            "optional":["internal", "proxy", "secure", "euca", "vpc", "region", "availabilityZone", "vpcId"]
        },
        {
            "method":"addVmwarePlatform",
            "required":["iaasName", "platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "userName", "password", "datacenter", "publicNetwork", "privateNetwork", "computeResource", "instanceTypeName", "cpu", "memory"],
            "optional":["internal", "proxy", ]
        },
        {
            "method":"addCloudstackPlatform",
            "required":["iaasName", "platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "zoneId", "networkId", "deviceType"],
            "optional":["internal", "proxy", "secure", "timeout", "hostId"]
        },
        {
            "method":"addVcloudPlatform",
            "required":["iaasName", "platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "orgName", "vdcName", "instanceTypeName", "cpu", "memory", "storageTypeName"],
            "optional":["internal", "proxy", "secure", "timeout", "defNetwork"]
        },
        {
            "method":"addOpenstackPlatform",
            "required":["iaasName", "platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "networkId", "tenantId"],
            "optional":["internal", "proxy", "tenantName", "availabilityZone"]
        },
        {
            "method":"addAzurePlatform",
            "required":["iaasName", "platformName", "platformNameDisp", "platformSimpleDisp", "region", "affinityGroupName", "cloudServiceName", "storageAccountName", "networkName"],
            "optional":["internal", "proxy", "availabilitySets"]
        },
        {
            "method":"updatePlatform",
            "required":["platformNo"],
            "optional":["platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "userName", "password", "publicNetwork", "privateNetwork", "timeout", "defNetwork"]
        },
        {
            "method":"updateAwsPlatform",
            "required":["platformNo"],
            "optional":["platformName", "platformNameDisp", "platformSimpleDisp", "endpoint"]
        },
        {
            "method":"updateVmwarePlatform",
            "required":["platformNo"],
            "optional":["platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "userName", "password", "publicNetwork", "privateNetwork"]
        },
        {
            "method":"updateCloudstackPlatform",
            "required":["platformNo"],
            "optional":["platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "timeout"]
        },
        {
            "method":"updateVcloudPlatform",
            "required":["platformNo"],
            "optional":["platformName", "platformNameDisp", "platformSimpleDisp", "endpoint", "timeout", "defNetwork"]
        },
        {
            "method":"updateOpenstackPlatform",
            "required":["platformNo"],
            "optional":["platformName", "platformNameDisp", "platformSimpleDisp", "endpoint"]
        },
        {
            "method":"deletePlatform",
            "required":["platformName"],
            "optional":[]
        },
        {
            "method":"enablePlatform",
            "required":["platformName"],
            "optional":[]
        },
        {
            "method":"disablePlatform",
            "required":["platformName"],
            "optional":[]
        },
        {
            "method":"listPlatform",
            "required":[],
            "optional":[]
        },
        {
            "method":"showPlatform",
            "required":["platformName"],
            "optional":[]
        },
        {
            "method":"listIaas",
            "required":[],
            "optional":[]
        },
        {
            "method":"showIaas",
            "required":["iaasName"],
            "optional":[]
        },
        {
            "method":"addInstanceType",
            "required":["platformName", "instanceTypeName", "cpu", "memory"],
            "optional":[]
        },
        {
            "method":"updateInstanceType",
            "required":["instanceTypeNo", "platformName"],
            "optional":["instanceTypeName", "cpu", "memory"]
        },
        {
            "method":"deleteInstanceType",
            "required":["instanceTypeNo", "platformName"],
            "optional":[]
        },
        {
            "method":"listInstanceType",
            "required":[],
            "optional":["platformName"]
        },
        {
            "method":"addStorageType",
            "required":["platformName", "storageTypeName"],
            "optional":[]
        },
        {
            "method":"updateStorageType",
            "required":["storageTypeNo", "platformName", "storageTypeName"],
            "optional":[]
        },
        {
            "method":"deleteStorageType",
            "required":["storageTypeNo", "platformName"],
            "optional":[]
        },
        {
            "method":"listStorageType",
            "required":[],
            "optional":["platformName"]
        },
        {
            "method":"addImage",
            "required":["imageName", "imageNameDisp", "osName", "osNameDisp", "instanceTypeList", "imageId"],
            "optional":["platformList", "serviceList", "zabbixTemplate", "icon"]
        },
        {
            "method":"addAwsImage",
            "required":["imageName", "imageNameDisp", "osName", "osNameDisp", "instanceTypeList", "imageId", "ebsImageFlg"],
            "optional":["platformList", "serviceList", "zabbixTemplate", "kernelId", "ramdiskId", "icon"]
        },
        {
            "method":"addImageCheck",
            "required":["moduleName"],
            "optional":["platformList"]
        },        
        {
            "method":"updateImage",
            "required":["imageNo"],
            "optional":["imageName", "imageNameDisp", "osName", "osNameDisp", "serviceList", "instanceTypeList", "zabbixTemplate", "icon"]
        },
        {
            "method":"updateAwsImage",
            "required":["imageNo"],
            "optional":["imageName", "imageNameDisp", "osName", "osNameDisp", "serviceList", "instanceTypeList", "zabbixTemplate", "kernelId", "ramdiskId", "icon"]
        },
        {
            "method":"updateImageCheck",
            "required":["imageNo"],
            "optional":["imageName", "imageNameDisp", "osName", "osNameDisp", "serviceList", "instanceTypeList", "zabbixTemplate", "kernelId", "ramdiskId", "icon"]
        },
        {
            "method":"deleteImageCheck",
            "required":["moduleName"],
            "optional":["platformList"]
        },
                {
            "method":"deleteImage",
            "required":["imageName"],
            "optional":["platformList"]
        },
        {
            "method":"enableImage",
            "required":["imageNo"],
            "optional":[]
        },
        {
            "method":"disableImage",
            "required":["imageNo"],
            "optional":[]
        },
        {
            "method":"listImage",
            "required":[],
            "optional":[]
        },
        {
            "method":"showImage",
            "required":["imageNo"],
            "optional":[]
        },
        {
            "method":"addService",
            "required":["serviceName", "serviceNameDisp", "layer", "layerNameDisp", "runOrder", "zabbixTemplate"],
            "optional":["addressUrl", "imageNoList"]
        },
        {
            "method":"addServiceCheck",
            "required":["moduleName"],
            "optional":["imageNoList"]
        },
        {
            "method":"updateService",
            "required":["serviceName"],
            "optional":["serviceNameDisp", "layer", "layerNameDisp", "runOrder", "zabbixTemplate", "addressUrl"]
        },
        {
            "method":"deleteServiceCheck",
            "required":["moduleName"],
            "optional":["imageNoList"]
        },
        {
            "method":"deleteService",
            "required":["serviceName"],
            "optional":["imageNoList"]
        },
        {
            "method":"enableService",
            "required":["serviceName"],
            "optional":[]
        },
        {
            "method":"disableService",
            "required":["serviceName"],
            "optional":[]
        },
        {
            "method":"listService",
            "required":[],
            "optional":[]
        },
        {
            "method":"showService",
            "required":["serviceName"],
            "optional":[]
        },
        {
            "method":"validateService",
            "required":["imageNo","serviceList"],
            "optional":[]
        },
        {
            "method":"revokeService",
            "required":["imageNo","serviceList"],
            "optional":[]
        },
        {
            "method":"installModule",
            "required":["moduleName"],
            "optional":[]
        },
        {
            "method":"removeModule",
            "required":["moduleName"],
            "optional":[]
        },
                {
            "method":"removeModuleCheck",
            "required":["moduleName","mifDict"],
            "optional":[]
        },
        {
            "method":"updateModule",
            "required":["moduleName"],
            "optional":[]
        },
        {
            "method":"listModule",
            "required":[],
            "optional":["installed"]
        },
        {
            "method":"showModule",
            "required":["moduleName"],
            "optional":[]
        },
        {
            "method":"cleanCache",
            "required":[],
            "optional":[]
        },
    ]
    
    PlatformArgsFormat=[
        {"argument":"iaasName", "length":300, "format":"halfAlpha"},
        {"argument":"platformNo", "length":19, "format":"number"},
        {"argument":"platformName", "length":300, "format":"halfAlpha"},
        {"argument":"platformNameDisp", "length":300, "format":None},
        {"argument":"platformSimpleDisp", "length":300, "format":None},
        {"argument":"internal", "length":1, "format":"boolean"},
        {"argument":"proxy", "length":1, "format":"boolean"},
        {"argument":"endpoint", "length":300, "format":"url"},
        {"argument":"secure", "length":1, "format":"boolean"},
        {"argument":"euca", "length":1, "format":"boolean"},
        {"argument":"vpc", "length":1, "format":"boolean"},
        {"argument":"region", "length":300, "format":None},
        {"argument":"availabilityZone", "length":300, "format":None},
        {"argument":"vpcId", "length":300, "format":"halfAlpha"},
        {"argument":"userName", "length":300, "format":"halfAlpha"},
        {"argument":"password", "length":300, "format":"halfAlpha"},
        {"argument":"datacenter", "length":300, "format":None},
        {"argument":"publicNetwork", "length":300, "format":None},
        {"argument":"privateNetwork", "length":300, "format":None},
        {"argument":"computeResource", "length":300, "format":None},
        {"argument":"cpu", "length":19, "format":"number"},
        {"argument":"memory", "length":19, "format":"number"},
        {"argument":"zoneId", "length":300, "format":"halfAlpha"},
        {"argument":"networkId", "length":300, "format":"halfAlpha"},
        {"argument":"timeout", "length":19, "format":"number"},
        {"argument":"deviceType", "length":300, "format":"halfAlpha"},
        {"argument":"hostId", "length":300, "format":"halfAlpha"},
        {"argument":"orgName", "length":300, "format":None},
        {"argument":"vdcName", "length":300, "format":None},
        {"argument":"defNetwork", "length":300, "format":None},
        {"argument":"instanceTypeName", "length":300, "format":None},
        {"argument":"storageTypeName", "length":300, "format":None},
        {"argument":"tenantId", "length":300, "format":"halfAlpha"},
        {"argument":"tenantName", "length":300, "format":None},
        {"argument":"affinityGroupName", "length":300, "format":None},
        {"argument":"cloudServiceName", "length":300, "format":None},
        {"argument":"storageAccountName", "length":300, "format":None},
        {"argument":"networkName", "length":300, "format":None},
        {"argument":"availabilitySets", "length":300, "format":None},
        {"argument":"iaasName", "length":300, "format":"halfAlpha"},
        {"argument":"instanceTypeNo", "length":19, "format":"number"},
        {"argument":"storageTypeNo", "length":19, "format":"number"},
        {"argument":"imageNo", "length":19, "format":"number"},
        {"argument":"imageName", "length":300, "format":"halfAlpha"},
        {"argument":"imageNameDisp", "length":300, "format":None},
        {"argument":"osName", "length":300, "format":"halfAlpha"},
        {"argument":"osNameDisp", "length":300, "format":None},
        {"argument":"serviceList", "length":300, "format":"halfAlpha"},
        {"argument":"instanceTypeList", "length":300, "format":None},
        {"argument":"zabbixTemplate", "length":300, "format":"halfAlpha"},
        {"argument":"imageId", "length":300, "format":"halfAlpha"},
        {"argument":"kernelId", "length":300, "format":"halfAlpha"},
        {"argument":"ramdiskId", "length":300, "format":"halfAlpha"},
        {"argument":"ebsImageFlg", "length":1, "format":"boolean"},
        {"argument":"icon", "length":300, "format":None},
        {"argument":"serviceModule", "length":300, "format":"halfAlpha"},
        {"argument":"serviceName", "length":300, "format":"halfAlpha"},
        {"argument":"serviceNameDisp", "length":300, "format":None},
        {"argument":"layer", "length":300, "format":"halfAlpha"},
        {"argument":"layerNameDisp", "length":300, "format":None},
        {"argument":"runOrder", "length":19, "format":"number"},
        {"argument":"zabbixTemplate", "length":300, "format":"halfAlpha"},
        {"argument":"addressUrl", "length":300, "format":"url"},
        {"argument":"imageModule", "length":300, "format":"halfAlpha"},
        {"argument":"platformList", "length":300, "format":"halfAlpha"},
        {"argument":"moduleName", "length":300, "format":"halfAlpha"},
    ]

