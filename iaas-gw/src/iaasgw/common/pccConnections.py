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
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertyUtil import getProxy
from libcloud.common.base import ConnectionUserAndKey
from libcloud.common.cloudstack import CloudStackConnection
from libcloud.compute.drivers.ec2 import EC2Connection, EC2_EU_WEST_HOST, \
    EC2_US_WEST_HOST, EC2_US_WEST_OREGON_HOST, EC2_AP_SOUTHEAST_HOST, \
    EC2_AP_NORTHEAST_HOST, EC2Response
from libcloud.compute.drivers.vcloud import VCloudConnection
import base64
import hmac
import time
import urllib

proxyinfo = getProxy()
proxy_host = ""
proxy_port = ""
if "host" in proxyinfo:
    proxy_host = proxyinfo["host"]
if "port" in proxyinfo:
    proxy_port = proxyinfo["port"]


#EC2 ロードバランサ用の設定
API_VERSION = '2011-11-15'
NAMESPACE = 'http://elasticloadbalancing.amazonaws.com/doc/%s/' % API_VERSION
LBHOSTS = {"NORTHEAST":'elasticloadbalancing.ap-northeast-1.amazonaws.com',
         "US_EAST":'elasticloadbalancing.us-east-1.amazonaws.com',
         "EU_WEST":'elasticloadbalancing.eu-west-1.amazonaws.com',
         "US_WEST":'elasticloadbalancing.us-west-1.amazonaws.com',
         "US_WEST_OREGON":'elasticloadbalancing.us-west-2.amazonaws.com',
         "SOUTHEAST":'elasticloadbalancing.ap-southeast-1.amazonaws.com',
         }

#EC2用カスタムコネクション
class PCCEC2Connection(EC2Connection):
    logger = IaasLogger()
    useProxy = False

    def connect(self, host=None, port=None, base_url = None):
        # prefer the attribute base_url if its set or sent
        connection = None
        secure = self.secure

        if getattr(self, 'base_url', None) and base_url == None:
            (host, port, secure, self.request_path) = self._tuple_from_url(self.base_url)
        elif base_url != None:
            (host, port, secure, self.request_path) = self._tuple_from_url(base_url)
        else:
            host = host or self.host
            port = port or self.port

        kwargs = {'host': host, 'port': int(port)}

        if not self.useProxy:
            #self.logger.debug("**************** OFF PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        elif proxy_port is None or proxy_port == "":
            #self.logger.debug("**************** NON PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        else:
            #self.logger.debug("**************** ON PROXY !")
            connection = self.conn_classes[False](proxy_host, proxy_port)

        self.connection = connection

#EC2ロードバランサ用カスタムコネクション
class EC2LBConnectionUSE(ConnectionUserAndKey):
    logger = IaasLogger()
    responseCls = EC2Response
    useProxy = False
    host = LBHOSTS["US_EAST"]

    def __init__(self, user_id, key, secure=True, host=None, port=None, url=None):
        super(EC2LBConnectionUSE, self).__init__(user_id, key, secure=secure, host=host, port=port, url=url)

    def connect(self, host=None, port=None, base_url = None):
        # prefer the attribute base_url if its set or sent
        connection = None
        secure = self.secure

        if getattr(self, 'base_url', None) and base_url == None:
            (host, port, secure, self.request_path) = self._tuple_from_url(self.base_url)
        elif base_url != None:
            (host, port, secure, self.request_path) = self._tuple_from_url(base_url)
        else:
            host = host or self.host
            port = port or self.port

        kwargs = {'host': host, 'port': int(port)}

        if not self.useProxy:
            #self.logger.debug("**************** OFF PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        elif proxy_port is None or proxy_port == "":
            #self.logger.debug("**************** NON PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        else:
            #self.logger.debug("**************** ON PROXY !")
            connection = self.conn_classes[False](proxy_host, proxy_port)

        self.connection = connection

    def add_default_params(self, params):
        params['SignatureVersion'] = '2'
        params['SignatureMethod'] = 'HmacSHA1'
        params['AWSAccessKeyId'] = self.user_id
        params['Version'] = API_VERSION
        params['Timestamp'] = time.strftime('%Y-%m-%dT%H:%M:%SZ', time.gmtime())
        params['Signature'] = self._get_aws_auth_param(params, self.key, self.action)
        return params

    def _get_aws_auth_param(self, params, secret_key, path='/'):
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

