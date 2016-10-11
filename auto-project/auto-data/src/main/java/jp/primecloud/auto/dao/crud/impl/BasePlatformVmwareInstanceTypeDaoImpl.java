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
import jp.primecloud.auto.dao.crud.BasePlatformVmwareInstanceTypeDao;
import jp.primecloud.auto.entity.crud.PlatformVmwareInstanceType;

/**
 * <p>
 * {@link BasePlatformVmwareInstanceTypeDao}の実装クラスです。
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public abstract class BasePlatformVmwareInstanceTypeDaoImpl extends SqlMapClientDaoSupport implements BasePlatformVmwareInstanceTypeDao {

    protected String namespace = "PlatformVmwareInstanceType";

    /**
     * {@inheritDoc}
     */
    @Override
    public PlatformVmwareInstanceType read(
            Long instanceTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceTypeNo", instanceTypeNo);
        return (PlatformVmwareInstanceType) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformVmwareInstanceType> readAll() {
        return (List<PlatformVmwareInstanceType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlatformVmwareInstanceType readByPlatformNoAndInstanceTypeName(
            Long platformNo,
            String instanceTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("instanceTypeName", instanceTypeName);
        return (PlatformVmwareInstanceType) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByPlatformNoAndInstanceTypeName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformVmwareInstanceType> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<PlatformVmwareInstanceType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformVmwareInstanceType> readInInstanceTypeNos(
            Collection<Long> instanceTypeNos
        ) {
        if (instanceTypeNos.isEmpty()) {
            return new ArrayList<PlatformVmwareInstanceType>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (instanceTypeNos instanceof List) {
            paramMap.put("instanceTypeNos", instanceTypeNos);
        } else {
            paramMap.put("instanceTypeNos", new ArrayList<Long>(instanceTypeNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<PlatformVmwareInstanceType>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInInstanceTypeNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(PlatformVmwareInstanceType entity) {
        String id = "create";
        if (entity.getInstanceTypeNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(PlatformVmwareInstanceType entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(PlatformVmwareInstanceType entity) {
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
    public void deleteByInstanceTypeNo(
            Long instanceTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceTypeNo", instanceTypeNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByInstanceTypeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByPlatformNoAndInstanceTypeName(
            Long platformNo,
            String instanceTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("instanceTypeName", instanceTypeName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByPlatformNoAndInstanceTypeName"), paramMap);
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
    public long countByInstanceTypeNo(
            Long instanceTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceTypeNo", instanceTypeNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByInstanceTypeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByPlatformNoAndInstanceTypeName(
            Long platformNo,
            String instanceTypeName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("instanceTypeName", instanceTypeName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByPlatformNoAndInstanceTypeName"), paramMap);
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
