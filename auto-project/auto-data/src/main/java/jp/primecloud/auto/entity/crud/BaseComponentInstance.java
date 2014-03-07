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
 * COMPONENT_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseComponentInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** ASSOCIATE [BIT(0,0)] */
    private Boolean associate;

    /** ENABLED [BIT(0,0)] */
    private Boolean enabled;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

    /** CONFIGURE [BIT(0,0)] */
    private Boolean configure;

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
     * instanceNoを取得します。
     *
     * @return instanceNo
     */
    public Long getInstanceNo() {
        return instanceNo;
    }

    /**
     * instanceNoを設定します。
     *
     * @param instanceNo instanceNo
     */
    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

    /**
     * associateを取得します。
     *
     * @return associate
     */
    public Boolean getAssociate() {
        return associate;
    }

    /**
     * associateを設定します。
     *
     * @param associate associate
     */
    public void setAssociate(Boolean associate) {
        this.associate = associate;
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
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((associate == null) ? 0 : associate.hashCode());
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

        final BaseComponentInstance other = (BaseComponentInstance) obj;
        if (componentNo == null) {
            if (other.componentNo != null) { return false; }
        } else if (!componentNo.equals(other.componentNo)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (associate == null) {
            if (other.associate != null) { return false; }
        } else if (!associate.equals(other.associate)) {
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
        sb.append("ComponentInstance").append(" [");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("associate=").append(associate).append(", ");
        sb.append("enabled=").append(enabled).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("configure=").append(configure);
        sb.append("]");
        return sb.toString();
    }

}
