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
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.data.ComponentDtoContainer;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;
import jp.primecloud.auto.ui.data.InstanceParameterContainer;

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

    private ServerButtonsButtom serverButtonsBottom;

    private ServerDesc serverDesc;

    public ServerPanel(MainView sender) {
        this.sender = sender;

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

        serverTable = new ServerTable(null, new InstanceDtoContainer(), sender);
        upperLayout.addComponent(serverTable);
        serverTable.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tableRowSelected(event);
            }
        });

        serverButtonsBottom = new ServerButtonsButtom(sender);
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
        serverDesc.initializeData();
    }

    public void tableRowSelected(ValueChangeEvent event) {
        InstanceDto dto = (InstanceDto) serverTable.getValue();
        Instance instance = dto != null ? dto.getInstance() : null;
        serverButtonsBottom.refresh(dto);
        if (dto != null) {
            serverTable.setButtonStatus(dto.getInstance());
            // サーバ情報の更新(選択されているTabだけ更新する)
            if (serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescBasic) {
                //基本情報の更新
                serverDesc.serverDescBasic.left.setItem(dto);
                serverDesc.serverDescBasic.right.refresh(new ComponentDtoContainer(sender.getComponents(dto
                        .getComponentInstances())));
            } else if (serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescDetail) {
                //詳細情報の更新
                serverDesc.serverDescDetail.left.setServerName(instance);
                serverDesc.serverDescDetail.right.setContainerDataSource(new InstanceParameterContainer(dto));
                serverDesc.serverDescDetail.right.setHeaders();
            }
        }
    }

    public void refreshDesc() {
        InstanceDto dto = (InstanceDto) serverTable.getValue();
        Instance instance = dto != null ? dto.getInstance() : null;
        serverButtonsTop.initialize();
        serverButtonsBottom.refresh(dto);
        if (dto != null) {
            serverTable.setButtonStatus(instance);
            // サーバ情報の更新(選択されているTabだけ更新する)
            if (serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescBasic) {
                //基本情報の更新
                serverDesc.serverDescBasic.left.setItem(dto);
                serverDesc.serverDescBasic.right.refresh(new ComponentDtoContainer(sender.getComponents(dto
                        .getComponentInstances())));
            } else if (serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescDetail) {
                //詳細情報の更新
                serverDesc.serverDescDetail.left.setServerName(instance);
                serverDesc.serverDescDetail.right.setContainerDataSource(new InstanceParameterContainer(dto));
                serverDesc.serverDescDetail.right.setHeaders();
            }
        } else {
            serverDesc.initializeData();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean needsRefresh() {
        // サーバの更新チェック
        Collection<InstanceDto> instanceDtos = serverTable.getItemIds();
        for (InstanceDto dto : instanceDtos) {
            InstanceStatus status = InstanceStatus.fromStatus(dto.getInstance().getStatus());
            if (status == InstanceStatus.STARTING || status == InstanceStatus.STOPPING
                    || status == InstanceStatus.CONFIGURING) {
                return true;
            }
        }

        return false;
    }

}
