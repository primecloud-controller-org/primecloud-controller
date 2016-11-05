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
import jp.primecloud.auto.api.response.instance.EditInstanceAwsResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;

@Path("/EditInstanceAws")
public class EditInstanceAws extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EditInstanceAwsResponse editInstance(@QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
            @QueryParam(PARAM_NAME_COMMENT) String comment, @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType,
            @QueryParam(PARAM_NAME_KEY_NAME) String keyName,
            @QueryParam(PARAM_NAME_SECURITY_GROUPS) String securityGroups,
            @QueryParam(PARAM_NAME_AVAILABILITY_ZONE) String availabilityZone,
            @QueryParam(PARAM_NAME_IP_ADDRESS) String ipAddress, @QueryParam(PARAM_NAME_SUBNET) String subnet,
            @QueryParam(PARAM_NAME_PRIVATE_IP) String privateIp) {

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
        if (!PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            // プラットフォームがAWSでない
            throw new AutoApplicationException("EAPI-100031", "AWS", instanceNo, instance.getPlatformNo());
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

        PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());

        // Subnet(VPCのみ)
        Subnet awsSubnet = null;
        if (BooleanUtils.isTrue(platformAws.getVpc())) {
            ApiValidate.validateSubnet(subnet);

            awsSubnet = getSubnet(user.getUserNo(), instance.getPlatformNo(), subnet);
            if (awsSubnet == null) {
                // CidrBlockが存在しない
                throw new AutoApplicationException("EAPI-000017", instance.getPlatformNo(), subnet);
            }
        }

        // PrivateIpAddress(VPCのみ)
        if (BooleanUtils.isTrue(platformAws.getVpc()) && StringUtils.isNotEmpty(privateIp)) {
            ApiValidate.validatePrivateIpAddress(privateIp);

            // サブネット内で有効なIPアドレスかどうかのチェック
            if (!checkPrivateIp(subnet, privateIp)) {
                // サブネット内で有効なIPアドレスでない
                throw new AutoApplicationException("EAPI-000018", subnet, privateIp);
            }
        }

        // AvailabilityZone(非VPCのみ)
        if (BooleanUtils.isNotTrue(platformAws.getVpc())) {
            ApiValidate.validateAvailabilityZone(availabilityZone);

            if (StringUtils.isNotEmpty(availabilityZone)
                    && !checkAvailabilityZoneName(user.getUserNo(), instance.getPlatformNo(), availabilityZone)) {
                // AvailabilityZoneNameが存在しない
                throw new AutoApplicationException("EAPI-100017", instance.getPlatformNo(), availabilityZone);
            }
        }

        // IpAddress
        Long addressNo = null;
        if (StringUtils.isNotEmpty(ipAddress)) {
            ApiValidate.validateIpAddress(ipAddress, false);

            List<AwsAddress> awsAddresses = awsAddressDao.readByUserNo(user.getUserNo());
            for (AwsAddress awsAddress : awsAddresses) {
                if (instance.getPlatformNo().equals(awsAddress.getPlatformNo())) {
                    if (ipAddress.equals(awsAddress.getPublicIp())) {
                        addressNo = awsAddress.getAddressNo();
                        break;
                    }
                }
            }

            if (addressNo == null) {
                // IpAddressが存在しない
                throw new AutoApplicationException("EAPI-100016", instance.getInstanceNo(), ipAddress);
            }
        }

        // 更新処理
        if (BooleanUtils.isTrue(platformAws.getVpc())) {
            instanceService.updateAwsInstance(instance.getInstanceNo(), instance.getInstanceName(), comment, keyName,
                    instanceType, securityGroups, awsSubnet.getAvailabilityZone(), addressNo, awsSubnet.getSubnetId(),
                    privateIp);
        } else {
            instanceService.updateAwsInstance(instance.getInstanceNo(), instance.getInstanceName(), comment, keyName,
                    instanceType, securityGroups, availabilityZone, addressNo, null, null);
        }

        EditInstanceAwsResponse response = new EditInstanceAwsResponse();

        return response;
    }

    private boolean checkInstanceType(Long imageNo, String instanceType) {
        ImageAws imageAws = imageAwsDao.read(imageNo);
        if (StringUtils.isEmpty(imageAws.getInstanceTypes())) {
            return false;
        }

        for (String instanceType2 : StringUtils.split(imageAws.getInstanceTypes(), ",")) {
            if (StringUtils.equals(instanceType, instanceType2.trim())) {
                return true;
            }
        }

        return false;
    }

    private boolean checkKeyName(Long userNo, Long platformNo, String keyName) {
        List<KeyPairInfo> keyPairs = awsDescribeService.getKeyPairs(userNo, platformNo);
        for (KeyPairInfo keyPair : keyPairs) {
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
        List<SecurityGroup> groups = awsDescribeService.getSecurityGroups(userNo, platformNo);
        for (SecurityGroup group : groups) {
            groupNames2.add(group.getGroupName());
        }

        for (String groupName : groupNames) {
            if (!groupNames2.contains(groupName)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkAvailabilityZoneName(Long userNo, Long platformNo, String zoneName) {
        List<AvailabilityZone> availabilityZones = awsDescribeService.getAvailabilityZones(userNo, platformNo);
        for (AvailabilityZone availabilityZone : availabilityZones) {
            if (StringUtils.equals(zoneName, availabilityZone.getZoneName())) {
                return true;
            }
        }

        return false;
    }

    private Subnet getSubnet(Long userNo, Long platformNo, String cidrBlock) {
        List<Subnet> subnets = awsDescribeService.getSubnets(userNo, platformNo);
        for (Subnet subnet : subnets) {
            if (subnet.getCidrBlock().equals(cidrBlock)) {
                return subnet;
            }
        }

        return null;
    }

    private boolean checkPrivateIp(String cidrBlock, String privateIpAddress) {
        String[] splitCidr = cidrBlock.split("/");
        jp.primecloud.auto.common.component.Subnet subnet = new jp.primecloud.auto.common.component.Subnet(
                splitCidr[0].trim(), Integer.parseInt(splitCidr[1].trim()));
        String subnetIp = splitCidr[0].trim();
        //AWS(VPC)では先頭3つまでのIPが予約済みIP
        for (int i = 0; i < 3; i++) {
            subnetIp = jp.primecloud.auto.common.component.Subnet.getNextAddress(subnetIp);
            subnet.addReservedIp(subnetIp);
        }
        return subnet.isScorp(privateIpAddress);
    }

}
