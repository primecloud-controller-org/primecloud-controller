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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * サーバの新規作成画面を生成します。
 * </p>
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class WinServerAdd extends Window {

    private final String COLUMN_HEIGHT = "28px";

    private BasicForm basicForm;

    private List<PlatformDto> platforms;

    @Override
    public void attach() {
        //モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winServerAdd"));
        setModal(true);
        setWidth("620px");
        //setHeight("600px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true, true, false, true);
        layout.setSpacing(false);

        // フォーム
        basicForm = new BasicForm();
        layout.addComponent(basicForm);

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.addServer"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
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

        // プラットフォーム情報の表示
        loadData();
        basicForm.cloudTable.show(platforms);
        basicForm.cloudTable.selectFirst();
    }

    private class BasicForm extends Form {

        private TextField serverNameField;

        private TextField commentField;

        private SelectCloudTable cloudTable;

        private SelectImageTable imageTable;

        private AvailableServiceTable serviceTable;

        private Button attachButton;

        private List<Long> selectedComponentNos;

        @Override
        public void attach() {
            // サーバ名
            serverNameField = new TextField(ViewProperties.getCaption("field.serverName"));
            getLayout().addComponent(serverNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("95%");
            getLayout().addComponent(commentField);

            // プラットフォーム情報テーブル
            cloudTable = new SelectCloudTable();
            getLayout().addComponent(cloudTable);
            cloudTable.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    cloudTableSelect(event);
                }
            });

            // サーバ種別情報テーブル
            imageTable = new SelectImageTable();
            getLayout().addComponent(imageTable);
            imageTable.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    imageTableSelect(event);
                }
            });

            // サービスを有効にするかどうか
            String enableServiceConf = Config.getProperty("ui.enableService");
            boolean enableService = (enableServiceConf == null) || (BooleanUtils.toBoolean(enableServiceConf));

            if (enableService) {
                // サービス情報テーブル
                serviceTable = new AvailableServiceTable();
                Panel panel = new Panel();
                panel.setSizeFull();
                panel.setWidth("470px");
                CssLayout lay = new CssLayout();
                panel.setContent(lay);
                lay.setSizeFull();
                lay.addStyleName("win-server-add-panel");
                panel.addComponent(serviceTable);
                getLayout().addComponent(panel);

                // サービス選択ボタン
                attachButton = new Button(ViewProperties.getCaption("button.serverAttachService"));
                attachButton.setDescription(ViewProperties.getCaption("description.serverAttachService"));
                attachButton.setIcon(Icons.SERVICETAB.resource());
                attachButton.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        attachButtonClick(event);
                    }
                });

                HorizontalLayout attachLayout = new HorizontalLayout();
                attachLayout.setSpacing(true);
                Label attachLabel = new Label(ViewProperties.getCaption("label.serverAttachService"));
                attachLayout.addComponent(attachButton);
                attachLayout.addComponent(attachLabel);
                attachLayout.setComponentAlignment(attachLabel, Alignment.MIDDLE_LEFT);
                getLayout().addComponent(attachLayout);
            }

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000022");
            serverNameField.setRequired(true);
            serverNameField.setRequiredError(message);
            serverNameField.addValidator(new StringLengthValidator(message, -1, 15, false));
            serverNameField.addValidator(new RegexpValidator("^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", true, message));

            message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }

        private void cloudTableSelect(Property.ValueChangeEvent event) {
            // 選択がない場合はサーバ種別情報をクリア
            if (cloudTable.getValue() == null) {
                imageTable.removeAllItems();
                return;
            }

            // サーバ種別情報を表示
            PlatformDto platform = findPlatform(cloudTable.getValue());
            imageTable.show(platform.getImages());
            imageTable.selectFirst();
        }

        private void imageTableSelect(Property.ValueChangeEvent event) {
            // 選択したサービスをクリア
            selectedComponentNos = null;

            if (serviceTable == null) {
                return;
            }

            // 選択がない場合はサービス情報をクリア
            if (cloudTable.getValue() == null || imageTable.getValue() == null) {
                serviceTable.removeAllItems();
                serviceTable.setCaption("");
                attachButton.setEnabled(false);
                return;
            }

            // サービス情報を表示
            ImageDto image = findImage(cloudTable.getValue(), imageTable.getValue());
            serviceTable.show(image.getComponentTypes());

            // サービス情報テーブルのキャプションを変更
            String caption = "「" + image.getImage().getImageNameDisp() + "」で利用できるサービス";
            serviceTable.setCaption(caption);

            // サービス選択ボタンを状態を変更
            if (image.getComponentTypes().size() > 0) {
                attachButton.setEnabled(true);
            } else {
                attachButton.setEnabled(false);
            }
        }

        private void attachButtonClick(ClickEvent event) {
            if (cloudTable.getValue() == null || imageTable.getValue() == null) {
                return;
            }

            ImageDto image = findImage(cloudTable.getValue(), imageTable.getValue());

            WinServerAttachService winServerAttachService = new WinServerAttachService(null, image,
                    selectedComponentNos);
            winServerAttachService.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    List<Long> componentNos = (List<Long>) ContextUtils.getAttribute("componentNos");
                    if (componentNos != null) {
                        ContextUtils.removeAttribute("componentNos");
                        selectedComponentNos = componentNos;
                    }
                }
            });

            getWindow().getApplication().getMainWindow().addWindow(winServerAttachService);
        }

    }

    private class SelectCloudTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.selectCloud"));
            setWidth("470px");
            setPageLength(4);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("win-server-add-cloud");

            // カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Cloud", Label.class, new Label());
            setColumnExpandRatio("Cloud", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<PlatformDto> platforms) {
            removeAllItems();

            if (platforms == null) {
                return;
            }

            for (int i = 0; i < platforms.size(); i++) {
                PlatformDto platform = platforms.get(i);

                // プラットフォーム名
                Icons icon = IconUtils.getPlatformIcon(platform);
                String description = platform.getPlatform().getPlatformNameDisp();
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), icon, description),
                        Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { (i + 1), slbl }, platform.getPlatform().getPlatformNo());
            }
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }

        public void selectFirst() {
            if (size() > 0) {
                select(firstItemId());
            }
        }

    }

    private class SelectImageTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.selectImage"));
            setWidth("470px");
            setPageLength(3);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("win-server-add-os");

            // カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Image", Label.class, new Label());
            addContainerProperty("Detail", Label.class, new Label());
            setColumnExpandRatio("Image", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<ImageDto> images) {
            removeAllItems();

            if (images == null) {
                return;
            }

            for (int i = 0; i < images.size(); i++) {
                ImageDto image = images.get(i);

                // サーバ種別名
                String name = image.getImage().getImageNameDisp();
                Icons nameIcon = IconUtils.getImageIcon(image);
                Label nlbl = new Label(IconUtils.createImageTag(getApplication(), nameIcon, name), Label.CONTENT_XHTML);
                nlbl.setHeight(COLUMN_HEIGHT);

                // OS名
                String os = image.getImage().getOsDisp();
                Icons osIcon = IconUtils.getOsIcon(image);
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), osIcon, os), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { (i + 1), nlbl, slbl }, image.getImage().getImageNo());
            }
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }

        public void selectFirst() {
            if (size() > 0) {
                select(firstItemId());
            }
        }

    }

    private class AvailableServiceTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.availableService"));
            setWidth("100%");
            setHeight("100px");
            setPageLength(3);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(false);
            setMultiSelect(false);
            setImmediate(true);
            addStyleName("win-server-add-service");

            //カラム設定
            addContainerProperty("Service", Label.class, new Label());
            addContainerProperty("Description", String.class, null);
            setColumnExpandRatio("Service", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<ComponentType> componentTypes) {
            removeAllItems();

            if (componentTypes == null) {
                return;
            }

            for (ComponentType componentType : componentTypes) {
                // サービス名
                String name = componentType.getComponentTypeNameDisp();
                Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), nameIcon, name), Label.CONTENT_XHTML);
                slbl.setHeight("26px");

                // サービス説明
                String description = componentType.getLayerDisp();

                addItem(new Object[] { slbl, description }, componentType.getComponentTypeNo());
            }
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }

    }

    private void loadData() {
        // プラットフォーム情報を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        platforms = instanceService.getPlatforms(ViewContext.getUserNo());

        // 有効でないプラットフォーム情報を除外
        for (int i = platforms.size() - 1; i >= 0; i--) {
            if (BooleanUtils.isNotTrue(platforms.get(i).getPlatform().getSelectable())) {
                platforms.remove(i);
            }
        }

        // プラットフォーム情報をソート
        Collections.sort(platforms, new Comparator<PlatformDto>() {
            @Override
            public int compare(PlatformDto o1, PlatformDto o2) {
                int order1 = (o1.getPlatform().getViewOrder() != null) ? o1.getPlatform().getViewOrder()
                        : Integer.MAX_VALUE;
                int order2 = (o2.getPlatform().getViewOrder() != null) ? o2.getPlatform().getViewOrder()
                        : Integer.MAX_VALUE;
                return order1 - order2;
            }
        });

        for (PlatformDto platform : platforms) {
            List<ImageDto> images = platform.getImages();

            // 有効でないサーバ種別情報を除外
            for (int i = images.size() - 1; i >= 0; i--) {
                if (BooleanUtils.isNotTrue(images.get(i).getImage().getSelectable())) {
                    images.remove(i);
                }
            }

            // サーバ種別情報をソート
            Collections.sort(images, new Comparator<ImageDto>() {
                @Override
                public int compare(ImageDto o1, ImageDto o2) {
                    int order1 = (o1.getImage().getViewOrder() != null) ? o1.getImage().getViewOrder()
                            : Integer.MAX_VALUE;
                    int order2 = (o2.getImage().getViewOrder() != null) ? o2.getImage().getViewOrder()
                            : Integer.MAX_VALUE;
                    return order1 - order2;
                }
            });
        }

        for (PlatformDto platform : platforms) {
            for (ImageDto image : platform.getImages()) {
                List<ComponentType> componentTypes = image.getComponentTypes();

                // 有効でないサービス情報情報を除外
                for (int i = componentTypes.size() - 1; i >= 0; i--) {
                    if (BooleanUtils.isNotTrue(componentTypes.get(i).getSelectable())) {
                        componentTypes.remove(i);
                    }
                }

                // 利用可能なサービス情報をソート
                Collections.sort(componentTypes, new Comparator<ComponentType>() {
                    @Override
                    public int compare(ComponentType o1, ComponentType o2) {
                        int order1 = (o1.getViewOrder() != null) ? o1.getViewOrder() : Integer.MAX_VALUE;
                        int order2 = (o2.getViewOrder() != null) ? o2.getViewOrder() : Integer.MAX_VALUE;
                        return order1 - order2;
                    }
                });
            }
        }
    }

    private PlatformDto findPlatform(Long platformNo) {
        for (PlatformDto platform : platforms) {
            if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                return platform;
            }
        }
        return null;
    }

    private ImageDto findImage(Long platformNo, Long imageNo) {
        PlatformDto platform = findPlatform(platformNo);
        for (ImageDto image : platform.getImages()) {
            if (imageNo.equals(image.getImage().getImageNo())) {
                return image;
            }
        }
        return null;
    }

    private void addButtonClick(ClickEvent event) {
        // 入力値を取得
        String serverName = (String) basicForm.serverNameField.getValue();
        String comment = (String) basicForm.commentField.getValue();
        Long platformNo = basicForm.cloudTable.getValue();
        Long imageNo = basicForm.imageTable.getValue();

        // 入力チェック
        try {
            basicForm.serverNameField.validate();
            basicForm.commentField.validate();
        } catch (InvalidValueException e) {
            String errMes = e.getMessage();
            if (null == errMes) {
                // メッセージが取得できない場合は複合エラー 先頭を表示する
                InvalidValueException[] exceptions = e.getCauses();
                errMes = exceptions[0].getMessage();
            }

            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), errMes);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (platformNo == null) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000023"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (imageNo == null) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000024"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (serverName.startsWith("lb-")) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000083", serverName));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 選択されたイメージを取得
        PlatformDto platform = findPlatform(platformNo);
        ImageDto image = findImage(platformNo, imageNo);

        // サーバを作成
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        Long farmNo = ViewContext.getFarmNo();
        Long instanceNo = null;
        try {
            // AWSサーバを作成
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())) {
                String[] instanceTypes = image.getImageAws().getInstanceTypes().split(",");
                instanceNo = instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0].trim());
            }
            // VMwareサーバを作成
            else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatform().getPlatformType())) {
                String[] instanceTypes = image.getImageVmware().getInstanceTypes().split(",");
                instanceNo = instanceService.createVmwareInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0].trim());
            }
            // Niftyサーバを作成
            else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatform().getPlatformType())) {
                String[] instanceTypes = image.getImageNifty().getInstanceTypes().split(",");
                instanceNo = instanceService.createNiftyInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0].trim());
            }
            // CloudStackサーバを作成
            else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatform().getPlatformType())) {
                String[] instanceTypes = image.getImageCloudstack().getInstanceTypes().split(",");
                instanceNo = instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0].trim());
            }
            // vCloudサーバを作成
            else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatform().getPlatformType())) {
                String[] instanceTypes = image.getImageVcloud().getInstanceTypes().split(",");
                instanceNo = instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0].trim());
            }
            // Azureサーバを作成
            else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
                String[] instanceTypes = image.getImageAzure().getInstanceTypes().split(",");
                instanceNo = instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0].trim());
            }
            // OpenStackサーバを作成
            else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatform().getPlatformType())) {
                String[] instanceTypes = image.getImageOpenstack().getInstanceTypes().split(",");
                instanceNo = instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0].trim());
            }
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        aapl.doOpLog("SERVER", "Make Server", instanceNo, null, null, null);

        // サーバにサービスを関連付ける
        if (basicForm.selectedComponentNos != null && basicForm.selectedComponentNos.size() > 0) {
            instanceService.associateComponents(instanceNo, basicForm.selectedComponentNos);
        }

        // 画面を閉じる
        close();
    }

}
