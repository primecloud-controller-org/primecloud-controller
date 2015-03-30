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
 * NIFTY_VOLUMEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseNiftyVolume implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** VOLUME_NO [BIGINT(19,0)] */
    private Long volumeNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** VOLUME_NAME [VARCHAR(100,0)] */
    private String volumeName;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** SIZE [INT(10,0)] */
    private Integer size;

    /** VOLUME_ID [VARCHAR(20,0)] */
    private String volumeId;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

    /** INSTANCE_ID [VARCHAR(20,0)] */
    private String instanceId;

    /** SCSI_ID [INT(10,0)] */
    private Integer scsiId;

    /**
     * volumeNoを取得します。
     *
     * @return volumeNo
     */
    public Long getVolumeNo() {
        return volumeNo;
    }

    /**
     * volumeNoを設定します。
     *
     * @param volumeNo volumeNo
     */
    public void setVolumeNo(Long volumeNo) {
        this.volumeNo = volumeNo;
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
     * volumeNameを取得します。
     *
     * @return volumeName
     */
    public String getVolumeName() {
        return volumeName;
    }

    /**
     * volumeNameを設定します。
     *
     * @param volumeName volumeName
     */
    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
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
     * componentNoを取得します。
     *
     * @return componentNo
     */
    public Long getComponentNo() {
        return componentNo;
    }

    /**
     * componentNoを設定します。
     *
     * @param componentNo componentNo
     */
    public void setComponentNo(Long componentNo) {
        this.componentNo = componentNo;
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
     * sizeを取得します。
     *
     * @return size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * sizeを設定します。
     *
     * @param size size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * volumeIdを取得します。
     *
     * @return volumeId
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * volumeIdを設定します。
     *
     * @param volumeId volumeId
     */
    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
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
     * scsiIdを取得します。
     *
     * @return scsiId
     */
    public Integer getScsiId() {
        return scsiId;
    }

    /**
     * scsiIdを設定します。
     *
     * @param scsiId scsiId
     */
    public void setScsiId(Integer scsiId) {
        this.scsiId = scsiId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((volumeNo == null) ? 0 : volumeNo.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((volumeName == null) ? 0 : volumeName.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        result = prime * result + ((volumeId == null) ? 0 : volumeId.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
        result = prime * result + ((scsiId == null) ? 0 : scsiId.hashCode());

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

        final BaseNiftyVolume other = (BaseNiftyVolume) obj;
        if (volumeNo == null) {
            if (other.volumeNo != null) { return false; }
        } else if (!volumeNo.equals(other.volumeNo)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (volumeName == null) {
            if (other.volumeName != null) { return false; }
        } else if (!volumeName.equals(other.volumeName)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (componentNo == null) {
            if (other.componentNo != null) { return false; }
        } else if (!componentNo.equals(other.componentNo)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (size == null) {
            if (other.size != null) { return false; }
        } else if (!size.equals(other.size)) {
            return false;
        }
        if (volumeId == null) {
            if (other.volumeId != null) { return false; }
        } else if (!volumeId.equals(other.volumeId)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (instanceId == null) {
            if (other.instanceId != null) { return false; }
        } else if (!instanceId.equals(other.instanceId)) {
            return false;
        }
        if (scsiId == null) {
            if (other.scsiId != null) { return false; }
        } else if (!scsiId.equals(other.scsiId)) {
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
        sb.append("NiftyVolume").append(" [");
        sb.append("volumeNo=").append(volumeNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("volumeName=").append(volumeName).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("size=").append(size).append(", ");
        sb.append("volumeId=").append(volumeId).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("instanceId=").append(instanceId).append(", ");
        sb.append("scsiId=").append(scsiId);
        sb.append("]");
        return sb.toString();
    }

}
