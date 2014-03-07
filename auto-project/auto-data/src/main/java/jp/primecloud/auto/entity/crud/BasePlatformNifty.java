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
 * PLATFORM_NIFTYに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformNifty implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** WSDL [VARCHAR(300,0)] */
    private String wsdl;

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
     * wsdlを取得します。
     *
     * @return wsdl
     */
    public String getWsdl() {
        return wsdl;
    }

    /**
     * wsdlを設定します。
     *
     * @param wsdl wsdl
     */
    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((wsdl == null) ? 0 : wsdl.hashCode());

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

        final BasePlatformNifty other = (BasePlatformNifty) obj;
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (wsdl == null) {
            if (other.wsdl != null) { return false; }
        } else if (!wsdl.equals(other.wsdl)) {
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
        sb.append("PlatformNifty").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("wsdl=").append(wsdl);
        sb.append("]");
        return sb.toString();
    }

}
