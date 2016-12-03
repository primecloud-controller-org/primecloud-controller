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

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.InstanceDto;
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
@SuppressWarnings({ "serial", "unchecked" })
public class MainView extends VerticalLayout {

    protected Log log = LogFactory.getLog(MainView.class);

    private boolean enableService;

    private boolean enableLoadBalancer;

    private TopBar topBar;

    private TextField myCloudField;

    private Button reloadButton;

    TabSheet tab;

    ServicePanel servicePanel;

    ServerPanel serverPanel;

    LoadBalancerPanel loadBalancerPanel;

    @Override
    public void attach() {
        // サービスを表示するかどうか
        String enableService = Config.getProperty("ui.enableService");
        this.enableService = (enableService == null) || (BooleanUtils.toBoolean(enableService));

        // ロードバランサを表示するかどうか
        String enableLoadBalancer = Config.getProperty("ui.enableLoadBalancer");
        this.enableLoadBalancer = (enableLoadBalancer == null) || (BooleanUtils.toBoolean(enableLoadBalancer));

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
        servicePanel = new ServicePanel(this);
        if (this.enableService) {
            tab.addTab(servicePanel, ViewProperties.getCaption("tab.service"), Icons.SERVICETAB.resource());
        }

        // サーバタブ
        serverPanel = new ServerPanel(this);
        tab.addTab(serverPanel, ViewProperties.getCaption("tab.server"), Icons.SERVERTAB.resource());

        // ロードバランサタブ
        loadBalancerPanel = new LoadBalancerPanel(this);
        if (this.enableLoadBalancer) {
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

    public void initialize() {
        if (enableService) {
            servicePanel.initialize();
        }
        serverPanel.initialize();
        if (enableLoadBalancer) {
            loadBalancerPanel.initialize();
        }
    }

    public void refreshTable() {
        if (enableService) {
            servicePanel.refreshTable();
        }
        serverPanel.refreshTable();
        if (enableLoadBalancer) {
            loadBalancerPanel.refreshTable();
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
        boolean needsRefresh = servicePanel.needsRefresh();
        if (needsRefresh) {
            return true;
        }

        needsRefresh = serverPanel.needsRefresh();
        if (needsRefresh) {
            return true;
        }

        needsRefresh = loadBalancerPanel.needsRefresh();
        if (needsRefresh) {
            return true;
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
