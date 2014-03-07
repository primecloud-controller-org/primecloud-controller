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
package jp.primecloud.auto.service.dto;

import java.io.Serializable;

import jp.primecloud.auto.entity.crud.UserAuth;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class UserAuthDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean farmUse = false;
    private boolean serverMake = false;
    private boolean serverDelete = false;
    private boolean serverOperate = false;
    private boolean serviceMake = false;
    private boolean serviceDelete = false;
    private boolean serviceOperate = false;
    private boolean lbMake = false;
    private boolean lbDelete = false;
    private boolean lbOperate = false;

    public UserAuthDto(boolean isTrue) {
        farmUse = isTrue;
        serverMake = isTrue;
        serverDelete = isTrue;
        serverOperate = isTrue;
        serviceMake = isTrue;
        serviceDelete = isTrue;
        serviceOperate = isTrue;
        lbMake = isTrue;
        lbDelete = isTrue;
        lbOperate = isTrue;
    }

    public UserAuthDto(UserAuth auth) {
        farmUse = auth.getFarmUse();
        serverMake = auth.getServerMake();
        serverDelete = auth.getServerDelete();
        serverOperate = auth.getServerOperate();
        serviceMake = auth.getServiceMake();
        serviceDelete = auth.getServiceDelete();
        serviceOperate = auth.getServiceOperate();
        lbMake = auth.getLbMake();
        lbDelete = auth.getLbDelete();
        lbOperate = auth.getLbOperate();
    }


    public boolean isFarmUse() {
        return farmUse;
    }

    public void setFarmUse(boolean farmUse) {
        this.farmUse = farmUse;
    }

    public boolean isServerMake() {
        return serverMake;
    }

    public void setServerMake(boolean serverMake) {
        this.serverMake = serverMake;
    }

    public boolean isServerDelete() {
        return serverDelete;
    }

    public void setServerDelete(boolean serverDelete) {
        this.serverDelete = serverDelete;
    }

    public boolean isServerOperate() {
        return serverOperate;
    }

    public void setServerOperate(boolean serverOperate) {
        this.serverOperate = serverOperate;
    }

    public boolean isServiceMake() {
        return serviceMake;
    }

    public void setServiceMake(boolean serviceMake) {
        this.serviceMake = serviceMake;
    }

    public boolean isServiceDelete() {
        return serviceDelete;
    }

    public void setServiceDelete(boolean serviceDelete) {
        this.serviceDelete = serviceDelete;
    }

    public boolean isServiceOperate() {
        return serviceOperate;
    }

    public void setServiceOperate(boolean serviceOperate) {
        this.serviceOperate = serviceOperate;
    }

    public boolean isLbMake() {
        return lbMake;
    }

    public void setLbMake(boolean lbMake) {
        this.lbMake = lbMake;
    }

    public boolean isLbDelete() {
        return lbDelete;
    }

    public void setLbDelete(boolean lbDelete) {
        this.lbDelete = lbDelete;
    }

    public boolean isLbOperate() {
        return lbOperate;
    }

    public void setLbOperate(boolean lbOperate) {
        this.lbOperate = lbOperate;
    }

    @Override
    public String toString()
    {
        return "[UserAuthDto: farmUse=" + serverMake +
            " serverMake=" + serverDelete +
            " serverDelete=" + serverDelete +
            " serverOperate=" + serverOperate +
            " serviceMake=" + serviceMake +
            " serviceDelete=" + serviceDelete +
            " serviceOperate=" + serviceOperate +
            " lbMake=" + lbMake +
            " lbDelete=" + lbDelete +
            " lbOperate=" + lbOperate +
            "]";
    }
}
