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
import java.util.Map.Entry;

import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.log.service.OperationLogService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.component.mysql.MySQLConstants;
import com.vaadin.data.Container;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * <p>
 * サーバView画面の画面中央のサーバ一覧を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServerTable extends Table {

    final String COLUMN_HEIGHT = "28px";

    final MyCloudTabs sender;

    //項目名
    String[] CAPNAME = {
            ViewProperties.getCaption("field.no"),
            ViewProperties.getCaption("field.serverName"),
            ViewProperties.getCaption("field.fqdn"),
            ViewProperties.getCaption("field.ipAddress"),
            ViewProperties.getCaption("field.serverOsStatus"),
            ViewProperties.getCaption("field.serverServices")
//            ViewProperties.getCaption("field.serverOperation")
            };

    private Map<Long, List<Button>> map = new HashMap<Long, List<Button>>();

    ServerTable(String caption, Container container, final MyCloudTabs sender) {
        super(caption, container);

        this.sender = sender;
        setVisibleColumns(new Object[] {});

        setWidth("100%");
        if ( this.isEnabled() ) {
            setHeight("100%");
        }

        setPageLength(0);
        setSortDisabled(true);
        setColumnReorderingAllowed(false);
        setColumnCollapsingAllowed(false);
        setSelectable(true);
        setMultiSelect(false);
        setImmediate(true);
        setStyleName("server-table");
        setNullSelectionAllowed(false);
        setCacheRate(0.1);

        addGeneratedColumn("no", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto p = (InstanceDto) itemId;
                Label nlbl = new Label(String.valueOf(p.getInstance().getInstanceNo()));
                return nlbl;
            }
        });

        addGeneratedColumn("name", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto p = (InstanceDto) itemId;

                PlatformDto platformDto = p.getPlatform();
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

                Label nlbl = new Label("<img src=\"" + VaadinUtils.getIconPath(ServerTable.this, icon) + "\"><div>"
                        + p.getInstance().getInstanceName() + "</div>", Label.CONTENT_XHTML);
                nlbl.setHeight(COLUMN_HEIGHT);
                return nlbl;
            }
        });

        addGeneratedColumn("fqdn", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto p = (InstanceDto) itemId;
                Label nlbl = new Label( p.getInstance().getFqdn());
                return nlbl;
            }
        });

        addGeneratedColumn("publicIp", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto p = (InstanceDto) itemId;
                Label ipaddr = new Label(p.getInstance().getPublicIp());
                return ipaddr;
            }
        });

        addGeneratedColumn("status", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                InstanceDto p = (InstanceDto) itemId;
                String a = p.getInstance().getStatus().substring(0, 1).toUpperCase()
                        + p.getInstance().getStatus().substring(1).toLowerCase();
                Icons icon = Icons.fromName(a);
                Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(ServerTable.this, icon) + "\"><div>" + a + "</div>",
                        Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);
                return slbl;
            }
        });

        addGeneratedColumn("services", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {

                InstanceDto p = (InstanceDto) itemId;

                String context = "<div>";
                for (ComponentDto dto : sender.getComponents(p.getComponentInstances())) {
                    ComponentType componentType = dto.getComponentType();
                    String name = componentType.getComponentTypeNameDisp();
                    Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                    // Master
                    if(MySQLConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())){
                        Long masterInstanceNo = null;
                        for (InstanceConfig config : dto.getInstanceConfigs()) {
                            if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(config.getConfigName())) {
                                if (StringUtils.isEmpty(config.getConfigValue())) {
                                    masterInstanceNo = config.getInstanceNo();
                                    break;
                                }
                            }
                        }
                        if(masterInstanceNo != null){
                            if(masterInstanceNo.equals(p.getInstance().getInstanceNo())){
                                name = name +"_master";
                                nameIcon = Icons.MYSQL_MASTER;
                            }else{
                                name = name +"_slave";
                                nameIcon = Icons.MYSQL_SLAVE;
                            }
                        }else{
                            name = name +"_slave";
                            nameIcon = Icons.MYSQL_SLAVE;
                        }
                    }

                    context = context  +   "<img style=\"width: 5px;\" src=\" "
                            + VaadinUtils.getIconPath(ServerTable.this, Icons.fromName("SPACER")) + "\" >"
                            + "<img src=\"" + VaadinUtils.getIconPath(ServerTable.this, nameIcon) + "\" + "
                            + " title=\"" + name + "\">";
                }
                context = context + "</div>";

                Label slayout = new Label(context, Label.CONTENT_XHTML);
                slayout.setHeight(COLUMN_HEIGHT);
                return slayout;
            }

        });

