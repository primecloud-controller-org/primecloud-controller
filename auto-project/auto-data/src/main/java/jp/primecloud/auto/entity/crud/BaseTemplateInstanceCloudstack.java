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
 * TEMPLATE_INSTANCE_CLOUDSTACKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseTemplateInstanceCloudstack implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** TEMPLATE_INSTANCE_NO [BIGINT(19,0)] */
    private Long templateInstanceNo;

    /** TEMPLATE_INSTANCE_NAME [VARCHAR(50,0)] */
    private String templateInstanceName;

    /** TEMPLATE_NO [BIGINT(19,0)] */
    private Long templateNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** IMAGE_NO [BIGINT(19,0)] */
    private Long imageNo;

    /** COMMENT [VARCHAR(100,0)] */
    private String comment;

    /** INSTANCE_TYPE [VARCHAR(100,0)] */
    private String instanceType;

    /**
     * templateInstanceNoを取得します。
     *
     * @return templateInstanceNo
     */
    public Long getTemplateInstanceNo() {
        return templateInstanceNo;
    }

    /**
     * templateInstanceNoを設定します。
     *
     * @param templateInstanceNo templateInstanceNo
     */
    public void setTemplateInstanceNo(Long templateInstanceNo) {
        this.templateInstanceNo = templateInstanceNo;
    }

    /**
     * templateInstanceNameを取得します。
     *
     * @return templateInstanceName
     */
    public String getTemplateInstanceName() {
        return templateInstanceName;
    }

    /**
     * templateInstanceNameを設定します。
     *
     * @param templateInstanceName templateInstanceName
     */
    public void setTemplateInstanceName(String templateInstanceName) {
        this.templateInstanceName = templateInstanceName;
    }

    /**
     * templateNoを取得します。
     *
     * @return templateNo
     */
    public Long getTemplateNo() {
        return templateNo;
    }

    /**
     * templateNoを設定します。
     *
     * @param templateNo templateNo
     */
    public void setTemplateNo(Long templateNo) {
        this.templateNo = templateNo;
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((templateInstanceNo == null) ? 0 : templateInstanceNo.hashCode());
        result = prime * result + ((templateInstanceName == null) ? 0 : templateInstanceName.hashCode());
        result = prime * result + ((templateNo == null) ? 0 : templateNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((imageNo == null) ? 0 : imageNo.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());

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

        final BaseTemplateInstanceCloudstack other = (BaseTemplateInstanceCloudstack) obj;
        if (templateInstanceNo == null) {
            if (other.templateInstanceNo != null) { return false; }
        } else if (!templateInstanceNo.equals(other.templateInstanceNo)) {
            return false;
        }
        if (templateInstanceName == null) {
            if (other.templateInstanceName != null) { return false; }
        } else if (!templateInstanceName.equals(other.templateInstanceName)) {
            return false;
        }
        if (templateNo == null) {
            if (other.templateNo != null) { return false; }
        } else if (!templateNo.equals(other.templateNo)) {
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
        if (comment == null) {
            if (other.comment != null) { return false; }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (instanceType == null) {
            if (other.instanceType != null) { return false; }
        } else if (!instanceType.equals(other.instanceType)) {
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
        sb.append("TemplateInstanceCloudstack").append(" [");
        sb.append("templateInstanceNo=").append(templateInstanceNo).append(", ");
        sb.append("templateInstanceName=").append(templateInstanceName).append(", ");
        sb.append("templateNo=").append(templateNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("imageNo=").append(imageNo).append(", ");
        sb.append("comment=").append(comment).append(", ");
        sb.append("instanceType=").append(instanceType);
        sb.append("]");
        return sb.toString();
    }

}
