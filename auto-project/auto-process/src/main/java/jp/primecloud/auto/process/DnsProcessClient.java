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

import java.net.InetAddress;
import java.net.UnknownHostException;

import jp.primecloud.auto.common.component.DnsStrategyInterface;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class DnsProcessClient {

    protected Log log = LogFactory.getLog(getClass());

    protected DnsStrategyInterface dnsStrategy;

    protected boolean reverseEnabled = true;

    public void addForward(String fqdn, String publicIp) {
        // 正引きの追加
        dnsStrategy.addForward(fqdn, publicIp);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100141", fqdn, publicIp));
        }
    }

    public void addReverse(String fqdn, String publicIp) {
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

    public void addCanonicalName(String fqdn, String canonicalName) {
        // CNAMEの追加
        dnsStrategy.addCanonicalName(fqdn, canonicalName);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100145", fqdn, canonicalName));
        }
    }

    public void deleteForward(String fqdn) {
        // 正引きの削除
        dnsStrategy.deleteForward(fqdn);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100143", fqdn));
        }
    }

    public void deleteReverse(String publicIp) {
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

    public void deleteCanonicalName(String fqdn) {
        // CNAMEの削除
        dnsStrategy.deleteCanonicalName(fqdn);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100146", fqdn));
        }
    }

    public String resolveHost(String fqdn) {
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
    public void setDnsStrategy(DnsStrategyInterface dnsStrategy) {
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

}
