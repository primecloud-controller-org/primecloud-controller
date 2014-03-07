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
import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupCreateParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupGetParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupMassAddParam;
import jp.primecloud.auto.zabbix.model.usergroup.UsergroupUpdateParam;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * <p>
 * Zabbix APIのUsergroupを操作するためのクラスです。
 * </p>
 *
 */
public class UsergroupClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public UsergroupClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * ユーザーグループ情報を取得します。<br/>
     * ユーザーグループ情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link UsergroupGetParam}
     * @return 取得したユーザーグループ情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Usergroup> get(UsergroupGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONArray result = (JSONArray) accessor.execute("usergroup.get", params);

        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(Usergroup.class);
        return (List<Usergroup>) JSONArray.toCollection(result, config);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> create(UsergroupCreateParam param) {
        if (param.getName() == null || param.getName().length() == 0) {
            throw new IllegalArgumentException("name is required.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("usergroup.create", params);

        JSONArray usrgrpids = result.getJSONArray("usrgrpids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(usrgrpids, config);
    }

    /**
     * ユーザグループ情報とユーザ情報、権限情報を紐付けます。
     * userids,usrgrpidsパラメータを必ず指定する必要があります。<br/>
     * @param param {@link UsergroupMassAddParam}
     * @return 更新されたユーザグループ情報のusrgrpidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> massAdd(UsergroupMassAddParam param) {
        if (param.getUserids() == null || param.getUserids().isEmpty()) {
            throw new IllegalArgumentException("userids is required.");
        }
        if (param.getUsrgrpids() == null || param.getUsrgrpids().isEmpty()) {
            throw new IllegalArgumentException("usrgrpids is required.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("usergroup.massAdd", params);

        JSONArray usrgrpids = result.getJSONArray("usrgrpids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(usrgrpids, config);
    }

    /**
     * ユーザグループ情報を更新します。
     * usrgrpidパラメータを必ず指定する必要があります。<br/>
     * @param param {@link UsergroupUpdateParam}
     * @return 更新されたユーザグループ情報のusrgrpidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> update(UsergroupUpdateParam param) {
        if (param.getUsrgrpid() == null || param.getUsrgrpid().length() == 0) {
            throw new IllegalArgumentException("usrgrpid is required.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("usergroup.update", params);

        JSONArray usrgrpids = result.getJSONArray("usrgrpids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(usrgrpids, config);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> delete(List<String> groupids) {
        if (groupids == null || groupids.isEmpty()) {
            throw new IllegalArgumentException("groupid is required.");
    }
        JSONArray params = JSONArray.fromObject(groupids, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("usergroup.delete", params);

        JSONArray usrgrpids = result.getJSONArray("usrgrpids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(usrgrpids, config);
    }
}
