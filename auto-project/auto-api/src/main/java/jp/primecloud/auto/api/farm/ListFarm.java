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
package jp.primecloud.auto.api.farm;


import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import jp.primecloud.auto.api.response.farm.FarmResponse;
import jp.primecloud.auto.api.response.farm.ListFarmResponse;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.User;


@Path("/ListFarm")
public class ListFarm extends ApiSupport {

    /**
     *
     * ファーム一覧取得
     *
     * @param userName ユーザ名
     *
     * @return ListFarmResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ListFarmResponse listFarm(
            @QueryParam(PARAM_NAME_USER) String userName){

        ListFarmResponse response = new ListFarmResponse();

            // 入力チェック
            // User
            ApiValidate.validateUser(userName);

            // ユーザの取得
            User user = userDao.readByUsername(userName);

            // ファームの取得
            List<Farm> farms = farmDao.readByUserNo(user.getUserNo());
            for (Farm farm: farms) {
                FarmResponse farmResponse = new FarmResponse(farm);
                response.addFarm(farmResponse);
            }

            response.setSuccess(true);

        return  response;
    }
}