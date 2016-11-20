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
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
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

import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
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
@SuppressWarnings({ "serial", "unchecked" })
public class WinLoadBalancerEdit extends Window {

    final String COLUMN_HEIGHT = "30px";

    final String TAB_HEIGHT = "480px";

    Application apl;

    Long loadBalancerNo;

    Long instanceNo;

    BasicTab basicTab;

    HealthCheckTab healthCheckTab;

    LoadBalancerDto loadBalancerDto;

    LoadBalancerPlatformDto platformDto;

    List<ComponentDto> componentDtos;

    List<Subnet> subnets;

    List<String> securityGroups = new ArrayList<String>();

    WinLoadBalancerEdit(Application ap, Long loadBalancerNo) {
        apl = ap;
        this.loadBalancerNo = loadBalancerNo;

        // 初期データの取得
        initData();

        //モーダルウインドウ
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
        basicTab = new BasicTab();
        tab.addTab(basicTab, ViewProperties.getCaption("tab.basic"), Icons.BASIC.resource());

        // ヘルスチェックタブ
        healthCheckTab = new HealthCheckTab();
        tab.addTab(healthCheckTab, ViewProperties.getCaption("tab.helthCheck"), Icons.DETAIL.resource());

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // okButtonボタン
        Button okButton = new Button();
        okButton.setCaption(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editLoadBalancer"));

        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                okButtonClick(event);
            }
        });
        okbar.addComponent(okButton);
        // [Enter]でeditButtonクリック
        okButton.setClickShortcut(KeyCode.ENTER);
        okButton.focus();

        // Cancel
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinLoadBalancerEdit.this.close();
            }
        });
        okbar.addComponent(cancelButton);

        // 入力チェックの設定
        initValidation();

        // データの表示
        showData();
    }

    private class BasicTab extends VerticalLayout {

        final String INTERNAL_CAPTION_ID = "EnableInternalName";

        final String SERVICE_CAPTION_ID = "ServiceName";

        final String SUBNET_CAPTION_ID = "subnet";

        Form form;

        TextField loadBalancerNameField;

        TextField commentField;

        Label cloudLabel;

        Label typeLabel;

        ComboBox serviceSelect;

        TwinColSelect subnetSelect;

        ComboBox grpSelect;

        ComboBox internalSelect;

        BasicTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // フォーム
            form = new Form();
            form.setSizeFull();
            addComponent(form);

            // LB名
            loadBalancerNameField = new TextField(ViewProperties.getCaption("field.loadBalancerName"));

            // コメント欄
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("95%");

            // プラットフォーム
            cloudLabel = new Label();
            cloudLabel.setCaption(ViewProperties.getCaption("field.cloud"));
            cloudLabel.addStyleName("icon-label");

            // ロードバランサ種別
            typeLabel = new Label();
            typeLabel.setCaption(ViewProperties.getCaption("field.loadBalancerType"));
            typeLabel.addStyleName("icon-label");

            // UltraMonkeyサーバ編集ボタン
            Button editServerButton = new Button(ViewProperties.getCaption("button.UltraMonkeyEdit"));
            editServerButton.setDescription(ViewProperties.getCaption("description.UltraMonkeyEdit"));
            editServerButton.setIcon(Icons.EDITMINI.resource());

            editServerButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    WinServerEdit winServerEdit = new WinServerEdit(getApplication(), instanceNo);
                    winServerEdit.addListener(new CloseListener() {
                        @Override
                        public void windowClose(CloseEvent e) {

                        }
                    });
                    getWindow().getApplication().getMainWindow().addWindow(winServerEdit);
                }
            });
            HorizontalLayout editlay = new HorizontalLayout();
            Label txt = new Label(ViewProperties.getCaption("field.UltraMonkeyEdit"));
            editlay.addComponent(editServerButton);
            editlay.addComponent(txt);
            editlay.setComponentAlignment(txt, Alignment.MIDDLE_LEFT);

            // 割り当てサービス
            serviceSelect = new ComboBox();
            serviceSelect.setCaption(ViewProperties.getCaption("field.loadBalancerService"));
            serviceSelect.setNullSelectionAllowed(false);
            serviceSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serviceSelect.setItemCaptionPropertyId(SERVICE_CAPTION_ID);

            //サブネット
            //サブネット選択(上部のラベル)
            AbsoluteLayout aboveLayout = new AbsoluteLayout();
            aboveLayout.setWidth("100%");
            aboveLayout.setHeight("20px");
            Label selectLbl = new Label(ViewProperties.getCaption("field.selectSubnet"));
            Label selectedLbl = new Label(ViewProperties.getCaption("field.selectedSubnet"));
            aboveLayout.addComponent(selectLbl, "left:7%");
            aboveLayout.addComponent(selectedLbl, "left:60%");

            //サブネット選択(選択コンポーネント本体)
            subnetSelect = new TwinColSelect(ViewProperties.getCaption("field.subnetZone"));
            subnetSelect.setRows(7);
            subnetSelect.setNullSelectionAllowed(true);
            subnetSelect.setMultiSelect(true);
            subnetSelect.setImmediate(true);
            subnetSelect.setWidth("100%");
            subnetSelect.setItemCaptionPropertyId(SUBNET_CAPTION_ID);
            subnetSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            subnetSelect.getWidthUnits();

            //サブネット選択(下部のラベル)
            AbsoluteLayout belowLayout = new AbsoluteLayout();
            belowLayout.setWidth("100%");
            belowLayout.setHeight("20px");
            Label descriptionLbl = new Label(ViewProperties.getCaption("field.selectSubnetDescription"));
            belowLayout.addComponent(descriptionLbl);

            //クロスゾーン負荷分散キャプション
            AbsoluteLayout belowLayout2 = new AbsoluteLayout();
            belowLayout2.setWidth("100%");
            belowLayout2.setHeight("20px");
            Label descriptionLbl2 = new Label(ViewProperties.getCaption("field.crosszone"));
            belowLayout2.addComponent(descriptionLbl2);

            //セキュリティグループ
            grpSelect = new ComboBox();
            grpSelect.setImmediate(true);
            grpSelect.setCaption(ViewProperties.getCaption("field.securityGroup"));
            grpSelect.setNullSelectionAllowed(false);

            //内部ロードバランサ
            internalSelect = new ComboBox();
            internalSelect.setImmediate(true);
            internalSelect.setCaption(ViewProperties.getCaption("field.internallb"));
            internalSelect.setNullSelectionAllowed(false);

            //表示or非表示
            form.getLayout().addComponent(loadBalancerNameField);
            form.getLayout().addComponent(commentField);
            //ultramonkeyの場合の未表示
            if (instanceNo != null) {
                form.getLayout().addComponent(editlay);
            }
            form.getLayout().addComponent(cloudLabel);
            form.getLayout().addComponent(typeLabel);
            form.getLayout().addComponent(serviceSelect);

            PlatformAws platformAws = platformDto.getPlatformAws();
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerDto.getLoadBalancer().getType())
                    && platformAws.getVpc()) {
                form.getLayout().addComponent(internalSelect);
                form.getLayout().addComponent(grpSelect);
                form.getLayout().addComponent(aboveLayout);
                form.getLayout().addComponent(subnetSelect);
                form.getLayout().addComponent(belowLayout);
                form.getLayout().addComponent(belowLayout2);
            }

            //活性or非活性
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancerDto.getLoadBalancer().getStatus());
            if (LoadBalancerStatus.STOPPED != status) {
                //ロードバランサのステータスがSTOPPED以外の場合
                internalSelect.setEnabled(false);
                aboveLayout.setEnabled(false);
                subnetSelect.setEnabled(false);
                belowLayout.setEnabled(false);
                belowLayout2.setEnabled(false);
                grpSelect.setEnabled(false);
                editServerButton.setEnabled(false);
            }
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));

            PlatformAws platformAws = platformDto.getPlatformAws();
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerDto.getLoadBalancer().getType())
                    && platformAws.getVpc()) {
                message = ViewMessages.getMessage("IUI-000108");
                subnetSelect.setRequired(true);
                subnetSelect.setRequiredError(message);

                message = ViewMessages.getMessage("IUI-000029");
                grpSelect.setRequired(true);
                grpSelect.setRequiredError(message);
            }
        }

        private void showData() {
            // ロードバランサー名
            loadBalancerNameField.setReadOnly(false);
            loadBalancerNameField.setValue(loadBalancerDto.getLoadBalancer().getLoadBalancerName());
            loadBalancerNameField.setReadOnly(true);

            // コメントの設定
            String comment = loadBalancerDto.getLoadBalancer().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            // プラットフォーム
            Platform platform = platformDto.getPlatform();
            PlatformAws platformAws = platformDto.getPlatformAws();

            //プラットフォームアイコン名の取得
            Icons icon = IconUtils.getPlatformIcon(platformDto);
            String description = platform.getPlatformNameDisp();
            String cloudValue = IconUtils.createImageTag(apl, icon, description);
            cloudLabel.setValue(cloudValue);
            cloudLabel.setContentMode(Label.CONTENT_XHTML);

            // ロードバランサ種別
            String type = loadBalancerDto.getLoadBalancer().getType();
            Icons typeIcon = Icons.NONE;
            String typeString = ViewProperties.getLoadBalancerType(type);
            String typeValue = IconUtils.createImageTag(apl, typeIcon, typeString);
            typeLabel.setValue(typeValue);
            typeLabel.setContentMode(Label.CONTENT_XHTML);

            // 割り当てサービス選択
            IndexedContainer serviceContainer = new IndexedContainer();
            serviceContainer.addContainerProperty(SERVICE_CAPTION_ID, String.class, null);
            for (ComponentDto componentDto : componentDtos) {
                Item item = serviceContainer.addItem(componentDto);
                item.getItemProperty(SERVICE_CAPTION_ID).setValue(componentDto.getComponent().getComponentName());
            }
            serviceSelect.setContainerDataSource(serviceContainer);

            // 既に割り当てられているサービスを選択する
            Long componentNo = loadBalancerDto.getLoadBalancer().getComponentNo();
            for (ComponentDto componentDto : componentDtos) {
                if (componentNo.equals(componentDto.getComponent().getComponentNo())) {
                    serviceSelect.select(componentDto);
                    break;
                }
            }

            // リスナーが存在する場合 は選択不可にする
            if (loadBalancerDto.getLoadBalancerListeners().size() > 0) {
                serviceSelect.setEnabled(false);
            }

            // 有効無効コンボ
            if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
                internalSelect.setContainerDataSource(getEnabledList());
                internalSelect.select("無効");
                if (loadBalancerDto.getAwsLoadBalancer().getInternal()) {
                    internalSelect.select("有効");
                }
            }

            // サブネット
            if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
                //ELB + VPCの場合
                subnetSelect.setContainerDataSource(createSubnetContainer());
                if (StringUtils.isNotEmpty(loadBalancerDto.getAwsLoadBalancer().getSubnetId())) {
                    List<String> lbSubnets = new ArrayList<String>();
                    for (String lbSubnet : loadBalancerDto.getAwsLoadBalancer().getSubnetId().split(",")) {
                        lbSubnets.add(lbSubnet.trim());
                    }
                    for (Subnet subnet : subnets) {
                        if (lbSubnets.contains(subnet.getSubnetId())) {
                            subnetSelect.select(subnet);
                        }
                    }
                } else {
                    subnetSelect.select("");
                }
            }

            //セキュリティグループ
            if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
                //ELB + VPCの場合
                grpSelect.setContainerDataSource(new IndexedContainer(securityGroups));
                grpSelect.select(loadBalancerDto.getAwsLoadBalancer().getSecurityGroups());
            }
        }

        private IndexedContainer getEnabledList() {
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty(INTERNAL_CAPTION_ID, String.class, null);

            Item item = container.addItem("有効");
            item.getItemProperty(INTERNAL_CAPTION_ID).setValue("有効");

            item = container.addItem("無効");
            item.getItemProperty(INTERNAL_CAPTION_ID).setValue("無効");

            return container;
        }

        private IndexedContainer createSubnetContainer() {
            IndexedContainer subnetContainer = new IndexedContainer();
            subnetContainer.addContainerProperty(SUBNET_CAPTION_ID, String.class, null);
            subnetContainer.addContainerProperty("cidrBlock", String.class, null);
            subnetContainer.addContainerProperty("subnetId", String.class, null);
            subnetContainer.addContainerProperty("zoneid", String.class, null);

            for (Subnet subnet : subnets) {
                Item item = subnetContainer.addItem(subnet);
                String subnetDisp = subnet.getCidrBlock() + "[" + subnet.getAvailabilityZone() + "]";
                item.getItemProperty(SUBNET_CAPTION_ID).setValue(subnetDisp);
                item.getItemProperty("cidrBlock").setValue(subnet.getCidrBlock());
                item.getItemProperty("subnetId").setValue(subnet.getSubnetId());
                item.getItemProperty("zoneid").setValue(subnet.getAvailabilityZone());
            }

            return subnetContainer;
        }

    }

    private class HealthCheckTab extends VerticalLayout {

        final String TEXT_WIDTH = "120px";

        final String CHECKPROTOCOL_CAPTION_ID = "ProtocolName";

        Form mainForm;

        Form subForm;

        ComboBox checkProtocolSelect;

        TextField checkPortField;

        TextField checkPathField;

        TextField checkTimeoutField;

        TextField checkIntervalField;

        TextField unhealthyThresholdField;

        TextField healthyThresholdField;

        HealthCheckTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, true, true);
            setSpacing(false);

            // メインフォーム
            mainForm = new Form();
            Layout mainLayout = mainForm.getLayout();
            addComponent(mainForm);

            // 監視プロトコル
            checkProtocolSelect = new ComboBox(ViewProperties.getCaption("field.checkProtocol"));
            checkProtocolSelect.setWidth(TEXT_WIDTH);
            checkProtocolSelect.setImmediate(true);
            checkProtocolSelect.setNullSelectionAllowed(false);
            mainLayout.addComponent(checkProtocolSelect);
            checkProtocolSelect.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    changeCheckProtocol(event);
                }
            });

            // 監視ポート
            checkPortField = new TextField(ViewProperties.getCaption("field.checkPort"));
            checkPortField.setWidth(TEXT_WIDTH);
            mainLayout.addComponent(checkPortField);

            // 監視Path
            checkPathField = new TextField(ViewProperties.getCaption("field.checkPath"));
            checkPathField.setImmediate(true);
            mainLayout.addComponent(checkPathField);

            // ヘルスチェック詳細設定パネル
            Panel panel = new Panel(ViewProperties.getCaption("field.healthCheckDetail"));
            ((Layout) panel.getContent()).setMargin(false, false, false, true);
            ((Layout) panel.getContent()).setHeight("200px");
            ((Layout) panel.getContent()).setWidth("315px");
            mainLayout.addComponent(panel);

            // サブフォーム
            subForm = new Form();
            subForm.setStyleName("panel-healthcheck-setting");
            FormLayout sublayout = (FormLayout) this.subForm.getLayout();
            sublayout.setMargin(false, false, false, false);
            panel.addComponent(subForm);

            // タイムアウト時間
            checkTimeoutField = new TextField(ViewProperties.getCaption("field.checkTimeout"));
            checkTimeoutField.setWidth(TEXT_WIDTH);
            sublayout.addComponent(checkTimeoutField);

            // ヘルスチェック間隔
            checkIntervalField = new TextField(ViewProperties.getCaption("field.checkInterval"));
            checkIntervalField.setWidth(TEXT_WIDTH);
            sublayout.addComponent(checkIntervalField);

            // 障害閾値
            unhealthyThresholdField = new TextField(ViewProperties.getCaption("field.checkDownThreshold"));
            unhealthyThresholdField.setWidth(TEXT_WIDTH);
            sublayout.addComponent(unhealthyThresholdField);

            // 復帰閾値
            healthyThresholdField = new TextField(ViewProperties.getCaption("field.checkRecoverThreshold"));
            healthyThresholdField.setWidth(TEXT_WIDTH);
            sublayout.addComponent(healthyThresholdField);
        }

        private void initValidation() {
            // 入力チェック
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
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerDto.getLoadBalancer().getType())) {
                message = ViewMessages.getMessage("IUI-000075", 2, 10);
                healthyThresholdField.setRequired(true);
                healthyThresholdField.setRequiredError(message);
                healthyThresholdField.addValidator(new IntegerRangeValidator(2, 10, message));
            }
        }

        private void showData() {
            // 監視プロトコル
            IndexedContainer protocols = getCheckProtocolList();
            checkProtocolSelect.setContainerDataSource(protocols);
            checkProtocolSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            checkProtocolSelect.setItemCaptionPropertyId(CHECKPROTOCOL_CAPTION_ID);

            LoadBalancerHealthCheck healthCheck = loadBalancerDto.getLoadBalancerHealthCheck();
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
                if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancerDto.getLoadBalancer().getType())) {
                    healthyThresholdField.setEnabled(false);
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
                if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancerDto.getLoadBalancer().getType())) {
                    healthyThresholdField.setEnabled(false);
                }
            }
        }

        private IndexedContainer getCheckProtocolList() {
            // TODO: ロードバランサの種別によって対応プロトコルを設定
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty(CHECKPROTOCOL_CAPTION_ID, String.class, null);

            Item item = container.addItem("TCP");
            item.getItemProperty(CHECKPROTOCOL_CAPTION_ID).setValue("TCP");

            item = container.addItem("HTTP");
            item.getItemProperty(CHECKPROTOCOL_CAPTION_ID).setValue("HTTP");

            return container;
        }

        private void changeCheckProtocol(Property.ValueChangeEvent event) {
            if ("HTTP".equals(checkProtocolSelect.getValue())) {
                checkPathField.setEnabled(true);
            } else {
                checkPathField.setValue("");
                checkPathField.setEnabled(false);
            }
        }

    }

    private void initData() {
        Long userNo = ViewContext.getUserNo();
        Long farmNo = ViewContext.getFarmNo();

        // ロードバランサ情報を取得
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        List<LoadBalancerDto> loadBalancerDtos = loadBalancerService.getLoadBalancers(farmNo);
        for (LoadBalancerDto loadBalancerDto : loadBalancerDtos) {
            if (loadBalancerNo.equals(loadBalancerDto.getLoadBalancer().getLoadBalancerNo())) {
                this.loadBalancerDto = loadBalancerDto;
                break;
            }
        }

        //ロードバランサ種別を設定
        String type = loadBalancerDto.getLoadBalancer().getType();

        // プラットフォーム情報を取得
        Long platformNo = loadBalancerDto.getLoadBalancer().getPlatformNo();
        List<LoadBalancerPlatformDto> platformDtos = loadBalancerService.getPlatforms(userNo);
        for (LoadBalancerPlatformDto platformDto : platformDtos) {
            if (platformNo.equals(platformDto.getPlatform().getPlatformNo())) {
                this.platformDto = platformDto;
                break;
            }
        }

        // コンポーネント情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        componentDtos = componentService.getComponents(farmNo);

        //ultramonkeyはインスタンスを特定
        if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(type)) {
            this.instanceNo = loadBalancerService.getLoadBalancerInstance(loadBalancerNo);
        }

        AwsDescribeService awsDescribeService = BeanContext.getBean(AwsDescribeService.class);

        //サブネットを取得
        PlatformAws platformAws = platformDto.getPlatformAws();
        this.subnets = new ArrayList<Subnet>();
        if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
            List<Subnet> subnets = awsDescribeService.getSubnets(userNo, platformNo);
            for (Subnet subnet : subnets) {
                this.subnets.add(subnet);
            }
        }

        //セキュリティグループを取得
        this.securityGroups = new ArrayList<String>();
        if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
            List<SecurityGroup> groups = awsDescribeService.getSecurityGroups(userNo, platformNo);
            for (SecurityGroup group : groups) {
                this.securityGroups.add(group.getGroupName());
            }
        }
    }

    private void initValidation() {
        basicTab.initValidation();
        healthCheckTab.initValidation();
    }

    private void showData() {
        basicTab.showData();
        healthCheckTab.showData();
    }

    private void okButtonClick(ClickEvent event) {
        String type = loadBalancerDto.getLoadBalancer().getType();
        PlatformAws platformAws = platformDto.getPlatformAws();

        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        ComponentDto componentDto = (ComponentDto) basicTab.serviceSelect.getValue();
        String subnetId = null;
        String zone = null;
        Collection<Subnet> subnets = null;
        String securityGroup = null;
        boolean isInternalLb = false;
        if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
            subnets = (Collection<Subnet>) basicTab.subnetSelect.getValue();
            securityGroup = (String) basicTab.grpSelect.getValue();
            if ("有効".equals((String) basicTab.internalSelect.getValue())) {
                isInternalLb = true;
            }
        }
        String checkProtocol = (String) healthCheckTab.checkProtocolSelect.getValue();
        String checkPortString = (String) healthCheckTab.checkPortField.getValue();
        String checkPath = (String) healthCheckTab.checkPathField.getValue();
        String checkTimeoutString = (String) healthCheckTab.checkTimeoutField.getValue();
        String checkIntervalString = (String) healthCheckTab.checkIntervalField.getValue();
        String unhealthyThresholdString = (String) healthCheckTab.unhealthyThresholdField.getValue();
        String healthyThresholdString = (String) healthCheckTab.healthyThresholdField.getValue();

        // TODO: 入力チェック
        try {
            basicTab.commentField.validate();
            if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
                basicTab.subnetSelect.validate();
                basicTab.grpSelect.validate();
            }

            healthCheckTab.checkProtocolSelect.validate();
            healthCheckTab.checkPortField.validate();
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

        //サブネットのチェック
        if (PCCConstant.LOAD_BALANCER_ELB.equals(type) && platformAws.getVpc()) {
            if (subnets != null) {
                StringBuffer subnetBuffer = new StringBuffer();
                StringBuffer zoneBuffer = new StringBuffer();
                List<String> zones = new ArrayList<String>();
                for (Subnet subnet : subnets) {
                    if (zones.contains(subnet.getAvailabilityZone())) {
                        //同じゾーンのサブネットを複数選択している場合
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                                ViewMessages.getMessage("IUI-000110"));
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }
                    zones.add(subnet.getAvailabilityZone());
                    subnetBuffer.append(subnetBuffer.length() > 0 ? "," + subnet.getSubnetId() : subnet.getSubnetId());
                    zoneBuffer.append(zoneBuffer.length() > 0 ? "," + subnet.getAvailabilityZone() : subnet
                            .getAvailabilityZone());
                }
                subnetId = subnetBuffer.toString();
                zone = zoneBuffer.toString();
            }
        }

        //オペレーションログ
        AutoApplication aapl = (AutoApplication) apl;
        aapl.doOpLog("LOAD_BALANCER", "Edit Load_Balancer", null, null, loadBalancerNo, null);

        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        // TODO CLOUD BRANCHING
        if (PCCConstant.LOAD_BALANCER_ELB.equals(type)) {
            // AWSロードバランサを変更
            try {
                String loadBalancerName = loadBalancerDto.getLoadBalancer().getLoadBalancerName();
                Long componentNo = componentDto.getComponent().getComponentNo();
                loadBalancerService.updateAwsLoadBalancer(loadBalancerNo, loadBalancerName, comment, componentNo,
                        subnetId, securityGroup, zone, isInternalLb);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(type)) {
            // Ultramonkeyロードバランサを変更
            try {
                String loadBalancerName = loadBalancerDto.getLoadBalancer().getLoadBalancerName();
                Long componentNo = componentDto.getComponent().getComponentNo();
                loadBalancerService.updateUltraMonkeyLoadBalancer(loadBalancerNo, loadBalancerName, comment,
                        componentNo);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // ヘルスチェック設定の変更
        Integer checkPort = Integer.valueOf(checkPortString);
        Integer checkTimeout = Integer.valueOf(checkTimeoutString);
        Integer checkInterval = Integer.valueOf(checkIntervalString);
        Integer healthyThreshold = Integer.valueOf(healthyThresholdString);
        Integer unhealthyThreshold = Integer.valueOf(unhealthyThresholdString);
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
