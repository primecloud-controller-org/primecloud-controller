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

import java.util.List;

import jp.primecloud.auto.common.component.DnsStrategy;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.process.ProcessLogger;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class LoadBalancerProcess extends ServiceSupport {

    protected ComponentLoadBalancerProcess componentLoadBalancerProcess;

    protected ElasticLoadBalancerProcess elasticLoadBalancerProcess;

    protected IaasGatewayFactory iaasGatewayFactory;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    protected DnsStrategy dnsStrategy;


    public void start(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない
            throw new AutoException("EPROCESS-000010", loadBalancerNo);
        }

        if (BooleanUtils.isNotTrue(loadBalancer.getEnabled())) {
            // 開始対象のロードバランサではない
            return;
        }

        LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
        if (status != LoadBalancerStatus.STOPPED && status != LoadBalancerStatus.RUNNING) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                log.debug(MessageUtils.format("LoadBalancer {1} status is {2}.(loadBalancerNo={0})", loadBalancerNo,
                        loadBalancer.getLoadBalancerName(), status));
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200001", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // ステータスの更新
        if (status == LoadBalancerStatus.RUNNING) {
            status = LoadBalancerStatus.CONFIGURING;
        } else {
            status = LoadBalancerStatus.STARTING;
        }
        loadBalancer.setStatus(status.toString());
        loadBalancerDao.update(loadBalancer);

        // イベントログ出力
        if (status == LoadBalancerStatus.STARTING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                    "LoadBalancerStart", new Object[] { loadBalancer.getLoadBalancerName() });
        } else if (status == LoadBalancerStatus.CONFIGURING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                    "LoadBalancerReload", new Object[] { loadBalancer.getLoadBalancerName() });
        }

        try {
            // ロードバランサ開始処理
            startLoadBalancer(loadBalancer);

        } catch (RuntimeException e) {
            loadBalancer = loadBalancerDao.read(loadBalancerNo);
            status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());

            // イベントログ出力
            if (status == LoadBalancerStatus.STARTING) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                        "LoadBalancerStartFail", new Object[] { loadBalancer.getLoadBalancerName() });
            } else if (status == LoadBalancerStatus.CONFIGURING) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                        "LoadBalancerReloadFail", new Object[] { loadBalancer.getLoadBalancerName() });
            }

            // ステータスの更新
            loadBalancer = loadBalancerDao.read(loadBalancerNo);
            loadBalancer.setStatus(LoadBalancerStatus.WARNING.toString());
            loadBalancerDao.update(loadBalancer);

            throw e;
        }

        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());

        // イベントログ出力
        if (status == LoadBalancerStatus.STARTING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                    "LoadBalancerStartFinish", new Object[] { loadBalancer.getLoadBalancerName() });
        } else if (status == LoadBalancerStatus.CONFIGURING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                    "LoadBalancerReloadFinish", new Object[] { loadBalancer.getLoadBalancerName() });
        }

        // ステータスの更新
        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        loadBalancer.setStatus(LoadBalancerStatus.RUNNING.toString());
        loadBalancerDao.update(loadBalancer);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200002", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public void stop(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない
            throw new AutoException("EPROCESS-000010", loadBalancerNo);
        }

        if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
            // 終了対象のインスタンスではない
            return;
        }

        LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
        if (status != LoadBalancerStatus.STOPPED && status != LoadBalancerStatus.RUNNING
                && status != LoadBalancerStatus.WARNING) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                log.debug(MessageUtils.format("LoadBalancer {1} status is {2}.(loadBalancerNo={0})", loadBalancerNo,
                        loadBalancer.getLoadBalancerName(), status));
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200003", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // ステータスの更新
        loadBalancer.setStatus(LoadBalancerStatus.STOPPING.toString());
        loadBalancerDao.update(loadBalancer);

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                "LoadBalancerStop", new Object[] { loadBalancer.getLoadBalancerName() });

        try {
            // ロードバランサ終了処理
            stopLoadBalancer(loadBalancer);

        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                "LoadBalancerStopFinish", new Object[] { loadBalancer.getLoadBalancerName() });

        // ステータスの更新
        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        loadBalancer.setStatus(LoadBalancerStatus.STOPPED.toString());
        loadBalancerDao.update(loadBalancer);

        // リスナーや振り分けインスタンスのステータスを更新
        List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerListener listener : listeners) {
            LoadBalancerListenerStatus status2 = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
            if (status2 != LoadBalancerListenerStatus.STOPPED) {
                listener.setStatus(LoadBalancerListenerStatus.STOPPED.toString());
                loadBalancerListenerDao.update(listener);
            }
        }

        List<LoadBalancerInstance> lbInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerInstance lbInstance : lbInstances) {
            LoadBalancerInstanceStatus status2 = LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus());
            if (status2 != LoadBalancerInstanceStatus.STOPPED) {
                lbInstance.setStatus(LoadBalancerInstanceStatus.STOPPED.toString());
                loadBalancerInstanceDao.update(lbInstance);
            }
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200004", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public void configure(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない
            throw new AutoException("EPROCESS-000010", loadBalancerNo);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200011", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        LoadBalancerStatus initStatus = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());

        // ステータスの更新
        if (BooleanUtils.isTrue(loadBalancer.getConfigure())) {
            loadBalancer.setStatus(LoadBalancerStatus.CONFIGURING.toString());
            loadBalancerDao.update(loadBalancer);
        }

        // イベントログ出力
        if (BooleanUtils.isTrue(loadBalancer.getConfigure())) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                    "LoadBalancerConfig", new Object[] { loadBalancer.getLoadBalancerName() });
        }

        try {
            // ロードバランサ設定処理
            configureLoadBalancer(loadBalancer);

        } catch (RuntimeException e) {
            // イベントログ出力
            if (BooleanUtils.isTrue(loadBalancer.getConfigure())) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                        "LoadBalancerConfigFail", new Object[] { loadBalancer.getLoadBalancerName() });
            }

            // ステータスの更新
            if (BooleanUtils.isTrue(loadBalancer.getConfigure())) {
                loadBalancer = loadBalancerDao.read(loadBalancerNo);
                loadBalancer.setStatus(initStatus.toString());
                loadBalancer.setConfigure(false);
                loadBalancerDao.update(loadBalancer);
            }

            // ロードバランサ停止時はエラーを握り潰す
            if (BooleanUtils.isNotTrue(loadBalancer.getEnabled())) {
                log.warn(e.getMessage());
                return;
            }

            throw e;
        }

        // イベントログ出力
        if (BooleanUtils.isTrue(loadBalancer.getConfigure())) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, null,
                    "LoadBalancerConfigFinish", new Object[] { loadBalancer.getLoadBalancerName() });
        }

        // ステータスの更新
        if (BooleanUtils.isTrue(loadBalancer.getConfigure())) {
            loadBalancer = loadBalancerDao.read(loadBalancerNo);
            loadBalancer.setStatus(initStatus.toString());
            loadBalancer.setConfigure(false);
            loadBalancerDao.update(loadBalancer);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200012", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    protected void startLoadBalancer(LoadBalancer loadBalancer) {
        String type = loadBalancer.getType();
        if ("ultramonkey".equals(type)) {
            componentLoadBalancerProcess.start(loadBalancer.getLoadBalancerNo());
        } else if ("aws".equals(type) || "cloudstack".equals(type)) {
            elasticLoadBalancerProcess.start(loadBalancer.getLoadBalancerNo());
        }
    }

    protected void stopLoadBalancer(LoadBalancer loadBalancer) {
        String type = loadBalancer.getType();
        if ("ultramonkey".equals(type)) {
            componentLoadBalancerProcess.stop(loadBalancer.getLoadBalancerNo());
        } else if ("aws".equals(type) || "cloudstack".equals(type)) {
            elasticLoadBalancerProcess.stop(loadBalancer.getLoadBalancerNo());
        }
    }

    protected void configureLoadBalancer(LoadBalancer loadBalancer) {
        String type = loadBalancer.getType();
        if ("ultramonkey".equals(type)) {
            componentLoadBalancerProcess.configure(loadBalancer.getLoadBalancerNo());
        } else if ("aws".equals(type) || "cloudstack".equals(type)) {
            elasticLoadBalancerProcess.configure(loadBalancer.getLoadBalancerNo());
        }
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
     * componentLoadBalancerProcessを設定します。
     *
     * @param componentLoadBalancerProcess componentLoadBalancerProcess
     */
    public void setComponentLoadBalancerProcess(ComponentLoadBalancerProcess componentLoadBalancerProcess) {
        this.componentLoadBalancerProcess = componentLoadBalancerProcess;
    }

    /**
     * elasticLoadBalancerProcessを設定します。
     *
     * @param ElasticLoadBalancerProcess elasticLoadBalancerProcess
     */
    public void setElasticLoadBalancerProcess(ElasticLoadBalancerProcess elasticLoadBalancerProcess) {
        this.elasticLoadBalancerProcess = elasticLoadBalancerProcess;
    }

    /**
     * iaasGatewayFactoryを設定します。
     *
     * @param iaasGatewayFactory iaasGatewayFactory
     */
    public void setIaasGatewayFactory(IaasGatewayFactory iaasGatewayFactory) {
        this.iaasGatewayFactory = iaasGatewayFactory;
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    /**
     * processLoggerを設定します。
     *
     * @param processLogger processLogger
     */
    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }

}
