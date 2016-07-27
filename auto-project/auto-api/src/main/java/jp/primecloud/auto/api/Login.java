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
package jp.primecloud.auto.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.response.LoginResponse;

@Path("/Login")
public class Login extends ApiSupport {

    /**
     * ログイン処理を行います。
     *
     * @param user ユーザ名
     * @param password PCCユーザパスワード
     * @return LoginResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LoginResponse login(@QueryParam(PARAM_NAME_USER) String userName,
            @QueryParam(PARAM_NAME_PASSWORD) String password) {

        LoginResponse response = new LoginResponse();

        // 入力チェック
        // User
        ApiValidate.validateUser(userName);
        // Password
        ApiValidate.validatePassword(password);

        // ログイン認証
        userService.authenticate(userName, password);

        response.setSuccess(true);

        return response;
    }

}
