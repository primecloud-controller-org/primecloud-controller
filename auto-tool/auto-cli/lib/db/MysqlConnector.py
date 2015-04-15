# -*- coding: utf-8 -*-

from sqlalchemy.engine import create_engine
from sqlalchemy.orm.session import sessionmaker
from sqlalchemy.schema import MetaData, Table
from sqlalchemy.sql import func
from urlparse import urlparse
import MysqlConnector
import sys

def readProperties(name):
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
            host = url.netloc.strip(':' + str(url.port))
            port = url.port
            dbname = url.path.replace("/", "")
        elif "db.username" in line:
            user = line.split("=")[1].strip().rstrip("\n")
        elif "db.password" in line:
            pswd = line.split("=")[1].strip().rstrip("\n")
    
    if "user" == name:
        return user
    elif "pswd" == name:
        return pswd
    elif "host" == name:
        return host
    elif "dbname" == name:
        return dbname

#接続情報
user = readProperties("user")
pswd = readProperties("pswd")
host = readProperties("host")
dbname = readProperties("dbname")

connectinfo = 'mysql+mysqlconnector://' + user +  ':' + pswd +'@'+ host + '/' + dbname
connectinfo2 = 'mysql+mysqlconnector://' + user +  ':' + pswd +'@'+ host + '/' + dbname +'_log'
ENGINE = create_engine(connectinfo, encoding='utf-8', pool_size=20, max_overflow=0)
METADATA = MetaData()
METADATA.bind = ENGINE

Session = sessionmaker(bind=ENGINE)

class MysqlConnector:
    session = None
    
    def __init__(self):
        self.session = Session()

    def getTable(self, tableName):
        table_object = Table(tableName, METADATA, autoload=True)
        return table_object

    def getMax(self, tableColumn):
        max_object = func.max(tableColumn)
        return max_object

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