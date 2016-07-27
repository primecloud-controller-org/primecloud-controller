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
import jp.primecloud.auto.api.response.platform.PlatformAwsResponse;
import jp.primecloud.auto.api.response.platform.PlatformAzureResponse;
import jp.primecloud.auto.api.response.platform.PlatformCloudstackResponse;
import jp.primecloud.auto.api.response.platform.PlatformNiftyResponse;
import jp.primecloud.auto.api.response.platform.PlatformOpenstackResponse;
import jp.primecloud.auto.api.response.platform.PlatformResponse;
import jp.primecloud.auto.api.response.platform.PlatformVcloudResponse;
import jp.primecloud.auto.api.response.platform.PlatformVmwareResponse;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PlatformAzure;
import jp.primecloud.auto.entity.crud.PlatformCloudstack;
import jp.primecloud.auto.entity.crud.PlatformNifty;
import jp.primecloud.auto.entity.crud.PlatformOpenstack;
import jp.primecloud.auto.entity.crud.PlatformVcloud;
import jp.primecloud.auto.entity.crud.PlatformVmware;
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

        ListPlatformResponse response = new ListPlatformResponse();

        //ユーザ取得
        User user = checkAndGetUser();

        // プラットフォーム情報取得
        List<Platform> platforms = platformDao.readAll();
        for (Platform platform : platforms) {
            if (!platformService.isUseablePlatforms(user.getUserNo(), platform)
                    || BooleanUtils.isNotTrue(platform.getSelectable())) {
                //認証情報が存在しない or 無効プラットフォーム → 表示しない
                continue;
            }
            PlatformResponse platformResponse = new PlatformResponse(platform);
            // TODO CLOUD BRANCHING
            if (PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                PlatformAwsResponse awsResponse = getAwsDetail(user.getUserNo(), platform.getPlatformNo());
                platformResponse.setAws(awsResponse);
            } else if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                PlatformCloudstackResponse csResponse = getCloudstackDetail(user.getUserNo(), platform.getPlatformNo());
                platformResponse.setCloudstack(csResponse);
            } else if (PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                PlatformVmwareResponse vmwareResponse = getVmwareDetail(user.getUserNo(), platform.getPlatformNo());
                platformResponse.setVmware(vmwareResponse);
            } else if (PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                PlatformNiftyResponse niftyResponse = getNiftyDetail(user.getUserNo(), platform.getPlatformNo());
                platformResponse.setNifty(niftyResponse);
            } else if (PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                PlatformVcloudResponse vcloudResponse = getVcloudDetail(user.getUserNo(), platform.getPlatformNo());
                platformResponse.setVcloud(vcloudResponse);
            } else if (PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                PlatformOpenstackResponse openstackResponse = getOpenstackDetail(user.getUserNo(),
                        platform.getPlatformNo());
                platformResponse.setOpenstack(openstackResponse);
            } else if (PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                PlatformAzureResponse azureResponse = getAzureDetail(user.getUserNo(), platform.getPlatformNo());
                platformResponse.setAzure(azureResponse);
            }
            response.getPlatforms().add(platformResponse);
        }

        response.setSuccess(true);

        return response;
    }

    private PlatformAwsResponse getAwsDetail(Long userNo, Long platformNo) {
        PlatformAws aws = platformAwsDao.read(platformNo);
        return new PlatformAwsResponse(aws);
    }

    private PlatformCloudstackResponse getCloudstackDetail(Long userNo, Long platformNo) {
        PlatformCloudstack cloudstack = platformCloudstackDao.read(platformNo);
        return new PlatformCloudstackResponse(cloudstack);
    }

    private PlatformVmwareResponse getVmwareDetail(Long userNo, Long platformNo) {
        PlatformVmware vmware = platformVmwareDao.read(platformNo);
        return new PlatformVmwareResponse(vmware);
    }

    private PlatformNiftyResponse getNiftyDetail(Long userNo, Long platformNo) {
        PlatformNifty nifty = platformNiftyDao.read(platformNo);
        return new PlatformNiftyResponse(nifty);
    }

    private PlatformVcloudResponse getVcloudDetail(Long userNo, Long platformNo) {
        PlatformVcloud vcloud = platformVcloudDao.read(platformNo);
        return new PlatformVcloudResponse(vcloud);
    }

    private PlatformOpenstackResponse getOpenstackDetail(Long userNo, Long platformNo) {
        PlatformOpenstack openstack = platformOpenstackDao.read(platformNo);
        return new PlatformOpenstackResponse(openstack);
    }

    private PlatformAzureResponse getAzureDetail(Long userNo, Long platformNo) {
        PlatformAzure azure = platformAzureDao.read(platformNo);
        return new PlatformAzureResponse(azure);
    }

}
