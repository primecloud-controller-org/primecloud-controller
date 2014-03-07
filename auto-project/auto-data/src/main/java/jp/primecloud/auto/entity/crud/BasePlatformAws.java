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
 * PLATFORM_AWSに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformAws implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** HOST [VARCHAR(500,0)] */
    private String host;

    /** PORT [INT(10,0)] */
    private Integer port;

    /** SECURE [BIT(0,0)] */
    private Boolean secure;

    /** EUCA [BIT(0,0)] */
    private Boolean euca;

    /** VPC [BIT(0,0)] */
    private Boolean vpc;

    /** REGION [VARCHAR(50,0)] */
    private String region;

    /** AVAILABILITY_ZONE [VARCHAR(300,0)] */
    private String availabilityZone;

    /** VPC_ID [VARCHAR(30,0)] */
    private String vpcId;

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
     * hostを取得します。
     *
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * hostを設定します。
     *
     * @param host host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * portを取得します。
     *
     * @return port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * portを設定します。
     *
     * @param port port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * secureを取得します。
     *
     * @return secure
     */
    public Boolean getSecure() {
        return secure;
    }

    /**
     * secureを設定します。
     *
     * @param secure secure
     */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    /**
     * eucaを取得します。
     *
     * @return euca
     */
    public Boolean getEuca() {
        return euca;
    }

    /**
     * eucaを設定します。
     *
     * @param euca euca
     */
    public void setEuca(Boolean euca) {
        this.euca = euca;
    }

    /**
     * vpcを取得します。
     *
     * @return vpc
     */
    public Boolean getVpc() {
        return vpc;
    }

    /**
     * vpcを設定します。
     *
     * @param vpc vpc
     */
    public void setVpc(Boolean vpc) {
        this.vpc = vpc;
    }

    /**
     * regionを取得します。
     *
     * @return region
     */
    public String getRegion() {
        return region;
    }

    /**
     * regionを設定します。
     *
     * @param region region
     */
    public void setRegion(String region) {
        this.region = region;
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
     * vpcIdを取得します。
     *
     * @return vpcId
     */
    public String getVpcId() {
        return vpcId;
    }

    /**
     * vpcIdを設定します。
     *
     * @param vpcId vpcId
     */
    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((secure == null) ? 0 : secure.hashCode());
        result = prime * result + ((euca == null) ? 0 : euca.hashCode());
        result = prime * result + ((vpc == null) ? 0 : vpc.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());
        result = prime * result + ((vpcId == null) ? 0 : vpcId.hashCode());

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

        final BasePlatformAws other = (BasePlatformAws) obj;
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (host == null) {
            if (other.host != null) { return false; }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port == null) {
            if (other.port != null) { return false; }
        } else if (!port.equals(other.port)) {
            return false;
        }
        if (secure == null) {
            if (other.secure != null) { return false; }
        } else if (!secure.equals(other.secure)) {
            return false;
        }
        if (euca == null) {
            if (other.euca != null) { return false; }
        } else if (!euca.equals(other.euca)) {
            return false;
        }
        if (vpc == null) {
            if (other.vpc != null) { return false; }
        } else if (!vpc.equals(other.vpc)) {
            return false;
        }
        if (region == null) {
            if (other.region != null) { return false; }
        } else if (!region.equals(other.region)) {
            return false;
        }
        if (availabilityZone == null) {
            if (other.availabilityZone != null) { return false; }
        } else if (!availabilityZone.equals(other.availabilityZone)) {
            return false;
        }
        if (vpcId == null) {
            if (other.vpcId != null) { return false; }
        } else if (!vpcId.equals(other.vpcId)) {
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
        sb.append("PlatformAws").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("host=").append(host).append(", ");
        sb.append("port=").append(port).append(", ");
        sb.append("secure=").append(secure).append(", ");
        sb.append("euca=").append(euca).append(", ");
        sb.append("vpc=").append(vpc).append(", ");
        sb.append("region=").append(region).append(", ");
        sb.append("availabilityZone=").append(availabilityZone).append(", ");
        sb.append("vpcId=").append(vpcId);
        sb.append("]");
        return sb.toString();
    }

}
