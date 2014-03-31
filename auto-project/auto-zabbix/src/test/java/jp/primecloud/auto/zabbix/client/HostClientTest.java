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
import java.util.List;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.client.HostClient;
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
import org.junit.Ignore;
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
    @Ignore
    public void testGet() {
        // 全件取得
        HostGetParam param = new HostGetParam();
        param.setOutput("extend");
        List<Host> hosts = client.host().get(param);
        for (Host host : hosts) {
            log.trace(ReflectionToStringBuilder.toString(host, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        if (hosts.size() > 0) {
            // hostidを指定して取得
            List<String> hostids = new ArrayList<String>();
            hostids.add(hosts.get(0).getHostid().toString());
            param.setHostids(hostids);
            param.setSelectParentTemplates("extend");
            param.setSelectGroups("extend");
            List<Host> hosts2 = client.host().get(param);

            assertEquals(1, hosts2.size());
            assertEquals(hosts.get(0).getHostid(), hosts2.get(0).getHostid());

            Host host = hosts2.get(0);
            log.trace(ReflectionToStringBuilder.toString(host, ToStringStyle.SHORT_PREFIX_STYLE));
            //for (Template template : host.getTemplates()) {
            for (Template template : host.getParenttemplates()) {
                log.trace(ReflectionToStringBuilder.toString(template, ToStringStyle.SHORT_PREFIX_STYLE));
            }
            for (Hostgroup hostgroup : host.getGroups()) {
                log.trace(ReflectionToStringBuilder.toString(hostgroup, ToStringStyle.SHORT_PREFIX_STYLE));
            }
        }
    }

    @Test
    @Ignore
    public void testGet2() {
        // 存在しないhostidを指定した場合
        HostGetParam param = new HostGetParam();
        List<String> hostids = new ArrayList<String>();
        hostids.add("999999");
        param.setHostids(hostids);
        List<Host> hosts = client.host().get(param);
        assertEquals(0, hosts.size());
    }

    @Test
    @Ignore
    public void testCreateUpdateDelete() {
        // Create
        Hostgroup hostgroup = new Hostgroup();
        hostgroup.setGroupid("4");
        
        HostCreateParam param = new HostCreateParam();
        param.setGroups(Arrays.asList(hostgroup));
        param.setHost("test.scsk.jp");
        List<String> hostids = client.host().create(param);
        for (String id : hostids) {
            log.trace(id);
        }
        assertEquals(1, hostids.size());

        String hostid = hostids.get(0);

        // Update
        HostUpdateParam param2 = new HostUpdateParam();
        param2.setHostid(hostid);
        param2.setHost("test.scsk.jp.update");
        List<String> hostids2 = client.host().update(param2);
        for (String id : hostids2) {
            log.trace(id);
        }
        assertEquals(1, hostids2.size());
        assertEquals(hostid, hostids2.get(0));

        // Delete
        List<String> hostids3 = client.host().delete(Arrays.asList(hostid));
        for (String id : hostids3) {
            log.trace(id);
        }
        assertEquals(1, hostids3.size());
        assertEquals(hostid, hostids3.get(0));
    }

    @Test
    @Ignore
    public void testCreate() {
        // hostを指定していない場合
        Hostgroup hostgroup = new Hostgroup();
        hostgroup.setGroupid("4");
        
        HostCreateParam param = new HostCreateParam();
        param.setGroups(Arrays.asList(hostgroup));
        try {
            client.host().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testCreate2() {
        // groupsを指定していない場合
        HostCreateParam param = new HostCreateParam();
        param.setHost("test.scsk.jp");
        try {
            client.host().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testCreate3() {
        // 事前準備
        Hostgroup hostgroup = new Hostgroup();
        hostgroup.setGroupid("4");
        
        HostCreateParam param = new HostCreateParam();
        param.setGroups(Arrays.asList(hostgroup));
        param.setHost("test.scsk.jp");
        List<String> hostids = client.host().create(param);
        String hostid = hostids.get(0);

        // 存在するhostを指定した場合
        try {
            HostCreateParam param2 = new HostCreateParam();
            param2.setGroups(Arrays.asList(hostgroup));
            param2.setHost("test.scsk.jp");
            client.host().create(param2);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            client.host().delete(Arrays.asList(hostid));
        }
    }

    @Test
    @Ignore
    public void testUpdate() {
        // hostidを指定していない場合
        HostUpdateParam param = new HostUpdateParam();
        param.setHost("test.scsk.jp.update");
        try {
            client.host().update(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testUpdate2() {
        // 事前準備
        Hostgroup hostgroup = new Hostgroup();
        hostgroup.setGroupid("4");
        
        HostCreateParam param = new HostCreateParam();
        param.setGroups(Arrays.asList(hostgroup));
        param.setHost("test.scsk.jp");
        List<String> hostids = client.host().create(param);
        String hostid = hostids.get(0);

        HostCreateParam param2 = new HostCreateParam();
        param2.setGroups(Arrays.asList(hostgroup));
        param2.setHost("create.test.scsk.jp");
        List<String> hostids2 = client.host().create(param2);
        String hostid2 = hostids2.get(0);

        // 存在するhostを指定した場合
        try {
            HostUpdateParam param3 = new HostUpdateParam();
            param3.setHostid(hostid2);
            param3.setHost("test.scsk.jp");
            client.host().update(param3);
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            client.host().delete(Arrays.asList(hostid));
            client.host().delete(Arrays.asList(hostid2));
        }
    }

    @Test
    @Ignore
    public void testUpdate3() {
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
    @Ignore
    public void testDelete() {
        // hostidを指定していない場合
        try {
            client.host().delete(null);
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testDelete2() {
        // 存在しないhostidを指定した場合
        try {
            client.host().delete(Arrays.asList("999999"));
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

}
