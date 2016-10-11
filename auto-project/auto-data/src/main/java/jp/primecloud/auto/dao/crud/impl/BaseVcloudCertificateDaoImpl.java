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
import jp.primecloud.auto.dao.crud.BaseVcloudCertificateDao;
import jp.primecloud.auto.entity.crud.VcloudCertificate;

/**
 * <p>
 * {@link BaseVcloudCertificateDao}の実装クラスです。
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public abstract class BaseVcloudCertificateDaoImpl extends SqlMapClientDaoSupport implements BaseVcloudCertificateDao {

    protected String namespace = "VcloudCertificate";

    /**
     * {@inheritDoc}
     */
    @Override
    public VcloudCertificate read(
            Long userNo,
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        return (VcloudCertificate) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudCertificate> readAll() {
        return (List<VcloudCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudCertificate> readByUserNo(
            Long userNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        return (List<VcloudCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByUserNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudCertificate> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<VcloudCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudCertificate> readInUserNos(
            Collection<Long> userNos
        ) {
        if (userNos.isEmpty()) {
            return new ArrayList<VcloudCertificate>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (userNos instanceof List) {
            paramMap.put("userNos", userNos);
        } else {
            paramMap.put("userNos", new ArrayList<Long>(userNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<VcloudCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInUserNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudCertificate> readInPlatformNos(
            Collection<Long> platformNos
        ) {
        if (platformNos.isEmpty()) {
            return new ArrayList<VcloudCertificate>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (platformNos instanceof List) {
            paramMap.put("platformNos", platformNos);
        } else {
            paramMap.put("platformNos", new ArrayList<Long>(platformNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<VcloudCertificate>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInPlatformNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(VcloudCertificate entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(VcloudCertificate entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(VcloudCertificate entity) {
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
    public void deleteByUserNoAndPlatformNo(
            Long userNo,
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByUserNoAndPlatformNo"), paramMap);
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
    public long countByUserNoAndPlatformNo(
            Long userNo,
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userNo", userNo);
        paramMap.put("platformNo", platformNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByUserNoAndPlatformNo"), paramMap);
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
