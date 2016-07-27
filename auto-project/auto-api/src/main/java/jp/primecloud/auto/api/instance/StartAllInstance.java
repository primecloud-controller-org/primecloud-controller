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
import jp.primecloud.auto.api.response.instance.StartAllInstanceResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

@Path("/StartAllInstance")
public class StartAllInstance extends ApiSupport {

    /**
     * サーバ起動(ALL) ファーム内のすべてのサーバを起動
     *
     * @param farmNo ファーム番号
     * @param isStartService サービス起動有無 true:サービスも起動、false:サーバのみ起動、null:サーバのみ起動
     * @return StartAllInstanceResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StartAllInstanceResponse startAllInstance(@QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_IS_START_SERVICE) String isStartService) {

        StartAllInstanceResponse response = new StartAllInstanceResponse();

        // 入力チェック
        // FarmNo
        ApiValidate.validateFarmNo(farmNo);
        // IsStartService
        ApiValidate.validateIsStartService(isStartService);

        // データチェック
        // ファーム
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
            if (InstanceStatus.STOPPED == status) {
                //ファーム内の停止済みのインスタンスが起動対象
                instanceNos.add(instance.getInstanceNo());
            }
        }

        // サーバ起動設定処理
        if (StringUtils.isEmpty(isStartService)) {
            processService.startInstances(Long.parseLong(farmNo), instanceNos);
        } else {
            processService.startInstances(Long.parseLong(farmNo), instanceNos, Boolean.parseBoolean(isStartService));
        }

        response.setSuccess(true);

        return response;
    }

}
