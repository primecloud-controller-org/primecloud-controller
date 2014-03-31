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

import jp.primecloud.auto.dao.crud.BaseVmwareNetworkDao;
import jp.primecloud.auto.entity.crud.VmwareNetwork;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseVmwareNetworkDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseVmwareNetworkDaoImpl extends SqlMapClientDaoSupport implements BaseVmwareNetworkDao {

    protected String namespace = "VmwareNetwork";

    /**
     * {@inheritDoc}
     */
    @Override
    public VmwareNetwork read(
            Long networkNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("networkNo", networkNo);
        return (VmwareNetwork) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VmwareNetwork> readAll() {
        return (List<VmwareNetwork>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VmwareNetwork readByPlatformNoAndNetworkName(
            Long platformNo,
            String networkName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("networkName", networkName);
        return (VmwareNetwork) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByPlatformNoAndNetworkName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VmwareNetwork> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<VmwareNetwork>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VmwareNetwork> readByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        return (List<VmwareNetwork>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VmwareNetwork> readInNetworkNos(
            Collection<Long> networkNos
        ) {
        if (networkNos.isEmpty()) {
            return new ArrayList<VmwareNetwork>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (networkNos instanceof List) {
            paramMap.put("networkNos", networkNos);
        } else {
            paramMap.put("networkNos", new ArrayList<Long>(networkNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<VmwareNetwork>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInNetworkNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(VmwareNetwork entity) {
        String id = "create";
        if (entity.getNetworkNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(VmwareNetwork entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(VmwareNetwork entity) {
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
    public void deleteByNetworkNo(
            Long networkNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("networkNo", networkNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByNetworkNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByPlatformNoAndNetworkName(
            Long platformNo,
            String networkName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("networkName", networkName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByPlatformNoAndNetworkName"), paramMap);
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
    public long countAll() {
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByNetworkNo(
            Long networkNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("networkNo", networkNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByNetworkNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByPlatformNoAndNetworkName(
            Long platformNo,
            String networkName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        paramMap.put("networkName", networkName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByPlatformNoAndNetworkName"), paramMap);
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

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
