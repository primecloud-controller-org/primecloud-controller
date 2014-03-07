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
import jp.primecloud.auto.api.response.lb.EditLoadBalancerHealthCheckResponse;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;



@Path("/EditLoadBalancerHealthCheck")
public class EditLoadBalancerHealthCheck extends ApiSupport {

    /**
     *
     * ロードバランサ ヘルスチェック情報 編集
     * @param farmNo ファーム番号
     * @param loadBalancerNo ロードバランサ番号
     * @param checkProtocol プロトコル
     * @param checkPort ポート
     * @param checkPath 監視パス
     * @param checkTimeout タイムアウト
     * @param checkInterval チェック間隔 （秒）
     * @param healthyThreshold 障害 しきい値 （回数）
     * @param unhealthyThreshold 復帰 しきい値 （回数）
     *
     * @return EditLoadBalancerHealthCheckResponse
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public EditLoadBalancerHealthCheckResponse editLoadBalancerHealthCheck(
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo,
            @QueryParam(PARAM_NAME_CHECK_PROTOCOL) String checkProtocol,
            @QueryParam(PARAM_NAME_CHECK_PORT) String checkPort,
            @QueryParam(PARAM_NAME_CHECK_PATH) String checkPath,
            @QueryParam(PARAM_NAME_CHECK_TIMEOUT) String checkTimeout,
            @QueryParam(PARAM_NAME_CHECK_INTERVAL) String checkInterval,
            @QueryParam(PARAM_NAME_HEALTHY_THRESHOLD) String healthyThreshold,
            @QueryParam(PARAM_NAME_UNHEALTHY_THRESHOLD) String unhealthyThreshold){

        EditLoadBalancerHealthCheckResponse response = new EditLoadBalancerHealthCheckResponse();

        try {
            // 入力チェック
            // LoadBalancerNo
            ApiValidate.validateLoadBalancerNo(loadBalancerNo);
            LoadBalancer loadBalancer = loadBalancerDao.read(Long.parseLong(loadBalancerNo));
            if (loadBalancer == null) {
                // ロードバランサが存在しない
                throw new AutoApplicationException("EAPI-100000", "LoadBalancer",
                        PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }
            if (BooleanUtils.isFalse(loadBalancer.getFarmNo().equals(Long.parseLong(farmNo)))) {
                //ファームとロードバランサが一致しない
                throw new AutoApplicationException("EAPI-100022", "LoadBalancer", farmNo, PARAM_NAME_LOAD_BALANCER_NO, loadBalancerNo);
            }
            // CheckProtocol
            ApiValidate.validateCheckProtocol(checkProtocol);
            // CheckPort
            ApiValidate.validateCheckPort(checkPort);
            // CheckPath
            ApiValidate.validateCheckPath(checkPath, "HTTP".equals(checkProtocol));
            if (StringUtils.equals("TCP", checkProtocol)) {
                //監視プロトコルが TCP の場合、監視Pathは入力不可 → 入力値を空にする
                checkPath = "";
            }
            // CheckTimeout
            ApiValidate.validateCheckTimeout(checkTimeout);
            // CheckInterval
            ApiValidate.validateCheckInterval(checkInterval);
            // HealthyThreshold
            if (LB_TYPE_ELB.equals(loadBalancer.getType())) {
                //AWSの場合のみ編集可
                ApiValidate.validateHealthyThreshold(healthyThreshold);
            } else if(LB_TYPE_ULTRA_MONKEY.equals(loadBalancer.getType())){
                //UltraMonkeyの場合は編集不可(テーブルの値を変えずに更新)
                LoadBalancerHealthCheck loadBalancerHealthCheck = loadBalancerHealthCheckDao.read(Long.parseLong(loadBalancerNo));
                healthyThreshold = String.valueOf(loadBalancerHealthCheck.getHealthyThreshold());
            }
            // UnhealthyThreshold
            ApiValidate.validateUnhealthyThreshold(unhealthyThreshold);

            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
            if (LoadBalancerStatus.WARNING == status) {
                // ロードバランサ ステータスが Warning
                throw new AutoApplicationException("EAPI-100025", loadBalancerNo);
            }

            //ヘルスチェック情報 更新
            loadBalancerService.configureHealthCheck(
                    Long.parseLong(loadBalancerNo), checkProtocol, Integer.parseInt(checkPort),
                    checkPath, Integer.parseInt(checkTimeout), Integer.parseInt(checkInterval),
                    Integer.parseInt(healthyThreshold), Integer.parseInt(unhealthyThreshold));

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