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
package jp.primecloud.auto.zabbix.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.trigger.Trigger;
import jp.primecloud.auto.zabbix.model.trigger.TriggerGetParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * {@link TriggerClient}のテストクラスです。
 * </p>
 *
 */
public class TriggerClientTest {

    private Log log = LogFactory.getLog(TriggerClientTest.class);

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
    public void testGetAll() {
        // 全件取得
        TriggerGetParam param = new TriggerGetParam();
        param.setHostids(Arrays.asList("10001"));
        param.setOutput("extend");

        List<Trigger> triggers = client.trigger().get(param);
        for (Trigger trigger : triggers) {
            log.trace(ReflectionToStringBuilder.toString(trigger, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(triggers.size() > 0);
    }

    @Test
    public void testGet() {
        // itemidを指定して取得
        TriggerGetParam param = new TriggerGetParam();
        param.setTriggerids(Arrays.asList("10010", "10011"));
        param.setOutput("extend");

        List<Trigger> triggers = client.trigger().get(param);

        assertEquals(2, triggers.size());
        assertTrue("10010".equals(triggers.get(0).getTriggerid()) || "10011".equals(triggers.get(0).getTriggerid()));
        assertTrue("10010".equals(triggers.get(1).getTriggerid()) || "10011".equals(triggers.get(1).getTriggerid()));
    }

}
