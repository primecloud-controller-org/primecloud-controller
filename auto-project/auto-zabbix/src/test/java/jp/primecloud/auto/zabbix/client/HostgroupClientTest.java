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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.client.HostgroupClient;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupCreateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupGetParam;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupUpdateParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
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
    @Ignore
    public void testGet() {
        // 全件取得
        HostgroupGetParam param = new HostgroupGetParam();
        param.setOutput("extend");
        List<Hostgroup> hostgroups = client.hostgroup().get(param);
        for (Hostgroup hostgroup : hostgroups) {
            log.trace(ReflectionToStringBuilder.toString(hostgroup, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        if (hostgroups.size() > 0) {
            // hostgroupidを指定して取得
            List<String> hostgroupids = new ArrayList<String>();
            hostgroupids.add(hostgroups.get(0).getGroupid());
            param.setGroupids(hostgroupids);
            List<Hostgroup> hostgroups2 = client.hostgroup().get(param);

            assertEquals(1, hostgroups2.size());
            assertEquals(hostgroups.get(0).getGroupid(), hostgroups2.get(0).getGroupid());
        }
    }

    @Test
    @Ignore
    public void testGet2() {
        // 存在しないhostgroupidを指定した場合
        HostgroupGetParam param = new HostgroupGetParam();
        List<String> hostgroupids = new ArrayList<String>();
        hostgroupids.add("999999");
        param.setGroupids(hostgroupids);
        List<Hostgroup> hostgroups = client.hostgroup().get(param);
        assertEquals(0, hostgroups.size());
    }

    @Test
    @Ignore
    public void testCreateUpdateDelete() {
        // Create
        HostgroupCreateParam param = new HostgroupCreateParam();
        param.setName("TestServer");
        List<String> hostgroupids = client.hostgroup().create(param);
        for (String id : hostgroupids) {
            log.trace(id);
        }
        assertEquals(1, hostgroupids.size());

        String groupid = hostgroupids.get(0);

        // Update
        HostgroupUpdateParam param2 = new HostgroupUpdateParam();
        param2.setGroupid(groupid);
        param2.setName("TestServerUpdate");
        List<String> hostgroupids2 = client.hostgroup().update(param2);
        for (String id : hostgroupids2) {
            log.trace(id);
        }
        assertEquals(1, hostgroupids2.size());
        assertEquals(groupid, hostgroupids2.get(0));

        // Delete
        List<String> hostgroupids3 = client.hostgroup().delete(Arrays.asList(groupid));
        for (String id : hostgroupids3) {
            log.trace(id);
        }
        assertEquals(1, hostgroupids3.size());
        assertEquals(groupid, hostgroupids3.get(0));
    }

    @Test
    @Ignore
    public void testCreate() {
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
    @Ignore
    public void testCreate2() {
        // 事前準備
        HostgroupCreateParam param = new HostgroupCreateParam();
        param.setName("TestServer");
        List<String> hostgroupids = client.hostgroup().create(param);
        String groupid = hostgroupids.get(0);

        // 存在するnameを指定した場合
        try {
            HostgroupCreateParam param2 = new HostgroupCreateParam();
            param2.setName("TestServer");
            client.hostgroup().create(param2);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            client.hostgroup().delete(Arrays.asList(groupid));
        }
    }

    @Test
    @Ignore
    public void testUpdate() {
        // groupidを指定していない場合
        HostgroupUpdateParam param = new HostgroupUpdateParam();
        param.setName("TestServer");
        try {
            client.hostgroup().update(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testUpdate2() {
        // 事前準備
        HostgroupCreateParam param = new HostgroupCreateParam();
        param.setName("TestServer");
        List<String> hostgroupids = client.hostgroup().create(param);
        String groupid = hostgroupids.get(0);

        HostgroupCreateParam param2 = new HostgroupCreateParam();
        param2.setName("TestServer2");
        List<String> hostgroupids2 = client.hostgroup().create(param2);
        String groupid2 = hostgroupids2.get(0);

        // 存在するgroupidを指定した場合
        try {
            HostgroupUpdateParam param3 = new HostgroupUpdateParam();
            param3.setGroupid(groupid2);
            param3.setName("TestServer");
            client.hostgroup().update(param3);
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            client.hostgroup().delete(Arrays.asList(groupid));
            client.hostgroup().delete(Arrays.asList(groupid2));
        }
    }

    @Test
    @Ignore
    public void testUpdate3() {
        // 存在しないgroupidを指定した場合
        HostgroupUpdateParam param = new HostgroupUpdateParam();
        param.setGroupid("999999");
        try {
            client.hostgroup().update(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testDelete() {
        // groupidを指定していない場合
        try {
            client.hostgroup().delete(null);
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testDelete2() {
        // 存在しないgroupidを指定した場合
        try {
            client.hostgroup().delete(Arrays.asList("999999"));
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testGet3() {
        // 存在するgroup名を指定した場合
        HostgroupGetParam param = new HostgroupGetParam();
        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("name", Arrays.asList((Object)"Linux servers"));
        param.setFilter(filter);
        param.setOutput("extend");

        List<Hostgroup> hostgroups = client.hostgroup().get(param);

        assertEquals(1, hostgroups.size());
        assertEquals("2", hostgroups.get(0).getGroupid());
        assertEquals("Linux servers", hostgroups.get(0).getName());
    }

    @Test
    @Ignore
    public void testGet4() {
        // 存在しないgroup名を指定した場合
        HostgroupGetParam param = new HostgroupGetParam();
        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("name", Arrays.asList((Object)"dummy"));
        param.setFilter(filter);
        param.setOutput("extend");

        List<Hostgroup> hostgroups = client.hostgroup().get(param);

        assertEquals(0, hostgroups.size());
    }

}
