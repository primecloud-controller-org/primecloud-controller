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
 * VCLOUD_CERTIFICATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVcloudCertificate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** VCLOUD_ACCESS_ID [VARCHAR(100,0)] */
    private String vcloudAccessId;

    /** VCLOUD_SECRET_KEY [VARCHAR(100,0)] */
    private String vcloudSecretKey;

    /**
     * userNoを取得します。
     *
     * @return userNo
     */
    public Long getUserNo() {
        return userNo;
    }

    /**
     * userNoを設定します。
     *
     * @param userNo userNo
     */
    public void setUserNo(Long userNo) {
        this.userNo = userNo;
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
     * vcloudAccessIdを取得します。
     *
     * @return vcloudAccessId
     */
    public String getVcloudAccessId() {
        return vcloudAccessId;
    }

    /**
     * vcloudAccessIdを設定します。
     *
     * @param vcloudAccessId vcloudAccessId
     */
    public void setVcloudAccessId(String vcloudAccessId) {
        this.vcloudAccessId = vcloudAccessId;
    }

    /**
     * vcloudSecretKeyを取得します。
     *
     * @return vcloudSecretKey
     */
    public String getVcloudSecretKey() {
        return vcloudSecretKey;
    }

    /**
     * vcloudSecretKeyを設定します。
     *
     * @param vcloudSecretKey vcloudSecretKey
     */
    public void setVcloudSecretKey(String vcloudSecretKey) {
        this.vcloudSecretKey = vcloudSecretKey;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((vcloudAccessId == null) ? 0 : vcloudAccessId.hashCode());
        result = prime * result + ((vcloudSecretKey == null) ? 0 : vcloudSecretKey.hashCode());

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

        final BaseVcloudCertificate other = (BaseVcloudCertificate) obj;
        if (userNo == null) {
            if (other.userNo != null) { return false; }
        } else if (!userNo.equals(other.userNo)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (vcloudAccessId == null) {
            if (other.vcloudAccessId != null) { return false; }
        } else if (!vcloudAccessId.equals(other.vcloudAccessId)) {
            return false;
        }
        if (vcloudSecretKey == null) {
            if (other.vcloudSecretKey != null) { return false; }
        } else if (!vcloudSecretKey.equals(other.vcloudSecretKey)) {
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
        sb.append("VcloudCertificate").append(" [");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("vcloudAccessId=").append(vcloudAccessId).append(", ");
        sb.append("vcloudSecretKey=").append(vcloudSecretKey);
        sb.append("]");
        return sb.toString();
    }

}
