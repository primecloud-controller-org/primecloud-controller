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
package jp.primecloud.auto.zabbix.model.application;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * ApplicationのGetメソッド用パラメータクラスです。
 * </p>
 *
 */
public class ApplicationGetParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> hostids;

    @Deprecated
    private List<String> templateids;

    private List<String> applicationids;

    private String output;

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
     * templateidsを取得します。
     *
     * @return templateids
     */
    @Deprecated
    public List<String> getTemplateids() {
        return templateids;
    }

    /**
     * templateidsを設定します。
     *
     * @param templateids templateids
     */
    @Deprecated
    public void setTemplateids(List<String> templateids) {
        this.templateids = templateids;
    }

    /**
     * applicationidsを取得します。
     *
     * @return applicationids
     */
    public List<String> getApplicationids() {
        return applicationids;
    }

    /**
     * applicationidsを設定します。
     *
     * @param applicationids applicationids
     */
    public void setApplicationids(List<String> applicationids) {
        this.applicationids = applicationids;
    }

    /**
     * outputを取得します。
     *
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
