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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.template.Template;
import jp.primecloud.auto.zabbix.model.template.TemplateGetParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * {@link TemplateClient}のテストクラスです。
 * </p>
 *
 */
public class TemplateClientTest {

    private Log log = LogFactory.getLog(TemplateClientTest.class);

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
        TemplateGetParam param = new TemplateGetParam();
        param.setOutput("extend");

        List<Template> templates = client.template().get(param);
        for (Template template : templates) {
            log.trace(ReflectionToStringBuilder.toString(template, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(templates.size() > 0);
    }

    @Test
    public void testGet() {
        // templateidを指定して取得
        TemplateGetParam param = new TemplateGetParam();
        param.setTemplateids(Arrays.asList("10001"));
        param.setOutput("extend");

        List<Template> templates = client.template().get(param);
        assertEquals(1, templates.size());
        assertEquals("10001", templates.get(0).getTemplateid());
    }

    @Test
    public void testGetNotExist() {
        // 存在しないtemplateidを指定した場合
        TemplateGetParam param = new TemplateGetParam();
        param.setTemplateids(Arrays.asList("999999"));
        param.setOutput("extend");

        List<Template> templates = client.template().get(param);
        assertEquals(0, templates.size());
    }

    @Test
    public void testGetByFilter() {
        // 存在するtemplate名を指定した場合
        String templateName;
        if (client.checkVersion("2.0") < 0) {
            templateName = "Template_Linux";
        } else {
            templateName = "Template OS Linux";
        }

        TemplateGetParam param = new TemplateGetParam();
        param.setOutput("extend");

        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("host", Arrays.asList((Object) templateName));
        param.setFilter(filter);

        List<Template> templates = client.template().get(param);
        assertEquals(1, templates.size());
        assertEquals(templateName, templates.get(0).getHost());
    }

    @Test
    public void testGetByFilter2() {
        // 存在しないtemplate名を指定した場合
        TemplateGetParam param = new TemplateGetParam();
        param.setOutput("extend");

        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("host", Arrays.asList((Object) "dummy"));
        param.setFilter(filter);

        List<Template> templates = client.template().get(param);
        assertEquals(0, templates.size());
    }

}
