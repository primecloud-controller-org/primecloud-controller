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
package jp.primecloud.auto.api.component;


import java.util.ArrayList;
import java.util.HashMap;
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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.api.response.component.StartComponentResponse;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AzureInstance;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoApplicationException;


@Path("/StartComponent")
public class StartComponent extends ApiSupport{

    /**
     *
     * サービス起動処理
     *
     * @param uriInfo URI情報(InstanceNo取得の為)
     * @param farmNo ファーム番号
     * @param componentNo コンポーネント番号
     * @param instanceNos インスタンス番号(複数、カンマ区切り)
     * @return StartComponentResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public StartComponentResponse startComponent(
            @Context UriInfo uriInfo,
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo,
            @QueryParam(PARAM_NAME_INSTANCE_NOS) String instanceNos){

        StartComponentResponse response = new StartComponentResponse();

            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // ComponentNo
            ApiValidate.validateComponentNo(componentNo);
            // InstanceNo
            List<Long> instanceNoList = createInstanceNosToList(instanceNos);

            // ファーム取得
            Farm farm = farmDao.read(Long.parseLong(farmNo));
            if (farm == null) {
                // ファームが存在しない
                throw new AutoApplicationException("EAPI-100000", "Farm", PARAM_NAME_FARM_NO, farmNo);
            }

            // インスタンス取得
            for (Long instanceNo: instanceNoList) {
                Instance instance = instanceDao.read(instanceNo);
                if (instance == null || BooleanUtils.isTrue(instance.getLoadBalancer())) {
                    // インスタンスが存在しない or インスタンスがロードバランサ
                    throw new AutoApplicationException("EAPI-100000", "Instance", PARAM_NAME_INSTANCE_NO, instanceNo);
                }
            }

            // コンポーネント取得
            Component component = componentDao.read(Long.parseLong(componentNo));
            if (component == null || BooleanUtils.isTrue(component.getLoadBalancer())) {
                // コンポーネントが存在しない または ロードバランサコンポーネント
                throw new AutoApplicationException("EAPI-100000",
                        "Component", PARAM_NAME_COMPONENT_NO, componentNo);
            }

            if (BooleanUtils.isFalse(component.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとコンポーネントが一致しない
                throw new AutoApplicationException("EAPI-100022", "Component", farmNo, PARAM_NAME_COMPONENT_NO, componentNo);
            }

            boolean skipServer = false;
            for (Long instanceNo: instanceNoList) {
                // コンポーネントインスタンス取得
                ComponentInstance componentInstance = componentInstanceDao.read(Long.parseLong(componentNo), instanceNo);
                if (componentInstance == null) {
                    // コンポーネントインスタンスが存在しない
                    throw new AutoApplicationException("EAPI-100000", "ComponentInstance", PARAM_NAME_INSTANCE_NO, instanceNo);
                }
                Instance instance = instanceDao.read(componentInstance.getInstanceNo());
                Platform platform = platformDao.read(instance.getPlatformNo());
                    PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
                    AwsInstance awsInstance = awsInstanceDao.read(instance.getInstanceNo());
                AzureInstance azureInstance = azureInstanceDao.read(instance.getInstanceNo());
                boolean vpc = false;
                String subnetId = null;
                boolean subnetErrFlg;
                if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                    // サブネットチェック
                    vpc = platformAws.getVpc();
                    subnetId = awsInstance.getSubnetId();
                    subnetErrFlg = processService.checkSubnet(platform.getPlatformType(), vpc, subnetId);
                    if (subnetErrFlg == true) {
                        // EC2+VPCでサブネットが設定されていないサーバは起動不可
                        throw new AutoApplicationException("EAPI-100034", component.getComponentName(), instance.getInstanceName());
                    }
                }
                if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                    // サブネットチェック
                    subnetId = azureInstance.getSubnetId();
                    subnetErrFlg = processService.checkSubnet(platform.getPlatformType(), vpc, subnetId);
                    if (subnetErrFlg == true) {
                        // サブネットが設定されていないサーバは起動不可
                        throw new AutoApplicationException("EAPI-100034", component.getComponentName(), instance.getInstanceName());
                    }
                    // インスタンス起動チェック（同時起動）
                    HashMap<String, Boolean> flgMap = new HashMap<String, Boolean>();
                    flgMap = processService.checkStartupAll(platform.getPlatformType(),
                            azureInstance.getInstanceName(),
                            skipServer);
                    skipServer = flgMap.get("skipServer");
                    boolean startupAllErrFlg;
                    startupAllErrFlg = flgMap.get("startupAllErrFlg");
                    if (startupAllErrFlg == true) {
                        // インスタンス作成中のものがあった場合は、起動不可
                        throw new AutoApplicationException("EAPI-100039", component.getComponentName(), instance.getInstanceName());
                    }

                    // インスタンス起動チェック（個別起動）
                    boolean startupErrFlg;
                    startupErrFlg = processService.checkStartup(platform.getPlatformType(),
                            azureInstance.getInstanceName(),
                            azureInstance.getInstanceNo());
                    if (startupErrFlg == true) {
                        // インスタンス作成中のものがあった場合は、起動不可
                        // 同一インスタンスNoは、除外する
                        throw new AutoApplicationException("EAPI-100039", component.getComponentName(), instance.getInstanceName());
                    }
                }
            }

            // サービス起動設定
            processService.startComponents(Long.valueOf(farmNo), Long.valueOf(componentNo), instanceNoList);

            response.setSuccess(true);

        return  response;
	}

    private List<Long> createInstanceNosToList(String instanceNos) {
        ApiValidate.validateInstanceNos(instanceNos, false);

        List<Long> logInstanceNos = new ArrayList<Long>();
        for (String tmpInstanceNo: commaTextToList(instanceNos)) {
            ApiValidate.validateInstanceNos(tmpInstanceNo, true);
            logInstanceNos.add(Long.parseLong(tmpInstanceNo));
        }
        return logInstanceNos;
    }

    private static List<String> commaTextToList(String commaText) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isNotEmpty(commaText)) {
            for (String splitStr: StringUtils.split(commaText, ",")) {
                list.add(splitStr.trim());
            }
        }
        return list;
    }
}