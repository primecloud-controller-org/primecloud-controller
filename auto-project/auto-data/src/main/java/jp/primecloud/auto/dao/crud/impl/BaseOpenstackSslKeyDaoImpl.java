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
import jp.primecloud.auto.dao.crud.BaseOpenstackSslKeyDao;
import jp.primecloud.auto.entity.crud.OpenstackSslKey;

/**
 * <p>
 * {@link BaseOpenstackSslKeyDao}の実装クラスです。
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public abstract class BaseOpenstackSslKeyDaoImpl extends SqlMapClientDaoSupport implements BaseOpenstackSslKeyDao {

    protected String namespace = "OpenstackSslKey";

    /**
     * {@inheritDoc}
     */
    @Override
    public OpenstackSslKey read(
            Long keyNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyNo", keyNo);
        return (OpenstackSslKey) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<OpenstackSslKey> readAll() {
        return (List<OpenstackSslKey>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<OpenstackSslKey> readInKeyNos(
            Collection<Long> keyNos
        ) {
        if (keyNos.isEmpty()) {
            return new ArrayList<OpenstackSslKey>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (keyNos instanceof List) {
            paramMap.put("keyNos", keyNos);
        } else {
            paramMap.put("keyNos", new ArrayList<Long>(keyNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<OpenstackSslKey>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInKeyNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(OpenstackSslKey entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(OpenstackSslKey entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(OpenstackSslKey entity) {
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
    public void deleteByKeyNo(
            Long keyNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyNo", keyNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByKeyNo"), paramMap);
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
    public long countByKeyNo(
            Long keyNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyNo", keyNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByKeyNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
