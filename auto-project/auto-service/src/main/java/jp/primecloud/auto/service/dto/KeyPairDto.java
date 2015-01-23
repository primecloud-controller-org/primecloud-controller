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

public class KeyPairDto  implements Serializable {


    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = -3555137064603440344L;

    private String keyName;

    private String keyFingerprint;

    private Long keyNo;

    public String getKeyName() {
        return keyName;
    }
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
    public String getKeyFingerprint() {
        return keyFingerprint;
    }
    public void setKeyFingerprint(String keyFingerprint) {
        this.keyFingerprint = keyFingerprint;
    }
    public KeyPairDto withKeyName(String keyName){
        this.keyName = keyName;
        return this;
    }
    public Long getKeyNo() {
        return keyNo;
    }
    public void setKeyNo(Long keyNo) {
        this.keyNo = keyNo;
    }
}
