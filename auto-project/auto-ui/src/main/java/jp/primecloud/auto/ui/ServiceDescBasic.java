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

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Property;
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
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * サービスView下部の基本情報を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServiceDescBasic extends Panel {

    private MainView sender;

    private BasicInfoOpe left;

    private AttachServersOpe right;

    private ServiceSvrOperation serverOpe;

    private ComponentDto component;

    public ServiceDescBasic(MainView sender) {
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
        layout.addStyleName("service-desc-basic");
        setContent(layout);

        // サービス基本情報
        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setMargin(false);
        leftLayout.setSpacing(false);
        leftLayout.setWidth("100%");
        leftLayout.setHeight("100%");

        left = new BasicInfoOpe();
        left.setWidth("100%");
        leftLayout.addComponent(left);
        leftLayout.setExpandRatio(left, 1.0f);
        layout.addComponent(leftLayout);

        // 表同士の間隔をあける
        Label padding = new Label(" ");
        padding.setWidth("7px");
        padding.setHeight("99%");
        padding.addStyleName("desc-padding");
        layout.addComponent(padding);

        Label padding2 = new Label("");
        padding2.setWidth("1px");
        layout.addComponent(padding2);

        // 割り当てサーバ
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setMargin(false);
        rightLayout.setSpacing(false);
        rightLayout.setWidth("100%");
        rightLayout.setHeight("100%");

        right = new AttachServersOpe();
        right.setWidth("100%");
        rightLayout.addComponent(right);
        serverOpe = new ServiceSvrOperation();
        rightLayout.addComponent(serverOpe);
        rightLayout.setExpandRatio(right, 1.0f);
        layout.addComponent(rightLayout);

        layout.setExpandRatio(leftLayout, 40);
        layout.setExpandRatio(rightLayout, 60);
    }

    public void initialize() {
        this.component = null;

        left.initialize();
        right.getContainerDataSource().removeAllItems();
        serverOpe.initialize();
    }

    public void show(ComponentDto component, boolean clearCheckBox) {
        this.component = component;

        List<InstanceDto> instances = sender.getInstances(component.getComponentInstances());

        left.show(component);
        right.refresh(instances, clearCheckBox);
        serverOpe.refresh(instances);
    }

    private ComponentInstanceDto findComponentInstance(Long instanceNo) {
        for (ComponentInstanceDto componentInstance : component.getComponentInstances()) {
            if (componentInstance.getComponentInstance().getInstanceNo().equals(instanceNo)) {
                return componentInstance;
            }
        }

        return null;
    }

    // 割り当てサーバ
    private class AttachServersOpe extends Table {

        private final String COLUMN_HEIGHT = "28px";

        private Object[] COLUMNS = { "check", "instanceName", "urlIcon", "status", "platform" };

        //項目名
        private String[] COLNAME = { null, ViewProperties.getCaption("field.serverName"),
                ViewProperties.getCaption("field.managementUrl"), ViewProperties.getCaption("field.serviceStatus"),
                ViewProperties.getCaption("field.platform") };

        private Map<Long, CheckBox> checkBoxes = new HashMap<Long, CheckBox>();

        @Override
        public void attach() {
            setIcon(Icons.SERVERTAB.resource());
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("service-desc-basic-server");
            setCaption(ViewProperties.getCaption("table.serviceServers"));
            setHeight("100%");
            setSortDisabled(true);
            setImmediate(true);
            setVisible(true);

            addGeneratedColumn("check", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    CheckBox check;
                    if (checkBoxes.containsKey(instance.getInstance().getInstanceNo())) {
                        check = checkBoxes.get(instance.getInstance().getInstanceNo());
                    } else {
                        check = new CheckBox();
                        checkBoxes.put(instance.getInstance().getInstanceNo(), check);
                    }

                    check.setImmediate(true);
                    check.addListener(new ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent event) {
                            requestRepaint();
                        }
                    });

                    return check;
                }
            });

            addGeneratedColumn("instanceName", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    Label label = new Label(instance.getInstance().getInstanceName());
                    return label;
                }
            });

            addGeneratedColumn("urlIcon", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    ComponentInstanceDto componentInstance = findComponentInstance(
                            instance.getInstance().getInstanceNo());
                    String url = (componentInstance == null) ? "" : componentInstance.getUrl();
                    String status = (componentInstance == null) ? ""
                            : componentInstance.getComponentInstance().getStatus();

                    Icons icon = Icons.fromName(component.getComponentType().getComponentTypeName());

                    // MySQLならMasterとSlaveでアイコンを変える
                    if (MySQLConstants.COMPONENT_TYPE_NAME
                            .equals(component.getComponentType().getComponentTypeName())) {
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
                    InstanceDto instance = (InstanceDto) itemId;

                    ComponentInstanceDto componentInstance = findComponentInstance(
                            instance.getInstance().getInstanceNo());
                    String status = (componentInstance == null) ? ""
                            : componentInstance.getComponentInstance().getStatus();

                    Icons icon = Icons.fromName(status);
                    status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                    Label label = new Label(IconUtils.createImageTag(getApplication(), icon, status),
                            Label.CONTENT_XHTML);
                    label.setHeight(COLUMN_HEIGHT);

                    return label;
                }
            });

            addGeneratedColumn("platform", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    Icons icon = IconUtils.getPlatformIcon(instance.getPlatform());
                    String name = instance.getPlatform().getPlatform().getPlatformSimplenameDisp();
                    Label label = new Label(IconUtils.createImageTag(getApplication(), icon, name),
                            Label.CONTENT_XHTML);
                    label.setHeight(COLUMN_HEIGHT);

                    return label;
                }
            });

            setCellStyleGenerator(new StandardCellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    String style = super.getStyle(itemId, propertyId);
                    if (propertyId != null) {
                        Long instanceNo = instance.getInstance().getInstanceNo();
                        if (checkBoxes.containsKey(instanceNo) && (Boolean) checkBoxes.get(instanceNo).getValue()) {
                            style += " v-selected";
                        }
                    }

                    return style;
                }
            });

            setColumnExpandRatio("instanceName", 100);

            addListener(new ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    InstanceDto instance = (InstanceDto) event.getItemId();
                    Long instanceNo = instance.getInstance().getInstanceNo();
                    if (checkBoxes.containsKey(instanceNo)) {
                        checkBoxes.get(instanceNo).setValue(!(Boolean) checkBoxes.get(instanceNo).getValue());
                    }
                }
            });
        }

        public void refresh(List<InstanceDto> instances, boolean clearCheckBox) {
            if (clearCheckBox) {
                checkBoxes.clear();
            }
            setContainerDataSource(new InstanceDtoContainer(instances));
            setVisibleColumns(COLUMNS);
            if (instances.size() > 0) {
                setColumnHeaders(COLNAME);
            }
        }

    }

    // サービス基本情報
    private class BasicInfoOpe extends Panel {

        private final String COLUMN_HEIGHT = "30px";

        private GridLayout gridLayout;

        @Override
        public void attach() {
            setCaption(ViewProperties.getCaption("table.serviceBasicInfo"));
            setHeight("100%");
            setStyleName("service-desc-basic-panel");

            VerticalLayout layout = (VerticalLayout) getContent();
            layout.setStyleName("service-desc-basic-panel");
            layout.setMargin(true);

            gridLayout = new GridLayout(2, 5);
            gridLayout.setWidth("100%");
            gridLayout.setStyleName("service-desc-basic-info");
            gridLayout.setColumnExpandRatio(0, 35);
            gridLayout.setColumnExpandRatio(1, 65);
            layout.addComponent(gridLayout);

            int line = 0;

            // サービス名
            {
                Label label = new Label(ViewProperties.getCaption("field.serviceName"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // サービス
            {
                Label label = new Label(ViewProperties.getCaption("field.service"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // ステータス
            {
                Label label = new Label(ViewProperties.getCaption("field.status"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // コメント
            {
                Label label = new Label(ViewProperties.getCaption("field.comment"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }

            // ロードバランサ
            {
                Label label = new Label(ViewProperties.getCaption("field.loadBalancer"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line++);
            }
        }

        public void initialize() {
            int line = 0;

            // サービス名
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // サービス
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

            // コメント
            {
                Label label = new Label("", Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ロードバランサ
            {
                VerticalLayout layout = new VerticalLayout();
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(layout, 1, line++);
            }
        }

        public void show(ComponentDto component) {
            int line = 0;

            // サービス名
            {
                Label label = new Label(component.getComponent().getComponentName(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // サービス
            {
                Icons icon = Icons.fromName(component.getComponentType().getComponentTypeName());
                String name = component.getComponentType().getComponentTypeNameDisp();
                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, name), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ステータス
            {
                String status = component.getStatus();
                Icons icon = Icons.fromName(status);
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, status), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // コメント
            {
                Label label = new Label(component.getComponent().getComment(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ロードバランサ
            {
                VerticalLayout layout = new VerticalLayout();
                layout.setSpacing(false);

                List<LoadBalancerDto> loadBalancers = sender
                        .getLoadBalancers(component.getComponent().getComponentNo());
                for (LoadBalancerDto loadBalancer : loadBalancers) {
                    layout.addComponent(createLoadBalancerButton(loadBalancer));
                }

                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(layout, 1, line++);
            }
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

    }

    private class ServiceSvrOperation extends HorizontalLayout {

        private Button checkAllButton;

        private Button startButton;

        private Button stopButton;

        @Override
        public void attach() {
            addStyleName("operation-buttons");
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

            startButton = new Button(ViewProperties.getCaption("button.startService"));
            startButton.setDescription(ViewProperties.getCaption("description.startService"));
            startButton.setWidth("90px");
            startButton.setIcon(Icons.PLAYMINI.resource());
            startButton.setEnabled(false);
            startButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    startButtonClick(event);
                }
            });
            addComponent(startButton);

            stopButton = new Button(ViewProperties.getCaption("button.stopService"));
            stopButton.setDescription(ViewProperties.getCaption("description.stopService"));
            stopButton.setWidth("90px");
            stopButton.setIcon(Icons.STOPMINI.resource());
            stopButton.setEnabled(false);
            stopButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    stopButtonClick(event);
                }
            });
            addComponent(stopButton);

            setComponentAlignment(checkAllButton, Alignment.MIDDLE_LEFT);
            setComponentAlignment(startButton, Alignment.BOTTOM_RIGHT);
            setComponentAlignment(stopButton, Alignment.BOTTOM_RIGHT);
            setExpandRatio(checkAllButton, 1.0f);
        }

        public void initialize() {
            checkAllButton.setEnabled(false);
            startButton.setEnabled(false);
            stopButton.setEnabled(false);
        }

        public void refresh(Collection<InstanceDto> instances) {
            if (instances.size() > 0) {
                checkAllButton.setEnabled(true);
                startButton.setEnabled(true);
                stopButton.setEnabled(true);
            } else {
                checkAllButton.setEnabled(false);
                startButton.setEnabled(false);
                stopButton.setEnabled(false);
            }
        }

        private void checkAllButtonClick(Button.ClickEvent event) {
            // 全てチェックされていれば全てオフ、それ以外は全てオンにする
            boolean checkAll = true;
            for (CheckBox checkBox : right.checkBoxes.values()) {
                if (BooleanUtils.isNotTrue((Boolean) checkBox.getValue())) {
                    checkAll = false;
                    break;
                }
            }

            for (CheckBox checkBox : right.checkBoxes.values()) {
                checkBox.setValue(!checkAll);
            }
        }

        private void startButtonClick(Button.ClickEvent event) {
            final List<InstanceDto> selectedInstances = new ArrayList<InstanceDto>();
            for (Object itemId : right.getItemIds()) {
                InstanceDto instance = (InstanceDto) itemId;
                Long instanceNo = instance.getInstance().getInstanceNo();
                if (right.checkBoxes.containsKey(instanceNo) && (Boolean) right.checkBoxes.get(instanceNo).getValue()) {
                    selectedInstances.add(instance);
                }
            }

            // サーバが選択されていることを確認
            if (selectedInstances.isEmpty()) {
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 変更中のサーバが存在しないことを確認
            for (InstanceDto instance : selectedInstances) {
                ComponentInstanceDto componentInstance = findComponentInstance(instance.getInstance().getInstanceNo());
                String status = componentInstance.getComponentInstance().getStatus();
                if (status.equals(ComponentInstanceStatus.CONFIGURING.toString())
                        || status.equals(ComponentInstanceStatus.STARTING.toString())
                        || status.equals(ComponentInstanceStatus.STOPPING.toString())
                        || status.equals(ComponentInstanceStatus.WARNING.toString())) {
                    String message = ViewMessages.getMessage("IUI-000044",
                            new Object[] { StringUtils.capitalize(status.toLowerCase()) });
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            // サブネットが設定されていることを確認
            for (InstanceDto instance : selectedInstances) {
                PlatformDto platform = instance.getPlatform();

                // AWSの場合
                if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())) {
                    if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())
                            && StringUtils.isEmpty(instance.getAwsInstance().getSubnetId())) {
                        throw new AutoApplicationException("IUI-000112", instance.getInstance().getInstanceName());
                    }
                }
                // Azureの場合
                else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
                    if (StringUtils.isEmpty(instance.getAzureInstance().getSubnetId())) {
                        throw new AutoApplicationException("IUI-000112", instance.getInstance().getInstanceName());
                    }
                }
            }

            boolean skipServer = false;
            for (InstanceDto instance : selectedInstances) {
                PlatformDto platform = instance.getPlatform();
                ProcessService processService = BeanContext.getBean(ProcessService.class);

                if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
                    // インスタンス起動チェック（同時起動）
                    Map<String, Boolean> flgMap = new HashMap<String, Boolean>();
                    flgMap = processService.checkStartupAll(platform.getPlatform().getPlatformType(),
                            instance.getAzureInstance().getInstanceName(), skipServer);
                    skipServer = flgMap.get("skipServer");
                    boolean startupAllErrFlg = flgMap.get("startupAllErrFlg");
                    if (startupAllErrFlg) {
                        // インスタンス作成中のものがあった場合は、起動不可
                        throw new AutoApplicationException("IUI-000134", instance.getInstance().getInstanceName());
                    }

                    // インスタンス起動チェック（個別起動）
                    boolean startupErrFlg = processService.checkStartup(platform.getPlatform().getPlatformType(),
                            instance.getAzureInstance().getInstanceName(), instance.getAzureInstance().getInstanceNo());
                    if (startupErrFlg) {
                        // インスタンス作成中のものがあった場合は、起動不可
                        // 同一インスタンスNoは、除外する
                        throw new AutoApplicationException("IUI-000134", instance.getInstance().getInstanceName());
                    }
                }
            }

            // 確認ダイアログを表示
            String message = ViewMessages.getMessage("IUI-000051",
                    new Object[] { selectedInstances.size(), component.getComponent().getComponentName() });
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    start(selectedInstances);
                }
            });

            getApplication().getMainWindow().addWindow(dialog);
        }

        private void start(List<InstanceDto> instances) {
            // オペレーションログ
            OperationLogger.writeComponent("SERVICE", "Start Service", component.getComponent().getComponentNo(), null);

            // 開始処理
            List<Long> instanceNos = new ArrayList<Long>();
            for (InstanceDto instance : instances) {
                instanceNos.add(instance.getInstance().getInstanceNo());
            }

            ProcessService processService = BeanContext.getBean(ProcessService.class);
            Long farmNo = component.getComponent().getFarmNo();
            Long componentNo = component.getComponent().getComponentNo();
            processService.startComponents(farmNo, componentNo, instanceNos);

            // 表示の更新
            sender.refreshTableOnly();
        }

        private void stopButtonClick(Button.ClickEvent event) {
            final List<InstanceDto> selectedInstances = new ArrayList<InstanceDto>();
            for (Object itemId : right.getItemIds()) {
                InstanceDto instance = (InstanceDto) itemId;
                Long instanceNo = instance.getInstance().getInstanceNo();
                if (right.checkBoxes.containsKey(instanceNo) && (Boolean) right.checkBoxes.get(instanceNo).getValue()) {
                    selectedInstances.add(instance);
                }
            }

            // サーバが選択されていることを確認
            if (selectedInstances.isEmpty()) {
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 変更中のサーバが存在しないことを確認
            for (InstanceDto instance : selectedInstances) {
                ComponentInstanceDto componentInstance = findComponentInstance(instance.getInstance().getInstanceNo());
                String status = componentInstance.getComponentInstance().getStatus();
                if (status.equals(ComponentInstanceStatus.CONFIGURING.toString())
                        || status.equals(ComponentInstanceStatus.STARTING.toString())
                        || status.equals(ComponentInstanceStatus.STOPPING.toString())) {
                    String message = ViewMessages.getMessage("IUI-000045",
                            new Object[] { StringUtils.capitalize(status.toLowerCase()) });
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            boolean isCheckbox = true;
            for (InstanceDto instance : selectedInstances) {
                // サーバに含まれる他のサービスのステータスを確認
                for (ComponentInstanceDto componentInstance : instance.getComponentInstances()) {
                    if (componentInstance.getComponentInstance().getComponentNo()
                            .equals(component.getComponent().getComponentNo())) {
                        continue;
                    }

                    // 停止していないサービスがある場合
                    if (!componentInstance.getComponentInstance().getStatus()
                            .equals(ComponentInstanceStatus.STOPPED.toString())) {
                        isCheckbox = false;
                        break;
                    }
                }
            }

            // 確認ダイアログの表示オプション
            HorizontalLayout optionLayout = new HorizontalLayout();
            final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000033"), false);
            checkBox.setImmediate(true);
            checkBox.setEnabled(isCheckbox);
            checkBox.setDescription(ViewProperties.getCaption("description.stopService.withServerStop"));
            optionLayout.addComponent(checkBox);

            // 確認ダイアログの表示
            String message = ViewMessages.getMessage("IUI-000052",
                    new Object[] { selectedInstances.size(), component.getComponent().getComponentName() });
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel, optionLayout);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    boolean stopInstance = (Boolean) checkBox.getValue();
                    stop(selectedInstances, stopInstance);
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        private void stop(List<InstanceDto> instances, boolean stopInstance) {
            // オペレーションログ
            OperationLogger.writeComponent("SERVICE", "Stop Service", component.getComponent().getComponentNo(),
                    String.valueOf(stopInstance));

            // 停止処理
            List<Long> instanceNos = new ArrayList<Long>();
            for (InstanceDto instance : instances) {
                instanceNos.add(instance.getInstance().getInstanceNo());
            }

            ProcessService processService = BeanContext.getBean(ProcessService.class);
            Long farmNo = component.getComponent().getFarmNo();
            Long componentNo = component.getComponent().getComponentNo();
            processService.stopComponents(farmNo, componentNo, instanceNos, stopInstance);

            // 表示の更新
            sender.refreshTableOnly();
        }

    }

}
