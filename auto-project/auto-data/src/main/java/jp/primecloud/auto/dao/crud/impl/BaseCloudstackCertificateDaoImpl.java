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

import jp.primecloud.auto.dao.crud.BaseCloudstackCertificateDao;
import jp.primecloud.auto.entity.crud.CloudstackCertificate;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseCloudstackCertificateDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseCloudstackCertificateDaoImpl extends SqlMapClientDaoSupport implements BaseCloudstackCertificateDao {

    protected String namespace = "CloudstackCertificate";

    /**
     * {@inheritDoc}
     */
    @Override
    public CloudstackCertificate read(
            Long account,
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("account", account);
        paramMap.put("platformNo", platformNo);
        return (CloudstackCertificate) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackCertificate> readAll() {
        return (List<CloudstackCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackCertificate> readByAccount(
            Long account
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("account", account);
        return (List<CloudstackCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByAccount"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackCertificate> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<CloudstackCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackCertificate> readInAccounts(
            Collection<Long> accounts
        ) {
        if (accounts.isEmpty()) {
            return new ArrayList<CloudstackCertificate>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (accounts instanceof List) {
            paramMap.put("accounts", accounts);
        } else {
            paramMap.put("accounts", new ArrayList<Long>(accounts));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<CloudstackCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInAccounts"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CloudstackCertificate> readInPlatformNos(
            Collection<Long> platformNos
        ) {
        if (platformNos.isEmpty()) {
            return new ArrayList<CloudstackCertificate>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (platformNos instanceof List) {
            paramMap.put("platformNos", platformNos);
        } else {
            paramMap.put("platformNos", new ArrayList<Long>(platformNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<CloudstackCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInPlatformNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(CloudstackCertificate entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(CloudstackCertificate entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(CloudstackCertificate entity) {
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
    public void deleteByAccountAndPlatformNo(
            Long account,
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("account", account);
        paramMap.put("platformNo", platformNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByAccountAndPlatformNo"), paramMap);
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
    public long countAll() {
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByAccountAndPlatformNo(
            Long account,
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("account", account);
        paramMap.put("platformNo", platformNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByAccountAndPlatformNo"), paramMap);
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

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
