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
import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.AbstractStringValidator;
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

    final String TEXT_WIDTH = "120px";

    final String PROTOCOL_CAPTION_ID = "ProtocolName";

    final String SSLKEY_CAPTION_ID = "SSLKeyName";

    Application apl;

    Long loadBalancerNo;

    Integer loadBalancerPort;

    String loadBalancerType;

    BasicForm basicForm;

    LoadBalancerDto loadBalancerDto;

    ComponentDto componentDto;

    List<SslKeyDto> keyList;

    WinLoadBalancerConfigListener(Application ap, Long loadBalancerNo, Integer loadBalancerPort) {
        apl = ap;
        this.loadBalancerNo = loadBalancerNo;
        this.loadBalancerPort = loadBalancerPort;

        //モーダルウインドウ
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
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, true, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

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
        okbar.addComponent(addButton);
        // [Enter]でaddButtonクリック
        addButton.setClickShortcut(KeyCode.ENTER);
        addButton.focus();

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        okbar.addComponent(cancelButton);

        // 初期データの取得
        initData();

        // 入力チェックの設定
        initValidation();

        // データの表示
        showData();
    }

    private class BasicForm extends Form {

        Form mainForm;

        Form subForm;

        TextField nameField;

        TextField serviceField;

        TextField loadBalancerPortField;

        TextField servicePortField;

        ComboBox protocolSelect;

        ComboBox sslKeySelect;

        BasicForm() {
            // メインフォーム
            mainForm = new Form();
            Layout mainLayout = mainForm.getLayout();
            addComponent(mainForm);

            // ロードバランサ名
            nameField = new TextField(ViewProperties.getCaption("field.loadBalancerName"));
            mainLayout.addComponent(nameField);

            // サービス名
            serviceField = new TextField(ViewProperties.getCaption("field.loadBalancerService"));
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
                    changeCheckEnabled(event);
                }
            });

            // SSLキー
            sslKeySelect = new ComboBox(ViewProperties.getCaption("field.loadBalancerSSLKey"));
            sslKeySelect.setWidth(TEXT_WIDTH);
            sublayout.addComponent(sslKeySelect);


        }

        private void initValidation() {
            // 入力チェック
            String message;

            // ロードバランサポート
            message = ViewMessages.getMessage("IUI-000066");
            loadBalancerPortField.setRequired(true);
            loadBalancerPortField.setRequiredError(message);
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerDto.getLoadBalancer().getType())) {
                loadBalancerPortField.addValidator(new AbstractStringValidator(message) {
                    @Override
                    protected boolean isValidString(String value) {
                        try {
                            int port = Integer.parseInt(value);
                            if (port != 80 && port != 443 && (port < 1024 || 65535 < port)) {
                                return false;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                        return true;
                    }
                });
            } else if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancerDto.getLoadBalancer().getType())) {
                loadBalancerPortField.addValidator(new IntegerRangeValidator(1, 65535, message));
            }

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

        private void showData() {
            // ロードバランサ名
            nameField.setValue(loadBalancerDto.getLoadBalancer().getLoadBalancerName());
            nameField.setReadOnly(true);

            // サービス名
            serviceField.setValue(componentDto.getComponent().getComponentName());
            serviceField.setReadOnly(true);

            // プロトコル
            IndexedContainer protocols = getProtocolList();
            protocolSelect.setContainerDataSource(protocols);
            protocolSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            protocolSelect.setItemCaptionPropertyId(PROTOCOL_CAPTION_ID);

            // SSLキー
            IndexedContainer keys = getKeyList();
            sslKeySelect.setContainerDataSource(keys);
            sslKeySelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            sslKeySelect.setItemCaptionPropertyId(SSLKEY_CAPTION_ID);

            // 追加時
            if (isAddMode()) {
                // 振り分けサービスに応じたデフォルト値を設定する
                String loadBalancerPort = "";
                String servicePort = "";
                String protocol = "";
                ComponentType componentType = componentDto.getComponentType();
                if ("apache".equals(componentType.getComponentTypeName())) {
                    loadBalancerPort = "80";
                    servicePort = "80";
                    protocol = "HTTP";
                } else if ("tomcat".equals(componentType.getComponentTypeName()) || "geronimo".equals(componentType.getComponentTypeName())) {
                    loadBalancerPort = "8080";
                    servicePort = "8080";
                    protocol = "HTTP";
                } else if ("mysql".equals(componentType.getComponentTypeName())) {
                    loadBalancerPort = "3306";
                    servicePort = "3306";
                    protocol = "TCP";
                }

                loadBalancerPortField.setValue(loadBalancerPort);
                servicePortField.setValue(servicePort);
                protocolSelect.select(protocol);
            }
            // 編集時
            else {
                // リスナーを取得
                LoadBalancerListener listener = null;
                for (LoadBalancerListener tmpListener : loadBalancerDto.getLoadBalancerListeners()) {
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
                    for (SslKeyDto key : keyList) {
                        if (listener.getSslKeyNo().equals(key.getKeyNo())) {
                            sslKeySelect.select(key);
                        }
                    }
                }

            }
        }

        private IndexedContainer getProtocolList() {
            // TODO: ロードバランサの種別によって対応プロトコルを設定
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty(PROTOCOL_CAPTION_ID, String.class, null);

            Item item = container.addItem("TCP");
            item.getItemProperty(PROTOCOL_CAPTION_ID).setValue("TCP");

            item = container.addItem("HTTP");
            item.getItemProperty(PROTOCOL_CAPTION_ID).setValue("HTTP");

            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancerType)){
                item = container.addItem("HTTPS");
                item.getItemProperty(PROTOCOL_CAPTION_ID).setValue("HTTPS");
            }

            return container;
        }

        private IndexedContainer getKeyList() {
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty(SSLKEY_CAPTION_ID, String.class, null);

            for (SslKeyDto key : keyList) {
                Item item = container.addItem(key);
                item.getItemProperty(SSLKEY_CAPTION_ID).setValue(key.getKeyName());
            }

            return container;
        }


        private void changeCheckEnabled(Property.ValueChangeEvent event) {
            if ("HTTPS".equals(protocolSelect.getValue())) {
                sslKeySelect.setEnabled(true);

            } else {
                sslKeySelect.setEnabled(false);
            }
        }

    }

    private boolean isAddMode() {
        return (loadBalancerPort == null);
    }

    private void initData() {
        // 初期データの取得
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        List<LoadBalancerDto> loadBalancerDtos = loadBalancerService.getLoadBalancers(ViewContext.getFarmNo());
        for (LoadBalancerDto loadBalancerDto : loadBalancerDtos) {
            if (loadBalancerNo.equals(loadBalancerDto.getLoadBalancer().getLoadBalancerNo())) {
                this.loadBalancerDto = loadBalancerDto;
                break;
            }
        }

        this.loadBalancerType = loadBalancerDto.getLoadBalancer().getType();
        this.keyList = loadBalancerService.getSSLKey(loadBalancerNo);

        Long componentNo = loadBalancerDto.getLoadBalancer().getComponentNo();

        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        List<ComponentDto> componentDtos = componentService.getComponents(ViewContext.getFarmNo());
        for (ComponentDto componentDto : componentDtos) {
            if (componentNo.equals(componentDto.getComponent().getComponentNo())) {
                this.componentDto = componentDto;
                break;
            }
        }
    }

    private void initValidation() {
        basicForm.initValidation();
    }

    private void showData() {
        basicForm.showData();
    }

    private void addButtonClick(Button.ClickEvent event) {
        // 入力値を取得
        String loadBalancerPortString = (String) basicForm.loadBalancerPortField.getValue();
        String servicePortString = (String) basicForm.servicePortField.getValue();
        String protocol = (String) basicForm.protocolSelect.getValue();
        SslKeyDto key = (SslKeyDto) basicForm.sslKeySelect.getValue();

        // TODO: 入力チェック
        try {
            basicForm.loadBalancerPortField.validate();
            basicForm.servicePortField.validate();
            basicForm.protocolSelect.validate();
            if (basicForm.sslKeySelect.isEnabled()) {
                basicForm.sslKeySelect.validate();
            } else {
                key = new SslKeyDto();
            }
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);

        Integer loadBalancerPort = Integer.valueOf(loadBalancerPortString);
        Integer servicePort = Integer.valueOf(servicePortString);
        AutoApplication aapl =  (AutoApplication)apl;
        Long sslKeyNo =  key.getKeyNo();

        if (isAddMode()) {
            // 追加時

            //オペレーションログ
            aapl.doOpLog("LOAD_BALANCER", "Attach LB_Listener", null, null, loadBalancerNo, String.valueOf(loadBalancerPort));

            // リスナーの追加
            try {
                loadBalancerService.createListener(loadBalancerNo, loadBalancerPort, servicePort, protocol, sslKeyNo);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else {
            // 編集時

            //オペレーションログ
            aapl.doOpLog("LOAD_BALANCER", "Edit LB_Listener", null, null, loadBalancerNo, String.valueOf(loadBalancerPort));

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
