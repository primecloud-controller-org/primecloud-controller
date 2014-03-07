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
from iaasgw.utils.massageUtil import getMassage
from iaasgw.utils.stringUtils import isNotEmpty
import logging.config
import os

#IaasGatewayルート
root = os.environ.get('IAASGW_HOME', "")
LOGGING_CONF = root + '/log.conf' # 今回使用する設定ファイルパスを指定します。
logging.config.fileConfig(LOGGING_CONF) # 設定ファイルをセットします。
logger = logging.getLogger("app") # ロガーのkeyをappにします。(後述)

class IaasLogger(object):
    def __init__(self):
        pass

    def start(self, *args):
        logger.info(getMassage("IaasGatewayStart", *args))
    def end(self, *args):
        logger.info(getMassage("IaasGatewayEnd", *args))
    def debug(self, str, messid = None, *args):
        logger.debug(self.makeMassage(str, messid, *args))
    def info(self, str, messid = None, *args):
        logger.info(self.makeMassage(str, messid, *args))
    def warn(self, str, messid = None, *args):
        logger.warn(self.makeMassage(str, messid, *args))
    def error(self, str, messid = None, *args):
        logger.error(self.makeMassage(str, messid, *args))
    def critical(self, str, messid = None, *args):
        logger.critical(self.makeMassage(str, messid, *args))


    def makeMassage(self, str = None, messid = None, *args):
        if isNotEmpty(str):
            return str
        else:
            return getMassage(messid, *args)
