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
package jp.primecloud.auto.tool.management.main;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.tool.management.db.SQLExecuter;
import jp.primecloud.auto.tool.management.zabbix.ZabbixScriptService;
import jp.primecloud.auto.tool.management.zabbix.ZabbixSqlService;
import jp.primecloud.auto.zabbix.model.user.User;
import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;

public class ZabbixMain {

    protected static Log log = LogFactory.getLog(ZabbixMain.class);

    public static void createExecute(CommandLine commandLine) {
        String username = commandLine.getOptionValue("username");
        String password = commandLine.getOptionValue("password");
        String firstname = commandLine.getOptionValue("firstname");
        String familyname = commandLine.getOptionValue("familyname");

        try {
        ZabbixScriptService zabbixScriptService = new ZabbixScriptService();
            List<String> usrgrpids = zabbixScriptService.createUserGroup(username);
            List<Usergroup> usergroups = zabbixScriptService.getUserGroup(usrgrpids);
            List<String> userids = zabbixScriptService.createUser(username, familyname, firstname, password, usergroups);
            List<String> hostgroupids = zabbixScriptService.createHostGroup(username);
            zabbixScriptService.massAddUserGroup(usrgrpids, userids, hostgroupids.get(0));
            log.info(username + " ユーザーを作成しました");
        } catch (Exception e) {
            log.error(username + " ユーザーの作成に失敗しました", e);
            System.out.println(username + " ユーザーの作成に失敗しました");
        }
    }

    public static void updateExecute(CommandLine commandLine) {
        String username = commandLine.getOptionValue("username");
        String password = commandLine.getOptionValue("password");

        //ZabbixのAPI用
        try {
        ZabbixScriptService zabbixScriptService = new ZabbixScriptService();
            List<User> users = zabbixScriptService.getUsers();
            String userid = "";
            for (User user : users) {
                if (user.getAlias().equals(username.toString())) {
                    userid = user.getUserid();
                }
            }
            if (StringUtils.isEmpty(userid)) {
                System.out.println(username + "はZabbixに存在しません");
                log.error(username + "はZabbixに存在しません");
                return;
            } else {
                zabbixScriptService.updateUser(userid, password);
            }
            log.info(username + " ユーザーのパスワードを更新しました。");

        } catch (Exception e) {
            log.error(username + " パスワードの変更に失敗しました", e);
            System.out.println(username + " パスワードの変更に失敗しました");
        }

    }

    public static void disableExecute(CommandLine commandLine) {
        String username = commandLine.getOptionValue("username");

        try {
            ZabbixScriptService zabbixScriptService = new ZabbixScriptService();
            List<Usergroup> usergroups = zabbixScriptService.getUserGroup(username);
            zabbixScriptService.updateUserGroup(usergroups.get(0).getUsrgrpid(), false);
            log.info(username + "を無効化しました。");
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
    }

    public static void enableExecute(CommandLine commandLine) {
        String username = commandLine.getOptionValue("username");

        try {
            ZabbixScriptService zabbixScriptService = new ZabbixScriptService();
            List<Usergroup> usergroups = zabbixScriptService.getUserGroup(username);
            zabbixScriptService.updateUserGroup(usergroups.get(0).getUsrgrpid(), true);
            log.info(username + "を有効化しました。");
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
    }

    public static void getUser(CommandLine commandLine) {
        String username = commandLine.getOptionValue("username");

        try {
            ZabbixScriptService zabbixScriptService = new ZabbixScriptService();
            String user = zabbixScriptService.getUser(username);
            if (StringUtils.isNotEmpty(user)) {
                System.out.println(user);
            } else {
                System.out.println("NULL");
            }
        } catch (Exception e) {
            log.error(username + " ユーザーが見つかりません", e);
            System.out.println(username + " ユーザーが見つかりません");
        }
    }

    public static void selectExecute(CommandLine commandLine) {
        ZabbixSqlService zabbixSqlService = new ZabbixSqlService();
        SQLExecuter sqlExecuter = zabbixSqlService.getSqlExecuter();
        String sql = commandLine.getOptionValue("sql");

        if (commandLine.hasOption("columntype") && commandLine.hasOption("columnname")) {
            String columnName = commandLine.getOptionValue("columnname");
            String columnType = commandLine.getOptionValue("columntype");

            try {
                Object result = sqlExecuter.getColumn(sql, columnName, columnType);
                if (result == null) {
                    System.out.println("NULL");
                } else {
                    System.out.print(result.toString());
                }
            } catch (Exception e) {
                log.error("[" + sql + "] の実行に失敗しました", e);
                System.out.println("[" + sql + "] の実行に失敗しました");
                return;
            }
        } else {
            try {
                List<List<Object>> results = sqlExecuter.showColumn(sql);
                for (List<Object> columns : results) {
                    for (Object object : columns) {
                        if (object == null) {
                            object = "NULL";
                        }
                        System.out.print(object.toString() + " ");
                    }
                    System.out.println();
                }
            } catch (Exception e) {
                log.error("[" + sql + "] の実行に失敗しました", e);
                System.out.println("[" + sql + "] の実行に失敗しました");
                return;
            }
        }
    }

    public static void checkApiVersion() {
        String version = "";
        try {
            ZabbixScriptService zabbixScriptService = new ZabbixScriptService();
            version = zabbixScriptService.getApiVersion();
            log.info("ZABBIXのAPIチェックを実行しました version:" + version);
        } catch (Exception e) {
            log.error("ZABBIXのAPIチェックに失敗しました", e);
            System.out.println("NULL");
        }
    }

}
