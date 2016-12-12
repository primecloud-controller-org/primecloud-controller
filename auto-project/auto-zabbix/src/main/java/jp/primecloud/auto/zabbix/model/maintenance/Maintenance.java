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
package jp.primecloud.auto.zabbix.model.maintenance;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.zabbix.model.timeperiod.Timeperiod;


/**
 * <p>
 * Maintenanceのエンティティクラスです。
 * </p>
 *
 */
public class Maintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    private String maintenanceid;

    private String name;

    private String maintenanceType;

    private String description;

    private String activeSince;

    private String activeTill;

    private List<Timeperiod> timeperiods;

    /**
     * maintenanceidを取得します。
     *
     * @return maintenanceid
     */
    public String getMaintenanceid() {
        return maintenanceid;
    }

    /**
     * maintenanceidを設定します。
     *
     * @param maintenanceid maintenanceid
     */
    public void setMaintenanceid(String maintenanceid) {
        this.maintenanceid = maintenanceid;
    }

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
     * maintenanceTypeを取得します。
     *
     * @return maintenanceType
     */
    public String getMaintenanceType() {
        return maintenanceType;
    }

    /**
     * maintenanceTypeを設定します。
     *
     * @param maintenanceType maintenanceType
     */
    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    /**
     * descriptionを取得します。
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * descriptionを設定します。
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * activeSinceを取得します。
     *
     * @return activeSince
     */
    public String getActiveSince() {
        return activeSince;
    }

    /**
     * activeSinceを設定します。
     *
     * @param activeSince activeSince
     */
    public void setActiveSince(String activeSince) {
        this.activeSince = activeSince;
    }

    /**
     * activeTillを取得します。
     *
     * @return activeTill
     */
    public String getActiveTill() {
        return activeTill;
    }

    /**
     * activeTillを設定します。
     *
     * @param activeTill activeTill
     */
    public void setActiveTill(String activeTill) {
        this.activeTill = activeTill;
    }

    /**
     * timeperiodsを取得します。
     *
     * @return timeperiods
     */
    public List<Timeperiod> getTimeperiods() {
        return timeperiods;
    }

    /**
     * timeperiodsを設定します。
     *
     * @param timeperiods timeperiods
     */
    public void setTimeperiods(List<Timeperiod> timeperiods) {
        this.timeperiods = timeperiods;
    }
}
