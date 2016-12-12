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
 * IMAGE_CLOUDSTACKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseImageCloudstack implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** IMAGE_NO [BIGINT(19,0)] */
    private Long imageNo;

    /** TEMPLATE_ID [VARCHAR(100,0)] */
    private String templateId;

    /** INSTANCE_TYPES [VARCHAR(500,0)] */
    private String instanceTypes;

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
     * templateIdを取得します。
     *
     * @return templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * templateIdを設定します。
     *
     * @param templateId templateId
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * instanceTypesを取得します。
     *
     * @return instanceTypes
     */
    public String getInstanceTypes() {
        return instanceTypes;
    }

    /**
     * instanceTypesを設定します。
     *
     * @param instanceTypes instanceTypes
     */
    public void setInstanceTypes(String instanceTypes) {
        this.instanceTypes = instanceTypes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((imageNo == null) ? 0 : imageNo.hashCode());
        result = prime * result + ((templateId == null) ? 0 : templateId.hashCode());
        result = prime * result + ((instanceTypes == null) ? 0 : instanceTypes.hashCode());

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

        final BaseImageCloudstack other = (BaseImageCloudstack) obj;
        if (imageNo == null) {
            if (other.imageNo != null) { return false; }
        } else if (!imageNo.equals(other.imageNo)) {
            return false;
        }
        if (templateId == null) {
            if (other.templateId != null) { return false; }
        } else if (!templateId.equals(other.templateId)) {
            return false;
        }
        if (instanceTypes == null) {
            if (other.instanceTypes != null) { return false; }
        } else if (!instanceTypes.equals(other.instanceTypes)) {
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
        sb.append("ImageCloudstack").append(" [");
        sb.append("imageNo=").append(imageNo).append(", ");
        sb.append("templateId=").append(templateId).append(", ");
        sb.append("instanceTypes=").append(instanceTypes);
        sb.append("]");
        return sb.toString();
    }

}
