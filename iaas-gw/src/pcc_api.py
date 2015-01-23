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

from iaasgw.log.log import IaasLogger
import base64
import hashlib
import hmac
import json
import logging
import sys
import urllib
import urllib2

logger = IaasLogger()

class PccAPIException(Exception):
    """ generic zabbix api exception
        code list:
        -32602 - Invalid params (eg already exists)
        -32500 - no permissions
    """
    pass


class Already_Exists(PccAPIException):
    pass


class InvalidProtoError(PccAPIException):
    """ Recived an invalid proto """
    pass


class PccAPI(object):
    __username__ = ''
    __password__ = ''

    auth = ''
    params = None
    method = None
    # HTTP or HTTPS
    proto = 'http'
    timeout = 10
    # sub-class instances.
    user = None
    passwd = None

    #接続情報
    url = None

    def __init__(self, server='http://localhost:8080/auto-api', servlet='rest', user=None, passwd=None, timeout=10, **kwargs):
        self.server = server
        self.url = server +'/'+ servlet
        self.proto = self.server.split("://")[0]
        self.timeout = timeout
        self.user = user
        self.passwd = passwd


    def do_request(self, params, resource='index'):
        headers = {'Content-Type': 'application/json'}
        callUrl = self.url + '/' + resource

        #callUrl = self.makeUrl(callUrl, params)

        request = urllib2.Request(url=callUrl, data=self.makeUrl(callUrl, params), headers=headers)

        if self.proto == "https":
            https_handler = urllib2.HTTPSHandler(debuglevel=0)
            opener = urllib2.build_opener(https_handler)
        elif self.proto == "http":
            http_handler = urllib2.HTTPHandler(debuglevel=0)
            opener = urllib2.build_opener(http_handler)
        else:
            raise PccAPIException("Unknow protocol %s" % self.proto)

        urllib2.install_opener(opener)
        response = opener.open(request, timeout=self.timeout)
        self.debug(logging.INFO, "Response Code: " + str(response.code))

        if response.code != 200:
            raise PccAPIException("HTTP ERROR %s: %s" % (response.status, response.reason))

        reads = response.read()
        if len(reads) == 0:
            raise PccAPIException("Received zero answer")
        try:
            jobj = json.loads(reads.decode('utf-8'))
        except ValueError as msg:
            print ("unable to decode. returned string: %s" % reads)
            sys.exit(-1)

        self.id += 1

        if 'error' in jobj:
            raise PccAPIException(msg, jobj['error']['code'])

        return jobj


    def makeUrl(self, callUrl, parames):
        paramString = ''
        for i, key in enumerate(parames.keys(), 0):
            print i, key
            if i > 0:
                paramString = paramString + '&'
            paramString = paramString + key + '=' + parames[key]

        print "paramString:::", paramString
        completeUrl = callUrl + '?' + paramString
        print "completeUrl:::", completeUrl
        signature = hmac.new(self.passwd, completeUrl, hashlib.sha256).hexdigest()
        print "signature:::", signature


        paramString = paramString + '&Signature=' + signature
        print "paramString2:::", paramString
        print "paramString2:::", base64.encodestring(paramString)

        return base64.encodestring(paramString)

        #completeUrl = callUrl + '?' + base64.encodestring(paramString)
        #print "completeUrl:::", completeUrl
        #return completeUrl


