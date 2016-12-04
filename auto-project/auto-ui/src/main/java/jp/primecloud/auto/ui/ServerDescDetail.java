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
import jp.primecloud.auto.ui.data.InstanceParameterContainer;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * サーバView下部の詳細情報を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServerDescDetail extends Panel {

    private DetailInfo left;

    private DetailParameters right;

    @Override
    public void attach() {
        setHeight("100%");
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout panel = (VerticalLayout) getContent();
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.setMargin(true);
        panel.setSpacing(false);
        panel.addStyleName("server-desc-detail");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addStyleName("server-desc-detail");

        left = new DetailInfo();
        left.setWidth("200px");
        layout.addComponent(left);

        right = new DetailParameters();
        right.setWidth("100%");
        right.setHeight("100%");
        layout.addComponent(right);

        layout.setExpandRatio(right, 100);

        panel.addComponent(layout);
        panel.setExpandRatio(layout, 1.0f);
    }

    public void initialize() {
        left.initialize();
        right.getContainerDataSource().removeAllItems();
    }

    public void show(InstanceDto instance) {
        left.show(instance);
        right.refresh(instance);
    }

    private class DetailInfo extends VerticalLayout {

        private Label serverNameLabel;

        @Override
        public void attach() {
            setMargin(false, false, false, true);
            setSpacing(true);
            setCaption(ViewProperties.getCaption("label.serverDetailInfo"));
            addStyleName("server-desc-detail-info");

            // サーバ名
            serverNameLabel = new Label();
            addComponent(serverNameLabel);
        }

        public void initialize() {
            serverNameLabel.setCaption(null);
        }

        public void show(InstanceDto instance) {
            serverNameLabel.setCaption(instance.getInstance().getInstanceName());
        }

    }

    private class DetailParameters extends Table {

        private String[] COLNAME = { ViewProperties.getCaption("field.categoryName"),
                ViewProperties.getCaption("field.parameterName"), ViewProperties.getCaption("field.parameterValue") };

        @Override
        public void attach() {
            // テーブル基本設定
            addStyleName("server-desc-detail-param");
            setCaption(ViewProperties.getCaption("label.serverDetailParams"));
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(true);

            // カラム設定
            addContainerProperty("Kind", String.class, null);
            addContainerProperty("Name", String.class, null);
            addContainerProperty("Value", String.class, null);
            setColumnExpandRatio("Value", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void refresh(InstanceDto instance) {
            setContainerDataSource(new InstanceParameterContainer(instance));
            setColumnHeaders(COLNAME);
        }

    }

}
