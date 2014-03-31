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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.common.component.FreeMarkerGenerator;
import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.common.component.PasswordGenerator;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PuppetInstance;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.puppet.PuppetClient;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.process.ProcessLogger;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class PuppetNodeProcess extends ServiceSupport {

    protected File manifestDir;

    protected FreeMarkerGenerator freeMarkerGenerator;

    protected PuppetClient puppetClient;

    protected PasswordGenerator passwordGenerator = new PasswordGenerator();

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    public void startNode(Long instanceNo) {
        PuppetInstance puppetInstance = puppetInstanceDao.read(instanceNo);
        if (puppetInstance == null) {
            // Puppet処理対象でない
            throw new AutoException("EPROCESS-000301", instanceNo);
        }

        Instance instance = instanceDao.read(instanceNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100201", instanceNo, instance.getInstanceName()));
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "InstanceBaseStart", null);

        // PuppetMaster用のパスワード発行
        createPassword(instanceNo);

        // Puppetクライアントの起動チェック
        checkClient(instanceNo);

        // マニフェスト用情報モデルの作成
        Map<String, Object> rootMap = createNodeMap(instanceNo, true);

        // ノード用マニフェストの生成
        createNodeManifest(instanceNo, true, rootMap);

        // マニフェストのリストア
        restoreManifest(instanceNo);

        try {
            // マニフェスト生成と実行
            configureInstance(instanceNo, true, rootMap);
        } finally {
            // マニフェストファイルのバックアップ
            backupManifest(instanceNo);
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "InstanceBaseStartFinish", null);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100202", instanceNo, instance.getInstanceName()));
        }
    }

    public void stopNode(Long instanceNo) {
        PuppetInstance puppetInstance = puppetInstanceDao.read(instanceNo);
        if (puppetInstance == null) {
            // Puppet処理対象でない
            throw new AutoException("EPROCESS-000301", instanceNo);
        }

        Instance instance = instanceDao.read(instanceNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100203", instanceNo, instance.getInstanceName()));
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "InstanceBaseStop", null);

        // マニフェスト用情報モデルの作成
        Map<String, Object> rootMap = createNodeMap(instanceNo, false);

        // マニフェストのリストア
        restoreManifest(instanceNo);

        try {
            // マニフェスト生成と実行
            configureInstance(instanceNo, false, rootMap);
        } finally {
            // マニフェストファイルの削除
            deleteManifest(instanceNo);
        }

        // PuppetMaster用のパスワード削除
        deletePassword(instanceNo);

        // Puppetクライアントの認証情報を削除
        clearCa(instanceNo);

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "InstanceBaseStopFinish", null);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100204", instanceNo, instance.getInstanceName()));
        }
    }

    protected void createNodeManifest(Long instanceNo, boolean start, Map<String, Object> rootMap) {
        Instance instance = instanceDao.read(instanceNo);

        // ノード用マニフェストの生成
        File manifestFile = new File(manifestDir, instance.getFqdn() + ".pp");
        generateManifest("node.ftl", rootMap, manifestFile, "UTF-8");
    }

    protected void configureInstance(Long instanceNo, boolean start, Map<String, Object> rootMap) {
        Instance instance = instanceDao.read(instanceNo);

        File manifestFile = new File(manifestDir, instance.getFqdn() + ".base.pp");

        // 既にあるマニフェストのダイジェスト取得
        String digest = getFileDigest(manifestFile, "UTF-8");

        // 停止時でマニフェストが存在しない場合、スキップする
        if (digest == null && !start) {
            return;
        }

        // 基本マニフェストの生成
        generateManifest("base.ftl", rootMap, manifestFile, "UTF-8");

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

        // マニフェストに変更があった場合、Puppetクライアントの設定更新処理を実行
        try {
            runPuppet(instance);
        } catch (RuntimeException e) {
            if (!start) {
                // puppetrunに失敗した場合、警告ログを出力する
                log.warn(e.getMessage());
            } else {
                throw e;
            }
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

    protected void runPuppet(Instance instance) {
        // Puppetクライアントの設定更新処理を実行
        try {
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "PuppetManifestApply",
                    new String[] { instance.getFqdn(), "base" });

            puppetClient.runClient(instance.getFqdn());

        } catch (RuntimeException e) {
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                    "PuppetManifestApplyFail", new String[] { instance.getFqdn(), "base" });

            // マニフェスト適用に失敗した場合、警告ログ出力した後にリトライする
            String code = (e instanceof AutoException) ? AutoException.class.cast(e).getCode() : null;
            if ("EPUPPET-000003".equals(code) || "EPUPPET-000007".equals(code)) {
                log.warn(e.getMessage());

                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                        "PuppetManifestApply", new String[] { instance.getFqdn(), "base" });

                try {
                    puppetClient.runClient(instance.getFqdn());

                } catch (RuntimeException e2) {
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                            "PuppetManifestApplyFail", new String[] { instance.getFqdn(), "base" });

                    throw e2;
                }
            } else {
                throw e;
            }
        }

        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                "PuppetManifestApplyFinish", new String[] { instance.getFqdn(), "base" });
    }

    protected Map<String, Object> createNodeMap(Long instanceNo, boolean start) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", start);

        // Instance
        Instance instance = instanceDao.read(instanceNo);
        map.put("instance", instance);

        // Farm
        Farm farm = farmDao.read(instance.getFarmNo());
        map.put("farm", farm);

        // User
        User user = userDao.read(farm.getUserNo());
        PccSystemInfo pccSystemInfo = pccSystemInfoDao.read();
        PasswordEncryptor encryptor = new PasswordEncryptor();
        user.setPassword(encryptor.decrypt(user.getPassword(), pccSystemInfo.getSecretKey()));
        map.put("user", user);

        // Component
        List<Component> components = componentDao.readByFarmNo(instance.getFarmNo());
        map.put("components", components);

        // PuppetInstance
        PuppetInstance puppetInstance = puppetInstanceDao.read(instanceNo);
        map.put("puppetInstance", puppetInstance);

        // Platform
        Platform platform = platformDao.read(instance.getPlatformNo());
        map.put("platform", platform);

        if ("aws".equals(platform.getPlatformType())) {
            // AwsVolume
            List<AwsVolume> awsVolumes = awsVolumeDao.readByInstanceNo(instanceNo);
            map.put("awsVolumes", awsVolumes);
        } else if ("cloudstack".equals(platform.getPlatformType())) {
            // CloudStackVolume
            List<CloudstackVolume> cloudstackVolumes = cloudstackVolumeDao.readByInstanceNo(instanceNo);
            map.put("cloudstackVolumes", cloudstackVolumes);
        } else if ("vmware".equals(platform.getPlatformType())) {
            // VmwareDisk
            List<VmwareDisk> vmwareDisks = vmwareDiskDao.readByInstanceNo(instanceNo);
            map.put("vmwareDisks", vmwareDisks);
        }

        // その他
        map.put("zabbixServer", Config.getProperty("zabbix.server"));
        map.put("rsyslogServer", Config.getProperty("rsyslog.server"));

        // Zabbix待ち受けIP  TODO cloudstackの場合はパブリック？
        String zabbixListenIp = instance.getPublicIp();
        if ("aws".equals(platform.getPlatformType())) {
            PlatformAws platformAws = platformAwsDao.read(platform.getPlatformNo());
            if (BooleanUtils.isTrue(platform.getInternal())) {
                // 内部のAWSプラットフォームの場合はprivateIpで待ち受ける
                zabbixListenIp = instance.getPrivateIp();

            } else if (BooleanUtils.isTrue(platformAws.getVpc())) {
                // 外部のAWSプラットフォームでVPCを用いる場合はprivateIpで待ち受ける
                zabbixListenIp = instance.getPrivateIp();
            }
        }
        map.put("zabbixListenIp", zabbixListenIp);

        // 関連するコンポーネント
        List<Component> associatedComponents = new ArrayList<Component>();
        List<ComponentType> associatedComponentTypes = new ArrayList<ComponentType>();

        List<ComponentInstance> componentInstances = componentInstanceDao.readByInstanceNo(instanceNo);
        for (ComponentInstance componentInstance : componentInstances) {
            // 無効な関連は除外
            if (BooleanUtils.isNotTrue(componentInstance.getEnabled())
                    || BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                continue;
            }

            for (Component component : components) {
                if (component.getComponentNo().equals(componentInstance.getComponentNo())) {
                    associatedComponents.add(component);
                    ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
                    associatedComponentTypes.add(componentType);
                }
            }
        }

        map.put("associatedComponents", associatedComponents);
        map.put("associatedComponentTypes", associatedComponentTypes);

        return map;
    }

    protected void restoreManifest(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        File manifestFile = new File(manifestDir, instance.getFqdn() + ".base.pp");

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
        File manifestFile = new File(manifestDir, instance.getFqdn() + ".base.pp");

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

        // マニフェストファイルの取得
        final String fqdn = instance.getFqdn();
        File[] manifestFiles = manifestDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(fqdn) && name.endsWith(".pp");
            }
        });

        // マニフェストファイルの削除
        for (File manifestFile : manifestFiles) {
            try {
                if (manifestFile.exists()) {
                    FileUtils.forceDelete(manifestFile);
                }
            } catch (IOException e) {
                // マニフェストファイルの削除失敗時
                log.warn(e.getMessage());
            }
        }
    }

    protected void createPassword(Long instanceNo) {
        PuppetInstance puppetInstance = puppetInstanceDao.read(instanceNo);

        // パスワードが設定済みの場合はスキップする
        if (StringUtils.isNotEmpty(puppetInstance.getPassword())) {
            return;
        }

        // パスワードを発行
        String password = passwordGenerator.generate(50);

        // データベース更新
        puppetInstance.setPassword(password);
        puppetInstanceDao.update(puppetInstance);
    }

    protected void deletePassword(Long instanceNo) {
        PuppetInstance puppetInstance = puppetInstanceDao.read(instanceNo);

        // パスワードが空の場合はスキップする
        if (StringUtils.isEmpty(puppetInstance.getPassword())) {
            return;
        }

        puppetInstance.setPassword(null);
        puppetInstanceDao.update(puppetInstance);
    }

    protected void checkClient(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // Puppetクライアントが起動していることを確認する
        int retry = 20;
        for (int count = 0; count <= retry; count++) {
            List<String> clients = puppetClient.listClients();
            if (clients.contains(instance.getFqdn())) {
                break;
            }

            if (count == retry) {
                throw new AutoException("EPROCESS-000302", instance.getFqdn());
            }

            try {
                Thread.sleep(1000 * 15);
            } catch (InterruptedException ignore) {
            }
        }
    }

    protected void clearCa(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        puppetClient.clearCa(instance.getFqdn());
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
