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
 * NIFTY_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseNiftyInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** KEY_PAIR_NO [BIGINT(19,0)] */
    private Long keyPairNo;

    /** INSTANCE_TYPE [VARCHAR(20,0)] */
    private String instanceType;

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

    /** INITIALIZED [BIT(0,0)] */
    private Boolean initialized;

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
     * keyPairNoを取得します。
     *
     * @return keyPairNo
     */
    public Long getKeyPairNo() {
        return keyPairNo;
    }

    /**
     * keyPairNoを設定します。
     *
     * @param keyPairNo keyPairNo
     */
    public void setKeyPairNo(Long keyPairNo) {
        this.keyPairNo = keyPairNo;
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
     * initializedを取得します。
     *
     * @return initialized
     */
    public Boolean getInitialized() {
        return initialized;
    }

    /**
     * initializedを設定します。
     *
     * @param initialized initialized
     */
    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((keyPairNo == null) ? 0 : keyPairNo.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((dnsName == null) ? 0 : dnsName.hashCode());
        result = prime * result + ((privateDnsName == null) ? 0 : privateDnsName.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((privateIpAddress == null) ? 0 : privateIpAddress.hashCode());
        result = prime * result + ((initialized == null) ? 0 : initialized.hashCode());

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

        final BaseNiftyInstance other = (BaseNiftyInstance) obj;
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (keyPairNo == null) {
            if (other.keyPairNo != null) { return false; }
        } else if (!keyPairNo.equals(other.keyPairNo)) {
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
        if (initialized == null) {
            if (other.initialized != null) { return false; }
        } else if (!initialized.equals(other.initialized)) {
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
        sb.append("NiftyInstance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("keyPairNo=").append(keyPairNo).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("instanceId=").append(instanceId).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("dnsName=").append(dnsName).append(", ");
        sb.append("privateDnsName=").append(privateDnsName).append(", ");
        sb.append("ipAddress=").append(ipAddress).append(", ");
        sb.append("privateIpAddress=").append(privateIpAddress).append(", ");
        sb.append("initialized=").append(initialized);
        sb.append("]");
        return sb.toString();
    }

}
