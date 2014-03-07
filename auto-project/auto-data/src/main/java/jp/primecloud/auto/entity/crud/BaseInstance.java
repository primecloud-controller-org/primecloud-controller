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
 * INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** INSTANCE_NAME [VARCHAR(30,0)] */
    private String instanceName;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** IMAGE_NO [BIGINT(19,0)] */
    private Long imageNo;

    /** ENABLED [BIT(0,0)] */
    private Boolean enabled;

    /** COMMENT [VARCHAR(100,0)] */
    private String comment;

    /** FQDN [VARCHAR(100,0)] */
    private String fqdn;

    /** INSTANCE_CODE [VARCHAR(30,0)] */
    private String instanceCode;

    /** PUBLIC_IP [VARCHAR(100,0)] */
    private String publicIp;

    /** PRIVATE_IP [VARCHAR(100,0)] */
    private String privateIp;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

    /** PROGRESS [INT(10,0)] */
    private Integer progress;

    /** COODINATE_STATUS [VARCHAR(20,0)] */
    private String coodinateStatus;

    /** LOAD_BALANCER [BIT(0,0)] */
    private Boolean loadBalancer;

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
     * instanceNameを取得します。
     *
     * @return instanceName
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * instanceNameを設定します。
     *
     * @param instanceName instanceName
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
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
     * fqdnを取得します。
     *
     * @return fqdn
     */
    public String getFqdn() {
        return fqdn;
    }

    /**
     * fqdnを設定します。
     *
     * @param fqdn fqdn
     */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    /**
     * instanceCodeを取得します。
     *
     * @return instanceCode
     */
    public String getInstanceCode() {
        return instanceCode;
    }

    /**
     * instanceCodeを設定します。
     *
     * @param instanceCode instanceCode
     */
    public void setInstanceCode(String instanceCode) {
        this.instanceCode = instanceCode;
    }

    /**
     * publicIpを取得します。
     *
     * @return publicIp
     */
    public String getPublicIp() {
        return publicIp;
    }

    /**
     * publicIpを設定します。
     *
     * @param publicIp publicIp
     */
    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    /**
     * privateIpを取得します。
     *
     * @return privateIp
     */
    public String getPrivateIp() {
        return privateIp;
    }

    /**
     * privateIpを設定します。
     *
     * @param privateIp privateIp
     */
    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
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
     * progressを取得します。
     *
     * @return progress
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * progressを設定します。
     *
     * @param progress progress
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * coodinateStatusを取得します。
     *
     * @return coodinateStatus
     */
    public String getCoodinateStatus() {
        return coodinateStatus;
    }

    /**
     * coodinateStatusを設定します。
     *
     * @param coodinateStatus coodinateStatus
     */
    public void setCoodinateStatus(String coodinateStatus) {
        this.coodinateStatus = coodinateStatus;
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
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((imageNo == null) ? 0 : imageNo.hashCode());
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((fqdn == null) ? 0 : fqdn.hashCode());
        result = prime * result + ((instanceCode == null) ? 0 : instanceCode.hashCode());
        result = prime * result + ((publicIp == null) ? 0 : publicIp.hashCode());
        result = prime * result + ((privateIp == null) ? 0 : privateIp.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((progress == null) ? 0 : progress.hashCode());
        result = prime * result + ((coodinateStatus == null) ? 0 : coodinateStatus.hashCode());
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

        final BaseInstance other = (BaseInstance) obj;
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (instanceName == null) {
            if (other.instanceName != null) { return false; }
        } else if (!instanceName.equals(other.instanceName)) {
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
        if (enabled == null) {
            if (other.enabled != null) { return false; }
        } else if (!enabled.equals(other.enabled)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) { return false; }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (fqdn == null) {
            if (other.fqdn != null) { return false; }
        } else if (!fqdn.equals(other.fqdn)) {
            return false;
        }
        if (instanceCode == null) {
            if (other.instanceCode != null) { return false; }
        } else if (!instanceCode.equals(other.instanceCode)) {
            return false;
        }
        if (publicIp == null) {
            if (other.publicIp != null) { return false; }
        } else if (!publicIp.equals(other.publicIp)) {
            return false;
        }
        if (privateIp == null) {
            if (other.privateIp != null) { return false; }
        } else if (!privateIp.equals(other.privateIp)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (progress == null) {
            if (other.progress != null) { return false; }
        } else if (!progress.equals(other.progress)) {
            return false;
        }
        if (coodinateStatus == null) {
            if (other.coodinateStatus != null) { return false; }
        } else if (!coodinateStatus.equals(other.coodinateStatus)) {
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
        sb.append("Instance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("instanceName=").append(instanceName).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("imageNo=").append(imageNo).append(", ");
        sb.append("enabled=").append(enabled).append(", ");
        sb.append("comment=").append(comment).append(", ");
        sb.append("fqdn=").append(fqdn).append(", ");
        sb.append("instanceCode=").append(instanceCode).append(", ");
        sb.append("publicIp=").append(publicIp).append(", ");
        sb.append("privateIp=").append(privateIp).append(", ");
        sb.append("status=").append(status).append(", ");
        sb.append("progress=").append(progress).append(", ");
        sb.append("coodinateStatus=").append(coodinateStatus).append(", ");
        sb.append("loadBalancer=").append(loadBalancer);
        sb.append("]");
        return sb.toString();
    }

}
