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


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import jp.primecloud.auto.api.response.farm.EditFarmResponse;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/EditFarm")
public class EditFarm extends ApiSupport {

    /**
     *
     * ファーム編集
     *
     * @param farmNo ファーム番号
     * @param comment コメント
     *
     * @return EditFarmResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public EditFarmResponse editFarm(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_COMMENT) String comment){

        EditFarmResponse response = new EditFarmResponse();

            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            Farm farm = farmDao.read(Long.parseLong(farmNo));
            if (farm == null) {
                // ファームが存在しない場合
                throw new AutoApplicationException("EAPI-100000", "Farm", PARAM_NAME_FARM_NO, farmNo);
            }
            // Comment
            ApiValidate.validateComment(comment);

            // ファーム更新
            farmService.updateFarm(Long.parseLong(farmNo), comment, farm.getDomainName());

            response.setSuccess(true);

        return  response;
    }
}