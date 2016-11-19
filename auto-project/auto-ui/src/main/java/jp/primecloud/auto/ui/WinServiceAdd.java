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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ComponentTypeDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * サービスの新規作成画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinServiceAdd extends Window {

    private final String COLUMN_HEIGHT = "30px";

    private BasicForm basicForm;

    private List<ComponentTypeDto> componentTypes;

    private List<InstanceDto> instances;

    @Override
    public void attach() {
        // モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winServiceAdd"));
        setModal(true);
        setWidth("630px");
        //setHeight("500px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(false);

        // フォーム
        basicForm = new BasicForm();
        layout.addComponent(basicForm);

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.addService"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
        addButton.setClickShortcut(KeyCode.ENTER); // [Enter]でaddButtonクリック
        addButton.focus();
        bottomLayout.addComponent(addButton);

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

        // サービス種類情報を表示
        loadData();
        basicForm.serviceTable.show(componentTypes);
        basicForm.serviceTable.selectFirst();
    }

    private class BasicForm extends Form {

        private TextField serviceNameField;

        private TextField commentField;

        private SelectServiceTable serviceTable;

        private TextField diskSizeField;

        private ServerSelect serverSelect;

        @Override
        public void attach() {
            setStyleName("win-service-add-form");

            // サービス名
            serviceNameField = new TextField(ViewProperties.getCaption("field.serviceName"));
            getLayout().addComponent(serviceNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("90%");
            getLayout().addComponent(commentField);

            // サービス種類情報テーブル
            serviceTable = new SelectServiceTable();
            getLayout().addComponent(serviceTable);
            serviceTable.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    serviceTableSelect(event);
                }
            });

            // ディスクサイズ
            diskSizeField = new TextField(ViewProperties.getCaption("field.diskSize"));
            getLayout().addComponent(diskSizeField);

            // サーバ選択フォーム
            AbsoluteLayout layout = new AbsoluteLayout();
            layout.setWidth("100%");
            layout.setHeight("20px");
            layout.setStyleName("serverselect");
            Label selectLbl = new Label(ViewProperties.getCaption("label.serverSelectable"));
            Label selectedLbl = new Label(ViewProperties.getCaption("label.serverSelected"));
            layout.addComponent(selectLbl, "left:10%");
            layout.addComponent(selectedLbl, "left:65%");
            getLayout().addComponent(layout);

            serverSelect = new ServerSelect();
            serverSelect.setWidth("95%");
            getLayout().addComponent(serverSelect);

            // 新規サーバ追加ボタン
            Button addServerButton = new Button(ViewProperties.getCaption("button.addServerQuick"));
            addServerButton.setDescription(ViewProperties.getCaption("description.addServerQuick"));
            addServerButton.setIcon(Icons.ADD.resource());
            // サーバ作成権限がない場合は無効
            if (!ViewContext.getAuthority().isServerMake()) {
                addServerButton.setEnabled(false);
            }

            addServerButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    addServerButton(event);
                }
            });

            HorizontalLayout layout2 = new HorizontalLayout();
            Label label = new Label(ViewProperties.getCaption("label.addServerQuick"));
            layout2.addComponent(addServerButton);
            layout2.addComponent(label);
            layout2.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
            getLayout().addComponent(layout2);

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000031");
            serviceNameField.setRequired(true);
            serviceNameField.setRequiredError(message);
            serviceNameField.addValidator(new StringLengthValidator(message, -1, 15, false));
            serviceNameField.addValidator(new RegexpValidator("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", true, message));

            message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));

            message = ViewMessages.getMessage("IUI-000032");
            diskSizeField.setRequired(true);
            diskSizeField.setRequiredError(message);
            diskSizeField.addValidator(new RegexpValidator("^[1-9]|[1-9][0-9]{1,2}|1000$", true, message));
        }

        private void serviceTableSelect(Property.ValueChangeEvent event) {
            // 選択がない場合はサーバ選択をクリア
            if (serviceTable.getValue() == null) {
                serverSelect.removeAllItems();
                return;
            }

            // 選択されたサービス種類で利用可能なサーバ情報を選択画面に表示
            ComponentTypeDto componentType = findComponentType(serviceTable.getValue());
            serverSelect.show(instances, componentType.getInstanceNos());
        }

    }

    private class SelectServiceTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.selectService"));
            setWidth("440px");
            setPageLength(4);
            setSortDisabled(true);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("win-service-add-service");

            // カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Service", Label.class, new Label());
            addContainerProperty("Description", String.class, null);
            setColumnExpandRatio("Description", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<ComponentTypeDto> componentTypes) {
            removeAllItems();

            if (componentTypes == null) {
                return;
            }

            for (int i = 0; i < componentTypes.size(); i++) {
                ComponentTypeDto componentType = componentTypes.get(i);

                // サービス名
                String name = componentType.getComponentType().getComponentTypeNameDisp();
                Icons nameIcon = Icons.fromName(componentType.getComponentType().getComponentTypeName());
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), nameIcon, name), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                // サービス説明
                String description = componentType.getComponentType().getLayerDisp();

                addItem(new Object[] { (i + 1), slbl, description }, componentType.getComponentType()
                        .getComponentTypeNo());
            }
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }

        public void selectFirst() {
            if (size() > 0) {
                select(firstItemId());
            }
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

        public void show(List<InstanceDto> instances, List<Long> availableInstanceNos) {
            removeAllItems();

            if (instances == null || availableInstanceNos == null) {
                return;
            }

            for (Long instanceNo : availableInstanceNos) {
                for (InstanceDto instance : instances) {
                    if (instanceNo.equals(instance.getInstance().getInstanceNo())) {
                        addItem(instance.getInstance().getInstanceName());
                        break;
                    }
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<String> getValue() {
            return (Collection<String>) super.getValue();
        }

    }

    private void loadData() {
        // サービス種類情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        componentTypes = componentService.getComponentTypes(ViewContext.getFarmNo());

        // 有効でないサービス種類情報を除外
        for (int i = componentTypes.size() - 1; i >= 0; i--) {
            if (BooleanUtils.isNotTrue(componentTypes.get(i).getComponentType().getSelectable())) {
                componentTypes.remove(i);
            }
        }

        // サービス種類情報をソート
        Collections.sort(componentTypes, new Comparator<ComponentTypeDto>() {
            @Override
            public int compare(ComponentTypeDto o1, ComponentTypeDto o2) {
                int order1 = (o1.getComponentType().getViewOrder() != null) ? o1.getComponentType().getViewOrder()
                        : Integer.MAX_VALUE;
                int order2 = (o2.getComponentType().getViewOrder() != null) ? o2.getComponentType().getViewOrder()
                        : Integer.MAX_VALUE;
                return order1 - order2;
            }
        });

        // 全インスタンスを取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        instances = instanceService.getInstances(ViewContext.getFarmNo());
    }

    private ComponentTypeDto findComponentType(Long componentTypeNo) {
        for (ComponentTypeDto componentType : componentTypes) {
            if (componentTypeNo.equals(componentType.getComponentType().getComponentTypeNo())) {
                return componentType;
            }
        }
        return null;
    }

    private void addServerButton(ClickEvent event) {
        // 選択されているサービス種類を取得
        final Long componentTypeNo = basicForm.serviceTable.getValue();
        if (componentTypeNo == null) {
            // サービス種類が選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000030"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        ComponentTypeDto componentType = findComponentType(componentTypeNo);

        WinServerAddSimple winServerAddSimple = new WinServerAddSimple(componentType.getComponentType());
        winServerAddSimple.addListener(new CloseListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void windowClose(CloseEvent e) {
                // 作成したサーバ名を取得
                List<String> addedServerNames = (List<String>) ContextUtils.getAttribute("serverNames");

                if (addedServerNames != null) {
                    ContextUtils.removeAttribute("serverNames");

                    // 選択されているサーバ名を取得
                    Collection<String> selectedServerNames = basicForm.serverSelect.getValue();

                    // サービス種類情報を再表示
                    loadData();
                    basicForm.serviceTable.show(componentTypes);

                    // 元のサービス種類を選択する
                    basicForm.serviceTable.select(componentTypeNo);

                    // 元のサーバ名と作成したサーバ名を選択する
                    List<String> serverNames = new ArrayList<String>(selectedServerNames);
                    serverNames.addAll(addedServerNames);
                    basicForm.serverSelect.setValue(serverNames);
                }
            }
        });

        getWindow().getApplication().getMainWindow().addWindow(winServerAddSimple);
    }

    private void addButtonClick(ClickEvent event) {
        // 入力値を取得
        String serviceName = (String) basicForm.serviceNameField.getValue();
        String comment = (String) basicForm.commentField.getValue();
        String diskSize = (String) basicForm.diskSizeField.getValue();
        Long componentTypeNo = basicForm.serviceTable.getValue();
        Collection<String> serverNames = basicForm.serverSelect.getValue();

        // 入力チェック
        try {
            basicForm.serviceNameField.validate();
            basicForm.commentField.validate();
            basicForm.diskSizeField.validate();
        } catch (InvalidValueException e) {
            String errMes = e.getMessage();
            if (null == errMes) {
                //メッセージが取得できない場合は複合エラー 先頭を表示する
                InvalidValueException[] exceptions = e.getCauses();
                errMes = exceptions[0].getMessage();
            }

            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), errMes);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (componentTypeNo == null) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000030"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if ("base".equals(serviceName) || serviceName.startsWith("lb-")) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000053", serviceName));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // サービスを作成
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        Long componentNo;
        try {
            componentNo = componentService.createComponent(ViewContext.getFarmNo(), serviceName, componentTypeNo,
                    comment, Integer.valueOf(diskSize));
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVICE", "Make Service", null, componentNo, null, null);

        // 選択されたサーバのinstanceNoのリスト
        List<Long> instanceNos = new ArrayList<Long>();
        for (InstanceDto instance : instances) {
            if (serverNames.contains(instance.getInstance().getInstanceName())) {
                instanceNos.add(instance.getInstance().getInstanceNo());
            }
        }

        // サービスにサーバを追加
        try {
            componentService.associateInstances(componentNo, instanceNos);
        } catch (AutoApplicationException e) {
            // エラーの場合、作成したサービスを削除する
            componentService.deleteComponent(componentNo);

            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 画面を閉じる
        close();
    }

}
