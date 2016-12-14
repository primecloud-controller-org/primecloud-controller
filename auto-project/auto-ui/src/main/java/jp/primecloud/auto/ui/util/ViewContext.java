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

import jp.primecloud.auto.common.log.LoggingUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ViewContext {

    private static final String PREFIX = ViewContext.class.getName() + ".";

    private static final String USER_NO_KEY = PREFIX + "userNo";

    private static final String FARM_NO_KEY = PREFIX + "farmNo";

    private static final String USERNAME_KEY = PREFIX + "username";

    private static final String FARM_NAME_KEY = PREFIX + "farmName";

    public static Long getUserNo() {
        return (Long) ContextUtils.getAttribute(USER_NO_KEY);
    }

    public static void setUserNo(Long userNo) {
        ContextUtils.setAttribute(USER_NO_KEY, userNo);
        LoggingUtils.setUserNo(userNo);
    }

    public static Long getFarmNo() {
        return (Long) ContextUtils.getAttribute(FARM_NO_KEY);
    }

    public static void setFarmNo(Long farmNo) {
        ContextUtils.setAttribute(FARM_NO_KEY, farmNo);
        LoggingUtils.setFarmNo(farmNo);
    }

    public static String getUsername() {
        return (String) ContextUtils.getAttribute(USERNAME_KEY);
    }

    public static void setUsername(String username) {
        ContextUtils.setAttribute(USERNAME_KEY, username);
        LoggingUtils.setUserName(username);
    }

    public static String getFarmName() {
        return (String) ContextUtils.getAttribute(FARM_NAME_KEY);
    }

    public static void setFarmName(String farmName) {
        ContextUtils.setAttribute(FARM_NAME_KEY, farmName);
        LoggingUtils.setFarmName(farmName);
    }

}
