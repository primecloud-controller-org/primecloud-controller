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
import jp.primecloud.auto.api.response.instance.EditInstanceResponse;
import jp.primecloud.auto.common.component.Subnet;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageAzure;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageOpenstack;
import jp.primecloud.auto.entity.crud.ImageVcloud;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PlatformAzure;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VcloudInstance;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.dto.AddressDto;
import jp.primecloud.auto.service.dto.InstanceNetworkDto;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.StorageTypeDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.ZoneDto;

import org.apache.commons.lang.StringUtils;

@Path("/EditInstance")
public class EditInstance extends ApiSupport {

    /**
     * サーバ編集
     * 
     * @param instanceNo インスタンス番号
     * @param instanceType インスタンスタイプ
     * @param keyName キーペア名(AWSのみ)
     * @param securityGroups セキュリティグループ(AWSのみ)
     * @param availabilityZone ゾーン(AWSのみ)
     * @param ipAddress 固定IPアドレス(AWS、VCloudのみ)
     * @param cidrBlock シダーブロック(サブネット)(cidrBlock)(EC2+VPCのみ)
     * @param privateIp プライベートIP(EC2+VPCのみ)
     * @param comment コメント
     * @param storageType ストレージタイプ(VCLOUDのみ)
     * @return EditInstanceResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EditInstanceResponse editInstance(@QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
            @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType, @QueryParam(PARAM_NAME_KEY_NAME) String keyName,
            @QueryParam(PARAM_NAME_SECURITY_GROUPS) String securityGroups,
            @QueryParam(PARAM_NAME_AVAILABILITY_ZONE) String availabilityZone,
            @QueryParam(PARAM_NAME_IP_ADDRESS) String ipAddress, @QueryParam(PARAM_NAME_SUBNET) String cidrBlock,
            @QueryParam(PARAM_NAME_PRIVATE_IP) String privateIp, @QueryParam(PARAM_NAME_COMMENT) String comment,
            @QueryParam(PARAM_NAME_STORAGE_TYPE) String storageType) {

        // InstanceNo
        ApiValidate.validateInstanceNo(instanceNo);

        //インスタンス取得
        Instance instance = getInstance(Long.parseLong(instanceNo));

        // 権限チェック
        User user = checkAndGetUser(instance);

        // インスタンスのステータスチェック
        InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
        if (InstanceStatus.STOPPED != status) {
            // インスタンスが停止済み以外
            throw new AutoApplicationException("EAPI-100014", instanceNo);
        }

        //プラットフォーム取得
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (platform == null) {
            // プラットフォームが存在しない
            throw new AutoApplicationException("EAPI-100000", "Platform", PARAM_NAME_PLATFORM_NO,
                    instance.getPlatformNo());
        }
        // プラットフォーム種別チェック
        if (!PLATFORM_TYPE_AWS.equals(platform.getPlatformType())
                && !PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())
                && !PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())
                && !PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())
                && !PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            //プラットフォームがAws、CloudStack、VCloud、OpenStack、Azure以外
            throw new AutoApplicationException("EAPI-100031", "EC2 or CloudStack or VCloud or OpenStack or Azure",
                    instance.getInstanceNo(), instance.getPlatformNo());
        }

        // イメージ取得
        Image image = imageDao.read(instance.getImageNo());
        if (image == null || image.getPlatformNo().equals(instance.getPlatformNo()) == false) {
            // イメージが存在しない
            throw new AutoApplicationException("EAPI-100000", "Image", PARAM_NAME_IMAGE_NO, instance.getImageNo());
        }

        if (PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            editAwsInstance(user.getUserNo(), instance, instanceType, keyName, securityGroups, cidrBlock,
                    availabilityZone, ipAddress, privateIp, comment);
        } else if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            editCloudstackInstance(user.getUserNo(), instance, instanceType, keyName, comment);
        } else if (PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            editVcloudInstance(user.getUserNo(), instance, instanceType, storageType, keyName, comment);
        } else if (PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            editOpenstackInstance(user.getUserNo(), instance, instanceType, keyName, securityGroups, availabilityZone,
                    comment);
        } else if (PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            editAzureInstance(user.getUserNo(), instance, instanceType, cidrBlock, availabilityZone, comment);
        }

        EditInstanceResponse response = new EditInstanceResponse();

        return response;
    }

    private void editAwsInstance(Long userNo, Instance instance, String instanceType, String keyName,
            String securityGroups, String cidrBlock, String availabilityZone, String ipAddress, String privateIp,
            String comment) {
        PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());

        // InstanceType
        ApiValidate.validateInstanceType(instanceType, true);
        ImageAws imageAws = imageAwsDao.read(instance.getImageNo());
        if (commaTextToList(imageAws.getInstanceTypes()).contains(instanceType) == false) {
            // インスタンスタイプがイメージのインスタンスタイプに含まれていない
            throw new AutoApplicationException("EAPI-000011", imageAws.getImageNo(), instanceType);
        }

        // KeyName
        ApiValidate.validateKeyName(keyName);
        if (checkKeyName(userNo, instance.getPlatformNo(), keyName) == false) {
            // キーペアがプラットフォームに存在しない
            throw new AutoApplicationException("EAPI-000012", instance.getPlatformNo(), keyName);
        }

        // SecurityGroups(AWSのみ)
        ApiValidate.validateSecurityGroups(securityGroups);
        if (checkSecurityGroups(userNo, instance.getPlatformNo(), platformAws.getVpcId(), securityGroups) == false) {
            //プラットフォームにセキュリティグループが存在しない
            throw new AutoApplicationException("EAPI-100019", instance.getPlatformNo(), securityGroups);
        }

        // Subnet(VPCのみ)
        String subnetId = null;
        if (platformAws.getVpc()) {
            //必須チェック
            ApiValidate.validateSubnet(cidrBlock);
            SubnetDto subnetDto = getSubnet(userNo, instance.getPlatformNo(), platformAws.getVpcId(), cidrBlock);
            if (subnetDto == null) {
                //サブネットがプラットフォームに存在しない
                throw new AutoApplicationException("EAPI-000017", instance.getPlatformNo(), cidrBlock);
            }
            subnetId = subnetDto.getSubnetId();
            availabilityZone = subnetDto.getZoneid();
        } else {
            subnetId = null;
        }

        //PrivateIpAddress(VPCのみ)
        if (platformAws.getVpc() && StringUtils.isNotEmpty(subnetId) && StringUtils.isNotEmpty(privateIp)) {
            //書式チェック
            ApiValidate.validatePrivateIpAddress(privateIp);
            //サブネット内、有効IPチェック
            if (checkPrivateIp(cidrBlock, privateIp) == false) {
                //サブネット内で有効なIPではない
                throw new AutoApplicationException("EAPI-000018", cidrBlock, privateIp);
            }
        } else {
            privateIp = null;
        }

        //AvailabilityZoneName
        if (platformAws.getVpc() == false) {
            //VPCではない場合
            //※VPCの場合はサブネットでゾーンが決まるので、入力チェックは行わない
            ApiValidate.validateAvailabilityZone(availabilityZone);
            if (StringUtils.isNotEmpty(availabilityZone)
                    && checkAvailabilityZoneName(userNo, instance.getPlatformNo(), availabilityZone) == false) {
                // AvailabilityZoneName がプラットフォームに存在しない
                throw new AutoApplicationException("EAPI-100017", instance.getPlatformNo(), availabilityZone);
            }
        }

        //IpAddress(ElasticIp)
        Long ipAddressNo = null;
        ApiValidate.validateIpAddress(ipAddress, false);
        if (StringUtils.isNotEmpty(ipAddress)) {
            ipAddressNo = getIpAddressNo(userNo, instance.getPlatformNo(), instance.getInstanceNo(), ipAddress);
            if (ipAddressNo == null) {
                // IPAddressがDBに存在しない or IPがすでに他のサーバに割り当て済み
                throw new AutoApplicationException("EAPI-100016", instance.getInstanceNo(), ipAddress);
            }
        }

        // Comment
        ApiValidate.validateComment(comment);

        // 更新処理
        instanceService.updateAwsInstance(instance.getInstanceNo(), instance.getInstanceName(), comment, keyName,
                instanceType, securityGroups, availabilityZone, ipAddressNo, subnetId, privateIp);
    }

    private void editCloudstackInstance(Long userNo, Instance instance, String instanceType, String keyName,
            String comment) {
        CloudstackInstance cloudstackInstance = cloudstackInstanceDao.read(instance.getInstanceNo());
        if (cloudstackInstance == null) {
            // CloudStackInstanceが存在しない
            throw new AutoApplicationException("EAPI-100000", "CloudStackInstance", PARAM_NAME_INSTANCE_NO,
                    instance.getInstanceNo());
        }

        // InstanceType
        ApiValidate.validateInstanceType(instanceType, true);
        ImageCloudstack imageCloudstack = imageCloudstackDao.read(instance.getImageNo());
        if (commaTextToList(imageCloudstack.getInstanceTypes()).contains(instanceType) == false) {
            // インスタンスタイプがイメージのインスタンスタイプに含まれていない
            throw new AutoApplicationException("EAPI-000011", imageCloudstack.getImageNo(), instanceType);
        }

        // KeyName
        if (StringUtils.isNotEmpty(keyName)) {
            if (checkKeyName(userNo, instance.getPlatformNo(), keyName) == false) {
                // キーペアがプラットフォームに存在しない
                throw new AutoApplicationException("EAPI-000012", instance.getPlatformNo(), keyName);
            }
        }

        // Comment
        ApiValidate.validateComment(comment);

        //更新処理
        instanceService.updateCloudstackInstance(instance.getInstanceNo(), instance.getInstanceName(), comment,
                keyName, instanceType, cloudstackInstance.getSecuritygroup(), cloudstackInstance.getZoneid(), null);
    }

    private void editVcloudInstance(Long userNo, Instance instance, String instanceType, String storageType,
            String keyName, String comment) {
        VcloudInstance vcloudInstance = vcloudInstanceDao.read(instance.getInstanceNo());
        if (vcloudInstance == null) {
            // VcloudInstanceが存在しない
            throw new AutoApplicationException("EAPI-100000", "VcloudInstance", PARAM_NAME_INSTANCE_NO,
                    instance.getInstanceNo());
        }

        // InstanceType
        ApiValidate.validateInstanceType(instanceType, true);
        ImageVcloud imageVcloud = imageVcloudDao.read(instance.getImageNo());
        if (commaTextToList(imageVcloud.getInstanceTypes()).contains(instanceType) == false) {
            // インスタンスタイプがイメージのインスタンスタイプに含まれていない
            throw new AutoApplicationException("EAPI-000011", imageVcloud.getImageNo(), instanceType);
        }

        // StorageType
        Long storageTypeNo = null;
        ApiValidate.validateStrageType(storageType);
        storageTypeNo = checkStorageType(userNo, instance.getPlatformNo(), storageType);
        if (storageTypeNo == null) {
            // ストレージタイプがプラットフォームに存在しない
            throw new AutoApplicationException("EAPI-000022", instance.getPlatformNo(), storageType);
        }

        // KeyName
        Long keyNo = null;
        ApiValidate.validateKeyName(keyName);
        keyNo = checkVcloudKeyName(userNo, instance.getPlatformNo(), keyName);
        if (keyNo == null) {
            // キーペアがプラットフォームに存在しない
            throw new AutoApplicationException("EAPI-000012", instance.getPlatformNo(), keyName);
        }

        // Comment
        ApiValidate.validateComment(comment);

        // TODO ネットワーク追加、ディスクの追加未対応
        List<InstanceNetworkDto> instanceNetworks = new ArrayList<InstanceNetworkDto>();

        //更新処理
        instanceService.updateVcloudInstance(instance.getInstanceNo(), instance.getInstanceName(), comment,
                storageTypeNo, keyNo, instanceType, instanceNetworks);
    }

    private void editOpenstackInstance(Long userNo, Instance instance, String instanceType, String keyName,
            String securityGroups, String availabilityZone, String comment) {

        // InstanceType
        ApiValidate.validateInstanceType(instanceType, true);
        ImageOpenstack imageOpenstack = imageOpenstackDao.read(instance.getImageNo());
        if (commaTextToList(imageOpenstack.getInstanceTypes()).contains(instanceType) == false) {
            // インスタンスタイプがイメージのインスタンスタイプに含まれていない
            throw new AutoApplicationException("EAPI-000011", imageOpenstack.getImageNo(), instanceType);
        }

        // KeyName
        ApiValidate.validateKeyName(keyName);
        if (checkKeyName(userNo, instance.getPlatformNo(), keyName) == false) {
            // キーペアがプラットフォームに存在しない
            throw new AutoApplicationException("EAPI-000012", instance.getPlatformNo(), keyName);
        }

        // SecurityGroups
        ApiValidate.validateSecurityGroups(securityGroups);
        if (checkSecurityGroups(userNo, instance.getPlatformNo(), null, securityGroups) == false) {
            //プラットフォームにセキュリティグループが存在しない
            throw new AutoApplicationException("EAPI-100019", instance.getPlatformNo(), securityGroups);
        }

        //AvailabilityZone
        ApiValidate.validateAvailabilityZone(availabilityZone);
        if (StringUtils.isNotEmpty(availabilityZone)
                && checkAvailabilityZoneName(userNo, instance.getPlatformNo(), availabilityZone) == false) {
            // AvailabilityZone がプラットフォームに存在しない
            throw new AutoApplicationException("EAPI-100017", instance.getPlatformNo(), availabilityZone);
        }

        // Comment
        ApiValidate.validateComment(comment);

        // 更新処理
        instanceService.updateOpenStackInstance(instance.getInstanceNo(), instance.getInstanceName(), comment,
                instanceType, availabilityZone, securityGroups, keyName);
    }

    private void editAzureInstance(Long userNo, Instance instance, String instanceType, String cidrBlock,
            String availabilitySet, String comment) {

        PlatformAzure platformAzure = platformAzureDao.read(instance.getPlatformNo());

        // InstanceType
        ApiValidate.validateInstanceType(instanceType, true);
        ImageAzure imageAzure = imageAzureDao.read(instance.getImageNo());
        if (commaTextToList(imageAzure.getInstanceTypes()).contains(instanceType) == false) {
            // インスタンスタイプがイメージのインスタンスタイプに含まれていない
            throw new AutoApplicationException("EAPI-000011", imageAzure.getImageNo(), instanceType);
        }

        //AvailabilitySet
        ApiValidate.validateAvailabilityZone(availabilitySet);
        if (StringUtils.isNotEmpty(availabilitySet)) {
            String[] availabilitySets = platformAzure.getAvailabilitySets().split(",");
            boolean existFlg = false;
            for (int i = 0; i <= availabilitySets.length; i++) {
                if (availabilitySet.equals(availabilitySets[i])) {
                    existFlg = true;
                }
            }
            if (!existFlg) {
                // AvailabilitySet がプラットフォームに存在しない
                throw new AutoApplicationException("EAPI-100017", instance.getPlatformNo(), availabilitySet);
            }
        }

        // Subnet
        String subnetId = null;
        ApiValidate.validateSubnet(cidrBlock);
        SubnetDto subnetDto = getAzureSubnet(userNo, instance.getPlatformNo(), platformAzure.getNetworkName(),
                cidrBlock);
        if (subnetDto == null) {
            //サブネットがプラットフォームに存在しない
            throw new AutoApplicationException("EAPI-000017", instance.getPlatformNo(), cidrBlock);
        }
        subnetId = subnetDto.getSubnetId();

        // Comment
        ApiValidate.validateComment(comment);

        //更新処理
        instanceService.updateAzureInstance(instance.getInstanceNo(), instance.getInstanceName(), comment,
                instanceType, availabilitySet, subnetId);
    }

    private Long checkStorageType(Long userNo, Long platformNo, String storageTypeName) {
        // ストレージタイプの名称がプラットフォームに存在するかチェック
        List<StorageTypeDto> storageTypes = iaasDescribeService.getStorageTypes(userNo, platformNo);
        for (StorageTypeDto storageType : storageTypes) {
            if (StringUtils.equals(storageTypeName, storageType.getStorageTypeName())) {
                return storageType.getStorageTypeNo();
            }
        }

        return null;
    }

    private boolean checkKeyName(Long userNo, Long platformNo, String keyName) {
        // キーペアの名称がプラットフォームに存在するかチェック
        //Aws or Eucalyptus or CloudStack
        List<KeyPairDto> keyPairs = iaasDescribeService.getKeyPairs(userNo, platformNo);
        for (KeyPairDto keyPair : keyPairs) {
            if (StringUtils.equals(keyName, keyPair.getKeyName())) {
                return true;
            }
        }

        return false;
    }

    private Long checkVcloudKeyName(Long userNo, Long platformNo, String keyName) {
        // キーペアの名称がプラットフォームに存在するかチェック
        //VCloud
        List<KeyPairDto> keyPairs = iaasDescribeService.getKeyPairs(userNo, platformNo);
        for (KeyPairDto keyPair : keyPairs) {
            if (StringUtils.equals(keyName, keyPair.getKeyName())) {
                return keyPair.getKeyNo();
            }
        }

        return null;
    }

    private boolean checkSecurityGroups(Long userNo, Long platformNo, String vpcId, String securityGroups) {
        boolean isContain = false;
        if (StringUtils.isNotEmpty(securityGroups)) {
            List<String> groupNames = getSecurityGroupNames(userNo, platformNo, vpcId);
            for (String grouName : securityGroups.split(",")) {
                if (groupNames.contains(grouName.trim())) {
                    isContain = true;
                } else {
                    return false;
                }
            }
        }
        return isContain;
    }

    private List<String> getSecurityGroupNames(Long userNo, Long platformNo, String vpcId) {
        List<String> groupNames = new ArrayList<String>();
        List<SecurityGroupDto> securityGroupDtos = iaasDescribeService.getSecurityGroups(userNo, platformNo, vpcId);
        for (SecurityGroupDto dto : securityGroupDtos) {
            groupNames.add(dto.getGroupName());
        }
        return groupNames;
    }

    private Long getIpAddressNo(Long userNo, Long platformNo, Long instanceNo, String ipAddress) {
        Long ipAddressNo = null;
        List<AddressDto> addresses = iaasDescribeService.getAddresses(userNo, platformNo);
        for (AddressDto address : addresses) {
            if (StringUtils.equals(ipAddress, address.getPublicIp())
                    && (address.getInstanceNo() == null || instanceNo == address.getInstanceNo())) {
                ipAddressNo = address.getAddressNo();
                break;
            }
        }
        return ipAddressNo;
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

    private SubnetDto getSubnet(Long userNo, Long platformNo, String vpcId, String cidrBlock) {
        List<SubnetDto> subnets = iaasDescribeService.getSubnets(userNo, platformNo, vpcId);
        for (SubnetDto subnetDto : subnets) {
            if (subnetDto.getCidrBlock().equals(cidrBlock)) {
                return subnetDto;
            }
        }
        return null;
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

    private boolean checkPrivateIp(String cidrBlock, String privateIpAddress) {
        String[] splitCidr = cidrBlock.split("/");
        Subnet subnet = new Subnet(splitCidr[0].trim(), Integer.parseInt(splitCidr[1].trim()));
        String subnetIp = splitCidr[0].trim();
        //AWS(VPC)では先頭3つまでのIPが予約済みIP
        for (int i = 0; i < 3; i++) {
            subnetIp = Subnet.getNextAddress(subnetIp);
            subnet.addReservedIp(subnetIp);
        }
        return subnet.isScorp(privateIpAddress);
    }

    private static List<String> commaTextToList(String commaText) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isNotEmpty(commaText)) {
            for (String splitStr : StringUtils.split(commaText, ",")) {
                list.add(splitStr.trim());
            }
        }
        return list;
    }

}
