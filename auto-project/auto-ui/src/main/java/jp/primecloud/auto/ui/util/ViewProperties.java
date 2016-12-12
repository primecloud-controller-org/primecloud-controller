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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ViewProperties {

    protected static Log log = LogFactory.getLog(ViewProperties.class);

    protected static ResourceBundle defaultBundle;

    protected static ResourceBundle userBundle;

    protected static final String USER_RESOURCE_DIR = "/opt/adc/conf/";

    static {
        // 標準のリソースバンドルの読み込み
        defaultBundle = ResourceBundle.getBundle("view");

        // 利用者によるカスタマイズ用のリソースバンドルの読み込み
        ClassLoader loader = new ClassLoader() {
            @Override
            public InputStream getResourceAsStream(String name) {
                File file = new File(USER_RESOURCE_DIR, name);
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }
        };
        try {
            userBundle = ResourceBundle.getBundle("view", Locale.getDefault(), loader);
        } catch (MissingResourceException ignore) {
        }
    }

    public static String get(String key) {
        if (userBundle != null) {
            try {
                return userBundle.getString(key);
            } catch (MissingResourceException ignore) {
            }
        }

        try {
            return defaultBundle.getString(key);
        } catch (MissingResourceException ignore) {
        }

        return "";
    }

    public static String getComponentTypeName(String key) {
        return get("componentType.name." + key);
    }

    public static String getComponentTypeLayer(String key) {
        return get("componentType.layer." + key);
    }

    public static String getPlatformName(String key) {
        return get("platform.name." + key);
    }

    public static String getPlatformSimpleName(String key) {
        return get("platform.simpleName." + key);
    }

    public static String getImageName(String key) {
        return get("image.name." + key);
    }

    public static String getImageOs(String key) {
        return get("image.os." + key);
    }

    public static String getTemplateName(String key) {
        return get("template.name." + key);
    }

    public static String getTemplateDesc(String key) {
        return get("template.description." + key);
    }

    public static String getCaption(String key) {
        return get("caption." + key);
    }

    public static String getLoadBalancerType(String key) {
        return get("loadBalancer.type." + key);
    }

}
