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
 * LOAD_BALANCER_HEALTH_CHECKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseLoadBalancerHealthCheck implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** LOAD_BALANCER_NO [BIGINT(19,0)] */
    private Long loadBalancerNo;

    /** CHECK_PROTOCOL [VARCHAR(20,0)] */
    private String checkProtocol;

    /** CHECK_PORT [INT(10,0)] */
    private Integer checkPort;

    /** CHECK_PATH [VARCHAR(100,0)] */
    private String checkPath;

    /** CHECK_TIMEOUT [INT(10,0)] */
    private Integer checkTimeout;

    /** CHECK_INTERVAL [INT(10,0)] */
    private Integer checkInterval;

    /** HEALTHY_THRESHOLD [INT(10,0)] */
    private Integer healthyThreshold;

    /** UNHEALTHY_THRESHOLD [INT(10,0)] */
    private Integer unhealthyThreshold;

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
     * checkProtocolを取得します。
     *
     * @return checkProtocol
     */
    public String getCheckProtocol() {
        return checkProtocol;
    }

    /**
     * checkProtocolを設定します。
     *
     * @param checkProtocol checkProtocol
     */
    public void setCheckProtocol(String checkProtocol) {
        this.checkProtocol = checkProtocol;
    }

    /**
     * checkPortを取得します。
     *
     * @return checkPort
     */
    public Integer getCheckPort() {
        return checkPort;
    }

    /**
     * checkPortを設定します。
     *
     * @param checkPort checkPort
     */
    public void setCheckPort(Integer checkPort) {
        this.checkPort = checkPort;
    }

    /**
     * checkPathを取得します。
     *
     * @return checkPath
     */
    public String getCheckPath() {
        return checkPath;
    }

    /**
     * checkPathを設定します。
     *
     * @param checkPath checkPath
     */
    public void setCheckPath(String checkPath) {
        this.checkPath = checkPath;
    }

    /**
     * checkTimeoutを取得します。
     *
     * @return checkTimeout
     */
    public Integer getCheckTimeout() {
        return checkTimeout;
    }

    /**
     * checkTimeoutを設定します。
     *
     * @param checkTimeout checkTimeout
     */
    public void setCheckTimeout(Integer checkTimeout) {
        this.checkTimeout = checkTimeout;
    }

    /**
     * checkIntervalを取得します。
     *
     * @return checkInterval
     */
    public Integer getCheckInterval() {
        return checkInterval;
    }

    /**
     * checkIntervalを設定します。
     *
     * @param checkInterval checkInterval
     */
    public void setCheckInterval(Integer checkInterval) {
        this.checkInterval = checkInterval;
    }

    /**
     * healthyThresholdを取得します。
     *
     * @return healthyThreshold
     */
    public Integer getHealthyThreshold() {
        return healthyThreshold;
    }

    /**
     * healthyThresholdを設定します。
     *
     * @param healthyThreshold healthyThreshold
     */
    public void setHealthyThreshold(Integer healthyThreshold) {
        this.healthyThreshold = healthyThreshold;
    }

    /**
     * unhealthyThresholdを取得します。
     *
     * @return unhealthyThreshold
     */
    public Integer getUnhealthyThreshold() {
        return unhealthyThreshold;
    }

    /**
     * unhealthyThresholdを設定します。
     *
     * @param unhealthyThreshold unhealthyThreshold
     */
    public void setUnhealthyThreshold(Integer unhealthyThreshold) {
        this.unhealthyThreshold = unhealthyThreshold;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((loadBalancerNo == null) ? 0 : loadBalancerNo.hashCode());
        result = prime * result + ((checkProtocol == null) ? 0 : checkProtocol.hashCode());
        result = prime * result + ((checkPort == null) ? 0 : checkPort.hashCode());
        result = prime * result + ((checkPath == null) ? 0 : checkPath.hashCode());
        result = prime * result + ((checkTimeout == null) ? 0 : checkTimeout.hashCode());
        result = prime * result + ((checkInterval == null) ? 0 : checkInterval.hashCode());
        result = prime * result + ((healthyThreshold == null) ? 0 : healthyThreshold.hashCode());
        result = prime * result + ((unhealthyThreshold == null) ? 0 : unhealthyThreshold.hashCode());

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

        final BaseLoadBalancerHealthCheck other = (BaseLoadBalancerHealthCheck) obj;
        if (loadBalancerNo == null) {
            if (other.loadBalancerNo != null) { return false; }
        } else if (!loadBalancerNo.equals(other.loadBalancerNo)) {
            return false;
        }
        if (checkProtocol == null) {
            if (other.checkProtocol != null) { return false; }
        } else if (!checkProtocol.equals(other.checkProtocol)) {
            return false;
        }
        if (checkPort == null) {
            if (other.checkPort != null) { return false; }
        } else if (!checkPort.equals(other.checkPort)) {
            return false;
        }
        if (checkPath == null) {
            if (other.checkPath != null) { return false; }
        } else if (!checkPath.equals(other.checkPath)) {
            return false;
        }
        if (checkTimeout == null) {
            if (other.checkTimeout != null) { return false; }
        } else if (!checkTimeout.equals(other.checkTimeout)) {
            return false;
        }
        if (checkInterval == null) {
            if (other.checkInterval != null) { return false; }
        } else if (!checkInterval.equals(other.checkInterval)) {
            return false;
        }
        if (healthyThreshold == null) {
            if (other.healthyThreshold != null) { return false; }
        } else if (!healthyThreshold.equals(other.healthyThreshold)) {
            return false;
        }
        if (unhealthyThreshold == null) {
            if (other.unhealthyThreshold != null) { return false; }
        } else if (!unhealthyThreshold.equals(other.unhealthyThreshold)) {
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
        sb.append("LoadBalancerHealthCheck").append(" [");
        sb.append("loadBalancerNo=").append(loadBalancerNo).append(", ");
        sb.append("checkProtocol=").append(checkProtocol).append(", ");
        sb.append("checkPort=").append(checkPort).append(", ");
        sb.append("checkPath=").append(checkPath).append(", ");
        sb.append("checkTimeout=").append(checkTimeout).append(", ");
        sb.append("checkInterval=").append(checkInterval).append(", ");
        sb.append("healthyThreshold=").append(healthyThreshold).append(", ");
        sb.append("unhealthyThreshold=").append(unhealthyThreshold);
        sb.append("]");
        return sb.toString();
    }

}
