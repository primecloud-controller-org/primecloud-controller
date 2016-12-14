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
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * <p>
 * ロードバランサ画面の中央にあるロードバランさ追加ボタンからストップボタンまでを生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerButtonsBottom extends CssLayout {

    private MainView sender;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    private Button startButton;

    private Button stopButton;

    public LoadBalancerButtonsBottom(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        addStyleName("loadbalancer-table-operation");
        setWidth("100%");
        setMargin(true);

        // Addボタン
        addButton = new Button(ViewProperties.getCaption("button.addLoadBalancer"));
        addButton.setDescription(ViewProperties.getCaption("description.addLoadBalancer"));
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
        editButton = new Button(ViewProperties.getCaption("button.editLoadBalancer"));
        editButton.setDescription(ViewProperties.getCaption("description.editLoadBalancer"));
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
        deleteButton = new Button(ViewProperties.getCaption("button.deleteLoadBalancer"));
        deleteButton.setDescription(ViewProperties.getCaption("description.deleteLoadBalancer"));
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
        stopButton = new Button(ViewProperties.getCaption("button.stopLoadBalancer"));
        stopButton.setDescription(ViewProperties.getCaption("description.stopLoadBalancer"));
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
        startButton = new Button(ViewProperties.getCaption("button.startLoadBalancer"));
        startButton.setDescription(ViewProperties.getCaption("description.startLoadBalancer"));
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

        initialize();
    }

    public void initialize() {
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    public void show(LoadBalancerDto loadBalancerDto) {
        // ステータスによってボタンの活性状態を切り替える
        LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancerDto.getLoadBalancer().getStatus());

        // Startボタン
        if (status == LoadBalancerStatus.STOPPED || status == LoadBalancerStatus.RUNNING) {
            startButton.setEnabled(true);
        } else {
            startButton.setEnabled(false);
        }

        // Stopボタン
        if (status == LoadBalancerStatus.RUNNING || status == LoadBalancerStatus.WARNING) {
            stopButton.setEnabled(true);
        } else {
            stopButton.setEnabled(false);
        }

        // Editボタン
        if (status == LoadBalancerStatus.STOPPED || status == LoadBalancerStatus.RUNNING) {
            editButton.setEnabled(true);
        } else {
            editButton.setEnabled(false);
        }

        // Deleteボタン
        if (status == LoadBalancerStatus.STOPPED) {
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
        }
    }

    private void refreshTable() {
        // 選択されているロードバランサを保持する
        Long selectedLoadBalancerNo = null;
        if (sender.loadBalancerPanel.loadBalancerTable.getValue() != null) {
            LoadBalancerDto loadBalancer = (LoadBalancerDto) sender.loadBalancerPanel.loadBalancerTable.getValue();
            selectedLoadBalancerNo = loadBalancer.getLoadBalancer().getLoadBalancerNo();
        }
        int index = sender.loadBalancerPanel.loadBalancerTable.getCurrentPageFirstItemIndex();

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサーバを選択し直す
        if (selectedLoadBalancerNo != null) {
            for (Object itemId : sender.loadBalancerPanel.loadBalancerTable.getItemIds()) {
                LoadBalancerDto loadBalancer = (LoadBalancerDto) itemId;
                if (selectedLoadBalancerNo.equals(loadBalancer.getLoadBalancer().getLoadBalancerNo())) {
                    sender.loadBalancerPanel.loadBalancerTable.select(itemId);
                    sender.loadBalancerPanel.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                    break;
                }
            }
        }
    }

    private void addButtonClick(ClickEvent event) {
        WinLoadBalancerAdd winLoadBalancerAdd = new WinLoadBalancerAdd();
        winLoadBalancerAdd.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                refreshTable();
            }
        });

        getWindow().addWindow(winLoadBalancerAdd);
    }

    private void startButtonClick(ClickEvent event) {
        final LoadBalancerDto loadBalancer = (LoadBalancerDto) sender.loadBalancerPanel.loadBalancerTable.getValue();

        // AWSロードバランサでVPCの場合、サブネットを設定していることを確認
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
            if (BooleanUtils.isTrue(loadBalancer.getPlatform().getPlatformAws().getVpc())) {
                if (StringUtils.isEmpty(loadBalancer.getAwsLoadBalancer().getSubnetId())) {
                    throw new AutoApplicationException("IUI-000111");
                }
            }
        }

        // 確認ダイアログを表示
        LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getLoadBalancer().getStatus());
        String code = status == LoadBalancerStatus.STOPPED ? "IUI-000057" : "IUI-000059";
        String message = ViewMessages.getMessage(code, loadBalancer.getLoadBalancer().getLoadBalancerName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                start(loadBalancer.getLoadBalancer().getLoadBalancerNo());
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void start(Long loadBalancerNo) {
        // オペレーションログ
        OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Start Load_Balancer", loadBalancerNo, null);

        // ロードバランサを起動
        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.startLoadBalancers(ViewContext.getFarmNo(), Arrays.asList(loadBalancerNo));

        // 表示を更新
        refreshTable();
    }

    private void stopButtonClick(ClickEvent event) {
        final LoadBalancerDto loadBalancer = (LoadBalancerDto) sender.loadBalancerPanel.loadBalancerTable.getValue();

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000058", loadBalancer.getLoadBalancer().getLoadBalancerName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                stop(loadBalancer.getLoadBalancer().getLoadBalancerNo());
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void stop(Long loadBalancerNo) {
        // オペレーションログ
        OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Stop Load_Balancer", loadBalancerNo, null);

        // ロードバランサを停止
        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.stopLoadBalancers(ViewContext.getFarmNo(), Arrays.asList(loadBalancerNo));

        // 表示を更新
        refreshTable();
    }

    private void editButtonClick(ClickEvent event) {
        LoadBalancerDto loadBalancer = (LoadBalancerDto) sender.loadBalancerPanel.loadBalancerTable.getValue();

        Window winLoadBalancerEdit;
        if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(loadBalancer.getLoadBalancer().getType())) {
            winLoadBalancerEdit = new WinCloudStackLoadBalancerEdit(loadBalancer.getLoadBalancer().getLoadBalancerNo());
        } else {
            winLoadBalancerEdit = new WinLoadBalancerEdit(loadBalancer.getLoadBalancer().getLoadBalancerNo());
        }

        winLoadBalancerEdit.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                refreshTable();
            }
        });

        getWindow().addWindow(winLoadBalancerEdit);
    }

    private void deleteButtonClick(ClickEvent event) {
        final LoadBalancerDto dto = (LoadBalancerDto) sender.loadBalancerPanel.loadBalancerTable.getValue();

        String message = ViewMessages.getMessage("IUI-000056", dto.getLoadBalancer().getLoadBalancerName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                delete(dto.getLoadBalancer().getLoadBalancerNo());
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    private void delete(Long loadBalancerNo) {
        // オペレーションログ
        OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Delete Load_Balancer", loadBalancerNo, null);

        // ロードバランサを削除
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        loadBalancerService.deleteLoadBalancer(loadBalancerNo);

        // ロードバランサの選択を解除
        sender.loadBalancerPanel.loadBalancerTable.select(null);

        // 表示を更新
        refreshTable();
    }

}
