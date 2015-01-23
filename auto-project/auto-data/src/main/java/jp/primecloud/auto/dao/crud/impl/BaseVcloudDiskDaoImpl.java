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
import jp.primecloud.auto.dao.crud.BaseVcloudDiskDao;
import jp.primecloud.auto.entity.crud.VcloudDisk;

/**
 * <p>
 * {@link BaseVcloudDiskDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseVcloudDiskDaoImpl extends SqlMapClientDaoSupport implements BaseVcloudDiskDao {

    protected String namespace = "VcloudDisk";

    /**
     * {@inheritDoc}
     */
    @Override
    public VcloudDisk read(
            Long diskNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("diskNo", diskNo);
        return (VcloudDisk) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudDisk> readAll() {
        return (List<VcloudDisk>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudDisk> readByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        return (List<VcloudDisk>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudDisk> readByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        return (List<VcloudDisk>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByComponentNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudDisk> readByInstanceNo(
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceNo", instanceNo);
        return (List<VcloudDisk>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByInstanceNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudDisk> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<VcloudDisk>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VcloudDisk> readInDiskNos(
            Collection<Long> diskNos
        ) {
        if (diskNos.isEmpty()) {
            return new ArrayList<VcloudDisk>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (diskNos instanceof List) {
            paramMap.put("diskNos", diskNos);
        } else {
            paramMap.put("diskNos", new ArrayList<Long>(diskNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<VcloudDisk>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInDiskNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(VcloudDisk entity) {
        String id = "create";
        if (entity.getDiskNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(VcloudDisk entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(VcloudDisk entity) {
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
    public void deleteByDiskNo(
            Long diskNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("diskNo", diskNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByDiskNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByComponentNo"), paramMap);
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
    public long countByDiskNo(
            Long diskNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("diskNo", diskNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByDiskNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByComponentNo"), paramMap);
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
