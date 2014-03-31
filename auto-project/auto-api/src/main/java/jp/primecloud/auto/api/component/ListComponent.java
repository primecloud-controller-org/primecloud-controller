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
package jp.primecloud.auto.api.component;


import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.component.ComponentResponse;
import jp.primecloud.auto.api.response.component.ListComponentResponse;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.impl.Comparators;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;



@Path("/ListComponent")
public class ListComponent extends ApiSupport {

    /**
     *
     * サービス情報取得 ファームのサービスをすべて取得
     *
     * @param farmNo ファーム番号
     *
     * @return ListComponentResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ListComponentResponse listComponent(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo){

        ListComponentResponse response = new ListComponentResponse();

        try {
            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);

            // ファーム取得
            Farm farm = farmDao.read(Long.parseLong(farmNo));
            if (farm == null) {
                // ファームが存在しない
                throw new AutoApplicationException("EAPI-100000", "Farm", PARAM_NAME_FARM_NO, farmNo);
            }

            // コンポーネント取得
            List<Component> components = componentDao.readByFarmNo(Long.valueOf(farmNo));
            if (components.isEmpty() == false) {
                //ソート
                Collections.sort(components, Comparators.COMPARATOR_COMPONENT);
            }

            for (Component component: components) {
                if (BooleanUtils.isTrue(component.getLoadBalancer())) {
                    //ロードバランサコンポーネントは除く
                    continue;
                }

                //コンポーネント情報設定
                ComponentResponse componentResponse = new ComponentResponse(component);

                //コンポーネントインスタンス取得
                List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(component.getComponentNo());
                if (componentInstances.isEmpty() == false) {
                    //ソート
                    Collections.sort(componentInstances, Comparators.COMPARATOR_COMPONENT_INSTANCE);
                    Integer instanceCount = 0;
                    for (ComponentInstance componentInstance: componentInstances) {
                        // 関連付けが無効で停止している場合は除外
                        if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                            if (status == ComponentInstanceStatus.STOPPED) {
                                continue;
                            }
                        }
                        instanceCount++;
                    }
                    //インスタンス数 設定
                    componentResponse.setInstanceCount(instanceCount);
                }

                //ロードバランサ取得
                LoadBalancer loadBalancer = null;
                List<LoadBalancer> loadBalancers = loadBalancerDao.readByComponentNo(component.getComponentNo());
                if (loadBalancers.isEmpty() == false) {
                    //ソート
                    Collections.sort(loadBalancers, Comparators.COMPARATOR_LOAD_BALANCER);
                    //ロードバランサは上記ソートの1件目を取得
                    loadBalancer = loadBalancers.get(0);

                    //ロードバランサ名 設定
                    componentResponse.setLoadBalancerName(loadBalancer.getLoadBalancerName());
                }

                response.addComponents(componentResponse);
            }

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