//        addGeneratedColumn("operation", new ColumnGenerator() {
//            public Component generateCell(Table source, Object itemId, Object columnId) {
//
//                List<Button> list = new ArrayList<Button>();
//
//                CssLayout blayout = new CssLayout();
//
//                blayout.setWidth("100%");
//                blayout.setHeight(COLUMN_HEIGHT);
//                blayout.setMargin(false);
//
//                InstanceDto p = (InstanceDto) itemId;
//
//                Button playButton = new Button(ViewProperties.getCaption("button.startServer"));
//                playButton.setDescription(ViewProperties.getCaption("description.startServer"));
//                playButton.addStyleName("borderless");
//                playButton.setIcon(Icons.PLAY.resource());
//                playButton.setEnabled(false);
//                playButton.addListener(Button.ClickEvent.class, ServerTable.this, "playButtonClick");
//                blayout.addComponent(playButton);
//                list.add(playButton);
//
//                Button stopButton = new Button(ViewProperties.getCaption("button.stopServer"));
//                stopButton.setDescription(ViewProperties.getCaption("description.stopServer"));
//                stopButton.addStyleName("borderless");
//                stopButton.setIcon(Icons.STOP.resource());
//                stopButton.setEnabled(false);
//                stopButton.addListener(Button.ClickEvent.class, ServerTable.this, "stopButtonClick");
//                blayout.addComponent(stopButton);
//                list.add(stopButton);
//
//                Button editButton = new Button(ViewProperties.getCaption("button.editServer"));
//                editButton.setDescription(ViewProperties.getCaption("description.editServer"));
//                editButton.addStyleName("borderless");
//                editButton.setIcon(Icons.EDIT.resource());
//                editButton.setEnabled(false);
//                editButton.addListener(Button.ClickEvent.class, ServerTable.this, "editButtonClick");
//                blayout.addComponent(editButton);
//                list.add(editButton);
//
//                Button delButton = new Button(ViewProperties.getCaption("button.deleteServer"));
//                delButton.setDescription(ViewProperties.getCaption("description.deleteServer"));
//                delButton.addStyleName("borderless");
//                delButton.setIcon(Icons.DELETE.resource());
//                delButton.setEnabled(false);
//                delButton.addListener(Button.ClickEvent.class, ServerTable.this, "delButtonClick");
//                blayout.addComponent(delButton);
//                list.add(delButton);
//
//                map.put(p.getInstance().getInstanceNo(), list);
//
//                return blayout;
//            }
//
//        });

        //テーブルに項目名を設定
        setColumnHeaders(CAPNAME);

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

//        setColumnExpandRatio("name", 100);
        setColumnExpandRatio("fqdn", 100);
        addListener(Table.ValueChangeEvent.class, sender, "tableRowSelected");

