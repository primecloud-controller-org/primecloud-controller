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
import java.util.List;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

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
public class LoadBalancerTableOperation extends CssLayout {

    Button btnNew;

    Button btnEdit;

    Button btnDelete;

    Button btnStart;

    Button btnStop;

    MyCloudTabs myCloudTabs;

    LoadBalancerTableOperation(MyCloudTabs sender) {
        this.myCloudTabs = sender;

        addStyleName("loadbalancer-table-operation");
        setWidth("100%");
        setMargin(true);

        // Newボタン
        btnNew = new Button(ViewProperties.getCaption("button.addLoadBalancer"));
        btnNew.setDescription(ViewProperties.getCaption("description.addLoadBalancer"));
        btnNew.setIcon(Icons.ADD.resource());
        btnNew.addStyleName("left");
        btnNew.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick();
            }
        });

        // Editボタン
        btnEdit = new Button(ViewProperties.getCaption("button.editLoadBalancer"));
        btnEdit.setDescription(ViewProperties.getCaption("description.editLoadBalancer"));
        btnEdit.setWidth("90px");
        btnEdit.setIcon(Icons.EDITMINI.resource());
        btnEdit.addStyleName("left");
        btnEdit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                editButtonClick();
            }
        });

        // Deleteボタン
        btnDelete = new Button(ViewProperties.getCaption("button.deleteLoadBalancer"));
        btnDelete.setDescription(ViewProperties.getCaption("description.deleteLoadBalancer"));
        btnDelete.setWidth("90px");
        btnDelete.setIcon(Icons.DELETEMINI.resource());
        btnDelete.addStyleName("left");
        btnDelete.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                delButtonClick();
            }
        });

        // Startボタン
        btnStart = new Button(ViewProperties.getCaption("button.startLoadBalancer"));
        btnStart.setDescription(ViewProperties.getCaption("description.startLoadBalancer"));
        btnStart.setWidth("90px");
        btnStart.setIcon(Icons.PLAYMINI.resource());
        btnStart.addStyleName("right");
        btnStart.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                playButtonClick();
            }
        });

        // Stopボタン
        btnStop = new Button(ViewProperties.getCaption("button.stopLoadBalancer"));
        btnStop.setDescription(ViewProperties.getCaption("description.stopLoadBalancer"));
        btnStop.setWidth("90px");
        btnStop.setIcon(Icons.STOPMINI.resource());
        btnStop.addStyleName("right");
        btnStop.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                stopButtonClick();
            }
        });

        //ボタンの初期化
        hide();

        Label spacer = new Label(" ", Label.CONTENT_XHTML);
        spacer.setWidth("30px");
        spacer.addStyleName("left");
        addComponent(btnNew);
        addComponent(spacer);
        addComponent(btnEdit);
        addComponent(btnDelete);
        addComponent(btnStop);
        addComponent(btnStart);

    }

    void hide() {
        //ボタンの初期化
        btnNew.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);

        //作成権限がなければ非活性
        UserAuthDto auth = ViewContext.getAuthority();
        if (!auth.isLbMake()) {
            btnNew.setEnabled(false);
        }
    }

    void refresh(Object loadbalancer) {
        if (loadbalancer != null) {
            //ステータスによってボタンの有効無効を切り替える
            String status = "";
            // TODO: ロードバランサーステータス取得ロジック
            //status = loadbalancer.getStatus();
            if ("STOPPED".equals(status)) {
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

            } else if ("RUNNING".equals(status)) {
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);

            } else if ("WARNING".equals(status)) {
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);

            } else {
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(false);
            }

            UserAuthDto auth = ViewContext.getAuthority();
            //権限に応じて操作可能なボタンを制御する
            if (!auth.isLbMake()) {
                btnNew.setEnabled(false);
                btnEdit.setEnabled(false);
            }
            if (!auth.isLbDelete()) {
                btnDelete.setEnabled(false);
            }
            if (!auth.isLbOperate()) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(false);
            }

        } else {
            //ボタンの無効化・非表示
            hide();
        }
    }

    public void addButtonClick() {
        // ロードバランサ追加処理
        final LoadBalancerDto dto = (LoadBalancerDto) myCloudTabs.loadBalancerTable.getValue();

        WinLoadBalancerAdd winLoadBalancerAdd = new WinLoadBalancerAdd();
        winLoadBalancerAdd.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                myCloudTabs.refreshTable();

                // 選択されていたロードバランサを選択し直す
                if (dto != null) {
                    for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                        LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                        if (dto.getLoadBalancer().getLoadBalancerNo()
                                .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                            myCloudTabs.loadBalancerTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        getWindow().addWindow(winLoadBalancerAdd);
    }

    public void playButtonClick() {
        // ロードバランサ開始処理
        final LoadBalancerDto dto = (LoadBalancerDto) myCloudTabs.loadBalancerTable.getValue();
        final int index = myCloudTabs.loadBalancerTable.getCurrentPageFirstItemIndex();

        if (PCCConstant.LOAD_BALANCER_ELB.equals(dto.getLoadBalancer().getType())) {
            PlatformDto platform = dto.getPlatform();
            if (platform.getPlatformAws().getVpc() && StringUtils.isEmpty(dto.getAwsLoadBalancer().getSubnetId())) {
                //ELB+VPCの場合、サブネットを設定しないと起動不可
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                        ViewMessages.getMessage("IUI-000111"));
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        LoadBalancerStatus status = LoadBalancerStatus.fromStatus(dto.getLoadBalancer().getStatus());
        String code = status == LoadBalancerStatus.STOPPED ? "IUI-000057" : "IUI-000059";
        String message = ViewMessages.getMessage(code, dto.getLoadBalancer().getLoadBalancerName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                ProcessService processService = BeanContext.getBean(ProcessService.class);
                Long farmNo = ViewContext.getFarmNo();
                List<Long> list = new ArrayList<Long>();

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("LOAD_BALANCER", "Start Load_Balancer", null, null, dto.getLoadBalancer()
                        .getLoadBalancerNo(), null);

                list.add(dto.getLoadBalancer().getLoadBalancerNo());
                processService.startLoadBalancers(farmNo, list);
                myCloudTabs.refreshTable();

                // 選択されていたロードバランサを選択し直す
                for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                    LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                    if (dto.getLoadBalancer().getLoadBalancerNo().equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                        myCloudTabs.loadBalancerTable.select(itemId);
                        myCloudTabs.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2);
                        break;
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void stopButtonClick() {
        // ロードバランサ停止処理
        final LoadBalancerDto dto = (LoadBalancerDto) myCloudTabs.loadBalancerTable.getValue();
        final int index = myCloudTabs.loadBalancerTable.getCurrentPageFirstItemIndex();

        String message = ViewMessages.getMessage("IUI-000058", dto.getLoadBalancer().getLoadBalancerName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                ProcessService processService = BeanContext.getBean(ProcessService.class);
                Long farmNo = ViewContext.getFarmNo();
                List<Long> list = new ArrayList<Long>();
                list.add(dto.getLoadBalancer().getLoadBalancerNo());

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("LOAD_BALANCER", "Stop Load_Balancer", null, null, dto.getLoadBalancer()
                        .getLoadBalancerNo(), null);

                processService.stopLoadBalancers(farmNo, list);
                myCloudTabs.refreshTable();

                // 選択されていたロードバランサを選択し直す
                for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                    LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                    if (dto.getLoadBalancer().getLoadBalancerNo().equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                        myCloudTabs.loadBalancerTable.select(itemId);
                        myCloudTabs.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2);
                        break;
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void editButtonClick() {
        // ロードバランサ編集処理
        final LoadBalancerDto dto = (LoadBalancerDto) myCloudTabs.loadBalancerTable.getValue();
        final int index = myCloudTabs.loadBalancerTable.getCurrentPageFirstItemIndex();

        Long loadBalancerNo = dto.getLoadBalancer().getLoadBalancerNo();
        if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(dto.getLoadBalancer().getType())) {
            WinCloudStackLoadBalancerEdit winLoadBalancerEdit = new WinCloudStackLoadBalancerEdit(getApplication(),
                    loadBalancerNo);
            winLoadBalancerEdit.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    myCloudTabs.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                        LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                        if (dto.getLoadBalancer().getLoadBalancerNo()
                                .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                            myCloudTabs.loadBalancerTable.select(itemId);
                            myCloudTabs.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                            setButtonStatus(dto2);
                            break;
                        }
                    }
                }
            });
            getWindow().addWindow(winLoadBalancerEdit);
        } else {
            WinLoadBalancerEdit winLoadBalancerEdit = new WinLoadBalancerEdit(getApplication(), loadBalancerNo);
            winLoadBalancerEdit.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    myCloudTabs.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                        LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                        if (dto.getLoadBalancer().getLoadBalancerNo()
                                .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                            myCloudTabs.loadBalancerTable.select(itemId);
                            myCloudTabs.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                            setButtonStatus(dto2);
                            break;
                        }
                    }
                }
            });
            getWindow().addWindow(winLoadBalancerEdit);
        }
        myCloudTabs.refreshTable();
    }

    public void delButtonClick() {
        // ロードバランサ削除処理
        final LoadBalancerDto dto = (LoadBalancerDto) myCloudTabs.loadBalancerTable.getValue();
        final int index = myCloudTabs.loadBalancerTable.getCurrentPageFirstItemIndex();

        String message = ViewMessages.getMessage("IUI-000056", dto.getLoadBalancer().getLoadBalancerName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("LOAD_BALANCER", "Delete Load_Balancer", null, null, dto.getLoadBalancer()
                        .getLoadBalancerNo(), null);

                Long loadBalancerNo = dto.getLoadBalancer().getLoadBalancerNo();
                LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
                loadBalancerService.deleteLoadBalancer(loadBalancerNo);
                myCloudTabs.loadBalancerTable.select(null);
                myCloudTabs.refreshTable();
                myCloudTabs.loadBalancerTable.setCurrentPageFirstItemIndex(index);
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void setButtonStatus(LoadBalancerDto loadBalancerDto) {
        if (loadBalancerDto != null) {
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancerDto.getLoadBalancer().getStatus());

            switch (status) {
                case STOPPED:
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(true);
                    break;

                case RUNNING:
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(true);
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(false);
                    break;

                case WARNING:
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    break;

                case STARTING:
                case CONFIGURING:
                case STOPPING:
                default:
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    break;
            }

            UserAuthDto auth = ViewContext.getAuthority();
            //権限に応じて操作可能なボタンを制御する
            if (!auth.isLbMake()) {
                btnNew.setEnabled(false);
                btnEdit.setEnabled(false);
            }
            if (!auth.isLbDelete()) {
                btnDelete.setEnabled(false);
            }
            if (!auth.isLbOperate()) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(false);
            }
        } else {
            hide();
        }
    }

}
