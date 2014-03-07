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
import java.util.List;

import jp.primecloud.auto.common.component.Subnet;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.NiftyDescribeService;
import jp.primecloud.auto.service.VmwareDescribeService;
import jp.primecloud.auto.service.dto.AddressDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.VmwareAddressDto;
import jp.primecloud.auto.service.dto.ZoneDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.Application;
import com.vaadin.data.Item;
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
        if ("aws".equals(platformType)) {
            awsDetailTab = new AWSDetailTab();
            tab.addTab(awsDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            awsDetailTab.initValidation();
            // 詳細設定のデータ表示
            awsDetailTab.showData();
        } else if ("vmware".equals(platformType)) {
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

        } else if ("nifty".equals(platformType)) {
            niftyDetailTab = new NiftyDetailTab();
            tab.addTab(niftyDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            niftyDetailTab.initValidation();
            // 詳細設定のデータ表示
            niftyDetailTab.showData();

        } else if ("cloudstack".equals(platformType)) {
            cloudStackDetailTab = new CloudStackDetailTab();
            tab.addTab(cloudStackDetailTab, ViewProperties.getCaption("tab.detail"), Icons.DETAIL.resource());
            layout.addComponent(tab);
            // 入力チェックの設定
            cloudStackDetailTab.initValidation();
            // 詳細設定のデータ表示
            cloudStackDetailTab.showData();
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

            // TODO: アイコン名の取得ロジックのリファクタリング
            String cloudName = platformDto.getPlatform().getPlatformNameDisp();
            Icons cloudIcon = Icons.NONE;
            String platformType = platformDto.getPlatform().getPlatformType();
            if ("aws".equals(platformType)) {
                if (platformDto.getPlatformAws().getEuca()) {
                    cloudIcon = Icons.EUCALYPTUS;
                } else {
                    cloudIcon = Icons.AWS;
                }
            } else if ("vmware".equals(platformType)) {
                cloudIcon = Icons.VMWARE;
            } else if ("nifty".equals(platformType)) {
                cloudIcon = Icons.NIFTY;
            } else if ("cloudstack".equals(platformType)) {
                cloudIcon = Icons.CLOUD_STACK;
            }
            cloudLabel.setCaption(cloudName);
            cloudLabel.setIcon(cloudIcon.resource());

            if (image != null) {
                // TODO: アイコン名の取得ロジックのリファクタリング
                String imageName = image.getImage().getImageNameDisp();
                String iconName = StringUtils.substringBefore(image.getImage().getImageName(), "_");
                Icons imageIcon;
                if ("application".equals(iconName)) {
                    imageIcon = Icons.PAAS;
                } else if ("prjserver".equals(iconName)) {
                    imageIcon = Icons.PRJSERVER;
                }else if ("windows".equals(iconName)) {
                    imageIcon = Icons.WINDOWS_APP;
                } else if ("ultramonkey".equals(iconName)) {
                    imageIcon = Icons.LOADBALANCER_TAB;
                } else {
                    imageIcon = Icons.fromName(iconName);
                }
                imageLabel.setCaption(imageName);
                imageLabel.setIcon(imageIcon.resource());

                // TODO: アイコン名の取得ロジックのリファクタリング
                String osName = image.getImage().getOsDisp();
                Icons osIcon = Icons.NONE;
                if (image.getImage().getOs().startsWith("centos")) {
                    osIcon = Icons.CENTOS;
                } else if (image.getImage().getOs().startsWith("windows")) {
                    osIcon = Icons.WINDOWS;
                }
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
                    for (ComponentType componentType: componentTypes) {
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
                for (SubnetDto subnetDto: subnets) {
                    if (subnetDto.getSubnetId().equals(instance.getAwsInstance().getSubnetId())) {
                        subnetSelect.select(subnetDto);
                        break;
                    }
                }
            }
            SubnetDto subnetDto = (SubnetDto)subnetSelect.getValue();
            if (subnetDto != null) {
                //サブネットが存在する場合は編集不可
                subnetSelect.setEnabled(false);
            }

            grpSelect.setContainerDataSource(new IndexedContainer(securityGroups));
            grpSelect.select(instance.getAwsInstance().getSecurityGroups());

            sizeSelect.setContainerDataSource(new IndexedContainer(instanceTypes));
            sizeSelect.select(instance.getAwsInstance().getInstanceType());

            zoneSelect.setContainerDataSource(createZoneContainer());
            for (ZoneDto zoneDto: zones) {
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
                for (AddressDto addressDto: elasticIps) {
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

            for (SubnetDto subnetDto: subnets) {
                Item item = subnetContainer.addItem(subnetDto);
                item.getItemProperty(CIDR_BLOCK_CAPTION_ID).setValue(subnetDto.getCidrBlock());
            }

            return subnetContainer;
        }

        private IndexedContainer createZoneContainer() {
            IndexedContainer zoneContainer = new IndexedContainer();
            zoneContainer.addContainerProperty(ZONE_CAPTION_ID, String.class, null);

            for (ZoneDto zoneDto: zones) {
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
            if (StringUtils.startsWith(image.getImage().getOs(), "windows")) {
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
            if (!instance.getInstance().getStatus().equalsIgnoreCase("STOPPED")) {
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
            if (!instance.getInstance().getStatus().equalsIgnoreCase("STOPPED")) {
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
                if(zone.isSameById(instance.getCloudstackInstance().getZoneid())){
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


    private void initData() {
        // サーバ情報を取得
        // TODO: ロジックを必ずリファクタリングすること！
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        this.instance = instanceService.getInstance(instanceNo);

        //LBかどうか
        this.isLoadBalancer =  BooleanUtils.isTrue(instance.getInstance().getLoadBalancer());

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
        if ("aws".equals(platformType)) {
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
                instanceTypes.add(instanceType);
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

        } else if ("vmware".equals(platformType)) {
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
                instanceTypes.add(instanceType);
            }
        } else if ("nifty".equals(platformType)) {
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
                instanceTypes.add(instanceType);
            }
        } else if ("cloudstack".equals(platformType)){
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
                instanceTypes.add(instanceType);
            }

            zones = describeService.getAvailabilityZones(ViewContext.getUserNo(), platform.getPlatformNo());

            elasticIps = new ArrayList<AddressDto>();
            List<AddressDto> addresses = describeService.getAddresses(ViewContext.getUserNo(), platform.getPlatformNo());
            for (AddressDto address : addresses) {
                elasticIps.add(address);
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

        // TODO: 入力チェック
        try {
            basicTab.commentField.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        if ("aws".equals(platformDto.getPlatform().getPlatformType())) {
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

        } else if ("vmware".equals(platformDto.getPlatform().getPlatformType())) {
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
        } else if ("nifty".equals(platformDto.getPlatform().getPlatformType())) {
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
        } else if ("cloudstack".equals(platformDto.getPlatform().getPlatformType())) {
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
        }

        //TODO LOG
        AutoApplication aapl =  (AutoApplication)apl;
        aapl.doOpLog("SERVER", "Edit Server", instanceNo, null, null, null);

        // サーバを更新
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);

        if ("aws".equals(platformDto.getPlatform().getPlatformType())) {
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
        } else if ("vmware".equals(platformDto.getPlatform().getPlatformType())) {
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
        } else if ("nifty".equals(platformDto.getPlatform().getPlatformType())) {
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
        } else if ("cloudstack".equals(platformDto.getPlatform().getPlatformType())){
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

        }

        // サーバにサービスを関連付ける
        if (componentNos != null && attachService) {
            instanceService.associateComponents(instanceNo, componentNos);
        }

        // 画面を閉じる
        close();
    }

}
