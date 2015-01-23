package jp.primecloud.auto.ui.mock.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.log.dao.crud.EventLogDao.SearchCondition;
import jp.primecloud.auto.log.entity.crud.EventLog;
import jp.primecloud.auto.log.service.EventLogService;
import jp.primecloud.auto.ui.mock.MockLogData;

public class MockEventLogService implements EventLogService {

    public static final int limit = 1000;

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public List<EventLog> readBySearchCondition(SearchCondition searchCondition) {
        MockLogData mockLogData = new MockLogData(1000);

        log.info("fromDate: " + searchCondition.getFromDate() + ", toDate: " + searchCondition.getToDate()
                + ", userNo: " + searchCondition.getUserNo() + ", FarmNo: " + searchCondition.getFarmNo()
                + " ,componentNo:" + searchCondition.getComponentNo() + ", instanceNo:"
                + searchCondition.getInstanceNo() + ", logLevel:" + searchCondition.getLogLevel());

        return mockLogData.getEventLogs();
    }

}
