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
from iaasgw.db.mysqlConnector import MysqlConnector

if __name__ == '__main__':
    conn = MysqlConnector()
    #session = conn.getSession()
    table = conn.getTable("user")


    instanceId = "test"
    #AWS_INSTANCE 取得
    table = conn.getTable("AWS_INSTANCE")
    awsInstance = conn.selectOne(table.select(table.c.INSTANCE_NO==instanceId))

    #PCC_INSTANCE 取得
    table = conn.getTable("INSTANCE")
    pccInstance = conn.selectOne(table.select(table.c.INSTANCE_NO==instanceId))

    #イメージ取得   再考の余地あり


    print awsInstance
    print pccInstance



#    sel = table.select(table.c.USER_NO ==1234567890123456789)
#    res = conn.select(sel, True)
#    print u"結果"
#    print res
#    print res["USERNAME"]

    #table.insert(['1234567890123456780', 'TEST', '12345']).execute()

#    ins = table.update(table.c.USER_NO ==1234567890123456789, values={'USERNAME':'TEST5'})
#    print ins.compile().params
    #ins.execute()

#    conn.execute(ins, True)
#    conn.close()

    #session.execute(ins)
    #session.commit()

    #print session.query(table).all()


#    try:
#        ins = table.insert()
#        conn.execute(ins, USER_NO='1234567890123456789', USERNAME=u'若林', PASSWORD='12345')
#
#    except Exception, e:
#        print(e)

