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
 * AZURE_CERTIFICATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAzureCertificate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** SUBSCRIPTION_ID [VARCHAR(100,0)] */
    private String subscriptionId;

    /** CERTIFICATE [VARCHAR(2,000,0)] */
    private String certificate;

    /** DEFAULT_SUBNET_ID [VARCHAR(30,0)] */
    private String defaultSubnetId;

    /** KEY_NAME [VARCHAR(100,0)] */
    private String keyName;

    /** KEY_PUBLIC [VARCHAR(1,000,0)] */
    private String keyPublic;

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
     * subscriptionIdを取得します。
     *
     * @return subscriptionId
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * subscriptionIdを設定します。
     *
     * @param subscriptionId subscriptionId
     */
    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     * certificateを取得します。
     *
     * @return certificate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * certificateを設定します。
     *
     * @param certificate certificate
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * defaultSubnetIdを取得します。
     *
     * @return defaultSubnetId
     */
    public String getDefaultSubnetId() {
        return defaultSubnetId;
    }

    /**
     * defaultSubnetIdを設定します。
     *
     * @param defaultSubnetId defaultSubnetId
     */
    public void setDefaultSubnetId(String defaultSubnetId) {
        this.defaultSubnetId = defaultSubnetId;
    }

    /**
     * keyNameを取得します。
     *
     * @return keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * keyNameを設定します。
     *
     * @param keyName keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * keyPublicを取得します。
     *
     * @return keyPublic
     */
    public String getKeyPublic() {
        return keyPublic;
    }

    /**
     * keyPublicを設定します。
     *
     * @param keyPublic keyPublic
     */
    public void setKeyPublic(String keyPublic) {
        this.keyPublic = keyPublic;
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
        result = prime * result + ((subscriptionId == null) ? 0 : subscriptionId.hashCode());
        result = prime * result + ((certificate == null) ? 0 : certificate.hashCode());
        result = prime * result + ((defaultSubnetId == null) ? 0 : defaultSubnetId.hashCode());
        result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
        result = prime * result + ((keyPublic == null) ? 0 : keyPublic.hashCode());

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

        final BaseAzureCertificate other = (BaseAzureCertificate) obj;
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
        if (subscriptionId == null) {
            if (other.subscriptionId != null) { return false; }
        } else if (!subscriptionId.equals(other.subscriptionId)) {
            return false;
        }
        if (certificate == null) {
            if (other.certificate != null) { return false; }
        } else if (!certificate.equals(other.certificate)) {
            return false;
        }
        if (defaultSubnetId == null) {
            if (other.defaultSubnetId != null) { return false; }
        } else if (!defaultSubnetId.equals(other.defaultSubnetId)) {
            return false;
        }
        if (keyName == null) {
            if (other.keyName != null) { return false; }
        } else if (!keyName.equals(other.keyName)) {
            return false;
        }
        if (keyPublic == null) {
            if (other.keyPublic != null) { return false; }
        } else if (!keyPublic.equals(other.keyPublic)) {
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
        sb.append("AzureCertificate").append(" [");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("subscriptionId=").append(subscriptionId).append(", ");
        sb.append("certificate=").append(certificate).append(", ");
        sb.append("defaultSubnetId=").append(defaultSubnetId).append(", ");
        sb.append("keyName=").append(keyName).append(", ");
        sb.append("keyPublic=").append(keyPublic);
        sb.append("]");
        return sb.toString();
    }

}
