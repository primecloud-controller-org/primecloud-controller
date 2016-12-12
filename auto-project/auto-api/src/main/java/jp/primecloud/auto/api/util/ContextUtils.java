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
package jp.primecloud.auto.api.util;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ContextUtils {

    private static ThreadLocal<HttpSession> sessions = new ThreadLocal<HttpSession>();

    private ContextUtils() {
    }

    public static HttpSession getSession() {
        return sessions.get();
    }

    public static void setSession(HttpSession session) {
        sessions.set(session);
    }

    public static void removeSession() {
        sessions.remove();
    }

    public static void invalidateSession() {
        HttpSession session = sessions.get();
        if (session != null) {
            session.invalidate();
            sessions.remove();
        }
    }

    public static Object getAttribute(String name) {
        return getSession().getAttribute(name);
    }

    public static void setAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    public static void removeAttribute(String name) {
        getSession().removeAttribute(name);
    }

}
