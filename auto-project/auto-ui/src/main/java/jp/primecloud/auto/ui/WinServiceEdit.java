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
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.AzureDisk;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.NiftyVolume;
import jp.primecloud.auto.entity.crud.OpenstackVolume;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.process.ComponentConstants;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.ComponentTypeDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
@SuppressWarnings("serial")
public class WinServiceEdit extends Window {

    private final String TAB_HEIGHT = "352px";

    private Long componentNo;

    private BasicTab basicTab;

    private DetailTab detailTab;

    private ComponentDto component;

    private List<Long> availableInstanceNos;

    private List<InstanceDto> instances;

    public WinServiceEdit(Long componentNo) {
        this.componentNo = componentNo;
    }

    @Override
    public void attach() {
        // モーダルウインドウ
        setIcon(Icons.EDITMINI.resource());
        setCaption(ViewProperties.getCaption("window.winServiceEdit"));
        setModal(true);
        setWidth("600px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        // タブ設定
        TabSheet tab = new TabSheet();
        layout.addComponent(tab);

        // 基本設定タブ
        basicTab = new BasicTab();
        tab.addTab(basicTab, ViewProperties.getCaption("tab.basic"), Icons.BASIC.resource());

        // 詳細設定タブ
        Boolean useCustomParam = BooleanUtils.toBooleanObject(Config.getProperty("userCustomize.useCustomParam"));
        if (BooleanUtils.isTrue(useCustomParam)) {
            detailTab = new DetailTab();
            tab.addTab(detailTab, ViewProperties.getCaption("tab.detail"), Icons.BASIC.resource());
        }

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editService.ok"));
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

        // サービス情報の表示
        loadData();
        basicTab.show(component, instances, availableInstanceNos);
        if (detailTab != null) {
            detailTab.show(component);
        }
    }

    private class BasicTab extends VerticalLayout {

        private TextField serviceNameField;

        private Label serviceLabel;

        private TextField commentField;

        private TextField diskSizeField;

        private ServerSelect serverSelect;

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            Form form = new Form();

            // サービス名
            serviceNameField = new TextField(ViewProperties.getCaption("field.serviceName"));
            serviceNameField.setReadOnly(true);
            form.getLayout().addComponent(serviceNameField);
            form.addStyleName("win-service-edit-form");

            // サービス
            serviceLabel = new Label("");
            CssLayout serviceLayout = new CssLayout();
            serviceLayout.setWidth("100%");
            serviceLayout.setCaption(ViewProperties.getCaption("field.service"));
            serviceLayout.addComponent(serviceLabel);
            form.getLayout().addComponent(serviceLayout);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("90%");
            form.getLayout().addComponent(commentField);

            // ディスクサイズ
            diskSizeField = new TextField(ViewProperties.getCaption("field.diskSize"));
            form.getLayout().addComponent(diskSizeField);

            // サーバ選択
            AbsoluteLayout layout = new AbsoluteLayout();
            layout.setWidth("100%");
            layout.setHeight("20px");
            layout.setStyleName("serverselect");
            Label selectLbl = new Label(ViewProperties.getCaption("label.serverSelectable"));
            Label selectedLbl = new Label(ViewProperties.getCaption("label.serverSelected"));
            layout.addComponent(selectLbl, "left:10%");
            layout.addComponent(selectedLbl, "left:65%");
            form.getLayout().addComponent(layout);

            serverSelect = new ServerSelect();
            serverSelect.setWidth("100%");
            serverSelect.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    valueChangeValidate();
                }
            });
            form.getLayout().addComponent(serverSelect);

            // 新規サーバ追加ボタン
            Button addServerButton = new Button(ViewProperties.getCaption("button.addServerQuick"));
            addServerButton.setDescription(ViewProperties.getCaption("description.addServerQuick"));
            addServerButton.setIcon(Icons.ADD.resource());
            addServerButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    addButtonClick(event);
                }
            });

            HorizontalLayout layout2 = new HorizontalLayout();
            Label label = new Label(ViewProperties.getCaption("label.addServerQuick"));
            layout2.addComponent(addServerButton);
            layout2.addComponent(label);
            layout2.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
            form.getLayout().addComponent(layout2);

            form.setReadOnly(true);
            addComponent(form);

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));

            message = ViewMessages.getMessage("IUI-000032");
            diskSizeField.setRequired(true);
            diskSizeField.setRequiredError(message);
            diskSizeField.addValidator(new RegexpValidator("^[1-9]|[1-9][0-9]{1,2}|1000$", true, message));
        }

        private void show(ComponentDto component, List<InstanceDto> instances, List<Long> availableInstanceNos) {
            // サービス名
            serviceNameField.setReadOnly(false);
            serviceNameField.setValue(component.getComponent().getComponentName());
            serviceNameField.setReadOnly(true);

            // サービス
            String name = component.getComponentType().getComponentTypeNameDisp();
            serviceLabel.setCaption(name);

            // コメント
            String comment = component.getComponent().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            // ディスクサイズ
            String diskSize = null;
            for (ComponentConfig config : component.getComponentConfigs()) {
                if (ComponentConstants.CONFIG_NAME_DISK_SIZE.equals(config.getConfigName())) {
                    diskSize = config.getConfigValue();
                    break;
                }
            }
            if (diskSize != null) {
                diskSizeField.setValue(diskSize);
            }

            // ディスクが1つでも存在する場合、ディスクサイズを変更できないようにする
            int countDisk = 0;
            for (InstanceDto instance : instances) {
                if (instance.getAwsVolumes() != null) {
                    for (AwsVolume awsVolume : instance.getAwsVolumes()) {
                        if (componentNo.equals(awsVolume.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (instance.getCloudstackVolumes() != null) {
                    for (CloudstackVolume csVolume : instance.getCloudstackVolumes()) {
                        if (componentNo.equals(csVolume.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (instance.getVmwareDisks() != null) {
                    for (VmwareDisk vmwareDisk : instance.getVmwareDisks()) {
                        if (componentNo.equals(vmwareDisk.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (instance.getVcloudDisks() != null) {
                    for (VcloudDisk vcloudDisk : instance.getVcloudDisks()) {
                        if (componentNo.equals(vcloudDisk.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (instance.getAzureDisks() != null) {
                    for (AzureDisk azureDisk : instance.getAzureDisks()) {
                        if (componentNo.equals(azureDisk.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (instance.getNiftyVolumes() != null) {
                    for (NiftyVolume niftyVolume : instance.getNiftyVolumes()) {
                        if (componentNo.equals(niftyVolume.getComponentNo())) {
                            countDisk++;
                            break;
                        }
                    }
                }
                if (instance.getOpenstackVolumes() != null) {
                    for (OpenstackVolume openstackVolume : instance.getOpenstackVolumes()) {
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

            // 利用可能なサーバ情報を選択画面に表示
            serverSelect.show(component, instances, availableInstanceNos);
        }
    }

    public class ServerSelect extends TwinColSelect {

        @Override
        public void attach() {
            setCaption(ViewProperties.getCaption("field.selectServer"));
            setRows(7);
            setNullSelectionAllowed(true);
            setMultiSelect(true);
            setImmediate(true);
            setStyleName("serverselect");
        }

        private void show(ComponentDto component, List<InstanceDto> instances, List<Long> availableInstanceNos) {
            removeAllItems();

            if (instances == null || availableInstanceNos == null) {
                return;
            }

            // 利用可能なサーバ情報を選択画面に表示
            for (Long instanceNo : availableInstanceNos) {
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
                        String value = instance.getInstance().getInstanceName() + " (" + status + ")";

                        // 関連付けがないものは未選択状態にする
                        if (BooleanUtils.isNotTrue(componentInstance.getComponentInstance().getAssociate())) {
                            addItem(value);
                        } else {
                            addItem(value);
                            select(value);
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
                    if (componentInstance.getComponentInstance().getInstanceNo()
                            .equals(instance.getInstance().getInstanceNo())) {
                        ComponentInstanceStatus componentInstanceStatus = ComponentInstanceStatus
                                .fromStatus(componentInstance.getComponentInstance().getStatus());
                        String status = StringUtils.capitalize(componentInstanceStatus.toString().toLowerCase());
                        String value = instance.getInstance().getInstanceName() + " (" + status + ")";

                        addItem(value);
                        select(value);
                        break;
                    }
                }
            }
        }

        private void select(Collection<String> itemIds) {
            for (String itemId : itemIds) {
                super.select(itemId);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<String> getItemIds() {
            return (Collection<String>) super.getItemIds();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<String> getValue() {
            return (Collection<String>) super.getValue();
        }

    }

    /**
     * 選択を解除できないサーバが解除された場合のエラー表示
     */
    private void valueChangeValidate() {
        // 選択されていないサーバ
        Set<String> notSelectedServerNames = new HashSet<String>(basicTab.serverSelect.getItemIds());
        notSelectedServerNames.removeAll(basicTab.serverSelect.getValue());

        // 選択されていないサーバの中に、Stoppedではないステータスのものが含まれているかチェックする
        List<String> notStoppedServerNames = new ArrayList<String>();
        for (String serverName : notSelectedServerNames) {
            if (StringUtils.contains(serverName.toUpperCase(), "(" + ComponentInstanceStatus.STOPPED.toString() + ")")) {
                continue;
            }

            if (!StringUtils.contains(serverName, "(")) {
                continue;
            }

            notStoppedServerNames.add(serverName);
        }

        // Stoppedではないステータスのサーバの選択を解除した場合、選択しなおしてエラーを表示する
        if (!notStoppedServerNames.isEmpty()) {
            basicTab.serverSelect.select(notStoppedServerNames);

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
        List<String> diskAttachedServerNames = new ArrayList<String>();
        for (String serverName : notSelectedServerNames) {
            int index = serverName.indexOf(" ");
            String instanceName = index == -1 ? serverName : serverName.substring(0, index);

            for (InstanceDto instance : instances) {
                if (!StringUtils.equals(instanceName, instance.getInstance().getInstanceName())) {
                    continue;
                }

                if (instance.getAwsVolumes() != null) {
                    for (AwsVolume awsVolume : instance.getAwsVolumes()) {
                        if (componentNo.equals(awsVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(awsVolume.getInstanceId())) {
                                diskAttachedServerNames.add(serverName);
                            }
                            break;
                        }
                    }
                }
                if (instance.getVmwareDisks() != null) {
                    for (VmwareDisk vmwareDisk : instance.getVmwareDisks()) {
                        if (componentNo.equals(vmwareDisk.getComponentNo())) {
                            if (BooleanUtils.isTrue(vmwareDisk.getAttached())) {
                                diskAttachedServerNames.add(serverName);
                            }
                            break;
                        }
                    }
                }
                if (instance.getCloudstackVolumes() != null) {
                    for (CloudstackVolume cloudstackVolume : instance.getCloudstackVolumes()) {
                        if (componentNo.equals(cloudstackVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(cloudstackVolume.getInstanceId())) {
                                diskAttachedServerNames.add(serverName);
                            }
                            break;
                        }
                    }
                }
                if (instance.getVcloudDisks() != null) {
                    for (VcloudDisk vcloudDisk : instance.getVcloudDisks()) {
                        if (componentNo.equals(vcloudDisk.getComponentNo())) {
                            if (BooleanUtils.isTrue(vcloudDisk.getAttached())) {
                                if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                    diskAttachedServerNames.add(serverName);
                                }
                            }
                            break;
                        }
                    }
                }
                if (instance.getAzureDisks() != null) {
                    for (AzureDisk azureDisk : instance.getAzureDisks()) {
                        if (componentNo.equals(azureDisk.getComponentNo())) {
                            if (StringUtils.isNotEmpty(azureDisk.getInstanceName())) {
                                if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                    diskAttachedServerNames.add(serverName);
                                }
                            }
                            break;
                        }
                    }
                }
                if (instance.getNiftyVolumes() != null) {
                    for (NiftyVolume niftyVolume : instance.getNiftyVolumes()) {
                        if (componentNo.equals(niftyVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(niftyVolume.getInstanceId())) {
                                diskAttachedServerNames.add(serverName);
                            }
                            break;
                        }
                    }
                }
                if (instance.getOpenstackVolumes() != null) {
                    for (OpenstackVolume openstackVolume : instance.getOpenstackVolumes()) {
                        if (componentNo.equals(openstackVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(openstackVolume.getInstanceId())) {
                                diskAttachedServerNames.add(serverName);
                            }
                            break;
                        }
                    }
                }
            }
        }

        // ディスクがアタッチされたままのサーバの選択を解除した場合、選択しなおしてエラーを表示する
        if (!diskAttachedServerNames.isEmpty()) {
            basicTab.serverSelect.select(diskAttachedServerNames);

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

        private Form form = new Form();

        private TextField customParam1Field;

        private TextField customParam2Field;

        private TextField customParam3Field;

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // カスタムパラメータ1
            customParam1Field = new TextField(ViewProperties.getCaption("field.customParam1"));
            customParam1Field.setWidth("90%");
            form.getLayout().addComponent(customParam1Field);

            // カスタムパラメータ2
            customParam2Field = new TextField(ViewProperties.getCaption("field.customParam2"));
            customParam2Field.setWidth("90%");
            form.getLayout().addComponent(customParam2Field);

            // カスタムパラメータ3
            customParam3Field = new TextField(ViewProperties.getCaption("field.customParam3"));
            customParam3Field.setWidth("90%");
            form.getLayout().addComponent(customParam3Field);

            form.setReadOnly(true);
            addComponent(form);

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000113");
            customParam1Field.addValidator(new StringLengthValidator(message, -1, 200, true));
            customParam1Field.addValidator(new RegexpValidator("^[0-9a-zA-Z-,._ ]*$", true, message));

            message = ViewMessages.getMessage("IUI-000114");
            customParam2Field.addValidator(new StringLengthValidator(message, -1, 200, true));
            customParam2Field.addValidator(new RegexpValidator("^[0-9a-zA-Z-,._ ]*$", true, message));

            message = ViewMessages.getMessage("IUI-000115");
            customParam3Field.addValidator(new StringLengthValidator(message, -1, 200, true));
            customParam3Field.addValidator(new RegexpValidator("^[0-9a-zA-Z-,._ ]*$", true, message));
        }

        private void show(ComponentDto component) {
            // カスタムパラメータ1
            ComponentConfig config1 = findComponentConfig(component, ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_1);
            if (config1 != null) {
                customParam1Field.setValue(config1.getConfigValue());
            }

            // カスタムパラメータ2
            ComponentConfig config2 = findComponentConfig(component, ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_2);
            if (config2 != null) {
                customParam2Field.setValue(config2.getConfigValue());
            }

            // カスタムパラメータ3
            ComponentConfig config3 = findComponentConfig(component, ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_3);
            if (config3 != null) {
                customParam3Field.setValue(config3.getConfigValue());
            }

            // サービスが停止していない場合、変更できないようにする
            ComponentStatus componentStatus = ComponentStatus.fromStatus(component.getStatus());
            if (ComponentStatus.STOPPED != componentStatus) {
                form.setEnabled(false);
            }
        }

        private ComponentConfig findComponentConfig(ComponentDto component, String configName) {
            for (ComponentConfig config : component.getComponentConfigs()) {
                if (configName.equals(config.getConfigName())) {
                    return config;
                }
            }
            return null;
        }

    }

    private void loadData() {
        // サービス情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        List<ComponentDto> components = componentService.getComponents(ViewContext.getFarmNo());
        for (ComponentDto component : components) {
            if (componentNo.equals(component.getComponent().getComponentNo())) {
                this.component = component;
                break;
            }
        }

        // 利用可能なサーバを取得
        ComponentTypeDto componentType = componentService.getComponentType(component.getComponent().getComponentNo());
        availableInstanceNos = componentType.getInstanceNos();

        // サーバ情報を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        instances = instanceService.getInstances(ViewContext.getFarmNo());
    }

    private void addButtonClick(ClickEvent event) {
        WinServerAddSimple winServerAddSimple = new WinServerAddSimple(component.getComponentType());
        winServerAddSimple.addListener(new CloseListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void windowClose(CloseEvent e) {
                // 作成したサーバ名を取得
                List<String> addedServerNames = (List<String>) ContextUtils.getAttribute("serverNames");

                if (addedServerNames != null) {
                    ContextUtils.removeAttribute("serverNames");

                    // 選択されているサーバ名を取得
                    Collection<String> selectedServerNames = basicTab.serverSelect.getValue();

                    // サーバ選択情報を再表示
                    loadData();
                    basicTab.serverSelect.show(component, instances, availableInstanceNos);

                    // 元のサーバ名と作成したサーバ名を選択する
                    List<String> serverNames = new ArrayList<String>(selectedServerNames);
                    serverNames.addAll(addedServerNames);
                    basicTab.serverSelect.setValue(serverNames);
                }
            }
        });

        getWindow().getApplication().getMainWindow().addWindow(winServerAddSimple);
    }

    private void okButtonClick(ClickEvent event) {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        String diskSize = (String) basicTab.diskSizeField.getValue();
        Collection<String> serverNames = basicTab.serverSelect.getValue();

        String customParam1 = null;
        String customParam2 = null;
        String customParam3 = null;
        if (detailTab != null) {
            customParam1 = (String) detailTab.customParam1Field.getValue();
            customParam2 = (String) detailTab.customParam2Field.getValue();
            customParam3 = (String) detailTab.customParam3Field.getValue();
        }

        // 入力チェック
        basicTab.commentField.validate();
        basicTab.diskSizeField.validate();

        if (detailTab != null) {
            detailTab.customParam1Field.validate();
            detailTab.customParam2Field.validate();
            detailTab.customParam3Field.validate();
        }

        // オペレーションログ
        OperationLogger.writeComponent("SERVICE", "Edit Service", componentNo, null);

        // サービスを更新
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        componentService.updateComponent(componentNo, comment, Integer.valueOf(diskSize), customParam1, customParam2,
                customParam3);

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

        // サービスにサーバを適用
        componentService.associateInstances(componentNo, instanceNos);

        // 画面を閉じる
        close();
    }

}
