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
                    +  ",'" + eventLog.getMessage() + "')\u003b");
        }

    }
}
