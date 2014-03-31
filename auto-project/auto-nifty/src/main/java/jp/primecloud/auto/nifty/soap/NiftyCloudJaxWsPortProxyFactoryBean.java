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
package jp.primecloud.auto.nifty.soap;

import jp.primecloud.auto.exception.AutoException;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;
import org.springframework.remoting.jaxws.JaxWsSoapFaultException;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyCloudJaxWsPortProxyFactoryBean extends JaxWsPortProxyFactoryBean {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        try {
            return super.doInvoke(invocation);
        } catch (Exception e) {
            // APIメソッド名
            String method = StringUtils.capitalize(invocation.getMethod().getName());

            // リクエスト情報
            String request = null;
            if (invocation.getArguments() != null || invocation.getArguments().length != 0) {
                request = jp.primecloud.auto.util.StringUtils.reflectToString(invocation.getArguments()[0]);
            }

            AutoException autoException;

            if (e instanceof JaxWsSoapFaultException) {
                JaxWsSoapFaultException ex = JaxWsSoapFaultException.class.cast(e);
                autoException = new AutoException("ENIFTY-000002", e, method, ex.getFaultCode(), ex.getFaultString(),
                        request);
            } else {
                autoException = new AutoException("ENIFTY-000001", e, method, request);
            }

            throw autoException;
        }
    }

}
