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

import jp.primecloud.auto.service.dto.ComponentDto;
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

    DetailInfoOpe left = new DetailInfoOpe();

    DetailParameters right = new DetailParameters();

    public ServiceDescDetail() {
        addStyleName(Reindeer.PANEL_LIGHT);
        setHeight("100%");

        HorizontalLayout hlPanels = new HorizontalLayout();
        hlPanels.setWidth("100%");
        hlPanels.setHeight("100%");
        hlPanels.setMargin(true);
        hlPanels.setSpacing(true);
        hlPanels.addStyleName("service-desc-detail");
        setContent(hlPanels);

        left.setWidth("250px");
        right.setWidth("100%");
        right.setHeight("100%");

        VerticalLayout layLeft = new VerticalLayout();
        layLeft.setMargin(false);
        layLeft.setSpacing(false);
        layLeft.setWidth("250px");
        layLeft.setHeight("100%");
        layLeft.addComponent(left);
        layLeft.setExpandRatio(left, 1.0f);

        hlPanels.addComponent(layLeft);
        hlPanels.addComponent(right);

        hlPanels.setExpandRatio(right, 100);

    }

    class DetailInfoOpe extends VerticalLayout {

        Label serviceName = new Label();

        DetailInfoOpe() {
            addStyleName("service-desc-detail-info");
            setCaption(ViewProperties.getCaption("label.serviceDetailInfo"));
            setSizeFull();

            setMargin(false, false, false, true);
            setSpacing(true);
            addStyleName("service-desc-detail-info");

            //名前ラベル追加
            addComponent(serviceName);

        }

        public void setItem(ComponentDto dto) {
            if (dto != null) {
                serviceName.setCaption(dto.getComponent().getComponentName());

            } else {
                serviceName.setCaption(null);
            }
        }
    }

    class DetailParameters extends Table {

        DetailParameters() {

            addStyleName("service-desc-detail-param");
            setCaption(ViewProperties.getCaption("label.serviceDetailParams"));

            //テーブル基本設定
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(false);

            //カラム設定
            addContainerProperty("Kind", String.class, null);
            addContainerProperty("Name", String.class, null);
            addContainerProperty("Value", String.class, null);

            setColumnExpandRatio("Value", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {
                    if (propertyId == null) {
                        return "";
                    } else {
                        return propertyId.toString().toLowerCase();
                    }
                }
            });

        }

        public void setHeaders(){
            setColumnHeaders(new String[]{
                    ViewProperties.getCaption("field.categoryName"),
                    ViewProperties.getCaption("field.parameterName"),
                    ViewProperties.getCaption("field.parameterValue")}
            );
        }
    }

    public void initializeData() {
        left.setItem(null);
        right.getContainerDataSource().removeAllItems();
    }
}
