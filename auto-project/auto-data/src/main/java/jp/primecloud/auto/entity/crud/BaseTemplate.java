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
 * TEMPLATEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseTemplate implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** TEMPLATE_NO [BIGINT(19,0)] */
    private Long templateNo;

    /** TEMPLATE_NAME [VARCHAR(50,0)] */
    private String templateName;

    /** TEMPLATE_NAME_DISP [VARCHAR(300,0)] */
    private String templateNameDisp;

    /** TEMPLATE_DESCRIPTION_DISP [VARCHAR(500,0)] */
    private String templateDescriptionDisp;

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
     * templateNameを取得します。
     *
     * @return templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * templateNameを設定します。
     *
     * @param templateName templateName
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * templateNameDispを取得します。
     *
     * @return templateNameDisp
     */
    public String getTemplateNameDisp() {
        return templateNameDisp;
    }

    /**
     * templateNameDispを設定します。
     *
     * @param templateNameDisp templateNameDisp
     */
    public void setTemplateNameDisp(String templateNameDisp) {
        this.templateNameDisp = templateNameDisp;
    }

    /**
     * templateDescriptionDispを取得します。
     *
     * @return templateDescriptionDisp
     */
    public String getTemplateDescriptionDisp() {
        return templateDescriptionDisp;
    }

    /**
     * templateDescriptionDispを設定します。
     *
     * @param templateDescriptionDisp templateDescriptionDisp
     */
    public void setTemplateDescriptionDisp(String templateDescriptionDisp) {
        this.templateDescriptionDisp = templateDescriptionDisp;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((templateNo == null) ? 0 : templateNo.hashCode());
        result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
        result = prime * result + ((templateNameDisp == null) ? 0 : templateNameDisp.hashCode());
        result = prime * result + ((templateDescriptionDisp == null) ? 0 : templateDescriptionDisp.hashCode());

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

        final BaseTemplate other = (BaseTemplate) obj;
        if (templateNo == null) {
            if (other.templateNo != null) { return false; }
        } else if (!templateNo.equals(other.templateNo)) {
            return false;
        }
        if (templateName == null) {
            if (other.templateName != null) { return false; }
        } else if (!templateName.equals(other.templateName)) {
            return false;
        }
        if (templateNameDisp == null) {
            if (other.templateNameDisp != null) { return false; }
        } else if (!templateNameDisp.equals(other.templateNameDisp)) {
            return false;
        }
        if (templateDescriptionDisp == null) {
            if (other.templateDescriptionDisp != null) { return false; }
        } else if (!templateDescriptionDisp.equals(other.templateDescriptionDisp)) {
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
        sb.append("Template").append(" [");
        sb.append("templateNo=").append(templateNo).append(", ");
        sb.append("templateName=").append(templateName).append(", ");
        sb.append("templateNameDisp=").append(templateNameDisp).append(", ");
        sb.append("templateDescriptionDisp=").append(templateDescriptionDisp);
        sb.append("]");
        return sb.toString();
    }

}
