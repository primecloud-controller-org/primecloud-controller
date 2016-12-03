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

import jp.primecloud.auto.entity.crud.ComponentType;
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
@SuppressWarnings({ "serial", "unchecked" })
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
                ComponentDto p = (ComponentDto) itemId;
                Label nlbl = new Label(String.valueOf(p.getComponent().getComponentNo()));
                return nlbl;
            }
        });

        addGeneratedColumn("name", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                String name;
                if (StringUtils.isEmpty(p.getComponent().getComment())) {
                    name = p.getComponent().getComponentName();
                } else {
                    name = p.getComponent().getComment() + "\n[" + p.getComponent().getComponentName() + "]";
                }
                Label nlbl = new Label(name, Label.CONTENT_PREFORMATTED);
                return nlbl;
            }
        });

        //サービスの割り当てサーバ数
        addGeneratedColumn("srvs", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                int srvs = 0;
                for (ComponentInstanceDto componentInstance : p.getComponentInstances()) {
                    if (BooleanUtils.isTrue(componentInstance.getComponentInstance().getAssociate())) {
                        srvs++;
                    }
                }
                Label lbl = new Label(Integer.toString(srvs));
                return lbl;
            }
        });

        addGeneratedColumn("status", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                String a = p.getStatus().substring(0, 1).toUpperCase() + p.getStatus().substring(1).toLowerCase();
                Icons icon = Icons.fromName(a);
                Label slbl = new Label(IconUtils.createImageTag(ServiceTable.this, icon, a), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                return slbl;
            }
        });

        addGeneratedColumn("loadBalancer", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto dto = (ComponentDto) itemId;

                Button button = null;
                for (LoadBalancerDto lbDto : (Collection<LoadBalancerDto>) sender.loadBalancerPanel.loadBalancerTable
                        .getItemIds()) {
                    if (dto.getComponent().getComponentNo().equals(lbDto.getLoadBalancer().getComponentNo())) {
                        button = createLoadBalancerButton(lbDto);
                        break;
                    }
                }
                if (button != null) {
                    return button;
                } else {
                    return (new Label(""));
                }
            }
        });

        addGeneratedColumn("serviceDetail", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                ComponentType componentType = p.getComponentType();

                // サービス名
                String name = componentType.getComponentTypeNameDisp();
                Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                Label slbl = new Label(IconUtils.createImageTag(ServiceTable.this, nameIcon, name), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);
                return slbl;
            }
        });

        //テーブルに項目名を設定
        setColumnHeaders(CAPNAME);

        //テーブルのカラムに対してStyleNameを設定
        setCellStyleGenerator(new StandardCellStyleGenerator());

        setColumnExpandRatio("serviceDetail", 100);
    }

    private Button createLoadBalancerButton(final LoadBalancerDto loadBalancerDto) {
        Button button = new Button();
        button.setCaption(loadBalancerDto.getLoadBalancer().getLoadBalancerName());
        button.setIcon(Icons.LOADBALANCER_TAB.resource());
        button.setData(loadBalancerDto);
        button.addStyleName("borderless");
        button.addStyleName("loadbalancer-button");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // ロードバランサを選択
                sender.loadBalancerPanel.loadBalancerTable.select(loadBalancerDto);

                // ロードバランサタブに移動
                sender.tab.setSelectedTab(sender.loadBalancerPanel);
            }
        });
        return button;
    }

    public void refreshData() {
        ((ComponentDtoContainer) getContainerDataSource()).refresh2(this);
    }

    public void refreshDesc() {
        sender.servicePanel.refreshDesc();
    }

}
