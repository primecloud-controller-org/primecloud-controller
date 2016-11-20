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

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.SslKeyDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.ui.validator.IntegerRangeValidator;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * ロードバランサ－リスナーの追加・編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinLoadBalancerConfigListener extends Window {

    private final String TEXT_WIDTH = "120px";

    private final String SSLKEY_CAPTION_ID = "SSLKeyName";

    private Long loadBalancerNo;

    private Integer loadBalancerPort;

    private BasicForm basicForm;

    private LoadBalancerDto loadBalancer;

    private ComponentDto component;

    private List<SslKeyDto> sslKeys;

    public WinLoadBalancerConfigListener(Long loadBalancerNo, Integer loadBalancerPort) {
        this.loadBalancerNo = loadBalancerNo;
        this.loadBalancerPort = loadBalancerPort;
    }

    @Override
    public void attach() {
        // モーダルウインドウ
        setModal(true);
        setWidth("480px");
        //setHeight("500px");
        if (isAddMode()) {
            setIcon(Icons.ADD.resource());
            setCaption(ViewProperties.getCaption("window.addLoadBalancerListener"));
        } else {
            setIcon(Icons.EDITMINI.resource());
            setCaption(ViewProperties.getCaption("window.editLoadBalancerListener"));
        }

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(false);

        // フォーム
        basicForm = new BasicForm();
        layout.addComponent(basicForm);

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, true, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button();
        if (isAddMode()) {
            addButton.setCaption(ViewProperties.getCaption("button.addLoadBalancerListener"));
            addButton.setDescription(ViewProperties.getCaption("description.addLoadBalancerListener"));
        } else {
            addButton.setCaption(ViewProperties.getCaption("button.ok"));
            addButton.setDescription(ViewProperties.getCaption("description.editLoadBalancerListener"));
        }
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addButtonClick(event);
            }
        });
        addButton.setClickShortcut(KeyCode.ENTER); // [Enter]でaddButtonクリック
        addButton.focus();
        bottomLayout.addComponent(addButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        bottomLayout.addComponent(cancelButton);

        // ロードバランサ情報の表示
        loadData();
        basicForm.show(loadBalancer, loadBalancerPort, component, sslKeys);
    }

    private class BasicForm extends Form {

        private Form subForm;

        private TextField nameField;

        private TextField serviceField;

        private TextField loadBalancerPortField;

        private TextField servicePortField;

        private ComboBox protocolSelect;

        private ComboBox sslKeySelect;

        @Override
        public void attach() {
            // メインフォーム
            Form mainForm = new Form();
            Layout mainLayout = mainForm.getLayout();
            addComponent(mainForm);

            // ロードバランサ名
            nameField = new TextField(ViewProperties.getCaption("field.loadBalancerName"));
            nameField.setReadOnly(true);
            mainLayout.addComponent(nameField);

            // サービス名
            serviceField = new TextField(ViewProperties.getCaption("field.loadBalancerService"));
            serviceField.setReadOnly(true);
            mainLayout.addComponent(serviceField);

            // ロードバランサ設定パネル
            Panel panel = new Panel(ViewProperties.getCaption("field.loadBalancerConfig"));
            ((Layout) panel.getContent()).setMargin(false, false, false, true);
            mainLayout.addComponent(panel);

            // サブフォーム
            subForm = new Form();
            FormLayout sublayout = (FormLayout) this.subForm.getLayout();
            sublayout.setMargin(false);
            sublayout.setSpacing(false);
            panel.getContent().addComponent(subForm);
            subForm.setHeight("200px");

            // ロードバランサポート
            loadBalancerPortField = new TextField(ViewProperties.getCaption("field.loadBalancerPort"));
            loadBalancerPortField.setWidth(TEXT_WIDTH);
            sublayout.addComponent(loadBalancerPortField);

            // サービスポート
            servicePortField = new TextField(ViewProperties.getCaption("field.loadBalancerServicePort"));
            servicePortField.setWidth(TEXT_WIDTH);
            sublayout.addComponent(servicePortField);

            // プロトコル
            protocolSelect = new ComboBox(ViewProperties.getCaption("field.loadBalancerProtocol"));
            protocolSelect.setWidth(TEXT_WIDTH);
            protocolSelect.setImmediate(true);
            sublayout.addComponent(protocolSelect);
            protocolSelect.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    protocolValueChange(event);
                }
            });

            // SSLキー
            sslKeySelect = new ComboBox(ViewProperties.getCaption("field.loadBalancerSSLKey"));
            sslKeySelect.setWidth(TEXT_WIDTH);
            sslKeySelect.addContainerProperty(SSLKEY_CAPTION_ID, String.class, null);
            sslKeySelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            sslKeySelect.setItemCaptionPropertyId(SSLKEY_CAPTION_ID);
            sublayout.addComponent(sslKeySelect);

            initValidation();
        }

        private void initValidation() {
            // ロードバランサポート
            String message = ViewMessages.getMessage("IUI-000066");
            loadBalancerPortField.setRequired(true);
            loadBalancerPortField.setRequiredError(message);
            loadBalancerPortField.addValidator(new IntegerRangeValidator(1, 65535, message));

            // サービスポート
            message = ViewMessages.getMessage("IUI-000067");
            servicePortField.setRequired(true);
            servicePortField.setRequiredError(message);
            servicePortField.addValidator(new IntegerRangeValidator(1, 65535, message));

            // プロトコル
            message = ViewMessages.getMessage("IUI-000068");
            protocolSelect.setRequired(true);
            protocolSelect.setRequiredError(message);

            // SSLキー
            message = ViewMessages.getMessage("IUI-000116");
            sslKeySelect.setRequired(true);
            sslKeySelect.setRequiredError(message);
        }

        public void show(LoadBalancerDto loadBalancer, Integer loadBalancerPort, ComponentDto component,
                List<SslKeyDto> sslKeys) {
            // ロードバランサ名
            nameField.setReadOnly(false);
            nameField.setValue(loadBalancer.getLoadBalancer().getLoadBalancerName());
            nameField.setReadOnly(true);

            // サービス名
            serviceField.setReadOnly(false);
            serviceField.setValue(component.getComponent().getComponentName());
            serviceField.setReadOnly(true);

            // プロトコル
            protocolSelect.addItem("HTTP");
            protocolSelect.addItem("TCP");
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getLoadBalancer().getType())) {
                protocolSelect.addItem("HTTPS");
                protocolSelect.addItem("SSL");
            }

            // SSLキー
            for (SslKeyDto sslKey : sslKeys) {
                Item item = sslKeySelect.addItem(sslKey.getKeyNo());
                item.getItemProperty(SSLKEY_CAPTION_ID).setValue(sslKey.getKeyName());
            }

            // 追加時
            if (loadBalancerPort == null) {
                // 振り分けサービスに応じたデフォルト値を設定する
                // TODO: デフォルト値を外部化する
                Integer servicePort = null;
                String protocol = null;

                ComponentType componentType = component.getComponentType();
                if ("apache".equals(componentType.getComponentTypeName())) {
                    loadBalancerPort = 80;
                    servicePort = 80;
                    protocol = "HTTP";
                } else if ("tomcat".equals(componentType.getComponentTypeName())
                        || "geronimo".equals(componentType.getComponentTypeName())) {
                    loadBalancerPort = 8080;
                    servicePort = 8080;
                    protocol = "HTTP";
                } else if ("mysql".equals(componentType.getComponentTypeName())) {
                    loadBalancerPort = 3306;
                    servicePort = 3306;
                    protocol = "TCP";
                }

                // ロードバランサポート
                if (loadBalancerPort != null) {
                    loadBalancerPortField.setValue(loadBalancerPort.toString());
                }

                // サービスポート
                if (servicePort != null) {
                    servicePortField.setValue(servicePort.toString());
                }

                // プロトコル
                if (protocol != null) {
                    protocolSelect.select(protocol);
                }
            }
            // 編集時
            else {
                // リスナーを取得
                LoadBalancerListener listener = null;
                for (LoadBalancerListener tmpListener : loadBalancer.getLoadBalancerListeners()) {
                    if (loadBalancerPort.equals(tmpListener.getLoadBalancerPort())) {
                        listener = tmpListener;
                        break;
                    }
                }

                // ロードバランサポート
                if (listener.getLoadBalancerPort() != null) {
                    loadBalancerPortField.setValue(listener.getLoadBalancerPort().toString());
                }

                // サービスポート
                if (listener.getServicePort() != null) {
                    servicePortField.setValue(listener.getServicePort().toString());
                }

                // プロトコル
                if (listener.getProtocol() != null) {
                    protocolSelect.select(listener.getProtocol());
                }

                // SSLKey
                if (listener.getSslKeyNo() != null) {
                    sslKeySelect.select(listener.getSslKeyNo());
                }
            }
        }

        private void protocolValueChange(Property.ValueChangeEvent event) {
            if ("HTTPS".equals(protocolSelect.getValue()) || "SSL".equals(protocolSelect.getValue())) {
                sslKeySelect.setEnabled(true);
            } else {
                sslKeySelect.setEnabled(false);
            }
        }

    }

    private boolean isAddMode() {
        return (loadBalancerPort == null);
    }

    private void loadData() {
        // ロードバランサ情報の取得
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        List<LoadBalancerDto> loadBalancers = loadBalancerService.getLoadBalancers(ViewContext.getFarmNo());
        for (LoadBalancerDto loadBalancer : loadBalancers) {
            if (loadBalancerNo.equals(loadBalancer.getLoadBalancer().getLoadBalancerNo())) {
                this.loadBalancer = loadBalancer;
                break;
            }
        }

        // SSLキー情報の取得
        this.sslKeys = loadBalancerService.getSSLKey(loadBalancerNo);

        // サービス情報の取得
        Long componentNo = loadBalancer.getLoadBalancer().getComponentNo();
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        List<ComponentDto> components = componentService.getComponents(ViewContext.getFarmNo());
        for (ComponentDto component : components) {
            if (componentNo.equals(component.getComponent().getComponentNo())) {
                this.component = component;
                break;
            }
        }
    }

    private void addButtonClick(Button.ClickEvent event) {
        // 入力値を取得
        String loadBalancerPortString = (String) basicForm.loadBalancerPortField.getValue();
        String servicePortString = (String) basicForm.servicePortField.getValue();
        String protocol = (String) basicForm.protocolSelect.getValue();
        Long sslKeyNo = (Long) basicForm.sslKeySelect.getValue();

        // 入力チェック
        try {
            basicForm.loadBalancerPortField.validate();
            basicForm.servicePortField.validate();
            basicForm.protocolSelect.validate();
            if (basicForm.sslKeySelect.isEnabled()) {
                basicForm.sslKeySelect.validate();
            }
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        Integer loadBalancerPort = Integer.valueOf(loadBalancerPortString);
        Integer servicePort = Integer.valueOf(servicePortString);

        // 追加時
        if (isAddMode()) {
            // オペレーションログ
            AutoApplication aapl = (AutoApplication) getApplication();
            aapl.doOpLog("LOAD_BALANCER", "Attach LB_Listener", null, null, loadBalancerNo, loadBalancerPortString);

            // リスナーの追加
            try {
                loadBalancerService.createListener(loadBalancerNo, loadBalancerPort, servicePort, protocol, sslKeyNo);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }
        // 編集時
        else {
            // オペレーションログ
            AutoApplication aapl = (AutoApplication) getApplication();
            aapl.doOpLog("LOAD_BALANCER", "Edit LB_Listener", null, null, loadBalancerNo, loadBalancerPortString);

            // リスナーの更新
            try {
                loadBalancerService.updateListener(loadBalancerNo, this.loadBalancerPort, loadBalancerPort,
                        servicePort, protocol, sslKeyNo);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // 画面を閉じる
        close();
    }

}
