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

import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.CloudstackLoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class LoadBalancerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private LoadBalancer loadBalancer;

    private PlatformDto platform;

    private AwsLoadBalancer awsLoadBalancer;

    private CloudstackLoadBalancer cloudstackLoadBalancer;

    private ComponentLoadBalancerDto componentLoadBalancerDto;

    private List<LoadBalancerListener> loadBalancerListeners;

    private LoadBalancerHealthCheck loadBalancerHealthCheck;

    private List<LoadBalancerInstance> loadBalancerInstances;

    private AutoScalingConfDto autoScalingConf;

    /**
     * AutoScalingConfを取得します。
     *
     * @return AutoScalingConf
     */
    public AutoScalingConfDto getAutoScalingConf() {
        return autoScalingConf;
    }

    /**
     * AutoScalingConfを設定します。
     *
     * @param AutoScalingConf autoScalingConf
     */
    public void setAutoScalingConf(AutoScalingConfDto autoScalingConf) {
        this.autoScalingConf = autoScalingConf;
    }

    /**
     * loadBalancerを取得します。
     *
     * @return loadBalancer
     */
    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * loadBalancerを設定します。
     *
     * @param loadBalancer loadBalancer
     */
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * platformを取得します。
     *
     * @return platform
     */
    public PlatformDto getPlatform() {
        return platform;
    }

    /**
     * platformを設定します。
     *
     * @param platform platform
     */
    public void setPlatform(PlatformDto platform) {
        this.platform = platform;
    }

    /**
     * cloudstackLoadBalancerを取得します。
     *
     * @return cloudstackLoadBalancer
     */
    public CloudstackLoadBalancer getCloudstackLoadBalancer() {
        return cloudstackLoadBalancer;
    }

    /**
     * cloudstackLoadBalancerを設定します。
     *
     * @param cloudstackLoadBalancer cloudstackLoadBalancer
     */
    public void setCloudstackLoadBalancer(CloudstackLoadBalancer cloudstackLoadBalancer) {
        this.cloudstackLoadBalancer = cloudstackLoadBalancer;
    }

    /**
     * awsLoadBalancerを取得します。
     *
     * @return awsLoadBalancer
     */
    public AwsLoadBalancer getAwsLoadBalancer() {
        return awsLoadBalancer;
    }

    /**
     * awsLoadBalancerを設定します。
     *
     * @param awsLoadBalancer awsLoadBalancer
     */
    public void setAwsLoadBalancer(AwsLoadBalancer awsLoadBalancer) {
        this.awsLoadBalancer = awsLoadBalancer;
    }

    /**
     * componentLoadBalancerDtoを取得します。
     *
     * @return componentLoadBalancerDto
     */
    public ComponentLoadBalancerDto getComponentLoadBalancerDto() {
        return componentLoadBalancerDto;
    }

    /**
     * componentLoadBalancerDtoを設定します。
     *
     * @param componentLoadBalancerDto componentLoadBalancerDto
     */
    public void setComponentLoadBalancerDto(ComponentLoadBalancerDto componentLoadBalancerDto) {
        this.componentLoadBalancerDto = componentLoadBalancerDto;
    }

    /**
     * loadBalancerListenersを取得します。
     *
     * @return loadBalancerListeners
     */
    public List<LoadBalancerListener> getLoadBalancerListeners() {
        return loadBalancerListeners;
    }

    /**
     * loadBalancerListenersを設定します。
     *
     * @param loadBalancerListeners loadBalancerListeners
     */
    public void setLoadBalancerListeners(List<LoadBalancerListener> loadBalancerListeners) {
        this.loadBalancerListeners = loadBalancerListeners;
    }

    /**
     * loadBalancerHealthCheckを取得します。
     *
     * @return loadBalancerHealthCheck
     */
    public LoadBalancerHealthCheck getLoadBalancerHealthCheck() {
        return loadBalancerHealthCheck;
    }

    /**
     * loadBalancerHealthCheckを設定します。
     *
     * @param loadBalancerHealthCheck loadBalancerHealthCheck
     */
    public void setLoadBalancerHealthCheck(LoadBalancerHealthCheck loadBalancerHealthCheck) {
        this.loadBalancerHealthCheck = loadBalancerHealthCheck;
    }

    /**
     * loadBalancerInstancesを取得します。
     *
     * @return loadBalancerInstances
     */
    public List<LoadBalancerInstance> getLoadBalancerInstances() {
        return loadBalancerInstances;
    }

    /**
     * loadBalancerInstancesを設定します。
     *
     * @param loadBalancerInstances loadBalancerInstances
     */
    public void setLoadBalancerInstances(List<LoadBalancerInstance> loadBalancerInstances) {
        this.loadBalancerInstances = loadBalancerInstances;
    }

}
