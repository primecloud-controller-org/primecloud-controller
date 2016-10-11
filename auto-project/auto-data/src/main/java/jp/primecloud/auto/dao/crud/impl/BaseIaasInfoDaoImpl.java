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
import jp.primecloud.auto.dao.crud.BaseIaasInfoDao;
import jp.primecloud.auto.entity.crud.IaasInfo;

/**
 * <p>
 * {@link BaseIaasInfoDao}の実装クラスです。
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public abstract class BaseIaasInfoDaoImpl extends SqlMapClientDaoSupport implements BaseIaasInfoDao {

    protected String namespace = "IaasInfo";

    /**
     * {@inheritDoc}
     */
    @Override
    public IaasInfo read(
            Long iaasNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("iaasNo", iaasNo);
        return (IaasInfo) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IaasInfo> readAll() {
        return (List<IaasInfo>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IaasInfo> readInIaasNos(
            Collection<Long> iaasNos
        ) {
        if (iaasNos.isEmpty()) {
            return new ArrayList<IaasInfo>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (iaasNos instanceof List) {
            paramMap.put("iaasNos", iaasNos);
        } else {
            paramMap.put("iaasNos", new ArrayList<Long>(iaasNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<IaasInfo>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInIaasNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(IaasInfo entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(IaasInfo entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(IaasInfo entity) {
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
    public void deleteByIaasNo(
            Long iaasNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("iaasNo", iaasNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByIaasNo"), paramMap);
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
    public long countByIaasNo(
            Long iaasNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("iaasNo", iaasNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByIaasNo"), paramMap);
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }

}
