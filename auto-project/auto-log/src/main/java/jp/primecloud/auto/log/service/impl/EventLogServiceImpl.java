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

import java.util.List;

import jp.primecloud.auto.log.dao.crud.EventLogDao;
import jp.primecloud.auto.log.dao.crud.EventLogDao.SearchCondition;
import jp.primecloud.auto.log.entity.crud.EventLog;
import jp.primecloud.auto.log.service.EventLogService;

/**
 * <p>
 * {@link EventLogService}の実装クラスです。
 * </p>
 *
 */
public class EventLogServiceImpl implements EventLogService {

    protected EventLogDao eventLogDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventLog> readBySearchCondition(SearchCondition searchCondition) {
        List<EventLog> eventLogs = eventLogDao.readBySearchCondition(searchCondition);
        return eventLogs;
    }

    /**
     * eventLogDaoを設定します。
     *
     * @param eventLogDao eventLogDao
     */
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }

}
