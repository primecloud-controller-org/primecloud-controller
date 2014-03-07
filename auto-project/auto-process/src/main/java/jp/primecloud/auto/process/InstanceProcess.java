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

import java.util.List;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.InstanceCoodinateStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.puppet.PuppetClient;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.process.nifty.NiftyProcess;
import jp.primecloud.auto.process.puppet.PuppetNodeProcess;
import jp.primecloud.auto.process.vmware.VmwareProcess;
import jp.primecloud.auto.process.zabbix.ZabbixHostProcess;

/**
 * <p>
 * 仮想インスタンスに対する処理の実施クラス
 * </p>
 *
 */
public class InstanceProcess extends ServiceSupport {

    protected IaasGatewayFactory iaasGatewayFactory;

    protected VmwareProcess vmwareProcess;

    protected NiftyProcess niftyProcess;

    protected PuppetNodeProcess puppetNodeProcess;

    protected ZabbixHostProcess zabbixHostProcess;

    protected DnsProcess dnsProcess;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    protected PuppetClient puppetClient;


    public void start(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        if (instance == null) {
            // インスタンスが存在しない
            throw new AutoException("EPROCESS-000002", instanceNo);
        }

        if (BooleanUtils.isNotTrue(instance.getEnabled())) {
            // 開始対象のインスタンスではない
            return;
        }

        //ファームの取得
        Farm farm = farmDao.read(instance.getFarmNo());

        // ログ用情報を格納
        LoggingUtils.setInstanceNo(instanceNo);
        LoggingUtils.setInstanceName(instance.getInstanceName());
        LoggingUtils.setInstanceType(processLogger.getInstanceType(instanceNo));
        LoggingUtils.setPlatformNo(instance.getPlatformNo());

        InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
        if (status != InstanceStatus.STOPPED && status != InstanceStatus.RUNNING) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                log.debug(MessageUtils.format("Instance {1} status is {2}.(instanceNo={0})", instanceNo, instance
                        .getInstanceName(), status));
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100001", instanceNo, instance.getInstanceName()));
        }

        // ステータス・進捗状況の更新
        if (status == InstanceStatus.RUNNING) {
            status = InstanceStatus.CONFIGURING;
        } else {
            status = InstanceStatus.STARTING;
        }
        instance.setStatus(status.toString());
        instanceDao.update(instance);

        // イベントログ出力
        if (status == InstanceStatus.STARTING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceStart", null);
        } else if (status == InstanceStatus.CONFIGURING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceReload", null);
        }

