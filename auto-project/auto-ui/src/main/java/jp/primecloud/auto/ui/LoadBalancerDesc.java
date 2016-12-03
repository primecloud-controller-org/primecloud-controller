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

import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * ロードバランサ画面下部のタイトルとタブを生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerDesc extends Panel {

    private MainView sender;

    TabSheet tabDesc = new TabSheet();

    LoadBalancerDescBasic loadBalancerDescBasic;

    LoadBalancerDescServer loadBalancerDescServer;

    public LoadBalancerDesc(MainView sender) {
        this.sender = sender;

        setWidth("100%");
        setHeight("100%");
        setCaption(ViewProperties.getCaption("panel.loadBalancerDesc"));
        addStyleName("loadbalancer-desc-panel");
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.addStyleName("loadbalancer-desc-layout");
        tabDesc.addStyleName(Reindeer.TABSHEET_BORDERLESS);
        tabDesc.setWidth("100%");
        tabDesc.setHeight("100%");
        loadBalancerDescBasic = new LoadBalancerDescBasic(sender);
        tabDesc.addTab(loadBalancerDescBasic, ViewProperties.getCaption("tab.loadBalancerDescBasic"),
                Icons.BASIC.resource());
        loadBalancerDescServer = new LoadBalancerDescServer(sender);
        tabDesc.addTab(loadBalancerDescServer, ViewProperties.getCaption("tab.loadBalancerDescServer"),
                Icons.DETAIL.resource());

        //タブ用リスナー
        tabDesc.addListener(TabSheet.SelectedTabChangeEvent.class, this, "selectedTabChange");
        addComponent(tabDesc);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        sender.loadBalancerPanel.refreshDesc();
    }

    public void initializeData() {
        loadBalancerDescBasic.initializeData();
        loadBalancerDescServer.initializeData();
    }

}
