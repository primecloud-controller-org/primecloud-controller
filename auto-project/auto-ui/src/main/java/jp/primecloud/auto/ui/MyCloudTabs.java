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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vaadin.henrik.refresher.Refresher;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.ComponentStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.common.status.ZabbixInstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.ZabbixInstance;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.data.ComponentDtoContainer;
import jp.primecloud.auto.ui.data.ComponentParameterContainer;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;
import jp.primecloud.auto.ui.data.InstanceParameterContainer;
import jp.primecloud.auto.ui.data.LoadBalancerDtoContainer;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * メイン画面の共通部分を生成します。<br>
 * 共通部分とは、画面上部にあるタブ（Viewの切り替え用タブ）、現在表示している画面の名称、StartAll・StopALLボタンのこと。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class MyCloudTabs extends Panel {

    protected Log log = LogFactory.getLog(MyCloudTabs.class);

    boolean enableService = true;

    boolean enableLoadBalancer = true;

    TabSheet tabDesc = new TabSheet();

    Panel pnService = new Panel();

    Panel pnServer = new Panel();

    Panel pnLoadBalancer = new Panel();

    ServiceDesc serviceDesc = new ServiceDesc();

    ServerDesc serverDesc = new ServerDesc();

    LoadBalancerDesc loadBalancerDesc = new LoadBalancerDesc();

    ServiceButtonsTop serviceButtonsTop = new ServiceButtonsTop();

    ServiceButtonsBottom serviceButtonsBottom = new ServiceButtonsBottom();

    ServerButtonsTop serverButtonsTop = new ServerButtonsTop();

    ServerButtonsButtom serverButtonsBottom = new ServerButtonsButtom();

    LoadBalancerTableOperation loadBalancerTableOpe = new LoadBalancerTableOperation(this);

    ServiceTable serviceTable = new ServiceTable(null, new ComponentDtoContainer(), this);

    ServerTable serverTable = new ServerTable(null, new InstanceDtoContainer(), this);

    LoadBalancerTable loadBalancerTable = new LoadBalancerTable(null, new LoadBalancerDtoContainer(), this);

    MyCloudTabs() {
        // サービスを有効にするかどうか
        String enableService = Config.getProperty("ui.enableService");
        this.enableService = (enableService == null) || (BooleanUtils.toBoolean(enableService));

        // ロードバランサの有効/無効を判定
        String enableLoadBalancer = Config.getProperty("ui.enableLoadBalancer");
        this.enableLoadBalancer = (enableLoadBalancer == null) || (BooleanUtils.toBoolean(enableLoadBalancer));

        setSizeFull();

        // 最初はdisableに設定しておく
        tabDesc.setSizeFull();
        tabDesc.setEnabled(false);
        tabDesc.addStyleName(Reindeer.TABSHEET_BORDERLESS);

        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setSizeFull();
        layout.setSpacing(false);
        layout.setMargin(false);

        //サービス用タブ設定
        pnService.setSizeFull();
        pnService.addStyleName(Reindeer.PANEL_LIGHT);
        VerticalLayout vlService = (VerticalLayout) pnService.getContent();
        vlService.setSizeFull();
        vlService.addStyleName("service-tab");
        vlService.setSpacing(false);
        vlService.setMargin(false);

        //スプリットパネル
        SplitPanel splService = new SplitPanel();
        splService.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splService.setSplitPosition(40);
        splService.setSizeFull();
        vlService.addComponent(splService);
        vlService.setExpandRatio(splService, 10);
        //スプリットパネル上段
        VerticalLayout layServiceUpper = new VerticalLayout();
        layServiceUpper.setSizeFull();
        layServiceUpper.setSpacing(false);
        layServiceUpper.setMargin(false);
        layServiceUpper.addComponent(serviceButtonsTop);
        layServiceUpper.addComponent(serviceTable);
        layServiceUpper.addComponent(serviceButtonsBottom);
        layServiceUpper.setExpandRatio(serviceTable, 10);
        splService.addComponent(layServiceUpper);
        //スプリットパネル下段
        splService.addComponent(serviceDesc);

        if (this.enableService) {
            tabDesc.addTab(pnService, ViewProperties.getCaption("tab.service"), Icons.SERVICETAB.resource());
        }

        //サーバ用タブ
        pnServer.setSizeFull();
        pnServer.addStyleName(Reindeer.PANEL_LIGHT);
        VerticalLayout vlServer = (VerticalLayout) pnServer.getContent();
        vlServer.setSizeFull();
        vlServer.addStyleName("server-tab");
        vlServer.setSpacing(false);
        vlServer.setMargin(false);

        //スプリットパネル
        SplitPanel splServer = new SplitPanel();
        splServer.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splServer.setSplitPosition(40);
        splServer.setSizeFull();
        vlServer.addComponent(splServer);
        //スプリットパネル上段
        VerticalLayout layServerUpper = new VerticalLayout();
        layServerUpper.setSizeFull();
        layServerUpper.setSpacing(false);
        layServerUpper.setMargin(false);
        layServerUpper.addComponent(serverButtonsTop);
        layServerUpper.addComponent(serverTable);
        layServerUpper.addComponent(serverButtonsBottom);
        layServerUpper.setExpandRatio(serverTable, 10);
        splServer.addComponent(layServerUpper);
        //スプリットパネル下段
        splServer.addComponent(serverDesc);

        tabDesc.addTab(pnServer, ViewProperties.getCaption("tab.server"), Icons.SERVERTAB.resource());


        //ロードバランサー用タブ
        pnLoadBalancer.setSizeFull();
        pnLoadBalancer.addStyleName(Reindeer.PANEL_LIGHT);
        VerticalLayout vlLBalancer = (VerticalLayout) pnLoadBalancer.getContent();
        vlLBalancer.setSizeFull();
        vlLBalancer.addStyleName("loadbalancer-tab");
        vlLBalancer.setSpacing(false);
        vlLBalancer.setMargin(false);

        CssLayout hLBalancer = new CssLayout();
        Label lLBalancer = new Label(ViewProperties.getCaption("label.loadbalancer"));
        hLBalancer.setWidth("100%");
        hLBalancer.setMargin(true);
        hLBalancer.addStyleName("loadbalancer-table-label");
        hLBalancer.addComponent(lLBalancer);
        hLBalancer.setHeight("28px");

        //スプリットパネル
        SplitPanel splLBalancer = new SplitPanel();
        splLBalancer.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        splLBalancer.setSplitPosition(40);
        splLBalancer.setSizeFull();
        vlLBalancer.addComponent(splLBalancer);
        //スプリットパネル上段
        VerticalLayout layLBUpper = new VerticalLayout();
        layLBUpper.setSizeFull();
        layLBUpper.setSpacing(false);
        layLBUpper.setMargin(false);
        layLBUpper.addComponent(hLBalancer);
        layLBUpper.addComponent(loadBalancerTable);
        layLBUpper.addComponent(loadBalancerTableOpe);
        layLBUpper.setExpandRatio(loadBalancerTable, 10);
        splLBalancer.addComponent(layLBUpper);
        //スプリットパネル下段
        splLBalancer.addComponent(loadBalancerDesc);

        if (this.enableLoadBalancer) {
            tabDesc.addTab(pnLoadBalancer, ViewProperties.getCaption("tab.loadbalancer"), Icons.LOADBALANCER_TAB.resource());
        }



        //タブ用リスナー
        tabDesc.addListener(TabSheet.SelectedTabChangeEvent.class, this, "selectedTabChange");
        layout.addComponent(tabDesc);

        Refresher timer = new Refresher();
        timer.setRefreshInterval(15 * 1000); //更新間隔(msec)
        timer.addListener(new Refresher.RefreshListener() {
            @Override
            public void refresh(Refresher source) {
                if (needsRefresh()) {
                    refreshTableOnly();
                }
            }
        });
        layout.addComponent(timer);
        layout.setExpandRatio(tabDesc, 100);

    }

    private class ServiceButtonsTop extends CssLayout implements Button.ClickListener {
        Button btnPlay, btnStop;

        ServiceButtonsTop() {

            //テーブル下ボタンの配置
            setWidth("100%");
            setMargin(false);
            addStyleName("service-buttons");
            addStyleName("service-table-label");
            Label lservice = new Label(ViewProperties.getCaption("label.service"),Label.CONTENT_XHTML);
            lservice.setWidth("200px");
            addComponent(lservice);

            btnStop = new Button(ViewProperties.getCaption("button.stopAllServices"));
            btnStop.setDescription(ViewProperties.getCaption("description.stopAllServices"));
            btnStop.setIcon(Icons.STOPMINI.resource());
            btnStop.addListener(this);
            btnStop.addStyleName("right");
            addComponent(btnStop);

            btnPlay = new Button(ViewProperties.getCaption("button.startAllServices"));
            btnPlay.setDescription(ViewProperties.getCaption("description.startAllServices"));
            btnPlay.setIcon(Icons.PLAYMINI.resource());
            btnPlay.addListener(this);
            btnPlay.addStyleName("right");
            addComponent(btnPlay);

            hide();


        }

        void hide(){
            btnStop.setEnabled(true);
            btnPlay.setEnabled(true);
            //オペレート権限がなければ非活性
            UserAuthDto auth = ViewContext.getAuthority();
            if (!auth.isServiceOperate()){
                btnStop.setEnabled(false);
                btnPlay.setEnabled(false);
            }
        }


        public void buttonClick(ClickEvent event) {
            final ComponentDto dto = (ComponentDto) serviceTable.getValue();

            if (event.getButton() == btnPlay) {
                List<ComponentDto> componentDtos = new ArrayList<ComponentDto>();
                for (Object itemId : serviceTable.getItemIds()) {
                    componentDtos.add((ComponentDto) itemId);
                }

                if (!componentDtos.isEmpty()) {
                    for (ComponentDto componentDto : componentDtos) {
                        if (componentDto.getStatus().equals(ComponentStatus.STARTING.toString())
                                || componentDto.getStatus().equals(ComponentStatus.STOPPING.toString())
                                || componentDto.getStatus().equals(ComponentStatus.CONFIGURING.toString())) {
                            String message = ViewMessages.getMessage("IUI-000046", new Object[] { StringUtils
                                    .capitalize(componentDto.getStatus().toLowerCase()) });
                            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"),
                                    message);
                            getApplication().getMainWindow().addWindow(dialog);
                            return;
                        }
                    }
                }

                String message = ViewMessages.getMessage("IUI-000009");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                        Buttons.OKCancelConfirm);
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onDialogResult(Result result) {
                        if (result != Result.OK) {
                            return;
                        }

                        ProcessService processService = BeanContext.getBean(ProcessService.class);
                        Long farmNo = ViewContext.getFarmNo();
                        List<Long> componentNos = new ArrayList<Long>();
                        for (Object itemId : serviceTable.getItemIds()) {
                            componentNos.add(((ComponentDto) itemId).getComponent().getComponentNo());
                        }

                        //オペレーションログ
                        AutoApplication apl = (AutoApplication)getApplication();
                        apl.doOpLog("SERVICE", "All Service Start", null, null, null, null);

                        processService.startComponents(farmNo, componentNos);
                        MyCloudTabs.this.refreshTable();

                        // 選択されていたサービスを選択し直す
                        if (dto != null) {
                            for (Object itemId : serviceTable.getItemIds()) {
                                ComponentDto dto2 = (ComponentDto) itemId;
                                if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                                    serviceTable.select(itemId);
                                    break;
                                }
                            }
                        }
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            } else if (event.getButton() == btnStop) {
                HorizontalLayout optionLayout = new HorizontalLayout();
                final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000033"), false);
                checkBox.setImmediate(true);
                optionLayout.addComponent(checkBox);

                String message = ViewMessages.getMessage("IUI-000010");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                        Buttons.OKCancelConfirm, optionLayout);
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onDialogResult(Result result) {
                        if (result != Result.OK) {
                            return;
                        }
                        ProcessService processService = BeanContext.getBean(ProcessService.class);
                        Long farmNo = ViewContext.getFarmNo();
                        List<Long> componentNos = new ArrayList<Long>();
                        for (Object itemId : serviceTable.getItemIds()) {
                            componentNos.add(((ComponentDto) itemId).getComponent().getComponentNo());
                        }
                        boolean stopInstance = (Boolean) checkBox.getValue();

                        //オペレーションログ
                        AutoApplication apl = (AutoApplication)getApplication();
                        apl.doOpLog("SERVICE", "All Service Stop", null, null, null, String.valueOf(stopInstance));

                        processService.stopComponents(farmNo, componentNos, stopInstance);
                        MyCloudTabs.this.refreshTable();

                        // 選択されていたサービスを選択し直す
                        if (dto != null) {
                            for (Object itemId : serviceTable.getItemIds()) {
                                ComponentDto dto2 = (ComponentDto) itemId;
                                if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                                    serviceTable.select(itemId);
                                    break;
                                }
                            }
                        }
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            }
        }
    }

    private class ServiceButtonsBottom extends CssLayout {
        Button btnNew;
        Button btnEdit;
        Button btnDelete;

        ServiceButtonsBottom() {

            //テーブル下ボタンの配置
            setWidth("100%");
            setMargin(true);
            addStyleName("service-buttons");

            // Newボタン
            btnNew = new Button(ViewProperties.getCaption("button.addService"));
            btnNew.setDescription(ViewProperties.getCaption("description.addService"));
            btnNew.setIcon(Icons.ADD.resource());
            btnNew.addStyleName("left");
            btnNew.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    addButtonClick(event);
                }
            });

            // Editボタン
            btnEdit = new Button(ViewProperties.getCaption("button.editService"));
            btnEdit.setDescription(ViewProperties.getCaption("description.editService"));
            btnEdit.setWidth("90px");
            btnEdit.setIcon(Icons.EDITMINI.resource());
            btnEdit.addStyleName("left");
            btnEdit.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serviceTable.editButtonClick(event);
                }
            });

            // Deleteボタン
            btnDelete = new Button(ViewProperties.getCaption("button.deleteService"));
            btnDelete.setDescription(ViewProperties.getCaption("description.deleteService"));
            btnDelete.setWidth("90px");
            btnDelete.setIcon(Icons.DELETEMINI.resource());
            btnDelete.addStyleName("left");
            btnDelete.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serviceTable.delButtonClick(event);
                }
            });

            //ボタンの無効化
            hide();

            Label spacer = new Label(" ",Label.CONTENT_XHTML);
            spacer.setWidth("30px");
            spacer.addStyleName("left");
            addComponent(btnNew);
            addComponent(spacer);
            addComponent(btnEdit);
            addComponent(btnDelete);
        }

        public void addButtonClick(ClickEvent event) {
            final ComponentDto dto = (ComponentDto) serviceTable.getValue();
            WinServiceAdd winServiceAdd = new WinServiceAdd(getApplication());
            winServiceAdd.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    MyCloudTabs.this.refreshTable();

                    // 選択されていたサービスを選択し直す
                    if (dto != null) {
                        for (Object itemId : serviceTable.getItemIds()) {
                            ComponentDto dto2 = (ComponentDto) itemId;
                            if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                                serviceTable.select(itemId);
                                break;
                            }
                        }
                    }
                }
            });
            getWindow().addWindow(winServiceAdd);
        }

        void hide(){
            //ボタンの無効化
            btnNew.setEnabled(true);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);

            //作成権限がなければ非活性
            UserAuthDto auth = ViewContext.getAuthority();
            if (!auth.isServiceMake()){
                btnNew.setEnabled(false);
            }

        }

        void refresh(ComponentDto dto){
            if (dto != null) {
                btnNew.setEnabled(true);
                //ステータスによってボタンの有効無効を切り替える
                String status = dto.getStatus();
                if ("STOPPED".equals(status)) {
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(true);
                } else if ("RUNNING".equals(status)) {
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(false);
                } else if ("WARNING".equals(status)) {
                    boolean processing = false;
                    for (ComponentInstanceDto componentInstance : dto.getComponentInstances()) {
                        ComponentInstanceStatus s = ComponentInstanceStatus.fromStatus(componentInstance.getComponentInstance().getStatus());
                        if (s != ComponentInstanceStatus.RUNNING && s != ComponentInstanceStatus.WARNING
                                && s != ComponentInstanceStatus.STOPPED) {
                            processing = true;
                            break;
                        }
                    }
                    btnEdit.setEnabled(!processing);
                    btnDelete.setEnabled(false);
                } else {
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                }

                //権限に応じて操作可能なボタンを制御する
                UserAuthDto auth = ViewContext.getAuthority();
                if (!auth.isServiceMake()){
                    btnNew.setEnabled(false);
                    btnEdit.setEnabled(false);
                }
                if (!auth.isServiceDelete()){
                    btnDelete.setEnabled(false);
                }


            } else {
                //ボタンの無効化・非表示
                hide();
            }
        }
    }

    private class ServerButtonsTop extends CssLayout implements Button.ClickListener {
        Button btnPlay, btnStop;

        ServerButtonsTop() {
            //テーブル下ボタンの配置
            setWidth("100%");
            setMargin(true);
            addStyleName("server-buttons");
            addStyleName("server-table-label");
            Label lserver = new Label(ViewProperties.getCaption("label.server"),Label.CONTENT_XHTML);
            lserver.setWidth("200px");
            addComponent(lserver);

            btnStop = new Button(ViewProperties.getCaption("button.stopAllServers"));
            btnStop.setDescription(ViewProperties.getCaption("description.stopAllServers"));
            btnStop.setIcon(Icons.STOPMINI.resource());
            btnStop.addListener(this);
            btnStop.addStyleName("right");
            addComponent(btnStop);

            btnPlay = new Button(ViewProperties.getCaption("button.startAllServers"));
            btnPlay.setDescription(ViewProperties.getCaption("description.startAllServers"));
            btnPlay.setIcon(Icons.PLAYMINI.resource());
            btnPlay.addListener(this);
            btnPlay.addStyleName("right");
            addComponent(btnPlay);

            hide();

        }

        void hide(){
            btnStop.setEnabled(true);
            btnPlay.setEnabled(true);
            //オペレート権限がなければ非活性
            UserAuthDto auth = ViewContext.getAuthority();
            if (!auth.isServerOperate()){
                btnStop.setEnabled(false);
                btnPlay.setEnabled(false);
            }
        }

        public void buttonClick(ClickEvent event) {
            final InstanceDto dto = (InstanceDto) serverTable.getValue();

            if (event.getButton() == btnPlay) {
                VerticalLayout optionLayout = new VerticalLayout();
                final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000035"), false);
                checkBox.setImmediate(true);
                optionLayout.addComponent(checkBox);
                optionLayout.setComponentAlignment(checkBox, Alignment.MIDDLE_CENTER);
                if (!enableService) {
                    optionLayout = null;
                }

                String message = ViewMessages.getMessage("IUI-000011");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                        Buttons.OKCancelConfirm , optionLayout);
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onDialogResult(Result result) {
                        if (result != Result.OK) {
                            return;
                        }
                        ProcessService processService = BeanContext.getBean(ProcessService.class);
                        Long farmNo = ViewContext.getFarmNo();
                        List<Long> instanceNos = new ArrayList<Long>();
                        for (Object itemId : serverTable.getItemIds()) {
                            instanceNos.add(((InstanceDto) itemId).getInstance().getInstanceNo());
                        }
                        boolean startService = (Boolean) checkBox.getValue();

                        //オペレーションログ
                        AutoApplication apl = (AutoApplication)getApplication();
                        apl.doOpLog("SERVER", "All Server Start", null, null, null, String.valueOf(startService));

                        processService.startInstances(farmNo, instanceNos, startService);
                        MyCloudTabs.this.refreshTable();

                        // 選択されていたサーバを選択し直す
                        if (dto != null) {
                            for (Object itemId : serverTable.getItemIds()) {
                                InstanceDto dto2 = (InstanceDto) itemId;
                                if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                                    serverTable.select(itemId);
                                    break;
                                }
                            }
                        }
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            } else if (event.getButton() == btnStop) {
                String message = ViewMessages.getMessage("IUI-000012");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancelConfirm);
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onDialogResult(Result result) {
                        if (result != Result.OK) {
                            return;
                        }
                        ProcessService processService = BeanContext.getBean(ProcessService.class);
                        Long farmNo = ViewContext.getFarmNo();
                        List<Long> instanceNos = new ArrayList<Long>();
                        for (Object itemId : serverTable.getItemIds()) {
                            instanceNos.add(((InstanceDto) itemId).getInstance().getInstanceNo());
                        }

                        //オペレーションログ
                        AutoApplication apl = (AutoApplication)getApplication();
                        apl.doOpLog("SERVER", "All Server Stop", null, null, null, null);

                        processService.stopInstances(farmNo, instanceNos);
                        MyCloudTabs.this.refreshTable();

                        // 選択されていたサーバを選択し直す
                        if (dto != null) {
                            for (Object itemId : serverTable.getItemIds()) {
                                InstanceDto dto2 = (InstanceDto) itemId;
                                if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                                    serverTable.select(itemId);
                                    break;
                                }
                            }
                        }
                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            }
        }

    }

    private class ServerButtonsButtom extends CssLayout {
        Button btnNew;
        Button btnEdit;
        Button btnDelete;
        Button btnStartMonitoring;
        Button btnStopMonitoring;
        Button btnStart;
        Button btnStop;

        ServerButtonsButtom() {
            //テーブル下ボタンの配置
            setWidth("100%");
            setMargin(true);
            addStyleName("server-buttons");

            btnNew = new Button(ViewProperties.getCaption("button.addServer"));
            btnNew.setDescription(ViewProperties.getCaption("description.addServer"));
            btnNew.setIcon(Icons.ADD.resource());
            btnNew.addStyleName("left");
            btnNew.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    addButtonClick(event);
                }
            });

            // Editボタン
            btnEdit = new Button(ViewProperties.getCaption("button.editServer"));
            btnEdit.setDescription(ViewProperties.getCaption("description.editServer"));
            btnEdit.setWidth("90px");
            btnEdit.setIcon(Icons.EDITMINI.resource());
            btnEdit.addStyleName("left");
            btnEdit.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serverTable.editButtonClick(event);
                }
            });

            // Deleteボタン
            btnDelete = new Button(ViewProperties.getCaption("button.deleteServer"));
            btnDelete.setDescription(ViewProperties.getCaption("description.deleteServer"));
            btnDelete.setWidth("90px");
            btnDelete.setIcon(Icons.DELETEMINI.resource());
            btnDelete.addStyleName("left");
            btnDelete.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serverTable.delButtonClick(event);
                }
            });

            // Start Monitoring ボタン
            btnStartMonitoring = new Button(ViewProperties.getCaption("button.startMonitoring"));
            btnStartMonitoring.setDescription(ViewProperties.getCaption("description.startMonitoring"));
            btnStartMonitoring.setWidth("150px");
            btnStartMonitoring.setIcon(Icons.START_MONITORING.resource());
            btnStartMonitoring.addStyleName("right");
            btnStartMonitoring.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serverTable.startMonitoringButtonClick(event);
                }
            });

            // Stop Monitoring ボタン
            btnStopMonitoring = new Button(ViewProperties.getCaption("button.stopMonitoring"));
            btnStopMonitoring.setDescription(ViewProperties.getCaption("description.stopMonitoring"));
            btnStopMonitoring.setWidth("150px");
            btnStopMonitoring.setIcon(Icons.STOP_MONITORING.resource());
            btnStopMonitoring.addStyleName("right");
            btnStopMonitoring.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serverTable.stopMonitoringButtonClick(event);
                }
            });

            // Startボタン
            btnStart = new Button(ViewProperties.getCaption("button.startServer"));
            btnStart.setDescription(ViewProperties.getCaption("description.startServer"));
            btnStart.setWidth("90px");
            btnStart.setIcon(Icons.PLAYMINI.resource());
            btnStart.addStyleName("right");
            btnStart.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serverTable.playButtonClick(event);
                }
            });

            // Stopボタン
            btnStop = new Button(ViewProperties.getCaption("button.stopServer"));
            btnStop.setDescription(ViewProperties.getCaption("description.stopServer"));
            btnStop.setWidth("90px");
            btnStop.setIcon(Icons.STOPMINI.resource());
            btnStop.addStyleName("right");
            btnStop.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    serverTable.stopButtonClick(event);
                }
            });

            //ボタンの初期化
            hide();

            Label spacer = new Label(" ",Label.CONTENT_XHTML);
            spacer.setWidth("30px");
            spacer.addStyleName("left");
            addComponent(btnNew);
            addComponent(spacer);
            addComponent(btnEdit);
            addComponent(btnDelete);
            addComponent(btnStop);
            addComponent(btnStart);

            //Zabbix使用フラグ と Zabbix監視変更可否フラグがtrueの場合のみ表示
            Boolean useZabbix = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useZabbix"));
            Boolean changeMonitoring = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.changeMonitoring"));
            if (BooleanUtils.isTrue(useZabbix) && BooleanUtils.isTrue(changeMonitoring)) {
                addComponent(btnStopMonitoring);
                addComponent(btnStartMonitoring);
            }
        }

        public void addButtonClick(ClickEvent event) {
            final InstanceDto dto = (InstanceDto) serverTable.getValue();

            WinServerAdd winServerAdd = new WinServerAdd(getApplication());
            winServerAdd.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    MyCloudTabs.this.refreshTable();

                    // 選択されていたサーバを選択し直す
                    if (dto != null) {
                        for (Object itemId : serverTable.getItemIds()) {
                            InstanceDto dto2 = (InstanceDto) itemId;
                            if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                                serverTable.select(itemId);
                                break;
                            }
                        }
                    }
                }
            });
            getWindow().addWindow(winServerAdd);
        }

        void hide(){
            //ボタンの初期化
            btnNew.setEnabled(true);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
            btnStartMonitoring.setEnabled(false);
            btnStopMonitoring.setEnabled(false);
            btnStart.setEnabled(false);
            btnStop.setEnabled(false);
            //作成権限がなければ非活性
            UserAuthDto auth = ViewContext.getAuthority();
            if (!auth.isServerMake()){
                btnNew.setEnabled(false);
            }

            ////ReloadボタンをStartボタンに
            btnStart.setCaption(ViewProperties.getCaption("button.startServer"));
            btnStart.setDescription(ViewProperties.getCaption("description.startServer"));

        }

        void refresh(InstanceDto instanceDto){

            if (instanceDto != null && instanceDto.getInstance() != null) {
                btnNew.setEnabled(true);
                //ステータスによってボタンの有効無効を切り替える
                String status = instanceDto.getInstance().getStatus();
                if ("STOPPED".equals(status)) {
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(true);
                    btnStartMonitoring.setEnabled(false);
                    btnStopMonitoring.setEnabled(false);
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);

                } else if ("RUNNING".equals(status)) {
                    ZabbixInstance zabbixInstance = instanceDto.getZabbixInstance();
                    boolean monitoring = false;
                    boolean unMonitoring = false;
                    if (zabbixInstance != null) {
                        ZabbixInstanceStatus zStatus = ZabbixInstanceStatus.fromStatus(zabbixInstance.getStatus());
                        if (ZabbixInstanceStatus.MONITORING.equals(zStatus)) {
                            monitoring = true;
                        } else if (ZabbixInstanceStatus.UN_MONITORING.equals(zStatus)){
                            unMonitoring = true;
                        }
                    }

                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(false);
                    btnStartMonitoring.setEnabled(unMonitoring);
                    btnStopMonitoring.setEnabled(monitoring);
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);

                } else if ("WARNING".equals(status)) {
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnStartMonitoring.setEnabled(false);
                    btnStopMonitoring.setEnabled(false);
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);

                } else {
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(false);
                    btnStart.setEnabled(false);
                    btnStartMonitoring.setEnabled(false);
                    btnStopMonitoring.setEnabled(false);
                    btnStop.setEnabled(false);
                }

                UserAuthDto auth = ViewContext.getAuthority();
                //権限に応じて操作可能なボタンを制御する
                if (!auth.isServerMake()){
                    btnNew.setEnabled(false);
                    btnEdit.setEnabled(false);
                }
                if (!auth.isServerDelete()){
                    btnDelete.setEnabled(false);
                }
                if (!auth.isServerOperate()){
                    btnStartMonitoring.setEnabled(false);
                    btnStopMonitoring.setEnabled(false);
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(false);
                }

            } else {
                //ボタンの無効化・非表示
                hide();
            }
        }
    }

    public void tableRowSelected(Table.ValueChangeEvent event) {

        synchronized (getApplication()) {
            //選択行のボタンを有効にする
            Property p = event.getProperty();
            if (p instanceof ServiceTable) {
                ServiceTable table = (ServiceTable) p;
                ComponentDto dto = (ComponentDto) table.getValue();
                serviceButtonsBottom.refresh(dto);

                if (dto != null) {
                    table.setButtonStatus(dto);
                    // サービス情報の更新(選択されているTabだけ更新する)
                    if ( serviceDesc.tabDesc.getSelectedTab() == serviceDesc.serviceDescBasic ){
                        //基本情報の更新
                        serviceDesc.serviceDescBasic.left.setItem(dto);
                        serviceDesc.serviceDescBasic.right.refresh(new InstanceDtoContainer(MyCloudTabs.this
                                .getInstances(dto.getComponentInstances())), true);
                    }else if ( serviceDesc.tabDesc.getSelectedTab() == serviceDesc.serviceDescDetail ){
                        //詳細情報の更新
                        serviceDesc.serviceDescDetail.left.setItem(dto);
                        Collection<InstanceDto> instanceDtos = (Collection<InstanceDto>) serverTable.getItemIds();
                        serviceDesc.serviceDescDetail.right.setContainerDataSource(new ComponentParameterContainer(dto,instanceDtos));
                        serviceDesc.serviceDescDetail.right.setHeaders();
                    }
                }
            } else if (p instanceof ServerTable) {
                ServerTable table = (ServerTable) p;
                InstanceDto dto = (InstanceDto) table.getValue();
                Instance instance = dto != null ? dto.getInstance() : null;

                serverButtonsBottom.refresh(dto);
                if (dto != null) {
                    table.setButtonStatus(dto.getInstance());
                    // サーバ情報の更新(選択されているTabだけ更新する)
                    if ( serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescBasic ){
                        //基本情報の更新
                        serverDesc.serverDescBasic.left.setItem(dto);
                        serverDesc.serverDescBasic.right.refresh(
                                new ComponentDtoContainer(MyCloudTabs.this.getComponents(dto.getComponentInstances())));
                    }else if ( serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescDetail ){
                        //詳細情報の更新
                        serverDesc.serverDescDetail.left.setServerName(instance);
                        serverDesc.serverDescDetail.right.setContainerDataSource(new InstanceParameterContainer(dto));
                        serverDesc.serverDescDetail.right.setHeaders();
                    }
                }
            } else if (p instanceof LoadBalancerTable) {
                LoadBalancerTable table = (LoadBalancerTable) p;
                LoadBalancerDto dto = (LoadBalancerDto) table.getValue();
                loadBalancerTableOpe.setButtonStatus(dto);
                // ロードバランサー情報の更新(選択されているTabだけ更新する)
                if (loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerDesc.loadBalancerDescBasic) {
                    //基本情報の更新
                    loadBalancerDesc.loadBalancerDescBasic.basicInfo.setItem(dto);
                    // ロードバランサー サービス テーブル情報更新
                    loadBalancerDesc.loadBalancerDescBasic.attachServiceTable.refresh(dto,true);

//                } else if (loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerDesc.loadBalancerDescDetail) {
//                    //詳細情報の更新

                } else if (loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerDesc.loadBalancerDescServer) {
                    // 割り当てサービス 詳細情報更新
                    loadBalancerDesc.loadBalancerDescServer.loadBalancerInfo.setItem(dto);
                    // ロードバランサー サーバ テーブル情報更新
                    loadBalancerDesc.loadBalancerDescServer.attachServiceServerTable.refresh(dto,true);
                }

            }
        }
    }

    public Collection<ComponentDto> getComponents(List<ComponentInstanceDto> componentInstances) {
        Collection<ComponentDto> dtos = (Collection<ComponentDto>) serviceTable.getItemIds();
        Set<ComponentDto> result = new LinkedHashSet<ComponentDto>();
        for (ComponentDto dto : dtos) {
            for (ComponentInstanceDto ci : componentInstances) {
                if (ci.getComponentInstance().getComponentNo().equals(dto.getComponent().getComponentNo())) {
                    result.add(dto);
                    break;
                }
            }
        }
        return result;
    }

    public Collection<InstanceDto> getInstances(List<ComponentInstanceDto> componentInstances) {
        Collection<InstanceDto> dtos = (Collection<InstanceDto>) serverTable.getItemIds();
        Set<InstanceDto> result = new LinkedHashSet<InstanceDto>();
        for (InstanceDto dto : dtos) {
            for (ComponentInstanceDto ci : componentInstances) {
                if (ci.getComponentInstance().getInstanceNo().equals(dto.getInstance().getInstanceNo())) {
                    result.add(dto);
                    break;
                }
            }
        }
        return result;
    }


    public void hide() {
        //初期表示タブの表示を権限により制御する
        if (tabDesc.getSelectedTab() == pnService) {
            serviceButtonsTop.hide();
            serviceButtonsBottom.hide();
        } else if (tabDesc.getSelectedTab() == pnServer) {
            serverButtonsTop.hide();
            serverButtonsBottom.hide();
        } else if (tabDesc.getSelectedTab() == pnLoadBalancer){
            loadBalancerTableOpe.hide();
        }
    }

    public void refreshTable() {
        if (enableService) {
            ((ComponentDtoContainer) serviceTable.getContainerDataSource()).refresh();
        }
        ((InstanceDtoContainer) serverTable.getContainerDataSource()).refresh();
        if (enableLoadBalancer) {
            ((LoadBalancerDtoContainer) loadBalancerTable.getContainerDataSource()).refresh();
        }
        refreshTableSelectItem();
        if (enableService) {
            serviceDesc.initializeData();
        }
        serverDesc.initializeData();
        if (enableLoadBalancer) {
            loadBalancerDesc.initializeData();
        }
    }

    public void refreshTableSelectItem() {
        if (enableService) {
            serviceTable.setValue(null);
        }
        serverTable.setValue(null);
        if (enableLoadBalancer) {
            loadBalancerTable.setValue(null);
        }
    }

    public void refreshTableOnly() {
        if (enableService) {
            serviceTable.refreshData();
        }
        serverTable.refreshData();
        if (enableLoadBalancer) {
            loadBalancerTable.refreshData();
        }
    }

    private boolean needsRefresh() {
        MyCloudTabs myCloudTabs = ((AutoApplication) getApplication()).myCloud.myCloudTabs;

        // サービスの更新チェック
        Collection<ComponentDto> componentDtos = myCloudTabs.serviceTable.getItemIds();
        for (ComponentDto dto : componentDtos) {
            // サービスViewのサービス情報のサービスステータスが、Warninngの際、リフレッシュする対応
            for (ComponentInstanceDto componentInstance : dto.getComponentInstances()) {
                ComponentInstanceStatus componentInstanceStatus = ComponentInstanceStatus.fromStatus(componentInstance.getComponentInstance().getStatus());
                if (componentInstanceStatus == ComponentInstanceStatus.STARTING || componentInstanceStatus == ComponentInstanceStatus.STOPPING
                        || componentInstanceStatus == ComponentInstanceStatus.CONFIGURING) {
                    return true;
                }
            }
        }

        // サーバの更新チェック
        Collection<InstanceDto> instanceDtos = myCloudTabs.serverTable.getItemIds();
        for (InstanceDto dto : instanceDtos) {
            InstanceStatus status = InstanceStatus.fromStatus(dto.getInstance().getStatus());
            if (status == InstanceStatus.STARTING || status == InstanceStatus.STOPPING
                    || status == InstanceStatus.CONFIGURING) {
                return true;
            }
        }

        // ロードバランサの更新チェック
        Collection<LoadBalancerDto> loadBalancerDtos = myCloudTabs.loadBalancerTable.getItemIds();
        for (LoadBalancerDto dto : loadBalancerDtos) {
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(dto.getLoadBalancer().getStatus());
            if (status == LoadBalancerStatus.STARTING || status == LoadBalancerStatus.STOPPING
                    || status == LoadBalancerStatus.CONFIGURING) {
                return true;
            }

            for (LoadBalancerListener listener : dto.getLoadBalancerListeners()) {
                LoadBalancerListenerStatus status2 = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                if (status2 == LoadBalancerListenerStatus.STARTING || status2 == LoadBalancerListenerStatus.STOPPING
                        || status2 == LoadBalancerListenerStatus.CONFIGURING) {
                    return true;
                }
            }

            for (LoadBalancerInstance lbInstance : dto.getLoadBalancerInstances()) {
                LoadBalancerInstanceStatus status2 = LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus());
                if (status2 == LoadBalancerInstanceStatus.STARTING || status2 == LoadBalancerInstanceStatus.STOPPING
                        || status2 == LoadBalancerInstanceStatus.CONFIGURING) {
                    return true;
                }
            }
        }

        return false;
    }

    public void refreshDesc(ServerTable table) {
        //サーバTabが選択されていなければ、サーバ画面はリフレッシュしない
        if (tabDesc.getSelectedTab() == pnServer) {
            InstanceDto dto = (InstanceDto) table.getValue();
            Instance instance = dto != null ? dto.getInstance() : null;

            serverButtonsTop.hide();
            serverButtonsBottom.refresh(dto);
            if (dto != null) {
                table.setButtonStatus(instance);
                // サーバ情報の更新(選択されているTabだけ更新する)
                if ( serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescBasic ){
                    //基本情報の更新
                    serverDesc.serverDescBasic.left.setItem(dto);
                    serverDesc.serverDescBasic.right.refresh(
                            new ComponentDtoContainer(MyCloudTabs.this.getComponents(dto.getComponentInstances())));
                }else if ( serverDesc.tabDesc.getSelectedTab() == serverDesc.serverDescDetail ){
                    //詳細情報の更新
                    serverDesc.serverDescDetail.left.setServerName(instance);
                    serverDesc.serverDescDetail.right.setContainerDataSource(new InstanceParameterContainer(dto));
                    serverDesc.serverDescDetail.right.setHeaders();
                }
            } else {
                serverDesc.initializeData();
            }
        }
    }

    public void refreshDesc(ServiceTable table) {
        //サービスTabが選択されていなければ、サービス画面はリフレッシュしない
        if (tabDesc.getSelectedTab() == pnService) {
            ComponentDto dto = (ComponentDto) table.getValue();
            serviceButtonsTop.hide();
            serviceButtonsBottom.refresh(dto);
            if (dto != null) {
                Component component = dto.getComponent();
                table.setButtonStatus(dto);
                // サービス情報の更新(選択されているTabだけ更新する)
                if ( serviceDesc.tabDesc.getSelectedTab() == serviceDesc.serviceDescBasic ){
                    //基本情報の更新
                    serviceDesc.serviceDescBasic.left.setItem(dto);
                    serviceDesc.serviceDescBasic.right.refresh(new InstanceDtoContainer(MyCloudTabs.this
                            .getInstances(dto.getComponentInstances())));

                }else if ( serviceDesc.tabDesc.getSelectedTab() == serviceDesc.serviceDescDetail ){
                    //詳細情報の更新
                    serviceDesc.serviceDescDetail.left.setItem(dto);
                    Collection<InstanceDto> instanceDtos = (Collection<InstanceDto>) serverTable.getItemIds();
                    serviceDesc.serviceDescDetail.right.setContainerDataSource(new ComponentParameterContainer(dto,
                            instanceDtos));
                    serviceDesc.serviceDescDetail.right.setHeaders();
                }
            } else {
                serviceDesc.initializeData();
            }
        }
    }

    public void refreshDesc(LoadBalancerTable table) {
        //ロードバランサーTabが選択されていなければ、ロードバランサー画面はリフレッシュしない
        if (tabDesc.getSelectedTab() == pnLoadBalancer) {
            LoadBalancerDto dto = (LoadBalancerDto) table.getValue();
            table.sender.loadBalancerTableOpe.setButtonStatus(dto);
            // ロードバランサー情報の更新(選択されているTabだけ更新する)
            if ( loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerDesc.loadBalancerDescBasic ){
                //基本情報の更新
                loadBalancerDesc.loadBalancerDescBasic.basicInfo.setItem(dto);
                // ロードバランサー サービステーブル情報更新
                loadBalancerDesc.loadBalancerDescBasic.attachServiceTable.refresh(dto,false);

//            }else if ( loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerDesc.loadBalancerDescDetail ){
//                //詳細情報の更新

            }else if (loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerDesc.loadBalancerDescServer ){
                // 割り当てサービス 詳細情報更新
                loadBalancerDesc.loadBalancerDescServer.loadBalancerInfo.setItem(dto);
                // ロードバランサー サーバ テーブル情報更新
                loadBalancerDesc.loadBalancerDescServer.attachServiceServerTable.refresh(dto,false);
            }
        }
    }
    public void selectedTabChange(SelectedTabChangeEvent event) {
        //タブ切り替え時にリフレッシュする(選択されていないTabは画面が更新されていないため)
        if (tabDesc.getSelectedTab() == pnService) {
            refreshDesc(serviceTable);
        } else if (tabDesc.getSelectedTab() == pnServer) {
            refreshDesc(serverTable);
        } else if (tabDesc.getSelectedTab() == pnLoadBalancer){
            refreshDesc(loadBalancerTable);
        }
    }
}
