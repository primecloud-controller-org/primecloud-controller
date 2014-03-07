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
package jp.primecloud.auto.zabbix.util;

import java.util.Locale;

import net.sf.json.processors.PropertyNameProcessor;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class JavaPropertyNameProcessor implements PropertyNameProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public String processPropertyName(Class beanClass, String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        name = name.toLowerCase(Locale.ENGLISH);
        String[] array = name.split("_");
        if (array.length == 1) {
            return array[0];
        }

        StringBuilder sb = new StringBuilder();
        if (array[0].length() == 1) {
            sb.append(Character.toUpperCase(array[0].charAt(0)));
        } else {
            sb.append(array[0]);
        }

        for (int i = 1; i < array.length; i++) {
            sb.append(Character.toUpperCase(array[i].charAt(0))).append(array[i].substring(1));
        }

        return sb.toString();
    }

}
