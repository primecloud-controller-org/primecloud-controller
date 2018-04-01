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
package jp.primecloud.auto.component.tomcat.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.component.tomcat.TomcatConstants;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.process.ComponentProcessContext;
import jp.primecloud.auto.process.puppet.PuppetComponentProcess;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class TomcatPuppetComponentProcess extends PuppetComponentProcess {

    /**
     * TODO: コンストラクタコメントを記述
     */
    public TomcatPuppetComponentProcess() {
        componentTypeName = TomcatConstants.COMPONENT_TYPE_NAME;
        awsVolumeDevice = TomcatConstants.AWS_VOLUME_DEVICE;
        vmwareDiskScsiId = TomcatConstants.VMWARE_DISK_SCSI_ID;
    }

    @Override
    protected Map<String, Object> createComponentMap(Long componentNo, ComponentProcessContext context, boolean start) {
        Map<String, Object> map = super.createComponentMap(componentNo, context, start);

        // データソース用
        List<Map<String, Object>> databases = new ArrayList<Map<String, Object>>();
        List<Component> components = componentDao.readByFarmNo(context.getFarmNo());
        for (Component dbComponent : components) {
            // MySQLのコンポーネントかどうかのチェック
            ComponentType dbComponentType = componentTypeDao.read(dbComponent.getComponentTypeNo());
            if (MySQLConstants.COMPONENT_TYPE_NAME.equals(dbComponentType.getComponentTypeName())) {
                Map<String, Object> database = new HashMap<String, Object>();
                database.put("component", dbComponent);

                // Masterのサーバを取得
                Long masterInstanceNo = null;
                List<Long> dbInstanceNos = context.getEnableInstanceNoMap().get(dbComponent.getComponentNo());
                if (dbInstanceNos != null && !dbInstanceNos.isEmpty()) {
                    List<InstanceConfig> configs = instanceConfigDao.readByComponentNo(dbComponent.getComponentNo());
                    for (InstanceConfig config : configs) {
                        if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(config.getConfigName())
                                && StringUtils.isEmpty(config.getConfigValue())) {
                            if (dbInstanceNos.contains(config.getInstanceNo())) {
                                masterInstanceNo = config.getInstanceNo();
                            }
                            break;
                        }
                    }
                }
                if (masterInstanceNo != null) {
                    // Masterの情報を格納
                    Instance masterInstance = instanceDao.read(masterInstanceNo);
                    database.put("instance", masterInstance);
                }

                databases.add(database);
            }
        }
        map.put("databases", databases);

        // for arvicio Sample
        Instance sampleDbInstance = null;
        if (!databases.isEmpty()) {
            sampleDbInstance = (Instance) databases.get(0).get("instance");
        }
        map.put("sampleDbInstance", sampleDbInstance);

        return map;
    }

}
