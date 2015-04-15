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

import jp.primecloud.auto.config.Config;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * 【未使用】過去に利用していたメニュー画面を生成します。
 * </p>
 */
@SuppressWarnings("serial")
public class MainMenu extends Panel {

    Tree tree = new Tree("");

    Window mainWindow;

    MainMenu(final Window mainWindow) {
        this.mainWindow = mainWindow;

        VerticalLayout layout = (VerticalLayout) getContent();
        setSizeFull();
        setStyleName("mainmenu");
        addStyleName(Reindeer.PANEL_LIGHT);

        layout.setMargin(false);
        layout.setSpacing(true);
        layout.setSizeFull();

        tree.setNullSelectionAllowed(false);

        final String[][] menus = new String[][] { new String[] { "MyCloud", "New", "Manage" },
                new String[] { "Cloud管理", "監視システム" } };

        for (int i = 0; i < menus.length; i++) {
            String planet = (menus[i][0]);
            tree.addItem(planet);
            if (menus[i].length == 1) {
                // The planet has no moons so make it a leaf.
                tree.setChildrenAllowed(planet, false);
            } else {
                // Add children (moons) under the planets.
                for (int j = 1; j < menus[i].length; j++) {
                    String moon = menus[i][j];
                    // Add the item as a regular item.
                    tree.addItem(moon);
                    // Set it to be a child.
                    tree.setParent(moon, planet);
                    // Make the moons look like leaves.
                    tree.setChildrenAllowed(moon, false);
                }
                // Expand the subtree.
                tree.expandItemsRecursively(planet);
            }
        }
        layout.addComponent(tree);

        //広告欄
        VerticalLayout adlayout = new VerticalLayout();
        adlayout.setSpacing(false);
        adlayout.setMargin(false);
        Label ad1 = new Label(
                "<a href='http://www.csk.com/systems/services/technology/index.html'  target='_blank'><img width='147' alt='先進技術への取り組み' src='./VAADIN/themes/classy/images/technology_ttl.jpg'></a>");
        ad1.setContentMode(Label.CONTENT_XHTML);
        adlayout.addComponent(ad1);
        Label ad2 = new Label(
                "<a href='http://www.csk.com/systems/services/technology/technology01.html'  target='_blank'><img width='147' alt='「ハイブリッドクラウド」実現に向けての取り組み'  src='./VAADIN/themes/classy/images/technology_menu1.jpg'></a>");
        ad2.setContentMode(Label.CONTENT_XHTML);
        adlayout.addComponent(ad2);
        Label ad3 = new Label(
                "<a href='http://www.csk.com/systems/services/technology/technology02.html'  target='_blank'><img width='147' alt='生産技術改善・革新への取り組み' src='./VAADIN/themes/classy/images/technology_menu2.jpg'></a>");
        ad3.setContentMode(Label.CONTENT_XHTML);
        adlayout.addComponent(ad3);
        Label ad4 = new Label(
                "<a href='http://www.csk.com/systems/services/technology/technology03.html'  target='_blank'><img width='147' alt='先進技術調査・評価' src='./VAADIN/themes/classy/images/technology_menu3.jpg'></a>");
        ad4.setContentMode(Label.CONTENT_XHTML);
        adlayout.addComponent(ad4);

        layout.addComponent(adlayout);
        layout.setComponentAlignment(adlayout, Alignment.MIDDLE_CENTER);

        // クリック時の処理
        ItemClickEvent.ItemClickListener listener = new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                String click = (String) event.getItemId();

                if ("Manage".equals(click)) {
                    showCloudEditWindow();
                } else if ("New".equals(click)) {
                    showCloudAddWindow();
                } else if ("監視システム".equals(click)) {
                    String url = Config.getProperty("zabbix.url");
                    mainWindow.open(new ExternalResource(url), "_blank");
                }
            }
        };
        tree.addListener(listener);

        // 初回表示時の処理
        showFirst();
    }

    private void showFirst() {
        // クラウド管理画面を表示する
        showCloudEditWindow();

    }

    private void showCloudAddWindow() {
        MyCloudAdd window = new MyCloudAdd( mainWindow.getApplication() );
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                ((AutoApplication) getApplication()).myCloud.refresh();
            }
        });
        mainWindow.addWindow(window);
    }

    private void showCloudEditWindow() {

        MyCloudManage window = new MyCloudManage( mainWindow.getApplication() );
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                ((AutoApplication) getApplication()).myCloud.refresh();
                //                ((AutoApplication) getApplication()).myCloud.myCloudTabs.workerStop();
                //                ((AutoApplication) getApplication()).myCloud.myCloudTabs.workerStart();
            }
        });
        mainWindow.addWindow(window);
    }

}
