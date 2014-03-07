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
package jp.primecloud.auto.tool.management.db;

import jp.primecloud.auto.config.Config;

public class SQLExecuterFactory {

    private String DB;

    private String DBUsername;

    private String DBPassword;

    public SQLExecuter createPCCSQLExecuter() {
        setDB(Config.getProperty("db.url"));
        setDBUsername(Config.getProperty("db.username"));
        setDBPassword(Config.getProperty("db.password"));
        return new SQLExecuter(DB, DBUsername, DBPassword);
    }

    public SQLExecuter createZabbixExecuter() {
        setDB(Config.getProperty("ZABBIX_DB"));
        setDBUsername(Config.getProperty("ZABBIX_DB_USER"));
        setDBPassword(Config.getProperty("ZABBIX_DB_PASSWORD"));
        return new SQLExecuter(DB, DBUsername, DBPassword);

    }

    /**
     * dBを取得します。
     *
     * @return dB
     */
    public String getDB() {
        return DB;
    }

    /**
     * dBを設定します。
     *
     * @param dB dB
     */
    public void setDB(String dB) {
        DB = dB;
    }

    /**
     * dBUsernameを取得します。
     *
     * @return dBUsername
     */
    public String getDBUsername() {
        return DBUsername;
    }

    /**
     * dBUsernameを設定します。
     *
     * @param dBUsername dBUsername
     */
    public void setDBUsername(String dBUsername) {
        DBUsername = dBUsername;
    }

    /**
     * dBPasswordを取得します。
     *
     * @return dBPassword
     */
    public String getDBPassword() {
        return DBPassword;
    }

    /**
     * dBPasswordを設定します。
     *
     * @param dBPassword dBPassword
     */
    public void setDBPassword(String dBPassword) {
        DBPassword = dBPassword;
    }
}