#クラウドスタック用カスタムコネクション
class PCCCloudStackConnection(CloudStackConnection):
    logger = IaasLogger()
    useProxy = False
    def connect(self, host=None, port=None, base_url = None):
        # prefer the attribute base_url if its set or sent
        connection = None
        secure = self.secure

        if getattr(self, 'base_url', None) and base_url == None:
            (host, port, secure, self.request_path) = self._tuple_from_url(self.base_url)
        elif base_url != None:
            (host, port, secure, self.request_path) = self._tuple_from_url(base_url)
        else:
            host = host or self.host
            port = port or self.port

        kwargs = {'host': host, 'port': int(port)}
        if not self.useProxy:
            #self.logger.debug("**************** OFF PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        elif proxy_port is None or proxy_port == "":
            #self.logger.debug("**************** NON PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        else:
            #self.logger.debug("**************** ON PROXY !")
            connection = self.conn_classes[False](proxy_host, proxy_port)

        self.connection = connection

#VCloud用カスタムコネクション
class PCCVCloudConnection(VCloudConnection):
    logger = IaasLogger()
    useProxy = False
    def connect(self, host=None, port=None, base_url = None):
        # prefer the attribute base_url if its set or sent
        connection = None
        secure = self.secure

        if getattr(self, 'base_url', None) and base_url == None:
            (host, port, secure, self.request_path) = self._tuple_from_url(self.base_url)
        elif base_url != None:
            (host, port, secure, self.request_path) = self._tuple_from_url(base_url)
        else:
            host = host or self.host
            port = port or self.port

        kwargs = {'host': host, 'port': int(port)}
        if not self.useProxy:
            #self.logger.debug("**************** OFF PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        elif proxy_port is None or proxy_port == "":
            #self.logger.debug("**************** NON PROXY !")
            connection = self.conn_classes[secure](**kwargs)
        else:
            #self.logger.debug("**************** ON PROXY !")
            connection = self.conn_classes[False](proxy_host, proxy_port)

        self.connection = connection



############################################################################################################
#
# EC2リュージョン別コネクション
#
############################################################################################################

class PCCEC2EUConnection(PCCEC2Connection):
    """
    Connection class for EC2 in the Western Europe Region
    """
    host = EC2_EU_WEST_HOST

class PCCEC2USWestConnection(PCCEC2Connection):
    """
    Connection class for EC2 in the Western US Region
    """

    host = EC2_US_WEST_HOST


class PCCEC2USWestOregonConnection(PCCEC2Connection):
    """
    Connection class for EC2 in the Western US Region (Oregon).
    """

    host = EC2_US_WEST_OREGON_HOST


class PCCEC2APSEConnection(PCCEC2Connection):
    """
    Connection class for EC2 in the Southeast Asia Pacific Region
    """

    host = EC2_AP_SOUTHEAST_HOST


class PCCEC2APNEConnection(PCCEC2Connection):
    """
    Connection class for EC2 in the Northeast Asia Pacific Region
    """

    host = EC2_AP_NORTHEAST_HOST

class PCCEucConnection(PCCEC2Connection):
    """
    Connection class for Eucalyptus
    """

    host = None

############################################################################################################
#
# EC2LBリュージョン別コネクション
#
############################################################################################################

class EC2LBConnectionEUW(EC2LBConnectionUSE):
    host = LBHOSTS["EU_WEST"]

class EC2LBConnectionUSW(EC2LBConnectionUSE):
    host = LBHOSTS["US_WEST"]

class EC2LBConnectionUSWO(EC2LBConnectionUSE):
    host = LBHOSTS["US_WEST_OREGON"]

class EC2LBConnectionSE(EC2LBConnectionUSE):
    host = LBHOSTS["SOUTHEAST"]

class EC2LBConnectionNE(EC2LBConnectionUSE):
    host = LBHOSTS["NORTHEAST"]

