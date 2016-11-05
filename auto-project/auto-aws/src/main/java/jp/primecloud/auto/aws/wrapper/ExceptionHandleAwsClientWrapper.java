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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.RunInstancesRequest;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class ExceptionHandleAwsClientWrapper extends AbstractAwsClientWrapper {

    /**
     * {@inheritDoc}
     */
    protected Object doInvoke(Object target, Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();

            if (targetException instanceof AmazonServiceException) {
                throw handleException((AmazonServiceException) targetException, method, args);
            } else if (targetException instanceof AmazonClientException) {
                throw handleException((AmazonClientException) targetException, method, args);
            } else {
                throw targetException;
            }
        }
    }

    protected AutoException handleException(AmazonServiceException exception, Method method, Object[] args) {
        Object request = null;
        if (method.getParameterTypes().length > 0) {
            request = args[0];
        }

        // UserDataにはパスワードが含まれているためマスクする
        if (request instanceof RunInstancesRequest) {
            ((RunInstancesRequest) request).setUserData(null);
        }

        String str = StringUtils.reflectToString(request);

        return new AutoException("EAWS-000003", exception, method.getName(), exception.getErrorCode(),
                exception.getMessage(), str);
    }

    protected AutoException handleException(AmazonClientException exception, Method method, Object[] args) {
        Object request = null;
        if (method.getParameterTypes().length > 0) {
            request = args[0];
        }

        // UserDataにはパスワードが含まれているためマスクする
        if (request instanceof RunInstancesRequest) {
            ((RunInstancesRequest) request).setUserData(null);
        }

        String str = StringUtils.reflectToString(request);
        return new AutoException("EAWS-000001", exception, method.getName(), str);
    }

}
