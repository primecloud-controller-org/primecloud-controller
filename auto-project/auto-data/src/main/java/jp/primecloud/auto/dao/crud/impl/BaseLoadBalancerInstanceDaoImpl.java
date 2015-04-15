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
import jp.primecloud.auto.dao.crud.BaseLoadBalancerInstanceDao;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;

/**
 * <p>
 * {@link BaseLoadBalancerInstanceDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseLoadBalancerInstanceDaoImpl extends SqlMapClientDaoSupport implements BaseLoadBalancerInstanceDao {

    protected String namespace = "LoadBalancerInstance";

    /**
     * {@inheritDoc}
     */
    @Override
    public LoadBalancerInstance read(
            Long loadBalancerNo,
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        paramMap.put("instanceNo", instanceNo);
        return (LoadBalancerInstance) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerInstance> readAll() {
        return (List<LoadBalancerInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerInstance> readByLoadBalancerNo(
            Long loadBalancerNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        return (List<LoadBalancerInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByLoadBalancerNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerInstance> readByInstanceNo(
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceNo", instanceNo);
        return (List<LoadBalancerInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByInstanceNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerInstance> readInLoadBalancerNos(
            Collection<Long> loadBalancerNos
        ) {
        if (loadBalancerNos.isEmpty()) {
            return new ArrayList<LoadBalancerInstance>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (loadBalancerNos instanceof List) {
            paramMap.put("loadBalancerNos", loadBalancerNos);
        } else {
            paramMap.put("loadBalancerNos", new ArrayList<Long>(loadBalancerNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<LoadBalancerInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInLoadBalancerNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<LoadBalancerInstance> readInInstanceNos(
            Collection<Long> instanceNos
        ) {
        if (instanceNos.isEmpty()) {
            return new ArrayList<LoadBalancerInstance>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (instanceNos instanceof List) {
            paramMap.put("instanceNos", instanceNos);
        } else {
            paramMap.put("instanceNos", new ArrayList<Long>(instanceNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<LoadBalancerInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInInstanceNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(LoadBalancerInstance entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(LoadBalancerInstance entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(LoadBalancerInstance entity) {
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
    public void deleteByLoadBalancerNoAndInstanceNo(
            Long loadBalancerNo,
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        paramMap.put("instanceNo", instanceNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByLoadBalancerNoAndInstanceNo"), paramMap);
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
    public void deleteByInstanceNo(
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceNo", instanceNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByInstanceNo"), paramMap);
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
    public long countByLoadBalancerNoAndInstanceNo(
            Long loadBalancerNo,
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loadBalancerNo", loadBalancerNo);
        paramMap.put("instanceNo", instanceNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByLoadBalancerNoAndInstanceNo"), paramMap);
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
    public long countByInstanceNo(
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceNo", instanceNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByInstanceNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
