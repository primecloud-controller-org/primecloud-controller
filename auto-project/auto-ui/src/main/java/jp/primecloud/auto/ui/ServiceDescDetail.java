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

import java.util.List;

import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.data.ComponentParameterContainer;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * サービスView下部の詳細情報を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServiceDescDetail extends Panel {

    private DetailInfoOpe left;

    private DetailParameters right;

    @Override
    public void attach() {
        addStyleName(Reindeer.PANEL_LIGHT);
        setHeight("100%");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addStyleName("service-desc-detail");
        setContent(layout);

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setMargin(false);
        leftLayout.setSpacing(false);
        leftLayout.setWidth("250px");
        leftLayout.setHeight("100%");

        left = new DetailInfoOpe();
        left.setWidth("250px");
        leftLayout.addComponent(left);
        leftLayout.setExpandRatio(left, 1.0f);
        layout.addComponent(leftLayout);

        right = new DetailParameters();
        right.setWidth("100%");
        right.setHeight("100%");
        layout.addComponent(right);

        layout.setExpandRatio(right, 100);
    }

    public void initialize() {
        left.initialize();
        right.getContainerDataSource().removeAllItems();
    }

    public void show(ComponentDto component, List<InstanceDto> instances) {
        left.show(component);
        right.refresh(component, instances);
    }

    private class DetailInfoOpe extends VerticalLayout {

        private Label serviceNameLabel;

        @Override
        public void attach() {
            addStyleName("service-desc-detail-info");
            setCaption(ViewProperties.getCaption("label.serviceDetailInfo"));
            setSizeFull();
            setMargin(false, false, false, true);
            setSpacing(true);
            addStyleName("service-desc-detail-info");

            // サービス名
            serviceNameLabel = new Label();
            addComponent(serviceNameLabel);
        }

        public void initialize() {
            serviceNameLabel.setCaption(null);
        }

        public void show(ComponentDto component) {
            serviceNameLabel.setCaption(component.getComponent().getComponentName());
        }

    }

    private class DetailParameters extends Table {

        private String[] COLNAME = { ViewProperties.getCaption("field.categoryName"),
                ViewProperties.getCaption("field.parameterName"), ViewProperties.getCaption("field.parameterValue") };

        @Override
        public void attach() {
            // テーブル基本設定
            addStyleName("service-desc-detail-param");
            setCaption(ViewProperties.getCaption("label.serviceDetailParams"));
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(false);

            // カラム設定
            addContainerProperty("Kind", String.class, null);
            addContainerProperty("Name", String.class, null);
            addContainerProperty("Value", String.class, null);
            setColumnExpandRatio("Value", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void refresh(ComponentDto component, List<InstanceDto> instances) {
            setContainerDataSource(new ComponentParameterContainer(component, instances));
            setColumnHeaders(COLNAME);
        }

    }

}
