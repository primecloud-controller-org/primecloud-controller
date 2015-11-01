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


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.lb.CreateLoadBalancerResponse;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;


@Path("/CreateLoadBalancer")
public class CreateLoadBalancer extends ApiSupport {

    /**
     *
     * ロードバランサリスナ作成
     *
     * @param userName ユーザ名
     * @param farmNo ファーム番号
     * @param loadBalancerName ロードバランサ番号
     * @param platformNo プラットフォーム番号
     * @param loadBalancerType ロードバランサー種別(aws or ultramonkey)
     * @param componentNo コンポーネント番号
     * @param comment コメント
     *
     * @return CreateLoadBalancerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CreateLoadBalancerResponse createLoadBalancer(
            @QueryParam(PARAM_NAME_USER) String userName,
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NAME) String loadBalancerName,
            @QueryParam(PARAM_NAME_PLATFORM_NO) String platformNo,
            @QueryParam(PARAM_NAME_LOAD_BALANCER_TYPE) String loadBalancerType,
            @QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo,
            @QueryParam(PARAM_NAME_COMMENT) String comment,
            @QueryParam(PARAM_NAME_IS_INTERNAL) String isInternal){

        CreateLoadBalancerResponse response = new CreateLoadBalancerResponse();

            User user = userDao.readByUsername(userName);

            // 入力チェック
            // Key(ユーザ名)
            ApiValidate.validateUser(userName);
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // LoadBalancerName
            ApiValidate.validateLoadBalancerName(loadBalancerName);
            // PlatformNo
            ApiValidate.validatePlatformNo(platformNo);
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

            // LoadBalancerType
            ApiValidate.validateLoadBalancerType(loadBalancerType);
            PlatformAws platformAws = platformAwsDao.read(Long.parseLong(platformNo));
            if (LB_TYPE_ELB.equals(loadBalancerType) && (PLATFORM_TYPE_AWS.equals(platform.getPlatformType()) == false || platformAws.getEuca())) {
                //loadBalancerType=ELB、プラットフォーム=EC2以外の場合→エラー
                //EC2プラットフォームのみ ELB(Elastic Load Balancing)が使用可能
                throw new AutoApplicationException("EAPI-000015", platform.getPlatformNo(), loadBalancerType);
            }
            // ComponentNo
            ApiValidate.validateComponentNo(componentNo);
            // Comment
            ApiValidate.validateComment(comment);
            // isInternal
            boolean internal = false;
            if (isInternal !=  null) {
                ApiValidate.validateIsInternal(isInternal);
                internal = Boolean.parseBoolean(isInternal);
            }
            if (!LB_TYPE_ELB.equals(loadBalancerType) || !platformAws.getVpc()) {
                if (BooleanUtils.isTrue(internal)) {
                    // ELB かつプラットフォームがVPC以外の場合は内部ロードバランサ指定不可
                    throw new AutoApplicationException("EAPI -100041", loadBalancerName);
                }
            }

            // コンポーネント取得
            Component component = componentDao.read(Long.parseLong(componentNo));
            if (component == null) {
                // コンポーネントが存在しない
                throw new AutoApplicationException("EAPI-100000", "Component", PARAM_NAME_COMPONENT_NO, componentNo);
            }
            if (BooleanUtils.isFalse(component.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとコンポーネントが一致しない
                throw new AutoApplicationException("EAPI-100022", "Component", farmNo, PARAM_NAME_COMPONENT_NO, componentNo);
            }

            // ロードバランサー作成
            Long newLoadBalancerNo = null;
            if (LB_TYPE_ELB.equals(loadBalancerType)) {
                //AWS ELB(Elastic Load Balancing)
                newLoadBalancerNo = loadBalancerService.createAwsLoadBalancer(
                        Long.parseLong(farmNo), loadBalancerName, comment,
                        Long.parseLong(platformNo), Long.parseLong(componentNo), internal);
            } else if (LB_TYPE_ULTRA_MONKEY.equals(loadBalancerType)) {
                //ultraMonkey
                newLoadBalancerNo = loadBalancerService.createUltraMonkeyLoadBalancer(
                        Long.parseLong(farmNo), loadBalancerName, comment,
                        Long.parseLong(platformNo), Long.parseLong(componentNo));

            } else if (LB_TYPE_CLOUDSTACK.equals(loadBalancerType)) {
                //cloudstack
                newLoadBalancerNo = loadBalancerService.createCloudstackLoadBalancer(
                        Long.parseLong(farmNo), loadBalancerName, comment,
                        Long.parseLong(platformNo), Long.parseLong(componentNo));
            }

            response.setLoadBalancerNo(newLoadBalancerNo);
            response.setSuccess(true);

        return  response;

    }
}