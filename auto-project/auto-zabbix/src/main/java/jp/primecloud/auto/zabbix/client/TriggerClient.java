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

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.zabbix.ZabbixAccessor;
import jp.primecloud.auto.zabbix.model.trigger.Trigger;
import jp.primecloud.auto.zabbix.model.trigger.TriggerGetParam;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;


/**
 * <p>
 * Zabbix APIのTriggerを操作するためのクラスです。
 * </p>
 *
 */
public class TriggerClient {
    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public TriggerClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * トリガー情報を取得します。<br/>
     * トリガー情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link TriggerGetParam}
     * @return 取得したトリガー情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Trigger> get(TriggerGetParam param) {
        //2012.10.24 現在、PCCでは使用していない
//        JSONObject params = JSONObject.fromObject(param, defaultConfig);
//        JSONArray result = (JSONArray) accessor.execute("trigger.get", params);
//
//        JsonConfig config = defaultConfig.copy();
//        config.setCollectionType(List.class);
//        config.setRootClass(Trigger.class);
//        config.setJavaPropertyFilter(new PropertyFilter() {
//            @Override
//            public boolean apply(Object source, String name, Object value) {
//                if ("hosts".equals(name)) {
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        return (List<Trigger>) JSONArray.toCollection(result, config);
        return new ArrayList<Trigger>();
    }
}
