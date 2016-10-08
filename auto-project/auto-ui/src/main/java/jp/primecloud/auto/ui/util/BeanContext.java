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
package jp.primecloud.auto.ui.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class BeanContext implements ApplicationContextAware, DisposableBean {

    protected static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        context = null;
    }

    public static Object getBean(String id) {
        return context.getBean(id);
    }

    public static <T> T getBean(Class<T> type) {
        Map<String, T> beans = context.getBeansOfType(type);
        if (beans.size() == 0) {
            return null;
        } else if (beans.size() > 1) {
            throw new RuntimeException("Many beans of type " + type.getName() + " exist.");
        }
        return beans.values().iterator().next();
    }

}
