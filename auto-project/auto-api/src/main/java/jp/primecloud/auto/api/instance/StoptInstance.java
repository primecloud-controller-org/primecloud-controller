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
import jp.primecloud.auto.api.response.instance.StopInstanceResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;



@Path("/StopInstance")
public class StoptInstance extends ApiSupport {

    /**
     *
     * サーバ停止処理
     *
     * @param farmNo ファーム番号
     * @param instanceNo インスタンス番号
     *
     * @return StopInstanceResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public StopInstanceResponse stoptInstance(
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo){

        StopInstanceResponse response = new StopInstanceResponse();

        try {
            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // InstanceNo
            ApiValidate.validateInstanceNo(instanceNo);

            // インスタンス取得
            Instance instance = instanceDao.read(Long.parseLong(instanceNo));
            if (instance == null || BooleanUtils.isTrue(instance.getLoadBalancer())) {
                // インスタンスが存在しない or インスタンスがロードバランサの場合
                throw new AutoApplicationException("EAPI-100000", "Instance",
                        PARAM_NAME_INSTANCE_NO, instanceNo);
            }

            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
            if (InstanceStatus.RUNNING != status && InstanceStatus.WARNING != status) {
                // インスタンスのステータスが 起動済み or 警告 ではない。
                throw new AutoApplicationException("EAPI-100024", instanceNo);
            }

            if (BooleanUtils.isFalse(instance.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとインスタンスが一致しない
                throw new AutoApplicationException("EAPI-100022", "Instance", farmNo, PARAM_NAME_INSTANCE_NO, instanceNo);
            }

            // サーバ停止設定処理
            List<Long> instanceNos = new ArrayList<Long>();
            instanceNos.add(Long.parseLong(instanceNo));
            processService.stopInstances(Long.parseLong(farmNo), instanceNos);

            response.setSuccess(true);
        } catch (Throwable e){
            String message = "";
            if (e instanceof AutoException || e instanceof AutoApplicationException) {
                message = e.getMessage();
            } else {
                message = MessageUtils.getMessage("EAPI-000000");
            }
            log.error(message, e);
            response.setMessage(message);
            response.setSuccess(false);
        }

        return  response;
    }
}