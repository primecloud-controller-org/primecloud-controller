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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.ComponentStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.AzureDisk;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.NiftyVolume;
import jp.primecloud.auto.entity.crud.OpenstackVolume;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.process.ComponentConstants;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.ComponentTypeDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * サービスの編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class WinServiceEdit extends Window {

    final String TAB_HEIGHT = "352px";

    Application apl;

    Long componentNo;

    TabSheet tab = new TabSheet();

    BasicTab basicTab;

    DetailTab detailTab;

    ComponentDto component;

    ComponentTypeDto componentType;

    List<InstanceDto> instances;

    WinServiceEdit(Application ap, Long componentNo) {
        apl = ap;
        this.componentNo = componentNo;

        // 初期データの取得
        initData();

        //モーダルウインドウ
        setIcon(Icons.EDITMINI.resource());
        setCaption(ViewProperties.getCaption("window.winServiceEdit"));
        setModal(true);
        setWidth("600px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        // タブ設定
        layout.addComponent(tab);

        basicTab = new BasicTab();
        tab.addTab(basicTab, ViewProperties.getCaption("tab.basic"), Icons.BASIC.resource());

        detailTab = new DetailTab();
        Boolean useCustomPara = BooleanUtils.toBooleanObject(Config.getProperty("userCustomize.useCustomParam"));
        if (BooleanUtils.isTrue(useCustomPara)) {
            tab.addTab(detailTab, ViewProperties.getCaption("tab.detail"), Icons.BASIC.resource());
        }

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editService.ok"));
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

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        okbar.addComponent(cancelButton);

        // 入力チェックの設定
        initValidation();

        // 基本設定のデータ表示
        basicTab.showData();

        // 詳細設定のデータ表示
        detailTab.showData();
    }

    private class BasicTab extends VerticalLayout {

        Form form = new Form();

        TextField serviceNameField;

        Label serviceLabel;

        TextField commentField;

        TextField diskSizeField;

        ServerSelect serverSelect;

        BasicTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // サービス名
            serviceNameField = new TextField(ViewProperties.getCaption("field.serviceName"));
            form.getLayout().addComponent(serviceNameField);
            form.addStyleName("win-service-edit-form");

            // サービス
            CssLayout layout = new CssLayout();
            layout.setWidth("100%");
            layout.setCaption(ViewProperties.getCaption("field.service"));
            serviceLabel = new Label("");

            layout.addComponent(serviceLabel);
            form.getLayout().addComponent(layout);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("90%");
            form.getLayout().addComponent(commentField);

            // ディスクサイズ
            diskSizeField = new TextField(ViewProperties.getCaption("field.diskSize"));
            form.getLayout().addComponent(diskSizeField);

            // サーバ選択
            AbsoluteLayout hlay = new AbsoluteLayout();
            hlay.setWidth("100%");
            hlay.setHeight("20px");
            hlay.setStyleName("serverselect");
            Label selectLbl = new Label(ViewProperties.getCaption("label.serverSelectable"));
            Label selectedLbl = new Label(ViewProperties.getCaption("label.serverSelected"));
            hlay.addComponent(selectLbl, "left:10%");
            hlay.addComponent(selectedLbl, "left:65%");

            form.getLayout().addComponent(hlay);

            serverSelect = new ServerSelect();
            serverSelect.setWidth("100%");
            //Runningのものを外せないようにするチェック
            serverSelect.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    valueChangeValidate(event, serverSelect);
                }
            });

            form.getLayout().addComponent(serverSelect);

            // 新規サーバ追加ボタン
            Button addServerButton = new Button(ViewProperties.getCaption("button.addServerQuick"));
            addServerButton.setDescription(ViewProperties.getCaption("description.addServerQuick"));
            addServerButton.setIcon(Icons.ADD.resource());
            //サーバ作成権限のチェック(無い場合は非活性)
            UserAuthDto auth = ViewContext.getAuthority();
            addServerButton.setEnabled(auth.isServerMake());

            addServerButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    // 表示しているサービスのcomponentTypeNoを取得
                    final Long componentTypeNo = componentType.getComponentType().getComponentTypeNo();

                    // 選択されているサーバ名を取得
                    final Collection<String> selectedServerNames = (Collection<String>) serverSelect.getValue();

                    WinServerAddSimple winServerAddSimple = new WinServerAddSimple(getApplication(), componentTypeNo);
                    winServerAddSimple.addListener(new CloseListener() {
                        @Override
                        public void windowClose(CloseEvent e) {
                            // 作成したサーバ名を取得
                            List<String> addedServerNames = (List<String>) ContextUtils.getAttribute("serverNames");

                            if (addedServerNames != null) {
                                ContextUtils.removeAttribute("serverNames");

                                // 表示データを初期化
                                initData();

                                // サーバ情報を表示
                                serverSelect.showData();

                                // 元のサーバ名と作成したサーバ名を選択する
                                List<String> serverNames = new ArrayList<String>(selectedServerNames);
                                serverNames.addAll(addedServerNames);
                                serverSelect.setValue(serverNames);
                            }
                        }
                    });
                    getWindow().getApplication().getMainWindow().addWindow(winServerAddSimple);
                }
            });
            HorizontalLayout addlay = new HorizontalLayout();
            Label txt = new Label(ViewProperties.getCaption("label.addServerQuick"));
            addlay.addComponent(addServerButton);
            addlay.addComponent(txt);
            addlay.setComponentAlignment(txt, Alignment.MIDDLE_LEFT);

            form.getLayout().addComponent(addlay);

            form.setReadOnly(true);
            addComponent(form);
        }

        private void showData() {
            // サービス名
            serviceNameField.setReadOnly(false);
            serviceNameField.setValue(component.getComponent().getComponentName());
            serviceNameField.setReadOnly(true);

            // サービス
            String name = componentType.getComponentType().getComponentTypeNameDisp();
            serviceLabel.setCaption(name);

            // コメント
            String comment = component.getComponent().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            // ディスクサイズ
            String diskSize = null;
            for (ComponentConfig config : component.getComponentConfigs()) {
                if ("diskSize".equals(config.getConfigName())) {
                    diskSize = config.getConfigValue();
                    break;
                }
            }
            if (diskSize != null) {
                diskSizeField.setValue(diskSize);
            }

            // ディスクが１つでも存在する場合、ディスクサイズを変更できないようにする
            int countDisk = 0;
            for (InstanceDto dto : instances) {
                if (dto.getAwsVolumes() != null) {
                    for (AwsVolume awsVolume : dto.getAwsVolumes()) {
                        if (componentNo.equals(awsVolume.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (dto.getCloudstackVolumes() != null) {
                    for (CloudstackVolume csVolume : dto.getCloudstackVolumes()) {
                        if (componentNo.equals(csVolume.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (dto.getVmwareDisks() != null) {
                    for (VmwareDisk vmwareDisk : dto.getVmwareDisks()) {
                        if (componentNo.equals(vmwareDisk.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (dto.getVcloudDisks() != null) {
                    for (VcloudDisk vcloudDisk : dto.getVcloudDisks()) {
                        if (componentNo.equals(vcloudDisk.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (dto.getAzureDisks() != null) {
                    for (AzureDisk azureDisk : dto.getAzureDisks()) {
                        if (componentNo.equals(azureDisk.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (dto.getNiftyVolumes() != null) {
                    for (NiftyVolume niftyVolume : dto.getNiftyVolumes()) {
                        if (componentNo.equals(niftyVolume.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (dto.getOpenstackVolumes() != null) {
                    for (OpenstackVolume openstackVolume : dto.getOpenstackVolumes()) {
                        if (componentNo.equals(openstackVolume.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
            }
            if (countDisk > 0) {
                diskSizeField.setReadOnly(true);
            }

            // サーバ選択
            serverSelect.showData();
        }

    }

    public class ServerSelect extends TwinColSelect {

        public ServerSelect() {
            setCaption(ViewProperties.getCaption("field.selectServer"));
            setRows(7);
            setNullSelectionAllowed(true);
            setMultiSelect(true);
            setImmediate(true);
            setStyleName("serverselect");
        }

        private void showData() {
            removeAllItems();

            // 利用可能なサーバ情報を選択画面に表示
            for (Long instanceNo : componentType.getInstanceNos()) {
                for (InstanceDto instance : instances) {
                    if (!instanceNo.equals(instance.getInstance().getInstanceNo())) {
                        continue;
                    }
                    ComponentInstanceDto componentInstance = null;
                    for (ComponentInstanceDto componentInstanceDto : component.getComponentInstances()) {
                        if (componentInstanceDto.getComponentInstance().getInstanceNo()
                                .equals(instance.getInstance().getInstanceNo())) {
                            componentInstance = componentInstanceDto;
                            break;
                        }
                    }
                    if (componentInstance != null) {
                        ComponentInstanceStatus componentInstanceStatus = ComponentInstanceStatus
                                .fromStatus(componentInstance.getComponentInstance().getStatus());
                        String status = StringUtils.capitalize(componentInstanceStatus.toString().toLowerCase());
                        //関連付けがないものは未選択状態にする
                        if (BooleanUtils.isNotTrue(componentInstance.getComponentInstance().getAssociate())) {
                            addItem(instance.getInstance().getInstanceName() + " (" + status + ")");
                        } else {
                            addItem(instance.getInstance().getInstanceName() + " (" + status + ")");
                            select(instance.getInstance().getInstanceName() + " (" + status + ")");
                        }
                    } else {
                        addItem(instance.getInstance().getInstanceName());
                    }

                }
            }

            // サービスに属するサーバ情報を選択画面に表示して選択
            for (ComponentInstanceDto componentInstance : component.getComponentInstances()) {
                // 関連付けが無効のものは除外
                if (BooleanUtils.isNotTrue(componentInstance.getComponentInstance().getAssociate())) {
                    continue;
                }

                for (InstanceDto instance : instances) {
                    ComponentInstanceStatus componentInstanceStatus = ComponentInstanceStatus
                            .fromStatus(componentInstance.getComponentInstance().getStatus());
                    String status = StringUtils.capitalize(componentInstanceStatus.toString().toLowerCase());

                    if (componentInstance.getComponentInstance().getInstanceNo()
                            .equals(instance.getInstance().getInstanceNo())) {
                        addItem(instance.getInstance().getInstanceName() + " (" + status + ")");
                        select(instance.getInstance().getInstanceName() + " (" + status + ")");
                        break;
                    }
                }
            }
        }

        public void select(Collection<Object> itemIds) {
            if (isMultiSelect()) {
                Set<Object> set = new HashSet<Object>((Set<?>) getValue());
                for (Object itemId : itemIds) {
                    if (!isSelected(itemId) && itemId != null && items.containsId(itemId)) {
                        set.add(itemId);
                    }
                }
                setValue(set);
            }
        }

    }

    public void valueChangeValidate(ValueChangeEvent event, final ServerSelect serverSelect) {
        Set<String> selectedItemsProperty = (Set<String>) event.getProperty().getValue();
        final Collection<String> notSelectedList = new ArrayList<String>();
        for (Object item : serverSelect.getItemIds()) {
            notSelectedList.add(item.toString());
        }
        notSelectedList.removeAll(selectedItemsProperty);

        Collection<Object> moveList = new ArrayList<Object>();
        //ステータスがSTOPPEDではなくかつステータスを含む場合
        for (String notSelectedItem : notSelectedList) {
            if (!StringUtils.contains(notSelectedItem.toUpperCase(), "(" + ComponentInstanceStatus.STOPPED.toString()
                    + ")")
                    && StringUtils.contains(notSelectedItem, "(")) {
                moveList.add(notSelectedItem);
            }
        }
        //選択しているものを移動しなおす
        serverSelect.select(moveList);

        //移動しなおすものがあればエラーを表示する
        if (!moveList.isEmpty()) {
            String message = ViewMessages.getMessage("IUI-000039");
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message, Buttons.OK);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 選択されていないものの中にディスクがアタッチされたままのものがあるかどうかチェック
        notSelectedList.removeAll(moveList);
        moveList = new ArrayList<Object>();

        for (String notSelectedItem : notSelectedList) {
            int index = notSelectedItem.indexOf(" ");
            String instanceName = index == -1 ? notSelectedItem : notSelectedItem.substring(0, index);
            // pcc-apiでも同様の処理が必要の為、サービスに切り出す
            ComponentService componentService = BeanContext.getBean(ComponentService.class);
            moveList = componentService.checkAttachDisk(ViewContext.getFarmNo(), componentNo, instanceName,
                    notSelectedItem, moveList);
        }

        if (!moveList.isEmpty()) {
            // ディスクがアタッチされたままのものを選択しなおす
            serverSelect.select(moveList);

            // エラー表示
            String message = ViewMessages.getMessage("IUI-000096");
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message, Buttons.OK);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

    }

    public class DetailTab extends VerticalLayout {

        Form form = new Form();

        TextField customParam1Feild;

        TextField customParam2Feild;

        TextField customParam3Feild;

        DetailTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // カスタムパラメータ1
            customParam1Feild = new TextField(ViewProperties.getCaption("field.customParam1"));
            form.getLayout().addComponent(customParam1Feild);
            customParam1Feild.setWidth("90%");

            // カスタムパラメータ2
            customParam2Feild = new TextField(ViewProperties.getCaption("field.customParam2"));
            form.getLayout().addComponent(customParam2Feild);
            customParam2Feild.setWidth("90%");

            // カスタムパラメータ3
            customParam3Feild = new TextField(ViewProperties.getCaption("field.customParam3"));
            form.getLayout().addComponent(customParam3Feild);
            customParam3Feild.setWidth("90%");

            //活性or非活性
            ComponentStatus status = ComponentStatus.fromStatus(component.getStatus());
            if (ComponentStatus.STOPPED != status) {
                //サービスステータスがStopped以外の場合は、カスタムパラメータ1～3は変更不可とする。
                form.setEnabled(false);
            }

            form.setReadOnly(true);
            addComponent(form);
        }

        private void showData() {
            // カスタムパラメータ1
            String customParam1 = null;
            for (ComponentConfig config : component.getComponentConfigs()) {
                if (ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_1.equals(config.getConfigName())) {
                    customParam1 = config.getConfigValue();
                    break;
                }
            }
            if (customParam1 != null) {
                customParam1Feild.setValue(customParam1);
            }

            // カスタムパラメータ2
            String customParam2 = null;
            for (ComponentConfig config : component.getComponentConfigs()) {
                if (ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_2.equals(config.getConfigName())) {
                    customParam2 = config.getConfigValue();
                    break;
                }
            }
            if (customParam2 != null) {
                customParam2Feild.setValue(customParam2);
            }

            // カスタムパラメータ3
            String customParam3 = null;
            for (ComponentConfig config : component.getComponentConfigs()) {
                if (ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_3.equals(config.getConfigName())) {
                    customParam3 = config.getConfigValue();
                    break;
                }
            }
            if (customParam3 != null) {
                customParam3Feild.setValue(customParam3);
            }
        }

    }

    private void initValidation() {
        String message = ViewMessages.getMessage("IUI-000003");
        basicTab.commentField.addValidator(new StringLengthValidator(message, -1, 100, true));

        message = ViewMessages.getMessage("IUI-000032");
        basicTab.diskSizeField.setRequired(true);
        basicTab.diskSizeField.setRequiredError(message);
        basicTab.diskSizeField.addValidator(new RegexpValidator("^[1-9]|[1-9][0-9]{1,2}|1000$", true, message));

        message = ViewMessages.getMessage("IUI-000113");
        detailTab.customParam1Feild.addValidator(new StringLengthValidator(message, -1, 200, true));
        detailTab.customParam1Feild.addValidator(new RegexpValidator(
                "^[0-9a-zA-Z-,._][0-9a-zA-Z-,._ ]*[0-9a-zA-Z-,._]$", true, message));

        message = ViewMessages.getMessage("IUI-000114");
        detailTab.customParam2Feild.addValidator(new StringLengthValidator(message, -1, 200, true));
        detailTab.customParam2Feild.addValidator(new RegexpValidator(
                "^[0-9a-zA-Z-,._][0-9a-zA-Z-,._ ]*[0-9a-zA-Z-,._]$", true, message));

        message = ViewMessages.getMessage("IUI-000115");
        detailTab.customParam3Feild.addValidator(new StringLengthValidator(message, -1, 200, true));
        detailTab.customParam3Feild.addValidator(new RegexpValidator(
                "^[0-9a-zA-Z-,._][0-9a-zA-Z-,._ ]*[0-9a-zA-Z-,._]$", true, message));
    }

    private void initData() {
        // サービス情報を取得
        // TODO: ロジックを必ずリファクタリングすること！
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        List<ComponentDto> components = componentService.getComponents(ViewContext.getFarmNo());
        for (ComponentDto component : components) {
            if (componentNo.equals(component.getComponent().getComponentNo())) {
                this.component = component;
                break;
            }
        }

        // 利用可能なサーバを取得
        componentType = componentService.getComponentType(component.getComponent().getComponentNo());

        // サーバ情報を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        instances = instanceService.getInstances(ViewContext.getFarmNo());
    }

    private void okButtonClick(ClickEvent event) {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String diskSize = (String) basicTab.diskSizeField.getValue();
        Collection<String> serverNames = (Collection<String>) basicTab.serverSelect.getValue();

        String customParam1 = (String) detailTab.customParam1Feild.getValue();
        String customParam2 = (String) detailTab.customParam2Feild.getValue();
        String customParam3 = (String) detailTab.customParam3Feild.getValue();

        // 選択されたサーバのinstanceNoのリスト
        List<Long> instanceNos = new ArrayList<Long>();
        for (InstanceDto instance : instances) {
            for (String serverName : serverNames) {
                int index = serverName.indexOf(" ");
                if (index != -1) {
                    serverName = serverName.substring(0, index);
                }
                if (StringUtils.equals(serverName, instance.getInstance().getInstanceName())) {
                    instanceNos.add(instance.getInstance().getInstanceNo());
                }
            }
        }

        // TODO: 入力チェック
        try {
            basicTab.commentField.validate();
            basicTab.diskSizeField.validate();

            detailTab.customParam1Feild.validate();
            detailTab.customParam2Feild.validate();
            detailTab.customParam3Feild.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        //オペレーションログ
        AutoApplication aapl = (AutoApplication) apl;
        aapl.doOpLog("SERVICE", "Edit Service", null, componentNo, null, null);

        // サービスを更新
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        try {
            componentService.updateComponent(componentNo, comment, Integer.valueOf(diskSize), customParam1,
                    customParam2, customParam3);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サービスにサーバを適用
        try {
            componentService.associateInstances(componentNo, instanceNos);
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
