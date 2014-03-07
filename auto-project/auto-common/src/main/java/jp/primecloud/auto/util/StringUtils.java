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
package jp.primecloud.auto.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * <p>
 * 文字列操作に関するユーティリティClassです。
 * </p>
 *
 */
public class StringUtils {

    private StringUtils() {
    }

    public static String reflectToString(Object object) {
        if (object == null) {
            return null;
        }

        // Stringの場合
        if (object instanceof String) {
            return (String) object;
        }

        // 数値型の場合
        if (object instanceof Number) {
            return object.toString();
        }

        // Boolean型の場合
        if (object instanceof Boolean) {
            return object.toString();
        }

        // Character型の場合
        if (object instanceof Character) {
            return object.toString();
        }

        // 配列の場合
        if (object instanceof Object[]) {
            return reflectToString(Arrays.asList((Object[]) object));
        }

        // コレクションの場合
        if (object instanceof Collection<?>) {
            Iterator<?> iterator = ((Collection<?>) object).iterator();
            if (!iterator.hasNext()) {
                return "[]";
            }

            StringBuilder str = new StringBuilder();
            str.append("[");
            while (true) {
                Object object2 = iterator.next();
                str.append(reflectToString(object2));
                if (!iterator.hasNext()) {
                    break;
                }
                str.append(", ");
            }
            str.append("]");
            return str.toString();
        }

        // マップの場合
        if (object instanceof Map<?, ?>) {
            Iterator<?> iterator = ((Map<?, ?>) object).entrySet().iterator();
            if (!iterator.hasNext()) {
                return "{}";
            }

            StringBuilder str = new StringBuilder();
            str.append("{");
            while (true) {
                Object entry = iterator.next();
                str.append(reflectToString(entry));
                if (!iterator.hasNext()) {
                    break;
                }
                str.append(", ");
            }
            str.append("}");
            return str.toString();
        }

        // Entry型の場合
        if (object instanceof Entry<?, ?>) {
            Entry<?, ?> entry = (Entry<?, ?>) object;
            StringBuilder str = new StringBuilder();
            str.append(reflectToString(entry.getKey()));
            str.append("=");
            str.append(reflectToString(entry.getValue()));
            return str.toString();
        }

        // toStringメソッドが実装されている場合
        try {
            Method method = object.getClass().getMethod("toString");
            if (!Object.class.equals(method.getDeclaringClass())) {
                return object.toString();
            }
        } catch (NoSuchMethodException ignore) {
        }

        // プロパティごとに文字列に変換
        try {
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
            StringBuilder str = new StringBuilder();
            str.append("[");
            for (PropertyDescriptor descriptor : descriptors) {
                if ("class".equals(descriptor.getName())) {
                    continue;
                }

                Method readMethod = descriptor.getReadMethod();
                if (readMethod == null) {
                    continue;
                }

                if (str.length() > 1) {
                    str.append(", ");
                }
                Object object2 = readMethod.invoke(object);
                str.append(descriptor.getName()).append("=").append(reflectToString(object2));
            }
            str.append("]");
            return str.toString();
        } catch (IntrospectionException ignore) {
        } catch (InvocationTargetException ignore) {
        } catch (IllegalAccessException ignore) {
        }

        // どうしようもない場合、commons-langを用いる
        return ReflectionToStringBuilder.toString(object);
    }

}
