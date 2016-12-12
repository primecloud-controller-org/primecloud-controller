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
import jp.primecloud.auto.api.response.lb.CreateLoadBalancerListenerResponse;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.AwsSslKey;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoApplicationException;

@Path("/CreateLoadBalancerListener")
public class CreateLoadBalancerListener extends ApiSupport {

    /**
     * ロードバランサリスナ作成
     *
     * @param loadBalancerNo ロードバランサ番号
     * @param loadBalancerPort ポート番号
     * @param servicePort サービス用ポート番号
     * @param protocol プロトコル
     * @param sslKeyNo AWS SSLのキー番号
     * @return CreateLoadBalancerListenerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CreateLoadBalancerListenerResponse createLoadBalancerListener(
            @QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo,
            @QueryParam(PARAM_NAME_LOAD_BALANCER_PORT) String loadBalancerPort,
            @QueryParam(PARAM_NAME_SERVICE_PORT) String servicePort, @QueryParam(PARAM_NAME_PROTOCOL) String protocol,
            @QueryParam(PARAM_NAME_SSL_KEY_NO) String sslKeyNo) {

        // 入力チェック
        // LoadBalancerNo
        ApiValidate.validateLoadBalancerNo(loadBalancerNo);

        // ロードバランサ取得
        LoadBalancer loadBalancer = getLoadBalancer(Long.parseLong(loadBalancerNo));

        // 権限チェック
        checkAndGetUser(loadBalancer);

        // プラットフォーム取得
        Platform platform = platformDao.read(loadBalancer.getPlatformNo());
        if (platform == null) {
            throw new AutoApplicationException("EAPI-100000", "Platform", PARAM_NAME_PLATFORM_NO,
                    loadBalancer.getPlatformNo());
        }

        if (PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            // プラットフォームがCloudStackの場合は処理を行わず終了
            CreateLoadBalancerListenerResponse response = new CreateLoadBalancerListenerResponse();
            return response;
        }

        // 入力チェック
        // LoadBalancerPort
        ApiValidate.validateLoadBalancerPort(loadBalancerPort);
        // ServicePort
        ApiValidate.validateServicePort(servicePort);
        // Protocol
        ApiValidate.validateProtocol(protocol);

        // ロードバランサ リスナ取得
        LoadBalancerListener loadBalancerListener = loadBalancerListenerDao.read(Long.parseLong(loadBalancerNo),
                Integer.parseInt(loadBalancerPort));
        if (loadBalancerListener != null) {
            // 既にレコードが存在する場合
            throw new AutoApplicationException("EAPI-100010", "LoadBalancerListener", PARAM_NAME_LOAD_BALANCER_NO,
                    loadBalancerNo, PARAM_NAME_LOAD_BALANCER_PORT, loadBalancerPort);
        }

        // SslKeyNo
        Long sslKeyNo2 = null;
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())
                && ("HTTPS".equals(protocol) || "SSL".equals(protocol))) {
            //ELBかつプロトコルがHTTPSまたはSSLの場合
            ApiValidate.validateSslKeyNo(sslKeyNo);

            sslKeyNo2 = Long.parseLong(sslKeyNo);

            // AWS_SSL_KEY取得
            AwsSslKey awsSslKey = awsSslKeyDao.read(sslKeyNo2);
            if (awsSslKey == null) {
                //存在しない場合
                throw new AutoApplicationException("EAPI-100000", "AwsSslKey", PARAM_NAME_SSL_KEY_NO, sslKeyNo);
            }
        }

        // ロードバランサリスナ作成
        loadBalancerService.createListener(Long.parseLong(loadBalancerNo), Integer.parseInt(loadBalancerPort),
                Integer.parseInt(servicePort), protocol, sslKeyNo2);

        CreateLoadBalancerListenerResponse response = new CreateLoadBalancerListenerResponse();

        return response;
    }

}
