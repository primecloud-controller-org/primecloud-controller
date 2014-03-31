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
package jp.primecloud.auto.zabbix.model.usergroup;

import java.io.Serializable;

/**
 * <p>
 * UsergroupのCreateメソッド用パラメータクラスです。
 * </p>
 *
 */
public class UsergroupUpdateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usrgrpid;

    private String name;

    private Integer guiAccess;

    private Integer usersStatus;

    private Integer apiAccess;

    private Integer debugMode;

    /**
     * usrgrpidを取得します。
     *
     * @return usrgrpid
     */
    public String getUsrgrpid() {
        return usrgrpid;
    }

    /**
     * usrgrpidを設定します。
     *
     * @param usrgrpid usrgrpid
     */
    public void setUsrgrpid(String usrgrpid) {
        this.usrgrpid = usrgrpid;
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
     * guiAccessを取得します。
     *
     * @return guiAccess
     */
    public Integer getGuiAccess() {
        return guiAccess;
    }

    /**
     * guiAccessを設定します。
     *
     * @param guiAccess guiAccess
     */
    public void setGuiAccess(Integer guiAccess) {
        this.guiAccess = guiAccess;
    }

    /**
     * usersStatusを取得します。
     *
     * @return usersStatus
     */
    public Integer getUsersStatus() {
        return usersStatus;
    }

    /**
     * usersStatusを設定します。
     *
     * @param usersStatus usersStatus
     */
    public void setUsersStatus(Integer usersStatus) {
        this.usersStatus = usersStatus;
    }

    /**
     * apiAccessを取得します。
     *
     * @return apiAccess
     */
    public Integer getApiAccess() {
        return apiAccess;
    }

    /**
     * apiAccessを設定します。
     *
     * @param apiAccess apiAccess
     */
    public void setApiAccess(Integer apiAccess) {
        this.apiAccess = apiAccess;
    }

    /**
     * debugModeを取得します。
     *
     * @return debugMode
     */
    public Integer getDebugMode() {
        return debugMode;
    }

    /**
     * debugModeを設定します。
     *
     * @param debugMode debugMode
     */
    public void setDebugMode(Integer debugMode) {
        this.debugMode = debugMode;
    }
}
