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
import java.util.Collections;

import jp.primecloud.auto.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class LoggingAwsClientWrapper extends AbstractAwsClientWrapper {

    protected Log log = LogFactory.getLog(getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Object target, Object proxy, Method method, Object[] args) throws Throwable {
        if (log.isDebugEnabled()) {
            Object request;
            if (method.getParameterTypes().length > 0) {
                request = args[0];
            } else {
                request = Collections.EMPTY_MAP;
            }

            log.debug(method.getName() + " request: " + StringUtils.reflectToString(request));
        }

        Object response;
        try {
            response = method.invoke(target, args);
        } catch (InvocationTargetException e) {
            if (log.isDebugEnabled()) {
                log.debug(method.getName() + " error: " + e.getTargetException().getMessage());
            }

            throw e.getTargetException();
        }

        if (log.isDebugEnabled()) {
            if (method.getReturnType() != void.class) {
                log.debug(method.getName() + " response: " + StringUtils.reflectToString(response));
            }
        }

        return response;
    }

}
