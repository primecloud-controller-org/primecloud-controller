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
import jp.primecloud.auto.dao.crud.BaseAwsVolumeDao;
import jp.primecloud.auto.entity.crud.AwsVolume;

/**
 * <p>
 * {@link BaseAwsVolumeDao}の実装クラスです。
 * </p>
 *
 */
public abstract class BaseAwsVolumeDaoImpl extends SqlMapClientDaoSupport implements BaseAwsVolumeDao {

    protected String namespace = "AwsVolume";

    /**
     * {@inheritDoc}
     */
    @Override
    public AwsVolume read(
            Long volumeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("volumeNo", volumeNo);
        return (AwsVolume) getSqlMapClientTemplate().queryForObject(getSqlMapId("read"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AwsVolume> readAll() {
        return (List<AwsVolume>) getSqlMapClientTemplate().queryForList(getSqlMapId("readAll"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AwsVolume readByComponentNoAndInstanceNo(
            Long componentNo,
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        paramMap.put("instanceNo", instanceNo);
        return (AwsVolume) getSqlMapClientTemplate().queryForObject(getSqlMapId("readByComponentNoAndInstanceNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AwsVolume> readByComponentNo(
            Long componentNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        return (List<AwsVolume>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByComponentNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AwsVolume> readByFarmNo(
            Long farmNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("farmNo", farmNo);
        return (List<AwsVolume>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByFarmNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AwsVolume> readByInstanceNo(
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("instanceNo", instanceNo);
        return (List<AwsVolume>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByInstanceNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AwsVolume> readByPlatformNo(
            Long platformNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("platformNo", platformNo);
        return (List<AwsVolume>) getSqlMapClientTemplate().queryForList(getSqlMapId("readByPlatformNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<AwsVolume> readInVolumeNos(
            Collection<Long> volumeNos
        ) {
        if (volumeNos.isEmpty()) {
            return new ArrayList<AwsVolume>();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (volumeNos instanceof List) {
            paramMap.put("volumeNos", volumeNos);
        } else {
            paramMap.put("volumeNos", new ArrayList<Long>(volumeNos));
        }
        paramMap.put("orderBys", new String[0]);
        return (List<AwsVolume>) getSqlMapClientTemplate().queryForList(getSqlMapId("readInVolumeNos"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(AwsVolume entity) {
        String id = "create";
        if (entity.getVolumeNo() == null) {
            id = "createAuto";
        }
        getSqlMapClientTemplate().insert(getSqlMapId(id), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(AwsVolume entity) {
        getSqlMapClientTemplate().insert(getSqlMapId("update"), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(AwsVolume entity) {
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
    public void deleteByVolumeNo(
            Long volumeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("volumeNo", volumeNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByVolumeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByComponentNoAndInstanceNo(
            Long componentNo,
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        paramMap.put("instanceNo", instanceNo);
        getSqlMapClientTemplate().delete(getSqlMapId("deleteByComponentNoAndInstanceNo"), paramMap);
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
    public long countByVolumeNo(
            Long volumeNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("volumeNo", volumeNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByVolumeNo"), paramMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByComponentNoAndInstanceNo(
            Long componentNo,
            Long instanceNo
        ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("componentNo", componentNo);
        paramMap.put("instanceNo", instanceNo);
        return (Long) getSqlMapClientTemplate().queryForObject(getSqlMapId("countByComponentNoAndInstanceNo"), paramMap);
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
