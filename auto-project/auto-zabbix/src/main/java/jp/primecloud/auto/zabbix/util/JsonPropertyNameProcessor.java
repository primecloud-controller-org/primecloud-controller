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

import jp.primecloud.auto.zabbix.model.host.HostGetParam;
import jp.primecloud.auto.zabbix.model.user.UserGetParam;
import net.sf.json.processors.PropertyNameProcessor;

/**
 * <p>
 * Javaのプロパティ名をJSONのキーにマッピングするクラスです。
 * </p>
 *
 */
public class JsonPropertyNameProcessor implements PropertyNameProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public String processPropertyName(Class beanClass, String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        if (beanClass != null) {
            if (HostGetParam.class.isAssignableFrom(beanClass)) {
                if (name.equals("selectParentTemplates")) {
                    return name;
                } else if (name.equals("selectGroups")) {
                    return name;
                }
            }
            if (UserGetParam.class.isAssignableFrom(beanClass)) {
                if (name.equals("selectUsrgrps")) {
                    return name;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (int i = 1; i < name.length(); i++) {
            if (Character.isUpperCase(name.charAt(i))) {
                if (sb.length() != 0) {
                    sb.append('_');
                }
                sb.append(name.substring(pos, i));
                pos = i;
            }
        }
        if (sb.length() != 0) {
            sb.append('_');
        }
        sb.append(name.substring(pos, name.length()));

        return sb.toString().toLowerCase(Locale.ENGLISH);
    }

}
