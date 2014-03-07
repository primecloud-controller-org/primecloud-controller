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

import java.util.Set;

import jp.primecloud.auto.zabbix.client.APIInfoClient;
import jp.primecloud.auto.zabbix.client.ApplicationClient;
import jp.primecloud.auto.zabbix.client.HostClient;
import jp.primecloud.auto.zabbix.client.HostgroupClient;
import jp.primecloud.auto.zabbix.client.ItemClient;
import jp.primecloud.auto.zabbix.client.TemplateClient;
import jp.primecloud.auto.zabbix.client.TriggerClient;
import jp.primecloud.auto.zabbix.client.UserClient;
import jp.primecloud.auto.zabbix.client.UsergroupClient;
import jp.primecloud.auto.zabbix.util.JavaPropertyNameProcessor;
import jp.primecloud.auto.zabbix.util.JsonPropertyNameProcessor;
import jp.primecloud.auto.zabbix.util.NullPropertyFilter;

import net.sf.json.JsonConfig;
import net.sf.json.processors.PropertyNameProcessorMatcher;


/**
 * <p>
 * Zabbixの各APIクラスをAccessorと結びつけるクライアントクラスです。
 * </p>
 *
 */
public class ZabbixClient {

    protected ZabbixAccessor accessor;

    protected JsonConfig defaultConfig;

    private APIInfoClient APIInfoClient;

    private HostClient hostClient;

    private HostgroupClient hostgroupClient;

    private UserClient userClient;

    private UsergroupClient usergroupClient;

    private TemplateClient templateClient;

    private ItemClient itemClient;

    private TriggerClient triggerClient;

    private ApplicationClient applicationClient;

    public ZabbixClient(ZabbixAccessor accessor) {
        this.accessor = accessor;
        defaultConfig = createDefaultConfig();

        APIInfoClient = new APIInfoClient(accessor, defaultConfig);
        hostClient = new HostClient(accessor, defaultConfig);
        hostgroupClient = new HostgroupClient(accessor, defaultConfig);
        userClient = new UserClient(accessor, defaultConfig);
        usergroupClient = new UsergroupClient(accessor, defaultConfig);
        templateClient = new TemplateClient(accessor, defaultConfig);
        itemClient = new ItemClient(accessor, defaultConfig);
        triggerClient = new TriggerClient(accessor, defaultConfig);
        applicationClient = new ApplicationClient(accessor, defaultConfig);
    }

    protected JsonConfig createDefaultConfig() {
        JsonConfig config = new JsonConfig();
        PropertyNameProcessorMatcher matcher = new PropertyNameProcessorMatcher() {
            @Override
            @SuppressWarnings("unchecked")
            public Object getMatch(Class target, Set set) {
                Object key = DEFAULT.getMatch(target, set);
                if (key == null) {
                    key = Object.class;
                }
                return key;
            }
        };
        config.setJavaPropertyNameProcessorMatcher(matcher);
        config.setJsonPropertyNameProcessorMatcher(matcher);
        config.registerJavaPropertyNameProcessor(Object.class, new JavaPropertyNameProcessor());
        config.registerJsonPropertyNameProcessor(Object.class, new JsonPropertyNameProcessor());
        config.setJsonPropertyFilter(new NullPropertyFilter());
        return config;
    }

    public ZabbixAccessor getAccessor() {
        return accessor;
    }

    public APIInfoClient APIInfo() {
        return APIInfoClient;
    }

    public HostClient host() {
        return hostClient;
    }

    public HostgroupClient hostgroup() {
        return hostgroupClient;
    }

    public UserClient user() {
        return userClient;
    }

    public UsergroupClient usergroup() {
        return usergroupClient;
    }

    public TemplateClient template() {
        return templateClient;
    }

    public ItemClient item() {
        return itemClient;
    }

    public TriggerClient trigger() {
        return triggerClient;
    }

    public ApplicationClient application() {
        return applicationClient;
    }
}
