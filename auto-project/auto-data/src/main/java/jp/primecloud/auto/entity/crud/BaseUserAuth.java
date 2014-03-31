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
 * user_authに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseUserAuth implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** FARM_USE [BIT(0,0)] */
    private Boolean farmUse;

    /** SERVER_MAKE [BIT(0,0)] */
    private Boolean serverMake;

    /** SERVER_DELETE [BIT(0,0)] */
    private Boolean serverDelete;

    /** SERVER_OPERATE [BIT(0,0)] */
    private Boolean serverOperate;

    /** SERVICE_MAKE [BIT(0,0)] */
    private Boolean serviceMake;

    /** SERVICE_DELETE [BIT(0,0)] */
    private Boolean serviceDelete;

    /** SERVICE_OPERATE [BIT(0,0)] */
    private Boolean serviceOperate;

    /** LB_MAKE [BIT(0,0)] */
    private Boolean lbMake;

    /** LB_DELETE [BIT(0,0)] */
    private Boolean lbDelete;

    /** LB_OPERATE [BIT(0,0)] */
    private Boolean lbOperate;

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
     * farmUseを取得します。
     *
     * @return farmUse
     */
    public Boolean getFarmUse() {
        return farmUse;
    }

    /**
     * farmUseを設定します。
     *
     * @param farmUse farmUse
     */
    public void setFarmUse(Boolean farmUse) {
        this.farmUse = farmUse;
    }

    /**
     * serverMakeを取得します。
     *
     * @return serverMake
     */
    public Boolean getServerMake() {
        return serverMake;
    }

    /**
     * serverMakeを設定します。
     *
     * @param serverMake serverMake
     */
    public void setServerMake(Boolean serverMake) {
        this.serverMake = serverMake;
    }

    /**
     * serverDeleteを取得します。
     *
     * @return serverDelete
     */
    public Boolean getServerDelete() {
        return serverDelete;
    }

    /**
     * serverDeleteを設定します。
     *
     * @param serverDelete serverDelete
     */
    public void setServerDelete(Boolean serverDelete) {
        this.serverDelete = serverDelete;
    }

    /**
     * serverOperateを取得します。
     *
     * @return serverOperate
     */
    public Boolean getServerOperate() {
        return serverOperate;
    }

    /**
     * serverOperateを設定します。
     *
     * @param serverOperate serverOperate
     */
    public void setServerOperate(Boolean serverOperate) {
        this.serverOperate = serverOperate;
    }

    /**
     * serviceMakeを取得します。
     *
     * @return serviceMake
     */
    public Boolean getServiceMake() {
        return serviceMake;
    }

    /**
     * serviceMakeを設定します。
     *
     * @param serviceMake serviceMake
     */
    public void setServiceMake(Boolean serviceMake) {
        this.serviceMake = serviceMake;
    }

    /**
     * serviceDeleteを取得します。
     *
     * @return serviceDelete
     */
    public Boolean getServiceDelete() {
        return serviceDelete;
    }

    /**
     * serviceDeleteを設定します。
     *
     * @param serviceDelete serviceDelete
     */
    public void setServiceDelete(Boolean serviceDelete) {
        this.serviceDelete = serviceDelete;
    }

    /**
     * serviceOperateを取得します。
     *
     * @return serviceOperate
     */
    public Boolean getServiceOperate() {
        return serviceOperate;
    }

    /**
     * serviceOperateを設定します。
     *
     * @param serviceOperate serviceOperate
     */
    public void setServiceOperate(Boolean serviceOperate) {
        this.serviceOperate = serviceOperate;
    }

    /**
     * lbMakeを取得します。
     *
     * @return lbMake
     */
    public Boolean getLbMake() {
        return lbMake;
    }

    /**
     * lbMakeを設定します。
     *
     * @param lbMake lbMake
     */
    public void setLbMake(Boolean lbMake) {
        this.lbMake = lbMake;
    }

    /**
     * lbDeleteを取得します。
     *
     * @return lbDelete
     */
    public Boolean getLbDelete() {
        return lbDelete;
    }

    /**
     * lbDeleteを設定します。
     *
     * @param lbDelete lbDelete
     */
    public void setLbDelete(Boolean lbDelete) {
        this.lbDelete = lbDelete;
    }

    /**
     * lbOperateを取得します。
     *
     * @return lbOperate
     */
    public Boolean getLbOperate() {
        return lbOperate;
    }

    /**
     * lbOperateを設定します。
     *
     * @param lbOperate lbOperate
     */
    public void setLbOperate(Boolean lbOperate) {
        this.lbOperate = lbOperate;
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
        result = prime * result + ((farmUse == null) ? 0 : farmUse.hashCode());
        result = prime * result + ((serverMake == null) ? 0 : serverMake.hashCode());
        result = prime * result + ((serverDelete == null) ? 0 : serverDelete.hashCode());
        result = prime * result + ((serverOperate == null) ? 0 : serverOperate.hashCode());
        result = prime * result + ((serviceMake == null) ? 0 : serviceMake.hashCode());
        result = prime * result + ((serviceDelete == null) ? 0 : serviceDelete.hashCode());
        result = prime * result + ((serviceOperate == null) ? 0 : serviceOperate.hashCode());
        result = prime * result + ((lbMake == null) ? 0 : lbMake.hashCode());
        result = prime * result + ((lbDelete == null) ? 0 : lbDelete.hashCode());
        result = prime * result + ((lbOperate == null) ? 0 : lbOperate.hashCode());

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

        final BaseUserAuth other = (BaseUserAuth) obj;
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
        if (farmUse == null) {
            if (other.farmUse != null) { return false; }
        } else if (!farmUse.equals(other.farmUse)) {
            return false;
        }
        if (serverMake == null) {
            if (other.serverMake != null) { return false; }
        } else if (!serverMake.equals(other.serverMake)) {
            return false;
        }
        if (serverDelete == null) {
            if (other.serverDelete != null) { return false; }
        } else if (!serverDelete.equals(other.serverDelete)) {
            return false;
        }
        if (serverOperate == null) {
            if (other.serverOperate != null) { return false; }
        } else if (!serverOperate.equals(other.serverOperate)) {
            return false;
        }
        if (serviceMake == null) {
            if (other.serviceMake != null) { return false; }
        } else if (!serviceMake.equals(other.serviceMake)) {
            return false;
        }
        if (serviceDelete == null) {
            if (other.serviceDelete != null) { return false; }
        } else if (!serviceDelete.equals(other.serviceDelete)) {
            return false;
        }
        if (serviceOperate == null) {
            if (other.serviceOperate != null) { return false; }
        } else if (!serviceOperate.equals(other.serviceOperate)) {
            return false;
        }
        if (lbMake == null) {
            if (other.lbMake != null) { return false; }
        } else if (!lbMake.equals(other.lbMake)) {
            return false;
        }
        if (lbDelete == null) {
            if (other.lbDelete != null) { return false; }
        } else if (!lbDelete.equals(other.lbDelete)) {
            return false;
        }
        if (lbOperate == null) {
            if (other.lbOperate != null) { return false; }
        } else if (!lbOperate.equals(other.lbOperate)) {
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
        sb.append("UserAuth").append(" [");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("farmUse=").append(farmUse).append(", ");
        sb.append("serverMake=").append(serverMake).append(", ");
        sb.append("serverDelete=").append(serverDelete).append(", ");
        sb.append("serverOperate=").append(serverOperate).append(", ");
        sb.append("serviceMake=").append(serviceMake).append(", ");
        sb.append("serviceDelete=").append(serviceDelete).append(", ");
        sb.append("serviceOperate=").append(serviceOperate).append(", ");
        sb.append("lbMake=").append(lbMake).append(", ");
        sb.append("lbDelete=").append(lbDelete).append(", ");
        sb.append("lbOperate=").append(lbOperate);
        sb.append("]");
        return sb.toString();
    }

}
