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
package jp.primecloud.auto.process.nifty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.puppet.PuppetClient;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.JSchUtils;
import jp.primecloud.auto.util.JSchUtils.JSchResult;
import com.jcraft.jsch.Session;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyInstanceProcess extends ServiceSupport {

    protected PuppetClient puppetClient;

    protected File imageDir;

    protected Long initTimeout;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    private final static Object lock = new Object();

    public void createInstance(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        // インスタンスIDがある場合はスキップ
        if (StringUtils.isNotEmpty(niftyInstance.getInstanceId())) {
            return;
        }

        // インスタンスの作成と起動
        run(niftyProcessClient, instanceNo);

        // 初期化スクリプトを実行
        init(niftyProcessClient, instanceNo);

        // インスタンスの停止
        stop(niftyProcessClient, instanceNo);
    }

    public void startInstance(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        // インスタンスIDがない場合はエラー
        if (StringUtils.isEmpty(niftyInstance.getInstanceId())) {
            // TODO: エラーコード
            throw new RuntimeException();
        }

        // Puppet認証情報の削除
        Instance instance = instanceDao.read(instanceNo);
        puppetClient.clearCa(instance.getFqdn());

        // インスタンスの起動
        start(niftyProcessClient, instanceNo);
    }

    public void stopInstance(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        // インスタンスIDがない場合はスキップ
        if (StringUtils.isEmpty(niftyInstance.getInstanceId())) {
            return;
        }

        // インスタンスの停止
        stop(niftyProcessClient, instanceNo);

        // 初期化フラグが立っていない場合は削除する
        niftyInstance = niftyInstanceDao.read(instanceNo);
        if (BooleanUtils.isNotTrue(niftyInstance.getInitialized())) {
            terminate(niftyProcessClient, instanceNo);
        }
    }

    public void deleteInstance(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        // インスタンスIDがない場合はスキップ
        if (StringUtils.isEmpty(niftyInstance.getInstanceId())) {
            return;
        }

        // インスタンスの削除
        terminate(niftyProcessClient, instanceNo);
    }

    protected void run(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        // イメージを取得
        Instance instance = instanceDao.read(instanceNo);
        ImageNifty imageNifty = imageNiftyDao.read(instance.getImageNo());

        // キーペアを取得
        NiftyKeyPair niftyKeyPair = niftyKeyPairDao.read(niftyInstance.getKeyPairNo());

        // 排他制御(apiを同時に実行するとエラーになる対策)
        synchronized(lock) {
            // イベントログ出力
            Platform platform = platformDao.read(niftyProcessClient.getPlatformNo());
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,  "NiftyInstanceCreate",
                    new Object[] { platform.getPlatformName() });

            // インスタンスの起動
            // TODO: rootパスワードを決める
            com.nifty.cloud.sdk.server.model.Instance instance2 = niftyProcessClient.runInstance(imageNifty.getImageId(), niftyKeyPair
                    .getKeyName(), "mini", "password");

            String instanceId = instance2.getInstanceId();

            // データベース更新
            niftyInstance = niftyInstanceDao.read(instanceNo);
            niftyInstance.setInstanceId(instanceId);
            niftyInstanceDao.update(niftyInstance);

            // インスタンスの起動待ち
            instance2 = niftyProcessClient.waitRunInstance(instanceId);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceCreateFinish",
                    new Object[] { platform.getPlatformName(), instance2.getInstanceId() });

            // データベース更新
            niftyInstance = niftyInstanceDao.read(instanceNo);
            niftyInstance.setStatus(instance2.getState().getName());
            niftyInstance.setDnsName(instance2.getDnsName());
            niftyInstance.setPrivateDnsName(instance2.getPrivateDnsName());
            niftyInstance.setIpAddress(instance2.getIpAddress());
            niftyInstance.setPrivateIpAddress(instance2.getPrivateIpAddress());
            niftyInstanceDao.update(niftyInstance);
        }
    }

    protected void start(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);
        String instanceId = niftyInstance.getInstanceId();

        // 排他制御(apiを同時に実行するとエラーになる対策)
        synchronized(lock) {
            // イベントログ出力
            Instance instance = instanceDao.read(instanceNo);
            Platform platform = platformDao.read(niftyProcessClient.getPlatformNo());
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceStart", new Object[] {
                    platform.getPlatformName(), instanceId });

            // インスタンスの開始
            niftyProcessClient.startInstance(instanceId, niftyInstance.getInstanceType());

            // インスタンスの開始待ち
            com.nifty.cloud.sdk.server.model.Instance instance2 = niftyProcessClient.waitStartInstance(instanceId);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceStartFinish", new Object[] {
                    platform.getPlatformName(), instanceId });

            // データベース更新
            niftyInstance = niftyInstanceDao.read(instanceNo);
            niftyInstance.setStatus(instance2.getState().getName());
            niftyInstance.setDnsName(instance2.getDnsName());
            niftyInstance.setPrivateDnsName(instance2.getPrivateDnsName());
            niftyInstance.setIpAddress(instance2.getIpAddress());
            niftyInstance.setPrivateIpAddress(instance2.getPrivateIpAddress());
            niftyInstanceDao.update(niftyInstance);
        }
    }

    protected void stop(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);
        String instanceId = niftyInstance.getInstanceId();

        // 排他制御(apiを同時に実行するとエラーになる対策)
        synchronized(lock) {
            // イベントログ出力
            Instance instance = instanceDao.read(instanceNo);
            Platform platform = platformDao.read(niftyProcessClient.getPlatformNo());
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceStop", new Object[] {
                    platform.getPlatformName(), instanceId });

            // インスタンスの停止
            niftyProcessClient.stopInstance(instanceId);

            // インスタンスの停止待ち
            com.nifty.cloud.sdk.server.model.Instance instance2 = niftyProcessClient.waitStopInstance(instanceId);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceStopFinish", new Object[] {
                    platform.getPlatformName(), instanceId });

            // データベース更新
            niftyInstance = niftyInstanceDao.read(instanceNo);
            niftyInstance.setStatus(instance2.getState().getName());
            niftyInstance.setDnsName(instance2.getDnsName());
            niftyInstance.setPrivateDnsName(instance2.getPrivateDnsName());
            niftyInstance.setIpAddress(instance2.getIpAddress());
            niftyInstance.setPrivateIpAddress(instance2.getPrivateIpAddress());
            niftyInstanceDao.update(niftyInstance);
        }
    }

    protected void terminate(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);
        String instanceId = niftyInstance.getInstanceId();

        try {
            // イベントログ出力
            Instance instance = instanceDao.read(instanceNo);
            Platform platform = platformDao.read(niftyProcessClient.getPlatformNo());
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceDelete", new Object[] {
                    platform.getPlatformName(), instanceId });

            // インスタンスの削除
            niftyProcessClient.terminateInstance(instanceId);

            // インスタンスの削除待ち
            niftyProcessClient.waitTerminateInstance(instanceId);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceDeleteFinish",
                    new Object[] { platform.getPlatformName(), instanceId });

        } catch (AutoException ignore) {
            // インスタンス削除異常終了時は、警告ログを出力して例外を握りつぶす
            log.warn(ignore.getMessage());
        }

        // データベース更新
        niftyInstance = niftyInstanceDao.read(instanceNo);
        niftyInstance.setInstanceId(null);
        niftyInstance.setStatus(null);
        niftyInstance.setDnsName(null);
        niftyInstance.setPrivateDnsName(null);
        niftyInstance.setIpAddress(null);
        niftyInstance.setPrivateIpAddress(null);
        niftyInstance.setInitialized(null);
        niftyInstanceDao.update(niftyInstance);
    }

    protected void init(NiftyProcessClient niftyProcessClient, Long instanceNo) {
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);
        NiftyKeyPair niftyKeyPair = niftyKeyPairDao.read(niftyInstance.getKeyPairNo());

        // イベントログ出力
        String instanceId = niftyInstance.getInstanceId();
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(niftyProcessClient.getPlatformNo());
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceInitialize", new Object[] {
                platform.getPlatformName(), instanceId });

        // SSHセッションを作成
        Session session = JSchUtils.createSessionByPrivateKey("root", niftyKeyPair.getPrivateKey(), niftyKeyPair
                .getPassphrase(), niftyInstance.getIpAddress());

        try {
            // 初期化スクリプトを転送
            Image image = imageDao.read(instance.getImageNo());
            File imageFile = new File(imageDir, image.getImageName() + ".tar");
            FileInputStream input = null;
            try {
                input = new FileInputStream(imageFile);
                JSchUtils.sftpPut(session, input, "/tmp/image.tar");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(input);
            }

            // ユーザデータを作成
            String userData = createUserData(instanceNo);

            // 初期化スクリプトを実行
            String command = "mkdir -p /tmp/image/ && tar xvf /tmp/image.tar -C /tmp/image/ && /tmp/image/init.sh \""
                    + userData + "\"";
            long timeout = 60 * 30 * 1000L;
            if (initTimeout != null) {
                timeout = initTimeout;
            }
            JSchResult result = JSchUtils.executeCommand(session, command, "UTF-8", timeout, true);

            if (result.getExitStatus() != 0) {
                // 初期化スクリプトの実行に失敗
                AutoException exception = new AutoException("EPROCESS-000616", instanceNo);
                exception.addDetailInfo("result="
                        + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
                throw exception;
            }

        } finally {
            if (session != null) {
                session.disconnect();
            }
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "NiftyInstanceInitializeFinish",
                new Object[] { platform.getPlatformName(), instanceId });

        // データベース更新
        niftyInstance = niftyInstanceDao.read(instanceNo);
        niftyInstance.setInitialized(true);
        niftyInstanceDao.update(niftyInstance);
    }

    protected String createUserData(Long instanceNo) {
        Map<String, String> map = createUserDataMap(instanceNo);

        if (map.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                sb.append(key).append("=").append(value).append(";");
            }
        }
        sb.delete(sb.length() - 1, sb.length());

        return sb.toString();
    }

    protected Map<String, String> createUserDataMap(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        Map<String, String> map = new HashMap<String, String>();

        // DB情報
        map.put("instanceName", instance.getInstanceName());
        map.put("farmName", farm.getFarmName());

        // FQDN
        String fqdn = instance.getFqdn();
        map.put("hostname", fqdn);

        // 初期スクリプト情報
        map.put("scriptserver", Config.getProperty("script.server"));

        // DNS情報
        map.putAll(createDnsUserDataMap(instanceNo));

        // Puppet情報
        map.putAll(createPuppetUserDataMap(instanceNo));

        // VPN情報
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (platform.getInternal() == false) {
            // 外部のプラットフォームの場合、VPN情報を含める
            map.putAll(createVpnUserDataMap(instanceNo));
        }
        // OpenVPNクライアント証明書ダウンロード先URL
        map.put("vpnclienturl", Config.getProperty("vpn.clienturl"));

        return map;
    }

    protected Map<String, String> createDnsUserDataMap(Long instanceNo) {
        Map<String, String> map = new HashMap<String, String>();

        // Primary DNSサーバ
        map.put("dns", Config.getProperty("dns.server"));

        // Secondry DNSサーバ
        String dns2 = Config.getProperty("dns.server2");
        if (dns2 != null && dns2.length() > 0) {
            map.put("dns2", dns2);
        }

        // DNSドメイン
        map.put("dnsdomain", Config.getProperty("dns.domain"));

        return map;
    }

    protected Map<String, String> createPuppetUserDataMap(Long instanceNo) {
        Map<String, String> map = new HashMap<String, String>();

        // PuppetMaster情報
        map.put("puppetmaster", Config.getProperty("puppet.masterHost"));

        return map;
    }

    protected Map<String, String> createVpnUserDataMap(Long instanceNo) {
        Map<String, String> map = new HashMap<String, String>();

        // VPN情報のユーザとパスワードをセットする
        Instance instance = instanceDao.read(instanceNo);
        map.put("vpnuser", instance.getFqdn());
        map.put("vpnuserpass", instance.getInstanceCode());

        // VPNサーバ情報
        map.put("vpnserver", Config.getProperty("vpn.server"));
        map.put("vpnport", Config.getProperty("vpn.port"));
        //map.put("vpnuser", Config.getProperty("vpn.user"));
        //map.put("vpnuserpass", Config.getProperty("vpn.userpass"));

        // ZIPパスワード
        map.put("vpnzippass", Config.getProperty("vpn.zippass"));

        return map;
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
     * imageDirを設定します。
     *
     * @param imageDir imageDir
     */
    public void setImageDir(File imageDir) {
        this.imageDir = imageDir;
    }

    /**
     * initTimeoutを設定します。
     *
     * @param initTimeout initTimeout
     */
    public void setInitTimeout(Long initTimeout) {
        this.initTimeout = initTimeout;
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
