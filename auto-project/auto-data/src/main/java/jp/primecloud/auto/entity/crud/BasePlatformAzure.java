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
 * PLATFORM_AZUREに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformAzure implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** LOCATION_NAME [VARCHAR(100,0)] */
    private String locationName;

    /** AFFINITY_GROUP_NAME [VARCHAR(100,0)] */
    private String affinityGroupName;

    /** CLOUD_SERVICE_NAME [VARCHAR(100,0)] */
    private String cloudServiceName;

    /** STORAGE_ACCOUNT_NAME [VARCHAR(100,0)] */
    private String storageAccountName;

    /** NETWORK_NAME [VARCHAR(100,0)] */
    private String networkName;

    /** AVAILABILITY_SETS [VARCHAR(500,0)] */
    private String availabilitySets;

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
     * availabilitySetsを取得します。
     *
     * @return availabilitySets
     */
    public String getAvailabilitySets() {
        return availabilitySets;
    }

    /**
     * availabilitySetsを設定します。
     *
     * @param availabilitySets availabilitySets
     */
    public void setAvailabilitySets(String availabilitySets) {
        this.availabilitySets = availabilitySets;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((locationName == null) ? 0 : locationName.hashCode());
        result = prime * result + ((affinityGroupName == null) ? 0 : affinityGroupName.hashCode());
        result = prime * result + ((cloudServiceName == null) ? 0 : cloudServiceName.hashCode());
        result = prime * result + ((storageAccountName == null) ? 0 : storageAccountName.hashCode());
        result = prime * result + ((networkName == null) ? 0 : networkName.hashCode());
        result = prime * result + ((availabilitySets == null) ? 0 : availabilitySets.hashCode());

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

        final BasePlatformAzure other = (BasePlatformAzure) obj;
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (locationName == null) {
            if (other.locationName != null) { return false; }
        } else if (!locationName.equals(other.locationName)) {
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
        if (availabilitySets == null) {
            if (other.availabilitySets != null) { return false; }
        } else if (!availabilitySets.equals(other.availabilitySets)) {
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
        sb.append("PlatformAzure").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("locationName=").append(locationName).append(", ");
        sb.append("affinityGroupName=").append(affinityGroupName).append(", ");
        sb.append("cloudServiceName=").append(cloudServiceName).append(", ");
        sb.append("storageAccountName=").append(storageAccountName).append(", ");
        sb.append("networkName=").append(networkName).append(", ");
        sb.append("availabilitySets=").append(availabilitySets);
        sb.append("]");
        return sb.toString();
    }

}
