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

import jp.primecloud.auto.service.dto.AddressDto;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.ZoneDto;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public interface IaasDescribeService {

    public List<ZoneDto> getAvailabilityZones(Long userNo, Long platformNo);

    public List<KeyPairDto> getKeyPairs(Long userNo, Long platformNo);

    public List<SecurityGroupDto> getSecurityGroups(Long userNo, Long platformNo, String vpcId);

    public List<SubnetDto> getSubnets(Long userNo, Long platformNo, String vpcId);

    public List<AddressDto> getAddresses(Long userNo, Long platformNo);

    public Long createAddress(Long userNo, Long platformNo);

    public void deleteAddress(Long userNo, Long platformNo, Long addressNo);

    public String getPassword(Long instanceNo, String privateKey);

}
