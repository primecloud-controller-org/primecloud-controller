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
package jp.primecloud.auto.ui.mock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.CloudstackAddress;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.NetworkDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.ZoneDto;
import jp.primecloud.auto.service.impl.IaasDescribeServiceImpl;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockIaasDescribeService extends IaasDescribeServiceImpl implements IaasDescribeService {

    @Override
    public List<ZoneDto> getAvailabilityZones(Long userNo, Long platformNo) {
        List<ZoneDto> zones = new ArrayList<ZoneDto>();

        zones.add(new ZoneDto().withZoneName("ap-northeast-1a"));
        zones.add(new ZoneDto().withZoneName("ap-northeast-1b"));
        zones.add(new ZoneDto().withZoneName("ap-northeast-1c"));

        return zones;
    }

    @Override
    public List<KeyPairDto> getKeyPairs(Long userNo, Long platformNo) {
        Platform platform = platformDao.read(platformNo);
        if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            return super.getKeyPairs(userNo, platformNo);
        }

        List<KeyPairDto> infos = new ArrayList<KeyPairDto>();

        infos.add(new KeyPairDto().withKeyName("key01"));
        infos.add(new KeyPairDto().withKeyName("key02"));
        infos.add(new KeyPairDto().withKeyName("key03"));

        return infos;
    }

    @Override
    public List<SecurityGroupDto> getSecurityGroups(Long userNo, Long platformNo, String vpcId) {
        List<SecurityGroupDto> groups = new ArrayList<SecurityGroupDto>();

        groups.add(new SecurityGroupDto().withGroupName("default"));
        groups.add(new SecurityGroupDto().withGroupName("group01"));
        groups.add(new SecurityGroupDto().withGroupName("group02"));

        return groups;
    }

    @Override
    public List<SubnetDto> getSubnets(Long userNo, Long platformNo, String vpcId) {
        List<SubnetDto> subnets = new ArrayList<SubnetDto>();

        {
            SubnetDto subnet = new SubnetDto();
            subnet.withSubnetId("subnet-a");
            subnet.withCidrBlock("192.168.1.0/26");
            subnet.withZone("ap-northeast-1a");
            subnets.add(subnet);
        }

        {
            SubnetDto subnet = new SubnetDto();
            subnet.withSubnetId("subnet-b");
            subnet.withCidrBlock("192.168.1.64/26");
            subnet.withZone("ap-northeast-1b");
            subnets.add(subnet);
        }

        {
            SubnetDto subnet = new SubnetDto();
            subnet.withSubnetId("subnet-c");
            subnet.withCidrBlock("192.168.1.128/26");
            subnet.withZone("ap-northeast-1c");
            subnets.add(subnet);
        }

        return subnets;
    }

    @Override
    public List<NetworkDto> getNetworks(Long userNo, Long platformNo) {
        List<NetworkDto> networks = new ArrayList<NetworkDto>();

        {
            NetworkDto network = new NetworkDto();
            network.setNetworkName("network1");
            network.setNetmask("255.255.255.0");
            network.setGateWay("192.168.1.1");
            network.setDns1("192.168.1.2");
            network.setDns2("192.168.1.3");
            network.setRangeFrom("192.168.1.11");
            network.setRangeTo("192.168.1.200");
            network.setIsPcc(true);
            networks.add(network);
        }

        {
            NetworkDto network = new NetworkDto();
            network.setNetworkName("network2");
            network.setNetmask("255.255.255.0");
            network.setGateWay("192.168.2.1");
            network.setDns1("192.168.2.2");
            network.setDns2("192.168.2.3");
            network.setRangeFrom("192.168.2.11");
            network.setRangeTo("192.168.2.200");
            network.setIsPcc(false);
            networks.add(network);
        }

        {
            NetworkDto network = new NetworkDto();
            network.setNetworkName("network3");
            network.setNetmask("255.255.255.0");
            network.setGateWay("192.168.3.1");
            network.setDns1("192.168.3.2");
            network.setDns2("192.168.3.3");
            network.setRangeFrom("192.168.3.11");
            network.setRangeTo("192.168.3.200");
            network.setIsPcc(false);
            networks.add(network);
        }

        return networks;
    }

    @Override
    public List<SubnetDto> getAzureSubnets(Long userNo, Long platformNo, String networkName) {
        List<SubnetDto> subnets = new ArrayList<SubnetDto>();

        {
            SubnetDto subnet = new SubnetDto();
            subnet.withSubnetId("subnet-a");
            subnet.withCidrBlock("192.168.1.0/26");
            subnets.add(subnet);
        }

        {
            SubnetDto subnet = new SubnetDto();
            subnet.withSubnetId("subnet-b");
            subnet.withCidrBlock("192.168.1.64/26");
            subnets.add(subnet);
        }

        {
            SubnetDto subnet = new SubnetDto();
            subnet.withSubnetId("subnet-c");
            subnet.withCidrBlock("192.168.1.128/26");
            subnets.add(subnet);
        }

        return subnets;
    }

    @Override
    public Long createAddress(Long userNo, Long platformNo) {
        Random random = new Random(System.currentTimeMillis());
        String ipaddress = "100.64." + random.nextInt(256) + "." + random.nextInt(256);

        CloudstackAddress address = new CloudstackAddress();
        address.setAccount(userNo);
        address.setPlatformNo(platformNo);
        address.setIpaddress(ipaddress);
        cloudstackAddressDao.create(address);

        return address.getAddressNo();
    }

    @Override
    public void deleteAddress(Long userNo, Long platformNo, Long addressNo) {
        cloudstackAddressDao.deleteByAddressNo(addressNo);
    }

}
