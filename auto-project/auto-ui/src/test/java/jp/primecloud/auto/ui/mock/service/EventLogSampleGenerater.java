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
package jp.primecloud.auto.ui.mock.service;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.primecloud.auto.log.entity.crud.EventLog;
import jp.primecloud.auto.ui.mock.MockLogData;

public class EventLogSampleGenerater {

    public static void main(String[] args) {

        MockLogData mockLogData = new MockLogData(10000);
        List<EventLog> list = mockLogData.getEventLogs();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (EventLog eventLog : list) {
            System.out.println("INSERT INTO EVENT_LOG values (null,'" + sdf1.format(eventLog.getLogDate()) + "','"
                    + eventLog.getLogLevel() + "'," + eventLog.getUserNo() + ",'" + eventLog.getUserName() + "',"
                    + eventLog.getFarmNo() + ",'" + eventLog.getFarmName() + "'," + eventLog.getComponentNo() + ",'"
                    + eventLog.getComponentName() + "'," + eventLog.getInstanceNo() + ",'" + eventLog.getInstanceName()
                    + ",'" + eventLog.getMessage() + "')\u003b");
        }

    }
}
