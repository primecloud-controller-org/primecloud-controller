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
package jp.primecloud.auto.ui.mock.process;

import java.util.Date;
import java.util.Random;

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.process.aws.AwsAddressProcess;
import jp.primecloud.auto.process.aws.AwsProcessClient;

import org.apache.commons.lang.time.DateFormatUtils;

public class MockAwsAddressProcess extends AwsAddressProcess {

    @Override
    public AwsAddress createAddress(AwsProcessClient awsProcessClient) {
        Random random = new Random(System.currentTimeMillis());
        String publicIp = "100.64." + random.nextInt(256) + "." + random.nextInt(256);

        AwsAddress awsAddress = new AwsAddress();
        awsAddress.setUserNo(awsProcessClient.getUserNo());
        awsAddress.setPlatformNo(awsProcessClient.getPlatform().getPlatformNo());
        awsAddress.setPublicIp(publicIp);
        awsAddress.setComment("Allocate at " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        awsAddressDao.create(awsAddress);

        return awsAddress;
    }

    @Override
    public void deleteAddress(AwsProcessClient awsProcessClient, Long addressNo) {
        awsAddressDao.deleteByAddressNo(addressNo);
    }

}
