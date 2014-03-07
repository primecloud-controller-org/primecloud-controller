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
import jp.primecloud.auto.zabbix.client.UserClient;
import jp.primecloud.auto.zabbix.model.user.User;
import jp.primecloud.auto.zabbix.model.user.UserAuthenticateParam;
import jp.primecloud.auto.zabbix.model.user.UserCreateParam;
import jp.primecloud.auto.zabbix.model.user.UserGetParam;
import jp.primecloud.auto.zabbix.model.user.UserUpdateParam;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * <p>
 * {@link UserClient}のテストクラスです。
 * </p>
 *
 */
public class UserClientTest {

    private Log log = LogFactory.getLog(UserClientTest.class);

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
    public void testLogin() throws Exception {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/zabbix.properties"));
        String username = properties.getProperty("zabbix.username");
        String password = properties.getProperty("zabbix.password");

        UserAuthenticateParam param = new UserAuthenticateParam();
        param.setUser(username);
        param.setPassword(password);
        String sessionId = client.user().login(param);
        log.trace(sessionId);
    }

    @Test
    @Ignore
    public void testLogin2() {
        // 認証情報が誤っている場合
        UserAuthenticateParam param = new UserAuthenticateParam();
        param.setUser("dummy");
        param.setPassword("dummy");
        try {
            client.user().login(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testGet() {
        // 全件取得
        UserGetParam param = new UserGetParam();
        param.setOutput("extend");
        List<User> users = client.user().get(param);
        for (User user : users) {
            log.trace(ReflectionToStringBuilder.toString(user, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        if (users.size() > 0) {
            // useridを指定して取得
            List<String> userids = new ArrayList<String>();
            userids.add(users.get(0).getUserid());
            param.setUserids(userids);
            List<User> users2 = client.user().get(param);

            assertEquals(1, users2.size());
            assertEquals(users.get(0).getUserid(), users2.get(0).getUserid());
        }
    }

    @Test
    @Ignore
    public void testGet2() {
        // 存在しないuseridを指定した場合
        UserGetParam param = new UserGetParam();
        List<String> userids = new ArrayList<String>();
        userids.add("999999");
        param.setUserids(userids);
        List<User> users = client.user().get(param);
        assertEquals(0, users.size());
    }

    @Test
    @Ignore
    public void testCreateUpdateDelete() {
        // Create
        UserCreateParam param = new UserCreateParam();
        param.setAlias("alias1");
        List<String> userids = client.user().create(param);
        for (String id : userids) {
            log.trace(id);
        }
        assertEquals(1, userids.size());

        String userid = userids.get(0);

        // Update
        UserUpdateParam param2 = new UserUpdateParam();
        param2.setUserid(userid);
        param2.setAlias("alias2");
        List<String> userid2 = client.user().update(param2);
        for (String id : userid2) {
            log.trace(id);
        }
        assertEquals(1, userid2.size());
        assertEquals(userid, userid2.get(0));

        // Delete
        List<String> userid3 = client.user().delete(Arrays.asList(userid));
        for (String id : userid3) {
            log.trace(id);
        }
        assertEquals(1, userid3.size());
        assertEquals(userid, userid3.get(0));
    }

    @Test
    @Ignore
    public void testCreate() {
        // aliasを指定していない場合
        UserCreateParam param = new UserCreateParam();
        param.setName("name1");
        try {
            client.user().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testCreate2() {
        // 事前準備
        UserCreateParam param = new UserCreateParam();
        param.setAlias("alias1");
        List<String> userids = client.user().create(param);
        String userid = userids.get(0);
        // 存在するaliasを指定した場合
        try {
            UserCreateParam param2 = new UserCreateParam();
            param2.setAlias("alias1");
            client.user().create(param2);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            client.user().delete(Arrays.asList(userid));
        }
    }

    @Test
    @Ignore
    public void testUpdate() {
        // useridを指定していない場合
        UserUpdateParam param = new UserUpdateParam();
        param.setAlias("alias1");
        try {
            client.user().update(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testUpdate2() {
        // 事前準備
        UserCreateParam param = new UserCreateParam();
        param.setAlias("alias1");
        List<String> userids = client.user().create(param);
        String userid = userids.get(0);

        UserCreateParam param2 = new UserCreateParam();
        param2.setAlias("alias2");
        String userid2 = client.user().create(param2).get(0);

        // 存在するaliasを指定した場合
        try {
            UserUpdateParam param3 = new UserUpdateParam();
            param3.setUserid(userid2);
            param3.setAlias("alias1");
            client.user().update(param3);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            client.user().delete(Arrays.asList(userid));
            client.user().delete(Arrays.asList(userid2));
        }
    }

    @Test
    @Ignore
    public void testUpdate3() {
        // 存在しないuseridを指定した場合
        UserUpdateParam param = new UserUpdateParam();
        param.setUserid("999999");
        try {
            client.user().update(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testDelete() {
        // useridを指定していない場合
        try {
            client.user().delete(null);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    @Ignore
    public void testDelete2() {
        // 存在しないuseridを指定した場合
        List<String> userid = client.user().delete(Arrays.asList("999999"));
        assertEquals(0, userid.size());
    }

}
