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
package jp.primecloud.auto.puppet.report;

import java.io.Serializable;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MetricsTime implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private double time;

    /**
     * nameを取得します。
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します。
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * timeを取得します。
     *
     * @return time
     */
    public double getTime() {
        return time;
    }

    /**
     * timeを設定します。
     *
     * @param time time
     */
    public void setTime(double time) {
        this.time = time;
    }

}
