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

import java.util.List;

import jp.primecloud.auto.service.ProcessService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockProcessService implements ProcessService {

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public void startInstances(Long farmNo, List<Long> instanceNos) {
        log.info("startInstances: " + instanceNos);
    }

    @Override
    public void startInstances(Long farmNo, List<Long> instanceNos, boolean startComponent) {
        log.info("startInstances: " + instanceNos + " , startComponent: " + startComponent);
    }

    @Override
    public void stopInstances(Long farmNo, List<Long> instanceNos) {
        log.info("stopInstances: " + instanceNos);
    }

    @Override
    public void startComponents(Long farmNo, List<Long> componentNos) {
        log.info("startComponents: " + componentNos);
    }

    @Override
    public void startComponents(Long farmNo, Long componentNo, List<Long> instanceNos) {
        log.info("startComponent: " + componentNo + ", instanceNos: " + instanceNos);

    }

    @Override
    public void stopComponents(Long farmNo, List<Long> componentNos) {
        log.info("stopComponents: " + componentNos);
    }

    @Override
    public void stopComponents(Long farmNo, List<Long> componentNos, boolean stopInstance) {
        log.info("stopComponents: " + componentNos + " , stopInstance: " + stopInstance);
    }

    @Override
    public void stopComponents(Long farmNo, Long componentNo, List<Long> instanceNos, boolean stopInstance) {
        log.info("stopComponent: " + componentNo + ", instanceNos: " + instanceNos + ", stopInstance: " + stopInstance);

    }

    @Override
    public void updateComponents(Long farmNo) {
        log.info("updateComponents");
    }

    @Override
    public void startLoadBalancers(Long farmNo, List<Long> loadBalancerNos) {
        log.info("startLoadBalancers: " + loadBalancerNos);
    }

    @Override
    public void stopLoadBalancers(Long farmNo, List<Long> loadBalancerNos) {
        log.info("stopLoadBalancers: " + loadBalancerNos);
    }

    @Override
    public void startLoadBalancerListeners(Long farmNo, Long loadBalancerNo, List<Integer> loadBalancerPorts) {
        log.info("startLoadBalancerListeners: " + loadBalancerNo + ", loadBalancerPorts=" + loadBalancerPorts);
    }

    @Override
    public void stopLoadBalancerListeners(Long farmNo, Long loadBalancerNo, List<Integer> loadBalancerPorts) {
        log.info("stopLoadBalancerListeners: " + loadBalancerNo + ", loadBalancerPorts=" + loadBalancerPorts);
    }

}
