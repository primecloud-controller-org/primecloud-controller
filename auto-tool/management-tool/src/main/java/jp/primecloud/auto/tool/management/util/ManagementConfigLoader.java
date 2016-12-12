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
package jp.primecloud.auto.tool.management.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.config.ConfigHolder;
import jp.primecloud.auto.util.MessageUtils;

public class ManagementConfigLoader {

    protected static Log log = LogFactory.getLog(ManagementConfigLoader.class);

    public static String propertyPath = "management-config.properties";

    public static String permanentPropPath = "permanent-config.properties";

    public static void init() {
        try {
            loadProperties(propertyPath);
            loadProperties(permanentPropPath);
//            loadConfig();
            loadProp();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    protected static void loadProperties(String propertyPath) throws Exception {
        InputStream input = ManagementConfigLoader.class.getClassLoader().getResourceAsStream(propertyPath);
        if (input == null) {
            throw new RuntimeException(MessageUtils.format("ConfigFile '{0}' is not found.", propertyPath));
        }
        Properties properties = ConfigHolder.getProperties();
        if (properties == null) {
            properties = new Properties();
        }
        try {
            properties.load(input);
            ConfigHolder.setProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    protected static void loadConfig() throws Exception {
//        String configPath = Config.getProperty("AUTOCONFIG_XML_PATH");
//        if (configPath == null) {
//            throw new RuntimeException(MessageUtils.format("Property '{0}' is not found.", "AUTOCONFIG_XML_PATH"));
//        }
//
//        File file = new File(configPath);
//        if (!file.exists()) {
//            throw new RuntimeException(MessageUtils.format("ConfigFile '{0}' is not found.", configPath));
//        }
//
//        // 設定ファイルの読み込み
//        AutoConfig autoConfig;
//        try {
//            JAXBContext context = JAXBContext.newInstance(AutoConfig.class);
//            autoConfig = (AutoConfig) context.createUnmarshaller().unmarshal(file);
//        } catch (JAXBException e) {
//            throw new RuntimeException(e);
//        }
//
//        // 設定情報の格納
//        ConfigHolder.initialize(autoConfig);
//    }

    protected static void loadProp() throws Exception {
        String configPath = Config.getProperty("AUTOCONFIG_PROPERTY_PATH");
        if (configPath == null) {
            throw new RuntimeException(MessageUtils.format("Property '{0}' is not found.", "AUTOCONFIG_PROPERTY_PATH"));
        }

        File file = new File(configPath);
        if (!file.exists()) {
            throw new RuntimeException(MessageUtils.format("ConfigFile '{0}' is not found.", configPath));
        }

        Properties properties = ConfigHolder.getProperties();
        try {
            properties.load(new FileInputStream(file));
            ConfigHolder.setProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
