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
package jp.primecloud.auto.api.instance;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.api.response.instance.CreateInstanceResponse;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.ImageVcloud;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.ImageOpenstack;
import jp.primecloud.auto.entity.crud.ImageAzure;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/CreateInstance")
public class CreateInstance extends ApiSupport {

    /**
     *
     * サーバ作成
     *
     * @param userName ユーザ名
     * @param farmNo ファーム番号
     * @param platformNo プラットフォーム番号
     * @param imageNo イメージ番号
     * @param instanceName インスタンス名
     * @param instanceType インスタンスタイプ
     * @param comment コメント
     *
     * @return CreateInstanceResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CreateInstanceResponse createInstance(
            @QueryParam(PARAM_NAME_USER) String userName,
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_PLATFORM_NO) String platformNo,
            @QueryParam(PARAM_NAME_IMAGE_NO) String imageNo,
            @QueryParam(PARAM_NAME_INSTANCE_NAME) String instanceName,
            @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType,
            @QueryParam(PARAM_NAME_COMMENT) String comment){

        CreateInstanceResponse response = new CreateInstanceResponse();

            // 入力チェック
            // Key(ユーザ名)
            ApiValidate.validateUser(userName);
            User user = userDao.readByUsername(userName);
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // PlatformNo
            ApiValidate.validatePlatformNo(platformNo);

            //プラットフォーム取得
            Platform platform = platformDao.read(Long.parseLong(platformNo));
            if (platform == null) {
                //プラットフォームが存在しない
                throw new AutoApplicationException("EAPI-100000", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
            }
            if (!platformService.isUseablePlatforms(user.getUserNo(), platform) ||
            	BooleanUtils.isNotTrue(platform.getSelectable())) {
                //認証情報が存在しない or 有効ではないプラットフォーム
                throw new AutoApplicationException("EAPI-000020", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
            }

            // ImageNo
            ApiValidate.validateImageNo(imageNo);
            Image image = imageDao.read(Long.parseLong(imageNo));
            if (image == null || !image.getPlatformNo().equals(platform.getPlatformNo())) {
                //イメージが存在しない or プラットフォームとイメージが一致しない
                throw new AutoApplicationException("EAPI-100000", "Image", PARAM_NAME_IMAGE_NO, imageNo);
            }
            if (BooleanUtils.isNotTrue(image.getSelectable())) {
                //選択不可イメージ
                throw new AutoApplicationException("EAPI-000020", "Image", PARAM_NAME_IMAGE_NO, imageNo);
            }
            // InstanceName
            ApiValidate.validateInstanceName(instanceName);
            // InstanceType
            ApiValidate.validateInstanceType(instanceType, false);
            List<String> instanceTypes = getInstanceTypes(platform, image);
            if (StringUtils.isEmpty(instanceType)) {
                instanceType = instanceTypes.get(0);
            } else {
                if (!instanceTypes.contains(instanceType)) {
                    // インスタンスタイプがイメージのインスタンスタイプに含まれていない
                    throw new AutoApplicationException("EAPI-000011", imageNo, instanceType);
                }
            }
            // Comment
            ApiValidate.validateComment(comment);

            // 対象となるIaas(プラットフォーム)にサーバ(インスタンス)を作成
            Long newInstanceNo = null;
            // TODO CLOUD BRANCHING
            if (PLATFORM_TYPE_AWS.equals(platform.getPlatformType()) ||
                PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType()) ||
                PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType()) ||
                PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType()) ||
                PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                // AWS or Eucalyptus or Cloudstack or VCloud or Openstack or Azure
                newInstanceNo = instanceService.createIaasInstance(Long.parseLong(farmNo), instanceName,
                        image.getPlatformNo(), comment, image.getImageNo() , instanceType);
            } else if (PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                // VMware
                newInstanceNo = instanceService.createVmwareInstance(Long.parseLong(farmNo), instanceName,
                        image.getPlatformNo(), comment, image.getImageNo(), instanceType);
            } else if (PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                // Nifty
                newInstanceNo = instanceService.createNiftyInstance(Long.parseLong(farmNo), instanceName,
                        image.getPlatformNo(), comment, image.getImageNo(), instanceType);
            }

            response.setInstanceNo(newInstanceNo);
            response.setSuccess(true);

        return  response;
    }

    private List<String> getInstanceTypes(Platform platform, Image image) {
        String instanceTypesText = null;
        // TODO CLOUD BRANCHING
        if (PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            //Aws or Eucalyptus
            ImageAws imageAws = imageAwsDao.read(image.getImageNo());
            instanceTypesText = imageAws.getInstanceTypes();
        } else if (PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            //VMWare
            ImageVmware imageVmware = imageVmwareDao.read(image.getImageNo());
            instanceTypesText = imageVmware.getInstanceTypes();
        } else if (PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            //Nifty
            ImageNifty imageNifty = imageNiftyDao.read(image.getImageNo());
            instanceTypesText = imageNifty.getInstanceTypes();
        } else if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            //CloudStack
            ImageCloudstack imageCloudstack = imageCloudstackDao.read(image.getImageNo());
            instanceTypesText = imageCloudstack.getInstanceTypes();
        } else if (PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            //VCloud
            ImageVcloud imageVcloud = imageVcloudDao.read(image.getImageNo());
            instanceTypesText = imageVcloud.getInstanceTypes();
        } else if (PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            //OpenStack
            ImageOpenstack imageOpenstack = imageOpenstackDao.read(image.getImageNo());
            instanceTypesText = imageOpenstack.getInstanceTypes();
        } else if (PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            //Azure
            ImageAzure imageAzure = imageAzureDao.read(image.getImageNo());
            instanceTypesText = imageAzure.getInstanceTypes();
        }
        List<String> instanceTypes = getInstanceTypesFromText(instanceTypesText);
        return instanceTypes;
    }

    private static List<String> getInstanceTypesFromText(String instanceTypesText) {
        List<String> instanceTypes = new ArrayList<String>();
        if (StringUtils.isNotEmpty(instanceTypesText)) {
            for (String instanceType: StringUtils.split(instanceTypesText, ",")) {
                instanceTypes.add(instanceType.trim());
            }
        }
        return instanceTypes;
    }
}