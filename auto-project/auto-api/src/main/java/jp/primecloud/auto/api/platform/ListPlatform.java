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
package jp.primecloud.auto.api.platform;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.response.platform.ListPlatformResponse;
import jp.primecloud.auto.api.response.platform.PlatformResponse;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;

import org.apache.commons.lang.BooleanUtils;

@Path("/ListPlatform")
public class ListPlatform extends ApiSupport {

    /**
     * プラットフォーム一覧取得
     *
     * @return ListPlatformResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListPlatformResponse listPlatform() {

        //ユーザ取得
        User user = checkAndGetUser();

        ListPlatformResponse response = new ListPlatformResponse();

        // プラットフォーム情報取得
        List<Platform> platforms = platformDao.readAll();
        for (Platform platform : platforms) {
            if (!platformService.isUsablePlatform(user.getUserNo(), platform)
                    || BooleanUtils.isNotTrue(platform.getSelectable())) {
                //認証情報が存在しない or 無効プラットフォーム → 表示しない
                continue;
            }
            PlatformResponse platformResponse = new PlatformResponse(platform);
            response.getPlatforms().add(platformResponse);
        }

        return response;
    }

}
