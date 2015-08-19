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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.zabbix.ZabbixAccessor;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupCreateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupGetParam;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupUpdateParam;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * <p>
 * Zabbix APIのHostgroupを操作するためのクラスです。
 * </p>
 *
 */
public class HostgroupClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public HostgroupClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * ホストグループ情報を取得します。<br/>
     * ホストグループ情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link HostgroupGetParam}
     * @return 取得したホストグループ情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Hostgroup> get(HostgroupGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONArray result = (JSONArray) accessor.execute("hostgroup.get", params);

        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(Hostgroup.class);
        return (List<Hostgroup>) JSONArray.toCollection(result, config);
    }

    /**
     * ホストグループ情報を作成します。<br/>
     * nameパラメータを必ず指定する必要があります。<br/>
     * 既に存在するホストのnameを指定した場合、例外をスローします。
     *
     * @param param {@link HostgroupCreateParam}
     * @return 作成されたホストグループ情報のhostgroupidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> create(HostgroupCreateParam param) {
        if (param.getName() == null || param.getName().length() == 0) {
            throw new IllegalArgumentException("name is required.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("hostgroup.create", params);

        JSONArray hostgroupids = result.getJSONArray("groupids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(hostgroupids, config);
    }

    /**
     * ホストグループ情報を更新します。<br/>
     * groupidパラメータを必ず指定する必要があります。<br/>
     * 存在しないgroupidを指定した場合、例外をスローします。<br/>
     *
     * @param param {@link HostgroupUpdateParam}
     * @return 更新したホストグループ情報のhostgroupidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> update(HostgroupUpdateParam param) {
        if (param.getGroupid() == null) {
            throw new IllegalArgumentException("groupid is required.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("hostgroup.update", params);

        JSONArray hostgroupids = result.getJSONArray("groupids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(hostgroupids, config);
    }

    /**
     * ホストグループ情報を削除します。<br/>
     * groupidパラメータを必ず指定する必要があります。<br/>
     * 存在しないgroupidを指定した場合、例外をスローします。<br/>
     *
     * @param groupids 削除するホストグループのIDのリスト
     * @return 削除したホストグループ情報のhostgroupidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> delete(List<String> groupids) {
        if (groupids == null || groupids.isEmpty()) {
            throw new IllegalArgumentException("groupid is required.");
        }

        JSONArray params;
        if (accessor.checkVersion("2.0") < 0) {
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (String groupid : groupids) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("groupid", groupid);
                list.add(map);
            }
            params = JSONArray.fromObject(list, defaultConfig);
        } else {
            params = JSONArray.fromObject(groupids, defaultConfig);
        }

        JSONObject result = (JSONObject) accessor.execute("hostgroup.delete", params);

        JSONArray hostgroupids = result.getJSONArray("groupids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(hostgroupids, config);
    }
}
