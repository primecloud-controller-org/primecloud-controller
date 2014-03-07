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

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class InstancesProcessContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long farmNo;

    private List<Long> startInstanceNos;

    private List<Long> stopInstanceNos;

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
     * startInstanceNosを取得します。
     *
     * @return startInstanceNos
     */
    public List<Long> getStartInstanceNos() {
        return startInstanceNos;
    }

    /**
     * startInstanceNosを設定します。
     *
     * @param startInstanceNos startInstanceNos
     */
    public void setStartInstanceNos(List<Long> startInstanceNos) {
        this.startInstanceNos = startInstanceNos;
    }

    /**
     * stopInstanceNosを取得します。
     *
     * @return stopInstanceNos
     */
    public List<Long> getStopInstanceNos() {
        return stopInstanceNos;
    }

    /**
     * stopInstanceNosを設定します。
     *
     * @param stopInstanceNos stopInstanceNos
     */
    public void setStopInstanceNos(List<Long> stopInstanceNos) {
        this.stopInstanceNos = stopInstanceNos;
    }

}
