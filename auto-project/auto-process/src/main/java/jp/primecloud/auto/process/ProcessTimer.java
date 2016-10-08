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
package jp.primecloud.auto.process;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ProcessTimer implements InitializingBean, DisposableBean {

    protected Log log = LogFactory.getLog(ProcessTimer.class);

    protected Timer timer;

    protected ProcessManager processManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    processManager.process();
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 15 * 1000, 15 * 1000);
    }

    @Override
    public void destroy() throws Exception {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * processManagerを設定します。
     *
     * @param processManager processManager
     */
    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

}
