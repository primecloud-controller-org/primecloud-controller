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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.component.FreeMarkerGenerator;
import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.InstanceCoodinateStatus;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PuppetInstance;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.InstancesProcessContext;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.puppet.PuppetClient;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

/**
 * <p>
 * puppetrunを実行するための情報収集と制御を行う
 * </p>
 *
 */
public class PuppetNodesProcess extends ServiceSupport {

    protected File manifestDir;

    protected FreeMarkerGenerator freeMarkerGenerator;

    protected PuppetClient puppetClient;

    protected ExecutorService executorService;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    public void configureNodes(InstancesProcessContext context) {
        // 処理対象のインスタンスがない場合はスキップ
        if ((context.getStartInstanceNos() == null || context.getStartInstanceNos().isEmpty())
                && (context.getStopInstanceNos() == null || context.getStopInstanceNos().isEmpty())) {
            return;
        }

        configureInstances(context);
    }

    protected void configureInstances(final InstancesProcessContext context) {
        List<Long> startInstanceNos = context.getStartInstanceNos();
        if (startInstanceNos == null) {
            startInstanceNos = new ArrayList<Long>();
        }
        List<Long> stopInstanceNos = context.getStopInstanceNos();
        if (stopInstanceNos == null) {
            stopInstanceNos = new ArrayList<Long>();
        }

        List<Long> targetInstanceNos = new ArrayList<Long>();
        targetInstanceNos.addAll(startInstanceNos);
        targetInstanceNos.addAll(stopInstanceNos);

        // Puppet設定対象のものを取得
        List<PuppetInstance> puppetInstances = puppetInstanceDao.readInInstanceNos(targetInstanceNos);
        List<Long> puppetInstanceNos = new ArrayList<Long>();
        for (PuppetInstance puppetInstance : puppetInstances) {
            puppetInstanceNos.add(puppetInstance.getInstanceNo());
        }

        // ステータスを変更
        List<Instance> instances = instanceDao.readInInstanceNos(targetInstanceNos);
        for (Instance instance : instances) {
            if (puppetInstanceNos.contains(instance.getInstanceNo())) {
                InstanceCoodinateStatus status;
                if (startInstanceNos.contains(instance.getInstanceNo())) {
                    status = InstanceCoodinateStatus.COODINATING;
                } else {
                    status = InstanceCoodinateStatus.UN_COODINATING;
                }

                //instance.setStatus(InstanceStatus.CONFIGURING.toString());
                instance.setCoodinateStatus(status.toString());
                instanceDao.update(instance);
            }
        }

        // Puppet設定対象のものが無ければ終了
        if (puppetInstanceNos.isEmpty()) {
            return;
        }

        // マニフェスト用情報モデルの作成
        final Map<String, Object> rootMap = createInstancesMap(context);

        // 並列実行
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        final Map<String, Object> loggingContext = LoggingUtils.getContext();
        for (final Long instanceNo : puppetInstanceNos) {
            final boolean start = startInstanceNos.contains(instanceNo) ? true : false;
            Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    LoggingUtils.setContext(loggingContext);
                    try {
                        doConfigureInstance(instanceNo, context, start, rootMap);
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

    protected Map<String, Object> createInstancesMap(InstancesProcessContext context) {
        Map<String, Object> map = new HashMap<String, Object>();

        // Farm
        Farm farm = farmDao.read(context.getFarmNo());
        map.put("farm", farm);

        // User
        User user = userDao.read(farm.getUserNo());
        PccSystemInfo pccSystemInfo = pccSystemInfoDao.read();
        PasswordEncryptor encryptor = new PasswordEncryptor();
        user.setPassword(encryptor.decrypt(user.getPassword(), pccSystemInfo.getSecretKey()));
        map.put("user", user);

        // Instances
        List<Instance> startInstances;
        if (context.getStartInstanceNos() == null || context.getStartInstanceNos().isEmpty()) {
            startInstances = new ArrayList<Instance>();
        } else {
            startInstances = instanceDao.readInInstanceNos(context.getStartInstanceNos());
        }
        map.put("startInstances", startInstances);

        List<Instance> stopInstances;
        if (context.getStopInstanceNos() == null || context.getStopInstanceNos().isEmpty()) {
            stopInstances = new ArrayList<Instance>();
        } else {
            stopInstances = instanceDao.readInInstanceNos(context.getStopInstanceNos());
        }
        map.put("stopInstances", stopInstances);

        return map;
    }

    private void doConfigureInstance(Long instanceNo, InstancesProcessContext context, boolean start,
            Map<String, Object> rootMap) {
        Instance instance = instanceDao.read(instanceNo);

        // ログ用情報を格納
        LoggingUtils.setInstanceNo(instanceNo);
        LoggingUtils.setInstanceName(instance.getInstanceName());
        LoggingUtils.setInstanceType(processLogger.getInstanceType(instanceNo));
        LoggingUtils.setPlatformNo(instance.getPlatformNo());

        // マニフェストファイルのリストア
        restoreManifest(instanceNo);

        try {
            configureInstance(instanceNo, context, start, rootMap);
        } catch (RuntimeException e) {
            if (start) {
                // ステータス更新
                instance = instanceDao.read(instanceNo);
                //instance.setStatus(InstanceStatus.WARNING.toString());
                instance.setCoodinateStatus(InstanceCoodinateStatus.WARNING.toString());
                instanceDao.update(instance);

                throw e;
            } else {
                // 処理に失敗した場合、警告ログを出力する
                log.warn(e.getMessage());
            }
        } finally {
            if (start) {
                // マニフェストファイルのバックアップ
                backupManifest(instanceNo);
            } else {
                // コンポーネントのマニフェスト削除
                deleteManifest(instanceNo);
            }
        }

        // ステータスを変更
        InstanceCoodinateStatus status = start ? InstanceCoodinateStatus.COODINATED
                : InstanceCoodinateStatus.UN_COODINATED;

        instance = instanceDao.read(instanceNo);
        //instance.setStatus(InstanceStatus.RUNNING.toString());
        instance.setCoodinateStatus(status.toString());
        instanceDao.update(instance);
    }

    protected void configureInstance(Long instanceNo, InstancesProcessContext context, boolean start,
            Map<String, Object> rootMap) {
        Instance instance = instanceDao.read(instanceNo);

        // マニフェスト用情報モデルにインスタンス情報固有を追加
        rootMap = createInstanceMap(instanceNo, context, start, rootMap);

        // マニフェストの出力
        File manifestName = new File(manifestDir, instance.getFqdn() + ".base_coordinate.pp");

        // 既にあるマニフェストファイルのダイジェストを求める
        String digest = getFileDigest(manifestName, "UTF-8");

        // 協調停止時でマニフェストが存在しない場合、スキップする
        if (digest == null && !start) {
            return;
        }

        // マニフェストの生成
        String templateName = "base_coordinate.ftl";
        generateManifest(templateName, rootMap, manifestName, "UTF-8");

        // 協調設定がされてない場合以外で、マニフェストが変更されていない場合はスキップする
        InstanceCoodinateStatus status = InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus());
        if (status != InstanceCoodinateStatus.UN_COODINATED) {
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
        runPuppet(instance);
    }

    protected void runPuppet(Instance instance) {
        Image image = imageDao.read(instance.getImageNo());
        // Puppetクライアントの設定更新処理を実行
        try {
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "PuppetManifestApply",
                    new String[] { instance.getFqdn(), "base_coordinate" });

            puppetClient.runClient(instance.getFqdn());
            if (StringUtils.startsWithIgnoreCase(image.getOs(), PCCConstant.OS_NAME_WIN)) {
                // TODO 協調設定が反映されない不具合対応
                // 1回の「puppet run」だと協調設定が空振りする事があるので、同じマニフェストの内容で2回実行する。
                // Linux系OSに関しては、puppetの「postrun_command」で対応可能なので実行は1回のみ
                log.debug(MessageUtils.format("run the puppet process(base_coordinate) twice for windows instance. (fqdn={0})", instance.getFqdn()));
                puppetClient.runClient(instance.getFqdn());
            }

        } catch (RuntimeException e) {
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                    "PuppetManifestApplyFail", new String[] { instance.getFqdn(), "base_coordinate" });

            // マニフェスト適用に失敗した場合、警告ログ出力した後にリトライする
            String code = (e instanceof AutoException) ? AutoException.class.cast(e).getCode() : null;
            if ("EPUPPET-000003".equals(code) || "EPUPPET-000007".equals(code)) {
                log.warn(e.getMessage());

                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                        "PuppetManifestApply", new String[] { instance.getFqdn(), "base_coordinate" });

                try {
                    puppetClient.runClient(instance.getFqdn());

                } catch (RuntimeException e2) {
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                            "PuppetManifestApplyFail", new String[] { instance.getFqdn(), "base_coordinate" });

                    throw e2;
                }
            } else {
                throw e;
            }
        }

        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                "PuppetManifestApplyFinish", new String[] { instance.getFqdn(), "base_coordinate" });
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> createInstanceMap(Long instanceNo, InstancesProcessContext context, boolean start,
            Map<String, Object> rootMap) {
        Map<String, Object> map = new HashMap<String, Object>(rootMap);

        // start
        map.put("start", start);

        // Instance
        Instance instance = instanceDao.read(instanceNo);
        map.put("instance", instance);

        // Platform
        Platform platform = platformDao.read(instance.getPlatformNo());
        map.put("platform", platform);

        // アクセスIP
        List<Instance> startInstances = (List<Instance>) rootMap.get("startInstances");
        Map<String, String> accessIps = new HashMap<String, String>();
        for (Instance startInstance : startInstances) {
            // 基本はpublicIpでアクセスする
            String accessIp = startInstance.getPublicIp();
            if (instance.getPlatformNo().equals(startInstance.getPlatformNo())) {
                // 同一のプラットフォームの場合
                // TODO CLOUD BRANCHING
                if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                    PlatformAws platformAws = platformAwsDao.read(startInstance.getPlatformNo());
                    if (platformAws.getVpc() == false) {
                        // VPCを使用しない場合はprivateIpでアクセスする
                        accessIp = startInstance.getPrivateIp();
                    }
                } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                    // Cloudstackプラットフォームの場合はpublicIpでアクセスする
                    accessIp = startInstance.getPublicIp();
                } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                    // VMwareプラットフォームの場合はprivateIpでアクセスする
                    accessIp = startInstance.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                    // ニフティクラウドプラットフォームの場合はprivateIpでアクセスする
                    accessIp = startInstance.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                    // VCloudプラットフォームの場合はprivateIpでアクセスする
                    accessIp = startInstance.getPrivateIp();
                } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                    // Azureプラットフォームの場合はpublicIpでアクセスする
                    accessIp = startInstance.getPublicIp();
                } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                    // Openstackプラットフォームの場合はpublicIpでアクセスする
                    accessIp = startInstance.getPublicIp();
                }
            }
            accessIps.put(startInstance.getInstanceNo().toString(), accessIp);
        }
        map.put("accessIps", accessIps);

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

    protected void restoreManifest(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        File manifestFile = new File(manifestDir, instance.getFqdn() + ".base_coordinate.pp");

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

    protected void backupManifest(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        File manifestFile = new File(manifestDir, instance.getFqdn() + ".base_coordinate.pp");

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

    protected void deleteManifest(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // マニフェストファイルを削除する
        File file = new File(manifestDir, instance.getFqdn() + ".base_coordinate.pp");
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
