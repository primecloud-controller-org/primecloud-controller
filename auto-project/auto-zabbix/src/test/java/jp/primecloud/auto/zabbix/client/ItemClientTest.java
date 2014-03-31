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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.application.Application;
import jp.primecloud.auto.zabbix.model.application.ApplicationGetParam;
import jp.primecloud.auto.zabbix.model.item.Item;
import jp.primecloud.auto.zabbix.model.item.ItemGetParam;
import jp.primecloud.auto.zabbix.model.item.ItemUpdateParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
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
    @Ignore
    public void testGet() {
        // 全件取得
        ItemGetParam param = new ItemGetParam();
        // List<String> list = new ArrayList<String>();
        //  list.add("18359");
        //  param.setItemids(list);
        param.setOutput("extend");
        List<String> hostids = new ArrayList<String>();
        hostids.add("10001");
        param.setHostids(hostids);
        List<Item> items = client.item().get(param);

        for (Item item : items) {
            log.debug(ReflectionToStringBuilder.toString(item, ToStringStyle.SHORT_PREFIX_STYLE));

        }
        /*        if (items.size() > 0) {
                    // ItemIdを指定して取得
                    List<Integer> itemIds = new ArrayList<Integer>();
                    itemIds.add(items.get(0).getItemId());
                    param.set(tempids);
                    List<Template> templates2 = client.template().get(param);

                    assertEquals(1, templates2.size());
                    assertEquals(templates.get(0).getTemplateid(), templates2.get(0).getTemplateid());
                }*/
    }

    @Test
    @Ignore
    public void testGetFromItemid() {
        ItemGetParam param = new ItemGetParam();
        List<String> itemids = new ArrayList<String>();
        itemids.add("83062");

        List<String> hostids = new ArrayList<String>();
        hostids.add("11367");

        //   param.setItemids(itemids);
        param.setHostids(hostids);
        param.setOutput("extend");
        List<Item> items = client.item().get(param);
        log.debug("itemSize: " + items.size());
        for (Item item : items) {
            log.debug(ReflectionToStringBuilder.toString(item, ToStringStyle.SHORT_PREFIX_STYLE));
        }
    }

    @Test
    @Ignore
    public void testGetItemFromTemplateid() {
        // 全件取得
        ItemGetParam param = new ItemGetParam();

        List<String> hostids = new ArrayList<String>();
        hostids.add("11749");
        param.setHostids(hostids);
        param.setApplication("tomcat");

        param.setOutput("extend");
        List<Item> items = client.item().get(param);
        log.debug("item_size :" + items.size());
        for (Item item : items) {
            log.debug(ReflectionToStringBuilder.toString(item, ToStringStyle.SHORT_PREFIX_STYLE));

        }
    }

    @Test
    @Ignore
    public void testGetFromApplication() {
        // 全件取得
        ItemGetParam param = new ItemGetParam();

        List<String> hostids = new ArrayList<String>();
        hostids.add("10283");
        param.setHostids(hostids);
        param.setApplication("syslog-ng");
        param.setOutput("extend");
        List<Item> items = client.item().get(param);
        log.debug("item_size :" + items.size());
        for (Item item : items) {
            log.debug(ReflectionToStringBuilder.toString(item, ToStringStyle.SHORT_PREFIX_STYLE));

        }
    }

    @Test
    @Ignore
    public void testUpdate() {
        ItemUpdateParam param = new ItemUpdateParam();
        param.setItemid("22285");
        //有効:0 無効にする：1
        param.setStatus(ItemUpdateParam.ENABLE);

        List<String> items = client.item().update(param);
        for (String item : items) {
            log.debug(ReflectionToStringBuilder.toString(item, ToStringStyle.SHORT_PREFIX_STYLE));

        }
    }

    @Test
    @Ignore
    public void testIroiro() {

        //テンプレートIDに紐づくアイテムを取得
        ApplicationGetParam applicationGetParam = new ApplicationGetParam();
        //ZabbixAPIのバグでhostidにtemplateidをセットする必要がある
        applicationGetParam.setHostids(Arrays.asList("10057"));
        applicationGetParam.setOutput("extend");
        List<Application> applications = client.application().get(applicationGetParam);

        List<Item> items = new ArrayList<Item>();
        ItemGetParam itemGetParam = new ItemGetParam();
        for (Application application : applications) {
            itemGetParam.setApplication(application.getName());
            itemGetParam.setHostids(Arrays.asList("11749"));
            itemGetParam.setOutput("extend");
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100325", "11749", application.getName()));
            }
            List<Item> applicationItems = client.item().get(itemGetParam);
            items.addAll(applicationItems);
        }

    }

    @Test
    @Ignore
    public void testDelete() {
        List<String> itemids = client.item().delete(Arrays.asList("99999999"));
        log.debug(itemids);
    }

}
