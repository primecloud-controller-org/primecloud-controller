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
 * AZURE_DISKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAzureDisk implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** DISK_NO [BIGINT(19,0)] */
    private Long diskNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** DISK_NAME [VARCHAR(100,0)] */
    private String diskName;

    /** INSTANCE_NAME [VARCHAR(30,0)] */
    private String instanceName;

    /** LUN [BIGINT(19,0)] */
    private Long lun;

    /** SIZE [INT(10,0)] */
    private Integer size;

    /** DEVICE [VARCHAR(20,0)] */
    private String device;

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
     * diskNameを取得します。
     *
     * @return diskName
     */
    public String getDiskName() {
        return diskName;
    }

    /**
     * diskNameを設定します。
     *
     * @param diskName diskName
     */
    public void setDiskName(String diskName) {
        this.diskName = diskName;
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
     * lunを取得します。
     *
     * @return lun
     */
    public Long getLun() {
        return lun;
    }

    /**
     * lunを設定します。
     *
     * @param lun lun
     */
    public void setLun(Long lun) {
        this.lun = lun;
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
     * deviceを取得します。
     *
     * @return device
     */
    public String getDevice() {
        return device;
    }

    /**
     * deviceを設定します。
     *
     * @param device device
     */
    public void setDevice(String device) {
        this.device = device;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((diskNo == null) ? 0 : diskNo.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((diskName == null) ? 0 : diskName.hashCode());
        result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
        result = prime * result + ((lun == null) ? 0 : lun.hashCode());
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        result = prime * result + ((device == null) ? 0 : device.hashCode());

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

        final BaseAzureDisk other = (BaseAzureDisk) obj;
        if (diskNo == null) {
            if (other.diskNo != null) { return false; }
        } else if (!diskNo.equals(other.diskNo)) {
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
        if (diskName == null) {
            if (other.diskName != null) { return false; }
        } else if (!diskName.equals(other.diskName)) {
            return false;
        }
        if (instanceName == null) {
            if (other.instanceName != null) { return false; }
        } else if (!instanceName.equals(other.instanceName)) {
            return false;
        }
        if (lun == null) {
            if (other.lun != null) { return false; }
        } else if (!lun.equals(other.lun)) {
            return false;
        }
        if (size == null) {
            if (other.size != null) { return false; }
        } else if (!size.equals(other.size)) {
            return false;
        }
        if (device == null) {
            if (other.device != null) { return false; }
        } else if (!device.equals(other.device)) {
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
        sb.append("AzureDisk").append(" [");
        sb.append("diskNo=").append(diskNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("diskName=").append(diskName).append(", ");
        sb.append("instanceName=").append(instanceName).append(", ");
        sb.append("lun=").append(lun).append(", ");
        sb.append("size=").append(size).append(", ");
        sb.append("device=").append(device);
        sb.append("]");
        return sb.toString();
    }

}
