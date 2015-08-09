/*
 * Copyright 2015 by SCSK Corporation.
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
package jp.primecloud.auto.zabbix.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.proxy.Proxy;
import jp.primecloud.auto.zabbix.model.proxy.ProxyGetParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>
 * {@link ProxyClient}のテストクラスです。
 * </p>
 *
 */
public class ProxyClientTest {

    private Log log = LogFactory.getLog(ProxyClientTest.class);

    private ZabbixClient client;

    @Before
    public void setUp() throws Exception {
        if (client == null) {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/zabbix.properties"));

            String url = properties.getProperty("zabbix.url");
            String username = properties.getProperty("zabbix.username");
            String password = properties.getProperty("zabbix.password");

            ZabbixClientFactory factory = new ZabbixClientFactory();
            factory.setUrl(url);
            client = factory.createClient(username, password);
        }
    }

    @Test
    @Ignore
    public void testGetAll() {
        // 全件取得
        ProxyGetParam param = new ProxyGetParam();
        param.setOutput("extend");

        List<Proxy> proxies = client.proxy().get(param);
        for (Proxy proxy : proxies) {
            log.trace(ReflectionToStringBuilder.toString(proxy, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(proxies.size() > 0);
    }

    @Test
    @Ignore
    public void testGetByFilter() {
        // 存在するproxy名を指定した場合
        ProxyGetParam param = new ProxyGetParam();
        param.setOutput("extend");

        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("host", Arrays.asList((Object) "proxy1"));
        param.setFilter(filter);

        List<Proxy> proxies = client.proxy().get(param);
        assertEquals(1, proxies.size());
        assertEquals("proxy1", proxies.get(0).getHost());
    }

}
