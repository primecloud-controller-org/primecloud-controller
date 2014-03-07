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
package jp.primecloud.auto.config;

import java.util.Properties;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ConfigHolder {

    private static Properties properties;

    private static Properties versionProperties;

    /**
     * propertiesを設定します。
     *
     * @param properties properties
     */
    public static void setProperties(Properties properties) {
        ConfigHolder.properties = properties;
    }

    /**
     * versionPropertiesを設定します。
     *
     * @param versionProperties versionProperties
     */
    public static void setVersionProperties(Properties versionProperties) {
        ConfigHolder.versionProperties = versionProperties;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     */
    public static void destroy() {
        properties = null;
        versionProperties = null;
    }

    /**
     * propertiesを取得します。
     *
     * @return properties
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * versionPropertiesを取得します。
     *
     * @return versionProperties
     */
    public static Properties getVersionProperties() {
        return versionProperties;
    }
}
