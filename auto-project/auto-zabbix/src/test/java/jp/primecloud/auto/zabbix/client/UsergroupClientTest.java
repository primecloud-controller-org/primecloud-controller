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
import jp.primecloud.auto.zabbix.model.user.User;
import jp.primecloud.auto.zabbix.model.user.UserCreateParam;
import jp.primecloud.auto.zabbix.model.user.UserGetParam;
import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupCreateParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupGetParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupMassAddParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupUpdateParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
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
    public void testGetAll() {
        // 全件取得
        UsergroupGetParam param = new UsergroupGetParam();
        param.setOutput("extend");

        List<Usergroup> usergroups = client.usergroup().get(param);
        for (Usergroup usergroup : usergroups) {
            log.trace(ReflectionToStringBuilder.toString(usergroup, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertTrue(usergroups.size() > 0);
    }

    @Test
    public void testGet() {
        // usrgrpidを指定して取得
        UsergroupGetParam param = new UsergroupGetParam();
        param.setUsrgrpids(Arrays.asList("8"));
        param.setOutput("extend");

        List<Usergroup> usergroups = client.usergroup().get(param);
        assertEquals(1, usergroups.size());
        assertEquals("8", usergroups.get(0).getUsrgrpid());
    }

    @Test
    public void testGetNotExist() {
        // 存在しないusrgrpidを指定した場合
        UsergroupGetParam param = new UsergroupGetParam();
        param.setUsrgrpids(Arrays.asList("999999"));
        param.setOutput("extend");

        List<Usergroup> usergroups = client.usergroup().get(param);
        assertEquals(0, usergroups.size());
    }

    @Test
    public void testCreateUpdateDelete() {
        // Create
        String usrgrpid;
        {
            UsergroupCreateParam param = new UsergroupCreateParam();
            param.setName("name1");
            param.setGuiAccess(1);
            param.setUsersStatus(1);
            param.setApiAccess(1);
            param.setDebugMode(1);

            List<String> usrgrpids = client.usergroup().create(param);
            assertEquals(1, usrgrpids.size());

            usrgrpid = usrgrpids.get(0);
        }

        // Get
        {
            UsergroupGetParam param = new UsergroupGetParam();
            param.setUsrgrpids(Arrays.asList(usrgrpid));
            param.setOutput("extend");

            List<Usergroup> usergroups = client.usergroup().get(param);
            assertEquals(1, usergroups.size());

            Usergroup usergroup = usergroups.get(0);
            assertEquals(usrgrpid, usergroup.getUsrgrpid());
            assertEquals("name1", usergroup.getName());
            assertEquals(1, usergroup.getGuiAccess().intValue());
            assertEquals(1, usergroup.getUsersStatus().intValue());
            assertEquals(1, usergroup.getDebugMode().intValue());

            // apiAccessは2.0以降で廃止されたパラメータ
            if (client.checkVersion("2.0") < 0) {
                assertEquals(1, usergroup.getApiAccess().intValue());
            }
        }

        // Update
        {
            UsergroupUpdateParam param = new UsergroupUpdateParam();
            param.setUsrgrpid(usrgrpid);
            param.setName("name2");
            param.setGuiAccess(0);
            param.setUsersStatus(0);
            param.setApiAccess(0);
            param.setDebugMode(0);

            List<String> usrgrpids = client.usergroup().update(param);
            assertEquals(1, usrgrpids.size());
            assertEquals(usrgrpid, usrgrpids.get(0));
        }

        // Get
        {
            UsergroupGetParam param = new UsergroupGetParam();
            param.setUsrgrpids(Arrays.asList(usrgrpid));
            param.setOutput("extend");

            List<Usergroup> usergroups = client.usergroup().get(param);
            assertEquals(1, usergroups.size());

            Usergroup usergroup = usergroups.get(0);
            assertEquals(usrgrpid, usergroup.getUsrgrpid());
            assertEquals("name2", usergroup.getName());
            assertEquals(0, usergroup.getGuiAccess().intValue());
            assertEquals(0, usergroup.getUsersStatus().intValue());
            assertEquals(0, usergroup.getDebugMode().intValue());

            // apiAccessは2.0以降で廃止されたパラメータ
            if (client.checkVersion("2.0") < 0) {
                assertEquals(0, usergroup.getApiAccess().intValue());
            }
        }

        // Delete
        {
            List<String> usrgrpids = client.usergroup().delete(Arrays.asList(usrgrpid));
            assertEquals(1, usrgrpids.size());
            assertEquals(usrgrpid, usrgrpids.get(0));
        }

        // Get
        {
            UsergroupGetParam param = new UsergroupGetParam();
            param.setUsrgrpids(Arrays.asList(usrgrpid));
            param.setOutput("extend");

            List<Usergroup> usergroups = client.usergroup().get(param);
            assertEquals(0, usergroups.size());
        }
    }

    @Test
    public void testCreateIllegalArgument() {
        // nameを指定していない場合
        UsergroupCreateParam param = new UsergroupCreateParam();

        try {
            client.usergroup().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testCreateExistName() {
        // 事前準備
        String usrgrpid;
        {
            UsergroupCreateParam param = new UsergroupCreateParam();
            param.setName("name1");
            List<String> usrgrpids = client.usergroup().create(param);
            usrgrpid = usrgrpids.get(0);
        }

        // 存在するnameを指定した場合
        try {
            UsergroupCreateParam param = new UsergroupCreateParam();
            param.setName("name1");
            client.usergroup().create(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.usergroup().delete(Arrays.asList(usrgrpid));
        }
    }

    @Test
    public void testUpdateIllegalArgument() {
        // usrgrpidを指定していない場合
        UsergroupUpdateParam param = new UsergroupUpdateParam();

        try {
            client.usergroup().update(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testUpdateExistName() {
        // 事前準備
        String usrgrpid;
        {
            UsergroupCreateParam param = new UsergroupCreateParam();
            param.setName("name1");
            List<String> usrgrpids = client.usergroup().create(param);
            usrgrpid = usrgrpids.get(0);
        }

        String usrgrpid2;
        {
            UsergroupCreateParam param = new UsergroupCreateParam();
            param.setName("name2");
            List<String> usrgrpids = client.usergroup().create(param);
            usrgrpid2 = usrgrpids.get(0);
        }

        // 存在するnameを指定した場合
        try {
            UsergroupUpdateParam param = new UsergroupUpdateParam();
            param.setUsrgrpid(usrgrpid2);
            param.setName("name1");
            client.usergroup().update(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.usergroup().delete(Arrays.asList(usrgrpid, usrgrpid2));
        }
    }

    @Test
    public void testUpdateNotExist() {
        // 存在しないusrgrpidを指定した場合
        UsergroupUpdateParam param = new UsergroupUpdateParam();
        param.setUsrgrpid("999999");

        try {
            client.usergroup().update(param);
            //fail(); // エラーは発生しない
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteIllegalArgument() {
        // useridを指定していない場合
        try {
            client.usergroup().delete(null);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteNotExist() {
        // 存在しないusrgrpidを指定した場合
        try {
            client.usergroup().delete(Arrays.asList("999999"));
            //fail(); // エラーは発生しない
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testMassAdd() {
        // 事前準備
        String userid;
        {
            UserCreateParam param = new UserCreateParam();
            param.setAlias("alias1");
            param.setPasswd("passwd1");
            Usergroup usergroup = new Usergroup();
            usergroup.setUsrgrpid("8");
            param.setUsrgrps(Arrays.asList(usergroup));

            List<String> userids = client.user().create(param);
            userid = userids.get(0);
        }

        String usrgrpid;
        {
            UsergroupCreateParam param = new UsergroupCreateParam();
            param.setName("name1");
            List<String> usrgrpids = client.usergroup().create(param);
            usrgrpid = usrgrpids.get(0);
        }

        try {
            // MassAdd
            {
                UsergroupMassAddParam param = new UsergroupMassAddParam();
                param.setUserids(Arrays.asList(userid));
                param.setUsrgrpids(Arrays.asList(usrgrpid));

                List<String> usrgrpids = client.usergroup().massAdd(param);

                assertEquals(1, usrgrpids.size());
                assertEquals(usrgrpid, usrgrpids.get(0));
            }

            // User Get
            {
                UserGetParam param = new UserGetParam();
                param.setUserids(Arrays.asList(userid));
                param.setOutput("extend");
                param.setSelectUsrgrps("extend");

                List<User> users = client.user().get(param);

                assertEquals(1, users.size());

                User user = users.get(0);
                assertEquals(userid, user.getUserid());
                assertEquals(2, user.getUsrgrps().size());
                assertTrue(Arrays.asList("8", usrgrpid).contains(user.getUsrgrps().get(0).getUsrgrpid()));
                assertTrue(Arrays.asList("8", usrgrpid).contains(user.getUsrgrps().get(1).getUsrgrpid()));
            }
        } finally {
            // 事後始末
            client.usergroup().delete(Arrays.asList(usrgrpid));
            client.user().delete(Arrays.asList(userid));
        }
    }
}
