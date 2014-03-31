package jp.primecloud.auto.ui.mock.service;

import jp.primecloud.auto.log.service.OperationLogService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockOperationLogService implements OperationLogService {

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public void writeOperationLog(Long arg0, String arg1, Long arg2, String arg3, String arg4, Long arg5, Long arg6,
            Long arg7, String arg8) {

        log.info(arg0 + " , " + arg1 + " , " + arg2 + " , " + arg3 + " , " + arg4 + " , " + arg5 + " , " + arg6 + " , "
                + arg7 + " , " + arg8);

    }

}
