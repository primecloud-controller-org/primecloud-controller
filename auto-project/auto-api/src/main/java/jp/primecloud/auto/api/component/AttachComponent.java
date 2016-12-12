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
import jp.primecloud.auto.api.response.component.AttachComponentResponse;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

@Path("/AttachComponent")
public class AttachComponent extends ApiSupport {

    /**
     * サービスのインスタンスへの紐づけ
     *
     * @param componentNo コンポーネント番号
     * @param instanceNo インスタンス番号
     * @return AttachComponentResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AttachComponentResponse attachComponent(@QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo,
            @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo) {

        // 入力チェック
        // ComponentNo
        ApiValidate.validateComponentNo(componentNo);
        // InstanceNo
        ApiValidate.validateInstanceNo(instanceNo);

        // コンポーネント取得
        Component component = getComponent(Long.parseLong(componentNo));

        // 権限チェック
        checkAndGetUser(component);

        // インスタンス取得
        Instance instance = getInstance(Long.parseLong(instanceNo));

        if (BooleanUtils.isFalse(instance.getFarmNo().equals(component.getFarmNo()))) {
            //ファームとインスタンスが一致しない
            throw new AutoApplicationException("EAPI-100022", "Instance", component.getFarmNo(),
                    PARAM_NAME_INSTANCE_NO, instanceNo);
        }

        // イメージ取得
        Image image = imageDao.read(instance.getImageNo());
        List<Long> componentTypeNos = new ArrayList<Long>();
        for (String componentTypeNo : image.getComponentTypeNos().split(",")) {
            componentTypeNos.add(Long.parseLong(componentTypeNo.trim()));
        }

        // 割り当て可能コンポーネントチェック
        if (!componentTypeNos.contains(component.getComponentTypeNo())) {
            // インスタンスのイメージに対象サービスのコンポーネントタイプが含まれていない場合はエラー
            throw new AutoApplicationException("EAPI-100003", componentNo, instanceNo);
        }

        // 割り当て対象対象サーバに紐づくサービスのレイヤチェック
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        List<ComponentInstance> instanceComponents = componentInstanceDao.readByInstanceNo(Long.parseLong(instanceNo));
        for (ComponentInstance componentInstance : instanceComponents) {
            if (componentInstance.getComponentNo().equals(Long.parseLong(componentNo))) {
                // すでにComponentInstanceのレコードが存在している
                continue;
            }
            Component tmpComponent = componentDao.read(componentInstance.getComponentNo());
            ComponentType componentType2 = componentTypeDao.read(tmpComponent.getComponentTypeNo());
            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
            if (StringUtils.equals(componentType.getLayer(), componentType2.getLayer())
                    && (BooleanUtils.isTrue(componentInstance.getAssociate()) || status != ComponentInstanceStatus.STOPPED)) {
                // 同レイヤ かつ 関連付けがされている または 停止中ではないコンポーネントの場合紐付け不可
                throw new AutoApplicationException("EAPI-100009", componentNo, instanceNo);
            }
        }

        // 現在サービスに紐づいているサーバの一覧を取得する
        List<Long> instanceNos = new ArrayList<Long>();
        List<ComponentInstance> componentInstances = componentInstanceDao
                .readByComponentNo(Long.parseLong(componentNo));
        for (ComponentInstance componentInstance : componentInstances) {
            instanceNos.add(componentInstance.getInstanceNo());
        }

        // サーバの一覧に当該のサーバのインスタンス番号を追加
        instanceNos.add(Long.valueOf(instanceNo));

        // サービスとサーバの紐づけ
        componentService.associateInstances(Long.parseLong(componentNo), instanceNos);

        AttachComponentResponse response = new AttachComponentResponse();

        return response;
    }

}
