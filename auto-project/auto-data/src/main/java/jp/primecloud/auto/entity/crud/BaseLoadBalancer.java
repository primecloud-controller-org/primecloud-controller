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
 * LOAD_BALANCERに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseLoadBalancer implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** LOAD_BALANCER_NO [BIGINT(19,0)] */
    private Long loadBalancerNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** LOAD_BALANCER_NAME [VARCHAR(30,0)] */
    private String loadBalancerName;

    /** COMMENT [VARCHAR(100,0)] */
    private String comment;

    /** FQDN [VARCHAR(100,0)] */
    private String fqdn;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** TYPE [VARCHAR(20,0)] */
    private String type;

    /** ENABLED [BIT(0,0)] */
    private Boolean enabled;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** CANONICAL_NAME [VARCHAR(100,0)] */
    private String canonicalName;

    /** CONFIGURE [BIT(0,0)] */
    private Boolean configure;

    /**
     * loadBalancerNoを取得します。
     *
     * @return loadBalancerNo
     */
    public Long getLoadBalancerNo() {
        return loadBalancerNo;
    }

    /**
     * loadBalancerNoを設定します。
     *
     * @param loadBalancerNo loadBalancerNo
     */
    public void setLoadBalancerNo(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
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
     * loadBalancerNameを取得します。
     *
     * @return loadBalancerName
     */
    public String getLoadBalancerName() {
        return loadBalancerName;
    }

    /**
     * loadBalancerNameを設定します。
     *
     * @param loadBalancerName loadBalancerName
     */
    public void setLoadBalancerName(String loadBalancerName) {
        this.loadBalancerName = loadBalancerName;
    }

    /**
     * commentを取得します。
     *
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * commentを設定します。
     *
     * @param comment comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * fqdnを取得します。
     *
     * @return fqdn
     */
    public String getFqdn() {
        return fqdn;
    }

    /**
     * fqdnを設定します。
     *
     * @param fqdn fqdn
     */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
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
     * typeを取得します。
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * typeを設定します。
     *
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * enabledを取得します。
     *
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * enabledを設定します。
     *
     * @param enabled enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * statusを取得します。
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     *
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * componentNoを取得します。
     *
     * @return componentNo
     */
    public Long getComponentNo() {
        return componentNo;
    }

    /**
     * componentNoを設定します。
     *
     * @param componentNo componentNo
     */
    public void setComponentNo(Long componentNo) {
        this.componentNo = componentNo;
    }

    /**
     * canonicalNameを取得します。
     *
     * @return canonicalName
     */
    public String getCanonicalName() {
        return canonicalName;
    }

    /**
     * canonicalNameを設定します。
     *
     * @param canonicalName canonicalName
     */
    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    /**
     * configureを取得します。
     *
     * @return configure
     */
    public Boolean getConfigure() {
        return configure;
    }

    /**
     * configureを設定します。
     *
     * @param configure configure
     */
    public void setConfigure(Boolean configure) {
        this.configure = configure;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((loadBalancerNo == null) ? 0 : loadBalancerNo.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((loadBalancerName == null) ? 0 : loadBalancerName.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((fqdn == null) ? 0 : fqdn.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((canonicalName == null) ? 0 : canonicalName.hashCode());
        result = prime * result + ((configure == null) ? 0 : configure.hashCode());

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

        final BaseLoadBalancer other = (BaseLoadBalancer) obj;
        if (loadBalancerNo == null) {
            if (other.loadBalancerNo != null) { return false; }
        } else if (!loadBalancerNo.equals(other.loadBalancerNo)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (loadBalancerName == null) {
            if (other.loadBalancerName != null) { return false; }
        } else if (!loadBalancerName.equals(other.loadBalancerName)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) { return false; }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (fqdn == null) {
            if (other.fqdn != null) { return false; }
        } else if (!fqdn.equals(other.fqdn)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) { return false; }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (enabled == null) {
            if (other.enabled != null) { return false; }
        } else if (!enabled.equals(other.enabled)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (componentNo == null) {
            if (other.componentNo != null) { return false; }
        } else if (!componentNo.equals(other.componentNo)) {
            return false;
        }
        if (canonicalName == null) {
            if (other.canonicalName != null) { return false; }
        } else if (!canonicalName.equals(other.canonicalName)) {
            return false;
        }
        if (configure == null) {
            if (other.configure != null) { return false; }
        } else if (!configure.equals(other.configure)) {
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
        sb.append("LoadBalancer").append(" [");
        sb.append("loadBalancerNo=").append(loadBalancerNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("loadBalancerName=").append(loadBalancerName).append(", ");
        sb.append("comment=").append(comment).append(", ");
        sb.append("fqdn=").append(fqdn).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("type=").append(type).append(", ");
        sb.append("enabled=").append(enabled).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("canonicalName=").append(canonicalName).append(", ");
        sb.append("configure=").append(configure);
        sb.append("]");
        return sb.toString();
    }

}
