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
package jp.primecloud.auto.zabbix.model.timeperiod;

import java.io.Serializable;

/**
 * <p>
 * Timeperiodのエンティティクラスです。
 * </p>
 *
 */
public class Timeperiod implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer timeperiodType;

    private Integer every;

    private Integer month;

    private Integer dayofweek;

    private Integer day;

    private Integer starTime;

    private Integer period;

    private Integer startDate;

    /**
     * timeperiodTypeを取得します。
     *
     * @return timeperiodType
     */
    public Integer getTimeperiodType() {
        return timeperiodType;
    }

    /**
     * timeperiodTypeを設定します。
     *
     * @param timeperiodType timeperiodType
     */
    public void setTimeperiodType(Integer timeperiodType) {
        this.timeperiodType = timeperiodType;
    }

    /**
     * everyを取得します。
     *
     * @return every
     */
    public Integer getEvery() {
        return every;
    }

    /**
     * everyを設定します。
     *
     * @param every every
     */
    public void setEvery(Integer every) {
        this.every = every;
    }

    /**
     * monthを取得します。
     *
     * @return month
     */
    public Integer getMonth() {
        return month;
    }

    /**
     * monthを設定します。
     *
     * @param month month
     */
    public void setMonth(Integer month) {
        this.month = month;
    }

    /**
     * dayofweekを取得します。
     *
     * @return dayofweek
     */
    public Integer getDayofweek() {
        return dayofweek;
    }

    /**
     * dayofweekを設定します。
     *
     * @param dayofweek dayofweek
     */
    public void setDayofweek(Integer dayofweek) {
        this.dayofweek = dayofweek;
    }

    /**
     * dayを取得します。
     *
     * @return day
     */
    public Integer getDay() {
        return day;
    }

    /**
     * dayを設定します。
     *
     * @param day day
     */
    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     * starTimeを取得します。
     *
     * @return starTime
     */
    public Integer getStarTime() {
        return starTime;
    }

    /**
     * starTimeを設定します。
     *
     * @param starTime starTime
     */
    public void setStarTime(Integer starTime) {
        this.starTime = starTime;
    }

    /**
     * periodを取得します。
     *
     * @return period
     */
    public Integer getPeriod() {
        return period;
    }

    /**
     * periodを設定します。
     *
     * @param period period
     */
    public void setPeriod(Integer period) {
        this.period = period;
    }

    /**
     * startDateを取得します。
     *
     * @return startDate
     */
    public Integer getStartDate() {
        return startDate;
    }

    /**
     * startDateを設定します。
     *
     * @param startDate startDate
     */
    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }
}
