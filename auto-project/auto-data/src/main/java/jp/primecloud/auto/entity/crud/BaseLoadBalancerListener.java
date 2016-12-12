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
 * LOAD_BALANCER_LISTENERに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseLoadBalancerListener implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** LOAD_BALANCER_NO [BIGINT(19,0)] */
    private Long loadBalancerNo;

    /** LOAD_BALANCER_PORT [INT(10,0)] */
    private Integer loadBalancerPort;

    /** SERVICE_PORT [INT(10,0)] */
    private Integer servicePort;

    /** PROTOCOL [VARCHAR(20,0)] */
    private String protocol;

    /** SSL_KEY_NO [BIGINT(19,0)] */
    private Long sslKeyNo;

    /** ENABLED [BIT(0,0)] */
    private Boolean enabled;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

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
     * loadBalancerPortを取得します。
     *
     * @return loadBalancerPort
     */
    public Integer getLoadBalancerPort() {
        return loadBalancerPort;
    }

    /**
     * loadBalancerPortを設定します。
     *
     * @param loadBalancerPort loadBalancerPort
     */
    public void setLoadBalancerPort(Integer loadBalancerPort) {
        this.loadBalancerPort = loadBalancerPort;
    }

    /**
     * servicePortを取得します。
     *
     * @return servicePort
     */
    public Integer getServicePort() {
        return servicePort;
    }

    /**
     * servicePortを設定します。
     *
     * @param servicePort servicePort
     */
    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    /**
     * protocolを取得します。
     *
     * @return protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * protocolを設定します。
     *
     * @param protocol protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * sslKeyNoを取得します。
     *
     * @return sslKeyNo
     */
    public Long getSslKeyNo() {
        return sslKeyNo;
    }

    /**
     * sslKeyNoを設定します。
     *
     * @param sslKeyNo sslKeyNo
     */
    public void setSslKeyNo(Long sslKeyNo) {
        this.sslKeyNo = sslKeyNo;
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
        result = prime * result + ((loadBalancerPort == null) ? 0 : loadBalancerPort.hashCode());
        result = prime * result + ((servicePort == null) ? 0 : servicePort.hashCode());
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((sslKeyNo == null) ? 0 : sslKeyNo.hashCode());
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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

        final BaseLoadBalancerListener other = (BaseLoadBalancerListener) obj;
        if (loadBalancerNo == null) {
            if (other.loadBalancerNo != null) { return false; }
        } else if (!loadBalancerNo.equals(other.loadBalancerNo)) {
            return false;
        }
        if (loadBalancerPort == null) {
            if (other.loadBalancerPort != null) { return false; }
        } else if (!loadBalancerPort.equals(other.loadBalancerPort)) {
            return false;
        }
        if (servicePort == null) {
            if (other.servicePort != null) { return false; }
        } else if (!servicePort.equals(other.servicePort)) {
            return false;
        }
        if (protocol == null) {
            if (other.protocol != null) { return false; }
        } else if (!protocol.equals(other.protocol)) {
            return false;
        }
        if (sslKeyNo == null) {
            if (other.sslKeyNo != null) { return false; }
        } else if (!sslKeyNo.equals(other.sslKeyNo)) {
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
        sb.append("LoadBalancerListener").append(" [");
        sb.append("loadBalancerNo=").append(loadBalancerNo).append(", ");
        sb.append("loadBalancerPort=").append(loadBalancerPort).append(", ");
        sb.append("servicePort=").append(servicePort).append(", ");
        sb.append("protocol=").append(protocol).append(", ");
        sb.append("sslKeyNo=").append(sslKeyNo).append(", ");
        sb.append("enabled=").append(enabled).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("configure=").append(configure);
        sb.append("]");
        return sb.toString();
    }

}
