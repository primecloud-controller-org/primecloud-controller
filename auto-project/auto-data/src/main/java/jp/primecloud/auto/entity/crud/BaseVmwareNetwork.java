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
 * VMWARE_NETWORKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVmwareNetwork implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** NETWORK_NO [BIGINT(19,0)] */
    private Long networkNo;

    /** NETWORK_NAME [VARCHAR(100,0)] */
    private String networkName;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** VLAN_ID [INT(10,0)] */
    private Integer vlanId;

    /** VSWITCH_NAME [VARCHAR(100,0)] */
    private String vswitchName;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** PUBLIC_NETWORK [BIT(0,0)] */
    private Boolean publicNetwork;

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
     * vlanIdを取得します。
     *
     * @return vlanId
     */
    public Integer getVlanId() {
        return vlanId;
    }

    /**
     * vlanIdを設定します。
     *
     * @param vlanId vlanId
     */
    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    /**
     * vswitchNameを取得します。
     *
     * @return vswitchName
     */
    public String getVswitchName() {
        return vswitchName;
    }

    /**
     * vswitchNameを設定します。
     *
     * @param vswitchName vswitchName
     */
    public void setVswitchName(String vswitchName) {
        this.vswitchName = vswitchName;
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
     * publicNetworkを取得します。
     *
     * @return publicNetwork
     */
    public Boolean getPublicNetwork() {
        return publicNetwork;
    }

    /**
     * publicNetworkを設定します。
     *
     * @param publicNetwork publicNetwork
     */
    public void setPublicNetwork(Boolean publicNetwork) {
        this.publicNetwork = publicNetwork;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((networkNo == null) ? 0 : networkNo.hashCode());
        result = prime * result + ((networkName == null) ? 0 : networkName.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((vlanId == null) ? 0 : vlanId.hashCode());
        result = prime * result + ((vswitchName == null) ? 0 : vswitchName.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((publicNetwork == null) ? 0 : publicNetwork.hashCode());

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

        final BaseVmwareNetwork other = (BaseVmwareNetwork) obj;
        if (networkNo == null) {
            if (other.networkNo != null) { return false; }
        } else if (!networkNo.equals(other.networkNo)) {
            return false;
        }
        if (networkName == null) {
            if (other.networkName != null) { return false; }
        } else if (!networkName.equals(other.networkName)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (vlanId == null) {
            if (other.vlanId != null) { return false; }
        } else if (!vlanId.equals(other.vlanId)) {
            return false;
        }
        if (vswitchName == null) {
            if (other.vswitchName != null) { return false; }
        } else if (!vswitchName.equals(other.vswitchName)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (publicNetwork == null) {
            if (other.publicNetwork != null) { return false; }
        } else if (!publicNetwork.equals(other.publicNetwork)) {
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
        sb.append("VmwareNetwork").append(" [");
        sb.append("networkNo=").append(networkNo).append(", ");
        sb.append("networkName=").append(networkName).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("vlanId=").append(vlanId).append(", ");
        sb.append("vswitchName=").append(vswitchName).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("publicNetwork=").append(publicNetwork);
        sb.append("]");
        return sb.toString();
    }

}
