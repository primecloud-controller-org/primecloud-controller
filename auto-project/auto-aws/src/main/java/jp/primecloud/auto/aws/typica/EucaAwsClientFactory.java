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
package jp.primecloud.auto.aws.typica;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jp.primecloud.auto.aws.AwsClientFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.xerox.amazonws.ec2.Jec2;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class EucaAwsClientFactory extends AwsClientFactory {

    private static final AmazonElasticLoadBalancing elbClient;

    static {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                throw new UnsupportedOperationException();
            }
        };
        elbClient = (AmazonElasticLoadBalancing) Proxy.newProxyInstance(EucaAwsClientFactory.class.getClassLoader(),
                new Class[] { AmazonElasticLoadBalancing.class }, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AmazonEC2 createEc2Client(String awsAccessId, String awsSecretKey) {
        boolean secure = (this.secure == null) ? false : this.secure;
        int port = (this.port == null) ? 8773 : this.port;

        Jec2 jec2 = new Jec2(awsAccessId, awsSecretKey, secure, host, port);

        jec2.setSignatureVersion(1);
        jec2.setResourcePrefix("/services/Eucalyptus");

        // Proxy設定
        if (proxyHost != null && proxyPort != null) {
            if (proxyUser != null && proxyPassword != null) {
                jec2.setProxyValues(proxyHost, proxyPort, proxyUser, proxyPassword);
            } else {
                jec2.setProxyValues(proxyHost, proxyPort);
            }
        }

        // 10秒でコネクションタイムアウトさせる
        jec2.setConnectionTimeout(10 * 1000);

        // リトライしない
        jec2.setMaxRetries(0);

        return new EucaEc2Client(jec2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AmazonElasticLoadBalancing createElbClient(String awsAccessId, String awsSecretKey) {
        return elbClient;
    }

}
