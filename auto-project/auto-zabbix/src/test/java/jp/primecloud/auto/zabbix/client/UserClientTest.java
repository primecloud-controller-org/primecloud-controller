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
import jp.primecloud.auto.zabbix.model.user.UserAuthenticateParam;
import jp.primecloud.auto.zabbix.model.user.UserCreateParam;
import jp.primecloud.auto.zabbix.model.user.UserGetParam;
import jp.primecloud.auto.zabbix.model.user.UserUpdateParam;
import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
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
    public void testGetAll() {
        // 全件取得
        UserGetParam param = new UserGetParam();
        param.setOutput("extend");
        param.setSelectUsrgrps("extend");

        List<User> users = client.user().get(param);
        for (User user : users) {
            log.trace(ReflectionToStringBuilder.toString(user, ToStringStyle.SHORT_PREFIX_STYLE));
            for (Usergroup usergroup : user.getUsrgrps()) {
                log.trace("    " + ReflectionToStringBuilder.toString(usergroup, ToStringStyle.SHORT_PREFIX_STYLE));
            }
        }

        assertTrue(users.size() > 0);
    }

    @Test
    public void testGet() {
        // useridを指定して取得
        UserGetParam param = new UserGetParam();
        param.setUserids(Arrays.asList("1"));
        param.setOutput("extend");
        param.setSelectUsrgrps("extend");

        List<User> users = client.user().get(param);
        assertEquals(1, users.size());
        assertEquals("1", users.get(0).getUserid());
        assertTrue(users.get(0).getUsrgrps().size() > 0);
    }

    @Test
    public void testGetNotExist() {
        // 存在しないuseridを指定した場合
        UserGetParam param = new UserGetParam();
        param.setUserids(Arrays.asList("999999"));
        param.setOutput("extend");

        List<User> users = client.user().get(param);
        assertEquals(0, users.size());
    }

    @Test
    public void testCreateUpdateDelete() {
        // Create
        String userid;
        {
            UserCreateParam param = new UserCreateParam();
            param.setAlias("alias1");
            param.setName("name1");
            param.setSurname("surname1");
            param.setPasswd("passwd1");
            param.setUrl("url1");
            param.setAutologin(0);
            param.setAutologout(1200);
            param.setRefresh(60);
            param.setType(2);
            param.setRowsPerPage(100);

            if (client.checkVersion("2.0.0") < 0) {
                param.setLang("ja_jp");
                param.setTheme("css_ob.css");
            } else {
                param.setLang("jp_JP");
                param.setTheme("originalblue");

                // 2.0からusrgrpsの指定が必須
                Usergroup usergroup = new Usergroup();
                usergroup.setUsrgrpid("8");
                param.setUsrgrps(Arrays.asList(usergroup));
            }

            List<String> userids = client.user().create(param);
            assertEquals(1, userids.size());

            userid = userids.get(0);
        }

        // Get
        {
            UserGetParam param = new UserGetParam();
            param.setUserids(Arrays.asList(userid));
            param.setOutput("extend");
            param.setSelectUsrgrps("extend");

            List<User> users = client.user().get(param);
            assertEquals(1, users.size());

            User user = users.get(0);
            assertEquals(userid, user.getUserid());
            assertEquals("alias1", user.getAlias());
            assertEquals("name1", user.getName());
            assertEquals("surname1", user.getSurname());
            //assertEquals("passwd1", user.getPasswd());　// Zabbixでハッシュ化されるため検証不能
            assertEquals("url1", user.getUrl());
            assertEquals(0, user.getAutologin().intValue());
            assertEquals(1200, user.getAutologout().intValue());
            assertEquals(60, user.getRefresh().intValue());
            assertEquals(2, user.getType().intValue());
            assertEquals(100, user.getRowsPerPage().intValue());

            if (client.checkVersion("2.0.0") < 0) {
                assertEquals("ja_jp", user.getLang());
                assertEquals("css_ob.css", user.getTheme());
            } else {
                assertEquals("jp_JP", user.getLang());
                assertEquals("originalblue", user.getTheme());

                assertEquals(1, user.getUsrgrps().size());
                assertEquals("8", user.getUsrgrps().get(0).getUsrgrpid());
            }
        }

        // Update
        {
            UserUpdateParam param = new UserUpdateParam();
            param.setUserid(userid);
            param.setAlias("alias2");
            param.setName("name2");
            param.setSurname("surname2");
            param.setPasswd("passwd2");
            param.setUrl("url2");
            param.setAutologin(1);
            param.setAutologout(0);
            param.setRefresh(120);
            param.setType(3);
            param.setRowsPerPage(200);

            if (client.checkVersion("2.0.0") < 0) {
                param.setLang("en_gb");
                param.setTheme("css_bb.css");
            } else {
                param.setLang("en_GB");
                param.setTheme("darkblue");
            }

            List<String> userids = client.user().update(param);
            assertEquals(1, userids.size());
            assertEquals(userid, userids.get(0));
        }

        // Get
        {
            UserGetParam param = new UserGetParam();
            param.setUserids(Arrays.asList(userid));
            param.setOutput("extend");
            param.setSelectUsrgrps("extend");

            List<User> users = client.user().get(param);
            assertEquals(1, users.size());

            User user = users.get(0);
            assertEquals("alias2", user.getAlias());
            assertEquals("name2", user.getName());
            assertEquals("surname2", user.getSurname());
            //assertEquals("passwd2", user.getPasswd());　// Zabbixでハッシュ化されるため検証不能
            assertEquals("url2", user.getUrl());
            assertEquals(1, user.getAutologin().intValue());
            assertEquals(0, user.getAutologout().intValue());
            assertEquals(120, user.getRefresh().intValue());
            assertEquals(3, user.getType().intValue());
            assertEquals(200, user.getRowsPerPage().intValue());

            if (client.checkVersion("2.0.0") < 0) {
                assertEquals("en_gb", user.getLang());
                assertEquals("css_bb.css", user.getTheme());
            } else {
                assertEquals("en_GB", user.getLang());
                assertEquals("darkblue", user.getTheme());

                assertEquals(1, user.getUsrgrps().size());
                assertEquals("8", user.getUsrgrps().get(0).getUsrgrpid());
            }
        }

        // Delete
        {
            List<String> userids = client.user().delete(Arrays.asList(userid));
            assertEquals(1, userids.size());
            assertEquals(userid, userids.get(0));
        }

        // Get
        {
            UserGetParam param = new UserGetParam();
            param.setUserids(Arrays.asList(userid));
            param.setOutput("extend");
            param.setSelectUsrgrps("extend");

            List<User> users = client.user().get(param);
            assertEquals(0, users.size());
        }
    }

    @Test
    public void testCreateIllegalArgument() {
        // aliasを指定していない場合
        UserCreateParam param = new UserCreateParam();
        param.setPasswd("passwd1");
        Usergroup usergroup = new Usergroup();
        usergroup.setUsrgrpid("8");
        param.setUsrgrps(Arrays.asList(usergroup));

        try {
            client.user().create(param);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testCreateIllegalArgument2() {
        // passwdを指定していない場合
        UserCreateParam param = new UserCreateParam();
        param.setAlias("alias1");
        Usergroup usergroup = new Usergroup();
        usergroup.setUsrgrpid("8");
        param.setUsrgrps(Arrays.asList(usergroup));

        // passwdの指定は2.0から必須になったため、1.8ではユーザを作成でき、2.0以降ではエラーになる
        if (client.checkVersion("2.0") < 0) {
            List<String> userids = client.user().create(param);
            client.user().delete(userids);
        } else {
            try {
                client.user().create(param);
                fail();
            } catch (IllegalArgumentException ignore) {
                log.trace(ignore.getMessage());
            }
        }
    }

    @Test
    public void testCreateIllegalArgument3() {
        // usrgrpsを指定していない場合
        UserCreateParam param = new UserCreateParam();
        param.setAlias("alias1");
        param.setPasswd("passwd1");

        // usrgrpsの指定は2.0から必須になったため、1.8ではユーザを作成でき、2.0以降ではエラーになる
        if (client.checkVersion("2.0") < 0) {
            List<String> userids = client.user().create(param);
            client.user().delete(userids);
        } else {
            try {
                client.user().create(param);
                fail();
            } catch (IllegalArgumentException ignore) {
                log.trace(ignore.getMessage());
            }
        }
    }

    @Test
    public void testCreateExistAlias() {
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

        // 存在するaliasを指定した場合
        try {
            UserCreateParam param = new UserCreateParam();
            param.setAlias("alias1");
            param.setPasswd("passwd1");
            Usergroup usergroup = new Usergroup();
            usergroup.setUsrgrpid("8");
            param.setUsrgrps(Arrays.asList(usergroup));

            client.user().create(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.user().delete(Arrays.asList(userid));
        }
    }

    @Test
    public void testUpdateIllegalArgument() {
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
    public void testUpdateExistAlias() {
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

        String userid2;
        {
            UserCreateParam param = new UserCreateParam();
            param.setAlias("alias2");
            param.setPasswd("passwd2");
            Usergroup usergroup = new Usergroup();
            usergroup.setUsrgrpid("8");
            param.setUsrgrps(Arrays.asList(usergroup));

            List<String> userids = client.user().create(param);
            userid2 = userids.get(0);
        }

        // 存在するaliasを指定した場合
        try {
            UserUpdateParam param = new UserUpdateParam();
            param.setUserid(userid2);
            param.setAlias("alias1");
            client.user().update(param);
            fail();
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        } finally {
            // 事後始末
            client.user().delete(Arrays.asList(userid, userid2));
        }
    }

    @Test
    public void testUpdateNotExist() {
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
    public void testDeleteIllegalArgument() {
        // useridを指定していない場合
        try {
            client.user().delete(null);
            fail();
        } catch (IllegalArgumentException ignore) {
            log.trace(ignore.getMessage());
        }
    }

    @Test
    public void testDeleteNotExist() {
        // 存在しないuseridを指定した場合
        try {
            client.user().delete(Arrays.asList("999999"));
            //fail(); // エラーは発生しない
        } catch (Exception ignore) {
            log.trace(ignore.getMessage());
        }
    }

}
