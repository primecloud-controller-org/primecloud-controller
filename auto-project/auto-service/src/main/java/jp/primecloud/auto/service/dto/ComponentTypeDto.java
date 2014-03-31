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
import java.util.List;

import jp.primecloud.auto.entity.crud.ComponentType;




/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentTypeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ComponentType componentType;

    private List<Long> instanceNos;

    /**
     * componentTypeを取得します。
     *
     * @return componentType
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * componentTypeを設定します。
     *
     * @param componentType componentType
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * instanceNosを取得します。
     *
     * @return instanceNos
     */
    public List<Long> getInstanceNos() {
        return instanceNos;
    }

    /**
     * instanceNosを設定します。
     *
     * @param instanceNos instanceNos
     */
    public void setInstanceNos(List<Long> instanceNos) {
        this.instanceNos = instanceNos;
    }

}
