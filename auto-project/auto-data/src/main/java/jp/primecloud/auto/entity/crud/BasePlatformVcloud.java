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
 * PLATFORM_VCLOUDに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformVcloud implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** URL [VARCHAR(500,0)] */
    private String url;

    /** ORG_NAME [VARCHAR(200,0)] */
    private String orgName;

    /** VDC_NAME [VARCHAR(200,0)] */
    private String vdcName;

    /** SECURE [BIT(0,0)] */
    private Boolean secure;

    /** TIMEOUT [INT(10,0)] */
    private Integer timeout;

    /** DEF_NETWORK [VARCHAR(500,0)] */
    private String defNetwork;

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
     * orgNameを取得します。
     *
     * @return orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * orgNameを設定します。
     *
     * @param orgName orgName
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * vdcNameを取得します。
     *
     * @return vdcName
     */
    public String getVdcName() {
        return vdcName;
    }

    /**
     * vdcNameを設定します。
     *
     * @param vdcName vdcName
     */
    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
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
     * defNetworkを取得します。
     *
     * @return defNetwork
     */
    public String getDefNetwork() {
        return defNetwork;
    }

    /**
     * defNetworkを設定します。
     *
     * @param defNetwork defNetwork
     */
    public void setDefNetwork(String defNetwork) {
        this.defNetwork = defNetwork;
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
        result = prime * result + ((orgName == null) ? 0 : orgName.hashCode());
        result = prime * result + ((vdcName == null) ? 0 : vdcName.hashCode());
        result = prime * result + ((secure == null) ? 0 : secure.hashCode());
        result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
        result = prime * result + ((defNetwork == null) ? 0 : defNetwork.hashCode());

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

        final BasePlatformVcloud other = (BasePlatformVcloud) obj;
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
        if (orgName == null) {
            if (other.orgName != null) { return false; }
        } else if (!orgName.equals(other.orgName)) {
            return false;
        }
        if (vdcName == null) {
            if (other.vdcName != null) { return false; }
        } else if (!vdcName.equals(other.vdcName)) {
            return false;
        }
        if (secure == null) {
            if (other.secure != null) { return false; }
        } else if (!secure.equals(other.secure)) {
            return false;
        }
        if (timeout == null) {
            if (other.timeout != null) { return false; }
        } else if (!timeout.equals(other.timeout)) {
            return false;
        }
        if (defNetwork == null) {
            if (other.defNetwork != null) { return false; }
        } else if (!defNetwork.equals(other.defNetwork)) {
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
        sb.append("PlatformVcloud").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("url=").append(url).append(", ");
        sb.append("orgName=").append(orgName).append(", ");
        sb.append("vdcName=").append(vdcName).append(", ");
        sb.append("secure=").append(secure).append(", ");
        sb.append("timeout=").append(timeout).append(", ");
        sb.append("defNetwork=").append(defNetwork);
        sb.append("]");
        return sb.toString();
    }

}
