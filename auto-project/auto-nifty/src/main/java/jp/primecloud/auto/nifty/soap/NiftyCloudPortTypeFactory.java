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

import java.util.Arrays;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import jp.primecloud.auto.nifty.soap.jaxws.NiftyCloudPortType;
import jp.primecloud.auto.nifty.soap.security.SignatureHandler;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyCloudPortTypeFactory {

    protected String wsdlDocumentUrl;

    protected Long timeout;

    /**
     * TODO: メソッドコメントを記述
     *
     * @param certificate
     * @param privateKey
     * @return
     */
    public NiftyCloudPortType createNiftyCloudPortType(String certificate, String privateKey) {
        HandlerResolver handlerResolver = createHandlerResolver(certificate, privateKey);
        NiftyCloudPortType niftyCloudPortType = createNiftyCloudPortType(handlerResolver);

        // タイムアウト設定
        prepareTimeout(niftyCloudPortType);

        return niftyCloudPortType;
    }

    @SuppressWarnings("unchecked")
    protected HandlerResolver createHandlerResolver(String certificate, String privateKey) {
        final SignatureHandler handler = new SignatureHandler(certificate, privateKey);

        HandlerResolver handlerResolver = new HandlerResolver() {
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                return Arrays.asList((Handler) handler);
            }
        };

        return handlerResolver;
    }

    protected NiftyCloudPortType createNiftyCloudPortType(HandlerResolver handlerResolver) {
        NiftyCloudJaxWsPortProxyFactoryBean factoryBean = new NiftyCloudJaxWsPortProxyFactoryBean();
        factoryBean.setServiceInterface(NiftyCloudPortType.class);
        try {
            Resource resource = new DefaultResourceLoader().getResource(wsdlDocumentUrl);
            factoryBean.setWsdlDocumentUrl(resource.getURL());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        factoryBean.setNamespaceUri("https://cp.cloud.nifty.com/api/1.3/");
        factoryBean.setServiceName("NiftyCloud");
        factoryBean.setPortName("NiftyCloudPort");
        factoryBean.setLookupServiceOnStartup(false);
        factoryBean.setHandlerResolver(handlerResolver);
        factoryBean.afterPropertiesSet();
        return (NiftyCloudPortType) factoryBean.getObject();
    }

    protected void prepareTimeout(NiftyCloudPortType niftyCloudPortType) {
        if (timeout != null && timeout.longValue() > 0) {
            int timeout = this.timeout.longValue() > Integer.MAX_VALUE ? Integer.MAX_VALUE : this.timeout.intValue();
            BindingProvider bindingProvider = (BindingProvider) niftyCloudPortType;
            bindingProvider.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeout);
        }
    }

    /**
     * wsdlDocumentUrlを設定します。
     *
     * @param wsdlDocumentUrl wsdlDocumentUrl
     */
    public void setWsdlDocumentUrl(String wsdlDocumentUrl) {
        this.wsdlDocumentUrl = wsdlDocumentUrl;
    }

    /**
     * timeoutを設定します。
     *
     * @param timeout timeout
     */
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
