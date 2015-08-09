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
import java.util.List;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.host.Host;
import jp.primecloud.auto.zabbix.model.host.HostCreateParam;
import jp.primecloud.auto.zabbix.model.host.HostGetParam;
import jp.primecloud.auto.zabbix.model.host.HostUpdateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.template.Template;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * {@link HostClient}のテストクラスです。
 * </p>
 *
 */
public class HostClientTest {

    private Log log = LogFactory.getLog(HostClientTest.class);

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
        HostGetParam param = new HostGetParam();
        param.setOutput("extend");
        param.setSelectGroups("extend");
        param.setSelectParentTemplates("extend");

        List<Host> hosts = client.host().get(param);
        for (Host host : hosts) {
            log.trace(ReflectionToStringBuilder.toString(host, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(hosts.size() > 0);
    }

    @Test
    public void testGet() {
        // hostidを指定して取得
        HostGetParam param = new HostGetParam();
        param.setHostids(Arrays.asList("10017"));
        param.setOutput("extend");
        param.setSelectGroups("extend");
        param.setSelectParentTemplates("extend");

        List<Host> hosts = client.host().get(param);
        assertEquals(1, hosts.size());
        assertEquals("10017", hosts.get(0).getHostid());
        assertEquals(1, hosts.get(0).getGroups().size());
        assertEquals(1, hosts.get(0).getParenttemplates().size());
    }

    @Test
    public void testGetNotExist() {
        // 存在しないhostidを指定した場合
        HostGetParam param = new HostGetParam();
        param.setHostids(Arrays.asList("999999"));
        param.setOutput("extend");
        param.setSelectGroups("extend");
        param.setSelectParentTemplates("extend");

        List<Host> hosts = client.host().get(param);
        assertEquals(0, hosts.size());
    }

    @Test
    public void testCreateUpdateDelete() {
        // Create
        String hostid;
        {
            HostCreateParam param = new HostCreateParam();
            param.setHost("host1");
            param.setDns("dns1");
            param.setIp("127.0.1.1");
            param.setPort(10001);
            param.setStatus(1);
            param.setUseip(1);

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("2");
            param.setGroups(Arrays.asList(hostgroup));

            Template template = new Template();
            template.setTemplateid("10001");
            param.setTemplates(Arrays.asList(template));

            List<String> hostids = client.host().create(param);
            assertEquals(1, hostids.size());

            hostid = hostids.get(0);
        }

        // Get
        {
            HostGetParam param = new HostGetParam();
            param.setHostids(Arrays.asList(hostid));
            param.setOutput("extend");
            param.setSelectGroups("extend");
            param.setSelectParentTemplates("extend");

            List<Host> hosts = client.host().get(param);
            assertEquals(1, hosts.size());

            Host host = hosts.get(0);
            assertEquals(hostid, host.getHostid());
            assertEquals("host1", host.getHost());
            assertEquals("dns1", host.getDns());
            assertEquals("127.0.1.1", host.getIp());
            assertEquals(10001, host.getPort().intValue());
            assertEquals(1, host.getStatus().intValue());
            assertEquals(1, host.getUseip().intValue());

            assertEquals(1, host.getGroups().size());
            assertEquals("2", host.getGroups().get(0).getGroupid());

            assertEquals(1, host.getParenttemplates().size());
            assertEquals("10001", host.getParenttemplates().get(0).getTemplateid());
        }

        // Update
        {
            HostUpdateParam param = new HostUpdateParam();
            param.setHostid(hostid);
            param.setHost("host2");
            param.setDns("dns2");
            param.setIp("127.0.1.2");
            param.setPort(10002);
            param.setStatus(0);
            param.setUseip(0);

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("4");
            param.setGroups(Arrays.asList(hostgroup));

            Template template = new Template();
            template.setTemplateid("10002");
            param.setTemplates(Arrays.asList(template));

            List<String> hostids = client.host().update(param);
            assertEquals(1, hostids.size());
            assertEquals(hostid, hostids.get(0));
        }

        // Get
        {
            HostGetParam param = new HostGetParam();
            param.setHostids(Arrays.asList(hostid));
            param.setOutput("extend");
            param.setSelectGroups("extend");
            param.setSelectParentTemplates("extend");

            List<Host> hosts = client.host().get(param);
            assertEquals(1, hosts.size());

            Host host = hosts.get(0);
            assertEquals(hostid, host.getHostid());
            assertEquals("host2", host.getHost());
            assertEquals("dns2", host.getDns());
            assertEquals("127.0.1.2", host.getIp());
            assertEquals(10002, host.getPort().intValue());
            assertEquals(0, host.getStatus().intValue());
            assertEquals(0, host.getUseip().intValue());

            assertEquals(1, host.getGroups().size());
            assertEquals("4", host.getGroups().get(0).getGroupid());

            assertEquals(1, host.getParenttemplates().size());
            assertEquals("10002", host.getParenttemplates().get(0).getTemplateid());
        }

        // Delete
        {
            List<String> hostids = client.host().delete(Arrays.asList(hostid));
            assertEquals(1, hostids.size());
            assertEquals(hostid, hostids.get(0));
        }

        // Get
        {
            HostGetParam param = new HostGetParam();
            param.setHostids(Arrays.asList(hostid));
            param.setOutput("extend");
            param.setSelectGroups("extend");
            param.setSelectParentTemplates("extend");

            List<Host> hosts = client.host().get(param);
            assertEquals(0, hosts.size());
        }
    }

    @Test
    public void testCreateIllegalArgument() {
        // hostを指定していない場合
        HostCreateParam param = new HostCreateParam();
        param.setDns("dns1");

        Hostgroup hostgroup = new Hostgroup();
        hostgroup.setGroupid("2");
        param.setGroups(Arrays.asList(hostgroup));

        try {
            client.host().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testCreateIllegalArgument2() {
        // groupsを指定していない場合
        HostCreateParam param = new HostCreateParam();
        param.setHost("host1");
        param.setDns("dns1");

        try {
            client.host().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testCreateExistHost() {
        // 事前準備
        String hostid;
        {
            HostCreateParam param = new HostCreateParam();
            param.setHost("host1");
            param.setDns("dns1");

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("2");
            param.setGroups(Arrays.asList(hostgroup));

            List<String> hostids = client.host().create(param);
            hostid = hostids.get(0);
        }

        // 存在するhostを指定した場合
        try {
            HostCreateParam param = new HostCreateParam();
            param.setHost("host1");
            param.setDns("dns2");

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("2");
            param.setGroups(Arrays.asList(hostgroup));

            client.host().create(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.host().delete(Arrays.asList(hostid));
        }
    }

    @Test
    public void testUpdateIllegalArgument() {
        // hostidを指定していない場合
        HostUpdateParam param = new HostUpdateParam();
        param.setHost("host1");
        param.setDns("dns1");

        try {
            client.host().update(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testUpdateExistHost() {
        // 事前準備
        String hostid;
        {
            HostCreateParam param = new HostCreateParam();
            param.setHost("host1");
            param.setDns("dns1");

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("2");
            param.setGroups(Arrays.asList(hostgroup));

            List<String> hostids = client.host().create(param);
            hostid = hostids.get(0);
        }

        String hostid2;
        {
            HostCreateParam param = new HostCreateParam();
            param.setHost("host2");
            param.setDns("dns2");

            Hostgroup hostgroup = new Hostgroup();
            hostgroup.setGroupid("2");
            param.setGroups(Arrays.asList(hostgroup));

            List<String> hostids = client.host().create(param);
            hostid2 = hostids.get(0);
        }

        // 存在するhostを指定した場合
        try {
            HostUpdateParam param = new HostUpdateParam();
            param.setHostid(hostid2);
            param.setHost("host1");
            param.setDns("dns2");
            client.host().update(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.host().delete(Arrays.asList(hostid, hostid2));
        }
    }

    @Test
    public void testUpdateNotExist() {
        // 存在しないhostidを指定した場合
        HostUpdateParam param = new HostUpdateParam();
        param.setHostid("999999");

        try {
            client.host().update(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteIllegalArgument() {
        // hostidを指定していない場合
        try {
            client.host().delete(null);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteNotExist() {
        // 存在しないhostidを指定した場合
        try {
            client.host().delete(Arrays.asList("999999"));
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

}
