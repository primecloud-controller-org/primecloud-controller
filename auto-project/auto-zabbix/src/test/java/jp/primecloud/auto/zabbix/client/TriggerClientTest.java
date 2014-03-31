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

import java.util.ArrayList;
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
import org.junit.Ignore;
import org.junit.Test;


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
    @Ignore
    public void testGet() {
        // 全件取得
        TriggerGetParam param = new TriggerGetParam();
     //    List<String> list = new ArrayList<String>();
      //    list.add("19944");
     //     param.setTriggerids(list);
        param.setOutput("extend");
        List<String> hostids = new ArrayList<String>();
        hostids.add("10001");
        param.setHostids(hostids);
        List<Trigger> triggers = client.trigger().get(param);

        for (Trigger trigger : triggers) {
            log.debug(ReflectionToStringBuilder.toString(trigger, ToStringStyle.SHORT_PREFIX_STYLE));

        }
        /*        if (triggers.size() > 0) {
                    // TriggerIdを指定して取得
                    List<Integer> triggerIds = new ArrayList<Integer>();
                    triggerIds.add(triggers.get(0).getTriggerId());
                    param.set(tempids);
                    List<Template> templates2 = client.template().get(param);

                    assertEquals(1, templates2.size());
                    assertEquals(templates.get(0).getTemplateid(), templates2.get(0).getTemplateid());
                }*/
    }
}
