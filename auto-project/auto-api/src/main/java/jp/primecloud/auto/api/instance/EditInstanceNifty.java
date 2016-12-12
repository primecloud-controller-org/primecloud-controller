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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.instance.EditInstanceNiftyResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.StringUtils;

@Path("/EditInstanceNifty")
public class EditInstanceNifty extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EditInstanceNiftyResponse editInstanceNifty(@Context UriInfo uriInfo,
            @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo, @QueryParam(PARAM_NAME_COMMENT) String comment,
            @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType, @QueryParam(PARAM_NAME_KEY_NAME) String keyName) {

        // 入力チェック
        ApiValidate.validateInstanceNo(instanceNo);

        // インスタンス取得
        Instance instance = getInstance(Long.parseLong(instanceNo));

        // 権限チェック
        User user = checkAndGetUser(instance);

        // インスタンスのステータスチェック
        InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
        if (InstanceStatus.STOPPED != status) {
            // インスタンスが停止していない
            throw new AutoApplicationException("EAPI-100014", instanceNo);
        }

        // プラットフォームの種別チェック
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (!PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            // プラットフォームがNiftyでない
            throw new AutoApplicationException("EAPI-100031", "Nifty", instanceNo, instance.getPlatformNo());
        }

        // Comment
        ApiValidate.validateComment(comment);

        // InstanceType
        ApiValidate.validateInstanceType(instanceType, true);
        if (!checkInstanceType(instance.getImageNo(), instanceType)) {
            // InstanceTypeが存在しない
            throw new AutoApplicationException("EAPI-000011", instance.getImageNo(), instanceType);
        }

        // KeyName
        ApiValidate.validateKeyName(keyName);
        NiftyKeyPair keyPair = getKeyPair(user.getUserNo(), instance.getPlatformNo(), keyName);
        if (keyPair == null) {
            // KeyNameが存在しない
            throw new AutoApplicationException("EAPI-000012", instance.getPlatformNo(), keyName);
        }

        // 更新処理
        instanceService.updateNiftyInstance(Long.parseLong(instanceNo), instance.getInstanceName(), comment,
                instanceType, keyPair.getKeyNo());

        EditInstanceNiftyResponse response = new EditInstanceNiftyResponse();

        return response;
    }

    private boolean checkInstanceType(Long imageNo, String instanceType) {
        ImageNifty imageNifty = imageNiftyDao.read(imageNo);
        if (StringUtils.isEmpty(imageNifty.getInstanceTypes())) {
            return false;
        }

        for (String instanceType2 : StringUtils.split(imageNifty.getInstanceTypes(), ",")) {
            if (StringUtils.equals(instanceType, instanceType2.trim())) {
                return true;
            }
        }

        return false;
    }

    private NiftyKeyPair getKeyPair(Long userNo, Long platformNo, String keyName) {
        List<NiftyKeyPair> niftyKeyPairs = niftyDescribeService.getKeyPairs(userNo, platformNo);
        for (NiftyKeyPair niftyKeyPair : niftyKeyPairs) {
            if (StringUtils.equals(keyName, niftyKeyPair.getKeyName())) {
                return niftyKeyPair;
            }
        }

        return null;
    }

}
