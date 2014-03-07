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
 * NIFTY_KEY_PAIRに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseNiftyKeyPair implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** KEY_NO [BIGINT(19,0)] */
    private Long keyNo;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** KEY_NAME [VARCHAR(100,0)] */
    private String keyName;

    /** PRIVATE_KEY [VARCHAR(10,240,0)] */
    private String privateKey;

    /** PASSPHRASE [VARCHAR(100,0)] */
    private String passphrase;

    /**
     * keyNoを取得します。
     *
     * @return keyNo
     */
    public Long getKeyNo() {
        return keyNo;
    }

    /**
     * keyNoを設定します。
     *
     * @param keyNo keyNo
     */
    public void setKeyNo(Long keyNo) {
        this.keyNo = keyNo;
    }

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
     * privateKeyを取得します。
     *
     * @return privateKey
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * privateKeyを設定します。
     *
     * @param privateKey privateKey
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * passphraseを取得します。
     *
     * @return passphrase
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * passphraseを設定します。
     *
     * @param passphrase passphrase
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((keyNo == null) ? 0 : keyNo.hashCode());
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
        result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
        result = prime * result + ((passphrase == null) ? 0 : passphrase.hashCode());

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

        final BaseNiftyKeyPair other = (BaseNiftyKeyPair) obj;
        if (keyNo == null) {
            if (other.keyNo != null) { return false; }
        } else if (!keyNo.equals(other.keyNo)) {
            return false;
        }
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
        if (keyName == null) {
            if (other.keyName != null) { return false; }
        } else if (!keyName.equals(other.keyName)) {
            return false;
        }
        if (privateKey == null) {
            if (other.privateKey != null) { return false; }
        } else if (!privateKey.equals(other.privateKey)) {
            return false;
        }
        if (passphrase == null) {
            if (other.passphrase != null) { return false; }
        } else if (!passphrase.equals(other.passphrase)) {
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
        sb.append("NiftyKeyPair").append(" [");
        sb.append("keyNo=").append(keyNo).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("keyName=").append(keyName).append(", ");
        sb.append("privateKey=").append(privateKey).append(", ");
        sb.append("passphrase=").append(passphrase);
        sb.append("]");
        return sb.toString();
    }

}
