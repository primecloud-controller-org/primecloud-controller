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
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.puppet.PuppetClient;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class PuppetLoadBalancerProcess extends ServiceSupport {

    protected File manifestDir;

    protected FreeMarkerGenerator freeMarkerGenerator;

    protected PuppetClient puppetClient;

    protected ExecutorService executorService;

    protected ProcessLogger processLogger;

    public void configure(final Long loadBalancerNo, final Long componentNo, List<Long> instanceNos) {
        final Map<String, Object> rootMap = createRootMap(loadBalancerNo, componentNo, instanceNos);

        // インスタンスが１つの場合は並列実行しない
        if (instanceNos.size() == 1) {
            configureInstance(loadBalancerNo, componentNo, instanceNos.get(0), rootMap);
            return;
        }

        // 並列実行
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        final Map<String, Object> loggingContext = LoggingUtils.getContext();
        for (final Long instanceNo : instanceNos) {
            Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LoggingUtils.setContext(loggingContext);
                    try {
                        Map<String, Object> map = new HashMap<String, Object>(rootMap);
                        configureInstance(loadBalancerNo, componentNo, instanceNo, map);
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

    protected void configureInstance(Long loadBalancerNo, Long componentNo, Long instanceNo, Map<String, Object> rootMap) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        Component component = componentDao.read(componentNo);
        Instance instance = instanceDao.read(instanceNo);

        // インスタンス情報
        Map<String, Object> instanceMap = createInstanceMap(instanceNo);
        rootMap.putAll(instanceMap);

        // マニフェストファイル
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        File manifestFile = new File(manifestDir, instance.getFqdn() + "." + component.getComponentName() + ".pp");

        // マニフェストファイルのリストア
        restoreManifest(manifestFile);

        // 既にあるマニフェストファイルのダイジェストを求める
        String digest = getFileDigest(manifestFile, "UTF-8");

        // ロードバランサ停止時でマニフェストが存在しない場合、スキップする
        if (digest == null && BooleanUtils.isNotTrue(loadBalancer.getEnabled())) {
            return;
        }

        // マニフェストの生成
        String templateName = "loadBalancer_" + componentType.getComponentTypeName() + ".ftl";
        generateManifest(templateName, rootMap, manifestFile, "UTF-8");

        // 生成したマニフェストのダイジェストの比較
        if (digest != null) {
            String newDigest = getFileDigest(manifestFile, "UTF-8");
            if (digest.equals(newDigest)) {
                // マニフェストファイルに変更がない場合
                if (log.isDebugEnabled()) {
                    log.debug(MessageUtils.format("Not changed manifest.(file={0})", manifestFile.getName()));
                }
                return;
            }
        }

        // Puppetクライアントの設定更新指示
        try {
            runPuppet(instance, component);
        } finally {
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                // マニフェストファイルのバックアップ
                backupManifest(manifestFile);
            } else {
                // マニフェストファイルの削除
                deleteManifest(manifestFile);
            }
        }
    }

    protected void runPuppet(Instance instance, Component component) {
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        String type = "loadBalancer_" + componentType.getComponentTypeName();

        // Puppetクライアントの設定更新処理を実行
        try {
            processLogger.debug(component, instance, "PuppetManifestApply", new String[] { instance.getFqdn(), type });

            puppetClient.runClient(instance.getFqdn());

        } catch (RuntimeException e) {
            processLogger.debug(component, instance, "PuppetManifestApplyFail", new String[] { instance.getFqdn(), type });

            // マニフェスト適用に失敗した場合、警告ログ出力した後にリトライする
            String code = (e instanceof AutoException) ? AutoException.class.cast(e).getCode() : null;
            if ("EPUPPET-000003".equals(code) || "EPUPPET-000007".equals(code)) {
                log.warn(e.getMessage());

                processLogger.debug(component, instance, "PuppetManifestApply", new String[] { instance.getFqdn(), type });

                try {
                    puppetClient.runClient(instance.getFqdn());

                } catch (RuntimeException e2) {
                    processLogger.debug(component, instance, "PuppetManifestApplyFail", new String[] { instance.getFqdn(), type });

                    throw e2;
                }
            } else {
                throw e;
            }
        }

        processLogger.debug(component, instance, "PuppetManifestApplyFinish", new String[] { instance.getFqdn(), type });
    }

    protected Map<String, Object> createRootMap(Long loadBalancerNo, Long componentNo, List<Long> instanceNos) {
        Map<String, Object> map = new HashMap<String, Object>();

        // Component
        Component component = componentDao.read(componentNo);
        map.put("component", component);

        // Instance
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        map.put("instances", instances);

        // Farm
        Farm farm = farmDao.read(component.getFarmNo());
        map.put("farm", farm);

        // User
        User user = userDao.read(farm.getUserNo());
        PccSystemInfo pccSystemInfo = pccSystemInfoDao.read();
        PasswordEncryptor encryptor = new PasswordEncryptor();
        String decryptPass = encryptor.decrypt(user.getPassword(), pccSystemInfo.getSecretKey());
        user.setPassword(decryptPass);
        map.put("user", user);

        // ComponentConfig
        List<ComponentConfig> componentConfigs = componentConfigDao.readByComponentNo(componentNo);
        Map<String, Object> configs = new HashMap<String, Object>();
        for (ComponentConfig componentConfig : componentConfigs) {
            configs.put(componentConfig.getConfigName(), componentConfig.getConfigValue());
        }
        map.put("configs", configs);

        // ロードバランサ情報
        Map<String, Object> loadBalancerMap = createLoadBalancerMap(loadBalancerNo);
        map.putAll(loadBalancerMap);

        return map;
    }

    protected Map<String, Object> createLoadBalancerMap(Long loadBalancerNo) {
        Map<String, Object> map = new HashMap<String, Object>();

        // LoadBalancer
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        map.put("loadBalancer", loadBalancer);

        // LoadBalancerListener
        List<LoadBalancerListener> allListeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);
        List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();
        for (LoadBalancerListener listener : allListeners) {
            if (BooleanUtils.isTrue(listener.getEnabled())) {
                listeners.add(listener);
            }
        }
        map.put("listeners", listeners);

        // LoadBalancerHealthCheck
        LoadBalancerHealthCheck healthCheck = loadBalancerHealthCheckDao.read(loadBalancerNo);
        map.put("healthCheck", healthCheck);

        // LoadBalancerinstance
        List<LoadBalancerInstance> lbInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
        List<Long> targetInstanceNos = new ArrayList<Long>();
        for (LoadBalancerInstance lbInstance : lbInstances) {
            if (BooleanUtils.isTrue(lbInstance.getEnabled())) {
                targetInstanceNos.add(lbInstance.getInstanceNo());
            }
        }
        List<Instance> tmpInstances = instanceDao.readInInstanceNos(targetInstanceNos);
        List<Instance> targetInstances = new ArrayList<Instance>();
        for (Instance tmpInstance : tmpInstances) {
            if (BooleanUtils.isTrue(tmpInstance.getEnabled())) {
                InstanceStatus status = InstanceStatus.fromStatus(tmpInstance.getStatus());
                if (status == InstanceStatus.RUNNING) {
                    targetInstances.add(tmpInstance);
                }
            }
        }
        map.put("targetInstances", targetInstances);

        return map;
    }

    protected Map<String, Object> createInstanceMap(Long instanceNo) {
        Map<String, Object> map = new HashMap<String, Object>();

        // Instance
        Instance instance = instanceDao.read(instanceNo);
        map.put("instance", instance);

        // アクセスIP
        List<Instance> allInstances = instanceDao.readByFarmNo(instance.getFarmNo());
        Platform platform = platformDao.read(instance.getPlatformNo());
        Map<String, String> accessIps = new HashMap<String, String>();
        for (Instance instance2 : allInstances) {
            // 起動していないインスタンスは対象外
            InstanceStatus status = InstanceStatus.fromStatus(instance2.getStatus());
            if (status != InstanceStatus.RUNNING) {
                continue;
            }

            // 基本はpublicIpでアクセスする
            String accessIp = instance2.getPublicIp();
            if (instance.getPlatformNo().equals(instance2.getPlatformNo())) {
                // 同一のプラットフォームの場合
                // TODO CLOUD BRANCHING
                if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                    PlatformAws platformAws = platformAwsDao.read(instance2.getPlatformNo());
                    if (BooleanUtils.isFalse(platformAws.getVpc())) {
                        // VPCを使用しない場合はprivateIpでアクセスする
                        accessIp = instance2.getPrivateIp();
                    }
                } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                    // CloudStackプラットフォームの場合はgetPublicIpでアクセスする
                    accessIp = instance2.getPublicIp();
                } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                    // VMwareプラットフォームの場合はprivateIpでアクセスする
                    accessIp = instance2.getPrivateIp();
                    //nifty
                } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                    // ニフティクラウドプラットフォームの場合はprivateIpでアクセスする
                    accessIp = instance2.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                    // VCloudプラットフォームの場合はprivateIpでアクセスする
                    accessIp = instance2.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                    // Azureプラットフォームの場合はgetPublicIpでアクセスする
                    accessIp = instance2.getPublicIp();
                } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                    // Openstackプラットフォームの場合はgetPublicIpでアクセスする
                    accessIp = instance2.getPublicIp();
                }
            }
            accessIps.put(instance2.getInstanceNo().toString(), accessIp);
        }
        map.put("accessIps", accessIps);

        // 待ち受けIP
        List<String> listenIps = new ArrayList<String>();
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            // AWSの場合
            listenIps.add(instance.getPrivateIp());
            PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
            if (BooleanUtils.isFalse(platform.getInternal()) && BooleanUtils.isFalse(platformAws.getVpc())) {
                // 外部のAWSプラットフォームでVPNを利用する場合(VPC+VPNでは無い、通常のVPNの場合)、PublicIpでも待ち受ける
                listenIps.add(instance.getPublicIp());
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            // CloudStackの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            // VMwareの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            // Niftyの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            // VCloudの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            // Azureの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            // Openstackの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        }

        map.put("listenIps", listenIps);

        return map;
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

    protected void restoreManifest(File manifestFile) {
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

    protected void backupManifest(File manifestFile) {
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

    protected void deleteManifest(File manifestFile) {
        // マニフェストファイルを削除する
        if (!manifestFile.exists()) {
            return;
        }
        try {
            FileUtils.forceDelete(manifestFile);
        } catch (IOException e) {
            // マニフェストファイルの削除失敗時
            log.warn(e.getMessage());
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
     * executorServiceを設定します。
     *
     * @param executorService executorService
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
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
