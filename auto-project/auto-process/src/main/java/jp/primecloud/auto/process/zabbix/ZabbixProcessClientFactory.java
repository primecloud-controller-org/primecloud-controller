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
package jp.primecloud.auto.process.zabbix;

import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.ZabbixClientFactory;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ZabbixProcessClientFactory {

    private String url;

    private String username;

    private String password;

    public ZabbixProcessClient createZabbixProcessClient() {
        ZabbixClientFactory factory = new ZabbixClientFactory();
        factory.setUrl(url);
        ZabbixClient zabbixClient = factory.createClient(username, password);

        ZabbixProcessClient client = new ZabbixProcessClient(zabbixClient);
        return client;
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * usernameを設定します。
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * passwordを設定します。
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
