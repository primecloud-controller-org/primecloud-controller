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
 * cloudstack_addressに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseCloudstackAddress implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** ADDRESS_NO [BIGINT(19,0)] */
    private Long addressNo;

    /** ACCOUNT [BIGINT(19,0)] */
    private Long account;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** INSTANCE_ID [VARCHAR(20,0)] */
    private String instanceId;

    /** ADDRESS_ID [VARCHAR(20,0)] */
    private String addressId;

    /** IPADDRESS [VARCHAR(100,0)] */
    private String ipaddress;

    /** NETWORKID [VARCHAR(20,0)] */
    private String networkid;

    /** STATE [VARCHAR(20,0)] */
    private String state;

    /** ZONEID [VARCHAR(100,0)] */
    private String zoneid;

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
     * accountを取得します。
     *
     * @return account
     */
    public Long getAccount() {
        return account;
    }

    /**
     * accountを設定します。
     *
     * @param account account
     */
    public void setAccount(Long account) {
        this.account = account;
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
     * instanceIdを取得します。
     *
     * @return instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * instanceIdを設定します。
     *
     * @param instanceId instanceId
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * addressIdを取得します。
     *
     * @return addressId
     */
    public String getAddressId() {
        return addressId;
    }

    /**
     * addressIdを設定します。
     *
     * @param addressId addressId
     */
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    /**
     * ipaddressを取得します。
     *
     * @return ipaddress
     */
    public String getIpaddress() {
        return ipaddress;
    }

    /**
     * ipaddressを設定します。
     *
     * @param ipaddress ipaddress
     */
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    /**
     * networkidを取得します。
     *
     * @return networkid
     */
    public String getNetworkid() {
        return networkid;
    }

    /**
     * networkidを設定します。
     *
     * @param networkid networkid
     */
    public void setNetworkid(String networkid) {
        this.networkid = networkid;
    }

    /**
     * stateを取得します。
     *
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * stateを設定します。
     *
     * @param state state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * zoneidを取得します。
     *
     * @return zoneid
     */
    public String getZoneid() {
        return zoneid;
    }

    /**
     * zoneidを設定します。
     *
     * @param zoneid zoneid
     */
    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((addressNo == null) ? 0 : addressNo.hashCode());
        result = prime * result + ((account == null) ? 0 : account.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
        result = prime * result + ((addressId == null) ? 0 : addressId.hashCode());
        result = prime * result + ((ipaddress == null) ? 0 : ipaddress.hashCode());
        result = prime * result + ((networkid == null) ? 0 : networkid.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((zoneid == null) ? 0 : zoneid.hashCode());

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

        final BaseCloudstackAddress other = (BaseCloudstackAddress) obj;
        if (addressNo == null) {
            if (other.addressNo != null) { return false; }
        } else if (!addressNo.equals(other.addressNo)) {
            return false;
        }
        if (account == null) {
            if (other.account != null) { return false; }
        } else if (!account.equals(other.account)) {
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
        if (instanceId == null) {
            if (other.instanceId != null) { return false; }
        } else if (!instanceId.equals(other.instanceId)) {
            return false;
        }
        if (addressId == null) {
            if (other.addressId != null) { return false; }
        } else if (!addressId.equals(other.addressId)) {
            return false;
        }
        if (ipaddress == null) {
            if (other.ipaddress != null) { return false; }
        } else if (!ipaddress.equals(other.ipaddress)) {
            return false;
        }
        if (networkid == null) {
            if (other.networkid != null) { return false; }
        } else if (!networkid.equals(other.networkid)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) { return false; }
        } else if (!state.equals(other.state)) {
            return false;
        }
        if (zoneid == null) {
            if (other.zoneid != null) { return false; }
        } else if (!zoneid.equals(other.zoneid)) {
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
        sb.append("CloudstackAddress").append(" [");
        sb.append("addressNo=").append(addressNo).append(", ");
        sb.append("account=").append(account).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("instanceId=").append(instanceId).append(", ");
        sb.append("addressId=").append(addressId).append(", ");
        sb.append("ipaddress=").append(ipaddress).append(", ");
        sb.append("networkid=").append(networkid).append(", ");
        sb.append("state=").append(state).append(", ");
        sb.append("zoneid=").append(zoneid);
        sb.append("]");
        return sb.toString();
    }

}
