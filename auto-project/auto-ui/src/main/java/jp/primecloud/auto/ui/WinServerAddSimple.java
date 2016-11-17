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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ComponentTypeDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.CommonUtils;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.Application;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * サービス追加画面から新規サーバ作成ボタンを押した際の新規サーバをサービスに追加画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinServerAddSimple extends Window {

    final String COLUMN_HEIGHT = "28px";

    final int MAX_ADD_SERVER = 10;

    Application apl;

    Long componentTypeNo;

    TextField prefixField;

    Table cloudTable;

    ComboBox serverNumber;

    List<PlatformDto> platforms;

    WinServerAddSimple(Application ap, Long componentTypeNo) {
        apl = ap;
        this.componentTypeNo = componentTypeNo;

        //モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winServerAddSimple"));
        setModal(true);
        setWidth("450px");
        setResizable(false);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true, true, false, true);
        layout.setSpacing(false);

        // フォーム
        layout.addComponent(new ServerAddForm());

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.add"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final List<String> serverNames = getServerNames();
                DialogConfirm dialog = null;
                String message = "";
                if (serverNames == null) {
                    return;
                } else if (serverNames.size() > 1) {
                    message = ViewMessages.getMessage("IUI-000043", serverNames.get(0),
                            serverNames.get(serverNames.size() - 1), serverNames.size());
                } else {
                    message = ViewMessages.getMessage("IUI-000042", serverNames.get(0), serverNames.size());
                }
                dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
                dialog.setCallback(new DialogConfirm.Callback() {
                    @Override
                    public void onDialogResult(Result result) {
                        if (result != Result.OK) {
                            return;
                        } else {
                            addButtonClick(serverNames);
                        }

                    }
                });
                getApplication().getMainWindow().addWindow(dialog);
            }
        });
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

    private class ServerAddForm extends Form {

        ServerAddForm() {
            //サーバ名 Prefix
            prefixField = new TextField(ViewProperties.getCaption("field.serverNamePrefix"));
            getLayout().addComponent(prefixField);

            //クラウド選択
            cloudTable = new SelectCloudTable();
            getLayout().addComponent(cloudTable);

            //サーバ台数選択
            serverNumber = new ComboBox(ViewProperties.getCaption("field.serverNumber"));
            serverNumber.setWidth("110px");
            serverNumber.setMultiSelect(false);
            //アイテムの追加
            for (int i = 1; i <= MAX_ADD_SERVER; i++) {
                serverNumber.addItem(i);
            }
            serverNumber.setNullSelectionAllowed(false);

            getLayout().addComponent(serverNumber);
        }

    }

    private class SelectCloudTable extends Table {

        SelectCloudTable() {
            //テーブル基本設定
            setCaption(ViewProperties.getCaption("table.selectCloud"));
            setWidth("260px");
            setPageLength(6);
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
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    if (propertyId == null) {
                        return "";
                    } else {
                        return propertyId.toString().toLowerCase();
                    }
                }
            });
        }

    }

    private void initValidation() {
        String message = ViewMessages.getMessage("IUI-000025");
        prefixField.setRequired(true);
        prefixField.setRequiredError(message);
        prefixField.addValidator(new StringLengthValidator(message, 1, 10, false));
        prefixField.addValidator(new RegexpValidator("^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", true, message));

        Validator serverNumberValidator = new Validator() {
            public boolean isValid(Object value) {
                if (value == null || !(value instanceof Integer)) {
                    return false;
                } else {
                    return ((Integer) value >= 1 && (Integer) value <= MAX_ADD_SERVER);
                }
            }

            public void validate(Object value) throws InvalidValueException {
                String message = ViewMessages.getMessage("IUI-000026");
                if (!isValid(value)) {
                    throw new InvalidValueException(message);
                }
            }
        };

        serverNumber.setRequired(true);
        serverNumber.addValidator(serverNumberValidator);
    }

    private void initData() {
        // ユーザ番号
        Long userNo = ViewContext.getUserNo();

        // クラウド情報を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        platforms = instanceService.getPlatforms(userNo);

        // クラウド情報をソート
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

        // コンポーネント情報を取得
        ComponentType componentType = null;
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        List<ComponentTypeDto> componentTypeDtos = componentService.getComponentTypes(ViewContext.getFarmNo());
        for (ComponentTypeDto componentTypeDto : componentTypeDtos) {
            if (componentTypeDto.getComponentType().getComponentTypeNo().equals(componentTypeNo)) {
                componentType = componentTypeDto.getComponentType();
            }
        }

        // サーバ名Prefixを設定
        String prefix = componentType.getLayer();
        if (prefix.indexOf('_') != -1) {
            prefix = prefix.substring(0, prefix.indexOf('_'));
        }
        prefixField.setValue(prefix);

        serverNumber.setValue(1);
    }

    private void showClouds() {
        cloudTable.removeAllItems();

        // クラウド情報をテーブルに追加
        for (int i = 0; i < platforms.size(); i++) {
            PlatformDto platform = platforms.get(i);

            if (BooleanUtils.isNotTrue(platform.getPlatform().getSelectable())) {
                //使用不可プラットフォームの場合、非表示
                continue;
            }

            // 選択されたcomponetTypeNoを利用可能かチェック
            boolean available = false;
            for (ImageDto image : platform.getImages()) {
                // 選択可能でないイメージの場合はスキップ
                if (BooleanUtils.isNotTrue(image.getImage().getSelectable())) {
                    continue;
                }
                for (ComponentType componentType : image.getComponentTypes()) {
                    if (componentTypeNo.equals(componentType.getComponentTypeNo())) {
                        available = true;
                        break;
                    }
                }
                if (available) {
                    break;
                }
            }
            if (!available) {
                continue;
            }

            //プラットフォームアイコン名の取得
            Icons icon = CommonUtils.getPlatformIcon(platform);

            String description = platform.getPlatform().getPlatformNameDisp();

            Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, icon) + "\"><div>" + description
                    + "</div>", Label.CONTENT_XHTML);
            slbl.setHeight(COLUMN_HEIGHT);

            cloudTable.addItem(new Object[] { (i + 1), slbl }, platform.getPlatform().getPlatformNo());
        }

        Long platformNo = null;
        if (cloudTable.getItemIds().size() > 0) {
            platformNo = (Long) cloudTable.getItemIds().toArray()[0];
        }

        // 先頭のクラウド情報を選択する
        cloudTable.select(platformNo);
    }

    private List<String> getServerNames() {
        // 入力値を取得
        String prefix = (String) prefixField.getValue();
        String serverNumber = String.valueOf(this.serverNumber.getValue());
        Long platformNo = (Long) cloudTable.getValue();

        // TODO: 入力チェック
        try {
            prefixField.validate();
            this.serverNumber.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return null;
        }
        if (platformNo == null) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000023"));
            getApplication().getMainWindow().addWindow(dialog);
            return null;
        }
        if (prefix.startsWith("lb-")) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000083", prefix));
            getApplication().getMainWindow().addWindow(dialog);
            return null;
        }

        // クラウド番号
        Long farmNo = ViewContext.getFarmNo();

        InstanceService instanceService = BeanContext.getBean(InstanceService.class);

        // 作成するサーバ名
        ArrayList<String> serverNames = new ArrayList<String>();
        List<InstanceDto> instances = instanceService.getInstances(farmNo);
        int max = 0;
        for (InstanceDto instance : instances) {
            String instanceName = instance.getInstance().getInstanceName();
            if (instanceName.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(instanceName.substring(prefix.length()));
                    if (number > max) {
                        max = number;
                    }
                } catch (NumberFormatException ignore) {
                }
            }
        }
        for (int i = 1; i <= Integer.parseInt(serverNumber); i++) {
            serverNames.add(prefix + (max + i));
        }
        return serverNames;
    }

    private void addButtonClick(List<String> serverNames) {
        // 入力値を取得
        Long platformNo = (Long) cloudTable.getValue();
        Long farmNo = ViewContext.getFarmNo();

        // TODO: サーバコメント
        String comment = "";
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);

        // サーバ種類を取得
        PlatformDto platformDto = null;
        ImageDto imageDto = null;
        for (PlatformDto platform : platforms) {
            if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                platformDto = platform;
                for (ImageDto tmpImage : platform.getImages()) {
                    // 選択可能でないイメージの場合はスキップ
                    if (!tmpImage.getImage().getSelectable()) {
                        continue;
                    }
                    for (ComponentType tmpComponentType : tmpImage.getComponentTypes()) {
                        if (componentTypeNo.equals(tmpComponentType.getComponentTypeNo())) {
                            imageDto = tmpImage;
                            break;
                        }
                    }
                    if (imageDto != null) {
                        break;
                    }
                }
                break;
            }
        }

        for (String serverName : serverNames) {
            // プラットフォーム
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformDto.getPlatform().getPlatformType())) {
                // AWSサーバを作成（ロジックを実行）
                try {
                    String[] instanceTypes = imageDto.getImageAws().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageDto.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platformDto.getPlatform().getPlatformType())) {
                // VMwareサーバを作成（ロジックを実行）
                try {
                    String[] instanceTypes = imageDto.getImageVmware().getInstanceTypes().split(",");
                    instanceService.createVmwareInstance(farmNo, serverName, platformNo, comment, imageDto.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platformDto.getPlatform().getPlatformType())) {
                // Niftyサーバを作成（ロジックを実行）
                try {
                    String[] instanceTypes = imageDto.getImageNifty().getInstanceTypes().split(",");
                    instanceService.createNiftyInstance(farmNo, serverName, platformNo, comment, imageDto.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platformDto.getPlatform().getPlatformType())) {
                // CloudStackサーバを作成（ロジックを実行）
                try {
                    String[] instanceTypes = imageDto.getImageCloudstack().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageDto.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platformDto.getPlatform().getPlatformType())) {
                // VCloudサーバを作成（ロジックを実行）
                try {
                    String[] instanceTypes = imageDto.getImageVcloud().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageDto.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platformDto.getPlatform().getPlatformType())) {
                // Azureサーバを作成（ロジックを実行）
                try {
                    String[] instanceTypes = imageDto.getImageAzure().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageDto.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platformDto.getPlatform().getPlatformType())) {
                // OpenStackサーバを作成（ロジックを実行）暫定実装
                try {
                    String[] instanceTypes = imageDto.getImageOpenstack().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, imageDto.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }
        }

        //オペレーションログ
        AutoApplication aapl = (AutoApplication) apl;
        String prefix = (String) prefixField.getValue();
        String serverNumber = String.valueOf(this.serverNumber.getValue());
        aapl.doOpLog("SIMPLE_SERVER", "Make Server Simple", null, null, null, prefix + ":" + serverNumber);

        // 作成したサーバ名をセッションに格納
        ContextUtils.setAttribute("serverNames", serverNames);

        // 画面を閉じる
        close();
    }

}
