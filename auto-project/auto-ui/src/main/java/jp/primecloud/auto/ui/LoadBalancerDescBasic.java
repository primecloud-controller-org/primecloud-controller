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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.service.AwsDescribeService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.Subnet;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * ロードバランサ画面下部の基本情報の生成を行います。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerDescBasic extends Panel {

    private MainView sender;

    private BasicInfo basicInfo;

    private AttachServiceTable attachServiceTable;

    private LoadbalancerServiceOperation loadBalancerOpe;

    private LoadBalancerDto loadBalancer;

    public LoadBalancerDescBasic(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        addStyleName(Reindeer.PANEL_LIGHT);
        setHeight("100%");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addStyleName("loadbalancer-desc-basic");
        setContent(layout);

        // ロードバランサ基本情報
        basicInfo = new BasicInfo();
        basicInfo.setWidth("100%");
        layout.addComponent(basicInfo);

        // 表同士の間隔をあける
        Label padding = new Label(" ");
        padding.setWidth("7px");
        padding.setHeight("99%");
        padding.addStyleName("desc-padding");
        layout.addComponent(padding);

        Label padding2 = new Label("");
        padding2.setWidth("1px");
        layout.addComponent(padding2);

        // ロードバランサリスナ一覧
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setWidth("100%");
        rightLayout.setHeight("100%");
        rightLayout.setMargin(false);
        rightLayout.setSpacing(false);

        attachServiceTable = new AttachServiceTable();
        attachServiceTable.setWidth("100%");
        rightLayout.addComponent(attachServiceTable);
        loadBalancerOpe = new LoadbalancerServiceOperation();
        rightLayout.addComponent(loadBalancerOpe);
        rightLayout.setExpandRatio(attachServiceTable, 100);
        layout.addComponent(rightLayout);

        layout.setExpandRatio(basicInfo, 43);
        layout.setExpandRatio(rightLayout, 57);
    }

    public void initialize() {
        this.loadBalancer = null;

        basicInfo.initialize();
        attachServiceTable.getContainerDataSource().removeAllItems();
        loadBalancerOpe.initialize();
    }

    public void show(LoadBalancerDto loadBalancer, boolean clearCheckBox) {
        this.loadBalancer = loadBalancer;

        basicInfo.show(loadBalancer);
        attachServiceTable.refresh(loadBalancer.getLoadBalancerListeners(), clearCheckBox);
        loadBalancerOpe.show(loadBalancer);
    }

    private void refreshTable() {
        // 選択されているロードバランサを保持する
        Long selectedLoadBalancerNo = null;
        if (sender.loadBalancerPanel.loadBalancerTable.getValue() != null) {
            LoadBalancerDto loadBalancer = (LoadBalancerDto) sender.loadBalancerPanel.loadBalancerTable.getValue();
            selectedLoadBalancerNo = loadBalancer.getLoadBalancer().getLoadBalancerNo();
        }
        int index = sender.loadBalancerPanel.loadBalancerTable.getCurrentPageFirstItemIndex();

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサーバを選択し直す
        if (selectedLoadBalancerNo != null) {
            for (Object itemId : sender.loadBalancerPanel.loadBalancerTable.getItemIds()) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;
                if (selectedLoadBalancerNo.equals(loadBalancer.getLoadBalancer().getLoadBalancerNo())) {
                    sender.loadBalancerPanel.loadBalancerTable.select(itemId);
                    sender.loadBalancerPanel.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                    break;
                }
            }
        }
    }

    private class BasicInfo extends Panel {

        private final String COLUMN_HEIGHT = "30px";

        private GridLayout gridLayout;

        @Override
        public void attach() {
            setCaption(ViewProperties.getCaption("table.loadBalancerBasicInfo"));
            setHeight("95%");
            setStyleName("loadbalancer-desc-basic-panel");

            VerticalLayout layout = (VerticalLayout) getContent();
            layout.setStyleName("loadbalancer-desc-basic-panel");
            layout.setMargin(true);

            gridLayout = new GridLayout(2, 9);
            gridLayout.setWidth("100%");
            gridLayout.setStyleName("loadbalancer-desc-basic-info");
            gridLayout.setColumnExpandRatio(0, 40);
            gridLayout.setColumnExpandRatio(1, 60);
            layout.addComponent(gridLayout);

            int line = 0;

            // ロードバランサ名
            {
                Label label = new Label(ViewProperties.getCaption("field.loadBalancerName"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // 割り当てサービス
            {
                Label label = new Label(ViewProperties.getCaption("field.loadBalancerService"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // FQDN
            {
                Label label = new Label(ViewProperties.getCaption("field.fqdn"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // ホスト名
            {
                Label label = new Label(ViewProperties.getCaption("field.loadBalancerHostname"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // ステータス
            {
                Label label = new Label(ViewProperties.getCaption("field.status"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // プラットフォーム
            {
                Label label = new Label(ViewProperties.getCaption("field.platform"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // ロードバランサ種別
            {
                Label label = new Label(ViewProperties.getCaption("field.loadBalancerType"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // サブネット
            {
                Label label = new Label(ViewProperties.getCaption("field.subnet"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // コメント
            {
                Label label = new Label(ViewProperties.getCaption("field.comment"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }
        }

        public void initialize() {
            int line = 0;

            // ロードバランサ名
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // 割り当てサービス
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // FQDN
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ホスト名
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ステータス
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

            // ロードバランサ種別
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // サブネット
            {
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

        public void show(LoadBalancerDto loadBalancer) {
            int line = 0;

            // ロードバランサ名
            {
                Label label = new Label(loadBalancer.getLoadBalancer().getLoadBalancerName(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // 割り当てサービス
            {
                ComponentDto component = sender.getComponent(loadBalancer.getLoadBalancer().getComponentNo());

                String name;
                if (StringUtils.isEmpty(component.getComponent().getComment())) {
                    name = component.getComponent().getComponentName();
                } else {
                    name = component.getComponent().getComment() + " [" + component.getComponent().getComponentName()
                            + "]";
                }
                Icons icon = Icons.fromName(component.getComponentType().getComponentTypeName());

                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, name), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // FQDN
            {
                Label label = new Label(loadBalancer.getLoadBalancer().getFqdn(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ホスト名
            {
                Label label = new Label(loadBalancer.getLoadBalancer().getCanonicalName(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ステータス
            {
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
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // プラットフォーム
            {
                Icons icon = IconUtils.getPlatformIcon(loadBalancer.getPlatform());
                String description = loadBalancer.getPlatform().getPlatform().getPlatformNameDisp();
                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, description),
                        Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ロードバランサ種別
            {
                String type = "";
                ;
                if (StringUtils.isNotEmpty(loadBalancer.getLoadBalancer().getType())) {
                    type = ViewProperties.getLoadBalancerType(loadBalancer.getLoadBalancer().getType());
                }
                Label label = new Label(type, Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // サブネット
            {
                StringBuilder sb = new StringBuilder();

                if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
                    if (BooleanUtils.isTrue(loadBalancer.getPlatform().getPlatformAws().getVpc())
                            && StringUtils.isNotEmpty(loadBalancer.getAwsLoadBalancer().getSubnetId())) {
                        List<String> subnetIds = new ArrayList<String>();
                        for (String subnetId : loadBalancer.getAwsLoadBalancer().getSubnetId().split(",")) {
                            subnetIds.add(subnetId.trim());
                        }

                        AwsDescribeService awsDescribeService = BeanContext.getBean(AwsDescribeService.class);
                        List<Subnet> subnets = awsDescribeService.getSubnets(ViewContext.getUserNo(), loadBalancer
                                .getLoadBalancer().getPlatformNo());

                        for (Subnet subnet : subnets) {
                            if (subnetIds.contains(subnet.getSubnetId())) {
                                if (sb.length() > 0) {
                                    sb.append("<br>");
                                }
                                sb.append(subnet.getCidrBlock());
                            }
                        }
                    }
                }

                Label label = new Label(sb.toString(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // コメント
            {
                Label label = new Label(loadBalancer.getLoadBalancer().getComment(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }
        }
    }

    // ロードバランサリスナ一覧
    private class AttachServiceTable extends Table {

        private final String COLUMN_HEIGHT = "28px";

        //項目名
        private final String[] COLNAME = { null, ViewProperties.getCaption("field.loadBalancerPort"),
                ViewProperties.getCaption("field.loadBalancerServicePort"),
                ViewProperties.getCaption("field.loadBalancerProtocol"),
                ViewProperties.getCaption("field.loadBalancerServiceStatus"),
                ViewProperties.getCaption("field.editLoadBalancerListener"), };

        private final String[] VISIBLE_COLNAME = { "check", "loadBalancerPort", "servicePort", "protocol", "status",
                "edit", };

        private Map<Integer, CheckBox> checkBoxes = new HashMap<Integer, CheckBox>();

        @Override
        public void attach() {
            setIcon(Icons.LISTENER_MINI.resource());
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("loadbalancer-desc-basic-listener");
            setCaption(ViewProperties.getCaption("table.loadBalancerListener"));
            setHeight("100%");
            setSortDisabled(true);
            setImmediate(true);
            setVisible(true);

            addGeneratedColumn("check", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener listener = (LoadBalancerListener) itemId;

                    CheckBox check;
                    if (checkBoxes.containsKey(listener.getLoadBalancerPort())) {
                        check = checkBoxes.get(listener.getLoadBalancerPort());
                    } else {
                        check = new CheckBox();
                        checkBoxes.put(listener.getLoadBalancerPort(), check);
                    }

                    check.setImmediate(true);
                    check.addListener(new ValueChangeListener() {
                        @Override
                        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                            requestRepaint();
                        }
                    });

                    return check;
                }
            });

            addGeneratedColumn("edit", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener listener = (LoadBalancerListener) itemId;

                    Button editButton = new Button(ViewProperties.getCaption("button.editLoadBalancerListener"));
                    editButton.setDescription(ViewProperties.getCaption("description.editLoadBalancerListener"));
                    editButton.addStyleName("borderless");
                    editButton.setIcon(Icons.EDITMINI.resource());
                    editButton.setData(listener);

                    LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                    if (status == LoadBalancerListenerStatus.STOPPED) {
                        editButton.addListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(ClickEvent event) {
                                editButtonClick(event);
                            }
                        });
                    } else {
                        editButton.setEnabled(false);
                    }

                    return editButton;
                }
            });

            addGeneratedColumn("status", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener listener = (LoadBalancerListener) itemId;

                    LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                    Icons icon = Icons.fromName(status.name());

                    String statusString;
                    if (status == LoadBalancerListenerStatus.STOPPED) {
                        statusString = "DISABLE";
                    } else if (status == LoadBalancerListenerStatus.RUNNING) {
                        statusString = "ENABLE";
                    } else if (status == LoadBalancerListenerStatus.STARTING
                            || status == LoadBalancerListenerStatus.STOPPING) {
                        statusString = "CONFIGURING";
                    } else {
                        statusString = status.name();
                    }
                    statusString = statusString.substring(0, 1).toUpperCase() + statusString.substring(1).toLowerCase();

                    Label label = new Label(IconUtils.createImageTag(getApplication(), icon, statusString),
                            Label.CONTENT_XHTML);
                    label.setHeight(COLUMN_HEIGHT);
                    return label;
                }
            });

            addGeneratedColumn("loadBalancerPort", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener listener = (LoadBalancerListener) itemId;

                    String protocol = listener.getProtocol();

                    // HTTP, HTTPSでないときは通常のラベル
                    if (!"HTTP".equals(protocol) && !"HTTPS".equals(protocol)) {
                        return new Label(listener.getLoadBalancerPort().toString());
                    }

                    // ホスト名を取得
                    String hostName = loadBalancer.getLoadBalancer().getCanonicalName();
                    if (loadBalancer.getComponentLoadBalancerDto() != null) {
                        hostName = loadBalancer.getComponentLoadBalancerDto().getIpAddress();
                    }

                    // ホスト名が空の場合は通常のラベル
                    if (StringUtils.isEmpty(hostName)) {
                        return new Label(listener.getLoadBalancerPort().toString());
                    }

                    String url = protocol.toLowerCase() + "://" + hostName + ":"
                            + listener.getLoadBalancerPort().toString() + "/";
                    Link link = new Link(listener.getLoadBalancerPort().toString(), new ExternalResource(url));
                    link.setTargetName("_blank");
                    link.setIcon(Icons.SHORTCUT.resource());
                    link.setDescription(url);
                    return link;
                }
            });

            addGeneratedColumn("servicePort", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener listener = (LoadBalancerListener) itemId;

                    Label label = new Label(listener.getServicePort().toString());
                    return label;
                }
            });

            addGeneratedColumn("protocol", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener listener = (LoadBalancerListener) itemId;

                    Label label = new Label(listener.getProtocol());
                    return label;
                }
            });

            setColumnExpandRatio("status", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    LoadBalancerListener listener = (LoadBalancerListener) itemId;

                    String style = super.getStyle(itemId, propertyId);
                    if (propertyId != null) {
                        Integer no = listener.getLoadBalancerPort();
                        if (checkBoxes.containsKey(no) && (Boolean) checkBoxes.get(no).getValue()) {
                            style += " v-selected";
                        }
                    }

                    return style;
                }
            });

            addListener(new ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    LoadBalancerListener listener = (LoadBalancerListener) event.getItemId();

                    if (checkBoxes.containsKey(listener.getLoadBalancerPort())) {
                        CheckBox checkBox = checkBoxes.get(listener.getLoadBalancerPort());
                        checkBox.setValue(!((Boolean) checkBox.getValue()).booleanValue());
                    }
                }
            });

        }

        public void refresh(Collection<LoadBalancerListener> listeners, boolean clearCheckBox) {
            if (clearCheckBox) {
                checkBoxes.clear();
            }
            setContainerDataSource(new LoadBalancerListenerContainer(listeners));
            setVisibleColumns(VISIBLE_COLNAME);
            if (listeners.size() > 0) {
                setColumnHeaders(COLNAME);
            }
        }

        private class LoadBalancerListenerContainer extends BeanItemContainer<LoadBalancerListener> {
            public LoadBalancerListenerContainer(Collection<LoadBalancerListener> listeners) {
                super(LoadBalancerListener.class);
                for (LoadBalancerListener listener : listeners) {
                    addItem(listener);
                }
            }
        }

        private void editButtonClick(Button.ClickEvent event) {
            LoadBalancerListener listener = (LoadBalancerListener) event.getButton().getData();

            WinLoadBalancerConfigListener win = new WinLoadBalancerConfigListener(listener.getLoadBalancerNo(),
                    listener.getLoadBalancerPort());
            win.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    refreshTable();
                }
            });

            getWindow().addWindow(win);
        }
    }

    private class LoadbalancerServiceOperation extends HorizontalLayout {

        private final String BUTTON_WIDTH = "90px";

        private Button checkAllButton;

        private Button addButton;

        private Button deleteButton;

        private Button enableButton;

        private Button disableButton;

        @Override
        public void attach() {
            addStyleName("loadbalancer-service-operation-buttons");
            setHeight("35px");
            setWidth("100%");
            setSpacing(true);

            checkAllButton = new Button(ViewProperties.getCaption("button.checkAll"));
            checkAllButton.setDescription(ViewProperties.getCaption("description.checkAll"));
            checkAllButton.addStyleName("borderless");
            checkAllButton.addStyleName("checkall");
            checkAllButton.setEnabled(false);
            checkAllButton.setIcon(Icons.CHECKON.resource());
            checkAllButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    checkAllButtonClick(event);
                }
            });
            addComponent(checkAllButton);

            addButton = new Button(ViewProperties.getCaption("button.addLoadBalancerListener"));
            addButton.setDescription(ViewProperties.getCaption("description.addLoadBalancerListener"));
            addButton.setWidth(BUTTON_WIDTH);
            addButton.setIcon(Icons.ATTACH_MINI.resource());
            addButton.setEnabled(false);
            addButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    addButtonClick(event);
                }
            });
            addComponent(addButton);

            deleteButton = new Button(ViewProperties.getCaption("button.delLoadBalancerListener"));
            deleteButton.setDescription(ViewProperties.getCaption("description.delLoadBalancerListener"));
            deleteButton.setWidth(BUTTON_WIDTH);
            deleteButton.setIcon(Icons.DETACH_MINI.resource());
            deleteButton.setEnabled(false);
            deleteButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    deleteButtonClick(event);
                }
            });
            addComponent(deleteButton);

            enableButton = new Button(ViewProperties.getCaption("button.enableLoadBalancerListener"));
            enableButton.setDescription(ViewProperties.getCaption("description.enableLoadBalancerListener"));
            enableButton.setWidth(BUTTON_WIDTH);
            enableButton.setIcon(Icons.ENABLE_MINI.resource());
            enableButton.setEnabled(false);
            enableButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    enableButtonClick(event);
                }
            });
            addComponent(enableButton);

            disableButton = new Button(ViewProperties.getCaption("button.disableLoadBalancerListener"));
            disableButton.setDescription(ViewProperties.getCaption("description.disableLoadBalancerListener"));
            disableButton.setWidth(BUTTON_WIDTH);
            disableButton.setIcon(Icons.DISABLE_MINI.resource());
            disableButton.setEnabled(false);
            disableButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    disableButtonClick(event);
                }
            });
            addComponent(disableButton);

            setComponentAlignment(checkAllButton, Alignment.MIDDLE_LEFT);
            setComponentAlignment(addButton, Alignment.BOTTOM_LEFT);
            setComponentAlignment(deleteButton, Alignment.BOTTOM_LEFT);
            setComponentAlignment(enableButton, Alignment.BOTTOM_LEFT);
            setComponentAlignment(disableButton, Alignment.BOTTOM_LEFT);
            setExpandRatio(checkAllButton, 1f);
            setExpandRatio(deleteButton, 10f);
            setExpandRatio(disableButton, 10f);
        }

        public void initialize() {
            checkAllButton.setEnabled(false);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            enableButton.setEnabled(false);
            disableButton.setEnabled(false);
        }

        public void show(LoadBalancerDto loadBalancer) {
            // CloudStackの場合、リスナーを利用できない
            if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(loadBalancer.getLoadBalancer().getType())) {
                checkAllButton.setEnabled(false);
                addButton.setEnabled(false);
                deleteButton.setEnabled(false);
                enableButton.setEnabled(false);
                disableButton.setEnabled(false);
            } else {
                checkAllButton.setEnabled(true);
                addButton.setEnabled(true);
                deleteButton.setEnabled(true);
                enableButton.setEnabled(true);
                disableButton.setEnabled(true);

                // 権限がなければボタンを無効にする
                UserAuthDto auth = ViewContext.getAuthority();
                if (!auth.isLbOperate()) {
                    addButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    enableButton.setEnabled(false);
                    disableButton.setEnabled(false);
                }
            }
        }

        private void checkAllButtonClick(Button.ClickEvent event) {
            // 全てチェックされていれば全てオフ、それ以外は全てオンにする
            boolean checkAll = true;
            for (CheckBox checkBox : attachServiceTable.checkBoxes.values()) {
                if (BooleanUtils.isNotTrue((Boolean) checkBox.getValue())) {
                    checkAll = false;
                    break;
                }
            }

            for (CheckBox checkBox : attachServiceTable.checkBoxes.values()) {
                checkBox.setValue(!checkAll);
            }
        }

        private void addButtonClick(Button.ClickEvent event) {
            WinLoadBalancerConfigListener win = new WinLoadBalancerConfigListener(loadBalancer.getLoadBalancer()
                    .getLoadBalancerNo(), null);
            win.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    refreshTable();
                }
            });

            getWindow().addWindow(win);
        }

        private void deleteButtonClick(Button.ClickEvent event) {
            // 選択されているリスナーを取得
            List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();
            for (Entry<Integer, CheckBox> entry : attachServiceTable.checkBoxes.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    for (LoadBalancerListener listener : loadBalancer.getLoadBalancerListeners()) {
                        if (listener.getLoadBalancerPort().equals(entry.getKey())) {
                            listeners.add(listener);
                            break;
                        }
                    }
                }
            }

            if (listeners.size() != 1) {
                // リスナーが選択されていない、または複数選択されている場合
                String message = ViewMessages.getMessage("IUI-000078");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            final LoadBalancerListener listener = listeners.get(0);

            LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
            if (status != LoadBalancerListenerStatus.STOPPED) {
                // リスナーが停止していない場合
                String message = ViewMessages.getMessage("IUI-000079");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 確認ダイアログを表示
            String message = ViewMessages.getMessage("IUI-000080", listener.getLoadBalancerPort());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    delete(listener);
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        private void delete(LoadBalancerListener listener) {
            // オペレーションログ
            OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Detach LB_Listener", listener.getLoadBalancerNo(),
                    String.valueOf(listener.getLoadBalancerPort()));

            // リスナーの削除
            LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
            loadBalancerService.deleteListener(loadBalancer.getLoadBalancer().getLoadBalancerNo(),
                    listener.getLoadBalancerPort());

            // 表示の更新
            refreshTable();
        }

        private void enableButtonClick(Button.ClickEvent event) {
            // 選択されているリスナーを取得
            final List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();
            for (Entry<Integer, CheckBox> entry : attachServiceTable.checkBoxes.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    for (LoadBalancerListener listener : loadBalancer.getLoadBalancerListeners()) {
                        if (listener.getLoadBalancerPort().equals(entry.getKey())) {
                            listeners.add(listener);
                            break;
                        }
                    }
                }
            }

            if (listeners.isEmpty()) {
                // リスナーが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000077");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            for (LoadBalancerListener listener : listeners) {
                LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                if (status == LoadBalancerListenerStatus.STARTING || status == LoadBalancerListenerStatus.CONFIGURING
                        || status == LoadBalancerListenerStatus.STOPPING
                        || status == LoadBalancerListenerStatus.WARNING) {
                    // リスナーを有効にできないステータスの場合
                    String status2;
                    if (status == LoadBalancerListenerStatus.STOPPED) {
                        status2 = "DISABLE";
                    } else if (status == LoadBalancerListenerStatus.RUNNING) {
                        status2 = "ENABLE";
                    } else if (status == LoadBalancerListenerStatus.STARTING
                            || status == LoadBalancerListenerStatus.STOPPING) {
                        status2 = "CONFIGURING";
                    } else {
                        status2 = status.toString();
                    }
                    status2 = status2.substring(0, 1).toUpperCase() + status2.substring(1).toLowerCase();

                    String message = ViewMessages.getMessage("IUI-000087", status2);
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            // 確認ダイアログを表示
            StringBuilder sb = new StringBuilder();
            for (LoadBalancerListener listener : listeners) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(listener.getLoadBalancerPort());
            }
            String message = ViewMessages.getMessage("IUI-000081", sb.toString());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    enable(listeners);
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        private void enable(List<LoadBalancerListener> listeners) {
            // オペレーションログ
            OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Enable LB_Listener", loadBalancer.getLoadBalancer()
                    .getLoadBalancerNo(), String.valueOf(listeners.size()));

            // リスナーの有効化
            List<Integer> loadBalancerPorts = new ArrayList<Integer>();
            for (LoadBalancerListener listener : listeners) {
                loadBalancerPorts.add(listener.getLoadBalancerPort());
            }

            ProcessService processService = BeanContext.getBean(ProcessService.class);
            processService.startLoadBalancerListeners(loadBalancer.getLoadBalancer().getFarmNo(), loadBalancer
                    .getLoadBalancer().getLoadBalancerNo(), loadBalancerPorts);

            // 表示の更新
            refreshTable();
        }

        private void disableButtonClick(Button.ClickEvent event) {
            // 選択されているリスナーを取得
            final List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();
            for (Entry<Integer, CheckBox> entry : attachServiceTable.checkBoxes.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    for (LoadBalancerListener listener : loadBalancer.getLoadBalancerListeners()) {
                        if (listener.getLoadBalancerPort().equals(entry.getKey())) {
                            listeners.add(listener);
                            break;
                        }
                    }
                }
            }

            if (listeners.isEmpty()) {
                // リスナーが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000077");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            for (LoadBalancerListener listener : listeners) {
                LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                if (status == LoadBalancerListenerStatus.STARTING || status == LoadBalancerListenerStatus.CONFIGURING
                        || status == LoadBalancerListenerStatus.STOPPING) {
                    // リスナーを無効有効にできないステータスの場合
                    String status2;
                    if (status == LoadBalancerListenerStatus.STOPPED) {
                        status2 = "DISABLE";
                    } else if (status == LoadBalancerListenerStatus.RUNNING) {
                        status2 = "ENABLE";
                    } else if (status == LoadBalancerListenerStatus.STARTING
                            || status == LoadBalancerListenerStatus.STOPPING) {
                        status2 = "CONFIGURING";
                    } else {
                        status2 = status.toString();
                    }
                    status2 = status2.substring(0, 1).toUpperCase() + status2.substring(1).toLowerCase();

                    String message = ViewMessages.getMessage("IUI-000088", status2);
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            // 確認ダイアログを表示
            StringBuilder sb = new StringBuilder();
            for (LoadBalancerListener listener : listeners) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(listener.getLoadBalancerPort());
            }
            String message = ViewMessages.getMessage("IUI-000082", sb.toString());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    disable(listeners);
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        private void disable(List<LoadBalancerListener> listeners) {
            // オペレーションログ
            OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Disable LB_Listener", loadBalancer.getLoadBalancer()
                    .getLoadBalancerNo(), String.valueOf(listeners.size()));

            // リスナーの無効化
            List<Integer> loadBalancerPorts = new ArrayList<Integer>();
            for (LoadBalancerListener listener : listeners) {
                loadBalancerPorts.add(listener.getLoadBalancerPort());
            }

            ProcessService processService = BeanContext.getBean(ProcessService.class);
            processService.stopLoadBalancerListeners(loadBalancer.getLoadBalancer().getFarmNo(), loadBalancer
                    .getLoadBalancer().getLoadBalancerNo(), loadBalancerPorts);

            // 表示の更新
            refreshTable();
        }
    }

}
