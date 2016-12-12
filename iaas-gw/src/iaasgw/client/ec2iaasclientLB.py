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
from iaasgw.client.ec2iaasclient import REGION_PARAM
from iaasgw.common.pccConnections import EC2LBConnectionUSE, EC2LBConnectionEUW, \
    EC2LBConnectionUSW, EC2LBConnectionUSWO, EC2LBConnectionSE, EC2LBConnectionNE, \
    NAMESPACE
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.module.ec2.ec2module import InstanceState, BackendServerDescription, \
    ListenerDescription, LBCookieStickinessPolicy, Policies, SourceSecurityGroup, \
    LoadBalancerDescription, HealthCheck, Listener, AppCookieStickinessPolicy
from iaasgw.utils.propertyUtil import getAwsInfo
from libcloud.compute.drivers.ec2 import EC2NodeDriver
from libcloud.compute.types import Provider
#環境によって位置が違います
#from libcloud.utils import findall, findattr
from libcloud.utils.xml import findall, findattr


class EC2IaasNodeDriverLB(EC2NodeDriver):
    type = Provider.EC2
    path = '/'
    def __init__(self, region, key, secret=None, secure=True, useProxy=None):

        param = REGION_PARAM[region]
        if region == "US_EAST":
            self.connectionCls = EC2LBConnectionUSE
        elif region == "EU_WEST":
            self.connectionCls = EC2LBConnectionEUW
        elif region == "US_WEST":
            self.connectionCls = EC2LBConnectionUSW
        elif region == "US_WEST_OREGON":
            self.connectionCls = EC2LBConnectionUSWO
        elif region == "SOUTHEAST":
            self.connectionCls = EC2LBConnectionSE
        elif region == "NORTHEAST":
            self.connectionCls = EC2LBConnectionNE

        self.api_name = param["api_name"]
        self.name = param["name"]
        self.friendly_name = param["friendly_name"]
        self.country = param["country"]
        self.region_name = param["region_name"]
        #プロキシ設定
        self.connectionCls.useProxy = useProxy

        super(EC2IaasNodeDriverLB, self).__init__(key=key, secret=secret, secure=secure)


