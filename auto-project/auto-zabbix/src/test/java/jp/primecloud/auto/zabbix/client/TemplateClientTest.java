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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.client.TemplateClient;
import jp.primecloud.auto.zabbix.model.template.Template;
import jp.primecloud.auto.zabbix.model.template.TemplateGetParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
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
    @Ignore
    public void testGet() {
        // 全件取得
        TemplateGetParam param = new TemplateGetParam();
        param.setOutput("extend");
        List<Template> templates = client.template().get(param);

        for (Template template : templates) {
            log.trace(ReflectionToStringBuilder.toString(template, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        if (templates.size() > 0) {
            // templateidを指定して取得
            List<String> tempids = new ArrayList<String>();
            tempids.add(templates.get(0).getTemplateid());
            param.setTemplateids(tempids);
            List<Template> templates2 = client.template().get(param);

            assertEquals(1, templates2.size());
            assertEquals(templates.get(0).getTemplateid(), templates2.get(0).getTemplateid());
        }
    }

    @Test
    @Ignore
    public void testGet5() {
        // 全件取得
        TemplateGetParam param = new TemplateGetParam();
        List<String> templateids = new ArrayList<String>();
        //templateids.add("11022");
        templateids.add("10057");
        param.setTemplateids(templateids);
        param.setOutput("extend");
        List<Template> templates = client.template().get(param);

        for (Template template : templates) {
            log.trace(ReflectionToStringBuilder.toString(template, ToStringStyle.SHORT_PREFIX_STYLE));
        }

    }

    @Test
    @Ignore
    public void testGet2() {
        // 存在しないtemplateidを指定した場合
        TemplateGetParam param = new TemplateGetParam();
        List<String> templateids = new ArrayList<String>();
        templateids.add("999999");
        param.setTemplateids(templateids);
        List<Template> temps = client.template().get(param);
        assertEquals(0, temps.size());
    }

    @Test
    @Ignore
    public void testGet3() {
        // 存在するtemplate名を指定した場合
        TemplateGetParam param = new TemplateGetParam();
        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("host", Arrays.asList((Object)"Template_OS_Linux"));
        param.setFilter(filter);
        param.setOutput("extend");

        List<Template> templates = client.template().get(param);
        assertEquals(1, templates.size());
        assertEquals("10057", templates.get(0).getTemplateid());
        assertEquals("Template_OS_Linux", templates.get(0).getHost());
    }

    @Test
    @Ignore
    public void testGet4() {
        // 存在しないtemplate名を指定した場合
        TemplateGetParam param = new TemplateGetParam();
        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("host", Arrays.asList((Object)"dummy"));
        param.setFilter(filter);
        param.setOutput("extend");

        List<Template> templates = client.template().get(param);
        assertEquals(0, templates.size());
    }

}
