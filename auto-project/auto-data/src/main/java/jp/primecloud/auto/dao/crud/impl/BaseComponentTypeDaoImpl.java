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
import jp.primecloud.auto.dao.crud.BaseComponentTypeDao;
import jp.primecloud.auto.entity.crud.ComponentType;

/**
 * <p>
 * {@link BaseComponentTypeDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseComponentTypeDaoImpl extends SqlMapClientDaoSupport implements BaseComponentTypeDao {

    protected String namespace = "ComponentType";

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentType read(
            Long componentTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeNo", componentTypeNo);
        return (ComponentType) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ComponentType> readAll() {
        return (List<ComponentType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentType readByComponentTypeName(
            String componentTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeName", componentTypeName);
        return (ComponentType) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByComponentTypeName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ComponentType> readInComponentTypeNos(
            Collection<Long> componentTypeNos
        ) {
        if (componentTypeNos.isEmpty()) {
            return new ArrayList<ComponentType>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (componentTypeNos instanceof List) {
            paramMap.put("componentTypeNos", componentTypeNos);
        } else {
            paramMap.put("componentTypeNos", new ArrayList<Long>(componentTypeNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<ComponentType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInComponentTypeNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ComponentType entity) {
        String id = "create";
        if (entity.getComponentTypeNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ComponentType entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(ComponentType entity) {
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
    public void deleteByComponentTypeNo(
            Long componentTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeNo", componentTypeNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByComponentTypeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByComponentTypeName(
            String componentTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeName", componentTypeName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByComponentTypeName"), paramMap);
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
    public long countByComponentTypeNo(
            Long componentTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeNo", componentTypeNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByComponentTypeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByComponentTypeName(
            String componentTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeName", componentTypeName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByComponentTypeName"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
