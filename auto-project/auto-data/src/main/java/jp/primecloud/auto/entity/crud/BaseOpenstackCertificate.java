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
 * OPENSTACK_CERTIFICATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseOpenstackCertificate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** OS_ACCESS_ID [VARCHAR(100,0)] */
    private String osAccessId;

    /** OS_SECRET_KEY [VARCHAR(100,0)] */
    private String osSecretKey;

    /** DEF_KEYPAIR [VARCHAR(100,0)] */
    private String defKeypair;

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
     * osAccessIdを取得します。
     *
     * @return osAccessId
     */
    public String getOsAccessId() {
        return osAccessId;
    }

    /**
     * osAccessIdを設定します。
     *
     * @param osAccessId osAccessId
     */
    public void setOsAccessId(String osAccessId) {
        this.osAccessId = osAccessId;
    }

    /**
     * osSecretKeyを取得します。
     *
     * @return osSecretKey
     */
    public String getOsSecretKey() {
        return osSecretKey;
    }

    /**
     * osSecretKeyを設定します。
     *
     * @param osSecretKey osSecretKey
     */
    public void setOsSecretKey(String osSecretKey) {
        this.osSecretKey = osSecretKey;
    }

    /**
     * defKeypairを取得します。
     *
     * @return defKeypair
     */
    public String getDefKeypair() {
        return defKeypair;
    }

    /**
     * defKeypairを設定します。
     *
     * @param defKeypair defKeypair
     */
    public void setDefKeypair(String defKeypair) {
        this.defKeypair = defKeypair;
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
        result = prime * result + ((osAccessId == null) ? 0 : osAccessId.hashCode());
        result = prime * result + ((osSecretKey == null) ? 0 : osSecretKey.hashCode());
        result = prime * result + ((defKeypair == null) ? 0 : defKeypair.hashCode());

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

        final BaseOpenstackCertificate other = (BaseOpenstackCertificate) obj;
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
        if (osAccessId == null) {
            if (other.osAccessId != null) { return false; }
        } else if (!osAccessId.equals(other.osAccessId)) {
            return false;
        }
        if (osSecretKey == null) {
            if (other.osSecretKey != null) { return false; }
        } else if (!osSecretKey.equals(other.osSecretKey)) {
            return false;
        }
        if (defKeypair == null) {
            if (other.defKeypair != null) { return false; }
        } else if (!defKeypair.equals(other.defKeypair)) {
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
        sb.append("OpenstackCertificate").append(" [");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("osAccessId=").append(osAccessId).append(", ");
        sb.append("osSecretKey=").append(osSecretKey).append(", ");
        sb.append("defKeypair=").append(defKeypair);
        sb.append("]");
        return sb.toString();
    }

}
