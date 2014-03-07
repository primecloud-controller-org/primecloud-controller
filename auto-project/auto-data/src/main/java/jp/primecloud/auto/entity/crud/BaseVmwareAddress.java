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
 * VMWARE_ADDRESSに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVmwareAddress implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** ADDRESS_NO [BIGINT(19,0)] */
    private Long addressNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** IP_ADDRESS [VARCHAR(100,0)] */
    private String ipAddress;

    /** SUBNET_MASK [VARCHAR(100,0)] */
    private String subnetMask;

    /** DEFAULT_GATEWAY [VARCHAR(100,0)] */
    private String defaultGateway;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** ENABLED [BIT(0,0)] */
    private Boolean enabled;

    /** ASSOCIATED [BIT(0,0)] */
    private Boolean associated;

    /**
     * addressNoを取得します。
     *
     * @return addressNo
     */
    public Long getAddressNo() {
        return addressNo;
    }

    /**
     * addressNoを設定します。
     *
     * @param addressNo addressNo
     */
    public void setAddressNo(Long addressNo) {
        this.addressNo = addressNo;
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

    /**
     * userNoを取得します。
     *
     * @return userNo
     */
    public Long getUserNo() {
        return userNo;
    }

    /**
     * userNoを設定します。
     *
     * @param userNo userNo
     */
    public void setUserNo(Long userNo) {
        this.userNo = userNo;
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
     * enabledを取得します。
     *
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * enabledを設定します。
     *
     * @param enabled enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * associatedを取得します。
     *
     * @return associated
     */
    public Boolean getAssociated() {
        return associated;
    }

    /**
     * associatedを設定します。
     *
     * @param associated associated
     */
    public void setAssociated(Boolean associated) {
        this.associated = associated;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((addressNo == null) ? 0 : addressNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((subnetMask == null) ? 0 : subnetMask.hashCode());
        result = prime * result + ((defaultGateway == null) ? 0 : defaultGateway.hashCode());
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((associated == null) ? 0 : associated.hashCode());

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

        final BaseVmwareAddress other = (BaseVmwareAddress) obj;
        if (addressNo == null) {
            if (other.addressNo != null) { return false; }
        } else if (!addressNo.equals(other.addressNo)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (ipAddress == null) {
            if (other.ipAddress != null) { return false; }
        } else if (!ipAddress.equals(other.ipAddress)) {
            return false;
        }
        if (subnetMask == null) {
            if (other.subnetMask != null) { return false; }
        } else if (!subnetMask.equals(other.subnetMask)) {
            return false;
        }
        if (defaultGateway == null) {
            if (other.defaultGateway != null) { return false; }
        } else if (!defaultGateway.equals(other.defaultGateway)) {
            return false;
        }
        if (userNo == null) {
            if (other.userNo != null) { return false; }
        } else if (!userNo.equals(other.userNo)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (enabled == null) {
            if (other.enabled != null) { return false; }
        } else if (!enabled.equals(other.enabled)) {
            return false;
        }
        if (associated == null) {
            if (other.associated != null) { return false; }
        } else if (!associated.equals(other.associated)) {
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
        sb.append("VmwareAddress").append(" [");
        sb.append("addressNo=").append(addressNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("ipAddress=").append(ipAddress).append(", ");
        sb.append("subnetMask=").append(subnetMask).append(", ");
        sb.append("defaultGateway=").append(defaultGateway).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("enabled=").append(enabled).append(", ");
        sb.append("associated=").append(associated);
        sb.append("]");
        return sb.toString();
    }

}
