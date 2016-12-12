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
 * INSTANCE_CONFIGに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseInstanceConfig implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** CONFIG_NO [BIGINT(19,0)] */
    private Long configNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** CONFIG_NAME [VARCHAR(50,0)] */
    private String configName;

    /** CONFIG_VALUE [VARCHAR(200,0)] */
    private String configValue;

    /**
     * configNoを取得します。
     *
     * @return configNo
     */
    public Long getConfigNo() {
        return configNo;
    }

    /**
     * configNoを設定します。
     *
     * @param configNo configNo
     */
    public void setConfigNo(Long configNo) {
        this.configNo = configNo;
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
     * configNameを取得します。
     *
     * @return configName
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * configNameを設定します。
     *
     * @param configName configName
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * configValueを取得します。
     *
     * @return configValue
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * configValueを設定します。
     *
     * @param configValue configValue
     */
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((configNo == null) ? 0 : configNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((configName == null) ? 0 : configName.hashCode());
        result = prime * result + ((configValue == null) ? 0 : configValue.hashCode());

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

        final BaseInstanceConfig other = (BaseInstanceConfig) obj;
        if (configNo == null) {
            if (other.configNo != null) { return false; }
        } else if (!configNo.equals(other.configNo)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (componentNo == null) {
            if (other.componentNo != null) { return false; }
        } else if (!componentNo.equals(other.componentNo)) {
            return false;
        }
        if (configName == null) {
            if (other.configName != null) { return false; }
        } else if (!configName.equals(other.configName)) {
            return false;
        }
        if (configValue == null) {
            if (other.configValue != null) { return false; }
        } else if (!configValue.equals(other.configValue)) {
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
        sb.append("InstanceConfig").append(" [");
        sb.append("configNo=").append(configNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("configName=").append(configName).append(", ");
        sb.append("configValue=").append(configValue);
        sb.append("]");
        return sb.toString();
    }

}
