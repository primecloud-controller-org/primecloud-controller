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

import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * <p>
 * サーバView画面の画面中央のサーバ一覧を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServerTable extends Table {

    private final MainView sender;

    private final String COLUMN_HEIGHT = "28px";

    //項目名
    private String[] CAPNAME = { ViewProperties.getCaption("field.no"), ViewProperties.getCaption("field.serverName"),
            ViewProperties.getCaption("field.fqdn"), ViewProperties.getCaption("field.ipAddress"),
            ViewProperties.getCaption("field.serverOsStatus"), ViewProperties.getCaption("field.serverServices") };

    public ServerTable(final MainView sender) {
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
        setStyleName("server-table");
        setNullSelectionAllowed(false);
        setCacheRate(0.1);

        addGeneratedColumn("no", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto instance = (InstanceDto) itemId;

                Label label = new Label(String.valueOf(instance.getInstance().getInstanceNo()));
                return label;
            }
        });

        addGeneratedColumn("name", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto instance = (InstanceDto) itemId;

                Icons icon = IconUtils.getPlatformIcon(instance.getPlatform());
                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, instance.getInstance()
                        .getInstanceName()), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                return label;
            }
        });

        addGeneratedColumn("fqdn", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto instance = (InstanceDto) itemId;

                Label label = new Label(instance.getInstance().getFqdn());
                return label;
            }
        });

        addGeneratedColumn("publicIp", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto instance = (InstanceDto) itemId;

                boolean showPublicIp = BooleanUtils.toBoolean(Config.getProperty("ui.showPublicIp"));
                String ipAddress = showPublicIp ? instance.getInstance().getPublicIp() : instance.getInstance()
                        .getPrivateIp();
                Label label = new Label(ipAddress);
                return label;
            }
        });

        addGeneratedColumn("status", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto instance = (InstanceDto) itemId;

                String status = instance.getInstance().getStatus();
                Icons icon = Icons.fromName(status);
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, status), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                return label;
            }
        });

        addGeneratedColumn("services", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto instance = (InstanceDto) itemId;

                String context = "<div>";
                for (ComponentDto component : sender.getComponents(instance.getComponentInstances())) {
                    ComponentType componentType = component.getComponentType();
                    String name = componentType.getComponentTypeNameDisp();
                    Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                    // Master
                    if (MySQLConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
                        Long masterInstanceNo = null;
                        for (InstanceConfig config : component.getInstanceConfigs()) {
                            if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(config.getConfigName())) {
                                if (StringUtils.isEmpty(config.getConfigValue())) {
                                    masterInstanceNo = config.getInstanceNo();
                                    break;
                                }
                            }
                        }
                        if (masterInstanceNo != null) {
                            if (masterInstanceNo.equals(instance.getInstance().getInstanceNo())) {
                                name = name + "_master";
                                nameIcon = Icons.MYSQL_MASTER;
                            } else {
                                name = name + "_slave";
                                nameIcon = Icons.MYSQL_SLAVE;
                            }
                        } else {
                            name = name + "_slave";
                            nameIcon = Icons.MYSQL_SLAVE;
                        }
                    }

                    context = context + "<img style=\"width: 5px;\" src=\" "
                            + IconUtils.getIconPath(getApplication(), Icons.SPACER) + "\" >" + "<img src=\""
                            + IconUtils.getIconPath(getApplication(), nameIcon) + "\" + " + " title=\"" + name + "\">";
                }
                context = context + "</div>";

                Label label = new Label(context, Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                return label;
            }
        });

        //テーブルに項目名を設定
        setColumnHeaders(CAPNAME);

        //テーブルのカラムに対してStyleNameを設定
        setCellStyleGenerator(new StandardCellStyleGenerator());

        setColumnExpandRatio("fqdn", 100);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<InstanceDto> getItemIds() {
        return (Collection<InstanceDto>) super.getItemIds();
    }

    public void refreshData() {
        ((InstanceDtoContainer) getContainerDataSource()).refresh2(this);
    }

    public void refreshDesc() {
        sender.serverPanel.refreshDesc();
    }

}
