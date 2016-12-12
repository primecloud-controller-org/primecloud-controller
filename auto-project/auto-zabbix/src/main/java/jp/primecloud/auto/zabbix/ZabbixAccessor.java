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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.zabbix.model.ResponseError;
import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * ZabbixのAPIにアクセスするためのAccessorクラスです。
 * </p>
 *
 */
public class ZabbixAccessor {

    private Log log = LogFactory.getLog(getClass());

    private static ZabbixAccessor zAccessor = new ZabbixAccessor();

    protected static List<String> ignoreAuthMethods;

    protected HttpClient httpClient;

    protected String apiUrl;

    protected String username;

    protected String password;

    private String auth;

    private int id;

    private String apiVersion;

    static {
        ignoreAuthMethods = new ArrayList<String>();
        ignoreAuthMethods.add("apiinfo.version");
        ignoreAuthMethods.add("user.login");
    }

    private ZabbixAccessor(){}

    public static ZabbixAccessor getInstance(HttpClient httpClient, String apiUrl, String username, String password) {
        zAccessor.init(httpClient, apiUrl, username, password);
        return zAccessor;
    }

    public void init(HttpClient httpClient, String apiUrl, String username, String password) {
        //設定されていなければ設定する
        if (this.httpClient == null){
            this.httpClient = httpClient;
        }
        if (this.apiUrl == null){
            this.apiUrl = apiUrl;
        }
        if (this.username == null){
            this.username = username;
        }
        if (this.password == null) {
            this.password = password;
        }
    }

    public synchronized Object execute(String method, JSON params) {
        // authが空の場合、認証する
        if (!ignoreAuthMethods.contains(method) && StringUtils.isEmpty(auth)) {
            authenticate();
        }

        Map<String, Object> request = new HashMap<String, Object>();
        request.put("jsonrpc", "2.0");
        request.put("method", method);
        request.put("params", params == null ? Collections.EMPTY_MAP : params);
        request.put("id", ++id);

        if (!ignoreAuthMethods.contains(method)) {
            request.put("auth", auth == null ? "" : auth);
        }

        String jsonRequest = JSONObject.fromObject(request).toString();
        if (log.isDebugEnabled()) {
            // パスワードのマスク化
            String str = jsonRequest;
            if (str.contains("password")) {
                str = str.replaceAll("\"password\":\".*?\"", "\"password\":\"--------\"");
            }
            if (str.contains("passwd")) {
                str = str.replaceAll("\"passwd\":\".*?\"", "\"passwd\":\"--------\"");
            }

            log.debug(str);
        }

        String jsonResponse = post(jsonRequest);
        if (log.isDebugEnabled()) {
            log.debug(jsonResponse);
        }

        JSONObject response = JSONObject.fromObject(jsonResponse);

        if (response.containsKey("error")) {
            ResponseError error = (ResponseError) JSONObject.toBean(response.getJSONObject("error"),
                    ResponseError.class);

            // 認証されていない場合、再度認証して実行する
            if ("Not authorized".equals(error.getData())) {
                auth = "";
                return execute(method, params);
            }

            // エラー発生時
            AutoException exception = new AutoException("EZABBIX-000001", method);
            exception.addDetailInfo("params=" + params);
            exception.addDetailInfo("error="
                    + ReflectionToStringBuilder.toString(error, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        return response.get("result");
    }

    protected void authenticate() {
        JSONObject params = new JSONObject();
        params.put("user", username);
        params.put("password", password);
        Object result = execute("user.login", params);
        auth = (String) result;
    }

    protected String post(String jsonRequest) {
        PostMethod postMethod = new PostMethod(apiUrl);
        postMethod.setRequestHeader("Content-Type", "application/json-rpc");
        postMethod.setRequestEntity(new ByteArrayRequestEntity(jsonRequest.getBytes()));

        int status;
        try {
            status = httpClient.executeMethod(postMethod);
        } catch (HttpException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (status < 200 || status >= 300) {
            String body;
            try {
                body = postMethod.getResponseBodyAsString();
            } catch (Exception ignore) {
                body = "";
            }
            throw new RuntimeException("Reponse Error: status=" + status + ", body=" + body);
        }

        String jsonResponse;
        try {
            jsonResponse = postMethod.getResponseBodyAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jsonResponse;
    }

    /**
     * API実行対象のZabbix Serverのバージョンと指定したバージョンを比較します。<br/>
     * 指定したバージョンと同じであれば0、指定したバージョンより後であれば0より大きい値、指定したバージョンより前であれば0より小さい値を返します。
     * 
     * @param zabbixVersion Zabbixバージョン
     * @return
     */
    public int checkVersion(String zabbixVersion) {
        if (apiVersion == null) {
            apiVersion = (String) this.execute("apiinfo.version", null);
        }

        return compareVersion(apiVersion, zabbixVersion);
    }

    protected int compareVersion(String apiVersion, String zabbixVersion) {
        int version1;
        {
            String[] array = apiVersion.split("\\.");
            version1 = Integer.parseInt(array[0]) * 1000 * 1000;
            version1 += Integer.parseInt(array[1]) * 1000;
            version1 += (array.length == 3) ? Integer.parseInt(array[2]) : 0;
        }

        int version2;
        {
            String[] array = zabbixVersion.split("\\.");
            version2 = Integer.parseInt(array[0]) * 1000 * 1000;
            version2 += Integer.parseInt(array[1]) * 1000;
            version2 += (array.length == 3) ? Integer.parseInt(array[2]) : 0;

            if (version2 <= 1008000) {
                version2 = 1000000;
            } else if (version2 == 1008001) {
                version2 = 1001000;
            } else if (version2 == 1008002) {
                version2 = 1002000;
            } else if (version2 < 2000000) {
                version2 = 1003000;
            } else if (version2 <= 2000003) {
                version2 = 1004000;
            }
        }

        return version1 - version2;
    }

}
