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
package jp.primecloud.auto.api.instance;


import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.instance.InstanceResponse;
import jp.primecloud.auto.api.response.instance.ListInstanceResponse;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.service.impl.Comparators;


@Path("/ListInstance")
public class ListInstance extends ApiSupport {

    /**
     *
     * サーバ情報取得 ファームに存在するすべてのサーバ
     *
     * @param farmNo ファーム番号
     *
     * @return ListInstanceResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public ListInstanceResponse listInstance(
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo){

        ListInstanceResponse response = new ListInstanceResponse();

            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);

            // ファーム取得
            Farm farm = getFarm(Long.parseLong(farmNo));

            // 権限チェック
            checkAndGetUser(farm);

            // インスタンス取得
            List<Instance> instances = instanceDao.readByFarmNo(Long.parseLong(farmNo));
            if (instances.isEmpty() == false) {
                //ソート
                Collections.sort(instances, Comparators.COMPARATOR_INSTANCE);
            }

            for (Instance instance: instances) {
                if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                    // ロードバランサのインスタンスは除く
                    continue;
                }
                //インスタンス情報設定
                response.addInstance(new InstanceResponse(instance));
            }

            response.setSuccess(true);

        return  response;
	}
}