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
package jp.primecloud.auto.process.puppet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import jp.primecloud.auto.common.component.FreeMarkerGenerator;
import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.AzureDisk;
import jp.primecloud.auto.entity.crud.AzureInstance;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.NiftyVolume;
import jp.primecloud.auto.entity.crud.OpenstackInstance;
import jp.primecloud.auto.entity.crud.OpenstackVolume;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PuppetInstance;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VcloudInstance;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.nifty.process.NiftyProcessClient;
import jp.primecloud.auto.nifty.process.NiftyProcessClientFactory;
import jp.primecloud.auto.process.ComponentConstants;
import jp.primecloud.auto.process.ComponentProcessContext;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.process.nifty.NiftyVolumeProcess;
import jp.primecloud.auto.process.vmware.VmwareDiskProcess;
import jp.primecloud.auto.process.vmware.VmwareProcessClient;
import jp.primecloud.auto.process.vmware.VmwareProcessClientFactory;
import jp.primecloud.auto.process.zabbix.ZabbixHostProcess;
import jp.primecloud.auto.puppet.PuppetClient;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class PuppetComponentProcess extends ServiceSupport {

    protected File manifestDir;

    protected FreeMarkerGenerator freeMarkerGenerator;

    protected PuppetClient puppetClient;

    protected IaasGatewayFactory iaasGatewayFactory;

    protected VmwareProcessClientFactory vmwareProcessClientFactory;

    protected ExecutorService executorService;

    protected VmwareDiskProcess vmwareDiskProcess;

    protected ZabbixHostProcess zabbixHostProcess;

    protected PuppetNodeProcess puppetNodeProcess;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    protected String componentTypeName;

    protected String awsVolumeDevice;

    protected String cloudStackDiskofferingid;

    protected Integer vmwareDiskScsiId;

    protected Integer vcloudDiskUnitNo;

    protected NiftyProcessClientFactory niftyProcessClientFactory;

    protected NiftyVolumeProcess niftyVolumeProcess;

    public String getComponentTypeName() {
        return componentTypeName;
    }

    public void createNodeManifest(ComponentProcessContext context) {
        // ノード用マニフェストの生成
        Farm farm = farmDao.read(context.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        List<Component> components = componentDao.readByFarmNo(context.getFarmNo());
        Map<Long, Component> componentMap = new HashMap<Long, Component>();
        for (Component component: components) {
            componentMap.put(component.getComponentNo(), component);
        }
        List<Instance> instances = instanceDao.readInInstanceNos(context.getRunningInstanceNos());
        Map<Long, Instance> instanceMap = new HashMap<Long, Instance>();
        for (Instance instance: instances) {
            instanceMap.put(instance.getInstanceNo(), instance);
        }
        List<ComponentType> componentTypes = componentTypeDao.readAll();
        Map<Long, ComponentType> componentTypeMap = new HashMap<Long, ComponentType>();
        for (ComponentType componentType: componentTypes) {
            componentTypeMap.put(componentType.getComponentTypeNo(), componentType);
        }

        // myCloud内のデータベースサーバリスト
        List<Instance> allDbInstances = new ArrayList<Instance>();
        // myCloud内のアプリケーションサーバリスト
        List<Instance> allApInstances = new ArrayList<Instance>();
        // myCloud内のWEBサーバリスト
        List<Instance> allWebInstances = new ArrayList<Instance>();
        // 自サービスと同じデータベースサーバリストのマップ
        Map<Long, List<Instance>> dbInstancesMap = new HashMap<Long, List<Instance>>();
        // 自サービスと同じアプリケーションサーバのマップ
        Map<Long, List<Instance>> apInstancesMap = new HashMap<Long, List<Instance>>();
        // 自サービスと同じWEBサーバリストのマップ
        Map<Long, List<Instance>> webInstancesMap = new HashMap<Long, List<Instance>>();
        // myCloud内のすべてのコンポーネント
        List<ComponentInstance> allComponentInstances = componentInstanceDao.readInInstanceNos(context.getRunningInstanceNos());
        for (ComponentInstance componentInstance : allComponentInstances) {
            // 無効な関連は除外
            if (BooleanUtils.isNotTrue(componentInstance.getEnabled())
                    || BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                continue;
            }

            Component component = componentMap.get(componentInstance.getComponentNo());
            Instance instance = instanceMap.get(componentInstance.getInstanceNo());
            ComponentType componentType = componentTypeMap.get(component.getComponentTypeNo());
            if (ComponentConstants.LAYER_NAME_DB.equals(componentType.getLayer())) {
                allDbInstances.add(instance);
                List<Instance> dbInstances = dbInstancesMap.get(component.getComponentNo());
                if (dbInstances == null) {
                    dbInstances = new ArrayList<Instance>();
                }
                dbInstances.add(instance);
                dbInstancesMap.put(component.getComponentNo(), dbInstances);
            } else if (ComponentConstants.LAYER_NAME_AP_JAVA.equals(componentType.getLayer())) {
                allApInstances.add(instance);
                List<Instance> apInstances = apInstancesMap.get(component.getComponentNo());
                if (apInstances == null) {
                    apInstances = new ArrayList<Instance>();
                }
                apInstances.add(instance);
                apInstancesMap.put(component.getComponentNo(), apInstances);
            } else if (ComponentConstants.LAYER_NAME_WEB.equals(componentType.getLayer())) {
                allWebInstances.add(instance);
                List<Instance> webInstances = webInstancesMap.get(component.getComponentNo());
                if (webInstances == null) {
                    webInstances = new ArrayList<Instance>();
                }
                webInstances.add(instance);
                webInstancesMap.put(component.getComponentNo(), webInstances);
            }
        }

        for (Instance instance : instances) {
            Platform platform = platformDao.read(instance.getPlatformNo());

            //OSがwindowsの場合は作成しない
            Image image = imageDao.read(instance.getImageNo());
            if (StringUtils.startsWithIgnoreCase(image.getOs(), PCCConstant.OS_NAME_WIN)) {
                continue;
            }

            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put("farm", farm);
            rootMap.put("user", user);
            rootMap.put("components", components);
            rootMap.put("instance", instance);
            rootMap.put("platform", platform);

            // 関連するコンポーネント
            List<Component> associatedComponents = new ArrayList<Component>();
            List<ComponentType> associatedComponentTypes = new ArrayList<ComponentType>();

            List<ComponentInstance> componentInstances = componentInstanceDao
                    .readByInstanceNo(instance.getInstanceNo());
            for (ComponentInstance componentInstance : componentInstances) {
                // 無効な関連は除外
                if (BooleanUtils.isNotTrue(componentInstance.getEnabled())
                        || BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                    continue;
                }

                Component component = componentMap.get(componentInstance.getComponentNo());
                ComponentType componentType = componentTypeMap.get(component.getComponentTypeNo());
                associatedComponents.add(component);
                associatedComponentTypes.add(componentType);
            }

            rootMap.put("associatedComponents", associatedComponents);
            rootMap.put("associatedComponentTypes", associatedComponentTypes);

            // 自サービスと同じデータベースサーバリスト
            List<Instance> dbInstances = new ArrayList<Instance>();
            // 自サービスと同じアプリケーションサーバリスト
            List<Instance> apInstances = new ArrayList<Instance>();
            // 自サービスと同じWEBサーバリスト
            List<Instance> webInstances = new ArrayList<Instance>();
            for (Component component: associatedComponents) {
                ComponentType componentType = componentTypeMap.get(component.getComponentTypeNo());
                if (ComponentConstants.LAYER_NAME_DB.equals(componentType.getLayer())) {
                    dbInstances = dbInstancesMap.get(component.getComponentNo());
                } else if (ComponentConstants.LAYER_NAME_AP_JAVA.equals(componentType.getLayer())) {
                    apInstances = apInstancesMap.get(component.getComponentNo());
                } else if (ComponentConstants.LAYER_NAME_WEB.equals(componentType.getLayer())) {
                    webInstances = webInstancesMap.get(component.getComponentNo());
                }
            }

            rootMap.put("dbInstances", dbInstances);
            rootMap.put("apInstances", apInstances);
            rootMap.put("webInstances", webInstances);

            rootMap.put("allDbInstances", allDbInstances);
            rootMap.put("allApInstances", allApInstances);
            rootMap.put("allWebInstances", allWebInstances);

            File manifestFile = new File(manifestDir, instance.getFqdn() + ".pp");
            generateManifest("node.ftl", rootMap, manifestFile, "UTF-8");
        }
    }

    public void startComponent(Long componentNo, ComponentProcessContext context) {
        // コンポーネントを開始するサーバがない場合はスキップ
        List<Long> instanceNos = context.getEnableInstanceNoMap().get(componentNo);
        if (instanceNos == null || instanceNos.isEmpty()) {
            return;
        }

        Component component = componentDao.read(componentNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100211", componentNo, component.getComponentName()));
        }

        // コンポーネントの開始処理を実行
        configureInstances(componentNo, context, true, instanceNos);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100212", componentNo, component.getComponentName()));
        }
    }

    public void stopComponent(Long componentNo, ComponentProcessContext context) {
        // コンポーネントを停止するサーバがない場合はスキップ
        List<Long> instanceNos = context.getDisableInstanceNoMap().get(componentNo);
        if (instanceNos == null || instanceNos.isEmpty()) {
            return;
        }

        Component component = componentDao.read(componentNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100213", componentNo, component.getComponentName()));
        }

        try {
            // コンポーネントの停止処理を実行
            configureInstances(componentNo, context, false, instanceNos);

        } catch (RuntimeException ignore) {
            // 処理に失敗した場合、警告ログを出力する
            log.warn(ignore.getMessage());
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100214", componentNo, component.getComponentName()));
        }
    }

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
                    processLogger.writeLogSupport(ProcessLogger.LOG_INFO, component, instance, "ComponentStart", null);
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

        // マニフェストファイルのリストア
        restoreManifest(componentNo, instanceNo);

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
        } finally {
            if (start) {
                // マニフェストファイルのバックアップ
                backupManifest(componentNo, instanceNo);
            } else {
                // コンポーネントのマニフェスト削除
                deleteManifest(componentNo, instanceNo);
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

    protected void configureInstance(Long componentNo, ComponentProcessContext context, boolean start, Long instanceNo,
            Map<String, Object> rootMap) {
        Component component = componentDao.read(componentNo);
        Instance instance = instanceDao.read(instanceNo);
        ComponentInstance componentInstance = componentInstanceDao.read(componentNo, instanceNo);

        if (start) {
            // ボリュームの開始処理
            startVolume(componentNo, instanceNo);
        } else {
            // Zabbixテンプレートの終了処理
            stopZabbixTemplate(componentNo, instanceNo);
        }

        // マニフェスト用情報モデルにインスタンス情報固有を追加
        rootMap = createInstanceMap(componentNo, context, start, instanceNo, rootMap);

        // マニフェストの出力
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        File manifestName = new File(manifestDir, instance.getFqdn() + "." + component.getComponentName() + ".pp");

        // 既にあるマニフェストファイルのダイジェストを求める
        String digest = getFileDigest(manifestName, "UTF-8");

        // コンポーネント停止時でマニフェストが存在しない場合、スキップする
        if (digest == null && !start) {
            return;
        }

        // マニフェストの生成
        String templateName = componentType.getComponentTypeName() + ".ftl";
        generateManifest(templateName, rootMap, manifestName, "UTF-8");

        // 強制設定をしない場合
        if (BooleanUtils.isNotTrue(componentInstance.getConfigure())) {
            // 既にあるマニフェストファイルのダイジェストと、生成したマニフェストファイルのダイジェストと比較する
            if (digest != null) {
                String newDigest = getFileDigest(manifestName, "UTF-8");
                if (digest.equals(newDigest)) {
                    // マニフェストファイルに変更がない場合
                    if (log.isDebugEnabled()) {
                        log.debug(MessageUtils.format("Not changed manifest.(file={0})", manifestName.getName()));
                    }
                    return;
                }
            }
        }

        // Puppetクライアントの設定更新指示
        runPuppet(instance, component);

        if (!start) {
            // ボリュームの終了処理
            stopVolume(componentNo, instanceNo);
        } else {
            // Zabbixテンプレートの開始処理
            startZabbixTemplate(componentNo, instanceNo);
        }
    }

    protected void generateManifest(String templateName, Map<String, Object> rootMap, File file, String encoding) {
        String data = freeMarkerGenerator.generate(templateName, rootMap);

        // 改行コードの変換（PuppetはLFでないと読み込めないことへの対応）
        data = data.replaceAll("\r\n", "\n");

        // 出力先ディレクトリの作成
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // ファイル出力
        try {
            FileUtils.writeStringToFile(file, data, encoding);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void runPuppet(Instance instance, Component component) {
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        String type = componentType.getComponentTypeName();

        // Puppetクライアントの設定更新処理を実行
        try {
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "PuppetManifestApply", new String[] { instance.getFqdn(), type });

            puppetClient.runClient(instance.getFqdn());

        } catch (RuntimeException e) {
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "PuppetManifestApplyFail", new String[] { instance.getFqdn(), type });

            // マニフェスト適用に失敗した場合、警告ログ出力した後にリトライする
            String code = (e instanceof AutoException) ? AutoException.class.cast(e).getCode() : null;
            if ("EPUPPET-000003".equals(code) || "EPUPPET-000007".equals(code)) {
                log.warn(e.getMessage());

                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "PuppetManifestApply", new String[] { instance.getFqdn(), type });

                try {
                    puppetClient.runClient(instance.getFqdn());

                } catch (RuntimeException e2) {
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "PuppetManifestApplyFail", new String[] { instance.getFqdn(), type });

                    throw e2;
                }
            } else {
                throw e;
            }
        }

        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "PuppetManifestApplyFinish", new String[] { instance.getFqdn(), type });
    }

    protected Map<String, Object> createComponentMap(Long componentNo, ComponentProcessContext context, boolean start) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", start);

        // Component
        Component component = componentDao.read(componentNo);
        map.put("component", component);

        // Farm
        Farm farm = farmDao.read(component.getFarmNo());
        map.put("farm", farm);

        // User
        User user = userDao.read(farm.getUserNo());
        PccSystemInfo pccSystemInfo = pccSystemInfoDao.read();
        PasswordEncryptor encryptor = new PasswordEncryptor();
        user.setPassword(encryptor.decrypt(user.getPassword(), pccSystemInfo.getSecretKey()));
        map.put("user", user);

        // ComponentConfig
        List<ComponentConfig> componentConfigs = componentConfigDao.readByComponentNo(componentNo);
        Map<String, Object> configs = new HashMap<String, Object>();
        for (ComponentConfig componentConfig : componentConfigs) {
            configs.put(componentConfig.getConfigName(), componentConfig.getConfigValue());

            if (ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_1.equals(componentConfig.getConfigName())) {
                map.put("customParam1", componentConfig.getConfigValue());
            }
            if (ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_2.equals(componentConfig.getConfigName())) {
                map.put("customParam2", componentConfig.getConfigValue());
            }
            if (ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_3.equals(componentConfig.getConfigName())) {
                map.put("customParam3", componentConfig.getConfigValue());
            }
        }
        map.put("configs", configs);

        return map;
    }

    protected Map<String, Object> createInstanceMap(Long componentNo, ComponentProcessContext context, boolean start,
            Long instanceNo, Map<String, Object> rootMap) {
        Map<String, Object> map = new HashMap<String, Object>(rootMap);

        // Instance
        Instance instance = instanceDao.read(instanceNo);
        map.put("instance", instance);

        // PuppetInstance
        PuppetInstance puppetInstance = puppetInstanceDao.read(instanceNo);
        map.put("puppetInstance", puppetInstance);

        // InstanceConfig
        List<InstanceConfig> instanceConfigs = instanceConfigDao.readByInstanceNo(instanceNo);
        Map<String, Object> configs = new HashMap<String, Object>();
        for (InstanceConfig instanceConfig : instanceConfigs) {
            if (componentNo.equals(instanceConfig.getComponentNo())) {
                configs.put(instanceConfig.getConfigName(), instanceConfig.getConfigValue());
            }
        }
        map.put("instanceConfigs", configs);

        // Platform
        Platform platform = platformDao.read(instance.getPlatformNo());
        map.put("platform", platform);
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            // AwsInstance
            AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
            map.put("awsInstance", awsInstance);

            // AwsVolume
            AwsVolume awsVolume = awsVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
            if (awsVolume != null) {
                map.put("awsVolume", awsVolume);
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            // CloudStackInstance
            CloudstackInstance cloudstackInstance = cloudstackInstanceDao.read(instanceNo);
            map.put("cloudstackInstance", cloudstackInstance);

            // CloudStackVolume
            CloudstackVolume cloudstackVolume = cloudstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
            if (cloudstackVolume != null) {
                map.put("cloudstackVolume", cloudstackVolume);
            }
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            // VmwareInstance
            VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
            map.put("vmwareInstance", vmwareInstance);

            // VmwareDisk
            VmwareDisk vmwareDisk = vmwareDiskDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
            if (vmwareDisk != null) {
                map.put("vmwareDisk", vmwareDisk);
            }
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            // NiftyInstance
            NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);
            map.put("niftyInstance", niftyInstance);
            // NiftyVolume
            NiftyVolume niftyVolume = niftyVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
            if (niftyVolume != null) {
                map.put("niftyVolume", niftyVolume);
            }
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            // VcloudInstance
            VcloudInstance vcloudInstance = vcloudInstanceDao.read(instanceNo);
            map.put("vcloudInstance", vcloudInstance);

            // VcloudDisk
            VcloudDisk vcloudDisk = null;
            List<VcloudDisk> vcloudDisks = vcloudDiskDao.readByComponentNo(componentNo);
            for (VcloudDisk tmpVcloudDisk: vcloudDisks) {
                if (tmpVcloudDisk.getInstanceNo().equals(instanceNo)) {
                    vcloudDisk = tmpVcloudDisk;
                    break;
                }
            }
            if (vcloudDisk != null) {
                map.put("vcloudDisk", vcloudDisk);
            }
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            // AzureInstance
            AzureInstance azureInstance = azureInstanceDao.read(instanceNo);
            map.put("azureInstance", azureInstance);

            // AzureDisk
            AzureDisk azureDisk = azureDiskDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
            if (azureDisk != null) {
                map.put("azureDisk", azureDisk);
            }
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            // OpenstackInstance
            OpenstackInstance openstackInstance = openstackInstanceDao.read(instanceNo);
            map.put("openstackInstance", openstackInstance);
            // OpenstackVolume
            OpenstackVolume openstackVolume = openstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
            if(openstackVolume != null) {
                map.put("openstackVolume", openstackVolume);
            }
        }

        // アクセスIP
        List<Instance> runningInstances = instanceDao.readInInstanceNos(context.getRunningInstanceNos());
        Map<String, String> accessIps = new HashMap<String, String>();
        for (Instance runningInstance : runningInstances) {
            // 基本はpublicIpでアクセスする
            String accessIp = runningInstance.getPublicIp();
            if (instance.getPlatformNo().equals(runningInstance.getPlatformNo())) {
                // 同一のプラットフォームの場合
                // TODO CLOUD BRANCHING
                if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                    PlatformAws platformAws = platformAwsDao.read(runningInstance.getPlatformNo());
                    if (BooleanUtils.isFalse(platformAws.getVpc())) {
                        // VPCを使用しない場合はprivateIpでアクセスする
                        accessIp = runningInstance.getPrivateIp();
                    }
                } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                    // CloudstackはpublicIpでアクセスする
                    accessIp = runningInstance.getPublicIp();
                } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                    // VMwareプラットフォームの場合はprivateIpでアクセスする
                    accessIp = runningInstance.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                    // ニフティクラウドプラットフォームの場合はprivateIpでアクセスする
                    accessIp = runningInstance.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                    // VCloudプラットフォームの場合はprivateIpでアクセスする
                    accessIp = runningInstance.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                    // Azureプラットフォームの場合はprivateIpでアクセスする
                    accessIp = runningInstance.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                    // OpenstackはpublicIpでアクセスする
                    accessIp = runningInstance.getPublicIp();
                }
            }
            accessIps.put(runningInstance.getInstanceNo().toString(), accessIp);
        }
        map.put("accessIps", accessIps);

        // ボリュームをデタッチしないかどうか
        boolean unDetachVolume = BooleanUtils.toBoolean(Config.getProperty("unDetachVolume"));
        map.put("unDetachVolume", unDetachVolume);

        return map;
    }

    protected String getFileDigest(File file, String encoding) {
        if (!file.exists()) {
            return null;
        }
        try {
            String content = FileUtils.readFileToString(file, encoding);
            return DigestUtils.shaHex(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void restoreManifest(Long componentNo, Long instanceNo) {
        Component component = componentDao.read(componentNo);
        Instance instance = instanceDao.read(instanceNo);
        File manifestFile = new File(manifestDir, instance.getFqdn() + "." + component.getComponentName() + ".pp");

        // マニフェストファイルのリストア
        File backupDir = new File(manifestDir, "backup");
        File backupFile = new File(backupDir, manifestFile.getName());

        if (!backupFile.exists()) {
            return;
        }

        try {
            if (manifestFile.exists()) {
                FileUtils.forceDelete(manifestFile);
            }
            FileUtils.moveFile(backupFile, manifestFile);
        } catch (IOException e) {
            // マニフェストファイルのリストア失敗時
            log.warn(e.getMessage());
        }
    }

    protected void backupManifest(Long componentNo, Long instanceNo) {
        Component component = componentDao.read(componentNo);
        Instance instance = instanceDao.read(instanceNo);
        File manifestFile = new File(manifestDir, instance.getFqdn() + "." + component.getComponentName() + ".pp");

        if (!manifestFile.exists()) {
            return;
        }

        // マニフェストファイルのバックアップ
        File backupDir = new File(manifestDir, "backup");
        File backupFile = new File(backupDir, manifestFile.getName());
        try {
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }
            if (backupFile.exists()) {
                FileUtils.forceDelete(backupFile);
            }
            FileUtils.moveFile(manifestFile, backupFile);
        } catch (IOException e) {
            // マニフェストファイルのバックアップ失敗時
            log.warn(e.getMessage());
        }
    }

    protected void deleteManifest(Long componentNo, Long instanceNo) {
        Component component = componentDao.read(componentNo);
        Instance instance = instanceDao.read(instanceNo);

        // マニフェストファイルを削除する
        File file = new File(manifestDir, instance.getFqdn() + "." + component.getComponentName() + ".pp");
        if (!file.exists()) {
            return;
        }
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            // マニフェストファイルの削除失敗時
            log.warn(e.getMessage());
        }
    }

    protected void deleteAssociate(Long componentNo, Long instanceNo) {
        ComponentInstance componentInstance = componentInstanceDao.read(componentNo, instanceNo);

        // コンポーネントとインスタンスの関連付けが無効で、コンポーネントが停止している場合、レコードを削除する
        if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
            if (status == ComponentInstanceStatus.STOPPED) {
                componentInstanceDao.delete(componentInstance);
            }
        }
    }

    protected String getAwsVolumeDevice() {
        return awsVolumeDevice;
    }

    protected Integer getVmwareDiskScsiId() {
        return vmwareDiskScsiId;
    }

    protected String getCloudStackDiskofferingid() {
        return cloudStackDiskofferingid;
    }

    protected Integer getVcloudDiskUnitNo() {
        return vcloudDiskUnitNo;
    }

    protected void startVolume(Long componentNo, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            startAwsVolume(componentNo, instanceNo);
        }else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            startCloudStackVolume(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            startVmwareDisk(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            startVcloudDisk(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            startAzureDisk(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            startOpenstackVolume(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            startNiftyVolume(componentNo, instanceNo);
        }
    }

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

        // AwsProcessClientの作成
        Farm farm = farmDao.read(awsVolume.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), awsVolume.getPlatformNo());

        // ボリュームの開始処理
        gateway.startVolume(instanceNo, awsVolume.getVolumeNo());
    }

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

        // IaasGatewayWrapperの作成
        Farm farm = farmDao.read(cloudstackVolume.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), cloudstackVolume.getPlatformNo());

