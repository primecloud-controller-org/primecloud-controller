/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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

import jp.primecloud.auto.service.impl.AwsDescribeServiceImpl;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;

public class MockAwsDescribeService extends AwsDescribeServiceImpl {

    @Override
    public List<AvailabilityZone> getAvailabilityZones(Long userNo, Long platformNo) {
        List<AvailabilityZone> zones = new ArrayList<AvailabilityZone>();

        zones.add(new AvailabilityZone().withZoneName("ap-northeast-1a"));
        zones.add(new AvailabilityZone().withZoneName("ap-northeast-1b"));
        zones.add(new AvailabilityZone().withZoneName("ap-northeast-1c"));

        return zones;
    }

    @Override
    public List<KeyPairInfo> getKeyPairs(Long userNo, Long platformNo) {
        List<KeyPairInfo> infos = new ArrayList<KeyPairInfo>();

        infos.add(new KeyPairInfo().withKeyName("key01"));
        infos.add(new KeyPairInfo().withKeyName("key02"));
        infos.add(new KeyPairInfo().withKeyName("key03"));

        return infos;
    }

    @Override
    public List<SecurityGroup> getSecurityGroups(Long userNo, Long platformNo) {
        List<SecurityGroup> groups = new ArrayList<SecurityGroup>();

        groups.add(new SecurityGroup().withGroupName("default"));
        groups.add(new SecurityGroup().withGroupName("group01"));
        groups.add(new SecurityGroup().withGroupName("group02"));

        return groups;
    }

    @Override
    public List<Subnet> getSubnets(Long userNo, Long platformNo) {
        List<Subnet> subnets = new ArrayList<Subnet>();

        {
            Subnet subnet = new Subnet();
            subnet.withSubnetId("subnet-a");
            subnet.withCidrBlock("192.168.1.0/26");
            subnet.withAvailabilityZone("ap-northeast-1a");
            subnets.add(subnet);
        }

        {
            Subnet subnet = new Subnet();
            subnet.withSubnetId("subnet-b");
            subnet.withCidrBlock("192.168.1.64/26");
            subnet.withAvailabilityZone("ap-northeast-1b");
            subnets.add(subnet);
        }

        {
            Subnet subnet = new Subnet();
            subnet.withSubnetId("subnet-c");
            subnet.withCidrBlock("192.168.1.128/26");
            subnet.withAvailabilityZone("ap-northeast-1c");
            subnets.add(subnet);
        }

        return subnets;
    }

    @Override
    public String getPassword(Long instanceNo, String privateKey) {
        return "password";
    }

}
