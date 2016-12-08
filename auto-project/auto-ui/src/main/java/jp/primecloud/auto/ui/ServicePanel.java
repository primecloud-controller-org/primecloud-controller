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
package jp.primecloud.auto.ui;

import java.util.Collection;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.ui.data.ComponentDtoContainer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class ServicePanel extends Panel {

    private MainView sender;

    private ServiceButtonsTop serviceButtonsTop;

    ServiceTable serviceTable;

    private ServiceButtonsBottom serviceButtonsBottom;

    private ServiceDesc serviceDesc;

    public ServicePanel(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setSizeFull();
        layout.addStyleName("service-tab");
        layout.setSpacing(false);
        layout.setMargin(false);

        // スプリットパネル
        SplitPanel splitPanel = new SplitPanel();
        splitPanel.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splitPanel.setSplitPosition(40);
        splitPanel.setSizeFull();
        layout.addComponent(splitPanel);
        layout.setExpandRatio(splitPanel, 10);

        // スプリットパネル上段
        VerticalLayout upperLayout = new VerticalLayout();
        upperLayout.setSizeFull();
        upperLayout.setSpacing(false);
        upperLayout.setMargin(false);

        serviceButtonsTop = new ServiceButtonsTop(sender);
        upperLayout.addComponent(serviceButtonsTop);

        serviceTable = new ServiceTable(sender);
        serviceTable.setContainerDataSource(new ComponentDtoContainer());
        upperLayout.addComponent(serviceTable);
        serviceTable.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tableRowSelected(event);
            }
        });

        serviceButtonsBottom = new ServiceButtonsBottom(sender);
        upperLayout.addComponent(serviceButtonsBottom);
        upperLayout.setExpandRatio(serviceTable, 10);
        splitPanel.addComponent(upperLayout);

        // スプリットパネル下段
        serviceDesc = new ServiceDesc(sender);
        splitPanel.addComponent(serviceDesc);
    }

    public void initialize() {
        serviceButtonsTop.initialize();
        serviceButtonsBottom.initialize();
    }

    public void refreshTable() {
        ((ComponentDtoContainer) serviceTable.getContainerDataSource()).refresh();
        serviceTable.setValue(null);
        serviceDesc.initialize();
    }

    public void tableRowSelected(ValueChangeEvent event) {
        ComponentDto component = (ComponentDto) serviceTable.getValue();
        if (component != null) {
            serviceButtonsBottom.refresh(component);
            serviceDesc.show(component, true);
        } else {
            serviceButtonsBottom.initialize();
            serviceDesc.initialize();
        }
    }

    public void refreshDesc() {
        ComponentDto component = (ComponentDto) serviceTable.getValue();
        serviceButtonsTop.initialize();
        if (component != null) {
            serviceButtonsBottom.refresh(component);
            serviceDesc.show(component, false);
        } else {
            serviceButtonsBottom.initialize();
            serviceDesc.initialize();
        }
    }

    public boolean needsRefresh() {
        Collection<ComponentDto> components = serviceTable.getItemIds();
        for (ComponentDto component : components) {
            for (ComponentInstanceDto componentInstance : component.getComponentInstances()) {
                ComponentInstanceStatus componentInstanceStatus = ComponentInstanceStatus.fromStatus(componentInstance
                        .getComponentInstance().getStatus());
                if (componentInstanceStatus == ComponentInstanceStatus.STARTING
                        || componentInstanceStatus == ComponentInstanceStatus.STOPPING
                        || componentInstanceStatus == ComponentInstanceStatus.CONFIGURING) {
                    return true;
                }
            }
        }

        return false;
    }

}
