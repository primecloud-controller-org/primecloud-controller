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
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ViewProperties {

    private static Log log = LogFactory.getLog(ViewProperties.class);

    private static final String PATH = "/opt/adc/conf/";

    private static final String BASE_NAME = "view";

    private static final String EXTENSION = ".properties";

    private static final Locale LOCALE = Locale.getDefault();

    private static final String LANG = LOCALE.getLanguage();

    private static final String COUNTRY = LOCALE.getCountry();

    private static final String VARIANT = LOCALE.getVariant();

//    protected static ResourceBundle bundle = ResourceBundle.getBundle("view");

    protected static Properties viewProperties = getProperties();

    public static void reload() {
        viewProperties = getProperties();
    }

    public static String get(String key) {
//        try {
//            String path = PATH + BASE_NAME + "_" + LANG + "_" + COUNTRY + "_" + VARIANT + EXTENSION;
//            File file = new File(path);
//            if (!file.exists()) {
//                path = PATH + BASE_NAME + "_" + LANG + "_" + COUNTRY + EXTENSION;
//                file = new File(path);
//            }
//            if (!file.exists()) {
//                path = PATH + BASE_NAME + "_" + LANG + EXTENSION;
//                file = new File(path);
//            }
//            if (!file.exists()) {
//                path = PATH + BASE_NAME + EXTENSION;
//                file = new File(path);
//            }
//            InputStream input = new FileInputStream(file);
//
//            Properties properties = new Properties();
//            properties.load(input);
//            input.close();
            String value = "";
            if (viewProperties != null) {
                value = viewProperties.getProperty(key, "");
            }
            return value;
//        } catch (MissingResourceException e) {
//            return "";
//        } catch (FileNotFoundException e) {
//            return "";
//        } catch (IOException e) {
//            return "";
//        }
    }

    private static Properties getProperties() {
        Properties properties = null;
        try {
            String path = PATH + BASE_NAME + "_" + LANG + "_" + COUNTRY + "_" + VARIANT + EXTENSION;
            File file = new File(path);
            if (!file.exists()) {
                path = PATH + BASE_NAME + "_" + LANG + "_" + COUNTRY + EXTENSION;
                file = new File(path);
            }
            if (!file.exists()) {
                path = PATH + BASE_NAME + "_" + LANG + EXTENSION;
                file = new File(path);
            }
            if (!file.exists()) {
                path = PATH + BASE_NAME + EXTENSION;
                file = new File(path);
            }
            InputStream input = new FileInputStream(file);
            properties = new Properties();
            properties.load(input);
            input.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return properties;
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
