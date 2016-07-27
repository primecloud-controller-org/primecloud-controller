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
import jp.primecloud.auto.api.response.lb.StartLoadBalancerResponse;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.StringUtils;

@Path("/StartLoadBalancer")
public class StartLoadBalancer extends ApiSupport {

    /**
     * ロードバランサ起動
     *
     * @param loadBalancerNo ロードバランサ番号
     * @return StartLoadBalancerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StartLoadBalancerResponse startLoadBalancer(@QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo) {

        StartLoadBalancerResponse response = new StartLoadBalancerResponse();

        // 入力チェック
        // LoadBalancerNo
        ApiValidate.validateLoadBalancerNo(loadBalancerNo);

        // ロードバランサ取得
        LoadBalancer loadBalancer = getLoadBalancer(Long.parseLong(loadBalancerNo));

        // 権限チェック
        checkAndGetUser(loadBalancer);

        // ロードバランサーのステータスチェック
        LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
        if (LoadBalancerStatus.STOPPED != status) {
            // ステータスが 停止済みではない
            throw new AutoApplicationException("EAPI-100020", loadBalancerNo);
        }

        if (LB_TYPE_ELB.equals(loadBalancer.getType())) {
            PlatformAws platformAws = platformAwsDao.read(loadBalancer.getPlatformNo());
            AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(Long.parseLong(loadBalancerNo));
            if (platformAws.getVpc() && StringUtils.isEmpty(awsLoadBalancer.getSubnetId())) {
                //ELB+VPCの場合、サブネットを設定しないと起動不可
                throw new AutoApplicationException("EAPI-100035", loadBalancer.getLoadBalancerName());
            }
        }

        // ロードバランサ 起動設定処理
        List<Long> lbNos = new ArrayList<Long>();
        lbNos.add(Long.parseLong(loadBalancerNo));
        processService.startLoadBalancers(loadBalancer.getFarmNo(), lbNos);

        response.setSuccess(true);

        return response;
    }

}
