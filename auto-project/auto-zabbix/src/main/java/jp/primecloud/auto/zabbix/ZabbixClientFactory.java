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
package jp.primecloud.auto.zabbix;

import org.apache.commons.httpclient.HttpClient;

/**
 * <p>
 * ZabbixClientインスタンスを生成するためのFactoryクラスです。
 * </p>
 *
 */
public class ZabbixClientFactory {

    protected String url;

    /**
     * ZabbixClientのインスタンスを生成します。
     *
     * @param username
     * @param password
     * @return ZabbixClient
     */
    public ZabbixClient createClient(String username, String password) {
        ZabbixAccessor accessor = createAccessor(username, password);
        return new ZabbixClient(accessor);
    }

    protected ZabbixAccessor createAccessor(String username, String password) {
        HttpClient httpClient = createHttpClient();
        String apiUrl = url + "api_jsonrpc.php";
        return ZabbixAccessor.getInstance(httpClient, apiUrl, username, password);
    }

    protected HttpClient createHttpClient() {
        return new HttpClient();
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
