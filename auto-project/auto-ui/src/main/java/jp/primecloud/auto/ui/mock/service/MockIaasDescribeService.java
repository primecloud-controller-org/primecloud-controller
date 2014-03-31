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

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.dto.AddressDto;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.ZoneDto;
import jp.primecloud.auto.ui.mock.XmlDataLoader;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockIaasDescribeService implements IaasDescribeService {

    List<AwsAddress> awsAddresses = XmlDataLoader.getData("awsAddress.xml", AwsAddress.class);

    @Override
    public List<ZoneDto> getAvailabilityZones(Long userNo, Long platformNo) {
        List<ZoneDto> zones = new ArrayList<ZoneDto>();

        zones.add(new ZoneDto().withZoneName("ap-northeast-1a"));
        zones.add(new ZoneDto().withZoneName("ap-northeast-1b"));

        return zones;
    }

    @Override
    public List<KeyPairDto> getKeyPairs(Long userNo, Long platformNo) {
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
        groups.add(new SecurityGroupDto().withGroupName("test01"));
        groups.add(new SecurityGroupDto().withGroupName("test02"));

        return groups;
    }

    @Override
    public List<SubnetDto> getSubnets(Long userNo, Long platformNo, String vpcId) {
        List<SubnetDto> subnets = new ArrayList<SubnetDto>();

        subnets.add(new SubnetDto().withSubnetId("subnet-00000001").withZone("ap-northeast-1a").withCidrBlock("192.168.12.160/27"));
        subnets.add(new SubnetDto().withSubnetId("subnet-00000002").withZone("ap-northeast-1b").withCidrBlock("192.168.12.128/27"));
        subnets.add(new SubnetDto().withSubnetId("subnet-00000003").withZone("ap-northeast-1b").withCidrBlock("192.168.12.16/27"));

        return subnets;
    }

    @Override
    public List<AddressDto> getAddresses(Long userNo, Long platformNo) {
        ArrayList<AddressDto> addresses = new ArrayList<AddressDto>();
        for (AwsAddress address: this.awsAddresses){
            addresses.add(new AddressDto(address));
        }
        return addresses;
    }

    @Override
    public Long createAddress(Long userNo, Long platformNo) {
        System.out.println("createAddress: userNo=" + userNo + " platformNo" + platformNo);
        return awsAddresses.get(0).getAddressNo();
    }

    @Override
    public void deleteAddress(Long addressNo, Long long2, Long long3) {
        System.out.println("deleteAddress: addressNo=" + addressNo);

    }

    @Override
    public String getPassword(Long instanceNo, String privateKey) {
        System.out.println("getPassword: instanceNo=" + instanceNo + " privateKey" + privateKey);
        return "password";
    }

}
