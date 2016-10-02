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
package jp.primecloud.auto.zabbix.model.user;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.zabbix.model.usergroup.Usergroup;

/**
 * <p>
 * Userのエンティティクラスです。
 * </p>
 *
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userid;

    private String alias;

    private String name;

    private String surname;

    private String passwd;

    private String url;

    private Integer autologin;

    private Integer autologout;

    private String lang;

    private Integer refresh;

    private Integer type;

    private String theme;

    private Integer attemptFailed;

    private String attemptIp;

    private Integer attemptClock;

    private Integer rowsPerPage;

    private List<Usergroup> usrgrps;

    /**
     * useridを取得します。
     *
     * @return userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * useridを設定します。
     *
     * @param userid userid
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * aliasを取得します。
     *
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * aliasを設定します。
     *
     * @param alias alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
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
     * surnameを取得します。
     *
     * @return surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * surnameを設定します。
     *
     * @param surname surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * passwdを取得します。
     *
     * @return passwd
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * passwdを設定します。
     *
     * @param passwd passwd
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    /**
     * urlを取得します。
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * autologinを取得します。
     *
     * @return autologin
     */
    public Integer getAutologin() {
        return autologin;
    }

    /**
     * autologinを設定します。
     *
     * @param autologin autologin
     */
    public void setAutologin(Integer autologin) {
        this.autologin = autologin;
    }

    /**
     * autologoutを取得します。
     *
     * @return autologout
     */
    public Integer getAutologout() {
        return autologout;
    }

    /**
     * autologoutを設定します。
     *
     * @param autologout autologout
     */
    public void setAutologout(Integer autologout) {
        this.autologout = autologout;
    }

    /**
     * langを取得します。
     *
     * @return lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * langを設定します。
     *
     * @param lang lang
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * refreshを取得します。
     *
     * @return refresh
     */
    public Integer getRefresh() {
        return refresh;
    }

    /**
     * refreshを設定します。
     *
     * @param refresh refresh
     */
    public void setRefresh(Integer refresh) {
        this.refresh = refresh;
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
     * themeを取得します。
     *
     * @return theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * themeを設定します。
     *
     * @param theme theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * attemptFailedを取得します。
     *
     * @return attemptFailed
     */
    public Integer getAttemptFailed() {
        return attemptFailed;
    }

    /**
     * attemptFailedを設定します。
     *
     * @param attemptFailed attemptFailed
     */
    public void setAttemptFailed(Integer attemptFailed) {
        this.attemptFailed = attemptFailed;
    }

    /**
     * attemptIpを取得します。
     *
     * @return attemptIp
     */
    public String getAttemptIp() {
        return attemptIp;
    }

    /**
     * attemptIpを設定します。
     *
     * @param attemptIp attemptIp
     */
    public void setAttemptIp(String attemptIp) {
        this.attemptIp = attemptIp;
    }

    /**
     * attemptClockを取得します。
     *
     * @return attemptClock
     */
    public Integer getAttemptClock() {
        return attemptClock;
    }

    /**
     * attemptClockを設定します。
     *
     * @param attemptClock attemptClock
     */
    public void setAttemptClock(Integer attemptClock) {
        this.attemptClock = attemptClock;
    }

    /**
     * rowsPerPageを取得します。
     *
     * @return rowsPerPage
     */
    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

    /**
     * rowsPerPageを設定します。
     *
     * @param rowsPerPage rowsPerPage
     */
    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    /**
     * usrgrpsを取得します。
     *
     * @return usrgrps
     */
    public List<Usergroup> getUsrgrps() {
        return usrgrps;
    }

    /**
     * usrgrpsを設定します。
     *
     * @param usrgrps usrgrps
     */
    public void setUsrgrps(List<Usergroup> usrgrps) {
        this.usrgrps = usrgrps;
    }

}
