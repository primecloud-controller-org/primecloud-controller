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

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import jp.primecloud.auto.dao.crud.BaseProxyDao;
import jp.primecloud.auto.entity.crud.Proxy;

/**
 * <p>
 * {@link BaseProxyDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseProxyDaoImpl extends SqlMapClientDaoSupport implements BaseProxyDao {

    protected String namespace = "Proxy";

    /**
     * {@inheritDoc}
     */
    @Override
    public Proxy read() {
        return (Proxy) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Proxy entity) {
        String id = "create";
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Proxy entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Proxy entity) {
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
    public long countAll() {
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countAll"));
    }

    protected String getSqlMapId(String id) {
        if (namespace == null || namespace.length() == 0) {
            return id;
        }
        return namespace + "." + id;
    }
}
