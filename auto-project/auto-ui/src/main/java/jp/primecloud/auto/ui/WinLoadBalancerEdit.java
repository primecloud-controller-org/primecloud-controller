package jp.primecloud.auto.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AutoScalingConf;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.AutoScalingConfDto;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.ui.validator.IntegerRangeValidator;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.RegexpValidator;
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
import com.vaadin.ui.Table;
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
    final String COLUMN_HEIGHT = "30px";

    final String TAB_HEIGHT = "450px";

    Application apl;

    Long loadBalancerNo;

    Long instanceNo;

    BasicTab basicTab;

    HealthCheckTab healthCheckTab;

    AutoScalingTab autoScalingTab;

    LoadBalancerDto loadBalancerDto;

    LoadBalancerPlatformDto platformDto;

    List<ComponentDto> componentDtos;

    List<PlatformDto> platforms;

    List<SubnetDto> subnets;

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

        // オートスケーリングタブ
        autoScalingTab = new AutoScalingTab();
        if (BooleanUtils.toBoolean(Config.getProperty("autoScaling.useAutoScaling"))) {
            tab.addTab(autoScalingTab, ViewProperties.getCaption("tab.autoScaling"), Icons.DETAIL.resource());
        }
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

            //セキュリティグループ
            grpSelect = new ComboBox();
            grpSelect.setImmediate(true);
            grpSelect.setCaption(ViewProperties.getCaption("field.securityGroup"));
            grpSelect.setNullSelectionAllowed(false);

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
            if ("aws".equals(loadBalancerDto.getLoadBalancer().getType()) && platformAws.getVpc()) {
                form.getLayout().addComponent(grpSelect);
                form.getLayout().addComponent(aboveLayout);
                form.getLayout().addComponent(subnetSelect);
                form.getLayout().addComponent(belowLayout);
            }

            //活性or非活性
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancerDto.getLoadBalancer().getStatus());
            if (LoadBalancerStatus.STOPPED != status) {
                //ロードバランサのステータスがSTOPPED以外の場合
                aboveLayout.setEnabled(false);
                subnetSelect.setEnabled(false);
                belowLayout.setEnabled(false);
                grpSelect.setEnabled(false);
                editServerButton.setEnabled(false);
            }

        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));

            PlatformAws platformAws = platformDto.getPlatformAws();
            if ("aws".equals(loadBalancerDto.getLoadBalancer().getType()) && platformAws.getVpc()) {
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

            // TODO: アイコン名の取得ロジックのリファクタリング
            Icons icon = Icons.NONE;
            if ("aws".equals(platform.getPlatformType())) {
                if (platformAws.getEuca()) {
                    icon = Icons.EUCALYPTUS;
                } else {
                    icon = Icons.AWS;
                }
            } else if ("vmware".equals(platform.getPlatformType())) {
                icon = Icons.VMWARE;
            } else if ("nifty".equals(platform.getPlatformType())) {
                icon = Icons.NIFTY;
            } else if ("cloudstack".equals(platform.getPlatformType())) {
                icon = Icons.CLOUD_STACK;
            }

            String description = platform.getPlatformNameDisp();
            String cloudValue = "<img src=\"" + VaadinUtils.getIconPath(apl, icon) + "\"><div>" + description
                    + "</div>";
            cloudLabel.setValue(cloudValue);
            cloudLabel.setContentMode(Label.CONTENT_XHTML);

            // ロードバランサ種別
            // TODO: アイコン名取得ロジックのリファクタリング
            String type = loadBalancerDto.getLoadBalancer().getType();
            Icons typeIcon = Icons.NONE;
            String typeString = ViewProperties.getLoadBalancerType(type);

            String typeValue = "<img src=\"" + VaadinUtils.getIconPath(apl, typeIcon) + "\"><div>" + typeString
                    + "</div>";
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

            // サブネット
            if ("aws".equals(type) && platformAws.getVpc()) {
                //ELB + VPCの場合
                subnetSelect.setContainerDataSource(createSubnetContainer());
                if (StringUtils.isNotEmpty(loadBalancerDto.getAwsLoadBalancer().getSubnetId())) {
                    List<String> lbSubnets = new ArrayList<String>();
                    for (String lbSubnet : loadBalancerDto.getAwsLoadBalancer().getSubnetId().split(",")) {
                        lbSubnets.add(lbSubnet.trim());
                    }
                    for (SubnetDto subnetDto : subnets) {
                        if (lbSubnets.contains(subnetDto.getSubnetId())) {
                            subnetSelect.select(subnetDto);
                        }
                    }
                } else {
                    subnetSelect.select("");
                }
            }

            //セキュリティグループ
            if ("aws".equals(type) && platformAws.getVpc()) {
                //ELB + VPCの場合
                grpSelect.setContainerDataSource(new IndexedContainer(securityGroups));
                grpSelect.select(loadBalancerDto.getAwsLoadBalancer().getSecurityGroups());
            }
        }

        private IndexedContainer createSubnetContainer() {
            IndexedContainer subnetContainer = new IndexedContainer();
            subnetContainer.addContainerProperty(SUBNET_CAPTION_ID, String.class, null);
            subnetContainer.addContainerProperty("cidrBlock", String.class, null);
            subnetContainer.addContainerProperty("subnetId", String.class, null);
            subnetContainer.addContainerProperty("zoneid", String.class, null);

            for (SubnetDto subnetDto : subnets) {
                Item item = subnetContainer.addItem(subnetDto);
                String subnetDisp = subnetDto.getCidrBlock() + "[" + subnetDto.getZoneid() + "]";
                item.getItemProperty(SUBNET_CAPTION_ID).setValue(subnetDisp);
                item.getItemProperty("cidrBlock").setValue(subnetDto.getCidrBlock());
                item.getItemProperty("subnetId").setValue(subnetDto.getSubnetId());
                item.getItemProperty("zoneid").setValue(subnetDto.getZoneid());
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
            if ("aws".equals(loadBalancerDto.getLoadBalancer().getType())) {
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
                if ("ultramonkey".equals(loadBalancerDto.getLoadBalancer().getType())) {
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

    private class AutoScalingTab extends VerticalLayout {
        final String CHECKENABLED_CAPTION_ID = "EnableName";

        Form mainForm;

        ComboBox checkEnabledSelect;

        SelectCloudTable cloudTable;

        SelectImageTable imageTable;

        ComboBox sizeSelect;

        TextField namingRuleField;

        TextField idleTimeMaxField;

        TextField idleTimeMinField;

        TextField continueLimitField;

        TextField addCountField;

        TextField delCountField;

        List<String> instanceTypes = new ArrayList<String>();

        final String COMBOBOX_WIDTH = "100px";

        AutoScalingTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, true, true);
            setSpacing(false);

            // メインフォーム
            mainForm = new Form();
            Layout mainLayout = mainForm.getLayout();
            addComponent(mainForm);

            // 有効無効
            checkEnabledSelect = new ComboBox(ViewProperties.getCaption("field.as.enabled"));
            checkEnabledSelect.setWidth(COMBOBOX_WIDTH);
            checkEnabledSelect.setImmediate(true);
            checkEnabledSelect.setNullSelectionAllowed(false);
            mainLayout.addComponent(checkEnabledSelect);
            checkEnabledSelect.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    changeCheckEnabled(event);
                }
            });

            // クラウド選択
            cloudTable = new SelectCloudTable();
            mainLayout.addComponent(cloudTable);

            // イメージ選択
            imageTable = new SelectImageTable();
            mainLayout.addComponent(imageTable);

            // サーバーサイズ
            sizeSelect = new ComboBox(ViewProperties.getCaption("field.as.instanceType"));
            sizeSelect.setWidth(COMBOBOX_WIDTH);
            sizeSelect.setNullSelectionAllowed(false);
            mainLayout.addComponent(sizeSelect);

            // ネーミングルール
            namingRuleField = new TextField(ViewProperties.getCaption("field.as.namingRule"));
            namingRuleField.setImmediate(true);
            mainLayout.addComponent(namingRuleField);

            // 増加指標CPU使用率
            idleTimeMaxField = new TextField(ViewProperties.getCaption("field.as.idleTimeMax"));
            idleTimeMaxField.setImmediate(true);
            mainLayout.addComponent(idleTimeMaxField);

            // 削減指標CPU使用率
            idleTimeMinField = new TextField(ViewProperties.getCaption("field.as.idleTimeMin"));
            idleTimeMinField.setImmediate(true);
            mainLayout.addComponent(idleTimeMinField);

            // 監視時間(継続)
            continueLimitField = new TextField(ViewProperties.getCaption("field.as.continueLimit"));
            continueLimitField.setImmediate(true);
            mainLayout.addComponent(continueLimitField);

            // 増加サーバー数
            addCountField = new TextField(ViewProperties.getCaption("field.as.addCount"));
            addCountField.setImmediate(true);
            mainLayout.addComponent(addCountField);

            // 削減サーバー数
            delCountField = new TextField(ViewProperties.getCaption("field.as.delCount"));
            delCountField.setImmediate(true);
            mainLayout.addComponent(delCountField);

        }

        private class SelectCloudTable extends Table {
            SelectCloudTable() {
                //テーブル基本設定
                setCaption(ViewProperties.getCaption("field.as.platformNo"));
                setWidth("420px");
                setPageLength(2);
                setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
                setSortDisabled(true);
                setColumnReorderingAllowed(false);
                setColumnCollapsingAllowed(false);
                setSelectable(true);
                setMultiSelect(false);
                setNullSelectionAllowed(false);
                setImmediate(true);
                addStyleName("win-server-add-cloud");

                //カラム設定
                addContainerProperty("No", Integer.class, null);
                addContainerProperty("Cloud", Label.class, new Label());
                setColumnExpandRatio("Cloud", 100);

                //テーブルのカラムに対してStyleNameを設定
                setCellStyleGenerator(new Table.CellStyleGenerator() {
                    @Override
                    public String getStyle(Object itemId, Object propertyId) {
                        if (propertyId == null) {
                            return "";
                        } else {
                            return propertyId.toString().toLowerCase();
                        }
                    }
                });

                // 行が選択されたときのイベント
                addListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        Long platformNo = (Long) cloudTable.getValue();

                        // サーバ種別を表示
                        showImages(platformNo);
                    }
                });
            }
        }

        private class SelectImageTable extends Table {
            SelectImageTable() {
                //テーブル基本設定
                setCaption(ViewProperties.getCaption("field.as.imageNo"));
                //            setWidth("100%");
                setWidth("420px");
                setPageLength(2);
                setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
                setSortDisabled(true);
                setColumnReorderingAllowed(false);
                setColumnCollapsingAllowed(false);
                setSelectable(true);
                setMultiSelect(false);
                setNullSelectionAllowed(false);
                setImmediate(true);
                addStyleName("win-server-add-os");

                //カラム設定
                addContainerProperty("No", Integer.class, null);
                addContainerProperty("Image", Label.class, new Label());
                addContainerProperty("Detail", Label.class, new Label());
                setColumnExpandRatio("Image", 100);

                //テーブルのカラムに対してStyleNameを設定
                setCellStyleGenerator(new Table.CellStyleGenerator() {
                    public String getStyle(Object itemId, Object propertyId) {
                        if (propertyId == null) {
                            return "";
                        } else {
                            return propertyId.toString().toLowerCase();
                        }
                    }
                });

                // 行が選択されたときのイベント
                addListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        Long platformNo = (Long) cloudTable.getValue();
                        Long imageNo = (Long) imageTable.getValue();

                        showTypes(platformNo, imageNo);

                    }
                });
            }
        }

        private void showImages(Long platformNo) {
            imageTable.removeAllItems();
            if (platformNo == null) {
                return;
            }

            // 選択されたクラウドで利用可能なサーバ種別情報を取得
            List<ImageDto> images = null;
            for (PlatformDto platform : platforms) {
                if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                    images = platform.getImages();
                    break;
                }
            }

            // サーバ種別情報がない場合
            if (images == null) {
                return;
            }

            // サーバ種別情報をテーブルに追加
            int n = 0;
            for (ImageDto image : images) {
                // 選択可能でないサーバ種別の場合はスキップ
                if (BooleanUtils.isNotTrue(image.getImage().getSelectable())) {
                    continue;
                }

                // サーバ種別名
                String name = image.getImage().getImageNameDisp();
                String iconName = StringUtils.substringBefore(image.getImage().getImageName(), "_");
                Icons nameIcon;
                if ("application".equals(iconName)) {
                    nameIcon = Icons.PAAS;
                } else if ("prjserver".equals(iconName)) {
                    nameIcon = Icons.PRJSERVER;
                } else if ("windows".equals(iconName)) {
                    nameIcon = Icons.WINDOWS_APP;
                } else if ("cloudstack".equals(iconName)) {
                    nameIcon = Icons.CLOUD_STACK;
                } else {
                    nameIcon = Icons.fromName(iconName);
                }

                Label nlbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, nameIcon) + "\"><div>" + name
                        + "</div>", Label.CONTENT_XHTML);
                nlbl.setHeight(COLUMN_HEIGHT);

                // OS名
                String os = image.getImage().getOsDisp();
                Icons osIcon = Icons.NONE;
                if (image.getImage().getOs().startsWith("centos")) {
                    osIcon = Icons.CENTOS;
                } else if (image.getImage().getOs().startsWith("windows")) {
                    osIcon = Icons.WINDOWS;
                }

                Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, osIcon) + "\"><div>" + os
                        + "</div>", Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                n++;
                imageTable.addItem(new Object[] { n, nlbl, slbl }, image.getImage().getImageNo());
            }

            Long imageNo = null;
            if (imageTable.getItemIds().size() > 0) {
                imageNo = (Long) imageTable.getItemIds().toArray()[0];
            }

            // 先頭のサーバ種別を選択する
            imageTable.select(imageNo);
        }

        private void showTypes(Long platformNo, Long imageNo) {
            sizeSelect.removeAllItems();
            if (platformNo == null || imageNo == null) {
                return;
            }
            // プラットフォーム選定
            PlatformDto platform = null;
            for (PlatformDto tmpPlatform : platforms) {
                if (platformNo.equals(tmpPlatform.getPlatform().getPlatformNo())) {
                    platform = tmpPlatform;
                }
            }

            // イメージ選定
            ImageDto image = null;
            for (ImageDto tmpImage : platform.getImages()) {
                if (tmpImage.getImage().getImageNo().equals(imageNo)) {
                    image = tmpImage;
                    break;
                }
            }

            if ("aws".equals(platform.getPlatform().getPlatformType())) {
                instanceTypes = new ArrayList<String>();
                for (String instanceType : image.getImageAws().getInstanceTypes().split(",")) {
                    instanceTypes.add(instanceType);
                }
            } else if ("vmware".equals(platform.getPlatform().getPlatformType())) {
                instanceTypes = new ArrayList<String>();
                for (String instanceType : image.getImageVmware().getInstanceTypes().split(",")) {
                    instanceTypes.add(instanceType);
                }
            } else if ("nifty".equals(platform.getPlatform().getPlatformType())) {
                instanceTypes = new ArrayList<String>();
                for (String instanceType : image.getImageNifty().getInstanceTypes().split(",")) {
                    instanceTypes.add(instanceType);
                }
            } else if ("cloudstack".equals(platform.getPlatform().getPlatformType())) {
                instanceTypes = new ArrayList<String>();
                for (String instanceType : image.getImageCloudstack().getInstanceTypes().split(",")) {
                    instanceTypes.add(instanceType);
                }
            }

            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
        }

        private void initValidation() {
            // 入力チェック
            String message;

            // ネーミングルール
            message = ViewMessages.getMessage("IUI-000101", 10);
            namingRuleField.setRequired(true);
            namingRuleField.setRequiredError(message);
            namingRuleField.addValidator(new StringLengthValidator(message, 1, 10, false));
            namingRuleField.addValidator(new RegexpValidator("^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", true, message));

            // 増加指標CPU使用率
            message = ViewMessages.getMessage("IUI-000102", 0, 100);
            idleTimeMaxField.setRequired(true);
            idleTimeMaxField.setRequiredError(message);
            idleTimeMaxField.addValidator(new IntegerRangeValidator(0, 100, message));

            // 削減指標CPU使用率
            message = ViewMessages.getMessage("IUI-000103", 0, 100);
            idleTimeMinField.setRequired(true);
            idleTimeMinField.setRequiredError(message);
            idleTimeMinField.addValidator(new IntegerRangeValidator(0, 100, message));

            // 監視時間(継続)
            message = ViewMessages.getMessage("IUI-000104", 1, 1000000);
            continueLimitField.setRequired(true);
            continueLimitField.setRequiredError(message);
            continueLimitField.addValidator(new IntegerRangeValidator(1, 1000000, message));

            // 増加サーバー数(台)
            message = ViewMessages.getMessage("IUI-000105", 1, 10);
            addCountField.setRequired(true);
            addCountField.setRequiredError(message);
            addCountField.addValidator(new IntegerRangeValidator(1, 10, message));

            // 削減サーバー数(台)
            message = ViewMessages.getMessage("IUI-000106", 1, 10);
            delCountField.setRequired(true);
            delCountField.setRequiredError(message);
            delCountField.addValidator(new IntegerRangeValidator(1, 10, message));

            // サーバータイプ
            message = ViewMessages.getMessage("IUI-000107");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);

        }

        private void showClouds() {
            cloudTable.removeAllItems();

            // クラウド情報をテーブルに追加
            for (int i = 0; i < platforms.size(); i++) {
                PlatformDto platform = platforms.get(i);

                if (BooleanUtils.isNotTrue(platform.getPlatform().getSelectable())) {
                    //使用不可プラットフォームの場合、非表示
                    continue;
                }

                String lbType = loadBalancerDto.getLoadBalancer().getType();
                Platform lbPlatform = platformDto.getPlatform();
                boolean isVpcElb = platformDto.getPlatformAws() != null ? platformDto.getPlatformAws().getVpc() : false;
                if ("aws".equals(lbType) && isVpcElb) {
                    //ELB+VPCの場合→同じプラットフォーム以外選択不可
                    if (lbPlatform.getPlatformNo().equals(platform.getPlatform().getPlatformNo()) == false) {
                        continue;
                    }
                } else if ("aws".equals(lbType) && isVpcElb == false) {
                    //ELBの場合→AWS以外選択不可
                    if ("aws".equals(platform.getPlatform().getPlatformType()) == false) {
                        continue;
                    }
                } else {
                    //ELB以外の場合→EC2+VPCのプラットフォームは選択不可
                    if ("aws".equals(platform.getPlatform().getPlatformType()) && platform.getPlatformAws().getVpc()) {
                        continue;
                    }
                }

                // TODO: アイコン名の取得ロジックのリファクタリング
                Icons icon = Icons.NONE;
                if ("aws".equals(platform.getPlatform().getPlatformType())) {
                    if (platform.getPlatformAws().getEuca()) {
                        icon = Icons.EUCALYPTUS;
                    } else {
                        icon = Icons.AWS;
                    }
                } else if ("vmware".equals(platform.getPlatform().getPlatformType())) {
                    icon = Icons.VMWARE;
                } else if ("nifty".equals(platform.getPlatform().getPlatformType())) {
                    icon = Icons.NIFTY;
                } else if ("cloudstack".equals(platform.getPlatform().getPlatformType())) {
                    icon = Icons.CLOUD_STACK;
                }

                String description = platform.getPlatform().getPlatformNameDisp();

                Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, icon) + "\"><div>" + description
                        + "</div>", Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                cloudTable.addItem(new Object[] { (i + 1), slbl }, platform.getPlatform().getPlatformNo());
            }

            Long platformNo = null;
            if (cloudTable.getItemIds().size() > 0) {
                platformNo = (Long) cloudTable.getItemIds().toArray()[0];
            }

            // 先頭のクラウド情報を選択する
            cloudTable.select(platformNo);
        }

        private void showData() {
            // 有効無効コンボ
            IndexedContainer enableds = getEnabledList();
            checkEnabledSelect.setContainerDataSource(enableds);
            checkEnabledSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            checkEnabledSelect.setItemCaptionPropertyId(CHECKENABLED_CAPTION_ID);

            //クラウド情報を表示
            showClouds();

            AutoScalingConfDto autoScalingConfDto = loadBalancerDto.getAutoScalingConf();
            if (autoScalingConfDto != null) {
                AutoScalingConf autoScalingConf = autoScalingConfDto.getAutoScalingConf();
                // オートスケーリング有効/無効
                if (autoScalingConf.getEnabled()) {
                    checkEnabledSelect.select("有効");
                } else {
                    checkEnabledSelect.select("無効");
                }

                // 調整プラットフォーム
                if (autoScalingConf.getPlatformNo() != null) {
                    cloudTable.select(autoScalingConf.getPlatformNo());
                }

                // 増減サーバイメージ
                if (autoScalingConf.getImageNo() != null) {
                    imageTable.setValue(autoScalingConf.getImageNo());
                }

                // 増減サーバタイプ
                if (autoScalingConf.getInstanceType() != null) {
                    sizeSelect.select(autoScalingConf.getInstanceType());
                }

                // 増減サーバネーミングルール
                if (autoScalingConf.getNamingRule() != null) {
                    String rule = autoScalingConf.getNamingRule().replace("%d", "");
                    namingRuleField.setValue(rule);
                }

                // 増加指標CPU使用率(%)
                if (autoScalingConf.getIdleTimeMax() != null) {
                    idleTimeMaxField.setValue(autoScalingConf.getIdleTimeMax().toString());
                }

                // 削減指標CPU使用率(%)
                if (autoScalingConf.getIdleTimeMin() != null) {
                    idleTimeMinField.setValue(autoScalingConf.getIdleTimeMin().toString());
                }

                // 監視継続時間(分)
                if (autoScalingConf.getContinueLimit() != null) {
                    continueLimitField.setValue(autoScalingConf.getContinueLimit().toString());
                }

                // 増加サーバー数(台)
                if (autoScalingConf.getAddCount() != null) {
                    addCountField.setValue(autoScalingConf.getAddCount().toString());
                }

                // 削減サーバー数(台)
                if (autoScalingConf.getDelCount() != null) {
                    delCountField.setValue(autoScalingConf.getDelCount().toString());
                }

            }
        }

        private IndexedContainer getEnabledList() {
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty(CHECKENABLED_CAPTION_ID, String.class, null);

            Item item = container.addItem("有効");
            item.getItemProperty(CHECKENABLED_CAPTION_ID).setValue("有効");

            item = container.addItem("無効");
            item.getItemProperty(CHECKENABLED_CAPTION_ID).setValue("無効");

            return container;
        }

        private void changeCheckEnabled(Property.ValueChangeEvent event) {
            if ("有効".equals(checkEnabledSelect.getValue())) {
                cloudTable.setEnabled(true);
                imageTable.setEnabled(true);
                sizeSelect.setEnabled(true);
                namingRuleField.setEnabled(true);
                idleTimeMaxField.setEnabled(true);
                idleTimeMinField.setEnabled(true);
                continueLimitField.setEnabled(true);
                addCountField.setEnabled(true);
                delCountField.setEnabled(true);
            } else {
                cloudTable.setEnabled(false);
                imageTable.setEnabled(false);
                sizeSelect.setEnabled(false);
                namingRuleField.setEnabled(false);
                idleTimeMaxField.setEnabled(false);
                idleTimeMinField.setEnabled(false);
                continueLimitField.setEnabled(false);
                addCountField.setEnabled(false);
                delCountField.setEnabled(false);
            }
        }
    }

    private void initData() {
        Long userNo = ViewContext.getUserNo();
        Long farmNo = ViewContext.getFarmNo();

        // ロードバランサ情報を取得
        // TODO: ロジックを必ずリファクタリングすること！
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
        // TODO: ロジックを必ずリファクタリングすること！
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
        if ("ultramonkey".equals(type)) {
            this.instanceNo = loadBalancerService.getLoadBalancerInstance(loadBalancerNo);
        }

        //サブネットを取得
        PlatformAws platformAws = platformDto.getPlatformAws();
        this.subnets = new ArrayList<SubnetDto>();
        if ("aws".equals(type) && platformAws.getVpc()) {
            IaasDescribeService iaasDescribeService = BeanContext.getBean(IaasDescribeService.class);
            List<SubnetDto> subnetDtos = iaasDescribeService.getSubnets(userNo, platformNo, platformAws.getVpcId());
            for (SubnetDto subnetDto : subnetDtos) {
                subnets.add(subnetDto);
            }
        }

        //セキュリティグループを取得
        this.securityGroups = new ArrayList<String>();
        if ("aws".equals(type) && platformAws.getVpc()) {
            IaasDescribeService iaasDescribeService = BeanContext.getBean(IaasDescribeService.class);
            List<SecurityGroupDto> securityGroupDtos = iaasDescribeService.getSecurityGroups(userNo, platformNo,
                    platformAws.getVpcId());
            for (SecurityGroupDto securityGroupDto : securityGroupDtos) {
                this.securityGroups.add(securityGroupDto.getGroupName());
            }
        }

        //プラットフォーム情報(AutoScaling用)を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        this.platforms = instanceService.getPlatforms(userNo);
    }

    private void initValidation() {
        basicTab.initValidation();
        healthCheckTab.initValidation();
        autoScalingTab.initValidation();
    }

    private void showData() {
        basicTab.showData();
        healthCheckTab.showData();
        autoScalingTab.showData();
    }

    private void okButtonClick(ClickEvent event) {
        Long farmNo = ViewContext.getFarmNo();
        String type = loadBalancerDto.getLoadBalancer().getType();
        PlatformAws platformAws = platformDto.getPlatformAws();

        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        ComponentDto componentDto = (ComponentDto) basicTab.serviceSelect.getValue();
        String subnetId = null;
        String zone = null;
        Collection<SubnetDto> subnets = null;
        String securityGroup = null;
        if ("aws".equals(type) && platformAws.getVpc()) {
            subnets = (Collection<SubnetDto>) basicTab.subnetSelect.getValue();
            securityGroup = (String) basicTab.grpSelect.getValue();
        }
        String checkProtocol = (String) healthCheckTab.checkProtocolSelect.getValue();
        String checkPortString = (String) healthCheckTab.checkPortField.getValue();
        String checkPath = (String) healthCheckTab.checkPathField.getValue();
        String checkTimeoutString = (String) healthCheckTab.checkTimeoutField.getValue();
        String checkIntervalString = (String) healthCheckTab.checkIntervalField.getValue();
        String unhealthyThresholdString = (String) healthCheckTab.unhealthyThresholdField.getValue();
        String healthyThresholdString = (String) healthCheckTab.healthyThresholdField.getValue();

        Long platformNo = (Long) autoScalingTab.cloudTable.getValue();
        Long imageNo = (Long) autoScalingTab.imageTable.getValue();
        String instanceType = (String) autoScalingTab.sizeSelect.getValue();
        String checkEnabledValue = (String) autoScalingTab.checkEnabledSelect.getValue();
        if ("有効".equals(checkEnabledValue)) {
            checkEnabledValue = "1";
        } else {
            checkEnabledValue = "0";
        }

        String namingRuleString = (String) autoScalingTab.namingRuleField.getValue();
        String idleTimeMaxString = (String) autoScalingTab.idleTimeMaxField.getValue();
        String idleTimeMinString = (String) autoScalingTab.idleTimeMinField.getValue();
        String continueLimitString = (String) autoScalingTab.continueLimitField.getValue();
        String addCountString = (String) autoScalingTab.addCountField.getValue();
        String delCountString = (String) autoScalingTab.delCountField.getValue();

        // TODO: 入力チェック
        try {
            basicTab.commentField.validate();
            if ("aws".equals(type) && platformAws.getVpc()) {
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

            if ("1".equals(checkEnabledValue)
                    && BooleanUtils.toBoolean(Config.getProperty("autoScaling.useAutoScaling"))) {
                autoScalingTab.namingRuleField.validate();
                autoScalingTab.idleTimeMaxField.validate();
                autoScalingTab.idleTimeMinField.validate();
                autoScalingTab.continueLimitField.validate();
                autoScalingTab.addCountField.validate();
                autoScalingTab.delCountField.validate();
                autoScalingTab.sizeSelect.validate();
            }

        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        //特殊入力チェック
        if (namingRuleString.startsWith("lb-")) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000083", namingRuleString));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        //サブネットのチェック
        if ("aws".equals(type) && platformAws.getVpc()) {
            if (subnets != null) {
                StringBuffer subnetBuffer = new StringBuffer();
                StringBuffer zoneBuffer = new StringBuffer();
                List<String> zones = new ArrayList<String>();
                for (SubnetDto subnetDto : subnets) {
                    if (zones.contains(subnetDto.getZoneid())) {
                        //同じゾーンのサブネットを複数選択している場合
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                                ViewMessages.getMessage("IUI-000110"));
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }
                    zones.add(subnetDto.getZoneid());
                    subnetBuffer.append(subnetBuffer.length() > 0 ? "," + subnetDto.getSubnetId() : subnetDto
                            .getSubnetId());
                    zoneBuffer.append(zoneBuffer.length() > 0 ? "," + subnetDto.getZoneid() : subnetDto.getZoneid());
                }
                subnetId = subnetBuffer.toString();
                zone = zoneBuffer.toString();
            }
        }

        //TODO LOG
        AutoApplication aapl = (AutoApplication) apl;
        aapl.doOpLog("LOAD_BALANCER", "Edit Load_Balancer", null, null, loadBalancerNo, null);

        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        // TODO: 固定文字列を外部化・定数化
        if ("aws".equals(type)) {
            // AWSロードバランサを変更
            try {
                String loadBalancerName = loadBalancerDto.getLoadBalancer().getLoadBalancerName();
                Long componentNo = componentDto.getComponent().getComponentNo();
                loadBalancerService.updateAwsLoadBalancer(loadBalancerNo, loadBalancerName, comment, componentNo,
                        subnetId, securityGroup, zone);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if ("ultramonkey".equals(type)) {
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

        // オートスケーリング設定の変更
        if (BooleanUtils.toBoolean(Config.getProperty("autoScaling.useAutoScaling"))) {
            Integer enabledValue = Integer.valueOf(checkEnabledValue);
            Long idleTimeMaxValue = Long.valueOf(idleTimeMaxString);
            Long idleTimeMinValue = Long.valueOf(idleTimeMinString);
            Long continueLimitValue = Long.valueOf(continueLimitString);
            Long addCountValue = Long.valueOf(addCountString);
            Long delCountValue = Long.valueOf(delCountString);
            namingRuleString = namingRuleString + "%d";
            try {
                loadBalancerService.updateAutoScalingConf(farmNo, loadBalancerNo, platformNo, imageNo, instanceType,
                        enabledValue, namingRuleString, idleTimeMaxValue, idleTimeMinValue, continueLimitValue,
                        addCountValue, delCountValue);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // 画面を閉じる
        close();
    }

}
