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
package jp.primecloud.auto.aws.wrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;

public abstract class AbstractAwsClientWrapper {

    /**
     * TODO: メソッドコメント
     * 
     * @param client
     * @return
     */
    public AmazonEC2 wrap(final AmazonEC2 client) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (!AmazonWebServiceResult.class.isAssignableFrom(method.getReturnType())) {
                    return method.invoke(client, args);
                }

                return doInvoke(client, proxy, method, args);
            }
        };

        return (AmazonEC2) Proxy.newProxyInstance(LoggingAwsClientWrapper.class.getClassLoader(),
                new Class[] { AmazonEC2.class }, handler);
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param client
     * @return
     */
    public AmazonElasticLoadBalancing wrap(final AmazonElasticLoadBalancing client) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (!AmazonWebServiceResult.class.isAssignableFrom(method.getReturnType())) {
                    return method.invoke(client, args);
                }

                return doInvoke(client, proxy, method, args);
            }
        };

        return (AmazonElasticLoadBalancing) Proxy.newProxyInstance(LoggingAwsClientWrapper.class.getClassLoader(),
                new Class[] { AmazonElasticLoadBalancing.class }, handler);
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param target
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    protected abstract Object doInvoke(Object target, Object proxy, Method method, Object[] args) throws Throwable;

}
