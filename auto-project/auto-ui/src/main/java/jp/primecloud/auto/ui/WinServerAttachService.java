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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.AzureDisk;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.OpenstackVolume;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * サーバ作成(編集)画面からサービスの割当ボタンを押したときのサービス割当画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinServerAttachService extends Window {

    private InstanceDto instance;

    private ImageDto image;

    private List<Long> selectedComponentNos;

    private AttachService attachService;

    private List<ComponentDto> components;

    public WinServerAttachService(InstanceDto instance, ImageDto image, List<Long> selectedComponentNos) {
        this.instance = instance;
        this.image = image;
        this.selectedComponentNos = selectedComponentNos;
    }

    @Override
    public void attach() {
        // モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winServerAttachService"));
        setModal(true);
        setWidth("500px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true, true, false, true);
        layout.setSpacing(false);

        // Form
        Form form = new Form();

        // サーバ名
        TextField serverNameField = new TextField(ViewProperties.getCaption("field.serverName"));
        if (instance != null) {
            serverNameField.setValue(instance.getInstance().getInstanceName());
        } else {
            serverNameField.setValue(ViewProperties.getCaption("field.newServer"));
        }
        serverNameField.setReadOnly(true);
        form.getLayout().addComponent(serverNameField);

        attachService = new AttachService(instance, image, selectedComponentNos);
        form.getLayout().addComponent(attachService);
        layout.addComponent(form);

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.ok"));
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
        okButton.setClickShortcut(KeyCode.ENTER); // [Enter]でokButtonクリック
        okButton.focus();
        bottomLayout.addComponent(okButton);

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

        // サービス情報を表示
        loadData();
        attachService.show(components);
    }

    private class AttachService extends Table {

        private final String COLUMN_HEIGHT = "32px";

        private InstanceDto instance;

        private ImageDto image;

        private List<Long> selectedComponentNos;

        private List<ComponentDto> components;

        public AttachService(InstanceDto instance, ImageDto image, List<Long> selectedComponentNos) {
            this.instance = instance;
            this.image = image;
            this.selectedComponentNos = (selectedComponentNos == null) ? new ArrayList<Long>() : selectedComponentNos;
        }

        @Override
        public void attach() {
            // テーブル基本設定
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("win-server-attach-service");
            setCaption(ViewProperties.getCaption("table.serverAttachServices"));
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(false);
            setVisible(true);
            setWidth("100%");
            setPageLength(5);

            // カラム設定
            addContainerProperty("check", CheckBox.class, new CheckBox());
            addContainerProperty("componentName", Label.class, new Label());
            addContainerProperty("status", Label.class, new Label());
            setColumnExpandRatio("componentName", 100);
            String[] headers = { "", ViewProperties.getCaption("field.serviceName"),
                    ViewProperties.getCaption("field.serviceStatus") };
            setColumnHeaders(headers);

            // テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    String style = super.getStyle(itemId, propertyId);

                    CheckBox checkBox = getCheckBox(getItem(itemId));

                    // チェックボックスが選択されていれば行の色を変える
                    if (checkBox.booleanValue()) {
                        style += " v-selected";
                    }
                    // チェックボックスが無効なら色を変更する
                    if (!checkBox.isEnabled()) {
                        style += " v-disabled";
                    }

                    return style;
                }
            });

            // 行が選択された場合、行のチェックボックスの値を変える
            addListener(new ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    CheckBox checkBox = getCheckBox(event.getItem());
                    if (checkBox.isEnabled()) {
                        checkBox.setValue(!checkBox.booleanValue());
                    }
                }
            });
        }

        public void show(List<ComponentDto> components) {
            this.components = components;

            removeAllItems();

            if (components == null) {
                return;
            }

            for (ComponentDto component : components) {
                // チェックボックス
                CheckBox checkBox = new CheckBox();
                checkBox.setImmediate(true);
                checkBox.setEnabled(false);
                if (selectedComponentNos.contains(component.getComponent().getComponentNo())) {
                    checkBox.setValue(true);
                } else {
                    checkBox.setValue(false);
                }

                checkBox.addListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        // チェックボックスの有効／無効を制御
                        changeCheckEnabled();

                        // テーブル再描画
                        requestRepaint();
                    }
                });

                // サービス名
                String serviceName = component.getComponent().getComponentName();
                if (StringUtils.isNotEmpty(component.getComponent().getComment())) {
                    serviceName = component.getComponent().getComment() + "\n[" + serviceName + "]";
                }
                Label serviceNameLabel = new Label(serviceName, Label.CONTENT_PREFORMATTED);
                serviceNameLabel.setHeight(COLUMN_HEIGHT);

                // ステータス
                String status = null;
                if (instance != null) {
                    for (ComponentInstanceDto componentInstance : instance.getComponentInstances()) {
                        if (componentInstance.getComponentInstance().getComponentNo()
                                .equals(component.getComponent().getComponentNo())) {
                            status = componentInstance.getComponentInstance().getStatus();
                            break;
                        }
                    }
                }
                if (StringUtils.isEmpty(status)) {
                    status = "Stopped";
                } else {
                    status = StringUtils.capitalize(StringUtils.lowerCase(status));
                }

                Icons statusIcon = Icons.fromName(status);
                Label statusLabel = new Label(IconUtils.createImageTag(getApplication(), statusIcon, status),
                        Label.CONTENT_XHTML);
                statusLabel.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { checkBox, serviceNameLabel, statusLabel }, component.getComponent()
                        .getComponentNo());
            }

            changeCheckEnabled();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<Long> getItemIds() {
            return (Collection<Long>) super.getItemIds();
        }

        public List<Long> getSelectedValues() {
            List<Long> componentNos = new ArrayList<Long>();
            for (Long componentNo : getItemIds()) {
                CheckBox checkBox = getCheckBox(getItem(componentNo));
                if (checkBox.booleanValue()) {
                    componentNos.add(componentNo);
                }
            }

            return componentNos;
        }

        private CheckBox getCheckBox(Item item) {
            return (CheckBox) item.getItemProperty("check").getValue();
        }

        private void changeCheckEnabled() {
            // チェックボックスの有効／無効を制御する
            Set<Long> disableComponentNos = new HashSet<Long>();

            // 利用可能でないサービスは無効
            List<Long> componentTypeNos = new ArrayList<Long>();
            for (ComponentType componentType : image.getComponentTypes()) {
                componentTypeNos.add(componentType.getComponentTypeNo());
            }
            for (ComponentDto component : components) {
                if (!componentTypeNos.contains(component.getComponent().getComponentTypeNo())) {
                    disableComponentNos.add(component.getComponent().getComponentNo());
                }
            }

            if (instance != null) {
                // 割り当て済みでStoppedでないサービスは無効
                for (ComponentInstanceDto componentInstance : instance.getComponentInstances()) {
                    for (ComponentDto component : components) {
                        if (componentInstance.getComponentInstance().getComponentNo()
                                .equals(component.getComponent().getComponentNo())) {
                            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance
                                    .getComponentInstance().getStatus());
                            if (status != ComponentInstanceStatus.STOPPED) {
                                disableComponentNos.add(component.getComponent().getComponentNo());
                            }
                            break;
                        }
                    }
                }

                // ディスクがアタッチされているサービスは無効
                if (instance.getAwsVolumes() != null) {
                    for (AwsVolume awsVolume : instance.getAwsVolumes()) {
                        if (StringUtils.isNotEmpty(awsVolume.getInstanceId())) {
                            disableComponentNos.add(awsVolume.getComponentNo());
                        }
                    }
                }
                if (instance.getCloudstackVolumes() != null) {
                    for (CloudstackVolume cloudstackVolume : instance.getCloudstackVolumes()) {
                        if (StringUtils.isNotEmpty(cloudstackVolume.getInstanceId())) {
                            disableComponentNos.add(cloudstackVolume.getComponentNo());
                        }
                    }
                }
                if (instance.getVmwareDisks() != null) {
                    for (VmwareDisk vmwareDisk : instance.getVmwareDisks()) {
                        if (BooleanUtils.isTrue(vmwareDisk.getAttached())) {
                            disableComponentNos.add(vmwareDisk.getComponentNo());
                        }
                    }
                }
                if (instance.getVcloudDisks() != null) {
                    for (VcloudDisk vcloudDisk : instance.getVcloudDisks()) {
                        if (BooleanUtils.isTrue(vcloudDisk.getAttached())) {
                            if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                disableComponentNos.add(vcloudDisk.getComponentNo());
                            }
                        }
                    }
                }
                if (instance.getAzureDisks() != null) {
                    for (AzureDisk azureDisk : instance.getAzureDisks()) {
                        if (StringUtils.isNotEmpty(azureDisk.getInstanceName())) {
                            if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                disableComponentNos.add(azureDisk.getComponentNo());
                            }
                        }
                    }
                }
                if (instance.getOpenstackVolumes() != null) {
                    for (OpenstackVolume openstackVolume : instance.getOpenstackVolumes()) {
                        if (StringUtils.isNotEmpty(openstackVolume.getInstanceId())) {
                            if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                disableComponentNos.add(openstackVolume.getComponentNo());
                            }
                        }
                    }
                }
            }

            // 同一レイヤのサービス種類が選択されているサービスは無効
            for (ComponentDto component : components) {
                CheckBox checkBox = getCheckBox(getItem(component.getComponent().getComponentNo()));
                if (checkBox.booleanValue()) {
                    for (ComponentDto component2 : components) {
                        if (component.getComponent().getComponentNo()
                                .equals(component2.getComponent().getComponentNo())) {
                            continue;
                        }

                        if (component.getComponentType().getLayer().equals(component2.getComponentType().getLayer())) {
                            disableComponentNos.add(component2.getComponent().getComponentNo());
                        }
                    }
                }
            }

            // 有効／無効を反映
            for (Long componentNo : getItemIds()) {
                CheckBox checkBox = getCheckBox(getItem(componentNo));
                if (disableComponentNos.contains(componentNo)) {
                    checkBox.setEnabled(false);
                } else {
                    checkBox.setEnabled(true);
                }
            }
        }
    }

    private void loadData() {
        // 全てのサービス情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        components = componentService.getComponents(ViewContext.getFarmNo());
    }

    private void addButtonClick(ClickEvent event) {
        // 選択したサービスの番号を取得
        List<Long> componentNos = attachService.getSelectedValues();

        // 選択したサービス追加したサービスの番号をセッションに格納
        ContextUtils.setAttribute("componentNos", componentNos);

        // 画面を閉じる
        close();
    }

}
