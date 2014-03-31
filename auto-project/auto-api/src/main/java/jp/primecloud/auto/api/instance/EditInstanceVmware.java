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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.instance.EditInstanceVmwareResponse;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.dto.VmwareAddressDto;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vmware.vim25.mo.ComputeResource;


@Path("/EditInstanceVmware")
public class EditInstanceVmware extends ApiSupport {

    /**
     *
     * サーバ編集(VMware)
     * @param userName PCCユーザ名
     * @param farmNo ファーム番号
     * @param instanceNo インスタンス番号
     * @param instanceType インスタンスタイプ
     * @param keyName キーペア名
     * @param コンピュートリソース(クラスタ)
     * @param isStaticIp 固定IPを使用するか否か true:固定IP、false:動的IP
     * @param ipAddress 固定IPアドレス
     * @param subnetMask サブネットマスク
     * @param defaultGateway ゲートウェイ
     * @param comment コメント
     *
     * @return EditInstanceVmwareResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public EditInstanceVmwareResponse editInstanceVmware(
            @Context UriInfo uriInfo,
            @QueryParam(PARAM_NAME_USER) String userName,
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
            @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType,
            @QueryParam(PARAM_NAME_KEY_NAME) String keyName,
            @QueryParam(PARAM_NAME_COMPUTE_RESOURCE) String computeResource,
            @QueryParam(PARAM_NAME_IS_STATIC_IP) String isStaticIp,
            @QueryParam(PARAM_NAME_IP_ADDRESS) String ipAddress,
            @QueryParam(PARAM_NAME_SUBNET_MASK) String subnetMask,
            @QueryParam(PARAM_NAME_DEFAULT_GATEWAY) String defaultGateway,
            @QueryParam(PARAM_NAME_COMMENT) String comment){

        EditInstanceVmwareResponse response = new EditInstanceVmwareResponse();

        try {
            // 入力チェック
            // Key(ユーザ名)
            ApiValidate.validateUser(userName);
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // InstanceNo
            ApiValidate.validateInstanceNo(instanceNo);

            // ユーザ取得
            User user = userDao.readByUsername(userName);
            if (user == null) {
                // ユーザが存在しない
                throw new AutoApplicationException("EAPI-100000", "User",
                        "UserName", userName);
            }

            //インスタンス取得
            Instance instance = instanceDao.read(Long.parseLong(instanceNo));
            if (instance == null || BooleanUtils.isTrue(instance.getLoadBalancer())) {
                // インスタンスが存在しない or インスタンスがロードバランサ
                throw new AutoApplicationException("EAPI-100000", "Instance",
                        PARAM_NAME_INSTANCE_NO, instanceNo);
            }
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
            if (InstanceStatus.STOPPED != status) {
                // インスタンスが停止済み以外
                throw new AutoApplicationException("EAPI-100014", instanceNo);
            }

            if (instance.getFarmNo().equals(Long.parseLong(farmNo)) == false) {
                //ファームとインスタンスが一致しない
                throw new AutoApplicationException("EAPI-100022", "Instance", farmNo, PARAM_NAME_INSTANCE_NO, instanceNo);
            }

            //プラットフォーム取得
            Platform platform = platformDao.read(instance.getPlatformNo());
            if (platform == null) {
                // プラットフォームが存在しない
                throw new AutoApplicationException("EAPI-100000", "Platform",
                        PARAM_NAME_PLATFORM_NO, instance.getPlatformNo());
            }
            if ("vmware".equals(platform.getPlatformType()) == false) {
                //プラットフォームがVMware以外
                throw new AutoApplicationException("EAPI-100031", "VMware", instanceNo, instance.getPlatformNo());
            }

            // イメージ取得
            Image image = imageDao.read(instance.getImageNo());
            if (image == null || image.getPlatformNo().equals(platform.getPlatformNo()) == false) {
                // イメージが存在しない
                throw new AutoApplicationException("EAPI-100000", "Image",
                        PARAM_NAME_IMAGE_NO, instance.getImageNo());
            }

            // InstanceType
            ApiValidate.validateInstanceType(instanceType, true);
            if(checkInstanceType(platform, image, instanceType) == false) {
                // インスタンスタイプがイメージのインスタンスタイプに含まれていない
                throw new AutoApplicationException("EAPI-000011", instance.getImageNo(), instanceType);
            }

            // KeyName
            ApiValidate.validateKeyName(keyName);
            Long keyPairNo = getKeyPairNo(user.getUserNo(), platform.getPlatformNo(), keyName);
            if (keyPairNo == null) {
                // キーペアがプラットフォームに存在しない
                throw new AutoApplicationException("EAPI-000012", platform.getPlatformNo(), keyName);
            }

            // ComputeResource
            ApiValidate.validateComputeResource(computeResource);
            if (checkComputeResource(platform.getPlatformNo(), computeResource) == false) {
                // コンピュートリソースがプラットフォームに存在しない
                throw new AutoApplicationException("EAPI-000016", platform.getPlatformNo(), computeResource);
            }

            // IsStaticIp
            ApiValidate.validateIsStaticIp(isStaticIp);
            VmwareAddressDto addressDto = null;
            if (Boolean.parseBoolean(isStaticIp)) {
                //IpAddress
                ApiValidate.validateIpAddress(ipAddress, true);
                //SubnetMask
                ApiValidate.validateSubnetMask(subnetMask);
                //DefaultGateway
                ApiValidate.validateDefaultGateway(defaultGateway);

                addressDto = new VmwareAddressDto();
                addressDto.setIpAddress(ipAddress);
                addressDto.setSubnetMask(subnetMask);
                addressDto.setDefaultGateway(defaultGateway);
            }
            // Comment
            ApiValidate.validateComment(comment);

            //インスタンス(VMware)の更新
            instanceService.updateVmwareInstance(
                    Long.parseLong(instanceNo), instance.getInstanceName(), comment,
                    instanceType, computeResource, null, keyPairNo, addressDto);

            response.setSuccess(true);
        } catch (Throwable e){
            String message = "";
            if (StringUtils.isEmpty(e.getMessage())) {
                message = MessageUtils.getMessage("EAPI-000000");
            } else {
                message = e.getMessage();
            }
            log.error(message, e);
            response.setMessage(message);
            response.setSuccess(false);
        }

        return  response;
    }

    /**
     *
     * カンマ区切りのインスタンスタイプ(名称)の文字列からインスタンスタイプ(名称)のリストを作成する
     *
     * @param instanceTypesText カンマ区切りのインスタンスタイプ(名称)文字列
     * @return インスタンスタイプ(名称)のリスト
     */
    private static List<String> getInstanceTypes(String instanceTypesText) {
        List<String> instanceTypes = new ArrayList<String>();
        if (StringUtils.isNotEmpty(instanceTypesText)) {
            for (String instanceType: StringUtils.split(instanceTypesText, ",")) {
                instanceTypes.add(instanceType.trim());
            }
        }
        return instanceTypes;
    }

