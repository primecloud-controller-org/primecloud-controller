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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.lb.ListLoadBalancerListenerResponse;
import jp.primecloud.auto.api.response.lb.LoadBalancerListenerResponse;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;

@Path("/ListLoadBalancerListener")
public class ListLoadBalancerListener extends ApiSupport {

    /**
     * ロードバランサ リスナー一覧取得
     *
     * @param loadBalancerNo ロードバランサ番号
     * @return ListLoadBalancerListenerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListLoadBalancerListenerResponse describeLoadBalancerHealthCheck(
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo) {

        // 入力チェック
        // LoadBalancerNo
        ApiValidate.validateLoadBalancerNo(loadBalancerNo);

        // ロードバランサ取得
        LoadBalancer loadBalancer = getLoadBalancer(Long.parseLong(loadBalancerNo));

        // 権限チェック
        checkAndGetUser(loadBalancer);

        ListLoadBalancerListenerResponse response = new ListLoadBalancerListenerResponse();

        List<LoadBalancerListener> loadBalancerListeners = loadBalancerListenerDao.readByLoadBalancerNo(Long
                .parseLong(loadBalancerNo));
        for (LoadBalancerListener listener : loadBalancerListeners) {
            LoadBalancerListenerResponse listenerResponse = new LoadBalancerListenerResponse(listener);
            response.getLoadBalancerListeners().add(listenerResponse);
        }

        return response;
    }

}
