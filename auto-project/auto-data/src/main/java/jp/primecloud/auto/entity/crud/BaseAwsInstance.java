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
 * AWS_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAwsInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** KEY_NAME [VARCHAR(100,0)] */
    private String keyName;

    /** INSTANCE_TYPE [VARCHAR(20,0)] */
    private String instanceType;

    /** SECURITY_GROUPS [VARCHAR(100,0)] */
    private String securityGroups;

    /** AVAILABILITY_ZONE [VARCHAR(100,0)] */
    private String availabilityZone;

    /** SUBNET_ID [VARCHAR(30,0)] */
    private String subnetId;

    /** INSTANCE_ID [VARCHAR(20,0)] */
    private String instanceId;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

    /** DNS_NAME [VARCHAR(100,0)] */
    private String dnsName;

    /** PRIVATE_DNS_NAME [VARCHAR(100,0)] */
    private String privateDnsName;

    /** IP_ADDRESS [VARCHAR(100,0)] */
    private String ipAddress;

    /** PRIVATE_IP_ADDRESS [VARCHAR(100,0)] */
    private String privateIpAddress;

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
     * securityGroupsを取得します。
     *
     * @return securityGroups
     */
    public String getSecurityGroups() {
        return securityGroups;
    }

    /**
     * securityGroupsを設定します。
     *
     * @param securityGroups securityGroups
     */
    public void setSecurityGroups(String securityGroups) {
        this.securityGroups = securityGroups;
    }

    /**
     * availabilityZoneを取得します。
     *
     * @return availabilityZone
     */
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * availabilityZoneを設定します。
     *
     * @param availabilityZone availabilityZone
     */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
     * subnetIdを取得します。
     *
     * @return subnetId
     */
    public String getSubnetId() {
        return subnetId;
    }

    /**
     * subnetIdを設定します。
     *
     * @param subnetId subnetId
     */
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
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
     * statusを取得します。
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     *
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * dnsNameを取得します。
     *
     * @return dnsName
     */
    public String getDnsName() {
        return dnsName;
    }

    /**
     * dnsNameを設定します。
     *
     * @param dnsName dnsName
     */
    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * privateDnsNameを取得します。
     *
     * @return privateDnsName
     */
    public String getPrivateDnsName() {
        return privateDnsName;
    }

    /**
     * privateDnsNameを設定します。
     *
     * @param privateDnsName privateDnsName
     */
    public void setPrivateDnsName(String privateDnsName) {
        this.privateDnsName = privateDnsName;
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
     * privateIpAddressを取得します。
     *
     * @return privateIpAddress
     */
    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    /**
     * privateIpAddressを設定します。
     *
     * @param privateIpAddress privateIpAddress
     */
    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
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
        result = prime * result + ((securityGroups == null) ? 0 : securityGroups.hashCode());
        result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());
        result = prime * result + ((subnetId == null) ? 0 : subnetId.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((dnsName == null) ? 0 : dnsName.hashCode());
        result = prime * result + ((privateDnsName == null) ? 0 : privateDnsName.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((privateIpAddress == null) ? 0 : privateIpAddress.hashCode());

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

        final BaseAwsInstance other = (BaseAwsInstance) obj;
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
        if (securityGroups == null) {
            if (other.securityGroups != null) { return false; }
        } else if (!securityGroups.equals(other.securityGroups)) {
            return false;
        }
        if (availabilityZone == null) {
            if (other.availabilityZone != null) { return false; }
        } else if (!availabilityZone.equals(other.availabilityZone)) {
            return false;
        }
        if (subnetId == null) {
            if (other.subnetId != null) { return false; }
        } else if (!subnetId.equals(other.subnetId)) {
            return false;
        }
        if (instanceId == null) {
            if (other.instanceId != null) { return false; }
        } else if (!instanceId.equals(other.instanceId)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (dnsName == null) {
            if (other.dnsName != null) { return false; }
        } else if (!dnsName.equals(other.dnsName)) {
            return false;
        }
        if (privateDnsName == null) {
            if (other.privateDnsName != null) { return false; }
        } else if (!privateDnsName.equals(other.privateDnsName)) {
            return false;
        }
        if (ipAddress == null) {
            if (other.ipAddress != null) { return false; }
        } else if (!ipAddress.equals(other.ipAddress)) {
            return false;
        }
        if (privateIpAddress == null) {
            if (other.privateIpAddress != null) { return false; }
        } else if (!privateIpAddress.equals(other.privateIpAddress)) {
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
        sb.append("AwsInstance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("keyName=").append(keyName).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("securityGroups=").append(securityGroups).append(", ");
        sb.append("availabilityZone=").append(availabilityZone).append(", ");
        sb.append("subnetId=").append(subnetId).append(", ");
        sb.append("instanceId=").append(instanceId).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("dnsName=").append(dnsName).append(", ");
        sb.append("privateDnsName=").append(privateDnsName).append(", ");
        sb.append("ipAddress=").append(ipAddress).append(", ");
        sb.append("privateIpAddress=").append(privateIpAddress);
        sb.append("]");
        return sb.toString();
    }

}
