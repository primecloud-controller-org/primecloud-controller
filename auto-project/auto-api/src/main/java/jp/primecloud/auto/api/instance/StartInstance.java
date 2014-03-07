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
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;



@Path("/StartInstance")
public class StartInstance extends ApiSupport {

    /**
     *
     * サーバ起動
     *
     * @param farmNo ファーム番号
     * @param instanceNo インスタンス番号
     * @param isStartService サービス起動有無 true:サービスも起動、false:サーバのみ起動、null:サーバのみ起動
     *
     * @return StartInstanceResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public StartInstanceResponse startInstance(
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
	        @QueryParam(PARAM_NAME_IS_START_SERVICE) String isStartService){

        StartInstanceResponse response = new StartInstanceResponse();

        try {
            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // InstanceNo
            ApiValidate.validateInstanceNo(instanceNo);
            // IsStartService
            ApiValidate.validateIsStartService(isStartService);

            // インスタンス取得
            Instance instance = instanceDao.read(Long.parseLong(instanceNo));
            if (instance == null || BooleanUtils.isTrue(instance.getLoadBalancer())) {
                // インスタンスが存在しない or インスタンスがロードバランサの場合
                throw new AutoApplicationException("EAPI-100000", "Instance",
                        PARAM_NAME_INSTANCE_NO, instanceNo);
            }

            if (BooleanUtils.isFalse(instance.getFarmNo().equals(Long.valueOf(farmNo)))) {
                //ファームとインスタンスが一致しない
                throw new AutoApplicationException("EAPI-100022", "Instance", farmNo, PARAM_NAME_INSTANCE_NO, instanceNo);
            }

            // インスタンスのステータスチェック(停止状態のインスタンスのみ起動対象)
            InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
            if (InstanceStatus.STOPPED != status) {
                // インスタンスのステータスが停止状態でない
                throw new AutoApplicationException("EAPI-100006", instanceNo);
            }

            // EC2+VPCの場合のサブネットチェック
            Platform platform = platformDao.read(instance.getPlatformNo());
            if (PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
                AwsInstance awsInstance = awsInstanceDao.read(Long.parseLong(instanceNo));
                if(platformAws.getVpc() && StringUtils.isEmpty(awsInstance.getSubnetId())) {
                    // EC2+VPCでサブネットが設定されていないサーバは起動不可
                    throw new AutoApplicationException("EAPI-100033", instance.getInstanceName());
                }
            }

            // サーバ起動設定処理
            List<Long> instanceNos = new ArrayList<Long>();
            instanceNos.add(Long.parseLong(instanceNo));
            if (StringUtils.isEmpty(isStartService)) {
                processService.startInstances(Long.parseLong(farmNo), instanceNos);
            } else {
                processService.startInstances(Long.parseLong(farmNo), instanceNos, Boolean.parseBoolean(isStartService));
            }

            response.setSuccess(true);
        } catch (Throwable e){
            String message = "";
            if (e instanceof AutoException || e instanceof AutoApplicationException) {
                message = e.getMessage();
            } else {
                message = MessageUtils.getMessage("EAPI-000000");
            }
            log.error(message, e);
            response.setMessage(message);
            response.setSuccess(false);
        }

        return  response;
	}
}