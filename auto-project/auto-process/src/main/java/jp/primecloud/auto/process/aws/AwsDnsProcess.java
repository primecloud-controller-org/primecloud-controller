/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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
package jp.primecloud.auto.process.aws;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jp.primecloud.auto.common.component.DnsStrategy;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class AwsDnsProcess extends ServiceSupport {

    protected DnsStrategy dnsStrategy;

    protected boolean reverseEnabled = true;

    protected ProcessLogger processLogger;

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void startDns(AwsProcessClient awsProcessClient, Long instanceNo) {
        if (BooleanUtils.isTrue(awsProcessClient.getPlatform().getInternal())) {
            // 内部のプラットフォームの場合
            if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getEuca())) {
                // Eucalyptusの場合
                startDnsEuca(awsProcessClient, instanceNo);
            } else {
                // Amazon EC2の場合
                if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getVpc())) {
                    // VPC環境の場合
                    startDnsNormal(awsProcessClient, instanceNo);
                } else {
                    // 非VPC環境の場合
                    startDnsClassic(awsProcessClient, instanceNo);
                }
            }
        } else {
            // 外部のプラットフォームの場合
            Instance instance = instanceDao.read(instanceNo);
            Image image = imageDao.read(instance.getImageNo());
            if (StringUtils.startsWithIgnoreCase(image.getOs(), "windows")) {
                // Windowsイメージの場合、VPNを使用していないものとする
                startDnsClassic(awsProcessClient, instanceNo);
            } else {
                // VPNを使用している場合
                startDnsVpn(awsProcessClient, instanceNo);
            }
        }

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "DnsRegist",
                new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void stopDns(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        if (BooleanUtils.isTrue(awsProcessClient.getPlatform().getInternal())) {
            // 内部のプラットフォームの場合
            if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getEuca())) {
                // Eucalyptusの場合
                stopDnsNormal(awsProcessClient, instanceNo);
            } else {
                // Amazon EC2の場合
                if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getVpc())) {
                    // VPC環境の場合
                    stopDnsNormal(awsProcessClient, instanceNo);
                } else {
                    // 非VPC環境の場合
                    stopDnsClassic(awsProcessClient, instanceNo);
                }
            }
        } else {
            // 外部のプラットフォームの場合
            Image image = imageDao.read(instance.getImageNo());
            if (StringUtils.startsWithIgnoreCase(image.getOs(), "windows")) {
                // Windowsイメージの場合、VPNを使用していないものとする
                stopDnsClassic(awsProcessClient, instanceNo);
            } else {
                // VPNを使用している場合
                stopDnsNormal(awsProcessClient, instanceNo);
            }
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "DnsUnregist",
                new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    protected void startDnsNormal(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // InstanceにIPアドレスが登録済みの場合はスキップする
        if (!StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        // PrivateIpAddressをIPアドレスとする
        String fqdn = instance.getFqdn();
        String publicIp = awsInstance.getPrivateIpAddress();
        String privateIp = awsInstance.getPrivateIpAddress();

        // 正引きの追加
        addForward(fqdn, publicIp);

        // 逆引きの追加
        addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void startDnsClassic(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // 最新のAwsInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), awsInstance.getDnsName())) {
            return;
        }

        String fqdn = instance.getFqdn();
        String publicIp = awsInstance.getIpAddress();
        String privateIp = awsInstance.getPrivateIpAddress();

        // CNAMEの追加
        addCanonicalName(fqdn, awsInstance.getDnsName());

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void startDnsEuca(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // 最新のAwsInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), awsInstance.getDnsName())) {
            return;
        }

        // DnsName, PrivateDnsNameをそのままIPアドレスとして使用する
        String fqdn = instance.getFqdn();
        String publicIp = awsInstance.getDnsName();
        String privateIp = awsInstance.getPrivateDnsName();

        // 正引きの追加
        addForward(fqdn, publicIp);

        // 逆引きの追加
        addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void startDnsVpn(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // InstanceにIPアドレスが登録済みの場合はスキップする
        if (!StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        // IPアドレスを正引きにより取得する（正引きの追加はインスタンス内で行う）
        String fqdn = instance.getFqdn();
        String publicIp = resolveHost(fqdn); // VPNインタフェースのIPアドレス
        String privateIp = awsInstance.getPrivateIpAddress();

        // 逆引きの追加
        addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void stopDnsNormal(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // IPアドレスがない場合はスキップ
        if (StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        String fqdn = instance.getFqdn();
        String publicIp = instance.getPublicIp();

        // 正引きの削除
        deleteForward(fqdn);

        // 逆引きの削除
        deleteReverse(publicIp);

        // データベースの更新
        instance.setPublicIp(null);
        instance.setPrivateIp(null);
        instanceDao.update(instance);
    }

    protected void stopDnsClassic(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // IPアドレスがない場合はスキップ
        if (StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        String fqdn = instance.getFqdn();

        // CNAMEの削除
        deleteCanonicalName(fqdn);

        // データベースの更新
        instance.setPublicIp(null);
        instance.setPrivateIp(null);
        instanceDao.update(instance);
    }

    protected void addForward(String fqdn, String publicIp) {
        // 正引きの追加
        dnsStrategy.addForward(fqdn, publicIp);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100141", fqdn, publicIp));
        }
    }

    protected void addReverse(String fqdn, String publicIp) {
        if (!reverseEnabled) {
            // 逆引きが無効の場合はスキップ
            return;
        }

        // 逆引きの追加
        dnsStrategy.addReverse(fqdn, publicIp);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100142", publicIp, fqdn));
        }
    }

    protected void addCanonicalName(String fqdn, String canonicalName) {
        // CNAMEの追加
        dnsStrategy.addCanonicalName(fqdn, canonicalName);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100145", fqdn, canonicalName));
        }
    }

    protected void deleteForward(String fqdn) {
        // 正引きの削除
        dnsStrategy.deleteForward(fqdn);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100143", fqdn));
        }
    }

    protected void deleteReverse(String publicIp) {
        if (!reverseEnabled) {
            // 逆引きが無効の場合はスキップ
            return;
        }

        // 逆引きの削除
        dnsStrategy.deleteReverse(publicIp);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100144", publicIp));
        }
    }

    protected void deleteCanonicalName(String fqdn) {
        // CNAMEの削除
        dnsStrategy.deleteCanonicalName(fqdn);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100146", fqdn));
        }
    }

    protected String resolveHost(String fqdn) {
        long timeout = 1000L * 60 * 5;
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                InetAddress address = InetAddress.getByName(fqdn);
                return address.getHostAddress();
            } catch (UnknownHostException ignore) {
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時
                throw new AutoException("EPROCESS-000205", fqdn);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignore) {
            }
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
     * reverseEnabledを設定します。
     *
     * @param reverseEnabled reverseEnabled
     */
    public void setReverseEnabled(boolean reverseEnabled) {
        this.reverseEnabled = reverseEnabled;
    }

    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }

}
