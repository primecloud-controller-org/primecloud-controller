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
 * API_CERTIFICATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseApiCertificate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** API_ACCESS_ID [VARCHAR(100,0)] */
    private String apiAccessId;

    /** API_SECRET_KEY [VARCHAR(100,0)] */
    private String apiSecretKey;

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
     * apiAccessIdを取得します。
     *
     * @return apiAccessId
     */
    public String getApiAccessId() {
        return apiAccessId;
    }

    /**
     * apiAccessIdを設定します。
     *
     * @param apiAccessId apiAccessId
     */
    public void setApiAccessId(String apiAccessId) {
        this.apiAccessId = apiAccessId;
    }

    /**
     * apiSecretKeyを取得します。
     *
     * @return apiSecretKey
     */
    public String getApiSecretKey() {
        return apiSecretKey;
    }

    /**
     * apiSecretKeyを設定します。
     *
     * @param apiSecretKey apiSecretKey
     */
    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecretKey = apiSecretKey;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((apiAccessId == null) ? 0 : apiAccessId.hashCode());
        result = prime * result + ((apiSecretKey == null) ? 0 : apiSecretKey.hashCode());

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

        final BaseApiCertificate other = (BaseApiCertificate) obj;
        if (userNo == null) {
            if (other.userNo != null) { return false; }
        } else if (!userNo.equals(other.userNo)) {
            return false;
        }
        if (apiAccessId == null) {
            if (other.apiAccessId != null) { return false; }
        } else if (!apiAccessId.equals(other.apiAccessId)) {
            return false;
        }
        if (apiSecretKey == null) {
            if (other.apiSecretKey != null) { return false; }
        } else if (!apiSecretKey.equals(other.apiSecretKey)) {
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
        sb.append("ApiCertificate").append(" [");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("apiAccessId=").append(apiAccessId).append(", ");
        sb.append("apiSecretKey=").append(apiSecretKey);
        sb.append("]");
        return sb.toString();
    }

}
