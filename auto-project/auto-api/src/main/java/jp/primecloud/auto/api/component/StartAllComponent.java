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
package jp.primecloud.auto.api.component;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.component.StartAllComponentResponse;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/StartAllComponent")
public class StartAllComponent extends ApiSupport{

    /**
     *
     * サービス起動処理(All) ファーム内のすべてのサービスを起動
     *
     * @param farmNo ファーム番号
     * @return StartAllComponentResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public StartAllComponentResponse startAllComponent(
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo){

        StartAllComponentResponse response = new StartAllComponentResponse();

            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);

            // ファーム取得
            Farm farm = farmDao.read(Long.parseLong(farmNo));
            if (farm == null) {
                // ファームが存在しない
                throw new AutoApplicationException("EAPI-100000", "Farm", PARAM_NAME_FARM_NO, farmNo);
            }

            // コンポーネント取得
            List<Component> components = componentDao.readByFarmNo(Long.parseLong(farmNo));
            List<Long> componentNos = new ArrayList<Long>();
            for (Component component: components) {
                if (BooleanUtils.isTrue(component.getLoadBalancer())) {
                    //ロードバランサコンポーネントは対象外
                    continue;
                }
                componentNos.add(component.getComponentNo());
            }

            // サービス起動設定
            processService.startComponents(Long.parseLong(farmNo), componentNos);

            response.setSuccess(true);

        return  response;
	}
}