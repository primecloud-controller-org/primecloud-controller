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
import jp.primecloud.auto.api.response.instance.StartInstanceResponse;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AzureInstance;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.StringUtils;

@Path("/StartInstance")
public class StartInstance extends ApiSupport {

    /**
     * サーバ起動
     *
     * @param instanceNo インスタンス番号
     * @param isStartService サービス起動有無 true:サービスも起動、false:サーバのみ起動、null:サーバのみ起動
     * @return StartInstanceResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StartInstanceResponse startInstance(@QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
            @QueryParam(PARAM_NAME_IS_START_SERVICE) String isStartService) {

        // 入力チェック
        // InstanceNo
        ApiValidate.validateInstanceNo(instanceNo);
        // IsStartService
        ApiValidate.validateIsStartService(isStartService);

        // インスタンス取得
        Instance instance = getInstance(Long.parseLong(instanceNo));

        // 権限チェック
        checkAndGetUser(instance);

        // インスタンスのステータスチェック(停止状態のインスタンスのみ起動対象)
        InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
        if (InstanceStatus.STOPPED != status) {
            // インスタンスのステータスが停止状態でない
            throw new AutoApplicationException("EAPI-100006", instanceNo);
        }

        Platform platform = platformDao.read(instance.getPlatformNo());
        PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
        AwsInstance awsInstance = awsInstanceDao.read(Long.parseLong(instanceNo));
        AzureInstance azureInstance = azureInstanceDao.read(Long.parseLong(instanceNo));

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
                throw new AutoApplicationException("EAPI-100033", instance.getInstanceName());
            }
        }
        if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            // サブネットチェック
            subnetId = azureInstance.getSubnetId();
            subnetErrFlg = processService.checkSubnet(platform.getPlatformType(), vpc, subnetId);
            if (subnetErrFlg == true) {
                // サブネットが設定されていないサーバは起動不可
                throw new AutoApplicationException("EAPI-100033", instance.getInstanceName());
            }
            // インスタンス起動チェック（個別起動）
            boolean startupErrFlg;
            startupErrFlg = processService.checkStartup(platform.getPlatformType(), azureInstance.getInstanceName(),
                    azureInstance.getInstanceNo());
            if (startupErrFlg == true) {
                // インスタンス作成中のものがあった場合は、起動不可
                // 同一インスタンスNoは、除外する
                throw new AutoApplicationException("EAPI-100038", instance.getInstanceName());
            }
        }

        // サーバ起動設定処理
        List<Long> instanceNos = new ArrayList<Long>();
        instanceNos.add(Long.parseLong(instanceNo));
        if (StringUtils.isEmpty(isStartService)) {
            processService.startInstances(instance.getFarmNo(), instanceNos);
        } else {
            processService.startInstances(instance.getFarmNo(), instanceNos, Boolean.parseBoolean(isStartService));
        }

        StartInstanceResponse response = new StartInstanceResponse();

        return response;
    }

}
