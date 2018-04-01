/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.instance.EditInstanceAzureResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.ImageAzure;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAzure;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.dto.SubnetDto;

import org.apache.commons.lang.StringUtils;

@Path("/EditInstanceAzure")
public class EditInstanceAzure extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EditInstanceAzureResponse editInstance(@QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
            @QueryParam(PARAM_NAME_COMMENT) String comment, @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType,
            @QueryParam(PARAM_NAME_AVAILABILITY_SET) String availabilitySet,
            @QueryParam(PARAM_NAME_SUBNET) String subnet) {

        // InstanceNo
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
        if (!PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            // プラットフォームがAzureでない
            throw new AutoApplicationException("EAPI-100031", "Azure", instanceNo, instance.getPlatformNo());
        }

        // Comment
        ApiValidate.validateComment(comment);

        // InstanceType
        ApiValidate.validateInstanceType(instanceType, true);
        if (!checkInstanceType(instance.getImageNo(), instanceType)) {
            // InstanceTypeが存在しない
            throw new AutoApplicationException("EAPI-000011", instance.getImageNo(), instanceType);
        }

        PlatformAzure platformAzure = platformAzureDao.read(instance.getPlatformNo());

        // AvailabilitySet
        ApiValidate.validateAvailabilityZone(availabilitySet);
        if (StringUtils.isNotEmpty(availabilitySet)) {
            if (!checkAvailabilitySet(availabilitySet, platformAzure)) {
                // AvailabilitySetが存在しない
                throw new AutoApplicationException("EAPI-100017", instance.getPlatformNo(), availabilitySet);
            }
        }

        // Subnet
        ApiValidate.validateSubnet(subnet);
        SubnetDto subnetDto = getAzureSubnet(user.getUserNo(), instance.getPlatformNo(), platformAzure.getNetworkName(),
                subnet);
        if (subnetDto == null) {
            // Subnetが存在しない
            throw new AutoApplicationException("EAPI-000017", instance.getPlatformNo(), subnet);
        }

        // 更新処理
        instanceService.updateAzureInstance(instance.getInstanceNo(), instance.getInstanceName(), comment, instanceType,
                availabilitySet, subnetDto.getSubnetId());

        EditInstanceAzureResponse response = new EditInstanceAzureResponse();

        return response;
    }

    private boolean checkInstanceType(Long imageNo, String instanceType) {
        ImageAzure imageAzure = imageAzureDao.read(imageNo);
        if (StringUtils.isEmpty(imageAzure.getInstanceTypes())) {
            return false;
        }

        for (String instanceType2 : StringUtils.split(imageAzure.getInstanceTypes(), ",")) {
            if (StringUtils.equals(instanceType, instanceType2.trim())) {
                return true;
            }
        }

        return false;
    }

    private boolean checkAvailabilitySet(String availabilitySet, PlatformAzure platformAzure) {
        for (String availabilitySet2 : StringUtils.split(platformAzure.getAvailabilitySets(), ",")) {
            if (StringUtils.equals(availabilitySet, availabilitySet2.trim())) {
                return true;
            }
        }

        return false;
    }

    private SubnetDto getAzureSubnet(Long userNo, Long platformNo, String networkName, String cidrBlock) {
        List<SubnetDto> subnets = iaasDescribeService.getAzureSubnets(userNo, platformNo, networkName);
        for (SubnetDto subnetDto : subnets) {
            if (subnetDto.getCidrBlock().equals(cidrBlock)) {
                return subnetDto;
            }
        }

        return null;
    }

}
