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

import jp.primecloud.auto.ui.data.LoadBalancerDtoContainer;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class LoadBalancerPanel extends Panel {

    LoadBalancerTable loadBalancerTable;

    LoadBalancerTableOperation loadBalancerTableOpe;

    LoadBalancerDesc loadBalancerDesc;

    public LoadBalancerPanel(MainView sender) {
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout vlLBalancer = (VerticalLayout) getContent();
        vlLBalancer.setSizeFull();
        vlLBalancer.addStyleName("loadbalancer-tab");
        vlLBalancer.setSpacing(false);
        vlLBalancer.setMargin(false);

        CssLayout hLBalancer = new CssLayout();
        Label lLBalancer = new Label(ViewProperties.getCaption("label.loadbalancer"));
        hLBalancer.setWidth("100%");
        hLBalancer.setMargin(true);
        hLBalancer.addStyleName("loadbalancer-table-label");
        hLBalancer.addComponent(lLBalancer);
        hLBalancer.setHeight("28px");

        //スプリットパネル
        SplitPanel splLBalancer = new SplitPanel();
        splLBalancer.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splLBalancer.setSplitPosition(40);
        splLBalancer.setSizeFull();
        vlLBalancer.addComponent(splLBalancer);

        //スプリットパネル上段
        VerticalLayout layLBUpper = new VerticalLayout();
        layLBUpper.setSizeFull();
        layLBUpper.setSpacing(false);
        layLBUpper.setMargin(false);
        layLBUpper.addComponent(hLBalancer);
        loadBalancerTable = new LoadBalancerTable(null, new LoadBalancerDtoContainer(), sender);
        layLBUpper.addComponent(loadBalancerTable);
        loadBalancerTableOpe = new LoadBalancerTableOperation(sender);
        layLBUpper.addComponent(loadBalancerTableOpe);
        layLBUpper.setExpandRatio(loadBalancerTable, 10);
        splLBalancer.addComponent(layLBUpper);

        //スプリットパネル下段
        loadBalancerDesc = new LoadBalancerDesc(sender);
        splLBalancer.addComponent(loadBalancerDesc);
    }

}
