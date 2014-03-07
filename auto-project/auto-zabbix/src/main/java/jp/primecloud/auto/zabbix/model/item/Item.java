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
package jp.primecloud.auto.zabbix.model.item;

import java.io.Serializable;
/**
 * <p>
 * Itemのエンティティクラスです。
 * </p>
 *
 */
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String itemid;

    private Integer type;

    private String snmpCommunity;

    private String snmpOid;

    private Integer snmpPort;

    private String hostid;

    private String description;

    private String key;

    private Integer delay;

    private Integer history;

    private Integer trends;

    private String lastvalue;

    private String lastclock;

    private String prevvalue;

    private Integer status;

    private Integer valueType;

    private String trapperHosts;

    private String units;

    private Integer multiplier;

    private Integer delta;

    private String prevorgvalue;

    private String snmpv3Securityname;

    private Integer snmpv3Securitylevel;

    private String snmpv3Authpassphrase;

    private String snmpv3Privpassphrase;

    private Integer formula;

    private String error;

    private Integer lastlogsize;

    private String logtimefmt;

    private String templateid;

    private Integer valuemapid;

    private String delayFlex;

    private String params;

    private String ipmiSensor;

    private Integer dataType;

    private Integer authtype;

    private String username;

    private String password;

    private String publickey;

    private String privatekey;

    private Integer mtime;

    private String application;

    /**
     * itemidを取得します。
     *
     * @return itemid
     */
    public String getItemid() {
        return itemid;
    }

    /**
     * itemidを設定します。
     *
     * @param itemid itemid
     */
    public void setItemid(String itemid) {
        this.itemid = itemid;
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
     * @param type type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * snmpCommunityを取得します。
     *
     * @return snmpCommunity
     */
    public String getSnmpCommunity() {
        return snmpCommunity;
    }

    /**
     * snmpCommunityを設定します。
     *
     * @param snmpCommunity snmpCommunity
     */
    public void setSnmpCommunity(String snmpCommunity) {
        this.snmpCommunity = snmpCommunity;
    }

    /**
     * snmpOidを取得します。
     *
     * @return snmpOid
     */
    public String getSnmpOid() {
        return snmpOid;
    }

    /**
     * snmpOidを設定します。
     *
     * @param snmpOid snmpOid
     */
    public void setSnmpOid(String snmpOid) {
        this.snmpOid = snmpOid;
    }

    /**
     * snmpPortを取得します。
     *
     * @return snmpPort
     */
    public Integer getSnmpPort() {
        return snmpPort;
    }

    /**
     * snmpPortを設定します。
     *
     * @param snmpPort snmpPort
     */
    public void setSnmpPort(Integer snmpPort) {
        this.snmpPort = snmpPort;
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
     * @param hostid hostid
     */
    public void setHostid(String hostid) {
        this.hostid = hostid;
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
     * keyを取得します。
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * keyを設定します。
     *
     * @param key key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * delayを取得します。
     *
     * @return delay
     */
    public Integer getDelay() {
        return delay;
    }

    /**
     * delayを設定します。
     *
     * @param delay delay
     */
    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    /**
     * historyを取得します。
     *
     * @return history
     */
    public Integer getHistory() {
        return history;
    }

    /**
     * historyを設定します。
     *
     * @param history history
     */
    public void setHistory(Integer history) {
        this.history = history;
    }

    /**
     * trendsを取得します。
     *
     * @return trends
     */
    public Integer getTrends() {
        return trends;
    }

    /**
     * trendsを設定します。
     *
     * @param trends trends
     */
    public void setTrends(Integer trends) {
        this.trends = trends;
    }

    /**
     * lastvalueを取得します。
     *
     * @return lastvalue
     */
    public String getLastvalue() {
        return lastvalue;
    }

    /**
     * lastvalueを設定します。
     *
     * @param lastvalue lastvalue
     */
    public void setLastvalue(String lastvalue) {
        this.lastvalue = lastvalue;
    }

    /**
     * lastclockを取得します。
     *
     * @return lastclock
     */
    public String getLastclock() {
        return lastclock;
    }

    /**
     * lastclockを設定します。
     *
     * @param lastclock lastclock
     */
    public void setLastclock(String lastclock) {
        this.lastclock = lastclock;
    }

    /**
     * prevvalueを取得します。
     *
     * @return prevvalue
     */
    public String getPrevvalue() {
        return prevvalue;
    }

    /**
     * prevvalueを設定します。
     *
     * @param prevvalue prevvalue
     */
    public void setPrevvalue(String prevvalue) {
        this.prevvalue = prevvalue;
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
     * valueTypeを取得します。
     *
     * @return valueType
     */
    public Integer getValueType() {
        return valueType;
    }

    /**
     * valueTypeを設定します。
     *
     * @param valueType valueType
     */
    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    /**
     * trapperHostsを取得します。
     *
     * @return trapperHosts
     */
    public String getTrapperHosts() {
        return trapperHosts;
    }

    /**
     * trapperHostsを設定します。
     *
     * @param trapperHosts trapperHosts
     */
    public void setTrapperHosts(String trapperHosts) {
        this.trapperHosts = trapperHosts;
    }

    /**
     * unitsを取得します。
     *
     * @return units
     */
    public String getUnits() {
        return units;
    }

    /**
     * unitsを設定します。
     *
     * @param units units
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * multiplierを取得します。
     *
     * @return multiplier
     */
    public Integer getMultiplier() {
        return multiplier;
    }

    /**
     * multiplierを設定します。
     *
     * @param multiplier multiplier
     */
    public void setMultiplier(Integer multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * deltaを取得します。
     *
     * @return delta
     */
    public Integer getDelta() {
        return delta;
    }

    /**
     * deltaを設定します。
     *
     * @param delta delta
     */
    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    /**
     * prevorgvalueを取得します。
     *
     * @return prevorgvalue
     */
    public String getPrevorgvalue() {
        return prevorgvalue;
    }

    /**
     * prevorgvalueを設定します。
     *
     * @param prevorgvalue prevorgvalue
     */
    public void setPrevorgvalue(String prevorgvalue) {
        this.prevorgvalue = prevorgvalue;
    }

    /**
     * snmpv3Securitynameを取得します。
     *
     * @return snmpv3Securityname
     */
    public String getSnmpv3Securityname() {
        return snmpv3Securityname;
    }

    /**
     * snmpv3Securitynameを設定します。
     *
     * @param snmpv3Securityname snmpv3Securityname
     */
    public void setSnmpv3Securityname(String snmpv3Securityname) {
        this.snmpv3Securityname = snmpv3Securityname;
    }

    /**
     * snmpv3Securitylevelを取得します。
     *
     * @return snmpv3Securitylevel
     */
    public Integer getSnmpv3Securitylevel() {
        return snmpv3Securitylevel;
    }

    /**
     * snmpv3Securitylevelを設定します。
     *
     * @param snmpv3Securitylevel snmpv3Securitylevel
     */
    public void setSnmpv3Securitylevel(Integer snmpv3Securitylevel) {
        this.snmpv3Securitylevel = snmpv3Securitylevel;
    }

    /**
     * snmpv3Authpassphraseを取得します。
     *
     * @return snmpv3Authpassphrase
     */
    public String getSnmpv3Authpassphrase() {
        return snmpv3Authpassphrase;
    }

    /**
     * snmpv3Authpassphraseを設定します。
     *
     * @param snmpv3Authpassphrase snmpv3Authpassphrase
     */
    public void setSnmpv3Authpassphrase(String snmpv3Authpassphrase) {
        this.snmpv3Authpassphrase = snmpv3Authpassphrase;
    }

    /**
     * snmpv3Privpassphraseを取得します。
     *
     * @return snmpv3Privpassphrase
     */
    public String getSnmpv3Privpassphrase() {
        return snmpv3Privpassphrase;
    }

    /**
     * snmpv3Privpassphraseを設定します。
     *
     * @param snmpv3Privpassphrase snmpv3Privpassphrase
     */
    public void setSnmpv3Privpassphrase(String snmpv3Privpassphrase) {
        this.snmpv3Privpassphrase = snmpv3Privpassphrase;
    }

    /**
     * formulaを取得します。
     *
     * @return formula
     */
    public Integer getFormula() {
        return formula;
    }

    /**
     * formulaを設定します。
     *
     * @param formula formula
     */
    public void setFormula(Integer formula) {
        this.formula = formula;
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
     * lastlogsizeを取得します。
     *
     * @return lastlogsize
     */
    public Integer getLastlogsize() {
        return lastlogsize;
    }

    /**
     * lastlogsizeを設定します。
     *
     * @param lastlogsize lastlogsize
     */
    public void setLastlogsize(Integer lastlogsize) {
        this.lastlogsize = lastlogsize;
    }

    /**
     * logtimefmtを取得します。
     *
     * @return logtimefmt
     */
    public String getLogtimefmt() {
        return logtimefmt;
    }

    /**
     * logtimefmtを設定します。
     *
     * @param logtimefmt logtimefmt
     */
    public void setLogtimefmt(String logtimefmt) {
        this.logtimefmt = logtimefmt;
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
     * valuemapidを取得します。
     *
     * @return valuemapid
     */
    public Integer getValuemapid() {
        return valuemapid;
    }

    /**
     * valuemapidを設定します。
     *
     * @param valuemapid valuemapid
     */
    public void setValuemapid(Integer valuemapid) {
        this.valuemapid = valuemapid;
    }

    /**
     * delayFlexを取得します。
     *
     * @return delayFlex
     */
    public String getDelayFlex() {
        return delayFlex;
    }

    /**
     * delayFlexを設定します。
     *
     * @param delayFlex delayFlex
     */
    public void setDelayFlex(String delayFlex) {
        this.delayFlex = delayFlex;
    }

    /**
     * paramsを取得します。
     *
     * @return params
     */
    public String getParams() {
        return params;
    }

    /**
     * paramsを設定します。
     *
     * @param params params
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * ipmiSensorを取得します。
     *
     * @return ipmiSensor
     */
    public String getIpmiSensor() {
        return ipmiSensor;
    }

    /**
     * ipmiSensorを設定します。
     *
     * @param ipmiSensor ipmiSensor
     */
    public void setIpmiSensor(String ipmiSensor) {
        this.ipmiSensor = ipmiSensor;
    }

    /**
     * dataTypeを取得します。
     *
     * @return dataType
     */
    public Integer getDataType() {
        return dataType;
    }

    /**
     * dataTypeを設定します。
     *
     * @param dataType dataType
     */
    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    /**
     * authtypeを取得します。
     *
     * @return authtype
     */
    public Integer getAuthtype() {
        return authtype;
    }

    /**
     * authtypeを設定します。
     *
     * @param authtype authtype
     */
    public void setAuthtype(Integer authtype) {
        this.authtype = authtype;
    }

    /**
     * usernameを取得します。
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * usernameを設定します。
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * passwordを取得します。
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * passwordを設定します。
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * publickeyを取得します。
     *
     * @return publickey
     */
    public String getPublickey() {
        return publickey;
    }

    /**
     * publickeyを設定します。
     *
     * @param publickey publickey
     */
    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    /**
     * privatekeyを取得します。
     *
     * @return privatekey
     */
    public String getPrivatekey() {
        return privatekey;
    }

    /**
     * privatekeyを設定します。
     *
     * @param privatekey privatekey
     */
    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    /**
     * mtimeを取得します。
     *
     * @return mtime
     */
    public Integer getMtime() {
        return mtime;
    }

    /**
     * mtimeを設定します。
     *
     * @param mtime mtime
     */
    public void setMtime(Integer mtime) {
        this.mtime = mtime;
    }

    /**
     * applicationを取得します。
     *
     * @return application
     */
    public String getApplication() {
        return application;
    }

    /**
     * applicationを設定します。
     *
     * @param application application
     */
    public void setApplication(String application) {
        this.application = application;
    }

}
