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
import jp.primecloud.auto.entity.crud.ComponentLoadBalancer;
import jp.primecloud.auto.entity.crud.Instance;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentLoadBalancerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ComponentLoadBalancer componentLoadBalancer;

    private Component component;

    private List<Instance> instances;

    private String ipAddress;

    /**
     * componentLoadBalancerを取得します。
     *
     * @return componentLoadBalancer
     */
    public ComponentLoadBalancer getComponentLoadBalancer() {
        return componentLoadBalancer;
    }

    /**
     * componentLoadBalancerを設定します。
     *
     * @param componentLoadBalancer componentLoadBalancer
     */
    public void setComponentLoadBalancer(ComponentLoadBalancer componentLoadBalancer) {
        this.componentLoadBalancer = componentLoadBalancer;
    }

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
     * instancesを取得します。
     *
     * @return instances
     */
    public List<Instance> getInstances() {
        return instances;
    }

    /**
     * instancesを設定します。
     *
     * @param instances instances
     */
    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }

    /**
     * ipAddressを取得します。
     *
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * ipAddressを設定します。
     *
     * @param ipAddress ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
