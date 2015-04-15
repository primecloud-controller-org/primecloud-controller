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
import jp.primecloud.auto.dao.crud.BaseAzureSubnetDao;
import jp.primecloud.auto.entity.crud.AzureSubnet;

/**
 * <p>
 * {@link BaseAzureSubnetDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseAzureSubnetDaoImpl extends SqlMapClientDaoSupport implements BaseAzureSubnetDao {

    protected String namespace = "AzureSubnet";

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureSubnet read(
            Long subnetNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("subnetNo", subnetNo);
        return (AzureSubnet) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AzureSubnet> readAll() {
        return (List<AzureSubnet>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AzureSubnet> readInSubnetNos(
            Collection<Long> subnetNos
        ) {
        if (subnetNos.isEmpty()) {
            return new ArrayList<AzureSubnet>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (subnetNos instanceof List) {
            paramMap.put("subnetNos", subnetNos);
        } else {
            paramMap.put("subnetNos", new ArrayList<Long>(subnetNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<AzureSubnet>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInSubnetNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(AzureSubnet entity) {
        String id = "create";
        if (entity.getSubnetNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(AzureSubnet entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(AzureSubnet entity) {
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
    public void deleteBySubnetNo(
            Long subnetNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("subnetNo", subnetNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteBySubnetNo"), paramMap);
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
    public long countBySubnetNo(
            Long subnetNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("subnetNo", subnetNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countBySubnetNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
