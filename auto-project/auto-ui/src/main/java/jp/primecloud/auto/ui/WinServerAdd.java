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

import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.Application;
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
@SuppressWarnings("serial")
public class WinServerAdd extends Window {
    final String COLUMN_HEIGHT = "28px";

    Application apl;

    TextField serverNameField;

    TextField commentField;

    Table cloudTable;

    Table imageTable;

    Table serviceTable;

    List<PlatformDto> platforms;

    ImageDto selectImage;

    List<Long> componentNos;

    boolean attachService = false;

    WinServerAdd(Application ap) {
        apl = ap;

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
        layout.addComponent(new BasicForm());

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.addServer"));

        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });

        // [Enter]でaddButtonクリック
        addButton.setClickShortcut(KeyCode.ENTER);
        addButton.focus();
        okbar.addComponent(addButton);

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

        // 入力チェックの設定
        initValidation();

        // 初期データの取得
        initData();

        // クラウドテーブルの表示
        showClouds();
    }

    private class BasicForm extends Form {
        BasicForm() {
            // サーバ名
            serverNameField = new TextField(ViewProperties.getCaption("field.serverName"));
            getLayout().addComponent(serverNameField);

            // コメント欄
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("95%");
            getLayout().addComponent(commentField);

            // クラウド選択
            cloudTable = new SelectCloudTable();
            getLayout().addComponent(cloudTable);

            // イメージ選択
            imageTable = new SelectImageTable();
            getLayout().addComponent(imageTable);

            // イメージごとの可能サービス表示
            Panel panel = new Panel();
            panel.setSizeFull();
            panel.setWidth("470px");
            CssLayout lay = new CssLayout();
            panel.setContent(lay);
            lay.setSizeFull();
            lay.addStyleName("win-server-add-panel");
            serviceTable = new AvailableServiceTable();
            panel.addComponent(serviceTable);
            getLayout().addComponent(panel);

            //サービス選択ボタン
            Button btnService = new Button(ViewProperties.getCaption("button.serverAttachService"));
            btnService.setDescription(ViewProperties.getCaption("description.serverAttachService"));
            btnService.setIcon(Icons.SERVICETAB.resource());
            btnService.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    WinServerAttachService winServerAttachService = new WinServerAttachService(getApplication(), null,
                            selectImage, componentNos);
                    winServerAttachService.addListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent e) {
                            List<Long> componentNos = (List<Long>) ContextUtils.getAttribute("componentNos");
                            if (componentNos != null) {
                                ContextUtils.removeAttribute("componentNos");
                                WinServerAdd.this.componentNos = componentNos;
                                attachService = true;
                            }
                        }
                    });
                    getWindow().getApplication().getMainWindow().addWindow(winServerAttachService);
                }
            });

            HorizontalLayout hlay = new HorizontalLayout();
            hlay.setSpacing(true);
            Label txt = new Label(ViewProperties.getCaption("label.serverAttachService"));
            hlay.addComponent(btnService);
            hlay.addComponent(txt);
            hlay.setComponentAlignment(txt, Alignment.MIDDLE_LEFT);

            getLayout().addComponent(hlay);
        }
    }

    private class SelectCloudTable extends Table {
        SelectCloudTable() {
            //テーブル基本設定
            setCaption(ViewProperties.getCaption("table.selectCloud"));
            //            setWidth("100%");
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

            //カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Cloud", Label.class, new Label());
            setColumnExpandRatio("Cloud", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {
                    if (propertyId == null) {
                        return "";
                    } else {
                        return propertyId.toString().toLowerCase();
                    }
                }
            });

            // 行が選択されたときのイベント
            addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    Long platformNo = (Long) getValue();

                    // サーバ種別を表示
                    showImages(platformNo);
                }
            });
        }
    }

    private class SelectImageTable extends Table {
        SelectImageTable() {
            //テーブル基本設定
            setCaption(ViewProperties.getCaption("table.selectImage"));
            //            setWidth("100%");
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

            //カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Image", Label.class, new Label());
            addContainerProperty("Detail", Label.class, new Label());
            setColumnExpandRatio("Image", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {
                    if (propertyId == null) {
                        return "";
                    } else {
                        return propertyId.toString().toLowerCase();
                    }
                }
            });

            // 行が選択されたときのイベント
            addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    Long platformNo = (Long) cloudTable.getValue();
                    Long imageNo = (Long) imageTable.getValue();

                    // 利用可能サービスを表示
                    showServices(platformNo, imageNo);

                    // 選択したサービスをクリア
                    componentNos = null;
                    attachService = false;
                }
            });
        }
    }

    private class AvailableServiceTable extends Table {
        AvailableServiceTable() {
            //テーブル基本設定
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

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {

                    if (propertyId == null) {
                        return "";
                    }
                    String ret = propertyId.toString().toLowerCase();

                    Long componentTypeNo = (Long) itemId;
                    List<ComponentType> componentTypes = selectImage.getComponentTypes();
                    for (ComponentType componentType: componentTypes) {
                        if (componentType.getComponentTypeNo().equals(componentTypeNo) &&
                            BooleanUtils.isNotTrue(componentType.getSelectable())) {
                            //無効コンポーネントタイプの場合は、セルの表示をDisableに変更する
                            ret += " v-disabled";
                            break;
                        }
                    }
                    return ret;
                }
            });
        }
    }

    private void initValidation() {
        String message = ViewMessages.getMessage("IUI-000022");
        serverNameField.setRequired(true);
        serverNameField.setRequiredError(message);
        serverNameField.addValidator(new StringLengthValidator(message, 1, 15, false));
        serverNameField.addValidator(new RegexpValidator("^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", true, message));

        message = ViewMessages.getMessage("IUI-000003");
        commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
    }

    private void initData() {
        // ユーザ番号
        Long userNo = ViewContext.getUserNo();

        // クラウド情報を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        platforms = instanceService.getPlatforms(userNo);
    }

    private void showClouds() {
        cloudTable.removeAllItems();

        // クラウド情報をテーブルに追加
        for (int i = 0; i < platforms.size(); i++) {
            PlatformDto platformDto = platforms.get(i);

            if (BooleanUtils.isNotTrue(platformDto.getPlatform().getSelectable())) {
                //使用不可プラットフォームの場合、非表示
                continue;
            }

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
            } else if ("cloudstack".equals(platformDto.getPlatform().getPlatformType())){
                icon = Icons.CLOUD_STACK;
            }

            String description = platformDto.getPlatform().getPlatformNameDisp();

            Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, icon) + "\"><div>" + description
                    + "</div>", Label.CONTENT_XHTML);
            slbl.setHeight(COLUMN_HEIGHT);

            cloudTable.addItem(new Object[] { (i + 1), slbl }, platformDto.getPlatform().getPlatformNo());
        }

        Long platformNo = null;
        if (cloudTable.getItemIds().size() > 0) {
            platformNo = (Long) cloudTable.getItemIds().toArray()[0];
        }

        // 先頭のクラウド情報を選択する
        cloudTable.select(platformNo);
    }

    private void showImages(Long platformNo) {
        imageTable.removeAllItems();
        serviceTable.removeAllItems();
        if (platformNo == null) {
            return;
        }

        // 選択されたクラウドで利用可能なサーバ種別情報を取得
        List<ImageDto> images = null;
        for (PlatformDto platform : platforms) {
            if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                images = platform.getImages();
                break;
            }
        }

        // サーバ種別情報がない場合
        if (images == null) {
            return;
        }

        // サーバ種別情報をテーブルに追加
        int n = 0;
        for (ImageDto image : images) {
            // 選択可能でないサーバ種別の場合はスキップ
            if (BooleanUtils.isNotTrue(image.getImage().getSelectable())) {
                continue;
            }

            // サーバ種別名
            // TODO: アイコン名取得ロジックのリファクタリング
            String name = image.getImage().getImageNameDisp();
            String iconName = StringUtils.substringBefore(image.getImage().getImageName(), "_");
            Icons nameIcon;
            if ("application".equals(iconName)) {
                nameIcon = Icons.PAAS;
            } else if ("prjserver".equals(iconName)) {
                nameIcon = Icons.PRJSERVER;
            }else if ("windows".equals(iconName)) {
                nameIcon = Icons.WINDOWS_APP;
            }else if ("cloudstack".equals(iconName)) {
                nameIcon = Icons.CLOUD_STACK;
            } else {
                nameIcon = Icons.fromName(iconName);
            }

            Label nlbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, nameIcon) + "\"><div>" + name + "</div>", Label.CONTENT_XHTML);
            nlbl.setHeight(COLUMN_HEIGHT);

            // OS名
            // TODO: アイコン名取得ロジックのリファクタリング
            String os = image.getImage().getOsDisp();
            Icons osIcon = Icons.NONE;
            if (image.getImage().getOs().startsWith("centos")) {
                osIcon = Icons.CENTOS;
            }else if (image.getImage().getOs().startsWith("windows")) {
                osIcon = Icons.WINDOWS;
            }

            Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, osIcon) + "\"><div>" + os + "</div>",
                    Label.CONTENT_XHTML);
            slbl.setHeight(COLUMN_HEIGHT);

            n++;
            imageTable.addItem(new Object[] { n, nlbl, slbl }, image.getImage().getImageNo());
        }

        Long imageNo = null;
        if (imageTable.getItemIds().size() > 0) {
            imageNo = (Long) imageTable.getItemIds().toArray()[0];
        }

        // 先頭のサーバ種別を選択する
        imageTable.select(imageNo);
    }

    private void showServices(Long platformNo, Long imageNo) {
        serviceTable.removeAllItems();
        if (platformNo == null || imageNo == null) {
            return;
        }

        // 利用可能なサービス情報を取得
        List<ComponentType> componentTypes = null;
        for (PlatformDto platform : platforms) {
            if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                List<ImageDto> images = platform.getImages();
                for (ImageDto image : images) {
                    if (imageNo.equals(image.getImage().getImageNo())) {
                        this.selectImage = image;

                        componentTypes = image.getComponentTypes();
                        //表のキャプションを変更
                        String caption = image.getImage().getImageNameDisp();
                        caption = "「" + caption + "」で利用できるサービス";
                        serviceTable.setCaption(caption);

                        break;
                    }
                }
                break;
            }
        }

        // サービス情報がない場合
        if (componentTypes == null) {
            return;
        }

        // サービス情報をテーブルに追加
        for (int i = 0; i < componentTypes.size(); i++) {
            ComponentType componentType = componentTypes.get(i);

            // サービス名
            String name = componentType.getComponentTypeNameDisp();
            Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());

            Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, nameIcon) + "\"><div>" + name
                    + "</div>", Label.CONTENT_XHTML);
            slbl.setHeight("26px");

            // サービス説明
            String description = componentType.getLayerDisp();

            serviceTable.addItem(new Object[] { slbl, description }, componentType.getComponentTypeNo());
        }
    }

    private void addButtonClick(ClickEvent event) {
        // 入力値を取得
        String serverName = (String) serverNameField.getValue();
        String comment = (String) commentField.getValue();
        Long platformNo = (Long) cloudTable.getValue();
        Long imageNo = (Long) imageTable.getValue();

        // TODO: 入力チェック
        try {
            serverNameField.validate();
            commentField.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (platformNo == null) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), ViewMessages
                    .getMessage("IUI-000023"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (imageNo == null) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), ViewMessages
                    .getMessage("IUI-000024"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (serverName.startsWith("lb-")) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), ViewMessages
                    .getMessage("IUI-000083", serverName));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 選択されたイメージを取得
        PlatformDto platformDto = null;
        ImageDto imageDto = null;
        for (PlatformDto platform : platforms) {
            if (platform.getPlatform().getPlatformNo().equals(platformNo)) {
                platformDto = platform;
                for (ImageDto tmpImage : platform.getImages()) {
                    if (tmpImage.getImage().getImageNo().equals(imageNo)) {
                        imageDto = tmpImage;
                        break;
                    }
                }
                break;
            }
        }

        // クラウド番号
        Long farmNo = ViewContext.getFarmNo();
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        Long instanceNo = null;
        if ("aws".equals(platformDto.getPlatform().getPlatformType())) {
            // AWSサーバを作成（ロジックを実行）
            try {
                String[] instanceTypes = imageDto.getImageAws().getInstanceTypes().split(",");
                instanceNo = instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0]);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if ("vmware".equals(platformDto.getPlatform().getPlatformType())) {
            // VMwareサーバを作成（ロジックを実行）
            try {
                String[] instanceTypes = imageDto.getImageVmware().getInstanceTypes().split(",");
                instanceNo = instanceService.createVmwareInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0]);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if ("nifty".equals(platformDto.getPlatform().getPlatformType())) {
            // Niftyサーバを作成（ロジックを実行）
            try {
                String[] instanceTypes = imageDto.getImageNifty().getInstanceTypes().split(",");
                instanceNo = instanceService.createNiftyInstance(farmNo, serverName, platformNo, comment, imageNo,
                        instanceTypes[0]);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        } else if ("cloudstack".equals(platformDto.getPlatform().getPlatformType())) {
            // CloudStackサーバを作成（ロジックを実行）
            try {
                String[] instanceTypes = imageDto.getImageCloudstack().getInstanceTypes().split(",");
                instanceNo = instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageNo, instanceTypes[0]);
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        //TODO LOG
        AutoApplication aapl =  (AutoApplication)apl;
        aapl.doOpLog("SERVER", "Make Server", instanceNo, null, null, null);

        // サーバにサービスを関連付ける
        if (componentNos != null && attachService) {
            instanceService.associateComponents(instanceNo, componentNos);
        }

        // 画面を閉じる
        close();
    }

}
