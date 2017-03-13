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

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.InstanceCoodinateStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.process.aws.AwsProcess;
import jp.primecloud.auto.process.hook.ProcessHook;
import jp.primecloud.auto.process.iaasgw.IaasGatewayProcess;
import jp.primecloud.auto.process.nifty.NiftyProcess;
import jp.primecloud.auto.process.puppet.PuppetNodeProcess;
import jp.primecloud.auto.process.vmware.VmwareProcess;
import jp.primecloud.auto.process.zabbix.ZabbixHostProcess;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;

/**
 * <p>
 * 仮想インスタンスに対する処理の実施クラス
 * </p>
 *
 */
public class InstanceProcess extends ServiceSupport {

    protected IaasGatewayProcess iaasGatewayProcess;

    protected AwsProcess awsProcess;

    protected VmwareProcess vmwareProcess;

    protected NiftyProcess niftyProcess;

    protected PuppetNodeProcess puppetNodeProcess;

    protected ZabbixHostProcess zabbixHostProcess;

    protected ProcessLogger processLogger;

    protected ProcessHook processHook;

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
        LoggingUtils.setInstanceType(processLogger.getInstanceType(instanceNo, instance.getPlatformNo()));
        LoggingUtils.setPlatformNo(instance.getPlatformNo());

        InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
        if (status != InstanceStatus.STOPPED && status != InstanceStatus.RUNNING) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                log.debug(MessageUtils.format("Instance {1} status is {2}.(instanceNo={0})", instanceNo,
                        instance.getInstanceName(), status));
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100001", instanceNo, instance.getInstanceName()));
        }

        // フック処理の実行
        processHook.execute("pre-start-instance", farm.getUserNo(), farm.getFarmNo(), instanceNo);

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
            processLogger.info(null, instance, "InstanceStart", null);
        } else if (status == InstanceStatus.CONFIGURING) {
            processLogger.info(null, instance, "InstanceReload", null);
        }

        // インスタンス起動処理
        try {
            // Puppet認証情報の削除
            if (puppetInstanceDao.countByInstanceNo(instanceNo) > 0) {
                puppetNodeProcess.clearCa(instanceNo);
            }

            // AWSの場合
            Platform platform = platformDao.read(instance.getPlatformNo());
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                awsProcess.start(instanceNo);
            }
            // IaasGateway対応サービスの場合
            else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                iaasGatewayProcess.start(instanceNo);
            }
            // VMwareの場合
            else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                vmwareProcess.start(instanceNo);
            }
            // Niftyの場合
            else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                niftyProcess.start(instanceNo);
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
                processLogger.info(null, instance, "InstanceStartFail", null);
            } else if (status == InstanceStatus.CONFIGURING) {
                processLogger.info(null, instance, "InstanceReloadFail", null);
            }

            // ステータスの更新
            instance.setStatus(InstanceStatus.WARNING.toString());
            instanceDao.update(instance);

            // フック処理の実行
            processHook.execute("post-start-instance", farm.getUserNo(), farm.getFarmNo(), instanceNo);

            throw e;
        }

        instance = instanceDao.read(instanceNo);
        status = InstanceStatus.fromStatus(instance.getStatus());

        // イベントログ出力
        if (status == InstanceStatus.STARTING) {
            processLogger.info(null, instance, "InstanceStartFinish", null);
        } else if (status == InstanceStatus.CONFIGURING) {
            processLogger.info(null, instance, "InstanceReloadFinish", null);
        }

        // ステータス・進捗状況の更新
        instance.setStatus(InstanceStatus.RUNNING.toString());
        instanceDao.update(instance);

        // フック処理の実行
        processHook.execute("post-start-instance", farm.getUserNo(), farm.getFarmNo(), instanceNo);

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
        LoggingUtils.setInstanceType(processLogger.getInstanceType(instanceNo, instance.getPlatformNo()));
        LoggingUtils.setPlatformNo(instance.getPlatformNo());

        InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
        if (status != InstanceStatus.STOPPED && status != InstanceStatus.RUNNING && status != InstanceStatus.WARNING) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                log.debug(MessageUtils.format("Instance {1} status is {2}.(instanceNo={0})", instanceNo,
                        instance.getInstanceName(), status));
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100003", instanceNo, instance.getInstanceName()));
        }

        // フック処理の実行
        processHook.execute("pre-stop-instance", farm.getUserNo(), farm.getFarmNo(), instanceNo);

        // ステータス・進捗状況の更新
        instance.setStatus(InstanceStatus.STOPPING.toString());
        instanceDao.update(instance);

        // イベントログ出力
        processLogger.info(null, instance, "InstanceStop", null);

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

        try {
            // インスタンス停止処理
            Platform platform = platformDao.read(instance.getPlatformNo());

            // AWSの場合
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                awsProcess.stop(instanceNo);
            }
            // IaasGateway対応サービスの場合
            else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                iaasGatewayProcess.stop(instanceNo);
            }
            // VMwareの場合
            else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                vmwareProcess.stop(instanceNo);
            }
            // Niftyの場合
            else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                niftyProcess.stop(instanceNo);
            }

        } catch (RuntimeException e) {
            instance = instanceDao.read(instanceNo);
            status = InstanceStatus.fromStatus(instance.getStatus());

            // イベントログ出力
            if (status == InstanceStatus.STOPPING) {
                processLogger.info(null, instance, "InstanceStopFail", null);
            } else if (status == InstanceStatus.CONFIGURING) {
                processLogger.info(null, instance, "InstanceReloadFail", null);
            }

            // ステータスの更新
            instance.setStatus(InstanceStatus.WARNING.toString());
            instance.setEnabled(true);
            instanceDao.update(instance);

            // フック処理の実行
            processHook.execute("post-stop-instance", farm.getUserNo(), farm.getFarmNo(), instanceNo);

            throw e;
        }

        // イベントログ出力
        processLogger.info(null, instance, "InstanceStopFinish", null);

        // ステータス・進捗状況の更新
        instance = instanceDao.read(instanceNo);
        instance.setStatus(InstanceStatus.STOPPED.toString());
        //※協調設定がWarningになっているサーバは次回の起動もWarningになってしまう為、停止時にステータスを停止時の状態にする。
        if (InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus()) == InstanceCoodinateStatus.WARNING) {
            instance.setCoodinateStatus(InstanceCoodinateStatus.UN_COODINATED.toString());
        }
        instanceDao.update(instance);

        // フック処理の実行
        processHook.execute("post-stop-instance", farm.getUserNo(), farm.getFarmNo(), instanceNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100004", instanceNo, instance.getInstanceName()));
        }
    }

    public void setIaasGatewayProcess(IaasGatewayProcess iaasGatewayProcess) {
        this.iaasGatewayProcess = iaasGatewayProcess;
    }

    public void setAwsProcess(AwsProcess awsProcess) {
        this.awsProcess = awsProcess;
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
     * processHookを設定します。
     *
     * @param processHook processHook
     */
    public void setProcessHook(ProcessHook processHook) {
        this.processHook = processHook;
    }

}
