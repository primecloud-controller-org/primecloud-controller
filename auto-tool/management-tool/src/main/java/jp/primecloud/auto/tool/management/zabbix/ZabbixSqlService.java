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

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.tool.management.db.SQLExecuter;
import jp.primecloud.auto.tool.management.db.SQLExecuterFactory;

public class ZabbixSqlService {

    protected static Log log = LogFactory.getLog(ZabbixSqlService.class);

    private SQLExecuter sqlExecuter;

    public ZabbixSqlService() {
        if (sqlExecuter == null) {
            sqlExecuter = new SQLExecuterFactory().createZabbixExecuter();
        }
    }

    public void createUsergroup(String userid, String username) throws SQLException, Exception {
        try {
            String sql1 = "update ids set nextid=nextid+1 where nodeid=0 and table_name='usrgrp' and field_name ='usrgrpid'";
            sqlExecuter.execute(sql1);
            String sql2 = "select nextid from ids where nodeid=0 and table_name='usrgrp' and field_name ='usrgrpid'";
            int usrgrpid = sqlExecuter.getNextid(sql2);
            String sql3 = "insert into usrgrp (usrgrpid, name, gui_access, users_status, api_access, debug_mode) values ("
                    + usrgrpid + ", '" + username + "', 0, 0, 0, 0)";
            sqlExecuter.execute(sql3);

            String sql4 = "update ids set nextid=nextid+1 where nodeid=0 and table_name='users_groups' and field_name ='id'";
            sqlExecuter.execute(sql4);

            String sql5 = "select nextid from ids where nodeid=0 and table_name='users_groups' and field_name ='id'";
            int id = sqlExecuter.getNextid(sql5);

            String sql6 = "insert into users_groups (id,usrgrpid,userid) values (" + id + "," + usrgrpid + "," + userid
                    + ")";
            sqlExecuter.execute(sql6);

            String sql7 = "select groupid from groups where name ='" + username + "'";
            int groupid = sqlExecuter.getGroupid(sql7);

            String sql8 = "update ids set nextid=nextid+1 where nodeid=0 and table_name='rights' and field_name ='rightid'";
            sqlExecuter.execute(sql8);

            String sql9 = "select nextid from ids where nodeid=0 and table_name='rights' and field_name ='rightid'";
            int rightid = sqlExecuter.getNextid(sql9);

            //groupidがusrgrpのusrgrpid
            //IDがgroupsのgroupid
            String sql10 = "insert into rights (rightid,groupid,permission,id) values (" + rightid + ", " + usrgrpid
                    + ", 2, " + groupid + ")";
            sqlExecuter.execute(sql10);

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }

    }

    public void updateUsergroup(String username, int enableFlag) throws SQLException, Exception {
        //status 0:enable 1:disable
        String updateUsergroup = "update usrgrp SET users_status =" + enableFlag + " where name ='" + username + "'";
        try {
            sqlExecuter.execute(updateUsergroup);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public String getUser(String username) throws SQLException, Exception {

        String getUserSql = "select alias from users where alias='" + username + "'";
        String nameResult = "";
        try {
            Object result = sqlExecuter.getColumn(getUserSql, "alias", "string");
            if (result != null) {
                nameResult = result.toString();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
        return nameResult;
    }

    /**
     * sqlExecuterを取得します。
     *
     * @return sqlExecuter
     */
    public SQLExecuter getSqlExecuter() {
        return sqlExecuter;
    }

    /**
     * sqlExecuterを設定します。
     *
     * @param sqlExecuter sqlExecuter
     */
    public void setSqlExecuter(SQLExecuter sqlExecuter) {
        this.sqlExecuter = sqlExecuter;
    }

}
