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
 * IAAS_INFOに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseIaasInfo implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** IAAS_NO [BIGINT(19,0)] */
    private Long iaasNo;

    /** IAAS_NAME [VARCHAR(50,0)] */
    private String iaasName;

    /** COMMENT [VARCHAR(300,0)] */
    private String comment;

    /**
     * iaasNoを取得します。
     *
     * @return iaasNo
     */
    public Long getIaasNo() {
        return iaasNo;
    }

    /**
     * iaasNoを設定します。
     *
     * @param iaasNo iaasNo
     */
    public void setIaasNo(Long iaasNo) {
        this.iaasNo = iaasNo;
    }

    /**
     * iaasNameを取得します。
     *
     * @return iaasName
     */
    public String getIaasName() {
        return iaasName;
    }

    /**
     * iaasNameを設定します。
     *
     * @param iaasName iaasName
     */
    public void setIaasName(String iaasName) {
        this.iaasName = iaasName;
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((iaasNo == null) ? 0 : iaasNo.hashCode());
        result = prime * result + ((iaasName == null) ? 0 : iaasName.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());

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

        final BaseIaasInfo other = (BaseIaasInfo) obj;
        if (iaasNo == null) {
            if (other.iaasNo != null) { return false; }
        } else if (!iaasNo.equals(other.iaasNo)) {
            return false;
        }
        if (iaasName == null) {
            if (other.iaasName != null) { return false; }
        } else if (!iaasName.equals(other.iaasName)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) { return false; }
        } else if (!comment.equals(other.comment)) {
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
        sb.append("IaasInfo").append(" [");
        sb.append("iaasNo=").append(iaasNo).append(", ");
        sb.append("iaasName=").append(iaasName).append(", ");
        sb.append("comment=").append(comment);
        sb.append("]");
        return sb.toString();
    }

}
