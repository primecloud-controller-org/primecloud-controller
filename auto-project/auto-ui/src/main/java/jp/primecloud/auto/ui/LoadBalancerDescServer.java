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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * ロードバランサ画面下部の詳細情報/割り当てサーバ情報の生成を行います。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerDescServer extends Panel {

    private MainView sender;

    private LoadBalancerDetailInfo loadBalancerInfo;

    private AttachSeriviceServerTable attachServiceServerTable;

    private LoadbalancerServerOperation loadBalancerOpe;

    private LoadBalancerDto loadBalancer;

    public LoadBalancerDescServer(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        addStyleName(Reindeer.PANEL_LIGHT);
        setHeight("100%");

        VerticalLayout panel = (VerticalLayout) getContent();
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.setMargin(true);
        panel.setSpacing(false);
        panel.addStyleName("loadbalancer-desc-basic");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addStyleName("loadbalancer-desc-basic");

        // ロードバランサ詳細情報
        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setWidth("100%");
        leftLayout.setHeight("100%");
        leftLayout.setMargin(true, false, false, false);
        leftLayout.setSpacing(false);

        loadBalancerInfo = new LoadBalancerDetailInfo();
        leftLayout.addComponent(loadBalancerInfo);
        leftLayout.setExpandRatio(loadBalancerInfo, 10);
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
        rightLayout.setWidth("100%");
        rightLayout.setHeight("100%");
        rightLayout.setMargin(false);
        rightLayout.setSpacing(false);
        rightLayout.addStyleName("loadbalancer-desc-server-right");

        attachServiceServerTable = new AttachSeriviceServerTable();
        rightLayout.addComponent(attachServiceServerTable);
        loadBalancerOpe = new LoadbalancerServerOperation();
        rightLayout.addComponent(loadBalancerOpe);
        rightLayout.setExpandRatio(attachServiceServerTable, 100);
        layout.addComponent(rightLayout);

        layout.setExpandRatio(leftLayout, 48);
        layout.setExpandRatio(rightLayout, 52);

        panel.addComponent(layout);
    }

    public void initialize() {
        this.loadBalancer = null;

        loadBalancerInfo.initialize();
        attachServiceServerTable.getContainerDataSource().removeAllItems();
        loadBalancerOpe.initialize();
    }

    public void show(LoadBalancerDto loadBalancer, boolean clearCheckBox) {
        this.loadBalancer = loadBalancer;

        loadBalancerInfo.show(loadBalancer);
        attachServiceServerTable.refresh(loadBalancer, clearCheckBox);
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

    // 詳細情報
    private class LoadBalancerDetailInfo extends Panel {

        private final String COLUMN_HEIGHT = "30px";

        private GridLayout gridLayout;

        @Override
        public void attach() {
            setCaption(ViewProperties.getCaption("table.loadBalancerDetailInfo"));
            setHeight("100%");
            setStyleName("loadbalancer-desc-basic-panel");

            VerticalLayout layout = (VerticalLayout) getContent();
            layout.setStyleName("loadbalancer-desc-basic-panel");
            layout.setMargin(true);
        }

        public void initialize() {
            getContent().removeAllComponents();
        }

        public void show(LoadBalancerDto loadBalancer) {
            VerticalLayout layout = (VerticalLayout) getContent();
            layout.removeAllComponents();

            int row = 1;
            if (loadBalancer.getLoadBalancerHealthCheck() != null) {
                row += 7;
            }
            if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(loadBalancer.getLoadBalancer().getType())) {
                row += 4;
            }

            gridLayout = new GridLayout(2, row);
            gridLayout.setMargin(false);
            gridLayout.setSpacing(false);
            gridLayout.setWidth("100%");
            gridLayout.setStyleName("loadbalancer-desc-basic-info");
            gridLayout.setColumnExpandRatio(0, 45);
            gridLayout.setColumnExpandRatio(1, 55);
            layout.addComponent(gridLayout);

            int line = 0;

            // ロードバランサ名
            {
                Label label = new Label(ViewProperties.getCaption("field.loadBalancerName"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(loadBalancer.getLoadBalancer().getLoadBalancerName(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // ヘルスチェック情報の表示
            line = showHealthCheck(loadBalancer, line);

            // CloudStack情報の表示
            if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(loadBalancer.getLoadBalancer().getType())) {
                line = showCloudStack(loadBalancer, line);
            }
        }

        private int showHealthCheck(LoadBalancerDto loadBalancer, int line) {
            LoadBalancerHealthCheck healthCheck = loadBalancer.getLoadBalancerHealthCheck();
            if (healthCheck == null) {
                return line;
            }

            // 監視プロトコル
            {
                Label label = new Label(ViewProperties.getCaption("field.checkProtocol"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(healthCheck.getCheckProtocol(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // 監視ポート
            {
                Label label = new Label(ViewProperties.getCaption("field.checkPort"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(healthCheck.getCheckPort().toString(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // 監視Path
            {
                Label label = new Label(ViewProperties.getCaption("field.checkPath"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(StringUtils.defaultString(healthCheck.getCheckPath(), ""), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // タイムアウト時間
            {
                Label label = new Label(ViewProperties.getCaption("field.checkTimeout"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(ObjectUtils.toString(healthCheck.getCheckTimeout(), ""), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // チェック間隔
            {
                Label label = new Label(ViewProperties.getCaption("field.checkInterval"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(ObjectUtils.toString(healthCheck.getCheckInterval(), ""), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // 障害しきい値
            {
                Label label = new Label(ViewProperties.getCaption("field.checkDownThreshold"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(ObjectUtils.toString(healthCheck.getUnhealthyThreshold(), ""), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // 復帰しきい値
            {
                Label label = new Label(ViewProperties.getCaption("field.checkRecoverThreshold"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(ObjectUtils.toString(healthCheck.getHealthyThreshold(), ""), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            return line;
        }

        private int showCloudStack(LoadBalancerDto loadBalancer, int line) {
            // アルゴリズム
            {
                Label label = new Label(ViewProperties.getCaption("field.algorithm"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(loadBalancer.getCloudstackLoadBalancer().getAlgorithm(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // パブリックIPアドレス
            {
                Label label = new Label(ViewProperties.getCaption("field.publicip"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(loadBalancer.getCloudstackLoadBalancer().getPublicip(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // パブリックポート
            {
                Label label = new Label(ViewProperties.getCaption("field.privateport"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(loadBalancer.getCloudstackLoadBalancer().getPublicport(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            // プライベートポート
            {
                Label label = new Label(ViewProperties.getCaption("field.checkProtocol"), Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);
                gridLayout.addComponent(label, 0, line);

                label = new Label(loadBalancer.getCloudstackLoadBalancer().getPrivateport(), Label.CONTENT_XHTML);
                gridLayout.removeComponent(1, line);
                gridLayout.addComponent(label, 1, line++);
            }

            return line;
        }

    }

    // 割り当てサーバ
    private class AttachSeriviceServerTable extends Table {

        private final String COLUMN_HEIGHT = "28px";

        //項目名
        private final String[] COLNAME = { null, ViewProperties.getCaption("field.serverName"),
                ViewProperties.getCaption("field.loadBalancerServerStatus"),
                ViewProperties.getCaption("field.serviceStatus") };

        private final String[] VISIBLE_COLNAME = { "check", "instanceName", "status", "serviceStatus" };

        private HashMap<Long, CheckBox> checkBoxes = new HashMap<Long, CheckBox>();

        private HashMap<Long, String> statusMap = new HashMap<Long, String>();

        @Override
        public void attach() {
            setIcon(Icons.SERVERTAB.resource());
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("loadbalancer-desc-service-servers");
            setCaption(ViewProperties.getCaption("table.loadBalancerServiceServers"));
            setWidth("100%");
            setHeight("100%");
            setSortDisabled(true);
            setImmediate(true);
            setVisible(true);

            addGeneratedColumn("check", new ColumnGenerator() {
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
                        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                            requestRepaint();
                        }
                    });
                    return check;
                }
            });

            addGeneratedColumn("instanceName", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    Icons icon = IconUtils.getPlatformIcon(instance.getPlatform());
                    Label label = new Label(IconUtils.createImageTag(getApplication(), icon, instance.getInstance()
                            .getInstanceName()), Label.CONTENT_XHTML);
                    label.setHeight(COLUMN_HEIGHT);
                    return label;
                }
            });

            addGeneratedColumn("status", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    LoadBalancerInstance lbInstance = null;
                    for (LoadBalancerInstance lbInstance2 : loadBalancer.getLoadBalancerInstances()) {
                        if (lbInstance2.getInstanceNo().equals(instance.getInstance().getInstanceNo())) {
                            lbInstance = lbInstance2;
                            break;
                        }
                    }

                    boolean lbInstanceEnabled = false;
                    LoadBalancerInstanceStatus lbInstanceStatus = LoadBalancerInstanceStatus.STOPPED;
                    if (lbInstance != null) {
                        lbInstanceEnabled = BooleanUtils.isTrue(lbInstance.getEnabled());
                        lbInstanceStatus = LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus());
                    }

                    LoadBalancerStatus lbStatus = LoadBalancerStatus.fromStatus(loadBalancer.getLoadBalancer()
                            .getStatus());
                    InstanceStatus instanceStatus = InstanceStatus.fromStatus(instance.getInstance().getStatus());
                    boolean lbRunning = (lbStatus == LoadBalancerStatus.RUNNING || lbStatus == LoadBalancerStatus.CONFIGURING);
                    boolean instanceRunning = (instanceStatus == InstanceStatus.RUNNING || instanceStatus == InstanceStatus.CONFIGURING);

                    String status;
                    boolean notice = false;
                    String noticeMessage = null;

                    if (lbInstanceStatus == LoadBalancerInstanceStatus.WARNING) {
                        status = "Warning";
                    } else if (lbRunning && instanceRunning) {
                        // ロードバランサーとサーバがどちらとものRunningのとき
                        if (lbInstanceEnabled) {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.RUNNING) {
                                status = "Enable";
                            } else {
                                status = "Configuring";
                            }
                        } else {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.STOPPED) {
                                status = "Disable";
                            } else {
                                status = "Configuring";
                            }
                        }
                    } else {
                        // ロードバランサとサーバのどちらか一方でもRunningでないとき
                        if (lbInstanceEnabled) {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.RUNNING) {
                                status = "Enable";
                            } else {
                                status = "Enable";
                                notice = true;
                                if (!lbRunning && !instanceRunning) {
                                    noticeMessage = MessageUtils.getMessage("IUI-000091");
                                } else if (!lbRunning) {
                                    noticeMessage = MessageUtils.getMessage("IUI-000092");
                                } else if (!instanceRunning) {
                                    noticeMessage = MessageUtils.getMessage("IUI-000093");
                                }
                            }
                        } else {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.STOPPED) {
                                status = "Disable";
                            } else {
                                status = "Disable";
                            }
                        }
                    }

                    statusMap.put(instance.getInstance().getInstanceNo(), status);
                    Icons icon;
                    Label label;
                    if (notice) {
                        icon = Icons.fromName(status + "_WITH_ATTENTION");
                        label = new Label(IconUtils.createImageTag(getApplication(), icon, status), Label.CONTENT_XHTML);
                        label.setDescription(noticeMessage);
                    } else {
                        icon = Icons.fromName(status);
                        label = new Label(IconUtils.createImageTag(getApplication(), icon, status), Label.CONTENT_XHTML);
                    }
                    label.setHeight(COLUMN_HEIGHT);
                    return label;
                }
            });

            addGeneratedColumn("serviceStatus", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    String status = "";
                    instance.getComponentInstances();
                    for (ComponentInstanceDto componentInstance : instance.getComponentInstances()) {
                        if (componentInstance.getComponentInstance().getInstanceNo()
                                .equals(instance.getInstance().getInstanceNo())) {
                            status = componentInstance.getComponentInstance().getStatus();
                            break;
                        }
                    }

                    Icons icon = Icons.fromName(status);
                    status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                    Label label = new Label(IconUtils.createImageTag(getApplication(), icon, status),
                            Label.CONTENT_XHTML);
                    label.setHeight(COLUMN_HEIGHT);
                    return label;
                }
            });

            setColumnExpandRatio("instanceName", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    InstanceDto instance = (InstanceDto) itemId;

                    String style = super.getStyle(itemId, propertyId);
                    if (propertyId != null) {
                        if (checkBoxes.containsKey(instance.getInstance().getInstanceNo())
                                && (Boolean) checkBoxes.get(instance.getInstance().getInstanceNo()).getValue()) {
                            style += " v-selected";
                        }
                    }
                    return style;
                }
            });

            addListener(new ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    InstanceDto instance = (InstanceDto) event.getItemId();

                    CheckBox checkBox = checkBoxes.get(instance.getInstance().getInstanceNo());
                    if (checkBox != null) {
                        checkBox.setValue(!((Boolean) checkBox.getValue()).booleanValue());
                    }
                }
            });
        }

        public void refresh(LoadBalancerDto loadBalancer, boolean clearCheckBox) {
            if (clearCheckBox) {
                checkBoxes.clear();
            }

            ComponentDto component = sender.getComponent(loadBalancer.getLoadBalancer().getComponentNo());
            setContainerDataSource(new InstanceDtoContainer(sender.getInstances(component.getComponentInstances())));
            setVisibleColumns(VISIBLE_COLNAME);
            setColumnHeaders(COLNAME);
        }

    }

    private class LoadbalancerServerOperation extends HorizontalLayout {

        private final String BUTTON_WIDTH = "90px";

        private Button checkAllButton;

        private Button enableButton;

        private Button disableButton;

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

            enableButton = new Button(ViewProperties.getCaption("button.enableLoadBalanceServer"));
            enableButton.setDescription(ViewProperties.getCaption("description.enableLoadBalanceServer"));
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

            disableButton = new Button(ViewProperties.getCaption("button.disableLoadBalanceServer"));
            disableButton.setDescription(ViewProperties.getCaption("description.disableLoadBalanceServer"));
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
            setComponentAlignment(enableButton, Alignment.BOTTOM_RIGHT);
            setComponentAlignment(disableButton, Alignment.BOTTOM_RIGHT);
            setExpandRatio(checkAllButton, 1.0f);
        }

        public void initialize() {
            checkAllButton.setEnabled(false);
            enableButton.setEnabled(false);
            disableButton.setEnabled(false);
        }

        public void show(LoadBalancerDto loadBalancer) {
            if (loadBalancer.getLoadBalancerInstances().size() == 0) {
                checkAllButton.setEnabled(false);
                enableButton.setEnabled(false);
                disableButton.setEnabled(false);
            } else {
                checkAllButton.setEnabled(true);
                enableButton.setEnabled(true);
                disableButton.setEnabled(true);

                // 権限がなければボタンを無効にする
                UserAuthDto auth = ViewContext.getAuthority();
                if (!auth.isLbOperate()) {
                    enableButton.setEnabled(false);
                    disableButton.setEnabled(false);
                }
            }
        }

        private void checkAllButtonClick(Button.ClickEvent event) {
            // 全てチェックされていれば全てオフ、それ以外は全てオンにする
            boolean checkAll = true;
            for (CheckBox checkBox : attachServiceServerTable.checkBoxes.values()) {
                if (BooleanUtils.isNotTrue((Boolean) checkBox.getValue())) {
                    checkAll = false;
                    break;
                }
            }

            for (CheckBox checkBox : attachServiceServerTable.checkBoxes.values()) {
                checkBox.setValue(!checkAll);
            }
        }

        private void enableButtonClick(Button.ClickEvent event) {
            // 選択されているサーバの番号を取得
            final List<Long> instanceNos = new ArrayList<Long>();
            for (Map.Entry<Long, CheckBox> entry : attachServiceServerTable.checkBoxes.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    instanceNos.add(entry.getKey());
                }
            }

            if (instanceNos.isEmpty()) {
                // サーバが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 振り分けサーバを有効にできるかどうかのチェック
            for (LoadBalancerInstance lbInstance : loadBalancer.getLoadBalancerInstances()) {
                if (instanceNos.contains(lbInstance.getInstanceNo())) {
                    String status = attachServiceServerTable.statusMap.get(lbInstance.getInstanceNo());
                    if ("Configuring".equals(status) || "Warning".equals(status)) {
                        // 振り分けサーバを有効にできないステータスの場合
                        String message = ViewMessages.getMessage("IUI-000089", status);
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }
                }
            }

            // 確認ダイアログの表示
            String message = ViewMessages.getMessage("IUI-000085", new Object[] { instanceNos.size() });
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    enable(loadBalancer.getLoadBalancer().getLoadBalancerNo(), instanceNos);
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        private void enable(Long loadBalancerNo, List<Long> instanceNos) {
            // オペレーションログ
            OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Enable Server", loadBalancerNo,
                    String.valueOf(instanceNos.size()));

            // 振り分けの有効化
            LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
            loadBalancerService.enableInstances(loadBalancerNo, instanceNos);

            // 表示の更新
            refreshTable();
        }

        private void disableButtonClick(Button.ClickEvent event) {
            // 選択されているサーバの番号を取得
            final List<Long> instanceNos = new ArrayList<Long>();
            for (Map.Entry<Long, CheckBox> entry : attachServiceServerTable.checkBoxes.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    instanceNos.add(entry.getKey());
                }
            }

            if (instanceNos.isEmpty()) {
                // サーバが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 振り分けサーバを無効にできるかどうかのチェック
            for (LoadBalancerInstance lbInstance : loadBalancer.getLoadBalancerInstances()) {
                if (instanceNos.contains(lbInstance.getInstanceNo())) {
                    String status = attachServiceServerTable.statusMap.get(lbInstance.getInstanceNo());
                    if ("Configuring".equals(status)) {
                        // 振り分けサーバを有効にできないステータスの場合
                        String message = ViewMessages.getMessage("IUI-000090", status);
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }
                }
            }

            String message = ViewMessages.getMessage("IUI-000086", new Object[] { instanceNos.size() });
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    disable(loadBalancer.getLoadBalancer().getLoadBalancerNo(), instanceNos);
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        private void disable(Long loadBalancerNo, List<Long> instanceNos) {
            // オペレーションログ
            OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Disable Server", loadBalancerNo,
                    String.valueOf(instanceNos.size()));

            // 振り分けの無効化
            LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
            loadBalancerService.disableInstances(loadBalancerNo, instanceNos);

            // 表示の更新
            refreshTable();
        }

    }

}