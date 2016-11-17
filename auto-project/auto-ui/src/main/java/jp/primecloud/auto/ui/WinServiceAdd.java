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
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
@SuppressWarnings({ "serial", "unchecked" })
public class WinServiceAdd extends Window {

    final String COLUMN_HEIGHT = "30px";

    Application apl;

    TextField serviceNameField;

    TextField commentField;

    Table serviceTable;

    TextField diskSizeField;

    TwinColSelect serverSelect;

    List<ComponentTypeDto> componentTypes;

    List<InstanceDto> instances;

    WinServiceAdd(Application ap) {
        apl = ap;

        //モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winServiceAdd"));
        setModal(true);
        setWidth("630px");
        //setHeight("500px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(false);

        // フォーム
        layout.addComponent(new BasicForm());

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // Add
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.addService"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinServiceAdd.this.addButtonClick(event);
            }
        });
        okbar.addComponent(addButton);
        // [Enter]でaddButtonクリック
        addButton.setClickShortcut(KeyCode.ENTER);
        addButton.focus();

        // Cancel
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinServiceAdd.this.close();
            }
        });
        okbar.addComponent(cancelButton);

        // 入力チェックの設定
        initValidation();

        // 初期データの取得
        initData();

        // サービス情報を表示
        showServices();
    }

    private class BasicForm extends Form {

        BasicForm() {
            setStyleName("win-service-add-form");

            // サービス名
            serviceNameField = new TextField(ViewProperties.getCaption("field.serviceName"));
            getLayout().addComponent(serviceNameField);

            //コメント欄
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("90%");
            getLayout().addComponent(commentField);

            // サービス選択テーブル
            serviceTable = new SelectServiceTable();
            getLayout().addComponent(serviceTable);

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
            //サーバ作成権限のチェック(無い場合は非活性)
            UserAuthDto auth = ViewContext.getAuthority();
            addServerButton.setEnabled(auth.isServerMake());

            addServerButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    // 選択されているサービスを取得
                    final Long componentTypeNo = (Long) serviceTable.getValue();
                    if (componentTypeNo == null) {
                        // TODO: サービスが選択されていない場合
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                                ViewMessages.getMessage("IUI-000030"));
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }

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

                                // サービス情報を表示
                                showServices();

                                // 元のサービス種類を選択する
                                serviceTable.select(componentTypeNo);

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
            HorizontalLayout hlay = new HorizontalLayout();
            Label txt = new Label(ViewProperties.getCaption("label.addServerQuick"));
            hlay.addComponent(addServerButton);
            hlay.addComponent(txt);
            hlay.setComponentAlignment(txt, Alignment.MIDDLE_LEFT);

            getLayout().addComponent(hlay);
        }

    }

    private class SelectServiceTable extends Table {

        SelectServiceTable() {
            //テーブル基本設定
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

            //カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Service", Label.class, new Label());
            addContainerProperty("Description", String.class, null);
            setColumnExpandRatio("Description", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator());

            // 行が選択されたときのイベント
            addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    Long componentTypeNo = (Long) getValue();

                    // サーバ選択を表示
                    showSelectServers(componentTypeNo);
                }
            });
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

    private void initData() {
        // クラウド番号
        Long farmNo = ViewContext.getFarmNo();

        // サービス種類情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        componentTypes = componentService.getComponentTypes(farmNo);

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
        instances = instanceService.getInstances(farmNo);
    }

    private void showServices() {
        serviceTable.removeAllItems();

        // 取得したサービス種類情報をテーブルに追加
        for (int i = 0; i < componentTypes.size(); i++) {
            ComponentTypeDto componentType = componentTypes.get(i);

            if (BooleanUtils.isNotTrue(componentType.getComponentType().getSelectable())) {
                //使用不可コンポーネントタイプの場合、非表示
                continue;
            }

            // サービス名
            String name = componentType.getComponentType().getComponentTypeNameDisp();
            Icons nameIcon = Icons.fromName(componentType.getComponentType().getComponentTypeName());

            Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, nameIcon) + "\"><div>" + name
                    + "</div>", Label.CONTENT_XHTML);
            slbl.setHeight(COLUMN_HEIGHT);

            // サービス説明
            String description = componentType.getComponentType().getLayerDisp();

            serviceTable.addItem(new Object[] { (i + 1), slbl, description }, componentType.getComponentType()
                    .getComponentTypeNo());
        }

        Long componentTypeNo = null;
        if (serviceTable.getItemIds().size() > 0) {
            componentTypeNo = (Long) serviceTable.getItemIds().toArray()[0];
        }

        // 先頭のサービス種類を選択する
        serviceTable.select(componentTypeNo);
    }

    private void showSelectServers(Long componentTypeNo) {
        serverSelect.removeAllItems();
        if (componentTypeNo == null) {
            return;
        }

        // 選択されたサービス種類を取得
        ComponentTypeDto componentType = null;
        for (ComponentTypeDto tmpComponentType : componentTypes) {
            if (tmpComponentType.getComponentType().getComponentTypeNo().equals(componentTypeNo)) {
                componentType = tmpComponentType;
                break;
            }
        }

        // 選択されたサービス種類で利用可能なサーバ情報を選択画面に表示
        for (Long instanceNo : componentType.getInstanceNos()) {
            for (InstanceDto instance : instances) {
                if (instanceNo.equals(instance.getInstance().getInstanceNo())) {
                    serverSelect.addItem(instance.getInstance().getInstanceName());
                    break;
                }
            }
        }
    }

    private void addButtonClick(ClickEvent event) {
        // 入力値を取得
        String serviceName = (String) serviceNameField.getValue();
        String comment = (String) commentField.getValue();
        String diskSize = (String) diskSizeField.getValue();
        Long componentTypeNo = (Long) serviceTable.getValue();
        Collection<String> serverNames = (Collection<String>) serverSelect.getValue();

        // 選択されたサーバのinstanceNoのリスト
        List<Long> instanceNos = new ArrayList<Long>();
        for (InstanceDto instance : instances) {
            if (serverNames.contains(instance.getInstance().getInstanceName())) {
                instanceNos.add(instance.getInstance().getInstanceNo());
            }
        }

        // TODO: 入力チェック
        try {
            serviceNameField.validate();
            commentField.validate();
            diskSizeField.validate();
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

        // クラウド番号
        Long farmNo = ViewContext.getFarmNo();

        // サービスを作成（ロジックを実行）
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        Long componentNo;
        try {
            componentNo = componentService.createComponent(farmNo, serviceName, componentTypeNo, comment,
                    Integer.valueOf(diskSize));
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        //オペレーションログ
        AutoApplication aapl = (AutoApplication) apl;
        aapl.doOpLog("SERVICE", "Make Service", null, componentNo, null, null);

        // サービスにサーバを追加（ロジックを実行）
        try {
            componentService.associateInstances(componentNo, instanceNos);
        } catch (AutoApplicationException e) {
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
