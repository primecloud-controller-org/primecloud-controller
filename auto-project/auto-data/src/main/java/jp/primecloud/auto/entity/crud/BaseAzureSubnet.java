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
package jp.primecloud.auto.entity.crud;

import java.io.Serializable;

/**
 * <p>
 * AZURE_SUBNETに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAzureSubnet implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** SUBNET_NO [BIGINT(19,0)] */
    private Long subnetNo;

    /** SUBNET_NAME [VARCHAR(100,0)] */
    private String subnetName;

    /** NETWORK_NAME [VARCHAR(100,0)] */
    private String networkName;

    /**
     * subnetNoを取得します。
     *
     * @return subnetNo
     */
    public Long getSubnetNo() {
        return subnetNo;
    }

    /**
     * subnetNoを設定します。
     *
     * @param subnetNo subnetNo
     */
    public void setSubnetNo(Long subnetNo) {
        this.subnetNo = subnetNo;
    }

    /**
     * subnetNameを取得します。
     *
     * @return subnetName
     */
    public String getSubnetName() {
        return subnetName;
    }

    /**
     * subnetNameを設定します。
     *
     * @param subnetName subnetName
     */
    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
    }

    /**
     * networkNameを取得します。
     *
     * @return networkName
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * networkNameを設定します。
     *
     * @param networkName networkName
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((subnetNo == null) ? 0 : subnetNo.hashCode());
        result = prime * result + ((subnetName == null) ? 0 : subnetName.hashCode());
        result = prime * result + ((networkName == null) ? 0 : networkName.hashCode());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }

        final BaseAzureSubnet other = (BaseAzureSubnet) obj;
        if (subnetNo == null) {
            if (other.subnetNo != null) { return false; }
        } else if (!subnetNo.equals(other.subnetNo)) {
            return false;
        }
        if (subnetName == null) {
            if (other.subnetName != null) { return false; }
        } else if (!subnetName.equals(other.subnetName)) {
            return false;
        }
        if (networkName == null) {
            if (other.networkName != null) { return false; }
        } else if (!networkName.equals(other.networkName)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AzureSubnet").append(" [");
        sb.append("subnetNo=").append(subnetNo).append(", ");
        sb.append("subnetName=").append(subnetName).append(", ");
        sb.append("networkName=").append(networkName);
        sb.append("]");
        return sb.toString();
    }

}
