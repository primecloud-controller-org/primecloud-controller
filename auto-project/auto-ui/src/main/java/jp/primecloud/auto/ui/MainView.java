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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vaadin.henrik.refresher.Refresher;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * メイン画面を生成します。
 * </p>
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class MainView extends VerticalLayout {

    protected Log log = LogFactory.getLog(MainView.class);

    private boolean enableService;

    private boolean enableLoadBalancer;

    private TopBar topBar;

    private TextField lblMycloud;

    private Button reloadb;

    TabSheet tab = new TabSheet();

    ServicePanel servicePanel;

    ServerPanel serverPanel;

    LoadBalancerPanel loadBalancerPanel;

    MainView() {
        // サービスを有効にするかどうか
        String enableService = Config.getProperty("ui.enableService");
        this.enableService = (enableService == null) || (BooleanUtils.toBoolean(enableService));

        // ロードバランサの有効/無効を判定
        String enableLoadBalancer = Config.getProperty("ui.enableLoadBalancer");
        this.enableLoadBalancer = (enableLoadBalancer == null) || (BooleanUtils.toBoolean(enableLoadBalancer));

        setWidth("100%");
        setHeight("100%");
        addStyleName("mycloud-panel");

        setMargin(false);
        setSpacing(false);

        // 画面上部
        topBar = new TopBar(this);
        addComponent(topBar);

        CssLayout hlay = new CssLayout();
        hlay.setWidth("100%");
        hlay.setHeight("28px");
        hlay.addStyleName("mycloud-name");
        hlay.setMargin(true);

        //クラウド名ラベル
        lblMycloud = new TextField();
        lblMycloud.setWidth("80%");
        lblMycloud.addStyleName("mycloud-label");
        lblMycloud.setEnabled(false);
        lblMycloud.setReadOnly(true);
        hlay.addComponent(lblMycloud);

        //リロードボタン
        reloadb = new Button(ViewProperties.getCaption("button.reload"));
        reloadb.setDescription(ViewProperties.getCaption("description.reload"));
        reloadb.addStyleName("sync-button");
        reloadb.addStyleName("borderless");
        reloadb.setIcon(Icons.SYNC.resource());
        reloadb.setEnabled(false);
        reloadb.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // リロード前のタブを保持
                Component c = tab.getSelectedTab();
                refresh();
                tab.setSelectedTab(c);
            }
        });
        hlay.addComponent(reloadb);
        addComponent(hlay);

        Panel panel = new Panel();
        panel.setSizeFull();

        // 最初はdisableに設定しておく
        tab.setSizeFull();
        tab.setEnabled(false);
        tab.addStyleName(Reindeer.TABSHEET_BORDERLESS);

        panel.addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) panel.getContent();
        layout.setSizeFull();
        layout.setSpacing(false);
        layout.setMargin(false);

        //サービス用タブ設定
        servicePanel = new ServicePanel(this);
        if (this.enableService) {
            tab.addTab(servicePanel, ViewProperties.getCaption("tab.service"), Icons.SERVICETAB.resource());
        }

        //サーバ用タブ
        serverPanel = new ServerPanel(this);
        tab.addTab(serverPanel, ViewProperties.getCaption("tab.server"), Icons.SERVERTAB.resource());

        //ロードバランサー用タブ
        loadBalancerPanel = new LoadBalancerPanel(this);
        if (this.enableLoadBalancer) {
            tab.addTab(loadBalancerPanel, ViewProperties.getCaption("tab.loadbalancer"),
                    Icons.LOADBALANCER_TAB.resource());
        }

        //タブ用リスナー
        tab.addListener(TabSheet.SelectedTabChangeEvent.class, this, "selectedTabChange");
        layout.addComponent(tab);

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
        layout.setExpandRatio(tab, 100);

        //myCloud本体の表示部分
        addComponent(panel);
        setExpandRatio(panel, 100);
    }

    public void loginSuccess() {
        // ログインユーザ名表示
        topBar.showUserName(ViewContext.getUsername());

        // myCloud一件もない場合は、ダイアログを表示する。
        FarmService farmService = BeanContext.getBean(FarmService.class);
        List<FarmDto> farms = farmService.getFarms(ViewContext.getUserNo(), ViewContext.getLoginUser());
        if (farms.size() < 1) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"),
                    ViewMessages.getMessage("IUI-000038"));
            dialog.setCallback(new Callback() {
                @Override
                public void onDialogResult(Result result) {
                    // myCloud管理画面を表示
                    topBar.showCloudEditWindow();
                }
            });

            getApplication().getMainWindow().addWindow(dialog);
        } else {
            // myCloud管理画面を表示
            topBar.showCloudEditWindow();
        }
    }

    public void refresh() {
        FarmDto dto = null;
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            FarmService farmService = BeanContext.getBean(FarmService.class);
            dto = farmService.getFarm(farmNo);
        }

        if (dto != null) {
            Farm farm = dto.getFarm();
            String mycloud = farm.getFarmName() + " [ " + farm.getDomainName() + " ] - " + farm.getComment();
            lblMycloud.setReadOnly(false);
            lblMycloud.setValue(mycloud);
            lblMycloud.setReadOnly(true);
            refreshTable();
            //refreshTableSelectItem();
            tab.setEnabled(true);
            tab.setSelectedTab(servicePanel);
            serverPanel.serverDesc.tabDesc.setSelectedTab(serverPanel.serverDesc.serverDescBasic);
            servicePanel.serviceDesc.tabDesc.setSelectedTab(servicePanel.serviceDesc.serviceDescBasic);
            loadBalancerPanel.loadBalancerDesc.tabDesc
                    .setSelectedTab(loadBalancerPanel.loadBalancerDesc.loadBalancerDescBasic);

            reloadb.setEnabled(true);
        } else {
            lblMycloud.setReadOnly(false);
            lblMycloud.setValue("");
            lblMycloud.setReadOnly(true);
            refreshTable();
            //refreshTableSelectItem();
            tab.setEnabled(false);
            reloadb.setEnabled(false);
        }
    }

    public void tableRowSelected(Table.ValueChangeEvent event) {
        synchronized (getApplication()) {
            //選択行のボタンを有効にする
            Property p = event.getProperty();
            if (p instanceof ServiceTable) {
                ServiceTable table = (ServiceTable) p;
                ComponentDto dto = (ComponentDto) table.getValue();
                servicePanel.serviceButtonsBottom.refresh(dto);

                if (dto != null) {
                    table.setButtonStatus(dto);
                    // サービス情報の更新(選択されているTabだけ更新する)
                    if (servicePanel.serviceDesc.tabDesc.getSelectedTab() == servicePanel.serviceDesc.serviceDescBasic) {
                        //基本情報の更新
                        servicePanel.serviceDesc.serviceDescBasic.left.setItem(dto);
                        servicePanel.serviceDesc.serviceDescBasic.right
                                .refresh(
                                        new InstanceDtoContainer(
                                                MainView.this.getInstances(dto.getComponentInstances())), true);
                    } else if (servicePanel.serviceDesc.tabDesc.getSelectedTab() == servicePanel.serviceDesc.serviceDescDetail) {
                        //詳細情報の更新
                        servicePanel.serviceDesc.serviceDescDetail.left.setItem(dto);
                        Collection<InstanceDto> instanceDtos = (Collection<InstanceDto>) serverPanel.serverTable
                                .getItemIds();
                        servicePanel.serviceDesc.serviceDescDetail.right
                                .setContainerDataSource(new ComponentParameterContainer(dto, instanceDtos));
                        servicePanel.serviceDesc.serviceDescDetail.right.setHeaders();
                    }
                }
            } else if (p instanceof ServerTable) {
                ServerTable table = (ServerTable) p;
                InstanceDto dto = (InstanceDto) table.getValue();
                Instance instance = dto != null ? dto.getInstance() : null;

                serverPanel.serverButtonsBottom.refresh(dto);
                if (dto != null) {
                    table.setButtonStatus(dto.getInstance());
                    // サーバ情報の更新(選択されているTabだけ更新する)
                    if (serverPanel.serverDesc.tabDesc.getSelectedTab() == serverPanel.serverDesc.serverDescBasic) {
                        //基本情報の更新
                        serverPanel.serverDesc.serverDescBasic.left.setItem(dto);
                        serverPanel.serverDesc.serverDescBasic.right.refresh(new ComponentDtoContainer(MainView.this
                                .getComponents(dto.getComponentInstances())));
                    } else if (serverPanel.serverDesc.tabDesc.getSelectedTab() == serverPanel.serverDesc.serverDescDetail) {
                        //詳細情報の更新
                        serverPanel.serverDesc.serverDescDetail.left.setServerName(instance);
                        serverPanel.serverDesc.serverDescDetail.right
                                .setContainerDataSource(new InstanceParameterContainer(dto));
                        serverPanel.serverDesc.serverDescDetail.right.setHeaders();
                    }
                }
            } else if (p instanceof LoadBalancerTable) {
                LoadBalancerTable table = (LoadBalancerTable) p;
                LoadBalancerDto dto = (LoadBalancerDto) table.getValue();
                loadBalancerPanel.loadBalancerTableOpe.setButtonStatus(dto);
                // ロードバランサー情報の更新(選択されているTabだけ更新する)
                if (loadBalancerPanel.loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerPanel.loadBalancerDesc.loadBalancerDescBasic) {
                    //基本情報の更新
                    loadBalancerPanel.loadBalancerDesc.loadBalancerDescBasic.basicInfo.setItem(dto);
                    // ロードバランサー サービス テーブル情報更新
                    loadBalancerPanel.loadBalancerDesc.loadBalancerDescBasic.attachServiceTable.refresh(dto, true);
                } else if (loadBalancerPanel.loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerPanel.loadBalancerDesc.loadBalancerDescServer) {
                    // 割り当てサービス 詳細情報更新
                    loadBalancerPanel.loadBalancerDesc.loadBalancerDescServer.loadBalancerInfo.setItem(dto);
                    // ロードバランサー サーバ テーブル情報更新
                    loadBalancerPanel.loadBalancerDesc.loadBalancerDescServer.attachServiceServerTable.refresh(dto,
                            true);
                }
            }
        }
    }

    public Collection<ComponentDto> getComponents(List<ComponentInstanceDto> componentInstances) {
        Collection<ComponentDto> dtos = (Collection<ComponentDto>) servicePanel.serviceTable.getItemIds();
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
        Collection<InstanceDto> dtos = (Collection<InstanceDto>) serverPanel.serverTable.getItemIds();
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
        if (tab.getSelectedTab() == servicePanel) {
            servicePanel.serviceButtonsTop.hide();
            servicePanel.serviceButtonsBottom.hide();
        } else if (tab.getSelectedTab() == serverPanel) {
            serverPanel.serverButtonsTop.hide();
            serverPanel.serverButtonsBottom.hide();
        } else if (tab.getSelectedTab() == loadBalancerPanel) {
            loadBalancerPanel.loadBalancerTableOpe.hide();
        }
    }

    public void refreshTable() {
        if (enableService) {
            ((ComponentDtoContainer) servicePanel.serviceTable.getContainerDataSource()).refresh();
        }
        ((InstanceDtoContainer) serverPanel.serverTable.getContainerDataSource()).refresh();
        if (enableLoadBalancer) {
            ((LoadBalancerDtoContainer) loadBalancerPanel.loadBalancerTable.getContainerDataSource()).refresh();
        }
        refreshTableSelectItem();
        if (enableService) {
            servicePanel.serviceDesc.initializeData();
        }
        serverPanel.serverDesc.initializeData();
        if (enableLoadBalancer) {
            loadBalancerPanel.loadBalancerDesc.initializeData();
        }
    }

    public void refreshTableSelectItem() {
        if (enableService) {
            servicePanel.serviceTable.setValue(null);
        }
        serverPanel.serverTable.setValue(null);
        if (enableLoadBalancer) {
            loadBalancerPanel.loadBalancerTable.setValue(null);
        }
    }

    public void refreshTableOnly() {
        if (enableService) {
            servicePanel.serviceTable.refreshData();
        }
        serverPanel.serverTable.refreshData();
        if (enableLoadBalancer) {
            loadBalancerPanel.loadBalancerTable.refreshData();
        }
    }

    private boolean needsRefresh() {
        // サービスの更新チェック
        Collection<ComponentDto> componentDtos = servicePanel.serviceTable.getItemIds();
        for (ComponentDto dto : componentDtos) {
            // サービスViewのサービス情報のサービスステータスが、Warninngの際、リフレッシュする対応
            for (ComponentInstanceDto componentInstance : dto.getComponentInstances()) {
                ComponentInstanceStatus componentInstanceStatus = ComponentInstanceStatus.fromStatus(componentInstance
                        .getComponentInstance().getStatus());
                if (componentInstanceStatus == ComponentInstanceStatus.STARTING
                        || componentInstanceStatus == ComponentInstanceStatus.STOPPING
                        || componentInstanceStatus == ComponentInstanceStatus.CONFIGURING) {
                    return true;
                }
            }
        }

        // サーバの更新チェック
        Collection<InstanceDto> instanceDtos = serverPanel.serverTable.getItemIds();
        for (InstanceDto dto : instanceDtos) {
            InstanceStatus status = InstanceStatus.fromStatus(dto.getInstance().getStatus());
            if (status == InstanceStatus.STARTING || status == InstanceStatus.STOPPING
                    || status == InstanceStatus.CONFIGURING) {
                return true;
            }
        }

        // ロードバランサの更新チェック
        Collection<LoadBalancerDto> loadBalancerDtos = loadBalancerPanel.loadBalancerTable.getItemIds();
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
        if (tab.getSelectedTab() == serverPanel) {
            InstanceDto dto = (InstanceDto) table.getValue();
            Instance instance = dto != null ? dto.getInstance() : null;

            serverPanel.serverButtonsTop.hide();
            serverPanel.serverButtonsBottom.refresh(dto);
            if (dto != null) {
                table.setButtonStatus(instance);
                // サーバ情報の更新(選択されているTabだけ更新する)
                if (serverPanel.serverDesc.tabDesc.getSelectedTab() == serverPanel.serverDesc.serverDescBasic) {
                    //基本情報の更新
                    serverPanel.serverDesc.serverDescBasic.left.setItem(dto);
                    serverPanel.serverDesc.serverDescBasic.right.refresh(new ComponentDtoContainer(MainView.this
                            .getComponents(dto.getComponentInstances())));
                } else if (serverPanel.serverDesc.tabDesc.getSelectedTab() == serverPanel.serverDesc.serverDescDetail) {
                    //詳細情報の更新
                    serverPanel.serverDesc.serverDescDetail.left.setServerName(instance);
                    serverPanel.serverDesc.serverDescDetail.right
                            .setContainerDataSource(new InstanceParameterContainer(dto));
                    serverPanel.serverDesc.serverDescDetail.right.setHeaders();
                }
            } else {
                serverPanel.serverDesc.initializeData();
            }
        }
    }

    public void refreshDesc(ServiceTable table) {
        //サービスTabが選択されていなければ、サービス画面はリフレッシュしない
        if (tab.getSelectedTab() == servicePanel) {
            ComponentDto dto = (ComponentDto) table.getValue();
            servicePanel.serviceButtonsTop.hide();
            servicePanel.serviceButtonsBottom.refresh(dto);
            if (dto != null) {
                table.setButtonStatus(dto);
                // サービス情報の更新(選択されているTabだけ更新する)
                if (servicePanel.serviceDesc.tabDesc.getSelectedTab() == servicePanel.serviceDesc.serviceDescBasic) {
                    //基本情報の更新
                    servicePanel.serviceDesc.serviceDescBasic.left.setItem(dto);
                    servicePanel.serviceDesc.serviceDescBasic.right.refresh(new InstanceDtoContainer(MainView.this
                            .getInstances(dto.getComponentInstances())));

                } else if (servicePanel.serviceDesc.tabDesc.getSelectedTab() == servicePanel.serviceDesc.serviceDescDetail) {
                    //詳細情報の更新
                    servicePanel.serviceDesc.serviceDescDetail.left.setItem(dto);
                    Collection<InstanceDto> instanceDtos = (Collection<InstanceDto>) serverPanel.serverTable
                            .getItemIds();
                    servicePanel.serviceDesc.serviceDescDetail.right
                            .setContainerDataSource(new ComponentParameterContainer(dto, instanceDtos));
                    servicePanel.serviceDesc.serviceDescDetail.right.setHeaders();
                }
            } else {
                servicePanel.serviceDesc.initializeData();
            }
        }
    }

    public void refreshDesc(LoadBalancerTable table) {
        //ロードバランサーTabが選択されていなければ、ロードバランサー画面はリフレッシュしない
        if (tab.getSelectedTab() == loadBalancerPanel) {
            LoadBalancerDto dto = (LoadBalancerDto) table.getValue();
            loadBalancerPanel.loadBalancerTableOpe.setButtonStatus(dto);
            // ロードバランサー情報の更新(選択されているTabだけ更新する)
            if (loadBalancerPanel.loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerPanel.loadBalancerDesc.loadBalancerDescBasic) {
                //基本情報の更新
                loadBalancerPanel.loadBalancerDesc.loadBalancerDescBasic.basicInfo.setItem(dto);
                // ロードバランサー サービステーブル情報更新
                loadBalancerPanel.loadBalancerDesc.loadBalancerDescBasic.attachServiceTable.refresh(dto, false);
            } else if (loadBalancerPanel.loadBalancerDesc.tabDesc.getSelectedTab() == loadBalancerPanel.loadBalancerDesc.loadBalancerDescServer) {
                // 割り当てサービス 詳細情報更新
                loadBalancerPanel.loadBalancerDesc.loadBalancerDescServer.loadBalancerInfo.setItem(dto);
                // ロードバランサー サーバ テーブル情報更新
                loadBalancerPanel.loadBalancerDesc.loadBalancerDescServer.attachServiceServerTable.refresh(dto, false);
            }
        }
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        //タブ切り替え時にリフレッシュする(選択されていないTabは画面が更新されていないため)
        if (tab.getSelectedTab() == servicePanel) {
            refreshDesc(servicePanel.serviceTable);
        } else if (tab.getSelectedTab() == serverPanel) {
            refreshDesc(serverPanel.serverTable);
        } else if (tab.getSelectedTab() == loadBalancerPanel) {
            refreshDesc(loadBalancerPanel.loadBalancerTable);
        }
    }

}
