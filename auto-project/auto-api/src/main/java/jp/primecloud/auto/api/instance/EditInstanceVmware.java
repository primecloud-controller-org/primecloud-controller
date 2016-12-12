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
import jp.primecloud.auto.api.response.instance.EditInstanceVmwareResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.dto.VmwareAddressDto;

import org.apache.commons.lang.StringUtils;

import com.vmware.vim25.mo.ComputeResource;

@Path("/EditInstanceVmware")
public class EditInstanceVmware extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EditInstanceVmwareResponse editInstanceVmware(@Context UriInfo uriInfo,
            @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo, @QueryParam(PARAM_NAME_COMMENT) String comment,
            @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType, @QueryParam(PARAM_NAME_KEY_NAME) String keyName,
            @QueryParam(PARAM_NAME_COMPUTE_RESOURCE) String computeResource,
            @QueryParam(PARAM_NAME_IS_STATIC_IP) String isStaticIp,
            @QueryParam(PARAM_NAME_IP_ADDRESS) String ipAddress, @QueryParam(PARAM_NAME_SUBNET_MASK) String subnetMask,
            @QueryParam(PARAM_NAME_DEFAULT_GATEWAY) String defaultGateway) {

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
        if (!PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            // プラットフォームがVMwareでない
            throw new AutoApplicationException("EAPI-100031", "VMware", instanceNo, instance.getPlatformNo());
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
        VmwareKeyPair keyPair = getKeyPair(user.getUserNo(), instance.getPlatformNo(), keyName);
        if (keyPair == null) {
            // KeyNameが存在しない
            throw new AutoApplicationException("EAPI-000012", instance.getPlatformNo(), keyName);
        }

        // ComputeResource
        ApiValidate.validateComputeResource(computeResource);
        if (!checkComputeResource(instance.getPlatformNo(), computeResource)) {
            // ComputeResourceが存在しない
            throw new AutoApplicationException("EAPI-000016", instance.getPlatformNo(), computeResource);
        }

        // IsStaticIp
        ApiValidate.validateIsStaticIp(isStaticIp);
        VmwareAddressDto addressDto = null;
        if (Boolean.parseBoolean(isStaticIp)) {
            ApiValidate.validateIpAddress(ipAddress, true);
            ApiValidate.validateSubnetMask(subnetMask);
            ApiValidate.validateDefaultGateway(defaultGateway);

            addressDto = new VmwareAddressDto();
            addressDto.setIpAddress(ipAddress);
            addressDto.setSubnetMask(subnetMask);
            addressDto.setDefaultGateway(defaultGateway);
        }

        // 更新処理
        instanceService.updateVmwareInstance(Long.parseLong(instanceNo), instance.getInstanceName(), comment,
                instanceType, computeResource, null, keyPair.getKeyNo(), addressDto);

        EditInstanceVmwareResponse response = new EditInstanceVmwareResponse();

        return response;
    }

    private boolean checkInstanceType(Long imageNo, String instanceType) {
        ImageVmware imageVmware = imageVmwareDao.read(imageNo);
        if (StringUtils.isEmpty(imageVmware.getInstanceTypes())) {
            return false;
        }

        for (String instanceType2 : StringUtils.split(imageVmware.getInstanceTypes(), ",")) {
            if (StringUtils.equals(instanceType, instanceType2.trim())) {
                return true;
            }
        }

        return false;
    }

    private VmwareKeyPair getKeyPair(Long userNo, Long platformNo, String keyName) {
        List<VmwareKeyPair> vmwareKeyPairs = vmwareDescribeService.getKeyPairs(userNo, platformNo);
        for (VmwareKeyPair vmwareKeyPair : vmwareKeyPairs) {
            if (StringUtils.equals(vmwareKeyPair.getKeyName(), keyName)) {
                return vmwareKeyPair;
            }
        }

        return null;
    }

    private boolean checkComputeResource(Long platformNo, String computeResourceName) {
        List<ComputeResource> computeResources = vmwareDescribeService.getComputeResources(platformNo);
        for (ComputeResource computeResource : computeResources) {
            if (StringUtils.equals(computeResourceName, computeResource.getName())) {
                return true;
            }
        }

        return false;
    }

}
