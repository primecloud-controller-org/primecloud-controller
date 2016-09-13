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
import java.util.Date;

/**
 * <p>
 * VCLOUD_TASKに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseVcloudTask implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** P_ID [BIGINT(19,0)] */
    private Long PId;

    /** REGIST_TIME [DATETIME(0,0)] */
    private Date registTime;

    /** VAPP [VARCHAR(100,0)] */
    private String vapp;

    /**
     * PIdを取得します。
     *
     * @return PId
     */
    public Long getPId() {
        return PId;
    }

    /**
     * PIdを設定します。
     *
     * @param PId PId
     */
    public void setPId(Long PId) {
        this.PId = PId;
    }

    /**
     * registTimeを取得します。
     *
     * @return registTime
     */
    public Date getRegistTime() {
        return registTime;
    }

    /**
     * registTimeを設定します。
     *
     * @param registTime registTime
     */
    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    /**
     * vappを取得します。
     *
     * @return vapp
     */
    public String getVapp() {
        return vapp;
    }

    /**
     * vappを設定します。
     *
     * @param vapp vapp
     */
    public void setVapp(String vapp) {
        this.vapp = vapp;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((PId == null) ? 0 : PId.hashCode());
        result = prime * result + ((registTime == null) ? 0 : registTime.hashCode());
        result = prime * result + ((vapp == null) ? 0 : vapp.hashCode());

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

        final BaseVcloudTask other = (BaseVcloudTask) obj;
        if (PId == null) {
            if (other.PId != null) { return false; }
        } else if (!PId.equals(other.PId)) {
            return false;
        }
        if (registTime == null) {
            if (other.registTime != null) { return false; }
        } else if (!registTime.equals(other.registTime)) {
            return false;
        }
        if (vapp == null) {
            if (other.vapp != null) { return false; }
        } else if (!vapp.equals(other.vapp)) {
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
        sb.append("VcloudTask").append(" [");
        sb.append("PId=").append(PId).append(", ");
        sb.append("registTime=").append(registTime).append(", ");
        sb.append("vapp=").append(vapp);
        sb.append("]");
        return sb.toString();
    }

}
