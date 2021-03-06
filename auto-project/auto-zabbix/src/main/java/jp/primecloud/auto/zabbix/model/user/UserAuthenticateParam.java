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

/**
 * <p>
 * UserのAuthenticateメソッド用パラメータクラスです。
 * </p>
 *
 */
public class UserAuthenticateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String user;

    private String password;

    /**
     * userを取得します。
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * userを設定します。
     *
     * @param user user
     */
    public void setUser(String user) {
        this.user = user;
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

}
