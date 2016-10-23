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
package jp.primecloud.auto.service.dto;

import java.io.Serializable;

import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.UserAuth;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;

    private UserAuth userAuth;

    /**
     * userを取得します。
     *
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * userを設定します。
     *
     * @param user user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * userAuthを取得します。
     *
     * @return userAuth
     */
    public UserAuth getUserAuth() {
        return userAuth;
    }

    /**
     * userを設定します。
     *
     * @param userAuth userAuth
     */
    public void setUser(UserAuth userAuth) {
        this.userAuth = userAuth;
    }

}
