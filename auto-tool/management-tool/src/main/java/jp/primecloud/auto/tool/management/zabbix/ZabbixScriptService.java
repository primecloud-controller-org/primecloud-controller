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
package jp.primecloud.auto.tool.management.zabbix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupCreateParam;
import jp.primecloud.auto.zabbix.model.right.Right;
import jp.primecloud.auto.zabbix.model.user.User;
import jp.primecloud.auto.zabbix.model.user.UserCreateParam;
import jp.primecloud.auto.zabbix.model.user.UserGetParam;
import jp.primecloud.auto.zabbix.model.user.UserUpdateParam;
import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupCreateParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupGetParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupMassAddParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupUpdateParam;

public class ZabbixScriptService {

    protected ZabbixClient zabbixClient;

    //Zabbixの情報
    private static String zabbixUrl = Config.getProperty("zabbix.url");

    private static String zabbixUsername = Config.getProperty("zabbix.username");

    private static String zabbixPassword = Config.getProperty("zabbix.password");

    public ZabbixScriptService(String url, String username, String password) {
        ZabbixClientFactory factory = new ZabbixClientFactory();
        factory.setUrl(url);
        zabbixClient = factory.createClient(username, password);
    }

    public ZabbixScriptService() {
        this(zabbixUrl, zabbixUsername, zabbixPassword);
    }

    public List<String> createUser(String alias, String surname, String name, String password, List<Usergroup> usergroups) {
        UserCreateParam userCreateParam = new UserCreateParam();
        userCreateParam.setAlias(alias);
        userCreateParam.setName(name);
        userCreateParam.setSurname(surname);
        userCreateParam.setPasswd(password);
        userCreateParam.setUrl("overview.php");
        userCreateParam.setUsrgrps(usergroups);

        if (zabbixClient.checkVersion("2.0.0") < 0) {
            userCreateParam.setLang("ja_jp");
        } else {
            userCreateParam.setLang("ja_JP");
        }

        List<String> users = zabbixClient.user().create(userCreateParam);
       
        return users;
    }

    public List<User> getUsers() {
        UserGetParam param = new UserGetParam();
        param.setOutput("extend");
        List<User> users = zabbixClient.user().get(param);
        return users;
    }

    public String getUser(String username) {
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getName().equals(username)) {
                return user.getName();
            }
        }
        return "";
    }

    public List<String> updateUser(String userid, String passwd) {
        UserUpdateParam param = new UserUpdateParam();
        param.setUserid(userid);
        param.setPasswd(passwd);
        return zabbixClient.user().update(param);
    }

    public List<String> deleteUser(String userid) {
        return zabbixClient.user().delete(Arrays.asList(userid));
    }

    public List<String> createHostGroup(String name) {
        HostgroupCreateParam param = new HostgroupCreateParam();
        param.setName(name);
        return zabbixClient.hostgroup().create(param);
    }

    public List<Usergroup> getUserGroup(List<String> usrgrpids) {
        UsergroupGetParam usergroupGetParam = new UsergroupGetParam();
        usergroupGetParam.setUsrgrpids(usrgrpids);
        return zabbixClient.usergroup().get(usergroupGetParam);
    }

    public List<String> createUserGroup(String name) {
        UsergroupCreateParam param = new UsergroupCreateParam();
        param.setName(name);
        return zabbixClient.usergroup().create(param);
    }

    public List<String> massAddUserGroup(List<String> usrgrpids, List<String> userids, String hostgroupid) {
        UsergroupMassAddParam param = new UsergroupMassAddParam();
        param.setUsrgrpids(usrgrpids);
        param.setUserids(userids);
        Right right = new Right();
        right.setId(hostgroupid);
        right.setPermission("2");
        param.setRights(Arrays.asList(right));
        return zabbixClient.usergroup().massAdd(param);
    }

    public List<String> updateUserGroup(String usrgrpid, Boolean status) {
        UsergroupUpdateParam param = new UsergroupUpdateParam();
        param.setUsrgrpid(usrgrpid);
        if (status != null) {
            param.setUsersStatus(status ? 0 : 1); // 有効の場合は0、無効の場合は1
        }
        return zabbixClient.usergroup().update(param);
    }

    public List<Usergroup> getUserGroup(String name) {
        UsergroupGetParam param = new UsergroupGetParam();
        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("name", Arrays.asList((Object)name));
        param.setFilter(filter);
        param.setOutput("extend");
        return zabbixClient.usergroup().get(param);
    }

    public String getApiVersion() {
        return zabbixClient.APIInfo().version();
    }
}
