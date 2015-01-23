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
 * NIFTY_CERTIFICATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseNiftyCertificate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** NIFTY_ACCESS_ID [VARCHAR(100,0)] */
    private String niftyAccessId;

    /** NIFTY_SECRET_KEY [VARCHAR(100,0)] */
    private String niftySecretKey;

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
     * niftyAccessIdを取得します。
     *
     * @return niftyAccessId
     */
    public String getNiftyAccessId() {
        return niftyAccessId;
    }

    /**
     * niftyAccessIdを設定します。
     *
     * @param niftyAccessId niftyAccessId
     */
    public void setNiftyAccessId(String niftyAccessId) {
        this.niftyAccessId = niftyAccessId;
    }

    /**
     * niftySecretKeyを取得します。
     *
     * @return niftySecretKey
     */
    public String getNiftySecretKey() {
        return niftySecretKey;
    }

    /**
     * niftySecretKeyを設定します。
     *
     * @param niftySecretKey niftySecretKey
     */
    public void setNiftySecretKey(String niftySecretKey) {
        this.niftySecretKey = niftySecretKey;
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
        result = prime * result + ((niftyAccessId == null) ? 0 : niftyAccessId.hashCode());
        result = prime * result + ((niftySecretKey == null) ? 0 : niftySecretKey.hashCode());

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

        final BaseNiftyCertificate other = (BaseNiftyCertificate) obj;
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
        if (niftyAccessId == null) {
            if (other.niftyAccessId != null) { return false; }
        } else if (!niftyAccessId.equals(other.niftyAccessId)) {
            return false;
        }
        if (niftySecretKey == null) {
            if (other.niftySecretKey != null) { return false; }
        } else if (!niftySecretKey.equals(other.niftySecretKey)) {
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
        sb.append("NiftyCertificate").append(" [");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("niftyAccessId=").append(niftyAccessId).append(", ");
        sb.append("niftySecretKey=").append(niftySecretKey);
        sb.append("]");
        return sb.toString();
    }

}
