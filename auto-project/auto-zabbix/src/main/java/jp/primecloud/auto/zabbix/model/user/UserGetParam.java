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
package jp.primecloud.auto.zabbix.model.user;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * UserのGetメソッド用パラメータクラスです。
 * </p>
 *
 */
public class UserGetParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> userids;

    private String output;

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
