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

import jp.primecloud.auto.dao.crud.BaseTemplateComponentDao;
import jp.primecloud.auto.entity.crud.TemplateComponent;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseTemplateComponentDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseTemplateComponentDaoImpl extends SqlMapClientDaoSupport implements BaseTemplateComponentDao {

    protected String namespace = "TemplateComponent";

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateComponent read(
            Long templateComponentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateComponentNo", templateComponentNo);
        return (TemplateComponent) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateComponent> readAll() {
        return (List<TemplateComponent>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateComponent readByTemplateNoAndTemplateComponentName(
            Long templateNo,
            String templateComponentName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        paramMap.put("templateComponentName", templateComponentName);
        return (TemplateComponent) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByTemplateNoAndTemplateComponentName"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateComponent> readByTemplateNo(
            Long templateNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        return (List<TemplateComponent>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByTemplateNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateComponent> readByComponentTypeNo(
            Long componentTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeNo", componentTypeNo);
        return (List<TemplateComponent>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByComponentTypeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateComponent> readInTemplateComponentNos(
            Collection<Long> templateComponentNos
        ) {
        if (templateComponentNos.isEmpty()) {
            return new ArrayList<TemplateComponent>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (templateComponentNos instanceof List) {
            paramMap.put("templateComponentNos", templateComponentNos);
        } else {
            paramMap.put("templateComponentNos", new ArrayList<Long>(templateComponentNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<TemplateComponent>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInTemplateComponentNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(TemplateComponent entity) {
        String id = "create";
        if (entity.getTemplateComponentNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(TemplateComponent entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(TemplateComponent entity) {
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
    public void deleteByTemplateComponentNo(
            Long templateComponentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateComponentNo", templateComponentNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByTemplateComponentNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTemplateNoAndTemplateComponentName(
            Long templateNo,
            String templateComponentName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        paramMap.put("templateComponentName", templateComponentName);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByTemplateNoAndTemplateComponentName"), paramMap);
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
    public void deleteByComponentTypeNo(
            Long componentTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeNo", componentTypeNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByComponentTypeNo"), paramMap);
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
    public long countByTemplateComponentNo(
            Long templateComponentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateComponentNo", templateComponentNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByTemplateComponentNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByTemplateNoAndTemplateComponentName(
            Long templateNo,
            String templateComponentName
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        paramMap.put("templateComponentName", templateComponentName);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByTemplateNoAndTemplateComponentName"), paramMap);
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
    public long countByComponentTypeNo(
            Long componentTypeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentTypeNo", componentTypeNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByComponentTypeNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
