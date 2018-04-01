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
package jp.primecloud.auto.zabbix.model.usergroup;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.zabbix.model.right.Right;

/**
 * <p>
 * UsergroupのMassAddメソッド用パラメータクラスです。
 * </p>
 *
 */
public class UsergroupMassAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> usrgrpids;

    private List<String> userids;

    private List<Right> rights;

    /**
     * usrgrpidsを取得します。
     *
     * @return usrgrpids
     */
    public List<String> getUsrgrpids() {
        return usrgrpids;
    }

    /**
     * usrgrpidsを設定します。
     *
     * @param usrgrpids usrgrpids
     */
    public void setUsrgrpids(List<String> usrgrpids) {
        this.usrgrpids = usrgrpids;
    }

    /**
     * useridsを取得します。
     *
     * @return userids
     */
    public List<String> getUserids() {
        return userids;
    }

    /**
     * useridsを設定します。
     *
     * @param userids userids
     */
    public void setUserids(List<String> userids) {
        this.userids = userids;
    }

    /**
     * rightsを取得します。
     *
     * @return rights
     */
    public List<Right> getRights() {
        return rights;
    }

    /**
     * rightsを設定します。
     *
     * @param rights rights
     */
    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

}
