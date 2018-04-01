package jp.primecloud.auto.api.component;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.component.ComponentTypeResponse;
import jp.primecloud.auto.api.response.component.ListComponentTypeResponse;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.service.dto.ComponentTypeDto;

@Path("/ListComponentType")
public class ListComponentType extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListComponentTypeResponse listComponentType(@QueryParam(PARAM_NAME_FARM_NO) String farmNo) {
        // 入力チェック
        ApiValidate.validateFarmNo(farmNo);

        // ファーム取得
        Farm farm = getFarm(Long.parseLong(farmNo));

        // 権限チェック
        checkAndGetUser(farm);

        List<ComponentTypeDto> componentTypeDtos = componentService.getComponentTypes(farm.getFarmNo());

        ListComponentTypeResponse response = new ListComponentTypeResponse();

        for (ComponentTypeDto componentTypeDto : componentTypeDtos) {
            ComponentTypeResponse componentTypeResponse = new ComponentTypeResponse(
                    componentTypeDto.getComponentType());
            response.getComponentTypes().add(componentTypeResponse);
        }

        return response;
    }

}
