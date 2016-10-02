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
package jp.primecloud.auto.zabbix.model.proxy;

import java.io.Serializable;

/**
 * <p>
 * Proxyのエンティティクラスです。
 * </p>
 *
 */
public class Proxy implements Serializable {

    private static final long serialVersionUID = 1L;

    private String proxyid;

    private String host;

    private Integer status;

    private Integer lastaccess;

    private String proxyHostid;

    private Integer disableUntil;

    private String error;

    private Integer available;

    private Integer errorsFrom;

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

    private String description;

    private Integer tlsConnect;

    private Integer tlsAccept;

    private String tlsIssuer;

    private String tlsSubject;

    private String tlsPskIdentity;

    private String tlsPsk;

    /**
     * proxyidを取得します。
     *
     * @return proxyid
     */
    public String getProxyid() {
        return proxyid;
    }

    /**
     * proxyidを設定します。
     *
     * @param proxyid proxyid
     */
    public void setProxyid(String proxyid) {
        this.proxyid = proxyid;
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
     * @param jmxDisableUntil jmxDisableUntil
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
     * @param jmxAvailable jmxAvailable
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
     * @param jmxErrorsFrom jmxErrorsFrom
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
     * @param jmxError jmxError
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
     * @param name name
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
     * @param flags flags
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
     * @param templateid templateid
     */
    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    /**
     * descriptionを取得します。
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * descriptionを設定します。
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * tlsConnectを取得します。
     *
     * @return tlsConnect
     */
    public Integer getTlsConnect() {
        return tlsConnect;
    }

    /**
     * tlsConnectを設定します。
     *
     * @param tlsConnect tlsConnect
     */
    public void setTlsConnect(Integer tlsConnect) {
        this.tlsConnect = tlsConnect;
    }

    /**
     * tlsAcceptを取得します。
     *
     * @return tlsAccept
     */
    public Integer getTlsAccept() {
        return tlsAccept;
    }

    /**
     * tlsAcceptを設定します。
     *
     * @param tlsAccept tlsAccept
     */
    public void setTlsAccept(Integer tlsAccept) {
        this.tlsAccept = tlsAccept;
    }

    /**
     * tlsIssuerを取得します。
     *
     * @return tlsIssuer
     */
    public String getTlsIssuer() {
        return tlsIssuer;
    }

    /**
     * tlsIssuerを設定します。
     *
     * @param tlsIssuer tlsIssuer
     */
    public void setTlsIssuer(String tlsIssuer) {
        this.tlsIssuer = tlsIssuer;
    }

    /**
     * tlsSubjectを取得します。
     *
     * @return tlsSubject
     */
    public String getTlsSubject() {
        return tlsSubject;
    }

    /**
     * tlsSubjectを設定します。
     *
     * @param tlsSubject tlsSubject
     */
    public void setTlsSubject(String tlsSubject) {
        this.tlsSubject = tlsSubject;
    }

    /**
     * tlsPskIdentityを取得します。
     *
     * @return tlsPskIdentity
     */
    public String getTlsPskIdentity() {
        return tlsPskIdentity;
    }

    /**
     * tlsPskIdentityを設定します。
     *
     * @param tlsPskIdentity tlsPskIdentity
     */
    public void setTlsPskIdentity(String tlsPskIdentity) {
        this.tlsPskIdentity = tlsPskIdentity;
    }

    /**
     * tlsPskを取得します。
     *
     * @return tlsPsk
     */
    public String getTlsPsk() {
        return tlsPsk;
    }

    /**
     * tlsPskを設定します。
     *
     * @param tlsPsk tlsPsk
     */
    public void setTlsPsk(String tlsPsk) {
        this.tlsPsk = tlsPsk;
    }

}
