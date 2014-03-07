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

import jp.primecloud.auto.dao.crud.BaseLoadBalancerDao;
import jp.primecloud.auto.entity.crud.LoadBalancer;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseLoadBalancerDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseLoadBalancerDaoImpl extends SqlMapClientDaoSupport implements BaseLoadBalancerDao {

    protected String namespace = "LoadBalancer";

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadBalancer read(
            Long loadBalancerNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        return (LoadBalancer) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancer> readAll() {
        return (List<LoadBalancer>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadBalancer readByFarmNoAndLoadBalancerName(
            Long farmNo,
            String loadBalancerName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        paramMap.put("loadBalancerName", loadBalancerName);
        return (LoadBalancer) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByFarmNoAndLoadBalancerName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancer> readByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        return (List<LoadBalancer>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancer> readByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        return (List<LoadBalancer>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByComponentNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancer> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<LoadBalancer>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancer> readInLoadBalancerNos(
            Collection<Long> loadBalancerNos
        ) {
        if (loadBalancerNos.isEmpty()) {
            return new ArrayList<LoadBalancer>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (loadBalancerNos instanceof List) {
            paramMap.put("loadBalancerNos", loadBalancerNos);
        } else {
            paramMap.put("loadBalancerNos", new ArrayList<Long>(loadBalancerNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<LoadBalancer>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInLoadBalancerNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(LoadBalancer entity) {
        String id = "create";
        if (entity.getLoadBalancerNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(LoadBalancer entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(LoadBalancer entity) {
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
    public void deleteByLoadBalancerNo(
            Long loadBalancerNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByLoadBalancerNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByFarmNoAndLoadBalancerName(
            Long farmNo,
            String loadBalancerName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        paramMap.put("loadBalancerName", loadBalancerName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByFarmNoAndLoadBalancerName"), paramMap);
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
    public long countByLoadBalancerNo(
            Long loadBalancerNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByLoadBalancerNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByFarmNoAndLoadBalancerName(
            Long farmNo,
            String loadBalancerName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        paramMap.put("loadBalancerName", loadBalancerName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByFarmNoAndLoadBalancerName"), paramMap);
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
