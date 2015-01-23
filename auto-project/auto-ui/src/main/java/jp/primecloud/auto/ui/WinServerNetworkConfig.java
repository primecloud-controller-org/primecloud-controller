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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.dto.InstanceNetworkDto;
import jp.primecloud.auto.service.dto.NetworkDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * ネットワーク追加/編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinServerNetworkConfig extends Window {

    Long instanceNo;

    Long platformNo;

    InstanceNetworkDto instanceNetworkDto;

    List<InstanceNetworkDto> instanceNetworkDtos;

    Application ap;

    ComboBox networkSelect;
    ComboBox ipModeSelect;
    TextField ipAddressField;
    TextField netmaskField;
    TextField gateWayField;
    TextField dns1Field;
    TextField dns2Field;
    OptionGroup primaryOpg;

    static final String CID_NETWORK  = "Network";
    static final String CID_IP_MODE  = "IpMode";
    static final String CID_PRIMARY  = "Primary";
    static final String COMBOBOX_WIDTH = "180px";

    Boolean isAddMode;
    Map<String, NetworkDto> networkMap = new LinkedHashMap<String, NetworkDto>();

    WinServerNetworkConfig(Application ap, Long instanceNo, Long platformNo,
            InstanceNetworkDto instanceNetworkDto, List<InstanceNetworkDto> instanceNetworkDtos) {
        this.ap = ap;
        this.instanceNo = instanceNo;
        this.platformNo = platformNo;
        this.instanceNetworkDto = instanceNetworkDto;
        this.isAddMode = (this.instanceNetworkDto == null) ? true: false;
        this.instanceNetworkDtos = instanceNetworkDtos;

        // Window
        if (isAddMode) {
            //ディスク追加画面
            setIcon(Icons.ADD.resource());
            setCaption(ViewProperties.getCaption("window.WinServerAddNetwork"));
        } else {
            //ディスク編集画面
            setIcon(Icons.EDIT.resource());
            setCaption(ViewProperties.getCaption("window.WinServerEditNetwork"));
        }
        setModal(true);
        setWidth("380px");
        setResizable(false);

        // Layout
        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        // Form
        Form form = new Form();

        //ネットワーク選択
        networkSelect = new ComboBox(ViewProperties.getCaption("field.netWork"));
        networkSelect.setWidth(COMBOBOX_WIDTH);
        networkSelect.setNullSelectionAllowed(false);
        networkSelect.setItemCaptionPropertyId(CID_NETWORK);
        networkSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        networkSelect.setImmediate(true);
        networkSelect.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                changeNetwork(event);
            }
        });


        //IPモード選択
        ipModeSelect = new ComboBox(ViewProperties.getCaption("caption.field.ipMode"));
        ipModeSelect.setWidth(COMBOBOX_WIDTH);
        ipModeSelect.setNullSelectionAllowed(false);
        ipModeSelect.setItemCaptionPropertyId(CID_IP_MODE);
        ipModeSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        ipModeSelect.setImmediate(true);
        ipModeSelect.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                changeIpMode(event);
            }
        });

        //IPアドレス
        ipAddressField = new TextField(ViewProperties.getCaption("field.ipAddress"));
        ipAddressField.setWidth("100%");
        ipAddressField.setImmediate(true);

        //ネットマスク
        netmaskField = new TextField(ViewProperties.getCaption("field.netmask"));
        netmaskField.setWidth("100%");
        netmaskField.setReadOnly(true);
        netmaskField.setImmediate(true);

        //ゲートウェイ
        gateWayField= new TextField(ViewProperties.getCaption("field.gateway"));
        gateWayField.setWidth("100%");
        gateWayField.setReadOnly(true);
        gateWayField.setImmediate(true);

        //DNS1
        dns1Field= new TextField(ViewProperties.getCaption("field.dns1"));
        dns1Field.setWidth("100%");
        dns1Field.setReadOnly(true);
        dns1Field.setImmediate(true);

        //DNS2
        dns2Field= new TextField(ViewProperties.getCaption("field.dns2"));
        dns2Field.setWidth("100%");
        dns2Field.setReadOnly(true);
        dns2Field.setImmediate(true);

        //Primary
        primaryOpg = new OptionGroup(ViewProperties.getCaption("field.primary"));
        primaryOpg.addItem(CID_PRIMARY);
        primaryOpg.setItemCaption(CID_PRIMARY,"");
        primaryOpg.setNullSelectionAllowed(false);
        primaryOpg.setImmediate(true);

        form.getLayout().addComponent(networkSelect);
        form.getLayout().addComponent(ipModeSelect);
        form.getLayout().addComponent(ipAddressField);
        form.getLayout().addComponent(netmaskField);
        form.getLayout().addComponent(gateWayField);
        form.getLayout().addComponent(dns1Field);
        form.getLayout().addComponent(dns2Field);
        form.getLayout().addComponent(primaryOpg);
        layout.addComponent(form);

        // 下部のバー
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setSpacing(true);
        buttonBar.setMargin(false, true, true, false);
        layout.addComponent(buttonBar);
        layout.setComponentAlignment(buttonBar, Alignment.BOTTOM_RIGHT);

        // OK Button
        Button btnOk = new Button(ViewProperties.getCaption("button.ok"));
        btnOk.setDescription(ViewProperties.getCaption("description.ok"));
        btnOk.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                okButtonClick(event);
            }
        });
        buttonBar.addComponent(btnOk);
        // [Enter]でOKボタンクリック
        btnOk.setClickShortcut(KeyCode.ENTER);

        //Cancel Button
        Button btnCancel = new Button(ViewProperties.getCaption("button.cancel"));
        btnCancel.setDescription(ViewProperties.getCaption("description.cancel"));
        btnCancel.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        buttonBar.addComponent(btnCancel);

        //データ取得
        initData();

        //データ表示
        showData();

        // 入力チェックの設定
        initValidation();
    }

    private void changeNetwork(Property.ValueChangeEvent event) {
        NetworkDto network = (NetworkDto) networkSelect.getValue();
        if (network != null) {
            ipModeSelect.setValue("POOL");

            netmaskField.setReadOnly(false);
            netmaskField.setValue(network.getNetmask());
            netmaskField.setReadOnly(true);

            gateWayField.setReadOnly(false);
            gateWayField.setValue(network.getGateWay());
            gateWayField.setReadOnly(true);

            dns1Field.setReadOnly(false);
            dns1Field.setValue(network.getDns1());
            dns1Field.setReadOnly(true);

            dns2Field.setReadOnly(false);
            dns2Field.setValue(network.getDns2());
            dns2Field.setReadOnly(true);
        } else {
            ipModeSelect.setValue("POOL");

            netmaskField.setReadOnly(false);
            netmaskField.setValue("");
            netmaskField.setReadOnly(true);

            gateWayField.setReadOnly(false);
            gateWayField.setValue("");
            gateWayField.setReadOnly(true);

            dns1Field.setReadOnly(false);
            dns1Field.setValue("");
            dns1Field.setReadOnly(true);

            dns2Field.setReadOnly(false);
            dns2Field.setValue("");
            dns2Field.setReadOnly(true);
        }
    }

    private void changeIpMode(Property.ValueChangeEvent event) {
        String ipMode = (String) ipModeSelect.getValue();
        if (StringUtils.isNotEmpty(ipMode)) {
            if ("MANUAL".equals(ipMode)) {
                ipAddressField.setReadOnly(false);
                ipAddressField.setValue("");
                ipAddressField.setRequired(true);
            } else {
                ipAddressField.setReadOnly(false);
                ipAddressField.setValue("");
                ipAddressField.setReadOnly(true);
                ipAddressField.setRequired(false);
            }
        }
    }

    private void initData() {
        IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
        List<NetworkDto> networks = describeService.getNetworks(ViewContext.getUserNo(), platformNo);
        for (NetworkDto network: networks) {
            networkMap.put(network.getNetworkName(), network);
        }
    }

    private void showData() {
        //ネットワーク選択
        networkSelect.setContainerDataSource(createNetworkContainer());
        if (isAddMode) {
            if (networkSelect.getItemIds().size() > 0) {
                NetworkDto networkDto = (NetworkDto) networkSelect.getItemIds().toArray()[0];
                networkSelect.select(networkDto);
            }
        } else {
            NetworkDto network = networkMap.get(instanceNetworkDto.getNetworkName());
            if (network != null) {
                networkSelect.select(network);
            }
            networkSelect.setReadOnly(true);
        }

        //IPモード
        ipModeSelect.setContainerDataSource(createIpModeContainer());
        if (isAddMode) {
            if (ipModeSelect.getItemIds().size() > 0) {
                String ipMode = (String) ipModeSelect.getItemIds().toArray()[0];
                ipModeSelect.select(ipMode);
            }
        } else {
            ipModeSelect.select(instanceNetworkDto.getIpMode());
        }

        //IPアドレス
        if (!isAddMode){
            String ipMode = (String) ipModeSelect.getValue();
            if ("MANUAL".equals(ipMode)) {
                ipAddressField.setReadOnly(false);
                ipAddressField.setValue(instanceNetworkDto.getIpAddress());
                ipAddressField.setRequired(true);
            } else {
                ipAddressField.setReadOnly(false);
                ipAddressField.setValue(instanceNetworkDto.getIpAddress());
                ipAddressField.setReadOnly(true);
                ipAddressField.setRequired(false);
            }
        }

        //プライマリ
        if (!isAddMode && BooleanUtils.isTrue(instanceNetworkDto.isPrimary())) {
            primaryOpg.select(CID_PRIMARY);
        }
    }

    private IndexedContainer createNetworkContainer() {
        IndexedContainer networkContainer = new IndexedContainer();
        networkContainer.addContainerProperty(CID_NETWORK, String.class, null);

        for (NetworkDto network: networkMap.values()) {
            //新規追加時に必須ネットワーク(PCC)は表示されない
            if (isAddMode && network.isPcc()) {
                continue;
            }
            Item item = networkContainer.addItem(network);
            item.getItemProperty(CID_NETWORK).setValue(network.getNetworkName());
        }

        return networkContainer;
    }

    private IndexedContainer createIpModeContainer() {
        IndexedContainer ipModeContainer = new IndexedContainer();
        ipModeContainer.addContainerProperty(CID_IP_MODE, String.class, null);

        //POOL
        Item item = ipModeContainer.addItem("POOL");
        item.getItemProperty(CID_IP_MODE).setValue(ViewProperties.getCaption("field.ipMode.pool"));

        //MANUAL
        item = ipModeContainer.addItem("MANUAL");
        item.getItemProperty(CID_IP_MODE).setValue(ViewProperties.getCaption("field.ipMode.manual"));

        //DHCP
        item = ipModeContainer.addItem("DHCP");
        item.getItemProperty(CID_IP_MODE).setValue(ViewProperties.getCaption("field.ipMode.dhcp"));

        return ipModeContainer;
    }

    private void initValidation() {
        //ネットワーク選択
        String message = ViewMessages.getMessage("IUI-000100");
        networkSelect.setRequired(true);
        networkSelect.setRequiredError(message);

        //IPモード選択
        message = ViewMessages.getMessage("IUI-000126");
        ipModeSelect.setRequired(true);
        ipModeSelect.setRequiredError(message);

        //IPアドレス
        message = ViewMessages.getMessage("IUI-000094", ipAddressField.getCaption());
        ipAddressField.setRequired(false);
        ipAddressField.setRequiredError(message);
        Validator ipAddressFieldValidator = new RegexpValidator(
                "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                ViewMessages.getMessage("IUI-000095", ipAddressField.getCaption()));
        ipAddressField.addValidator(ipAddressFieldValidator);
    }

    private void okButtonClick(ClickEvent event) {
        // 入力値を取得
        NetworkDto networkDto = (NetworkDto) networkSelect.getValue();
        String ipMode = (String) ipModeSelect.getValue();
        String ipAddress = (String) ipAddressField.getValue();

        // 入力チェック
        try {
            //基本バリデーション
            networkSelect.validate();
            ipModeSelect.validate();
            ipAddressField.validate();

            //カスタムチェック
            if ("MANUAL".equals(ipMode)) {
                IaasDescribeService describeService = BeanContext.getBean(IaasDescribeService.class);
                String otherInstanceName = describeService.hasIpAddresse(platformNo, instanceNo, ipAddress);
                if (StringUtils.isNotEmpty(otherInstanceName)) {
                    //すでにIPが同ネットワーク内で割り当てられている場合
                    throw new InvalidValueException(ViewMessages.getMessage("IUI-000128", ipAddress, otherInstanceName));
                }
            }
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 入力値を格納
        if (isAddMode) {
            //追加
            instanceNetworkDto = new InstanceNetworkDto();
            instanceNetworkDto.setNetworkName(networkDto.getNetworkName());
            instanceNetworkDto.setIpMode(ipMode);
            instanceNetworkDto.setIpAddress(ipAddress);
            instanceNetworkDto.setPrimary(primaryOpg.isSelected(CID_PRIMARY));
            instanceNetworkDto.setNew(true);
            instanceNetworkDto.setDelete(false);
            instanceNetworkDto.setRequired(false);
            instanceNetworkDtos.add(instanceNetworkDto);
        } else {
            //変更
            instanceNetworkDto.setIpMode(ipMode);
            instanceNetworkDto.setIpAddress(ipAddress);
            instanceNetworkDto.setPrimary(primaryOpg.isSelected(CID_PRIMARY));
            instanceNetworkDto.setDelete(false);
        }

        if (BooleanUtils.isTrue(instanceNetworkDto.isPrimary())) {
            //プライマリが設定された場合、対象ネットワーク以外のネットワークを非プライマリにする
            for (InstanceNetworkDto tempDto: instanceNetworkDtos) {
                if (!instanceNetworkDto.equals(tempDto)) {
                    tempDto.setPrimary(false);
                }
            }
        }

        // ログイン画面を閉じる
        close();
    }
}
