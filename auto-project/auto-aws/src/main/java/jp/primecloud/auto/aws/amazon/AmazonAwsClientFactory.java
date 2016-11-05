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
package jp.primecloud.auto.aws.amazon;

import jp.primecloud.auto.aws.AwsClientFactory;
import jp.primecloud.auto.aws.wrapper.ExceptionHandleAwsClientWrapper;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AmazonAwsClientFactory extends AwsClientFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public AmazonEC2 createEc2Client(String awsAccessId, String awsSecretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessId, awsSecretKey);
        ClientConfiguration configuration = createConfiguration();

        AmazonEC2 client = new AmazonEC2Client(credentials, configuration);

        if (host != null) {
            client.setEndpoint(AmazonEC2.ENDPOINT_PREFIX + "." + host);
        }

        client = new ExceptionHandleAwsClientWrapper().wrap(client);

        return client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AmazonElasticLoadBalancing createElbClient(String awsAccessId, String awsSecretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessId, awsSecretKey);
        ClientConfiguration configuration = createConfiguration();

        AmazonElasticLoadBalancing client = new AmazonElasticLoadBalancingClient(credentials, configuration);

        if (host != null) {
            client.setEndpoint(AmazonElasticLoadBalancing.ENDPOINT_PREFIX + "." + host);
        }

        client = new ExceptionHandleAwsClientWrapper().wrap(client);

        return client;
    }

    protected ClientConfiguration createConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();

        // Proxy設定
        if (proxyHost != null && proxyPort != null) {
            configuration.setProxyHost(proxyHost);
            configuration.setProxyPort(proxyPort);

            if (proxyUser != null && proxyPassword != null) {
                configuration.setProxyUsername(proxyUser);
                configuration.setProxyPassword(proxyPassword);
            }
        }

        // リトライしない
        configuration.setMaxErrorRetry(0);

        return configuration;
    }

}
