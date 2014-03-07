/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.log.service.impl;



import java.util.Date;

import jp.primecloud.auto.log.dao.crud.OperationLogDao;
import jp.primecloud.auto.log.entity.crud.OperationLog;
import jp.primecloud.auto.log.service.EventLogService;
import jp.primecloud.auto.log.service.OperationLogService;


/**
 * <p>
 * {@link EventLogService}の実装クラスです。
 * </p>
 *
 */
public class OperationLogServiceImpl implements OperationLogService {

    protected OperationLogDao operationLogDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeOperationLog(Long userNo,
                                  String userName,
                                  Long farmNo,
                                  String screen,
                                  String operation,
                                  Long instanceNo,
                                  Long componentNo,
                                  Long loadBalancerNo,
                                  String memo) {
        // オペレーションログの出力
        OperationLog operationLog = new OperationLog();
        operationLog.setOLogDate(new Date());
        operationLog.setUserNo(userNo);
        operationLog.setUserName(userName);
        operationLog.setScreen(screen);
        operationLog.setOperation(operation);
        operationLog.setFarmNo(farmNo);
        operationLog.setInstanceNo(instanceNo);
        operationLog.setComponentNo(componentNo);
        operationLog.setLoadBalancerNo(loadBalancerNo);
        operationLog.setMemo(memo);

        operationLogDao.create(operationLog);
    }

    /**
     * operationLogDaoを設定します。
     *
     * @param operationLogDao operationLogDao
     */
    public void setOperationLogDao(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }


}
