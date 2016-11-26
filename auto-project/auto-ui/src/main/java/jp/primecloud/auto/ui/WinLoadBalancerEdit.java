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
import java.util.List;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.AwsDescribeService;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.ui.validator.IntegerRangeValidator;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * ロードバランサの編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinLoadBalancerEdit extends Window {

    private final String TAB_HEIGHT = "480px";

    private Long loadBalancerNo;

    private Long loadBalancerInstanceNo;

    private BasicTab basicTab;

    private HealthCheckTab healthCheckTab;

    private LoadBalancerDto loadBalancer;

    private LoadBalancerPlatformDto platform;

    private List<ComponentDto> components;

    private List<Subnet> subnets;

    private List<String> securityGroups;

    public WinLoadBalancerEdit(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
    }

    @Override
    public void attach() {
        // 初期データの取得
        loadData();

        // モーダルウインドウ
        setIcon(Icons.EDITMINI.resource());
        setCaption(ViewProperties.getCaption("window.winLoadBalancerEdit"));
        setModal(true);
        setWidth("820px");
        //setHeight("500px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        // タブ設定
        TabSheet tab = new TabSheet();
        layout.addComponent(tab);

        // 基本情報タブ
        Boolean awsVpc = null;
        if (platform.getPlatformAws() != null) {
            awsVpc = platform.getPlatformAws().getVpc();
        }
        basicTab = new BasicTab(loadBalancer.getLoadBalancer().getType(), awsVpc);
        tab.addTab(basicTab, ViewProperties.getCaption("tab.basic"), Icons.BASIC.resource());

        // ヘルスチェックタブ
        healthCheckTab = new HealthCheckTab(loadBalancer.getLoadBalancer().getType());
        tab.addTab(healthCheckTab, ViewProperties.getCaption("tab.helthCheck"), Icons.DETAIL.resource());

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button();
        okButton.setCaption(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editLoadBalancer"));
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                okButtonClick(event);
            }
        });
        okButton.setClickShortcut(KeyCode.ENTER); // [Enter]でeditButtonクリック
        okButton.focus();
        bottomLayout.addComponent(okButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        bottomLayout.addComponent(cancelButton);

        // データの表示
        basicTab.show(loadBalancer, platform, components, securityGroups, subnets);
        healthCheckTab.show(loadBalancer);
    }

    private class BasicTab extends VerticalLayout {

        private final String INTERNAL_CAPTION_ID = "EnableInternalName";

        private final String SERVICE_CAPTION_ID = "ServiceName";

        private final String SUBNET_CAPTION_ID = "subnet";

        private String loadBalancerType;

        private Boolean awsVpc;

        private TextField loadBalancerNameField;

        private TextField commentField;

        private Label cloudLabel;

        private Label typeLabel;

        private ComboBox serviceSelect;

        private Button editServerButton;

        private ComboBox internalSelect;

        private ComboBox securityGroupSelect;

        private TwinColSelect subnetSelect;

        private List<Component> stoppedOnlyComponents = new ArrayList<Component>();

        public BasicTab(String loadBalancerType, Boolean awsVpc) {
            this.loadBalancerType = loadBalancerType;
            this.awsVpc = awsVpc;
        }

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // フォーム
            Form form = new Form();
            form.setSizeFull();
            addComponent(form);

            // ロードバランサ名
            loadBalancerNameField = new TextField(ViewProperties.getCaption("field.loadBalancerName"));
            form.getLayout().addComponent(loadBalancerNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("95%");
            form.getLayout().addComponent(commentField);

            // プラットフォーム
            cloudLabel = new Label();
            cloudLabel.setCaption(ViewProperties.getCaption("field.cloud"));
            cloudLabel.addStyleName("icon-label");
            form.getLayout().addComponent(cloudLabel);

            // ロードバランサ種別
            typeLabel = new Label();
            typeLabel.setCaption(ViewProperties.getCaption("field.loadBalancerType"));
            typeLabel.addStyleName("icon-label");
            form.getLayout().addComponent(typeLabel);

            // 割り当てサービス
            serviceSelect = new ComboBox();
            serviceSelect.setCaption(ViewProperties.getCaption("field.loadBalancerService"));
            serviceSelect.setNullSelectionAllowed(false);
            serviceSelect.addContainerProperty(SERVICE_CAPTION_ID, String.class, null);
            serviceSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serviceSelect.setItemCaptionPropertyId(SERVICE_CAPTION_ID);
            form.getLayout().addComponent(serviceSelect);

            // UltraMonkeyロードバランサの場合
            if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancerType)) {
                // サーバ編集ボタン
                editServerButton = new Button(ViewProperties.getCaption("button.UltraMonkeyEdit"));
                editServerButton.setDescription(ViewProperties.getCaption("description.UltraMonkeyEdit"));
                editServerButton.setIcon(Icons.EDITMINI.resource());
                editServerButton.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        WinServerEdit winServerEdit = new WinServerEdit(loadBalancerInstanceNo);
                        winServerEdit.addListener(new CloseListener() {
                            @Override
                            public void windowClose(CloseEvent e) {

                            }
                        });
                        getWindow().getApplication().getMainWindow().addWindow(winServerEdit);
                    }
                });
                stoppedOnlyComponents.add(editServerButton);

                HorizontalLayout layout = new HorizontalLayout();
                Label txt = new Label(ViewProperties.getCaption("field.UltraMonkeyEdit"));
                layout.addComponent(editServerButton);
                layout.addComponent(txt);
                layout.setComponentAlignment(txt, Alignment.MIDDLE_LEFT);
                form.getLayout().addComponent(layout);
            }
            // AWSロードバランサの場合
            else if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerType)) {
                if (BooleanUtils.isTrue(awsVpc)) {
                    // 内部ロードバランサ
                    internalSelect = new ComboBox();
                    internalSelect.setImmediate(true);
                    internalSelect.setCaption(ViewProperties.getCaption("field.internallb"));
                    internalSelect.setNullSelectionAllowed(false);
                    internalSelect.addContainerProperty(INTERNAL_CAPTION_ID, String.class, null);
                    internalSelect.setItemCaptionPropertyId(INTERNAL_CAPTION_ID);
                    internalSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
                    form.getLayout().addComponent(internalSelect);
                    stoppedOnlyComponents.add(internalSelect);

                    // セキュリティグループ
                    securityGroupSelect = new ComboBox();
                    securityGroupSelect.setImmediate(true);
                    securityGroupSelect.setCaption(ViewProperties.getCaption("field.securityGroup"));
                    securityGroupSelect.setNullSelectionAllowed(false);
                    form.getLayout().addComponent(securityGroupSelect);
                    stoppedOnlyComponents.add(securityGroupSelect);

                    // サブネットのラベル
                    AbsoluteLayout subnetLayout = new AbsoluteLayout();
                    subnetLayout.setWidth("100%");
                    subnetLayout.setHeight("20px");
                    Label selectLbl = new Label(ViewProperties.getCaption("field.selectSubnet"));
                    Label selectedLbl = new Label(ViewProperties.getCaption("field.selectedSubnet"));
                    subnetLayout.addComponent(selectLbl, "left:7%");
                    subnetLayout.addComponent(selectedLbl, "left:60%");
                    form.getLayout().addComponent(subnetLayout);
                    stoppedOnlyComponents.add(subnetLayout);

                    // サブネット
                    subnetSelect = new TwinColSelect(ViewProperties.getCaption("field.subnetZone"));
                    subnetSelect.setRows(7);
                    subnetSelect.setNullSelectionAllowed(true);
                    subnetSelect.setMultiSelect(true);
                    subnetSelect.setImmediate(true);
                    subnetSelect.setWidth("100%");
                    subnetSelect.addContainerProperty(SUBNET_CAPTION_ID, String.class, null);
                    subnetSelect.setItemCaptionPropertyId(SUBNET_CAPTION_ID);
                    subnetSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
                    subnetSelect.getWidthUnits();
                    form.getLayout().addComponent(subnetSelect);
                    stoppedOnlyComponents.add(subnetSelect);

                    // サブネットのラベル2
                    AbsoluteLayout subnetLayout2 = new AbsoluteLayout();
                    subnetLayout2.setWidth("100%");
                    subnetLayout2.setHeight("20px");
                    Label descriptionLbl = new Label(ViewProperties.getCaption("field.selectSubnetDescription"));
                    subnetLayout2.addComponent(descriptionLbl);
                    form.getLayout().addComponent(subnetLayout2);
                    stoppedOnlyComponents.add(subnetLayout2);

                    // サブネットのラベル3
                    AbsoluteLayout subnetLayout3 = new AbsoluteLayout();
                    subnetLayout3.setWidth("100%");
                    subnetLayout3.setHeight("20px");
                    Label descriptionLbl2 = new Label(ViewProperties.getCaption("field.crosszone"));
                    subnetLayout3.addComponent(descriptionLbl2);
                    form.getLayout().addComponent(subnetLayout3);
                    stoppedOnlyComponents.add(subnetLayout3);
                }
            }

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));

            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerType)) {
                if (BooleanUtils.isTrue(awsVpc)) {
                    message = ViewMessages.getMessage("IUI-000029");
                    securityGroupSelect.setRequired(true);
                    securityGroupSelect.setRequiredError(message);

                    message = ViewMessages.getMessage("IUI-000108");
                    subnetSelect.setRequired(true);
                    subnetSelect.setRequiredError(message);
                }
            }
        }

        public void show(LoadBalancerDto loadBalancer, LoadBalancerPlatformDto platform, List<ComponentDto> components,
                List<String> securityGroups, List<Subnet> subnets) {
            // ロードバランサー名
            loadBalancerNameField.setReadOnly(false);
            loadBalancerNameField.setValue(loadBalancer.getLoadBalancer().getLoadBalancerName());
            loadBalancerNameField.setReadOnly(true);

            // コメント
            String comment = loadBalancer.getLoadBalancer().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            // プラットフォーム名
            Icons platformIcon = IconUtils.getPlatformIcon(platform);
            String description = platform.getPlatform().getPlatformNameDisp();
            String cloudValue = IconUtils.createImageTag(getApplication(), platformIcon, description);
            cloudLabel.setValue(cloudValue);
            cloudLabel.setContentMode(Label.CONTENT_XHTML);

            // ロードバランサ種別
            Icons typeIcon = Icons.NONE;
            String typeString = ViewProperties.getLoadBalancerType(loadBalancer.getLoadBalancer().getType());
            String typeValue = IconUtils.createImageTag(getApplication(), typeIcon, typeString);
            typeLabel.setValue(typeValue);
            typeLabel.setContentMode(Label.CONTENT_XHTML);

            // 割り当てサービス
            for (ComponentDto component : components) {
                Item item = serviceSelect.addItem(component.getComponent().getComponentNo());
                item.getItemProperty(SERVICE_CAPTION_ID).setValue(component.getComponent().getComponentName());
            }
            serviceSelect.select(loadBalancer.getLoadBalancer().getComponentNo());

            // リスナーが存在する場合 は選択不可にする
            if (loadBalancer.getLoadBalancerListeners().size() > 0) {
                serviceSelect.setEnabled(false);
            }

            // AWSロードバランサの場合
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
                // VPCの場合
                if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                    // 内部ロードバランサ
                    Item disableItem = internalSelect.addItem(false);
                    disableItem.getItemProperty(INTERNAL_CAPTION_ID).setValue("無効");
                    Item enableItem = internalSelect.addItem(true);
                    enableItem.getItemProperty(INTERNAL_CAPTION_ID).setValue("有効");
                    internalSelect.select(BooleanUtils.isTrue(loadBalancer.getAwsLoadBalancer().getInternal()));

                    // セキュリティグループ
                    for (String securityGroup : securityGroups) {
                        securityGroupSelect.addItem(securityGroup);
                    }

                    if (loadBalancer.getAwsLoadBalancer().getSecurityGroups() != null) {
                        securityGroupSelect.setValue(loadBalancer.getAwsLoadBalancer().getSecurityGroups());
                    }

                    // サブネット
                    for (Subnet subnet : subnets) {
                        Item item = subnetSelect.addItem(subnet.getSubnetId());
                        String subnetDisp = subnet.getCidrBlock() + " [" + subnet.getAvailabilityZone() + "]";
                        item.getItemProperty(SUBNET_CAPTION_ID).setValue(subnetDisp);
                    }

                    if (loadBalancer.getAwsLoadBalancer().getSubnetId() != null) {
                        for (String subnetId : loadBalancer.getAwsLoadBalancer().getSubnetId().split(",")) {
                            subnetId = subnetId.trim();

                            for (Subnet subnet : subnets) {
                                if (StringUtils.equals(subnet.getSubnetId(), subnetId)) {
                                    subnetSelect.select(subnetId);
                                }
                            }
                        }
                    }
                }
            }

            // ロードバランサが停止していない場合、変更できない項目を無効化する
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getLoadBalancer().getStatus());
            if (LoadBalancerStatus.STOPPED != status) {
                for (Component stoppedOnlyComponent : stoppedOnlyComponents) {
                    stoppedOnlyComponent.setEnabled(false);
                }
            }
        }

    }

    private class HealthCheckTab extends VerticalLayout {

        private final String TEXT_WIDTH = "120px";

        private String loadBalancerType;

        private ComboBox checkProtocolSelect;

        private TextField checkPortField;

        private TextField checkPathField;

        private TextField checkTimeoutField;

        private TextField checkIntervalField;

        private TextField unhealthyThresholdField;

        private TextField healthyThresholdField;

        public HealthCheckTab(String loadBalancerType) {
            this.loadBalancerType = loadBalancerType;
        }

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, true, true);
            setSpacing(false);

            // メインフォーム
            Form mainForm = new Form();
            addComponent(mainForm);

            // 監視プロトコル
            checkProtocolSelect = new ComboBox(ViewProperties.getCaption("field.checkProtocol"));
            checkProtocolSelect.setWidth(TEXT_WIDTH);
            checkProtocolSelect.setImmediate(true);
            checkProtocolSelect.setNullSelectionAllowed(false);
            checkProtocolSelect.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    checkProtocolValueChange(event);
                }
            });
            mainForm.getLayout().addComponent(checkProtocolSelect);

            // 監視ポート
            checkPortField = new TextField(ViewProperties.getCaption("field.checkPort"));
            checkPortField.setWidth(TEXT_WIDTH);
            mainForm.getLayout().addComponent(checkPortField);

            // 監視Path
            checkPathField = new TextField(ViewProperties.getCaption("field.checkPath"));
            checkPathField.setImmediate(true);
            mainForm.getLayout().addComponent(checkPathField);

            // ヘルスチェック詳細設定パネル
            Panel panel = new Panel(ViewProperties.getCaption("field.healthCheckDetail"));
            ((Layout) panel.getContent()).setMargin(false, false, false, true);
            ((Layout) panel.getContent()).setHeight("200px");
            ((Layout) panel.getContent()).setWidth("315px");
            mainForm.getLayout().addComponent(panel);

            // サブフォーム
            Form subForm = new Form();
            subForm.setStyleName("panel-healthcheck-setting");
            subForm.getLayout().setMargin(false, false, false, false);
            panel.addComponent(subForm);

            // タイムアウト時間
            checkTimeoutField = new TextField(ViewProperties.getCaption("field.checkTimeout"));
            checkTimeoutField.setWidth(TEXT_WIDTH);
            subForm.getLayout().addComponent(checkTimeoutField);

            // ヘルスチェック間隔
            checkIntervalField = new TextField(ViewProperties.getCaption("field.checkInterval"));
            checkIntervalField.setWidth(TEXT_WIDTH);
            subForm.getLayout().addComponent(checkIntervalField);

            // 障害閾値
            unhealthyThresholdField = new TextField(ViewProperties.getCaption("field.checkDownThreshold"));
            unhealthyThresholdField.setWidth(TEXT_WIDTH);
            subForm.getLayout().addComponent(unhealthyThresholdField);

            // 復帰閾値
            healthyThresholdField = new TextField(ViewProperties.getCaption("field.checkRecoverThreshold"));
            healthyThresholdField.setWidth(TEXT_WIDTH);
            subForm.getLayout().addComponent(healthyThresholdField);

            // UltraMonkeyロードバランサの場合、復帰閾値は設定できない
            if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancerType)) {
                healthyThresholdField.setEnabled(false);
            }

            initValidation();
        }

        private void initValidation() {
            String message;

            // 監視プロトコル
            message = ViewMessages.getMessage("IUI-000069");
            checkProtocolSelect.setRequired(true);
            checkProtocolSelect.setRequiredError(message);

            // 監視ポート
            message = ViewMessages.getMessage("IUI-000070", 1, 65535);
            checkPortField.setRequired(true);
            checkPortField.setRequiredError(message);
            checkPortField.addValidator(new IntegerRangeValidator(1, 65535, message));

            // 監視Path
            message = ViewMessages.getMessage("IUI-000071", 100);
            checkPathField.setRequired(true);
            checkPathField.setRequiredError(message);
            checkPathField.addValidator(new StringLengthValidator(message, 1, 100, false));

            // タイムアウト時間
            message = ViewMessages.getMessage("IUI-000072", 2, 60);
            checkTimeoutField.setRequired(true);
            checkTimeoutField.setRequiredError(message);
            checkTimeoutField.addValidator(new IntegerRangeValidator(2, 60, message));

            // ヘルスチェック間隔
            message = ViewMessages.getMessage("IUI-000073", 5, 600);
            checkIntervalField.setRequired(true);
            checkIntervalField.setRequiredError(message);
            checkIntervalField.addValidator(new IntegerRangeValidator(5, 600, message));

            // 障害閾値
            message = ViewMessages.getMessage("IUI-000074", 2, 10);
            unhealthyThresholdField.setRequired(true);
            unhealthyThresholdField.setRequiredError(message);
            unhealthyThresholdField.addValidator(new IntegerRangeValidator(2, 10, message));

            // 復帰閾値
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerType)) {
                message = ViewMessages.getMessage("IUI-000075", 2, 10);
                healthyThresholdField.setRequired(true);
                healthyThresholdField.setRequiredError(message);
                healthyThresholdField.addValidator(new IntegerRangeValidator(2, 10, message));
            }
        }

        public void show(LoadBalancerDto loadBalancer) {
            // 監視プロトコル
            checkProtocolSelect.addItem("TCP");
            checkProtocolSelect.addItem("HTTP");

            LoadBalancerHealthCheck healthCheck = loadBalancer.getLoadBalancerHealthCheck();
            if (healthCheck != null) {
                // 監視プロトコル
                if (healthCheck.getCheckProtocol() != null) {
                    checkProtocolSelect.select(healthCheck.getCheckProtocol());
                }

                // 監視Port
                if (healthCheck.getCheckPort() != null) {
                    checkPortField.setValue(healthCheck.getCheckPort().toString());
                }

                // 監視Path
                if (healthCheck.getCheckPath() != null) {
                    checkPathField.setValue(healthCheck.getCheckPath());
                }

                // タイムアウト時間
                if (healthCheck.getCheckTimeout() != null) {
                    checkTimeoutField.setValue(healthCheck.getCheckTimeout().toString());
                }

                // ヘルスチェック間隔
                if (healthCheck.getCheckInterval() != null) {
                    checkIntervalField.setValue(healthCheck.getCheckInterval().toString());
                }

                // 障害閾値
                if (healthCheck.getUnhealthyThreshold() != null) {
                    unhealthyThresholdField.setValue(healthCheck.getUnhealthyThreshold().toString());
                }

                // 復帰閾値
                if (healthCheck.getHealthyThreshold() != null) {
                    healthyThresholdField.setValue(healthCheck.getHealthyThreshold().toString());
                }

            } else {
                // デフォルト表示
                checkProtocolSelect.select("HTTP");
                checkPortField.setValue("80");
                checkPathField.setValue("/index.html");
                checkTimeoutField.setValue("5");
                checkIntervalField.setValue("30");
                unhealthyThresholdField.setValue("2");
                healthyThresholdField.setValue("10");
            }
        }

        private void checkProtocolValueChange(Property.ValueChangeEvent event) {
            if ("HTTP".equals(checkProtocolSelect.getValue())) {
                checkPathField.setEnabled(true);
            } else {
                checkPathField.setValue("");
                checkPathField.setEnabled(false);
            }
        }

    }

    private void loadData() {
        Long userNo = ViewContext.getUserNo();
        Long farmNo = ViewContext.getFarmNo();

        // ロードバランサ情報を取得
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        List<LoadBalancerDto> loadBalancers = loadBalancerService.getLoadBalancers(farmNo);
        for (LoadBalancerDto loadBalancer : loadBalancers) {
            if (loadBalancerNo.equals(loadBalancer.getLoadBalancer().getLoadBalancerNo())) {
                this.loadBalancer = loadBalancer;
                break;
            }
        }

        // ロードバランサのプラットフォーム情報を取得
        Long platformNo = loadBalancer.getLoadBalancer().getPlatformNo();
        List<LoadBalancerPlatformDto> platforms = loadBalancerService.getPlatforms(userNo);
        for (LoadBalancerPlatformDto platform : platforms) {
            if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                this.platform = platform;
                break;
            }
        }

        // コンポーネント情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        components = componentService.getComponents(farmNo);

        // UltraMonkeyロードバランサの場合
        if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancer.getLoadBalancer().getType())) {
            // インスタンスを特定する
            this.loadBalancerInstanceNo = loadBalancerService.getLoadBalancerInstance(loadBalancerNo);
        }
        // AWSロードバランサの場合
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
            // VPCの場合
            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                AwsDescribeService awsDescribeService = BeanContext.getBean(AwsDescribeService.class);

                // サブネットを取得
                this.subnets = new ArrayList<Subnet>();
                List<Subnet> subnets = awsDescribeService.getSubnets(userNo, platformNo);
                for (Subnet subnet : subnets) {
                    this.subnets.add(subnet);
                }

                // セキュリティグループを取得
                this.securityGroups = new ArrayList<String>();
                List<SecurityGroup> groups = awsDescribeService.getSecurityGroups(userNo, platformNo);
                for (SecurityGroup group : groups) {
                    this.securityGroups.add(group.getGroupName());
                }
            }
        }
    }

    private Subnet findSubnet(String subnetId) {
        for (Subnet subnet : subnets) {
            if (StringUtils.equals(subnet.getSubnetId(), subnetId)) {
                return subnet;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void okButtonClick(ClickEvent event) {
        // 入力チェック
        try {
            // 基本設定
            basicTab.commentField.validate();
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
                if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                    basicTab.subnetSelect.validate();
                    basicTab.securityGroupSelect.validate();
                }
            }

            // ヘルスチェック設定
            healthCheckTab.checkProtocolSelect.validate();
            healthCheckTab.checkPortField.validate();
            String checkProtocol = (String) healthCheckTab.checkProtocolSelect.getValue();
            if ("HTTP".equals(checkProtocol)) {
                healthCheckTab.checkPathField.validate();
            }
            healthCheckTab.checkTimeoutField.validate();
            healthCheckTab.checkIntervalField.validate();
            healthCheckTab.unhealthyThresholdField.validate();
            healthCheckTab.healthyThresholdField.validate();

        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サブネットのチェック
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                // 同じゾーンのサブネットを複数選択できない
                Collection<String> selectedSubnetIds = (Collection<String>) basicTab.subnetSelect.getValue();
                List<String> zones = new ArrayList<String>();
                for (String subnetId : selectedSubnetIds) {
                    Subnet subnet = findSubnet(subnetId);
                    if (zones.contains(subnet.getAvailabilityZone())) {
                        // 同じゾーンのサブネットを複数選択している場合
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                                ViewMessages.getMessage("IUI-000110"));
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }
                    zones.add(subnet.getAvailabilityZone());
                }
            }
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("LOAD_BALANCER", "Edit Load_Balancer", null, null, loadBalancerNo, null);

        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);

        // ロードバランサを変更
        try {
            String comment = (String) basicTab.commentField.getValue();
            Long componentNo = (Long) basicTab.serviceSelect.getValue();

            // AWSロードバランサを変更
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
                String subnetId = null;
                String zone = null;
                String securityGroup = null;
                boolean internal = false;

                if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                    securityGroup = (String) basicTab.securityGroupSelect.getValue();
                    internal = (Boolean) basicTab.internalSelect.getValue();

                    Collection<String> selectedSubnetIds = (Collection<String>) basicTab.subnetSelect.getValue();
                    for (String selectedSubnetId : selectedSubnetIds) {
                        subnetId = (subnetId == null) ? selectedSubnetId : (subnetId + "," + selectedSubnetId);

                        Subnet subnet = findSubnet(selectedSubnetId);
                        zone = (zone == null) ? subnet.getAvailabilityZone() : (zone + "," + subnet
                                .getAvailabilityZone());
                    }
                }

                String loadBalancerName = loadBalancer.getLoadBalancer().getLoadBalancerName();
                loadBalancerService.updateAwsLoadBalancer(loadBalancerNo, loadBalancerName, comment, componentNo,
                        subnetId, securityGroup, zone, internal);
            }
            // UltraMonkeyロードバランサを変更
            else if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancer.getLoadBalancer().getType())) {
                String loadBalancerName = loadBalancer.getLoadBalancer().getLoadBalancerName();
                loadBalancerService.updateUltraMonkeyLoadBalancer(loadBalancerNo, loadBalancerName, comment,
                        componentNo);
            }
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // ヘルスチェック設定の変更
        String checkProtocol = (String) healthCheckTab.checkProtocolSelect.getValue();
        Integer checkPort = Integer.valueOf((String) healthCheckTab.checkPortField.getValue());
        String checkPath = (String) healthCheckTab.checkPathField.getValue();
        Integer checkTimeout = Integer.valueOf((String) healthCheckTab.checkTimeoutField.getValue());
        Integer checkInterval = Integer.valueOf((String) healthCheckTab.checkIntervalField.getValue());
        Integer healthyThreshold = Integer.valueOf((String) healthCheckTab.unhealthyThresholdField.getValue());
        Integer unhealthyThreshold = Integer.valueOf((String) healthCheckTab.healthyThresholdField.getValue());

        try {
            loadBalancerService.configureHealthCheck(loadBalancerNo, checkProtocol, checkPort, checkPath, checkTimeout,
                    checkInterval, healthyThreshold, unhealthyThreshold);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 画面を閉じる
        close();
    }

}
