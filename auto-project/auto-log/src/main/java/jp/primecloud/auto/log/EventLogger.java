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
package jp.primecloud.auto.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.log.dao.crud.EventLogDao;
import jp.primecloud.auto.log.entity.crud.EventLog;
import jp.primecloud.auto.util.CompositeResourceBundle;
import jp.primecloud.auto.util.MessageUtils;

/**
 * <p>
 * イベントログをデータベースに出力する為のクラスです。<br />
 * ログレベルごとにデータベースに書きこみます。
 * </p>
 *
 */
public class EventLogger {

    protected EventLogDao eventLogDao;

    protected String[] properties = new String[] { "eventLog" };

    protected ResourceBundle bundle;

    public void initialize() {
        List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        for (String baseName : properties) {
            bundles.add(ResourceBundle.getBundle(baseName));
        }
        this.bundle = new CompositeResourceBundle(bundles);
    }

    public void error(String code, Object[] additions) {
        Long componentNo = LoggingUtils.getComponentNo();
        String componentName = LoggingUtils.getComponentName();
        Long instanceNo = LoggingUtils.getInstanceNo();
        String instanceName = LoggingUtils.getInstanceName();
        String instanceType = LoggingUtils.getInstanceType();
        Long platformNo = LoggingUtils.getPlatformNo();

        log(EventLogLevel.ERROR, componentNo, componentName, instanceNo, instanceName, code, instanceType, platformNo,
                additions);
    }

    public void log(EventLogLevel logLevel, Long componentNo, String componentName, Long instanceNo,
            String instanceName, String code, String instanceType, Long platformNo, Object[] additions) {
        Long farmNo = LoggingUtils.getFarmNo();
        String farmName = LoggingUtils.getFarmName();

        log(logLevel, farmNo, farmName, componentNo, componentName, instanceNo, instanceName, code, instanceType,
                platformNo, additions);
    }

    public void log(EventLogLevel logLevel, Long farmNo, String farmName, Long componentNo, String componentName,
            Long instanceNo, String instanceName, String code, String instanceType, Long platformNo, Object[] additions) {
        Long userNo = LoggingUtils.getUserNo();
        String userName = LoggingUtils.getUserName();

        log(logLevel, userNo, userName, farmNo, farmName, componentNo, componentName, instanceNo, instanceName, code,
                instanceType, platformNo, additions);
    }

    /**
     * データベースにイベントログを書き込みます。
     *
     * @param logLevel
     * @param userNo
     * @param userName
     * @param farmNo
     * @param farmName
     * @param componentNo
     * @param componentName
     * @param instanceNo
     * @param instanceName
     * @param code
     * @param instanceType
     * @param platformNo
     * @param additions
     */
    public void log(EventLogLevel logLevel, Long userNo, String userName, Long farmNo, String farmName,
            Long componentNo, String componentName, Long instanceNo, String instanceName, String code,
            String instanceType, Long platformNo, Object[] additions) {
        // イベントログメッセージの取得
        String pattern = getPattern(code);
        String message = MessageUtils.format(pattern, additions);

        // イベントログの出力
        EventLog eventLog = new EventLog();
        eventLog.setLogDate(new Date());
        eventLog.setLogLevel(logLevel.getCode());
        eventLog.setUserNo(userNo);
        eventLog.setUserName(userName);
        eventLog.setFarmNo(farmNo);
        eventLog.setFarmName(farmName);
        eventLog.setComponentNo(componentNo);
        eventLog.setComponentName(componentName);
        eventLog.setInstanceNo(instanceNo);
        eventLog.setInstanceName(instanceName);
        eventLog.setMessageCode(code);
        eventLog.setMessage(message);
        eventLog.setInstanceType(instanceType);
        eventLog.setPlatformNo(platformNo);
        eventLogDao.create(eventLog);
    }

    protected String getPattern(String code) {
        if (bundle == null) {
            initialize();
        }

        try {
            return bundle.getString(code);
        } catch (MissingResourceException e) {
            return code;
        }
    }

    /**
     * eventLogDaoを設定します。
     *
     * @param eventLogDao eventLogDao
     */
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }

    /**
     * propertiesを設定します。
     *
     * @param properties properties
     */
    public void setProperties(String[] properties) {
        this.properties = properties;
    }

}
