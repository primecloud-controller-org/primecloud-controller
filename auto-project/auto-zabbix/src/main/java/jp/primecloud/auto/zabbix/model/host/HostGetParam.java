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
package jp.primecloud.auto.zabbix.model.host;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * HostのGetメソッド用パラメータクラスです。
 * </p>
 *
 */
public class HostGetParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> hostids;

    private String output;

    private String selectGroups;

    private String selectParentTemplates;

    private Map<String, List<Object>> filter;

    public Map<String, List<Object>> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, List<Object>> filter) {
        this.filter = filter;
    }

    private List<String> groupids;

    public List<String> getGroupids() {
        return groupids;
    }

    public void setGroupids(List<String> groupids) {
        this.groupids = groupids;
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

    /**
     * selectGroupsを取得します。
     *
     * @return selectGroups
     */
    public String getSelectGroups() {
        return selectGroups;
    }

    /**
     * selectGroupsを設定します。
     *
     * @param selectGroups selectGroups
     */
    public void setSelectGroups(String selectGroups) {
        this.selectGroups = selectGroups;
    }

    /**
     * selectParentTemplatesを取得します。
     *
     * @return selectParentTemplates
     */
    public String getSelectParentTemplates() {
        return selectParentTemplates;
    }

    /**
     * selectParentTemplatesを設定します。
     *
     * @param selectParentTemplates selectParentTemplates
     */
    public void setSelectParentTemplates(String selectParentTemplates) {
        this.selectParentTemplates = selectParentTemplates;
    }
}
