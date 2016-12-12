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
import jp.primecloud.auto.zabbix.model.application.Application;
import jp.primecloud.auto.zabbix.model.application.ApplicationGetParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * {@link ApplicationClient}のテストクラスです。
 * </p>
 *
 */
public class ApplicationClientTest {

    private Log log = LogFactory.getLog(ApplicationClientTest.class);

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
        ApplicationGetParam param = new ApplicationGetParam();
        param.setOutput("extend");

        List<Application> applications = client.application().get(param);
        for (Application application : applications) {
            log.trace(ReflectionToStringBuilder.toString(application, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(applications.size() > 0);
    }

    @Test
    public void testGet() {
        // applicationidを指定して取得
        ApplicationGetParam param = new ApplicationGetParam();
        param.setApplicationids(Arrays.asList("1"));
        param.setOutput("extend");

        List<Application> applications = client.application().get(param);
        assertEquals(1, applications.size());
        assertEquals("1", applications.get(0).getApplicationid());
    }

    @Test
    public void testGetNotExist() {
        // 存在しないapplicationidを指定した場合
        ApplicationGetParam param = new ApplicationGetParam();
        param.setApplicationids(Arrays.asList("999999"));
        param.setOutput("extend");

        List<Application> applications = client.application().get(param);
        assertEquals(0, applications.size());
    }

    @Test
    public void testGetByTemplateid() {
        String templateid = "10001";

        ApplicationGetParam param = new ApplicationGetParam();
        // templateidをhostidに指定する
        param.setHostids(Arrays.asList(templateid));
        param.setOutput("extend");

        List<Application> applications = client.application().get(param);
        for (Application application : applications) {
            log.trace(ReflectionToStringBuilder.toString(application, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(applications.size() > 0);
    }

}
