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
package jp.primecloud.auto.component.apache.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.component.apache.ApacheConstants;
import jp.primecloud.auto.component.geronimo.GeronimoConstants;
import jp.primecloud.auto.component.tomcat.TomcatConstants;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.process.ComponentProcessContext;
import jp.primecloud.auto.process.puppet.PuppetComponentProcess;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ApachePuppetComponentProcess extends PuppetComponentProcess {

    /**
     * TODO: コンストラクタコメントを記述
     */
    public ApachePuppetComponentProcess() {
        componentTypeName = ApacheConstants.COMPONENT_TYPE_NAME;
        awsVolumeDevice = ApacheConstants.AWS_VOLUME_DEVICE;
        vmwareDiskScsiId = ApacheConstants.VMWARE_DISK_SCSI_ID;
    }

    @Override
    protected Map<String, Object> createComponentMap(Long componentNo, ComponentProcessContext context, boolean start) {
        Map<String, Object> map = super.createComponentMap(componentNo, context, start);

        // for arvicio Sample
        Component component = componentDao.read(componentNo);
        List<Component> components = componentDao.readByFarmNo(component.getFarmNo());
        for (Component apComponent : components) {
            ComponentType apComponentType = componentTypeDao.read(apComponent.getComponentTypeNo());

            // Tomcatコンポーネントの場合
            if (TomcatConstants.COMPONENT_TYPE_NAME.equals(apComponentType.getComponentTypeName())) {
                List<Instance> sampleTomcatInstances;
                List<Long> apInstanceNos = context.getEnableInstanceNoMap().get(apComponent.getComponentNo());
                if (apInstanceNos == null || apInstanceNos.isEmpty()) {
                    sampleTomcatInstances = new ArrayList<Instance>();
                } else {
                    sampleTomcatInstances = instanceDao.readInInstanceNos(apInstanceNos);
                }
                map.put("sampleTomcatInstances", sampleTomcatInstances);
                break;
            }
            // Geronimoコンポーネントの場合
            if (GeronimoConstants.COMPONENT_TYPE_NAME.equals(apComponentType.getComponentTypeName())) {
                List<Instance> sampleGeronimoInstances;
                List<Long> apInstanceNos = context.getEnableInstanceNoMap().get(apComponent.getComponentNo());
                if (apInstanceNos == null || apInstanceNos.isEmpty()) {
                    sampleGeronimoInstances = new ArrayList<Instance>();
                } else {
                    sampleGeronimoInstances = instanceDao.readInInstanceNos(apInstanceNos);
                }
                map.put("sampleGeronimoInstances", sampleGeronimoInstances);
                break;
            }
        }

        return map;
    }

}
