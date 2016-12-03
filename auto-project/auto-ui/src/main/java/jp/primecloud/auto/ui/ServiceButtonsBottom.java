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
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
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

    ServiceButtonsBottom(final MainView sender) {
        this.sender = sender;

        //テーブル下ボタンの配置
        setWidth("100%");
        setMargin(true);
        addStyleName("service-buttons");

        // Newボタン
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

        // Editボタン
        editButton = new Button(ViewProperties.getCaption("button.editService"));
        editButton.setDescription(ViewProperties.getCaption("description.editService"));
        editButton.setWidth("90px");
        editButton.setIcon(Icons.EDITMINI.resource());
        editButton.addStyleName("left");
        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.servicePanel.serviceTable.editButtonClick(event);
            }
        });

        // Deleteボタン
        deleteButton = new Button(ViewProperties.getCaption("button.deleteService"));
        deleteButton.setDescription(ViewProperties.getCaption("description.deleteService"));
        deleteButton.setWidth("90px");
        deleteButton.setIcon(Icons.DELETEMINI.resource());
        deleteButton.addStyleName("left");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.servicePanel.serviceTable.delButtonClick(event);
            }
        });

        //ボタンの無効化
        hide();

        Label spacer = new Label(" ", Label.CONTENT_XHTML);
        spacer.setWidth("30px");
        spacer.addStyleName("left");
        addComponent(addButton);
        addComponent(spacer);
        addComponent(editButton);
        addComponent(deleteButton);
    }

    public void addButtonClick(ClickEvent event) {
        final ComponentDto dto = (ComponentDto) sender.servicePanel.serviceTable.getValue();
        WinServiceAdd winServiceAdd = new WinServiceAdd();
        winServiceAdd.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                sender.refreshTable();

                // 選択されていたサービスを選択し直す
                if (dto != null) {
                    for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                        ComponentDto dto2 = (ComponentDto) itemId;
                        if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                            sender.servicePanel.serviceTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        getWindow().addWindow(winServiceAdd);
    }

    void hide() {
        //ボタンの無効化
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        //作成権限がなければ非活性
        UserAuthDto auth = ViewContext.getAuthority();
        if (!auth.isServiceMake()) {
            addButton.setEnabled(false);
        }
    }

    void refresh(ComponentDto dto) {
        if (dto != null) {
            addButton.setEnabled(true);
            //ステータスによってボタンの有効無効を切り替える
            String status = dto.getStatus();
            if ("STOPPED".equals(status)) {
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else if ("RUNNING".equals(status)) {
                editButton.setEnabled(true);
                deleteButton.setEnabled(false);
            } else if ("WARNING".equals(status)) {
                boolean processing = false;
                for (ComponentInstanceDto componentInstance : dto.getComponentInstances()) {
                    ComponentInstanceStatus s = ComponentInstanceStatus.fromStatus(componentInstance
                            .getComponentInstance().getStatus());
                    if (s != ComponentInstanceStatus.RUNNING && s != ComponentInstanceStatus.WARNING
                            && s != ComponentInstanceStatus.STOPPED) {
                        processing = true;
                        break;
                    }
                }
                editButton.setEnabled(!processing);
                deleteButton.setEnabled(false);
            } else {
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }

            //権限に応じて操作可能なボタンを制御する
            UserAuthDto auth = ViewContext.getAuthority();
            if (!auth.isServiceMake()) {
                addButton.setEnabled(false);
                editButton.setEnabled(false);
            }
            if (!auth.isServiceDelete()) {
                deleteButton.setEnabled(false);
            }

        } else {
            //ボタンの無効化・非表示
            hide();
        }
    }

}
