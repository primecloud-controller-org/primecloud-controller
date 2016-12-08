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

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.ZabbixInstanceStatus;
import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.data.ComponentDtoContainer;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * サーバView下部の基本情報を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServerDescBasic extends Panel {

    private MainView sender;

    private BasicInfo left;

    private AttachService right;

    public ServerDescBasic(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setHeight("100%");
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(true);
        layout.setSpacing(false);
        layout.addStyleName("server-desc-basic");

        HorizontalLayout layout2 = new HorizontalLayout();
        layout2.setWidth("100%");
        layout2.setHeight("100%");
        layout2.setMargin(true);
        layout2.setSpacing(true);
        layout2.addStyleName("server-desc-basic");

        // サーバ基本情報
        left = new BasicInfo();
        left.setWidth("100%");
        layout2.addComponent(left);

        // 表同士の間隔をあける
        Label padding = new Label(" ");
        padding.setWidth("7px");
        padding.setHeight("99%");
        padding.addStyleName("desc-padding");
        layout2.addComponent(padding);

        Label padding2 = new Label("");
        padding2.setWidth("1px");
        layout2.addComponent(padding2);

        // 割り当てサービス
        String enableService = Config.getProperty("ui.enableService");
        if (enableService == null || BooleanUtils.toBoolean(enableService)) {
            right = new AttachService();
            right.setHeight("100%");
            right.setWidth("100%");
            layout2.addComponent(right);
        }

        layout2.setExpandRatio(left, 40);
        if (right != null) {
            layout2.setExpandRatio(right, 60);
        } else {
            VerticalLayout dummyLayout = new VerticalLayout();
            dummyLayout.setSizeFull();
            layout2.addComponent(dummyLayout);
            layout2.setExpandRatio(dummyLayout, 60);
        }

        layout.addComponent(layout2);
        layout.setExpandRatio(layout2, 1.0f);
    }

    public void initialize() {
        left.initialize();
        if (right != null) {
            right.getContainerDataSource().removeAllItems();
        }
    }

    public void show(InstanceDto instance) {
        left.show(instance);
        if (right != null) {
            right.refresh(sender.getComponents(instance.getComponentInstances()));
        }
    }

    private ComponentInstanceDto findComponentInstance(List<ComponentInstanceDto> componentInstances, Long componentNo) {
        for (ComponentInstanceDto componentInstance : componentInstances) {
            if (componentNo.equals(componentInstance.getComponentInstance().getComponentNo())) {
                return componentInstance;
            }
        }

        return null;
    }

    // 割り当てサービス
    private class AttachService extends Table {

        private final String COLUMN_HEIGHT = "28px";

        private Object[] COLUMNS = { "componentName", "urlIcon", "status", "serviceDetail" };

        private String[] COLNAME = { ViewProperties.getCaption("field.serviceName"),
                ViewProperties.getCaption("field.managementUrl"), ViewProperties.getCaption("field.serviceStatus"),
                ViewProperties.getCaption("field.serviceDetail") };

        @Override
        public void attach() {
            setIcon(Icons.SERVICETAB.resource());
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("server-desc-basic-service");
            setCaption(ViewProperties.getCaption("table.serverServices"));
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(false);
            setVisible(true);

            addGeneratedColumn("componentName", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    ComponentDto component = (ComponentDto) itemId;

                    String name = component.getComponent().getComponentName();
                    if (StringUtils.isNotEmpty(component.getComponent().getComment())) {
                        name = component.getComponent().getComment() + "\n[" + name + "]";
                    }
                    Label label = new Label(name, Label.CONTENT_PREFORMATTED);
                    label.setHeight(COLUMN_HEIGHT);
                    return label;
                }
            });

            addGeneratedColumn("urlIcon", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    ComponentDto component = (ComponentDto) itemId;
                    InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

                    ComponentInstanceDto componentInstance = findComponentInstance(instance.getComponentInstances(),
                            component.getComponent().getComponentNo());
                    String url = (componentInstance == null) ? "" : componentInstance.getUrl();
                    String status = (componentInstance == null) ? "" : componentInstance.getComponentInstance()
                            .getStatus();

                    Icons icon = Icons.fromName(component.getComponentType().getComponentTypeName());

                    // MySQLならMasterとSlaveでアイコンを変える
                    if (MySQLConstants.COMPONENT_TYPE_NAME.equals(component.getComponentType().getComponentTypeName())) {
                        // Master
                        Long masterInstanceNo = null;
                        for (InstanceConfig config : instance.getInstanceConfigs()) {
                            if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(config.getConfigName())) {
                                if (StringUtils.isEmpty(config.getConfigValue())) {
                                    masterInstanceNo = config.getInstanceNo();
                                    break;
                                }
                            }
                        }
                        if (masterInstanceNo != null) {
                            if (masterInstanceNo.equals(instance.getInstance().getInstanceNo())) {
                                icon = Icons.MYSQL_MASTER;
                            } else {
                                icon = Icons.MYSQL_SLAVE;
                            }
                        } else {
                            icon = Icons.MYSQL_SLAVE;
                        }
                    }

                    Link link = new Link(ViewProperties.getCaption("field.managementLink"), new ExternalResource(url));
                    link.setTargetName("_blank");
                    link.setIcon(icon.resource());
                    link.setHeight(COLUMN_HEIGHT);
                    link.setEnabled(false);

                    if (status.equals(ComponentInstanceStatus.RUNNING.toString())) {
                        link.setDescription(url);
                        link.setEnabled(true);
                    }

                    return link;
                }
            });

            addGeneratedColumn("status", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    ComponentDto component = (ComponentDto) itemId;
                    InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

                    ComponentInstanceDto componentInstance = findComponentInstance(instance.getComponentInstances(),
                            component.getComponent().getComponentNo());
                    String status = (componentInstance == null) ? "" : componentInstance.getComponentInstance()
                            .getStatus();

                    Icons icon = Icons.fromName(status);
                    status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                    Label label = new Label(IconUtils.createImageTag(ServerDescBasic.this, icon, status),
                            Label.CONTENT_XHTML);
                    label.setHeight(COLUMN_HEIGHT);
                    return label;
                }
            });

            addGeneratedColumn("serviceDetail", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    ComponentDto component = (ComponentDto) itemId;
                    InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

                    //MySQLならMasterとSlaveでアイコンを変える
                    String type = component.getComponentType().getComponentTypeName();
                    String name = component.getComponentType().getComponentTypeNameDisp();
                    if (MySQLConstants.COMPONENT_TYPE_NAME.equals(type)) {
                        // Master
                        Long masterInstanceNo = null;
                        for (InstanceConfig config : instance.getInstanceConfigs()) {
                            if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(config.getConfigName())) {
                                if (StringUtils.isEmpty(config.getConfigValue())) {
                                    masterInstanceNo = config.getInstanceNo();
                                    break;
                                }
                            }
                        }
                        if (masterInstanceNo != null) {
                            if (masterInstanceNo.equals(instance.getInstance().getInstanceNo())) {
                                name = name + " " + ViewProperties.getComponentTypeName(type + ".master");
                            } else {
                                name = name + " " + ViewProperties.getComponentTypeName(type + ".slave");
                            }
                        } else {
                            name = name + " " + ViewProperties.getComponentTypeName(type + ".slave");
                        }
                    }

                    Label label = new Label(name);
                    return label;
                }
            });

            setColumnExpandRatio("serviceDetail", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void refresh(List<ComponentDto> components) {
            setContainerDataSource(new ComponentDtoContainer(components));
            setVisibleColumns(COLUMNS);
            if (components.size() > 0) {
                setColumnHeaders(COLNAME);
            }
        }

    }

    // サーバ基本情報
    private class BasicInfo extends Panel {

        private final String COLUMN_HEIGHT = "30px";

        private Button getPassword;

        private GridLayout gridLayout;

        private boolean useZabbix = BooleanUtils.toBoolean(Config.getProperty("zabbix.useZabbix"));

        @Override
        public void attach() {
            setCaption(ViewProperties.getCaption("table.serverBasicInfo"));
            setHeight("100%");
            setStyleName("server-desc-basic-panel");

            VerticalLayout layout = (VerticalLayout) getContent();
            layout.setStyleName("server-desc-basic-panel");
            layout.setMargin(true);

            int rowNum = 6 + (useZabbix ? 1 : 0);
            gridLayout = new GridLayout(2, rowNum);
            gridLayout.setWidth("100%");
            gridLayout.setStyleName("server-desc-basic-info");
            gridLayout.setColumnExpandRatio(0, 35);
            gridLayout.setColumnExpandRatio(1, 65);
            layout.addComponent(gridLayout);

            int line = 0;

            // FQDN
            {
                Label label = new Label(ViewProperties.getCaption("field.fqdn"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // IPアドレス
            {
                Label label = new Label(ViewProperties.getCaption("field.ipAddress"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // プラットフォーム
            {
                Label label = new Label(ViewProperties.getCaption("field.platform"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // OS
            {
                Label label = new Label(ViewProperties.getCaption("field.os"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // サーバOSステータス
            {
                Label label = new Label(ViewProperties.getCaption("field.serverOsStatus"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // サーバ監視ステータス
            if (useZabbix) {
                Label label = new Label(ViewProperties.getCaption("field.serverMonitoringStatus"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // コメント
            {
                Label label = new Label(ViewProperties.getCaption("field.comment"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // Windows用パスワード取得ボタン
            getPassword = new Button(ViewProperties.getCaption("button.getPassword"));
            getPassword.setDescription(ViewProperties.getCaption("description.getPassword"));
            getPassword.setIcon(Icons.LOGIN.resource());
            getPassword.addStyleName("getpassword");
            getPassword.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();
                    Long instanceNo = (Long) getPassword.getData();
                    WinPassword winPassword = new WinPassword(instance, instanceNo);
                    getWindow().addWindow(winPassword);
                }
            });
        }

        public void initialize() {
            int line = 0;

            // FQDN
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // IPアドレス
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // プラットフォーム
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // OS
            {
                CssLayout layout = new CssLayout();
                layout.setSizeFull();
                layout.setMargin(false);
                Label label = new Label("", Label.CONTENT_XHTML);
                layout.addComponent(label);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(layout, 1, line++);
            }

            // サーバOSステータス
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // サーバ監視ステータス
            if (useZabbix) {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // コメント
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }
        }

        public void show(InstanceDto instance) {
            PlatformDto platform = instance.getPlatform();

            int line = 0;

            // FQDN
            {
                Label label = new Label(instance.getInstance().getFqdn(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // IPアドレス
            {
                boolean showPublicIp = BooleanUtils.toBoolean(Config.getProperty("ui.showPublicIp"));
                String ipAddress = showPublicIp ? instance.getInstance().getPublicIp() : instance.getInstance()
                        .getPrivateIp();
                Label label = new Label(ipAddress, Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // プラットフォーム
            {
                Icons icon = IconUtils.getPlatformIcon(platform);
                String description = platform.getPlatform().getPlatformNameDisp();
                Label label = new Label(IconUtils.createImageTag(ServerDescBasic.this, icon, description),
                        Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // OS
            {
                CssLayout layout = new CssLayout();
                layout.setSizeFull();
                layout.setMargin(false);
                Icons icon = IconUtils.getOsIcon(instance.getImage());
                String os = instance.getImage().getImage().getOsDisp();
                Label label = new Label(IconUtils.createImageTag(ServerDescBasic.this, icon, os), Label.CONTENT_XHTML);
                layout.addComponent(label);

                // OSがWindowsの場合パスワード取得ボタンを表示
                if (instance.getImage().getImage().getOs().startsWith(PCCConstant.OS_NAME_WIN)) {
                    boolean show = true;

                    // Azureの場合はボタンを表示させない
                    if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
                        show = false;
                    }

                    // Eucalyptusの場合はボタンを表示させない
                    if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())
                            && BooleanUtils.isTrue(platform.getPlatformAws().getEuca())) {
                        show = false;
                    }

                    if (show) {
                        InstanceStatus instanceStatus = InstanceStatus.fromStatus(instance.getInstance().getStatus());
                        if (instanceStatus == InstanceStatus.RUNNING) {
                            getPassword.setEnabled(true);
                            getPassword.setData(instance.getInstance().getInstanceNo());
                        } else {
                            getPassword.setEnabled(false);
                        }
                        layout.addComponent(getPassword);
                        layout.setHeight("60px");
                    }
                }

                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(layout, 1, line++);
            }

            // サーバOSステータス
            {
                String status = instance.getInstance().getStatus();
                Icons icon = Icons.fromName(status);
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                Label label = new Label(IconUtils.createImageTag(ServerDescBasic.this, icon, status),
                        Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // サーバ監視ステータス
            if (useZabbix) {
                String status = ZabbixInstanceStatus.UN_MONITORING.toString();
                if (instance.getZabbixInstance() != null
                        && StringUtils.isNotEmpty(instance.getZabbixInstance().getStatus())) {
                    status = instance.getZabbixInstance().getStatus();
                }
                Icons icon = Icons.fromName(status);

                StringBuilder sb = new StringBuilder();
                String[] array = status.split("_");
                for (String str : array) {
                    sb.append(str.substring(0, 1).toUpperCase()).append(str.substring(1).toLowerCase());
                }
                status = sb.toString();

                Label label = new Label(IconUtils.createImageTag(ServerDescBasic.this, icon, status),
                        Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            //コメント
            {
                Label label = new Label(instance.getInstance().getComment(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }
        }

    }

}
