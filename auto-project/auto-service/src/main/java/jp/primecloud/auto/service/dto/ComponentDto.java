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
package jp.primecloud.auto.service.dto;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.InstanceConfig;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Component component;

    private ComponentType componentType;

    private List<ComponentConfig> componentConfigs;

    private List<ComponentInstanceDto> componentInstances;

    private List<InstanceConfig> instanceConfigs;

    private String status;

    /**
     * componentを取得します。
     *
     * @return component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * componentを設定します。
     *
     * @param component component
     */
    public void setComponent(Component component) {
        this.component = component;
    }

    /**
     * componentTypeを取得します。
     *
     * @return componentType
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * componentTypeを設定します。
     *
     * @param componentType componentType
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * componentConfigsを取得します。
     *
     * @return componentConfigs
     */
    public List<ComponentConfig> getComponentConfigs() {
        return componentConfigs;
    }

    /**
     * componentConfigsを設定します。
     *
     * @param componentConfigs componentConfigs
     */
    public void setComponentConfigs(List<ComponentConfig> componentConfigs) {
        this.componentConfigs = componentConfigs;
    }

    /**
     * componentInstancesを取得します。
     *
     * @return componentInstances
     */
    public List<ComponentInstanceDto> getComponentInstances() {
        return componentInstances;
    }

    /**
     * componentInstancesを設定します。
     *
     * @param componentInstances componentInstances
     */
    public void setComponentInstances(List<ComponentInstanceDto> componentInstances) {
        this.componentInstances = componentInstances;
    }

    /**
     * instanceConfigsを取得します。
     *
     * @return instanceConfigs
     */
    public List<InstanceConfig> getInstanceConfigs() {
        return instanceConfigs;
    }

    /**
     * instanceConfigsを設定します。
     *
     * @param instanceConfigs instanceConfigs
     */
    public void setInstanceConfigs(List<InstanceConfig> instanceConfigs) {
        this.instanceConfigs = instanceConfigs;
    }

    /**
     * statusを取得します。
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     *
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
