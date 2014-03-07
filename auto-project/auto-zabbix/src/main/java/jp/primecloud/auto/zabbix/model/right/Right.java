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
package jp.primecloud.auto.zabbix.model.right;

import java.io.Serializable;

/**
 * <p>
 * Rightのエンティティクラスです。
 * </p>
 *
 */
public class Right implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupid;

    private String id;

    private String permission;

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
     * idを取得します。
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * idを設定します。
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * permissionを取得します。
     *
     * @return permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * permissionを設定します。
     *
     * @param permission permission
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }
}
