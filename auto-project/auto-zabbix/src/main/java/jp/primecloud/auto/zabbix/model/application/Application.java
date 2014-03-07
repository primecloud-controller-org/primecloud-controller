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
package jp.primecloud.auto.zabbix.model.application;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.zabbix.model.host.Host;


/**
 * <p>
 * Applicationのエンティティクラスです。
 * </p>
 *
 */
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    private String applicationid;

    private List<Host> hosts;

    private String name;

    private String templateid;

    /**
     * applicationidを取得します。
     *
     * @return applicationid
     */
    public String getApplicationid() {
        return applicationid;
    }

    /**
     * applicationidを設定します。
     *
     * @param applicationid applicationid
     */
    public void setApplicationid(String applicationid) {
        this.applicationid = applicationid;
    }

    /**
     * hostsを取得します。
     *
     * @return hosts
     */
    public List<Host> getHosts() {
        return hosts;
    }

    /**
     * hostsを設定します。
     *
     * @param hosts hosts
     */
    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    /**
     * nameを取得します。
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します。
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * templateidを取得します。
     *
     * @return templateid
     */
    public String getTemplateid() {
        return templateid;
    }

    /**
     * templateidを設定します。
     *
     * @param templateid templateid
     */
    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

}