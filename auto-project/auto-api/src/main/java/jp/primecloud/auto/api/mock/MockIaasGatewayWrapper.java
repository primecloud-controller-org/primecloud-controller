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
package jp.primecloud.auto.api.mock;

import java.util.Arrays;
import java.util.List;

import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.log.EventLogger;

import org.apache.commons.lang.StringUtils;

public class MockIaasGatewayWrapper extends IaasGatewayWrapper {

    public MockIaasGatewayWrapper(Long userNo, Long platformNo, EventLogger eventLogger) {
        super(userNo, platformNo, eventLogger);
    }

    public String excGateway(String gwMod, List<String> gwParams) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String describeKeyPairs() {
        return StringUtils.join(Arrays.asList("key1", "key2"), "##");
    }

    @Override
    public String describeSecurityGroups(String vpcId) {
        return StringUtils.join(Arrays.asList("sg-1111", "sg-2222"), "##");
    }

    @Override
    public String describeAvailabilityZones() {
        return StringUtils.join(Arrays.asList("ap-northeast-1a", "ap-northeast-1b"), "##");
    }

    @Override
    public String describeSubnets(String vpcId) {
        String subnet1 = StringUtils.join(Arrays.asList("subnet-1111", "ap-northeast-1a", "172.31.0.0/20"), "#");
        String subnet2 = StringUtils.join(Arrays.asList("subnet-2222", "ap-northeast-1b", "172.31.16.0/20"), "#");
        return StringUtils.join(Arrays.asList(subnet1, subnet2), "##");
    }

    @Override
    public String describeNetworks() {
        return super.describeNetworks();
    }

    @Override
    public String describeAzureSubnets(String networkName) {
        return super.describeAzureSubnets(networkName);
    }

    @Override
    public String describeFlavors(String flavorIds) {
        return super.describeFlavors(flavorIds);
    }

    @Override
    public void createMyCloud(String farmName) {
        super.createMyCloud(farmName);
    }

    @Override
    public void deleteMyCloud(Long farmNo) {
        super.deleteMyCloud(farmNo);
    }

    @Override
    public void startInstance(Long instanceNo) {
        super.startInstance(instanceNo);
    }

    @Override
    public void stopInstance(Long instanceNo) {
        super.stopInstance(instanceNo);
    }

    @Override
    public void terminateInstance(String instanceId) {
        super.terminateInstance(instanceId);
    }

    @Override
    public void stopVolume(Long instanceNo, Long volumeNo) {
        super.stopVolume(instanceNo, volumeNo);
    }

    @Override
    public void startVolume(Long instanceNo, Long volumeNo) {
        super.startVolume(instanceNo, volumeNo);
    }

    @Override
    public void deleteVolume(String volumeId) {
        super.deleteVolume(volumeId);
    }

    @Override
    public String allocateAddress() {
        return super.allocateAddress();
    }

    @Override
    public void releaseAddress(String publicIp) {
        super.releaseAddress(publicIp);
    }

    @Override
    public String createKeyPair(String keyName) {
        return super.createKeyPair(keyName);
    }

    @Override
    public void deleteKeyPair(String keyName) {
        super.deleteKeyPair(keyName);
    }

    @Override
    public void importKeyPair(String keyName, String publicKeyMaterial) {
        super.importKeyPair(keyName, publicKeyMaterial);
    }

    @Override
    public String createSnapshot(String volumeId) {
        return super.createSnapshot(volumeId);
    }

    @Override
    public void deleteSnapshot(String snapshotId) {
        super.deleteSnapshot(snapshotId);
    }

    @Override
    public void startLoadBalancer(Long loadBalancerNo) {
        super.startLoadBalancer(loadBalancerNo);
    }

    @Override
    public void stopLoadBalancer(Long loadBalancerNo) {
        super.stopLoadBalancer(loadBalancerNo);
    }

    @Override
    public void configureLoadBalancer(Long loadBalancerNo) {
        super.configureLoadBalancer(loadBalancerNo);
    }

    @Override
    public String getPasswordData(String instanceNo) {
        return super.getPasswordData(instanceNo);
    }

    @Override
    public boolean synchronizeCloud(Long farmNo) {
        return super.synchronizeCloud(farmNo);
    }

}
