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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.InstanceCoodinateStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.process.lb.LoadBalancerProcess;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ProcessManager extends ServiceSupport {

    protected InstanceProcess instanceProcess;

    protected ComponentProcess componentProcess;

    protected InstancesProcess instancesProcess;

    protected LoadBalancerProcess loadBalancerProcess;

    protected ExecutorService executorService;

    protected EventLogger eventLogger;

    public void process() {
        // 処理対象のファームを取得
        List<Farm> farms = new ArrayList<Farm>();
        List<Farm> allFarms = farmDao.readAll();
        for (Farm farm : allFarms) {
            if (BooleanUtils.isTrue(farm.getScheduled())) {
                farms.add(farm);
            }
        }

        List<Long> farmNos = new ArrayList<Long>();
        for (Farm farm : farms) {
            farmNos.add(farm.getFarmNo());
        }

        log.debug("farmNos: " + farmNos);

        if (farms.isEmpty()) {
            return;
        }

        for (Farm farm : farms) {
            // TODO: ファーム処理の実行停止状態の場合はスキップ

            processFarm(farm);
        }
    }

    protected void processFarm(Farm farm) {
        // インスタンス、ロードバランサの開始処理
        boolean next = processStartInstance(farm);
        boolean next2 = processStartLoadBalancer(farm);
        if (!next || !next2) {
            return;
        }

        // インスタンス群の協調開始処理
        next = processStartInstances(farm);
        if (!next) {
            return;
        }

        // コンポーネントの設定処理
        next = processConfigureComponent(farm);
        if (!next) {
            return;
        }

        // インスタンス群の協調停止処理
        next = processStopInstances(farm);
        if (!next) {
            return;
        }

        // インスタンス、ロードバランサの停止処理
        next = processStopInstance(farm);
        next2 = processStopLoadBalancer(farm);
        if (!next || !next2) {
            return;
        }

        // ファームを処理対象外にする
        unscheduled(farm.getFarmNo());
    }

    protected void unscheduled(Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        if (BooleanUtils.isTrue(farm.getScheduled())) {
            farm.setScheduled(false);
            farmDao.update(farm);
        }
    }

    protected boolean processStartInstance(final Farm farm) {
        // 有効なインスタンスを取得
        List<Instance> instances = new ArrayList<Instance>();
        List<Instance> allInstances = instanceDao.readByFarmNo(farm.getFarmNo());
        for (Instance instance : allInstances) {
            // ロードバランサインスタンスは対象外
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                continue;
            }

            if (BooleanUtils.isTrue(instance.getEnabled())) {
                instances.add(instance);
            }
        }

        // 有効なインスタンスが無い場合
        if (instances.isEmpty()) {
            return true;
        }

        // インスタンスの状態を取得
        boolean processing = false;
        List<Long> targetInstanceNos = new ArrayList<Long>();
        for (Instance instance : instances) {
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());

            if (status != InstanceStatus.RUNNING && status != InstanceStatus.WARNING) {
                processing = true;

                if (status == InstanceStatus.STOPPED) {
                    targetInstanceNos.add(instance.getInstanceNo());
                }
            }
        }

        // 停止しているインスタンスを起動する
        if (!targetInstanceNos.isEmpty()) {
            final User user = userDao.read(farm.getUserNo());
            for (final Long instanceNo : targetInstanceNos) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        LoggingUtils.setUserNo(user.getMasterUser());
                        LoggingUtils.setUserName(user.getUsername());
                        LoggingUtils.setFarmNo(farm.getFarmNo());
                        LoggingUtils.setFarmName(farm.getFarmName());
                        LoggingUtils.setLoginUserNo(user.getUserNo());
                        try {
                            instanceProcess.start(instanceNo);
                        } catch (MultiCauseException ignore) {
                        } catch (Throwable e) {
                            log.error(e.getMessage(), e);

                            // イベントログ出力
                            eventLogger.error("SystemError", new Object[] { e.getMessage() });
                        } finally {
                            LoggingUtils.removeContext();
                        }
                    }
                };
                executorService.execute(runnable);
            }
        }

        // 処理中のインスタンスがある場合
        if (processing) {
            return false;
        }

        return true;
    }

    protected boolean processStartInstances(final Farm farm) {
        // コンポーネントが処理中の場合
        if (BooleanUtils.isTrue(farm.getComponentProcessing())) {
            return false;
        }

        // 起動しているインスタンスの中で、有効だが協調していないものがあるかどうかチェック
        boolean coodinated = true;
        List<Instance> allInstances = instanceDao.readByFarmNo(farm.getFarmNo());
        for (Instance instance : allInstances) {
            if (InstanceStatus.fromStatus(instance.getStatus()) == InstanceStatus.RUNNING) {
                if (BooleanUtils.isTrue(instance.getEnabled())) {
                    if (InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus()) == InstanceCoodinateStatus.UN_COODINATED) {
                        coodinated = false;
                        break;
                    }
                }
            }
        }

        // 協調すべきインスタンスがある場合、インスタンス群の協調設定処理を実行
        if (!coodinated) {
            final User user = userDao.read(farm.getUserNo());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LoggingUtils.setUserNo(user.getMasterUser());
                    LoggingUtils.setUserName(user.getUsername());
                    LoggingUtils.setFarmNo(farm.getFarmNo());
                    LoggingUtils.setFarmName(farm.getFarmName());
                    LoggingUtils.setLoginUserNo(user.getUserNo());
                    try {
                        instancesProcess.start(farm.getFarmNo());
                    } catch (MultiCauseException ignore) {
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);

                        // イベントログ出力
                        eventLogger.error("SystemError", new Object[] { e.getMessage() });
                    } finally {
                        LoggingUtils.removeContext();
                    }
                }
            };
            executorService.execute(runnable);
        }

        return coodinated;
    }

    protected boolean processConfigureComponent(final Farm farm) {
        // コンポーネントが処理中の場合
        if (BooleanUtils.isTrue(farm.getComponentProcessing())) {
            return false;
        }

        boolean configured = true;

        // 起動中のInstanceのComponentInstnaceを全て取得
        List<Instance> instances = instanceDao.readByFarmNo(farm.getFarmNo());
        List<Long> instanceNos = new ArrayList<Long>();
        for (Instance instance : instances) {
            // ロードバランサインスタンスは対象外
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                continue;
            }

            if (InstanceStatus.fromStatus(instance.getStatus()) == InstanceStatus.RUNNING) {
                instanceNos.add(instance.getInstanceNo());
            }
        }
        List<ComponentInstance> componentInstances = componentInstanceDao.readInInstanceNos(instanceNos);

        // ComponentInstanceが設定済みかどうかのチェック
        for (ComponentInstance componentInstance : componentInstances) {
            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
            if (BooleanUtils.isTrue(componentInstance.getEnabled())) {
                if (status == ComponentInstanceStatus.WARNING) {
                    // 起動対象のコンポーネントが異常状態の場合、設定処理をスキップする
                    unscheduled(farm.getFarmNo());
                    return false;
                } else if (status != ComponentInstanceStatus.RUNNING) {
                    configured = false;
                }
            } else {
                if (status != ComponentInstanceStatus.STOPPED) {
                    configured = false;
                }
            }
        }

        // ロードバランサ情報の取得
        List<LoadBalancer> loadBalancers = null;
        List<LoadBalancerListener> listeners = null;
        List<LoadBalancerInstance> lbInstances = null;
        Map<Long, LoadBalancer> loadBalancerMap = null;
        Map<Long, Instance> instanceMap = null;
        if (configured) {
            loadBalancers = loadBalancerDao.readByFarmNo(farm.getFarmNo());
            loadBalancerMap = new HashMap<Long, LoadBalancer>();
            for (LoadBalancer loadBalancer : loadBalancers) {
                loadBalancerMap.put(loadBalancer.getLoadBalancerNo(), loadBalancer);
            }
            listeners = loadBalancerListenerDao.readInLoadBalancerNos(loadBalancerMap.keySet());
            lbInstances = loadBalancerInstanceDao.readInLoadBalancerNos(loadBalancerMap.keySet());
            instanceMap = new HashMap<Long, Instance>();
            for (Instance instance : instances) {
                instanceMap.put(instance.getInstanceNo(), instance);
            }
        }

        // ロードバランサリスナーが設定済みかどうかチェック
        if (configured) {
            for (LoadBalancerListener listener : listeners) {
                LoadBalancer loadBalancer = loadBalancerMap.get(listener.getLoadBalancerNo());
                LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
                LoadBalancerListenerStatus status2 = LoadBalancerListenerStatus.fromStatus(listener.getStatus());

                if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                    if (status == LoadBalancerStatus.RUNNING) {
                        // ロードバランサが有効で起動状態の場合
                        if (BooleanUtils.isTrue(listener.getEnabled())) {
                            if (status2 == LoadBalancerListenerStatus.WARNING) {
                                // 起動対象のリスナーが異常状態の場合、設定処理をスキップする
                                unscheduled(farm.getFarmNo());
                                return false;
                            } else if (status2 != LoadBalancerListenerStatus.RUNNING) {
                                configured = false;
                            }
                        } else {
                            if (status2 != LoadBalancerListenerStatus.STOPPED) {
                                configured = false;
                            }
                        }
                    }
                } else {
                    if (status == LoadBalancerStatus.RUNNING || status == LoadBalancerStatus.WARNING) {
                        // ロードバランサが無効で起動または異常状態の場合
                        if (status2 != LoadBalancerListenerStatus.STOPPED) {
                            configured = false;
                        }
                    }
                }
            }
        }

        // ロードバランサインスタンスが設定済みかどうかチェック
        if (configured) {
            for (LoadBalancerInstance lbInstance : lbInstances) {
                LoadBalancer loadBalancer = loadBalancerMap.get(lbInstance.getLoadBalancerNo());
                LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
                LoadBalancerInstanceStatus status2 = LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus());
                Instance instance = instanceMap.get(lbInstance.getInstanceNo());
                InstanceStatus instanceStatus = InstanceStatus.fromStatus(instance.getStatus());

                // インスタンスが起動状態のもののみチェックする
                if (instanceStatus == InstanceStatus.RUNNING) {
                    if (BooleanUtils.isTrue(loadBalancer.getEnabled()) && BooleanUtils.isTrue(instance.getEnabled())) {
                        if (status == LoadBalancerStatus.RUNNING) {
                            // ロードバランサとインスタンスが有効で、ロードバランサが起動状態の場合
                            if (BooleanUtils.isTrue(lbInstance.getEnabled())) {
                                if (status2 == LoadBalancerInstanceStatus.WARNING) {
                                    // 起動対象のロードバランサインスタンスが異常状態の場合、設定処理をスキップする
                                    unscheduled(farm.getFarmNo());
                                    return false;
                                } else if (status2 != LoadBalancerInstanceStatus.RUNNING) {
                                    configured = false;
                                }
                            } else {
                                if (status2 != LoadBalancerInstanceStatus.STOPPED) {
                                    configured = false;
                                }
                            }
                        }
                    } else {
                        if (status == LoadBalancerStatus.RUNNING || status == LoadBalancerStatus.WARNING) {
                            // ロードバランサまたはインスタンスが無効で、ロードバランサが起動または異常状態の場合
                            if (status2 != LoadBalancerInstanceStatus.STOPPED) {
                                configured = false;
                            }
                        }
                    }
                }
            }
        }

        // 強制設定を行うコンポーネントがある場合、未設定状態にする
        if (configured) {
            for (ComponentInstance componentInstance : componentInstances) {
                if (BooleanUtils.isTrue(componentInstance.getConfigure())) {
                    configured = false;
                    break;
                }
            }
        }

        // 設定を行うロードバランサがある場合、未設定状態にする
        if (configured) {
            for (LoadBalancer loadBalancer : loadBalancers) {
                if (BooleanUtils.isTrue(loadBalancer.getConfigure())) {
                    LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
                    if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                        if (status == LoadBalancerStatus.RUNNING) {
                            configured = false;
                            break;
                        }
                    } else {
                        if (status == LoadBalancerStatus.RUNNING || status == LoadBalancerStatus.WARNING) {
                            configured = false;
                            break;
                        }
                    }
                }
            }
        }

        // 設定を行うロードバランサリスナーがある場合、未設定状態にする
        if (configured) {
            for (LoadBalancerListener listener : listeners) {
                if (BooleanUtils.isTrue(listener.getConfigure())) {
                    LoadBalancer loadBalancer = loadBalancerMap.get(listener.getLoadBalancerNo());
                    LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
                    if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                        if (status == LoadBalancerStatus.RUNNING) {
                            configured = false;
                            break;
                        }
                    } else {
                        if (status == LoadBalancerStatus.RUNNING || status == LoadBalancerStatus.WARNING) {
                            configured = false;
                            break;
                        }
                    }
                }
            }
        }

        // 設定済みでない場合、コンポーネントの設定処理を実行
        if (!configured) {
            final User user = userDao.read(farm.getUserNo());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LoggingUtils.setUserNo(user.getMasterUser());
                    LoggingUtils.setUserName(user.getUsername());
                    LoggingUtils.setFarmNo(farm.getFarmNo());
                    LoggingUtils.setFarmName(farm.getFarmName());
                    LoggingUtils.setLoginUserNo(user.getUserNo());
                    try {
                        componentProcess.configure(farm.getFarmNo());
                    } catch (MultiCauseException ignore) {
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);

                        // イベントログ出力
                        eventLogger.error("SystemError", new Object[] { e.getMessage() });
                    } finally {
                        LoggingUtils.removeContext();
                    }
                }
            };
            executorService.execute(runnable);
        }

        return configured;
    }

    protected boolean processStopInstances(final Farm farm) {
        // コンポーネントが処理中の場合
        if (BooleanUtils.isTrue(farm.getComponentProcessing())) {
            return false;
        }

        // 起動しているインスタンスの中で、無効だが協調しているものがあるかどうかチェック
        boolean coodinated = false;
        List<Instance> allInstances = instanceDao.readByFarmNo(farm.getFarmNo());
        for (Instance instance : allInstances) {
            if (InstanceStatus.fromStatus(instance.getStatus()) == InstanceStatus.RUNNING) {
                if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                    if (InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus()) == InstanceCoodinateStatus.COODINATED) {
                        coodinated = true;
                        break;
                    }
                }
            }
        }

        // 協調すべきでないインスタンスがある場合、インスタンス群の協調設定処理を実行
        if (coodinated) {
            final User user = userDao.read(farm.getUserNo());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LoggingUtils.setUserNo(user.getMasterUser());
                    LoggingUtils.setUserName(user.getUsername());
                    LoggingUtils.setFarmNo(farm.getFarmNo());
                    LoggingUtils.setFarmName(farm.getFarmName());
                    LoggingUtils.setLoginUserNo(user.getUserNo());
                    try {
                        instancesProcess.stop(farm.getFarmNo());
                    } catch (MultiCauseException ignore) {
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);

                        // イベントログ出力
                        eventLogger.error("SystemError", new Object[] { e.getMessage() });
                    } finally {
                        LoggingUtils.removeContext();
                    }
                }
            };
            executorService.execute(runnable);
        }

        return !coodinated;
    }

    protected boolean processStopInstance(final Farm farm) {
        // 無効なインスタンスを取得
        List<Instance> instances = new ArrayList<Instance>();
        List<Instance> allInstances = instanceDao.readByFarmNo(farm.getFarmNo());
        for (Instance instance : allInstances) {
            // ロードバランサインスタンスは対象外
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                continue;
            }

            if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                instances.add(instance);
            }
        }

        // 無効なインスタンスが無い場合
        if (instances.isEmpty()) {
            return true;
        }

        // インスタンスの状態を取得
        boolean processing = false;
        List<Long> targetInstanceNos = new ArrayList<Long>();
        for (Instance instance : instances) {
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());

            if (status != InstanceStatus.STOPPED) {
                processing = true;

                if (status == InstanceStatus.RUNNING || status == InstanceStatus.WARNING) {
                    targetInstanceNos.add(instance.getInstanceNo());
                }
            }
        }

        // 起動しているインスタンスを停止する
        if (!targetInstanceNos.isEmpty()) {
            final User user = userDao.read(farm.getUserNo());
            for (final Long instanceNo : targetInstanceNos) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        LoggingUtils.setUserNo(user.getMasterUser());
                        LoggingUtils.setUserName(user.getUsername());
                        LoggingUtils.setFarmNo(farm.getFarmNo());
                        LoggingUtils.setFarmName(farm.getFarmName());
                        LoggingUtils.setLoginUserNo(user.getUserNo());
                        try {
                            log.debug("instanceProcess.stop(instanceNo):"+String.valueOf(instanceNo));
                            instanceProcess.stop(instanceNo);
                        } catch (MultiCauseException ignore) {
                        } catch (Throwable e) {
                            log.error(e.getMessage(), e);

                            // イベントログ出力
                            eventLogger.error("SystemError", new Object[] { e.getMessage() });
                        } finally {
                            LoggingUtils.removeContext();
                        }
                    }
                };
                executorService.execute(runnable);
            }
        }

        // 処理中のインスタンスがある場合
        if (processing) {
            return false;
        }

        return true;
    }

    protected boolean processStartLoadBalancer(final Farm farm) {
        // 有効なロードバランサを取得
        List<LoadBalancer> loadBalancers = new ArrayList<LoadBalancer>();
        List<LoadBalancer> allLoadBalancers = loadBalancerDao.readByFarmNo(farm.getFarmNo());
        for (LoadBalancer loadBalancer : allLoadBalancers) {
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                loadBalancers.add(loadBalancer);
            }
        }

        // 有効なロードバランサがない場合
        if (loadBalancers.isEmpty()) {
            return true;
        }

        // ロードバランサの状態を取得
        boolean processing = false;
        List<Long> targetLoadBalancerNos = new ArrayList<Long>();
        for (LoadBalancer loadBalancer : loadBalancers) {
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());

            if (status != LoadBalancerStatus.RUNNING && status != LoadBalancerStatus.WARNING) {
                processing = true;

                if (status == LoadBalancerStatus.STOPPED) {
                    targetLoadBalancerNos.add(loadBalancer.getLoadBalancerNo());
                }
            }
        }

        // 停止しているロードバランサを起動する
        if (!targetLoadBalancerNos.isEmpty()) {
            final User user = userDao.read(farm.getUserNo());
            for (final Long loadBalancerNo : targetLoadBalancerNos) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        LoggingUtils.setUserNo(user.getMasterUser());
                        LoggingUtils.setUserName(user.getUsername());
                        LoggingUtils.setFarmNo(farm.getFarmNo());
                        LoggingUtils.setFarmName(farm.getFarmName());
                        LoggingUtils.setLoginUserNo(user.getUserNo());
                        try {
                            loadBalancerProcess.start(loadBalancerNo);
                        } catch (MultiCauseException ignore) {
                        } catch (Throwable e) {
                            log.error(e.getMessage(), e);

                            // イベントログ出力
                            eventLogger.error("SystemError", new Object[] { e.getMessage() });
                        } finally {
                            LoggingUtils.removeContext();
                        }
                    }
                };
                executorService.execute(runnable);
            }
        }

        // 処理中のロードバランサがある場合
        if (processing) {
            return false;
        }

        return true;
    }

    protected boolean processStopLoadBalancer(final Farm farm) {
        // 無効なロードバランサを取得
        List<LoadBalancer> loadBalancers = new ArrayList<LoadBalancer>();
        List<LoadBalancer> allLoadBalancers = loadBalancerDao.readByFarmNo(farm.getFarmNo());
        for (LoadBalancer loadBalancer : allLoadBalancers) {
            if (BooleanUtils.isNotTrue(loadBalancer.getEnabled())) {
                loadBalancers.add(loadBalancer);
            }
        }

        // 無効なロードバランサが無い場合
        if (loadBalancers.isEmpty()) {
            return true;
        }

        // ロードバランサの状態を取得
        boolean processing = false;
        List<Long> targetLoadBalancerNos = new ArrayList<Long>();
        for (LoadBalancer loadBalancer : loadBalancers) {
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());

            if (status != LoadBalancerStatus.STOPPED) {
                processing = true;

                if (status == LoadBalancerStatus.RUNNING || status == LoadBalancerStatus.WARNING) {
                    targetLoadBalancerNos.add(loadBalancer.getLoadBalancerNo());
                }
            }
        }

        // 起動しているロードバランサを停止する
        if (!targetLoadBalancerNos.isEmpty()) {
            final User user = userDao.read(farm.getUserNo());
            for (final Long loadBalancerNo : targetLoadBalancerNos) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        LoggingUtils.setUserNo(user.getMasterUser());
                        LoggingUtils.setUserName(user.getUsername());
                        LoggingUtils.setFarmNo(farm.getFarmNo());
                        LoggingUtils.setFarmName(farm.getFarmName());
                        LoggingUtils.setLoginUserNo(user.getUserNo());
                        try {
                            loadBalancerProcess.stop(loadBalancerNo);
                        } catch (MultiCauseException ignore) {
                        } catch (Throwable e) {
                            log.error(e.getMessage(), e);

                            // イベントログ出力
                            eventLogger.error("SystemError", new Object[] { e.getMessage() });
                        } finally {
                            LoggingUtils.removeContext();
                        }
                    }
                };
                executorService.execute(runnable);
            }
        }

        // 処理中のインスタンスがある場合
        if (processing) {
            return false;
        }

        return true;
    }

    /**
     * instanceProcessを設定します。
     *
     * @param instanceProcess instanceProcess
     */
    public void setInstanceProcess(InstanceProcess instanceProcess) {
        this.instanceProcess = instanceProcess;
    }

    /**
     * componentProcessを設定します。
     *
     * @param componentProcess componentProcess
     */
    public void setComponentProcess(ComponentProcess componentProcess) {
        this.componentProcess = componentProcess;
    }

    /**
     * instancesProcessを設定します。
     *
     * @param instancesProcess instancesProcess
     */
    public void setInstancesProcess(InstancesProcess instancesProcess) {
        this.instancesProcess = instancesProcess;
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

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
