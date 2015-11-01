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
package jp.primecloud.auto.api.lb;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.api.response.lb.EditLoadBalancerAutoScalingResponse;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.AutoScalingConf;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.ImageVcloud;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/EditLoadBalancerAutoScaling")
public class EditLoadBalancerAutoScaling extends ApiSupport {

    /**
     *
     * ロードバランサ オートスケーリング情報 編集
     *
     * @param userName ユーザ名
     * @param farmNo ファーム番号
     * @param loadBalancerNo ロードバランサ番号
     * @param enabled 有効/無効
     * @param platformNo プラットフォーム番号
     * @param imageNo イメージ番号
     * @param instanceType インスタンス種別
     * @param namingRule ネーミングルール(増減サーバ名称規約)
     * @param idleTimeMax 増加指標CPU使用率(%)
     * @param idleTimeMin 削減指標CPU使用率(%)
     * @param continueLimit 監視継続時間(秒)
     * @param addCount 増加サーバー数(台)
     * @param delCount 削減サーバー数(台)
     *
     * @return EditLoadBalancerAutoScalingResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public EditLoadBalancerAutoScalingResponse editLoadBalancer(
	        @QueryParam(PARAM_NAME_USER) String userName,
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo,
            @QueryParam(PARAM_NAME_ENABLED) String enabled,
            @QueryParam(PARAM_NAME_PLATFORM_NO) String platformNo,
            @QueryParam(PARAM_NAME_IMAGE_NO) String imageNo,
            @QueryParam(PARAM_NAME_INSTANCE_TYPE) String instanceType,
            @QueryParam(PARAM_NAME_NAMING_RULE) String namingRule,
            @QueryParam(PARAM_NAME_IDLE_TIME_MAX) String idleTimeMax,
            @QueryParam(PARAM_NAME_IDLE_TIME_MIN) String idleTimeMin,
            @QueryParam(PARAM_NAME_CONTINUE_LIMIT) String continueLimit,
            @QueryParam(PARAM_NAME_ADD_COUNT) String addCount,
	        @QueryParam(PARAM_NAME_DEL_COUNT) String delCount){

        EditLoadBalancerAutoScalingResponse response = new EditLoadBalancerAutoScalingResponse();

            // 入力チェック
            // Key(ユーザ名)
            ApiValidate.validateUser(userName);
            User user = userDao.readByUsername(userName);
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // LoadBalancerNo
            ApiValidate.validateLoadBalancerNo(loadBalancerNo);

            LoadBalancer loadBalancer = loadBalancerDao.read(Long.parseLong(loadBalancerNo));
            if (loadBalancer == null) {
                // ロードバランサが存在しない
                throw new AutoApplicationException("EAPI-100000", "LoadBalancer",
                        PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }
            if (!loadBalancer.getFarmNo().equals(Long.parseLong(farmNo))) {
                //ファームとロードバランサが一致しない
                throw new AutoApplicationException("EAPI-100022", "LoadBalancer", farmNo, PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }

            // Enabled
            ApiValidate.validateEnabled(enabled);

            if (Boolean.valueOf(enabled)) {
                //Enabledがtrueの場合
                //PlatformNo
                ApiValidate.validatePlatformNo(platformNo);

                //プラットフォーム取得
                Platform platform = platformDao.read(Long.parseLong(platformNo));
                if (platform == null) {
                    //プラットフォームが存在しない
                    throw new AutoApplicationException("EAPI-100000", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
                }
                if (!platformService.isUseablePlatforms(user.getUserNo(), platform) ||
                    BooleanUtils.isNotTrue(platform.getSelectable())) {
                    //認証情報が存在しない or 有効ではないプラットフォーム
                    throw new AutoApplicationException("EAPI-000020", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
                }

                Platform lbPlatform = platformDao.read(loadBalancer.getPlatformNo());
                if (PLATFORM_TYPE_AWS.equals(lbPlatform.getPlatformType())) {
                	PlatformAws platformAws = platformAwsDao.read(loadBalancer.getPlatformNo());
                	if(!platformAws.getEuca() && platformAws.getVpc()) {
                		//ロードバランサがEC2+VPCプラットフォームの場合、同プラットフォーム以外設定不可
                		if(!lbPlatform.getPlatformNo().equals(platform.getPlatformNo())) {
                			throw new AutoApplicationException("EAPI-100036", loadBalancer.getLoadBalancerName(),
                			  loadBalancer.getLoadBalancerNo(), platform.getPlatformNo());
                		}
                	}
                }

                //ImageNo
                ApiValidate.validateImageNo(imageNo);
                Image image = imageDao.read(Long.parseLong(imageNo));
                if (image == null || !image.getPlatformNo().equals(platform.getPlatformNo())) {
                    //イメージが存在しない or プラットフォーム番号が一致しない
                    throw new AutoApplicationException("EAPI-100000", "Image", PARAM_NAME_IMAGE_NO, imageNo);
                }
                if (BooleanUtils.isNotTrue(image.getSelectable())) {
                    //選択不可イメージ
                    throw new AutoApplicationException("EAPI-000020", "Image", PARAM_NAME_IMAGE_NO, imageNo);
                }
                //InstanceType
                ApiValidate.validateInstanceType(instanceType, true);
                List<String> instanceTypes = getInstanceTypes(platform, image);
                if (!instanceTypes.contains(instanceType)) {
                    // インスタンスタイプがイメージのインスタンスタイプに含まれていない
                    throw new AutoApplicationException("EAPI-000011", imageNo, instanceType);
                }
                //NamingRule
                ApiValidate.validateNamingRule(namingRule);
                namingRule = namingRule + "%d";
                //IdleTimeMax
                ApiValidate.validateIdleTimeMax(idleTimeMax);
                //IdleTimeMin
                ApiValidate.validateIdleTimeMin(idleTimeMin);
                //ContinueLimit
                ApiValidate.validateContinueLimit(continueLimit);
                //AddCount
                ApiValidate.validateAddCount(addCount);
                //DelCount
                ApiValidate.validateDelCount(delCount);
            }

            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
            if (LoadBalancerStatus.WARNING == status) {
                // ロードバランサ ステータスが Warning
                throw new AutoApplicationException("EAPI-100025", loadBalancerNo);
            }

            //オートスケーリング情報 更新
            if (Boolean.valueOf(enabled)) {
                //有効
                loadBalancerService.updateAutoScalingConf(
                        loadBalancer.getFarmNo(), Long.parseLong(loadBalancerNo),
                        Long.parseLong(platformNo), Long.parseLong(imageNo),
                        instanceType, Boolean.parseBoolean(enabled) ? 1: 0,
                        namingRule, Long.parseLong(idleTimeMax),
                        Long.parseLong(idleTimeMin), Long.parseLong(continueLimit),
                        Long.parseLong(addCount), Long.parseLong(delCount));
            } else {
                //無効
                AutoScalingConf autoScalingConf = autoScalingConfDao.read(Long.parseLong(loadBalancerNo));
                loadBalancerService.updateAutoScalingConf(
                        loadBalancer.getFarmNo(), Long.parseLong(loadBalancerNo),
                        autoScalingConf.getPlatformNo(), autoScalingConf.getImageNo(),
                        autoScalingConf.getInstanceType(), Boolean.parseBoolean(enabled) ? 1: 0,
                        autoScalingConf.getNamingRule(), autoScalingConf.getIdleTimeMax(),
                        autoScalingConf.getIdleTimeMin(), autoScalingConf.getContinueLimit(),
                        autoScalingConf.getAddCount(), autoScalingConf.getDelCount());
            }

            response.setSuccess(true);

        return  response;
    }

    /**
     *
     * イメージからインスタンスタイプの一覧(リスト)を取得
     *
     * @param platform プラットフォーム
     * @param image イメージ
     * @return インスタンスタイプの一覧(リスト)
     */
    private List<String> getInstanceTypes(Platform platform, Image image) {
        String instanceTypesText = null;
        if (PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            //Aws or Eucalyptus
            ImageAws imageAws = imageAwsDao.read(image.getImageNo());
            instanceTypesText = imageAws.getInstanceTypes();
        } else if (PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            //VMWare
            ImageVmware imageVmware = imageVmwareDao.read(image.getImageNo());
            instanceTypesText = imageVmware.getInstanceTypes();
        } else if (PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            //Nifty
            ImageNifty imageNifty = imageNiftyDao.read(image.getImageNo());
            instanceTypesText = imageNifty.getInstanceTypes();
        } else if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            //CloudStack
            ImageCloudstack imageCloudstack = imageCloudstackDao.read(image.getImageNo());
            instanceTypesText = imageCloudstack.getInstanceTypes();
        } else if (PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            //VCloud
            ImageVcloud imageVcloud = imageVcloudDao.read(image.getImageNo());
            instanceTypesText = imageVcloud.getInstanceTypes();
        }
        List<String> instanceTypes = getInstanceTypesFromText(instanceTypesText);
        return instanceTypes;
    }

    /**
     *
     * イメージのインスタンスタイプ一覧文字列(カンマ区切りのインスタンスタイプの一覧)からインスタンスの一覧(リスト)を取得
     *
     * @param instanceTypesText インスタンスタイプ一覧文字列
     * @return instanceTypes インスタンスタイプの一覧(リスト)
     */
    private static List<String> getInstanceTypesFromText(String instanceTypesText) {
        List<String> instanceTypes = new ArrayList<String>();
        if (StringUtils.isNotEmpty(instanceTypesText)) {
            for (String instanceType: StringUtils.split(instanceTypesText, ",")) {
                instanceTypes.add(instanceType.trim());
            }
        }
        return instanceTypes;
    }
}