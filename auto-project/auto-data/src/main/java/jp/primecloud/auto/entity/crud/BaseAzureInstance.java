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
 * AZURE_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAzureInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** INSTANCE_NAME [VARCHAR(30,0)] */
    private String instanceName;

    /** AFFINITY_GROUP_NAME [VARCHAR(100,0)] */
    private String affinityGroupName;

    /** CLOUD_SERVICE_NAME [VARCHAR(100,0)] */
    private String cloudServiceName;

    /** STORAGE_ACCOUNT_NAME [VARCHAR(100,0)] */
    private String storageAccountName;

    /** NETWORK_NAME [VARCHAR(100,0)] */
    private String networkName;

    /** INSTANCE_TYPE [VARCHAR(20,0)] */
    private String instanceType;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

    /** SUBNET_ID [VARCHAR(30,0)] */
    private String subnetId;

    /** PRIVATE_IP_ADDRESS [VARCHAR(100,0)] */
    private String privateIpAddress;

    /** LOCATION_NAME [VARCHAR(100,0)] */
    private String locationName;

    /** AVAILABILITY_SET [VARCHAR(100,0)] */
    private String availabilitySet;

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
     * instanceNameを取得します。
     *
     * @return instanceName
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * instanceNameを設定します。
     *
     * @param instanceName instanceName
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * affinityGroupNameを取得します。
     *
     * @return affinityGroupName
     */
    public String getAffinityGroupName() {
        return affinityGroupName;
    }

    /**
     * affinityGroupNameを設定します。
     *
     * @param affinityGroupName affinityGroupName
     */
    public void setAffinityGroupName(String affinityGroupName) {
        this.affinityGroupName = affinityGroupName;
    }

    /**
     * cloudServiceNameを取得します。
     *
     * @return cloudServiceName
     */
    public String getCloudServiceName() {
        return cloudServiceName;
    }

    /**
     * cloudServiceNameを設定します。
     *
     * @param cloudServiceName cloudServiceName
     */
    public void setCloudServiceName(String cloudServiceName) {
        this.cloudServiceName = cloudServiceName;
    }

    /**
     * storageAccountNameを取得します。
     *
     * @return storageAccountName
     */
    public String getStorageAccountName() {
        return storageAccountName;
    }

    /**
     * storageAccountNameを設定します。
     *
     * @param storageAccountName storageAccountName
     */
    public void setStorageAccountName(String storageAccountName) {
        this.storageAccountName = storageAccountName;
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
     * locationNameを取得します。
     *
     * @return locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * locationNameを設定します。
     *
     * @param locationName locationName
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * availabilitySetを取得します。
     *
     * @return availabilitySet
     */
    public String getAvailabilitySet() {
        return availabilitySet;
    }

    /**
     * availabilitySetを設定します。
     *
     * @param availabilitySet availabilitySet
     */
    public void setAvailabilitySet(String availabilitySet) {
        this.availabilitySet = availabilitySet;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
        result = prime * result + ((affinityGroupName == null) ? 0 : affinityGroupName.hashCode());
        result = prime * result + ((cloudServiceName == null) ? 0 : cloudServiceName.hashCode());
        result = prime * result + ((storageAccountName == null) ? 0 : storageAccountName.hashCode());
        result = prime * result + ((networkName == null) ? 0 : networkName.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((subnetId == null) ? 0 : subnetId.hashCode());
        result = prime * result + ((privateIpAddress == null) ? 0 : privateIpAddress.hashCode());
        result = prime * result + ((locationName == null) ? 0 : locationName.hashCode());
        result = prime * result + ((availabilitySet == null) ? 0 : availabilitySet.hashCode());

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

        final BaseAzureInstance other = (BaseAzureInstance) obj;
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (instanceName == null) {
            if (other.instanceName != null) { return false; }
        } else if (!instanceName.equals(other.instanceName)) {
            return false;
        }
        if (affinityGroupName == null) {
            if (other.affinityGroupName != null) { return false; }
        } else if (!affinityGroupName.equals(other.affinityGroupName)) {
            return false;
        }
        if (cloudServiceName == null) {
            if (other.cloudServiceName != null) { return false; }
        } else if (!cloudServiceName.equals(other.cloudServiceName)) {
            return false;
        }
        if (storageAccountName == null) {
            if (other.storageAccountName != null) { return false; }
        } else if (!storageAccountName.equals(other.storageAccountName)) {
            return false;
        }
        if (networkName == null) {
            if (other.networkName != null) { return false; }
        } else if (!networkName.equals(other.networkName)) {
            return false;
        }
        if (instanceType == null) {
            if (other.instanceType != null) { return false; }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (subnetId == null) {
            if (other.subnetId != null) { return false; }
        } else if (!subnetId.equals(other.subnetId)) {
            return false;
        }
        if (privateIpAddress == null) {
            if (other.privateIpAddress != null) { return false; }
        } else if (!privateIpAddress.equals(other.privateIpAddress)) {
            return false;
        }
        if (locationName == null) {
            if (other.locationName != null) { return false; }
        } else if (!locationName.equals(other.locationName)) {
            return false;
        }
        if (availabilitySet == null) {
            if (other.availabilitySet != null) { return false; }
        } else if (!availabilitySet.equals(other.availabilitySet)) {
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
        sb.append("AzureInstance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("instanceName=").append(instanceName).append(", ");
        sb.append("affinityGroupName=").append(affinityGroupName).append(", ");
        sb.append("cloudServiceName=").append(cloudServiceName).append(", ");
        sb.append("storageAccountName=").append(storageAccountName).append(", ");
        sb.append("networkName=").append(networkName).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("subnetId=").append(subnetId).append(", ");
        sb.append("privateIpAddress=").append(privateIpAddress).append(", ");
        sb.append("locationName=").append(locationName).append(", ");
        sb.append("availabilitySet=").append(availabilitySet);
        sb.append("]");
        return sb.toString();
    }

}
