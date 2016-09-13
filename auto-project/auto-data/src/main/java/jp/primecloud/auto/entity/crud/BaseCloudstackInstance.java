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
 * CLOUDSTACK_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseCloudstackInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** KEY_NAME [VARCHAR(100,0)] */
    private String keyName;

    /** INSTANCE_TYPE [VARCHAR(20,0)] */
    private String instanceType;

    /** INSTANCE_ID [VARCHAR(20,0)] */
    private String instanceId;

    /** DISPLAYNAME [VARCHAR(100,0)] */
    private String displayname;

    /** IPADDRESS [VARCHAR(100,0)] */
    private String ipaddress;

    /** STATE [VARCHAR(20,0)] */
    private String state;

    /** ZONEID [VARCHAR(100,0)] */
    private String zoneid;

    /** NETWORKID [VARCHAR(20,0)] */
    private String networkid;

    /** SECURITYGROUP [VARCHAR(100,0)] */
    private String securitygroup;

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
     * keyNameを取得します。
     *
     * @return keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * keyNameを設定します。
     *
     * @param keyName keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * instanceTypeを取得します。
     *
     * @return instanceType
     */
    public String getInstanceType() {
        return instanceType;
    }

    /**
     * instanceTypeを設定します。
     *
     * @param instanceType instanceType
     */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
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
     * displaynameを取得します。
     *
     * @return displayname
     */
    public String getDisplayname() {
        return displayname;
    }

    /**
     * displaynameを設定します。
     *
     * @param displayname displayname
     */
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
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
     * securitygroupを取得します。
     *
     * @return securitygroup
     */
    public String getSecuritygroup() {
        return securitygroup;
    }

    /**
     * securitygroupを設定します。
     *
     * @param securitygroup securitygroup
     */
    public void setSecuritygroup(String securitygroup) {
        this.securitygroup = securitygroup;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
        result = prime * result + ((displayname == null) ? 0 : displayname.hashCode());
        result = prime * result + ((ipaddress == null) ? 0 : ipaddress.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((zoneid == null) ? 0 : zoneid.hashCode());
        result = prime * result + ((networkid == null) ? 0 : networkid.hashCode());
        result = prime * result + ((securitygroup == null) ? 0 : securitygroup.hashCode());

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

        final BaseCloudstackInstance other = (BaseCloudstackInstance) obj;
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (keyName == null) {
            if (other.keyName != null) { return false; }
        } else if (!keyName.equals(other.keyName)) {
            return false;
        }
        if (instanceType == null) {
            if (other.instanceType != null) { return false; }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (instanceId == null) {
            if (other.instanceId != null) { return false; }
        } else if (!instanceId.equals(other.instanceId)) {
            return false;
        }
        if (displayname == null) {
            if (other.displayname != null) { return false; }
        } else if (!displayname.equals(other.displayname)) {
            return false;
        }
        if (ipaddress == null) {
            if (other.ipaddress != null) { return false; }
        } else if (!ipaddress.equals(other.ipaddress)) {
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
        if (networkid == null) {
            if (other.networkid != null) { return false; }
        } else if (!networkid.equals(other.networkid)) {
            return false;
        }
        if (securitygroup == null) {
            if (other.securitygroup != null) { return false; }
        } else if (!securitygroup.equals(other.securitygroup)) {
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
        sb.append("CloudstackInstance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("keyName=").append(keyName).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("instanceId=").append(instanceId).append(", ");
        sb.append("displayname=").append(displayname).append(", ");
        sb.append("ipaddress=").append(ipaddress).append(", ");
        sb.append("state=").append(state).append(", ");
        sb.append("zoneid=").append(zoneid).append(", ");
        sb.append("networkid=").append(networkid).append(", ");
        sb.append("securitygroup=").append(securitygroup);
        sb.append("]");
        return sb.toString();
    }

}
