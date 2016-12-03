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

import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * サービスView画面下部のタイトルとタブを生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServiceDesc extends Panel {

    private MainView sender;

    private TabSheet tab;

    private ServiceDescBasic serviceDescBasic;

    private ServiceDescDetail serviceDescDetail;

    public ServiceDesc(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setWidth("100%");
        setHeight("100%");
        setCaption(ViewProperties.getCaption("panel.serviceDesc"));
        addStyleName("service-desc-panel");
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.addStyleName("service-desc-layout");

        tab = new TabSheet();
        tab.addStyleName(Reindeer.TABSHEET_BORDERLESS);
        tab.setWidth("100%");
        tab.setHeight("100%");
        addComponent(tab);

        // 基本情報タブ
        serviceDescBasic = new ServiceDescBasic(sender);
        tab.addTab(serviceDescBasic, ViewProperties.getCaption("tab.serviceDescBasic"), Icons.BASIC.resource());

        // 詳細情報タブ
        serviceDescDetail = new ServiceDescDetail();
        tab.addTab(serviceDescDetail, ViewProperties.getCaption("tab.serviceDescDetail"), Icons.DETAIL.resource());

        tab.addListener(new SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                ServiceDesc.this.selectedTabChange(event);
            }
        });
    }

    public void initialize() {
        serviceDescBasic.initialize();
        serviceDescDetail.initialize();
    }

    @SuppressWarnings("unchecked")
    public void refresh(ComponentDto dto, boolean clearCheckBox) {
        if (tab.getSelectedTab() == serviceDescBasic) {
            serviceDescBasic.show(dto, clearCheckBox);
        } else if (tab.getSelectedTab() == serviceDescDetail) {
            Collection<InstanceDto> instanceDtos = (Collection<InstanceDto>) sender.serverPanel.serverTable
                    .getItemIds();
            serviceDescDetail.show(dto, instanceDtos);
        }
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        sender.servicePanel.refreshDesc();
    }

}
