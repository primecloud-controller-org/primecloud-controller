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
 * platform_cloudstackに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformCloudstack implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** HOST [VARCHAR(300,0)] */
    private String host;

    /** PATH [VARCHAR(500,0)] */
    private String path;

    /** PORT [INT(10,0)] */
    private Integer port;

    /** SECURE [BIT(0,0)] */
    private Boolean secure;

    /** ZONE_ID [VARCHAR(100,0)] */
    private String zoneId;

    /** NETWORK_ID [VARCHAR(500,0)] */
    private String networkId;

    /** TIMEOUT [INT(10,0)] */
    private Integer timeout;

    /** DEVICE_TYPE [VARCHAR(20,0)] */
    private String deviceType;

    /** HOST_ID [VARCHAR(200,0)] */
    private String hostId;

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
     * pathを取得します。
     *
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * pathを設定します。
     *
     * @param path path
     */
    public void setPath(String path) {
        this.path = path;
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
     * zoneIdを取得します。
     *
     * @return zoneId
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * zoneIdを設定します。
     *
     * @param zoneId zoneId
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
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
     * timeoutを取得します。
     *
     * @return timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * timeoutを設定します。
     *
     * @param timeout timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * deviceTypeを取得します。
     *
     * @return deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * deviceTypeを設定します。
     *
     * @param deviceType deviceType
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * hostIdを取得します。
     *
     * @return hostId
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * hostIdを設定します。
     *
     * @param hostId hostId
     */
    public void setHostId(String hostId) {
        this.hostId = hostId;
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
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((secure == null) ? 0 : secure.hashCode());
        result = prime * result + ((zoneId == null) ? 0 : zoneId.hashCode());
        result = prime * result + ((networkId == null) ? 0 : networkId.hashCode());
        result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
        result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
        result = prime * result + ((hostId == null) ? 0 : hostId.hashCode());

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

        final BasePlatformCloudstack other = (BasePlatformCloudstack) obj;
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
        if (path == null) {
            if (other.path != null) { return false; }
        } else if (!path.equals(other.path)) {
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
        if (zoneId == null) {
            if (other.zoneId != null) { return false; }
        } else if (!zoneId.equals(other.zoneId)) {
            return false;
        }
        if (networkId == null) {
            if (other.networkId != null) { return false; }
        } else if (!networkId.equals(other.networkId)) {
            return false;
        }
        if (timeout == null) {
            if (other.timeout != null) { return false; }
        } else if (!timeout.equals(other.timeout)) {
            return false;
        }
        if (deviceType == null) {
            if (other.deviceType != null) { return false; }
        } else if (!deviceType.equals(other.deviceType)) {
            return false;
        }
        if (hostId == null) {
            if (other.hostId != null) { return false; }
        } else if (!hostId.equals(other.hostId)) {
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
        sb.append("PlatformCloudstack").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("host=").append(host).append(", ");
        sb.append("path=").append(path).append(", ");
        sb.append("port=").append(port).append(", ");
        sb.append("secure=").append(secure).append(", ");
        sb.append("zoneId=").append(zoneId).append(", ");
        sb.append("networkId=").append(networkId).append(", ");
        sb.append("timeout=").append(timeout).append(", ");
        sb.append("deviceType=").append(deviceType).append(", ");
        sb.append("hostId=").append(hostId);
        sb.append("]");
        return sb.toString();
    }

}
