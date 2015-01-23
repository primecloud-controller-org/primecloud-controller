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

import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewProperties;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

/**
 * <p>
 * 各画面共通部分であるmyCloud名とリロードボタンを生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class MyCloud extends VerticalLayout {

    TextField lblMycloud;

    Button reloadb;

    MyCloudTabs myCloudTabs = new MyCloudTabs();

    public MyCloud() {
        setWidth("100%");
        setHeight("100%");
        addStyleName("mycloud-panel");

        VerticalLayout layout = this;
        layout.setMargin(false);
        layout.setSpacing(false);

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
                Component c = MyCloud.this.myCloudTabs.tabDesc.getSelectedTab();
                refresh();
                MyCloud.this.myCloudTabs.tabDesc.setSelectedTab(c);
            }
        });
        hlay.addComponent(reloadb);
        layout.addComponent(hlay);
        //myCloud本体の表示部分
        layout.addComponent(myCloudTabs);

        layout.setExpandRatio(myCloudTabs, 100);


    }

    public void hide() {
        myCloudTabs.hide();
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
            myCloudTabs.refreshTable();
            //myCloudTabs.refreshTableSelectItem();
            myCloudTabs.tabDesc.setEnabled(true);
            myCloudTabs.tabDesc.setSelectedTab(myCloudTabs.pnService);
            myCloudTabs.serverDesc.tabDesc.setSelectedTab(myCloudTabs.serverDesc.serverDescBasic);
            myCloudTabs.serviceDesc.tabDesc.setSelectedTab(myCloudTabs.serviceDesc.serviceDescBasic);
            myCloudTabs.loadBalancerDesc.tabDesc.setSelectedTab(myCloudTabs.loadBalancerDesc.loadBalancerDescBasic);

            reloadb.setEnabled(true);
        } else {
            lblMycloud.setReadOnly(false);
            lblMycloud.setValue("");
            lblMycloud.setReadOnly(true);
            myCloudTabs.refreshTable();
            //myCloudTabs.refreshTableSelectItem();
            myCloudTabs.tabDesc.setEnabled(false);
            reloadb.setEnabled(false);
        }
    }

}
