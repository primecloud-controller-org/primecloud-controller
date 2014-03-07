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
 * auto_scaling_confに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAutoScalingConf implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** LOAD_BALANCER_NO [BIGINT(19,0)] */
    private Long loadBalancerNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** IMAGE_NO [BIGINT(19,0)] */
    private Long imageNo;

    /** INSTANCE_TYPE [VARCHAR(20,0)] */
    private String instanceType;

    /** IDLE_TIME_MAX [BIGINT(19,0)] */
    private Long idleTimeMax;

    /** IDLE_TIME_MIN [BIGINT(19,0)] */
    private Long idleTimeMin;

    /** CONTINUE_LIMIT [BIGINT(19,0)] */
    private Long continueLimit;

    /** ADD_COUNT [BIGINT(19,0)] */
    private Long addCount;

    /** DEL_COUNT [BIGINT(19,0)] */
    private Long delCount;

    /** ENABLED [BIT(0,0)] */
    private Boolean enabled;

    /** NAMING_RULE [VARCHAR(20,0)] */
    private String namingRule;

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
     * imageNoを取得します。
     *
     * @return imageNo
     */
    public Long getImageNo() {
        return imageNo;
    }

    /**
     * imageNoを設定します。
     *
     * @param imageNo imageNo
     */
    public void setImageNo(Long imageNo) {
        this.imageNo = imageNo;
    }

    /**
     * instanceTypeを取得します。
     *
     * @return instanceType
     */
    public String getInstanceType() {
        return instanceType;
    }

    /**
     * instanceTypeを設定します。
     *
     * @param instanceType instanceType
     */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    /**
     * idleTimeMaxを取得します。
     *
     * @return idleTimeMax
     */
    public Long getIdleTimeMax() {
        return idleTimeMax;
    }

    /**
     * idleTimeMaxを設定します。
     *
     * @param idleTimeMax idleTimeMax
     */
    public void setIdleTimeMax(Long idleTimeMax) {
        this.idleTimeMax = idleTimeMax;
    }

    /**
     * idleTimeMinを取得します。
     *
     * @return idleTimeMin
     */
    public Long getIdleTimeMin() {
        return idleTimeMin;
    }

    /**
     * idleTimeMinを設定します。
     *
     * @param idleTimeMin idleTimeMin
     */
    public void setIdleTimeMin(Long idleTimeMin) {
        this.idleTimeMin = idleTimeMin;
    }

    /**
     * continueLimitを取得します。
     *
     * @return continueLimit
     */
    public Long getContinueLimit() {
        return continueLimit;
    }

    /**
     * continueLimitを設定します。
     *
     * @param continueLimit continueLimit
     */
    public void setContinueLimit(Long continueLimit) {
        this.continueLimit = continueLimit;
    }

    /**
     * addCountを取得します。
     *
     * @return addCount
     */
    public Long getAddCount() {
        return addCount;
    }

    /**
     * addCountを設定します。
     *
     * @param addCount addCount
     */
    public void setAddCount(Long addCount) {
        this.addCount = addCount;
    }

    /**
     * delCountを取得します。
     *
     * @return delCount
     */
    public Long getDelCount() {
        return delCount;
    }

    /**
     * delCountを設定します。
     *
     * @param delCount delCount
     */
    public void setDelCount(Long delCount) {
        this.delCount = delCount;
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
     * namingRuleを取得します。
     *
     * @return namingRule
     */
    public String getNamingRule() {
        return namingRule;
    }

    /**
     * namingRuleを設定します。
     *
     * @param namingRule namingRule
     */
    public void setNamingRule(String namingRule) {
        this.namingRule = namingRule;
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
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((imageNo == null) ? 0 : imageNo.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((idleTimeMax == null) ? 0 : idleTimeMax.hashCode());
        result = prime * result + ((idleTimeMin == null) ? 0 : idleTimeMin.hashCode());
        result = prime * result + ((continueLimit == null) ? 0 : continueLimit.hashCode());
        result = prime * result + ((addCount == null) ? 0 : addCount.hashCode());
        result = prime * result + ((delCount == null) ? 0 : delCount.hashCode());
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((namingRule == null) ? 0 : namingRule.hashCode());

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

        final BaseAutoScalingConf other = (BaseAutoScalingConf) obj;
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
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (imageNo == null) {
            if (other.imageNo != null) { return false; }
        } else if (!imageNo.equals(other.imageNo)) {
            return false;
        }
        if (instanceType == null) {
            if (other.instanceType != null) { return false; }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (idleTimeMax == null) {
            if (other.idleTimeMax != null) { return false; }
        } else if (!idleTimeMax.equals(other.idleTimeMax)) {
            return false;
        }
        if (idleTimeMin == null) {
            if (other.idleTimeMin != null) { return false; }
        } else if (!idleTimeMin.equals(other.idleTimeMin)) {
            return false;
        }
        if (continueLimit == null) {
            if (other.continueLimit != null) { return false; }
        } else if (!continueLimit.equals(other.continueLimit)) {
            return false;
        }
        if (addCount == null) {
            if (other.addCount != null) { return false; }
        } else if (!addCount.equals(other.addCount)) {
            return false;
        }
        if (delCount == null) {
            if (other.delCount != null) { return false; }
        } else if (!delCount.equals(other.delCount)) {
            return false;
        }
        if (enabled == null) {
            if (other.enabled != null) { return false; }
        } else if (!enabled.equals(other.enabled)) {
            return false;
        }
        if (namingRule == null) {
            if (other.namingRule != null) { return false; }
        } else if (!namingRule.equals(other.namingRule)) {
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
        sb.append("AutoScalingConf").append(" [");
        sb.append("loadBalancerNo=").append(loadBalancerNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("imageNo=").append(imageNo).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("idleTimeMax=").append(idleTimeMax).append(", ");
        sb.append("idleTimeMin=").append(idleTimeMin).append(", ");
        sb.append("continueLimit=").append(continueLimit).append(", ");
        sb.append("addCount=").append(addCount).append(", ");
        sb.append("delCount=").append(delCount).append(", ");
        sb.append("enabled=").append(enabled).append(", ");
        sb.append("namingRule=").append(namingRule);
        sb.append("]");
        return sb.toString();
    }

}
