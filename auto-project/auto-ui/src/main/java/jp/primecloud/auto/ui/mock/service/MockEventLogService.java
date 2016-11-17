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
package jp.primecloud.auto.ui.mock.service;

import java.util.List;

import jp.primecloud.auto.log.dao.crud.EventLogDao.SearchCondition;
import jp.primecloud.auto.log.entity.crud.EventLog;
import jp.primecloud.auto.log.service.EventLogService;
import jp.primecloud.auto.ui.mock.MockLogData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockEventLogService implements EventLogService {

    public static final int limit = 1000;

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public List<EventLog> readBySearchCondition(SearchCondition searchCondition) {
        MockLogData mockLogData = new MockLogData(1000);

        log.info("fromDate: " + searchCondition.getFromDate() + ", toDate: " + searchCondition.getToDate()
                + ", userNo: " + searchCondition.getUserNo() + ", FarmNo: " + searchCondition.getFarmNo()
                + " ,componentNo:" + searchCondition.getComponentNo() + ", instanceNo:"
                + searchCondition.getInstanceNo() + ", logLevel:" + searchCondition.getLogLevel());

        return mockLogData.getEventLogs();
    }

}
