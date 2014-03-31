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
 * AWS_CERTIFICATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAwsCertificate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** AWS_ACCESS_ID [VARCHAR(100,0)] */
    private String awsAccessId;

    /** AWS_SECRET_KEY [VARCHAR(100,0)] */
    private String awsSecretKey;

    /** DEF_KEYPAIR [VARCHAR(100,0)] */
    private String defKeypair;

    /** DEF_SUBNET [VARCHAR(30,0)] */
    private String defSubnet;

    /** DEF_LB_SUBNET [VARCHAR(100,0)] */
    private String defLbSubnet;

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
     * awsAccessIdを取得します。
     *
     * @return awsAccessId
     */
    public String getAwsAccessId() {
        return awsAccessId;
    }

    /**
     * awsAccessIdを設定します。
     *
     * @param awsAccessId awsAccessId
     */
    public void setAwsAccessId(String awsAccessId) {
        this.awsAccessId = awsAccessId;
    }

    /**
     * awsSecretKeyを取得します。
     *
     * @return awsSecretKey
     */
    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    /**
     * awsSecretKeyを設定します。
     *
     * @param awsSecretKey awsSecretKey
     */
    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
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
     * defSubnetを取得します。
     *
     * @return defSubnet
     */
    public String getDefSubnet() {
        return defSubnet;
    }

    /**
     * defSubnetを設定します。
     *
     * @param defSubnet defSubnet
     */
    public void setDefSubnet(String defSubnet) {
        this.defSubnet = defSubnet;
    }

    /**
     * defLbSubnetを取得します。
     *
     * @return defLbSubnet
     */
    public String getDefLbSubnet() {
        return defLbSubnet;
    }

    /**
     * defLbSubnetを設定します。
     *
     * @param defLbSubnet defLbSubnet
     */
    public void setDefLbSubnet(String defLbSubnet) {
        this.defLbSubnet = defLbSubnet;
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
        result = prime * result + ((awsAccessId == null) ? 0 : awsAccessId.hashCode());
        result = prime * result + ((awsSecretKey == null) ? 0 : awsSecretKey.hashCode());
        result = prime * result + ((defKeypair == null) ? 0 : defKeypair.hashCode());
        result = prime * result + ((defSubnet == null) ? 0 : defSubnet.hashCode());
        result = prime * result + ((defLbSubnet == null) ? 0 : defLbSubnet.hashCode());

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

        final BaseAwsCertificate other = (BaseAwsCertificate) obj;
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
        if (awsAccessId == null) {
            if (other.awsAccessId != null) { return false; }
        } else if (!awsAccessId.equals(other.awsAccessId)) {
            return false;
        }
        if (awsSecretKey == null) {
            if (other.awsSecretKey != null) { return false; }
        } else if (!awsSecretKey.equals(other.awsSecretKey)) {
            return false;
        }
        if (defKeypair == null) {
            if (other.defKeypair != null) { return false; }
        } else if (!defKeypair.equals(other.defKeypair)) {
            return false;
        }
        if (defSubnet == null) {
            if (other.defSubnet != null) { return false; }
        } else if (!defSubnet.equals(other.defSubnet)) {
            return false;
        }
        if (defLbSubnet == null) {
            if (other.defLbSubnet != null) { return false; }
        } else if (!defLbSubnet.equals(other.defLbSubnet)) {
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
        sb.append("AwsCertificate").append(" [");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("awsAccessId=").append(awsAccessId).append(", ");
        sb.append("awsSecretKey=").append(awsSecretKey).append(", ");
        sb.append("defKeypair=").append(defKeypair).append(", ");
        sb.append("defSubnet=").append(defSubnet).append(", ");
        sb.append("defLbSubnet=").append(defLbSubnet);
        sb.append("]");
        return sb.toString();
    }

}
