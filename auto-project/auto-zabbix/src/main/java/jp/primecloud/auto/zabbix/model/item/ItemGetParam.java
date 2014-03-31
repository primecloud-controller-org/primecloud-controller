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
package jp.primecloud.auto.zabbix.model.item;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * ItemのGetメソッド用パラメータクラスです。
 * </p>
 *
 */
public class ItemGetParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> itemids;

    private List<String> hostids;

    private String application;

    private String output;

    /**
     * itemidsを取得します。
     *
     * @return itemids
     */
    public List<String> getItemids() {
        return itemids;
    }

    /**
     * itemidsを設定します。
     *
     * @param itemids itemids
     */
    public void setItemids(List<String> itemids) {
        this.itemids = itemids;
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
     * applicationを取得します。
     *
     * @return application
     */
    public String getApplication() {
        return application;
    }

    /**
     * applicationを設定します。
     *
     * @param application application
     */
    public void setApplication(String application) {
        this.application = application;
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
