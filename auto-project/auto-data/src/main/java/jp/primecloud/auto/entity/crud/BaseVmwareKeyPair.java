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
 * VMWARE_KEY_PAIRに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVmwareKeyPair implements Serializable {

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

    /** KEY_PUBLIC [VARCHAR(1,000,0)] */
    private String keyPublic;

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
        result = prime * result + ((keyNo == null) ? 0 : keyNo.hashCode());
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
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

        final BaseVmwareKeyPair other = (BaseVmwareKeyPair) obj;
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
        sb.append("VmwareKeyPair").append(" [");
        sb.append("keyNo=").append(keyNo).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("keyName=").append(keyName).append(", ");
        sb.append("keyPublic=").append(keyPublic);
        sb.append("]");
        return sb.toString();
    }

}
