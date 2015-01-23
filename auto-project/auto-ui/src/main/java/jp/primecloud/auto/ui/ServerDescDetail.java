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

import jp.primecloud.auto.entity.crud.Instance;
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

    DetailInfo left = new DetailInfo();

    DetailParameters right = new DetailParameters();

    public ServerDescDetail() {
        setHeight("100%");
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout panel = (VerticalLayout)getContent();
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.setMargin(true);
        panel.setSpacing(false);
        panel.addStyleName("server-desc-detail");

        HorizontalLayout hlPanels = new HorizontalLayout();
        hlPanels.setWidth("100%");
        hlPanels.setHeight("100%");
        hlPanels.setMargin(true);
        hlPanels.setSpacing(true);
        hlPanels.addStyleName("server-desc-detail");
//        setContent(hlPanels);

        left.setWidth("200px");
        right.setWidth("100%");
        right.setHeight("100%");

        hlPanels.addComponent(left);
        hlPanels.addComponent(right);

        hlPanels.setExpandRatio(right, 100);

        panel.addComponent(hlPanels);
        panel.setExpandRatio(hlPanels, 1.0f);
    }

    class DetailInfo extends VerticalLayout {
        Label serverName = new Label();

        DetailInfo() {
            setMargin(false, false, false, true);
            setSpacing(true);

            setCaption(ViewProperties.getCaption("label.serverDetailInfo"));
            addStyleName("server-desc-detail-info");

            //名前ラベル追加
            addComponent(serverName);
        }

        public void setServerName(Instance instance) {
            if (instance != null) {
                serverName.setCaption(instance.getInstanceName());
            } else {
                serverName.setCaption(null);
            }
        }
    }

    class DetailParameters extends Table {
        DetailParameters() {

            addStyleName("server-desc-detail-param");
            setCaption(ViewProperties.getCaption("label.serverDetailParams"));

            //テーブル基本設定
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(true);

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
        left.setServerName(null);
        right.getContainerDataSource().removeAllItems();
    }

}
