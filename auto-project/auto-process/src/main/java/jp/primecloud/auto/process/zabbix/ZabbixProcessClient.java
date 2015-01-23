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
package jp.primecloud.auto.process.zabbix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.zabbix.ZabbixClient;
import jp.primecloud.auto.zabbix.model.application.Application;
import jp.primecloud.auto.zabbix.model.application.ApplicationGetParam;
import jp.primecloud.auto.zabbix.model.host.Host;
import jp.primecloud.auto.zabbix.model.host.HostCreateParam;
import jp.primecloud.auto.zabbix.model.host.HostGetParam;
import jp.primecloud.auto.zabbix.model.host.HostUpdateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupCreateParam;
import jp.primecloud.auto.zabbix.model.hostgroup.HostgroupGetParam;
import jp.primecloud.auto.zabbix.model.item.Item;
import jp.primecloud.auto.zabbix.model.item.ItemGetParam;
import jp.primecloud.auto.zabbix.model.item.ItemUpdateParam;
import jp.primecloud.auto.zabbix.model.proxy.Proxy;
import jp.primecloud.auto.zabbix.model.proxy.ProxyGetParam;
import jp.primecloud.auto.zabbix.model.template.Template;
import jp.primecloud.auto.zabbix.model.template.TemplateGetParam;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ZabbixProcessClient {

    protected Log log = LogFactory.getLog(getClass());

    protected ZabbixClient zabbixClient;

    public ZabbixProcessClient(ZabbixClient zabbixClient) {
        this.zabbixClient = zabbixClient;
    }

    public ZabbixClient getZabbixClient() {
        return zabbixClient;
    }

    public Proxy getProxy(String proxyName) {
        ProxyGetParam param = new ProxyGetParam();
        Map<String, List<Object>> search = new HashMap<String, List<Object>>();
        search.put("host", Arrays.asList((Object)proxyName));
        param.setSearch(search);
        param.setOutput("extend");

        List<Proxy> proxies = zabbixClient.proxy().get(param);
        if (proxies.isEmpty()) {
            log.info(MessageUtils.getMessage("IPROCESS-100334", proxyName));
            return null;
        }

        return proxies.get(0);
    }

    public Host getHost(String hostid) {
        HostGetParam param = new HostGetParam();
        param.setHostids(Arrays.asList(hostid));
        param.setOutput("extend");

        List<Host> hosts = zabbixClient.host().get(param);

        // API実行結果チェック
        if (hosts.size() == 0) {
            // ホストが存在しない場合
            throw new AutoException("EPROCESS-000402", hostid);

        } else if (hosts.size() > 1) {
            // ホストを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000403", hostid);
            exception.addDetailInfo("result=" + hosts);
            throw exception;
        }

        return hosts.get(0);
    }

    public String createHost(String hostname, String fqdn, List<Hostgroup> hostgroups,
            Boolean status, Boolean userIp, String ip, String proxyHostid) {
        HostCreateParam param = new HostCreateParam();
        param.setHost(hostname);
        param.setGroups(hostgroups);
        param.setDns(fqdn);
        param.setPort(10050);
        if (status != null) {
            param.setStatus(status ? 0 : 1); // 有効の場合は0、無効の場合は1
        }
        if (userIp != null) {
            param.setUseip(userIp ? 1: 0);  // DNSの場合は0、IPの場合は1
            param.setIp(StringUtils.isEmpty(ip) ? "0.0.0.0": ip);
        }
        if (StringUtils.isNotEmpty(proxyHostid)) {
            param.setProxyHostid(proxyHostid);
        }

        List<String> hostids = zabbixClient.host().create(param);
        String hostid = hostids.get(0);

        if (log.isInfoEnabled()) {
            List<String> groupids = new ArrayList<String>();
            if (hostgroups != null) {
                for (Hostgroup hostgroup : hostgroups) {
                    groupids.add(hostgroup.getGroupid());
                }
            }
            log.info(MessageUtils.getMessage("IPROCESS-100311", hostid, fqdn, groupids, status));
        }

        return hostid;
    }

    public String updateHost(String hostid, String hostname, String fqdn, List<Hostgroup> hostgroups,
            Boolean status, Boolean userIp, String ip, String proxyHostid) {
        HostUpdateParam param = new HostUpdateParam();
        param.setHostid(hostid);
        param.setHost(hostname);
        param.setGroups(hostgroups);
        param.setDns(fqdn);
        param.setPort(10050);
        if (status != null) {
            param.setStatus(status ? 0 : 1);// 有効の場合は0、無効の場合は1
        }
        if (userIp != null) {
            param.setUseip(BooleanUtils.toInteger(userIp));// DNSの場合は0、IPの場合は1
            param.setIp(StringUtils.isEmpty(ip) ? "0.0.0.0": ip);
        }
        if (StringUtils.isNotEmpty(proxyHostid)) {
            param.setProxyHostid(proxyHostid);
        }

        List<String> hostids = zabbixClient.host().update(param);
        hostid = hostids.get(0);

        if (log.isInfoEnabled()) {
            List<String> groupids = new ArrayList<String>();
            if (hostgroups != null) {
                for (Hostgroup hostgroup : hostgroups) {
                    groupids.add(hostgroup.getGroupid());
                }
            }
            log.info(MessageUtils.getMessage("IPROCESS-100312", hostid, fqdn, groupids, status));
        }
        return hostid;
    }

    public String deleteHost(String hostid) {
        List<String> hostids = zabbixClient.host().delete(Arrays.asList(hostid));
        hostid = hostids.get(0);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100313", hostid));
        }

        return hostid;
    }

    public void createHostgroup(String name) {
        HostgroupCreateParam param = new HostgroupCreateParam();
        param.setName(name);
        zabbixClient.hostgroup().create(param);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100326", name));
        }
    }

    public void deleteHostgroup(String groupid, String name) {
        zabbixClient.hostgroup().delete(Arrays.asList(groupid));

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100327", name));
        }
    }

    public Hostgroup getHostgroupByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is required.");
        }
        HostgroupGetParam param = new HostgroupGetParam();
        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("name", Arrays.asList((Object)name));
        param.setFilter(filter);
        param.setOutput("extend");
        List<Hostgroup> hostgroups = zabbixClient.hostgroup().get(param);
        if (hostgroups.isEmpty()) {
            return null;
        }
        Hostgroup hostgroup = hostgroups.get(0);

        return hostgroup;
    }

    public Hostgroup getHostgroup(String groupid) {
        HostgroupGetParam param = new HostgroupGetParam();
        param.setGroupids(Arrays.asList(groupid));
        param.setOutput("extend");

        List<Hostgroup> hostgroups = zabbixClient.hostgroup().get(param);
        if (hostgroups.isEmpty()) {
            return null;
        }

        return hostgroups.get(0);
    }

    public List<Hostgroup> getHostgroups() {
        HostgroupGetParam param = new HostgroupGetParam();
        param.setOutput("extend");
        List<Hostgroup> hostgroups = zabbixClient.hostgroup().get(param);

        return hostgroups;
    }

    public List<Hostgroup> getHostgroupsByHostid(String hostid) {
        HostGetParam param = new HostGetParam();
        param.setHostids(Arrays.asList(hostid));
        param.setOutput("extend");
        param.setSelectGroups("extend");

        List<Host> hosts = zabbixClient.host().get(param);

        if (hosts.isEmpty()) {
            return new ArrayList<Hostgroup>();
        }

        return hosts.get(0).getGroups();
    }

    public Template getTemplateByName(String name) {
        TemplateGetParam param = new TemplateGetParam();
        Map<String, List<Object>> filter = new HashMap<String, List<Object>>();
        filter.put("host", Arrays.asList((Object)name));
        param.setFilter(filter);
        param.setOutput("extend");

        List<Template> templates = zabbixClient.template().get(param);
        if (templates.isEmpty()) {
            return null;
        }

        return templates.get(0);
    }

    public List<Template> getTemplatesByHostid(String hostid) {
        // ホストに適用されているテンプレートを取得
        HostGetParam param = new HostGetParam();
        param.setHostids(Arrays.asList(hostid));
        param.setOutput("extend");
        param.setSelectParentTemplates("extend");

        List<Host> hosts = zabbixClient.host().get(param);

        if (hosts.isEmpty()) {
            return new ArrayList<Template>();
        }

        return hosts.get(0).getParenttemplates();
    }

    public boolean addTemplate(String hostid, Template template) {
        // 現在適用されているテンプレートを取得
        List<Template> templates = getTemplatesByHostid(hostid);
        List<String> templateids = new ArrayList<String>();
        for (Template hostTemplate : templates) {
            templateids.add(hostTemplate.getTemplateid());
        }

        // テンプレートが適用されていればスキップする
        if (templateids.contains(template.getTemplateid())) {
            return false;
        }

        // テンプレートを適用する
        templates.add(template);

        HostUpdateParam param2 = new HostUpdateParam();
        param2.setHostid(hostid);
        param2.setTemplates(templates);
        List<String> hostids = zabbixClient.host().update(param2);
        hostid = hostids.get(0);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100321", hostid, template.getTemplateid()));
        }

        return true;
    }

    public boolean removeTemplate(String hostid, Template template) {
        // 現在適用されているテンプレートを取得
        List<Template> templates = getTemplatesByHostid(hostid);
        List<String> templateids = new ArrayList<String>();
        for (Template hostTemplate : templates) {
            templateids.add(hostTemplate.getTemplateid());
        }

        // テンプレートが適用されていなければスキップする
        if (!templateids.contains(template.getTemplateid())) {
            return false;
        }

        // テンプレートを除去する
        for (int i = 0; i < templates.size(); i++) {
            if (StringUtils.equals(template.getTemplateid(), templates.get(i).getTemplateid())) {
                templates.remove(i);
                break;
            }
        }

        HostUpdateParam param = new HostUpdateParam();
        param.setHostid(hostid);
        param.setTemplates(templates);
        zabbixClient.host().update(param);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100322", hostid, template.getTemplateid()));
        }
        return true;
    }

    public boolean enableItems(String hostid, String templateid) {
        //アイテムを有効化する
        List<Item> items = getItemsByTemplateid(hostid, templateid);

        if (items.isEmpty()) {
            return false;
        }

        int enabledItems = 0;
        for (Item item : items) {
            if (!ItemUpdateParam.ENABLE.equals(item.getStatus())) {
                ItemUpdateParam itemUpdateParam = new ItemUpdateParam();
                itemUpdateParam.setItemid(item.getItemid());
                itemUpdateParam.setStatus(ItemUpdateParam.ENABLE);
                zabbixClient.item().update(itemUpdateParam);
                enabledItems++;
            }
        }

        if (enabledItems > 0) {
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100323", hostid, templateid, items.size()));
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean disableItems(String hostid, String templateid) {
        //アイテムを無効化する
        List<Item> items = getItemsByTemplateid(hostid, templateid);

        if (items.isEmpty()) {
            return false;
        }

        int disabledItems = 0;
        for (Item item : items) {
            if (!ItemUpdateParam.DISABLE.equals(item.getStatus())) {
                ItemUpdateParam itemUpdateParam = new ItemUpdateParam();
                itemUpdateParam.setItemid(item.getItemid());
                itemUpdateParam.setStatus(ItemUpdateParam.DISABLE);
                zabbixClient.item().update(itemUpdateParam);
                disabledItems++;
            }
        }
        if (disabledItems > 0) {
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100324", hostid, templateid, items.size()));
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean deleteItems(String hostid, String templateid) {
        // アイテムを削除する
        List<Item> items = getItemsByTemplateid(hostid, templateid);

        if (items.isEmpty()) {
            return false;
        }

        List<String> itemids = new ArrayList<String>();
        for (Item item : items) {
            itemids.add(item.getItemid());
        }

        zabbixClient.item().delete(itemids);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100325", hostid, templateid, items.size()));
        }

        return true;
    }

    public List<Item> getItemsByTemplateid(String hostid, String templateid) {
        //テンプレートIDに紐づくアイテムを取得
        ApplicationGetParam applicationGetParam = new ApplicationGetParam();
        //hostidにtemplateidをセットする必要がある
        applicationGetParam.setHostids(Arrays.asList(templateid));
        applicationGetParam.setOutput("extend");
        List<Application> applications = zabbixClient.application().get(applicationGetParam);

        List<Item> items = new ArrayList<Item>();
        for (Application application : applications) {
            ItemGetParam itemGetParam = new ItemGetParam();
            itemGetParam.setApplication(application.getName());
            itemGetParam.setHostids(Arrays.asList(hostid));
            itemGetParam.setOutput("extend");

            items.addAll(zabbixClient.item().get(itemGetParam));
        }

        return items;
    }

}
