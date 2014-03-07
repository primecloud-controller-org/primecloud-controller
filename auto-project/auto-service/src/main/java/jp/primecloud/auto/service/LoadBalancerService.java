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
package jp.primecloud.auto.service;

import java.util.List;

import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.service.dto.SslKeyDto;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public interface LoadBalancerService {

    public List<LoadBalancerDto> getLoadBalancers(Long farmNo);

    public Long getLoadBalancerInstance(Long loadBalancerNo);

    public Long createAwsLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo,
            Long componentNo);

    public Long createCloudstackLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo, Long componentNo);

    public Long createUltraMonkeyLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo,
            Long componentNo);

    public void updateCloudstackLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment, Long componentNo,
                                        String algorithm, String pubricPort, String privatePort);

    public void updateAwsLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment,
            Long componentNo, String subnetId, String securityGroupName, String availabilityZone);

    public void updateUltraMonkeyLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment,
            Long componentNo);

    public void deleteLoadBalancer(Long loadBalancerNo);

    public void createListener(Long loadBalancerNo, Integer loadBalancerPort, Integer servicePort, String protocol, Long sslKeyNo);

    public void updateListener(Long loadBalancerNo, Integer originalLoadBalancerPort, Integer loadBalancerPort,
            Integer servicePort, String protocol, Long sslKeyNo);

    public void deleteListener(Long loadBalancerNo, Integer loadBalancerPort);

    public void configureHealthCheck(Long loadBalancerNo, String checkProtocol, Integer checkPort, String checkPath,
            Integer checkTimeout, Integer checkInterval, Integer healthyThreshold, Integer unhealthyThreshold);

    public void updateAutoScalingConf(Long farmNo, Long loadBalancerNo, Long platformNo, Long imageNo, String instanceType,
            Integer enabled, String namingRule, Long idleTimeMax, Long idleTimeMin,  Long continueLimit, Long addCount, Long delCount);

    public void enableInstances(Long loadBalancerNo, List<Long> instanceNos);

    public void disableInstances(Long loadBalancerNo, List<Long> instanceNos);

    public List<LoadBalancerPlatformDto> getPlatforms(Long userNo);

    public List<SslKeyDto> getSSLKey(Long loadBalancerNo);


}
