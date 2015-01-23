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

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import jp.primecloud.auto.zabbix.ZabbixAccessor;
import jp.primecloud.auto.zabbix.model.proxy.Proxy;
import jp.primecloud.auto.zabbix.model.proxy.ProxyGetParam;

/**
 * <p>
 * Zabbix APIのProxyを操作するためのクラスです。
 * </p>
 *
 */
public class ProxyClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public ProxyClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * プロキシ情報を取得します。<br/>
     * プロキシ情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link ProxyGetParam}
     * @return 取得したプロキシ情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Proxy> get(ProxyGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONArray result = (JSONArray) accessor.execute("proxy.get", params);

        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(Proxy.class);
        return (List<Proxy>) JSONArray.toCollection(result, config);
    }
}