//        alwaysRecalculateColumnWidths = true;
    }

    public void playButtonClick(Button.ClickEvent event) {
        final InstanceDto dto = (InstanceDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();
        final PlatformDto platform = dto.getPlatform();
        if ("aws".equals(platform.getPlatform().getPlatformType()) &&
            platform.getPlatformAws().getVpc() &&
            StringUtils.isEmpty(dto.getAwsInstance().getSubnetId())) {
            //EC2+VPCの場合、サブネットを設定しないと起動不可
            DialogConfirm dialog = new DialogConfirm(
                    ViewProperties.getCaption("dialog.error"), ViewMessages.getMessage("IUI-000111"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        HorizontalLayout optionLayout = new HorizontalLayout();
        final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000035"), false);
        checkBox.setImmediate(true);
        optionLayout.addComponent(checkBox);

        String actionName = event.getButton().getDescription();
        String message = ViewMessages.getMessage("IUI-000013", new Object[]{dto.getInstance().getInstanceName(), actionName});
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel, optionLayout);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                ProcessService processService = BeanContext.getBean(ProcessService.class);
                Long farmNo = ViewContext.getFarmNo();
                List<Long> list = new ArrayList<Long>();
                list.add(dto.getInstance().getInstanceNo());
                boolean startService = (Boolean) checkBox.getValue();

                //TODO LOG
                AutoApplication apl = (AutoApplication)getApplication();
                apl.doOpLog("SERVER", "Start Server", dto.getInstance().getInstanceNo(), null, null, String.valueOf(startService));

                processService.startInstances(farmNo, list, startService);
                sender.refreshTable();

                // 選択されていたサーバを選択し直す
                for (Object itemId : getItemIds()) {
                    InstanceDto dto2 = (InstanceDto) itemId;
                    if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                        select(itemId);
                        setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2.getInstance());
                        break;
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void stopButtonClick(Button.ClickEvent event) {
        final InstanceDto dto = (InstanceDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();

        String message = ViewMessages.getMessage("IUI-000014", dto.getInstance().getInstanceName());
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
                list.add(dto.getInstance().getInstanceNo());

                //TODO LOG
                AutoApplication apl = (AutoApplication)getApplication();
                apl.doOpLog("SERVER", "Stop Server", dto.getInstance().getInstanceNo(), null, null, null);

                processService.stopInstances(farmNo, list);
                sender.refreshTable();

                // 選択されていたサーバを選択し直す
                for (Object itemId : getItemIds()) {
                    InstanceDto dto2 = (InstanceDto) itemId;
                    if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                        select(itemId);
                        setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2.getInstance());
                        break;
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void editButtonClick(Button.ClickEvent event) {

        final InstanceDto dto = (InstanceDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();

        WinServerEdit winServerEdit = new WinServerEdit(getApplication(),dto.getInstance().getInstanceNo());
        winServerEdit.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                sender.refreshTable();

                // 選択されていたサーバを選択し直す
                for (Object itemId : getItemIds()) {
                    InstanceDto dto2 = (InstanceDto) itemId;
                    if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                        select(itemId);
                        setCurrentPageFirstItemIndex(index);
                        setButtonStatus(dto2.getInstance());
                        break;
                    }
                }
            }
        });
        getWindow().addWindow(winServerEdit);
        sender.refreshTable();
    }

    public void delButtonClick(Button.ClickEvent event) {
        final InstanceDto dto = (InstanceDto) this.getValue();
        final int index = this.getCurrentPageFirstItemIndex();

        String message = ViewMessages.getMessage("IUI-000015", dto.getInstance().getInstanceName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                //TODO LOG
                AutoApplication apl = (AutoApplication)getApplication();
                apl.doOpLog("SERVER", "Delete Server", dto.getInstance().getInstanceNo(), null, null, null);

                Long instanceNo = dto.getInstance().getInstanceNo();
                InstanceService instanceService = BeanContext.getBean(InstanceService.class);
                instanceService.deleteInstance(instanceNo);
                select(null);
                sender.refreshTable();
                setCurrentPageFirstItemIndex(index);
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void setButtonStatus(Instance instance) {
        for (Entry<Long, List<Button>> entry : map.entrySet()) {
            Long key = entry.getKey();
            if (key.equals(instance.getInstanceNo())) {
                for (Button button : entry.getValue()) {
                    if ("".equals(instance.getStatus()) || InstanceStatus.STOPPED.toString().equals(instance.getStatus())) {
                        if (("STOP".equals(button.getCaption()))) {
                            button.setEnabled(false);
                        } else if (("START".equals(button.getCaption()))) {
                            button.setIcon(Icons.PLAY.resource());
                            button.setDescription(ViewProperties.getCaption("description.startServer"));
                            button.setEnabled(true);
                        } else {
                            button.setEnabled(true);
                        }
                    } else if (InstanceStatus.RUNNING.toString().equals(instance.getStatus())) {
                        if (("STOP".equals(button.getCaption()))) {
                            button.setEnabled(true);
                        } else if (("START".equals(button.getCaption()))) {
                            button.setIcon(Icons.RELOAD.resource());
                            button.setDescription(ViewProperties.getCaption("description.reloadServer"));
                            button.setEnabled(true);
                        } else {
                            button.setEnabled(false);
                        }
                    } else if (InstanceStatus.WARNING.toString().equals(instance.getStatus())) {
                        if (("STOP".equals(button.getCaption()))) {
                            button.setEnabled(true);
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
        ((InstanceDtoContainer) getContainerDataSource()).refresh2(this);
    }

    public void refreshDesc() {
        sender.refreshDesc(this);
    }

}
