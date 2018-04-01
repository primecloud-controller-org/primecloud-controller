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
     * ユーザグループ情報を作成します。<br/>
     * nameパラメータを必ず指定する必要があります。<br/>
     * 既に存在するユーザグループのnameを指定した場合、例外をスローします。
     *
     * @param param {@link UsergroupCreateParam}
     * @return 作成されたユーザグループ情報のusrgrpidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> create(UsergroupCreateParam param) {
        if (param.getName() == null || param.getName().length() == 0) {
            throw new IllegalArgumentException("name is required.");
        }

        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        if (accessor.checkVersion("2.0") >= 0) {
            // api_accessは2.0以降で廃止されたパラメータ
            if (params.containsKey("api_access")) {
                params.remove("api_access");
            }
        }

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

        // Zabbix 2.2.9でusrgrpidsが数値のArrayとして返ってくることへの対応
        List<?> ids = (List<?>) JSONArray.toCollection(usrgrpids, config);
        List<String> resultIds = new ArrayList<String>();
        for (Object id : ids) {
            resultIds.add(id.toString());
        }
        return resultIds;
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
        if (accessor.checkVersion("2.0") >= 0) {
            // api_accessは2.0以降で廃止されたパラメータ
            if (params.containsKey("api_access")) {
                params.remove("api_access");
            }
        }

        JSONObject result = (JSONObject) accessor.execute("usergroup.update", params);

        JSONArray usrgrpids = result.getJSONArray("usrgrpids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(usrgrpids, config);
    }

    /**
     * ユーザグループ情報を削除します。<br/>
     * usrgrpidsパラメータを必ず指定する必要があります。<br/>
     * 存在しないusrgrpidを指定した場合、空のリストを返します。
     *
     * @param usrgrpids usrgrpids
     * @return 削除したユーザグループ情報のusrgrpidのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> delete(List<String> usrgrpids) {
        if (usrgrpids == null || usrgrpids.isEmpty()) {
            throw new IllegalArgumentException("usrgrpid is required.");
        }
        JSONArray params = JSONArray.fromObject(usrgrpids, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("usergroup.delete", params);

        JSONArray resultUsrgrpids = result.getJSONArray("usrgrpids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(resultUsrgrpids, config);
    }
}
