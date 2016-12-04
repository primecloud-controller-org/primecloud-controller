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
 * サーバView画面下部のタイトルとタブを生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServerDesc extends Panel {

    private MainView sender;

    private TabSheet tab;

    private ServerDescBasic serverDescBasic;

    private ServerDescDetail serverDescDetail;

    public ServerDesc(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setWidth("100%");
        setHeight("100%");
        setCaption(ViewProperties.getCaption("panel.serverDesc"));
        addStyleName(Reindeer.PANEL_LIGHT);
        addStyleName("server-desc-panel");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.addStyleName("server-desc-layout");

        tab = new TabSheet();
        tab.addStyleName(Reindeer.TABSHEET_BORDERLESS);
        tab.setWidth("100%");
        tab.setHeight("100%");
        addComponent(tab);

        // 基本情報タブ
        serverDescBasic = new ServerDescBasic(sender);
        tab.addTab(serverDescBasic, ViewProperties.getCaption("tab.serverDescBasic"), Icons.BASIC.resource());

        // 詳細情報タブ
        serverDescDetail = new ServerDescDetail();
        tab.addTab(serverDescDetail, ViewProperties.getCaption("tab.serverDescDetail"), Icons.DETAIL.resource());

        tab.addListener(new SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                ServerDesc.this.selectedTabChange(event);
            }
        });
    }

    public void initialize() {
        serverDescBasic.initialize();
        serverDescDetail.initialize();
    }

    public void show(InstanceDto instance) {
        if (tab.getSelectedTab() == serverDescBasic) {
            serverDescBasic.show(instance);
        } else if (tab.getSelectedTab() == serverDescDetail) {
            serverDescDetail.show(instance);
        }
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        sender.serverPanel.refreshDesc();
    }

}
