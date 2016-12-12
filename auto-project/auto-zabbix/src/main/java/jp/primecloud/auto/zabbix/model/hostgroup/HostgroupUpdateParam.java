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
package jp.primecloud.auto.zabbix.model.hostgroup;

import java.io.Serializable;

/**
 * <p>
 * HostgroupのUpdateメソッド用パラメータクラスです。
 * </p>
 *
 */
public class HostgroupUpdateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupid;

    private String name;

    /**
     * groupidを取得します。
     *
     * @return groupid
     */
    public String getGroupid() {
        return groupid;
    }

    /**
     * groupidを設定します。
     *
     * @param groupid groupid
     */
    public void setGroupid(String groupid) {
        this.groupid = groupid;
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

}
