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
package jp.primecloud.auto.service;

import java.util.List;

import jp.primecloud.auto.entity.crud.AwsAddress;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public interface AwsDescribeService {

    public List<AvailabilityZone> getAvailabilityZones(Long userNo, Long platformNo);

    public List<KeyPairInfo> getKeyPairs(Long userNo, Long platformNo);

    public List<SecurityGroup> getSecurityGroups(Long userNo, Long platformNo);

    public List<Subnet> getSubnets(Long userNo, Long platformNo);

    public List<AwsAddress> getAddresses(Long userNo, Long platformNo);

    public String getPassword(Long instanceNo, String privateKey);

}
