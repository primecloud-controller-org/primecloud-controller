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
import jp.primecloud.auto.dao.crud.BaseSystemConfigDao;
import jp.primecloud.auto.entity.crud.SystemConfig;

/**
 * <p>
 * {@link BaseSystemConfigDao}の実装クラスです。
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public abstract class BaseSystemConfigDaoImpl extends SqlMapClientDaoSupport implements BaseSystemConfigDao {

    protected String namespace = "SystemConfig";

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemConfig read(
            String name
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", name);
        return (SystemConfig) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<SystemConfig> readAll() {
        return (List<SystemConfig>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<SystemConfig> readInNames(
            Collection<String> names
        ) {
        if (names.isEmpty()) {
            return new ArrayList<SystemConfig>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (names instanceof List) {
            paramMap.put("names", names);
        } else {
            paramMap.put("names", new ArrayList<String>(names));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<SystemConfig>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInNames"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(SystemConfig entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(SystemConfig entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(SystemConfig entity) {
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
    public void deleteByName(
            String name
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", name);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByName"), paramMap);
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
    public long countByName(
            String name
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", name);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByName"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
