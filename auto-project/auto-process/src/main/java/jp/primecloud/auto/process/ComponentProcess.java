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
package jp.primecloud.auto.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.PuppetInstance;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.process.lb.LoadBalancerProcess;
import jp.primecloud.auto.process.puppet.PuppetComponentProcess;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentProcess extends ServiceSupport {

    protected PuppetComponentProcess puppetComponentProcess;

    protected PuppetComponentProcess[] extPuppetComponentProcesses;

    protected LoadBalancerProcess loadBalancerProcess;

    protected ExecutorService executorService;

    public void configure(Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        if (BooleanUtils.isTrue(farm.getComponentProcessing())) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                String message = MessageUtils.format("Component is being configured.(farmNo={0})", farmNo);
                log.debug(message);
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100011", farmNo));
        }

        // ステータス変更
        farm.setComponentProcessing(true);
        farmDao.update(farm);

        try {
            // 処理対象の情報を取得
            ComponentProcessContext context = createContext(farmNo);

            List<Component> components = null;
            if (!context.getRunningInstanceNos().isEmpty()) {
                // インスタンスのノードのマニフェストを生成
                puppetComponentProcess.createNodeManifest(context);
                components = getComponents(farmNo);
            }

            // コンポーネントに関する開始処理
            if (!context.getRunningInstanceNos().isEmpty()) {
                for (int i = 0; i < components.size(); i++) {
                    Component component = components.get(i);

                    // ログ用情報を格納
                    LoggingUtils.setComponentNo(component.getComponentNo());
                    LoggingUtils.setComponentName(component.getComponentName());

                    startPuppet(components.get(i), context);

                    // 正常に終了した場合のみログ用情報をクリアする
                    LoggingUtils.setComponentNo(null);
                    LoggingUtils.setComponentName(null);
                }
            }

            // ロードバランサの設定処理
            if (!context.getTargetLoadBalancerNos().isEmpty()) {
                // 並列実行
                List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
                final Map<String, Object> loggingContext = LoggingUtils.getContext();
                for (final Long loadBalancerNo : context.getTargetLoadBalancerNos()) {
                    Callable<Void> callable = new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            LoggingUtils.setContext(loggingContext);
                            try {
                                loadBalancerProcess.configure(loadBalancerNo);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);

                                // TODO: イベントログ出力

                                throw e;
                            } finally {
                                LoggingUtils.removeContext();
                            }
                            return null;
                        }
                    };
                    callables.add(callable);
                }

                try {
                    List<Future<Void>> futures = executorService.invokeAll(callables);

                    // 並列実行で例外発生時の処理
                    List<Throwable> throwables = new ArrayList<Throwable>();
                    for (Future<Void> future : futures) {
                        try {
                            future.get();
                        } catch (ExecutionException e) {
                            throwables.add(e.getCause());
                        } catch (InterruptedException ignore) {
                        }
                    }

                    // 例外を処理する
                    if (throwables.size() > 0) {
                        throw new MultiCauseException(throwables.toArray(new Throwable[throwables.size()]));
                    }
                } catch (InterruptedException e) {
                }
            }

            // コンポーネントに関する終了処理
            if (!context.getRunningInstanceNos().isEmpty()) {
                for (int i = components.size() - 1; i >= 0; i--) {
                    Component component = components.get(i);

                    // ログ用情報を格納
                    LoggingUtils.setComponentNo(component.getComponentNo());
                    LoggingUtils.setComponentName(component.getComponentName());

                    stopPuppet(components.get(i), context);

                    // 正常に終了した場合のみログ用情報をクリアする
                    LoggingUtils.setComponentNo(null);
                    LoggingUtils.setComponentName(null);
                }
            }
        } finally {
            // ステータス変更
            farm = farmDao.read(farmNo);
            farm.setComponentProcessing(false);
            farmDao.update(farm);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100012", farmNo));
        }
    }

    protected ComponentProcessContext createContext(Long farmNo) {
        ComponentProcessContext context = new ComponentProcessContext();
        context.setFarmNo(farmNo);

        // 処理対象のサーバを取得
        List<Instance> instances = instanceDao.readByFarmNo(farmNo);
        List<Long> runningInstanceNos = new ArrayList<Long>();
        for (Instance instance : instances) {
            // サーバが起動していない場合は対象外
            if (InstanceStatus.fromStatus(instance.getStatus()) != InstanceStatus.RUNNING) {
                continue;
            }

            // ロードバランサのサーバは対象外
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                continue;
            }

            // PuppetInstanceレコードがなければ対象外  ただしOSがwindowsの場合はカウントする
            PuppetInstance puppetInstance = puppetInstanceDao.read(instance.getInstanceNo());
            Image image = imageDao.read(instance.getImageNo());
            if (puppetInstance == null && !StringUtils.startsWithIgnoreCase(image.getOs(), PCCConstant.OS_NAME_WIN)) {
                continue;
            }

            runningInstanceNos.add(instance.getInstanceNo());
        }
        context.setRunningInstanceNos(runningInstanceNos);

        // 処理対象のコンポーネントを取得
        List<ComponentInstance> componentInstances = componentInstanceDao.readInInstanceNos(runningInstanceNos);
        Map<Long, List<Long>> enableInstanceNoMap = new HashMap<Long, List<Long>>();
        Map<Long, List<Long>> disableInstanceNoMap = new HashMap<Long, List<Long>>();
        for (ComponentInstance componentInstance : componentInstances) {
            Map<Long, List<Long>> map;
            if (BooleanUtils.isTrue(componentInstance.getEnabled())) {
                map = enableInstanceNoMap;
            } else {
                map = disableInstanceNoMap;
            }

            List<Long> list = map.get(componentInstance.getComponentNo());
            if (list == null) {
                list = new ArrayList<Long>();
                map.put(componentInstance.getComponentNo(), list);
            }
            list.add(componentInstance.getInstanceNo());
        }
        context.setEnableInstanceNoMap(enableInstanceNoMap);
        context.setDisableInstanceNoMap(disableInstanceNoMap);

        // 処理対象のロードバランサを取得
        List<LoadBalancer> loadBalancers = loadBalancerDao.readByFarmNo(farmNo);
        List<Long> loadBalancerNos = new ArrayList<Long>();
        for (LoadBalancer loadBalancer : loadBalancers) {
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());

            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                // 有効なロードバランサの場合、起動状態でなければ対象外
                if (status != LoadBalancerStatus.RUNNING) {
                    continue;
                }
            } else {
                // 無効なロードバランサの場合、起動または異常状態でなければ対象外
                if (status != LoadBalancerStatus.RUNNING && status != LoadBalancerStatus.WARNING) {
                    continue;
                }
            }

            loadBalancerNos.add(loadBalancer.getLoadBalancerNo());
        }
        context.setTargetLoadBalancerNos(loadBalancerNos);

        return context;
    }

    protected List<Component> getComponents(Long farmNo) {
        // コンポーネントのリスト取得
        List<Component> components = componentDao.readByFarmNo(farmNo);

        // ロードバランサコンポーネントを除外
        List<Component> tmpComponents = new ArrayList<Component>();
        for (Component component : components) {
            if (BooleanUtils.isTrue(component.getLoadBalancer())) {
                continue;
            }
            tmpComponents.add(component);
        }
        components = tmpComponents;

        // コンポーネントを昇順でソート
        Comparator<Component> comparator = new Comparator<Component>() {
            @Override
            public int compare(Component o1, Component o2) {
                ComponentType c1 = componentTypeDao.read(o1.getComponentTypeNo());
                ComponentType c2 = componentTypeDao.read(o2.getComponentTypeNo());
                Integer runOrder1 = c1.getRunOrder();
                Integer runOrder2 = c2.getRunOrder();

                int ro1 = runOrder1 == null ? 0 : runOrder1.intValue();
                int ro2 = runOrder2 == null ? 0 : runOrder2.intValue();

                return ro1 - ro2;
            }
        };

        Collections.sort(components, comparator);

        return components;
    }

    protected void startPuppet(Component component, ComponentProcessContext context) {
        // ComponentTypeNameが一致する拡張クラスがあれば、それを利用する
        PuppetComponentProcess puppetComponentProcess = this.puppetComponentProcess;
        if (extPuppetComponentProcesses != null) {
            ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
            for (PuppetComponentProcess process : extPuppetComponentProcesses) {
                if (StringUtils.equals(componentType.getComponentTypeName(), process.getComponentTypeName())) {
                    puppetComponentProcess = process;
                    break;
                }
            }
        }

        puppetComponentProcess.startComponent(component.getComponentNo(), context);
    }

    protected void stopPuppet(Component component, ComponentProcessContext context) {
        // ComponentTypeNameが一致する拡張クラスがあれば、それを利用する
        PuppetComponentProcess puppetComponentProcess = this.puppetComponentProcess;
        if (extPuppetComponentProcesses != null) {
            ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
            for (PuppetComponentProcess process : extPuppetComponentProcesses) {
                if (StringUtils.equals(componentType.getComponentTypeName(), process.getComponentTypeName())) {
                    puppetComponentProcess = process;
                    break;
                }
            }
        }

        try {
            puppetComponentProcess.stopComponent(component.getComponentNo(), context);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * puppetComponentProcessを設定します。
     *
     * @param puppetComponentProcess puppetComponentProcess
     */
    public void setPuppetComponentProcess(PuppetComponentProcess puppetComponentProcess) {
        this.puppetComponentProcess = puppetComponentProcess;
    }

    /**
     * extPuppetComponentProcessesを設定します。
     *
     * @param extPuppetComponentProcesses extPuppetComponentProcesses
     */
    public void setExtPuppetComponentProcesses(PuppetComponentProcess[] extPuppetComponentProcesses) {
        this.extPuppetComponentProcesses = extPuppetComponentProcesses;
    }

    /**
     * loadBalancerProcessを設定します。
     *
     * @param loadBalancerProcess loadBalancerProcess
     */
    public void setLoadBalancerProcess(LoadBalancerProcess loadBalancerProcess) {
        this.loadBalancerProcess = loadBalancerProcess;
    }

    /**
     * executorServiceを設定します。
     *
     * @param executorService executorService
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

}
