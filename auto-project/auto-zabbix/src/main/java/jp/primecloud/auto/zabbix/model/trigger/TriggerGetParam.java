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
package jp.primecloud.auto.zabbix.model.trigger;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * TriggerのGetメソッド用パラメータクラスです。
 * </p>
 *
 */
public class TriggerGetParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> triggerids;

    private List<String> hostids;

    private String output;

    /**
     * triggeridsを取得します。
     *
     * @return triggerids
     */
    public List<String> getTriggerids() {
        return triggerids;
    }

    /**
     * triggeridsを設定します。
     *
     * @param triggerids triggerids
     */
    public void setTriggerids(List<String> triggerids) {
        this.triggerids = triggerids;
    }

    /**
     * hostidsを取得します。
     *
     * @return hostids
     */
    public List<String> getHostids() {
        return hostids;
    }

    /**
     * hostidsを設定します。
     *
     * @param hostids hostids
     */
    public void setHostids(List<String> hostids) {
        this.hostids = hostids;
    }

    /**
     * outputを取得します。
     *
     * @return output
     */
    public String getOutput() {
        return output;
    }

    /**
     * outputを設定します。
     *
     * @param output output
     */
    public void setOutput(String output) {
        this.output = output;
    }
}
