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
import jp.primecloud.auto.zabbix.model.user.User;
import jp.primecloud.auto.zabbix.model.user.UserAuthenticateParam;
import jp.primecloud.auto.zabbix.model.user.UserCreateParam;
import jp.primecloud.auto.zabbix.model.user.UserGetParam;
import jp.primecloud.auto.zabbix.model.user.UserUpdateParam;
import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * <p>
 * Zabbix APIのUserを操作するためのクラスです。
 * </p>
 *
 */
public class UserClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public UserClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * ユーザ情報を取得します。<br/>
     * ユーザ情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link UserGetParam}
     * @return 取得したユーザ情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<User> get(UserGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        if (accessor.checkVersion("2.0") < 0) {
            if (params.containsKey("selectUsrgrps")) {
                params.put("select_usrgrps", params.remove("selectUsrgrps"));
            }
        }

        JSONArray result = (JSONArray) accessor.execute("user.get", params);

        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(User.class);
        config.getClassMap().put("usrgrps", Usergroup.class);
        return (List<User>) JSONArray.toCollection(result, config);
    }

    /**
     * Zabbixサーバへ認証を行います。<br/>
     * 認証情報が正しくない場合、例外をスローします。
     *
     * @param param {@link UserAuthenticateParam}
     * @return セッションID
     */
    public String login(UserAuthenticateParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        return (String) accessor.execute("user.login", params);
    }

    /**
     * ユーザ情報を作成します。<br/>
     * aliasパラメータを必ず指定する必要があります。<br/>
     * 既に存在するユーザのaliasを指定した場合、例外をスローします。
     *
     * @param param {@link UserCreateParam}
     * @return 作成されたユーザ情報のuseridのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> create(UserCreateParam param) {
        if (param.getAlias() == null || param.getAlias().length() == 0) {
            throw new IllegalArgumentException("alias is required.");
        }
        if (accessor.checkVersion("2.0") >= 0) {
            if (param.getPasswd() == null || param.getPasswd().length() == 0) {
                throw new IllegalArgumentException("passwd is required.");
            }

            if (param.getUsrgrps() == null || param.getUsrgrps().isEmpty()) {
                throw new IllegalArgumentException("usrgrps is required.");
            }
        }

        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("user.create", params);

        JSONArray userids = result.getJSONArray("userids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(userids, config);
    }

    /**
     * ユーザ情報を更新します。<br/>
     * useridパラメータを必ず指定する必要があります。<br/>
     * 存在しないuseridを指定した場合、例外をスローします。<br/>
     * 既に存在するユーザのaliasを指定した場合、例外をスローします。
     *
     * @param param {@link UserUpdateParam}
     * @return 更新したユーザ情報のuseridのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> update(UserUpdateParam param) {
        if (param.getUserid() == null) {
            throw new IllegalArgumentException("userid is required.");
        }
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        JSONObject result = (JSONObject) accessor.execute("user.update", params);

        JSONArray userids = result.getJSONArray("userids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(userids, config);
    }

    /**
     * ユーザ情報を削除します。<br/>
     * useridパラメータを必ず指定する必要があります。<br/>
     * 存在しないuseridを指定した場合、空のリストを返します。
     *
     * @param userid userid
     * @return 削除したユーザ情報のuseridのリスト
     */
    @SuppressWarnings("unchecked")
    public List<String> delete(List<String> userids) {
        if (userids == null || userids.isEmpty()) {
            throw new IllegalArgumentException("userid is required.");
        }

        JSONArray params;
        if (accessor.checkVersion("2.0") < 0) {
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (String userid : userids) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", userid);
                list.add(map);
            }
            params = JSONArray.fromObject(list, defaultConfig);
        } else {
            params = JSONArray.fromObject(userids, defaultConfig);
        }

        JSONObject result = (JSONObject) accessor.execute("user.delete", params);

        JSONArray resultUserids = result.getJSONArray("userids");
        JsonConfig config = defaultConfig.copy();
        config.setCollectionType(List.class);
        config.setRootClass(String.class);
        return (List<String>) JSONArray.toCollection(resultUserids, config);
    }

}
