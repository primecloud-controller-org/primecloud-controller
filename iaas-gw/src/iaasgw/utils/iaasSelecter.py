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
from iaasgw.controller.cloudStack.cloudStackController import \
    CloudStackController
from iaasgw.controller.ec2.ec2controller import EC2Controller
from iaasgw.db.mysqlConnector import MysqlConnector
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertyUtil import getPlatformProperty
from sqlalchemy.sql.expression import and_


def iaasSelect(user, platformNo, isLb = False):
    logger = IaasLogger()
    conn = MysqlConnector(user)

    '''プラットフォームを判断しユーザー情報を取得する
    '''
    platforminfo = getPlatformProperty(platformNo)
    platformType =  platforminfo["platformType"]
    platformName =  platforminfo["platformName"]

    logger.info(u"      Platform::No[%s]::種別[%s]::名称[%s]" % (str(platformNo), platformType, platformName))

    #ユーザー取得
    userTable = conn.getTable("USER")
    userinfo = conn.selectOne(userTable.select(userTable.c.USER_NO==user))


    iaasController = None
    if platformType == "aws":
        if platformName == "eucalyptus":
            return
        #EC2 およびユーカリ
        #AWS_CERTIFICATE 取得
        certificateTable = conn.getTable("AWS_CERTIFICATE")
        certificate = conn.selectOne(certificateTable.select(and_(certificateTable.c.USER_NO==user,  certificateTable.c.PLATFORM_NO==platformNo)))
        accessInfo = {"ACCESS_ID": str(certificate["AWS_ACCESS_ID"]), "SECRET_KEY": str(certificate["AWS_SECRET_KEY"]), "USER": user, "USER_NAME": userinfo["USERNAME"]}
        iaasController = EC2Controller(conn, accessInfo, platforminfo, isLb)
    elif platformType == "vmware":
        #vmware
        pass
    elif platformType == "nifty":
        #nifty
        pass
    elif platformType == "cloudstack":
        #CloudStack
        certificateTable = conn.getTable("CLOUDSTACK_CERTIFICATE")
        certificate = conn.selectOne(certificateTable.select(and_(certificateTable.c.ACCOUNT==user, certificateTable.c.PLATFORM_NO==platformNo)))
        accessInfo = {"ACCESS_ID": str(certificate["CLOUDSTACK_ACCESS_ID"]), "SECRET_KEY": str(certificate["CLOUDSTACK_SECRET_KEY"]), "USER": user, "USER_NAME": userinfo["USERNAME"]}
        iaasController = CloudStackController(conn, accessInfo, platforminfo)
    else:
        raise IaasException("PlatformError", platformNo)

    return iaasController

