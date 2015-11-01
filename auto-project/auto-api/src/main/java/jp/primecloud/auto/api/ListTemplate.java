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
package jp.primecloud.auto.api;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.ListTemplateResponse;
import jp.primecloud.auto.api.response.TemplateResponse;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.Template;
import jp.primecloud.auto.entity.crud.TemplateComponent;
import jp.primecloud.auto.entity.crud.TemplateInstance;
import jp.primecloud.auto.entity.crud.User;


@Path("/ListTemplate")
public class ListTemplate extends ApiSupport {

    /**
     *
     * テンプレート一覧取得
     * @param userName ユーザ名
     *
     * @return ListTemplateResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ListTemplateResponse listTemplate(
	        @QueryParam(PARAM_NAME_USER) String userName){

        ListTemplateResponse response = new ListTemplateResponse();

            // 入力チェック
            // Key(ユーザ名)
            ApiValidate.validateUser(userName);

            //ユーザ取得
            User user = userDao.readByUsername(userName);

            //使用可能プラットフォーム取得
            List<Long> platformNos = getEnabledPlatformNos(user.getUserNo());

            //使用可能イメージ取得
            List<Long> imageNos = getEnabledImageNos();

            //使用可能コンポーネントタイプ取得
            List<Long> componentTypeNos = getEnabledComponentTypeNos();

            // テンプレート取得
            List<Template> templates = templateDao.readAll();
            for (Template template: templates) {
                List<TemplateInstance> templateInstances = templateInstanceDao.readByTemplateNo(template.getTemplateNo());
                boolean enable = true;
                for (TemplateInstance templateInstance: templateInstances) {
                    if (!platformNos.contains(templateInstance.getPlatformNo())) {
                        //有効なプラットフォームが存在しない
                        enable = false;
                        break;
                    }
                    if (!imageNos.contains(templateInstance.getImageNo())) {
                        //有効なイメージが存在しない
                        enable = false;
                        break;
                    }
                }

                if (!enable) {
                    //使用不可のプラットフォーム、イメージ、コンポーネントタイプが存在する場合は表示しない
                    continue;
                }

                List<TemplateComponent> templateComponents = templateComponentDao.readByTemplateNo(template.getTemplateNo());
                for (TemplateComponent templateComponent: templateComponents) {
                    if (!componentTypeNos.contains(templateComponent.getComponentTypeNo())) {
                        enable = false;
                        break;
                    }
                }

                if (!enable) {
                	//使用不可のプラットフォーム、イメージ、コンポーネントタイプが存在する場合は表示しない
                	continue;
                }

                    //使用可能なプラットフォームに紐づくテンプレートインスタンスのテンプレート情報のみ表示
                    TemplateResponse templateResponse = new TemplateResponse(template);
                    response.addTemplate(templateResponse);
                }

            response.setSuccess(true);

        return  response;
	}

   /**
    *
    * 使用可能なプラットフォーム番号のリストを取得する
    *
    * @param userNo ユーザ番号
    * @return 使用可能なプラットフォーム番号のリスト
    */
   private List<Long> getEnabledPlatformNos(Long userNo) {
       List<Long> platformNos = new ArrayList<Long>();
       List<Platform> platforms = platformDao.readAll();
       for (Platform platform: platforms) {
            if (!platformService.isUseablePlatforms(userNo, platform) ||
            	BooleanUtils.isNotTrue(platform.getSelectable())) {
                //認証情報が存在しない or 有効プラットフォームではない場合はリストに含めない
                   continue;
               }
            platformNos.add(platform.getPlatformNo());
        }
        return platformNos;
    }

    /**
     *
     * 使用可能なイメージ番号のリストを取得する
     *
     * @return 使用可能なイメージ番号のリスト
     */
    private List<Long> getEnabledImageNos() {
       List<Long> imageNos = new ArrayList<Long>();
       List<Image> images = imageDao.readAll();
       for (Image image: images) {
           if (BooleanUtils.isNotTrue(image.getSelectable())) {
               //有効イメージではない場合、ロードバランサーイメージの場合はリストに含めない
                   continue;
               }
           imageNos.add(image.getImageNo());
       }
       return imageNos;
               }

    /**
     *
     * 使用可能なコンポーネントタイプ番号のリストを取得する
     *
     * @return 使用可能なコンポーネントタイプ番号のリスト
     */
    private List<Long> getEnabledComponentTypeNos() {
       List<Long> componentTypeNos = new ArrayList<Long>();
       List<ComponentType> componentTypes = componentTypeDao.readAll();
       for (ComponentType componentType: componentTypes) {
           if (BooleanUtils.isNotTrue(componentType.getSelectable())) {
               //有効コンポーネントタイプではない場合、ロードバランサーイメージの場合はリストに含めない
                   continue;
               }
           componentTypeNos.add(componentType.getComponentTypeNo());
           }
       return componentTypeNos;
   }

}