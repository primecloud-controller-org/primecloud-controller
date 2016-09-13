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
 * AWS_SSL_KEYに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAwsSslKey implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** KEY_NO [BIGINT(19,0)] */
    private Long keyNo;

    /** KEY_NAME [VARCHAR(100,0)] */
    private String keyName;

    /** SSLCERTIFICATEID [VARCHAR(100,0)] */
    private String sslcertificateid;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

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
     * sslcertificateidを取得します。
     *
     * @return sslcertificateid
     */
    public String getSslcertificateid() {
        return sslcertificateid;
    }

    /**
     * sslcertificateidを設定します。
     *
     * @param sslcertificateid sslcertificateid
     */
    public void setSslcertificateid(String sslcertificateid) {
        this.sslcertificateid = sslcertificateid;
    }

    /**
     * farmNoを取得します。
     *
     * @return farmNo
     */
    public Long getFarmNo() {
        return farmNo;
    }

    /**
     * farmNoを設定します。
     *
     * @param farmNo farmNo
     */
    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((keyNo == null) ? 0 : keyNo.hashCode());
        result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
        result = prime * result + ((sslcertificateid == null) ? 0 : sslcertificateid.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());

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

        final BaseAwsSslKey other = (BaseAwsSslKey) obj;
        if (keyNo == null) {
            if (other.keyNo != null) { return false; }
        } else if (!keyNo.equals(other.keyNo)) {
            return false;
        }
        if (keyName == null) {
            if (other.keyName != null) { return false; }
        } else if (!keyName.equals(other.keyName)) {
            return false;
        }
        if (sslcertificateid == null) {
            if (other.sslcertificateid != null) { return false; }
        } else if (!sslcertificateid.equals(other.sslcertificateid)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
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
        sb.append("AwsSslKey").append(" [");
        sb.append("keyNo=").append(keyNo).append(", ");
        sb.append("keyName=").append(keyName).append(", ");
        sb.append("sslcertificateid=").append(sslcertificateid).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("platformNo=").append(platformNo);
        sb.append("]");
        return sb.toString();
    }

}
