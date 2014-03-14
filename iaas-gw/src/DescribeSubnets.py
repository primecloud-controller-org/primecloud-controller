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
from iaasgw.utils.iaasSelecter import iaasSelect
import os
import sys
import traceback

if __name__ == '__main__':
    param = sys.argv
    logger = IaasLogger()

    #ログ用パラメータ
    logparam = ["DescribeSubnets",os.getpid(), "無し"]
    logger.start(logparam)

    #実行
    try:
        #パラメータ解説
        #  0.ファイル名
        #  1.ユーザー名
        #  2.プラットフォームNo
        #  3.VPCID
        #
        # 例：param = [None, "1", "6", "1",]
        iaasController = iaasSelect(param[1], param[2])
        if iaasController == None:
            sys.exit()

        vpcid = None
        if len(param) == 4:
            vpcid = param[3]

        subnets = iaasController.describeSubnets(vpcid)
        print subnets
    except:
        logger.error(traceback.format_exc())
        raise
    logger.end(logparam)

