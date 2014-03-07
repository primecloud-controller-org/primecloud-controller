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

import jp.primecloud.auto.entity.crud.ComponentInstance;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentInstanceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ComponentInstance componentInstance;

    private String url;

    /**
     * componentInstanceを取得します。
     *
     * @return componentInstance
     */
    public ComponentInstance getComponentInstance() {
        return componentInstance;
    }

    /**
     * componentInstanceを設定します。
     *
     * @param componentInstance componentInstance
     */
    public void setComponentInstance(ComponentInstance componentInstance) {
        this.componentInstance = componentInstance;
    }

    /**
     * urlを取得します。
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
