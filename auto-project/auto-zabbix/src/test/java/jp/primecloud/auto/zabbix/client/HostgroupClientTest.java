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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.host.Host;
import jp.primecloud.auto.zabbix.model.host.HostGetParam;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupCreateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupGetParam;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupUpdateParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * {@link HostgroupClient}のテストクラスです。
 * </p>
 *
 */
public class HostgroupClientTest {

    private Log log = LogFactory.getLog(HostgroupClientTest.class);

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
        HostgroupGetParam param = new HostgroupGetParam();
        param.setOutput("extend");

        List<Hostgroup> hostgroups = client.hostgroup().get(param);
        for (Hostgroup hostgroup : hostgroups) {
            log.trace(ReflectionToStringBuilder.toString(hostgroup, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(hostgroups.size() > 0);
    }

    @Test
    public void testGet() {
        // groupidを指定して取得
        HostgroupGetParam param = new HostgroupGetParam();
        param.setGroupids(Arrays.asList("2"));
        param.setOutput("extend");

        List<Hostgroup> hostgroups = client.hostgroup().get(param);
        assertEquals(1, hostgroups.size());
        assertEquals("2", hostgroups.get(0).getGroupid());
    }

    @Test
    public void testGetNotExist() {
        // 存在しないhostidを指定した場合
        HostGetParam param = new HostGetParam();
        param.setHostids(Arrays.asList("999999"));
        param.setOutput("extend");

        List<Host> hosts = client.host().get(param);
        assertEquals(0, hosts.size());
    }

    @Test
    public void testCreateUpdateDelete() {
        // Create
        String groupid;
        {
            HostgroupCreateParam param = new HostgroupCreateParam();
            param.setName("name1");

            List<String> groupids = client.hostgroup().create(param);
            assertEquals(1, groupids.size());

            groupid = groupids.get(0);
        }

        // Get
        {
            HostgroupGetParam param = new HostgroupGetParam();
            param.setGroupids(Arrays.asList(groupid));
            param.setOutput("extend");

            List<Hostgroup> hostgroups = client.hostgroup().get(param);
            assertEquals(1, hostgroups.size());

            Hostgroup hostgroup = hostgroups.get(0);
            assertEquals(groupid, hostgroup.getGroupid());
            assertEquals("name1", hostgroup.getName());
        }

        // Update
        {
            HostgroupUpdateParam param = new HostgroupUpdateParam();
            param.setGroupid(groupid);
            param.setName("name2");

            List<String> groupids = client.hostgroup().update(param);
            assertEquals(1, groupids.size());
            assertEquals(groupid, groupids.get(0));
        }

        // Get
        {
            HostgroupGetParam param = new HostgroupGetParam();
            param.setGroupids(Arrays.asList(groupid));
            param.setOutput("extend");

            List<Hostgroup> hostgroups = client.hostgroup().get(param);
            assertEquals(1, hostgroups.size());

            Hostgroup hostgroup = hostgroups.get(0);
            assertEquals(groupid, hostgroup.getGroupid());
            assertEquals("name2", hostgroup.getName());
        }

        // Delete
        {
            List<String> groupids = client.hostgroup().delete(Arrays.asList(groupid));
            assertEquals(1, groupids.size());
            assertEquals(groupid, groupids.get(0));
        }

        // Get
        {
            HostgroupGetParam param = new HostgroupGetParam();
            param.setGroupids(Arrays.asList(groupid));
            param.setOutput("extend");

            List<Hostgroup> hostgroups = client.hostgroup().get(param);
            assertEquals(0, hostgroups.size());
        }
    }

    @Test
    public void testCreateIllegalArgument() {
        // nameを指定していない場合
        HostgroupCreateParam param = new HostgroupCreateParam();

        try {
            client.hostgroup().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testCreateExistName() {
        // 事前準備
        String groupid;
        {
            HostgroupCreateParam param = new HostgroupCreateParam();
            param.setName("name1");
            List<String> hostgroupids = client.hostgroup().create(param);
            groupid = hostgroupids.get(0);
        }

        // 存在するnameを指定した場合
        try {
            HostgroupCreateParam param = new HostgroupCreateParam();
            param.setName("name1");
            client.hostgroup().create(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.hostgroup().delete(Arrays.asList(groupid));
        }
    }

    @Test
    public void testUpdateIllegalArgument() {
        // groupidを指定していない場合
        HostgroupUpdateParam param = new HostgroupUpdateParam();
        param.setName("name1");

        try {
            client.hostgroup().update(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testUpdateExistName() {
        // 事前準備
        String groupid;
        {
            HostgroupCreateParam param = new HostgroupCreateParam();
            param.setName("name1");
            List<String> hostgroupids = client.hostgroup().create(param);
            groupid = hostgroupids.get(0);
        }

        String groupid2;
        {
            HostgroupCreateParam param = new HostgroupCreateParam();
            param.setName("name2");
            List<String> hostgroupids2 = client.hostgroup().create(param);
            groupid2 = hostgroupids2.get(0);
        }

        // 存在するnameを指定した場合
        try {
            HostgroupUpdateParam param = new HostgroupUpdateParam();
            param.setGroupid(groupid2);
            param.setName("name1");
            client.hostgroup().update(param);
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.hostgroup().delete(Arrays.asList(groupid, groupid2));
        }
    }

    @Test
    public void testUpdateNotExist() {
        // 存在しないgroupidを指定した場合
        HostgroupUpdateParam param = new HostgroupUpdateParam();
        param.setGroupid("999999");
        param.setName("name1");

        try {
            client.hostgroup().update(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteIllegalArgument() {
        // groupidを指定していない場合
        try {
            client.hostgroup().delete(null);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteNotExist() {
        // 存在しないgroupidを指定した場合
        try {
            client.hostgroup().delete(Arrays.asList("999999"));
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testGetByFilter() {
        // 存在するgroup名を指定した場合
        HostgroupGetParam param = new HostgroupGetParam();
        param.setOutput("extend");

        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("name", Arrays.asList((Object) "Linux servers"));
        param.setFilter(filter);

        List<Hostgroup> hostgroups = client.hostgroup().get(param);
        assertEquals(1, hostgroups.size());
        assertEquals("2", hostgroups.get(0).getGroupid());
        assertEquals("Linux servers", hostgroups.get(0).getName());
    }

    @Test
    public void testGetByFilter2() {
        // 存在しないgroup名を指定した場合
        HostgroupGetParam param = new HostgroupGetParam();
        param.setOutput("extend");

        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("name", Arrays.asList((Object) "dummy"));
        param.setFilter(filter);

        List<Hostgroup> hostgroups = client.hostgroup().get(param);
        assertEquals(0, hostgroups.size());
    }

}
