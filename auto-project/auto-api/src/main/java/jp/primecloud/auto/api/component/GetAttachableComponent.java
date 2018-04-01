package jp.primecloud.auto.api.component;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.component.ComponentInstanceResponse;
import jp.primecloud.auto.api.response.component.GetAttachableComponentResponse;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.service.dto.ComponentTypeDto;

@Path("/GetAttachableComponent")
public class GetAttachableComponent extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GetAttachableComponentResponse getAttachableComponent(
            @QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo) {
        // 入力チェック
        ApiValidate.validateComponentNo(componentNo);

        // コンポーネント取得
        Component component = getComponent(Long.parseLong(componentNo));

        // 権限チェック
        checkAndGetUser(component);

        GetAttachableComponentResponse response = new GetAttachableComponentResponse();

        ComponentTypeDto componentTypeDto = componentService.getComponentType(component.getComponentNo());
        List<ComponentInstance> componentInstances = componentInstanceDao
                .readByComponentNo(Long.parseLong(componentNo));

        for (Long instanceNo : componentTypeDto.getInstanceNos()) {
            ComponentInstance componentInstance = null;
            for (ComponentInstance componentInstance2 : componentInstances) {
                if (componentInstance2.getInstanceNo().equals(instanceNo)) {
                    componentInstance = componentInstance2;
                    break;
                }
            }

            ComponentInstanceResponse componentInstanceResponse;
            if (componentInstance == null) {
                ComponentInstance componentInstance2 = new ComponentInstance();
                componentInstance2.setInstanceNo(instanceNo);
                componentInstance2.setAssociate(false);
                componentInstanceResponse = new ComponentInstanceResponse(componentInstance2);
            } else {
                componentInstanceResponse = new ComponentInstanceResponse(componentInstance);
            }

            response.getInstances().add(componentInstanceResponse);
        }

        return response;
    }

}
