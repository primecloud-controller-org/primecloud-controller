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
import jp.primecloud.auto.dao.crud.BaseApiCertificateDao;
import jp.primecloud.auto.entity.crud.ApiCertificate;

/**
 * <p>
 * {@link BaseApiCertificateDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseApiCertificateDaoImpl extends SqlMapClientDaoSupport implements BaseApiCertificateDao {

    protected String namespace = "ApiCertificate";

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiCertificate read(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        return (ApiCertificate) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ApiCertificate> readAll() {
        return (List<ApiCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiCertificate readByApiAccessId(
            String apiAccessId
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("apiAccessId", apiAccessId);
        return (ApiCertificate) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByApiAccessId"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiCertificate readByApiSecretKey(
            String apiSecretKey
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("apiSecretKey", apiSecretKey);
        return (ApiCertificate) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByApiSecretKey"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ApiCertificate> readInUserNos(
            Collection<Long> userNos
        ) {
        if (userNos.isEmpty()) {
            return new ArrayList<ApiCertificate>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (userNos instanceof List) {
            paramMap.put("userNos", userNos);
        } else {
            paramMap.put("userNos", new ArrayList<Long>(userNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<ApiCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInUserNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ApiCertificate entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ApiCertificate entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(ApiCertificate entity) {
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
    public void deleteByApiAccessId(
            String apiAccessId
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("apiAccessId", apiAccessId);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByApiAccessId"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByApiSecretKey(
            String apiSecretKey
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("apiSecretKey", apiSecretKey);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByApiSecretKey"), paramMap);
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
    public long countByApiAccessId(
            String apiAccessId
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("apiAccessId", apiAccessId);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByApiAccessId"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByApiSecretKey(
            String apiSecretKey
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("apiSecretKey", apiSecretKey);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByApiSecretKey"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
