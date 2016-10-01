/*
 * Copyright 2015 by SCSK Corporation.
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
package jp.primecloud.auto.zabbix.model.hostinterface;

import java.io.Serializable;

/**
 * <p>
 * Hostinterfaceのエンティティクラスです。
 * </p>
 *
 */
public class Hostinterface implements Serializable {

    private static final long serialVersionUID = 1L;

    private String interfaceid;

    private String dns;

    private String hostid;

    private String ip;

    private Integer main;

    private Integer port;

    private Integer type;

    private Integer useip;

    private Integer bulk;

    /**
     * interfaceidを取得します。
     * 
     * @return interfaceid
     */
    public String getInterfaceid() {
        return interfaceid;
    }

    /**
     * interfaceidを設定します。
     * 
     * @param interfaceid String
     */
    public void setInterfaceid(String interfaceid) {
        this.interfaceid = interfaceid;
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
     * @param dns String
     */
    public void setDns(String dns) {
        this.dns = dns;
    }

    /**
     * hostidを取得します。
     * 
     * @return hostid
     */
    public String getHostid() {
        return hostid;
    }

    /**
     * hostidを設定します。
     * 
     * @param hostid String
     */
    public void setHostid(String hostid) {
        this.hostid = hostid;
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
     * @param ip String
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * mainを取得します。
     * 
     * @return main
     */
    public Integer getMain() {
        return main;
    }

    /**
     * mainを設定します。
     * 
     * @param main Integer
     */
    public void setMain(Integer main) {
        this.main = main;
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
     * @param port Integer
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * typeを取得します。
     * 
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * typeを設定します。
     * 
     * @param type Integer
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * useipを取得します。
     * 
     * @return useip
     */
    public Integer getUseip() {
        return useip;
    }

    /**
     * useipを設定します。
     * 
     * @param useip Integer
     */
    public void setUseip(Integer useip) {
        this.useip = useip;
    }

    /**
     * bulkを取得します。
     *
     * @return bulk
     */
    public Integer getBulk() {
        return bulk;
    }

    /**
     * bulkを設定します。
     *
     * @param bulk bulk
     */
    public void setBulk(Integer bulk) {
        this.bulk = bulk;
    }

}
