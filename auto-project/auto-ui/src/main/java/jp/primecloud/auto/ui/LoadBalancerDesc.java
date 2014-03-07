package jp.primecloud.auto.ui;

import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * ロードバランサ画面下部のタイトルとタブを生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerDesc extends Panel {

    TabSheet tabDesc = new TabSheet();

    LoadBalancerDescBasic loadBalancerDescBasic = new LoadBalancerDescBasic();

//    LoadBalancerDescDetail loadBalancerDescDetail = new LoadBalancerDescDetail();

    LoadBalancerDescServer loadBalancerDescServer = new LoadBalancerDescServer();

    public LoadBalancerDesc() {
        setWidth("100%");
        setHeight("100%");
        setCaption(ViewProperties.getCaption("panel.loadBalancerDesc"));
        addStyleName("loadbalancer-desc-panel");
        addStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.addStyleName("loadbalancer-desc-layout");
        tabDesc.addStyleName(Reindeer.TABSHEET_BORDERLESS);
        tabDesc.setWidth("100%");
        tabDesc.setHeight("100%");

        tabDesc.addTab(loadBalancerDescBasic,  ViewProperties.getCaption("tab.loadBalancerDescBasic"), Icons.BASIC.resource());
//        tabDesc.addTab(loadBalancerDescDetail, ViewProperties.getCaption("tab.loadBalancerDescDetail"), Icons.DETAIL.resource());
        tabDesc.addTab(loadBalancerDescServer, ViewProperties.getCaption("tab.loadBalancerDescServer"), Icons.DETAIL.resource());

        //タブ用リスナー
        tabDesc.addListener(TabSheet.SelectedTabChangeEvent.class, this, "selectedTabChange");
        addComponent(tabDesc);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        AutoApplication ap = (AutoApplication)getApplication();
        LoadBalancerTable tbl = (LoadBalancerTable) ap.myCloud.myCloudTabs.loadBalancerTable;
        ap.myCloud.myCloudTabs.refreshDesc(tbl);
    }

    public void initializeData() {
        loadBalancerDescBasic.initializeData();
//        loadBalancerDescDetail.initializeData();
        loadBalancerDescServer.initializeData();
    }

}
