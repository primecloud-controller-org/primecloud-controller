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

import java.util.Collection;

import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.data.LoadBalancerDtoContainer;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.StringUtils;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * <p>
 * ロードバランサ画面の画面中央のロードバランサ一覧を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerTable extends Table {

    private final MainView sender;

    private final String COLUMN_HEIGHT = "28px";

    //項目名
    private String[] CAPNAME = { ViewProperties.getCaption("field.no"),
            ViewProperties.getCaption("field.loadBalancerName"), ViewProperties.getCaption("field.loadBalancerStatus"),
            ViewProperties.getCaption("field.loadBalancerHostname"),
            ViewProperties.getCaption("field.loadBalancerType"), ViewProperties.getCaption("field.loadBalancerService") };

    public LoadBalancerTable(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setVisibleColumns(new Object[] {});
        setWidth("100%");
        setHeight("100%");
        setPageLength(0);
        setSortDisabled(true);
        setColumnReorderingAllowed(false);
        setColumnCollapsingAllowed(false);
        setSelectable(true);
        setMultiSelect(false);
        setImmediate(true);
        setStyleName("loadbalancer-table");
        setNullSelectionAllowed(false);
        setCacheRate(0.1);

        addGeneratedColumn("no", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;

                Label label = new Label(String.valueOf(loadBalancer.getLoadBalancer().getLoadBalancerNo()));
                return label;
            }
        });

        addGeneratedColumn("name", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;

                Label label;
                if (StringUtils.isEmpty(loadBalancer.getLoadBalancer().getComment())) {
                    label = new Label(loadBalancer.getLoadBalancer().getLoadBalancerName(), Label.CONTENT_XHTML);
                } else {
                    String name = loadBalancer.getLoadBalancer().getComment() + "\n["
                            + loadBalancer.getLoadBalancer().getLoadBalancerName() + "]";
                    label = new Label(name, Label.CONTENT_PREFORMATTED);
                    label.setHeight(COLUMN_HEIGHT);
                }
                return label;
            }
        });

        addGeneratedColumn("status", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;

                LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getLoadBalancer().getStatus());

                Icons icon;
                if (status == LoadBalancerStatus.RUNNING && loadBalancer.getLoadBalancerListeners().size() == 0) {
                    // ステータスがRUNNINGでもリスナーが存在しない場合はアイコンを変える
                    icon = Icons.RUN_WARNING;
                } else {
                    icon = Icons.fromName(status.name());
                }

                String statusString = status.name().substring(0, 1).toUpperCase()
                        + status.name().substring(1).toLowerCase();

                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, statusString),
                        Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                return label;
            }
        });

        addGeneratedColumn("hostName", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;

                Label label = new Label("", Label.CONTENT_TEXT);
                if (loadBalancer.getLoadBalancer().getCanonicalName() != null) {
                    label.setValue(loadBalancer.getLoadBalancer().getCanonicalName());
                }
                return label;
            }
        });

        addGeneratedColumn("type", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;

                Icons icon = IconUtils.getPlatformIcon(loadBalancer.getPlatform());
                String type = ViewProperties.getLoadBalancerType(loadBalancer.getLoadBalancer().getType());
                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, type), Label.CONTENT_XHTML);
                return label;
            }
        });

        addGeneratedColumn("serviceName", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;

                ComponentDto component = sender.getComponent(loadBalancer.getLoadBalancer().getComponentNo());

                Label label;
                if (StringUtils.isEmpty(component.getComponent().getComment())) {
                    label = new Label(component.getComponent().getComponentName(), Label.CONTENT_TEXT);
                } else {
                    String name = component.getComponent().getComment() + "\n["
                            + component.getComponent().getComponentName() + "]";
                    label = new Label(name, Label.CONTENT_PREFORMATTED);
                    label.setHeight(COLUMN_HEIGHT);
                }
                return label;
            }
        });

        //テーブルに項目名を設定
        setColumnHeaders(CAPNAME);

        //テーブルのカラムに対してStyleNameを設定
        setCellStyleGenerator(new StandardCellStyleGenerator());

        setColumnExpandRatio("hostName", 100);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<LoadBalancerDto> getItemIds() {
        return (Collection<LoadBalancerDto>) super.getItemIds();
    }

    public void refreshData() {
        ((LoadBalancerDtoContainer) getContainerDataSource()).refresh2(this);
    }

    public void refreshDesc() {
        sender.loadBalancerPanel.refreshDesc();
    }

}
