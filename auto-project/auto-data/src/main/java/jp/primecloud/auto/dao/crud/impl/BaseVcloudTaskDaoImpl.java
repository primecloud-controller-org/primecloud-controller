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
import jp.primecloud.auto.dao.crud.BaseVcloudTaskDao;
import jp.primecloud.auto.entity.crud.VcloudTask;

/**
 * <p>
 * {@link BaseVcloudTaskDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseVcloudTaskDaoImpl extends SqlMapClientDaoSupport implements BaseVcloudTaskDao {

    protected String namespace = "VcloudTask";

    /**
     * {@inheritDoc}
     */
    @Override
    public VcloudTask read(
            Long PId
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("PId", PId);
        return (VcloudTask) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudTask> readAll() {
        return (List<VcloudTask>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudTask> readInPIds(
            Collection<Long> PIds
        ) {
        if (PIds.isEmpty()) {
            return new ArrayList<VcloudTask>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (PIds instanceof List) {
            paramMap.put("PIds", PIds);
        } else {
            paramMap.put("PIds", new ArrayList<Long>(PIds));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<VcloudTask>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInPIds"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(VcloudTask entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(VcloudTask entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(VcloudTask entity) {
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
    public void deleteByPId(
            Long PId
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("PId", PId);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByPId"), paramMap);
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
    public long countByPId(
            Long PId
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("PId", PId);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByPId"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
