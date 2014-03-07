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
 * CLOUDSTACK_CERTIFICATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseCloudstackCertificate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** ACCOUNT [BIGINT(19,0)] */
    private Long account;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** CLOUDSTACK_ACCESS_ID [VARCHAR(100,0)] */
    private String cloudstackAccessId;

    /** CLOUDSTACK_SECRET_KEY [VARCHAR(100,0)] */
    private String cloudstackSecretKey;

    /** DEF_KEYPAIR [VARCHAR(100,0)] */
    private String defKeypair;

    /**
     * accountを取得します。
     *
     * @return account
     */
    public Long getAccount() {
        return account;
    }

    /**
     * accountを設定します。
     *
     * @param account account
     */
    public void setAccount(Long account) {
        this.account = account;
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
     * cloudstackAccessIdを取得します。
     *
     * @return cloudstackAccessId
     */
    public String getCloudstackAccessId() {
        return cloudstackAccessId;
    }

    /**
     * cloudstackAccessIdを設定します。
     *
     * @param cloudstackAccessId cloudstackAccessId
     */
    public void setCloudstackAccessId(String cloudstackAccessId) {
        this.cloudstackAccessId = cloudstackAccessId;
    }

    /**
     * cloudstackSecretKeyを取得します。
     *
     * @return cloudstackSecretKey
     */
    public String getCloudstackSecretKey() {
        return cloudstackSecretKey;
    }

    /**
     * cloudstackSecretKeyを設定します。
     *
     * @param cloudstackSecretKey cloudstackSecretKey
     */
    public void setCloudstackSecretKey(String cloudstackSecretKey) {
        this.cloudstackSecretKey = cloudstackSecretKey;
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
        result = prime * result + ((account == null) ? 0 : account.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((cloudstackAccessId == null) ? 0 : cloudstackAccessId.hashCode());
        result = prime * result + ((cloudstackSecretKey == null) ? 0 : cloudstackSecretKey.hashCode());
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

        final BaseCloudstackCertificate other = (BaseCloudstackCertificate) obj;
        if (account == null) {
            if (other.account != null) { return false; }
        } else if (!account.equals(other.account)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (cloudstackAccessId == null) {
            if (other.cloudstackAccessId != null) { return false; }
        } else if (!cloudstackAccessId.equals(other.cloudstackAccessId)) {
            return false;
        }
        if (cloudstackSecretKey == null) {
            if (other.cloudstackSecretKey != null) { return false; }
        } else if (!cloudstackSecretKey.equals(other.cloudstackSecretKey)) {
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
        sb.append("CloudstackCertificate").append(" [");
        sb.append("account=").append(account).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("cloudstackAccessId=").append(cloudstackAccessId).append(", ");
        sb.append("cloudstackSecretKey=").append(cloudstackSecretKey).append(", ");
        sb.append("defKeypair=").append(defKeypair);
        sb.append("]");
        return sb.toString();
    }

}
