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
import java.util.List;

import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.ui.ServiceTable;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ViewContext;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ComponentDtoContainer extends BeanItemContainer<ComponentDto> implements
		Serializable {

	/**
	 * Natural property order for Farm bean. Used in tables and forms.
	 */
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
	    "componentNo", "componentName", "status"
	};

	/**
	 * "Human readable" captions for properties in same order as in
	 * NATURAL_COL_ORDER.
	 */
	public static final String[] COL_HEADERS_ENGLISH = new String[] {
        "no", "name", "status"
	};

    public static final Object[] SERVICE_DESC = new Object[] {
        "componentName", "serviceDetail", "status"
    };

    public static final Object[] SERVER_DESC = new Object[] {
        "componentName", "urlIcon" , "status", "serviceDetail"
    };

	public ComponentDtoContainer() {

		super(ComponentDto.class);
		refresh();

	}

    public ComponentDtoContainer(Collection<ComponentDto> components) {

        super(ComponentDto.class);

        for (ComponentDto dto : components) {
            addItem(dto);
        }
    }

    public void refresh() {

        // ロジックを実行
        removeAllItems();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            ComponentService componentService = BeanContext.getBean(ComponentService.class);
            for (ComponentDto componentDto : componentService.getComponents(farmNo)) {
                addItem(componentDto);
            }
        }
    }

    public void refresh2(ServiceTable table) {

        // ロジックを実行
        Collection<ComponentDto> collection = this.getItemIds();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            ComponentService componentService = BeanContext.getBean(ComponentService.class);
            Object[] o = collection.toArray();  //現在のitem
            List<ComponentDto> dtos = componentService.getComponents(farmNo); //取得したデータ
            for (int i = 0; i < o.length; i++) {
                ComponentDto oldComponent = (ComponentDto) o[i];
                for (int j = 0; j < dtos.size(); j++) {
                    ComponentDto newComponent = dtos.get(j);
                    if (oldComponent.getComponent().getComponentNo().equals(
                            newComponent.getComponent().getComponentNo())) {
                        final BeanItem<ComponentDto> dto = this.getItem(o[i]);
                        dto.getItemProperty("component").setValue(newComponent.getComponent());
                        dto.getItemProperty("componentType").setValue(newComponent.getComponentType());
                        dto.getItemProperty("componentConfigs").setValue(newComponent.getComponentConfigs());
                        dto.getItemProperty("componentInstances").setValue(newComponent.getComponentInstances());
                        dto.getItemProperty("instanceConfigs").setValue(newComponent.getInstanceConfigs());
                        dto.getItemProperty("status").setValue(newComponent.getStatus());
                        dtos.remove(newComponent);
                        break;
                    } else {
                        if (dtos.size() == j + 1) {
                            removeItem(oldComponent);
                        }
                    }
                }
            }
            for (ComponentDto component : dtos) {
                addItem(component);
            }
        }

        final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
            private static final long serialVersionUID = -3002746333251784195L;

            public Container getContainer() {
                return ComponentDtoContainer.this;
            }
        };

        table.containerItemSetChange(event);
        table.refreshDesc();

    }

}
