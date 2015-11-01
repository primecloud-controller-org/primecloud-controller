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


import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.api.response.ImageResponse;
import jp.primecloud.auto.api.response.ListImageResponse;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/ListImage")
public class ListImage extends ApiSupport {

    /**
     *
     * イメージ一覧取得
     * @param userName ユーザ名
     * @param farmNo ファーム番号
     * @param platformNo プラットフォーム番号
     *
     * @return ListImageResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public ListImageResponse listImage(
	        @QueryParam(PARAM_NAME_USER) String userName,
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_PLATFORM_NO) String platformNo){

        ListImageResponse response = new ListImageResponse();

            // 入力チェック
            // Key(ユーザ名)
            ApiValidate.validateUser(userName);
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // PlatformNo
            ApiValidate.validatePlatformNo(platformNo);

            //ユーザ取得
            User user = userDao.readByUsername(userName);

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

            // イメージ情報取得
            List<Image> images = imageDao.readByPlatformNo(Long.parseLong(platformNo));
            for (Image image: images) {
                //プラットフォーム取得
                if (BooleanUtils.isTrue(image.getSelectable())) {
                    //対象プラットフォーム かつ 選択可能イメージのみ表示
                    ImageResponse imageResponse = new ImageResponse(platform, image);
                    response.addImage(imageResponse);
                }
            }

            response.setSuccess(true);

        return  response;
	}
}