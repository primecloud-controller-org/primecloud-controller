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

import jp.primecloud.auto.dao.crud.BaseLoadBalancerListenerDao;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseLoadBalancerListenerDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseLoadBalancerListenerDaoImpl extends SqlMapClientDaoSupport implements BaseLoadBalancerListenerDao {

    protected String namespace = "LoadBalancerListener";

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadBalancerListener read(
            Long loadBalancerNo,
            Integer loadBalancerPort
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        paramMap.put("loadBalancerPort", loadBalancerPort);
        return (LoadBalancerListener) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerListener> readAll() {
        return (List<LoadBalancerListener>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerListener> readByLoadBalancerNo(
            Long loadBalancerNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        return (List<LoadBalancerListener>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByLoadBalancerNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerListener> readInLoadBalancerNos(
            Collection<Long> loadBalancerNos
        ) {
        if (loadBalancerNos.isEmpty()) {
            return new ArrayList<LoadBalancerListener>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (loadBalancerNos instanceof List) {
            paramMap.put("loadBalancerNos", loadBalancerNos);
        } else {
            paramMap.put("loadBalancerNos", new ArrayList<Long>(loadBalancerNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<LoadBalancerListener>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInLoadBalancerNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerListener> readInLoadBalancerPorts(
            Collection<Integer> loadBalancerPorts
        ) {
        if (loadBalancerPorts.isEmpty()) {
            return new ArrayList<LoadBalancerListener>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (loadBalancerPorts instanceof List) {
            paramMap.put("loadBalancerPorts", loadBalancerPorts);
        } else {
            paramMap.put("loadBalancerPorts", new ArrayList<Integer>(loadBalancerPorts));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<LoadBalancerListener>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInLoadBalancerPorts"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(LoadBalancerListener entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(LoadBalancerListener entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(LoadBalancerListener entity) {
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
    public void deleteByLoadBalancerNoAndLoadBalancerPort(
            Long loadBalancerNo,
            Integer loadBalancerPort
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        paramMap.put("loadBalancerPort", loadBalancerPort);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByLoadBalancerNoAndLoadBalancerPort"), paramMap);
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
    public long countAll() {
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByLoadBalancerNoAndLoadBalancerPort(
            Long loadBalancerNo,
            Integer loadBalancerPort
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        paramMap.put("loadBalancerPort", loadBalancerPort);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByLoadBalancerNoAndLoadBalancerPort"), paramMap);
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

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
