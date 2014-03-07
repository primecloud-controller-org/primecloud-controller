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

import jp.primecloud.auto.dao.crud.BaseComponentConfigDao;
import jp.primecloud.auto.entity.crud.ComponentConfig;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseComponentConfigDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseComponentConfigDaoImpl extends SqlMapClientDaoSupport implements BaseComponentConfigDao {

    protected String namespace = "ComponentConfig";

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentConfig read(
            Long configNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("configNo", configNo);
        return (ComponentConfig) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ComponentConfig> readAll() {
        return (List<ComponentConfig>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentConfig readByComponentNoAndConfigName(
            Long componentNo,
            String configName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        paramMap.put("configName", configName);
        return (ComponentConfig) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByComponentNoAndConfigName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ComponentConfig> readByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        return (List<ComponentConfig>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByComponentNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ComponentConfig> readInConfigNos(
            Collection<Long> configNos
        ) {
        if (configNos.isEmpty()) {
            return new ArrayList<ComponentConfig>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (configNos instanceof List) {
            paramMap.put("configNos", configNos);
        } else {
            paramMap.put("configNos", new ArrayList<Long>(configNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<ComponentConfig>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInConfigNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ComponentConfig entity) {
        String id = "create";
        if (entity.getConfigNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ComponentConfig entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(ComponentConfig entity) {
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
    public void deleteByConfigNo(
            Long configNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("configNo", configNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByConfigNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByComponentNoAndConfigName(
            Long componentNo,
            String configName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        paramMap.put("configName", configName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByComponentNoAndConfigName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByComponentNo"), paramMap);
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
    public long countByConfigNo(
            Long configNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("configNo", configNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByConfigNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByComponentNoAndConfigName(
            Long componentNo,
            String configName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        paramMap.put("configName", configName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByComponentNoAndConfigName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByComponentNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
