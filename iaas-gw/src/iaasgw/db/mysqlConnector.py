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
from iaasgw.utils.massageUtil import getMassage
from iaasgw.utils.readIniFile import readIni
from sqlalchemy.engine import create_engine
from sqlalchemy.orm.session import sessionmaker
from sqlalchemy.schema import MetaData, Table
from xmlrpclib import datetime

#接続情報
user = readIni("MYSQL","USER")
pswd = readIni("MYSQL","PASS")
url = readIni("MYSQL","URL")
dbname = readIni("MYSQL","DBNAME")


connectinfo = 'mysql+mysqlconnector://' + user +  ':' + pswd +'@'+ url + '/' + dbname
connectinfo2 = 'mysql+mysqlconnector://' + user +  ':' + pswd +'@'+ url + '/' + dbname +'_log'
#engine = create_engine('mysql://user:pass@***.***.***.***/dbname', encoding='utf-8')
ENGINE = create_engine(connectinfo, encoding='utf-8', pool_size=20, max_overflow=0)
METADATA = MetaData()
METADATA.bind = ENGINE
Session = sessionmaker(bind=ENGINE)

ENGINE2 = create_engine(connectinfo2, encoding='utf-8', pool_size=20, max_overflow=0)
METADATA2 = MetaData()
METADATA2.bind = ENGINE2

class MysqlConnector(object):
    logger = IaasLogger()
    session = None
    userNo = None

    LOGLEVEL = {"OFF":100,
                "ERROR":40,
                "WARN":30,
                "INFO":20,
                "DEBUG":10,
                "ALL":100,}


    def __init__(self, userNo=None):
        #self.logger.info(connectinfo)
        self.session = Session()
        self.userNo = userNo


    def getTable(self, tableName):
        table_object = Table(tableName, METADATA, autoload=True)
        return table_object

    def execute(self, sql, autocommit = False):
        res = self.session.execute(sql)
        if autocommit == True:
            self.commit();

        return res.rowcount

    def selectOne(self, sql):
        res = self.select(sql)
        if res:
            return res[0]
        else:
            return None

    def select(self, sql):
        #res = sql.execute()
        res = self.session.execute(sql)
        values = res.fetchall()
        keys  = res.keys()

        rs = []
        for row in values:
            rec = {}
            for i, item in enumerate(keys):
                rec.update({item:row[i]})
            rs.append(rec)

        return rs

    def rollback(self):
        return self.session.rollback()

    def commit(self):
        return self.session.commit()

    def close(self):
        self.session.close()

    #基本的に利用しないでください
    def remakeSession(self):
        self.session.close()
        self.session = Session()

    #####################################################
    #
    #以下イベントログ登録
    #
    #####################################################
    def error(self, farmNo, componentNo, componentName, instanceNo, instanceName, code, additions) :
        self.beforOutLog("ERROR", farmNo, componentNo, componentName, instanceNo, instanceName, code, additions)

    def warn(self, farmNo, componentNo, componentName, instanceNo, instanceName, code, additions) :
        self.beforOutLog("WARN", farmNo, componentNo, componentName, instanceNo, instanceName, code, additions)

    def info(self, farmNo, componentNo, componentName, instanceNo, instanceName, code, additions) :
        self.beforOutLog("INFO", farmNo, componentNo, componentName, instanceNo, instanceName, code, additions)

    def debug(self, farmNo, componentNo, componentName, instanceNo, instanceName, code, additions) :
        self.beforOutLog("DEBUG", farmNo, componentNo, componentName, instanceNo, instanceName, code, additions)

    def beforOutLog(self, logLevel, farmNo, componentNo, componentName, instanceNo, instanceName, code, additions):


        farmT = Table("FARM", METADATA, autoload=True)
        farm = self.selectOne(farmT.select(farmT.c.FARM_NO==farmNo))
        farmName = None
        if farm is not None:
            farmName = farm["FARM_NAME"]

        userT = Table("USER", METADATA, autoload=True)
        user = self.selectOne(userT.select(userT.c.USER_NO==self.userNo))
        userName = None
        if user is not None:
            userName = user["USERNAME"]

        instanceType = None
        platformNo = None
        if instanceNo is not None:
            instanceT = Table("INSTANCE", METADATA, autoload=True)
            instance = self.selectOne(instanceT.select(instanceT.c.INSTANCE_NO==instanceNo))
            if instance is not None:
                platformNo = instance["PLATFORM_NO"]

            isSelect = True
            if isSelect:
                awsinstanceT = Table("AWS_INSTANCE", METADATA, autoload=True)
                awsinstance = self.selectOne(awsinstanceT.select(awsinstanceT.c.INSTANCE_NO==instanceNo))
                if awsinstance is not None:
                    instanceType = awsinstance["INSTANCE_TYPE"]
                    isSelect = False

            if isSelect:
                csinstanceT = Table("CLOUDSTACK_INSTANCE", METADATA, autoload=True)
                csinstance = self.selectOne(csinstanceT.select(csinstanceT.c.INSTANCE_NO==instanceNo))
                if csinstance is not None:
                    instanceType = csinstance["INSTANCE_TYPE"]
                    isSelect = False

            if isSelect:
                vminstanceT = Table("VMWARE_INSTANCE", METADATA, autoload=True)
                vminstance = self.selectOne(vminstanceT.select(vminstanceT.c.INSTANCE_NO==instanceNo))
                if vminstance is not None:
                    instanceType = vminstance["INSTANCE_TYPE"]
                    isSelect = False

            if isSelect:
                nifinstanceT = Table("NIFTY_INSTANCE", METADATA, autoload=True)
                nifinstance = self.selectOne(nifinstanceT.select(nifinstanceT.c.INSTANCE_NO==instanceNo))
                if nifinstance is not None:
                    instanceType = nifinstance["INSTANCE_TYPE"]
                    isSelect = False


        self.outLog(logLevel, self.userNo, userName, farmNo, farmName, componentNo, componentName, instanceNo, instanceName, code, instanceType,  platformNo, additions)

    def outLog(self,logLevel, userNo, userName, farmNo, farmName, componentNo, componentName, instanceNo, instanceName, code, instanceType,  platformNo, additions):
        # イベントログメッセージの取得
        message = getMassage(code, additions)

        log_table = Table("EVENT_LOG", METADATA2, autoload=True)
        log_table.insert({"LOG_NO":None,
                          "LOG_DATE":datetime.datetime.today(),
                          "LOG_LEVEL":self.LOGLEVEL[logLevel],
                          "USER_NO":userNo,
                          "USER_NAME":userName,
                          "FARM_NO":farmNo,
                          "FARM_NAME":farmName,
                          "COMPONENT_NO":componentNo,
                          "COMPONENT_NAME":componentName,
                          "INSTANCE_NO":instanceNo,
                          "INSTANCE_NAME":instanceName,
                          "MESSAGE_CODE":code,
                          "MESSAGE":message,
                          "INSTANCE_TYPE":instanceType,
                          "PLATFORM_NO":platformNo}).execute()

