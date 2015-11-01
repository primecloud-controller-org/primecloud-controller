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

import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.api.response.instance.EnableZabbixMonitoringInstanceResponse;


@Path("/EnableZabbixMonitoringInstance")
public class EnableZabbixMonitoringInstance extends ApiSupport {

    /**
     *
     * Zabbix監視有効化(サーバ)
     *
     * @param farmNo ファーム番号
     * @param instanceNo インスタンス番号
     * @return EnableZabbixMonitoringInstanceResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public EnableZabbixMonitoringInstanceResponse enableZabbixMonitoringInstance(
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo) {

        EnableZabbixMonitoringInstanceResponse response = new EnableZabbixMonitoringInstanceResponse();

            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // InstanceNo
            // TODO モック用にコメントアウト
            //ApiValidate.validateInstanceNo(instanceNo);

            // Zabbix監視有効化(サーバ)
            //instanceService.enableZabbixMonitoring(Long.parseLong(instanceNo));
            // TODO モック用の処理
            instanceService.enableZabbixMonitoring((StringUtils.isEmpty(instanceNo)) ? null: Long.parseLong(instanceNo));

            response.setSuccess(true);

        return  response;
    }
}