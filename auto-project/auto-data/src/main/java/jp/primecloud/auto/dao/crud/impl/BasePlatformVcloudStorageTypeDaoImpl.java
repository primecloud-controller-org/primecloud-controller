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
import jp.primecloud.auto.dao.crud.BasePlatformVcloudStorageTypeDao;
import jp.primecloud.auto.entity.crud.PlatformVcloudStorageType;

/**
 * <p>
 * {@link BasePlatformVcloudStorageTypeDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BasePlatformVcloudStorageTypeDaoImpl extends SqlMapClientDaoSupport implements BasePlatformVcloudStorageTypeDao {

    protected String namespace = "PlatformVcloudStorageType";

    /**
     * {@inheritDoc}
     */
    @Override
    public PlatformVcloudStorageType read(
            Long storageTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("storageTypeNo", storageTypeNo);
        return (PlatformVcloudStorageType) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformVcloudStorageType> readAll() {
        return (List<PlatformVcloudStorageType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlatformVcloudStorageType readByPlatformNoAndStorageTypeName(
            Long platformNo,
            String storageTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("storageTypeName", storageTypeName);
        return (PlatformVcloudStorageType) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByPlatformNoAndStorageTypeName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformVcloudStorageType> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<PlatformVcloudStorageType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformVcloudStorageType> readInStorageTypeNos(
            Collection<Long> storageTypeNos
        ) {
        if (storageTypeNos.isEmpty()) {
            return new ArrayList<PlatformVcloudStorageType>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (storageTypeNos instanceof List) {
            paramMap.put("storageTypeNos", storageTypeNos);
        } else {
            paramMap.put("storageTypeNos", new ArrayList<Long>(storageTypeNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<PlatformVcloudStorageType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInStorageTypeNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(PlatformVcloudStorageType entity) {
        String id = "create";
        if (entity.getStorageTypeNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(PlatformVcloudStorageType entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(PlatformVcloudStorageType entity) {
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
    public void deleteByStorageTypeNo(
            Long storageTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("storageTypeNo", storageTypeNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByStorageTypeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByPlatformNoAndStorageTypeName(
            Long platformNo,
            String storageTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("storageTypeName", storageTypeName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByPlatformNoAndStorageTypeName"), paramMap);
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
    public long countByStorageTypeNo(
            Long storageTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("storageTypeNo", storageTypeNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByStorageTypeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByPlatformNoAndStorageTypeName(
            Long platformNo,
            String storageTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("storageTypeName", storageTypeName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByPlatformNoAndStorageTypeName"), paramMap);
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
