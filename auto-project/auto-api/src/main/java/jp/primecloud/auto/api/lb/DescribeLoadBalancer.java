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


import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.lb.AutoScalingConfResponse;
import jp.primecloud.auto.api.response.lb.DescribeLoadBalancerResponse;
import jp.primecloud.auto.api.response.lb.LoadBalancerHealthCheckResponse;
import jp.primecloud.auto.api.response.lb.LoadBalancerInstanceResponse;
import jp.primecloud.auto.api.response.lb.LoadBalancerListenerResponse;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.entity.crud.AutoScalingConf;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.impl.Comparators;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;



@Path("/DescribeLoadBalancer")
public class DescribeLoadBalancer extends ApiSupport {

    /**
     *
     * ロードバランサ情報取得
     *
     * @param farmNo ファーム番号
     * @param loadBalancerNo ロードバランサ番号
     *
     * @return DescribeLoadBalancerResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DescribeLoadBalancerResponse describeLoadBalancer(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo){

        DescribeLoadBalancerResponse response = new DescribeLoadBalancerResponse();

        try {
            // 入力チェック
            // FarmNo
            ApiValidate.validateFarmNo(farmNo);
            // LoadBalancerNo
            ApiValidate.validateLoadBalancerNo(loadBalancerNo);

            // ロードバランサ取得
            LoadBalancer loadBalancer = loadBalancerDao.read(Long.parseLong(loadBalancerNo));
            if (loadBalancer == null) {
                // ロードバランサが存在しない
                throw new AutoApplicationException("EAPI-100000", "LoadBalancer", PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }

            if (BooleanUtils.isFalse(loadBalancer.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとロードバランサが一致しない
                throw new AutoApplicationException("EAPI-100022", "LoadBalancer", farmNo, PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }

            //ロードバランサ情報設定
            response = new DescribeLoadBalancerResponse(loadBalancer);

            //リスナー取得
            List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(Long.parseLong(loadBalancerNo));
            if (listeners.isEmpty() == false) {
                //ソート
                Collections.sort(listeners, Comparators.COMPARATOR_LOAD_BALANCER_LISTENER);
            }
            for (LoadBalancerListener listener: listeners) {
                //リスナー情報設定
                response.addListener(new LoadBalancerListenerResponse(listener));
            }

            //ヘルスチェック取得
            LoadBalancerHealthCheck healthCheck = loadBalancerHealthCheckDao.read(Long.parseLong(loadBalancerNo));
            if (healthCheck != null) {
                //ヘルスチェック情報設定
                response.setHealthCheck(new LoadBalancerHealthCheckResponse(healthCheck));
            }

            //コンポーネントインスタンス情報取得
            List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(loadBalancer.getComponentNo());
            if (componentInstances.isEmpty() == false) {
                //ソート
                Collections.sort(componentInstances, Comparators.COMPARATOR_COMPONENT_INSTANCE);
            }

            for (ComponentInstance componentInstance: componentInstances) {
                // 関連付けが無効で停止している場合は除外
                if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                    ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                    if (status == ComponentInstanceStatus.STOPPED) {
                        continue;
                    }
                }
                //ロードバランサインスタンス取得
                LoadBalancerInstance loadBalancerInstance =
                    loadBalancerInstanceDao.read(Long.parseLong(loadBalancerNo), componentInstance.getInstanceNo());

                LoadBalancerInstanceResponse loadBalancerInstanceResponse = new LoadBalancerInstanceResponse();
                loadBalancerInstanceResponse.setInstanceNo(componentInstance.getInstanceNo());
                if (loadBalancerInstance == null) {
                    //ロードバランサインスタンスが存在しない場合、ロードバランサインスタンス作成時のデータを設定
                    loadBalancerInstanceResponse.setEnabled(false);
                    loadBalancerInstanceResponse.setStatus(LoadBalancerInstanceStatus.STOPPED.toString());
                } else {
                    loadBalancerInstanceResponse.setEnabled(loadBalancerInstance.getEnabled());
                    loadBalancerInstanceResponse.setStatus(loadBalancerInstance.getStatus());
                }
                //ロードバランサインスタンス情報設定
                response.addInstance(loadBalancerInstanceResponse);
            }

            //オートスケーリング取得
            AutoScalingConf autoScalingConf = autoScalingConfDao.read(Long.parseLong(loadBalancerNo));
            if (autoScalingConf != null) {
                //オートスケーリング情報設定
                response.setAutoScaling(new AutoScalingConfResponse(autoScalingConf));
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