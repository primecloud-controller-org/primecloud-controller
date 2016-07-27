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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.instance.StopAllInstanceResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;

import org.apache.commons.lang.BooleanUtils;

@Path("/StopAllInstance")
public class StopAllInstance extends ApiSupport {

    /**
     * サーバ停止(ALL) ファーム内のすべてのサーバを起動
     *
     * @param farmNo ファーム番号
     * @return StopAllInstanceResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StopAllInstanceResponse stoptAllInstance(@QueryParam(PARAM_NAME_FARM_NO) String farmNo) {

        // 入力チェック
        // FarmNo
        ApiValidate.validateFarmNo(farmNo);

        // ファーム取得
        Farm farm = getFarm(Long.parseLong(farmNo));

        // 権限チェック
        checkAndGetUser(farm);

        // インスタンス取得
        List<Long> instanceNos = new ArrayList<Long>();
        List<Instance> instances = instanceDao.readByFarmNo(Long.parseLong(farmNo));
        for (Instance instance : instances) {
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                //ロードバランサ以外
                continue;
            }
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
            if (InstanceStatus.RUNNING == status) {
                //ファーム内の起動済みのインスタンスが停止対象
                instanceNos.add(instance.getInstanceNo());
            }
        }

        // サーバ停止設定処理
        processService.stopInstances(Long.parseLong(farmNo), instanceNos);

        StopAllInstanceResponse response = new StopAllInstanceResponse();

        return response;
    }

}
