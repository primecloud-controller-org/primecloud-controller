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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.log.dao.crud.BaseEventLogDao;
import jp.primecloud.auto.log.entity.crud.EventLog;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseEventLogDao}の実装クラスです。
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public abstract class BaseEventLogDaoImpl extends SqlMapClientDaoSupport implements BaseEventLogDao {

    protected String namespace = "EventLog";

    /**
     * {@inheritDoc}
     */
    @Override
    public EventLog read(
            Long logNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logNo", logNo);
        return (EventLog) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<EventLog> readAll() {
        return (List<EventLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<EventLog> readByLogDate(
            java.util.Date logDate
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logDate", logDate);
        return (List<EventLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByLogDate"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<EventLog> readByLogDateAndUserNo(
            java.util.Date logDate, 
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logDate", logDate);
        paramMap.put("userNo", userNo);
        return (List<EventLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByLogDateAndUserNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<EventLog> readInLogNos(
            Collection<Long> logNos
        ) {
        if (logNos.isEmpty()) {
            return new ArrayList<EventLog>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (logNos instanceof List) {
            paramMap.put("logNos", logNos);
        } else {
            paramMap.put("logNos", new ArrayList<Long>(logNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<EventLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInLogNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(EventLog entity) {
        String id = "create";
        if (entity.getLogNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(EventLog entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(EventLog entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("delete"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        getSqlMapClientTemplate().delete(getSqlMapId("deleteAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByLogNo(
            Long logNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logNo", logNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByLogNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByLogDate(
            java.util.Date logDate
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logDate", logDate);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByLogDate"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByLogDateAndUserNo(
            java.util.Date logDate, 
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logDate", logDate);
        paramMap.put("userNo", userNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByLogDateAndUserNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countAll() {
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByLogNo(
            Long logNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logNo", logNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByLogNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByLogDate(
            java.util.Date logDate
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logDate", logDate);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByLogDate"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByLogDateAndUserNo(
            java.util.Date logDate, 
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("logDate", logDate);
        paramMap.put("userNo", userNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByLogDateAndUserNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
