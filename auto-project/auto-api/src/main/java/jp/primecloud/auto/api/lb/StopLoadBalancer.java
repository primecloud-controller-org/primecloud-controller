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


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.lb.StopLoadBalancerResponse;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;



@Path("/StopLoadBalancer")
public class StopLoadBalancer extends ApiSupport {

    /**
     *
     * ロードバランサ停止
     *
     * @param farmNo ファーム番号
     * @param loadBalancerNo ロードバランサ番号
     *
     * @return StopLoadBalancerResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public StopLoadBalancerResponse stopLoadBalancer(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo){

        StopLoadBalancerResponse response = new StopLoadBalancerResponse();

        try {
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

            // ロードバランサーのステータスチェック
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
            if (LoadBalancerStatus.RUNNING != status && LoadBalancerStatus.WARNING != status) {
                // ステータスが 起動済み or 警告 ではない
                throw new AutoApplicationException("EAPI-100021", loadBalancerNo);
            }

            if (BooleanUtils.isFalse(loadBalancer.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとロードバランサーが一致しない
                throw new AutoApplicationException("EAPI-100022", "LoadBalancer", farmNo, PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }

            // ロードバランサ 停止設定処理
            List<Long> lbNos = new ArrayList<Long>();
            lbNos.add(Long.parseLong(loadBalancerNo));
            processService.stopLoadBalancers(Long.parseLong(farmNo), lbNos);

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