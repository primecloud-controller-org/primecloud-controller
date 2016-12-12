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
package jp.primecloud.auto.common.interceptor;

import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.util.MessageUtils;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * 例外をハンドリングするインターセプタClassです。
 * </p>
 *
 */
public class ExceptionHandleInterceptor implements MethodInterceptor {

    protected Log log = LogFactory.getLog(ExceptionHandleInterceptor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            throw handle(e);
        }
    }

    protected Throwable handle(Throwable ex) {
        if (ex instanceof AutoApplicationException) {
            // 何もしない

        } else if (ex instanceof AutoException) {
            // エラーログを出力
            AutoException e = (AutoException) ex;
            log.error(e.getMessage(), e);

        } else if (ex instanceof MultiCauseException) {
            // 各原因を再帰的に処理
            for (Throwable cause : ((MultiCauseException) ex).getCauses()) {
                handle(cause);
            }

        } else {
            // エラーログを出力
            String message = "[ECOMMON-000000] " + MessageUtils.getMessage("ECOMMON-000000");
            log.error(message, ex);
        }

        return ex;
    }

}
