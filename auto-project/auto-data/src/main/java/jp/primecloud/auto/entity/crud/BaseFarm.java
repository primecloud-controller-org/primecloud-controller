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
 * FARMに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseFarm implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** FARM_NAME [VARCHAR(30,0)] */
    private String farmName;

    /** COMMENT [VARCHAR(100,0)] */
    private String comment;

    /** DOMAIN_NAME [VARCHAR(100,0)] */
    private String domainName;

    /** SCHEDULED [BIT(0,0)] */
    private Boolean scheduled;

    /** COMPONENT_PROCESSING [BIT(0,0)] */
    private Boolean componentProcessing;

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
     * userNoを取得します。
     *
     * @return userNo
     */
    public Long getUserNo() {
        return userNo;
    }

    /**
     * userNoを設定します。
     *
     * @param userNo userNo
     */
    public void setUserNo(Long userNo) {
        this.userNo = userNo;
    }

    /**
     * farmNameを取得します。
     *
     * @return farmName
     */
    public String getFarmName() {
        return farmName;
    }

    /**
     * farmNameを設定します。
     *
     * @param farmName farmName
     */
    public void setFarmName(String farmName) {
        this.farmName = farmName;
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
     * domainNameを取得します。
     *
     * @return domainName
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * domainNameを設定します。
     *
     * @param domainName domainName
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * scheduledを取得します。
     *
     * @return scheduled
     */
    public Boolean getScheduled() {
        return scheduled;
    }

    /**
     * scheduledを設定します。
     *
     * @param scheduled scheduled
     */
    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }

    /**
     * componentProcessingを取得します。
     *
     * @return componentProcessing
     */
    public Boolean getComponentProcessing() {
        return componentProcessing;
    }

    /**
     * componentProcessingを設定します。
     *
     * @param componentProcessing componentProcessing
     */
    public void setComponentProcessing(Boolean componentProcessing) {
        this.componentProcessing = componentProcessing;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((farmName == null) ? 0 : farmName.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((domainName == null) ? 0 : domainName.hashCode());
        result = prime * result + ((scheduled == null) ? 0 : scheduled.hashCode());
        result = prime * result + ((componentProcessing == null) ? 0 : componentProcessing.hashCode());

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

        final BaseFarm other = (BaseFarm) obj;
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (userNo == null) {
            if (other.userNo != null) { return false; }
        } else if (!userNo.equals(other.userNo)) {
            return false;
        }
        if (farmName == null) {
            if (other.farmName != null) { return false; }
        } else if (!farmName.equals(other.farmName)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) { return false; }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (domainName == null) {
            if (other.domainName != null) { return false; }
        } else if (!domainName.equals(other.domainName)) {
            return false;
        }
        if (scheduled == null) {
            if (other.scheduled != null) { return false; }
        } else if (!scheduled.equals(other.scheduled)) {
            return false;
        }
        if (componentProcessing == null) {
            if (other.componentProcessing != null) { return false; }
        } else if (!componentProcessing.equals(other.componentProcessing)) {
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
        sb.append("Farm").append(" [");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("farmName=").append(farmName).append(", ");
        sb.append("comment=").append(comment).append(", ");
        sb.append("domainName=").append(domainName).append(", ");
        sb.append("scheduled=").append(scheduled).append(", ");
        sb.append("componentProcessing=").append(componentProcessing);
        sb.append("]");
        return sb.toString();
    }

}
