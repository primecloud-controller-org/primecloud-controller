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
import java.util.Date;

/**
 * <p>
 * AWS_SNAPSHOTに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAwsSnapshot implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** SNAPSHOT_NO [BIGINT(19,0)] */
    private Long snapshotNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** VOLUME_NO [BIGINT(19,0)] */
    private Long volumeNo;

    /** SNAPSHOT_ID [VARCHAR(20,0)] */
    private String snapshotId;

    /** CREATE_DATE [DATETIME(0,0)] */
    private Date createDate;

    /**
     * snapshotNoを取得します。
     *
     * @return snapshotNo
     */
    public Long getSnapshotNo() {
        return snapshotNo;
    }

    /**
     * snapshotNoを設定します。
     *
     * @param snapshotNo snapshotNo
     */
    public void setSnapshotNo(Long snapshotNo) {
        this.snapshotNo = snapshotNo;
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
     * snapshotIdを取得します。
     *
     * @return snapshotId
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    /**
     * snapshotIdを設定します。
     *
     * @param snapshotId snapshotId
     */
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * createDateを取得します。
     *
     * @return createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * createDateを設定します。
     *
     * @param createDate createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((snapshotNo == null) ? 0 : snapshotNo.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((volumeNo == null) ? 0 : volumeNo.hashCode());
        result = prime * result + ((snapshotId == null) ? 0 : snapshotId.hashCode());
        result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());

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

        final BaseAwsSnapshot other = (BaseAwsSnapshot) obj;
        if (snapshotNo == null) {
            if (other.snapshotNo != null) { return false; }
        } else if (!snapshotNo.equals(other.snapshotNo)) {
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
        if (volumeNo == null) {
            if (other.volumeNo != null) { return false; }
        } else if (!volumeNo.equals(other.volumeNo)) {
            return false;
        }
        if (snapshotId == null) {
            if (other.snapshotId != null) { return false; }
        } else if (!snapshotId.equals(other.snapshotId)) {
            return false;
        }
        if (createDate == null) {
            if (other.createDate != null) { return false; }
        } else if (!createDate.equals(other.createDate)) {
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
        sb.append("AwsSnapshot").append(" [");
        sb.append("snapshotNo=").append(snapshotNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("volumeNo=").append(volumeNo).append(", ");
        sb.append("snapshotId=").append(snapshotId).append(", ");
        sb.append("createDate=").append(createDate);
        sb.append("]");
        return sb.toString();
    }

}
