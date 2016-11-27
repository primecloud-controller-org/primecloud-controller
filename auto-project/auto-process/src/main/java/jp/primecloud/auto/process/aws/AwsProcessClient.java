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

import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsProcessClient {

    protected Log log = LogFactory.getLog(getClass());

    protected Long userNo;

    protected Platform platform;

    protected PlatformAws platformAws;

    protected AmazonEC2 ec2Client;

    protected AmazonElasticLoadBalancing elbClient;

    protected int describeInterval = 15;

    /**
     * TODO: コンストラクタコメント
     * 
     * @param userNo
     * @param platform
     * @param platformAws
     * @param ec2Client
     * @param elbClient
     */
    public AwsProcessClient(Long userNo, Platform platform, PlatformAws platformAws, AmazonEC2 ec2Client,
            AmazonElasticLoadBalancing elbClient) {
        this.userNo = userNo;
        this.platform = platform;
        this.platformAws = platformAws;
        this.ec2Client = ec2Client;
        this.elbClient = elbClient;
    }

    public Long getUserNo() {
        return userNo;
    }

    public Platform getPlatform() {
        return platform;
    }

    public PlatformAws getPlatformAws() {
        return platformAws;
    }

    public AmazonEC2 getEc2Client() {
        return ec2Client;
    }

    public AmazonElasticLoadBalancing getElbClient() {
        return elbClient;
    }

    public int getDescribeInterval() {
        return describeInterval;
    }

    public void setDescribeInterval(int describeInterval) {
        this.describeInterval = describeInterval;
    }

}
