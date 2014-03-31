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
import java.util.Collection;

import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.dto.ComponentDto;
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
public class ComponentContainer extends BeanItemContainer<Component> implements Serializable {

    /**
     * Natural property order for Farm bean. Used in tables and forms.
     */
    public static final Object[] NATURAL_COL_ORDER = new Object[] { "componentNo", "componentName", "status" };

    /**
     * "Human readable" captions for properties in same order as in
     * NATURAL_COL_ORDER.
     */
    public static final String[] COL_HEADERS_ENGLISH = new String[] { "no", "name", "status" };

    public static final Object[] SERVICE_DESC = new Object[] { "componentName", "serviceDetail", "status" };

    public static final Object[] SERVER_DESC = new Object[] {
    //        "componentName", "serviceDetail", "status"
//             "componentName", "serviceDetail" };
    "componentName", "urlIcon" , "status", "serviceDetail" };

    public ComponentContainer() {
        super(Component.class);
        refresh();

    }

    public ComponentContainer(Collection<ComponentDto> components) {

        super(Component.class);

        for (ComponentDto dto : components) {
            addItem(dto.getComponent());
        }
    }

    public void refresh() {

        // ロジックを実行
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        removeAllItems();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            for (ComponentDto componentDto : componentService.getComponents(farmNo)) {
                addItem(componentDto);
            }
        }
    }

}
