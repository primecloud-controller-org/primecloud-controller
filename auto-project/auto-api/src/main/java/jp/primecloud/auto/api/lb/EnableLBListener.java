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

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.lb.EnableLBListenerResponse;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/EnableLBListener")
public class EnableLBListener extends ApiSupport {

    /**
     *
     * ロードバランサリスナ 有効化
     *
     * @param farmNo ファーム番号
     * @param loadBalancerNo ロードバランサ番号
     * @param loadBalancerPort ポート番号
     *
     * @return EnableLBListenerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public EnableLBListenerResponse enableLBListener(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo,
	        @QueryParam(PARAM_NAME_LOAD_BALANCER_PORT) String loadBalancerPort){

        EnableLBListenerResponse response = new EnableLBListenerResponse();

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

            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
            if (LoadBalancerStatus.RUNNING != status) {
                //ロードバランサが起動済みではない
                throw new AutoApplicationException("EAPI-100028", loadBalancerNo, loadBalancerPort);
            }

            // 入力チェック
            // LoadBalancerPort
            ApiValidate.validateLoadBalancerPort(loadBalancer.getType(), loadBalancerPort);

            LoadBalancerListener loadBalancerListener = loadBalancerListenerDao.read(Long.parseLong(loadBalancerNo), Integer.parseInt(loadBalancerPort));
            if (loadBalancerListener == null) {
                //ロードバランサリスナが存在しない
                throw new AutoApplicationException("EAPI-100030", "LoadBalancerListener",
                        PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo, PARAM_NAME_LOAD_BALANCER_PORT, loadBalancerPort);
            }

            // プラットフォーム取得
            Platform platform = platformDao.read(loadBalancer.getPlatformNo());
            if(platform == null) {
                //プラットフォームが存在しない
                throw new AutoApplicationException("EAPI-100000", "Platform",
                        PARAM_NAME_PLATFORM_NO, loadBalancer.getPlatformNo());
            }

            if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                // プラットフォームがCloudStackの場合は処理を行わず終了
                response.setSuccess(true);
                return response;
            }

            // ロードバランサリスナ 有効化
            List<Integer> lbPorts = new ArrayList<Integer>();
            lbPorts.add(Integer.parseInt(loadBalancerPort));
            processService.startLoadBalancerListeners(
                    Long.parseLong(farmNo), Long.parseLong(loadBalancerNo), lbPorts);

            response.setSuccess(true);

        return  response;
	}
}