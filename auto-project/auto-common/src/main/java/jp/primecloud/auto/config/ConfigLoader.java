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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ConfigLoader {

    private Log log = LogFactory.getLog(ConfigLoader.class);

    private String propPath = "/opt/adc/conf/config.properties";

    private String versionPath = "version.properties";

    /**
     * TODO: メソッドコメントを記述
     *
     * @param servletContext
     */
    public void initialize(ServletContext servletContext) {
        destroy(servletContext);

        loadProp();
        loadVersionProp();
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param servletContext
     */
    public void destroy(ServletContext servletContext) {
        ConfigHolder.destroy();
    }

    protected void loadProp() {
        try {
            File file = new File(propPath);
            InputStream input = new FileInputStream(file);

            Properties properties = new Properties();
            properties.load(input);
            input.close();

            ConfigHolder.setProperties(properties);

        } catch (IOException e){
            log.warn(MessageUtils.getMessage("ECOMMON-000101", propPath));
            throw new AutoException("ECOMMON-000101", e, propPath);
        }
    }

    protected void loadVersionProp() {
        InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(versionPath);
        if (input == null) {
            log.warn(MessageUtils.getMessage("ECOMMON-000101", versionPath));
        } else {
            Properties properties = new Properties();
            try {
                properties.load(input);
                ConfigHolder.setVersionProperties(properties);
            } catch (IOException ignore) {
            }
        }

    }
}