//        //再起動を掛ける為、Puppet終了設定処理
//        try {
//            // 終了設定処理
//            if (puppetInstanceDao.countByInstanceNo(instanceNo) > 0) {
//                puppetNodeProcess.stopNode(instanceNo);
//            }
//        } catch (RuntimeException e) {
//            log.warn(e.getMessage());
//        }


        // ボリュームの開始処理
        gateway.startVolume(instanceNo, cloudstackVolume.getVolumeNo());


//        // Puppet初期設定処理
//        if (puppetInstanceDao.countByInstanceNo(instanceNo) > 0) {
//            log.info("Puppet 終了設定処理");
//            puppetNodeProcess.startNode(instanceNo);
//        }


    }


    protected void startVmwareDisk(Long componentNo, Long instanceNo) {
        // SCSI IDの取得
        Integer scsiId = getVmwareDiskScsiId();
        if (scsiId == null) {
            // SCSI IDを取得できない場合はスキップ
            return;
        }

        // ディスク情報の取得
        VmwareDisk vmwareDisk = vmwareDiskDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);

        // ディスク情報がない場合は作成する
        if (vmwareDisk == null) {
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

            vmwareDisk = new VmwareDisk();
            vmwareDisk.setFarmNo(instance.getFarmNo());
            vmwareDisk.setPlatformNo(instance.getPlatformNo());
            vmwareDisk.setComponentNo(componentNo);
            vmwareDisk.setInstanceNo(instanceNo);
            vmwareDisk.setSize(diskSize);
            vmwareDisk.setScsiId(scsiId);
            vmwareDiskDao.create(vmwareDisk);
        }
        // ディスクがアタッチされておらず、アタッチするSCSI IDが異なる場合は変更する
        else if (BooleanUtils.isNotTrue(vmwareDisk.getAttached()) && !scsiId.equals(vmwareDisk.getScsiId())) {
            vmwareDisk.setScsiId(scsiId);
            vmwareDiskDao.update(vmwareDisk);
        }

        // VmwareProcessClientの作成
        VmwareProcessClient vmwareProcessClient = vmwareProcessClientFactory.createVmwareProcessClient(vmwareDisk.getPlatformNo());

        try {
            // ディスクの開始処理
            vmwareDiskProcess.attachDisk(vmwareProcessClient, instanceNo, vmwareDisk.getDiskNo());

        } finally {
            vmwareProcessClient.getVmwareClient().logout();
        }
    }

    protected void startVcloudDisk(Long componentNo, Long instanceNo) {
        // ディスク情報の取得
        VcloudDisk vcloudDisk = null;
        List<VcloudDisk> vcloudDisks = vcloudDiskDao.readByComponentNo(componentNo);
        for (VcloudDisk tmpVcloudDisk: vcloudDisks) {
            if (tmpVcloudDisk.getInstanceNo().equals(instanceNo)) {
                vcloudDisk = tmpVcloudDisk;
                break;
            }
        }

        // ディスク情報がない場合は作成する
        if (vcloudDisk == null) {
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

            vcloudDisk = new VcloudDisk();
            vcloudDisk.setFarmNo(instance.getFarmNo());
            vcloudDisk.setPlatformNo(instance.getPlatformNo());
            vcloudDisk.setComponentNo(componentNo);
            vcloudDisk.setInstanceNo(instanceNo);
            vcloudDisk.setSize(diskSize);
            //UNIT NOはIaasGateWayでディスクをアタッチした後に動的に決定するので、NULLで登録
            vcloudDisk.setUnitNo(null);
            vcloudDisk.setAttached(false);
            //サービスで追加されるディスクはデータディスクではないのでfalseを設定
            vcloudDisk.setDataDisk(false);
            vcloudDiskDao.create(vcloudDisk);
        }
        // ディスクがアタッチされていない場合は UNIT NO が変わる可能性があるので明示的にNULLに変更する
        // IaasGateWayでアタッチ後UNIT NOが設定される
        else if (BooleanUtils.isNotTrue(vcloudDisk.getAttached())) {
            vcloudDisk.setUnitNo(null);
            vcloudDiskDao.update(vcloudDisk);
        }

        // IaasGatewayWrapperの作成
        Farm farm = farmDao.read(vcloudDisk.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), vcloudDisk.getPlatformNo());

        // ボリューム(ディスク)の開始処理
        gateway.startVolume(instanceNo, vcloudDisk.getDiskNo());
    }

    protected void startAzureDisk(Long componentNo, Long instanceNo) {
        // ボリューム情報の取得
        AzureDisk azureDisk = azureDiskDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);

        // ボリューム情報がない場合は作成する
        if (azureDisk == null) {
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
            //AzureInstance azureInstance = azureInstanceDao.read(instanceNo);

            //NAME ZONEID SIZE SNAPSHOTID DISKOFFERINGID が必要
            // DISKOFFERINGID と SNAPSHOTID のいずれか一方が必要
            azureDisk = new AzureDisk();
            azureDisk.setFarmNo(instance.getFarmNo());
            azureDisk.setPlatformNo(instance.getPlatformNo());
            azureDisk.setComponentNo(componentNo);
            azureDisk.setInstanceNo(instanceNo);
            //azureDisk.setDiskName(diskName);
            //azureDisk.setInstanceName(azureInstance.getInstanceName());
            //azureDisk.setLun(lun);
            azureDisk.setSize(diskSize);
            azureDiskDao.create(azureDisk);
        }else if (azureDisk.getInstanceName() != null && !"".equals(azureDisk.getInstanceName())){
            //すでにアタッチ済みの場合は処理を行わない
            //AWS等はGWでチェックし何もせずリターンするがPuppetの処理の都合上ここでリターンする
            return;
        }

        // IaasGatewayWrapperの作成
        Farm farm = farmDao.read(azureDisk.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), azureDisk.getPlatformNo());

        // ボリュームの開始処理
        gateway.startVolume(instanceNo, azureDisk.getDiskNo());

    }

    protected void startOpenstackVolume(Long componentNo, Long instanceNo) {
        // ボリューム情報の取得
        OpenstackVolume openstackVolume = openstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);

        // ボリューム情報がない場合は作成する
        if (openstackVolume == null) {
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
            StringBuilder sb = new StringBuilder();
            Instance instance = instanceDao.read(instanceNo);
            OpenstackInstance openstackInstance = openstackInstanceDao.read(instanceNo);
            //コンポーネント情報の取得
            Component component = componentDao.read(componentNo);
            String compName = null;
            //FQDNが設定されている場合
            if (StringUtils.isNotEmpty(instance.getFqdn())) {
                //ボリューム名称としてFQDNを設定
                sb.append(instance.getFqdn());
                //コンポーネント名称がある場合、FQDN_コンポーネント名称の形式に編集
                if (StringUtils.isNotEmpty(component.getComponentName())) {
                    sb.append("_");
                    sb.append(component.getComponentName());
                }
                compName = sb.toString();
            } else {
                compName = "vol";
            }

            openstackVolume = new OpenstackVolume();
            openstackVolume.setFarmNo(instance.getFarmNo());
            openstackVolume.setVolumeName(compName);
            openstackVolume.setPlatformNo(instance.getPlatformNo());
            openstackVolume.setComponentNo(componentNo);
            openstackVolume.setInstanceNo(instanceNo);
            openstackVolume.setSize(diskSize);
            openstackVolume.setAvailabilityZone(openstackInstance.getAvailabilityZone());
            openstackVolumeDao.create(openstackVolume);
        }

        // AwsProcessClientの作成
        Farm farm = farmDao.read(openstackVolume.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), openstackVolume.getPlatformNo());

        // ボリュームの開始処理
        gateway.startVolume(instanceNo, openstackVolume.getVolumeNo());
    }

    protected void startNiftyVolume(Long componentNo, Long instanceNo) {
        // ボリューム情報の取得
        NiftyVolume niftyVolume = niftyVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);

        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());
        // ボリューム情報がない場合は作成する
        if (niftyVolume == null) {
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
            StringBuilder sb = new StringBuilder();
            //コンポーネント情報の取得
            Component component = componentDao.read(componentNo);
            String compName = null;
            //FQDNが設定されている場合
            if (StringUtils.isNotEmpty(instance.getFqdn())) {
                //ボリューム名称としてFQDNを設定
                sb.append(instance.getFqdn());
                //コンポーネント名称がある場合、FQDN_コンポーネント名称の形式に編集
                if (StringUtils.isNotEmpty(component.getComponentName())) {
                    sb.append("_");
                    sb.append(component.getComponentName());
                }
                compName = sb.toString();
            } else {
                compName = "vol";
            }

            niftyVolume = new NiftyVolume();
            niftyVolume.setFarmNo(instance.getFarmNo());
            niftyVolume.setVolumeName(compName);
            niftyVolume.setPlatformNo(instance.getPlatformNo());
            niftyVolume.setComponentNo(componentNo);
            niftyVolume.setInstanceNo(instanceNo);
            niftyVolume.setSize(diskSize);
            niftyVolumeDao.create(niftyVolume);
        }

        // NiftyProcessClientの作成
        String clientType;
        clientType = PCCConstant.NIFTYCLIENT_TYPE_DISK;
        NiftyProcessClient niftyProcessClient = niftyProcessClientFactory.createNiftyProcessClient(farm.getUserNo(),
                niftyVolume.getPlatformNo(), clientType);
        // ディスクの開始処理
        niftyVolumeProcess.startVoiume(niftyProcessClient, instanceNo, niftyVolume.getVolumeNo());
    }

    protected void stopVolume(Long componentNo, Long instanceNo) {
        // デタッチ処理を行わない場合はスキップ
        boolean unDetachVolume = BooleanUtils.toBoolean(Config.getProperty("unDetachVolume"));
        if (unDetachVolume) {
            return;
        }

        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            stopAwsVolume(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            stopCloudStackVolume(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            stopVmwareDisk(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            stopVcloudDisk(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            stopAzureDisk(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            stopOpenstackVolume(componentNo, instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            stopNiftyVolume(componentNo, instanceNo);
        }
    }

    protected void stopAwsVolume(Long componentNo, Long instanceNo) {
        AwsVolume awsVolume = awsVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (awsVolume == null) {
            // ボリュームがない場合はスキップ
            return;
        }

        // AwsProcessClientの作成
        Farm farm = farmDao.read(awsVolume.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), awsVolume.getPlatformNo());

        // ボリュームの終了処理
        gateway.stopVolume(instanceNo, awsVolume.getVolumeNo());
    }

    protected void stopCloudStackVolume(Long componentNo, Long instanceNo) {
        CloudstackVolume cloudstackVolume = cloudstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (cloudstackVolume == null) {
            // ボリュームがない場合はスキップ
            return;
        }

        // IaasGatewayWrapperの作成
        Farm farm = farmDao.read(cloudstackVolume.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), cloudstackVolume.getPlatformNo());

        // ボリュームの終了処理
        gateway.stopVolume(instanceNo, cloudstackVolume.getVolumeNo());
    }

    protected void stopVmwareDisk(Long componentNo, Long instanceNo) {
        VmwareDisk vmwareDisk = vmwareDiskDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (vmwareDisk == null) {
            // ディスクがない場合はスキップ
            return;
        }

        // VmwareProcessClientの作成
        VmwareProcessClient vmwareProcessClient = vmwareProcessClientFactory.createVmwareProcessClient(vmwareDisk.getPlatformNo());

        try {
            // ディスクの終了処理
            vmwareDiskProcess.detachDisk(vmwareProcessClient, instanceNo, vmwareDisk.getDiskNo());

        } finally {
            vmwareProcessClient.getVmwareClient().logout();
        }
    }

    protected void stopVcloudDisk(Long componentNo, Long instanceNo) {
        VcloudDisk vcloudDisk = null;
        List<VcloudDisk> vcloudDisks = vcloudDiskDao.readByComponentNo(componentNo);
        for (VcloudDisk tmpVcloudDisk: vcloudDisks) {
            if (tmpVcloudDisk.getInstanceNo().equals(instanceNo)) {
                vcloudDisk = tmpVcloudDisk;
                break;
            }
        }

        if (vcloudDisk == null) {
            // ディスクがない場合はスキップ
            return;
        }

        // IaasGatewayWrapperの作成
        Farm farm = farmDao.read(vcloudDisk.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), vcloudDisk.getPlatformNo());

        // ボリューム(ディスク)の停止処理
        gateway.stopVolume(instanceNo, vcloudDisk.getDiskNo());
    }

    protected void stopAzureDisk(Long componentNo, Long instanceNo) {
        AzureDisk azureDisk = azureDiskDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (azureDisk == null) {
            // ボリュームがない場合はスキップ
            return;
        }

        // IaasGatewayWrapperの作成
        Farm farm = farmDao.read(azureDisk.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), azureDisk.getPlatformNo());

        // ボリュームの終了処理
        gateway.stopVolume(instanceNo, azureDisk.getDiskNo());
    }

    protected void stopOpenstackVolume(Long componentNo, Long instanceNo) {
        OpenstackVolume openstackVolume = openstackVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (openstackVolume == null) {
            // ボリュームがない場合はスキップ
            return;
        }

        // AwsProcessClientの作成
        Farm farm = farmDao.read(openstackVolume.getFarmNo());
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), openstackVolume.getPlatformNo());

        // ボリュームの終了処理
        gateway.stopVolume(instanceNo, openstackVolume.getVolumeNo());
    }

    protected void stopNiftyVolume(Long componentNo, Long instanceNo) {
        NiftyVolume niftyVolume = niftyVolumeDao.readByComponentNoAndInstanceNo(componentNo, instanceNo);
        if (niftyVolume == null) {
            // ボリュームがない場合はスキップ
            return;
        }

        // NiftyProcessClientの作成
        Farm farm = farmDao.read(niftyVolume.getFarmNo());
        String clientType;
        clientType = PCCConstant.NIFTYCLIENT_TYPE_DISK;
        NiftyProcessClient niftyProcessClient = niftyProcessClientFactory.createNiftyProcessClient(farm.getUserNo(),
                niftyVolume.getPlatformNo(), clientType);
        // デタッチ処理
        niftyVolumeProcess.stopVolume(niftyProcessClient, instanceNo, niftyVolume.getVolumeNo());
    }

    protected void startZabbixTemplate(Long componentNo, Long instanceNo) {
        if (zabbixInstanceDao.countByInstanceNo(instanceNo) > 0) {
            zabbixHostProcess.startTemplate(instanceNo, componentNo);
        }
    }

    protected void stopZabbixTemplate(Long componentNo, Long instanceNo) {
        if (zabbixInstanceDao.countByInstanceNo(instanceNo) > 0) {
            try {
                zabbixHostProcess.stopTemplate(instanceNo, componentNo);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    /**
     * manifestDirを設定します。
     *
     * @param manifestDir manifestDir
     */
    public void setManifestDir(File manifestDir) {
        this.manifestDir = manifestDir;
    }

    /**
     * freeMarkerGeneratorを設定します。
     *
     * @param freeMarkerGenerator freeMarkerGenerator
     */
    public void setFreeMarkerGenerator(FreeMarkerGenerator freeMarkerGenerator) {
        this.freeMarkerGenerator = freeMarkerGenerator;
    }

    /**
     * puppetClientを設定します。
     *
     * @param puppetClient puppetClient
     */
    public void setPuppetClient(PuppetClient puppetClient) {
        this.puppetClient = puppetClient;
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
     * vmwareProcessClientFactoryを設定します。
     *
     * @param vmwareProcessClientFactory vmwareProcessClientFactory
     */
    public void setVmwareProcessClientFactory(VmwareProcessClientFactory vmwareProcessClientFactory) {
        this.vmwareProcessClientFactory = vmwareProcessClientFactory;
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
     * vmwareDiskProcessを設定します。
     *
     * @param vmwareDiskProcess vmwareDiskProcess
     */
    public void setVmwareDiskProcess(VmwareDiskProcess vmwareDiskProcess) {
        this.vmwareDiskProcess = vmwareDiskProcess;
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
     * puppetNodeProcessを設定します。
     *
     * @param puppetNodeProcess puppetNodeProcess
     */
    public void setPuppetNodeProcess(PuppetNodeProcess puppetNodeProcess) {
        this.puppetNodeProcess = puppetNodeProcess;
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
     * componentTypeNameを設定します。
     *
     * @param componentTypeName componentTypeName
     */
    public void setComponentTypeName(String componentTypeName) {
        this.componentTypeName = componentTypeName;
    }

    /**
     * awsVolumeDeviceを設定します。
     *
     * @param awsVolumeDevice awsVolumeDevice
     */
    public void setAwsVolumeDevice(String awsVolumeDevice) {
        this.awsVolumeDevice = awsVolumeDevice;
    }

    /**
     * cloudStackDiskofferingidを設定します。
     *
     * @param cloudStackDiskofferingid cloudStackDiskofferingid
     */
    public void setCloudStackDiskofferingid(String cloudStackDiskofferingid) {
        this.cloudStackDiskofferingid = cloudStackDiskofferingid;
    }

    /**
     * vmwareDiskScsiIdを設定します。
     *
     * @param vmwareDiskScsiId vmwareDiskScsiId
     */
    public void setVmwareDiskScsiId(Integer vmwareDiskScsiId) {
        this.vmwareDiskScsiId = vmwareDiskScsiId;
    }

    /**
     * vcloudDiskUnitNoを設定します。
     *
     * @param vcloudDiskUnitNo vcloudDiskUnitNo
     */
    public void setVcloudDiskUnitNo(Integer vcloudDiskUnitNo) {
        this.vcloudDiskUnitNo = vcloudDiskUnitNo;
    }

    /**
     * niftyProcessClientFactoryを設定します。
     * @param niftyProcessClientFactory niftyProcessClientFactory
     */
    public void setNiftyProcessClientFactory(NiftyProcessClientFactory niftyProcessClientFactory) {
        this.niftyProcessClientFactory = niftyProcessClientFactory;
    }

    /**
     * niftyVolumeProcessを設定します。
     * @param niftyVolumeProcess niftyVolumeProcess
     */
    public void setNiftyVolumeProcess(NiftyVolumeProcess niftyVolumeProcess) {
        this.niftyVolumeProcess = niftyVolumeProcess;
    }

}
