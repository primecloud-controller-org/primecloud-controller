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

import jp.primecloud.auto.entity.crud.PlatformVcloudStorageType;

public class StorageTypeDto implements Serializable {

    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = 1L;

    private Long storageTypeNo;

    private String storageTypeName;

    public StorageTypeDto(PlatformVcloudStorageType storageType) {
        this.storageTypeNo = storageType.getStorageTypeNo();
        this.storageTypeName = storageType.getStorageTypeName();
    }

    public Long getStorageTypeNo() {
        return storageTypeNo;
    }

    public void setStorageTypeNo(Long storageTypeNo) {
        this.storageTypeNo = storageTypeNo;
    }

    public String getStorageTypeName() {
        return storageTypeName;
    }

    public void setStorageTypeName(String storageTypeName) {
        this.storageTypeName = storageTypeName;
    }

}
