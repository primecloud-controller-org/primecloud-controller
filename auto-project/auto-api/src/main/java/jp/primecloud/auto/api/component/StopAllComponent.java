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
import jp.primecloud.auto.api.response.component.StopAllComponentResponse;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Farm;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

@Path("/StopAllComponent")
public class StopAllComponent extends ApiSupport {

    /**
     * サービス停止(ALL)
     * @param farmNo ファーム番号
     * @param isStopInstance サーバ停止有無 true:サーバも停止、false:サービスのみ停止
     * @return StopComponentResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StopAllComponentResponse stopComponent(@QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_IS_STOP_INSTANCE) String isStopInstance) {

        // 入力チェック
        // FarmNo
        ApiValidate.validateFarmNo(farmNo);
        // IsStopInstance
        ApiValidate.validateIsStopInstance(isStopInstance);

        // ファーム取得
        Farm farm = getFarm(Long.parseLong(farmNo));

        // 権限チェック
        checkAndGetUser(farm);

        // コンポーネント取得
        List<Component> components = componentDao.readByFarmNo(Long.parseLong(farmNo));
        List<Long> componentNos = new ArrayList<Long>();
        for (Component component : components) {
            if (BooleanUtils.isTrue(component.getLoadBalancer())) {
                //ロードバランサコンポーネントは対象外
                continue;
            }
            componentNos.add(component.getComponentNo());
        }

        // サービス停止設定
        if (StringUtils.isEmpty(isStopInstance)) {
            processService.stopComponents(Long.parseLong(farmNo), componentNos);
        } else {
            processService.stopComponents(Long.parseLong(farmNo), componentNos, Boolean.parseBoolean(isStopInstance));
        }

        StopAllComponentResponse response = new StopAllComponentResponse();

        return response;
    }

}
