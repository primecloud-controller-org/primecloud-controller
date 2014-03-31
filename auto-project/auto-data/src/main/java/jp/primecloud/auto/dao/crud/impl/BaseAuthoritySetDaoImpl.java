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

import jp.primecloud.auto.dao.crud.BaseAuthoritySetDao;
import jp.primecloud.auto.entity.crud.AuthoritySet;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseAuthoritySetDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseAuthoritySetDaoImpl extends SqlMapClientDaoSupport implements BaseAuthoritySetDao {

    protected String namespace = "AuthoritySet";

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthoritySet read(
            Long setNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("setNo", setNo);
        return (AuthoritySet) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AuthoritySet> readAll() {
        return (List<AuthoritySet>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthoritySet readBySetName(
            String setName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("setName", setName);
        return (AuthoritySet) getSqlMapClientTemplate().queryForObject(getSqlMapId("readBySetName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AuthoritySet> readInSetNos(
            Collection<Long> setNos
        ) {
        if (setNos.isEmpty()) {
            return new ArrayList<AuthoritySet>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (setNos instanceof List) {
            paramMap.put("setNos", setNos);
        } else {
            paramMap.put("setNos", new ArrayList<Long>(setNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<AuthoritySet>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInSetNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(AuthoritySet entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(AuthoritySet entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(AuthoritySet entity) {
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
    public void deleteBySetNo(
            Long setNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("setNo", setNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteBySetNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteBySetName(
            String setName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("setName", setName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteBySetName"), paramMap);
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
    public long countBySetNo(
            Long setNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("setNo", setNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countBySetNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countBySetName(
            String setName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("setName", setName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countBySetName"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
