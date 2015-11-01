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
package jp.primecloud.auto.api.lb;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.lb.DeleteLoadBalancerResponse;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/DeleteLoadBalancer")
public class DeleteLoadBalancer extends ApiSupport {

    /**
     *
     * ロードバランサ削除
     *
     * @param farmNo ファーム番号
     * @param loadBalancerNo ロードバランサ番号
     *
     * @return DeleteLoadBalancerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public DeleteLoadBalancerResponse deleteLoadBalancer(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo){

        DeleteLoadBalancerResponse response = new DeleteLoadBalancerResponse();

            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // LoadBalancerNo
            ApiValidate.validateLoadBalancerNo(loadBalancerNo);

            // ロードバランサ取得
            LoadBalancer loadBalancer = loadBalancerDao.read(Long.parseLong(loadBalancerNo));
            if (loadBalancer == null) {
                // ロードバランサが存在しない
                throw new AutoApplicationException("EAPI-100000", "LoadBalancer",
                        PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }
            if (BooleanUtils.isFalse(loadBalancer.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとロードバランサが一致しない
                throw new AutoApplicationException("EAPI-100022", "LoadBalancer", farmNo, PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }

            // ロードバランサーのステータスチェック
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
            if (LoadBalancerStatus.STOPPED != status) {
                // ステータスが 停止済みではない
                throw new AutoApplicationException("EAPI-100023", loadBalancerNo);
            }

            // ロードバランサ 削除
            loadBalancerService.deleteLoadBalancer(Long.parseLong(loadBalancerNo));

            response.setSuccess(true);

        return  response;
    }
}