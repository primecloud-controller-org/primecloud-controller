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

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.ComponentStatus;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

@SuppressWarnings("serial")
public class ServiceButtonsBottom extends CssLayout {

    private MainView sender;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    public ServiceButtonsBottom(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setWidth("100%");
        setMargin(true);
        addStyleName("service-buttons");

        // Addボタン
        addButton = new Button(ViewProperties.getCaption("button.addService"));
        addButton.setDescription(ViewProperties.getCaption("description.addService"));
        addButton.setIcon(Icons.ADD.resource());
        addButton.addStyleName("left");
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
        addComponent(addButton);

        // スペースを入れる
        Label spacer = new Label(" ", Label.CONTENT_XHTML);
        spacer.setWidth("30px");
        spacer.addStyleName("left");
        addComponent(spacer);

        // Editボタン
        editButton = new Button(ViewProperties.getCaption("button.editService"));
        editButton.setDescription(ViewProperties.getCaption("description.editService"));
        editButton.setWidth("90px");
        editButton.setIcon(Icons.EDITMINI.resource());
        editButton.addStyleName("left");
        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                editButtonClick(event);
            }
        });
        addComponent(editButton);

        // Deleteボタン
        deleteButton = new Button(ViewProperties.getCaption("button.deleteService"));
        deleteButton.setDescription(ViewProperties.getCaption("description.deleteService"));
        deleteButton.setWidth("90px");
        deleteButton.setIcon(Icons.DELETEMINI.resource());
        deleteButton.addStyleName("left");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                deleteButtonClick(event);
            }
        });
        addComponent(deleteButton);

        initialize();
    }

    public void initialize() {
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public void refresh(ComponentDto component) {
        // ステータスによってボタンの活性状態を切り替える
        ComponentStatus status = ComponentStatus.fromStatus(component.getStatus());

        // Editボタン
        if (status == ComponentStatus.STOPPED || status == ComponentStatus.RUNNING) {
            editButton.setEnabled(true);
        } else if (status == ComponentStatus.WARNING) {
            // サービスを設定中のサーバがなければ有効にする
            boolean processing = false;
            for (ComponentInstanceDto componentInstance : component.getComponentInstances()) {
                ComponentInstanceStatus status2 = ComponentInstanceStatus
                        .fromStatus(componentInstance.getComponentInstance().getStatus());
                if (status2 != ComponentInstanceStatus.RUNNING && status2 != ComponentInstanceStatus.WARNING
                        && status2 != ComponentInstanceStatus.STOPPED) {
                    processing = true;
                    break;
                }
            }
            editButton.setEnabled(!processing);
        } else {
            editButton.setEnabled(false);
        }

        // Deleteボタン
        if (status == ComponentStatus.STOPPED) {
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
        }
    }

    private void refreshTable() {
        // 選択されているサービスを保持する
        Long selectedComponentNo = null;
        if (sender.servicePanel.serviceTable.getValue() != null) {
            ComponentDto component = (ComponentDto) sender.servicePanel.serviceTable.getValue();
            selectedComponentNo = component.getComponent().getComponentNo();
        }
        int index = sender.servicePanel.serviceTable.getCurrentPageFirstItemIndex();

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサービスを選択し直す
        if (selectedComponentNo != null) {
            for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                ComponentDto component = (ComponentDto) itemId;
                if (selectedComponentNo.equals(component.getComponent().getComponentNo())) {
                    sender.servicePanel.serviceTable.select(itemId);
                    sender.servicePanel.serviceTable.setCurrentPageFirstItemIndex(index);
                    break;
                }
            }
        }
    }

    private void addButtonClick(ClickEvent event) {
        WinServiceAdd winServiceAdd = new WinServiceAdd();
        winServiceAdd.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                refreshTable();
            }
        });

        getWindow().addWindow(winServiceAdd);
    }

    private void editButtonClick(Button.ClickEvent event) {
        ComponentDto component = (ComponentDto) sender.servicePanel.serviceTable.getValue();

        WinServiceEdit winServiceEdit = new WinServiceEdit(component.getComponent().getComponentNo());
        winServiceEdit.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                refreshTable();
            }
        });

        getWindow().addWindow(winServiceEdit);
    }

    private void deleteButtonClick(Button.ClickEvent event) {
        final ComponentDto component = (ComponentDto) sender.servicePanel.serviceTable.getValue();

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000018", component.getComponent().getComponentName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                delete(component.getComponent().getComponentNo());
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void delete(Long componentNo) {
        // オペレーションログ
        OperationLogger.writeComponent("SERVICE", "Delete Service", componentNo, null);

        // サービスを削除
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        componentService.deleteComponent(componentNo);

        // サービスの選択を解除
        sender.servicePanel.serviceTable.select(null);

        // 表示を更新
        refreshTable();
    }

}
