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
import jp.primecloud.auto.api.response.lb.DetachLoadBalancerResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;

@Path("/DetachLoadBalancer")
public class DetachLoadBalancer extends ApiSupport {

    /**
     * ロードバランサ サーバ割り当て無効化
     * 
     * @param loadBalancerNo ロードバランサ番号
     * @param instanceNo インスタンス番号
     * @return DetachLoadBalancerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DetachLoadBalancerResponse detachLoadBalancer(
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo,
            @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo) {

        // 入力チェック
        // LoadBalancerNo
        ApiValidate.validateLoadBalancerNo(loadBalancerNo);
        // InstanceNo
        ApiValidate.validateInstanceNo(instanceNo);

        // ロードバランサ取得
        LoadBalancer loadBalancer = getLoadBalancer(Long.parseLong(loadBalancerNo));

        // 権限チェック
        checkAndGetUser(loadBalancer);

        // インスタンス取得
        Instance instance = getInstance(Long.parseLong(instanceNo));

        if (BooleanUtils.isFalse(instance.getFarmNo().equals(loadBalancer.getFarmNo()))) {
            //ファームとインスタンスが一致しない
            throw new AutoApplicationException("EAPI-100022", "Instance", loadBalancer.getFarmNo(),
                    PARAM_NAME_INSTANCE_NO, instanceNo);
        }

        // サーバのステータスチェック
        InstanceStatus instanceStatus = InstanceStatus.fromStatus(instance.getStatus());
        if (instanceStatus == InstanceStatus.CONFIGURING) {
            // ステータスが Configuring の場合は割り当て解除不可
            throw new AutoApplicationException("EAPI-100002", loadBalancerNo, instanceNo);
        }

        // ロードバランサ サーバ割り当て無効化設定処理
        List<Long> instanceNos = new ArrayList<Long>();
        instanceNos.add(Long.parseLong(instanceNo));
        loadBalancerService.disableInstances(Long.parseLong(loadBalancerNo), instanceNos);

        DetachLoadBalancerResponse response = new DetachLoadBalancerResponse();

        return response;
    }

}
