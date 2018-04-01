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
package jp.primecloud.auto.component.mysql.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.process.ComponentProcessContext;
import jp.primecloud.auto.process.puppet.PuppetComponentProcess;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MySQLPuppetComponentProcess extends PuppetComponentProcess {

    /**
     * TODO: コンストラクタコメントを記述
     */
    public MySQLPuppetComponentProcess() {
        componentTypeName = MySQLConstants.COMPONENT_TYPE_NAME;
        awsVolumeDevice = MySQLConstants.AWS_VOLUME_DEVICE;
        vmwareDiskScsiId = MySQLConstants.VMWARE_DISK_SCSI_ID;
    }

    @Override
    protected void configureInstances(Long componentNo, ComponentProcessContext context, boolean start,
            List<Long> instanceNos) {
        // MasterとSlaveの仕分け
        List<Long> masterInstanceNos = new ArrayList<Long>();
        List<Long> slaveInstanceNos = new ArrayList<Long>();
        Map<Long, Long> masterInstanceNoMap = createMasterInstanceNoMap(componentNo, context, start);
        for (Entry<Long, Long> entry : masterInstanceNoMap.entrySet()) {
            if (entry.getValue() == null) {
                masterInstanceNos.add(entry.getKey());
            } else {
                slaveInstanceNos.add(entry.getKey());
            }
        }

        if (start) {
            // Masterのサーバについて処理を行う
            if (!masterInstanceNos.isEmpty()) {
                super.configureInstances(componentNo, context, start, masterInstanceNos);
            }

            // Slaveのサーバについて処理を行う
            if (!slaveInstanceNos.isEmpty()) {
                super.configureInstances(componentNo, context, start, slaveInstanceNos);
            }
        } else {
            // Slaveのサーバについて処理を行う
            if (!slaveInstanceNos.isEmpty()) {
                super.configureInstances(componentNo, context, start, slaveInstanceNos);
            }

            // Masterのサーバについて処理を行う
            if (!masterInstanceNos.isEmpty()) {
                super.configureInstances(componentNo, context, start, masterInstanceNos);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configureInstance(Long componentNo, ComponentProcessContext context, boolean start, Long instanceNo,
            Map<String, Object> rootMap) {
        // Slaveのサーバでボリュームが未作成で、かつMasterが起動していない場合は起動できない
        if (start) {
            Map<Long, Long> masterInstanceNoMap = (Map<Long, Long>) rootMap.get("masterInstanceNoMap");
            Long masterInstanceNo = masterInstanceNoMap.get(instanceNo);
            if (masterInstanceNo != null) {
                AwsVolume awsVolume = awsVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
                if (awsVolume == null) {
                    ComponentInstance master = componentInstanceDao.read(componentNo, masterInstanceNo);
                    if (master == null || ComponentInstanceStatus
                            .fromStatus(master.getStatus()) != ComponentInstanceStatus.RUNNING) {
                        throw new AutoException("EPROCESS-200101", instanceNo, masterInstanceNo);
                    }
                }
            }
        }

        super.configureInstance(componentNo, context, start, instanceNo, rootMap);
    }

    protected Map<Long, Long> createMasterInstanceNoMap(Long componentNo, ComponentProcessContext context,
            boolean start) {
        Map<Long, Long> masterInstanceNoMap = new HashMap<Long, Long>();
        List<Long> instanceNos;
        if (start) {
            instanceNos = context.getEnableInstanceNoMap().get(componentNo);
        } else {
            instanceNos = context.getDisableInstanceNoMap().get(componentNo);
        }
        if (instanceNos != null) {
            for (Long instanceNo : instanceNos) {
                InstanceConfig config = instanceConfigDao.readByInstanceNoAndComponentNoAndConfigName(instanceNo,
                        componentNo, MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO);
                Long masterInstanceNo = null;
                if (config != null && StringUtils.isNotEmpty(config.getConfigValue())) {
                    masterInstanceNo = Long.valueOf(config.getConfigValue());
                }
                masterInstanceNoMap.put(instanceNo, masterInstanceNo);
            }
        }

        return masterInstanceNoMap;
    }

    @Override
    protected Map<String, Object> createComponentMap(Long componentNo, ComponentProcessContext context, boolean start) {
        Map<String, Object> map = super.createComponentMap(componentNo, context, start);

        // masterInstanceNoMap
        Map<Long, Long> masterInstanceNoMap = createMasterInstanceNoMap(componentNo, context, start);
        map.put("masterInstanceNoMap", masterInstanceNoMap);

        // phpMyAdmin
        ComponentConfig componentConfig = componentConfigDao.readByComponentNoAndConfigName(componentNo,
                MySQLConstants.CONFIG_NAME_PHP_MY_ADMIN);
        if (componentConfig != null) {
            boolean phpMyAdmin = BooleanUtils.toBoolean(componentConfig.getConfigValue());
            map.put("phpMyAdmin", phpMyAdmin);
        }

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> createInstanceMap(Long componentNo, ComponentProcessContext context, boolean start,
            Long instanceNo, Map<String, Object> rootMap) {
        Map<String, Object> map = super.createInstanceMap(componentNo, context, start, instanceNo, rootMap);

        // Masterサーバ、Slaveサーバに応じて、サーバ固有の情報モデルを追加する
        Map<Long, Long> masterInstanceNoMap = (Map<Long, Long>) rootMap.get("masterInstanceNoMap");
        Long masterInstanceNo = masterInstanceNoMap.get(instanceNo);
        if (masterInstanceNo == null) {
            // Masterサーバの場合
            map.put("mysqlType", "MASTER");

            // SlaveのInstances
            List<Instance> slaveInstances = new ArrayList<Instance>();
            for (Entry<Long, Long> entry : masterInstanceNoMap.entrySet()) {
                if (instanceNo.equals(entry.getValue())) {
                    Instance instance = instanceDao.read(entry.getKey());
                    slaveInstances.add(instance);
                }
            }
            map.put("slaveInstances", slaveInstances);
        } else {
            // Slaveサーバの場合
            map.put("mysqlType", "SLAVE");
            map.put("masterInstanceNo", masterInstanceNo);

            // MasterのInstance
            ComponentInstance master = componentInstanceDao.read(componentNo, masterInstanceNo);
            if (master != null
                    && ComponentInstanceStatus.fromStatus(master.getStatus()) == ComponentInstanceStatus.RUNNING) {
                Instance masterInstance = instanceDao.read(masterInstanceNo);
                map.put("masterInstance", masterInstance);
            }
        }

        return map;
    }

}
