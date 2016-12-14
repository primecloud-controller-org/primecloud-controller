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
package jp.primecloud.auto.common.log;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class LoggingUtils {

    protected static ThreadLocal<Long> userNos = new ThreadLocal<Long>();

    protected static ThreadLocal<String> userNames = new ThreadLocal<String>();

    protected static ThreadLocal<Long> farmNos = new ThreadLocal<Long>();

    protected static ThreadLocal<String> farmNames = new ThreadLocal<String>();

    protected static ThreadLocal<Long> componentNos = new ThreadLocal<Long>();

    protected static ThreadLocal<String> componentNames = new ThreadLocal<String>();

    protected static ThreadLocal<Long> instanceNos = new ThreadLocal<Long>();

    protected static ThreadLocal<String> instanceNames = new ThreadLocal<String>();

    protected static ThreadLocal<String> instanceTypes = new ThreadLocal<String>();

    protected static ThreadLocal<Long> platformNos = new ThreadLocal<Long>();

    private LoggingUtils() {
    }

    /**
     * ログヘッダの文字列を構築します。
     *
     * @return ログヘッダの文字列
     */
    public static String createLogHeader() {
        StringBuilder header = new StringBuilder();

        // ユーザ情報
        String userName = userNames.get();
        if (userName != null) {
            header.append("[user=").append(userName).append("] ");
        }

        // ファーム情報
        String farmName = farmNames.get();
        if (farmName != null) {
            header.append("[farm=").append(farmName).append("] ");
        }

        return header.toString();
    }

    public static Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("userNo", getUserNo());
        context.put("userName", getUserName());
        context.put("farmNo", getFarmNo());
        context.put("farmName", getFarmName());
        context.put("componentNo", getComponentNo());
        context.put("componentName", getComponentName());
        context.put("instanceNo", getInstanceNo());
        context.put("instanceName", getInstanceName());
        context.put("instanceType", getInstanceType());
        context.put("platformNo", getPlatformNo());
        return context;
    }

    public static void setContext(Map<String, Object> context) {
        setUserNo((Long) context.get("userNo"));
        setUserName((String) context.get("userName"));
        setFarmNo((Long) context.get("farmNo"));
        setFarmName((String) context.get("farmName"));
        setComponentNo((Long) context.get("componentNo"));
        setComponentName((String) context.get("componentName"));
        setInstanceNo((Long) context.get("instanceNo"));
        setInstanceName((String) context.get("instanceName"));
        setInstanceType((String) context.get("instanceType"));
        setPlatformNo((Long) context.get("platformNo"));
    }

    public static void removeContext() {
        userNos.remove();
        userNames.remove();
        farmNos.remove();
        farmNames.remove();
        componentNos.remove();
        componentNames.remove();
        instanceNos.remove();
        instanceNames.remove();
        instanceTypes.remove();
        platformNos.remove();
    }

    public static Long getUserNo() {
        return userNos.get();
    }

    public static void setUserNo(Long userNo) {
        userNos.set(userNo);
    }

    public static String getUserName() {
        return userNames.get();
    }

    public static void setUserName(String userName) {
        userNames.set(userName);
    }

    public static Long getFarmNo() {
        return farmNos.get();
    }

    public static void setFarmNo(Long farmNo) {
        farmNos.set(farmNo);
    }

    public static String getFarmName() {
        return farmNames.get();
    }

    public static void setFarmName(String farmName) {
        farmNames.set(farmName);
    }

    public static Long getComponentNo() {
        return componentNos.get();
    }

    public static void setComponentNo(Long componentNo) {
        componentNos.set(componentNo);
    }

    public static String getComponentName() {
        return componentNames.get();
    }

    public static void setComponentName(String componentName) {
        componentNames.set(componentName);
    }

    public static Long getInstanceNo() {
        return instanceNos.get();
    }

    public static void setInstanceNo(Long instanceNo) {
        instanceNos.set(instanceNo);
    }

    public static String getInstanceName() {
        return instanceNames.get();
    }

    public static void setInstanceName(String instanceName) {
        instanceNames.set(instanceName);
    }

    public static String getInstanceType() {
        return instanceTypes.get();
    }

    public static void setInstanceType(String instanceType) {
        instanceTypes.set(instanceType);
    }

    public static Long getPlatformNo() {
        return platformNos.get();
    }

    public static void setPlatformNo(Long platformNo) {
        platformNos.set(platformNo);
    }

}
