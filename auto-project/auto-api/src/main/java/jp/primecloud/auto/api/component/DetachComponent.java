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


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.component.DetachComponentResponse;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;



@Path("/DetachComponent")
public class DetachComponent extends ApiSupport {

    /**
     *
     * サービスとインスタンスの紐づけの解除
     *
     * @param farmNo ファーム番号
     * @param componentNo コンポーネント番号
     * @param instanceNo インスタンス番号
     * @return DetachComponentResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DetachComponentResponse detachComponent(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo,
            @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo){

        DetachComponentResponse response = new DetachComponentResponse();

        try {
            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // ComponentNo
            ApiValidate.validateComponentNo(componentNo);
            // InstanceNo
            ApiValidate.validateInstanceNo(instanceNo);

            // コンポーネント取得
            Component component = componentDao.read(Long.parseLong(componentNo));
            if (component == null) {
                // コンポーネントが存在しない
                throw new AutoApplicationException("EAPI-100000", "Component", PARAM_NAME_COMPONENT_NO, componentNo);
            }

            if (BooleanUtils.isFalse(component.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとコンポーネントが一致しない
                throw new AutoApplicationException("EAPI-100022", "Component", farmNo, PARAM_NAME_COMPONENT_NO, componentNo);
            }

            // 現在サービスに紐づいているサーバの一覧を取得する
            List<Long> instanceNos = new ArrayList<Long>();
            List<ComponentInstance> componentInstances =  componentInstanceDao.readByComponentNo(Long.parseLong(componentNo));
            for (ComponentInstance componentInstance: componentInstances) {
                if (componentInstance.getInstanceNo().equals(Long.parseLong(instanceNo))) {
                    ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                    if (status == ComponentInstanceStatus.RUNNING) {
                        // サービス起動中は割り当て解除できない
                        throw new AutoApplicationException("EAPI-100013", componentNo, instanceNo);
                    }
                }
                if(!instanceNos.contains(componentInstance.getInstanceNo())) {
                    instanceNos.add(componentInstance.getInstanceNo());
                }
            }

            // サーバの一覧から当該サーバのインスタンス番号を削除
            instanceNos.remove(Long.parseLong(instanceNo));

            // サービスとサーバの紐づけ解除(プロセス処理)
            componentService.associateInstances(Long.parseLong(componentNo), instanceNos);

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