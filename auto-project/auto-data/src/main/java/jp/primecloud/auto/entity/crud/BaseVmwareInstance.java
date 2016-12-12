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
 * VMWARE_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVmwareInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** MACHINE_NAME [VARCHAR(100,0)] */
    private String machineName;

    /** INSTANCE_TYPE [VARCHAR(30,0)] */
    private String instanceType;

    /** COMPUTE_RESOURCE [VARCHAR(100,0)] */
    private String computeResource;

    /** RESOURCE_POOL [VARCHAR(100,0)] */
    private String resourcePool;

    /** DATASTORE [VARCHAR(100,0)] */
    private String datastore;

    /** KEY_PAIR_NO [BIGINT(19,0)] */
    private Long keyPairNo;

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
     * machineNameを取得します。
     *
     * @return machineName
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * machineNameを設定します。
     *
     * @param machineName machineName
     */
    public void setMachineName(String machineName) {
        this.machineName = machineName;
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
     * computeResourceを取得します。
     *
     * @return computeResource
     */
    public String getComputeResource() {
        return computeResource;
    }

    /**
     * computeResourceを設定します。
     *
     * @param computeResource computeResource
     */
    public void setComputeResource(String computeResource) {
        this.computeResource = computeResource;
    }

    /**
     * resourcePoolを取得します。
     *
     * @return resourcePool
     */
    public String getResourcePool() {
        return resourcePool;
    }

    /**
     * resourcePoolを設定します。
     *
     * @param resourcePool resourcePool
     */
    public void setResourcePool(String resourcePool) {
        this.resourcePool = resourcePool;
    }

    /**
     * datastoreを取得します。
     *
     * @return datastore
     */
    public String getDatastore() {
        return datastore;
    }

    /**
     * datastoreを設定します。
     *
     * @param datastore datastore
     */
    public void setDatastore(String datastore) {
        this.datastore = datastore;
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
        result = prime * result + ((machineName == null) ? 0 : machineName.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((computeResource == null) ? 0 : computeResource.hashCode());
        result = prime * result + ((resourcePool == null) ? 0 : resourcePool.hashCode());
        result = prime * result + ((datastore == null) ? 0 : datastore.hashCode());
        result = prime * result + ((keyPairNo == null) ? 0 : keyPairNo.hashCode());
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

        final BaseVmwareInstance other = (BaseVmwareInstance) obj;
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (machineName == null) {
            if (other.machineName != null) { return false; }
        } else if (!machineName.equals(other.machineName)) {
            return false;
        }
        if (instanceType == null) {
            if (other.instanceType != null) { return false; }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (computeResource == null) {
            if (other.computeResource != null) { return false; }
        } else if (!computeResource.equals(other.computeResource)) {
            return false;
        }
        if (resourcePool == null) {
            if (other.resourcePool != null) { return false; }
        } else if (!resourcePool.equals(other.resourcePool)) {
            return false;
        }
        if (datastore == null) {
            if (other.datastore != null) { return false; }
        } else if (!datastore.equals(other.datastore)) {
            return false;
        }
        if (keyPairNo == null) {
            if (other.keyPairNo != null) { return false; }
        } else if (!keyPairNo.equals(other.keyPairNo)) {
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
        sb.append("VmwareInstance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("machineName=").append(machineName).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("computeResource=").append(computeResource).append(", ");
        sb.append("resourcePool=").append(resourcePool).append(", ");
        sb.append("datastore=").append(datastore).append(", ");
        sb.append("keyPairNo=").append(keyPairNo).append(", ");
        sb.append("ipAddress=").append(ipAddress).append(", ");
        sb.append("privateIpAddress=").append(privateIpAddress);
        sb.append("]");
        return sb.toString();
    }

}
