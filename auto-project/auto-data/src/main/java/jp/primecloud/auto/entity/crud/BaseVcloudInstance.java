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
 * VCLOUD_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVcloudInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** VM_NAME [VARCHAR(100,0)] */
    private String vmName;

    /** STORAGE_TYPE_NO [BIGINT(19,0)] */
    private Long storageTypeNo;

    /** INSTANCE_TYPE [VARCHAR(30,0)] */
    private String instanceType;

    /** KEY_PAIR_NO [BIGINT(19,0)] */
    private Long keyPairNo;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

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
     * vmNameを取得します。
     *
     * @return vmName
     */
    public String getVmName() {
        return vmName;
    }

    /**
     * vmNameを設定します。
     *
     * @param vmName vmName
     */
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    /**
     * storageTypeNoを取得します。
     *
     * @return storageTypeNo
     */
    public Long getStorageTypeNo() {
        return storageTypeNo;
    }

    /**
     * storageTypeNoを設定します。
     *
     * @param storageTypeNo storageTypeNo
     */
    public void setStorageTypeNo(Long storageTypeNo) {
        this.storageTypeNo = storageTypeNo;
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
        result = prime * result + ((vmName == null) ? 0 : vmName.hashCode());
        result = prime * result + ((storageTypeNo == null) ? 0 : storageTypeNo.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((keyPairNo == null) ? 0 : keyPairNo.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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

        final BaseVcloudInstance other = (BaseVcloudInstance) obj;
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (vmName == null) {
            if (other.vmName != null) { return false; }
        } else if (!vmName.equals(other.vmName)) {
            return false;
        }
        if (storageTypeNo == null) {
            if (other.storageTypeNo != null) { return false; }
        } else if (!storageTypeNo.equals(other.storageTypeNo)) {
            return false;
        }
        if (instanceType == null) {
            if (other.instanceType != null) { return false; }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (keyPairNo == null) {
            if (other.keyPairNo != null) { return false; }
        } else if (!keyPairNo.equals(other.keyPairNo)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) {
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
        sb.append("VcloudInstance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("vmName=").append(vmName).append(", ");
        sb.append("storageTypeNo=").append(storageTypeNo).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("keyPairNo=").append(keyPairNo).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("ipAddress=").append(ipAddress).append(", ");
        sb.append("privateIpAddress=").append(privateIpAddress);
        sb.append("]");
        return sb.toString();
    }

}
