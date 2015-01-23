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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.component.Subnet;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PlatformAzure;
import jp.primecloud.auto.entity.crud.PlatformOpenstack;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.exception.AutoApplicationException;
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
import jp.primecloud.auto.ui.util.CommonUtils;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import com.vaadin.Application;
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
    final String COLUMN_HEIGHT = "30px";

    final String TAB_HEIGHT = "420px";

    Application apl;

    Long instanceNo;

    boolean isLoadBalancer = false;

    TabSheet tab = new TabSheet();

    BasicTab basicTab;

    AWSDetailTab awsDetailTab;

    VMWareDetailTab vmwareDetailTab;

    NiftyDetailTab niftyDetailTab;

    CloudStackDetailTab cloudStackDetailTab;

    VcloudDetailTab vcloudDetailTab;

    VcloudNetworkTab vcloudNetworkTab;

    AzureDetailTab azureDetailTab;

    OpenStackDetailTab openStackDetailTab;

    VmwareEditIpTab vmwareEditIpTab;

    InstanceDto instance;

    PlatformDto platformDto;

    ImageDto image;

    List<String> keyPairs;

    List<String> securityGroups;

    List<String> instanceTypes;

    List<VmwareKeyPair> vmwareKeyPairs;

    List<String> clusters;

    List<ZoneDto> zones;

    List<Long> componentNos;

    List<String> networks;

    List<NiftyKeyPair> niftyKeyPairs;

    List<AddressDto> elasticIps;

    List<SubnetDto> subnets;

    List<KeyPairDto> vcloudKeyPairs;

    List<StorageTypeDto> storageTypes;

    List<DataDiskDto> dataDisks;

    List<DataDiskDto> deleteDataDisks;

    Map<String, NetworkDto> networkMap;

    List<InstanceNetworkDto> instanceNetworks;

    List<String> availabilitySets;

    List<String> zoneNames;

    boolean attachService = false;

    WinServerEdit(Application ap, Long instanceNo) {
        apl = ap;
        this.instanceNo = instanceNo;

        // 初期データの取得
        initData();

        //モーダルウインドウ
        setCaption(ViewProperties.getCaption("window.winServerEdit"));
        setModal(true);
        setWidth("600px");
        setIcon(Icons.EDITMINI.resource());

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        //基本情報Tabの追加
        basicTab = new BasicTab();
        tab.addTab(basicTab, ViewProperties.getCaption("tab.basic"), Icons.BASIC.resource());
        layout.addComponent(tab);
        // 入力チェックの設定
        basicTab.initValidation();
        // 基本設定のデータ表示
        basicTab.showData();

        //詳細設定Tabの追加
        String platformType = platformDto.getPlatform().getPlatformType();
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformType)) {
            awsDetailTab = new AWSDetailTab();
            tab.addTab(awsDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            awsDetailTab.initValidation();
            // 詳細設定のデータ表示
            awsDetailTab.showData();
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platformType)) {
            vmwareDetailTab = new VMWareDetailTab();
            tab.addTab(vmwareDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            // 入力チェックの設定
            vmwareDetailTab.initValidation();
            // 詳細設定のデータ表示
            vmwareDetailTab.showData();

            boolean enableVmwareStaticIp = BooleanUtils.toBoolean(Config.getProperty("ui.enableVmwareEditIp"));
            if (BooleanUtils.isTrue(enableVmwareStaticIp)) {
                this.vmwareEditIpTab = new VmwareEditIpTab();
                tab.addTab(vmwareEditIpTab, ViewProperties.getCaption("tab.editIp"), Icons.DETAIL.resource());
                this.vmwareEditIpTab.showData();
                this.vmwareEditIpTab.initValidation();
            }

            layout.addComponent(tab);

        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platformType)) {
            niftyDetailTab = new NiftyDetailTab();
            tab.addTab(niftyDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            niftyDetailTab.initValidation();
            // 詳細設定のデータ表示
            niftyDetailTab.showData();

        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platformType)) {
            cloudStackDetailTab = new CloudStackDetailTab();
            tab.addTab(cloudStackDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            cloudStackDetailTab.initValidation();
            // 詳細設定のデータ表示
            cloudStackDetailTab.showData();
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platformType)) {
            //詳細設定タブ
            vcloudDetailTab = new VcloudDetailTab();
            tab.addTab(vcloudDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            // 入力チェックの設定
            vcloudDetailTab.initValidation();
            // 詳細設定のデータ表示
            vcloudDetailTab.showData();

            //ネットワーク設定タブ
            vcloudNetworkTab = new VcloudNetworkTab();
            tab.addTab(vcloudNetworkTab, ViewProperties.getCaption("tab.network"), Icons.DETAIL.resource());
            vcloudNetworkTab.showData();

            layout.addComponent(tab);
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platformType)) {
            azureDetailTab = new AzureDetailTab();
            tab.addTab(azureDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            azureDetailTab.initValidation();
            // 詳細設定のデータ表示
            azureDetailTab.showData();
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platformType)) {
            openStackDetailTab = new OpenStackDetailTab();
            tab.addTab(openStackDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            openStackDetailTab.initValidation();
            // 詳細設定のデータ表示
            openStackDetailTab.showData();
        }

        //        tab.addTab(new UserTab(), "ユーザ設定", new ThemeResource("icons/user.png"));
        //        layout.addComponent(tab);

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editServer.ok"));
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                okButtonClick(event);
            }
        });
        okbar.addComponent(okButton);
        // [Enter]でokButtonクリック
        okButton.setClickShortcut(KeyCode.ENTER);
        okButton.focus();

        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        okbar.addComponent(cancelButton);
    }

    private class BasicTab extends VerticalLayout {
        Form form = new Form();

        private TextField serverNameField;

        private TextField hostNameField;

        private TextField commentField;

        private Label cloudLabel;

        private Label imageLabel;

        private Label osLabel;

        private AvailableServiceTable serviceTable;

        BasicTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

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

            // クラウド
            CssLayout layout = new CssLayout();
            layout.setWidth("100%");
            layout.setCaption(ViewProperties.getCaption("field.cloud"));
            cloudLabel = new Label();
            layout.addComponent(cloudLabel);
            form.getLayout().addComponent(layout);

            // サーバ種別
            CssLayout hlimg = new CssLayout();
            hlimg.setWidth("100%");
            hlimg.setCaption(ViewProperties.getCaption("field.image"));
            imageLabel = new Label();
            hlimg.addComponent(imageLabel);
            form.getLayout().addComponent(hlimg);

            // OS
            CssLayout hlOS = new CssLayout();
            hlOS.setWidth("100%");
            hlOS.setCaption(ViewProperties.getCaption("field.os"));
            osLabel = new Label();
            hlOS.addComponent(osLabel);
            form.getLayout().addComponent(hlOS);

            //LB以外の場合
            if (!isLoadBalancer) {

                // 利用可能サービス
                Panel panel = new Panel();
                serviceTable = new AvailableServiceTable();
                panel.addComponent(serviceTable);
                form.getLayout().addComponent(panel);
                panel.setSizeFull();
                form.setSizeFull();

                //サービス選択ボタン
                Button btnService = new Button(ViewProperties.getCaption("button.serverAttachService"));
                btnService.setDescription(ViewProperties.getCaption("description.serverAttachService"));
                btnService.setIcon(Icons.SERVICETAB.resource());
                btnService.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        WinServerAttachService winServerAttachService = new WinServerAttachService(getApplication(),
                                instance, image, componentNos);
                        winServerAttachService.addListener(new Window.CloseListener() {
                            @Override
                            public void windowClose(Window.CloseEvent e) {
                                List<Long> componentNos = (List<Long>) ContextUtils.getAttribute("componentNos");
                                if (componentNos != null) {
                                    ContextUtils.removeAttribute("componentNos");
                                    WinServerEdit.this.componentNos = componentNos;
                                    attachService = true;
                                }
                            }
                        });
                        getWindow().getApplication().getMainWindow().addWindow(winServerAttachService);
                    }
                });
                HorizontalLayout hlay = new HorizontalLayout();
                hlay.setSpacing(true);

                Label txt = new Label(ViewProperties.getCaption("label.serverAttachService"));
                hlay.addComponent(btnService);
                hlay.addComponent(txt);
                hlay.setComponentAlignment(txt, Alignment.MIDDLE_LEFT);

                form.getLayout().addComponent(hlay);
            }
            addComponent(form);

        }

        private void showData() {
            serverNameField.setReadOnly(false);
            serverNameField.setValue(instance.getInstance().getInstanceName());
            serverNameField.setReadOnly(true);

            hostNameField.setReadOnly(false);
            hostNameField.setValue(instance.getInstance().getFqdn());
            hostNameField.setReadOnly(true);

            String comment = instance.getInstance().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            String cloudName = platformDto.getPlatform().getPlatformNameDisp();
            //プラットフォームアイコン名の取得
            Icons cloudIcon = CommonUtils.getPlatformIcon(platformDto);
            cloudLabel.setCaption(cloudName);
            cloudLabel.setIcon(cloudIcon.resource());

            if (image != null) {
                String imageName = image.getImage().getImageNameDisp();
                Icons imageIcon = CommonUtils.getImageIcon(image);
                imageLabel.setCaption(imageName);
                imageLabel.setIcon(imageIcon.resource());

                String osName = image.getImage().getOsDisp();
                Icons osIcon = CommonUtils.getOsIcon(image);
                osLabel.setCaption(osName);
                osLabel.setIcon(osIcon.resource());
            }

            //LB以外の場合
            if (!isLoadBalancer) {
                serviceTable.showData();
            }
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }
    }

    //WinServerAddのものと同じ
    private class AvailableServiceTable extends Table {
        AvailableServiceTable() {
            //テーブル基本設定
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

            //カラム設定
            addContainerProperty("Service", Label.class, new Label());
            addContainerProperty("Description", String.class, null);
            setColumnExpandRatio("Service", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {

                    if (propertyId == null) {
                        return "";
                    }
                    String ret = propertyId.toString().toLowerCase();

                    Long componentTypeNo = (Long) itemId;
                    List<ComponentType> componentTypes = image.getComponentTypes();
                    for (ComponentType componentType : componentTypes) {
                        if (componentType.getComponentTypeNo().equals(componentTypeNo) &&
                            BooleanUtils.isNotTrue(componentType.getSelectable())) {
                            //無効コンポーネントタイプの場合は、セルの表示をDisableに変更する
                            ret += " v-disabled";
                            break;
                        }
                    }
                    return ret;
                }
            });
        }

        private void showData() {
            removeAllItems();
            if (image == null) {
                return;
            }

            for (int i = 0; i < image.getComponentTypes().size(); i++) {
                ComponentType componentType = image.getComponentTypes().get(i);

                // サービス名
                String name = componentType.getComponentTypeNameDisp();
                Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());

                Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, nameIcon) + "\"><div>" + name
                        + "</div>", Label.CONTENT_XHTML);
                slbl.setHeight("26px");

                // サービス説明
                String description = componentType.getLayerDisp();

                addItem(new Object[] { slbl, description }, componentType.getComponentTypeNo());
            }
        }
    }

    private class AWSDetailTab extends VerticalLayout {

        Form form = new Form();

        ComboBox sizeSelect;

        ComboBox keySelect;

        ComboBox grpSelect;

        ComboBox subnetSelect;

        TextField privateIpField;

        ComboBox zoneSelect;

        ComboBox elasticIpSelect;

        final String CIDR_BLOCK_CAPTION_ID = "cidrBlock";

        final String ZONE_CAPTION_ID = "zoneName";

        final String ELASTIC_IP_CAPTION_ID = "ElasticIP";

        final AddressDto NULL_ADDRESS = new AddressDto();

        final String TEXT_WIDTH = "150px";

        final String COMBOBOX_WIDTH = "150px";

        final String IP_COMBOBOX_WIDTH = "220px";

        final String BUTTON_WIDTH = "150px";

        AWSDetailTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            //サーバサイズ(インスタンスタイプ)
            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setWidth(COMBOBOX_WIDTH);
            sizeSelect.setNullSelectionAllowed(false);

            //キーペア
            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setWidth(COMBOBOX_WIDTH);
            keySelect.setNullSelectionAllowed(false);

            //セキュリティグループ
            grpSelect = new ComboBox(ViewProperties.getCaption("field.securityGroup"));
            grpSelect.setWidth(COMBOBOX_WIDTH);
            grpSelect.setImmediate(true);
            grpSelect.setNullSelectionAllowed(false);

            //サブネット
            subnetSelect = new ComboBox(ViewProperties.getCaption("field.subnet"));
            subnetSelect.setImmediate(true);
            subnetSelect.setWidth(COMBOBOX_WIDTH);
            subnetSelect.setNullSelectionAllowed(false);
            subnetSelect.setItemCaptionPropertyId(CIDR_BLOCK_CAPTION_ID);
            subnetSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            //プライベートIP
            privateIpField = new TextField(ViewProperties.getCaption("field.privateIp"));
            privateIpField.setImmediate(true);
            privateIpField.setWidth(TEXT_WIDTH);

            //ゾーン
            zoneSelect = new ComboBox(ViewProperties.getCaption("field.zone"));
            zoneSelect.setWidth(COMBOBOX_WIDTH);
            zoneSelect.setNullSelectionAllowed(false);
            zoneSelect.setItemCaptionPropertyId(ZONE_CAPTION_ID);
            zoneSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            //ElasticIp
            elasticIpSelect = new ComboBox(ViewProperties.getCaption("field.elasticIp"));
            elasticIpSelect.setWidth(IP_COMBOBOX_WIDTH);
            elasticIpSelect.setNullSelectionAllowed(false);
            elasticIpSelect.setItemCaptionPropertyId(ELASTIC_IP_CAPTION_ID);
            elasticIpSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            Label spacer = new Label(" ");
            spacer.addStyleName("desc-padding-horizontal");
            spacer.setHeight("5px");

            //表示or非表示
            PlatformAws platformAws = platformDto.getPlatformAws();
            form.getLayout().addComponent(sizeSelect);
            form.getLayout().addComponent(keySelect);
            form.getLayout().addComponent(grpSelect);
            if (platformAws.getEuca() == false && platformAws.getVpc()) {
                form.getLayout().addComponent(subnetSelect);
                form.getLayout().addComponent(privateIpField);
            } else {
                form.getLayout().addComponent(zoneSelect);
            }
            form.getLayout().addComponent(spacer);
            form.getLayout().addComponent(elasticIpSelect);

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setMargin(false);
            //            Label labelElasticIp = new Label(ViewProperties.getCaption("label.elasticIp"));

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

            //            layout.addComponent(labelElasticIp);
            layout.addComponent(addElasticIp);
            layout.addComponent(deleteElasticIp);
            form.getLayout().addComponent(layout);

            addComponent(form);

            //活性or非活性
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                // サーバがStopped以外の場合は、詳細設定タブ自体を変更不可とする
                form.setEnabled(false);
            } else {
                //Image image = Config.getImage(instance.getInstance().getImageNo());
                if (image.getImageAws().getEbsImage() && StringUtils.isNotEmpty(instance.getAwsInstance().getInstanceId())) {
                    // EBSイメージで既に作成済みの場合、いくつかの項目を変更不可とする
                    //sizeSelect.setEnabled(false);
                    keySelect.setEnabled(false);
                    grpSelect.setEnabled(false);
                    subnetSelect.setEnabled(false);
                    privateIpField.setEnabled(false);
                    zoneSelect.setEnabled(false);
                }
            }

            if (platformAws.getVpc()) {
                elasticIpSelect.setEnabled(false);
                addElasticIp.setEnabled(false);
                deleteElasticIp.setEnabled(false);
                //                labelElasticIp.setEnabled(false);
            }
        }

        private void addButtonClick() {
            // ElasticIPを取得する
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            Long addressNo = describeService.createAddress(ViewContext.getUserNo(), instance.getInstance().getPlatformNo());

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
                if (null == instance.getAwsAddress()
                        || !instance.getAwsAddress().getAddressNo().equals(address.getAddressNo())) {
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
                        describeService.deleteAddress(address.getUserNo(), address.getPlatformNo(), address.getAddressNo());

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
            List<AddressDto> addresses = describeService.getAddresses(ViewContext.getUserNo(), instance.getInstance().getPlatformNo());

            elasticIps = addresses;
            elasticIpSelect.setContainerDataSource(createElasticIpContainer());
        }

        private void showData() {
            PlatformAws platformAws = platformDto.getPlatformAws();

            keySelect.setContainerDataSource(new IndexedContainer(keyPairs));
            keySelect.select(instance.getAwsInstance().getKeyName());

            if (platformAws.getEuca() == false && platformAws.getVpc()) {
                subnetSelect.setContainerDataSource(createSubnetContainer());
                for (SubnetDto subnetDto : subnets) {
                    if (subnetDto.getSubnetId().equals(instance.getAwsInstance().getSubnetId())) {
                        subnetSelect.select(subnetDto);
                        break;
                    }
                }
            }
            SubnetDto subnetDto = (SubnetDto) subnetSelect.getValue();
            if (subnetDto != null) {
                //サブネットが存在する場合は編集不可
                subnetSelect.setEnabled(false);
            }

            grpSelect.setContainerDataSource(new IndexedContainer(securityGroups));
            grpSelect.select(instance.getAwsInstance().getSecurityGroups());

            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getAwsInstance().getInstanceType());

            zoneSelect.setContainerDataSource(createZoneContainer());
            for (ZoneDto zoneDto : zones) {
                if (StringUtils.equals(zoneDto.getZoneName(), instance.getAwsInstance().getAvailabilityZone())) {
                    zoneSelect.select(zoneDto);
                    break;
                }
            }
            if (instance.getAwsVolumes() != null && instance.getAwsVolumes().size() > 0) {
                //ボリュームが存在する場合は編集不可
                zoneSelect.setEnabled(false);
            }

            privateIpField.setValue(instance.getAwsInstance().getPrivateIpAddress());

            elasticIpSelect.setContainerDataSource(createElasticIpContainer());
            if (null != instance.getAwsAddress()) {
                for (AddressDto addressDto : elasticIps) {
                    if (addressDto.getAddressNo().equals(instance.getAwsAddress().getAddressNo())) {
                        elasticIpSelect.select(addressDto);
                        break;
                    }
                }
            } else {
                elasticIpSelect.select(NULL_ADDRESS);
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

        private IndexedContainer createZoneContainer() {
            IndexedContainer zoneContainer = new IndexedContainer();
            zoneContainer.addContainerProperty(ZONE_CAPTION_ID, String.class, null);

            for (ZoneDto zoneDto : zones) {
                Item item = zoneContainer.addItem(zoneDto);
                item.getItemProperty(ZONE_CAPTION_ID).setValue(zoneDto.getZoneName());
            }

            return zoneContainer;
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

            PlatformAws platformAws = platformDto.getPlatformAws();
            if (platformAws.getEuca()) {
                // Eucalyptus の場合は入力必須
                message = ViewMessages.getMessage("IUI-000050");
                zoneSelect.setRequired(true);
                zoneSelect.setRequiredError(message);
            }

            if (platformAws.getEuca() == false && platformAws.getVpc()) {
                //EC2+VPCの場合
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
    }

    private class VMWareDetailTab extends VerticalLayout {

        Form form = new Form();

        ComboBox keySelect;

        //        ComboBox grpSelect;

        ComboBox sizeSelect;

        ComboBox clusterSelect;

        VMWareDetailTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setNullSelectionAllowed(false);
            // Windowsの場合はキーペアを無効にする
            if (StringUtils.startsWith(image.getImage().getOs(), PCCConstant.OS_NAME_WIN)) {
                keySelect.setEnabled(false);
            }
            clusterSelect = new ComboBox(ViewProperties.getCaption("field.cluster"));
            clusterSelect.setNullSelectionAllowed(false);

            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setNullSelectionAllowed(false);

            form.getLayout().addComponent(sizeSelect);
            form.getLayout().addComponent(keySelect);
            form.getLayout().addComponent(clusterSelect);

            addComponent(form);

            // サーバがStopped以外の場合は、変更不可とする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                form.setEnabled(false);
            }

        }

        private void showData() {
            VmwareKeyPair selectedKeyPair = null;
            for (VmwareKeyPair vmwareKeyPair : vmwareKeyPairs) {
                if (vmwareKeyPair.getKeyNo().equals(instance.getVmwareInstance().getKeyPairNo())) {
                    selectedKeyPair = vmwareKeyPair;
                    break;
                }
            }
            keySelect.setContainerDataSource(new IndexedContainer(keyPairs));
            keySelect.select(selectedKeyPair.getKeyName());

            clusterSelect.setContainerDataSource(new IndexedContainer(clusters));
            clusterSelect.select(instance.getVmwareInstance().getComputeResource());
            if (StringUtils.isNotEmpty(instance.getVmwareInstance().getDatastore())) {
                clusterSelect.setEnabled(false);
            }

            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getVmwareInstance().getInstanceType());
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
    }

    private class VmwareEditIpTab extends VerticalLayout {

        Form form = new Form();

        OptionGroup ipOptionGroup;

        TextField ipAddressField;

        TextField subnetMaskField;

        TextField defaultGatewayField;

        VmwareEditIpTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            ipOptionGroup = new OptionGroup(ViewProperties.getCaption("field.optionIp"));
            ipOptionGroup.addItem(ViewProperties.getCaption("field.dhcpIp"));
            ipOptionGroup.addItem(ViewProperties.getCaption("field.staticIp"));
            ipOptionGroup.setNullSelectionAllowed(false);
            ipOptionGroup.setImmediate(true);
            ipOptionGroup.select(ViewProperties.getCaption("field.dhcpIp"));
            ipOptionGroup.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    String value = (String) event.getProperty().getValue();
                    if (value.equals(ViewProperties.getCaption("field.dhcpIp"))) {
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

            ipAddressField = new TextField(ViewProperties.getCaption("field.ipAddress"));
            ipAddressField.setWidth("100%");

            subnetMaskField = new TextField(ViewProperties.getCaption("field.subnetMask"));
            subnetMaskField.setWidth("100%");

            defaultGatewayField = new TextField(ViewProperties.getCaption("field.defaultGateway"));
            defaultGatewayField.setWidth("100%");

            form.getLayout().addComponent(ipAddressField);
            form.getLayout().addComponent(subnetMaskField);
            form.getLayout().addComponent(defaultGatewayField);

            addComponent(form);

            // サーバがStopped以外の場合は、変更不可とする
            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (InstanceStatus.STOPPED != status) {
                form.setEnabled(false);
            }
        }

        private void showData() {
            VmwareAddress vmwareAddress = instance.getVmwareAddress();

            ipAddressField.setEnabled(false);
            subnetMaskField.setEnabled(false);
            defaultGatewayField.setEnabled(false);

            if (vmwareAddress != null) {
                if (BooleanUtils.isTrue(vmwareAddress.getEnabled())) {
                    ipAddressField.setEnabled(true);
                    subnetMaskField.setEnabled(true);
                    defaultGatewayField.setEnabled(true);
                    ipOptionGroup.select(ViewProperties.getCaption("field.staticIp"));
                }
                ipAddressField.setValue(vmwareAddress.getIpAddress());
                subnetMaskField.setValue(vmwareAddress.getSubnetMask());
                defaultGatewayField.setValue(vmwareAddress.getDefaultGateway());
            }
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
    }

    private class NiftyDetailTab extends VerticalLayout {

        Form form = new Form();

        ComboBox keySelect;

        ComboBox sizeSelect;

        NiftyDetailTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            keySelect = new ComboBox(ViewProperties.getCaption("field.keyPair"));
            keySelect.setNullSelectionAllowed(false);

            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setNullSelectionAllowed(false);

            form.getLayout().addComponent(sizeSelect);
            form.getLayout().addComponent(keySelect);

            addComponent(form);

            InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());
            if (status != InstanceStatus.STOPPED) {
                // サーバが停止中でない場合、全体を変更不可とする
                form.setEnabled(false);
            } else {
                // サーバが作成済みのとき、キーペアは変更不可
                if (StringUtils.isNotEmpty(instance.getNiftyInstance().getInstanceId())) {
                    keySelect.setEnabled(false);
                }
            }
        }

        private void showData() {
            keySelect.setContainerDataSource(new IndexedContainer(keyPairs));
            keySelect.select(instance.getNiftyKeyPair().getKeyName());

            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getNiftyInstance().getInstanceType());
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
    }

    private class CloudStackDetailTab extends VerticalLayout {
        Form form = new Form();

        ComboBox networkSelect;

        ComboBox keySelect;

        ComboBox grpSelect;

        ComboBox sizeSelect;

        ComboBox zoneSelect;

        ComboBox elasticIpSelect;

        final String ELASTIC_IP_CAPTION_ID = "ElasticIP";

        final String ZONE_CAPTION_ID = "zoneName";

        final AddressDto NULL_ADDRESS = new AddressDto();

        final String COMBOBOX_WIDTH = "150px";

        final String IP_COMBOBOX_WIDTH = "220px";

        final String BUTTON_WIDTH = "150px";

        CloudStackDetailTab() {
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

            //            layout.addComponent(labelElasticIp);
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

        private void addButtonClick() {
            // ElasticIPを取得する
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            Long addressNo = describeService.createAddress(ViewContext.getUserNo(), instance.getInstance().getPlatformNo());

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
                        describeService.deleteAddress(address.getUserNo(), address.getPlatformNo(), address.getAddressNo());

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
            List<AddressDto> addresses = describeService.getAddresses(ViewContext.getUserNo(), instance.getInstance().getPlatformNo());

            elasticIps = addresses;
            elasticIpSelect.setContainerDataSource(createElasticIpContainer());
        }

        private void showData() {
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
    }

    private class VcloudDetailTab extends VerticalLayout {
        final String CID_STORAGE_TYPE = "StorageType";
        final String CID_KEY_PAIR = "KeyPair";
        final String WIDTH_COMBOBOX = "220px";
        final String KEY_PAIR_WIDTH_COMBOBOX = "150px";

        Form form = new Form();
        ComboBox storageTypeSelect;
        ComboBox sizeSelect;
        ComboBox keySelect;
        DataDiskTable dataDiskTable;
        DataDiskTableButtons dataDiskTableButtons;

        VcloudDetailTab() {
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

        private void showData() {
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
            dataDiskTable.showData();
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
            final String PID_UNIT_NO = "UnitNo";
            final String PID_DISK_SIZE = "DiskSize";
            final int WIDTH_UNIT_NO = 194;
            final int WIDTH_DISK_SIZE = 194;

            DataDiskTable() {
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
                setColumnHeaders(new String[] {
                        ViewProperties.getCaption("field.unitNo"),
                        ViewProperties.getCaption("field.diskSize") });

                //ヘッダーサイズ設定
                setColumnWidth(PID_UNIT_NO, WIDTH_UNIT_NO);
                setColumnWidth(PID_DISK_SIZE, WIDTH_DISK_SIZE);

                //テーブルのカラムに対してStyleNameを設定
                setCellStyleGenerator(new Table.CellStyleGenerator() {
                    @Override
                    public String getStyle(Object itemId, Object propertyId) {

                        if (propertyId == null) {
                            return "";
                        }
                        String ret = propertyId.toString().toLowerCase();
                        return ret;
                    }
                });

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

            void showData() {
                removeAllItems();
                for (DataDiskDto diskDto : dataDisks) {
                    String unitNo = ViewProperties.getCaption("field.unattached");
                    if (diskDto.getUnitNo() != null) {
                        unitNo = String.valueOf(diskDto.getUnitNo());
                    }
                    addItem(new Object[] { unitNo, diskDto.getDiskSize() }, diskDto);
                }
            }

            void initData() {
                //InstanceDto取得
                //ただし、変数「instance」は変更せず、
                InstanceService instanceService = BeanContext.getBean(InstanceService.class);
                InstanceDto instanceDto = instanceService.getInstance(instanceNo);

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
        }

        private class DataDiskTableButtons extends HorizontalLayout {
            Button btnAdd;
            Button btnEdit;
            Button btnDelete;

            DataDiskTableButtons() {
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

            void addButtonClick(ClickEvent event) {
                WinServerDataDiskConfig winServerDataDiskConfig = new WinServerDataDiskConfig(getApplication(), instanceNo, null);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        dataDiskTable.initData();
                        dataDiskTable.showData();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            void editButtonClick(ClickEvent event) {
                DataDiskDto dataDiskDto = (DataDiskDto) dataDiskTable.getValue();
                WinServerDataDiskConfig winServerDataDiskConfig = new WinServerDataDiskConfig(getApplication(), instanceNo, dataDiskDto);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        dataDiskTable.initData();
                        dataDiskTable.showData();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            void deleteButtonClick(ClickEvent event) {
                String message = ViewMessages.getMessage("IUI-000124");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
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
                        instanceService.detachDataDisk(instanceNo, dataDiskDto.getDiskNo());

                        //データ取得&テーブル再表示
                        dataDiskTable.initData();
                        dataDiskTable.showData();
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            }
        }
    }

    private class VcloudNetworkTab extends VerticalLayout {

        Form form = new Form();

        NetworkTable networkTable;
        NetworkTableButtons networkTableButtons;

        VcloudNetworkTab() {
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

            showData();
        }

        private void showData() {
            networkTable.showData();
        }

        private class NetworkTable extends Table {
            final String PID_NETWORK_NAME = "NetworkName";
            final String PID_IP_MODE = "IpMode";
            final String PID_IP_ADDRESS = "IpAddress";
            final String PID_PRIMARY = "Primary";
            final int WIDTH_NETWORK_NAME = 180;
            final int WIDTH_IP_MOD = 114;
            final int WIDTH_IP_ADDRESS = 97;
            final int WIDTH_PRIMARY = 67;

            NetworkTable() {
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
                setColumnHeaders(new String[] {
                        ViewProperties.getCaption("field.networkName"),
                        ViewProperties.getCaption("field.ipMode"),
                        ViewProperties.getCaption("field.ipAddress"),
                        ViewProperties.getCaption("field.primary") });

                //ヘッダーサイズ設定
                setColumnWidth(PID_NETWORK_NAME, WIDTH_NETWORK_NAME);
                setColumnWidth(PID_IP_MODE, WIDTH_IP_MOD);
                setColumnWidth(PID_IP_ADDRESS, WIDTH_IP_ADDRESS);
                setColumnWidth(PID_PRIMARY, WIDTH_PRIMARY);

                //テーブルのカラムに対してStyleNameを設定
                setCellStyleGenerator(new Table.CellStyleGenerator() {
                    @Override
                    public String getStyle(Object itemId, Object propertyId) {

                        if (propertyId == null) {
                            return "";
                        }
                        String ret = propertyId.toString().toLowerCase();
                        return ret;
                    }
                });

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
                            networkTableButtons.btnDelete.setEnabled((!selectDto.isRequired() && !selectDto.isPrimary()));
                        }
                    }
                });
            }

            void showData() {
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
                        slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, Icons.SELECTMINI) + "\">", Label.CONTENT_XHTML);
                    }
                    addItem(new Object[] { network.getNetworkName(),  ipModeName, instanceNetwork.getIpAddress(), slbl}, instanceNetwork);
                }
            }

            void initData() {
                //InstanceDto取得
                //ただし、変数「instance」は変更せず、
                IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
                InstanceService instanceService = BeanContext.getBean(InstanceService.class);
                InstanceDto instanceDto = instanceService.getInstance(instanceNo);

                //Network
                networkMap = new HashMap<String, NetworkDto>();
                List<NetworkDto> networkDtos = describeService.getNetworks(ViewContext.getUserNo(), instanceDto.getInstance().getPlatformNo());
                for (NetworkDto networkDto : networkDtos) {
                    networkMap.put(networkDto.getNetworkName(), networkDto);
                }
            }
        }

        private class NetworkTableButtons extends HorizontalLayout {
            Button btnAdd;
            Button btnEdit;
            Button btnDelete;

            NetworkTableButtons() {
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

            void addButtonClick(ClickEvent event) {
                WinServerNetworkConfig winServerDataDiskConfig =
                    new WinServerNetworkConfig(getApplication(), instanceNo, instance.getInstance().getPlatformNo(), null, instanceNetworks);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        //テーブル再表示
                        networkTable.initData();
                        networkTable.showData();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            void editButtonClick(ClickEvent event) {
                InstanceNetworkDto instanceNetwork = (InstanceNetworkDto) networkTable.getValue();
                WinServerNetworkConfig winServerDataDiskConfig =
                    new WinServerNetworkConfig(getApplication(), instanceNo, instance.getInstance().getPlatformNo(), instanceNetwork, instanceNetworks);
                winServerDataDiskConfig.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(CloseEvent e) {
                        //テーブル再表示
                        networkTable.initData();
                        networkTable.showData();
                    }
                });
                getWindow().getApplication().getMainWindow().addWindow(winServerDataDiskConfig);
            }

            void deleteButtonClick(ClickEvent event) {
                String message = ViewMessages.getMessage("IUI-000127");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
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
                        networkTable.initData();
                        networkTable.showData();
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            }
        }
    }

    private class AzureDetailTab extends VerticalLayout {
        Form form = new Form();

        ComboBox sizeSelect;

        ComboBox availabilitySetSelect;

        TextField locationField;

        TextField affinityField;

        TextField cloudServiceField;

        ComboBox subnetSelect;

        TextField storageAccountField;

        final String COMBOBOX_WIDTH = "150px";

        final String TEXT_WIDTH = "150px";

        final String CIDR_BLOCK_CAPTION_ID = "cidrBlock";

        AzureDetailTab() {
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
            //            form.getLayout().addComponent(locationField);
            //            form.getLayout().addComponent(affinityField);
            //            form.getLayout().addComponent(cloudServiceField);
            form.getLayout().addComponent(subnetSelect);
            form.getLayout().addComponent(spacer);
            //            form.getLayout().addComponent(storageAccountField);

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
                // if (StringUtils.isNotEmpty(instance.getAzureInstance().getInstanceId())) {
                // 既に作成済みの場合、いくつかの項目を変更不可とする
                // 変更可 sizeSelect.setEnabled(false);
                locationField.setEnabled(false);
                //                    affinityField.setEnabled(false);
                //                    cloudServiceField.setEnabled(false);
                //                    storageAccountField.setEnabled(false);
                // }
                // サーバが作成済みのとき、変更不可
                if (StringUtils.isNotEmpty(instance.getAzureInstance().getInstanceName())) {
                    subnetSelect.setEnabled(false);
                    // TODO 可用性セットが設定済みの場合も変更可能かもしれないが、
                    // 現段階では、APIから可用性セットの情報を取得できないのでサーバー作成済みの場合、変更不可とする
                    availabilitySetSelect.setEnabled(false);
                }
            }

        }

        private void showData() {
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

            //            locationField.setValue(instance.getAwsInstance().getPrivateIpAddress());

            //            affinityField.setValue(instance.getAwsInstance().getPrivateIpAddress());

            //マッピングされたボリュームが存在する場合は変更不可
            //            if (instance.getAzureVolumes() != null && instance.getAzureVolumes().size() > 0) {
            //                zoneSelect.setEnabled(false);
            //            }

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
    }

    private class OpenStackDetailTab extends VerticalLayout {
        Form form = new Form();

        ComboBox sizeSelect;

        ComboBox zoneSelect;

        ComboBox grpSelect;

        ComboBox keySelect;

        final String COMBOBOX_WIDTH = "150px";

        OpenStackDetailTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            sizeSelect = new ComboBox(ViewProperties.getCaption("field.serverSize"));
            sizeSelect.setWidth(COMBOBOX_WIDTH);
            sizeSelect.setNullSelectionAllowed(false);

            zoneSelect = new ComboBox(ViewProperties.getCaption("field.zone"));
            zoneSelect.setWidth(COMBOBOX_WIDTH);
            zoneSelect.setNullSelectionAllowed(false);

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
            //            form.getLayout().addComponent(subnetField);
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
                // 既に作成済みの場合、いくつかの項目を変更不可とする
                    form.setEnabled(false);
                // 変更可 sizeSelect.setEnabled(false);
                //                    affinityField.setEnabled(false);
                //                    cloudServiceField.setEnabled(false);
                //                    subnetField.setEnabled(false);
                //                    storageAccountField.setEnabled(false);
                 }
            }

        }

        private void showData() {
            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getOpenstackInstance().getInstanceType());

            zoneSelect.setContainerDataSource(new IndexedContainer(zoneNames));
            zoneSelect.select(instance.getOpenstackInstance().getAvailabilityZone());

            grpSelect.setContainerDataSource(new IndexedContainer(securityGroups));
            grpSelect.select(instance.getOpenstackInstance().getSecurityGroups());

            keySelect.setContainerDataSource(new IndexedContainer(keyPairs));
            keySelect.select(instance.getOpenstackInstance().getKeyName());

            //マッピングされたボリュームが存在する場合は変更不可
            //            if (instance.getAzureVolumes() != null && instance.getAzureVolumes().size() > 0) {
            //                zoneSelect.setEnabled(false);
            //            }

        }

        private void initValidation() {
            String message;

            message = ViewMessages.getMessage("IUI-000027");
            sizeSelect.setRequired(true);
            sizeSelect.setRequiredError(message);

        }
    }

    private void initData() {
        // サーバ情報を取得
        // TODO: ロジックを必ずリファクタリングすること！
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        this.instance = instanceService.getInstance(instanceNo);

        //LBかどうか
        this.isLoadBalancer = BooleanUtils.isTrue(instance.getInstance().getLoadBalancer());

        // 利用可能サービス情報を取得
        // TODO: ロジックを必ずリファクタリングすること
        Long imageNo = instance.getInstance().getImageNo();
        List<PlatformDto> platforms = instanceService.getPlatforms(ViewContext.getUserNo());
        for (PlatformDto platformDto : platforms) {
            if (instance.getInstance().getPlatformNo().equals(platformDto.getPlatform().getPlatformNo())) {
                this.platformDto = platformDto;
                for (ImageDto image : platformDto.getImages()) {
                    if (imageNo.equals(image.getImage().getImageNo())) {
                        this.image = image;
                        break;
                    }
                }
                break;
            }
        }

        Platform platform = platformDto.getPlatform();
        String platformType = platform.getPlatformType();
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformType)) {
            PlatformAws platformAws = platformDto.getPlatformAws();
            // 情報を取得
            // TODO: ロジックを必ずリファクタリングすること
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            //キーペア
            List<KeyPairDto> infos = describeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatformNo());
            keyPairs = new ArrayList<String>();
            for (KeyPairDto info : infos) {
                keyPairs.add(info.getKeyName());
            }

            //セキュリティグループ
            securityGroups = new ArrayList<String>();
            List<SecurityGroupDto> groups;
            if (platformAws.getEuca() == false && platformAws.getVpc()) {
                groups = describeService.getSecurityGroups(ViewContext.getUserNo(), platform.getPlatformNo(), platformAws.getVpcId());
            } else {
                groups = describeService.getSecurityGroups(ViewContext.getUserNo(), platform.getPlatformNo(), null);
            }
            for (SecurityGroupDto group : groups) {
                securityGroups.add(group.getGroupName());
            }

            //インスタンスタイプ
            instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageAws().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }

            //ゾーン
            zones = describeService.getAvailabilityZones(ViewContext.getUserNo(), platform.getPlatformNo());
            if (platformAws.getEuca() == false && platformAws.getVpc() == false) {
                //EC2 VPCではない場合、空行を先頭に追加してゾーンを無指定にできるようにする
                zones.add(0, new ZoneDto());
            }

            //サブネット
            if (platformAws.getEuca() == false && platformAws.getVpc()) {
                subnets = describeService.getSubnets(ViewContext.getUserNo(), platform.getPlatformNo(), platformAws.getVpcId());
            }

            //ElasticIp
            elasticIps = new ArrayList<AddressDto>();
            List<AddressDto> addresses = describeService.getAddresses(ViewContext.getUserNo(), platform.getPlatformNo());
            for (AddressDto address : addresses) {
                elasticIps.add(address);
            }

        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platformType)) {
            // VMware情報を取得
            // TODO: ロジックを必ずリファクタリングすること
            VmwareDescribeService vmwareDescribeService = BeanContext.getBean(VmwareDescribeService.class);
            vmwareKeyPairs = vmwareDescribeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatformNo());
            keyPairs = new ArrayList<String>();
            for (VmwareKeyPair vmwareKeyPair : vmwareKeyPairs) {
                keyPairs.add(vmwareKeyPair.getKeyName());
            }

            List<ComputeResource> computeResources = vmwareDescribeService.getComputeResources(platform.getPlatformNo());
            clusters = new ArrayList<String>();
            for (ComputeResource computeResource : computeResources) {
                clusters.add(computeResource.getName());
            }

            instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageVmware().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platformType)) {
            // Nifty情報を取得
            // TODO: ロジックを必ずリファクタリングすること
            NiftyDescribeService niftyDescribeService = BeanContext.getBean(NiftyDescribeService.class);
            niftyKeyPairs = niftyDescribeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatformNo());
            keyPairs = new ArrayList<String>();
            for (NiftyKeyPair niftyKeyPair : niftyKeyPairs) {
                keyPairs.add(niftyKeyPair.getKeyName());
            }

            instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageNifty().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platformType)) {
            // CloudStack情報を取得
            // 情報を取得
            // TODO: ロジックを必ずリファクタリングすること
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            List<KeyPairDto> infos = describeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatformNo());
            keyPairs = new ArrayList<String>();
            for (KeyPairDto info : infos) {
                keyPairs.add(info.getKeyName());
            }

            networks = new ArrayList<String>();
            for (String network : platformDto.getPlatformCloudstack().getNetworkId().split(",")) {
                networks.add(network);
            }

            securityGroups = new ArrayList<String>();
            if (StringUtils.isEmpty(instance.getCloudstackInstance().getNetworkid())) {
                List<SecurityGroupDto> groups = describeService.getSecurityGroups(ViewContext.getUserNo(), platform.getPlatformNo(), null);
                for (SecurityGroupDto group : groups) {
                    securityGroups.add(group.getGroupName());
                }
            }

            instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageCloudstack().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }

            zones = describeService.getAvailabilityZones(ViewContext.getUserNo(), platform.getPlatformNo());

            elasticIps = new ArrayList<AddressDto>();
            List<AddressDto> addresses = describeService.getAddresses(ViewContext.getUserNo(), platform.getPlatformNo());
            for (AddressDto address : addresses) {
                elasticIps.add(address);
            }

        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platformType)) {
            // VCloud情報を取得
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            //StorageType
            storageTypes = describeService.getStorageTypes(ViewContext.getUserNo(), platform.getPlatformNo());

            //KeyPair
            vcloudKeyPairs = describeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatformNo());

            //InstanceType
            instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageVcloud().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }

            //DataDisk
            deleteDataDisks = new ArrayList<DataDiskDto>();
            dataDisks = new ArrayList<DataDiskDto>();
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

            //Network
            List<NetworkDto> networkDtos = describeService.getNetworks(ViewContext.getUserNo(), platform.getPlatformNo());
            networkMap = new HashMap<String, NetworkDto>();
            for (NetworkDto networkDto : networkDtos) {
                networkMap.put(networkDto.getNetworkName(), networkDto);
            }

            //InstanceNetwork
            instanceNetworks = new ArrayList<InstanceNetworkDto>();
            List<VcloudInstanceNetwork> tmpInstanceNetworks = this.instance.getVcloudInstanceNetworks();
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

        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platformType)) {
            // Azure情報を取得
            PlatformAzure platformAzure = platformDto.getPlatformAzure();
            // 情報を取得
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);

            instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageAzure().getInstanceTypes().split(",")) {
                instanceTypes.add(instanceType.trim());
            }
            // 可用性セット
            availabilitySets = new ArrayList<String>();
            for (String availabilitySet : platformAzure.getAvailabilitySets().split(",")) {
                availabilitySets.add(availabilitySet.trim());
            }
            //サブネット
            subnets = describeService.getAzureSubnets(ViewContext.getUserNo(), platform.getPlatformNo(),
                    platformAzure.getNetworkName());

        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platformType)) {
            // OpenStack情報を取得
            PlatformOpenstack platformOpenstack = platformDto.getPlatformOpenstack();
            // 情報を取得
            IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
            //instanceTypes
            instanceTypes = new ArrayList<String>();
            for (String instanceType : image.getImageOpenstack().getInstanceTypes().split(",")) {
                //IDで取得されるため、今後名称に変換する必要有り
                instanceTypes.add(instanceType.trim());
            }
            // Availablility Zone
            zoneNames = new ArrayList<String>();
            for (String availabilityZone : platformOpenstack.getAvailabilityZone().split(",")) {
                zoneNames.add(availabilityZone.trim());
            }

            //セキュリティグループ
            securityGroups = new ArrayList<String>();
            List<SecurityGroupDto> groups;
            groups = describeService.getSecurityGroups(ViewContext.getUserNo(), platform.getPlatformNo(), null);
            for (SecurityGroupDto group : groups) {
                securityGroups.add(group.getGroupName());
            }

            //キーペア
            List<KeyPairDto> infos = describeService.getKeyPairs(ViewContext.getUserNo(), platform.getPlatformNo());
            keyPairs = new ArrayList<String>();
            for (KeyPairDto info : infos) {
                keyPairs.add(info.getKeyName());
            }

        }

        // サーバに関連付けられたサービスを取得
        componentNos = new ArrayList<Long>();
        List<ComponentInstanceDto> componentInstances = instance.getComponentInstances();
        for (ComponentInstanceDto componentInstance : componentInstances) {
            if (BooleanUtils.isTrue(componentInstance.getComponentInstance().getAssociate())) {
                componentNos.add(componentInstance.getComponentInstance().getComponentNo());
            }
        }
    }

    private void okButtonClick(ClickEvent event) {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String keyName = null;
        Long keyNo = null;
        String groupName = null;
        String serverSize = null;
        String network = null;
        String cluster = null;
        ZoneDto zoneDto = null;
        String zoneName = null;
        String zoneId = null;
        SubnetDto subnetDto = null;
        String subnetId = null;
        String privateIp = null;
        AddressDto address = null;
        VmwareAddressDto vmwareAddressDto = null;
        Long storageTypeNo = null;
        List<InstanceNetworkDto> instanceNetworkDtos = null;
        String availabilitySet = null;

        // TODO: 入力チェック
        try {
            basicTab.commentField.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformDto.getPlatform().getPlatformType())) {
            PlatformAws platformAws = platformDto.getPlatformAws();
            // 入力値を取得
            keyName = (String) awsDetailTab.keySelect.getValue();
            groupName = (String) awsDetailTab.grpSelect.getValue();
            serverSize = (String) awsDetailTab.sizeSelect.getValue();

            if (platformAws.getEuca() == false && platformAws.getVpc()) {
                subnetDto = (SubnetDto) awsDetailTab.subnetSelect.getValue();
                if (subnetDto != null) {
                    subnetId = subnetDto.getSubnetId();
                    zoneName = subnetDto.getZoneid();
                }
                privateIp = (String) awsDetailTab.privateIpField.getValue();
            } else {
                zoneDto = (ZoneDto) awsDetailTab.zoneSelect.getValue();
                if (zoneDto != null) {
                    zoneName = zoneDto.getZoneName();
                }
                privateIp = (String) awsDetailTab.privateIpField.getValue();
            }
            address = (AddressDto) awsDetailTab.elasticIpSelect.getValue();

            // TODO: 入力チェック
            try {
                awsDetailTab.sizeSelect.validate();
                awsDetailTab.keySelect.validate();
                awsDetailTab.zoneSelect.validate();
                if (awsDetailTab.grpSelect.isEnabled()) {
                    awsDetailTab.grpSelect.validate();
                }
                if (platformAws.getEuca() == false && platformAws.getVpc()) {
                    awsDetailTab.subnetSelect.validate();
                    awsDetailTab.privateIpField.validate();
                }
                awsDetailTab.elasticIpSelect.validate();
            } catch (InvalidValueException e) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            //サブネットのIPアドレスの有効チェック
            if (platformAws.getEuca() == false && platformAws.getVpc() && StringUtils.isNotEmpty(privateIp)) {
                String[] cidr = subnetDto.getCidrBlock().split("/");
                Subnet subnet = new Subnet(cidr[0], Integer.parseInt(cidr[1]));
                String subnetIp = cidr[0];
                //AWS(VPC)では先頭3つまでのIPが予約済みIP
                for (int i = 0; i < 3; i++) {
                    subnetIp = Subnet.getNextAddress(subnetIp);
                    subnet.addReservedIp(subnetIp);
                }
                if (subnet.isScorp(privateIp) == false) {
                    //有効なサブネットではない
                    String message = ViewMessages.getMessage("IUI-000109", subnet.getAvailableMinIp(), subnet.getAvailableMaxIp());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            //すでに設定されているElasticIPでなく、かつ割り当て済み済みの場合は登録できない
            if (!awsDetailTab.NULL_ADDRESS.equals(address) && null != address.getInstanceNo()) {
                if (null == instance.getAwsAddress()
                        || !instance.getAwsAddress().getAddressNo().equals(address.getAddressNo())) {
                    String message = ViewMessages.getMessage("IUI-000064");
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platformDto.getPlatform().getPlatformType())) {
            // 入力値を取得
            keyName = (String) vmwareDetailTab.keySelect.getValue();
            cluster = (String) vmwareDetailTab.clusterSelect.getValue();
            serverSize = (String) vmwareDetailTab.sizeSelect.getValue();

            Boolean isStaticipSelected = false;
            if (vmwareEditIpTab != null) {
                isStaticipSelected = (Boolean) vmwareEditIpTab.ipOptionGroup.isSelected(ViewProperties
                        .getCaption("field.staticIp"));
                if (BooleanUtils.isTrue(isStaticipSelected)) {
                    String ipAddress = (String) vmwareEditIpTab.ipAddressField.getValue();
                    String subnetMask = (String) vmwareEditIpTab.subnetMaskField.getValue();
                    String defaultGateway = (String) vmwareEditIpTab.defaultGatewayField.getValue();

                    vmwareAddressDto = new VmwareAddressDto();
                    vmwareAddressDto.setIpAddress(ipAddress);
                    vmwareAddressDto.setSubnetMask(subnetMask);
                    vmwareAddressDto.setDefaultGateway(defaultGateway);
                }
            }

            // TODO: 入力チェック
            try {
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
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platformDto.getPlatform().getPlatformType())) {
            // 入力値を取得
            keyName = (String) niftyDetailTab.keySelect.getValue();
            serverSize = (String) niftyDetailTab.sizeSelect.getValue();

            // TODO: 入力チェック
            try {
                niftyDetailTab.keySelect.validate();
                niftyDetailTab.sizeSelect.validate();
            } catch (InvalidValueException e) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platformDto.getPlatform().getPlatformType())) {
            // 入力値を取得
            network = (String) cloudStackDetailTab.networkSelect.getValue();
            keyName = (String) cloudStackDetailTab.keySelect.getValue();
            groupName = (String) cloudStackDetailTab.grpSelect.getValue();
            serverSize = (String) cloudStackDetailTab.sizeSelect.getValue();
            zoneDto = (ZoneDto) cloudStackDetailTab.zoneSelect.getValue();
            if (zoneDto != null) {
                zoneId = zoneDto.getZoneId();
            }
            address = (AddressDto) cloudStackDetailTab.elasticIpSelect.getValue();

            // TODO: 入力チェック
            try {
                cloudStackDetailTab.sizeSelect.validate();
                cloudStackDetailTab.zoneSelect.validate();
                cloudStackDetailTab.elasticIpSelect.validate();
            } catch (InvalidValueException e) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            //すでに設定されているElasticIPでなく、かつ割り当て済み済みの場合は登録できない
            if (!cloudStackDetailTab.NULL_ADDRESS.equals(address) && null != address.getInstanceNo()) {
                if (null == instance.getCloudstackAddress()
                        || !instance.getCloudstackAddress().getAddressNo().equals(address.getAddressNo())) {
                    String message = ViewMessages.getMessage("IUI-000064");
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platformDto.getPlatform().getPlatformType())) {
            // 入力値を取得
            storageTypeNo = (Long) vcloudDetailTab.storageTypeSelect.getValue();
            keyNo = (Long) vcloudDetailTab.keySelect.getValue();
            serverSize = (String) vcloudDetailTab.sizeSelect.getValue();

            // 入力チェック
            try {
                vcloudDetailTab.storageTypeSelect.validate();
                vcloudDetailTab.keySelect.validate();
                vcloudDetailTab.sizeSelect.validate();
            } catch (InvalidValueException e) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platformDto.getPlatform().getPlatformType())) {
            // 入力値を取得
            serverSize = (String) azureDetailTab.sizeSelect.getValue();
            availabilitySet = (String) azureDetailTab.availabilitySetSelect.getValue();
            subnetDto = (SubnetDto) azureDetailTab.subnetSelect.getValue();
            if (subnetDto != null) {
                subnetId = subnetDto.getSubnetId();
            }

            // 入力チェック
            try {
                azureDetailTab.sizeSelect.validate();
                azureDetailTab.subnetSelect.validate();
            } catch (InvalidValueException e) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platformDto.getPlatform().getPlatformType())) {
            // 入力値を取得
            serverSize = (String) openStackDetailTab.sizeSelect.getValue();
            zoneName = (String) openStackDetailTab.zoneSelect.getValue();
            groupName = (String) openStackDetailTab.grpSelect.getValue();
            keyName = (String) openStackDetailTab.keySelect.getValue();

            // 入力チェック
            try {
                openStackDetailTab.sizeSelect.validate();
            } catch (InvalidValueException e) {
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        //オペレーションログ
        AutoApplication aapl = (AutoApplication) apl;
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // サーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);

        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformDto.getPlatform().getPlatformType())) {
            // AWSサーバを更新
            try {
                Long addressNo = address.getAddressNo();
                instanceService.updateAwsInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                        keyName, serverSize, groupName, zoneName, addressNo, subnetId, privateIp);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platformDto.getPlatform().getPlatformType())) {
            // VMwareサーバを更新
            VmwareKeyPair selectedKeyPair = null;
            for (VmwareKeyPair vmwareKeyPair : vmwareKeyPairs) {
                if (vmwareKeyPair.getKeyName().equals(keyName)) {
                    selectedKeyPair = vmwareKeyPair;
                    break;
                }
            }

            try {
                instanceService.updateVmwareInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                        serverSize, cluster, null, selectedKeyPair.getKeyNo(), vmwareAddressDto);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platformDto.getPlatform().getPlatformType())) {
            // Niftyサーバを更新
            NiftyKeyPair selectedKeyPair = null;
            for (NiftyKeyPair niftyKeyPair : niftyKeyPairs) {
                if (niftyKeyPair.getKeyName().equals(keyName)) {
                    selectedKeyPair = niftyKeyPair;
                    break;
                }
            }

            try {
                instanceService.updateNiftyInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                        serverSize, selectedKeyPair.getKeyNo());
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platformDto.getPlatform().getPlatformType())) {
            //CloudStackサーバを更新
            try {
                Long addressNo = address.getAddressNo();
                instanceService.updateCloudstackInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                        keyName, serverSize, groupName, zoneId, addressNo);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platformDto.getPlatform().getPlatformType())) {
            //VCloudサーバを更新
            try {
                instanceService.updateVcloudInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                        storageTypeNo, keyNo, serverSize, instanceNetworks);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platformDto.getPlatform().getPlatformType())) {
            //Azureサーバを更新
            try {
                instanceService.updateAzureInstance(instanceNo, instance.getInstance().getInstanceName(), comment, serverSize, availabilitySet, subnetId);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platformDto.getPlatform().getPlatformType())) {
            //OpenStackサーバを更新
            try {
                instanceService.updateOpenStackInstance(instanceNo, instance.getInstance().getInstanceName(), comment,
                        serverSize, availabilitySet, groupName, keyName);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // サーバにサービスを関連付ける
        if (componentNos != null && attachService) {
            instanceService.associateComponents(instanceNo, componentNos);
        }

        // 画面を閉じる
        close();
    }
}
