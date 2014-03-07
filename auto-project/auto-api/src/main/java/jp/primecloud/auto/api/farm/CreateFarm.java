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
package jp.primecloud.auto.api.farm;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.farm.CreateFarmResponse;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.Template;
import jp.primecloud.auto.entity.crud.TemplateComponent;
import jp.primecloud.auto.entity.crud.TemplateInstance;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;



@Path("/CreateFarm")
public class CreateFarm extends ApiSupport {

    /**
     *
     * ファーム作成
     *
     * @param userName ユーザ名
     * @param farmName ファーム名
     * @param templateNo テンプレート番号
     * @param comment コメント
     *
     * @return CreateFarmResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public CreateFarmResponse createFarm(
            @QueryParam(PARAM_NAME_USER) String userName,
            @QueryParam(PARAM_NAME_CLOUD_NAME) String cloudName,
            @QueryParam(PARAM_NAME_TEMPLATE_NO) String templateNo,
            @QueryParam(PARAM_NAME_COMMENT) String comment){

        CreateFarmResponse response = new CreateFarmResponse();

        try {
            // 入力チェック
            // User
            ApiValidate.validateUser(userName);
            // CloudName
            ApiValidate.validateCloudName(cloudName);
            // TemplateNo
            ApiValidate.validateTemplateNo(templateNo);
            // Comment
            ApiValidate.validateComment(comment);

            // ユーザの取得
            User user = userDao.readByUsername(userName);
            if (user == null) {
                // ユーザが存在しない場合、エラー
                throw new AutoApplicationException("EAPI-100000", "User", PARAM_NAME_USER, userName);
            }

            // テンプレートの取得
            Template template = templateDao.read(Long.parseLong(templateNo));
            if (template == null) {
                // テンプレートが存在しない場合、エラー
                throw new AutoApplicationException("EAPI-100000", "Template", PARAM_NAME_TEMPLATE_NO, templateNo);
            }

            // テンプレートのチェック
            if (!checkTemplate(user.getUserNo(), template)) {
                // テンプレートに使用不可のプラットフォーム、イメージ、コンポーネントタイプが含まれている場合、エラー
                throw new AutoApplicationException("EAPI-000021", template.getTemplateName(), templateNo);
            }

            // ファームを作成
            Long newFarmNo = farmService.createFarm(user.getUserNo(), cloudName, comment);

            // テンプレートを元にサーバ、サービスを作成
            templateService.applyTemplate(newFarmNo, Long.parseLong(templateNo));

            response.setFarmNo(newFarmNo);
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

    /**
     *
     * @param userNo ユーザ番号
     * @param template テンプレート情報
     * @return テンプレートに使用不可のプラットフォーム、イメージ、コンポーネントを含んでいた場合エラー
     */
    private boolean checkTemplate(Long userNo, Template template) {
        List<Long> platformNos = null;
        List<Long> imageNos = null;
        List<Long> componentTypeNos = null;

        //TemplateInstance取得
        List<TemplateInstance> templateInstances = templateInstanceDao.readByTemplateNo(template.getTemplateNo());
        if (templateInstances.size() > 0) {
        	platformNos = getEnabledPlatformNos(userNo);
        	imageNos = getEnabledImageNos();
        }
        for (TemplateInstance templateInstance: templateInstances) {
            if (!platformNos.contains(templateInstance.getPlatformNo())) {
                //有効なプラットフォームが存在しない
            	return false;
            }
            if (!imageNos.contains(templateInstance.getImageNo())) {
                //有効なイメージが存在しない
            	return false;
            }
        }

        List<TemplateComponent> templateComponents = templateComponentDao.readByTemplateNo(template.getTemplateNo());
        if (templateComponents.size() > 0) {
        	componentTypeNos = getEnabledComponentTypeNos();
        }
        for (TemplateComponent templateComponent: templateComponents) {
            if (!componentTypeNos.contains(templateComponent.getComponentTypeNo())) {
            	return false;
            }
        }
        return true;
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