        Platform platform = platformDao.read(instance.getPlatformNo());
        Image image = imageDao.read(instance.getImageNo());
        try {

            //事前処理 AWSの場合に必要
            if ("aws".equals(platform.getPlatformType())) {
                AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
                // インスタンスイメージの場合や、EBSイメージでインスタンスIDがない場合
                ImageAws imageAws = imageAwsDao.read(image.getImageNo());
                if (!imageAws.getEbsImage() || StringUtils.isEmpty(awsInstance.getInstanceId())) {
                    // インスタンスIDがある場合はPuppet認証情報の削除
                    if (StringUtils.isEmpty(awsInstance.getInstanceId())) {
                        clearPuppetCa(instanceNo);
                    }
                // EBSイメージでインスタンスIDがある場合
                } else {
                    // インスタンスが停止中の場合はPuppet認証情報の削除
                    if (StringUtils.equals(awsInstance.getStatus(), "stopped")) {
                        clearPuppetCa(instanceNo);
                    }
                }
            }

            // インスタンス起動処理
            if ("aws".equals(platform.getPlatformType()) || "cloudstack".equals(platform.getPlatformType())) {
                //IaasGateway対応サービス
                IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), instance.getPlatformNo());
                gateway.startInstance(instanceNo);

            } else if ("vmware".equals(platform.getPlatformType())) {
                //**********************************TODO IaasGateway対応後は　上記へ一本化します

                // VMwareの場合
                vmwareProcess.start(instanceNo);
            } else if ("nifty".equals(platform.getPlatformType())) {
                //**********************************TODO IaasGateway対応後は　上記へ一本化します
                // Niftyの場合
                niftyProcess.start(instanceNo);
            }

            //事後処理 AWSの場合に必要

            if ("aws".equals(platform.getPlatformType()) || "cloudstack".equals(platform.getPlatformType())) {
                // DNSに関する処理
                dnsProcess.startDns(platform, instanceNo);
            }

            // 進捗状況の更新
            instance = instanceDao.read(instanceNo);
            instanceDao.update(instance);

            // 初期設定処理
            if (puppetInstanceDao.countByInstanceNo(instanceNo) > 0) {
                puppetNodeProcess.startNode(instanceNo);
            }

            // 進捗状況の更新
            instance = instanceDao.read(instanceNo);
            instanceDao.update(instance);

            // 監視設定処理
            if (zabbixInstanceDao.countByInstanceNo(instanceNo) > 0) {
                zabbixHostProcess.startHost(instanceNo);
            }

        } catch (RuntimeException e) {
            instance = instanceDao.read(instanceNo);
            status = InstanceStatus.fromStatus(instance.getStatus());

            // イベントログ出力
            if (status == InstanceStatus.STARTING) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceStartFail", null);
            } else if (status == InstanceStatus.CONFIGURING) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceReloadFail", null);
            }

            // ステータスの更新
            instance.setStatus(InstanceStatus.WARNING.toString());
            instanceDao.update(instance);

            throw e;
        }

        instance = instanceDao.read(instanceNo);
        status = InstanceStatus.fromStatus(instance.getStatus());

        // イベントログ出力
        if (status == InstanceStatus.STARTING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceStartFinish", null);
        } else if (status == InstanceStatus.CONFIGURING) {
            processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceReloadFinish", null);
        }

        // ステータス・進捗状況の更新
        instance.setStatus(InstanceStatus.RUNNING.toString());
        instanceDao.update(instance);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100002", instanceNo, instance.getInstanceName()));
        }
    }

    public void stop(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        if (instance == null) {
            // インスタンスが存在しない
            throw new AutoException("EPROCESS-000002", instanceNo);
        }

        if (BooleanUtils.isTrue(instance.getEnabled())) {
            // 終了対象のインスタンスではない
            return;
        }

        //ファームの取得
        Farm farm = farmDao.read(instance.getFarmNo());

        // ログ用情報を格納
        LoggingUtils.setInstanceNo(instanceNo);
        LoggingUtils.setInstanceName(instance.getInstanceName());
        LoggingUtils.setInstanceType(processLogger.getInstanceType(instanceNo));
        LoggingUtils.setPlatformNo(instance.getPlatformNo());

        InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
        if (status != InstanceStatus.STOPPED && status != InstanceStatus.RUNNING && status != InstanceStatus.WARNING) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                log.debug(MessageUtils.format("Instance {1} status is {2}.(instanceNo={0})", instanceNo, instance
                        .getInstanceName(), status));
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100003", instanceNo, instance.getInstanceName()));
        }

        // ステータス・進捗状況の更新
        instance.setStatus(InstanceStatus.STOPPING.toString());
        instanceDao.update(instance);

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceStop", null);

        try {
            // 監視設定処理
            if (zabbixInstanceDao.countByInstanceNo(instanceNo) > 0) {
                zabbixHostProcess.stopHost(instanceNo);
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        // 進捗状況の更新
        instance = instanceDao.read(instanceNo);
        instanceDao.update(instance);

        try {
            // 終了設定処理
            if (puppetInstanceDao.countByInstanceNo(instanceNo) > 0) {
                puppetNodeProcess.stopNode(instanceNo);
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        // 進捗状況の更新
        instance = instanceDao.read(instanceNo);
        instanceDao.update(instance);
        Platform platform = platformDao.read(instance.getPlatformNo());

        //事前処理 AWSの場合に必要
        if ("aws".equals(platform.getPlatformType()) || "cloudstack".equals(platform.getPlatformType())) {
            try {
                // DNSに関する処理
                dnsProcess.stopDns(platform, instanceNo);
            } catch (RuntimeException e) {
                log.warn(e.getMessage());
            }
        }


        // インスタンス停止処理
        if ("aws".equals(platform.getPlatformType()) || "cloudstack".equals(platform.getPlatformType())) {
            // IaasGateway対応サービス
            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), instance.getPlatformNo());
            gateway.stopInstance(instanceNo);

        } else if ("vmware".equals(platform.getPlatformType())) {
            //**********************************TODO IaasGateway対応後は　上記へ一本化します
            // VMwareの場合
            vmwareProcess.stop(instanceNo);
        } else if ("nifty".equals(platform.getPlatformType())) {
            //**********************************TODO IaasGateway対応後は　上記へ一本化します
            // Niftyの場合
            niftyProcess.stop(instanceNo);
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_INFO, null, instance, "InstanceStopFinish", null);

        // ステータス・進捗状況の更新
        instance = instanceDao.read(instanceNo);
        instance.setStatus(InstanceStatus.STOPPED.toString());
        //※協調設定がWarningになっているサーバは次回の起動もWarningになってしまう為、停止時にステータスを停止時の状態にする。
        if (InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus()) == InstanceCoodinateStatus.WARNING) {
            instance.setCoodinateStatus(InstanceCoodinateStatus.UN_COODINATED.toString());
        }
        instanceDao.update(instance);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100004", instanceNo, instance.getInstanceName()));
        }
    }

    protected void clearPuppetCa( Long instanceNo) {
        List<String> clients = puppetClient.listClients();

        Instance instance = instanceDao.read(instanceNo);
        String fqdn = instance.getFqdn();

        if (clients.contains(fqdn)) {
            puppetClient.clearCa(fqdn);
        }
    }

    public void setPuppetClient(PuppetClient puppetClient) {
        this.puppetClient = puppetClient;
    }

    /**
     * awsProcessClientFactoryを設定します。
     *
     * @param awsProcessClientFactory awsProcessClientFactory
     */
    public void setIaasGatewayFactory(IaasGatewayFactory iaasGatewayFactory) {
        this.iaasGatewayFactory = iaasGatewayFactory;
    }

    /**
     * vmwareProcessを設定します。
     *
     * @param vmwareProcess vmwareProcess
     */
    public void setVmwareProcess(VmwareProcess vmwareProcess) {
        this.vmwareProcess = vmwareProcess;
    }

    /**
     * niftyProcessを設定します。
     *
     * @param niftyProcess niftyProcess
     */
    public void setNiftyProcess(NiftyProcess niftyProcess) {
        this.niftyProcess = niftyProcess;
    }

    /**
     * puppetNodeProcessを設定します。
     *
     * @param puppetNodeProcess puppetNodeProcess
     */
    public void setPuppetNodeProcess(PuppetNodeProcess puppetNodeProcess) {
        this.puppetNodeProcess = puppetNodeProcess;
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

    /**
     * dnsProcessを設定します。
     *
     * @param dnsProcess dnsProcess
     */
    public void setDnsProcess(DnsProcess dnsProcess) {
        this.dnsProcess = dnsProcess;
    }

}
