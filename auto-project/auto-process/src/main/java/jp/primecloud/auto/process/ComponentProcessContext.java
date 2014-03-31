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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentProcessContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long farmNo;

    private List<Long> runningInstanceNos;

    private Map<Long, List<Long>> enableInstanceNoMap;

    private Map<Long, List<Long>> disableInstanceNoMap;

    private List<Long> targetLoadBalancerNos;

    /**
     * farmNoを取得します。
     *
     * @return farmNo
     */
    public Long getFarmNo() {
        return farmNo;
    }

    /**
     * farmNoを設定します。
     *
     * @param farmNo farmNo
     */
    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }

    /**
     * runningInstanceNosを取得します。
     *
     * @return runningInstanceNos
     */
    public List<Long> getRunningInstanceNos() {
        return runningInstanceNos;
    }

    /**
     * runningInstanceNosを設定します。
     *
     * @param runningInstanceNos runningInstanceNos
     */
    public void setRunningInstanceNos(List<Long> runningInstanceNos) {
        this.runningInstanceNos = runningInstanceNos;
    }

    /**
     * enableInstanceNoMapを取得します。
     *
     * @return enableInstanceNoMap
     */
    public Map<Long, List<Long>> getEnableInstanceNoMap() {
        return enableInstanceNoMap;
    }

    /**
     * enableInstanceNoMapを設定します。
     *
     * @param enableInstanceNoMap enableInstanceNoMap
     */
    public void setEnableInstanceNoMap(Map<Long, List<Long>> enableInstanceNoMap) {
        this.enableInstanceNoMap = enableInstanceNoMap;
    }

    /**
     * disableInstanceNoMapを取得します。
     *
     * @return disableInstanceNoMap
     */
    public Map<Long, List<Long>> getDisableInstanceNoMap() {
        return disableInstanceNoMap;
    }

    /**
     * disableInstanceNoMapを設定します。
     *
     * @param disableInstanceNoMap disableInstanceNoMap
     */
    public void setDisableInstanceNoMap(Map<Long, List<Long>> disableInstanceNoMap) {
        this.disableInstanceNoMap = disableInstanceNoMap;
    }

    /**
     * targetLoadBalancerNosを取得します。
     *
     * @return targetLoadBalancerNos
     */
    public List<Long> getTargetLoadBalancerNos() {
        return targetLoadBalancerNos;
    }

    /**
     * targetLoadBalancerNosを設定します。
     *
     * @param targetLoadBalancerNos targetLoadBalancerNos
     */
    public void setTargetLoadBalancerNos(List<Long> targetLoadBalancerNos) {
        this.targetLoadBalancerNos = targetLoadBalancerNos;
    }

}
