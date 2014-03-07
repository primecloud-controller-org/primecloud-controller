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
import java.util.List;
import java.util.Properties;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.client.UsergroupClient;
import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupGetParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * <p>
 * {@link UsergroupClient}のテストクラスです。
 * </p>
 *
 */
public class UsergroupClientTest {

    private Log log = LogFactory.getLog(UsergroupClientTest.class);

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
        UsergroupGetParam param = new UsergroupGetParam();
        param.setOutput("extend");
        List<Usergroup> usergroups = client.usergroup().get(param);
        for (Usergroup usergroup : usergroups) {
            log.trace(ReflectionToStringBuilder.toString(usergroup, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        if (usergroups.size() > 0) {
            // usergroupidを指定して取得
            List<String> usergroupids = new ArrayList<String>();
            usergroupids.add(usergroups.get(0).getUsrgrpid());
            param.setUsrgrpids(usergroupids);
            List<Usergroup> usergroups2 = client.usergroup().get(param);

            assertEquals(1, usergroups2.size());
            assertEquals(usergroups.get(0).getUsrgrpid(), usergroups2.get(0).getUsrgrpid());
        }
    }

    @Test
    @Ignore
    public void testGet2() {
        // 存在しないusergroupidを指定した場合
        UsergroupGetParam param = new UsergroupGetParam();
        List<String> usergroupids = new ArrayList<String>();
        usergroupids.add("999999");
        param.setUsrgrpids(usergroupids);
        List<Usergroup> usergroups = client.usergroup().get(param);
        assertEquals(0, usergroups.size());
    }

}
