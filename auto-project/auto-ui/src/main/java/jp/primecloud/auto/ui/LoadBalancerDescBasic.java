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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.service.AwsDescribeService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.Subnet;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * ロードバランサ画面下部の基本情報の生成を行います。
 * </p>
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class LoadBalancerDescBasic extends Panel {

    BasicInfo basicInfo = new BasicInfo();

    AttachServiceTable attachServiceTable = new AttachServiceTable("", null);

    LoadbalancerServiceOperation loadBalancerOpe = new LoadbalancerServiceOperation();

    LoadBalancerDescBasic() {
        addStyleName(Reindeer.PANEL_LIGHT);
        setHeight("100%");

        HorizontalLayout hlLayout = new HorizontalLayout();
        hlLayout.setWidth("100%");
        hlLayout.setHeight("100%");
        hlLayout.setMargin(true);
        hlLayout.setSpacing(true);
        hlLayout.addStyleName("loadbalancer-desc-basic");
        setContent(hlLayout);

        basicInfo.setWidth("100%");
        attachServiceTable.setWidth("100%");

        //表同士の間隔をあける
        Label padding = new Label(" ");
        padding.setWidth("7px");
        padding.setHeight("99%");
        padding.addStyleName("desc-padding");

        Label padding2 = new Label("");
        padding2.setWidth("1px");

        //右側レイアウト
        VerticalLayout vRightLayout = new VerticalLayout();
        vRightLayout.setWidth("100%");
        vRightLayout.setHeight("100%");
        vRightLayout.setMargin(false);
        vRightLayout.setSpacing(false);
        vRightLayout.addComponent(attachServiceTable);
        vRightLayout.addComponent(loadBalancerOpe);
        vRightLayout.setExpandRatio(attachServiceTable, 100);

        hlLayout.addComponent(basicInfo);
        hlLayout.addComponent(padding);
        hlLayout.addComponent(padding2);
        hlLayout.addComponent(vRightLayout);
        hlLayout.setExpandRatio(basicInfo, 43);
        hlLayout.setExpandRatio(vRightLayout, 57);

        attachServiceTable.refresh(null);
    }

    class BasicInfo extends Panel {

        final String COLUMN_HEIGHT = "30px";

        //項目名
        final String[] CAPTION_FIELD = { "field.loadBalancerName", "field.loadBalancerService", "field.fqdn",
                "field.loadBalancerHostname", "field.status", "field.platform", "field.loadBalancerType",
                "field.subnet", "field.comment", };

        HashMap<String, Label> displayLabels = new HashMap<String, Label>();

        LoadBalancerDto loadBalancerDto;

        GridLayout layout;

        BasicInfo() {
            setCaption(ViewProperties.getCaption("table.loadBalancerBasicInfo"));
            setHeight("95%");
            setStyleName("loadbalancer-desc-basic-panel");

            VerticalLayout vlay = (VerticalLayout) getContent();
            vlay.setStyleName("loadbalancer-desc-basic-panel");
            vlay.setMargin(true);

            layout = new GridLayout(2, CAPTION_FIELD.length);
            layout.setWidth("100%");
            layout.setStyleName("loadbalancer-desc-basic-info");
            layout.setColumnExpandRatio(0, 40);
            layout.setColumnExpandRatio(1, 60);
            vlay.addComponent(layout);

            initDisplayLabels();
        }

        private void initDisplayLabels() {
            for (int i = 0; i < CAPTION_FIELD.length; i++) {
                //項目名
                Label lbl1 = new Label(ViewProperties.getCaption(CAPTION_FIELD[i]), Label.CONTENT_XHTML);
                lbl1.setHeight(COLUMN_HEIGHT);
                layout.addComponent(lbl1, 0, i);
                //表示内容ラベル
                Label lbl2 = new Label("", Label.CONTENT_XHTML);
                displayLabels.put(CAPTION_FIELD[i], lbl2);
                layout.addComponent(lbl2, 1, i);
            }
        }

        private void resetLabels() {
            for (Label lbl : displayLabels.values()) {
                lbl.setValue("");
            }
        }

        public void setItem(LoadBalancerDto dto) {
            //前回表示時の値をクリア
            resetLabels();

            loadBalancerDto = dto;

            MyCloudTabs myCloudTabs = null;
            Component c = LoadBalancerDescBasic.this;
            while (c != null) {
                if (c instanceof MyCloudTabs) {
                    myCloudTabs = (MyCloudTabs) c;
                    break;
                }
                c = c.getParent();
            }
            Component me = LoadBalancerDescBasic.this;

            if (dto != null) {
                LoadBalancer lb = dto.getLoadBalancer();

                //ロードバランサー名
                displayLabels.get("field.loadBalancerName").setValue(lb.getLoadBalancerName());

                // 割り当てサービス
                ComponentDto componentDto = null;
                for (ComponentDto cDto : (Collection<ComponentDto>) myCloudTabs.serviceTable.getItemIds()) {
                    if (cDto.getComponent().getComponentNo().equals(dto.getLoadBalancer().getComponentNo())) {
                        componentDto = cDto;
                        break;
                    }
                }
                String name;
                if (StringUtils.isEmpty(componentDto.getComponent().getComment())) {
                    name = componentDto.getComponent().getComponentName();
                } else {
                    name = componentDto.getComponent().getComment() + " ["
                            + componentDto.getComponent().getComponentName() + "]";
                }
                ComponentType componentType = componentDto.getComponentType();
                Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                displayLabels.get("field.loadBalancerService").setValue(IconUtils.createImageTag(me, nameIcon, name));

                //FQDN
                if (lb.getFqdn() != null) {
                    displayLabels.get("field.fqdn").setValue(lb.getFqdn());
                }

                //ホスト名
                String hostName = lb.getCanonicalName();
                displayLabels.get("field.loadBalancerHostname").setValue(hostName);

                //ステータス
                LoadBalancerStatus status = LoadBalancerStatus.fromStatus(lb.getStatus());
                String statusString = status.name().substring(0, 1).toUpperCase()
                        + status.name().substring(1).toLowerCase();
                String iconName;
                //ロードバランサーがRUNNIGだが、リスナ一覧にリスナーが存在しない(+WARNING)
                if (status == LoadBalancerStatus.RUNNING && dto.getLoadBalancerListeners().size() == 0) {
                    iconName = Icons.RUN_WARNING.file().substring(0, Icons.RUN_WARNING.file().length() - 4);
                } else {
                    iconName = statusString;
                }
                displayLabels.get("field.status").setValue(
                        IconUtils.createImageTag(me, Icons.fromName(iconName), statusString));

                //プラットフォーム
                Platform platform = dto.getPlatform().getPlatform();
                PlatformAws platformAws = dto.getPlatform().getPlatformAws();

                //プラットフォームアイコン名の取得
                Icons icon = IconUtils.getPlatformIcon(dto.getPlatform());

                String description = platform.getPlatformNameDisp();
                displayLabels.get("field.platform").setValue(IconUtils.createImageTag(me, icon, description));

                //タイプ
                if (lb.getType() != null) {
                    String type = ViewProperties.getLoadBalancerType(lb.getType());
                    displayLabels.get("field.loadBalancerType").setValue(type);
                }

                //サブネット
                if (PCCConstant.LOAD_BALANCER_ELB.equals(lb.getType()) && platformAws.getVpc()
                        && StringUtils.isNotEmpty(dto.getAwsLoadBalancer().getSubnetId())) {
                    List<String> lbSubnets = new ArrayList<String>();
                    for (String lbSubnet : dto.getAwsLoadBalancer().getSubnetId().split(",")) {
                        lbSubnets.add(lbSubnet.trim());
                    }
                    AwsDescribeService awsDescribeService = BeanContext.getBean(AwsDescribeService.class);
                    List<Subnet> subnets = awsDescribeService.getSubnets(ViewContext.getUserNo(), lb.getPlatformNo());
                    StringBuffer subnetBuffer = new StringBuffer();
                    for (Subnet subnet : subnets) {
                        if (lbSubnets.contains(subnet.getSubnetId())) {
                            subnetBuffer.append(subnetBuffer.length() > 0 ? "<br>" + subnet.getCidrBlock() : subnet
                                    .getCidrBlock());
                        }
                    }
                    displayLabels.get("field.subnet").setValue(subnetBuffer.toString());
                }

                //コメント
                if (lb.getComment() != null) {
                    displayLabels.get("field.comment").setValue(lb.getComment());
                }
            }
        }
    }

    //右側割り当てサービスサーバ一覧
    class AttachServiceTable extends Table {

        final String COLUMN_HEIGHT = "28px";

        //項目名
        final String[] COLNAME = { null, ViewProperties.getCaption("field.loadBalancerPort"),
                ViewProperties.getCaption("field.loadBalancerServicePort"),
                ViewProperties.getCaption("field.loadBalancerProtocol"),
                ViewProperties.getCaption("field.loadBalancerServiceStatus"),
                ViewProperties.getCaption("field.editLoadBalancerListener"), };

        final String[] VISIBLE_COLNAME = { "check", "loadBalancerPort", "servicePort", "protocol", "status", "edit", };

        HashMap<Integer, CheckBox> checkList = new HashMap<Integer, CheckBox>();

        public AttachServiceTable(String caption, Container dataSource) {
            super(caption, dataSource);
            setIcon(Icons.LISTENER_MINI.resource());

            addGeneratedColumn("check", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener p = (LoadBalancerListener) itemId;
                    Integer no = p.getLoadBalancerPort();

                    CheckBox check;
                    if (checkList.containsKey(no)) {
                        check = checkList.get(no);
                    } else {
                        check = new CheckBox();
                        checkList.put(no, check);
                    }

                    check.setImmediate(true);

                    check.addListener(new ValueChangeListener() {
                        @Override
                        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {

                            requestRepaint();
                        }
                    });
                    return check;
                }
            });

            addGeneratedColumn("edit", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener p = (LoadBalancerListener) itemId;

                    Button btnEdit = new Button(ViewProperties.getCaption("button.editLoadBalancerListener"));
                    btnEdit.setDescription(ViewProperties.getCaption("description.editLoadBalancerListener"));
                    btnEdit.addStyleName("borderless");
                    btnEdit.setIcon(Icons.EDITMINI.resource());
                    btnEdit.setData(p);

                    LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(p.getStatus());
                    if (status == LoadBalancerListenerStatus.STOPPED) {
                        btnEdit.addListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(ClickEvent event) {
                                editButtonClick(event);
                            }
                        });
                    } else {
                        btnEdit.setEnabled(false);
                    }

                    return btnEdit;
                }
            });

            addGeneratedColumn("status", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener p = (LoadBalancerListener) itemId;
                    String status = p.getStatus();

                    // TODO: 振り分け状態のリファクタリング
                    //LoadBalancerInstanceに存在しない場合にはStop状態
                    if ("".equals(status) || status.equalsIgnoreCase("STOPPED")) {
                        status = "DISABLE";
                    } else if (status.equalsIgnoreCase("RUNNING")) {
                        status = "ENABLE";
                    } else if (status.equalsIgnoreCase("STARTING")) {
                        status = "CONFIGURING";
                    } else if (status.equalsIgnoreCase("STOPPING")) {
                        status = "CONFIGURING";
                    }

                    Icons icon = Icons.fromName(status);
                    String a = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                    Label slbl = new Label(IconUtils.createImageTag(LoadBalancerDescBasic.this, icon, a),
                            Label.CONTENT_XHTML);
                    slbl.setHeight(COLUMN_HEIGHT);
                    return slbl;
                }
            });

            addGeneratedColumn("loadBalancerPort", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener p = (LoadBalancerListener) itemId;
                    String protocol = p.getProtocol();

                    // HTTP, HTTPSでないときは通常のラベル
                    if (!"HTTP".equals(protocol) && !"HTTPS".equals(protocol)) {
                        return new Label(p.getLoadBalancerPort().toString());
                    }

                    // ホスト名を取得
                    LoadBalancerDto dto = basicInfo.loadBalancerDto;
                    String hostName = dto.getLoadBalancer().getCanonicalName();
                    if (dto.getComponentLoadBalancerDto() != null) {
                        hostName = dto.getComponentLoadBalancerDto().getIpAddress();
                    }

                    // ホスト名が空の場合は通常のラベル
                    if (StringUtils.isEmpty(hostName)) {
                        return new Label(p.getLoadBalancerPort().toString());
                    }

                    String url = protocol.toLowerCase() + "://" + hostName + ":" + p.getLoadBalancerPort().toString()
                            + "/";
                    Link link = new Link(p.getLoadBalancerPort().toString(), new ExternalResource(url));
                    link.setTargetName("_blank");
                    link.setIcon(Icons.SHORTCUT.resource());
                    link.setDescription(url);
                    return link;
                }
            });

            addGeneratedColumn("servicePort", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener p = (LoadBalancerListener) itemId;

                    Label slbl = new Label(p.getServicePort().toString());
                    return slbl;
                }
            });

            addGeneratedColumn("protocol", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    LoadBalancerListener p = (LoadBalancerListener) itemId;

                    Label slbl = new Label(p.getProtocol());
                    return slbl;
                }
            });

            setColumnExpandRatio("status", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    LoadBalancerListener p = (LoadBalancerListener) itemId;

                    if (propertyId == null) {
                        return "";
                    } else {
                        String ret = propertyId.toString().toLowerCase();
                        Integer no = p.getLoadBalancerPort();
                        if (checkList.containsKey(no) && (Boolean) checkList.get(no).getValue()) {
                            ret += " v-selected";
                        }
                        return ret;
                    }
                }
            });

            addListener(new ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    LoadBalancerListener p = (LoadBalancerListener) event.getItemId();
                    Integer no = p.getLoadBalancerPort();
                    if (checkList.containsKey(no)) {
                        checkList.get(no).setValue(!(Boolean) checkList.get(no).getValue());
                    }
                    loadBalancerOpe.refresh();
                }
            });

            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("loadbalancer-desc-basic-listener");
            setCaption(ViewProperties.getCaption("table.loadBalancerListener"));
            setHeight("100%");
            setSortDisabled(true);
            setImmediate(true);
            setVisible(true);
        }

        public void refresh(LoadBalancerDto dto) {
            refresh(dto, false);
        }

        public void refresh(LoadBalancerDto dto, boolean clearCheckBox) {
            if (dto != null) {
                setContainerDataSource(new LoadBalancerListenerContainer(dto.getLoadBalancerListeners()));
            } else {
                setContainerDataSource(null);
            }
            setVisibleColumns(VISIBLE_COLNAME);

            if (clearCheckBox) {
                checkList.clear();
            }

            if (dto != null && dto.getLoadBalancerListeners() != null && dto.getLoadBalancerListeners().size() > 0) {
                setColumnHeaders(COLNAME);
            }

            loadBalancerOpe.refresh();
        }

        class LoadBalancerListenerContainer extends BeanItemContainer<LoadBalancerListener> implements Serializable {
            public LoadBalancerListenerContainer(Collection<LoadBalancerListener> listeners) {
                super(LoadBalancerListener.class);
                for (LoadBalancerListener listener : listeners) {
                    addItem(listener);
                }
            }
        }

        public void editButtonClick(Button.ClickEvent event) {
            LoadBalancerListener listener = (LoadBalancerListener) event.getButton().getData();
            final LoadBalancerDto dto = basicInfo.loadBalancerDto;

            WinLoadBalancerConfigListener win = new WinLoadBalancerConfigListener(getApplication(),
                    listener.getLoadBalancerNo(), listener.getLoadBalancerPort());
            win.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    MyCloudTabs myCloudTabs = ((AutoApplication) getApplication()).myCloud.myCloudTabs;
                    myCloudTabs.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    if (dto != null) {
                        for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                            LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                            if (dto.getLoadBalancer().getLoadBalancerNo()
                                    .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                                myCloudTabs.loadBalancerTable.select(itemId);
                                break;
                            }
                        }
                    }
                }
            });
            getWindow().addWindow(win);
        }
    }

    public class LoadbalancerServiceOperation extends HorizontalLayout {

        final String BUTTON_WIDTH = "90px";

        Button btnCheckAll;

        Button btnAdd;

        Button btnDelete;

        Button btnEnable;

        Button btnDisable;

        LoadbalancerServiceOperation() {
            addStyleName("loadbalancer-service-operation-buttons");
            setHeight("35px");
            setWidth("100%");
            setSpacing(true);

            btnCheckAll = new Button(ViewProperties.getCaption("button.checkAll"));
            btnCheckAll.setDescription(ViewProperties.getCaption("description.checkAll"));
            btnCheckAll.addStyleName("borderless");
            btnCheckAll.addStyleName("checkall");
            btnCheckAll.setEnabled(false);
            btnCheckAll.setIcon(Icons.CHECKON.resource());
            btnCheckAll.addListener(Button.ClickEvent.class, this, "checkAllButtonClick");

            btnAdd = new Button(ViewProperties.getCaption("button.addLoadBalancerListener"));
            btnAdd.setDescription(ViewProperties.getCaption("description.addLoadBalancerListener"));
            btnAdd.setWidth(BUTTON_WIDTH);
            btnAdd.setIcon(Icons.ATTACH_MINI.resource());
            btnAdd.setEnabled(false);
            btnAdd.addListener(Button.ClickEvent.class, this, "addButtonClick");

            btnDelete = new Button(ViewProperties.getCaption("button.delLoadBalancerListener"));
            btnDelete.setDescription(ViewProperties.getCaption("description.delLoadBalancerListener"));
            btnDelete.setWidth(BUTTON_WIDTH);
            btnDelete.setIcon(Icons.DETACH_MINI.resource());
            btnDelete.setEnabled(false);
            btnDelete.addListener(Button.ClickEvent.class, this, "deleteButtonClick");

            btnEnable = new Button(ViewProperties.getCaption("button.enableLoadBalancerListener"));
            btnEnable.setDescription(ViewProperties.getCaption("description.enableLoadBalancerListener"));
            btnEnable.setWidth(BUTTON_WIDTH);
            btnEnable.setIcon(Icons.ENABLE_MINI.resource());
            btnEnable.setEnabled(false);
            btnEnable.addListener(Button.ClickEvent.class, this, "enableButtonClick");

            btnDisable = new Button(ViewProperties.getCaption("button.disableLoadBalancerListener"));
            btnDisable.setDescription(ViewProperties.getCaption("description.disableLoadBalancerListener"));
            btnDisable.setWidth(BUTTON_WIDTH);
            btnDisable.setIcon(Icons.DISABLE_MINI.resource());
            btnDisable.setEnabled(false);
            btnDisable.addListener(Button.ClickEvent.class, this, "disableButtonClick");

            addComponent(btnCheckAll);
            addComponent(btnAdd);
            addComponent(btnDelete);
            addComponent(btnEnable);
            addComponent(btnDisable);

            setComponentAlignment(btnCheckAll, Alignment.MIDDLE_LEFT);
            setComponentAlignment(btnAdd, Alignment.BOTTOM_LEFT);
            setComponentAlignment(btnDelete, Alignment.BOTTOM_LEFT);
            setComponentAlignment(btnEnable, Alignment.BOTTOM_LEFT);
            setComponentAlignment(btnDisable, Alignment.BOTTOM_LEFT);
            setExpandRatio(btnCheckAll, 1f);
            setExpandRatio(btnDelete, 10f);
            setExpandRatio(btnDisable, 10f);
        }

        void refresh() {
            LoadBalancerDto dto = basicInfo.loadBalancerDto;

            // ロードバランサの選択状態に応じてボタンの有効／無効を制御する
            // CloudStackはリスナーを利用しない
            if (dto == null || PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(dto.getLoadBalancer().getType())) {
                btnCheckAll.setEnabled(false);
                btnAdd.setEnabled(false);
                btnDelete.setEnabled(false);
                btnEnable.setEnabled(false);
                btnDisable.setEnabled(false);
            } else {
                btnCheckAll.setEnabled(true);
                btnAdd.setEnabled(true);
                btnDelete.setEnabled(true);
                btnEnable.setEnabled(true);
                btnDisable.setEnabled(true);
            }

            UserAuthDto auth = ViewContext.getAuthority();
            //権限に応じて操作可能なボタンを制御する
            if (!auth.isLbOperate()) {
                btnAdd.setEnabled(false);
                btnDelete.setEnabled(false);
                btnEnable.setEnabled(false);
                btnDisable.setEnabled(false);
            }
        }

        public void checkAllButtonClick(Button.ClickEvent event) {
            //全てCheckされていれば全てOFF それ以外は全てON
            boolean checkAll = true;
            for (Integer no : attachServiceTable.checkList.keySet()) {
                if (!(Boolean) attachServiceTable.checkList.get(no).getValue()) {
                    checkAll = false;
                    break;
                }
            }
            for (CheckBox chk : attachServiceTable.checkList.values()) {
                chk.setValue(!checkAll);
            }
        }

        public void addButtonClick(Button.ClickEvent event) {
            final LoadBalancerDto dto = basicInfo.loadBalancerDto;

            WinLoadBalancerConfigListener win = new WinLoadBalancerConfigListener(getApplication(), dto
                    .getLoadBalancer().getLoadBalancerNo(), null);
            win.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    MyCloudTabs myCloudTabs = ((AutoApplication) getApplication()).myCloud.myCloudTabs;
                    myCloudTabs.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    if (dto != null) {
                        for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                            LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                            if (dto.getLoadBalancer().getLoadBalancerNo()
                                    .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                                myCloudTabs.loadBalancerTable.select(itemId);
                                break;
                            }
                        }
                    }
                }
            });
            getWindow().addWindow(win);
        }

        public void deleteButtonClick(Button.ClickEvent event) {
            final LoadBalancerDto dto = basicInfo.loadBalancerDto;

            // 選択されているリスナーを取得
            List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();
            for (Entry<Integer, CheckBox> entry : attachServiceTable.checkList.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    for (LoadBalancerListener listener : dto.getLoadBalancerListeners()) {
                        if (listener.getLoadBalancerPort().equals(entry.getKey())) {
                            listeners.add(listener);
                            break;
                        }
                    }
                }
            }

            if (listeners.size() != 1) {
                // リスナーが選択されていない、または複数選択されている場合
                String message = ViewMessages.getMessage("IUI-000078");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            final LoadBalancerListener listener = listeners.get(0);

            LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
            if (status != LoadBalancerListenerStatus.STOPPED) {
                // リスナーが停止していない場合
                String message = ViewMessages.getMessage("IUI-000079");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            String message = ViewMessages.getMessage("IUI-000080", listener.getLoadBalancerPort());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    //オペレーションログ
                    AutoApplication apl = (AutoApplication) getApplication();
                    apl.doOpLog("LOAD_BALANCER", "Detach LB_Listener", null, null, listener.getLoadBalancerNo(),
                            String.valueOf(listener.getLoadBalancerPort()));

                    // リスナーの削除
                    LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
                    loadBalancerService.deleteListener(listener.getLoadBalancerNo(), listener.getLoadBalancerPort());

                    // 表示の更新
                    MyCloudTabs myCloudTabs = ((AutoApplication) getApplication()).myCloud.myCloudTabs;
                    myCloudTabs.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    if (dto != null) {
                        for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                            LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                            if (dto.getLoadBalancer().getLoadBalancerNo()
                                    .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                                myCloudTabs.loadBalancerTable.select(itemId);
                                break;
                            }
                        }
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        public void enableButtonClick(Button.ClickEvent event) {
            final LoadBalancerDto dto = basicInfo.loadBalancerDto;

            // 選択されているリスナーを取得
            final List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();
            for (Entry<Integer, CheckBox> entry : attachServiceTable.checkList.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    for (LoadBalancerListener listener : dto.getLoadBalancerListeners()) {
                        if (listener.getLoadBalancerPort().equals(entry.getKey())) {
                            listeners.add(listener);
                            break;
                        }
                    }
                }
            }

            if (listeners.isEmpty()) {
                // リスナーが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000077");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            for (LoadBalancerListener listener : listeners) {
                LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                if (status == LoadBalancerListenerStatus.STARTING || status == LoadBalancerListenerStatus.CONFIGURING
                        || status == LoadBalancerListenerStatus.STOPPING
                        || status == LoadBalancerListenerStatus.WARNING) {
                    // リスナーを有効にできないステータスの場合
                    // TODO: 振り分け状態のリファクタリング
                    String status2;
                    if (status == LoadBalancerListenerStatus.STOPPED) {
                        status2 = "DISABLE";
                    } else if (status == LoadBalancerListenerStatus.RUNNING) {
                        status2 = "ENABLE";
                    } else if (status == LoadBalancerListenerStatus.STARTING
                            || status == LoadBalancerListenerStatus.STOPPING) {
                        status2 = "CONFIGURING";
                    } else {
                        status2 = status.toString();
                    }
                    status2 = status2.substring(0, 1).toUpperCase() + status2.substring(1).toLowerCase();

                    String message = ViewMessages.getMessage("IUI-000087", status2);
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            final List<Integer> loadBalancerPorts = new ArrayList<Integer>();
            for (LoadBalancerListener listener : listeners) {
                loadBalancerPorts.add(listener.getLoadBalancerPort());
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < loadBalancerPorts.size(); i++) {
                sb.append(loadBalancerPorts.get(i));
                if (i < loadBalancerPorts.size() - 1) {
                    sb.append(",");
                }
            }
            String message = ViewMessages.getMessage("IUI-000081", sb.toString());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    //オペレーションログ
                    AutoApplication apl = (AutoApplication) getApplication();
                    apl.doOpLog("LOAD_BALANCER", "Enable LB_Listener", null, null, dto.getLoadBalancer()
                            .getLoadBalancerNo(), String.valueOf(loadBalancerPorts.size()));

                    // リスナーの有効化
                    ProcessService processService = BeanContext.getBean(ProcessService.class);
                    processService.startLoadBalancerListeners(dto.getLoadBalancer().getFarmNo(), dto.getLoadBalancer()
                            .getLoadBalancerNo(), loadBalancerPorts);

                    // 表示の更新
                    MyCloudTabs myCloudTabs = ((AutoApplication) getApplication()).myCloud.myCloudTabs;
                    myCloudTabs.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    if (dto != null) {
                        for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                            LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                            if (dto.getLoadBalancer().getLoadBalancerNo()
                                    .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                                myCloudTabs.loadBalancerTable.select(itemId);
                                break;
                            }
                        }
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        public void disableButtonClick(Button.ClickEvent event) {
            final LoadBalancerDto dto = basicInfo.loadBalancerDto;

            // 選択されているリスナーを取得
            final List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();
            for (Entry<Integer, CheckBox> entry : attachServiceTable.checkList.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    for (LoadBalancerListener listener : dto.getLoadBalancerListeners()) {
                        if (listener.getLoadBalancerPort().equals(entry.getKey())) {
                            listeners.add(listener);
                            break;
                        }
                    }
                }
            }

            if (listeners.isEmpty()) {
                // リスナーが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000077");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            for (LoadBalancerListener listener : listeners) {
                LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                if (status == LoadBalancerListenerStatus.STARTING || status == LoadBalancerListenerStatus.CONFIGURING
                        || status == LoadBalancerListenerStatus.STOPPING) {
                    // リスナーを無効有効にできないステータスの場合
                    // TODO: 振り分け状態のリファクタリング
                    String status2;
                    if (status == LoadBalancerListenerStatus.STOPPED) {
                        status2 = "DISABLE";
                    } else if (status == LoadBalancerListenerStatus.RUNNING) {
                        status2 = "ENABLE";
                    } else if (status == LoadBalancerListenerStatus.STARTING
                            || status == LoadBalancerListenerStatus.STOPPING) {
                        status2 = "CONFIGURING";
                    } else {
                        status2 = status.toString();
                    }
                    status2 = status2.substring(0, 1).toUpperCase() + status2.substring(1).toLowerCase();

                    String message = ViewMessages.getMessage("IUI-000088", status2);
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }

            final List<Integer> loadBalancerPorts = new ArrayList<Integer>();
            for (LoadBalancerListener listener : listeners) {
                loadBalancerPorts.add(listener.getLoadBalancerPort());
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < loadBalancerPorts.size(); i++) {
                sb.append(loadBalancerPorts.get(i));
                if (i < loadBalancerPorts.size() - 1) {
                    sb.append(",");
                }
            }
            String message = ViewMessages.getMessage("IUI-000082", sb.toString());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    //オペレーションログ
                    AutoApplication apl = (AutoApplication) getApplication();
                    apl.doOpLog("LOAD_BALANCER", "Disable LB_Listener", null, null, dto.getLoadBalancer()
                            .getLoadBalancerNo(), String.valueOf(loadBalancerPorts.size()));

                    // リスナーの無効化
                    ProcessService processService = BeanContext.getBean(ProcessService.class);
                    processService.stopLoadBalancerListeners(dto.getLoadBalancer().getFarmNo(), dto.getLoadBalancer()
                            .getLoadBalancerNo(), loadBalancerPorts);

                    // 表示の更新
                    MyCloudTabs myCloudTabs = ((AutoApplication) getApplication()).myCloud.myCloudTabs;
                    myCloudTabs.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    if (dto != null) {
                        for (Object itemId : myCloudTabs.loadBalancerTable.getItemIds()) {
                            LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                            if (dto.getLoadBalancer().getLoadBalancerNo()
                                    .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                                myCloudTabs.loadBalancerTable.select(itemId);
                                break;
                            }
                        }
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        public void refreshTable(Component component) {
            MyCloudTabs myCloudTabs = null;
            while (component != null) {
                if (component instanceof MyCloudTabs) {
                    myCloudTabs = (MyCloudTabs) component;
                    break;
                }
                component = component.getParent();
            }
            myCloudTabs.refreshTableOnly();
        }
    }

    public void initializeData() {
        attachServiceTable.getContainerDataSource().removeAllItems();
        loadBalancerOpe.refresh();

        AutoApplication ap = (AutoApplication) getApplication();
        LoadBalancerDto dto = (LoadBalancerDto) ap.myCloud.myCloudTabs.loadBalancerTable.getValue();
        basicInfo.setItem(dto);
    }

}
