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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.host.HostCreateParam;
import jp.primecloud.auto.zabbix.model.host.HostUpdateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.item.Item;
import jp.primecloud.auto.zabbix.model.item.ItemGetParam;
import jp.primecloud.auto.zabbix.model.item.ItemUpdateParam;
import jp.primecloud.auto.zabbix.model.template.Template;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

public class ItemClientTest {

    private Log log = LogFactory.getLog(ItemClientTest.class);

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
        ItemGetParam param = new ItemGetParam();
        param.setHostids(Arrays.asList("10001"));
        param.setOutput("extend");

        List<Item> items = client.item().get(param);
        for (Item item : items) {
            log.trace(ReflectionToStringBuilder.toString(item, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(items.size() > 0);
    }

    @Test
    public void testGet() {
        // itemidを指定して取得
        ItemGetParam param = new ItemGetParam();
        param.setItemids(Arrays.asList("10009"));
        param.setOutput("extend");

        List<Item> items = client.item().get(param);

        assertTrue(items.size() > 0);
        assertEquals(1, items.size());
        assertEquals("10009", items.get(0).getItemid());
    }

    @Test
    public void testGetByApplication() {
        ItemGetParam param = new ItemGetParam();
        param.setHostids(Arrays.asList("10001"));
        param.setApplication("OS");
        param.setOutput("extend");

        List<Item> items = client.item().get(param);
        assertTrue(items.size() > 0);
        for (Item item : items) {
            assertEquals("OS", item.getApplication());
        }
    }

    @Test
    public void testUpdateDelete() {
        // 事前準備（ホストにテンプレートをリンクして作成）
        String hostid;
        {
            HostCreateParam param = new HostCreateParam();
            param.setHost("host1");
            param.setDns("dns1");

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("2");
            param.setGroups(Arrays.asList(hostgroup));

            Template template = new Template();
            template.setTemplateid("10001");
            param.setTemplates(Arrays.asList(template));

            List<String> hostids = client.host().create(param);
            hostid = hostids.get(0);
        }

        String itemid;
        {
            ItemGetParam param = new ItemGetParam();
            param.setHostids(Arrays.asList(hostid));
            param.setOutput("extend");

            List<Item> items = client.item().get(param);
            itemid = items.get(0).getItemid();
        }

        // Update
        {
            ItemUpdateParam param = new ItemUpdateParam();
            param.setItemid(itemid);
            param.setStatus(ItemUpdateParam.DISABLE);

            List<String> itemids = client.item().update(param);
            assertEquals(1, itemids.size());
            assertEquals(itemid, itemids.get(0));
        }

        // Get
        {
            ItemGetParam param = new ItemGetParam();
            param.setItemids(Arrays.asList(itemid));
            param.setOutput("extend");

            List<Item> items = client.item().get(param);
            assertEquals(1, items.size());

            Item item = items.get(0);
            assertEquals(ItemUpdateParam.DISABLE, item.getStatus());
        }

        // Update
        {
            ItemUpdateParam param = new ItemUpdateParam();
            param.setItemid(itemid);
            param.setStatus(ItemUpdateParam.ENABLE);

            List<String> itemids = client.item().update(param);
            assertEquals(1, itemids.size());
            assertEquals(itemid, itemids.get(0));
        }

        // Get
        {
            ItemGetParam param = new ItemGetParam();
            param.setItemids(Arrays.asList(itemid));
            param.setOutput("extend");

            List<Item> items = client.item().get(param);
            assertEquals(1, items.size());

            Item item = items.get(0);
            assertEquals(ItemUpdateParam.ENABLE, item.getStatus());
        }

        // 事前準備（ホストからテンプレートのリンクを削除）
        {
            HostUpdateParam param = new HostUpdateParam();
            param.setHostid(hostid);
            param.setDns("dns1");

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("2");
            param.setGroups(Arrays.asList(hostgroup));

            param.setTemplates(new ArrayList<Template>());

            client.host().update(param);
        }

        // Delete
        {
            List<String> itemids = client.item().delete(Arrays.asList(itemid));
            assertEquals(1, itemids.size());
            assertEquals(itemid, itemids.get(0));
        }

        // Get
        {
            ItemGetParam param = new ItemGetParam();
            param.setItemids(Arrays.asList(itemid));
            param.setOutput("extend");

            List<Item> items = client.item().get(param);
            assertEquals(0, items.size());
        }

        // 事後始末（ホストを削除）
        {
            client.host().delete(Arrays.asList(hostid));
        }
    }

    @Test
    public void testDeleteIllegalArgument() {
        // itemidを指定していない場合
        try {
            client.item().delete(null);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteNotExist() {
        // 存在しないitemidを指定した場合
        try {
            client.item().delete(Arrays.asList("99999999"));
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

}
