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
 * VCLOUD_DISKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVcloudDisk implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** DISK_NO [BIGINT(19,0)] */
    private Long diskNo;

    /** DISK_ID [VARCHAR(20,0)] */
    private String diskId;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** SIZE [INT(10,0)] */
    private Integer size;

    /** UNIT_NO [INT(10,0)] */
    private Integer unitNo;

    /** ATTACHED [BIT(0,0)] */
    private Boolean attached;

    /** DATA_DISK [BIT(0,0)] */
    private Boolean dataDisk;

    /**
     * diskNoを取得します。
     *
     * @return diskNo
     */
    public Long getDiskNo() {
        return diskNo;
    }

    /**
     * diskNoを設定します。
     *
     * @param diskNo diskNo
     */
    public void setDiskNo(Long diskNo) {
        this.diskNo = diskNo;
    }

    /**
     * diskIdを取得します。
     *
     * @return diskId
     */
    public String getDiskId() {
        return diskId;
    }

    /**
     * diskIdを設定します。
     *
     * @param diskId diskId
     */
    public void setDiskId(String diskId) {
        this.diskId = diskId;
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
     * unitNoを取得します。
     *
     * @return unitNo
     */
    public Integer getUnitNo() {
        return unitNo;
    }

    /**
     * unitNoを設定します。
     *
     * @param unitNo unitNo
     */
    public void setUnitNo(Integer unitNo) {
        this.unitNo = unitNo;
    }

    /**
     * attachedを取得します。
     *
     * @return attached
     */
    public Boolean getAttached() {
        return attached;
    }

    /**
     * attachedを設定します。
     *
     * @param attached attached
     */
    public void setAttached(Boolean attached) {
        this.attached = attached;
    }

    /**
     * dataDiskを取得します。
     *
     * @return dataDisk
     */
    public Boolean getDataDisk() {
        return dataDisk;
    }

    /**
     * dataDiskを設定します。
     *
     * @param dataDisk dataDisk
     */
    public void setDataDisk(Boolean dataDisk) {
        this.dataDisk = dataDisk;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((diskNo == null) ? 0 : diskNo.hashCode());
        result = prime * result + ((diskId == null) ? 0 : diskId.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        result = prime * result + ((unitNo == null) ? 0 : unitNo.hashCode());
        result = prime * result + ((attached == null) ? 0 : attached.hashCode());
        result = prime * result + ((dataDisk == null) ? 0 : dataDisk.hashCode());

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

        final BaseVcloudDisk other = (BaseVcloudDisk) obj;
        if (diskNo == null) {
            if (other.diskNo != null) { return false; }
        } else if (!diskNo.equals(other.diskNo)) {
            return false;
        }
        if (diskId == null) {
            if (other.diskId != null) { return false; }
        } else if (!diskId.equals(other.diskId)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
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
        if (unitNo == null) {
            if (other.unitNo != null) { return false; }
        } else if (!unitNo.equals(other.unitNo)) {
            return false;
        }
        if (attached == null) {
            if (other.attached != null) { return false; }
        } else if (!attached.equals(other.attached)) {
            return false;
        }
        if (dataDisk == null) {
            if (other.dataDisk != null) { return false; }
        } else if (!dataDisk.equals(other.dataDisk)) {
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
        sb.append("VcloudDisk").append(" [");
        sb.append("diskNo=").append(diskNo).append(", ");
        sb.append("diskId=").append(diskId).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("size=").append(size).append(", ");
        sb.append("unitNo=").append(unitNo).append(", ");
        sb.append("attached=").append(attached).append(", ");
        sb.append("dataDisk=").append(dataDisk);
        sb.append("]");
        return sb.toString();
    }

}
