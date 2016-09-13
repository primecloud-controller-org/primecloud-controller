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
import jp.primecloud.auto.dao.crud.BaseCloudstackAddressDao;
import jp.primecloud.auto.entity.crud.CloudstackAddress;

/**
 * <p>
 * {@link BaseCloudstackAddressDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseCloudstackAddressDaoImpl extends SqlMapClientDaoSupport implements BaseCloudstackAddressDao {

    protected String namespace = "CloudstackAddress";

    /**
     * {@inheritDoc}
     */
    @Override
    public CloudstackAddress read(
            Long addressNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("addressNo", addressNo);
        return (CloudstackAddress) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackAddress> readAll() {
        return (List<CloudstackAddress>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackAddress> readByAccount(
            Long account
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("account", account);
        return (List<CloudstackAddress>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByAccount"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackAddress> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<CloudstackAddress>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackAddress> readByInstanceNo(
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceNo", instanceNo);
        return (List<CloudstackAddress>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByInstanceNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackAddress> readInAddressNos(
            Collection<Long> addressNos
        ) {
        if (addressNos.isEmpty()) {
            return new ArrayList<CloudstackAddress>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (addressNos instanceof List) {
            paramMap.put("addressNos", addressNos);
        } else {
            paramMap.put("addressNos", new ArrayList<Long>(addressNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<CloudstackAddress>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInAddressNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(CloudstackAddress entity) {
        String id = "create";
        if (entity.getAddressNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(CloudstackAddress entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(CloudstackAddress entity) {
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
    public void deleteByAddressNo(
            Long addressNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("addressNo", addressNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByAddressNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByAccount(
            Long account
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("account", account);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByAccount"), paramMap);
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
    public long countByAddressNo(
            Long addressNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("addressNo", addressNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByAddressNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByAccount(
            Long account
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("account", account);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByAccount"), paramMap);
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
