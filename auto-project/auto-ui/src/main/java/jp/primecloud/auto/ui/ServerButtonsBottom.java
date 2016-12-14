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

import java.util.Arrays;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.ZabbixInstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

@SuppressWarnings("serial")
public class ServerButtonsBottom extends CssLayout {

    private MainView sender;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    private Button startButton;

    private Button stopButton;

    private Button startMonitoringButton;

    private Button stopMonitoringButton;

    public ServerButtonsBottom(final MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setWidth("100%");
        setMargin(true);
        addStyleName("server-buttons");

        // Addボタン
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
        addComponent(addButton);

        // スペースを入れる
        Label spacer = new Label(" ", Label.CONTENT_XHTML);
        spacer.setWidth("30px");
        spacer.addStyleName("left");
        addComponent(spacer);

        // Editボタン
        editButton = new Button(ViewProperties.getCaption("button.editServer"));
        editButton.setDescription(ViewProperties.getCaption("description.editServer"));
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
        deleteButton = new Button(ViewProperties.getCaption("button.deleteServer"));
        deleteButton.setDescription(ViewProperties.getCaption("description.deleteServer"));
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

        // Stopボタン
        stopButton = new Button(ViewProperties.getCaption("button.stopServer"));
        stopButton.setDescription(ViewProperties.getCaption("description.stopServer"));
        stopButton.setWidth("90px");
        stopButton.setIcon(Icons.STOPMINI.resource());
        stopButton.addStyleName("right");
        stopButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                stopButtonClick(event);
            }
        });
        addComponent(stopButton);

        // Startボタン
        startButton = new Button(ViewProperties.getCaption("button.startServer"));
        startButton.setDescription(ViewProperties.getCaption("description.startServer"));
        startButton.setWidth("90px");
        startButton.setIcon(Icons.PLAYMINI.resource());
        startButton.addStyleName("right");
        startButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                startButtonClick(event);
            }
        });
        addComponent(startButton);

        // 監視の変更ボタンを使用するかどうか
        boolean useZabbix = BooleanUtils.toBoolean(Config.getProperty("zabbix.useZabbix"));
        boolean changeMonitoring = BooleanUtils.toBoolean(Config.getProperty("zabbix.changeMonitoring"));
        if (useZabbix && changeMonitoring) {
            // Stop Monitoring ボタン
            stopMonitoringButton = new Button(ViewProperties.getCaption("button.stopMonitoring"));
            stopMonitoringButton.setDescription(ViewProperties.getCaption("description.stopMonitoring"));
            stopMonitoringButton.setWidth("150px");
            stopMonitoringButton.setIcon(Icons.STOP_MONITORING.resource());
            stopMonitoringButton.addStyleName("right");
            stopMonitoringButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    stopMonitoringButtonClick(event);
                }
            });
            addComponent(stopMonitoringButton);

            // Start Monitoring ボタン
            startMonitoringButton = new Button(ViewProperties.getCaption("button.startMonitoring"));
            startMonitoringButton.setDescription(ViewProperties.getCaption("description.startMonitoring"));
            startMonitoringButton.setWidth("150px");
            startMonitoringButton.setIcon(Icons.START_MONITORING.resource());
            startMonitoringButton.addStyleName("right");
            startMonitoringButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    startMonitoringButtonClick(event);
                }
            });
            addComponent(startMonitoringButton);
        }

        initialize();
    }

    public void initialize() {
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        if (startMonitoringButton != null) {
            startMonitoringButton.setEnabled(false);
        }
        if (stopMonitoringButton != null) {
            stopMonitoringButton.setEnabled(false);
        }

        // ReloadボタンをStartボタンに
        startButton.setCaption(ViewProperties.getCaption("button.startServer"));
        startButton.setDescription(ViewProperties.getCaption("description.startServer"));
    }

    public void refresh(InstanceDto instance) {
        // ステータスによってボタンの活性状態を切り替える
        InstanceStatus status = InstanceStatus.fromStatus(instance.getInstance().getStatus());

        // Editボタン
        if (status == InstanceStatus.STOPPED || status == InstanceStatus.RUNNING) {
            editButton.setEnabled(true);
        } else {
            editButton.setEnabled(false);
        }

        // Deleteボタン
        if (status == InstanceStatus.STOPPED) {
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
        }

        // Startボタン
        if (status == InstanceStatus.STOPPED) {
            startButton.setEnabled(true);
        } else {
            startButton.setEnabled(false);
        }

        // Stopボタン
        if (status == InstanceStatus.RUNNING || status == InstanceStatus.WARNING) {
            stopButton.setEnabled(true);
        } else {
            stopButton.setEnabled(false);
        }

        // Start Monitoringボタン
        if (startMonitoringButton != null) {
            if (status == InstanceStatus.RUNNING) {
                boolean enabled = false;

                if (instance.getZabbixInstance() != null) {
                    ZabbixInstanceStatus status2 = ZabbixInstanceStatus.fromStatus(instance.getZabbixInstance()
                            .getStatus());
                    if (status2 == ZabbixInstanceStatus.UN_MONITORING) {
                        enabled = true;
                    }
                }

                startMonitoringButton.setEnabled(enabled);
            } else {
                startMonitoringButton.setEnabled(false);
            }
        }

        // Stop Monitoringボタン
        if (stopMonitoringButton != null) {
            if (status == InstanceStatus.RUNNING) {
                boolean enabled = false;

                if (instance.getZabbixInstance() != null) {
                    ZabbixInstanceStatus status2 = ZabbixInstanceStatus.fromStatus(instance.getZabbixInstance()
                            .getStatus());
                    if (status2 == ZabbixInstanceStatus.MONITORING) {
                        enabled = true;
                    }
                }

                stopMonitoringButton.setEnabled(enabled);
            } else {
                stopMonitoringButton.setEnabled(false);
            }
        }
    }

    private void refreshTable() {
        // 選択されているサーバを保持する
        Long selectedInstanceNo = null;
        if (sender.serverPanel.serverTable.getValue() != null) {
            InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();
            selectedInstanceNo = instance.getInstance().getInstanceNo();
        }
        int index = sender.serverPanel.serverTable.getCurrentPageFirstItemIndex();

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサーバを選択し直す
        if (selectedInstanceNo != null) {
            for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                InstanceDto instance = (InstanceDto) itemId;
                if (selectedInstanceNo.equals(instance.getInstance().getInstanceNo())) {
                    sender.serverPanel.serverTable.select(itemId);
                    sender.serverPanel.serverTable.setCurrentPageFirstItemIndex(index);
                    break;
                }
            }
        }
    }

    private void addButtonClick(ClickEvent event) {
        WinServerAdd winServerAdd = new WinServerAdd();
        winServerAdd.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                refreshTable();
            }
        });

        getWindow().addWindow(winServerAdd);
    }

    private void editButtonClick(Button.ClickEvent event) {
        InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

        WinServerEdit winServerEdit = new WinServerEdit(instance.getInstance().getInstanceNo());
        winServerEdit.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                refreshTable();
            }
        });

        getWindow().addWindow(winServerEdit);
    }

    private void deleteButtonClick(Button.ClickEvent event) {
        final InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000015", instance.getInstance().getInstanceName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                delete(instance.getInstance().getInstanceNo());
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    private void delete(Long instanceNo) {
        // オペレーションログ
        OperationLogger.writeInstance("SERVER", "Delete Server", instanceNo, null);

        // サーバを削除
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        instanceService.deleteInstance(instanceNo);

        // サーバの選択を解除
        sender.serverPanel.serverTable.select(null);

        // 表示を更新
        refreshTable();
    }

    private void startButtonClick(Button.ClickEvent event) {
        final InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();
        PlatformDto platform = instance.getPlatform();

        // AWSの場合
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())) {
            // サブネットが設定されていることを確認
            if (BooleanUtils.isTrue(platform.getPlatformAws().getVpc())
                    && StringUtils.isEmpty(instance.getAwsInstance().getSubnetId())) {
                throw new AutoApplicationException("IUI-000111");
            }
        }
        // Azureの場合
        else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
            // サブネットが設定されていることを確認
            if (StringUtils.isEmpty(instance.getAzureInstance().getSubnetId())) {
                throw new AutoApplicationException("IUI-000111");
            }

            // インスタンス起動チェック（個別起動）
            ProcessService processService = BeanContext.getBean(ProcessService.class);
            boolean startupErrFlg = processService.checkStartup(platform.getPlatform().getPlatformType(), instance
                    .getAzureInstance().getInstanceName(), instance.getAzureInstance().getInstanceNo());
            if (startupErrFlg == true) {
                // インスタンス作成中のものがあった場合は、起動不可
                // 同一インスタンスNoは、除外する
                throw new AutoApplicationException("IUI-000133");
            }
        }

        // ダイアログの表示オプション
        VerticalLayout optionLayout = null;
        final CheckBox checkBox;
        String enableService = Config.getProperty("ui.enableService");
        if (enableService == null || BooleanUtils.toBoolean(enableService)) {
            optionLayout = new VerticalLayout();
            checkBox = new CheckBox(ViewMessages.getMessage("IUI-000035"), false);
            checkBox.setImmediate(true);
            optionLayout.addComponent(checkBox);
            optionLayout.setComponentAlignment(checkBox, Alignment.MIDDLE_CENTER);
        } else {
            checkBox = null;
        }

        // 確認ダイアログを表示
        String actionName = event.getButton().getDescription();
        String message = ViewMessages.getMessage("IUI-000013", new Object[] { instance.getInstance().getInstanceName(),
                actionName });
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancel, optionLayout);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                boolean startService = (checkBox == null) ? false : (Boolean) checkBox.getValue();
                start(instance.getInstance().getInstanceNo(), startService);
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void start(Long instanceNo, boolean startService) {
        // オペレーションログ
        OperationLogger.writeInstance("SERVER", "Start Server", instanceNo, String.valueOf(startService));

        // サーバを起動
        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.startInstances(ViewContext.getFarmNo(), Arrays.asList(instanceNo), startService);

        // 表示を更新
        refreshTable();
    }

    private void stopButtonClick(Button.ClickEvent event) {
        final InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000014", instance.getInstance().getInstanceName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                stop(instance.getInstance().getInstanceNo());
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void stop(Long instanceNo) {
        // オペレーションログ
        OperationLogger.writeInstance("SERVER", "Stop Server", instanceNo, null);

        // サーバを停止
        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.stopInstances(ViewContext.getFarmNo(), Arrays.asList(instanceNo));

        // 表示を更新
        refreshTable();
    }

    private void startMonitoringButtonClick(Button.ClickEvent event) {
        final InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000121", instance.getInstance().getInstanceName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                startMonitoring(instance);
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void startMonitoring(InstanceDto instance) {
        // オペレーションログ
        OperationLogger.writeInstance("SERVER", "Start Monitoring Server", instance.getInstance().getInstanceNo(),
                instance.getInstance().getInstanceName());

        //監視を有効化
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        instanceService.enableZabbixMonitoring(instance.getInstance().getInstanceNo());

        // 表示を更新
        refreshTable();
    }

    private void stopMonitoringButtonClick(Button.ClickEvent event) {
        final InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000122", instance.getInstance().getInstanceName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                stopMonitoring(instance);
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void stopMonitoring(InstanceDto instance) {
        // オペレーションログ
        OperationLogger.writeInstance("SERVER", "Stop Monitoring Server", instance.getInstance().getInstanceNo(),
                instance.getInstance().getInstanceName());

        // 監視を無効化
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        instanceService.disableZabbixMonitoring(instance.getInstance().getInstanceNo());

        // 表示を更新
        refreshTable();
    }

}
