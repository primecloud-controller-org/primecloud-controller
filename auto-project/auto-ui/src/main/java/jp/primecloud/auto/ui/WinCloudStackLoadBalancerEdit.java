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

import java.util.List;

import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * ロードバランサの編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinCloudStackLoadBalancerEdit extends Window {

    private final String TAB_HEIGHT = "360px";

    private Long loadBalancerNo;

    private BasicTab basicTab;

    private LoadBalancerDto loadBalancer;

    private LoadBalancerPlatformDto platform;

    private List<ComponentDto> components;

    public WinCloudStackLoadBalancerEdit(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
    }

    @Override
    public void attach() {
        // モーダルウインドウ
        setIcon(Icons.EDITMINI.resource());
        setCaption(ViewProperties.getCaption("window.winLoadBalancerEdit"));
        setModal(true);
        setWidth("500px");
        //setHeight("500px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        // タブ設定
        TabSheet tab = new TabSheet();
        layout.addComponent(tab);

        // 基本情報タブ
        basicTab = new BasicTab();
        tab.addTab(basicTab, ViewProperties.getCaption("tab.basic"), Icons.BASIC.resource());

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // editボタン
        Button editButton = new Button();
        editButton.setCaption(ViewProperties.getCaption("button.editLoadBalancerService"));
        editButton.setDescription(ViewProperties.getCaption("description.editLoadBalancer"));
        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                editButtonClick(event);
            }
        });
        bottomLayout.addComponent(editButton);
        editButton.setClickShortcut(KeyCode.ENTER); // [Enter]でeditButtonクリック
        editButton.focus();

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinCloudStackLoadBalancerEdit.this.close();
            }
        });
        bottomLayout.addComponent(cancelButton);

        // 初期データの取得
        loadData();

        // データの表示
        basicTab.show(loadBalancer, platform, components);
    }

    private class BasicTab extends VerticalLayout {

        private final String SERVICE_CAPTION_ID = "ServiceName";

        private TextField loadBalancerNameField;

        private TextField commentField;

        private Label cloudLabel;

        private Label typeLabel;

        private ComboBox serviceSelect;

        private ComboBox algorithmSelect;

        private TextField publicPortField;

        private TextField privatePortField;

        @Override
        public void attach() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // フォーム
            Form form = new Form();
            form.setSizeFull();
            addComponent(form);

            // ロードバランサ名
            loadBalancerNameField = new TextField(ViewProperties.getCaption("field.loadBalancerName"));
            form.getLayout().addComponent(loadBalancerNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("95%");
            form.getLayout().addComponent(commentField);

            // プラットフォーム
            cloudLabel = new Label();
            cloudLabel.setCaption(ViewProperties.getCaption("field.cloud"));
            cloudLabel.addStyleName("icon-label");
            form.getLayout().addComponent(cloudLabel);

            // ロードバランサ種別
            typeLabel = new Label();
            typeLabel.setCaption(ViewProperties.getCaption("field.loadBalancerType"));
            typeLabel.addStyleName("icon-label");
            form.getLayout().addComponent(typeLabel);

            // 割り当てサービス
            serviceSelect = new ComboBox();
            serviceSelect.setCaption(ViewProperties.getCaption("field.loadBalancerService"));
            serviceSelect.setNullSelectionAllowed(false);
            serviceSelect.addContainerProperty(SERVICE_CAPTION_ID, String.class, null);
            serviceSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serviceSelect.setItemCaptionPropertyId(SERVICE_CAPTION_ID);
            form.getLayout().addComponent(serviceSelect);

            // アルゴリズム
            algorithmSelect = new ComboBox();
            algorithmSelect.setCaption(ViewProperties.getCaption("field.algorithm"));
            algorithmSelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(algorithmSelect);

            // パブリックポート
            publicPortField = new TextField(ViewProperties.getCaption("field.publicport"));
            publicPortField.setWidth("95%");
            form.getLayout().addComponent(publicPortField);

            // プライベートポート
            privatePortField = new TextField(ViewProperties.getCaption("field.privateport"));
            privatePortField.setWidth("95%");
            form.getLayout().addComponent(privatePortField);

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }

        public void show(LoadBalancerDto loadBalancer, LoadBalancerPlatformDto platform, List<ComponentDto> components) {
            // ロードバランサー名
            loadBalancerNameField.setReadOnly(false);
            loadBalancerNameField.setValue(loadBalancer.getLoadBalancer().getLoadBalancerName());
            loadBalancerNameField.setReadOnly(true);

            // コメントの設定
            String comment = loadBalancer.getLoadBalancer().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            // アルゴリズムの設定
            algorithmSelect.addItem("roundrobin");
            algorithmSelect.addItem("leastconn");
            if (StringUtils.isNotEmpty(loadBalancer.getCloudstackLoadBalancer().getAlgorithm())) {
                algorithmSelect.select(loadBalancer.getCloudstackLoadBalancer().getAlgorithm());
            }

            // パブリックポートの設定
            String publicport = loadBalancer.getCloudstackLoadBalancer().getPublicport();
            if (publicport != null) {
                publicPortField.setValue(publicport);
            }

            // プライベートポートの設定
            String privateport = loadBalancer.getCloudstackLoadBalancer().getPrivateport();
            if (comment != null) {
                privatePortField.setValue(privateport);
            }

            // プラットフォーム
            Icons icon = IconUtils.getPlatformIcon(platform);
            String description = platform.getPlatform().getPlatformNameDisp();
            String cloudValue = IconUtils.createImageTag(getApplication(), icon, description);
            cloudLabel.setValue(cloudValue);
            cloudLabel.setContentMode(Label.CONTENT_XHTML);

            // ロードバランサ種別
            String type = loadBalancer.getLoadBalancer().getType();
            Icons typeIcon = Icons.NONE;
            String typeString = ViewProperties.getLoadBalancerType(type);
            String typeValue = IconUtils.createImageTag(getApplication(), typeIcon, typeString);
            typeLabel.setValue(typeValue);
            typeLabel.setContentMode(Label.CONTENT_XHTML);

            // 割り当てサービス選択
            for (ComponentDto component : components) {
                Item item = serviceSelect.addItem(component.getComponent().getComponentNo());
                item.getItemProperty(SERVICE_CAPTION_ID).setValue(component.getComponent().getComponentName());
            }
            serviceSelect.select(loadBalancer.getLoadBalancer().getComponentNo());

            // リスナーが存在する場合は選択不可にする
            if (!loadBalancer.getLoadBalancerListeners().isEmpty()) {
                serviceSelect.setEnabled(false);
            }
        }

    }

    private void loadData() {
        // ロードバランサ情報を取得
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        List<LoadBalancerDto> loadBalancers = loadBalancerService.getLoadBalancers(ViewContext.getFarmNo());
        for (LoadBalancerDto loadBalancer : loadBalancers) {
            if (loadBalancerNo.equals(loadBalancer.getLoadBalancer().getLoadBalancerNo())) {
                this.loadBalancer = loadBalancer;
                break;
            }
        }

        // プラットフォーム情報を取得
        Long platformNo = loadBalancer.getLoadBalancer().getPlatformNo();
        List<LoadBalancerPlatformDto> platforms = loadBalancerService.getPlatforms(ViewContext.getUserNo());
        for (LoadBalancerPlatformDto platform : platforms) {
            if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                this.platform = platform;
                break;
            }
        }

        // コンポーネント情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        components = componentService.getComponents(ViewContext.getFarmNo());
    }

    private void editButtonClick(ClickEvent event) {
        // 入力チェック
        basicTab.commentField.validate();
        basicTab.algorithmSelect.validate();
        basicTab.publicPortField.validate();
        basicTab.privatePortField.validate();

        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);

        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        Long componentNo = (Long) basicTab.serviceSelect.getValue();
        String algorithm = (String) basicTab.algorithmSelect.getValue();
        String publicPort = (String) basicTab.publicPortField.getValue();
        String privatePort = (String) basicTab.privatePortField.getValue();

        // ロードバランサを変更
        String loadBalancerName = loadBalancer.getLoadBalancer().getLoadBalancerName();
        loadBalancerService.updateCloudstackLoadBalancer(loadBalancerNo, loadBalancerName, comment, componentNo,
                algorithm, publicPort, privatePort);

        // 画面を閉じる
        close();
    }

}
