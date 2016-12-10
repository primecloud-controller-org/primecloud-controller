/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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

import jp.primecloud.auto.log.service.OperationLogService;

public class OperationLogger {

    public static void writeFarm(String screen, String operation, Long farmNo, String memo) {
        write(screen, operation, farmNo, null, null, null, memo);
    }

    public static void writeInstance(String screen, String operation, Long instanceNo, String memo) {
        write(screen, operation, ViewContext.getFarmNo(), instanceNo, null, null, memo);
    }

    public static void writeComponent(String screen, String operation, Long componentNo, String memo) {
        write(screen, operation, ViewContext.getFarmNo(), null, componentNo, null, memo);
    }

    public static void writeLoadBalancer(String screen, String operation, Long loadBalancerNo, String memo) {
        write(screen, operation, ViewContext.getFarmNo(), null, null, loadBalancerNo, memo);
    }

    public static void write(String screen, String operation, String memo) {
        write(screen, operation, ViewContext.getFarmNo(), null, null, null, memo);
    }

    private static void write(String screen, String operation, Long farmNo, Long instanceNo, Long componentNo,
            Long loadBalancerNo, String memo) {
        OperationLogService orerationLogService = BeanContext.getBean(OperationLogService.class);
        orerationLogService.writeOperationLog(ViewContext.getUserNo(), ViewContext.getUsername(), farmNo, screen,
                operation, instanceNo, componentNo, loadBalancerNo, memo);
    }

}
