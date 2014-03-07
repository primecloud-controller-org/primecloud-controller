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

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long farmNo;

    private String componentName;

    private Long componentTypeNo;

    private String comment;

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
     * componentNameを取得します。
     *
     * @return componentName
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * componentNameを設定します。
     *
     * @param componentName componentName
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
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

    
    
    
}
