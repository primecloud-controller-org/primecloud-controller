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
import java.util.Collection;
import java.util.List;

import jp.primecloud.auto.zabbix.ZabbixAccessor;
import jp.primecloud.auto.zabbix.model.template.Template;
import jp.primecloud.auto.zabbix.model.template.TemplateGetParam;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * <p>
 * Zabbix APIのTemplateを操作するためのクラスです。
 * </p>
 *
 */
public class TemplateClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    /**
     * コンストラクタです。
     *
     * @param accessor {@link ZabbixAccessor}
     * @param defaultConfig デフォルトの{@link JsonConfig}
     */
    public TemplateClient(ZabbixAccessor accessor, JsonConfig defaultConfig) {
        this.accessor = accessor;
        this.defaultConfig = defaultConfig;
    }

    /**
     * テンプレート情報を取得します。<br/>
     * テンプレート情報が存在しない場合、空のリストを返します。
     *
     * @param param {@link TemplateGetParam}
     * @return 取得したテンプレート情報のリスト
     */
    @SuppressWarnings("unchecked")
    public List<Template> get(TemplateGetParam param) {
        JSONObject params = JSONObject.fromObject(param, defaultConfig);
        Object result = accessor.execute("template.get", params);

        if (result instanceof JSONArray) {
            JsonConfig config = defaultConfig.copy();
            config.setCollectionType(List.class);
            config.setRootClass(Template.class);
            return (List<Template>) JSONArray.toCollection((JSONArray) result, config);
        } else {
            List<Template> templates = new ArrayList<Template>();
            Collection<?> values = ((JSONObject) result).values();
            JsonConfig config = defaultConfig.copy();
            config.setRootClass(Template.class);
            for (Object value : values) {
                JSONObject jsonValue = (JSONObject) value;
                templates.add((Template) JSONObject.toBean(jsonValue, config));
            }
            return templates;
        }
    }
}