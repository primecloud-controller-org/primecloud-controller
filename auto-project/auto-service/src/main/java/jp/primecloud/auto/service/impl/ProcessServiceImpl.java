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
package jp.primecloud.auto.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentLoadBalancer;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ProcessServiceImpl extends ServiceSupport implements ProcessService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void startInstances(Long farmNo, List<Long> instanceNos) {
        this.startInstances(farmNo, instanceNos, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startInstances(Long farmNo, List<Long> instanceNos, boolean startComponent) {
        // インスタンスを有効にする
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        for (Instance instance : instances) {
            Platform platform = platformDao.read(instance.getPlatformNo());
            if ("aws".equals(platform.getPlatformType())) {
                PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
                AwsInstance awsInstance = awsInstanceDao.read(instance.getInstanceNo());
                if (platformAws.getVpc() && StringUtils.isEmpty(awsInstance.getSubnetId())) {
                    //EC2+VPCでサブネットが設定されていないサーバは起動不可
                    instanceNos.remove(instance.getInstanceNo());
                    continue;
                }
            }
            if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                instance.setEnabled(true);
                instanceDao.update(instance);
            }
        }

        if (startComponent) {
            // コンポーネントとの関連付けを有効にする
            List<ComponentInstance> componentInstances = componentInstanceDao.readInInstanceNos(instanceNos);
            for (ComponentInstance componentInstance : componentInstances) {
                if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                    // 関連付けが無効なコンポーネントは無効にする
                    if (BooleanUtils.isTrue(componentInstance.getEnabled())
                            || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                        componentInstance.setEnabled(false);
                        componentInstance.setConfigure(true);
                        componentInstanceDao.update(componentInstance);
                    }
                    continue;
                }
                if (BooleanUtils.isNotTrue(componentInstance.getEnabled())
                        || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                    componentInstance.setEnabled(true);
                    componentInstance.setConfigure(true);
                    componentInstanceDao.update(componentInstance);
                }
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopInstances(Long farmNo, List<Long> instanceNos) {
        // インスタンスを無効にする
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        for (Instance instance : instances) {
            if (BooleanUtils.isTrue(instance.getEnabled())) {
                instance.setEnabled(false);
                instanceDao.update(instance);
            }
        }

        // コンポーネントとの関連付けを無効にする
        List<ComponentInstance> componentInstances = componentInstanceDao.readInInstanceNos(instanceNos);
        for (ComponentInstance componentInstance : componentInstances) {
            if (BooleanUtils.isTrue(componentInstance.getEnabled())
                    || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                componentInstance.setEnabled(false);
                componentInstance.setConfigure(true);
                componentInstanceDao.update(componentInstance);
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startComponents(Long farmNo, List<Long> componentNos) {
        // コンポーネントを有効にする
        List<ComponentInstance> componentInstances = componentInstanceDao.readInComponentNos(componentNos);
        for (ComponentInstance componentInstance : componentInstances) {
            if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                // 関連付けが無効なコンポーネントは無効にする
                if (BooleanUtils.isTrue(componentInstance.getEnabled())
                        || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                    componentInstance.setEnabled(false);
                    componentInstance.setConfigure(true);
                    componentInstanceDao.update(componentInstance);
                }
                continue;
            }
            Instance instance = instanceDao.read(componentInstance.getInstanceNo());
            Platform platform = platformDao.read(instance.getPlatformNo());
            if ("aws".equals(platform.getPlatformType())) {
                PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
                AwsInstance awsInstance = awsInstanceDao.read(instance.getInstanceNo());
                if (platformAws.getVpc() && StringUtils.isEmpty(awsInstance.getSubnetId())) {
                    //EC2+VPCでサブネットが設定されていないサーバは起動不可
                    continue;
                }
            }
            if (BooleanUtils.isNotTrue(componentInstance.getEnabled())
                    || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                componentInstance.setEnabled(true);
                componentInstance.setConfigure(true);
                componentInstanceDao.update(componentInstance);
            }
        }

        // コンポーネントに関連するインスタンスを有効にする
        Set<Long> instanceNos = new LinkedHashSet<Long>();
        for (ComponentInstance componentInstance : componentInstances) {
            instanceNos.add(componentInstance.getInstanceNo());
        }
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        for (Instance instance : instances) {
            Platform platform = platformDao.read(instance.getPlatformNo());
            if ("aws".equals(platform.getPlatformType())) {
                PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
                AwsInstance awsInstance = awsInstanceDao.read(instance.getInstanceNo());
                if (platformAws.getVpc() && StringUtils.isEmpty(awsInstance.getSubnetId())) {
                    //EC2+VPCでサブネットが設定されていないサーバは起動不可
                    continue;
                }
            }
            if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                instance.setEnabled(true);
                instanceDao.update(instance);
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startComponents(Long farmNo, Long componentNo, List<Long> instanceNos) {
        // コンポーネントを有効にする
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentNo);
        for (ComponentInstance componentInstance : componentInstances) {
            if (!instanceNos.contains(componentInstance.getInstanceNo())) {
                continue;
            }
            if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                // 関連付けが無効なコンポーネントは無効にする
                if (BooleanUtils.isTrue(componentInstance.getEnabled())
                        || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                    componentInstance.setEnabled(false);
                    componentInstance.setConfigure(true);
                    componentInstanceDao.update(componentInstance);
                }
                continue;
            }
            if (BooleanUtils.isNotTrue(componentInstance.getEnabled())
                    || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                componentInstance.setEnabled(true);
                componentInstance.setConfigure(true);
                componentInstanceDao.update(componentInstance);
            }
        }

        // インスタンスが起動していない場合は起動する
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        for (Instance instance : instances) {
            if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                instance.setEnabled(true);
                instanceDao.update(instance);
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopComponents(Long farmNo, List<Long> componentNos) {
        stopComponents(farmNo, componentNos, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopComponents(Long farmNo, List<Long> componentNos, boolean stopInstance) {
        // コンポーネントを無効にする
        List<ComponentInstance> componentInstances = componentInstanceDao.readInComponentNos(componentNos);
        for (ComponentInstance componentInstance : componentInstances) {
            if (BooleanUtils.isTrue(componentInstance.getEnabled())
                    || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                componentInstance.setEnabled(false);
                componentInstance.setConfigure(true);
                componentInstanceDao.update(componentInstance);
            }
        }

        // 関連する全てのコンポーネントが無効なインスタンスを無効にする
        if (stopInstance) {
            Set<Long> instanceNos = new LinkedHashSet<Long>();
            for (ComponentInstance componentInstance : componentInstances) {
                instanceNos.add(componentInstance.getInstanceNo());
            }
            List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
            for (Instance instance : instances) {
                if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                    continue;
                }

                // 全てのコンポーネントが無効かどうかのチェック
                boolean allDisabled = true;
                List<ComponentInstance> componentInstances2 = componentInstanceDao.readByInstanceNo(instance
                        .getInstanceNo());
                for (ComponentInstance componentInstance : componentInstances2) {
                    if (BooleanUtils.isTrue(componentInstance.getEnabled())) {
                        allDisabled = false;
                        break;
                    }
                }

                // 全てのコンポーネントが無効の場合
                if (allDisabled) {
                    instance.setEnabled(false);
                    instanceDao.update(instance);
                }
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopComponents(Long farmNo, Long componentNo, List<Long> instanceNos, boolean stopInstance) {
        // コンポーネントを無効にする
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentNo);
        for (ComponentInstance componentInstance : componentInstances) {
            if (!instanceNos.contains(componentInstance.getInstanceNo())) {
                continue;
            }
            if (BooleanUtils.isTrue(componentInstance.getEnabled())
                    || BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
                componentInstance.setEnabled(false);
                componentInstance.setConfigure(true);
                componentInstanceDao.update(componentInstance);
            }
        }

        if (stopInstance) {
            // 関連する全てのコンポーネントが無効なインスタンスを無効にする
            for (ComponentInstance componentInstance : componentInstances) {
                instanceNos.add(componentInstance.getInstanceNo());
            }
            List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
            for (Instance instance : instances) {
                if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                    continue;
                }

                // 全てのコンポーネントが無効かどうかのチェック
                boolean allDisabled = true;
                List<ComponentInstance> componentInstances2 = componentInstanceDao.readByInstanceNo(instance
                        .getInstanceNo());
                for (ComponentInstance componentInstance : componentInstances2) {
                    if (BooleanUtils.isTrue(componentInstance.getEnabled())) {
                        allDisabled = false;
                        break;
                    }
                }

                // 全てのコンポーネントが無効の場合
                if (allDisabled) {
                    instance.setEnabled(false);
                    instanceDao.update(instance);
                }
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateComponents(Long farmNo) {
        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLoadBalancers(Long farmNo, List<Long> loadBalancerNos) {
        // ロードバランサを有効にする
        List<LoadBalancer> loadBalancers = loadBalancerDao.readInLoadBalancerNos(loadBalancerNos);
        for (LoadBalancer loadBalancer : loadBalancers) {
            if (BooleanUtils.isNotTrue(loadBalancer.getEnabled())
                    || BooleanUtils.isNotTrue(loadBalancer.getConfigure())) {
                loadBalancer.setEnabled(true);
                loadBalancer.setConfigure(true);
                loadBalancerDao.update(loadBalancer);
            }
        }

        // コンポーネント型ロードバランサの場合、インスタンスやコンポーネントを有効にする
        List<ComponentLoadBalancer> componentLoadBalancers = componentLoadBalancerDao
                .readInLoadBalancerNos(loadBalancerNos);
        if (!componentLoadBalancers.isEmpty()) {
            // 振り分け対象のコンポーネント番号を取得
            Set<Long> componentNos = new HashSet<Long>();
            for (ComponentLoadBalancer componentLoadBalancer : componentLoadBalancers) {
                componentNos.add(componentLoadBalancer.getComponentNo());
            }

            // コンポーネントを有効にする
            List<ComponentInstance> componentInstances = componentInstanceDao.readInComponentNos(componentNos);
            for (ComponentInstance componentInstance : componentInstances) {
                if (BooleanUtils.isNotTrue(componentInstance.getEnabled())) {
                    componentInstance.setEnabled(true);
                    componentInstanceDao.update(componentInstance);
                }
            }

            // インスタンスを有効にする
            Set<Long> instanceNos = new HashSet<Long>();
            for (ComponentInstance componentInstance : componentInstances) {
                instanceNos.add(componentInstance.getInstanceNo());
            }
            List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
            for (Instance instance : instances) {
                if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                    instance.setEnabled(true);
                    instanceDao.update(instance);
                }
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLoadBalancers(Long farmNo, List<Long> loadBalancerNos) {
        // ロードバランサを無効にする
        List<LoadBalancer> loadBalancers = loadBalancerDao.readInLoadBalancerNos(loadBalancerNos);
        for (LoadBalancer loadBalancer : loadBalancers) {
            if (BooleanUtils.isTrue(loadBalancer.getEnabled()) || BooleanUtils.isNotTrue(loadBalancer.getConfigure())) {
                loadBalancer.setEnabled(false);
                loadBalancer.setConfigure(true);
                loadBalancerDao.update(loadBalancer);
            }
        }

        // コンポーネント型ロードバランサの場合、インスタンスやコンポーネントを無効にする
        List<ComponentLoadBalancer> componentLoadBalancers = componentLoadBalancerDao
                .readInLoadBalancerNos(loadBalancerNos);
        if (!componentLoadBalancers.isEmpty()) {
            // 振り分け対象のコンポーネント番号を取得
            Set<Long> componentNos = new HashSet<Long>();
            for (ComponentLoadBalancer componentLoadBalancer : componentLoadBalancers) {
                componentNos.add(componentLoadBalancer.getComponentNo());
            }

            // コンポーネントを無効にする
            List<ComponentInstance> componentInstances = componentInstanceDao.readInComponentNos(componentNos);
            for (ComponentInstance componentInstance : componentInstances) {
                if (BooleanUtils.isTrue(componentInstance.getEnabled())) {
                    componentInstance.setEnabled(false);
                    componentInstanceDao.update(componentInstance);
                }
            }

            // インスタンスを無効にする
            Set<Long> instanceNos = new HashSet<Long>();
            for (ComponentInstance componentInstance : componentInstances) {
                instanceNos.add(componentInstance.getInstanceNo());
            }
            List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
            for (Instance instance : instances) {
                if (BooleanUtils.isTrue(instance.getEnabled())) {
                    instance.setEnabled(false);
                    instanceDao.update(instance);
                }
            }
        }

        // リスナーを無効にする
        List<LoadBalancerListener> listeners = loadBalancerListenerDao.readInLoadBalancerNos(loadBalancerNos);
        for (LoadBalancerListener listener : listeners) {
            if (BooleanUtils.isTrue(listener.getEnabled())) {
                listener.setEnabled(false);
                loadBalancerListenerDao.update(listener);
            }
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLoadBalancerListeners(Long farmNo, Long loadBalancerNo, List<Integer> loadBalancerPorts) {
        if (loadBalancerPorts.isEmpty()) {
            return;
        }

        // リスナーを有効にする
        List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerListener listener : listeners) {
            if (!loadBalancerPorts.contains(listener.getLoadBalancerPort())) {
                continue;
            }
            if (BooleanUtils.isNotTrue(listener.getEnabled()) || BooleanUtils.isNotTrue(listener.getConfigure())) {
                listener.setEnabled(true);
                listener.setConfigure(true);
                loadBalancerListenerDao.update(listener);
            }
        }

        // ロードバランサを有効にする
        startLoadBalancers(farmNo, Arrays.asList(loadBalancerNo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLoadBalancerListeners(Long farmNo, Long loadBalancerNo, List<Integer> loadBalancerPorts) {
        if (loadBalancerPorts.isEmpty()) {
            return;
        }

        // リスナーを無効にする
        List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerListener listener : listeners) {
            if (!loadBalancerPorts.contains(listener.getLoadBalancerPort())) {
                continue;
            }
            if (BooleanUtils.isTrue(listener.getEnabled()) || BooleanUtils.isNotTrue(listener.getConfigure())) {
                listener.setEnabled(false);
                listener.setConfigure(true);
                loadBalancerListenerDao.update(listener);
            }
        }

        // ロードバランサを設定対象にする
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (BooleanUtils.isNotTrue(loadBalancer.getConfigure())) {
            loadBalancer.setConfigure(true);
            loadBalancerDao.update(loadBalancer);
        }

        // ファームを更新処理対象として登録
        scheduleFarm(farmNo);
    }

    protected void scheduleFarm(Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        if (BooleanUtils.isNotTrue(farm.getScheduled())) {
            farm.setScheduled(true);
            farmDao.update(farm);
        }
    }

}
