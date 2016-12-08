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
import java.util.List;

import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.data.ComponentDtoContainer;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * <p>
 * サービスView画面の画面中央のサーバ一覧を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServiceTable extends Table {

    private MainView sender;

    private final String COLUMN_HEIGHT = "28px";

    //項目名
    private String[] CAPNAME = { ViewProperties.getCaption("field.no"), ViewProperties.getCaption("field.serviceName"),
            ViewProperties.getCaption("field.serverSum"), ViewProperties.getCaption("field.serviceStatus"),
            ViewProperties.getCaption("field.serviceIpPort"), ViewProperties.getCaption("field.serviceDetail") };

    public ServiceTable(MainView sender) {
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
        setNullSelectionAllowed(false);
        setStyleName("service-table");

        addGeneratedColumn("no", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto component = (ComponentDto) itemId;

                Label label = new Label(String.valueOf(component.getComponent().getComponentNo()));
                return label;
            }
        });

        addGeneratedColumn("name", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto component = (ComponentDto) itemId;

                String name = component.getComponent().getComponentName();
                if (StringUtils.isNotEmpty(component.getComponent().getComment())) {
                    name = component.getComponent().getComment() + "\n[" + name + "]";
                }
                Label label = new Label(name, Label.CONTENT_PREFORMATTED);
                return label;
            }
        });

        //サービスの割り当てサーバ数
        addGeneratedColumn("srvs", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto component = (ComponentDto) itemId;

                int srvs = 0;
                for (ComponentInstanceDto componentInstance : component.getComponentInstances()) {
                    if (BooleanUtils.isTrue(componentInstance.getComponentInstance().getAssociate())) {
                        srvs++;
                    }
                }
                Label label = new Label(Integer.toString(srvs));
                return label;
            }
        });

        addGeneratedColumn("status", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto component = (ComponentDto) itemId;

                String status = component.getStatus();
                Icons icon = Icons.fromName(status);
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                Label label = new Label(IconUtils.createImageTag(ServiceTable.this, icon, status), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                return label;
            }
        });

        addGeneratedColumn("loadBalancer", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto component = (ComponentDto) itemId;

                List<LoadBalancerDto> loadBalancers = sender
                        .getLoadBalancers(component.getComponent().getComponentNo());
                if (loadBalancers.size() > 0) {
                    return createLoadBalancerButton(loadBalancers.get(0));
                } else {
                    return (new Label(""));
                }
            }
        });

        addGeneratedColumn("serviceDetail", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto component = (ComponentDto) itemId;

                String name = component.getComponentType().getComponentTypeNameDisp();
                Icons icon = Icons.fromName(component.getComponentType().getComponentTypeName());
                Label label = new Label(IconUtils.createImageTag(ServiceTable.this, icon, name), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                return label;
            }
        });

        //テーブルに項目名を設定
        setColumnHeaders(CAPNAME);

        //テーブルのカラムに対してStyleNameを設定
        setCellStyleGenerator(new StandardCellStyleGenerator());

        setColumnExpandRatio("serviceDetail", 100);
    }

    private Button createLoadBalancerButton(final LoadBalancerDto loadBalancer) {
        Button button = new Button();
        button.setCaption(loadBalancer.getLoadBalancer().getLoadBalancerName());
        button.setIcon(Icons.LOADBALANCER_TAB.resource());
        button.setData(loadBalancer);
        button.addStyleName("borderless");
        button.addStyleName("loadbalancer-button");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // ロードバランサを選択
                sender.loadBalancerPanel.loadBalancerTable.select(loadBalancer);

                // ロードバランサタブに移動
                sender.tab.setSelectedTab(sender.loadBalancerPanel);
            }
        });
        return button;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ComponentDto> getItemIds() {
        return (Collection<ComponentDto>) super.getItemIds();
    }

    public void refreshData() {
        ((ComponentDtoContainer) getContainerDataSource()).refresh2(this);
    }

    public void refreshDesc() {
        sender.servicePanel.refreshDesc();
    }

}
