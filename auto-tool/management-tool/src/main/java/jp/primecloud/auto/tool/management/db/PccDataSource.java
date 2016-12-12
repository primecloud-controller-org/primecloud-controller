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

package jp.primecloud.auto.tool.management.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.config.Config;

public class PccDataSource extends BasicDataSource {

    protected static Log log = LogFactory.getLog(PccDataSource.class);

    public PccDataSource() {
//        setUrl(Config.getProperty("db.url"));
//        setUsername(Config.getProperty("db.username"));
//        setPassword(Config.getProperty("db.password"));
        setUrl(Config.getProperty("db.url"));
        log.info(PccDataSource.class.toString());
        log.info(Config.getProperty("db.url"));
        setUsername(Config.getProperty("db.username"));
        log.info(Config.getProperty("db.username"));
        setPassword(Config.getProperty("db.password"));
        log.info(Config.getProperty("db.password"));
    }
}
