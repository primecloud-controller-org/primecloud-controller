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
import jp.primecloud.auto.api.response.lb.DescribeLoadBalancerHealthCheckResponse;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.exception.AutoApplicationException;

@Path("/DescribeLoadBalancerHealthCheck")
public class DescribeLoadBalancerHealthCheck extends ApiSupport {

    /**
     * ロードバランサ(ヘルスチェック)情報取得
     *
     * @param loadBalancerNo ロードバランサ番号
     * @return DescribeLoadBalancerHealthCheckResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DescribeLoadBalancerHealthCheckResponse describeLoadBalancerHealthCheck(
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo) {

        // 入力チェック
        // LoadBalancerNo
        ApiValidate.validateLoadBalancerNo(loadBalancerNo);

        // ロードバランサ取得
        LoadBalancer loadBalancer = getLoadBalancer(Long.parseLong(loadBalancerNo));

        // 権限チェック
        checkAndGetUser(loadBalancer);

        // ヘルスチェック取得
        LoadBalancerHealthCheck healthCheck = loadBalancerHealthCheckDao.read(Long.parseLong(loadBalancerNo));
        if (healthCheck == null) {
            // ヘルスチェックが存在しない
            throw new AutoApplicationException("EAPI-100000", "LoadBalancerHealthCheck", PARAM_NAME_LOAD_BALANCER_NO,
                    loadBalancerNo);
        }

        DescribeLoadBalancerHealthCheckResponse response = new DescribeLoadBalancerHealthCheckResponse(loadBalancer,
                healthCheck);

        return response;
    }

}
