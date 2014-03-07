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
 * TEMPLATE_COMPONENTに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseTemplateComponent implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** TEMPLATE_COMPONENT_NO [BIGINT(19,0)] */
    private Long templateComponentNo;

    /** TEMPLATE_COMPONENT_NAME [VARCHAR(50,0)] */
    private String templateComponentName;

    /** TEMPLATE_NO [BIGINT(19,0)] */
    private Long templateNo;

    /** COMPONENT_TYPE_NO [BIGINT(19,0)] */
    private Long componentTypeNo;

    /** COMMENT [VARCHAR(100,0)] */
    private String comment;

    /** DISK_SIZE [INT(10,0)] */
    private Integer diskSize;

    /** ASSOCIATE [VARCHAR(500,0)] */
    private String associate;

    /**
     * templateComponentNoを取得します。
     *
     * @return templateComponentNo
     */
    public Long getTemplateComponentNo() {
        return templateComponentNo;
    }

    /**
     * templateComponentNoを設定します。
     *
     * @param templateComponentNo templateComponentNo
     */
    public void setTemplateComponentNo(Long templateComponentNo) {
        this.templateComponentNo = templateComponentNo;
    }

    /**
     * templateComponentNameを取得します。
     *
     * @return templateComponentName
     */
    public String getTemplateComponentName() {
        return templateComponentName;
    }

    /**
     * templateComponentNameを設定します。
     *
     * @param templateComponentName templateComponentName
     */
    public void setTemplateComponentName(String templateComponentName) {
        this.templateComponentName = templateComponentName;
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
     * diskSizeを取得します。
     *
     * @return diskSize
     */
    public Integer getDiskSize() {
        return diskSize;
    }

    /**
     * diskSizeを設定します。
     *
     * @param diskSize diskSize
     */
    public void setDiskSize(Integer diskSize) {
        this.diskSize = diskSize;
    }

    /**
     * associateを取得します。
     *
     * @return associate
     */
    public String getAssociate() {
        return associate;
    }

    /**
     * associateを設定します。
     *
     * @param associate associate
     */
    public void setAssociate(String associate) {
        this.associate = associate;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((templateComponentNo == null) ? 0 : templateComponentNo.hashCode());
        result = prime * result + ((templateComponentName == null) ? 0 : templateComponentName.hashCode());
        result = prime * result + ((templateNo == null) ? 0 : templateNo.hashCode());
        result = prime * result + ((componentTypeNo == null) ? 0 : componentTypeNo.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((diskSize == null) ? 0 : diskSize.hashCode());
        result = prime * result + ((associate == null) ? 0 : associate.hashCode());

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

        final BaseTemplateComponent other = (BaseTemplateComponent) obj;
        if (templateComponentNo == null) {
            if (other.templateComponentNo != null) { return false; }
        } else if (!templateComponentNo.equals(other.templateComponentNo)) {
            return false;
        }
        if (templateComponentName == null) {
            if (other.templateComponentName != null) { return false; }
        } else if (!templateComponentName.equals(other.templateComponentName)) {
            return false;
        }
        if (templateNo == null) {
            if (other.templateNo != null) { return false; }
        } else if (!templateNo.equals(other.templateNo)) {
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
        if (diskSize == null) {
            if (other.diskSize != null) { return false; }
        } else if (!diskSize.equals(other.diskSize)) {
            return false;
        }
        if (associate == null) {
            if (other.associate != null) { return false; }
        } else if (!associate.equals(other.associate)) {
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
        sb.append("TemplateComponent").append(" [");
        sb.append("templateComponentNo=").append(templateComponentNo).append(", ");
        sb.append("templateComponentName=").append(templateComponentName).append(", ");
        sb.append("templateNo=").append(templateNo).append(", ");
        sb.append("componentTypeNo=").append(componentTypeNo).append(", ");
        sb.append("comment=").append(comment).append(", ");
        sb.append("diskSize=").append(diskSize).append(", ");
        sb.append("associate=").append(associate);
        sb.append("]");
        return sb.toString();
    }

}
