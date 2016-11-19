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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.data.ComponentDtoContainer;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * <p>
 * サービスView画面の画面中央のサーバ一覧を生成します。
 * </p>
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class ServiceTable extends Table {

    MyCloudTabs sender;

    final String COLUMN_HEIGHT = "28px";

    //項目名
    String[] CAPNAME = { ViewProperties.getCaption("field.no"), ViewProperties.getCaption("field.serviceName"),
            ViewProperties.getCaption("field.serverSum"), ViewProperties.getCaption("field.serviceStatus"),
            ViewProperties.getCaption("field.serviceIpPort"), ViewProperties.getCaption("field.serviceDetail") };

    private Map<Long, List<Button>> map = new HashMap<Long, List<Button>>();

    public ServiceTable(String caption, Container container, MyCloudTabs sender) {
        super(caption, container);
        this.sender = sender;
        setVisibleColumns(new Object[] {});

        setWidth("100%");
        setHeight("100%");

        setPageLength(0);
        setSortDisabled(true);
        setColumnReorderingAllowed(false);
        setColumnCollapsingAllowed(false);
        setSelectable(true);
        setMultiSelect(false);
        setImmediate(true);
        setNullSelectionAllowed(false);
        setStyleName("service-table");

        addGeneratedColumn("no", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                Label nlbl = new Label(String.valueOf(p.getComponent().getComponentNo()));
                return nlbl;
            }
        });

        addGeneratedColumn("name", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                String name;
                if (StringUtils.isEmpty(p.getComponent().getComment())) {
                    name = p.getComponent().getComponentName();
                } else {
                    name = p.getComponent().getComment() + "\n[" + p.getComponent().getComponentName() + "]";
                }
                Label nlbl = new Label(name, Label.CONTENT_PREFORMATTED);
                return nlbl;
            }
        });

        //サービスの割り当てサーバ数
        addGeneratedColumn("srvs", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                int srvs = 0;
                for (ComponentInstanceDto componentInstance : p.getComponentInstances()) {
                    if (BooleanUtils.isTrue(componentInstance.getComponentInstance().getAssociate())) {
                        srvs++;
                    }
                }
                Label lbl = new Label(Integer.toString(srvs));
                return lbl;
            }
        });

        addGeneratedColumn("status", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                String a = p.getStatus().substring(0, 1).toUpperCase() + p.getStatus().substring(1).toLowerCase();
                Icons icon = Icons.fromName(a);
                Label slbl = new Label(IconUtils.createImageTag(ServiceTable.this, icon, a), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                return slbl;
            }
        });

        addGeneratedColumn("loadBalancer", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto dto = (ComponentDto) itemId;

                MyCloudTabs myCloudTabs = null;
                Component c = ServiceTable.this;
                while (c != null) {
                    if (c instanceof MyCloudTabs) {
                        myCloudTabs = (MyCloudTabs) c;
                        break;
                    }
                    c = c.getParent();
                }
                Button btn = null;
                for (LoadBalancerDto lbDto : (Collection<LoadBalancerDto>) myCloudTabs.loadBalancerTable.getItemIds()) {
                    if (dto.getComponent().getComponentNo().equals(lbDto.getLoadBalancer().getComponentNo())) {
                        btn = getLoadBalancerButton(lbDto);
                        break;
                    }
                }
                if (btn != null) {
                    return btn;
                } else {
                    return (new Label(""));
                }
            }
        });

        addGeneratedColumn("serviceDetail", new ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                ComponentDto p = (ComponentDto) itemId;
                ComponentType componentType = p.getComponentType();

                // サービス名
                String name = componentType.getComponentTypeNameDisp();
                Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                Label slbl = new Label(IconUtils.createImageTag(ServiceTable.this, nameIcon, name), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);
                return slbl;
            }
        });

        //テーブルに項目名を設定
        setColumnHeaders(CAPNAME);

        //テーブルのカラムに対してStyleNameを設定
        setCellStyleGenerator(new StandardCellStyleGenerator());

        setColumnExpandRatio("serviceDetail", 100);

        addListener(Table.ValueChangeEvent.class, sender, "tableRowSelected");
    }

    Button getLoadBalancerButton(LoadBalancerDto lbDto) {
        Button btn = new Button();
        btn.setCaption(lbDto.getLoadBalancer().getLoadBalancerName());
        btn.setIcon(Icons.LOADBALANCER_TAB.resource());
        btn.setData(lbDto);
        btn.addStyleName("borderless");
        btn.addStyleName("loadbalancer-button");
        btn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                loadBalancerButtonClick(event);
            }
        });
        return btn;
    }

    void loadBalancerButtonClick(ClickEvent event) {
        Button btn = event.getButton();
        LoadBalancerDto dto = (LoadBalancerDto) btn.getData();

        MyCloudTabs myCloudTabs = null;
        Component c = ServiceTable.this;
        while (c != null) {
            if (c instanceof MyCloudTabs) {
                myCloudTabs = (MyCloudTabs) c;
                break;
            }
            c = c.getParent();
        }
        //該当ロードバランサーを選択
        myCloudTabs.loadBalancerTable.select(dto);
        //ロードバランサーTabに移動
        myCloudTabs.tabDesc.setSelectedTab(myCloudTabs.pnLoadBalancer);
    }

    public void playButtonClick(Button.ClickEvent event) {
        final ComponentDto dto = (ComponentDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();

        String actionName = event.getButton().getDescription();
        String message = ViewMessages.getMessage("IUI-000016", new Object[] { dto.getComponent().getComponentName(),
                actionName });
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                ProcessService processService = BeanContext.getBean(ProcessService.class);
                Long farmNo = ViewContext.getFarmNo();
                List<Long> list = new ArrayList<Long>();
                list.add(dto.getComponent().getComponentNo());
                processService.startComponents(farmNo, list);
                sender.refreshTable();

                // 選択されていたサービスを選択し直す
                for (Object itemId : getItemIds()) {
                    ComponentDto dto2 = (ComponentDto) itemId;
                    if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                        select(itemId);
                        setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2);
                        break;
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void stopButtonClick(Button.ClickEvent event) {
        final ComponentDto dto = (ComponentDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();

        HorizontalLayout optionLayout = new HorizontalLayout();
        final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000033"), false);
        checkBox.setImmediate(true);
        optionLayout.addComponent(checkBox);

        String message = ViewMessages.getMessage("IUI-000017", dto.getComponent().getComponentName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancel, optionLayout);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                ProcessService processService = BeanContext.getBean(ProcessService.class);
                Long farmNo = ViewContext.getFarmNo();
                List<Long> list = new ArrayList<Long>();
                list.add(dto.getComponent().getComponentNo());
                boolean stopInstance = (Boolean) checkBox.getValue();
                processService.stopComponents(farmNo, list, stopInstance);
                sender.refreshTable();

                // 選択されていたサービスを選択し直す
                for (Object itemId : getItemIds()) {
                    ComponentDto dto2 = (ComponentDto) itemId;
                    if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                        select(itemId);
                        setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2);
                        break;
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void editButtonClick(Button.ClickEvent event) {
        final ComponentDto dto = (ComponentDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();

        WinServiceEdit winServiceEdit = new WinServiceEdit(dto.getComponent().getComponentNo());
        winServiceEdit.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                sender.refreshTable();

                // 選択されていたサービスを選択し直す
                for (Object itemId : getItemIds()) {
                    ComponentDto dto2 = (ComponentDto) itemId;
                    if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                        select(itemId);
                        setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2);
                        break;
                    }
                }
            }
        });
        getWindow().addWindow(winServiceEdit);
    }

    public void delButtonClick(Button.ClickEvent event) {
        final ComponentDto dto = (ComponentDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();

        String message = ViewMessages.getMessage("IUI-000018", dto.getComponent().getComponentName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("SERVICE", "Delete Service", null, dto.getComponent().getComponentNo(), null, null);

                Long componentNo = dto.getComponent().getComponentNo();
                ComponentService componentService = BeanContext.getBean(ComponentService.class);
                componentService.deleteComponent(componentNo);
                select(null);
                sender.refreshTable();
                setCurrentPageFirstItemIndex(index);
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void setButtonStatus(ComponentDto dto) {
        //サービスの状態に合わせて、ボタンの状態を変更
        // サービス起動状態： START,EDIT,STOP ボタン有効
        // サービス停止状態： START,EDIT,DELETE ボタン有効
        // サービス起動中：何も押せない
        // WARNING；EDIT,STOP ボタン有効

        jp.primecloud.auto.entity.crud.Component component = dto.getComponent();
        for (Entry<Long, List<Button>> entry : map.entrySet()) {
            Long key = entry.getKey();
            if (key.equals(component.getComponentNo())) {
                for (Button button : entry.getValue()) {
                    if ("".equals(dto.getStatus()) || "STOPPED".equals(dto.getStatus())) {
                        if (("STOP".equals(button.getCaption()))) {
                            button.setEnabled(false);
                        } else if (("START".equals(button.getCaption()))) {
                            button.setIcon(Icons.PLAY.resource());
                            button.setDescription(ViewProperties.getCaption("description.startService"));
                            button.setEnabled(true);
                        } else {
                            button.setEnabled(true);
                        }
                    } else if ("RUNNING".equals(dto.getStatus())) {
                        if (("STOP".equals(button.getCaption()))) {
                            button.setEnabled(true);
                        } else if (("START".equals(button.getCaption()))) {
                            button.setIcon(Icons.RELOAD.resource());
                            button.setDescription(ViewProperties.getCaption("description.reloadService"));
                            button.setEnabled(true);
                        } else if (("EDIT".equals(button.getCaption()))) {
                            button.setEnabled(true);
                        } else {
                            button.setEnabled(false);
                        }
                    } else if ("WARNING".equals(dto.getStatus())) {
                        if (("STOP".equals(button.getCaption()))) {
                            button.setEnabled(true);
                        } else if ("START".equals(button.getCaption())) {
                            button.setIcon(Icons.RELOAD.resource());
                            button.setDescription(ViewProperties.getCaption("description.reloadService"));
                            button.setEnabled(true);
                        } else if ("EDIT".equals(button.getCaption())) {
                            // 処理中のサーバがなければ有効、あれば無効にする
                            boolean processing = false;
                            for (ComponentInstanceDto componentInstance : dto.getComponentInstances()) {
                                ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance
                                        .getComponentInstance().getStatus());
                                if (status != ComponentInstanceStatus.RUNNING
                                        && status != ComponentInstanceStatus.WARNING
                                        && status != ComponentInstanceStatus.STOPPED) {
                                    processing = true;
                                    break;
                                }
                            }
                            button.setEnabled(!processing);
                        } else {
                            button.setEnabled(false);
                        }
                    } else {
                        button.setEnabled(false);
                    }
                }
            } else {
                for (Button button : entry.getValue()) {
                    button.setEnabled(false);
                }
            }
        }
    }

    public void refreshData() {
        ((ComponentDtoContainer) getContainerDataSource()).refresh2(this);
    }

    public void refreshDesc() {
        sender.refreshDesc(this);
    }

}
