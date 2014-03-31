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

import jp.primecloud.auto.zabbix.ZabbixAccessor;
import jp.primecloud.auto.zabbix.model.application.Application;
import jp.primecloud.auto.zabbix.model.application.ApplicationGetParam;
import jp.primecloud.auto.zabbix.model.host.Host;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * <p>
 * Zabbix APIのApplicationを操作するためのクラスです。
 * </p>
 *
 */
public class ApplicationClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public ApplicationClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * アプリケーション情報を取得します。<br/>
     * アプリケーション情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link ApplicationGetParam}
     * @return 取得したアプリケーション情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Application> get(ApplicationGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);

        JSONArray result = (JSONArray) accessor.execute("application.get", params);

        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(Application.class);
        config.getClassMap().put("hosts", Host.class);

        return (List<Application>) JSONArray.toCollection(result, config);
    }

}
