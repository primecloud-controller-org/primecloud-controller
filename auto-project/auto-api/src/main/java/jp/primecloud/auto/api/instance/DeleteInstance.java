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


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.instance.DeleteInstanceResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/DeleteInstance")
public class DeleteInstance extends ApiSupport {
    /**
     *
     * サーバ削除
     *
     * @param farmNo ファーム番号
     * @param instanceNo インスタンス番号
     * @return DeleteInstanceResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public DeleteInstanceResponse deleteInstance(
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo) {

        DeleteInstanceResponse response = new DeleteInstanceResponse();

            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // InstanceNo
            ApiValidate.validateInstanceNo(instanceNo);

            // インスタンス取得
            Instance instance = instanceDao.read(Long.parseLong(instanceNo));
            if (instance == null) {
                // インスタンスが存在しない
                throw new AutoApplicationException("EAPI-100000", "Instance",
                        PARAM_NAME_INSTANCE_NO, instanceNo);
            }

            if (BooleanUtils.isFalse(instance.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとインスタンスが一致しない
                throw new AutoApplicationException("EAPI-100022", "Instance", farmNo, PARAM_NAME_INSTANCE_NO, instanceNo);
            }

            // インスタンスの停止チェック
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
            if (InstanceStatus.STOPPED != status) {
                // インスタンスが停止状態でない場合
                throw new AutoApplicationException("EAPI-100007", instanceNo);
            }

            // サーバ削除処理(プロセス処理)
            instanceService.deleteInstance(Long.parseLong(instanceNo));

            response.setSuccess(true);

        return  response;
    }
}