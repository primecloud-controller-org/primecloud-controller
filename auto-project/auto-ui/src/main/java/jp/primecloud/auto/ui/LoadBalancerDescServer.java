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

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.CloudstackLoadBalancer;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.AwsDescribeService;
import jp.primecloud.auto.service.LoadBalancerService;
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
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.Subnet;
import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * ロードバランサ画面下部の詳細情報/割り当てサーバ情報の生成を行います。
 * </p>
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class LoadBalancerDescServer extends Panel {

    private MainView sender;

    LoadBalancerDetailInfo loadBalancerInfo = new LoadBalancerDetailInfo();

    AttachSeriviceServerTable attachServiceServerTable = new AttachSeriviceServerTable("", null);

    private LoadbalancerServerOperation loadBalancerOpe = new LoadbalancerServerOperation();

    LoadBalancerDescServer(MainView sender) {
        this.sender = sender;

        addStyleName(Reindeer.PANEL_LIGHT);
        setHeight("100%");

        VerticalLayout panel = (VerticalLayout) getContent();
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.setMargin(true);
        panel.setSpacing(false);
        panel.addStyleName("loadbalancer-desc-basic");

        HorizontalLayout hlLayout = new HorizontalLayout();
        hlLayout.setWidth("100%");
        hlLayout.setHeight("100%");
        hlLayout.setMargin(true);
        hlLayout.setSpacing(true);
        hlLayout.addStyleName("loadbalancer-desc-basic");

        VerticalLayout vLeftLayout = new VerticalLayout();

        vLeftLayout.setWidth("100%");
        vLeftLayout.setHeight("100%");
        vLeftLayout.setMargin(true, false, false, false);
        vLeftLayout.setSpacing(false);
        vLeftLayout.addComponent(loadBalancerInfo);
        vLeftLayout.setExpandRatio(loadBalancerInfo, 10);

        VerticalLayout vRightLayout = new VerticalLayout();
        vRightLayout.setWidth("100%");
        vRightLayout.setHeight("100%");
        vRightLayout.setMargin(false);
        vRightLayout.setSpacing(false);
        vRightLayout.addStyleName("loadbalancer-desc-server-right");
        vRightLayout.addComponent(attachServiceServerTable);
        vRightLayout.addComponent(loadBalancerOpe);
        vRightLayout.setExpandRatio(attachServiceServerTable, 100);

        vRightLayout.setWidth("100%");

        //表同士の間隔をあける
        Label padding = new Label(" ");
        padding.setWidth("7px");
        padding.setHeight("99%");
        padding.addStyleName("desc-padding");

        Label padding2 = new Label("");
        padding2.setWidth("1px");

        hlLayout.addComponent(vLeftLayout);
        hlLayout.addComponent(padding);
        hlLayout.addComponent(padding2);
        hlLayout.addComponent(vRightLayout);
        hlLayout.setExpandRatio(vLeftLayout, 48);
        hlLayout.setExpandRatio(vRightLayout, 52);

        panel.addComponent(hlLayout);

        attachServiceServerTable.refresh(null);
    }

    class LoadBalancerDetailInfo extends Panel {

        private LoadBalancerDto loadBalancerDto;

        private final String COLUMN_HEIGHT = "30px";

        //項目名
        private final String[] CAPTION_FIELD_DEF = { "field.loadBalancerName", "field.loadBalancerService",
                "field.fqdn", "field.loadBalancerHostname", "field.status", "field.platform", "field.loadBalancerType",
                "field.subnet", "field.comment", "field.checkProtocol", "field.checkPort", "field.checkPath",
                "field.checkTimeout", "field.checkInterval", "field.checkDownThreshold", "field.checkRecoverThreshold", };

        //項目名
        private final String[] CAPTION_FIELD_CLOUDSTACK = { "field.loadBalancerName", "field.loadBalancerService",
                "field.fqdn", "field.loadBalancerHostname", "field.status", "field.platform", "field.loadBalancerType",
                "field.comment", "field.algorithm", "field.publicip", "field.publicport", "field.privateport", };

        private HashMap<String, Label> displayLabels = new HashMap<String, Label>();

        private GridLayout layout;

        LoadBalancerDetailInfo() {

            setCaption(ViewProperties.getCaption("table.loadBalancerDetailInfo"));
            setHeight("100%");
            setStyleName("loadbalancer-desc-basic-panel");

            VerticalLayout vlay = (VerticalLayout) getContent();
            vlay.setStyleName("loadbalancer-desc-basic-panel");
            vlay.setMargin(true);

            //ロードバランサー詳細情報表示
            layout = new GridLayout(2, CAPTION_FIELD_DEF.length);
            layout.setMargin(false);
            layout.setSpacing(false);
            layout.setWidth("100%");
            layout.setStyleName("loadbalancer-desc-basic-info");
            layout.setColumnExpandRatio(0, 45);
            layout.setColumnExpandRatio(1, 55);

            vlay.addComponent(layout);

            initDisplayLabels(CAPTION_FIELD_DEF);
        }

        private void initDisplayLabels(String[] captionField) {
            for (int i = 0; i < captionField.length; i++) {
                //項目名
                Label lbl1 = new Label(ViewProperties.getCaption(captionField[i]), Label.CONTENT_TEXT);
                lbl1.setHeight(COLUMN_HEIGHT);
                layout.addComponent(lbl1, 0, i);

                //表示内容ラベル
                Label lbl2 = new Label("", Label.CONTENT_XHTML);
                displayLabels.put(captionField[i], lbl2);
                layout.addComponent(lbl2, 1, i);
            }
        }

        private void resetLabels() {
            for (Label lbl : displayLabels.values()) {
                lbl.setValue("");
            }
        }

        private void reMakeLayout(String[] captionField) {
            VerticalLayout vlay = (VerticalLayout) getContent();
            vlay.removeAllComponents();

            //ロードバランサー詳細情報表示
            layout = new GridLayout(2, captionField.length);
            layout.setMargin(false);
            layout.setSpacing(false);
            layout.setWidth("100%");
            layout.setStyleName("loadbalancer-desc-basic-info");
            layout.setColumnExpandRatio(0, 45);
            layout.setColumnExpandRatio(1, 55);

            vlay.addComponent(layout);

            initDisplayLabels(captionField);
        }

        public void setItem(LoadBalancerDto dto) {
            //前回表示時の値をクリア
            resetLabels();

            loadBalancerDto = dto;

            if (dto != null) {

                if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(dto.getLoadBalancer().getType())) {
                    reMakeLayout(CAPTION_FIELD_CLOUDSTACK);
                } else {
                    reMakeLayout(CAPTION_FIELD_DEF);
                }

                Component me = LoadBalancerDescServer.this;

                LoadBalancer lb = dto.getLoadBalancer();

                //ロードバランサー名
                displayLabels.get("field.loadBalancerName").setValue(lb.getLoadBalancerName());

                // 割り当てサービス
                ComponentDto componentDto = null;
                for (ComponentDto cDto : (Collection<ComponentDto>) sender.servicePanel.serviceTable.getItemIds()) {
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
                if (hostName != null) {
                    displayLabels.get("field.loadBalancerHostname").setValue(hostName);
                }

                //ステータス
                LoadBalancerStatus status = LoadBalancerStatus.fromStatus(lb.getStatus());
                String a = status.name().substring(0, 1).toUpperCase() + status.name().substring(1).toLowerCase();
                displayLabels.get("field.status").setValue(IconUtils.createImageTag(me, Icons.fromName(a), a));

                //プラットフォーム
                PlatformDto platfromDto = dto.getPlatform();
                //プラットフォームアイコン名の取得
                Icons icon = IconUtils.getPlatformIcon(platfromDto);
                String description = platfromDto.getPlatform().getPlatformNameDisp();
                displayLabels.get("field.platform").setValue(IconUtils.createImageTag(me, icon, description));

                //タイプ
                if (lb.getType() != null) {
                    String type = ViewProperties.getLoadBalancerType(lb.getType());
                    displayLabels.get("field.loadBalancerType").setValue(type);
                }

                //コメント
                if (lb.getComment() != null) {
                    displayLabels.get("field.comment").setValue(lb.getComment());
                }

                //サブネット
                PlatformAws platformAws = platfromDto.getPlatformAws();
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

                if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(dto.getLoadBalancer().getType())) {
                    CloudstackLoadBalancer cslb = dto.getCloudstackLoadBalancer();
                    if (cslb != null) {
                        //アルゴリズム
                        if (cslb.getAlgorithm() != null) {
                            displayLabels.get("field.algorithm").setValue(cslb.getAlgorithm());
                        }
                        //パブリックIPアドレス
                        if (cslb.getPublicip() != null) {
                            displayLabels.get("field.publicip").setValue(cslb.getPublicip());
                        }
                        //パブリックポート
                        if (cslb.getPublicport() != null) {
                            displayLabels.get("field.publicport").setValue(cslb.getPublicport());
                        }
                        //プライベートポート
                        if (cslb.getPrivateport() != null) {
                            displayLabels.get("field.privateport").setValue(cslb.getPrivateport());
                        }
                    }
                } else {
                    LoadBalancerHealthCheck hchk = dto.getLoadBalancerHealthCheck();
                    if (hchk != null) {
                        //監視プロトコル
                        if (hchk.getCheckProtocol() != null) {
                            displayLabels.get("field.checkProtocol").setValue(hchk.getCheckProtocol());
                        }

                        //監視ポート
                        if (hchk.getCheckPort() != null) {
                            displayLabels.get("field.checkPort").setValue(hchk.getCheckPort().toString());
                        }

                        //監視Path
                        if (hchk.getCheckPath() != null) {
                            displayLabels.get("field.checkPath").setValue(hchk.getCheckPath());
                        }

                        //ヘルスチェックタイムアウト時間
                        if (hchk.getCheckTimeout() != null) {
                            displayLabels.get("field.checkTimeout").setValue(hchk.getCheckTimeout().toString());
                        }

                        //ヘルスチェック間隔
                        if (hchk.getCheckInterval() != null) {
                            displayLabels.get("field.checkInterval").setValue(hchk.getCheckInterval().toString());
                        }

                        //障害閾値
                        if (hchk.getUnhealthyThreshold() != null) {
                            displayLabels.get("field.checkDownThreshold").setValue(
                                    hchk.getUnhealthyThreshold().toString());
                        }

                        //復帰閾値
                        if (hchk.getHealthyThreshold() != null) {
                            displayLabels.get("field.checkRecoverThreshold").setValue(
                                    hchk.getHealthyThreshold().toString());
                        }
                    }
                }
            }
        }

    }

    //右側割り当てサービスサーバ一覧
    class AttachSeriviceServerTable extends Table {

        private final String COLUMN_HEIGHT = "28px";

        //項目名
        private final String[] COLNAME = { null, ViewProperties.getCaption("field.serverName"),
                ViewProperties.getCaption("field.loadBalancerServerStatus"),
                ViewProperties.getCaption("field.serviceStatus") };

        private final String[] VISIBLE_COLNAME = { "check", "instanceName", "status", "serviceStatus" };

        private ComponentDto componentDto;

        private HashMap<Long, CheckBox> checkList = new HashMap<Long, CheckBox>();

        private HashMap<Long, String> statusMap = new HashMap<Long, String>();

        public AttachSeriviceServerTable(String caption, Container dataSource) {
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

                    PlatformDto platformDto = p.getPlatform();

                    //プラットフォームアイコン名の取得
                    Icons icon = IconUtils.getPlatformIcon(platformDto);

                    Label slbl = new Label(IconUtils.createImageTag(AttachSeriviceServerTable.this, icon, p
                            .getInstance().getInstanceName()), Label.CONTENT_XHTML);
                    slbl.setHeight(COLUMN_HEIGHT);

                    return slbl;
                }
            });

            addGeneratedColumn("status", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto instanceDto = (InstanceDto) itemId;
                    LoadBalancerDto loadBalancerDto = loadBalancerInfo.loadBalancerDto;

                    LoadBalancerInstance lbInstance = null;
                    for (LoadBalancerInstance lbInstance2 : loadBalancerDto.getLoadBalancerInstances()) {
                        if (lbInstance2.getInstanceNo().equals(instanceDto.getInstance().getInstanceNo())) {
                            lbInstance = lbInstance2;
                            break;
                        }
                    }

                    boolean lbInstanceEnabled = false;
                    LoadBalancerInstanceStatus lbInstanceStatus = LoadBalancerInstanceStatus.STOPPED;
                    if (lbInstance != null) {
                        lbInstanceEnabled = BooleanUtils.isTrue(lbInstance.getEnabled());
                        lbInstanceStatus = LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus());
                    }

                    LoadBalancerStatus lbStatus = LoadBalancerStatus.fromStatus(loadBalancerDto.getLoadBalancer()
                            .getStatus());
                    InstanceStatus instanceStatus = InstanceStatus.fromStatus(instanceDto.getInstance().getStatus());
                    boolean lbRunning = (lbStatus == LoadBalancerStatus.RUNNING || lbStatus == LoadBalancerStatus.CONFIGURING);
                    boolean instanceRunning = (instanceStatus == InstanceStatus.RUNNING || instanceStatus == InstanceStatus.CONFIGURING);

                    String status;
                    boolean notice = false;
                    String noticeMessage = null;

                    if (lbInstanceStatus == LoadBalancerInstanceStatus.WARNING) {
                        status = "Warning";
                    } else if (lbRunning && instanceRunning) {
                        // ロードバランサーとサーバがどちらとものRunningのとき
                        if (lbInstanceEnabled) {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.RUNNING) {
                                status = "Enable";
                            } else {
                                status = "Configuring";
                            }
                        } else {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.STOPPED) {
                                status = "Disable";
                            } else {
                                status = "Configuring";
                            }
                        }
                    } else {
                        // ロードバランサとサーバのどちらか一方でもRunningでないとき
                        if (lbInstanceEnabled) {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.RUNNING) {
                                status = "Enable";
                            } else {
                                status = "Enable";
                                notice = true;
                                if (!lbRunning && !instanceRunning) {
                                    noticeMessage = MessageUtils.getMessage("IUI-000091");
                                } else if (!lbRunning) {
                                    noticeMessage = MessageUtils.getMessage("IUI-000092");
                                } else if (!instanceRunning) {
                                    noticeMessage = MessageUtils.getMessage("IUI-000093");
                                }
                            }
                        } else {
                            if (lbInstanceStatus == LoadBalancerInstanceStatus.STOPPED) {
                                status = "Disable";
                            } else {
                                status = "Disable";
                            }
                        }
                    }

                    statusMap.put(instanceDto.getInstance().getInstanceNo(), status);
                    Icons icon = null;
                    Label slbl;
                    if (notice) {
                        icon = Icons.fromName(status + "_WITH_ATTENTION");
                        slbl = new Label(IconUtils.createImageTag(AttachSeriviceServerTable.this, icon, status),
                                Label.CONTENT_XHTML);
                        slbl.setDescription(noticeMessage);
                    } else {
                        icon = Icons.fromName(status);
                        slbl = new Label(IconUtils.createImageTag(AttachSeriviceServerTable.this, icon, status),
                                Label.CONTENT_XHTML);
                    }
                    slbl.setHeight(COLUMN_HEIGHT);
                    return slbl;

                }
            });

            addGeneratedColumn("serviceStatus", new ColumnGenerator() {
                public Component generateCell(Table source, Object itemId, Object columnId) {
                    InstanceDto p = (InstanceDto) itemId;

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
                    Label slbl = new Label(IconUtils.createImageTag(AttachSeriviceServerTable.this, icon, a),
                            Label.CONTENT_XHTML);
                    slbl.setHeight(COLUMN_HEIGHT);
                    return slbl;

                }
            });

            setColumnExpandRatio("instanceName", 100);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    InstanceDto p = (InstanceDto) itemId;

                    if (propertyId == null) {
                        return "";
                    } else {
                        String ret = propertyId.toString().toLowerCase();
                        Long no = p.getInstance().getInstanceNo();
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
                    InstanceDto p = (InstanceDto) event.getItemId();
                    Long no = p.getInstance().getInstanceNo();
                    if (checkList.containsKey(no)) {
                        checkList.get(no).setValue(!(Boolean) checkList.get(no).getValue());
                    }
                }
            });

            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            addStyleName("loadbalancer-desc-service-servers");
            setCaption(ViewProperties.getCaption("table.loadBalancerServiceServers"));
            setWidth("100%");
            setHeight("100%");
            setSortDisabled(true);
            setImmediate(true);
            setVisible(true);
        }

        public void refresh(LoadBalancerDto dto) {
            refresh(dto, false);
        }

        public void refresh(LoadBalancerDto dto, boolean clearCheckBox) {
            if (clearCheckBox) {
                checkList.clear();
            }

            //ComponentDtoの検索
            componentDto = null;
            if (dto != null) {
                Collection<ComponentDto> dtos = (Collection<ComponentDto>) sender.servicePanel.serviceTable
                        .getItemIds();
                for (ComponentDto cDto : dtos) {
                    if (dto.getLoadBalancer().getComponentNo().equals(cDto.getComponent().getComponentNo())) {
                        componentDto = cDto;
                        break;
                    }
                }
            }
            if (componentDto != null) {
                setContainerDataSource(new InstanceDtoContainer(sender.getInstances(componentDto
                        .getComponentInstances())));
                setVisibleColumns(VISIBLE_COLNAME);
                setColumnHeaders(COLNAME);
            } else {
                setContainerDataSource(null);
                setVisibleColumns(VISIBLE_COLNAME);
            }

            loadBalancerOpe.refresh();
        }

    }

    public class LoadbalancerServerOperation extends HorizontalLayout {

        private final String BUTTON_WIDTH = "90px";

        private Button checkAllButton;

        private Button enableButton;

        private Button disableButton;

        LoadbalancerServerOperation() {
            addStyleName("operation-buttons");
            setHeight("35px");
            setWidth("100%");
            setSpacing(true);

            checkAllButton = new Button(ViewProperties.getCaption("button.checkAll"));
            checkAllButton.setDescription(ViewProperties.getCaption("description.checkAll"));
            checkAllButton.addStyleName("borderless");
            checkAllButton.addStyleName("checkall");
            checkAllButton.setEnabled(false);
            checkAllButton.setIcon(Icons.CHECKON.resource());
            checkAllButton.addListener(Button.ClickEvent.class, this, "checkAllButtonClick");

            enableButton = new Button(ViewProperties.getCaption("button.enableLoadBalanceServer"));
            enableButton.setDescription(ViewProperties.getCaption("description.enableLoadBalanceServer"));
            enableButton.setWidth(BUTTON_WIDTH);
            enableButton.setIcon(Icons.ENABLE_MINI.resource());
            enableButton.setEnabled(false);
            enableButton.addListener(Button.ClickEvent.class, this, "enableButtonClick");

            disableButton = new Button(ViewProperties.getCaption("button.disableLoadBalanceServer"));
            disableButton.setDescription(ViewProperties.getCaption("description.disableLoadBalanceServer"));
            disableButton.setWidth(BUTTON_WIDTH);
            disableButton.setIcon(Icons.DISABLE_MINI.resource());
            disableButton.setEnabled(false);
            disableButton.addListener(Button.ClickEvent.class, this, "disableButtonClick");

            addComponent(checkAllButton);
            addComponent(enableButton);
            addComponent(disableButton);

            setComponentAlignment(checkAllButton, Alignment.MIDDLE_LEFT);
            setComponentAlignment(enableButton, Alignment.BOTTOM_RIGHT);
            setComponentAlignment(disableButton, Alignment.BOTTOM_RIGHT);
            setExpandRatio(checkAllButton, 1.0f);
        }

        void refresh() {
            Container container = attachServiceServerTable.getContainerDataSource();
            if (container != null && container.getItemIds().size() > 0) {
                checkAllButton.setEnabled(true);
                enableButton.setEnabled(true);
                disableButton.setEnabled(true);
            } else {
                checkAllButton.setEnabled(false);
                enableButton.setEnabled(false);
                disableButton.setEnabled(false);
            }

            UserAuthDto auth = ViewContext.getAuthority();
            //権限に応じて操作可能なボタンを制御する
            if (!auth.isLbOperate()) {
                enableButton.setEnabled(false);
                disableButton.setEnabled(false);
            }
        }

        public void checkAllButtonClick(Button.ClickEvent event) {

            //全てCheckされていれば全てOFF それ以外は全てON
            boolean checkAll = true;
            for (Long no : attachServiceServerTable.checkList.keySet()) {
                if (!(Boolean) attachServiceServerTable.checkList.get(no).getValue()) {
                    checkAll = false;
                    break;
                }
            }
            for (CheckBox chk : attachServiceServerTable.checkList.values()) {
                chk.setValue(!checkAll);
            }

        }

        public void enableButtonClick(Button.ClickEvent event) {
            final LoadBalancerDto dto = loadBalancerInfo.loadBalancerDto;
            if (dto == null) {
                return;
            }

            // 選択されているサーバの番号を取得
            final List<Long> instanceNos = new ArrayList<Long>();
            for (Map.Entry<Long, CheckBox> entry : attachServiceServerTable.checkList.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    instanceNos.add(entry.getKey());
                }
            }

            if (instanceNos.isEmpty()) {
                // サーバが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 振り分けサーバを有効にできるかどうかのチェック
            for (LoadBalancerInstance lbInstance : dto.getLoadBalancerInstances()) {
                if (instanceNos.contains(lbInstance.getInstanceNo())) {
                    String status = attachServiceServerTable.statusMap.get(lbInstance.getInstanceNo());
                    if ("Configuring".equals(status) || "Warning".equals(status)) {
                        // 振り分けサーバを有効にできないステータスの場合
                        String message = ViewMessages.getMessage("IUI-000089", status);
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }
                }
            }

            String message = ViewMessages.getMessage("IUI-000085", new Object[] { instanceNos.size() });
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
                    apl.doOpLog("LOAD_BALANCER", "Enable Server", null, null,
                            dto.getLoadBalancer().getLoadBalancerNo(), String.valueOf(instanceNos.size()));

                    // 振り分けの有効化
                    LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
                    try {
                        loadBalancerService.enableInstances(dto.getLoadBalancer().getLoadBalancerNo(), instanceNos);
                    } catch (AutoApplicationException e) {
                        String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }

                    // ロードバランサテーブルの選択状態を取得
                    int index = sender.loadBalancerPanel.loadBalancerTable.getCurrentPageFirstItemIndex();

                    // 画面のリフレッシュ
                    sender.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    for (Object itemId : sender.loadBalancerPanel.loadBalancerTable.getItemIds()) {
                        LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                        if (dto.getLoadBalancer().getLoadBalancerNo()
                                .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                            sender.loadBalancerPanel.loadBalancerTable.select(itemId);
                            sender.loadBalancerPanel.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                            sender.loadBalancerPanel.loadBalancerTableOpe.setButtonStatus(dto2);
                            break;
                        }
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

        public void disableButtonClick(Button.ClickEvent event) {
            final LoadBalancerDto dto = loadBalancerInfo.loadBalancerDto;
            if (dto == null) {
                return;
            }

            // 選択されているサーバの番号を取得
            final List<Long> instanceNos = new ArrayList<Long>();
            for (Map.Entry<Long, CheckBox> entry : attachServiceServerTable.checkList.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    instanceNos.add(entry.getKey());
                }
            }

            if (instanceNos.isEmpty()) {
                // サーバが選択されていない場合
                String message = ViewMessages.getMessage("IUI-000037");
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }

            // 振り分けサーバを無効にできるかどうかのチェック
            for (LoadBalancerInstance lbInstance : dto.getLoadBalancerInstances()) {
                if (instanceNos.contains(lbInstance.getInstanceNo())) {
                    String status = attachServiceServerTable.statusMap.get(lbInstance.getInstanceNo());
                    if ("Configuring".equals(status)) {
                        // 振り分けサーバを有効にできないステータスの場合
                        String message = ViewMessages.getMessage("IUI-000090", status);
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }
                }
            }

            String message = ViewMessages.getMessage("IUI-000086", new Object[] { instanceNos.size() });
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
                    apl.doOpLog("LOAD_BALANCER", "Disable Server", null, null, dto.getLoadBalancer()
                            .getLoadBalancerNo(), String.valueOf(instanceNos.size()));

                    // 振り分けの無効化
                    LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
                    try {
                        loadBalancerService.disableInstances(dto.getLoadBalancer().getLoadBalancerNo(), instanceNos);
                    } catch (AutoApplicationException e) {
                        String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                        getApplication().getMainWindow().addWindow(dialog);
                        return;
                    }

                    // ロードバランサテーブルの選択状態を取得
                    int index = sender.loadBalancerPanel.loadBalancerTable.getCurrentPageFirstItemIndex();

                    // 画面のリフレッシュ
                    sender.refreshTable();

                    // 選択されていたロードバランサを選択し直す
                    for (Object itemId : sender.loadBalancerPanel.loadBalancerTable.getItemIds()) {
                        LoadBalancerDto dto2 = (LoadBalancerDto) itemId;
                        if (dto.getLoadBalancer().getLoadBalancerNo()
                                .equals(dto2.getLoadBalancer().getLoadBalancerNo())) {
                            sender.loadBalancerPanel.loadBalancerTable.select(itemId);
                            sender.loadBalancerPanel.loadBalancerTable.setCurrentPageFirstItemIndex(index);
                            sender.loadBalancerPanel.loadBalancerTableOpe.setButtonStatus(dto2);
                            break;
                        }
                    }
                }
            });
            getApplication().getMainWindow().addWindow(dialog);
        }

    }

    public void initializeData() {
        attachServiceServerTable.getContainerDataSource().removeAllItems();
        loadBalancerOpe.refresh();

        LoadBalancerDto dto = (LoadBalancerDto) sender.loadBalancerPanel.loadBalancerTable.getValue();
        loadBalancerInfo.setItem(dto);
    }

}