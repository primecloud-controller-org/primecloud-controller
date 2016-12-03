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

import jp.primecloud.auto.ui.data.InstanceDtoContainer;

import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class ServerPanel extends Panel {

    ServerButtonsTop serverButtonsTop;

    ServerTable serverTable;

    ServerButtonsButtom serverButtonsBottom;

    ServerDesc serverDesc;

    public ServerPanel(MainView sender) {
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout vlServer = (VerticalLayout) getContent();
        vlServer.setSizeFull();
        vlServer.addStyleName("server-tab");
        vlServer.setSpacing(false);
        vlServer.setMargin(false);

        //スプリットパネル
        SplitPanel splServer = new SplitPanel();
        splServer.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splServer.setSplitPosition(40);
        splServer.setSizeFull();
        vlServer.addComponent(splServer);

        //スプリットパネル上段
        VerticalLayout layServerUpper = new VerticalLayout();
        layServerUpper.setSizeFull();
        layServerUpper.setSpacing(false);
        layServerUpper.setMargin(false);
        serverButtonsTop = new ServerButtonsTop(sender);
        layServerUpper.addComponent(serverButtonsTop);
        serverTable = new ServerTable(null, new InstanceDtoContainer(), sender);
        layServerUpper.addComponent(serverTable);
        serverButtonsBottom = new ServerButtonsButtom(sender);
        layServerUpper.addComponent(serverButtonsBottom);
        layServerUpper.setExpandRatio(serverTable, 10);
        splServer.addComponent(layServerUpper);

        //スプリットパネル下段
        serverDesc = new ServerDesc(sender);
        splServer.addComponent(serverDesc);
    }

}
