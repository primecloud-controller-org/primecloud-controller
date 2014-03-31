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

import java.net.InetAddress;
import java.net.UnknownHostException;

import jp.primecloud.auto.common.component.DnsStrategy;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.process.ProcessLogger;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyDnsProcess extends ServiceSupport {

    protected DnsStrategy dnsStrategy;

    protected boolean reverseEnabled = true;

    protected ProcessLogger processLogger;

    protected EventLogger eventLogger;

    public void startDns(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (platform.getInternal()) {
            // 内部のプラットフォームの場合
            startDnsNormal(instanceNo);
        } else {
            // 外部のプラットフォームの場合
            startDnsVpn(instanceNo);
        }

        // イベントログ出力
        instance = instanceDao.read(instanceNo);
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "DnsRegist",
                new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    public void stopDns(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (platform.getInternal()) {
            // 内部のプラットフォームの場合
            stopDnsNormal(instanceNo);
        } else {
            // 外部のプラットフォームの場合
            stopDnsVpn(instanceNo);
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "DnsUnregist",
                new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    protected void startDnsNormal(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        // 最新のNiftyInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), niftyInstance.getIpAddress())) {
            return;
        }

        String fqdn = instance.getFqdn();
        String publicIp = niftyInstance.getIpAddress();
        String privateIp = niftyInstance.getPrivateIpAddress();

        // 正引きの追加
        addForward(fqdn, publicIp);

        // 逆引きの追加
        addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void startDnsVpn(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // InstanceにIPアドレスが登録済みの場合はスキップする
        if (!StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        // IPアドレスを正引きにより取得する（正引きの追加はインスタンス内で行う）
        String fqdn = instance.getFqdn();
        String publicIp = resolveHost(fqdn); // VPNインタフェースのIPアドレス
        String privateIp = niftyInstance.getPrivateIpAddress();

        // 逆引きの追加
        addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void stopDnsNormal(Long instanceNo) {
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

    protected void stopDnsVpn(Long instanceNo) {
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
