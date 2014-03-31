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

import jp.primecloud.auto.dao.crud.BaseNiftyKeyPairDao;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseNiftyKeyPairDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseNiftyKeyPairDaoImpl extends SqlMapClientDaoSupport implements BaseNiftyKeyPairDao {

    protected String namespace = "NiftyKeyPair";

    /**
     * {@inheritDoc}
     */
    @Override
    public NiftyKeyPair read(
            Long keyNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyNo", keyNo);
        return (NiftyKeyPair) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NiftyKeyPair> readAll() {
        return (List<NiftyKeyPair>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NiftyKeyPair readByUserNoAndPlatformNoAndKeyName(
            Long userNo,
            Long platformNo,
            String keyName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        paramMap.put("keyName", keyName);
        return (NiftyKeyPair) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByUserNoAndPlatformNoAndKeyName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NiftyKeyPair> readByUserNo(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        return (List<NiftyKeyPair>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByUserNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NiftyKeyPair> readByUserNoAndPlatformNo(
            Long userNo, 
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        return (List<NiftyKeyPair>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByUserNoAndPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NiftyKeyPair> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<NiftyKeyPair>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NiftyKeyPair> readInKeyNos(
            Collection<Long> keyNos
        ) {
        if (keyNos.isEmpty()) {
            return new ArrayList<NiftyKeyPair>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (keyNos instanceof List) {
            paramMap.put("keyNos", keyNos);
        } else {
            paramMap.put("keyNos", new ArrayList<Long>(keyNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<NiftyKeyPair>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInKeyNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(NiftyKeyPair entity) {
        String id = "create";
        if (entity.getKeyNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(NiftyKeyPair entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(NiftyKeyPair entity) {
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
    public void deleteByUserNoAndPlatformNoAndKeyName(
            Long userNo,
            Long platformNo,
            String keyName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        paramMap.put("keyName", keyName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByUserNoAndPlatformNoAndKeyName"), paramMap);
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
    public void deleteByUserNoAndPlatformNo(
            Long userNo, 
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByUserNoAndPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByPlatformNo"), paramMap);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByUserNoAndPlatformNoAndKeyName(
            Long userNo,
            Long platformNo,
            String keyName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        paramMap.put("keyName", keyName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByUserNoAndPlatformNoAndKeyName"), paramMap);
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
    public long countByUserNoAndPlatformNo(
            Long userNo, 
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByUserNoAndPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByPlatformNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
