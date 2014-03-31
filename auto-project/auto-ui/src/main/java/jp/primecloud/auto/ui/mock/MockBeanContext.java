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
package jp.primecloud.auto.ui.mock;

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jp.primecloud.auto.ui.util.BeanContext;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockBeanContext implements ServletContextListener {

    private boolean mockMode = true;

    private ContextLoaderListener contextLoaderListener = new ContextLoaderListener();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String param = sce.getServletContext().getInitParameter("mockMode");
        mockMode = BooleanUtils.toBoolean(param);

        if (mockMode) {
            new BeanContext().setApplicationContext(createMockContext());
        } else {
            contextLoaderListener.contextInitialized(sce);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (mockMode) {
            try {
                new BeanContext().destroy();
            } catch (Exception ignore) {
            }
        } else {
            contextLoaderListener.contextDestroyed(sce);
        }
    }

    protected ApplicationContext createMockContext() {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getBean".equals(method.getName())) {
                    return getBean((String) args[0]);
                } else if ("getBeansOfType".equals(method.getName())) {
                    return getBeansOfType((Class<?>) args[0]);
                }
                throw new UnsupportedOperationException(method.getName());
            }
        };

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = new Class<?>[] { ApplicationContext.class };
        return (ApplicationContext) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    protected Object getBean(String id) {
        try {
            String className = getClass().getPackage().getName() + ".service.Mock" + StringUtils.capitalize(id);
            return Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        Map<String, T> beans = new HashMap<String, T>();
        String id = Introspector.decapitalize(clazz.getSimpleName());
        beans.put(id, clazz.cast(getBean(id)));
        return beans;
    }

}
