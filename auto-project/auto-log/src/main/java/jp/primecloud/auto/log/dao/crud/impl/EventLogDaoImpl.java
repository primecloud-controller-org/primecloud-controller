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
package jp.primecloud.auto.log.dao.crud.impl;

import java.util.List;

import jp.primecloud.auto.log.dao.crud.EventLogDao;
import jp.primecloud.auto.log.entity.crud.EventLog;


/**
 * <p>
 * {@link EventLogDao}の実装クラスです。
 * </p>
 *
 */
public class EventLogDaoImpl extends BaseEventLogDaoImpl implements EventLogDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<EventLog> readBySearchCondition(SearchCondition searchCondition) {
        return (List<EventLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readBySearchCondition"),
                searchCondition);
    }

    @Override
    public long countBySearchCondition(SearchCondition searchCondition) {
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countBySearchCondition"), searchCondition);
    }

}
