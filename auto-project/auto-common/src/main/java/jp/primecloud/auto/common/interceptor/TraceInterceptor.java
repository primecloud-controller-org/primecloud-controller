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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * デバッグ用のトレースログを出力するインターセプタClassです。
 * </p>
 *
 */
public class TraceInterceptor implements MethodInterceptor {

    protected Log log = LogFactory.getLog(TraceInterceptor.class);

    protected ThreadLocal<Integer> indents = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        };
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!log.isDebugEnabled()) {
            return invocation.proceed();
        }

        // 開始ログの出力
        log.debug(startLog(invocation));

        // 開始時間の記録
        long startTime = System.nanoTime();

        // インデントの設定
        indents.set(indents.get() + 1);

        try {
            return invocation.proceed();
        } finally {
            // インデントの設定
            indents.set(indents.get() - 1);

            // 終了時間の記録
            long stopTime = System.nanoTime();

            // 終了ログの出力
            log.debug(stopLog(invocation, stopTime - startTime));
        }
    }

    protected String startLog(MethodInvocation invocation) {
        StringBuilder sb = new StringBuilder();

        // インデント
        for (int i = 0; i < indents.get(); i++) {
            sb.append("\t");
        }

        sb.append(invocation.getThis().getClass().getSimpleName());
        sb.append(".");
        sb.append(invocation.getMethod().getName());
        sb.append(" start.");

        return sb.toString();
    }

    protected String stopLog(MethodInvocation invocation, long time) {
        StringBuilder sb = new StringBuilder();

        // インデント
        for (int i = 0; i < indents.get(); i++) {
            sb.append("\t");
        }

        sb.append(invocation.getThis().getClass().getSimpleName());
        sb.append(".");
        sb.append(invocation.getMethod().getName());
        sb.append(" stop.\t");
        sb.append(time / 1000000.0).append("[ms]");

        return sb.toString();
    }

}
