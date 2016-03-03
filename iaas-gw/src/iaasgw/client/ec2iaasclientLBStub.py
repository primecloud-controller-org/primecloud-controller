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
from hashlib import sha1
from iaasgw.exception.iaasException import IaasException
from iaasgw.module.ec2.ec2module import InstanceState, BackendServerDescription, \
    ListenerDescription, LBCookieStickinessPolicy, Policies, SourceSecurityGroup, \
    LoadBalancerDescription, HealthCheck, Listener, AppCookieStickinessPolicy
from libcloud.common.base import ConnectionUserAndKey
from libcloud.compute.drivers.ec2 import EC2APNENodeDriver, EC2Response
from libcloud.utils import findall, findattr
import base64
import hmac
import time
import urllib

API_VERSION = '2011-08-15'
NAMESPACE = 'http://elasticloadbalancing.amazonaws.com/doc/%s/' % API_VERSION

class EC2LBConnection(ConnectionUserAndKey):
    """
    Repersents a single connection to the EC2 Endpoint
    """

    host = 'elasticloadbalancing.us-east-1.amazonaws.com'
    responseCls = EC2Response

    def add_default_params(self, params):
        params['SignatureVersion'] = '2'
        params['SignatureMethod'] = 'HmacSHA1'
        params['AWSAccessKeyId'] = self.user_id
        params['Version'] = API_VERSION
        params['Timestamp'] = time.strftime('%Y-%m-%dT%H:%M:%SZ', time.gmtime())
        params['Signature'] = self._get_aws_auth_param(params, self.key, self.action)
        return params

    def _get_aws_auth_param(self, params, secret_key, path='/'):
        """
        Creates the signature required for AWS, per
        http://bit.ly/aR7GaQ [docs.amazonwebservices.com]:

        StringToSign = HTTPVerb + "\n" +
                       ValueOfHostHeaderInLowercase + "\n" +
                       HTTPRequestURI + "\n" +
                       CanonicalizedQueryString <from the preceding step>
        """
        keys = params.keys()
        keys.sort()
        pairs = []
        for key in keys:
            pairs.append(urllib.quote(key, safe='') + '=' +
                         urllib.quote(params[key], safe='-_~'))

        qs = '&'.join(pairs)
        string_to_sign = '\n'.join(('GET', self.host, path, qs))

        b64_hmac = base64.b64encode(
            hmac.new(secret_key, string_to_sign, digestmod=sha1).digest()
        )
        return b64_hmac

class EC2APNEConnectionLB(EC2LBConnection):
    host = 'elasticloadbalancing.ap-northeast-1.amazonaws.com'

class EC2APNENodeDriverLB(EC2APNENodeDriver):
    connectionCls = EC2APNEConnectionLB

class EC2IaasClientLB(EC2APNENodeDriverLB):


    debugout = True

    ############################################################
    #
    #    基礎データ
    #
    ############################################################
    def getPlatformNo(self):
        return self.platformNo

    def setPlatformNo(self, platformNo):
        self.platformNo = platformNo

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
        return []

    def describeInstanceHealth(self, loadBalancerName, instances = None):
        params = {'Action': 'DescribeInstanceHealth', 'LoadBalancerName':loadBalancerName}
        #任意パラメータ
        if instances != None:
            for i, instance in enumerate(instances,1):
                params['Instances.member.%d.InstanceId' % i] = instance.id

        elem = self.connection.request(self.path, params=params).object

        return None


    ############################################################
    #
    #    LoadBalancer 操作系
    #
    ############################################################

    def createLoadBalancer(self, availabilityZones, listeners, loadBalancerName ):

        params = {'Action': 'CreateLoadBalancer', 'LoadBalancerName' : loadBalancerName}
        for i,listener in enumerate(listeners, 1):
            params['Listeners.member.%d.LoadBalancerPort' % i] = listener.loadBalancerPort
            params['Listeners.member.%d.InstancePort' % i] = listener.instancePort
            params['Listeners.member.%d.Protocol' % i] = listener.protocol
            if listener.protocol=='HTTPS' or listener.protocol=='SSL':
                params['Listeners.member.%d.SSLCertificateId' % i] = listener.sslCertificateId

        for j, availabilityZone in enumerate(availabilityZones,1):
            params['AvailabilityZones.member.%d' % j] = availabilityZone.name

        return u"dummy"


    def deleteLoadBalancer(self, loadBalancerName):

        params = {'Action': 'DeleteLoadBalancer', 'LoadBalancerName':loadBalancerName}


    def configureHealthCheck(self, healthCheck, loadBalancerName):
        params = {'Action': 'ConfigureHealthCheck',  'LoadBalancerName':loadBalancerName}
        params["HealthCheck.Timeout"] = healthCheck.timeout
        params["HealthCheck.Target"] = healthCheck.target
        params["HealthCheck.Interval"] = healthCheck.interval
        params["HealthCheck.UnhealthyThreshold"] = healthCheck.unhealthyThreshold
        params["HealthCheck.HealthyThreshold"] = healthCheck.healthyThreshold


        return None

    def createLoadBalancerListeners(self, listeners, loadBalancerName):
        params = {'Action': 'CreateLoadBalancerListeners', 'LoadBalancerName':loadBalancerName}
        for i,listener in enumerate(listeners, 1):
            params['Listeners.member.%d.LoadBalancerPort' % i] = listener.loadBalancerPort
            params['Listeners.member.%d.InstancePort' % i] = listener.instancePort
            params['Listeners.member.%d.Protocol' % i] = listener.protocol
            if listener.protocol=='HTTPS' or listener.protocol=='SSL':
                params['Listeners.member.%d.SSLCertificateId' % i] = listener.sslCertificateId



    def deleteLoadBalancerListeners(self, loadBalancerPorts, loadBalancerName):
        params = {'Action': 'DeleteLoadBalancerListeners', 'LoadBalancerName':loadBalancerName}
        for i, loadBalancerPort in enumerate(loadBalancerPorts, 1):
            params['LoadBalancerPorts.member.%d' % i] = loadBalancerPort


    def registerInstancesWithLoadBalancer(self, instances, loadBalancerName):
        params = {'Action': 'RegisterInstancesWithLoadBalancer', 'LoadBalancerName':loadBalancerName}

        for i, instance in enumerate(instances,1):
            #params['Instances.member.%d.InstanceId' % i] = instance.id
            params['Instances.member.%d.InstanceId' % i] = instance

        return None


    def deregisterInstancesFromLoadBalancer(self, instances, loadBalancerName):
        params = {'Action': 'DeregisterInstancesFromLoadBalancer', 'LoadBalancerName':loadBalancerName}

        for i, instance in enumerate(instances,1):
            #params['Instances.member.%d.InstanceId' % i] = instance.id
            params['Instances.member.%d.InstanceId' % i] = instance


        return None





