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

import jp.primecloud.auto.dao.crud.BaseTemplateInstanceDao;
import jp.primecloud.auto.entity.crud.TemplateInstance;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseTemplateInstanceDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseTemplateInstanceDaoImpl extends SqlMapClientDaoSupport implements BaseTemplateInstanceDao {

    protected String namespace = "TemplateInstance";

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateInstance read(
            Long templateInstanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateInstanceNo", templateInstanceNo);
        return (TemplateInstance) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateInstance> readAll() {
        return (List<TemplateInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateInstance readByTemplateNoAndTemplateInstanceName(
            Long templateNo,
            String templateInstanceName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        paramMap.put("templateInstanceName", templateInstanceName);
        return (TemplateInstance) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByTemplateNoAndTemplateInstanceName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateInstance> readByTemplateNo(
            Long templateNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        return (List<TemplateInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByTemplateNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateInstance> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<TemplateInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateInstance> readByImageNo(
            Long imageNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("imageNo", imageNo);
        return (List<TemplateInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByImageNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateInstance> readInTemplateInstanceNos(
            Collection<Long> templateInstanceNos
        ) {
        if (templateInstanceNos.isEmpty()) {
            return new ArrayList<TemplateInstance>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (templateInstanceNos instanceof List) {
            paramMap.put("templateInstanceNos", templateInstanceNos);
        } else {
            paramMap.put("templateInstanceNos", new ArrayList<Long>(templateInstanceNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<TemplateInstance>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInTemplateInstanceNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(TemplateInstance entity) {
        String id = "create";
        if (entity.getTemplateInstanceNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(TemplateInstance entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(TemplateInstance entity) {
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
    public void deleteByTemplateInstanceNo(
            Long templateInstanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateInstanceNo", templateInstanceNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByTemplateInstanceNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTemplateNoAndTemplateInstanceName(
            Long templateNo,
            String templateInstanceName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        paramMap.put("templateInstanceName", templateInstanceName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByTemplateNoAndTemplateInstanceName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTemplateNo(
            Long templateNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByTemplateNo"), paramMap);
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
    public void deleteByImageNo(
            Long imageNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("imageNo", imageNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByImageNo"), paramMap);
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
    public long countByTemplateInstanceNo(
            Long templateInstanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateInstanceNo", templateInstanceNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByTemplateInstanceNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByTemplateNoAndTemplateInstanceName(
            Long templateNo,
            String templateInstanceName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        paramMap.put("templateInstanceName", templateInstanceName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByTemplateNoAndTemplateInstanceName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByTemplateNo(
            Long templateNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByTemplateNo"), paramMap);
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
    public long countByImageNo(
            Long imageNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("imageNo", imageNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByImageNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
