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
package jp.primecloud.auto.service;

import java.util.List;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public interface ProcessService {

    public void startInstances(Long farmNo, List<Long> instanceNos);

    public void startInstances(Long farmNo, List<Long> instanceNos, boolean startComponent);

    public void stopInstances(Long farmNo, List<Long> instanceNos);

    public void startComponents(Long farmNo, List<Long> componentNos);

    public void startComponents(Long farmNo, Long componentNo, List<Long> instanceNos);

    public void stopComponents(Long farmNo, List<Long> componentNos);

    public void stopComponents(Long farmNo, List<Long> componentNos, boolean stopInstance);

    public void stopComponents(Long farmNo, Long componentNo, List<Long> instanceNos, boolean stopInstance);

    public void updateComponents(Long farmNo);

    public void startLoadBalancers(Long farmNo, List<Long> loadBalancerNos);

    public void stopLoadBalancers(Long farmNo, List<Long> loadBalancerNos);

    public void startLoadBalancerListeners(Long farmNo, Long loadBalancerNo, List<Integer> loadBalancerPorts);

    public void stopLoadBalancerListeners(Long farmNo, Long loadBalancerNo, List<Integer> loadBalancerPorts);

}
