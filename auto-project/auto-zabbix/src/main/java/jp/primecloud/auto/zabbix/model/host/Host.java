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
import jp.primecloud.auto.zabbix.model.hostinterface.Hostinterface;
import jp.primecloud.auto.zabbix.model.maintenance.Maintenance;
import jp.primecloud.auto.zabbix.model.template.Template;

/**
 * <p>
 * Hostのエンティティクラスです。
 * </p>
 *
 */
public class Host implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostid;

    private String proxyHostid;

    private String host;

    private String dns;

    private Integer useip;

    private String ip;

    private Integer port;

    private Integer status;

    private Integer disableUntil;

    private String error;

    private Integer available;

    private Integer errorsFrom;

    private Integer lastaccess;

    private Integer inbytes;

    private Integer outbytes;

    private Integer useipmi;

    private Integer ipmiPort;

    private Integer ipmiAuthtype;

    private Integer ipmiPrivilege;

    private String ipmiUsername;

    private String ipmiPassword;

    private Integer ipmiDisableUntil;

    private Integer ipmiAvailable;

    private Integer snmpDisableUntil;

    private Integer snmpAvailable;

    private Integer maintenanceid;

    private Integer maintenanceStatus;

    private Integer maintenanceType;

    private Integer maintenanceFrom;

    private String ipmiIp;

    private Integer ipmiErrorsFrom;

    private Integer snmpErrorsFrom;

    private String ipmiError;

    private String snmpError;

    private String jmxDisableUntil;

    private String jmxAvailable;

    private String jmxErrorsFrom;

    private String jmxError;

    private String name;

    private String flags;

    private String templateid;

    private List<Template> parenttemplates;

    private List <Hostgroup> groups;

    private List<Maintenance> maintenances;

    private List<Hostinterface> interfaces;

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
     * @param hostid hostid
     */
    public void setHostid(String hostid) {
        this.hostid = hostid;
    }

    /**
     * proxyHostidを取得します。
     *
     * @return proxyHostid
     */
    public String getProxyHostid() {
        return proxyHostid;
    }

    /**
     * proxyHostidを設定します。
     *
     * @param proxyHostid proxyHostid
     */
    public void setProxyHostid(String proxyHostid) {
        this.proxyHostid = proxyHostid;
    }

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
     * @param useip useip
     */
    public void setUseip(Integer useip) {
        this.useip = useip;
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

    /**
     * disableUntilを取得します。
     *
     * @return disableUntil
     */
    public Integer getDisableUntil() {
        return disableUntil;
    }

    /**
     * disableUntilを設定します。
     *
     * @param disableUntil disableUntil
     */
    public void setDisableUntil(Integer disableUntil) {
        this.disableUntil = disableUntil;
    }

    /**
     * errorを取得します。
     *
     * @return error
     */
    public String getError() {
        return error;
    }

    /**
     * errorを設定します。
     *
     * @param error error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * availableを取得します。
     *
     * @return available
     */
    public Integer getAvailable() {
        return available;
    }

    /**
     * availableを設定します。
     *
     * @param available available
     */
    public void setAvailable(Integer available) {
        this.available = available;
    }

    /**
     * errorsFromを取得します。
     *
     * @return errorsFrom
     */
    public Integer getErrorsFrom() {
        return errorsFrom;
    }

    /**
     * errorsFromを設定します。
     *
     * @param errorsFrom errorsFrom
     */
    public void setErrorsFrom(Integer errorsFrom) {
        this.errorsFrom = errorsFrom;
    }

    /**
     * lastaccessを取得します。
     *
     * @return lastaccess
     */
    public Integer getLastaccess() {
        return lastaccess;
    }

    /**
     * lastaccessを設定します。
     *
     * @param lastaccess lastaccess
     */
    public void setLastaccess(Integer lastaccess) {
        this.lastaccess = lastaccess;
    }

    /**
     * inbytesを取得します。
     *
     * @return inbytes
     */
    public Integer getInbytes() {
        return inbytes;
    }

    /**
     * inbytesを設定します。
     *
     * @param inbytes inbytes
     */
    public void setInbytes(Integer inbytes) {
        this.inbytes = inbytes;
    }

    /**
     * outbytesを取得します。
     *
     * @return outbytes
     */
    public Integer getOutbytes() {
        return outbytes;
    }

    /**
     * outbytesを設定します。
     *
     * @param outbytes outbytes
     */
    public void setOutbytes(Integer outbytes) {
        this.outbytes = outbytes;
    }

    /**
     * useipmiを取得します。
     *
     * @return useipmi
     */
    public Integer getUseipmi() {
        return useipmi;
    }

    /**
     * useipmiを設定します。
     *
     * @param useipmi useipmi
     */
    public void setUseipmi(Integer useipmi) {
        this.useipmi = useipmi;
    }

    /**
     * ipmiPortを取得します。
     *
     * @return ipmiPort
     */
    public Integer getIpmiPort() {
        return ipmiPort;
    }

    /**
     * ipmiPortを設定します。
     *
     * @param ipmiPort ipmiPort
     */
    public void setIpmiPort(Integer ipmiPort) {
        this.ipmiPort = ipmiPort;
    }

    /**
     * ipmiAuthtypeを取得します。
     *
     * @return ipmiAuthtype
     */
    public Integer getIpmiAuthtype() {
        return ipmiAuthtype;
    }

    /**
     * ipmiAuthtypeを設定します。
     *
     * @param ipmiAuthtype ipmiAuthtype
     */
    public void setIpmiAuthtype(Integer ipmiAuthtype) {
        this.ipmiAuthtype = ipmiAuthtype;
    }

    /**
     * ipmiPrivilegeを取得します。
     *
     * @return ipmiPrivilege
     */
    public Integer getIpmiPrivilege() {
        return ipmiPrivilege;
    }

    /**
     * ipmiPrivilegeを設定します。
     *
     * @param ipmiPrivilege ipmiPrivilege
     */
    public void setIpmiPrivilege(Integer ipmiPrivilege) {
        this.ipmiPrivilege = ipmiPrivilege;
    }

    /**
     * ipmiUsernameを取得します。
     *
     * @return ipmiUsername
     */
    public String getIpmiUsername() {
        return ipmiUsername;
    }

    /**
     * ipmiUsernameを設定します。
     *
     * @param ipmiUsername ipmiUsername
     */
    public void setIpmiUsername(String ipmiUsername) {
        this.ipmiUsername = ipmiUsername;
    }

    /**
     * ipmiPasswordを取得します。
     *
     * @return ipmiPassword
     */
    public String getIpmiPassword() {
        return ipmiPassword;
    }

    /**
     * ipmiPasswordを設定します。
     *
     * @param ipmiPassword ipmiPassword
     */
    public void setIpmiPassword(String ipmiPassword) {
        this.ipmiPassword = ipmiPassword;
    }

    /**
     * ipmiDisableUntilを取得します。
     *
     * @return ipmiDisableUntil
     */
    public Integer getIpmiDisableUntil() {
        return ipmiDisableUntil;
    }

    /**
     * ipmiDisableUntilを設定します。
     *
     * @param ipmiDisableUntil ipmiDisableUntil
     */
    public void setIpmiDisableUntil(Integer ipmiDisableUntil) {
        this.ipmiDisableUntil = ipmiDisableUntil;
    }

    /**
     * ipmiAvailableを取得します。
     *
     * @return ipmiAvailable
     */
    public Integer getIpmiAvailable() {
        return ipmiAvailable;
    }

    /**
     * ipmiAvailableを設定します。
     *
     * @param ipmiAvailable ipmiAvailable
     */
    public void setIpmiAvailable(Integer ipmiAvailable) {
        this.ipmiAvailable = ipmiAvailable;
    }

    /**
     * snmpDisableUntilを取得します。
     *
     * @return snmpDisableUntil
     */
    public Integer getSnmpDisableUntil() {
        return snmpDisableUntil;
    }

    /**
     * snmpDisableUntilを設定します。
     *
     * @param snmpDisableUntil snmpDisableUntil
     */
    public void setSnmpDisableUntil(Integer snmpDisableUntil) {
        this.snmpDisableUntil = snmpDisableUntil;
    }

    /**
     * snmpAvailableを取得します。
     *
     * @return snmpAvailable
     */
    public Integer getSnmpAvailable() {
        return snmpAvailable;
    }

    /**
     * snmpAvailableを設定します。
     *
     * @param snmpAvailable snmpAvailable
     */
    public void setSnmpAvailable(Integer snmpAvailable) {
        this.snmpAvailable = snmpAvailable;
    }

    /**
     * maintenanceidを取得します。
     *
     * @return maintenanceid
     */
    public Integer getMaintenanceid() {
        return maintenanceid;
    }

    /**
     * maintenanceidを設定します。
     *
     * @param maintenanceid maintenanceid
     */
    public void setMaintenanceid(Integer maintenanceid) {
        this.maintenanceid = maintenanceid;
    }

    /**
     * maintenanceStatusを取得します。
     *
     * @return maintenanceStatus
     */
    public Integer getMaintenanceStatus() {
        return maintenanceStatus;
    }

    /**
     * maintenanceStatusを設定します。
     *
     * @param maintenanceStatus maintenanceStatus
     */
    public void setMaintenanceStatus(Integer maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }

    /**
     * maintenanceTypeを取得します。
     *
     * @return maintenanceType
     */
    public Integer getMaintenanceType() {
        return maintenanceType;
    }

    /**
     * maintenanceTypeを設定します。
     *
     * @param maintenanceType maintenanceType
     */
    public void setMaintenanceType(Integer maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    /**
     * maintenanceFromを取得します。
     *
     * @return maintenanceFrom
     */
    public Integer getMaintenanceFrom() {
        return maintenanceFrom;
    }

    /**
     * maintenanceFromを設定します。
     *
     * @param maintenanceFrom maintenanceFrom
     */
    public void setMaintenanceFrom(Integer maintenanceFrom) {
        this.maintenanceFrom = maintenanceFrom;
    }

    /**
     * ipmiIpを取得します。
     *
     * @return ipmiIp
     */
    public String getIpmiIp() {
        return ipmiIp;
    }

    /**
     * ipmiIpを設定します。
     *
     * @param ipmiIp ipmiIp
     */
    public void setIpmiIp(String ipmiIp) {
        this.ipmiIp = ipmiIp;
    }

    /**
     * ipmiErrorsFromを取得します。
     *
     * @return ipmiErrorsFrom
     */
    public Integer getIpmiErrorsFrom() {
        return ipmiErrorsFrom;
    }

    /**
     * ipmiErrorsFromを設定します。
     *
     * @param ipmiErrorsFrom ipmiErrorsFrom
     */
    public void setIpmiErrorsFrom(Integer ipmiErrorsFrom) {
        this.ipmiErrorsFrom = ipmiErrorsFrom;
    }

    /**
     * snmpErrorsFromを取得します。
     *
     * @return snmpErrorsFrom
     */
    public Integer getSnmpErrorsFrom() {
        return snmpErrorsFrom;
    }

    /**
     * snmpErrorsFromを設定します。
     *
     * @param snmpErrorsFrom snmpErrorsFrom
     */
    public void setSnmpErrorsFrom(Integer snmpErrorsFrom) {
        this.snmpErrorsFrom = snmpErrorsFrom;
    }

    /**
     * ipmiErrorを取得します。
     *
     * @return ipmiError
     */
    public String getIpmiError() {
        return ipmiError;
    }

    /**
     * ipmiErrorを設定します。
     *
     * @param ipmiError ipmiError
     */
    public void setIpmiError(String ipmiError) {
        this.ipmiError = ipmiError;
    }

    /**
     * snmpErrorを取得します。
     *
     * @return snmpError
     */
    public String getSnmpError() {
        return snmpError;
    }

    /**
     * snmpErrorを設定します。
     *
     * @param snmpError snmpError
     */
    public void setSnmpError(String snmpError) {
        this.snmpError = snmpError;
    }

    /**
     * jmxDisableUntilを取得します。
     *
     * @return jmxDisableUntil
     */
    public String getJmxDisableUntil() {
        return jmxDisableUntil;
    }

    /**
     * jmxDisableUntilを設定します。
     *
     * @param jmxDisableUntil String
     */
    public void setJmxDisableUntil(String jmxDisableUntil) {
        this.jmxDisableUntil = jmxDisableUntil;
    }

    /**
     * jmxAvailableを取得します。
     *
     * @return jmxAvailable
     */
    public String getJmxAvailable() {
        return jmxAvailable;
    }

    /**
     * jmxAvailableを設定します。
     *
     * @param jmxAvailable String
     */
    public void setJmxAvailable(String jmxAvailable) {
        this.jmxAvailable = jmxAvailable;
    }

    /**
     * jmxErrorsFromを取得します。
     *
     * @return jmxErrorsFrom
     */
    public String getJmxErrorsFrom() {
        return jmxErrorsFrom;
    }

    /**
     * jmxErrorsFromを設定します。
     *
     * @param jmxErrorsFrom String
     */
    public void setJmxErrorsFrom(String jmxErrorsFrom) {
        this.jmxErrorsFrom = jmxErrorsFrom;
    }

    /**
     * jmxErrorを取得します。
     *
     * @return jmxError
     */
    public String getJmxError() {
        return jmxError;
    }

    /**
     * jmxErrorを設定します。
     *
     * @param jmxError String
     */
    public void setJmxError(String jmxError) {
        this.jmxError = jmxError;
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
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * flagsを取得します。
     *
     * @return flags
     */
    public String getFlags() {
        return flags;
    }

    /**
     * flagsを設定します。
     *
     * @param flags String
     */
    public void setFlags(String flags) {
        this.flags = flags;
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
     * @param templateid String
     */
    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    /**
     * parenttemplatesを取得します。
     *
     * @return parenttemplates
     */
    public List<Template> getParenttemplates() {
        return parenttemplates;
    }

    /**
     * parenttemplatesを設定します。
     *
     * @param parenttemplates parenttemplates
     */
    public void setParenttemplates(List<Template> parenttemplates) {
        this.parenttemplates = parenttemplates;
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
     * maintenancesを取得します。
     *
     * @return maintenances
     */
    public List<Maintenance> getMaintenances() {
        return maintenances;
    }

    /**
     * maintenancesを設定します。
     *
     * @param maintenances maintenances
     */
    public void setMaintenances(List<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }

    /**
     * interfacesを取得します。
     *
     * @return interfaces
     */
    public List<Hostinterface> getInterfaces() {
        return interfaces;
    }

    /**
     * interfacesを設定します。
     *
     * @param interfaces List<Hostinterface>
     */
    public void setInterfaces(List<Hostinterface> interfaces) {
        this.interfaces = interfaces;
    }

}
