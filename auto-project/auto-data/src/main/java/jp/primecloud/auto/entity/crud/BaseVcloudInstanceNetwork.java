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
 * VCLOUD_INSTANCE_NETWORKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVcloudInstanceNetwork implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** NETWORK_NO [BIGINT(19,0)] */
    private Long networkNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** NETWORK_NAME [VARCHAR(100,0)] */
    private String networkName;

    /** NETWORK_INDEX [INT(10,0)] */
    private Integer networkIndex;

    /** IP_MODE [VARCHAR(100,0)] */
    private String ipMode;

    /** IP_ADDRESS [VARCHAR(100,0)] */
    private String ipAddress;

    /** IS_PRIMARY [BIT(0,0)] */
    private Boolean isPrimary;

    /**
     * networkNoを取得します。
     *
     * @return networkNo
     */
    public Long getNetworkNo() {
        return networkNo;
    }

    /**
     * networkNoを設定します。
     *
     * @param networkNo networkNo
     */
    public void setNetworkNo(Long networkNo) {
        this.networkNo = networkNo;
    }

    /**
     * platformNoを取得します。
     *
     * @return platformNo
     */
    public Long getPlatformNo() {
        return platformNo;
    }

    /**
     * platformNoを設定します。
     *
     * @param platformNo platformNo
     */
    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    /**
     * instanceNoを取得します。
     *
     * @return instanceNo
     */
    public Long getInstanceNo() {
        return instanceNo;
    }

    /**
     * instanceNoを設定します。
     *
     * @param instanceNo instanceNo
     */
    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

    /**
     * farmNoを取得します。
     *
     * @return farmNo
     */
    public Long getFarmNo() {
        return farmNo;
    }

    /**
     * farmNoを設定します。
     *
     * @param farmNo farmNo
     */
    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
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
     * networkIndexを取得します。
     *
     * @return networkIndex
     */
    public Integer getNetworkIndex() {
        return networkIndex;
    }

    /**
     * networkIndexを設定します。
     *
     * @param networkIndex networkIndex
     */
    public void setNetworkIndex(Integer networkIndex) {
        this.networkIndex = networkIndex;
    }

    /**
     * ipModeを取得します。
     *
     * @return ipMode
     */
    public String getIpMode() {
        return ipMode;
    }

    /**
     * ipModeを設定します。
     *
     * @param ipMode ipMode
     */
    public void setIpMode(String ipMode) {
        this.ipMode = ipMode;
    }

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
     * isPrimaryを取得します。
     *
     * @return isPrimary
     */
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    /**
     * isPrimaryを設定します。
     *
     * @param isPrimary isPrimary
     */
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((networkNo == null) ? 0 : networkNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((networkName == null) ? 0 : networkName.hashCode());
        result = prime * result + ((networkIndex == null) ? 0 : networkIndex.hashCode());
        result = prime * result + ((ipMode == null) ? 0 : ipMode.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((isPrimary == null) ? 0 : isPrimary.hashCode());

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

        final BaseVcloudInstanceNetwork other = (BaseVcloudInstanceNetwork) obj;
        if (networkNo == null) {
            if (other.networkNo != null) { return false; }
        } else if (!networkNo.equals(other.networkNo)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (networkName == null) {
            if (other.networkName != null) { return false; }
        } else if (!networkName.equals(other.networkName)) {
            return false;
        }
        if (networkIndex == null) {
            if (other.networkIndex != null) { return false; }
        } else if (!networkIndex.equals(other.networkIndex)) {
            return false;
        }
        if (ipMode == null) {
            if (other.ipMode != null) { return false; }
        } else if (!ipMode.equals(other.ipMode)) {
            return false;
        }
        if (ipAddress == null) {
            if (other.ipAddress != null) { return false; }
        } else if (!ipAddress.equals(other.ipAddress)) {
            return false;
        }
        if (isPrimary == null) {
            if (other.isPrimary != null) { return false; }
        } else if (!isPrimary.equals(other.isPrimary)) {
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
        sb.append("VcloudInstanceNetwork").append(" [");
        sb.append("networkNo=").append(networkNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("networkName=").append(networkName).append(", ");
        sb.append("networkIndex=").append(networkIndex).append(", ");
        sb.append("ipMode=").append(ipMode).append(", ");
        sb.append("ipAddress=").append(ipAddress).append(", ");
        sb.append("isPrimary=").append(isPrimary);
        sb.append("]");
        return sb.toString();
    }

}
