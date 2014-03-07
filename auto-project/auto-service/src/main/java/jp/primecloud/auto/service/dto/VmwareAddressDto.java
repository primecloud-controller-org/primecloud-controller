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
package jp.primecloud.auto.service.dto;

import java.io.Serializable;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareAddressDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ipAddress;

    private String subnetMask;

    private String defaultGateway;

    /**
     * ipAddressを取得します。
     *
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * ipAddressを設定します。
     *
     * @param ipAddress ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * subnetMaskを取得します。
     *
     * @return subnetMask
     */
    public String getSubnetMask() {
        return subnetMask;
    }

    /**
     * subnetMaskを設定します。
     *
     * @param subnetMask subnetMask
     */
    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    /**
     * defaultGatewayを取得します。
     *
     * @return defaultGateway
     */
    public String getDefaultGateway() {
        return defaultGateway;
    }

    /**
     * defaultGatewayを設定します。
     *
     * @param defaultGateway defaultGateway
     */
    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

}
