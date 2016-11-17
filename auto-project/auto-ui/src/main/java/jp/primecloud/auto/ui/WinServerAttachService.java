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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import jp.primecloud.auto.ui.data.ComponentDtoContainer;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
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

    AutoApplication apl;

    AttachService attachService;

    InstanceDto instance;

    ImageDto image;

    List<ComponentDto> components;

    List<Long> componentNos;

    WinServerAttachService(Application ap, InstanceDto instance, ImageDto image, List<Long> componentNos) {
        this.apl = (AutoApplication) ap;
        this.instance = instance;
        this.image = image;
        this.componentNos = componentNos;

        //モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winServerAttachService"));
        setModal(true);
        setWidth("500px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true, true, false, true);
        layout.setSpacing(false);

        //Form
        Form form = new Form();

        //サーバ名
        TextField serverNameField = new TextField(ViewProperties.getCaption("field.serverName"));
        if (instance != null) {
            serverNameField.setValue(instance.getInstance().getInstanceName());
        } else {
            serverNameField.setValue(ViewProperties.getCaption("field.newServer"));
        }
        serverNameField.setReadOnly(true);
        form.getLayout().addComponent(serverNameField);

        attachService = new AttachService("", null);
        form.getLayout().addComponent(attachService);
        layout.addComponent(form);

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.ok"));
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
        // [Enter]でokButtonクリック
        okButton.setClickShortcut(KeyCode.ENTER);
        okButton.focus();
        okbar.addComponent(okButton);

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

        // データの表示
        showData();
    }

    class AttachService extends Table {
        final String COLUMN_HEIGHT = "32px";

        String[] COLNAME = { "", ViewProperties.getCaption("field.serviceName"),
                ViewProperties.getCaption("field.serviceStatus"), };

        String[] VISIBLE_COLNAME = { "check", "componentName", "status" };

        HashMap<Long, CheckBox> checkList = new HashMap<Long, CheckBox>();

        public AttachService(String caption, Container dataSource) {
            super(caption, dataSource);

            addGeneratedColumn("check", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    ComponentDto p = (ComponentDto) itemId;
                    CheckBox check = new CheckBox();
                    check.setImmediate(true);
                    check.setValue(false);
                    check.setEnabled(false);

                    // 選択済みならチェック
                    if (componentNos != null) {
                        if (componentNos.contains(p.getComponent().getComponentNo())) {
                            check.setValue(true);
                        }
                    }

                    checkList.put(p.getComponent().getComponentNo(), check);

                    check.addListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent event) {
                            // チェックボックスの有効／無効を制御
                            changeCheckEnabled();

                            //テーブル再描画
                            requestRepaint();
                        }
                    });

                    // チェックボックスの有効／無効を制御
                    // （本来はすべての行についてaddGeneratedColumnが終わった後に１度だけするべき）
                    changeCheckEnabled();

                    return check;
                }
            });

            addGeneratedColumn("componentName", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    ComponentDto p = (ComponentDto) itemId;

                    String name;
                    if (StringUtils.isEmpty(p.getComponent().getComment())) {
                        name = p.getComponent().getComponentName();
                    } else {
                        name = p.getComponent().getComment() + "\n[" + p.getComponent().getComponentName() + "]";
                    }
                    Label slbl = new Label(name, Label.CONTENT_PREFORMATTED);
                    slbl.setHeight(COLUMN_HEIGHT);
                    return slbl;
                }
            });

            addGeneratedColumn("status", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    ComponentDto p = (ComponentDto) itemId;

                    ComponentInstanceDto ci = null;
                    //String status = "";
                    if (instance != null) {
                        for (ComponentInstanceDto componentInstance : instance.getComponentInstances()) {
                            if (componentInstance.getComponentInstance().getComponentNo()
                                    .equals(p.getComponent().getComponentNo())) {
                                ci = componentInstance;
                                break;
                            }
                        }
                    }
                    String status = null;
                    if (ci != null) {
                        status = StringUtils.capitalize(StringUtils.lowerCase(ci.getComponentInstance().getStatus()));
                    }
                    if (StringUtils.isEmpty(status)) {
                        status = "Stopped";
                    }

                    Icons icon = Icons.fromName(status);
                    Label slbl = new Label(IconUtils.createImageTag(apl, icon, status), Label.CONTENT_XHTML);
                    slbl.setHeight(COLUMN_HEIGHT);
                    return slbl;
                }
            });

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    ComponentDto p = (ComponentDto) itemId;

                    if (propertyId == null) {
                        return "";
                    }

                    String ret = propertyId.toString().toLowerCase();

                    if (checkList.containsKey(p.getComponent().getComponentNo())) {
                        //checkboxが選択されていれば行の色を変える
                        if ((Boolean) checkList.get(p.getComponent().getComponentNo()).getValue()) {
                            ret += " v-selected";
                        }
                        //checkboxが無効なら色を変更する
                        if (!(Boolean) checkList.get(p.getComponent().getComponentNo()).isEnabled()) {
                            ret += " v-disabled";
                        }
                    }
                    return ret;
                }
            });

            addListener(new ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    ComponentDto p = (ComponentDto) event.getItemId();
                    CheckBox chk = checkList.get(p.getComponent().getComponentNo());
                    if (chk.isEnabled()) {
                        chk.setValue(!chk.booleanValue());
                    }
                }
            });

            setColumnExpandRatio("componentName", 100);
        }

        public void changeCheckEnabled() {
            // チェックボックスの有効／無効を制御する
            Map<Long, Boolean> checkMap = new HashMap<Long, Boolean>();

            Map<Long, ComponentDto> componentMap = new HashMap<Long, ComponentDto>();
            for (ComponentDto component : components) {
                checkMap.put(component.getComponent().getComponentNo(), true);
                componentMap.put(component.getComponent().getComponentNo(), component);
            }

            // 利用可能でないサービスは無効
            List<Long> componentTypeNos = new ArrayList<Long>();
            for (ComponentType componentType : image.getComponentTypes()) {
                componentTypeNos.add(Long.valueOf(componentType.getComponentTypeNo()));
            }
            for (ComponentDto component : components) {
                if (!componentTypeNos.contains(component.getComponent().getComponentTypeNo())) {
                    checkMap.put(component.getComponent().getComponentNo(), false);
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
                                checkMap.put(component.getComponent().getComponentNo(), false);
                            }
                            break;
                        }
                    }
                }

                // ディスクがアタッチされているサービスは無効
                if (instance.getAwsVolumes() != null) {
                    for (AwsVolume awsVolume : instance.getAwsVolumes()) {
                        if (StringUtils.isNotEmpty(awsVolume.getInstanceId())) {
                            checkMap.put(awsVolume.getComponentNo(), false);
                        }
                    }
                }
                if (instance.getCloudstackVolumes() != null) {
                    for (CloudstackVolume cloudstackVolume : instance.getCloudstackVolumes()) {
                        if (StringUtils.isNotEmpty(cloudstackVolume.getInstanceId())) {
                            checkMap.put(cloudstackVolume.getComponentNo(), false);
                        }
                    }
                }
                if (instance.getVmwareDisks() != null) {
                    for (VmwareDisk vmwareDisk : instance.getVmwareDisks()) {
                        if (BooleanUtils.isTrue(vmwareDisk.getAttached())) {
                            checkMap.put(vmwareDisk.getComponentNo(), false);
                        }
                    }
                }
                if (instance.getVcloudDisks() != null) {
                    for (VcloudDisk vcloudDisk : instance.getVcloudDisks()) {
                        if (BooleanUtils.isTrue(vcloudDisk.getAttached())) {
                            if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                checkMap.put(vcloudDisk.getComponentNo(), false);
                            }
                        }
                    }
                }
                if (instance.getAzureDisks() != null) {
                    for (AzureDisk azureDisk : instance.getAzureDisks()) {
                        if (StringUtils.isNotEmpty(azureDisk.getInstanceName())) {
                            if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                checkMap.put(azureDisk.getComponentNo(), false);
                            }
                        }
                    }
                }
                if (instance.getOpenstackVolumes() != null) {
                    for (OpenstackVolume openstackVolume : instance.getOpenstackVolumes()) {
                        if (StringUtils.isNotEmpty(openstackVolume.getInstanceId())) {
                            if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                checkMap.put(openstackVolume.getComponentNo(), false);
                            }
                        }
                    }
                }
            }

            // 同一レイヤのサービス種類が選択されているサービスは無効
            for (Map.Entry<Long, CheckBox> entry : attachService.checkList.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    ComponentType componentType = componentMap.get(entry.getKey()).getComponentType();
                    for (ComponentDto component : components) {
                        if (entry.getKey().equals(component.getComponent().getComponentNo())) {
                            continue;
                        }
                        ComponentType componentType2 = component.getComponentType();
                        if (componentType.getLayer().equals(componentType2.getLayer())) {
                            checkMap.put(component.getComponent().getComponentNo(), false);
                        }
                    }
                }
            }

            // 有効／無効を反映
            for (Map.Entry<Long, CheckBox> entry : attachService.checkList.entrySet()) {
                entry.getValue().setEnabled(checkMap.get(entry.getKey()));
            }
        }

        @Override
        public void setContainerDataSource(Container newDataSource) {
            super.setContainerDataSource(newDataSource);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("win-server-attach-service");
            setCaption(ViewProperties.getCaption("table.serverAttachServices"));
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(false);
            setVisible(true);
            setWidth("100%");
            setPageLength(5);
        }

        public void refresh(ComponentDtoContainer dataSource) {
            setContainerDataSource(dataSource);
            setVisibleColumns(VISIBLE_COLNAME);
            setColumnHeaders(COLNAME);
        }

    }

    private void initData() {
        // 全てのサービス情報を取得
        Long farmNo = ViewContext.getFarmNo();
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        components = componentService.getComponents(farmNo);
    }

    private void showData() {
        // テーブルにサービス情報を表示
        attachService.refresh(new ComponentDtoContainer(components));
    }

    private void addButtonClick(ClickEvent event) {
        // 選択したサービスの番号を取得
        List<Long> componentNos = new ArrayList<Long>();
        for (Map.Entry<Long, CheckBox> entry : attachService.checkList.entrySet()) {
            if (entry.getValue().booleanValue()) {
                componentNos.add(entry.getKey());
            }
        }

        // 選択したサービス追加したサービスの番号をセッションに格納
        ContextUtils.setAttribute("componentNos", componentNos);

        // 画面を閉じる
        close();
    }

}
