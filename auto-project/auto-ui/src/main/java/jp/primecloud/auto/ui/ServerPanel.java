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

import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class ServerPanel extends Panel {

    private MainView sender;

    private ServerButtonsTop serverButtonsTop;

    ServerTable serverTable;

    private ServerButtonsBottom serverButtonsBottom;

    private ServerDesc serverDesc;

    public ServerPanel(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setSizeFull();
        layout.addStyleName("server-tab");
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

        serverButtonsTop = new ServerButtonsTop(sender);
        upperLayout.addComponent(serverButtonsTop);

        serverTable = new ServerTable(sender);
        serverTable.setContainerDataSource(new InstanceDtoContainer());
        upperLayout.addComponent(serverTable);
        serverTable.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tableRowSelected(event);
            }
        });

        serverButtonsBottom = new ServerButtonsBottom(sender);
        upperLayout.addComponent(serverButtonsBottom);
        upperLayout.setExpandRatio(serverTable, 10);
        splitPanel.addComponent(upperLayout);

        // スプリットパネル下段
        serverDesc = new ServerDesc(sender);
        splitPanel.addComponent(serverDesc);
    }

    public void initialize() {
        serverButtonsTop.initialize();
        serverButtonsBottom.initialize();
    }

    public void refreshTable() {
        ((InstanceDtoContainer) serverTable.getContainerDataSource()).refresh();
        serverTable.setValue(null);
        serverDesc.initialize();
    }

    public void tableRowSelected(ValueChangeEvent event) {
        InstanceDto instance = (InstanceDto) serverTable.getValue();
        if (instance != null) {
            serverButtonsBottom.refresh(instance);
            serverDesc.show(instance);
        }
    }

    public void refreshDesc() {
        InstanceDto instance = (InstanceDto) serverTable.getValue();
        serverButtonsTop.initialize();
        if (instance != null) {
            serverButtonsBottom.refresh(instance);
            serverDesc.show(instance);
        } else {
            serverButtonsBottom.initialize();
            serverDesc.initialize();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean needsRefresh() {
        Collection<InstanceDto> instances = serverTable.getItemIds();
        for (InstanceDto instance : instances) {
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status == InstanceStatus.STARTING || status == InstanceStatus.STOPPING
                    || status == InstanceStatus.CONFIGURING) {
                return true;
            }
        }

        return false;
    }

}
