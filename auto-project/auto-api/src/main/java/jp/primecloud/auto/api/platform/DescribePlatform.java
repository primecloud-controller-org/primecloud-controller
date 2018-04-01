/*
 * Copyright 2015 by SCSK Corporation.
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.platform.DescribePlatformResponse;
import jp.primecloud.auto.api.response.platform.PlatformAwsResponse;
import jp.primecloud.auto.api.response.platform.PlatformAzureResponse;
import jp.primecloud.auto.api.response.platform.PlatformCloudstackResponse;
import jp.primecloud.auto.api.response.platform.PlatformNiftyResponse;
import jp.primecloud.auto.api.response.platform.PlatformOpenstackResponse;
import jp.primecloud.auto.api.response.platform.PlatformResponse;
import jp.primecloud.auto.api.response.platform.PlatformVcloudResponse;
import jp.primecloud.auto.api.response.platform.PlatformVmwareResponse;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.CloudstackCertificate;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.vmware.vim25.mo.ComputeResource;

/**
 * <p>
 * プラットフォーム情報取得
 * </p>
 */
@Path("/DescribePlatform")
public class DescribePlatform extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DescribePlatformResponse describePlatform(@QueryParam(PARAM_NAME_PLATFORM_NO) String platformNo) {
        // 入力チェック
        ApiValidate.validatePlatformNo(platformNo);

        // ユーザ取得
        User user = checkAndGetUser();

        // プラットフォーム取得
        Platform platform = platformDao.read(Long.parseLong(platformNo));

        // プラットフォームが存在しない場合
        if (platform == null) {
            throw new AutoApplicationException("EAPI-100000", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }

        // プラットフォームを選択できない場合
        if (BooleanUtils.isNotTrue(platform.getSelectable())) {
            throw new AutoApplicationException("EAPI-000020", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }

        // プラットフォームを利用できない場合
        if (!platformService.isUsablePlatform(user.getUserNo(), platform)) {
            throw new AutoApplicationException("EAPI-000020", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }

        PlatformResponse platformResponse = new PlatformResponse(platform);

        // AWS
        if (PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            PlatformAwsResponse awsResponse = getAwsDetail(user.getUserNo(), platform.getPlatformNo());
            platformResponse.setAws(awsResponse);
        }
        // VMware
        else if (PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            PlatformVmwareResponse vmwareResponse = getVmwareDetail(user.getUserNo(), platform.getPlatformNo());
            platformResponse.setVmware(vmwareResponse);
        }
        // Nifty
        else if (PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            PlatformNiftyResponse niftyResponse = getNiftyDetail(user.getUserNo(), platform.getPlatformNo());
            platformResponse.setNifty(niftyResponse);
        }
        // CloudStack
        else if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            PlatformCloudstackResponse csResponse = getCloudstackDetail(user.getUserNo(), platform.getPlatformNo());
            platformResponse.setCloudstack(csResponse);
        }
        // vCloud
        else if (PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            PlatformVcloudResponse vcloudResponse = getVcloudDetail(user.getUserNo(), platform.getPlatformNo());
            platformResponse.setVcloud(vcloudResponse);
        }
        // Azure
        else if (PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            PlatformAzureResponse azureResponse = getAzureDetail(user.getUserNo(), platform.getPlatformNo());
            platformResponse.setAzure(azureResponse);
        }
        // OpenStack
        else if (PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            PlatformOpenstackResponse openstackResponse = getOpenstackDetail(user.getUserNo(),
                    platform.getPlatformNo());
            platformResponse.setOpenstack(openstackResponse);
        }

        DescribePlatformResponse response = new DescribePlatformResponse(platformResponse);

        return response;
    }

    private PlatformAwsResponse getAwsDetail(Long userNo, Long platformNo) {
        PlatformAwsResponse response = new PlatformAwsResponse();
        PlatformAws aws = platformAwsDao.read(platformNo);

        // キー名
        List<KeyPairInfo> keyPairs = awsDescribeService.getKeyPairs(userNo, platformNo);
        for (KeyPairInfo keyPair : keyPairs) {
            response.getKeyNames().add(keyPair.getKeyName());
        }

        // セキュリティグループ
        List<SecurityGroup> securityGroups = awsDescribeService.getSecurityGroups(userNo, platformNo);
        for (SecurityGroup securityGroup : securityGroups) {
            response.getSecurityGroups().add(securityGroup.getGroupName());
        }

        // デフォルトキーペア
        AwsCertificate certificate = awsCertificateDao.read(userNo, platformNo);
        response.setDefKeyPair(StringUtils.isEmpty(certificate.getDefKeypair()) ? null : certificate.getDefKeypair());

        // サブネット
        if (BooleanUtils.isTrue(aws.getVpc())) {
            List<Subnet> subnets = awsDescribeService.getSubnets(userNo, platformNo);
            for (Subnet subnet : subnets) {
                response.getSubnets().add(subnet.getCidrBlock());
            }

            // デフォルトサブネット
            if (StringUtils.isNotEmpty(certificate.getDefSubnet())) {
                for (Subnet subnet : subnets) {
                    if (StringUtils.equals(subnet.getSubnetId(), certificate.getDefSubnet())) {
                        response.setDefSubnet(subnet.getCidrBlock());
                    }
                }
            }

            // ELBのデフォルトサブネット
            if (StringUtils.isNotEmpty(certificate.getDefLbSubnet())) {
                for (Subnet subnet : subnets) {
                    if (StringUtils.equals(subnet.getSubnetId(), certificate.getDefLbSubnet())) {
                        response.setDefLbSubnet(subnet.getCidrBlock());
                    }
                }
            }
        }

        return response;
    }

    private PlatformCloudstackResponse getCloudstackDetail(Long userNo, Long platformNo) {
        PlatformCloudstackResponse response = new PlatformCloudstackResponse();

        //デフォルトキーペア
        CloudstackCertificate certificate = cloudstackCertificateDao.read(userNo, platformNo);
        response.setDefKeyPair(StringUtils.isEmpty(certificate.getDefKeypair()) ? null : certificate.getDefKeypair());

        return response;
    }

    private PlatformVmwareResponse getVmwareDetail(Long userNo, Long platformNo) {
        PlatformVmwareResponse response = new PlatformVmwareResponse();

        // キー名
        List<VmwareKeyPair> keyPairs = vmwareDescribeService.getKeyPairs(userNo, platformNo);
        for (VmwareKeyPair keyPair : keyPairs) {
            response.getKeyNames().add(keyPair.getKeyName());
        }

        // ComputeResource
        List<ComputeResource> computeResources = vmwareDescribeService.getComputeResources(platformNo);
        for (ComputeResource computeResource : computeResources) {
            response.getComputeResources().add(computeResource.getName());
        }

        return response;
    }

    private PlatformNiftyResponse getNiftyDetail(Long userNo, Long platformNo) {
        return new PlatformNiftyResponse();
    }

    private PlatformVcloudResponse getVcloudDetail(Long userNo, Long platformNo) {
        return new PlatformVcloudResponse();
    }

    private PlatformOpenstackResponse getOpenstackDetail(Long userNo, Long platformNo) {
        return new PlatformOpenstackResponse();
    }

    private PlatformAzureResponse getAzureDetail(Long userNo, Long platformNo) {
        return new PlatformAzureResponse();
    }

}
