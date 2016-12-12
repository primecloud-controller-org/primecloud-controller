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
package jp.primecloud.auto.process.aws;

import java.util.LinkedHashMap;
import java.util.Map;

import com.amazonaws.services.ec2.model.InstanceType;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsInstanceTypeDefinition {

    private static final Map<String, Integer> instanceStoreCounts = new LinkedHashMap<String, Integer>();

    static {
        initInstanceStoreCounts();
    }

    private static void initInstanceStoreCounts() {
        // http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/InstanceStorage.html
        // 現行世代
        // M3
        addInstanceStoreCount(InstanceType.M3Medium, 1);
        addInstanceStoreCount(InstanceType.M3Large, 1);
        addInstanceStoreCount(InstanceType.M3Xlarge, 2);
        addInstanceStoreCount(InstanceType.M32xlarge, 2);

        // C3
        addInstanceStoreCount(InstanceType.C3Large, 2);
        addInstanceStoreCount(InstanceType.C3Xlarge, 2);
        addInstanceStoreCount(InstanceType.C32xlarge, 2);
        addInstanceStoreCount(InstanceType.C34xlarge, 2);
        addInstanceStoreCount(InstanceType.C38xlarge, 2);

        // G2
        addInstanceStoreCount(InstanceType.G22xlarge, 1);
        addInstanceStoreCount(InstanceType.G28xlarge, 2);

        // X1
        addInstanceStoreCount(InstanceType.X116xlarge, 1);
        addInstanceStoreCount(InstanceType.X132xlarge, 2);

        // R3
        addInstanceStoreCount(InstanceType.R3Large, 1);
        addInstanceStoreCount(InstanceType.R3Xlarge, 1);
        addInstanceStoreCount(InstanceType.R32xlarge, 1);
        addInstanceStoreCount(InstanceType.R34xlarge, 1);
        addInstanceStoreCount(InstanceType.R38xlarge, 2);

        // I2
        addInstanceStoreCount(InstanceType.I2Xlarge, 1);
        addInstanceStoreCount(InstanceType.I22xlarge, 2);
        addInstanceStoreCount(InstanceType.I24xlarge, 4);
        addInstanceStoreCount(InstanceType.I28xlarge, 8);

        // D2
        addInstanceStoreCount(InstanceType.D2Xlarge, 3);
        addInstanceStoreCount(InstanceType.D22xlarge, 6);
        addInstanceStoreCount(InstanceType.D24xlarge, 12);
        addInstanceStoreCount(InstanceType.D28xlarge, 24);

        // 旧世代
        // M1
        addInstanceStoreCount(InstanceType.M1Small, 1);
        addInstanceStoreCount(InstanceType.M1Medium, 1);
        addInstanceStoreCount(InstanceType.M1Large, 2);
        addInstanceStoreCount(InstanceType.M1Xlarge, 4);

        // M2
        addInstanceStoreCount(InstanceType.M2Xlarge, 1);
        addInstanceStoreCount(InstanceType.M22xlarge, 1);
        addInstanceStoreCount(InstanceType.M24xlarge, 2);

        // C1
        addInstanceStoreCount(InstanceType.C1Medium, 1);
        addInstanceStoreCount(InstanceType.C1Xlarge, 4);

        // CC2
        addInstanceStoreCount(InstanceType.Cc28xlarge, 4);

        // CG1
        addInstanceStoreCount(InstanceType.Cg14xlarge, 2);

        // CR1
        addInstanceStoreCount(InstanceType.Cr18xlarge, 2);

        // HI1
        addInstanceStoreCount(InstanceType.Hi14xlarge, 2);

        // HS1
        addInstanceStoreCount(InstanceType.Hs18xlarge, 24);
    }

    public static void addInstanceStoreCount(InstanceType instanceType, int count) {
        addInstanceStoreCount(instanceType.toString(), count);
    }

    public static void addInstanceStoreCount(String instanceType, int count) {
        instanceStoreCounts.put(instanceType, count);
    }

    public static int getInstanceStoreCount(String instanceType) {
        Integer count = instanceStoreCounts.get(instanceType);
        return count == null ? 0 : count.intValue();
    }

}
