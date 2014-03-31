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
import jp.primecloud.auto.zabbix.model.item.Item;
import jp.primecloud.auto.zabbix.model.item.ItemGetParam;
import jp.primecloud.auto.zabbix.model.item.ItemUpdateParam;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;


/**
 * <p>
 * Zabbix APIのItemを操作するためのクラスです。
 * </p>
 *
 */
public class ItemClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public ItemClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * アイテム情報を取得します。<br/>
     * アイテム情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link ItemGetParam}
     * @return 取得したアイテム情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Item> get(ItemGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONArray result = (JSONArray) accessor.execute("item.get", params);

        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(Item.class);
        config.setJavaPropertyFilter(new PropertyFilter() {
            @Override
            public boolean apply(Object source, String name, Object value) {
                if ("hosts".equals(name)) {
                    return true;
                }
                return false;
            }
        });

        return (List<Item>) JSONArray.toCollection(result, config);
    }

    /**
     * アイテム情報を更新します。<br/>
     * itemidパラメータを必ず指定する必要があります。
     *
     * @param param {@link ItemUpdateParam}
     * @return 更新したアイテム情報のitemidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> update(ItemUpdateParam param) {
        if (param.getItemid() == null) {
            throw new IllegalArgumentException("itemid is required.");
        }

        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("item.update", params);
        JSONArray itemids = result.getJSONArray("itemids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);

        return (List<String>) JSONArray.toCollection(itemids, config);
    }

    /**
     * アイテム情報を削除します。<br/>
     * itemidsパラメータを必ず指定する必要があります。<br/>
     * 存在しないitemidを指定した場合、例外をスローします。
     *
     * @param itemids itemids
     * @return 削除したアイテム情報のitemidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> delete(List<String> itemids) {
        if (itemids == null || itemids.isEmpty()) {
            throw new IllegalArgumentException("itemid is required.");
        }

        JSONArray params = JSONArray.fromObject(itemids, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("item.delete", params);

        JSONArray retItemids = result.getJSONArray("itemids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);

        return (List<String>) JSONArray.toCollection(retItemids, config);
    }
}