class EC2IaasClientLB(EC2IaasNodeDriverLB):
    platformNo = None
    debugout = True
    awsInfo = None
    logger = IaasLogger()
    username = None

    def __init__(self, platforminfo, username, key, secret=None):
        self.platformNo = platforminfo["platformNo"]
        self.username = username
        useProxy  =  platforminfo["proxy"]
        #プロキシ利用
        if useProxy == 1:
            useProxy = True
        else:
            useProxy = False

        #リュージョン取得
        self.awsInfo = getAwsInfo(self.platformNo)
        region =  self.awsInfo["region"]



        super(EC2IaasClientLB, self).__init__(region=region, key=key, secret=secret, useProxy=useProxy)


    ############################################################
    #
    #    基礎データ
    #
    ############################################################
    def getPlatformNo(self):
        return self.platformNo

    def setPlatformNo(self, platformNo):
        self.platformNo = platformNo

    def getUsername(self):
        return self.username

    ############################################################
    #
    #    参照系  describe
    #
    ############################################################

    def describeLoadBalancer(self, loadBalancerName):
        loadBalancerDescriptions = self.describeLoadBalancers(loadBalancerName)

        if len(loadBalancerDescriptions) == 0:
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000131", [loadBalancerName,])

        if len(loadBalancerDescriptions) > 1:
            #インスタンスを複数参照できた場合
            raise IaasException("EPROCESS-000132", [loadBalancerName,])

        return loadBalancerDescriptions[0]

    def describeLoadBalancers(self, loadBalancerName=None):
        params = {'Action': 'DescribeLoadBalancers'}
        #任意パラメータ
        if loadBalancerName != None:
            params.update(self._pathlist('LoadBalancerNames.member.0', loadBalancerName))

        elem = self.connection.request(self.path, params=params).object

        loadBalancerDescriptions = []
        for rs in findall(element=elem, xpath='DescribeLoadBalancersResult/LoadBalancerDescriptions/member', namespace=NAMESPACE):
            loadBalancerDescriptions.append(self._to_loadBalancerDescription(rs))

        return loadBalancerDescriptions

    def describeInstanceHealth(self, loadBalancerName, instances = None):
        params = {'Action': 'DescribeInstanceHealth', 'LoadBalancerName':loadBalancerName}
        #任意パラメータ
        if instances != None:
            for i, instance in enumerate(instances,1):
                params['Instances.member.%d.InstanceId' % i] = instance.id

        elem = self.connection.request(self.path, params=params).object

        instanceStates = []
        for rs in findall(element=elem, xpath='DescribeInstanceHealthResult/InstanceStates/member', namespace=NAMESPACE):
            instanceStates.append(self._to_instanceState(rs))

        return instanceStates


    ############################################################
    #
    #    LoadBalancer 操作系
    #
    ############################################################

    def modifyLoadBalancer(self, loadBalancerName):

        params = {'Action': 'ModifyLoadBalancerAttributes', 'LoadBalancerName' : loadBalancerName, 'LoadBalancerAttributes.CrossZoneLoadBalancing.Enabled' : 'true'}
        self.logger.debug(params)
        elem = self.connection.request(self.path, params=params).object


    def createLoadBalancer(self, availabilityZone, listeners, loadBalancerName, subnetIds, securityGroups, internal):

        params = {'Action': 'CreateLoadBalancer', 'LoadBalancerName' : loadBalancerName}
        for i,listener in enumerate(listeners, 1):
            params['Listeners.member.%d.LoadBalancerPort' % i] = listener.loadBalancerPort
            params['Listeners.member.%d.InstancePort' % i] = listener.instancePort
            params['Listeners.member.%d.Protocol' % i] = listener.protocol
            if listener.protocol=='HTTPS' or listener.protocol=='SSL':
                params['Listeners.member.%d.SSLCertificateId' % i] = listener.sslCertificateId

        #選択パラメータ
        if len(subnetIds) != 0:
            #サブネットが指定されている場合サブネットを指定する
            for i,subnetId in enumerate(subnetIds, 1):
                params['Subnets.member.%d' % i] = subnetId
            #セキュリティグループ
            for i,group in enumerate(securityGroups, 1):
                params['SecurityGroups.member.%d' % i] = group

        else:
            #サブネットが指定されていない場合はゾーンを指定する
            params['AvailabilityZones.member.1'] = availabilityZone

        #内部ロードバランサ有効
        if internal:
            params['Scheme'] = "internal"

        self.logger.debug(params)

        elem = self.connection.request(self.path, params=params).object

        dnsName  = findattr(element=elem, xpath="CreateLoadBalancerResult/DNSName", namespace=NAMESPACE)

        return dnsName


    def deleteLoadBalancer(self, loadBalancerName):

        params = {'Action': 'DeleteLoadBalancer', 'LoadBalancerName':loadBalancerName}

        elem = self.connection.request(self.path, params=params).object

    def configureHealthCheck(self, healthCheck, loadBalancerName):
        params = {'Action': 'ConfigureHealthCheck',  'LoadBalancerName':loadBalancerName}
        params["HealthCheck.Timeout"] = str(healthCheck.timeout)
        params["HealthCheck.Target"] = healthCheck.target
        params["HealthCheck.Interval"] = str(healthCheck.interval)
        params["HealthCheck.UnhealthyThreshold"] = str(healthCheck.unhealthyThreshold)
        params["HealthCheck.HealthyThreshold"] = str(healthCheck.healthyThreshold)

        elem = self.connection.request(self.path, params=params).object

        #True Falseを受ける
        for rs in findall(element=elem, xpath='ConfigureHealthCheckResult/HealthCheck', namespace=NAMESPACE):
            healthCheckh = self._to_healthCheck(rs)

        return healthCheckh

    def createLoadBalancerListeners(self, listeners, loadBalancerName):
        params = {'Action': 'CreateLoadBalancerListeners', 'LoadBalancerName':loadBalancerName}
        for i,listener in enumerate(listeners, 1):
            params['Listeners.member.%d.LoadBalancerPort' % i] = str(listener.loadBalancerPort)
            params['Listeners.member.%d.InstancePort' % i] = str(listener.instancePort)
            params['Listeners.member.%d.Protocol' % i] = listener.protocol
            if listener.protocol=='HTTPS' or listener.protocol=='SSL':
                params['Listeners.member.%d.SSLCertificateId' % i] = listener.sslCertificateId

        elem = self.connection.request(self.path, params=params).object

    def applySecurityGroupsToLoadBalancer(self, securityGroups, loadBalancerName):
        params = {'Action': 'ApplySecurityGroupsToLoadBalancer',  'LoadBalancerName':loadBalancerName}
        for i,group in enumerate(securityGroups, 1):
            params["SecurityGroups.member.%d" % i] = str(group)

        self.connection.request(self.path, params=params).object


    def deleteLoadBalancerListeners(self, loadBalancerPorts, loadBalancerName):
        params = {'Action': 'DeleteLoadBalancerListeners', 'LoadBalancerName':loadBalancerName}
        for i, loadBalancerPort in enumerate(loadBalancerPorts, 1):
            params['LoadBalancerPorts.member.%d' % i] = str(loadBalancerPort)

        elem = self.connection.request(self.path, params=params).object


    def registerInstancesWithLoadBalancer(self, instances, loadBalancerName):
        params = {'Action': 'RegisterInstancesWithLoadBalancer', 'LoadBalancerName':loadBalancerName}

        for i, instance in enumerate(instances,1):
            params['Instances.member.%d.InstanceId' % i] = instance

        elem = self.connection.request(self.path, params=params).object

        instanceids = []
        for rs in findall(element=elem, xpath='RegisterInstancesWithLoadBalancerResult/Instances/member', namespace=NAMESPACE):
            instanceids.append(findattr(element=rs, xpath="InstanceId", namespace=NAMESPACE))

        return instanceids


    def deregisterInstancesFromLoadBalancer(self, instances, loadBalancerName):
        params = {'Action': 'DeregisterInstancesFromLoadBalancer', 'LoadBalancerName':loadBalancerName}

        for i, instance in enumerate(instances,1):
            #params['Instances.member.%d.InstanceId' % i] = instance.id
            params['Instances.member.%d.InstanceId' % i] = instance

        elem = self.connection.request(self.path, params=params).object

        instanceids = []
        for rs in findall(element=elem, xpath='DeregisterInstancesFromLoadBalancerResult/Instances/member', namespace=NAMESPACE):
            instanceids.append(findattr(element=rs, xpath="InstanceId ", namespace=NAMESPACE))

        return instanceids



    '''********************************以下ローカルメソッド******************************************
    '''
    def build_list_params(self, params, items, label):
        if isinstance(items, str):
            items = [items]
        for i, item in enumerate(items, 1):
            params[label % i] = item


    def _to_instanceState(self,  element):
        n = InstanceState(
            description = findattr(element=element, xpath="Description", namespace=NAMESPACE),
            instanceId = findattr(element=element, xpath="InstanceId", namespace=NAMESPACE),
            reasonCode = findattr(element=element, xpath="ReasonCode", namespace=NAMESPACE),
            state = findattr(element=element, xpath="State", namespace=NAMESPACE),
        )
        return n



    def _to_loadBalancerDescription(self,  element):

        availabilityZones  = []
        for rs in findall(element=element, xpath='AvailabilityZones', namespace=NAMESPACE):
            availabilityZones.append(findattr(element=rs, xpath="member", namespace=NAMESPACE))

        backendServerDescriptions = []
        for rs in findall(element=element, xpath='BackendServerDescriptions/member', namespace=NAMESPACE):
            backendServerDescription = BackendServerDescription(
                instancePort = findattr(element=rs, xpath="InstancePort", namespace=NAMESPACE),
                policyNames = findattr(element=rs, xpath="PolicyNames", namespace=NAMESPACE),
            )

            backendServerDescriptions.append(backendServerDescription)



        elmlist =  findall(element=element, xpath='HealthCheck', namespace=NAMESPACE)
        healthCheckh = self._to_healthCheck(elmlist[0])


        instances = []
        for rs in findall(element=element, xpath='Instances/member', namespace=NAMESPACE):
            instances.append(findattr(element=rs, xpath="InstanceId ", namespace=NAMESPACE))


        listenerDescriptions = []
        for rs in findall(element=element, xpath='ListenerDescriptions/member', namespace=NAMESPACE):

            elmlist = findall(element=rs, xpath='Listener', namespace=NAMESPACE)
            listener = self._to_listener(elmlist[0])
            if listener.protocol=='HTTPS' or listener.protocol=='SSL':
                listener.setSSLCertificateId(findattr(element=element, xpath="SSLCertificateId", namespace=NAMESPACE))

            policyNames = []
            for rs3 in findall(element=rs, xpath='PolicyNames/member', namespace=NAMESPACE):
                policyNames.append(findattr(element=rs3, xpath="name", namespace=NAMESPACE))

            n = ListenerDescription(listener, policyNames)

            listenerDescriptions.append(n)



        appCookieStickinessPolicies = []
        for rs in findall(element=element, xpath='Policies/AppCookieStickinessPolicies/member', namespace=NAMESPACE):
            appCookieStickinessPolicy = AppCookieStickinessPolicy(
                cookieName = findattr(element=rs, xpath="CookieName", namespace=NAMESPACE),
                policyName = findattr(element=rs, xpath="PolicyName", namespace=NAMESPACE),
            )

            appCookieStickinessPolicies.append(appCookieStickinessPolicy)

        lbCookieStickinessPolicies = []
        for rs in findall(element=element, xpath='Policies/LbCookieStickinessPolicy/member', namespace=NAMESPACE):
            lbCookieStickinessPolicy = LBCookieStickinessPolicy(
                cookieExpirationPeriod  = findattr(element=rs, xpath="CookieExpirationPeriod ", namespace=NAMESPACE),
                policyName = findattr(element=rs, xpath="PolicyName", namespace=NAMESPACE),
            )

            lbCookieStickinessPolicies.append(lbCookieStickinessPolicy)

        otherPolicies = []
        for rs in findall(element=element, xpath='Policies/OtherPolicies', namespace=NAMESPACE):
            otherPolicies.append(findattr(element=element, xpath="member", namespace=NAMESPACE))


        policies = Policies(
            appCookieStickinessPolicies = appCookieStickinessPolicies,
            lbCookieStickinessPolicies  = lbCookieStickinessPolicies,
            otherPolicies = otherPolicies,
        )


        sourceSecurityGroups  = []
        for rs in findall(element=element, xpath='SourceSecurityGroup', namespace=NAMESPACE):
            sourceSecurityGroup = SourceSecurityGroup(
                groupName  = findattr(element=rs, xpath="GroupName", namespace=NAMESPACE),
                ownerAlias = findattr(element=rs, xpath="OwnerAlias", namespace=NAMESPACE),
            )

            sourceSecurityGroups.append(sourceSecurityGroup)


        boadBalancerDescription = LoadBalancerDescription(
            availabilityZones = availabilityZones,
            backendServerDescriptions = backendServerDescriptions,
            canonicalHostedZoneName = findattr(element=element, xpath="CanonicalHostedZoneName", namespace=NAMESPACE),
            canonicalHostedZoneNameID = findattr(element=element, xpath="CanonicalHostedZoneNameID", namespace=NAMESPACE),
            createdTime = findattr(element=element, xpath="CreatedTime", namespace=NAMESPACE),
            dnsName = findattr(element=element, xpath="DNSName", namespace=NAMESPACE),
            healthCheck = healthCheckh,
            instances = instances,
            listenerDescriptions = listenerDescriptions,
            loadBalancerName = findattr(element=element, xpath="LoadBalancerName", namespace=NAMESPACE),
            policies = policies,
            sourceSecurityGroup = sourceSecurityGroups,
        )


        return boadBalancerDescription

    def _to_healthCheck (self,  element):
        n = HealthCheck(
            healthyThreshold = findattr(element=element, xpath="HealthyThreshold", namespace=NAMESPACE),
            interval = findattr(element=element, xpath="Interval", namespace=NAMESPACE),
            target = findattr(element=element, xpath="Target", namespace=NAMESPACE),
            timeout = findattr(element=element, xpath="Timeout", namespace=NAMESPACE),
            unhealthyThreshold = findattr(element=element, xpath="UnhealthyThreshold", namespace=NAMESPACE),
        )
        return n



    def _to_listener(self,  element):
        n = Listener(
            instancePort = findattr(element=element, xpath="InstancePort", namespace=NAMESPACE),
            instanceProtocol = findattr(element=element, xpath="InstanceProtocol", namespace=NAMESPACE),
            loadBalancerPort = findattr(element=element, xpath="LoadBalancerPort", namespace=NAMESPACE),
            protocol = findattr(element=element, xpath="Protocol", namespace=NAMESPACE),
        )
        return n

