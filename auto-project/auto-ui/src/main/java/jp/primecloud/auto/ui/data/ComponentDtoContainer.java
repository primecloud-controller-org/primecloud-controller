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
public class ComponentDtoContainer extends BeanItemContainer<ComponentDto> implements Serializable {

    public ComponentDtoContainer() {
        super(ComponentDto.class);
        refresh();
    }

    public ComponentDtoContainer(Collection<ComponentDto> components) {
        super(ComponentDto.class);

        for (ComponentDto component : components) {
            addItem(component);
        }
    }

    public void refresh() {
        // ロジックを実行
        removeAllItems();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            ComponentService componentService = BeanContext.getBean(ComponentService.class);
            for (ComponentDto component : componentService.getComponents(farmNo)) {
                addItem(component);
            }
        }
    }

    public void refresh2(ServiceTable table) {
        // ロジックを実行
        Collection<ComponentDto> collection = this.getItemIds();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            ComponentService componentService = BeanContext.getBean(ComponentService.class);
            Object[] o = collection.toArray(); //現在のitem
            List<ComponentDto> components = componentService.getComponents(farmNo); //取得したデータ
            for (int i = 0; i < o.length; i++) {
                ComponentDto oldComponent = (ComponentDto) o[i];
                for (int j = 0; j < components.size(); j++) {
                    ComponentDto newComponent = components.get(j);
                    if (oldComponent.getComponent().getComponentNo()
                            .equals(newComponent.getComponent().getComponentNo())) {
                        final BeanItem<ComponentDto> item = this.getItem(o[i]);
                        item.getItemProperty("component").setValue(newComponent.getComponent());
                        item.getItemProperty("componentType").setValue(newComponent.getComponentType());
                        item.getItemProperty("componentConfigs").setValue(newComponent.getComponentConfigs());
                        item.getItemProperty("componentInstances").setValue(newComponent.getComponentInstances());
                        item.getItemProperty("instanceConfigs").setValue(newComponent.getInstanceConfigs());
                        item.getItemProperty("status").setValue(newComponent.getStatus());
                        components.remove(newComponent);
                        break;
                    } else {
                        if (components.size() == j + 1) {
                            removeItem(oldComponent);
                        }
                    }
                }
            }
            for (ComponentDto component : components) {
                addItem(component);
            }
        }

        final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
            @Override
            public Container getContainer() {
                return ComponentDtoContainer.this;
            }
        };

        table.containerItemSetChange(event);
        table.refreshDesc();
    }

}
