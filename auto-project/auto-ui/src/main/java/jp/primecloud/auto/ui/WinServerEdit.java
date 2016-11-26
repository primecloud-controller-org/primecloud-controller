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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.process.aws.AwsAddressProcess;
import jp.primecloud.auto.process.aws.AwsProcessClient;
import jp.primecloud.auto.process.aws.AwsProcessClientFactory;
import jp.primecloud.auto.service.AwsDescribeService;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.NiftyDescribeService;
import jp.primecloud.auto.service.VmwareDescribeService;
import jp.primecloud.auto.service.dto.AddressDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.DataDiskDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.InstanceNetworkDto;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.NetworkDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.StorageTypeDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.VmwareAddressDto;
import jp.primecloud.auto.service.dto.ZoneDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.util.IpAddressUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vmware.vim25.mo.ComputeResource;

/**
 * <p>
 * サーバ編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinServerEdit extends Window {

    private final String TAB_HEIGHT = "420px";

    private Long instanceNo;

    private BasicTab basicTab;

    private AwsDetailTab awsDetailTab;

    private VmwareDetailTab vmwareDetailTab;

    private VmwareEditIpTab vmwareEditIpTab;

    private NiftyDetailTab niftyDetailTab;

    private CloudStackDetailTab cloudStackDetailTab;

    private VcloudDetailTab vcloudDetailTab;

    private VcloudNetworkTab vcloudNetworkTab;

    private AzureDetailTab azureDetailTab;

    private OpenStackDetailTab openStackDetailTab;

    private InstanceDto instance;

    private PlatformDto platform;

    private ImageDto image;

    public WinServerEdit(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

    @Override
    public void attach() {
        // 初期データの取得
        loadData();

        // モーダルウインドウ
        setCaption(ViewProperties.getCaption("window.winServerEdit"));
        setModal(true);
        setWidth("600px");
        setIcon(Icons.EDITMINI.resource());

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        TabSheet tab = new TabSheet();

        // 基本情報タブ
        basicTab = new BasicTab(instance, platform, image);
        tab.addTab(basicTab, ViewProperties.getCaption("tab.basic"), Icons.BASIC.resource());
        layout.addComponent(tab);
        basicTab.loadData();
        basicTab.initValidation();
        basicTab.show();

        // AWSの場合
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())) {
            // 詳細設定タブ
            awsDetailTab = new AwsDetailTab(instance, platform, image);
            tab.addTab(awsDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());

            awsDetailTab.loadData();
            awsDetailTab.initValidation();
            awsDetailTab.show();
        }
        // VMwareの場合
        else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatform().getPlatformType())) {
            // 詳細設定タブ
            vmwareDetailTab = new VmwareDetailTab(instance, platform, image);
            tab.addTab(vmwareDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());

            vmwareDetailTab.loadData();
            vmwareDetailTab.initValidation();
            vmwareDetailTab.show();

            // IP設定タブ
            boolean enableVmwareStaticIp = BooleanUtils.toBoolean(Config.getProperty("ui.enableVmwareEditIp"));
            if (BooleanUtils.isTrue(enableVmwareStaticIp)) {
                vmwareEditIpTab = new VmwareEditIpTab(instance);
                tab.addTab(vmwareEditIpTab, ViewProperties.getCaption("tab.editIp"), Icons.DETAIL.resource());

                vmwareEditIpTab.initValidation();
                vmwareEditIpTab.show();
            }
        }
        // Niftyの場合
        else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatform().getPlatformType())) {
            // 詳細設定タブ
            niftyDetailTab = new NiftyDetailTab(instance, platform, image);
            tab.addTab(niftyDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());

            niftyDetailTab.loadData();
            niftyDetailTab.initValidation();
            niftyDetailTab.show();
        }
        // CloudStackの場合
        else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatform().getPlatformType())) {
            // 詳細設定タブ
            cloudStackDetailTab = new CloudStackDetailTab(instance, platform, image);
            tab.addTab(cloudStackDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());

            cloudStackDetailTab.loadData();
            cloudStackDetailTab.initValidation();
            cloudStackDetailTab.show();
        }
        // vCloudの場合
        else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatform().getPlatformType())) {
            // 詳細設定タブ
            vcloudDetailTab = new VcloudDetailTab(instance, platform, image);
            tab.addTab(vcloudDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());

            vcloudDetailTab.loadData();
            vcloudDetailTab.initValidation();
            vcloudDetailTab.show();

            // ネットワーク設定タブ
            vcloudNetworkTab = new VcloudNetworkTab(instance, platform);
            tab.addTab(vcloudNetworkTab, ViewProperties.getCaption("tab.network"), Icons.DETAIL.resource());

            vcloudNetworkTab.loadData();
            vcloudNetworkTab.show();
        }
        // Azureの場合
        else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
            // 詳細設定タブ
            azureDetailTab = new AzureDetailTab(instance, platform, image);
            tab.addTab(azureDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());

            azureDetailTab.loadData();
            azureDetailTab.initValidation();
            azureDetailTab.show();
        }
        // OpenStackの場合
        else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatform().getPlatformType())) {
            // 詳細設定タブ
            openStackDetailTab = new OpenStackDetailTab(instance, platform, image);
            tab.addTab(openStackDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());

            openStackDetailTab.loadData();
            openStackDetailTab.initValidation();
            openStackDetailTab.show();
        }

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editServer.ok"));
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                okButtonClick(event);
            }
        });
        bottomLayout.addComponent(okButton);
        okButton.setClickShortcut(KeyCode.ENTER); // [Enter]でokButtonクリック
        okButton.focus();

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
    }

    private class BasicTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private TextField serverNameField;

        private TextField hostNameField;

        private TextField commentField;

        private Label cloudLabel;

        private Label imageLabel;

        private Label osLabel;

        private AvailableServiceTable serviceTable;

        private List<Long> componentNos;

        private boolean attachService = false;

        public BasicTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;
        }

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            Form form = new Form();
            form.setSizeFull();
            form.addStyleName("win-server-edit-form");

            // サーバ名
            serverNameField = new TextField(ViewProperties.getCaption("field.serverName"));
            form.getLayout().addComponent(serverNameField);

            // ホスト名
            hostNameField = new TextField(ViewProperties.getCaption("field.hostName"));
            hostNameField.setWidth("100%");
            form.getLayout().addComponent(hostNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("100%");
            form.getLayout().addComponent(commentField);

            // プラットフォーム
            CssLayout cloudLayout = new CssLayout();
            cloudLayout.setWidth("100%");
            cloudLayout.setCaption(ViewProperties.getCaption("field.cloud"));
            cloudLabel = new Label();
            cloudLayout.addComponent(cloudLabel);
            form.getLayout().addComponent(cloudLayout);

            // サーバ種別
            CssLayout imageLayout = new CssLayout();
            imageLayout.setWidth("100%");
            imageLayout.setCaption(ViewProperties.getCaption("field.image"));
            imageLabel = new Label();
            imageLayout.addComponent(imageLabel);
            form.getLayout().addComponent(imageLayout);

            // OS
            CssLayout osLayout = new CssLayout();
            osLayout.setWidth("100%");
            osLayout.setCaption(ViewProperties.getCaption("field.os"));
            osLabel = new Label();
            osLayout.addComponent(osLabel);
            form.getLayout().addComponent(osLayout);

            // ロードバランサでない場合
            if (BooleanUtils.isNotTrue(instance.getInstance().getLoadBalancer())) {
                // サービスを有効にするかどうか
                String enableService = Config.getProperty("ui.enableService");

                // サービスを有効にする場合
                if (StringUtils.isEmpty(enableService) || BooleanUtils.toBoolean(enableService)) {
                    // 利用可能サービス
                    Panel panel = new Panel();
                    serviceTable = new AvailableServiceTable();
                    panel.addComponent(serviceTable);
                    form.getLayout().addComponent(panel);
                    panel.setSizeFull();

                    // サービス選択ボタン
                    Button attachServiceButton = new Button(ViewProperties.getCaption("button.serverAttachService"));
                    attachServiceButton.setDescription(ViewProperties.getCaption("description.serverAttachService"));
                    attachServiceButton.setIcon(Icons.SERVICETAB.resource());
                    attachServiceButton.addListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            attachServiceButtonClick(event);
                        }
                    });

                    HorizontalLayout layout = new HorizontalLayout();
                    layout.setSpacing(true);
                    layout.addComponent(attachServiceButton);
                    Label label = new Label(ViewProperties.getCaption("label.serverAttachService"));
                    layout.addComponent(label);
                    layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
                    form.getLayout().addComponent(layout);
                }
            }

            addComponent(form);
        }

        private void loadData() {
            // サーバに関連付けられたサービスを取得
            componentNos = new ArrayList<Long>();
            List<ComponentInstanceDto> componentInstances = instance.getComponentInstances();
            for (ComponentInstanceDto componentInstance : componentInstances) {
                if (BooleanUtils.isTrue(componentInstance.getComponentInstance().getAssociate())) {
                    componentNos.add(componentInstance.getComponentInstance().getComponentNo());
                }
            }
        }

        public void show() {
            // サーバ名
            serverNameField.setReadOnly(false);
            serverNameField.setValue(instance.getInstance().getInstanceName());
            serverNameField.setReadOnly(true);

            // ホスト名
            hostNameField.setReadOnly(false);
            hostNameField.setValue(instance.getInstance().getFqdn());
            hostNameField.setReadOnly(true);

            // コメント
            String comment = instance.getInstance().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            // プラットフォーム
            String cloudName = platform.getPlatform().getPlatformNameDisp();
            Icons cloudIcon = IconUtils.getPlatformIcon(platform);
            cloudLabel.setCaption(cloudName);
            cloudLabel.setIcon(cloudIcon.resource());

            // サーバ種別
            String imageName = image.getImage().getImageNameDisp();
            Icons imageIcon = IconUtils.getImageIcon(image);
            imageLabel.setCaption(imageName);
            imageLabel.setIcon(imageIcon.resource());

            // OS
            String osName = image.getImage().getOsDisp();
            Icons osIcon = IconUtils.getOsIcon(image);
            osLabel.setCaption(osName);
            osLabel.setIcon(osIcon.resource());

            // ロードバランサでない場合
            if (BooleanUtils.isNotTrue(instance.getInstance().getLoadBalancer())) {
                // 利用可能サービス
                serviceTable.show(image.getComponentTypes());
            }
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }

        private void attachServiceButtonClick(ClickEvent event) {
            WinServerAttachService winServerAttachService = new WinServerAttachService(instance, image, componentNos);
            winServerAttachService.addListener(new Window.CloseListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void windowClose(Window.CloseEvent e) {
                    List<Long> componentNos = (List<Long>) ContextUtils.getAttribute("componentNos");
                    if (componentNos != null) {
                        ContextUtils.removeAttribute("componentNos");
                        BasicTab.this.componentNos = componentNos;
                        attachService = true;
                    }
                }
            });

            getWindow().getApplication().getMainWindow().addWindow(winServerAttachService);
        }

    }

    private class AvailableServiceTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.availableService"));
            setWidth("100%");
            setPageLength(3);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(false);
            setMultiSelect(false);
            setImmediate(true);
            addStyleName("win-server-add-service");

            // カラム設定
            addContainerProperty("Service", Label.class, new Label());
            addContainerProperty("Description", String.class, null);
            setColumnExpandRatio("Service", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<ComponentType> componentTypes) {
            removeAllItems();

            if (componentTypes == null) {
                return;
            }

            for (ComponentType componentType : componentTypes) {
                // サービス名
                String name = componentType.getComponentTypeNameDisp();
                Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), nameIcon, name), Label.CONTENT_XHTML);
                slbl.setHeight("26px");

                // サービス説明
                String description = componentType.getLayerDisp();

                addItem(new Object[] { slbl, description }, componentType.getComponentTypeNo());
            }
        }

    }

    private class AwsDetailTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private Form form = new Form();

        private ComboBox sizeSelect;

        private ComboBox keySelect;

        private ComboBox grpSelect;

        private ComboBox subnetSelect;

        private TextField privateIpField;

        private ComboBox zoneSelect;

        private ComboBox elasticIpSelect;

        private List<String> keyNames;

        private List<String> groupNames;

        private List<AvailabilityZone> zones;

        private List<AwsAddress> elasticIps;

        private List<Subnet> subnets;

        private final String CIDR_BLOCK_CAPTION_ID = "cidrBlock";

        private final String ZONE_CAPTION_ID = "zoneName";

        private final String ELASTIC_IP_CAPTION_ID = "ElasticIP";

        private final Long NULL_ADDRESS = Long.valueOf(-1L);

        private final String TEXT_WIDTH = "150px";

        private final String COMBOBOX_WIDTH = "150px";

        private final String IP_COMBOBOX_WIDTH = "220px";

        private final String BUTTON_WIDTH = "150px";

        public AwsDetailTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;
        }

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // サーバサイズ
            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setWidth(COMBOBOX_WIDTH);
            sizeSelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(sizeSelect);

            // キーペア
            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setWidth(COMBOBOX_WIDTH);
            keySelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(keySelect);

            // セキュリティグループ
            grpSelect = new ComboBox(ViewProperties.getCaption("field.securityGroup"));
            grpSelect.setWidth(COMBOBOX_WIDTH);
            grpSelect.setImmediate(true);
            grpSelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(grpSelect);

            // VPCの場合
            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                // サブネット
                subnetSelect = new ComboBox(ViewProperties.getCaption("field.subnet"));
                subnetSelect.setImmediate(true);
                subnetSelect.setWidth(COMBOBOX_WIDTH);
                subnetSelect.setNullSelectionAllowed(false);
                subnetSelect.addContainerProperty(CIDR_BLOCK_CAPTION_ID, String.class, null);
                subnetSelect.setItemCaptionPropertyId(CIDR_BLOCK_CAPTION_ID);
                subnetSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
                form.getLayout().addComponent(subnetSelect);

                // プライベートIPアドレス
                privateIpField = new TextField(ViewProperties.getCaption("field.privateIp"));
                privateIpField.setImmediate(true);
                privateIpField.setWidth(TEXT_WIDTH);
                form.getLayout().addComponent(privateIpField);
            }
            // 非VPCの場合
            else {
                // ゾーン
                zoneSelect = new ComboBox(ViewProperties.getCaption("field.zone"));
                zoneSelect.setWidth(COMBOBOX_WIDTH);
                zoneSelect.setNullSelectionAllowed(false);
                zoneSelect.addContainerProperty(ZONE_CAPTION_ID, String.class, null);
                zoneSelect.setItemCaptionPropertyId(ZONE_CAPTION_ID);
                zoneSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
                form.getLayout().addComponent(zoneSelect);
            }

            Label spacer = new Label(" ");
            spacer.addStyleName("desc-padding-horizontal");
            spacer.setHeight("5px");
            form.getLayout().addComponent(spacer);

            // ElasticIp
            elasticIpSelect = new ComboBox(ViewProperties.getCaption("field.elasticIp"));
            elasticIpSelect.setWidth(IP_COMBOBOX_WIDTH);
            elasticIpSelect.setNullSelectionAllowed(false);
            elasticIpSelect.addContainerProperty(ELASTIC_IP_CAPTION_ID, String.class, null);
            elasticIpSelect.setItemCaptionPropertyId(ELASTIC_IP_CAPTION_ID);
            elasticIpSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            form.getLayout().addComponent(elasticIpSelect);

            Button addButton = new Button(ViewProperties.getCaption("button.addElasticIp"));
            addButton.setDescription(ViewProperties.getCaption("description.addElasticIp"));
            addButton.setIcon(Icons.ADD.resource());
            addButton.setWidth(BUTTON_WIDTH);
            addButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    addButtonClick();
                }
            });

            Button deleteButton = new Button(ViewProperties.getCaption("button.deleteElasticIp"));
            deleteButton.setDescription(ViewProperties.getCaption("description.deleteElasticIp"));
            deleteButton.setIcon(Icons.DELETEMINI.resource());
            deleteButton.setWidth(BUTTON_WIDTH);
            deleteButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    deleteButtonClick();
                }
            });

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setMargin(false);
            layout.addComponent(addButton);
            layout.addComponent(deleteButton);
            form.getLayout().addComponent(layout);

            addComponent(form);
        }

        private void loadData() {
            AwsDescribeService awsDescribeService = BeanContext.getBean(AwsDescribeService.class);
            Long platformNo = platform.getPlatform().getPlatformNo();

            // キーペア情報を取得
            List<KeyPairInfo> keyPairInfos = awsDescribeService.getKeyPairs(ViewContext.getUserNo(), platformNo);
            List<String> keyNames = new ArrayList<String>();
            for (KeyPairInfo keyPairInfo : keyPairInfos) {
                keyNames.add(keyPairInfo.getKeyName());
            }
            this.keyNames = keyNames;

            // セキュリティグループ情報を取得
            List<String> groupNames = new ArrayList<String>();
            List<SecurityGroup> securityGroups = awsDescribeService.getSecurityGroups(ViewContext.getUserNo(),
                    platformNo);
            for (SecurityGroup securityGroup : securityGroups) {
                groupNames.add(securityGroup.getGroupName());
            }
            this.groupNames = groupNames;

            // VPCの場合
            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                // サブネット情報の取得
                List<Subnet> subnets = awsDescribeService.getSubnets(ViewContext.getUserNo(), platformNo);
                this.subnets = subnets;
            }
            // 非VPCの場合
            else {
                //　ゾーン情報の取得
                List<AvailabilityZone> zones = awsDescribeService.getAvailabilityZones(ViewContext.getUserNo(),
                        platformNo);
                if (BooleanUtils.isNotTrue(platform.getPlatformAws().getEuca())) {
                    // EC2の場合、空行を先頭に追加してゾーンを無指定にできるようにする
                    zones.add(0, new AvailabilityZone());
                }
                this.zones = zones;
            }

            // ElasticIp情報の取得
            List<AwsAddress> elasticIps = awsDescribeService.getAddresses(ViewContext.getUserNo(), platformNo);
            this.elasticIps = elasticIps;
        }

        private void initValidation() {
            String message;
            message = ViewMessages.getMessage("IUI-000027");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000028");
            keySelect.setRequired(true);
            keySelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000029");
            grpSelect.setRequired(true);
            grpSelect.setRequiredError(message);

            if (BooleanUtils.isTrue(platform.getPlatformAws().getEuca())) {
                // Eucalyptus の場合は入力必須
                message = ViewMessages.getMessage("IUI-000050");
                zoneSelect.setRequired(true);
                zoneSelect.setRequiredError(message);
            }

            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                // VPCの場合
                message = ViewMessages.getMessage("IUI-000108");
                subnetSelect.setRequired(true);
                subnetSelect.setRequiredError(message);

                privateIpField.setRequired(false);
                Validator privateIpFieldValidator = new RegexpValidator(
                        "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                        ViewMessages.getMessage("IUI-000095", privateIpField.getCaption()));
                privateIpField.addValidator(privateIpFieldValidator);
            }

            message = ViewMessages.getMessage("IUI-000063");
            elasticIpSelect.setRequired(true);
            elasticIpSelect.setRequiredError(message);
        }

        public void show() {
            //　サーバサイズ
            for (String instanceType : image.getImageAws().getInstanceTypes().split(",")) {
                sizeSelect.addItem(instanceType.trim());
            }
            sizeSelect.select(instance.getAwsInstance().getInstanceType());

            // キーペア
            for (String keyName : keyNames) {
                keySelect.addItem(keyName);
            }
            keySelect.select(instance.getAwsInstance().getKeyName());

            // セキュリティグループ
            for (String groupName : groupNames) {
                grpSelect.addItem(groupName);
            }
            grpSelect.select(instance.getAwsInstance().getSecurityGroups());

            // VPCの場合
            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                // サブネット
                for (Subnet subnet : subnets) {
                    Item item = subnetSelect.addItem(subnet.getSubnetId());
                    item.getItemProperty(CIDR_BLOCK_CAPTION_ID).setValue(subnet.getCidrBlock());
                }
                subnetSelect.select(instance.getAwsInstance().getSubnetId());

                // プライベートIPアドレス
                privateIpField.setValue(instance.getAwsInstance().getPrivateIpAddress());
            }
            // 非VPCの場合
            else {
                // ゾーン
                for (AvailabilityZone zone : zones) {
                    Item item = zoneSelect.addItem(zone.getZoneName());
                    item.getItemProperty(ZONE_CAPTION_ID).setValue(zone.getZoneName());
                }
                zoneSelect.select(instance.getAwsInstance().getAvailabilityZone());
            }

            // ElasticIP
            showElasticIp();
            if (instance.getAwsAddress() != null) {
                elasticIpSelect.select(instance.getAwsAddress().getAddressNo());
            } else {
                elasticIpSelect.select(NULL_ADDRESS);
            }

            // サーバが停止していない場合、詳細設定タブ自体を変更できないようにする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                form.setEnabled(false);
            }

            // サーバが既に作成済みの場合、いくつかの項目を変更できないようにする
            if (StringUtils.isNotEmpty(instance.getAwsInstance().getInstanceId())) {
                keySelect.setEnabled(false);
                if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                    subnetSelect.setEnabled(false);
                    privateIpField.setEnabled(false);
                } else {
                    grpSelect.setEnabled(false); // セキュリティグループは非VPCの場合のみ変更できない
                    zoneSelect.setEnabled(false);
                }
            }

            // ボリュームが作成済みの場合、いくつかの項目を変更できないようにする
            if (instance.getAwsVolumes() != null && instance.getAwsVolumes().size() > 0) {
                if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                    subnetSelect.setEnabled(false);
                } else {
                    zoneSelect.setEnabled(false);
                }
            }
        }

        public void showElasticIp() {
            elasticIpSelect.removeAllItems();

            String dynamic = ViewProperties.getCaption("field.elasticIp.dynamic");
            String associated = ViewProperties.getCaption("field.elasticIp.associated");
            {
                Item item = elasticIpSelect.addItem(NULL_ADDRESS);
                item.getItemProperty(ELASTIC_IP_CAPTION_ID).setValue(dynamic);
            }
            for (AwsAddress address : elasticIps) {
                Item item = elasticIpSelect.addItem(address.getAddressNo());
                if (address.getInstanceNo() == null) {
                    item.getItemProperty(ELASTIC_IP_CAPTION_ID).setValue(address.getPublicIp());
                } else {
                    item.getItemProperty(ELASTIC_IP_CAPTION_ID).setValue(address.getPublicIp() + " " + associated);
                }
            }
        }

        public Subnet findSubnet(String subnetId) {
            for (Subnet subnet : subnets) {
                if (subnet.getSubnetId().equals(subnetId)) {
                    return subnet;
                }
            }
            return null;
        }

        public AwsAddress findAwsAddress(Long addressNo) {
            for (AwsAddress awsAddress : elasticIps) {
                if (addressNo.equals(awsAddress.getAddressNo())) {
                    return awsAddress;
                }
            }
            return null;
        }

        private void addButtonClick() {
            // 新しいElasticIPを取得する
            AwsProcessClientFactory awsProcessClientFactory = BeanContext.getBean(AwsProcessClientFactory.class);
            AwsAddressProcess awsAddressProcess = BeanContext.getBean(AwsAddressProcess.class);
            AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(ViewContext.getUserNo(),
                    instance.getInstance().getPlatformNo());
            AwsAddress awsAddress = awsAddressProcess.createAddress(awsProcessClient);

            // ElasticIP情報を再取得して表示する
            AwsDescribeService awsDescribeService = BeanContext.getBean(AwsDescribeService.class);
            elasticIps = awsDescribeService.getAddresses(ViewContext.getUserNo(), instance.getInstance()
                    .getPlatformNo());
            showElasticIp();
            elasticIpSelect.select(awsAddress.getAddressNo());

            // 取得したElasticIPをダイアログ表示する
            String message = ViewMessages.getMessage("IUI-000061", awsAddress.getPublicIp());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OK);
            getApplication().getMainWindow().addWindow(dialog);
        }

        private void deleteButtonClick() {
            Long addressNo = (Long) elasticIpSelect.getValue();

            // ElasticIPが選択されていない場合
            if (addressNo == null || NULL_ADDRESS.equals(addressNo)) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                        ViewMessages.getMessage("IUI-000062"));
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 自サーバ以外に割り当てられているElasticIPが選択されている場合は削除できない
            final AwsAddress address = findAwsAddress(addressNo);
            if (address.getInstanceNo() != null
                    && !address.getInstanceNo().equals(instance.getInstance().getInstanceNo())) {
                String message = ViewMessages.getMessage("IUI-000064");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            String message = ViewMessages.getMessage("IUI-000060", address.getPublicIp());
            DialogConfirm dialogConfirm = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialogConfirm.setCallback(new Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    // ElasticIPを削除する
                    AwsProcessClientFactory awsProcessClientFactory = BeanContext
                            .getBean(AwsProcessClientFactory.class);
                    AwsAddressProcess awsAddressProcess = BeanContext.getBean(AwsAddressProcess.class);
                    AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(
                            address.getUserNo(), address.getPlatformNo());
                    awsAddressProcess.deleteAddress(awsProcessClient, address.getAddressNo());

                    // ElasticIP情報を再取得して表示する
                    AwsDescribeService awsDescribeService = BeanContext.getBean(AwsDescribeService.class);
                    elasticIps = awsDescribeService.getAddresses(ViewContext.getUserNo(), instance.getInstance()
                            .getPlatformNo());
                    showElasticIp();

                    // 動的なIPを選択状態にする
                    elasticIpSelect.select(NULL_ADDRESS);
                }
            });

            getApplication().getMainWindow().addWindow(dialogConfirm);
        }
    }

    private class VmwareDetailTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private Form form = new Form();

        private ComboBox sizeSelect;

        private ComboBox keySelect;

        private ComboBox clusterSelect;

        private List<VmwareKeyPair> vmwareKeyPairs;

        private List<String> clusters;

        private final String KEY_CAPTION_ID = "keyName";

        public VmwareDetailTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;
        }

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // サーバサイズ
            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(sizeSelect);

            // キーペア
            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setNullSelectionAllowed(false);
            keySelect.addContainerProperty(KEY_CAPTION_ID, String.class, null);
            keySelect.setItemCaptionPropertyId(KEY_CAPTION_ID);
            keySelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            // Windowsの場合はキーペアを無効にする
            if (StringUtils.startsWith(image.getImage().getOs(), PCCConstant.OS_NAME_WIN)) {
                keySelect.setEnabled(false);
            }
            form.getLayout().addComponent(keySelect);

            // クラスタ
            clusterSelect = new ComboBox(ViewProperties.getCaption("field.cluster"));
            clusterSelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(clusterSelect);

            addComponent(form);
        }

        private void loadData() {
            VmwareDescribeService vmwareDescribeService = BeanContext.getBean(VmwareDescribeService.class);
            Long platformNo = platform.getPlatform().getPlatformNo();

            // キーペア情報を取得
            List<VmwareKeyPair> vmwareKeyPairs = vmwareDescribeService.getKeyPairs(ViewContext.getUserNo(), platformNo);
            this.vmwareKeyPairs = vmwareKeyPairs;

            // クラスタ情報を取得
            List<ComputeResource> computeResources = vmwareDescribeService.getComputeResources(platformNo);
            List<String> clusters = new ArrayList<String>();
            for (ComputeResource computeResource : computeResources) {
                clusters.add(computeResource.getName());
            }
            this.clusters = clusters;
        }

        private void initValidation() {
            String message;

            message = ViewMessages.getMessage("IUI-000028");
            keySelect.setRequired(true);
            keySelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000034");
            clusterSelect.setRequired(true);
            clusterSelect.setRequiredError(message);
        }

        public void show() {
            // サーバサイズ
            for (String instanceType : image.getImageVmware().getInstanceTypes().split(",")) {
                sizeSelect.addItem(instanceType.trim());
            }
            sizeSelect.select(instance.getVmwareInstance().getInstanceType());

            // キーペア
            for (VmwareKeyPair vmwareKeyPair : vmwareKeyPairs) {
                Item item = keySelect.addItem(vmwareKeyPair.getKeyNo());
                item.getItemProperty(KEY_CAPTION_ID).setValue(vmwareKeyPair.getKeyName());
            }
            keySelect.select(instance.getVmwareInstance().getKeyPairNo());

            // クラスタ
            clusterSelect.setContainerDataSource(new IndexedContainer(clusters));
            clusterSelect.select(instance.getVmwareInstance().getComputeResource());
            if (StringUtils.isNotEmpty(instance.getVmwareInstance().getDatastore())) {
                clusterSelect.setEnabled(false);
            }

            // サーバが停止していない場合、詳細設定タブ自体を変更できないようにする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                form.setEnabled(false);
            }
        }

    }

    private class VmwareEditIpTab extends VerticalLayout {

        private InstanceDto instance;

        private Form form = new Form();

        private OptionGroup ipOptionGroup;

        private TextField ipAddressField;

        private TextField subnetMaskField;

        private TextField defaultGatewayField;

        private final String IP_OPTION_CAPTION_ID = "ipOption";

        private final String IP_OPTION_DHCP = "dhcp";

        private final String IP_OPTION_STATIC = "static";

        public VmwareEditIpTab(InstanceDto instance) {
            this.instance = instance;
        }

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // IPアドレス設定
            ipOptionGroup = new OptionGroup(ViewProperties.getCaption("field.optionIp"));
            ipOptionGroup.setNullSelectionAllowed(false);
            ipOptionGroup.setImmediate(true);
            ipOptionGroup.addContainerProperty(IP_OPTION_CAPTION_ID, String.class, null);
            ipOptionGroup.setItemCaptionPropertyId(IP_OPTION_CAPTION_ID);
            ipOptionGroup.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            Item ipOptionItem = ipOptionGroup.addItem(IP_OPTION_DHCP);
            ipOptionItem.getItemProperty(IP_OPTION_CAPTION_ID).setValue(ViewProperties.getCaption("field.dhcpIp"));
            Item ipOptionItem2 = ipOptionGroup.addItem(IP_OPTION_STATIC);
            ipOptionItem2.getItemProperty(IP_OPTION_CAPTION_ID).setValue(ViewProperties.getCaption("field.staticIp"));

            ipOptionGroup.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    String value = (String) event.getProperty().getValue();
                    if (IP_OPTION_DHCP.equals(value)) {
                        ipAddressField.setEnabled(false);
                        subnetMaskField.setEnabled(false);
                        defaultGatewayField.setEnabled(false);
                    } else {
                        ipAddressField.setEnabled(true);
                        subnetMaskField.setEnabled(true);
                        defaultGatewayField.setEnabled(true);
                    }
                }
            });
            form.getLayout().addComponent(ipOptionGroup);

            // IPアドレス
            ipAddressField = new TextField(ViewProperties.getCaption("field.ipAddress"));
            ipAddressField.setWidth("100%");
            form.getLayout().addComponent(ipAddressField);

            // サブネットマスク
            subnetMaskField = new TextField(ViewProperties.getCaption("field.subnetMask"));
            subnetMaskField.setWidth("100%");
            form.getLayout().addComponent(subnetMaskField);

            // デフォルトゲートウェイ
            defaultGatewayField = new TextField(ViewProperties.getCaption("field.defaultGateway"));
            defaultGatewayField.setWidth("100%");
            form.getLayout().addComponent(defaultGatewayField);

            addComponent(form);
        }

        private void initValidation() {
            String message;
            message = ViewMessages.getMessage("IUI-000094", ipAddressField.getCaption());
            Validator ipAddressFieldValidator = new RegexpValidator(
                    "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                    ViewMessages.getMessage("IUI-000095", ipAddressField.getCaption()));

            ipAddressField.setRequired(true);
            ipAddressField.setRequiredError(message);
            ipAddressField.addValidator(ipAddressFieldValidator);

            message = ViewMessages.getMessage("IUI-000094", subnetMaskField.getCaption());
            Validator subnetMaskFieldValidator = new RegexpValidator(
                    "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                    ViewMessages.getMessage("IUI-000095", subnetMaskField.getCaption()));
            subnetMaskField.setRequired(true);
            subnetMaskField.setRequiredError(message);
            subnetMaskField.addValidator(subnetMaskFieldValidator);

            message = ViewMessages.getMessage("IUI-000094", defaultGatewayField.getCaption());
            Validator defaultGatewayFieldValidator = new RegexpValidator(
                    "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                    ViewMessages.getMessage("IUI-000095", defaultGatewayField.getCaption()));
            defaultGatewayField.setRequired(true);
            defaultGatewayField.setRequiredError(message);
            defaultGatewayField.addValidator(defaultGatewayFieldValidator);
        }

        public void show() {
            VmwareAddress vmwareAddress = instance.getVmwareAddress();

            if (vmwareAddress == null) {
                ipOptionGroup.select(IP_OPTION_DHCP);
            } else {
                ipAddressField.setValue(vmwareAddress.getIpAddress());
                subnetMaskField.setValue(vmwareAddress.getSubnetMask());
                defaultGatewayField.setValue(vmwareAddress.getDefaultGateway());

                if (BooleanUtils.isTrue(vmwareAddress.getEnabled())) {
                    ipOptionGroup.select(IP_OPTION_STATIC);
                } else {
                    ipOptionGroup.select(IP_OPTION_DHCP);
                }
            }

            // サーバがStopped以外の場合は、変更不可とする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (InstanceStatus.STOPPED != status) {
                form.setEnabled(false);
            }
        }

    }

    private class NiftyDetailTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private Form form = new Form();

        private ComboBox sizeSelect;

        private ComboBox keySelect;

        private List<NiftyKeyPair> niftyKeyPairs;

        public NiftyDetailTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;
        }

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // サーバサイズ
            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(sizeSelect);

            // キーペア
            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(keySelect);

            addComponent(form);
        }

        private void loadData() {
            NiftyDescribeService niftyDescribeService = BeanContext.getBean(NiftyDescribeService.class);

            // キーペア情報を取得
            List<NiftyKeyPair> niftyKeyPairs = niftyDescribeService.getKeyPairs(ViewContext.getUserNo(), platform
                    .getPlatform().getPlatformNo());
            this.niftyKeyPairs = niftyKeyPairs;
        }

        private void initValidation() {
            String message;
            message = ViewMessages.getMessage("IUI-000027");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000028");
            keySelect.setRequired(true);
            keySelect.setRequiredError(message);
        }

        public void show() {
            // サーバサイズ
            for (String instanceType : image.getImageNifty().getInstanceTypes().split(",")) {
                sizeSelect.addItem(instanceType.trim());
            }
            sizeSelect.select(instance.getNiftyInstance().getInstanceType());

            // キーペア
            for (NiftyKeyPair niftyKeyPair : niftyKeyPairs) {
                keySelect.addItem(niftyKeyPair.getKeyName());
            }
            keySelect.select(instance.getNiftyKeyPair().getKeyName());

            // サーバが停止していない場合、詳細設定タブ自体を変更できないようにする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                form.setEnabled(false);
            }

            // サーバが既に作成済みの場合、キーペアを変更できないようにする            
            if (StringUtils.isNotEmpty(instance.getNiftyInstance().getInstanceId())) {
                keySelect.setEnabled(false);
            }
        }

    }

    private class CloudStackDetailTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private Form form = new Form();

        private ComboBox networkSelect;

        private ComboBox keySelect;

        private ComboBox grpSelect;

        private ComboBox sizeSelect;

        private ComboBox zoneSelect;

        private ComboBox elasticIpSelect;

        private List<String> keyPairs;

        private List<String> securityGroups;

        private List<String> instanceTypes;

        private List<ZoneDto> zones;

        private List<String> networks;

        private List<AddressDto> elasticIps;

        private final String ELASTIC_IP_CAPTION_ID = "ElasticIP";

        private final String ZONE_CAPTION_ID = "zoneName";

        private final AddressDto NULL_ADDRESS = new AddressDto();

        private final String COMBOBOX_WIDTH = "150px";

        private final String IP_COMBOBOX_WIDTH = "220px";

        private final String BUTTON_WIDTH = "150px";

        public CloudStackDetailTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;

            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            networkSelect = new ComboBox(ViewProperties.getCaption("field.netWork"));
            networkSelect.setWidth(COMBOBOX_WIDTH);
            networkSelect.setNullSelectionAllowed(false);

            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setWidth(COMBOBOX_WIDTH);
            keySelect.setNullSelectionAllowed(false);

            grpSelect = new ComboBox(ViewProperties.getCaption("field.securityGroup"));
            grpSelect.setWidth(COMBOBOX_WIDTH);
            grpSelect.setNullSelectionAllowed(false);

            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setWidth(COMBOBOX_WIDTH);
            sizeSelect.setNullSelectionAllowed(false);

            zoneSelect = new ComboBox(ViewProperties.getCaption("field.zone"));
            zoneSelect.setWidth(COMBOBOX_WIDTH);
            zoneSelect.setNullSelectionAllowed(false);
            zoneSelect.setItemCaptionPropertyId(ZONE_CAPTION_ID);
            zoneSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            elasticIpSelect = new ComboBox(ViewProperties.getCaption("field.elasticIp"));
            elasticIpSelect.setWidth(IP_COMBOBOX_WIDTH);
            elasticIpSelect.setNullSelectionAllowed(false);
            elasticIpSelect.setItemCaptionPropertyId(ELASTIC_IP_CAPTION_ID);
            elasticIpSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            Label spacer = new Label(" ");
            spacer.addStyleName("desc-padding-horizontal");
            spacer.setHeight("5px");

            form.getLayout().addComponent(zoneSelect);
            form.getLayout().addComponent(keySelect);
            form.getLayout().addComponent(sizeSelect);
            form.getLayout().addComponent(networkSelect);
            form.getLayout().addComponent(grpSelect);
            form.getLayout().addComponent(spacer);
            form.getLayout().addComponent(elasticIpSelect);

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setMargin(false);

            Button addElasticIp = new Button(ViewProperties.getCaption("button.addElasticIp"));
            addElasticIp.setDescription(ViewProperties.getCaption("description.addElasticIp"));
            addElasticIp.setIcon(Icons.ADD.resource());
            addElasticIp.setWidth(BUTTON_WIDTH);
            addElasticIp.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    addButtonClick();
                }
            });

            Button deleteElasticIp = new Button(ViewProperties.getCaption("button.deleteElasticIp"));
            deleteElasticIp.setDescription(ViewProperties.getCaption("description.deleteElasticIp"));
            deleteElasticIp.setIcon(Icons.DELETEMINI.resource());
            deleteElasticIp.setWidth(BUTTON_WIDTH);
            deleteElasticIp.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    deleteButtonClick();
                }
            });

            layout.addComponent(addElasticIp);
            layout.addComponent(deleteElasticIp);
            form.getLayout().addComponent(layout);

            addComponent(form);

            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                // サーバがStopped以外の場合は、詳細設定タブ自体を変更不可とする
                form.setEnabled(false);
            } else {
                // 既に作成済みの場合、いくつかの項目を変更不可とする
                if (StringUtils.isNotEmpty(instance.getCloudstackInstance().getInstanceId())) {
                    // 既に作成済みの場合、いくつかの項目を変更不可とする
                    grpSelect.setEnabled(false);
                    zoneSelect.setEnabled(false);
                    networkSelect.setEnabled(false);
                }
                elasticIpSelect.setEnabled(false);
                addElasticIp.setEnabled(false);
                deleteElasticIp.setEnabled(false);
            }
        }

        private void loadData() {
            // CloudStack情報を取得
            // 情報を取得
            // TODO: ロジックを必ずリファクタリングすること
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            List<KeyPairDto> infos = describeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatform()
                    .getPlatformNo());
            List<String> keyPairs = new ArrayList<String>();
            for (KeyPairDto info : infos) {
                keyPairs.add(info.getKeyName());
            }
            this.keyPairs = keyPairs;

            List<String> networks = new ArrayList<String>();
            for (String network : platform.getPlatformCloudstack().getNetworkId().split(",")) {
                networks.add(network);
            }
            this.networks = networks;

            List<String> securityGroups = new ArrayList<String>();
            if (StringUtils.isEmpty(instance.getCloudstackInstance().getNetworkid())) {
                List<SecurityGroupDto> groups = describeService.getSecurityGroups(ViewContext.getUserNo(), platform
                        .getPlatform().getPlatformNo(), null);
                for (SecurityGroupDto group : groups) {
                    securityGroups.add(group.getGroupName());
                }
            }
            this.securityGroups = securityGroups;

            List<String> instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageCloudstack().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }
            this.instanceTypes = instanceTypes;

            List<ZoneDto> zones = describeService.getAvailabilityZones(ViewContext.getUserNo(), platform.getPlatform()
                    .getPlatformNo());
            this.zones = zones;

            List<AddressDto> elasticIps = new ArrayList<AddressDto>();
            List<AddressDto> addresses = describeService.getAddresses(ViewContext.getUserNo(), platform.getPlatform()
                    .getPlatformNo());
            for (AddressDto address : addresses) {
                elasticIps.add(address);
            }
            this.elasticIps = elasticIps;
        }

        private void initValidation() {
            String message;
            message = ViewMessages.getMessage("IUI-000100");
            networkSelect.setRequired(true);
            networkSelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000027");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000050");
            zoneSelect.setRequired(true);
            zoneSelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000063");
            elasticIpSelect.setRequired(true);
            elasticIpSelect.setRequiredError(message);
        }

        public void show() {
            networkSelect.setContainerDataSource(new IndexedContainer(networks));
            networkSelect.select(instance.getCloudstackInstance().getNetworkid());

            keySelect.setContainerDataSource(new IndexedContainer(keyPairs));
            keySelect.select(instance.getCloudstackInstance().getKeyName());

            grpSelect.setContainerDataSource(new IndexedContainer(securityGroups));
            grpSelect.select(instance.getCloudstackInstance().getSecuritygroup());

            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getCloudstackInstance().getInstanceType());

            zoneSelect.setContainerDataSource(createZoneContainer());
            for (ZoneDto zone : zones) {
                if (zone.isSameById(instance.getCloudstackInstance().getZoneid())) {
                    zoneSelect.select(zone);
                }
            }
            //マッピングされたボリュームが存在する場合は変更不可
            if (instance.getCloudstackVolumes() != null && instance.getCloudstackVolumes().size() > 0) {
                zoneSelect.setEnabled(false);
            }

            elasticIpSelect.setContainerDataSource(createElasticIpContainer());
            if (null != instance.getCloudstackAddress()) {
                elasticIpSelect.select(new AddressDto((instance.getCloudstackAddress())));
            } else {
                elasticIpSelect.select(NULL_ADDRESS);
            }
        }

        private IndexedContainer createElasticIpContainer() {
            IndexedContainer elasticIpContainer = new IndexedContainer();
            elasticIpContainer.addContainerProperty(ELASTIC_IP_CAPTION_ID, String.class, null);

            String dynamic = ViewProperties.getCaption("field.elasticIp.dynamic");
            String associated = ViewProperties.getCaption("field.elasticIp.associated");

            // ElasticIP無しの項目
            Item item = elasticIpContainer.addItem(NULL_ADDRESS);
            item.getItemProperty(ELASTIC_IP_CAPTION_ID).setValue(dynamic);

            for (AddressDto address : elasticIps) {
                item = elasticIpContainer.addItem(address);

                //InstanceNoがNullならPool状態
                if (null == address.getInstanceNo()) {
                    item.getItemProperty(ELASTIC_IP_CAPTION_ID).setValue(address.getPublicIp());
                } else {
                    item.getItemProperty(ELASTIC_IP_CAPTION_ID).setValue(address.getPublicIp() + " " + associated);
                }
            }

            return elasticIpContainer;
        }

        private IndexedContainer createZoneContainer() {
            IndexedContainer elasticIpContainer = new IndexedContainer();
            elasticIpContainer.addContainerProperty(ZONE_CAPTION_ID, String.class, null);

            for (ZoneDto cluster : zones) {
                Item item = elasticIpContainer.addItem(cluster);
                item.getItemProperty(ZONE_CAPTION_ID).setValue(cluster.getZoneName());
            }

            return elasticIpContainer;
        }

        private void addButtonClick() {
            // ElasticIPを取得する
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            Long addressNo = describeService.createAddress(ViewContext.getUserNo(), instance.getInstance()
                    .getPlatformNo());

            //ElasticIPをリセット
            resetElasticIps();

            // 取得したAddressを抽出する
            AddressDto address = null;
            for (AddressDto tmpAddress : elasticIps) {
                if (tmpAddress.getAddressNo().equals(addressNo)) {
                    address = tmpAddress;
                    break;
                }
            }

            // 取得したElasticIPを選択状態にする
            elasticIpSelect.select(address);

            //取得したPublicIPを表示する
            String message = ViewMessages.getMessage("IUI-000061", address.getPublicIp());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OK);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        private void deleteButtonClick() {
            final AddressDto address = (AddressDto) elasticIpSelect.getValue();

            // ElasticIPが選択されていない場合
            if (address == null || NULL_ADDRESS.equals(address)) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                        ViewMessages.getMessage("IUI-000062"));
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            //すでに設定されているElasticIPでなく、かつ割り当て済の場合は削除できない
            if (null != address.getInstanceNo()) {
                if (null == instance.getCloudstackAddress()
                        || !instance.getCloudstackAddress().getAddressNo().equals(address.getAddressNo())) {
                    String message = ViewMessages.getMessage("IUI-000064");
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            String message = ViewMessages.getMessage("IUI-000060", address.getPublicIp());
            DialogConfirm dialogConfirm = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialogConfirm.setCallback(new Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result == Result.OK) {
                        // ElasticIPを削除する
                        IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
                        describeService.deleteAddress(address.getUserNo(), address.getPlatformNo(),
                                address.getAddressNo());

                        //ElasticIPをリセット
                        resetElasticIps();

                        // 動的なIPを選択状態にする
                        elasticIpSelect.select(NULL_ADDRESS);
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialogConfirm);
        }

        private void resetElasticIps() {
            // Addressの情報を取り直す
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            List<AddressDto> addresses = describeService.getAddresses(ViewContext.getUserNo(), instance.getInstance()
                    .getPlatformNo());

            elasticIps = addresses;
            elasticIpSelect.setContainerDataSource(createElasticIpContainer());
        }

    }

    private class VcloudDetailTab extends VerticalLayout {

        private final String CID_STORAGE_TYPE = "StorageType";

        private final String CID_KEY_PAIR = "KeyPair";

        private final String WIDTH_COMBOBOX = "220px";

        private final String KEY_PAIR_WIDTH_COMBOBOX = "150px";

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private Form form = new Form();

        private ComboBox storageTypeSelect;

        private ComboBox sizeSelect;

        private ComboBox keySelect;

        private DataDiskTable dataDiskTable;

        private DataDiskTableButtons dataDiskTableButtons;

        private List<String> instanceTypes;

        private List<KeyPairDto> vcloudKeyPairs;

        private List<StorageTypeDto> storageTypes;

        private List<DataDiskDto> dataDisks;

        public VcloudDetailTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;

            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            //ストレージタイプ
            storageTypeSelect = new ComboBox(ViewProperties.getCaption("field.storageType"));
            storageTypeSelect.setWidth(WIDTH_COMBOBOX);
            storageTypeSelect.setNullSelectionAllowed(false);
            storageTypeSelect.setItemCaptionPropertyId(CID_STORAGE_TYPE);
            storageTypeSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            //サーバサイズ
            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setWidth(WIDTH_COMBOBOX);
            sizeSelect.setNullSelectionAllowed(false);

            //キーペア
            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setWidth(KEY_PAIR_WIDTH_COMBOBOX);
            keySelect.setNullSelectionAllowed(false);
            keySelect.setItemCaptionPropertyId(CID_KEY_PAIR);
            keySelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            // Windowsの場合はキーペアを無効にする
            if (StringUtils.startsWith(image.getImage().getOs(), PCCConstant.OS_NAME_WIN)) {
                keySelect.setEnabled(false);
            }

            Label spacer = new Label(" ");
            spacer.addStyleName("desc-padding-horizontal");
            spacer.setHeight("5px");

            //データディスクテーブル
            dataDiskTable = new DataDiskTable();

            //データディスクボタン
            dataDiskTableButtons = new DataDiskTableButtons();

            form.getLayout().addComponent(storageTypeSelect);
            form.getLayout().addComponent(sizeSelect);
            form.getLayout().addComponent(keySelect);
            form.getLayout().addComponent(spacer);
            form.getLayout().addComponent(dataDiskTable);
            form.getLayout().addComponent(dataDiskTableButtons);

            addComponent(form);

            // サーバがStopped以外の場合は、変更不可とする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                storageTypeSelect.setEnabled(false);
                sizeSelect.setEnabled(false);
                keySelect.setEnabled(false);
            }
        }

        private void loadData() {
            // VCloud情報を取得
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            //StorageType
            List<StorageTypeDto> storageTypes = describeService.getStorageTypes(ViewContext.getUserNo(), platform
                    .getPlatform().getPlatformNo());
            this.storageTypes = storageTypes;

            //KeyPair
            List<KeyPairDto> vcloudKeyPairs = describeService.getKeyPairs(ViewContext.getUserNo(), platform
                    .getPlatform().getPlatformNo());
            this.vcloudKeyPairs = vcloudKeyPairs;

            //InstanceType
            List<String> instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageVcloud().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }
            this.instanceTypes = instanceTypes;

            //DataDisk
            List<DataDiskDto> dataDisks = new ArrayList<DataDiskDto>();
            List<VcloudDisk> vcloudDisks = instance.getVcloudDisks();
            for (VcloudDisk vcloudDisk : vcloudDisks) {
                if (BooleanUtils.isTrue(vcloudDisk.getDataDisk())) {
                    DataDiskDto diskDto = new DataDiskDto();
                    diskDto.setDiskNo(vcloudDisk.getDiskNo());
                    diskDto.setDiskSize(vcloudDisk.getSize());
                    diskDto.setUnitNo(vcloudDisk.getUnitNo());
                    dataDisks.add(diskDto);
                }
            }
            this.dataDisks = dataDisks;
        }

        public void show() {
            //ストレージタイプ
            StorageTypeDto selectedStorageType = null;
            for (StorageTypeDto storageTypeDto : storageTypes) {
                if (storageTypeDto.getStorageTypeNo().equals(instance.getVcloudInstance().getStorageTypeNo())) {
                    selectedStorageType = storageTypeDto;
                    break;
                }
            }
            storageTypeSelect.setContainerDataSource(createStorageTypeContainer());
            storageTypeSelect.select(selectedStorageType.getStorageTypeNo());

            //インスタンスタイプ
            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getVcloudInstance().getInstanceType());

            //キーペア
            KeyPairDto selectedKeyPair = null;
            for (KeyPairDto keyPairDto : vcloudKeyPairs) {
                if (keyPairDto.getKeyNo().equals(instance.getVcloudInstance().getKeyPairNo())) {
                    selectedKeyPair = keyPairDto;
                    break;
                }
            }
            keySelect.setContainerDataSource(createKeyPairContainer());
            keySelect.select(selectedKeyPair.getKeyNo());

            //データディスク
            dataDiskTable.show();
        }

        private IndexedContainer createKeyPairContainer() {
            IndexedContainer keyPairContainer = new IndexedContainer();
            keyPairContainer.addContainerProperty(CID_KEY_PAIR, String.class, null);

            for (KeyPairDto keyPairDto : vcloudKeyPairs) {
                Item item = keyPairContainer.addItem(keyPairDto.getKeyNo());
                item.getItemProperty(CID_KEY_PAIR).setValue(keyPairDto.getKeyName());
            }

            return keyPairContainer;
        }

        private IndexedContainer createStorageTypeContainer() {
            IndexedContainer storageTypeContainer = new IndexedContainer();
            storageTypeContainer.addContainerProperty(CID_STORAGE_TYPE, String.class, null);

            for (StorageTypeDto storageTypeDto : storageTypes) {
                Item item = storageTypeContainer.addItem(storageTypeDto.getStorageTypeNo());
                item.getItemProperty(CID_STORAGE_TYPE).setValue(storageTypeDto.getStorageTypeName());
            }

            return storageTypeContainer;
        }

        private void initValidation() {
            String message;
            //ストレージタイプ
            message = ViewMessages.getMessage("IUI-000123");
            storageTypeSelect.setRequired(true);
            storageTypeSelect.setRequiredError(message);

            //インスタンスタイプ
            message = ViewMessages.getMessage("IUI-000027");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);

            //キーペア
            message = ViewMessages.getMessage("IUI-000028");
            keySelect.setRequired(true);
            keySelect.setRequiredError(message);
        }

        private class DataDiskTable extends Table {

            private final String PID_UNIT_NO = "UnitNo";

            private final String PID_DISK_SIZE = "DiskSize";

            private final int WIDTH_UNIT_NO = 194;

            private final int WIDTH_DISK_SIZE = 194;

            public DataDiskTable() {
                //テーブル基本設定
                setCaption(ViewProperties.getCaption("table.diskData"));
                setWidth("100%");
                setPageLength(3);
                setSortDisabled(true);
                setColumnHeaderMode(COLUMN_HEADER_MODE_EXPLICIT);
                setColumnReorderingAllowed(false);
                setColumnCollapsingAllowed(false);
                setSelectable(true);
                setMultiSelect(false);
                setNullSelectionAllowed(false);
                setImmediate(true);
                addStyleName("win-server-edit-datadisk");

                //カラム設定
                addContainerProperty(PID_UNIT_NO, String.class, null);
                addContainerProperty(PID_DISK_SIZE, Integer.class, null);

                //ヘッダー設定
                setColumnHeaders(new String[] { ViewProperties.getCaption("field.unitNo"),
                        ViewProperties.getCaption("field.diskSize") });

                //ヘッダーサイズ設定
                setColumnWidth(PID_UNIT_NO, WIDTH_UNIT_NO);
                setColumnWidth(PID_DISK_SIZE, WIDTH_DISK_SIZE);

                //テーブルのカラムに対してStyleNameを設定
                setCellStyleGenerator(new StandardCellStyleGenerator());

                // 行が選択されたときのイベント
                addListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        DataDiskDto selectDto = (DataDiskDto) getValue();
                        if (selectDto == null) {
                            dataDiskTableButtons.btnEdit.setEnabled(false);
                            dataDiskTableButtons.btnDelete.setEnabled(false);
                        } else {
                            dataDiskTableButtons.btnEdit.setEnabled(true);
                            dataDiskTableButtons.btnDelete.setEnabled(true);
                        }
                    }
                });
            }

            private void loadData() {
                //InstanceDto取得
                //ただし、変数「instance」は変更せず、
                InstanceService instanceService = BeanContext.getBean(InstanceService.class);
                InstanceDto instanceDto = instanceService.getInstance(instance.getInstance().getInstanceNo());

                //DataDisk
                dataDisks = new ArrayList<DataDiskDto>();
                List<VcloudDisk> vcloudDisks = instanceDto.getVcloudDisks();
                for (VcloudDisk vcloudDisk : vcloudDisks) {
                    if (BooleanUtils.isTrue(vcloudDisk.getDataDisk())) {
                        DataDiskDto diskDto = new DataDiskDto();
                        diskDto.setDiskNo(vcloudDisk.getDiskNo());
                        diskDto.setDiskSize(vcloudDisk.getSize());
                        diskDto.setUnitNo(vcloudDisk.getUnitNo());
                        dataDisks.add(diskDto);
                    }
                }
            }

            public void show() {
                removeAllItems();
                for (DataDiskDto diskDto : dataDisks) {
                    String unitNo = ViewProperties.getCaption("field.unattached");
                    if (diskDto.getUnitNo() != null) {
                        unitNo = String.valueOf(diskDto.getUnitNo());
                    }
                    addItem(new Object[] { unitNo, diskDto.getDiskSize() }, diskDto);
                }
            }

        }

        private class DataDiskTableButtons extends HorizontalLayout {

            private Button btnAdd;

            private Button btnEdit;

            private Button btnDelete;

            public DataDiskTableButtons() {
                setMargin(false);
                setSpacing(true);

                //Addボタン
                btnAdd = new Button(ViewProperties.getCaption("button.add"));
                btnAdd.setIcon(Icons.ADD.resource());
                btnAdd.setDescription(ViewProperties.getCaption("description.add"));
                btnAdd.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        addButtonClick(event);
                    }
                });

                //Editボタン
                btnEdit = new Button(ViewProperties.getCaption("button.edit"));
                btnEdit.setIcon(Icons.EDITMINI.resource());
                btnEdit.setDescription(ViewProperties.getCaption("description.edit"));
                btnEdit.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        editButtonClick(event);
                    }
                });
                btnEdit.setEnabled(false);

                //Deleteボタン
                btnDelete = new Button(ViewProperties.getCaption("button.delete"));
                btnDelete.setIcon(Icons.DELETEMINI.resource());
                btnDelete.setDescription(ViewProperties.getCaption("description.delete"));
                btnDelete.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        deleteButtonClick(event);
                    }
                });
                btnDelete.setEnabled(false);

                addComponent(btnAdd);
                addComponent(btnEdit);
                addComponent(btnDelete);

                setComponentAlignment(btnAdd, Alignment.MIDDLE_LEFT);
                setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);
                setComponentAlignment(btnDelete, Alignment.MIDDLE_LEFT);
            }

            private void addButtonClick(ClickEvent event) {
                WinServerDataDiskConfig winServerDataDiskConfig = new WinServerDataDiskConfig(getApplication(),
                        instance.getInstance().getInstanceNo(), null);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        dataDiskTable.loadData();
                        dataDiskTable.show();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            private void editButtonClick(ClickEvent event) {
                DataDiskDto dataDiskDto = (DataDiskDto) dataDiskTable.getValue();
                WinServerDataDiskConfig winServerDataDiskConfig = new WinServerDataDiskConfig(getApplication(),
                        instance.getInstance().getInstanceNo(), dataDiskDto);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        dataDiskTable.loadData();
                        dataDiskTable.show();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            private void deleteButtonClick(ClickEvent event) {
                String message = ViewMessages.getMessage("IUI-000124");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                        Buttons.OKCancel);
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onDialogResult(Result result) {
                        if (result != Result.OK) {
                            return;
                        }
                        //ディスク削除
                        // 更新処理 & IaasGateWay処理(ディスクデタッチ)
                        DataDiskDto dataDiskDto = (DataDiskDto) dataDiskTable.getValue();
                        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
                        instanceService.detachDataDisk(instance.getInstance().getInstanceNo(), dataDiskDto.getDiskNo());

                        //データ取得&テーブル再表示
                        dataDiskTable.loadData();
                        dataDiskTable.show();
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            }

        }

    }

    private class VcloudNetworkTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private Form form = new Form();

        private NetworkTable networkTable;

        private NetworkTableButtons networkTableButtons;

        private Map<String, NetworkDto> networkMap;

        private List<InstanceNetworkDto> instanceNetworks;

        public VcloudNetworkTab(InstanceDto instance, PlatformDto platform) {
            this.instance = instance;
            this.platform = platform;

            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            //テーブル
            networkTable = new NetworkTable();
            form.getLayout().addComponent(networkTable);

            //ボタン
            networkTableButtons = new NetworkTableButtons();
            form.getLayout().addComponent(networkTableButtons);

            addComponent(form);

            // サーバがStopped以外の場合は、変更不可とする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                form.setEnabled(false);
            }
        }

        private void loadData() {
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);

            //Network
            List<NetworkDto> networkDtos = describeService.getNetworks(ViewContext.getUserNo(), platform.getPlatform()
                    .getPlatformNo());
            Map<String, NetworkDto> networkMap = new HashMap<String, NetworkDto>();
            for (NetworkDto networkDto : networkDtos) {
                networkMap.put(networkDto.getNetworkName(), networkDto);
            }
            this.networkMap = networkMap;

            //InstanceNetwork
            List<InstanceNetworkDto> instanceNetworks = new ArrayList<InstanceNetworkDto>();
            List<VcloudInstanceNetwork> tmpInstanceNetworks = instance.getVcloudInstanceNetworks();
            for (VcloudInstanceNetwork instanceNetwork : tmpInstanceNetworks) {
                InstanceNetworkDto instanceNetworkDto = new InstanceNetworkDto();
                instanceNetworkDto.setNetworkNo(instanceNetwork.getNetworkNo());
                instanceNetworkDto.setNetworkName(instanceNetwork.getNetworkName());
                instanceNetworkDto.setNew(false);
                instanceNetworkDto.setDelete(false);
                instanceNetworkDto.setIpMode(instanceNetwork.getIpMode());
                instanceNetworkDto.setIpAddress(instanceNetwork.getIpAddress());
                instanceNetworkDto.setRequired(networkMap.get(instanceNetwork.getNetworkName()).isPcc());
                instanceNetworkDto.setPrimary(BooleanUtils.isTrue(instanceNetwork.getIsPrimary()));
                instanceNetworks.add(instanceNetworkDto);
            }
            this.instanceNetworks = instanceNetworks;
        }

        public void show() {
            networkTable.show();
        }

        private class NetworkTable extends Table {

            private final String PID_NETWORK_NAME = "NetworkName";

            private final String PID_IP_MODE = "IpMode";

            private final String PID_IP_ADDRESS = "IpAddress";

            private final String PID_PRIMARY = "Primary";

            private final int WIDTH_NETWORK_NAME = 180;

            private final int WIDTH_IP_MOD = 114;

            private final int WIDTH_IP_ADDRESS = 97;

            private final int WIDTH_PRIMARY = 67;

            public NetworkTable() {
                //テーブル基本設定
                setWidth("100%");
                setPageLength(3);
                setSortDisabled(true);
                setColumnHeaderMode(COLUMN_HEADER_MODE_EXPLICIT);
                setColumnReorderingAllowed(false);
                setColumnCollapsingAllowed(false);
                setSelectable(true);
                setMultiSelect(false);
                setNullSelectionAllowed(false);
                setImmediate(true);
                addStyleName("win-server-edit-network");

                //カラム設定
                addContainerProperty(PID_NETWORK_NAME, String.class, null);
                addContainerProperty(PID_IP_MODE, String.class, null);
                addContainerProperty(PID_IP_ADDRESS, String.class, null);
                addContainerProperty(PID_PRIMARY, Label.class, null);

                //ヘッダー設定
                setColumnHeaders(new String[] { ViewProperties.getCaption("field.networkName"),
                        ViewProperties.getCaption("field.ipMode"), ViewProperties.getCaption("field.ipAddress"),
                        ViewProperties.getCaption("field.primary") });

                //ヘッダーサイズ設定
                setColumnWidth(PID_NETWORK_NAME, WIDTH_NETWORK_NAME);
                setColumnWidth(PID_IP_MODE, WIDTH_IP_MOD);
                setColumnWidth(PID_IP_ADDRESS, WIDTH_IP_ADDRESS);
                setColumnWidth(PID_PRIMARY, WIDTH_PRIMARY);

                //テーブルのカラムに対してStyleNameを設定
                setCellStyleGenerator(new StandardCellStyleGenerator());

                // 行が選択されたときのイベント
                addListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        InstanceNetworkDto selectDto = (InstanceNetworkDto) getValue();
                        if (selectDto == null) {
                            networkTableButtons.btnEdit.setEnabled(false);
                            networkTableButtons.btnDelete.setEnabled(false);
                        } else {
                            networkTableButtons.btnEdit.setEnabled(true);
                            //PCCネットワークまたはプライマリの場合は削除不可
                            networkTableButtons.btnDelete
                                    .setEnabled((!selectDto.isRequired() && !selectDto.isPrimary()));
                        }
                    }
                });
            }

            private void loadData() {
                //InstanceDto取得
                //ただし、変数「instance」は変更せず、
                IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
                InstanceService instanceService = BeanContext.getBean(InstanceService.class);
                InstanceDto instanceDto = instanceService.getInstance(instance.getInstance().getInstanceNo());

                //Network
                networkMap = new HashMap<String, NetworkDto>();
                List<NetworkDto> networkDtos = describeService.getNetworks(ViewContext.getUserNo(), instanceDto
                        .getInstance().getPlatformNo());
                for (NetworkDto networkDto : networkDtos) {
                    networkMap.put(networkDto.getNetworkName(), networkDto);
                }
            }

            public void show() {
                removeAllItems();
                for (int i = 0; i < instanceNetworks.size(); i++) {
                    InstanceNetworkDto instanceNetwork = instanceNetworks.get(i);
                    if (instanceNetwork.isDelete()) {
                        //削除対象ネットワーク
                        continue;
                    }
                    NetworkDto network = networkMap.get(instanceNetwork.getNetworkName());
                    String ipModeName = null;
                    if ("POOL".equals(instanceNetwork.getIpMode())) {
                        ipModeName = ViewProperties.getCaption("field.ipMode.pool");
                    } else if ("MANUAL".equals(instanceNetwork.getIpMode())) {
                        ipModeName = ViewProperties.getCaption("field.ipMode.manual");
                    } else if ("DHCP".equals(instanceNetwork.getIpMode())) {
                        ipModeName = ViewProperties.getCaption("field.ipMode.dhcp");
                    }
                    Label slbl = new Label("");
                    if (BooleanUtils.isTrue(instanceNetwork.isPrimary())) {
                        slbl = new Label(IconUtils.createImageTag(getApplication(), Icons.SELECTMINI),
                                Label.CONTENT_XHTML);
                    }
                    addItem(new Object[] { network.getNetworkName(), ipModeName, instanceNetwork.getIpAddress(), slbl },
                            instanceNetwork);
                }
            }

        }

        private class NetworkTableButtons extends HorizontalLayout {

            private Button btnAdd;

            private Button btnEdit;

            private Button btnDelete;

            public NetworkTableButtons() {
                setMargin(false);
                setSpacing(true);

                //Addボタン
                btnAdd = new Button(ViewProperties.getCaption("button.add"));
                btnAdd.setIcon(Icons.ADD.resource());
                btnAdd.setDescription(ViewProperties.getCaption("description.add"));
                btnAdd.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        addButtonClick(event);
                    }
                });

                //Editボタン
                btnEdit = new Button(ViewProperties.getCaption("button.edit"));
                btnEdit.setIcon(Icons.EDITMINI.resource());
                btnEdit.setDescription(ViewProperties.getCaption("description.edit"));
                btnEdit.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        editButtonClick(event);
                    }
                });
                btnEdit.setEnabled(false);

                //Deleteボタン
                btnDelete = new Button(ViewProperties.getCaption("button.delete"));
                btnDelete.setIcon(Icons.DELETEMINI.resource());
                btnDelete.setDescription(ViewProperties.getCaption("description.delete"));
                btnDelete.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        deleteButtonClick(event);
                    }
                });
                btnDelete.setEnabled(false);

                addComponent(btnAdd);
                addComponent(btnEdit);
                addComponent(btnDelete);

                setComponentAlignment(btnAdd, Alignment.MIDDLE_LEFT);
                setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);
                setComponentAlignment(btnDelete, Alignment.MIDDLE_LEFT);
            }

            private void addButtonClick(ClickEvent event) {
                WinServerNetworkConfig winServerDataDiskConfig = new WinServerNetworkConfig(getApplication(), instance
                        .getInstance().getInstanceNo(), instance.getInstance().getPlatformNo(), null, instanceNetworks);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        //テーブル再表示
                        networkTable.loadData();
                        networkTable.show();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            private void editButtonClick(ClickEvent event) {
                InstanceNetworkDto instanceNetwork = (InstanceNetworkDto) networkTable.getValue();
                WinServerNetworkConfig winServerDataDiskConfig = new WinServerNetworkConfig(getApplication(), instance
                        .getInstance().getInstanceNo(), instance.getInstance().getPlatformNo(), instanceNetwork,
                        instanceNetworks);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        //テーブル再表示
                        networkTable.loadData();
                        networkTable.show();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            private void deleteButtonClick(ClickEvent event) {
                String message = ViewMessages.getMessage("IUI-000127");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                        Buttons.OKCancel);
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onDialogResult(Result result) {
                        if (result != Result.OK) {
                            return;
                        }
                        //ディスク削除
                        InstanceNetworkDto instanceNetwork = (InstanceNetworkDto) networkTable.getValue();
                        if (instanceNetwork.isNew()) {
                            //VCloudInstanceNetworkにレコードが存在しない場合(新規追加→削除)
                            instanceNetworks.remove(instanceNetwork);
                        } else {
                            //VCloudInstanceNetworkにレコードが存在する場合(削除)
                            instanceNetwork.setDelete(true);
                        }

                        //テーブル再表示
                        networkTable.loadData();
                        networkTable.show();
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            }

        }

    }

    private class AzureDetailTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private Form form = new Form();

        private ComboBox sizeSelect;

        private ComboBox availabilitySetSelect;

        private TextField locationField;

        private TextField affinityField;

        private TextField cloudServiceField;

        private ComboBox subnetSelect;

        private TextField storageAccountField;

        private List<String> instanceTypes;

        private List<SubnetDto> subnets;

        private List<String> availabilitySets;

        private final String COMBOBOX_WIDTH = "150px";

        private final String TEXT_WIDTH = "150px";

        private final String CIDR_BLOCK_CAPTION_ID = "cidrBlock";

        public AzureDetailTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;

            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setWidth(COMBOBOX_WIDTH);
            sizeSelect.setNullSelectionAllowed(false);

            availabilitySetSelect = new ComboBox(ViewProperties.getCaption("field.availabilitySet"));
            availabilitySetSelect.setWidth(COMBOBOX_WIDTH);
            availabilitySetSelect.setNullSelectionAllowed(false);

            locationField = new TextField(ViewProperties.getCaption("field.location"));
            locationField.setImmediate(true);
            locationField.setWidth(TEXT_WIDTH);

            affinityField = new TextField(ViewProperties.getCaption("field.affinityGroup"));
            affinityField.setImmediate(true);
            affinityField.setWidth(TEXT_WIDTH);

            cloudServiceField = new TextField(ViewProperties.getCaption("field.cloudService"));
            cloudServiceField.setImmediate(true);
            cloudServiceField.setWidth(TEXT_WIDTH);

            subnetSelect = new ComboBox(ViewProperties.getCaption("field.subnet"));
            subnetSelect.setImmediate(true);
            subnetSelect.setWidth(COMBOBOX_WIDTH);
            subnetSelect.setNullSelectionAllowed(false);
            subnetSelect.setItemCaptionPropertyId(CIDR_BLOCK_CAPTION_ID);
            subnetSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            storageAccountField = new TextField(ViewProperties.getCaption("field.storageAccount"));
            storageAccountField.setImmediate(true);
            storageAccountField.setWidth(COMBOBOX_WIDTH);

            Label spacer = new Label(" ");
            spacer.addStyleName("desc-padding-horizontal");
            spacer.setHeight("5px");

            form.getLayout().addComponent(sizeSelect);
            form.getLayout().addComponent(availabilitySetSelect);
            form.getLayout().addComponent(subnetSelect);
            form.getLayout().addComponent(spacer);

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setMargin(false);

            form.getLayout().addComponent(layout);

            addComponent(form);

            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                // サーバがStopped以外の場合は、詳細設定タブ自体を変更不可とする
                form.setEnabled(false);
            } else {
                // 停止時は、いくつかの項目を変更不可とする
                locationField.setEnabled(false);
                // サーバが作成済みのとき、変更不可
                if (StringUtils.isNotEmpty(instance.getAzureInstance().getInstanceName())) {
                    subnetSelect.setEnabled(false);
                    // TODO 可用性セットが設定済みの場合も変更可能かもしれないが、
                    // 現段階では、APIから可用性セットの情報を取得できないのでサーバー作成済みの場合、変更不可とする
                    availabilitySetSelect.setEnabled(false);
                }
            }
        }

        private void loadData() {
            // Azure情報を取得
            // 情報を取得
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);

            List<String> instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageAzure().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }
            this.instanceTypes = instanceTypes;

            // 可用性セット
            List<String> availabilitySets = new ArrayList<String>();
            for (String availabilitySet : platform.getPlatformAzure().getAvailabilitySets().split(",")) {
                availabilitySets.add(availabilitySet.trim());
            }
            this.availabilitySets = availabilitySets;

            //サブネット
            List<SubnetDto> subnets = describeService.getAzureSubnets(ViewContext.getUserNo(), platform.getPlatform()
                    .getPlatformNo(), platform.getPlatformAzure().getNetworkName());
            this.subnets = subnets;
        }

        private void initValidation() {
            String message;

            message = ViewMessages.getMessage("IUI-000027");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000129");
            locationField.setRequired(true);
            locationField.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000130");
            affinityField.setRequired(true);
            affinityField.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000131");
            cloudServiceField.setRequired(true);
            cloudServiceField.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000108");
            subnetSelect.setRequired(true);
            subnetSelect.setRequiredError(message);

            message = ViewMessages.getMessage("IUI-000132");
            storageAccountField.setRequired(true);
            storageAccountField.setRequiredError(message);
        }

        private void show() {
            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getAzureInstance().getInstanceType());

            availabilitySetSelect.setContainerDataSource(new IndexedContainer(availabilitySets));
            availabilitySetSelect.select(instance.getAzureInstance().getAvailabilitySet());

            subnetSelect.setContainerDataSource(createSubnetContainer());
            for (SubnetDto subnetDto : subnets) {
                if (subnetDto.getSubnetId().equals(instance.getAzureInstance().getSubnetId())) {
                    subnetSelect.select(subnetDto);
                    break;
                }
            }
        }

        private IndexedContainer createSubnetContainer() {
            IndexedContainer subnetContainer = new IndexedContainer();
            subnetContainer.addContainerProperty(CIDR_BLOCK_CAPTION_ID, String.class, null);

            for (SubnetDto subnetDto : subnets) {
                Item item = subnetContainer.addItem(subnetDto);
                item.getItemProperty(CIDR_BLOCK_CAPTION_ID).setValue(subnetDto.getCidrBlock());
            }

            return subnetContainer;
        }

    }

    private class OpenStackDetailTab extends VerticalLayout {

        private InstanceDto instance;

        private PlatformDto platform;

        private ImageDto image;

        private Form form = new Form();

        private ComboBox sizeSelect;

        private ComboBox zoneSelect;

        private ComboBox grpSelect;

        private ComboBox keySelect;

        private List<String> keyPairs;

        private List<String> securityGroups;

        private List<String> instanceTypes;

        private List<ZoneDto> zones;

        private final String ZONE_CAPTION_ID = "zoneName";

        private final String COMBOBOX_WIDTH = "150px";

        public OpenStackDetailTab(InstanceDto instance, PlatformDto platform, ImageDto image) {
            this.instance = instance;
            this.platform = platform;
            this.image = image;

            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setWidth(COMBOBOX_WIDTH);
            sizeSelect.setNullSelectionAllowed(false);

            zoneSelect = new ComboBox(ViewProperties.getCaption("field.zone"));
            zoneSelect.setWidth(COMBOBOX_WIDTH);
            zoneSelect.setNullSelectionAllowed(false);
            zoneSelect.setItemCaptionPropertyId(ZONE_CAPTION_ID);
            zoneSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            grpSelect = new ComboBox(ViewProperties.getCaption("field.securityGroup"));
            grpSelect.setWidth(COMBOBOX_WIDTH);
            grpSelect.setImmediate(true);
            grpSelect.setNullSelectionAllowed(false);

            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setWidth(COMBOBOX_WIDTH);
            keySelect.setNullSelectionAllowed(false);

            Label spacer = new Label(" ");
            spacer.addStyleName("desc-padding-horizontal");
            spacer.setHeight("5px");

            form.getLayout().addComponent(sizeSelect);
            form.getLayout().addComponent(zoneSelect);
            form.getLayout().addComponent(grpSelect);
            form.getLayout().addComponent(keySelect);
            form.getLayout().addComponent(spacer);

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setMargin(false);

            form.getLayout().addComponent(layout);

            addComponent(form);

            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                // サーバがStopped以外の場合は、詳細設定タブ自体を変更不可とする
                form.setEnabled(false);
            } else {
                // 停止時は、いくつかの項目を変更不可とする
                if (StringUtils.isNotEmpty(instance.getOpenstackInstance().getInstanceId())) {
                    // 一度でも起動した場合、項目を変更不可とする
                    form.setEnabled(false);
                }
            }
        }

        private void loadData() {
            // 情報を取得
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            //instanceTypes
            List<String> instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageOpenstack().getInstanceTypes().split(",")) {
                //IDで取得されるため、今後名称に変換する必要有り
                instanceTypes.add(instanceType.trim());
            }
            this.instanceTypes = instanceTypes;

            // Availablility Zone
            List<ZoneDto> zones = describeService.getAvailabilityZones(ViewContext.getUserNo(), platform.getPlatform()
                    .getPlatformNo());
            this.zones = zones;

            //セキュリティグループ
            List<String> securityGroups = new ArrayList<String>();
            List<SecurityGroupDto> groups;
            groups = describeService.getSecurityGroups(ViewContext.getUserNo(), platform.getPlatform().getPlatformNo(),
                    null);
            for (SecurityGroupDto group : groups) {
                securityGroups.add(group.getGroupName());
            }
            this.securityGroups = securityGroups;

            //キーペア
            List<KeyPairDto> infos = describeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatform()
                    .getPlatformNo());
            List<String> keyPairs = new ArrayList<String>();
            for (KeyPairDto info : infos) {
                keyPairs.add(info.getKeyName());
            }
            this.keyPairs = keyPairs;
        }

        private void initValidation() {
            String message;

            message = ViewMessages.getMessage("IUI-000027");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);
        }

        public void show() {
            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getOpenstackInstance().getInstanceType());

            zoneSelect.setContainerDataSource(createZoneContainer());
            for (ZoneDto zoneDto : zones) {
                if (StringUtils.equals(zoneDto.getZoneName(), instance.getOpenstackInstance().getAvailabilityZone())) {
                    zoneSelect.select(zoneDto);
                    break;
                }
            }
            if (instance.getOpenstackVolumes() != null && instance.getOpenstackVolumes().size() > 0) {
                //ボリュームが存在する場合は編集不可
                zoneSelect.setEnabled(false);
            }

            grpSelect.setContainerDataSource(new IndexedContainer(securityGroups));
            grpSelect.select(instance.getOpenstackInstance().getSecurityGroups());

            keySelect.setContainerDataSource(new IndexedContainer(keyPairs));
            keySelect.select(instance.getOpenstackInstance().getKeyName());
        }

        private IndexedContainer createZoneContainer() {
            IndexedContainer zoneContainer = new IndexedContainer();
            zoneContainer.addContainerProperty(ZONE_CAPTION_ID, String.class, null);

            for (ZoneDto zoneDto : zones) {
                Item item = zoneContainer.addItem(zoneDto);
                item.getItemProperty(ZONE_CAPTION_ID).setValue(zoneDto.getZoneName());
            }

            return zoneContainer;
        }

    }

    private void loadData() {
        // サーバ情報を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        this.instance = instanceService.getInstance(instanceNo);

        // プラットフォーム情報を取得
        List<PlatformDto> platforms = instanceService.getPlatforms(ViewContext.getUserNo());
        for (PlatformDto platform : platforms) {
            if (instance.getInstance().getPlatformNo().equals(platform.getPlatform().getPlatformNo())) {
                this.platform = platform;
                break;
            }
        }

        // サーバ種別情報
        for (ImageDto image : platform.getImages()) {
            if (instance.getInstance().getImageNo().equals(image.getImage().getImageNo())) {
                this.image = image;
                break;
            }
        }

        // 有効でないサービス情報情報を除外
        List<ComponentType> componentTypes = image.getComponentTypes();
        for (int i = componentTypes.size() - 1; i >= 0; i--) {
            if (BooleanUtils.isNotTrue(componentTypes.get(i).getSelectable())) {
                componentTypes.remove(i);
            }
        }

        // 利用可能なサービス情報をソート
        Collections.sort(componentTypes, new Comparator<ComponentType>() {
            @Override
            public int compare(ComponentType o1, ComponentType o2) {
                int order1 = (o1.getViewOrder() != null) ? o1.getViewOrder() : Integer.MAX_VALUE;
                int order2 = (o2.getViewOrder() != null) ? o2.getViewOrder() : Integer.MAX_VALUE;
                return order1 - order2;
            }
        });
    }

    private void okButtonClick(ClickEvent event) {
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())) {
            updateAwsInstance();
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatform().getPlatformType())) {
            updateVmwareInstance();
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatform().getPlatformType())) {
            updateNiftyInstance();
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatform().getPlatformType())) {
            updateCloudstackInstance();
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatform().getPlatformType())) {
            updateVcloudInstance();
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
            updateAzureInstance();
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatform().getPlatformType())) {
            updateOpenstackInstance();
        }
    }

    private void updateAwsInstance() {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String keyName = (String) awsDetailTab.keySelect.getValue();
        String groupName = (String) awsDetailTab.grpSelect.getValue();
        String serverSize = (String) awsDetailTab.sizeSelect.getValue();
        String zoneName = null;
        String subnetId = null;
        String privateIp = null;
        Long addressNo = (Long) awsDetailTab.elasticIpSelect.getValue();

        Subnet subnet = null;
        if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
            subnetId = (String) awsDetailTab.subnetSelect.getValue();
            subnet = awsDetailTab.findSubnet(subnetId);
            zoneName = subnet.getAvailabilityZone();
            privateIp = (String) awsDetailTab.privateIpField.getValue();
        } else {
            zoneName = (String) awsDetailTab.zoneSelect.getValue();
        }

        if (awsDetailTab.NULL_ADDRESS.equals(addressNo)) {
            addressNo = null;
        }

        // 入力チェック
        try {
            basicTab.commentField.validate();
            awsDetailTab.sizeSelect.validate();
            awsDetailTab.keySelect.validate();
            awsDetailTab.grpSelect.validate();
            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())) {
                awsDetailTab.subnetSelect.validate();
                awsDetailTab.privateIpField.validate();
            } else {
                awsDetailTab.zoneSelect.validate();
            }
            awsDetailTab.elasticIpSelect.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // プライベートIPアドレスがサブネット内で有効かどうかをチェック
        if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc()) && StringUtils.isNotEmpty(privateIp)) {
            long privateIpAddress = IpAddressUtils.parse(privateIp);
            long networkAddress = IpAddressUtils.getNetworkAddress(subnet.getCidrBlock());
            long broadcastAddress = IpAddressUtils.getBroadcastAddress(subnet.getCidrBlock());

            // AWSのサブネットの最初の4つと最後の1つのIPアドレスは予約されているため使用できない
            if (privateIpAddress < networkAddress + 4 || broadcastAddress - 1 < privateIpAddress) {
                String message = ViewMessages.getMessage("IUI-000109", IpAddressUtils.format(networkAddress + 4),
                        IpAddressUtils.format(broadcastAddress - 1));
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // 自身以外に割り当て済みのElasticIPアドレスは利用できない
        if (addressNo != null) {
            AwsAddress awsAddress = awsDetailTab.findAwsAddress(addressNo);
            if (awsAddress.getInstanceNo() != null) {
                if (instance.getAwsAddress() == null
                        || !instance.getAwsAddress().getAddressNo().equals(awsAddress.getAddressNo())) {
                    String message = ViewMessages.getMessage("IUI-000064");
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // AWSサーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        try {
            instanceService.updateAwsInstance(instanceNo, instance.getInstance().getInstanceName(), comment, keyName,
                    serverSize, groupName, zoneName, addressNo, subnetId, privateIp);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サーバにサービスを関連付ける
        if (basicTab.componentNos != null && basicTab.attachService) {
            instanceService.associateComponents(instanceNo, basicTab.componentNos);
        }

        // 画面を閉じる
        close();
    }

    private void updateVmwareInstance() {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        Long keyNo = (Long) vmwareDetailTab.keySelect.getValue();
        String serverSize = (String) vmwareDetailTab.sizeSelect.getValue();
        String cluster = (String) vmwareDetailTab.clusterSelect.getValue();

        VmwareAddressDto vmwareAddressDto = null;
        Boolean isStaticipSelected = false;
        if (vmwareEditIpTab != null) {
            String ipOption = (String) vmwareEditIpTab.ipOptionGroup.getValue();
            if (vmwareEditIpTab.IP_OPTION_STATIC.equals(ipOption)) {
                String ipAddress = (String) vmwareEditIpTab.ipAddressField.getValue();
                String subnetMask = (String) vmwareEditIpTab.subnetMaskField.getValue();
                String defaultGateway = (String) vmwareEditIpTab.defaultGatewayField.getValue();

                vmwareAddressDto = new VmwareAddressDto();
                vmwareAddressDto.setIpAddress(ipAddress);
                vmwareAddressDto.setSubnetMask(subnetMask);
                vmwareAddressDto.setDefaultGateway(defaultGateway);
            }
        }

        // 入力チェック
        try {
            basicTab.commentField.validate();
            vmwareDetailTab.keySelect.validate();
            vmwareDetailTab.clusterSelect.validate();
            vmwareDetailTab.sizeSelect.validate();
            if (BooleanUtils.isTrue(isStaticipSelected)) {
                vmwareEditIpTab.ipAddressField.validate();
                vmwareEditIpTab.subnetMaskField.validate();
                vmwareEditIpTab.defaultGatewayField.validate();
            }
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // VMwareサーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        try {
            instanceService.updateVmwareInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                    serverSize, cluster, null, keyNo, vmwareAddressDto);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サーバにサービスを関連付ける
        if (basicTab.componentNos != null && basicTab.attachService) {
            instanceService.associateComponents(instanceNo, basicTab.componentNos);
        }

        // 画面を閉じる
        close();
    }

    private void updateNiftyInstance() {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String keyName = (String) niftyDetailTab.keySelect.getValue();
        String serverSize = (String) niftyDetailTab.sizeSelect.getValue();

        NiftyKeyPair selectedKeyPair = null;
        for (NiftyKeyPair niftyKeyPair : niftyDetailTab.niftyKeyPairs) {
            if (niftyKeyPair.getKeyName().equals(keyName)) {
                selectedKeyPair = niftyKeyPair;
                break;
            }
        }

        // 入力チェック
        try {
            basicTab.commentField.validate();
            niftyDetailTab.keySelect.validate();
            niftyDetailTab.sizeSelect.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // Niftyサーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        try {
            instanceService.updateNiftyInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                    serverSize, selectedKeyPair.getKeyNo());
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サーバにサービスを関連付ける
        if (basicTab.componentNos != null && basicTab.attachService) {
            instanceService.associateComponents(instanceNo, basicTab.componentNos);
        }

        // 画面を閉じる
        close();
    }

    private void updateCloudstackInstance() {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String keyName = (String) cloudStackDetailTab.keySelect.getValue();
        String groupName = (String) cloudStackDetailTab.grpSelect.getValue();
        String serverSize = (String) cloudStackDetailTab.sizeSelect.getValue();
        ZoneDto zoneDto = (ZoneDto) cloudStackDetailTab.zoneSelect.getValue();
        String zoneId = null;
        AddressDto address = (AddressDto) cloudStackDetailTab.elasticIpSelect.getValue();

        if (zoneDto != null) {
            zoneId = zoneDto.getZoneId();
        }

        // 入力チェック
        try {
            basicTab.commentField.validate();
            cloudStackDetailTab.sizeSelect.validate();
            cloudStackDetailTab.zoneSelect.validate();
            cloudStackDetailTab.elasticIpSelect.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 自身以外に割り当て済みのElasticIPアドレスは利用できない
        if (!cloudStackDetailTab.NULL_ADDRESS.equals(address) && address.getInstanceNo() != null) {
            if (instance.getCloudstackAddress() == null
                    || !instance.getCloudstackAddress().getAddressNo().equals(address.getAddressNo())) {
                String message = ViewMessages.getMessage("IUI-000064");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // CloudStackサーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        try {
            instanceService.updateCloudstackInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                    keyName, serverSize, groupName, zoneId, address.getAddressNo());
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サーバにサービスを関連付ける
        if (basicTab.componentNos != null && basicTab.attachService) {
            instanceService.associateComponents(instanceNo, basicTab.componentNos);
        }

        // 画面を閉じる
        close();
    }

    private void updateVcloudInstance() {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        Long keyNo = (Long) vcloudDetailTab.keySelect.getValue();
        String serverSize = (String) vcloudDetailTab.sizeSelect.getValue();
        Long storageTypeNo = (Long) vcloudDetailTab.storageTypeSelect.getValue();

        // 入力チェック
        try {
            basicTab.commentField.validate();
            vcloudDetailTab.storageTypeSelect.validate();
            vcloudDetailTab.keySelect.validate();
            vcloudDetailTab.sizeSelect.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // VCloudサーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        try {
            instanceService.updateVcloudInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                    storageTypeNo, keyNo, serverSize, vcloudNetworkTab.instanceNetworks);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サーバにサービスを関連付ける
        if (basicTab.componentNos != null && basicTab.attachService) {
            instanceService.associateComponents(instanceNo, basicTab.componentNos);
        }

        // 画面を閉じる
        close();
    }

    private void updateAzureInstance() {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String serverSize = (String) azureDetailTab.sizeSelect.getValue();
        SubnetDto subnetDto = (SubnetDto) azureDetailTab.subnetSelect.getValue();
        String subnetId = null;
        String availabilitySet = (String) azureDetailTab.availabilitySetSelect.getValue();

        if (subnetDto != null) {
            subnetId = subnetDto.getSubnetId();
        }

        // 入力チェック
        try {
            basicTab.commentField.validate();
            azureDetailTab.sizeSelect.validate();
            azureDetailTab.subnetSelect.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // Azureサーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        try {
            instanceService.updateAzureInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                    serverSize, availabilitySet, subnetId);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サーバにサービスを関連付ける
        if (basicTab.componentNos != null && basicTab.attachService) {
            instanceService.associateComponents(instanceNo, basicTab.componentNos);
        }

        // 画面を閉じる
        close();
    }

    private void updateOpenstackInstance() {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String keyName = (String) openStackDetailTab.keySelect.getValue();
        String groupName = (String) openStackDetailTab.grpSelect.getValue();
        String serverSize = (String) openStackDetailTab.sizeSelect.getValue();
        ZoneDto zoneDto = (ZoneDto) openStackDetailTab.zoneSelect.getValue();
        String zoneName = null;

        if (zoneDto != null) {
            zoneName = zoneDto.getZoneName();
        }

        // 入力チェック
        try {
            basicTab.commentField.validate();
            openStackDetailTab.sizeSelect.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // OpenStackサーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        try {
            instanceService.updateOpenStackInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                    serverSize, zoneName, groupName, keyName);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サーバにサービスを関連付ける
        if (basicTab.componentNos != null && basicTab.attachService) {
            instanceService.associateComponents(instanceNo, basicTab.componentNos);
        }

        // 画面を閉じる
        close();
    }

}
