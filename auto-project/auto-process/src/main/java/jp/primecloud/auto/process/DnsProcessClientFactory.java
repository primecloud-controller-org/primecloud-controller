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
package jp.primecloud.auto.process;

import jp.primecloud.auto.common.component.DnsStrategy;
import jp.primecloud.auto.config.Config;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class DnsProcessClientFactory {

    public DnsProcessClient createDnsProcessClient() {
        DnsProcessClient client = new DnsProcessClient();

        // dnsStrategy
        DnsStrategy dnsStrategy = createDnsStrategy();
        client.setDnsStrategy(dnsStrategy);

        // reverseEnabled
        String reverseEnabled = Config.getProperty("dns.reverseEnabled");
        client.setReverseEnabled(BooleanUtils.toBoolean(StringUtils.defaultIfEmpty(reverseEnabled, "true")));

        return client;
    }

    protected DnsStrategy createDnsStrategy() {
        DnsStrategy dnsStrategy = new DnsStrategy();

        // dnsServer
        String dnsServer = Config.getProperty("dns.server");
        dnsStrategy.setDnsServer(dnsServer);

        // timeToLive
        String timeToLive = Config.getProperty("dns.timeToLive");
        dnsStrategy.setTimeToLive(NumberUtils.toInt(timeToLive, 3600));

        return dnsStrategy;
    }

}
