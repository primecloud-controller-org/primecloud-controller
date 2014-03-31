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
package jp.primecloud.auto.vmware;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareClientFactory {

    protected String url;

    protected String username;

    protected String password;

    protected boolean ignoreCert = false;

    protected String datacenterName;

    /**
     * TODO: メソッドコメントを記述
     *
     * @return
     */
    public VmwareClient createVmwareClient() {
        VmwareClient vmwareClient = new VmwareClient(url, username, password, ignoreCert, datacenterName);
        return vmwareClient;
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * usernameを設定します。
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * passwordを設定します。
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * ignoreCertを設定します。
     *
     * @param ignoreCert ignoreCert
     */
    public void setIgnoreCert(boolean ignoreCert) {
        this.ignoreCert = ignoreCert;
    }

    /**
     * datacenterNameを設定します。
     *
     * @param datacenterName datacenterName
     */
    public void setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
    }

}
