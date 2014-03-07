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
 * COMPONENTに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseComponent implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** COMPONENT_NAME [VARCHAR(100,0)] */
    private String componentName;

    /** COMPONENT_TYPE_NO [BIGINT(19,0)] */
    private Long componentTypeNo;

    /** COMMENT [VARCHAR(100,0)] */
    private String comment;

    /** LOAD_BALANCER [BIT(0,0)] */
    private Boolean loadBalancer;

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
     * componentNameを取得します。
     *
     * @return componentName
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * componentNameを設定します。
     *
     * @param componentName componentName
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * componentTypeNoを取得します。
     *
     * @return componentTypeNo
     */
    public Long getComponentTypeNo() {
        return componentTypeNo;
    }

    /**
     * componentTypeNoを設定します。
     *
     * @param componentTypeNo componentTypeNo
     */
    public void setComponentTypeNo(Long componentTypeNo) {
        this.componentTypeNo = componentTypeNo;
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
     * loadBalancerを取得します。
     *
     * @return loadBalancer
     */
    public Boolean getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * loadBalancerを設定します。
     *
     * @param loadBalancer loadBalancer
     */
    public void setLoadBalancer(Boolean loadBalancer) {
        this.loadBalancer = loadBalancer;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((componentName == null) ? 0 : componentName.hashCode());
        result = prime * result + ((componentTypeNo == null) ? 0 : componentTypeNo.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((loadBalancer == null) ? 0 : loadBalancer.hashCode());

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

        final BaseComponent other = (BaseComponent) obj;
        if (componentNo == null) {
            if (other.componentNo != null) { return false; }
        } else if (!componentNo.equals(other.componentNo)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (componentName == null) {
            if (other.componentName != null) { return false; }
        } else if (!componentName.equals(other.componentName)) {
            return false;
        }
        if (componentTypeNo == null) {
            if (other.componentTypeNo != null) { return false; }
        } else if (!componentTypeNo.equals(other.componentTypeNo)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) { return false; }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (loadBalancer == null) {
            if (other.loadBalancer != null) { return false; }
        } else if (!loadBalancer.equals(other.loadBalancer)) {
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
        sb.append("Component").append(" [");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("componentName=").append(componentName).append(", ");
        sb.append("componentTypeNo=").append(componentTypeNo).append(", ");
        sb.append("comment=").append(comment).append(", ");
        sb.append("loadBalancer=").append(loadBalancer);
        sb.append("]");
        return sb.toString();
    }

}
