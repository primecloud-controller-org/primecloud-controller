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
 * SYSTEM_CONFIGに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseSystemConfig implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** NAME [VARCHAR(50,0)] */
    private String name;

    /** VALUE [VARCHAR(200,0)] */
    private String value;

    /**
     * nameを取得します。
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します。
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * valueを取得します。
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * valueを設定します。
     *
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());

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

        final BaseSystemConfig other = (BaseSystemConfig) obj;
        if (name == null) {
            if (other.name != null) { return false; }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) { return false; }
        } else if (!value.equals(other.value)) {
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
        sb.append("SystemConfig").append(" [");
        sb.append("name=").append(name).append(", ");
        sb.append("value=").append(value);
        sb.append("]");
        return sb.toString();
    }

}
