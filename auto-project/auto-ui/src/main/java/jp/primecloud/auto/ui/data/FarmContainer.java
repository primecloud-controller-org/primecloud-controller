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
package jp.primecloud.auto.ui.data;

import java.io.Serializable;

import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ViewContext;

import com.vaadin.data.util.BeanItemContainer;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class FarmContainer extends BeanItemContainer<Farm> implements Serializable {

    /**
     * Natural property order for Farm bean. Used in tables and forms.
     */
    public static final Object[] NATURAL_COL_ORDER = new Object[] { "farmNo", "farmName", "domainName", "comment" };

    /**
     * "Human readable" captions for properties in same order as in
     * NATURAL_COL_ORDER.
     */
    public static final String[] COL_HEADERS_ENGLISH = new String[] { "farmNo", "farmName", "domainName", "comment" };

    public FarmContainer() {
        super(Farm.class);

        // ロジックを実行
        FarmService farmService = BeanContext.getBean(FarmService.class);
        for (FarmDto farmDto : farmService.getFarms(ViewContext.getUserNo(), ViewContext.getLoginUser())) {
            addItem(farmDto.getFarm());
        }
    }

}
