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
 * PLATFORM_OPENSTACKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformOpenstack implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** URL [VARCHAR(500,0)] */
    private String url;

    /** NETWORK_ID [VARCHAR(40,0)] */
    private String networkId;

    /** TENANT_ID [VARCHAR(40,0)] */
    private String tenantId;

    /** TENANT_NM [VARCHAR(100,0)] */
    private String tenantNm;

    /** AVAILABILITY_ZONE [VARCHAR(100,0)] */
    private String availabilityZone;

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
     * urlを取得します。
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * networkIdを取得します。
     *
     * @return networkId
     */
    public String getNetworkId() {
        return networkId;
    }

    /**
     * networkIdを設定します。
     *
     * @param networkId networkId
     */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    /**
     * tenantIdを取得します。
     *
     * @return tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * tenantIdを設定します。
     *
     * @param tenantId tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * tenantNmを取得します。
     *
     * @return tenantNm
     */
    public String getTenantNm() {
        return tenantNm;
    }

    /**
     * tenantNmを設定します。
     *
     * @param tenantNm tenantNm
     */
    public void setTenantNm(String tenantNm) {
        this.tenantNm = tenantNm;
    }

    /**
     * availabilityZoneを取得します。
     *
     * @return availabilityZone
     */
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * availabilityZoneを設定します。
     *
     * @param availabilityZone availabilityZone
     */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((networkId == null) ? 0 : networkId.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        result = prime * result + ((tenantNm == null) ? 0 : tenantNm.hashCode());
        result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());

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

        final BasePlatformOpenstack other = (BasePlatformOpenstack) obj;
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) { return false; }
        } else if (!url.equals(other.url)) {
            return false;
        }
        if (networkId == null) {
            if (other.networkId != null) { return false; }
        } else if (!networkId.equals(other.networkId)) {
            return false;
        }
        if (tenantId == null) {
            if (other.tenantId != null) { return false; }
        } else if (!tenantId.equals(other.tenantId)) {
            return false;
        }
        if (tenantNm == null) {
            if (other.tenantNm != null) { return false; }
        } else if (!tenantNm.equals(other.tenantNm)) {
            return false;
        }
        if (availabilityZone == null) {
            if (other.availabilityZone != null) { return false; }
        } else if (!availabilityZone.equals(other.availabilityZone)) {
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
        sb.append("PlatformOpenstack").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("url=").append(url).append(", ");
        sb.append("networkId=").append(networkId).append(", ");
        sb.append("tenantId=").append(tenantId).append(", ");
        sb.append("tenantNm=").append(tenantNm).append(", ");
        sb.append("availabilityZone=").append(availabilityZone);
        sb.append("]");
        return sb.toString();
    }

}