    /**
     *
     * インスタンスタイプ(名称)が対象イメージで使用可能かチェックする
     *
     * @param platform プラットフォーム
     * @param image イメージ
     * @param instanceType インスタンスタイプ
     * @return true:存在する、false:存在しない
     */
    private boolean checkInstanceType(Platform platform, Image image, String instanceType) {
        List<String> instanceTypes = new ArrayList<String>();
        //VMWare
        ImageVmware imageVmware = imageVmwareDao.read(image.getImageNo());
        instanceTypes = getInstanceTypes(imageVmware.getInstanceTypes());
        return instanceTypes.contains(instanceType);
    }

    /**
     *
     * キーペア名からキーペアNoを取得
     *
     * @param userNo ユーザ番号
     * @param platformNo プラットフォーム番号
     * @param keyName キーペア名
     * @return キーペアNo(存在しない場合はNull)
     */
    private Long getKeyPairNo(Long userNo, Long platformNo, String keyName) {
        Long keyPairNo = null;
        //VMWare
        List<VmwareKeyPair> vmwareKeyPairs = vmwareDescribeService.getKeyPairs(userNo, platformNo);
        for (VmwareKeyPair vmwareKeyPair: vmwareKeyPairs) {
            if (StringUtils.equals(vmwareKeyPair.getKeyName(), keyName)) {
                keyPairNo = vmwareKeyPair.getKeyNo();
                break;
            }
        }

        return keyPairNo;
    }

    /**
     *
     * コンピュートリソース（クラスタ）が存在するかチェックする
     * @param platformNo プラットフォーム番号
     * @param computeResourceName コンピュートリソース
     * @return
     */
    private boolean checkComputeResource(Long platformNo, String computeResourceName) {
        boolean isContain = false;
        //VMWare
        List<ComputeResource> computeResources = vmwareDescribeService.getComputeResources(platformNo);
        for (ComputeResource computeResource: computeResources) {
            if (computeResource.getName().equals(computeResourceName)) {
                isContain = true;
                break;
            }
        }
        return isContain;
    }
}