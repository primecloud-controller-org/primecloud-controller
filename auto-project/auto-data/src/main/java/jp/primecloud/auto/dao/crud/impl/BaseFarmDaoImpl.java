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
package jp.primecloud.auto.dao.crud.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import jp.primecloud.auto.dao.crud.BaseFarmDao;
import jp.primecloud.auto.entity.crud.Farm;

/**
 * <p>
 * {@link BaseFarmDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseFarmDaoImpl extends SqlMapClientDaoSupport implements BaseFarmDao {

    protected String namespace = "Farm";

    /**
     * {@inheritDoc}
     */
    @Override
    public Farm read(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        return (Farm) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Farm> readAll() {
        return (List<Farm>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Farm readByFarmName(
            String farmName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmName", farmName);
        return (Farm) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByFarmName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Farm> readByUserNo(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        return (List<Farm>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByUserNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Farm> readInFarmNos(
            Collection<Long> farmNos
        ) {
        if (farmNos.isEmpty()) {
            return new ArrayList<Farm>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (farmNos instanceof List) {
            paramMap.put("farmNos", farmNos);
        } else {
            paramMap.put("farmNos", new ArrayList<Long>(farmNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<Farm>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInFarmNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Farm entity) {
        String id = "create";
        if (entity.getFarmNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Farm entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Farm entity) {
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
    public void deleteByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByFarmName(
            String farmName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmName", farmName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByFarmName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByUserNo(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByUserNo"), paramMap);
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
    public long countByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByFarmName(
            String farmName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmName", farmName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByFarmName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByUserNo(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByUserNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
