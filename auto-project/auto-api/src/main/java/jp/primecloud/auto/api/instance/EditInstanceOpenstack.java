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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.instance.EditInstanceOpenstackResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.ImageOpenstack;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.ZoneDto;

import org.apache.commons.lang.StringUtils;

@Path("/EditInstanceOpenstack")
public class EditInstanceOpenstack extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EditInstanceOpenstackResponse editInstance(@QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
            @QueryParam(PARAM_NAME_COMMENT) String comment, @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType,
            @QueryParam(PARAM_NAME_KEY_NAME) String keyName,
            @QueryParam(PARAM_NAME_SECURITY_GROUPS) String securityGroups,
            @QueryParam(PARAM_NAME_AVAILABILITY_ZONE) String availabilityZone) {

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
        if (!PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            // プラットフォームがOpenStackでない
            throw new AutoApplicationException("EAPI-100031", "OpenStack", instanceNo, instance.getPlatformNo());
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
        if (!checkKeyName(user.getUserNo(), instance.getPlatformNo(), keyName)) {
            // KeyNameが存在しない
            throw new AutoApplicationException("EAPI-000012", instance.getPlatformNo(), keyName);
        }

        // SecurityGroups
        ApiValidate.validateSecurityGroups(securityGroups);
        if (!checkSecurityGroups(user.getUserNo(), instance.getPlatformNo(), securityGroups)) {
            // SecurityGroupsが存在しない
            throw new AutoApplicationException("EAPI-100019", instance.getPlatformNo(), securityGroups);
        }

        // AvailabilityZone
        ApiValidate.validateAvailabilityZone(availabilityZone);
        if (StringUtils.isNotEmpty(availabilityZone)
                && !checkAvailabilityZoneName(user.getUserNo(), instance.getPlatformNo(), availabilityZone)) {
            // AvailabilityZoneが存在しない
            throw new AutoApplicationException("EAPI-100017", instance.getPlatformNo(), availabilityZone);
        }

        // 更新処理
        instanceService.updateOpenStackInstance(instance.getInstanceNo(), instance.getInstanceName(), comment,
                instanceType, availabilityZone, securityGroups, keyName);

        EditInstanceOpenstackResponse response = new EditInstanceOpenstackResponse();

        return response;
    }

    private boolean checkInstanceType(Long imageNo, String instanceType) {
        ImageOpenstack imageOpenstack = imageOpenstackDao.read(imageNo);
        if (StringUtils.isEmpty(imageOpenstack.getInstanceTypes())) {
            return false;
        }

        for (String instanceType2 : StringUtils.split(imageOpenstack.getInstanceTypes(), ",")) {
            if (StringUtils.equals(instanceType, instanceType2.trim())) {
                return true;
            }
        }

        return false;
    }

    private boolean checkKeyName(Long userNo, Long platformNo, String keyName) {
        List<KeyPairDto> keyPairs = iaasDescribeService.getKeyPairs(userNo, platformNo);
        for (KeyPairDto keyPair : keyPairs) {
            if (StringUtils.equals(keyName, keyPair.getKeyName())) {
                return true;
            }
        }

        return false;
    }

    private boolean checkSecurityGroups(Long userNo, Long platformNo, String securityGroups) {
        List<String> groupNames = new ArrayList<String>();
        for (String groupName : StringUtils.split(securityGroups, ",")) {
            groupNames.add(groupName.trim());
        }

        if (groupNames.isEmpty()) {
            return false;
        }

        List<String> groupNames2 = new ArrayList<String>();
        List<SecurityGroupDto> securityGroupDtos = iaasDescribeService.getSecurityGroups(userNo, platformNo, null);
        for (SecurityGroupDto securityGroupDto : securityGroupDtos) {
            groupNames2.add(securityGroupDto.getGroupName());
        }

        for (String groupName : groupNames) {
            if (!groupNames2.contains(groupName)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkAvailabilityZoneName(Long userNo, Long platformNo, String zoneName) {
        List<ZoneDto> zones = iaasDescribeService.getAvailabilityZones(userNo, platformNo);
        for (ZoneDto zone : zones) {
            if (StringUtils.equals(zoneName, zone.getZoneName())) {
                return true;
            }
        }

        return false;
    }

}
