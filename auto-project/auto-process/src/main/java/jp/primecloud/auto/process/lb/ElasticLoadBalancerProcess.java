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
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.component.DnsStrategy;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.process.zabbix.ElbZabbixHostProcess;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ElasticLoadBalancerProcess extends ServiceSupport {

    protected ExecutorService executorService;

    protected DnsStrategy dnsStrategy;

    protected ElbZabbixHostProcess elbZabbixHostProcess;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    protected IaasGatewayFactory iaasGatewayFactory;

    public void start(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200226", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        //IaasGatewayWrapper作成
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), loadBalancer.getPlatformNo());
        // ELBの起動
        gateway.startLoadBalancer(loadBalancer.getLoadBalancerNo());

        //Zabbixへの登録
        elbZabbixHostProcess.startHost(loadBalancerNo);

        // DNSサーバへの追加
        addDns(loadBalancer.getLoadBalancerNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200227", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public void stop(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200228", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // DNSサーバからの削除
        deleteDns(loadBalancerNo);

        //Zabbixからの削除
        elbZabbixHostProcess.stopHost(loadBalancerNo);


        //IaasGatewayWrapper作成
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), loadBalancer.getPlatformNo());
        // ELBの停止
        gateway.stopLoadBalancer(loadBalancer.getLoadBalancerNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200229", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

    }

    public void configure(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200230", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        //IaasGatewayWrapper作成
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), loadBalancer.getPlatformNo());

        try {
            // ロードバランサが無効の場合、監視を停止する
            if (BooleanUtils.isNotTrue(loadBalancer.getEnabled())) {
                elbZabbixHostProcess.stopTemplate(loadBalancerNo);
            }
            // 振り分け設定を変更
            gateway.configureLoadBalancer(loadBalancer.getLoadBalancerNo());


            // ロードバランサが有効の場合、監視を開始する
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                elbZabbixHostProcess.startTemplate(loadBalancerNo);
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
            log.info(MessageUtils.getMessage("IPROCESS-200231", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }


    protected void addDns(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // CNAMEが登録されている場合はスキップ
        if (StringUtils.equals(awsLoadBalancer.getDnsName(), loadBalancer.getCanonicalName())) {
            return;
        }

        String fqdn = loadBalancer.getFqdn();
        String canonicalName = awsLoadBalancer.getDnsName();

        // CNAMEの追加
        dnsStrategy.addCanonicalName(fqdn, canonicalName);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100145", fqdn, canonicalName));
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "DnsRegistCanonical", new Object[] { fqdn, canonicalName });

        // データベース更新
        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        loadBalancer.setCanonicalName(awsLoadBalancer.getDnsName());
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
     * ElbZabbixHostProcessを設定します。
     *
     * @param ElbZabbixHostProcess elbZabbixHostProcess
     */
    public void setElbZabbixHostProcess(ElbZabbixHostProcess elbZabbixHostProcess) {
        this.elbZabbixHostProcess = elbZabbixHostProcess;
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

    /**
     * iaasGatewayFactoryを設定します。
     *
     * @param iaasGatewayFactory iaasGatewayFactory
     */
    public void setIaasGatewayFactory(IaasGatewayFactory iaasGatewayFactory) {
        this.iaasGatewayFactory = iaasGatewayFactory;
    }

}
