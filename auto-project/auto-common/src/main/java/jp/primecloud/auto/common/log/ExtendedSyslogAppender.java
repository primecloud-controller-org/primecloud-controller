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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.net.SyslogAppender;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ExtendedSyslogAppender extends SyslogAppender {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSyslogHost(String syslogHost) {
        super.setSyslogHost(syslogHost);

        String hostname = null;
        if ("localhost".equalsIgnoreCase(syslogHost) || "127.0.0.1".equals(syslogHost)) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
                int index = hostname.indexOf(".");
                if (index != -1) {
                    hostname = hostname.substring(0, index);
                }
            } catch (UnknownHostException ignore) {
            }
        }
        if (hostname == null) {
            hostname = syslogHost;
        }
        MDC.put("host", hostname);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void append(LoggingEvent event) {
        // 例外情報を返さないLoggingEventにラップする
        ExtendedLoggingEvent event2 = new ExtendedLoggingEvent(event);
        super.append(event2);
    }

    private static class ExtendedLoggingEvent extends LoggingEvent {

        private static final long serialVersionUID = 1L;

        private LoggingEvent event;

        public ExtendedLoggingEvent(LoggingEvent event) {
            super("dummy", Logger.getLogger("dummy"), Level.DEBUG, "dummy", null);
            this.event = event;
        }

        @Override
        public ThrowableInformation getThrowableInformation() {
            // 例外情報を返さない
            return null;
        }

        @Override
        public String[] getThrowableStrRep() {
            // 例外情報を返さない 
            return null;
        }

        @Override
        public Level getLevel() {
            return event.getLevel();
        }

        @Override
        public LocationInfo getLocationInformation() {
            return event.getLocationInformation();
        }

        @Override
        public String getLoggerName() {
            return event.getLoggerName();
        }

        @Override
        public Object getMDC(String key) {
            return event.getMDC(key);
        }

        @Override
        public void getMDCCopy() {
            event.getMDCCopy();
        }

        @Override
        public Object getMessage() {
            return event.getMessage();
        }

        @Override
        public String getNDC() {
            return event.getNDC();
        }

        @Override
        public String getRenderedMessage() {
            return event.getRenderedMessage();
        }

        @Override
        public String getThreadName() {
            return event.getThreadName();
        }

    }

}
