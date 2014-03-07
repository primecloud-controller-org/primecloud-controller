package jp.primecloud.auto.ui;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.StringUtils;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
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
    final String COLUMN_HEIGHT = "30px";

    final String TAB_HEIGHT = "360px";

    Application apl;

    Long loadBalancerNo;

    BasicTab basicTab;

    LoadBalancerDto loadBalancerDto;

    LoadBalancerPlatformDto platformDto;

    List<ComponentDto> componentDtos;

    WinCloudStackLoadBalancerEdit(Application ap, Long loadBalancerNo) {
        apl = ap;
        this.loadBalancerNo = loadBalancerNo;

        //モーダルウインドウ
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
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // editButtonボタン
        Button editButton = new Button();
        editButton.setCaption(ViewProperties.getCaption("button.editLoadBalancerService"));
        editButton.setDescription(ViewProperties.getCaption("description.editLoadBalancer"));

        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                editButtonClick(event);
            }
        });
        okbar.addComponent(editButton);
        // [Enter]でeditButtonクリック
        editButton.setClickShortcut(KeyCode.ENTER);
        editButton.focus();

        // Cancel
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinCloudStackLoadBalancerEdit.this.close();
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

    private class BasicTab extends VerticalLayout {
        final String SERVICE_CAPTION_ID = "ServiceName";


        Form form;

        TextField loadBalancerNameField;

        TextField commentField;

        Label cloudLabel;

        Label typeLabel;

        ComboBox serviceSelect;

        TextField pubricPortField;

        TextField privatePortField;

        ComboBox algorithmSelect;


        BasicTab() {
            setHeight(TAB_HEIGHT);
            setMargin(false, true, false, true);
            setSpacing(false);

            // フォーム
            form = new Form();
            form.setSizeFull();
            addComponent(form);

            // LB名
            loadBalancerNameField = new TextField(ViewProperties.getCaption("field.loadBalancerName"));
            form.getLayout().addComponent(loadBalancerNameField);

            // コメント欄
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
            serviceSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serviceSelect.setItemCaptionPropertyId(SERVICE_CAPTION_ID);
            form.getLayout().addComponent(serviceSelect);

            // アルゴリズム欄
            algorithmSelect = new ComboBox();
            algorithmSelect.setCaption(ViewProperties.getCaption("field.algorithm"));
            algorithmSelect.setNullSelectionAllowed(false);
            form.getLayout().addComponent(algorithmSelect);

            // パブリックポート欄
            pubricPortField = new TextField(ViewProperties.getCaption("field.publicport"));
            pubricPortField.setWidth("95%");
            form.getLayout().addComponent(pubricPortField);

            // プライベートポート欄
            privatePortField = new TextField(ViewProperties.getCaption("field.privateport"));
            privatePortField.setWidth("95%");
            form.getLayout().addComponent(privatePortField);

        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }

        private void showData() {
            // ロードバランサー名
            loadBalancerNameField.setReadOnly(false);
            loadBalancerNameField.setValue(loadBalancerDto.getLoadBalancer().getLoadBalancerName());
            loadBalancerNameField.setReadOnly(true);

            // コメントの設定
            String comment = loadBalancerDto.getLoadBalancer().getComment();
            if (comment != null) {
                commentField.setValue(comment);
            }

            // アルゴリズムの設定
            List<String> algorithms = new ArrayList<String>();
            algorithms.add("roundrobin");
            algorithms.add("leastconn");
            algorithmSelect.setContainerDataSource(new IndexedContainer(algorithms));
            if (!StringUtils.isEmpty(loadBalancerDto.getCloudstackLoadBalancer().getAlgorithm())) {
                algorithmSelect.select(loadBalancerDto.getCloudstackLoadBalancer().getAlgorithm());
            }

            // パブリックポートの設定
            String publicport = loadBalancerDto.getCloudstackLoadBalancer().getPublicport();
            if (publicport != null) {
                pubricPortField.setValue(publicport);
            }

            // プライベートポートの設定
            String privateport = loadBalancerDto.getCloudstackLoadBalancer().getPrivateport();
            if (comment != null) {
                privatePortField.setValue(privateport);
            }


            // プラットフォーム
            // TODO: アイコン名の取得ロジックのリファクタリング
            Icons icon = Icons.NONE;
            if ("aws".equals(platformDto.getPlatform().getPlatformType())) {
                if (platformDto.getPlatformAws().getEuca()) {
                    icon = Icons.EUCALYPTUS;
                } else {
                    icon = Icons.AWS;
                }
            } else if ("vmware".equals(platformDto.getPlatform().getPlatformType())) {
                icon = Icons.VMWARE;
            } else if ("nifty".equals(platformDto.getPlatform().getPlatformType())) {
                icon = Icons.NIFTY;
            } else if ("cloudstack".equals(platformDto.getPlatform().getPlatformType())) {
                icon = Icons.CLOUD_STACK;
            }

            String description = platformDto.getPlatform().getPlatformNameDisp();
            String cloudValue = "<img src=\"" + VaadinUtils.getIconPath(apl, icon) + "\"><div>" + description
                    + "</div>";
            cloudLabel.setValue(cloudValue);
            cloudLabel.setContentMode(Label.CONTENT_XHTML);

            // ロードバランサ種別
            // TODO: アイコン名取得ロジックのリファクタリング
            String type = loadBalancerDto.getLoadBalancer().getType();
            Icons typeIcon = Icons.NONE;
            String typeString = ViewProperties.getLoadBalancerType(type);

            String typeValue = "<img src=\"" + VaadinUtils.getIconPath(apl, typeIcon) + "\"><div>" + typeString
                    + "</div>";
            typeLabel.setValue(typeValue);
            typeLabel.setContentMode(Label.CONTENT_XHTML);

            // 割り当てサービス選択
            IndexedContainer serviceContainer = new IndexedContainer();
            serviceContainer.addContainerProperty(SERVICE_CAPTION_ID, String.class, null);
            for (ComponentDto componentDto : componentDtos) {
                Item item = serviceContainer.addItem(componentDto);
                item.getItemProperty(SERVICE_CAPTION_ID).setValue(componentDto.getComponent().getComponentName());
            }
            serviceSelect.setContainerDataSource(serviceContainer);

            // 既に割り当てられているサービスを選択する
            Long componentNo = loadBalancerDto.getLoadBalancer().getComponentNo();
            for (ComponentDto componentDto : componentDtos) {
                if (componentNo.equals(componentDto.getComponent().getComponentNo())) {
                    serviceSelect.select(componentDto);
                    break;
                }
            }

            // リスナーが存在する場合は選択不可にする
            if (!loadBalancerDto.getLoadBalancerListeners().isEmpty()) {
                serviceSelect.setEnabled(false);
            }
        }
    }

    private void initData() {
        Long userNo = ViewContext.getUserNo();
        Long farmNo = ViewContext.getFarmNo();

        // ロードバランサ情報を取得
        // TODO: ロジックを必ずリファクタリングすること！
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        List<LoadBalancerDto> loadBalancerDtos = loadBalancerService.getLoadBalancers(farmNo);
        for (LoadBalancerDto loadBalancerDto : loadBalancerDtos) {
            if (loadBalancerNo.equals(loadBalancerDto.getLoadBalancer().getLoadBalancerNo())) {
                this.loadBalancerDto = loadBalancerDto;
                break;
            }
        }

        // プラットフォーム情報を取得
        // TODO: ロジックを必ずリファクタリングすること！
        Long platformNo = loadBalancerDto.getLoadBalancer().getPlatformNo();
        List<LoadBalancerPlatformDto> platformDtos = loadBalancerService.getPlatforms(userNo);
        for (LoadBalancerPlatformDto platformDto : platformDtos) {
            if (platformNo.equals(platformDto.getPlatform().getPlatformNo())) {
                this.platformDto = platformDto;
                break;
            }
        }

        // コンポーネント情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        componentDtos = componentService.getComponents(farmNo);
    }

    private void initValidation() {
        basicTab.initValidation();
    }

    private void showData() {
        basicTab.showData();
    }

    private void editButtonClick(ClickEvent event) {
        // 入力値を取得
        String comment = (String) basicTab.commentField.getValue();
        ComponentDto componentDto = (ComponentDto) basicTab.serviceSelect.getValue();

        String algorithm = (String) basicTab.algorithmSelect.getValue();
        String pubricPort = (String) basicTab.pubricPortField.getValue();
        String privatePort = (String) basicTab.privatePortField.getValue();


        // TODO: 入力チェック
        try {
            basicTab.commentField.validate();
            basicTab.algorithmSelect.validate();
            basicTab.pubricPortField.validate();
            basicTab.privatePortField.validate();

        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);

        try {
            String loadBalancerName = loadBalancerDto.getLoadBalancer().getLoadBalancerName();
            Long componentNo = componentDto.getComponent().getComponentNo();
            loadBalancerService.updateCloudstackLoadBalancer(loadBalancerNo, loadBalancerName, comment, componentNo,
                                                                                    algorithm, pubricPort, privatePort);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        // 画面を閉じる
        close();
    }

}
