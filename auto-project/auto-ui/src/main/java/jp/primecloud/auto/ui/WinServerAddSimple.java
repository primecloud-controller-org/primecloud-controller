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
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

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

    private final String COLUMN_HEIGHT = "28px";

    private final int MAX_ADD_SERVER = 10;

    private ComponentType componentType;

    private ServerAddForm serverAddForm;

    private List<PlatformDto> platforms;

    public WinServerAddSimple(ComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public void attach() {
        // モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winServerAddSimple"));
        setModal(true);
        setWidth("450px");
        setResizable(false);

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true, true, false, true);
        layout.setSpacing(false);

        // フォーム
        serverAddForm = new ServerAddForm();
        layout.addComponent(serverAddForm);

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.add"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
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
        serverAddForm.show(platforms, componentType);
    }

    private class ServerAddForm extends Form {

        private TextField prefixField;

        private SelectCloudTable cloudTable;

        private ComboBox serverNumber;

        @Override
        public void attach() {
            // サーバ名(Prefix)
            prefixField = new TextField(ViewProperties.getCaption("field.serverNamePrefix"));
            getLayout().addComponent(prefixField);

            // プラットフォーム
            cloudTable = new SelectCloudTable();
            getLayout().addComponent(cloudTable);

            // サーバ台数
            serverNumber = new ComboBox(ViewProperties.getCaption("field.serverNumber"));
            serverNumber.setWidth("110px");
            serverNumber.setMultiSelect(false);
            for (int i = 1; i <= MAX_ADD_SERVER; i++) {
                serverNumber.addItem(i);
            }
            serverNumber.setNullSelectionAllowed(false);
            serverNumber.setValue(1); // 初期値は1
            getLayout().addComponent(serverNumber);

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000025");
            prefixField.setRequired(true);
            prefixField.setRequiredError(message);
            prefixField.addValidator(new StringLengthValidator(message, 1, 10, false));
            prefixField.addValidator(new RegexpValidator("^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", true, message));

            Validator serverNumberValidator = new Validator() {
                @Override
                public boolean isValid(Object value) {
                    if (value == null || !(value instanceof Integer)) {
                        return false;
                    } else {
                        return ((Integer) value >= 1 && (Integer) value <= MAX_ADD_SERVER);
                    }
                }

                @Override
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

        public void show(List<PlatformDto> platforms, ComponentType componentType) {
            // サーバ名(Prefix)
            String prefix = componentType.getLayer();
            if (prefix.indexOf('_') != -1) {
                prefix = prefix.substring(0, prefix.indexOf('_'));
            }
            prefixField.setValue(prefix);

            // プラットフォーム
            cloudTable.show(platforms, componentType);
            cloudTable.selectFirst();
        }

    }

    private class SelectCloudTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
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
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<PlatformDto> platforms, ComponentType componentType) {
            removeAllItems();

            if (platforms == null) {
                return;
            }

            Long componentTypeNo = componentType.getComponentTypeNo();

            int index = 1;
            for (PlatformDto platform : platforms) {
                // サービス種別を利用可能かチェック
                boolean available = false;
                for (ImageDto tmpImage : platform.getImages()) {
                    for (ComponentType tmpComponentType : tmpImage.getComponentTypes()) {
                        if (componentTypeNo.equals(tmpComponentType.getComponentTypeNo())) {
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

                // プラットフォーム名
                Icons icon = IconUtils.getPlatformIcon(platform);
                String description = platform.getPlatform().getPlatformNameDisp();
                Label label = new Label(IconUtils.createImageTag(getApplication(), icon, description),
                        Label.CONTENT_XHTML);
                label.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { index, label }, platform.getPlatform().getPlatformNo());
                index++;
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

    private void addButtonClick(ClickEvent event) {
        // 入力値を取得
        String prefix = (String) serverAddForm.prefixField.getValue();
        String serverNumber = String.valueOf(serverAddForm.serverNumber.getValue());
        final Long platformNo = serverAddForm.cloudTable.getValue();

        // 入力チェック
        try {
            serverAddForm.prefixField.validate();
            serverAddForm.serverNumber.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (platformNo == null) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000023"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (prefix.startsWith("lb-")) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000083", prefix));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 作成するサーバ名
        final List<String> serverNames = createServerNames(prefix, Integer.parseInt(serverNumber));

        // 確認ダイアログの表示
        String message;
        if (serverNames.size() > 1) {
            message = ViewMessages.getMessage("IUI-000043", serverNames.get(0),
                    serverNames.get(serverNames.size() - 1), serverNames.size());
        } else {
            message = ViewMessages.getMessage("IUI-000042", serverNames.get(0), serverNames.size());
        }
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                } else {
                    addOkClick(platformNo, serverNames);
                }

            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private List<String> createServerNames(String prefix, int serverNumber) {
        // サーバ情報を取得
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        List<InstanceDto> instances = instanceService.getInstances(ViewContext.getFarmNo());

        // prefixと数字から成るサーバ名のうち、最大の数字を取得
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

        // 最大の数字より大きい数字のサーバ名を作成
        List<String> serverNames = new ArrayList<String>();
        for (int i = 1; i <= serverNumber; i++) {
            serverNames.add(prefix + (max + i));
        }

        return serverNames;
    }

    private void addOkClick(Long platformNo, List<String> serverNames) {
        // 選択されたプラットフォームの中で、サービス種類を利用可能なイメージを取得
        ImageDto image = null;
        Long componentTypeNo = componentType.getComponentTypeNo();
        PlatformDto platform = findPlatform(platformNo);
        for (ImageDto tmpImage : platform.getImages()) {
            for (ComponentType tmpComponentType : tmpImage.getComponentTypes()) {
                if (componentTypeNo.equals(tmpComponentType.getComponentTypeNo())) {
                    image = tmpImage;
                    break;
                }
            }
            if (image != null) {
                break;
            }
        }

        // サーバを作成
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);
        Long farmNo = ViewContext.getFarmNo();
        String comment = "";
        for (String serverName : serverNames) {
            try {
                // AWSサーバを作成
                if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())) {
                    String[] instanceTypes = image.getImageAws().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, image.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                }
                // VMwareサーバを作成
                else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatform().getPlatformType())) {
                    String[] instanceTypes = image.getImageVmware().getInstanceTypes().split(",");
                    instanceService.createVmwareInstance(farmNo, serverName, platformNo, comment, image.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                }
                // Niftyサーバを作成
                else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatform().getPlatformType())) {
                    String[] instanceTypes = image.getImageNifty().getInstanceTypes().split(",");
                    instanceService.createNiftyInstance(farmNo, serverName, platformNo, comment, image.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                }
                // CloudStackサーバを作成
                else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatform().getPlatformType())) {
                    String[] instanceTypes = image.getImageCloudstack().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, image.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                }
                // VCloudサーバを作成
                else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatform().getPlatformType())) {
                    String[] instanceTypes = image.getImageVcloud().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, image.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                }
                // Azureサーバを作成
                else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
                    String[] instanceTypes = image.getImageAzure().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, image.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                }
                // OpenStackサーバを作成
                else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatform().getPlatformType())) {
                    String[] instanceTypes = image.getImageOpenstack().getInstanceTypes().split(",");
                    instanceService.createIaasInstance(farmNo, serverName, platformNo, comment, image.getImage()
                            .getImageNo(), instanceTypes[0].trim());
                }
            } catch (AutoApplicationException e) {
                String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // オペレーションログ
        AutoApplication aapl = (AutoApplication) getApplication();
        String prefix = (String) serverAddForm.prefixField.getValue();
        String serverNumber = String.valueOf(serverAddForm.serverNumber.getValue());
        aapl.doOpLog("SIMPLE_SERVER", "Make Server Simple", null, null, null, prefix + ":" + serverNumber);

        // 作成したサーバ名をセッションに格納
        ContextUtils.setAttribute("serverNames", serverNames);

        // 画面を閉じる
        close();
    }

}
