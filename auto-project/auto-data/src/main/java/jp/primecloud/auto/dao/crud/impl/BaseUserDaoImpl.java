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

import jp.primecloud.auto.dao.crud.BaseUserDao;
import jp.primecloud.auto.entity.crud.User;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseUserDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseUserDaoImpl extends SqlMapClientDaoSupport implements BaseUserDao {

    protected String namespace = "User";

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        return (User) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User> readAll() {
        return (List<User>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User readByUsername(
            String username
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("username", username);
        return (User) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByUsername"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User> readInUserNos(
            Collection<Long> userNos
        ) {
        if (userNos.isEmpty()) {
            return new ArrayList<User>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (userNos instanceof List) {
            paramMap.put("userNos", userNos);
        } else {
            paramMap.put("userNos", new ArrayList<Long>(userNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<User>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInUserNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(User entity) {
        String id = "create";
        if (entity.getUserNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(User entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(User entity) {
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
    public void deleteByUsername(
            String username
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("username", username);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByUsername"), paramMap);
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
    public long countByUserNo(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByUserNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByUsername(
            String username
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("username", username);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByUsername"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User>  readByMasterUser(
            Long masterUserNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("masterUser", masterUserNo);
        paramMap.put("userNo", masterUserNo);
        return (List<User>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByMasterUser"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
