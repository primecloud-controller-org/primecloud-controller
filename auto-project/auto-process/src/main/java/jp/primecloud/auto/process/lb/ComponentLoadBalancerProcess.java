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
package jp.primecloud.auto.process.lb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import jp.primecloud.auto.common.component.DnsStrategy;
import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentLoadBalancer;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.ZabbixInstance;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.process.InstanceProcess;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.process.zabbix.ZabbixHostProcess;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentLoadBalancerProcess extends ServiceSupport {

    protected InstanceProcess instanceProcess;

    protected PuppetLoadBalancerProcess puppetLoadBalancerProcess;

    protected ExecutorService executorService;

    protected DnsStrategy dnsStrategy;

    protected ZabbixHostProcess zabbixHostProcess;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    public void start(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200201", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // コンポーネント型ロードバランサに紐づくインスタンス番号を取得する
        List<Long> instanceNos = getInstanceNos(loadBalancerNo);

        if (instanceNos.isEmpty()) {
            // TODO: エラーコード
            throw new RuntimeException("インスタンスがない");
        }

        // インスタンスの起動
        startInstances(loadBalancerNo, instanceNos);

        // DNSサーバへの追加
        addDns(loadBalancerNo, instanceNos);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200202", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public void stop(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200203", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // DNSサーバからの削除
        deleteDns(loadBalancerNo);

        // コンポーネント型ロードバランサに紐づくインスタンス番号を取得する
        List<Long> instanceNos = getInstanceNos(loadBalancerNo);

        if (instanceNos.isEmpty()) {
            return;
        }

        // インスタンスの停止
        stopInstances(loadBalancerNo, instanceNos);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200204", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

    }

    public void configure(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200205", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        ComponentLoadBalancer componentLoadBalancer = componentLoadBalancerDao.read(loadBalancerNo);

        // コンポーネント型ロードバランサに紐づくインスタンス番号を取得する
        List<Long> instanceNos = getInstanceNos(loadBalancerNo);

        try {
            // ロードバランサが無効の場合、監視を停止する
            if (BooleanUtils.isNotTrue(loadBalancer.getEnabled())) {
                List<ZabbixInstance> zabbixInstances = zabbixInstanceDao.readInInstanceNos(instanceNos);
                for (ZabbixInstance zabbixInstance : zabbixInstances) {
                    zabbixHostProcess.stopTemplate(zabbixInstance.getInstanceNo(), componentLoadBalancer
                            .getComponentNo());
                }
            }

            // 振り分け設定を変更
            puppetLoadBalancerProcess.configure(loadBalancerNo, componentLoadBalancer.getComponentNo(), instanceNos);

            // ロードバランサが有効の場合、監視を開始する
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                List<ZabbixInstance> zabbixInstances = zabbixInstanceDao.readInInstanceNos(instanceNos);
                for (ZabbixInstance zabbixInstance : zabbixInstances) {
                    zabbixHostProcess.startTemplate(zabbixInstance.getInstanceNo(), componentLoadBalancer
                            .getComponentNo());
                }
            }
        } catch (RuntimeException e) {
            loadBalancer = loadBalancerDao.read(loadBalancerNo);

            // リスナーのステータスを変更
            List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);
            for (LoadBalancerListener listener : listeners) {
                LoadBalancerListenerStatus status;
                if (BooleanUtils.isTrue(loadBalancer.getEnabled()) && BooleanUtils.isTrue(listener.getEnabled())) {
                    status = LoadBalancerListenerStatus.WARNING;
                } else {
                    status = LoadBalancerListenerStatus.STOPPED;
                }

                if (status != LoadBalancerListenerStatus.fromStatus(listener.getStatus())
                        || BooleanUtils.isTrue(listener.getConfigure())) {
                    listener.setStatus(status.toString());
                    listener.setConfigure(false);
                    loadBalancerListenerDao.update(listener);
                }
            }

            // 振り分けインスタンスのステータスを変更
            List<LoadBalancerInstance> lbInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
            List<Long> targetInstanceNos = new ArrayList<Long>();
            for (LoadBalancerInstance lbInstance : lbInstances) {
                targetInstanceNos.add(lbInstance.getInstanceNo());
            }
            List<Instance> targetInstances = instanceDao.readInInstanceNos(targetInstanceNos);
            Map<Long, Instance> targetInstanceMap = new HashMap<Long, Instance>();
            for (Instance targetInstance : targetInstances) {
                targetInstanceMap.put(targetInstance.getInstanceNo(), targetInstance);
            }

            for (LoadBalancerInstance lbInstance : lbInstances) {
                LoadBalancerInstanceStatus status;
                Instance targetInstance = targetInstanceMap.get(lbInstance.getInstanceNo());
                if (BooleanUtils.isTrue(loadBalancer.getEnabled()) && BooleanUtils.isTrue(lbInstance.getEnabled())
                        && BooleanUtils.isTrue(targetInstance.getEnabled())) {
                    status = LoadBalancerInstanceStatus.WARNING;
                } else {
                    status = LoadBalancerInstanceStatus.STOPPED;
                }

                if (status != LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus())) {
                    lbInstance.setStatus(status.toString());
                    loadBalancerInstanceDao.update(lbInstance);
                }
            }

            throw e;
        }

        // リスナーのステータスを変更
        List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerListener listener : listeners) {
            LoadBalancerListenerStatus status;
            if (BooleanUtils.isTrue(loadBalancer.getEnabled()) && BooleanUtils.isTrue(listener.getEnabled())) {
                status = LoadBalancerListenerStatus.RUNNING;
            } else {
                status = LoadBalancerListenerStatus.STOPPED;
            }

            if (status != LoadBalancerListenerStatus.fromStatus(listener.getStatus())
                    || BooleanUtils.isTrue(listener.getConfigure())) {
                listener.setStatus(status.toString());
                listener.setConfigure(false);
                loadBalancerListenerDao.update(listener);
            }
        }

        // 振り分けインスタンスのステータスを変更
        List<LoadBalancerInstance> lbInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
        List<Long> targetInstanceNos = new ArrayList<Long>();
        for (LoadBalancerInstance lbInstance : lbInstances) {
            targetInstanceNos.add(lbInstance.getInstanceNo());
        }
        List<Instance> targetInstances = instanceDao.readInInstanceNos(targetInstanceNos);
        Map<Long, Instance> targetInstanceMap = new HashMap<Long, Instance>();
        for (Instance targetInstance : targetInstances) {
            targetInstanceMap.put(targetInstance.getInstanceNo(), targetInstance);
        }

        for (LoadBalancerInstance lbInstance : lbInstances) {
            LoadBalancerInstanceStatus status;
            Instance targetInstance = targetInstanceMap.get(lbInstance.getInstanceNo());
            if (BooleanUtils.isTrue(loadBalancer.getEnabled()) && BooleanUtils.isTrue(lbInstance.getEnabled())
                    && BooleanUtils.isTrue(targetInstance.getEnabled())) {
                status = LoadBalancerInstanceStatus.RUNNING;
            } else {
                status = LoadBalancerInstanceStatus.STOPPED;
            }

            if (status != LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus())) {
                lbInstance.setStatus(status.toString());
                loadBalancerInstanceDao.update(lbInstance);
            }
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200206", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    protected List<Long> getInstanceNos(Long loadBalancerNo) {
        // コンポーネント型ロードバランサに紐づくインスタンス番号を取得する
        List<Long> instanceNos = new ArrayList<Long>();

        ComponentLoadBalancer componentLoadBalancer = componentLoadBalancerDao.read(loadBalancerNo);
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentLoadBalancer
                .getComponentNo());
        for (ComponentInstance componentInstance : componentInstances) {
            instanceNos.add(componentInstance.getInstanceNo());
        }

        return instanceNos;
    }

    protected void startInstances(Long loadBalancerNo, List<Long> instanceNos) {
        // インスタンスを有効にする
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        for (Instance instance : instances) {
            if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                instance.setEnabled(true);
                instanceDao.update(instance);
            }
        }

        // コンポーネントインスタンスを有効にする
        List<ComponentInstance> componentInstances = componentInstanceDao.readInInstanceNos(instanceNos);
        for (ComponentInstance componentInstance : componentInstances) {
            if (BooleanUtils.isNotTrue(componentInstance.getEnabled())) {
                componentInstance.setEnabled(true);
                componentInstanceDao.update(componentInstance);
            }
        }

        // 停止しているインスタンスを取得する
        List<Instance> targetInstances = new ArrayList<Instance>();
        for (Instance instance : instances) {
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
            if (status == InstanceStatus.STOPPED) {
                targetInstances.add(instance);
            }
        }

        // 停止しているインスタンスがない場合
        if (targetInstances.isEmpty()) {
            return;
        }

        // 停止しているインスタンスを起動する
        final LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        final Map<String, Object> loggingContext = LoggingUtils.getContext();
        for (final Instance instance : targetInstances) {
            Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LoggingUtils.setContext(loggingContext);
                    try {
                        if (log.isInfoEnabled()) {
                            log.info(MessageUtils.getMessage("IPROCESS-200211", loadBalancer.getLoadBalancerNo(),
                                    instance.getInstanceNo(), loadBalancer.getLoadBalancerName(), instance
                                            .getInstanceName()));
                        }

                        instanceProcess.start(instance.getInstanceNo());

                        if (log.isInfoEnabled()) {
                            log.info(MessageUtils.getMessage("IPROCESS-200212", loadBalancer.getLoadBalancerNo(),
                                    instance.getInstanceNo(), loadBalancer.getLoadBalancerName(), instance
                                            .getInstanceName()));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
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

    protected void stopInstances(Long loadBalancerNo, List<Long> instanceNos) {
        // インスタンスを無効にする
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        for (Instance instance : instances) {
            if (BooleanUtils.isTrue(instance.getEnabled())) {
                instance.setEnabled(false);
                instanceDao.update(instance);
            }
        }

        // 起動または異常なインスタンスを取得する
        List<Instance> targetInstances = new ArrayList<Instance>();
        for (Instance instance : instances) {
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
            if (status == InstanceStatus.RUNNING || status == InstanceStatus.WARNING) {
                targetInstances.add(instance);
            }
        }

        // 起動または異常なインスタンスがない場合
        if (targetInstances.isEmpty()) {
            return;
        }

        // 起動または異常なインスタンスを停止する
        final LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        final Map<String, Object> loggingContext = LoggingUtils.getContext();
        for (final Instance instance : targetInstances) {
            Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LoggingUtils.setContext(loggingContext);
                    try {
                        if (log.isInfoEnabled()) {
                            log.info(MessageUtils.getMessage("IPROCESS-200213", loadBalancer.getLoadBalancerNo(),
                                    instance.getInstanceNo(), loadBalancer.getLoadBalancerName(), instance
                                            .getInstanceName()));
                        }

                        instanceProcess.stop(instance.getInstanceNo());

                        if (log.isInfoEnabled()) {
                            log.info(MessageUtils.getMessage("IPROCESS-200214", loadBalancer.getLoadBalancerNo(),
                                    instance.getInstanceNo(), loadBalancer.getLoadBalancerName(), instance
                                            .getInstanceName()));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
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

    // TODO: ロードバランサの冗長化対応の際に見直す
    protected void addDns(Long loadBalancerNo, List<Long> instanceNos) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        // 先頭のインスタンスをロードバランサとして使用する
        Instance instance = instanceDao.read(instanceNos.get(0));

        // CNAMEが登録されている場合はスキップ
        if (StringUtils.equals(instance.getFqdn(), loadBalancer.getCanonicalName())) {
            return;
        }

        String fqdn = loadBalancer.getFqdn();
        String canonicalName = instance.getFqdn();

        // CNAMEの追加
        dnsStrategy.addCanonicalName(fqdn, canonicalName);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100145", fqdn, canonicalName));
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "DnsRegistCanonical", new Object[] { fqdn, canonicalName });

        // データベース更新
        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        loadBalancer.setCanonicalName(canonicalName);
        loadBalancerDao.update(loadBalancer);
    }

    // TODO: ロードバランサの冗長化対応の際に見直す
    protected void deleteDns(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        // CNAMEが登録されていない場合はスキップ
        if (StringUtils.isEmpty(loadBalancer.getCanonicalName())) {
            return;
        }

        String fqdn = loadBalancer.getFqdn();
        String canonicalName = loadBalancer.getCanonicalName();

        try {
            // CNAMEの削除
            dnsStrategy.deleteCanonicalName(fqdn);

            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100146", fqdn));
            }

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "DnsUnregistCanonical", new Object[] { fqdn, canonicalName });

        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        // データベース更新
        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        loadBalancer.setCanonicalName(null);
        loadBalancerDao.update(loadBalancer);
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
     * puppetLoadBalancerProcessを設定します。
     *
     * @param puppetLoadBalancerProcess puppetLoadBalancerProcess
     */
    public void setPuppetLoadBalancerProcess(PuppetLoadBalancerProcess puppetLoadBalancerProcess) {
        this.puppetLoadBalancerProcess = puppetLoadBalancerProcess;
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
     * dnsStrategyを設定します。
     *
     * @param dnsStrategy dnsStrategy
     */
    public void setDnsStrategy(DnsStrategy dnsStrategy) {
        this.dnsStrategy = dnsStrategy;
    }

    /**
     * zabbixHostProcessを設定します。
     *
     * @param zabbixHostProcess zabbixHostProcess
     */
    public void setZabbixHostProcess(ZabbixHostProcess zabbixHostProcess) {
        this.zabbixHostProcess = zabbixHostProcess;
    }

    /**
     * processLoggerを設定します。
     *
     * @param processLogger processLogger
     */
    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
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
