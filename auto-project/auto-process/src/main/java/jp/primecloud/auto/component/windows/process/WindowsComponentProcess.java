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
package jp.primecloud.auto.component.windows.process;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.component.windows.WindowsConstants;
import jp.primecloud.auto.process.ComponentConstants;
import jp.primecloud.auto.process.ComponentProcessContext;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.process.puppet.PuppetComponentProcess;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class WindowsComponentProcess extends PuppetComponentProcess {

    /**
     * TODO: コンストラクタコメントを記述
     */
    public WindowsComponentProcess() {
        componentTypeName = WindowsConstants.COMPONENT_TYPE_NAME;
        awsVolumeDevice = WindowsConstants.AWS_VOLUME_DEVICE;
        vmwareDiskScsiId = WindowsConstants.VMWARE_DISK_SCSI_ID;
    }

    @Override
    public void createNodeManifest(ComponentProcessContext context) {
        // ノード用マニフェストの生成 必要なし
        return;
    }


    @Override
    protected void configureInstances(final Long componentNo, final ComponentProcessContext context,
            final boolean start, List<Long> instanceNos) {
        Component component = componentDao.read(componentNo);

        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        Map<Long, Instance> instanceMap = new HashMap<Long, Instance>();
        for (Instance instance : instances) {
            instanceMap.put(instance.getInstanceNo(), instance);
        }

        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentNo);
        Map<Long, ComponentInstance> componentInstanceMap = new HashMap<Long, ComponentInstance>();
        for (ComponentInstance componentInstance : componentInstances) {
            componentInstanceMap.put(componentInstance.getInstanceNo(), componentInstance);
        }

        // 停止を行う場合、既に停止しているサービスは除外する
        if (!start) {
            List<Long> tmpInstanceNos = new ArrayList<Long>();
            for (Long instanceNo : instanceNos) {
                ComponentInstance componentInstance = componentInstanceMap.get(instanceNo);
                ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());

                if (status == ComponentInstanceStatus.STOPPED) {
                    // 設定フラグを無効にする
                    if (BooleanUtils.isTrue(componentInstance.getConfigure())) {
                        componentInstance.setConfigure(false);
                        componentInstanceDao.update(componentInstance);
                    }
                } else {
                    tmpInstanceNos.add(instanceNo);
                }
            }
            instanceNos = tmpInstanceNos;
        }

        if (instanceNos.isEmpty()) {
            return;
        }

        for (Long instanceNo : instanceNos) {
            ComponentInstance componentInstance = componentInstanceMap.get(instanceNo);
            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());

            // ステータスを変更
            if (start) {
                if (status == ComponentInstanceStatus.RUNNING) {
                    status = ComponentInstanceStatus.CONFIGURING;
                } else {
                    status = ComponentInstanceStatus.STARTING;
                }
            } else {
                status = ComponentInstanceStatus.STOPPING;
            }
            componentInstance.setStatus(status.toString());
            componentInstanceDao.update(componentInstance);

            // イベントログ出力
            if (BooleanUtils.isTrue(componentInstance.getConfigure())) {
                Instance instance = instanceMap.get(instanceNo);
                if (status == ComponentInstanceStatus.STARTING) {
                    processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentStart",  null);
                } else if (status == ComponentInstanceStatus.CONFIGURING) {
                    processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentReload", null);
                } else if (status == ComponentInstanceStatus.STOPPING) {
                    processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentStop", null);
                }
            }
        }

        // マニフェスト用情報モデルの作成
        final Map<String, Object> rootMap = createComponentMap(componentNo, context, start);

        // 並列実行
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        final Map<String, Object> loggingContext = LoggingUtils.getContext();
        for (final Long instanceNo : instanceNos) {
            Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LoggingUtils.setContext(loggingContext);
                    try {
                        doConfigureInstance(componentNo, context, start, instanceNo, rootMap);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);

                        // イベントログ出力
                        eventLogger.error("SystemError", new Object[] { e.getMessage() });

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

    private void doConfigureInstance(Long componentNo, ComponentProcessContext context, boolean start, Long instanceNo,
            Map<String, Object> rootMap) {
        Component component = componentDao.read(componentNo);
        Instance instance = instanceDao.read(instanceNo);

        // ログ用情報を格納
        LoggingUtils.setInstanceNo(instanceNo);
        LoggingUtils.setInstanceName(instance.getInstanceName());
        LoggingUtils.setInstanceType(processLogger.getInstanceType(instanceNo));
        LoggingUtils.setPlatformNo(instance.getPlatformNo());

        if (log.isInfoEnabled()) {
            String code = start ? "IPROCESS-100221" : "IPROCESS-100223";
            log.info(MessageUtils.getMessage(code, componentNo, instanceNo, component.getComponentName(), instance
                    .getInstanceName()));
        }

        try {
            configureInstance(componentNo, context, start, instanceNo, rootMap);
        } catch (RuntimeException e) {
            if (start) {
                ComponentInstance componentInstance = componentInstanceDao.read(componentNo, instanceNo);
                ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());

                // イベントログ出力
                if (BooleanUtils.isTrue(componentInstance.getConfigure())) {
                    if (status == ComponentInstanceStatus.STARTING) {
                        processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentStartFail", null);
                    } else if (status == ComponentInstanceStatus.CONFIGURING) {
                        processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentReloadFail", null);
                    } else if (status == ComponentInstanceStatus.STOPPING) {
                        processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentStopFail", null);
                    }
                }

                // ステータス更新
                if (status != ComponentInstanceStatus.WARNING || BooleanUtils.isTrue(componentInstance.getConfigure())) {
                    componentInstance.setStatus(ComponentInstanceStatus.WARNING.toString());
                    componentInstance.setConfigure(false);
                    componentInstanceDao.update(componentInstance);
                }

                throw e;
            } else {
                // 処理に失敗した場合、警告ログを出力する
                log.warn(e.getMessage());
            }
        }

        ComponentInstance componentInstance = componentInstanceDao.read(componentNo, instanceNo);
        ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());

        // イベントログ出力
        if (BooleanUtils.isTrue(componentInstance.getConfigure())) {
            if (status == ComponentInstanceStatus.STARTING) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentStartFinish", null);
            } else if (status == ComponentInstanceStatus.CONFIGURING) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentReloadFinish", null);
            } else if (status == ComponentInstanceStatus.STOPPING) {
                processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentStopFinish", null);
            }
        }

        // ステータスを変更
        if (start) {
            if (status == ComponentInstanceStatus.CONFIGURING || status == ComponentInstanceStatus.STARTING
                    || BooleanUtils.isTrue(componentInstance.getConfigure())) {
                componentInstance.setStatus(ComponentInstanceStatus.RUNNING.toString());
                componentInstance.setConfigure(false);
                componentInstanceDao.update(componentInstance);
            }
        } else {
            if (status == ComponentInstanceStatus.STOPPING || BooleanUtils.isTrue(componentInstance.getConfigure())) {
                componentInstance.setStatus(ComponentInstanceStatus.STOPPED.toString());
                componentInstance.setConfigure(false);
                componentInstanceDao.update(componentInstance);
            }
        }

        if (!start) {
            // コンポーネントとインスタンスの関連付けを削除
            deleteAssociate(componentNo, instanceNo);
        }

        if (log.isInfoEnabled()) {
            String code = start ? "IPROCESS-100222" : "IPROCESS-100224";
            log.info(MessageUtils.getMessage(code, componentNo, instanceNo, component.getComponentName(), instance
                    .getInstanceName()));
        }
    }

    @Override
    protected void configureInstance(Long componentNo, ComponentProcessContext context, boolean start, Long instanceNo,
            Map<String, Object> rootMap) {
        if (start) {
            // ボリュームの開始処理
            startVolume(componentNo, instanceNo);
        } else {
            // Zabbixテンプレートの終了処理
            stopZabbixTemplate(componentNo, instanceNo);
        }

        if (!start) {
            // ボリュームの終了処理
            stopVolume(componentNo, instanceNo);
        } else {
            // Zabbixテンプレートの開始処理
            startZabbixTemplate(componentNo, instanceNo);
        }
    }

    @Override
    protected void generateManifest(String templateName, Map<String, Object> rootMap, File file, String encoding) {
        //必要なし
        return;
    }

    @Override
    protected void runPuppet(Instance instance, Component component) {
        //必要なし
        return;
    }

    @Override
    protected Map<String, Object> createComponentMap(Long componentNo, ComponentProcessContext context, boolean start) {
        Map<String, Object> map = new HashMap<String, Object>();
        //必要なし
        return map;
    }

    @Override
    protected Map<String, Object> createInstanceMap(Long componentNo, ComponentProcessContext context, boolean start,
            Long instanceNo, Map<String, Object> rootMap) {
        Map<String, Object> map = new HashMap<String, Object>(rootMap);
        //必要なし
        return map;
    }

    @Override
    protected String getFileDigest(File file, String encoding) {
        //必要なし
        return null;
    }

    @Override
    protected void restoreManifest(Long componentNo, Long instanceNo) {
        //必要なし
        return;
    }

    @Override
    protected void backupManifest(Long componentNo, Long instanceNo) {
        //必要なし
        return;
    }

    @Override
    protected void deleteManifest(Long componentNo, Long instanceNo) {
        //必要なし
        return;
    }

    @Override
    protected void startAwsVolume(Long componentNo, Long instanceNo) {
        // デバイス名の取得
        String device = getAwsVolumeDevice();
        if (StringUtils.isEmpty(device)) {
            // デバイス名を取得できない場合はスキップ
            return;
        }

        // ボリューム情報の取得
        AwsVolume awsVolume = awsVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);

        // ボリューム情報がない場合は作成する
        if (awsVolume == null) {
            // ディスクサイズの取得
            Integer diskSize = null;
            ComponentConfig diskSizeConfig = componentConfigDao.readByComponentNoAndConfigName(componentNo,
                    ComponentConstants.CONFIG_NAME_DISK_SIZE);
            if (diskSizeConfig != null) {
                try {
                    diskSize = Integer.valueOf(diskSizeConfig.getConfigValue());
                } catch (NumberFormatException ignore) {
                }
            }
            if (diskSize == null) {
                // ディスクサイズが指定されていない場合はスキップ
                return;
            }

            Instance instance = instanceDao.read(instanceNo);
            AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

            awsVolume = new AwsVolume();
            awsVolume.setFarmNo(instance.getFarmNo());
            awsVolume.setVolumeName("vol");
            awsVolume.setPlatformNo(instance.getPlatformNo());
            awsVolume.setComponentNo(componentNo);
            awsVolume.setInstanceNo(instanceNo);
            awsVolume.setSize(diskSize);
            awsVolume.setAvailabilityZone(awsInstance.getAvailabilityZone());
            awsVolume.setDevice(device);
            awsVolumeDao.create(awsVolume);

        }
        // ディスクがアタッチされておらず、アタッチするデバイスが異なる場合は変更する
        else if (StringUtils.isEmpty(awsVolume.getInstanceId()) && !StringUtils.equals(device, awsVolume.getDevice())) {
            awsVolume.setDevice(device);
            awsVolumeDao.update(awsVolume);
        }

        //GWは呼ばず作成済みへ持っていく
        // ボリューム情報の再取得
        AwsVolume awsVolume2 = awsVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        awsVolume2.setStatus("in-use");
        awsVolume2.setInstanceId("WindowsDummy");
        awsVolumeDao.update(awsVolume2);


    }

    @Override
    protected void startCloudStackVolume(Long componentNo, Long instanceNo) {
        // ボリューム情報の取得
        CloudstackVolume cloudstackVolume = cloudstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);

        // ボリューム情報がない場合は作成する
        if (cloudstackVolume == null) {
            // ディスクサイズの取得
            Integer diskSize = null;
            ComponentConfig diskSizeConfig = componentConfigDao.readByComponentNoAndConfigName(componentNo,
                    ComponentConstants.CONFIG_NAME_DISK_SIZE);
            if (diskSizeConfig != null) {
                try {
                    diskSize = Integer.valueOf(diskSizeConfig.getConfigValue());
                } catch (NumberFormatException ignore) {
                }
            }

            if (diskSize == null) {
                // ディスクサイズが指定されていない場合はスキップ
                return;
            }

            Instance instance = instanceDao.read(instanceNo);
            CloudstackInstance cloudstackInstance = cloudstackInstanceDao.read(instanceNo);

            //NAME ZONEID SIZE SNAPSHOTID DISKOFFERINGID が必要
            // DISKOFFERINGID と SNAPSHOTID のいずれか一方が必要
            cloudstackVolume = new CloudstackVolume();
            cloudstackVolume.setFarmNo(instance.getFarmNo());
            cloudstackVolume.setName("vol");
            cloudstackVolume.setPlatformNo(instance.getPlatformNo());
            cloudstackVolume.setComponentNo(componentNo);
            cloudstackVolume.setInstanceNo(instanceNo);
            cloudstackVolume.setSize(diskSize);
            cloudstackVolume.setZoneid(cloudstackInstance.getZoneid());
            cloudstackVolumeDao.create(cloudstackVolume);
        }else if (cloudstackVolume.getInstanceId() != null && !"".equals(cloudstackVolume.getInstanceId())){
            //すでにアタッチ済みの場合は処理を行わない
            //AWS等はGWでチェックし何もせずリターンするがPuppetの処理の都合上ここでリターンする
            return;
        }

        //GWは呼ばず作成済みへ持っていく
        // ボリューム情報の再取得
        CloudstackVolume cloudstackVolume2 = cloudstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        cloudstackVolume2.setState("Allocated");
        cloudstackVolume2.setInstanceId("WindowsDummy");
        cloudstackVolumeDao.update(cloudstackVolume2);
    }


    @Override
    protected void stopAwsVolume(Long componentNo, Long instanceNo) {
        AwsVolume awsVolume = awsVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (awsVolume == null) {
            // ボリュームがない場合はスキップ
            return;
        }

        //GWは呼ばず開放へ持っていく
        awsVolume.setStatus("available");
        awsVolume.setInstanceId("");
        awsVolumeDao.update(awsVolume);

    }

    @Override
    protected void stopCloudStackVolume(Long componentNo, Long instanceNo) {
        CloudstackVolume cloudstackVolume = cloudstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (cloudstackVolume == null) {
            // ボリュームがない場合はスキップ
            return;
        }

        //GWは呼ばず開放へ持っていく
        cloudstackVolume.setState("Releasing");
        cloudstackVolume.setInstanceId("");
        cloudstackVolumeDao.update(cloudstackVolume);
    }

    @Override
    protected void startZabbixTemplate(Long componentNo, Long instanceNo) {
        if (zabbixInstanceDao.countByInstanceNo(instanceNo) > 0) {
            zabbixHostProcess.startTemplate(instanceNo, componentNo);
        }
    }

    @Override
    protected void stopZabbixTemplate(Long componentNo, Long instanceNo) {
        if (zabbixInstanceDao.countByInstanceNo(instanceNo) > 0) {
            try {
                zabbixHostProcess.stopTemplate(instanceNo, componentNo);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }
}
