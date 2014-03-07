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
 * CLOUDSTACK_VOLUMEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseCloudstackVolume implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** VOLUME_NO [BIGINT(19,0)] */
    private Long volumeNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** VOLUME_ID [VARCHAR(20,0)] */
    private String volumeId;

    /** DEVICEID [VARCHAR(20,0)] */
    private String deviceid;

    /** DISKOFFERINGID [VARCHAR(20,0)] */
    private String diskofferingid;

    /** NAME [VARCHAR(100,0)] */
    private String name;

    /** SIZE [INT(10,0)] */
    private Integer size;

    /** SNAPSHOTID [VARCHAR(20,0)] */
    private String snapshotid;

    /** STATE [VARCHAR(20,0)] */
    private String state;

    /** INSTANCE_ID [VARCHAR(20,0)] */
    private String instanceId;

    /** ZONEID [VARCHAR(100,0)] */
    private String zoneid;

    /** HYPERVISOR [VARCHAR(100,0)] */
    private String hypervisor;

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
     * deviceidを取得します。
     *
     * @return deviceid
     */
    public String getDeviceid() {
        return deviceid;
    }

    /**
     * deviceidを設定します。
     *
     * @param deviceid deviceid
     */
    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    /**
     * diskofferingidを取得します。
     *
     * @return diskofferingid
     */
    public String getDiskofferingid() {
        return diskofferingid;
    }

    /**
     * diskofferingidを設定します。
     *
     * @param diskofferingid diskofferingid
     */
    public void setDiskofferingid(String diskofferingid) {
        this.diskofferingid = diskofferingid;
    }

    /**
     * nameを取得します。
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します。
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
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
     * snapshotidを取得します。
     *
     * @return snapshotid
     */
    public String getSnapshotid() {
        return snapshotid;
    }

    /**
     * snapshotidを設定します。
     *
     * @param snapshotid snapshotid
     */
    public void setSnapshotid(String snapshotid) {
        this.snapshotid = snapshotid;
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
     * hypervisorを取得します。
     *
     * @return hypervisor
     */
    public String getHypervisor() {
        return hypervisor;
    }

    /**
     * hypervisorを設定します。
     *
     * @param hypervisor hypervisor
     */
    public void setHypervisor(String hypervisor) {
        this.hypervisor = hypervisor;
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
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((volumeId == null) ? 0 : volumeId.hashCode());
        result = prime * result + ((deviceid == null) ? 0 : deviceid.hashCode());
        result = prime * result + ((diskofferingid == null) ? 0 : diskofferingid.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        result = prime * result + ((snapshotid == null) ? 0 : snapshotid.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
        result = prime * result + ((zoneid == null) ? 0 : zoneid.hashCode());
        result = prime * result + ((hypervisor == null) ? 0 : hypervisor.hashCode());

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

        final BaseCloudstackVolume other = (BaseCloudstackVolume) obj;
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
        if (volumeId == null) {
            if (other.volumeId != null) { return false; }
        } else if (!volumeId.equals(other.volumeId)) {
            return false;
        }
        if (deviceid == null) {
            if (other.deviceid != null) { return false; }
        } else if (!deviceid.equals(other.deviceid)) {
            return false;
        }
        if (diskofferingid == null) {
            if (other.diskofferingid != null) { return false; }
        } else if (!diskofferingid.equals(other.diskofferingid)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) { return false; }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (size == null) {
            if (other.size != null) { return false; }
        } else if (!size.equals(other.size)) {
            return false;
        }
        if (snapshotid == null) {
            if (other.snapshotid != null) { return false; }
        } else if (!snapshotid.equals(other.snapshotid)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) { return false; }
        } else if (!state.equals(other.state)) {
            return false;
        }
        if (instanceId == null) {
            if (other.instanceId != null) { return false; }
        } else if (!instanceId.equals(other.instanceId)) {
            return false;
        }
        if (zoneid == null) {
            if (other.zoneid != null) { return false; }
        } else if (!zoneid.equals(other.zoneid)) {
            return false;
        }
        if (hypervisor == null) {
            if (other.hypervisor != null) { return false; }
        } else if (!hypervisor.equals(other.hypervisor)) {
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
        sb.append("CloudstackVolume").append(" [");
        sb.append("volumeNo=").append(volumeNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("volumeId=").append(volumeId).append(", ");
        sb.append("deviceid=").append(deviceid).append(", ");
        sb.append("diskofferingid=").append(diskofferingid).append(", ");
        sb.append("name=").append(name).append(", ");
        sb.append("size=").append(size).append(", ");
        sb.append("snapshotid=").append(snapshotid).append(", ");
        sb.append("state=").append(state).append(", ");
        sb.append("instanceId=").append(instanceId).append(", ");
        sb.append("zoneid=").append(zoneid).append(", ");
        sb.append("hypervisor=").append(hypervisor);
        sb.append("]");
        return sb.toString();
    }

}
