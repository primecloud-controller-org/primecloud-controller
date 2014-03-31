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


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.component.CreateComponentResponse;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;



@Path("/CreateComponent")
public class CreateComponent extends ApiSupport{

    /**
     *
     * サービス作成
     *
     * @param farmNo ファーム番号
     * @param componentName コンポーネント名
     * @param componentTypeNo コンポーネントタイプ番号
     * @param diskSize ディスクサイズ
     * @param comment コメント
     * @return CreateComponentResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public CreateComponentResponse createComponent(
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_COMPONENT_NAME) String componentName,
	        @QueryParam(PARAM_NAME_COMPONENT_TYPE_NO) String componentTypeNo,
	        @QueryParam(PARAM_NAME_DISK_SIZE) String diskSize,
	        @QueryParam(PARAM_NAME_COMMENT) String comment){

        CreateComponentResponse response = new CreateComponentResponse();

        try {
            // 入力チェック
            // farmNo
            ApiValidate.validateFarmNo(farmNo);
            // componentName
            ApiValidate.validateComponentName(componentName);
            // componentTypeNo
            ApiValidate.validateComponentTypeNo(componentTypeNo);
            // diskSize
            ApiValidate.validateDiskSize(diskSize);
            // comments
            ApiValidate.validateComment(comment);

            ComponentType componentType = componentTypeDao.read(Long.parseLong(componentTypeNo));
            if (componentType == null) {
                //コンポーネントタイプが存在しない
                throw new AutoApplicationException("EAPI-100000", "ComponentType", PARAM_NAME_COMPONENT_TYPE_NO, componentTypeNo);
            }
            if (BooleanUtils.isNotTrue(componentType.getSelectable())) {
                //有効ではないコンポーネントタイプ
                throw new AutoApplicationException("EAPI-000020", "ComponentType", PARAM_NAME_COMPONENT_TYPE_NO, componentTypeNo);
            }

            // サービス作成
            Long componentNo = componentService.createComponent(Long.parseLong(farmNo), componentName,
                    Long.valueOf(componentTypeNo), comment, Integer.parseInt(diskSize));

            response.setComponentNo(componentNo);
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