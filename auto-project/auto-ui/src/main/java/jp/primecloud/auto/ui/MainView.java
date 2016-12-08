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
import java.util.List;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vaadin.henrik.refresher.Refresher;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * メイン画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class MainView extends VerticalLayout {

    protected Log log = LogFactory.getLog(MainView.class);

    private TopBar topBar;

    private TextField myCloudField;

    private Button reloadButton;

    TabSheet tab;

    ServicePanel servicePanel;

    ServerPanel serverPanel;

    LoadBalancerPanel loadBalancerPanel;

    @Override
    public void attach() {
        setSizeFull();
        addStyleName("mycloud-panel");
        setMargin(false);
        setSpacing(false);

        // 画面上部のバー
        topBar = new TopBar(this);
        addComponent(topBar);

        // タブの上のバー
        CssLayout topLayout = new CssLayout();
        topLayout.setWidth("100%");
        topLayout.setHeight("28px");
        topLayout.addStyleName("mycloud-name");
        topLayout.setMargin(true);
        addComponent(topLayout);

        // myCloud名
        myCloudField = new TextField();
        myCloudField.setWidth("80%");
        myCloudField.addStyleName("mycloud-label");
        myCloudField.setEnabled(false);
        myCloudField.setReadOnly(true);
        topLayout.addComponent(myCloudField);

        // Reloadボタン
        reloadButton = new Button(ViewProperties.getCaption("button.reload"));
        reloadButton.setDescription(ViewProperties.getCaption("description.reload"));
        reloadButton.addStyleName("sync-button");
        reloadButton.addStyleName("borderless");
        reloadButton.setIcon(Icons.SYNC.resource());
        reloadButton.setEnabled(false);
        reloadButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                refresh();
            }
        });
        topLayout.addComponent(reloadButton);

        // myCloud本体
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);
        mainLayout.setMargin(false);

        Panel mainPanel = new Panel(mainLayout);
        mainPanel.setSizeFull();
        mainPanel.addStyleName(Reindeer.PANEL_LIGHT);
        addComponent(mainPanel);
        setExpandRatio(mainPanel, 100);

        // タブ
        tab = new TabSheet();
        tab.setSizeFull();
        tab.setEnabled(false);
        tab.addStyleName(Reindeer.TABSHEET_BORDERLESS);
        mainLayout.addComponent(tab);
        mainLayout.setExpandRatio(tab, 100);

        // サービスタブ
        String enableService = Config.getProperty("ui.enableService");
        if (enableService == null || BooleanUtils.toBoolean(enableService)) {
            servicePanel = new ServicePanel(this);
            tab.addTab(servicePanel, ViewProperties.getCaption("tab.service"), Icons.SERVICETAB.resource());
        }

        // サーバタブ
        serverPanel = new ServerPanel(this);
        tab.addTab(serverPanel, ViewProperties.getCaption("tab.server"), Icons.SERVERTAB.resource());

        // ロードバランサタブ
        String enableLoadBalancer = Config.getProperty("ui.enableLoadBalancer");
        if (enableLoadBalancer == null || BooleanUtils.toBoolean(enableLoadBalancer)) {
            loadBalancerPanel = new LoadBalancerPanel(this);
            tab.addTab(loadBalancerPanel, ViewProperties.getCaption("tab.loadbalancer"),
                    Icons.LOADBALANCER_TAB.resource());
        }

        // 選択されたタブが変更された場合
        tab.addListener(new SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                MainView.this.selectedTabChange(event);
            }
        });

        // 自動更新用のタイマー
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
        mainLayout.addComponent(timer);
    }

    public void loginSuccess() {
        topBar.loginSuccess();
    }

    public void refresh() {
        // myCloud情報を取得
        FarmDto dto = null;
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            FarmService farmService = BeanContext.getBean(FarmService.class);
            dto = farmService.getFarm(farmNo);
        }

        if (dto != null) {
            String mycloud = dto.getFarm().getFarmName() + " [ " + dto.getFarm().getDomainName() + " ]";
            if (StringUtils.isNotEmpty(dto.getFarm().getComment())) {
                mycloud = mycloud + " - " + dto.getFarm().getComment();
            }
            myCloudField.setReadOnly(false);
            myCloudField.setValue(mycloud);
            myCloudField.setReadOnly(true);
            refreshTable();
            tab.setEnabled(true);
            reloadButton.setEnabled(true);
        } else {
            myCloudField.setReadOnly(false);
            myCloudField.setValue("");
            myCloudField.setReadOnly(true);
            refreshTable();
            tab.setEnabled(false);
            reloadButton.setEnabled(false);
        }
    }

    public ComponentDto getComponent(Long componentNo) {
        if (servicePanel == null) {
            return null;
        }

        for (ComponentDto component : (Collection<ComponentDto>) servicePanel.serviceTable.getItemIds()) {
            if (componentNo.equals(component.getComponent().getComponentNo())) {
                return component;
            }
        }

        return null;
    }

    public List<ComponentDto> getComponents(List<ComponentInstanceDto> componentInstances) {
        List<ComponentDto> components = new ArrayList<ComponentDto>();
        if (servicePanel == null) {
            return components;
        }

        for (ComponentDto component : (Collection<ComponentDto>) servicePanel.serviceTable.getItemIds()) {
            for (ComponentInstanceDto componentInstance : componentInstances) {
                if (componentInstance.getComponentInstance().getComponentNo()
                        .equals(component.getComponent().getComponentNo())) {
                    if (!components.contains(component)) {
                        components.add(component);
                    }
                    break;
                }
            }
        }

        return components;
    }

    public List<InstanceDto> getInstances() {
        List<InstanceDto> instances = new ArrayList<InstanceDto>();

        instances.addAll((Collection<InstanceDto>) serverPanel.serverTable.getItemIds());

        return instances;
    }

    public List<InstanceDto> getInstances(List<ComponentInstanceDto> componentInstances) {
        List<InstanceDto> instances = new ArrayList<InstanceDto>();

        for (InstanceDto instance : (Collection<InstanceDto>) serverPanel.serverTable.getItemIds()) {
            for (ComponentInstanceDto componentInstance : componentInstances) {
                if (componentInstance.getComponentInstance().getInstanceNo()
                        .equals(instance.getInstance().getInstanceNo())) {
                    if (!instances.contains(instance)) {
                        instances.add(instance);
                    }
                    break;
                }
            }
        }

        return instances;
    }

    public List<LoadBalancerDto> getLoadBalancers(Long componentNo) {
        List<LoadBalancerDto> loadBalancers = new ArrayList<LoadBalancerDto>();
        if (loadBalancerPanel == null) {
            return loadBalancers;
        }

        for (LoadBalancerDto loadBalancer : (Collection<LoadBalancerDto>) loadBalancerPanel.loadBalancerTable
                .getItemIds()) {
            if (componentNo.equals(loadBalancer.getLoadBalancer().getComponentNo())) {
                loadBalancers.add(loadBalancer);
            }
        }

        return loadBalancers;
    }

    public void initialize() {
        if (servicePanel != null) {
            servicePanel.initialize();
        }
        serverPanel.initialize();
        if (loadBalancerPanel != null) {
            loadBalancerPanel.initialize();
        }
    }

    public void refreshTable() {
        if (servicePanel != null) {
            servicePanel.refreshTable();
        }
        serverPanel.refreshTable();
        if (loadBalancerPanel != null) {
            loadBalancerPanel.refreshTable();
        }
    }

    public void refreshTableOnly() {
        if (servicePanel != null) {
            servicePanel.serviceTable.refreshData();
        }
        serverPanel.serverTable.refreshData();
        if (loadBalancerPanel != null) {
            loadBalancerPanel.loadBalancerTable.refreshData();
        }
    }

    private boolean needsRefresh() {
        if (servicePanel != null) {
            if (servicePanel.needsRefresh()) {
                return true;
            }
        }

        if (serverPanel.needsRefresh()) {
            return true;
        }

        if (loadBalancerPanel != null) {
            if (loadBalancerPanel.needsRefresh()) {
                return true;
            }
        }

        return false;
    }

    private void selectedTabChange(SelectedTabChangeEvent event) {
        if (tab.getSelectedTab() == servicePanel) {
            servicePanel.refreshDesc();
        } else if (tab.getSelectedTab() == serverPanel) {
            serverPanel.refreshDesc();
        } else if (tab.getSelectedTab() == loadBalancerPanel) {
            loadBalancerPanel.refreshDesc();
        }
    }

}
