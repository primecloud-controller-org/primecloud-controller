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

import jp.primecloud.auto.dao.crud.BaseTemplateDao;
import jp.primecloud.auto.entity.crud.Template;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * <p>
 * {@link BaseTemplateDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseTemplateDaoImpl extends SqlMapClientDaoSupport implements BaseTemplateDao {

    protected String namespace = "Template";

    /**
     * {@inheritDoc}
     */
    @Override
    public Template read(
            Long templateNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("templateNo", templateNo);
        return (Template) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Template> readAll() {
        return (List<Template>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Template> readInTemplateNos(
            Collection<Long> templateNos
        ) {
        if (templateNos.isEmpty()) {
            return new ArrayList<Template>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (templateNos instanceof List) {
            paramMap.put("templateNos", templateNos);
        } else {
            paramMap.put("templateNos", new ArrayList<Long>(templateNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<Template>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInTemplateNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Template entity) {
        String id = "create";
        if (entity.getTemplateNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Template entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Template entity) {
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
    public long countAll() {
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countAll"));
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

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
