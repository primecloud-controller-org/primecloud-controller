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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.component.DeleteComponentResponse;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.exception.AutoApplicationException;

@Path("/DeleteComponent")
public class DeleteComponent extends ApiSupport {

    /**
     * サービス削除
     *
     * @param componentNo コンポーネント番号
     * @return StopComponentResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DeleteComponentResponse deleteComponent(@QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo) {

        DeleteComponentResponse response = new DeleteComponentResponse();

        // 入力チェック
        //ComponentNo
        ApiValidate.validateComponentNo(componentNo);

        // コンポーネント取得
        Component component = getComponent(Long.parseLong(componentNo));

        // 権限チェック
        checkAndGetUser(component);

        // コンポーネントインスタンス取得
        List<ComponentInstance> componentInstances = componentInstanceDao
                .readByComponentNo(Long.parseLong(componentNo));
        for (ComponentInstance componentInstance : componentInstances) {
            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
            if (status != ComponentInstanceStatus.STOPPED) {
                // コンポーネントが停止状態でない場合
                throw new AutoApplicationException("EAPI-100008", componentNo);
            }
        }

        // サービス削除処理
        componentService.deleteComponent(Long.parseLong(componentNo));

        response.setSuccess(true);

        return response;
    }

}
