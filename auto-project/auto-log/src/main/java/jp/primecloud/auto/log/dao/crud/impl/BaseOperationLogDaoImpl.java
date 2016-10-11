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

import jp.primecloud.auto.log.dao.crud.BaseOperationLogDao;
import jp.primecloud.auto.log.entity.crud.OperationLog;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;


/**
 * <p>
 * {@link BaseOperationLogDao}の実装クラスです。
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public abstract class BaseOperationLogDaoImpl extends SqlMapClientDaoSupport implements BaseOperationLogDao {

    protected String namespace = "OperationLog";

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationLog read(
            Long OLogNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogNo", OLogNo);
        return (OperationLog) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<OperationLog> readAll() {
        return (List<OperationLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<OperationLog> readByOLogDate(
            java.util.Date OLogDate
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogDate", OLogDate);
        return (List<OperationLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByOLogDate"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<OperationLog> readByOLogDateAndUserNo(
            java.util.Date OLogDate,
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogDate", OLogDate);
        paramMap.put("userNo", userNo);
        return (List<OperationLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByOLogDateAndUserNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<OperationLog> readInOLogNos(
            Collection<Long> OLogNos
        ) {
        if (OLogNos.isEmpty()) {
            return new ArrayList<OperationLog>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (OLogNos instanceof List) {
            paramMap.put("OLogNos", OLogNos);
        } else {
            paramMap.put("OLogNos", new ArrayList<Long>(OLogNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<OperationLog>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInOLogNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(OperationLog entity) {
        String id = "create";
        if (entity.getOLogNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(OperationLog entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(OperationLog entity) {
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
    public void deleteByOLogNo(
            Long OLogNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogNo", OLogNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByOLogNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByOLogDate(
            java.util.Date OLogDate
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogDate", OLogDate);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByOLogDate"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByOLogDateAndUserNo(
            java.util.Date OLogDate,
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogDate", OLogDate);
        paramMap.put("userNo", userNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByOLogDateAndUserNo"), paramMap);
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
    public long countByOLogNo(
            Long OLogNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogNo", OLogNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByOLogNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByOLogDate(
            java.util.Date OLogDate
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogDate", OLogDate);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByOLogDate"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByOLogDateAndUserNo(
            java.util.Date OLogDate,
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("OLogDate", OLogDate);
        paramMap.put("userNo", userNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByOLogDateAndUserNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
