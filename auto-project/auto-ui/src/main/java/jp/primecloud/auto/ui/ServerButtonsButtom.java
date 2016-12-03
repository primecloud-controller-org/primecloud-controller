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

import jp.primecloud.auto.common.status.ZabbixInstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ZabbixInstance;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

@SuppressWarnings("serial")
public class ServerButtonsButtom extends CssLayout {

    private MainView sender;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    private Button startMonitoringButton;

    private Button stopMonitoringButton;

    private Button startButton;

    private Button stopButton;

    ServerButtonsButtom(final MainView sender) {
        this.sender = sender;

        //テーブル下ボタンの配置
        setWidth("100%");
        setMargin(true);
        addStyleName("server-buttons");

        addButton = new Button(ViewProperties.getCaption("button.addServer"));
        addButton.setDescription(ViewProperties.getCaption("description.addServer"));
        addButton.setIcon(Icons.ADD.resource());
        addButton.addStyleName("left");
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });

        // Editボタン
        editButton = new Button(ViewProperties.getCaption("button.editServer"));
        editButton.setDescription(ViewProperties.getCaption("description.editServer"));
        editButton.setWidth("90px");
        editButton.setIcon(Icons.EDITMINI.resource());
        editButton.addStyleName("left");
        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.serverPanel.serverTable.editButtonClick(event);
            }
        });

        // Deleteボタン
        deleteButton = new Button(ViewProperties.getCaption("button.deleteServer"));
        deleteButton.setDescription(ViewProperties.getCaption("description.deleteServer"));
        deleteButton.setWidth("90px");
        deleteButton.setIcon(Icons.DELETEMINI.resource());
        deleteButton.addStyleName("left");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.serverPanel.serverTable.delButtonClick(event);
            }
        });

        // Start Monitoring ボタン
        startMonitoringButton = new Button(ViewProperties.getCaption("button.startMonitoring"));
        startMonitoringButton.setDescription(ViewProperties.getCaption("description.startMonitoring"));
        startMonitoringButton.setWidth("150px");
        startMonitoringButton.setIcon(Icons.START_MONITORING.resource());
        startMonitoringButton.addStyleName("right");
        startMonitoringButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.serverPanel.serverTable.startMonitoringButtonClick(event);
            }
        });

        // Stop Monitoring ボタン
        stopMonitoringButton = new Button(ViewProperties.getCaption("button.stopMonitoring"));
        stopMonitoringButton.setDescription(ViewProperties.getCaption("description.stopMonitoring"));
        stopMonitoringButton.setWidth("150px");
        stopMonitoringButton.setIcon(Icons.STOP_MONITORING.resource());
        stopMonitoringButton.addStyleName("right");
        stopMonitoringButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.serverPanel.serverTable.stopMonitoringButtonClick(event);
            }
        });

        // Startボタン
        startButton = new Button(ViewProperties.getCaption("button.startServer"));
        startButton.setDescription(ViewProperties.getCaption("description.startServer"));
        startButton.setWidth("90px");
        startButton.setIcon(Icons.PLAYMINI.resource());
        startButton.addStyleName("right");
        startButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.serverPanel.serverTable.playButtonClick(event);
            }
        });

        // Stopボタン
        stopButton = new Button(ViewProperties.getCaption("button.stopServer"));
        stopButton.setDescription(ViewProperties.getCaption("description.stopServer"));
        stopButton.setWidth("90px");
        stopButton.setIcon(Icons.STOPMINI.resource());
        stopButton.addStyleName("right");
        stopButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                sender.serverPanel.serverTable.stopButtonClick(event);
            }
        });

        //ボタンの初期化
        initialize();

        Label spacer = new Label(" ", Label.CONTENT_XHTML);
        spacer.setWidth("30px");
        spacer.addStyleName("left");
        addComponent(addButton);
        addComponent(spacer);
        addComponent(editButton);
        addComponent(deleteButton);
        addComponent(stopButton);
        addComponent(startButton);

        //Zabbix使用フラグ と Zabbix監視変更可否フラグがtrueの場合のみ表示
        Boolean useZabbix = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useZabbix"));
        Boolean changeMonitoring = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.changeMonitoring"));
        if (BooleanUtils.isTrue(useZabbix) && BooleanUtils.isTrue(changeMonitoring)) {
            addComponent(stopMonitoringButton);
            addComponent(startMonitoringButton);
        }
    }

    public void addButtonClick(ClickEvent event) {
        final InstanceDto dto = (InstanceDto) sender.serverPanel.serverTable.getValue();

        WinServerAdd winServerAdd = new WinServerAdd();
        winServerAdd.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                sender.refreshTable();

                // 選択されていたサーバを選択し直す
                if (dto != null) {
                    for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                        InstanceDto dto2 = (InstanceDto) itemId;
                        if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                            sender.serverPanel.serverTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        getWindow().addWindow(winServerAdd);
    }

    void initialize() {
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        startMonitoringButton.setEnabled(false);
        stopMonitoringButton.setEnabled(false);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);

        // 権限がなければボタンを無効にする
        UserAuthDto auth = ViewContext.getAuthority();
        if (!auth.isServerMake()) {
            addButton.setEnabled(false);
        }

        // ReloadボタンをStartボタンに
        startButton.setCaption(ViewProperties.getCaption("button.startServer"));
        startButton.setDescription(ViewProperties.getCaption("description.startServer"));
    }

    void refresh(InstanceDto instanceDto) {
        if (instanceDto != null && instanceDto.getInstance() != null) {
            addButton.setEnabled(true);
            //ステータスによってボタンの有効無効を切り替える
            String status = instanceDto.getInstance().getStatus();
            if ("STOPPED".equals(status)) {
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                startMonitoringButton.setEnabled(false);
                stopMonitoringButton.setEnabled(false);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);

            } else if ("RUNNING".equals(status)) {
                ZabbixInstance zabbixInstance = instanceDto.getZabbixInstance();
                boolean monitoring = false;
                boolean unMonitoring = false;
                if (zabbixInstance != null) {
                    ZabbixInstanceStatus zStatus = ZabbixInstanceStatus.fromStatus(zabbixInstance.getStatus());
                    if (ZabbixInstanceStatus.MONITORING.equals(zStatus)) {
                        monitoring = true;
                    } else if (ZabbixInstanceStatus.UN_MONITORING.equals(zStatus)) {
                        unMonitoring = true;
                    }
                }

                editButton.setEnabled(true);
                deleteButton.setEnabled(false);
                startMonitoringButton.setEnabled(unMonitoring);
                stopMonitoringButton.setEnabled(monitoring);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);

            } else if ("WARNING".equals(status)) {
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                startMonitoringButton.setEnabled(false);
                stopMonitoringButton.setEnabled(false);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);

            } else {
                editButton.setEnabled(true);
                deleteButton.setEnabled(false);
                startButton.setEnabled(false);
                startMonitoringButton.setEnabled(false);
                stopMonitoringButton.setEnabled(false);
                stopButton.setEnabled(false);
            }

            UserAuthDto auth = ViewContext.getAuthority();
            //権限に応じて操作可能なボタンを制御する
            if (!auth.isServerMake()) {
                addButton.setEnabled(false);
                editButton.setEnabled(false);
            }
            if (!auth.isServerDelete()) {
                deleteButton.setEnabled(false);
            }
            if (!auth.isServerOperate()) {
                startMonitoringButton.setEnabled(false);
                stopMonitoringButton.setEnabled(false);
                startButton.setEnabled(false);
                stopButton.setEnabled(false);
            }

        } else {
            //ボタンの無効化・非表示
            initialize();
        }
    }

}
