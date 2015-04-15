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

import jp.primecloud.auto.api.response.lb.DeleteLoadBalancerListenerResponse;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;


@Path("/DeleteLoadBalancerListener")
public class DeleteLoadBalancerListener extends ApiSupport {

    /**
     *
     * ロードバランサリスナ削除
     *
     * @param farmNo ファーム番号
     * @param loadBalancerNo ロードバランサ番号
     * @param loadBalancerPort ポート番号
     *
     * @return DeleteLoadBalancerListenerResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public DeleteLoadBalancerListenerResponse deleteLoadBalancerListener(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo,
	        @QueryParam(PARAM_NAME_LOAD_BALANCER_PORT) String loadBalancerPort){

        DeleteLoadBalancerListenerResponse response = new DeleteLoadBalancerListenerResponse();

        try {
            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // LoadBalancerNo
            ApiValidate.validateLoadBalancerNo(loadBalancerNo);

            LoadBalancer loadBalancer = loadBalancerDao.read(Long.parseLong(loadBalancerNo));
            if (loadBalancer == null) {
                //ロードバランサが存在しない
                throw new AutoApplicationException("EAPI-100000", "LoadBalancer",
                        PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }
            if (BooleanUtils.isFalse(loadBalancer.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとロードバランサが一致しない
                throw new AutoApplicationException("EAPI-100022", "LoadBalancer", farmNo, PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }

            // プラットフォーム取得
            Platform platform = platformDao.read(loadBalancer.getPlatformNo());
            if(platform == null) {
                throw new AutoApplicationException("EAPI-100000", "Platform",
                        PARAM_NAME_PLATFORM_NO, loadBalancer.getPlatformNo());
            }

            if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                // プラットフォームがCloudStackの場合は処理を行わず終了
                response.setSuccess(true);
                return response;
            }

            // 入力チェック
            // LoadBalancerPort
            ApiValidate.validateLoadBalancerPort(loadBalancer.getType(), loadBalancerPort);

            // ロードバランサリスナ削除
            loadBalancerService.deleteListener(Long.parseLong(loadBalancerNo), Integer.parseInt(loadBalancerPort));

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