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
package jp.primecloud.auto.zabbix.model.host;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.template.Template;


/**
 * <p>
 * HostのCreateメソッド用パラメータクラスです。
 * </p>
 *
 */
public class HostCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host;

    private List<Hostgroup> groups;

    private List<Template> templates;

    private String dns;

    private String ip;

    private Integer port;

    private Integer status;

    /**
     * hostを取得します。
     *
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * hostを設定します。
     *
     * @param host host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * groupsを取得します。
     *
     * @return groups
     */
    public List<Hostgroup> getGroups() {
        return groups;
    }

    /**
     * groupsを設定します。
     *
     * @param groups groups
     */
    public void setGroups(List<Hostgroup> groups) {
        this.groups = groups;
    }

    /**
     * templatesを取得します。
     *
     * @return templates
     */
    public List<Template> getTemplates() {
        return templates;
    }

    /**
     * templatesを設定します。
     *
     * @param templates templates
     */
    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    /**
     * dnsを取得します。
     *
     * @return dns
     */
    public String getDns() {
        return dns;
    }

    /**
     * dnsを設定します。
     *
     * @param dns dns
     */
    public void setDns(String dns) {
        this.dns = dns;
    }

    /**
     * ipを取得します。
     *
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * ipを設定します。
     *
     * @param ip ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * portを取得します。
     *
     * @return port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * portを設定します。
     *
     * @param port port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * statusを取得します。
     *
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     *
     * @param status status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

}
