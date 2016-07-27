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

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.lb.ListLoadBalancerResponse;
import jp.primecloud.auto.api.response.lb.LoadBalancerResponse;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.service.impl.Comparators;

import org.apache.commons.lang.BooleanUtils;

@Path("/ListLoadBalancer")
public class ListLoadBalancer extends ApiSupport {

    /**
     * ロードバランサ情報一覧取得
     *
     * @param farmNo ファーム番号
     * @return ListLoadBalancerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListLoadBalancerResponse listLoadBalancer(@QueryParam(PARAM_NAME_FARM_NO) String farmNo) {

        ListLoadBalancerResponse response = new ListLoadBalancerResponse();

        // 入力チェック
        // LoadBalancerNo
        ApiValidate.validateFarmNo(farmNo);

        // 権限チェック
        Farm farm = farmDao.read(Long.parseLong(farmNo));
        checkAndGetUser(farm);

        // ロードバランサ取得
        List<LoadBalancer> loadBalancers = loadBalancerDao.readByFarmNo(Long.parseLong(farmNo));
        if (BooleanUtils.isFalse(loadBalancers.isEmpty())) {
            //ソート
            Collections.sort(loadBalancers, Comparators.COMPARATOR_LOAD_BALANCER);
        }

        for (LoadBalancer loadBalancer : loadBalancers) {
            //ロードバランサ情報設定
            LoadBalancerResponse loadBalancerResponse = new LoadBalancerResponse(loadBalancer);

            // コンポーネント取得
            Component component = componentDao.read(loadBalancer.getComponentNo());
            if (component != null) {
                //コンポーネント名設定
                loadBalancerResponse.setComponentName(component.getComponentName());
            }
            response.getLoadBalancers().add(loadBalancerResponse);
        }

        response.setSuccess(true);

        return response;
    }

}
