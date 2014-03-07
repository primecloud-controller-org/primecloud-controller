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
import jp.primecloud.auto.zabbix.model.host.Host;
import jp.primecloud.auto.zabbix.model.host.HostCreateParam;
import jp.primecloud.auto.zabbix.model.host.HostGetParam;
import jp.primecloud.auto.zabbix.model.host.HostUpdateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.maintenance.Maintenance;
import jp.primecloud.auto.zabbix.model.template.Template;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * <p>
 * Zabbix APIのHostを操作するためのクラスです。
 * </p>
 *
 */
public class HostClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public HostClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * ホスト情報を取得します。<br/>
     * ホスト情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link HostGetParam}
     * @return 取得したホスト情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Host> get(HostGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONArray result = (JSONArray) accessor.execute("host.get", params);

        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(Host.class);
        config.getClassMap().put("parenttemplates", Template.class);
        config.getClassMap().put("groups", Hostgroup.class);
        config.getClassMap().put("maintenances", Maintenance.class);
        return (List<Host>) JSONArray.toCollection(result, config);
    }

    /**
     * ホスト情報を作成します。<br/>
     * host,groupsパラメータを必ず指定する必要があります。<br/>
     * 既に存在するホストのhost,groupsを指定した場合、例外をスローします。
     *
     * @param param {@link HostCreateParam}
     * @return 作成されたホスト情報のhostidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> create(HostCreateParam param) {
        if (param.getHost() == null || param.getHost().length() == 0) {
            throw new IllegalArgumentException("host is required.");
        }
        if (param.getGroups() == null || param.getGroups().size() == 0) {
            throw new IllegalArgumentException("groups is required.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("host.create", params);

        JSONArray hostids = result.getJSONArray("hostids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(hostids, config);
    }

    /**
     * ホスト情報を更新します。<br/>
     * hostidパラメータを必ず指定する必要があります。<br/>
     * 存在しないhostidを指定した場合、例外をスローします。<br/>
     * groupsに空リストを指定した場合、例外をスローします。
     *
     * @param param {@link HostUpdateParam}
     * @return 更新したホスト情報のhostidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> update(HostUpdateParam param) {
        if (param.getHostid() == null) {
            throw new IllegalArgumentException("hostid is required.");
        }
        if (param.getGroups() != null && param.getGroups().size() == 0) {
            throw new IllegalArgumentException("groups must be null or not empty.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("host.update", params);

        JSONArray hostids = result.getJSONArray("hostids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(hostids, config);
    }

    /**
     * ホスト情報を削除します。<br/>
     * hostidパラメータを必ず指定する必要があります。<br/>
     * 存在しないhostidを指定した場合、例外をスローします。<br/>
     *
     * @param hostids 削除するホストIDのリスト
     * @return 削除したホスト情報のhostidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> delete(List<String> hostids) {
        if (hostids == null || hostids.isEmpty()) {
            throw new IllegalArgumentException("hostid is required.");
        }

        List<Map<String, String>> list = new ArrayList<Map<String,String>>();
        for (String hostid: hostids) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("hostid", hostid);
            list.add(map);
        }
        //JSONObject params = JSONObject.fromObject(list, defaultConfig);
        JSONArray params = JSONArray.fromObject(list, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("host.delete", params);

        JSONArray resultHostids = result.getJSONArray("hostids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(resultHostids, config);
    }

}
