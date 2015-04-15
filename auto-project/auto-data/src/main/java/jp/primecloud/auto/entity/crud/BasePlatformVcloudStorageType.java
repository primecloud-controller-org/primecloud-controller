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
 * PLATFORM_VCLOUD_STORAGE_TYPEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformVcloudStorageType implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** STORAGE_TYPE_NO [BIGINT(19,0)] */
    private Long storageTypeNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** STORAGE_TYPE_NAME [VARCHAR(100,0)] */
    private String storageTypeName;

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
     * storageTypeNameを取得します。
     *
     * @return storageTypeName
     */
    public String getStorageTypeName() {
        return storageTypeName;
    }

    /**
     * storageTypeNameを設定します。
     *
     * @param storageTypeName storageTypeName
     */
    public void setStorageTypeName(String storageTypeName) {
        this.storageTypeName = storageTypeName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((storageTypeNo == null) ? 0 : storageTypeNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((storageTypeName == null) ? 0 : storageTypeName.hashCode());

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

        final BasePlatformVcloudStorageType other = (BasePlatformVcloudStorageType) obj;
        if (storageTypeNo == null) {
            if (other.storageTypeNo != null) { return false; }
        } else if (!storageTypeNo.equals(other.storageTypeNo)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (storageTypeName == null) {
            if (other.storageTypeName != null) { return false; }
        } else if (!storageTypeName.equals(other.storageTypeName)) {
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
        sb.append("PlatformVcloudStorageType").append(" [");
        sb.append("storageTypeNo=").append(storageTypeNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("storageTypeName=").append(storageTypeName);
        sb.append("]");
        return sb.toString();
    }

}
