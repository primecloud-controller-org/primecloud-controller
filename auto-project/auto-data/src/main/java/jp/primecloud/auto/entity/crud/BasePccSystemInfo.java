package jp.primecloud.auto.entity.crud;
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


import java.io.Serializable;

/**
 * <p>
 * PCC_SYSTEM_INFOに対応したエンティティのベーススクラスです。
 * </p>
 *
 */
public abstract class BasePccSystemInfo implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** SECRET_KEY [VARCHAR(16,0)] */
    private String secretKey;

    /**
     * secretKeyを取得します。
     *
     * @return secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * secretKeyを設定します。
     *
     * @param secretKey secretKey
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((secretKey == null) ? 0 : secretKey.hashCode());

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

        final BasePccSystemInfo other = (BasePccSystemInfo) obj;
        if (secretKey == null) {
            if (other.secretKey != null) { return false; }
        } else if (!secretKey.equals(other.secretKey)) {
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
        sb.append("PccSystemInfo").append(" [");
        sb.append("secretKey=").append(secretKey);
        sb.append("]");
        return sb.toString();
    }

}
