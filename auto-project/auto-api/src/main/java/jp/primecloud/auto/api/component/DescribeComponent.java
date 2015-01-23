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

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.component.ComponentInstanceResponse;
import jp.primecloud.auto.api.response.component.DescribeComponentResponse;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.impl.Comparators;
import jp.primecloud.auto.util.MessageUtils;


@Path("/DescribeComponent")
public class DescribeComponent extends ApiSupport {

    /**
     *
     * サービス情報取得
     *
     * @param farmNo ファーム番号
     * @param componentNo コンポーネント番号
     *
     * @return DescribeComponentResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DescribeComponentResponse describeComponent(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo){

        DescribeComponentResponse response = new DescribeComponentResponse();

        try {
            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // ComponentNo
            ApiValidate.validateComponentNo(componentNo);

            // コンポーネント取得
            Component component = componentDao.read(Long.parseLong(componentNo));
            if (component == null || BooleanUtils.isTrue(component.getLoadBalancer())) {
                // コンポーネントが存在しない または ロードバランサーコンポーネント
                throw new AutoApplicationException("EAPI-100000", "Component", PARAM_NAME_COMPONENT_NO, componentNo);
            }

            if (component.getFarmNo().equals(Long.parseLong(farmNo)) == false) {
                //ファームとコンポーネントが一致しない
                throw new AutoApplicationException("EAPI-100022", "Component", farmNo, PARAM_NAME_COMPONENT_NO, componentNo);
            }

            //コンポーネント情報設定
            response = new DescribeComponentResponse(component);

            List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(Long.parseLong(componentNo));
            if (componentInstances.isEmpty() == false) {
                //ソート
                Collections.sort(componentInstances, Comparators.COMPARATOR_COMPONENT_INSTANCE);
                for (ComponentInstance componentInstance: componentInstances) {
                    // 関連付けが無効で停止している場合は除外
                    if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                        ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                        if (status == ComponentInstanceStatus.STOPPED) {
                            continue;
                        }
                    }
                    //コンポーネントインスタンス情報設定
                    response.addInstance(new ComponentInstanceResponse(componentInstance));
                }
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