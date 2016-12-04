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

import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.data.LoadBalancerDtoContainer;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class LoadBalancerPanel extends Panel {

    private MainView sender;

    LoadBalancerTable loadBalancerTable;

    private LoadBalancerButtonsBottom loadBalancerButtonsBottom;

    private LoadBalancerDesc loadBalancerDesc;

    public LoadBalancerPanel(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setSizeFull();
        layout.addStyleName("loadbalancer-tab");
        layout.setSpacing(false);
        layout.setMargin(false);

        // スプリットパネル
        SplitPanel splitPanel = new SplitPanel();
        splitPanel.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splitPanel.setSplitPosition(40);
        splitPanel.setSizeFull();
        layout.addComponent(splitPanel);

        // スプリットパネル上段
        VerticalLayout upperLayout = new VerticalLayout();
        upperLayout.setSizeFull();
        upperLayout.setSpacing(false);
        upperLayout.setMargin(false);

        CssLayout upperTopLayout = new CssLayout();
        Label label = new Label(ViewProperties.getCaption("label.loadbalancer"));
        upperTopLayout.setWidth("100%");
        upperTopLayout.setMargin(true);
        upperTopLayout.addStyleName("loadbalancer-table-label");
        upperTopLayout.addComponent(label);
        upperTopLayout.setHeight("28px");
        upperLayout.addComponent(upperTopLayout);

        loadBalancerTable = new LoadBalancerTable(sender);
        loadBalancerTable.setContainerDataSource(new LoadBalancerDtoContainer());
        upperLayout.addComponent(loadBalancerTable);
        loadBalancerTable.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tableRowSelected(event);
            }
        });

        loadBalancerButtonsBottom = new LoadBalancerButtonsBottom(sender);
        upperLayout.addComponent(loadBalancerButtonsBottom);
        upperLayout.setExpandRatio(loadBalancerTable, 10);
        splitPanel.addComponent(upperLayout);

        // スプリットパネル下段
        loadBalancerDesc = new LoadBalancerDesc(sender);
        splitPanel.addComponent(loadBalancerDesc);
    }

    public void initialize() {
        loadBalancerButtonsBottom.initialize();
    }

    public void refreshTable() {
        ((LoadBalancerDtoContainer) loadBalancerTable.getContainerDataSource()).refresh();
        loadBalancerTable.setValue(null);
        loadBalancerDesc.initialize();
    }

    public void tableRowSelected(ValueChangeEvent event) {
        LoadBalancerDto loadBalancer = (LoadBalancerDto) loadBalancerTable.getValue();
        if (loadBalancer != null) {
            loadBalancerButtonsBottom.show(loadBalancer);
            loadBalancerDesc.show(loadBalancer, true);
        } else {
            loadBalancerButtonsBottom.initialize();
            loadBalancerDesc.initialize();
        }
    }

    public void refreshDesc() {
        LoadBalancerDto loadBalancer = (LoadBalancerDto) loadBalancerTable.getValue();
        if (loadBalancer != null) {
            loadBalancerButtonsBottom.show(loadBalancer);
            loadBalancerDesc.show(loadBalancer, false);
        } else {
            loadBalancerButtonsBottom.initialize();
            loadBalancerDesc.initialize();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean needsRefresh() {
        Collection<LoadBalancerDto> loadBalancerDtos = loadBalancerTable.getItemIds();
        for (LoadBalancerDto dto : loadBalancerDtos) {
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(dto.getLoadBalancer().getStatus());
            if (status == LoadBalancerStatus.STARTING || status == LoadBalancerStatus.STOPPING
                    || status == LoadBalancerStatus.CONFIGURING) {
                return true;
            }

            for (LoadBalancerListener listener : dto.getLoadBalancerListeners()) {
                LoadBalancerListenerStatus status2 = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                if (status2 == LoadBalancerListenerStatus.STARTING || status2 == LoadBalancerListenerStatus.STOPPING
                        || status2 == LoadBalancerListenerStatus.CONFIGURING) {
                    return true;
                }
            }

            for (LoadBalancerInstance lbInstance : dto.getLoadBalancerInstances()) {
                LoadBalancerInstanceStatus status2 = LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus());
                if (status2 == LoadBalancerInstanceStatus.STARTING || status2 == LoadBalancerInstanceStatus.STOPPING
                        || status2 == LoadBalancerInstanceStatus.CONFIGURING) {
                    return true;
                }
            }
        }

        return false;
    }

}
