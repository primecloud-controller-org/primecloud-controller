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

import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.data.InstanceDtoContainer;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.CommonUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import com.vaadin.data.Container;
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
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * サービスView下部の基本情報を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ServiceDescBasic extends Panel {

    BasicInfoOpe left = new BasicInfoOpe();

    AttachServersOpe right = new AttachServersOpe("", null);

    ServiceSvrOperation serverOpe = new ServiceSvrOperation();

    public ServiceDescBasic() {

        addStyleName(Reindeer.PANEL_LIGHT);

        setHeight("100%");

        HorizontalLayout hlPanels = new HorizontalLayout();
        hlPanels.setWidth("100%");
        hlPanels.setHeight("100%");
        hlPanels.setMargin(true);
        hlPanels.setSpacing(true);
        hlPanels.addStyleName("service-desc-basic");
        setContent(hlPanels);

        left.setWidth("100%");
        right.setWidth("100%");

        //表同士の間隔をあける
        Label padding = new Label(" ");
        padding.setWidth("7px");
        padding.setHeight("99%");
        padding.addStyleName("desc-padding");

        Label padding2 = new Label("");
        padding2.setWidth("1px");

        VerticalLayout layLeft = new VerticalLayout();
        layLeft.setMargin(false);
        layLeft.setSpacing(false);
        layLeft.setWidth("100%");
        layLeft.setHeight("100%");
        layLeft.addComponent(left);
        layLeft.setExpandRatio(left, 1.0f);

        VerticalLayout layRight = new VerticalLayout();
        layRight.setMargin(false);
        layRight.setSpacing(false);
        layRight.setWidth("100%");
        layRight.setHeight("100%");
        layRight.addComponent(right);
        layRight.addComponent(serverOpe);
        layRight.setExpandRatio(right, 1.0f);

        hlPanels.addComponent(layLeft);
        hlPanels.addComponent(padding);
        hlPanels.addComponent(padding2);
        hlPanels.addComponent(layRight);
        hlPanels.setExpandRatio(layLeft, 40);
        hlPanels.setExpandRatio(layRight, 60);
    }

    //右側サーバ一覧パネル
    class AttachServersOpe extends Table {

        final String COLUMN_HEIGHT = "28px";

        //項目名
        String[] COLNAME = { null, ViewProperties.getCaption("field.serverName"),
                ViewProperties.getCaption("field.managementUrl"), ViewProperties.getCaption("field.serviceStatus"),
                ViewProperties.getCaption("field.platform") };

        HashMap<Long, CheckBox> checkList = new HashMap<Long, CheckBox>();

        public AttachServersOpe(String caption, Container dataSource) {
            super(caption, dataSource);
            setIcon(Icons.SERVERTAB.resource());

            addGeneratedColumn("check", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto p = (InstanceDto) itemId;
                    Long no = p.getInstance().getInstanceNo();

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

            addGeneratedColumn("instanceName", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto p = (InstanceDto) itemId;
                    Label slbl = new Label(p.getInstance().getInstanceName());
                    return slbl;

                }
            });

            addGeneratedColumn("urlIcon", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto dto = (InstanceDto) itemId;
                    // 管理用
                    ComponentType componentType = left.component.getComponentType();
                    String type = componentType.getComponentTypeName();

                    //サービスが起動していれば有効にしてリンクを追加する
                    String status = "";
                    String url = "";

                    for (ComponentInstanceDto componentInstance : left.component.getComponentInstances()) {
                        if (componentInstance.getComponentInstance().getInstanceNo()
                                .equals(dto.getInstance().getInstanceNo())) {
                            status = componentInstance.getComponentInstance().getStatus();
                            url = componentInstance.getUrl();
                            break;
                        }
                    }
                    Icons icon = Icons.fromName(type);

                    //MySQLならMasterとSlaveでアイコンを変える
                    if (MySQLConstants.COMPONENT_TYPE_NAME.equals(type)) {
                        Long masterInstanceNo = null;
                        for (InstanceConfig config : dto.getInstanceConfigs()) {
                            if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(config.getConfigName())) {
                                if (StringUtils.isEmpty(config.getConfigValue())) {
                                    masterInstanceNo = config.getInstanceNo();
                                    break;
                                }
                            }
                        }
                        if (masterInstanceNo != null) {
                            if (masterInstanceNo.equals(dto.getInstance().getInstanceNo())) {
                                icon = Icons.MYSQL_MASTER;
                            } else {
                                icon = Icons.MYSQL_SLAVE;
                            }
                        } else {
                            icon = Icons.MYSQL_SLAVE;
                        }
                    }

                    Link slbl = new Link(ViewProperties.getCaption("field.managementLink"), new ExternalResource(url));
                    slbl.setTargetName("_blank");
                    slbl.setIcon(icon.resource());
                    slbl.setHeight(COLUMN_HEIGHT);
                    slbl.setEnabled(false);

                    if (status.equals(ComponentInstanceStatus.RUNNING.toString())) {
                        slbl.setDescription(url);
                        slbl.setEnabled(true);
                    }

                    return slbl;

                }
            });

            addGeneratedColumn("status", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto p = (InstanceDto) itemId;

                    MyCloudTabs myCloudTabs = null;
                    Component component = AttachServersOpe.this;
                    while (component != null) {
                        if (component instanceof MyCloudTabs) {
                            myCloudTabs = (MyCloudTabs) component;
                            break;
                        }
                        component = component.getParent();
                    }

                    ComponentDto componentDto = (ComponentDto) myCloudTabs.serviceTable.getValue();
                    String status = "";
                    for (ComponentInstanceDto componentInstance : componentDto.getComponentInstances()) {
                        if (componentInstance.getComponentInstance().getInstanceNo()
                                .equals(p.getInstance().getInstanceNo())) {
                            status = componentInstance.getComponentInstance().getStatus();
                            break;
                        }
                    }

                    String a = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();

                    Icons icon = Icons.fromName(a);
                    Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(ServiceDescBasic.this, icon)
                            + "\"><div>" + a + "</div>", Label.CONTENT_XHTML);
                    slbl.setHeight(COLUMN_HEIGHT);
                    return slbl;

                }
            });

            addGeneratedColumn("platform", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto p = (InstanceDto) itemId;
                    PlatformDto platform = p.getPlatform();

                    //プラットフォームアイコン名の取得
                    Icons icon = CommonUtils.getPlatformIcon(platform);

                    String description = platform.getPlatform().getPlatformSimplenameDisp();

                    Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(ServiceDescBasic.this, icon)
                            + "\"><div>" + description + "</div>", Label.CONTENT_XHTML);
                    slbl.setHeight(COLUMN_HEIGHT);

                    return slbl;

                }
            });

            //            setVisible(false);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {

                    InstanceDto dto = (InstanceDto) itemId;

                    if (propertyId == null) {
                        return "";
                    } else {
                        String ret = propertyId.toString().toLowerCase();
                        Long no = dto.getInstance().getInstanceNo();
                        if (checkList.containsKey(no) && (Boolean) checkList.get(no).getValue()) {
                            ret += " v-selected";
                        }
                        return ret;
                    }
                }
            });

            setColumnExpandRatio("instanceName", 100);

            addListener(new ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    InstanceDto dto = (InstanceDto) event.getItemId();
                    Long no = dto.getInstance().getInstanceNo();
                    if (checkList.containsKey(no)) {
                        checkList.get(no).setValue(!(Boolean) checkList.get(no).getValue());
                    }
//                    refresh(getContainerDataSource());
                }
            });

        }

        @Override
        public void setContainerDataSource(Container newDataSource) {
            super.setContainerDataSource(newDataSource);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("service-desc-basic-server");
            setCaption(ViewProperties.getCaption("table.serviceServers"));
            setHeight("100%");
            setSortDisabled(true);
            //            setSelectable(true);
            //            setMultiSelect(true);
            setImmediate(true);
            setVisible(true);

        }

        //        public void setHeaders(){
        //            //項目名設定
        //            setColumnHeaders(COLNAME);
        //        }

        public void refresh(Container dataSource) {
            refresh(dataSource, false);
        }

        public void refresh(Container dataSource, boolean clearCheckBox) {
            if (clearCheckBox) {
                checkList.clear();
            }
            setContainerDataSource(dataSource);
            setVisibleColumns(InstanceDtoContainer.SERVICE_DESC);
            if (dataSource != null && dataSource.size() > 0) {
                setColumnHeaders(COLNAME);
            }
            serverOpe.refresh();
        }
    }

    class BasicInfoOpe extends Panel {
        Label serviceName;

        VerticalLayout vlLoadBalancer;

        Label serviceDetail;

        Label status;

        Label comment;

        ComponentDto component;

        final String COLUMN_HEIGHT = "30px";

        //項目名
        String[] CAPNAME = {
                    ViewProperties.getCaption("field.serviceName"),
                    ViewProperties.getCaption("field.service"),
                    ViewProperties.getCaption("field.status"),
                    ViewProperties.getCaption("field.comment"),
                    ViewProperties.getCaption("field.loadBalancer"),
            };

        GridLayout layout;

        BasicInfoOpe() {

            setCaption(ViewProperties.getCaption("table.serviceBasicInfo"));
            setHeight("100%");
            setStyleName("service-desc-basic-panel");

            VerticalLayout vlay = (VerticalLayout) getContent();
            vlay.setStyleName("service-desc-basic-panel");
            vlay.setMargin(true);

            layout = new GridLayout(2, CAPNAME.length);
            layout.setWidth("100%");
            layout.setStyleName("service-desc-basic-info");
            layout.setColumnExpandRatio(0, 35);
            layout.setColumnExpandRatio(1, 65);
            vlay.addComponent(layout);

            //項目名設定
            for (int i = 0; i < CAPNAME.length; i++) {
                Label lbl1 = new Label(CAPNAME[i], Label.CONTENT_XHTML);
                Label lbl2 = new Label("");
                lbl1.setHeight(COLUMN_HEIGHT);
                layout.addComponent(lbl1, 0, i);
                layout.addComponent(lbl2, 1, i);
            }

        }

        public void setItem(ComponentDto dto) {
            component = dto;

            if (dto != null) {
                int line = 0;

                jp.primecloud.auto.entity.crud.Component component = dto.getComponent();
                ComponentType componentType = dto.getComponentType();

                //サービス名
                serviceName = new Label(component.getComponentName(), Label.CONTENT_TEXT);
                layout.removeComponent(1, line);
                layout.addComponent(serviceName, 1, line++);

                //サービス詳細
                Icons nameIcon = Icons.fromName(componentType.getComponentTypeName());
                String name = componentType.getComponentTypeNameDisp();

                serviceDetail = new Label("<img src=\"" + VaadinUtils.getIconPath(ServiceDescBasic.this, nameIcon) + "\"><div>"
                        + name + "</div>", Label.CONTENT_XHTML);
                layout.removeComponent(1, line);
                layout.addComponent(serviceDetail, 1, line++);

                //ステータス
                String stat = dto.getStatus().substring(0, 1).toUpperCase() + dto.getStatus().substring(1).toLowerCase();
                Icons icon = Icons.fromName(stat);
                status = new Label("<img src=\"" + VaadinUtils.getIconPath(ServiceDescBasic.this, icon) + "\"><div>"
                        + stat + "</div>", Label.CONTENT_XHTML);
                layout.removeComponent(1, line);
                layout.addComponent(status, 1, line++);

                //コメント
                comment = new Label(component.getComment(), Label.CONTENT_XHTML);
                layout.removeComponent(1, line);
                layout.addComponent(comment, 1, line++);

                //ロードバランサ
                vlLoadBalancer = new VerticalLayout();
                vlLoadBalancer.setSpacing(false);

                MyCloudTabs myCloudTabs = null;
                Component c = BasicInfoOpe.this;
                while (c != null) {
                    if (c instanceof MyCloudTabs) {
                        myCloudTabs = (MyCloudTabs) c;
                        break;
                    }
                    c = c.getParent();
                }
                for (LoadBalancerDto lbDto : (Collection<LoadBalancerDto>) myCloudTabs.loadBalancerTable.getItemIds()) {
                    if (dto.getComponent().getComponentNo().equals(lbDto.getLoadBalancer().getComponentNo())) {
                        vlLoadBalancer.addComponent(getLoadBalancerButton(lbDto));
                    }
                }

                layout.removeComponent(1, line);
                layout.addComponent(vlLoadBalancer, 1, line++);

            } else {
                int line = 0;
                //サービス名
                serviceName = new Label("", Label.CONTENT_XHTML);
                layout.removeComponent(1, line);
                layout.addComponent(serviceName, 1, line++);

                //サービス詳細
                serviceDetail = new Label("", Label.CONTENT_XHTML);
                layout.removeComponent(1, line);
                layout.addComponent(serviceDetail, 1, line++);

                //ステータス
                status = new Label("", Label.CONTENT_XHTML);
                layout.removeComponent(1, line);
                layout.addComponent(status, 1, line++);

                //コメント
                comment = new Label("", Label.CONTENT_XHTML);
                layout.removeComponent(1, line);
                layout.addComponent(comment, 1, line++);

                //ロードバランサ
                vlLoadBalancer = new VerticalLayout();
                layout.removeComponent(1, line);
                layout.addComponent(vlLoadBalancer, 1, line++);

            }
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
            Component c = ServiceDescBasic.this;
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
    }

    public void initializeData() {
        right.getContainerDataSource().removeAllItems();
        serverOpe.refresh();
        left.setItem(null);
    }

    public class ServiceSvrOperation extends HorizontalLayout {

        Button btnCheckAll;

        Button btnStart;

        Button btnStop;

        ServiceSvrOperation() {
            addStyleName("operation-buttons");
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

            btnStart = new Button(ViewProperties.getCaption("button.startService"));
            btnStart.setDescription(ViewProperties.getCaption("description.startService"));
            btnStart.setWidth("90px");
            btnStart.setIcon(Icons.PLAYMINI.resource());
            btnStart.setEnabled(false);
            btnStart.addListener(Button.ClickEvent.class, this, "playButtonClick");

            btnStop = new Button(ViewProperties.getCaption("button.stopService"));
            btnStop.setDescription(ViewProperties.getCaption("description.stopService"));
            btnStop.setWidth("90px");
            btnStop.setIcon(Icons.STOPMINI.resource());
            btnStop.setEnabled(false);
            btnStop.addListener(Button.ClickEvent.class, this, "stopButtonClick");

            addComponent(btnCheckAll);
            addComponent(btnStart);
            addComponent(btnStop);

            setComponentAlignment(btnCheckAll, Alignment.MIDDLE_LEFT);
            setComponentAlignment(btnStart, Alignment.BOTTOM_RIGHT);
            setComponentAlignment(btnStop, Alignment.BOTTOM_RIGHT);
            setExpandRatio(btnCheckAll, 1.0f);
        }

        void refresh() {
            Container container = right.getContainerDataSource();
            if (container != null && container.getItemIds().size() > 0) {

                btnCheckAll.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(true);

            } else {
                btnCheckAll.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(false);
            }

            UserAuthDto auth = ViewContext.getAuthority();
            //権限に応じて操作可能なボタンを制御する
            if (!auth.isServiceOperate()) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(false);
            }
        }

        public void checkAllButtonClick(Button.ClickEvent event) {

            //全てCheckされていれば全てOFF それ以外は全てON
            boolean checkAll = true;
            for (Long no : right.checkList.keySet()) {
                if (!(Boolean) right.checkList.get(no).getValue()) {
                    checkAll = false;
                    break;
                }
            }
            for (CheckBox chk : right.checkList.values()) {
                chk.setValue(!checkAll);
            }

//            right.refresh(right.getContainerDataSource());
        }

        public void playButtonClick(Button.ClickEvent event) {

            //Running , Warningのサーバが含まれていないかチェック
            final ComponentDto dto = left.component;
            //確認メッセージ
            final List<Long> instanceNos = new ArrayList<Long>();
            final Map<Long, InstanceDto> instanceMap = new HashMap<Long, InstanceDto>();
            for (Object itemId : right.getItemIds()) {
                InstanceDto instance = (InstanceDto) itemId;
                Long no = instance.getInstance().getInstanceNo();
                if (right.checkList.containsKey(no) && (Boolean) right.checkList.get(no).getValue()) {
                    InstanceDto tmpDto = (InstanceDto) itemId;
                    instanceNos.add(tmpDto.getInstance().getInstanceNo());
                    instanceMap.put(tmpDto.getInstance().getInstanceNo(), tmpDto);
                }
            }

            //選択されているサーバがあるかチェック
            if (instanceNos.isEmpty()) {
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            } else {
                //サーバステータスのステータスにStarting,Configuring,Stoppingが無いかを確認
                boolean skipServer = false;
                for (Long instanceNo : instanceNos) {
                    for (ComponentInstanceDto componentInstanceDto : dto.getComponentInstances()) {
                        if (instanceNo.equals(componentInstanceDto.getComponentInstance().getInstanceNo())) {
                            String status = componentInstanceDto.getComponentInstance().getStatus();
                            //コンポーネント側からのステータス確認
                            if (status.equals(ComponentInstanceStatus.CONFIGURING.toString()) ||
                                    status.equals(ComponentInstanceStatus.STARTING.toString()) ||
                                    status.equals(ComponentInstanceStatus.STOPPING.toString()) ||
                                    status.equals(ComponentInstanceStatus.WARNING.toString())){
                                String message = ViewMessages.getMessage("IUI-000044", new Object[] {StringUtils.capitalize(status.toLowerCase()) });
                                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                                getApplication().getMainWindow().addWindow(dialog);
                                return;
                            }
                            InstanceDto tmpDto = instanceMap.get(instanceNo);
                            PlatformDto platform = tmpDto.getPlatform();
                            ProcessService processService = BeanContext.getBean(ProcessService.class);
                            boolean vpc = false;
                            String subnetId = null;
                            boolean subnetErrFlg;
                            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatform().getPlatformType())) {
                                // サブネットチェック
                                vpc = platform.getPlatformAws().getVpc();
                                subnetId = tmpDto.getAwsInstance().getSubnetId();
                                subnetErrFlg = processService.checkSubnet(platform.getPlatform().getPlatformType(), vpc, subnetId);
                                if (subnetErrFlg == true) {
                                    //EC2+VPCの場合、サブネットを設定しないと起動不可
                                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                                        ViewMessages.getMessage("IUI-000112", tmpDto.getInstance().getInstanceName()));
                                getApplication().getMainWindow().addWindow(dialog);
                                return;
                            }
                            }
                            if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatform().getPlatformType())) {
                                // サブネットチェック
                                subnetId = tmpDto.getAzureInstance().getSubnetId();
                                subnetErrFlg = processService.checkSubnet(platform.getPlatform().getPlatformType(), vpc, subnetId);
                                if (subnetErrFlg == true) {
                                    // サブネットを設定しないと起動不可
                                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                                            ViewMessages.getMessage("IUI-000112", tmpDto.getInstance().getInstanceName()));
                                    getApplication().getMainWindow().addWindow(dialog);
                                    return;
                                }
                                // インスタンス起動チェック（同時起動）
                                HashMap<String, Boolean> flgMap = new HashMap<String, Boolean>();
                                flgMap = processService.checkStartupAll(platform.getPlatform().getPlatformType(),
                                        tmpDto.getAzureInstance().getInstanceName(),
                                        skipServer);
                                skipServer = flgMap.get("skipServer");
                                boolean startupAllErrFlg;
                                startupAllErrFlg = flgMap.get("startupAllErrFlg");
                                if (startupAllErrFlg == true) {
                                    // インスタンス作成中のものがあった場合は、起動不可
                                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                                            ViewMessages.getMessage("IUI-000134", tmpDto.getInstance()
                                                    .getInstanceName()));
                                    getApplication().getMainWindow().addWindow(dialog);
                                    return;
                                }
                                // インスタンス起動チェック（個別起動）
                                boolean startupErrFlg;
                                startupErrFlg = processService.checkStartup(platform.getPlatform().getPlatformType(),
                                        tmpDto.getAzureInstance().getInstanceName(),
                                        tmpDto.getAzureInstance().getInstanceNo());
                                if (startupErrFlg == true) {
                                            // インスタンス作成中のものがあった場合は、起動不可
                                    // 同一インスタンスNoは、除外する
                                            DialogConfirm dialog = new DialogConfirm(
                                                    ViewProperties.getCaption("dialog.error"), ViewMessages.getMessage(
                                                            "IUI-000134", tmpDto.getInstance().getInstanceName()));
                                            getApplication().getMainWindow().addWindow(dialog);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }

            String message = ViewMessages.getMessage("IUI-000051", new Object[] { instanceNos.size(),
                    dto.getComponent().getComponentName() });

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
                    apl.doOpLog("SERVICE", "Start Service", null, dto.getComponent().getComponentNo(), null, null);

                    //開始処理
                    ProcessService processService = BeanContext.getBean(ProcessService.class);
                    Long farmNo = dto.getComponent().getFarmNo();
                    Long componentNo = dto.getComponent().getComponentNo();
                    processService.startComponents(farmNo, componentNo, instanceNos);

                    //画面のリフレッシュ
                    refresh(right);

                }
            });

            getApplication().getMainWindow().addWindow(dialog);
        }

        public void stopButtonClick(Button.ClickEvent event) {

            final List<Long> instanceNos = new ArrayList<Long>();
            final List<InstanceDto> instanceDtos = new ArrayList<InstanceDto>();

            for (Object itemId : right.getItemIds()) {
                InstanceDto instance = (InstanceDto) itemId;
                Long no = instance.getInstance().getInstanceNo();
                if (right.checkList.containsKey(no) && (Boolean) right.checkList.get(no).getValue()) {
                    instanceNos.add(((InstanceDto) itemId).getInstance().getInstanceNo());
                    instanceDtos.add((InstanceDto) itemId);
                }

            }
            final ComponentDto dto = left.component;

            boolean isCheckbox = true;

            //選択されているサーバがあるかチェック
            if (instanceNos.isEmpty()) {
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            } else {
                //サーバステータスのステータスにStarting,Configuring,Stoppingが無いかを確認
                for (InstanceDto instanceDto : instanceDtos) {
                    for (ComponentInstanceDto componentInstanceDto : dto.getComponentInstances()) {
                        if (instanceDto.getInstance().getInstanceNo().equals(componentInstanceDto.getComponentInstance().getInstanceNo())) {
                            String status = componentInstanceDto.getComponentInstance().getStatus();
                            if (status.equals(ComponentInstanceStatus.CONFIGURING.toString()) ||
                                    status.equals(ComponentInstanceStatus.STARTING.toString()) ||
                                    status.equals(ComponentInstanceStatus.STOPPING.toString())) {
                                String message = ViewMessages.getMessage("IUI-000045", new Object[] {StringUtils.capitalize(status.toLowerCase()) });
                                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                                getApplication().getMainWindow().addWindow(dialog);
                                return;
                            }
                        }
                    }

                    //インスタンスに含まれる選択以外のステータスを確認
                    for (ComponentInstanceDto componentInstanceDto2 : instanceDto.getComponentInstances()) {
                        //同じコンポーネントの場合は無視する
                        if (componentInstanceDto2.getComponentInstance().getComponentNo().equals(dto.getComponent().getComponentNo())) {
                            continue;
                        }
                        if (!componentInstanceDto2.getComponentInstance().getStatus().equals(ComponentInstanceStatus.STOPPED.toString())) {
                            isCheckbox = false;
                            break;
                        }
                    }
                }
            }

            String message = ViewMessages.getMessage("IUI-000052", new Object[] { instanceNos.size(),
                    dto.getComponent().getComponentName() });

            HorizontalLayout optionLayout = new HorizontalLayout();
            final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000033"), false);
            checkBox.setImmediate(true);
            checkBox.setEnabled(isCheckbox);
            checkBox.setDescription(ViewProperties.getCaption("description.stopService.withServerStop"));
            optionLayout.addComponent(checkBox);

            //確認メッセージ
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel, optionLayout);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        return;
                    }

                    ProcessService processService = BeanContext.getBean(ProcessService.class);
                    Long farmNo = dto.getComponent().getFarmNo();
                    Long componentNo = dto.getComponent().getComponentNo();
                    boolean stopInstance = (Boolean) checkBox.getValue();

                    //オペレーションログ
                    AutoApplication apl = (AutoApplication) getApplication();
                    apl.doOpLog("SERVICE", "Stop Service", null, dto.getComponent().getComponentNo(), null, String.valueOf(stopInstance));

                    //停止処理
                    processService.stopComponents(farmNo, componentNo, instanceNos, stopInstance);

                    //画面のリフレッシュ
                    refresh(right);

                }
            });
            getApplication().getMainWindow().addWindow(dialog);

        }

        public void refresh(Component component) {
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

}
