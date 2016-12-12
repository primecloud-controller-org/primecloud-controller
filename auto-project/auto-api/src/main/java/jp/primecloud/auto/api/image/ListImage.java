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
package jp.primecloud.auto.api.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.image.ImageResponse;
import jp.primecloud.auto.api.response.image.ListImageResponse;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageAzure;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.ImageOpenstack;
import jp.primecloud.auto.entity.crud.ImageVcloud;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;

@Path("/ListImage")
public class ListImage extends ApiSupport {

    /**
     * イメージ一覧取得
     * 
     * @param platformNo プラットフォーム番号
     * @return ListImageResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListImageResponse listImage(@QueryParam(PARAM_NAME_PLATFORM_NO) String platformNo) {

        // 入力チェック
        // PlatformNo
        ApiValidate.validatePlatformNo(platformNo);

        //ユーザ取得
        User user = checkAndGetUser();

        //プラットフォーム取得
        Platform platform = platformDao.read(Long.parseLong(platformNo));

        if (platform == null) {
            //プラットフォームが存在しない
            throw new AutoApplicationException("EAPI-100000", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }
        if (!platformService.isUsablePlatform(user.getUserNo(), platform)
                || BooleanUtils.isNotTrue(platform.getSelectable())) {
            //認証情報が存在しない or 有効ではないプラットフォーム
            throw new AutoApplicationException("EAPI-000020", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }

        ListImageResponse response = new ListImageResponse();

        // イメージ情報取得
        List<Image> images = imageDao.readByPlatformNo(Long.parseLong(platformNo));
        for (Image image : images) {
            // 選択可能でないイメージは除外
            if (BooleanUtils.isNotTrue(image.getSelectable())) {
                continue;
            }

            // ロードバランサイメージは除外
            if (PCCConstant.IMAGE_NAME_ELB.equals(image.getImageName())
                    || PCCConstant.IMAGE_NAME_ULTRAMONKEY.equals(image.getImageName())) {
                continue;
            }

            //プラットフォーム取得
            if (BooleanUtils.isTrue(image.getSelectable())) {
                //対象プラットフォーム かつ 選択可能イメージのみ表示
                ImageResponse imageResponse = new ImageResponse(platform, image);
                imageResponse.getInstanceTypes().addAll(
                        getInstanceTypes(platform.getPlatformType(), image.getImageNo()));
                response.getImages().add(imageResponse);
            }
        }

        return response;
    }

    private List<String> getInstanceTypes(String platformType, Long imageNo) {
        String instanceTypesText = null;

        // AWS
        if (PLATFORM_TYPE_AWS.equals(platformType)) {
            ImageAws imageAws = imageAwsDao.read(imageNo);
            instanceTypesText = imageAws.getInstanceTypes();
        }
        // VMware
        else if (PLATFORM_TYPE_VMWARE.equals(platformType)) {
            ImageVmware imageVmware = imageVmwareDao.read(imageNo);
            instanceTypesText = imageVmware.getInstanceTypes();
        }
        // Nifty
        else if (PLATFORM_TYPE_NIFTY.equals(platformType)) {
            ImageNifty imageNifty = imageNiftyDao.read(imageNo);
            instanceTypesText = imageNifty.getInstanceTypes();
        }
        // CloudStack
        else if (PLATFORM_TYPE_CLOUDSTACK.equals(platformType)) {
            ImageCloudstack imageCloudstack = imageCloudstackDao.read(imageNo);
            instanceTypesText = imageCloudstack.getInstanceTypes();
        }
        // vCloud
        else if (PLATFORM_TYPE_VCLOUD.equals(platformType)) {
            ImageVcloud imageVcloud = imageVcloudDao.read(imageNo);
            instanceTypesText = imageVcloud.getInstanceTypes();
        }
        // Azure
        else if (PLATFORM_TYPE_AZURE.equals(platformType)) {
            ImageAzure imageAzure = imageAzureDao.read(imageNo);
            instanceTypesText = imageAzure.getInstanceTypes();
        }
        // OpenStack
        else if (PLATFORM_TYPE_OPENSTACK.equals(platformType)) {
            ImageOpenstack imageOpenstack = imageOpenstackDao.read(imageNo);
            instanceTypesText = imageOpenstack.getInstanceTypes();
        }

        if (instanceTypesText == null) {
            return new ArrayList<String>();
        }

        return Arrays.asList(instanceTypesText.split(","));
    }

}
