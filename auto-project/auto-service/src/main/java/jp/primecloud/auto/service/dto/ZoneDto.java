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

public class ZoneDto implements Serializable {

    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = 1316029562243214773L;

    private String zoneId;

    private String zoneName;

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public ZoneDto withZoneName(String zoneName){
        this.zoneName = zoneName;
        return this;
    }

    public boolean isSameById(String id){
        if(zoneId == null && id != null)return false;

        if(zoneId.equals(id)) return true;
        else return false;
    }


}
