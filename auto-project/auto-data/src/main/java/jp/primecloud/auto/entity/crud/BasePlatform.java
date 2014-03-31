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
 * PLATFORMに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatform implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** PLATFORM_NAME [VARCHAR(100,0)] */
    private String platformName;

    /** PLATFORM_NAME_DISP [VARCHAR(300,0)] */
    private String platformNameDisp;

    /** PLATFORM_SIMPLENAME_DISP [VARCHAR(200,0)] */
    private String platformSimplenameDisp;

    /** INTERNAL [BIT(0,0)] */
    private Boolean internal;

    /** PROXY [BIT(0,0)] */
    private Boolean proxy;

    /** PLATFORM_TYPE [VARCHAR(100,0)] */
    private String platformType;

    /** SELECTABLE [BIT(0,0)] */
    private Boolean selectable;

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
     * platformNameを取得します。
     *
     * @return platformName
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * platformNameを設定します。
     *
     * @param platformName platformName
     */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    /**
     * platformNameDispを取得します。
     *
     * @return platformNameDisp
     */
    public String getPlatformNameDisp() {
        return platformNameDisp;
    }

    /**
     * platformNameDispを設定します。
     *
     * @param platformNameDisp platformNameDisp
     */
    public void setPlatformNameDisp(String platformNameDisp) {
        this.platformNameDisp = platformNameDisp;
    }

    /**
     * platformSimplenameDispを取得します。
     *
     * @return platformSimplenameDisp
     */
    public String getPlatformSimplenameDisp() {
        return platformSimplenameDisp;
    }

    /**
     * platformSimplenameDispを設定します。
     *
     * @param platformSimplenameDisp platformSimplenameDisp
     */
    public void setPlatformSimplenameDisp(String platformSimplenameDisp) {
        this.platformSimplenameDisp = platformSimplenameDisp;
    }

    /**
     * internalを取得します。
     *
     * @return internal
     */
    public Boolean getInternal() {
        return internal;
    }

    /**
     * internalを設定します。
     *
     * @param internal internal
     */
    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    /**
     * proxyを取得します。
     *
     * @return proxy
     */
    public Boolean getProxy() {
        return proxy;
    }

    /**
     * proxyを設定します。
     *
     * @param proxy proxy
     */
    public void setProxy(Boolean proxy) {
        this.proxy = proxy;
    }

    /**
     * platformTypeを取得します。
     *
     * @return platformType
     */
    public String getPlatformType() {
        return platformType;
    }

    /**
     * platformTypeを設定します。
     *
     * @param platformType platformType
     */
    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    /**
     * selectableを取得します。
     *
     * @return selectable
     */
    public Boolean getSelectable() {
        return selectable;
    }

    /**
     * selectableを設定します。
     *
     * @param selectable selectable
     */
    public void setSelectable(Boolean selectable) {
        this.selectable = selectable;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((platformName == null) ? 0 : platformName.hashCode());
        result = prime * result + ((platformNameDisp == null) ? 0 : platformNameDisp.hashCode());
        result = prime * result + ((platformSimplenameDisp == null) ? 0 : platformSimplenameDisp.hashCode());
        result = prime * result + ((internal == null) ? 0 : internal.hashCode());
        result = prime * result + ((proxy == null) ? 0 : proxy.hashCode());
        result = prime * result + ((platformType == null) ? 0 : platformType.hashCode());
        result = prime * result + ((selectable == null) ? 0 : selectable.hashCode());

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

        final BasePlatform other = (BasePlatform) obj;
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (platformName == null) {
            if (other.platformName != null) { return false; }
        } else if (!platformName.equals(other.platformName)) {
            return false;
        }
        if (platformNameDisp == null) {
            if (other.platformNameDisp != null) { return false; }
        } else if (!platformNameDisp.equals(other.platformNameDisp)) {
            return false;
        }
        if (platformSimplenameDisp == null) {
            if (other.platformSimplenameDisp != null) { return false; }
        } else if (!platformSimplenameDisp.equals(other.platformSimplenameDisp)) {
            return false;
        }
        if (internal == null) {
            if (other.internal != null) { return false; }
        } else if (!internal.equals(other.internal)) {
            return false;
        }
        if (proxy == null) {
            if (other.proxy != null) { return false; }
        } else if (!proxy.equals(other.proxy)) {
            return false;
        }
        if (platformType == null) {
            if (other.platformType != null) { return false; }
        } else if (!platformType.equals(other.platformType)) {
            return false;
        }
        if (selectable == null) {
            if (other.selectable != null) { return false; }
        } else if (!selectable.equals(other.selectable)) {
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
        sb.append("Platform").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("platformName=").append(platformName).append(", ");
        sb.append("platformNameDisp=").append(platformNameDisp).append(", ");
        sb.append("platformSimplenameDisp=").append(platformSimplenameDisp).append(", ");
        sb.append("internal=").append(internal).append(", ");
        sb.append("proxy=").append(proxy).append(", ");
        sb.append("platformType=").append(platformType).append(", ");
        sb.append("selectable=").append(selectable);
        sb.append("]");
        return sb.toString();
    }

}
