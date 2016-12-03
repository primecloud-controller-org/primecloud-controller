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

import jp.primecloud.auto.ui.data.ComponentDtoContainer;

import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class ServicePanel extends Panel {

    ServiceButtonsTop serviceButtonsTop;

    ServiceTable serviceTable;

    ServiceButtonsBottom serviceButtonsBottom;

    ServiceDesc serviceDesc;

    public ServicePanel(MainView sender) {
        //サービス用タブ設定
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout vlService = (VerticalLayout) getContent();
        vlService.setSizeFull();
        vlService.addStyleName("service-tab");
        vlService.setSpacing(false);
        vlService.setMargin(false);

        //スプリットパネル
        SplitPanel splService = new SplitPanel();
        splService.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splService.setSplitPosition(40);
        splService.setSizeFull();
        vlService.addComponent(splService);
        vlService.setExpandRatio(splService, 10);

        //スプリットパネル上段
        VerticalLayout layServiceUpper = new VerticalLayout();
        layServiceUpper.setSizeFull();
        layServiceUpper.setSpacing(false);
        layServiceUpper.setMargin(false);
        serviceButtonsTop = new ServiceButtonsTop(sender);
        layServiceUpper.addComponent(serviceButtonsTop);
        serviceTable = new ServiceTable(null, new ComponentDtoContainer(), sender);
        layServiceUpper.addComponent(serviceTable);
        serviceButtonsBottom = new ServiceButtonsBottom(sender);
        layServiceUpper.addComponent(serviceButtonsBottom);
        layServiceUpper.setExpandRatio(serviceTable, 10);
        splService.addComponent(layServiceUpper);

        //スプリットパネル下段
        serviceDesc = new ServiceDesc(sender);
        splService.addComponent(serviceDesc);
    }

}
