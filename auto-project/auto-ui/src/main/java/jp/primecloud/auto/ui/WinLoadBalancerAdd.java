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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Property;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * ロードバランサ新規作成画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinLoadBalancerAdd extends Window {

    private final String COLUMN_HEIGHT = "30px";

    private BasicForm basicForm;

    private List<LoadBalancerPlatformDto> platforms;

    private List<ComponentDto> components;

    @Override
    public void attach() {
        // モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.winLoadBalancerAdd"));
        setModal(true);
        setWidth("600px");
        //setHeight("500px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(false);

        // フォーム
        basicForm = new BasicForm();
        layout.addComponent(basicForm);

        // 下部のバー
        HorizontalLayout buttomLayout = new HorizontalLayout();
        buttomLayout.setSpacing(true);
        buttomLayout.setMargin(false, false, true, false);
        layout.addComponent(buttomLayout);
        layout.setComponentAlignment(buttomLayout, Alignment.BOTTOM_RIGHT);

        // AddButtonボタン
        Button addButton = new Button();
        addButton.setCaption(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.addLoadBalancer"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
        addButton.setClickShortcut(KeyCode.ENTER); // [Enter]でaddButtonクリック
        addButton.focus();
        buttomLayout.addComponent(addButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        buttomLayout.addComponent(cancelButton);

        // プラットフォーム情報の表示
        loadData();
        basicForm.cloudTable.show(platforms);
        basicForm.cloudTable.selectFirst();
        basicForm.serviceTable.show(components);
        basicForm.serviceTable.selectFirst();
    }

    private class BasicForm extends Form {

        private TextField loadBalancerNameField;

        private TextField commentField;

        private SelectCloudTable cloudTable;

        private SelectTypeTable typeTable;

        private SelectServiceTable serviceTable;

        @Override
        public void attach() {
            // LB名
            loadBalancerNameField = new TextField(ViewProperties.getCaption("field.loadBalancerName"));
            getLayout().addComponent(loadBalancerNameField);

            // コメント欄
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("95%");
            getLayout().addComponent(commentField);

            // クラウド選択
            cloudTable = new SelectCloudTable();
            getLayout().addComponent(cloudTable);
            cloudTable.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    cloudTableSelect(event);
                }
            });

            // LB種別選択
            typeTable = new SelectTypeTable();
            getLayout().addComponent(typeTable);

            // 割り当てサービス選択
            serviceTable = new SelectServiceTable();
            getLayout().addComponent(serviceTable);

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000055");
            loadBalancerNameField.setRequired(true);
            loadBalancerNameField.setRequiredError(message);
            loadBalancerNameField.addValidator(new StringLengthValidator(message, 1, 15, false));
            loadBalancerNameField
                    .addValidator(new RegexpValidator("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", true, message));

            message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }

        private void cloudTableSelect(Property.ValueChangeEvent event) {
            // 選択がない場合はロードバランサ種別情報をクリア
            if (cloudTable.getValue() == null) {
                typeTable.removeAllItems();
                return;
            }

            // ロードバランサ種別情報を表示
            LoadBalancerPlatformDto platform = findPlatform(cloudTable.getValue());
            typeTable.show(platform.getTypes());
            typeTable.selectFirst();
        }

    }

    private class SelectCloudTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.selectCloud"));
            setWidth("340px");
            setPageLength(3);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("loadbalancer-add-table");

            // カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Cloud", Label.class, new Label());
            setColumnExpandRatio("Cloud", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<LoadBalancerPlatformDto> platforms) {
            removeAllItems();

            if (platforms == null) {
                return;
            }

            for (int i = 0; i < platforms.size(); i++) {
                LoadBalancerPlatformDto platformDto = platforms.get(i);

                // プラットフォーム名
                Icons icon = IconUtils.getPlatformIcon(platformDto);
                String description = platformDto.getPlatform().getPlatformNameDisp();
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), icon, description),
                        Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { (i + 1), slbl }, platformDto.getPlatform().getPlatformNo());
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

    private class SelectTypeTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.loadBalancerType"));
            setWidth("340px");
            setPageLength(2);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("loadbalancer-add-table");

            // カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Detail", Label.class, new Label());
            setColumnExpandRatio("Detail", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<String> types) {
            removeAllItems();

            if (types == null) {
                return;
            }

            for (int i = 0; i < types.size(); i++) {
                String type = types.get(i);

                // ロードバランサ種別
                Icons typeIcon = Icons.NONE;
                String typeString = ViewProperties.getLoadBalancerType(type);
                Label nlbl = new Label(IconUtils.createImageTag(getApplication(), typeIcon, typeString),
                        Label.CONTENT_XHTML);
                nlbl.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { (i + 1), nlbl }, type);
            }
        }

        @Override
        public String getValue() {
            return (String) super.getValue();
        }

        public void selectFirst() {
            if (size() > 0) {
                select(firstItemId());
            }
        }

    }

    private class SelectServiceTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setCaption(ViewProperties.getCaption("table.loadBalancerService"));
            setWidth("98%");
            setPageLength(4);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("loadbalancer-select-service");

            // カラム設定
            addContainerProperty("Name", Label.class, new Label());
            addContainerProperty("Detail", Label.class, new Label());
            setColumnExpandRatio("Detail", 100);
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<ComponentDto> components) {
            removeAllItems();

            if (components == null) {
                return;
            }

            for (int i = 0; i < components.size(); i++) {
                ComponentDto componentDto = components.get(i);

                // サービス名
                String serviceName = componentDto.getComponent().getComponentName();
                if (StringUtils.isNotEmpty(componentDto.getComponent().getComment())) {
                    serviceName = componentDto.getComponent().getComment() + "\n[" + serviceName + "]";
                }
                Label nameLabel = new Label(serviceName, Label.CONTENT_PREFORMATTED);

                // サービス種類
                ComponentType componentType = componentDto.getComponentType();
                String typeName = componentType.getComponentTypeNameDisp();
                Icons typeIcon = Icons.fromName(componentType.getComponentTypeName());
                Label typeLabel = new Label(IconUtils.createImageTag(getApplication(), typeIcon, typeName),
                        Label.CONTENT_XHTML);
                typeLabel.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { nameLabel, typeLabel }, componentDto.getComponent().getComponentNo());
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
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        platforms = loadBalancerService.getPlatforms(ViewContext.getUserNo());

        // 有効でないプラットフォーム情報を除外
        for (int i = platforms.size() - 1; i >= 0; i--) {
            if (BooleanUtils.isNotTrue(platforms.get(i).getPlatform().getSelectable())) {
                platforms.remove(i);
            }
        }

        // プラットフォーム情報をソート
        Collections.sort(platforms, new Comparator<LoadBalancerPlatformDto>() {
            @Override
            public int compare(LoadBalancerPlatformDto o1, LoadBalancerPlatformDto o2) {
                int order1 = (o1.getPlatform().getViewOrder() != null) ? o1.getPlatform().getViewOrder()
                        : Integer.MAX_VALUE;
                int order2 = (o2.getPlatform().getViewOrder() != null) ? o2.getPlatform().getViewOrder()
                        : Integer.MAX_VALUE;
                return order1 - order2;
            }
        });

        // サービス情報を取得
        ComponentService componentService = BeanContext.getBean(ComponentService.class);
        components = componentService.getComponents(ViewContext.getFarmNo());
    }

    private LoadBalancerPlatformDto findPlatform(Long platformNo) {
        for (LoadBalancerPlatformDto platform : platforms) {
            if (platformNo.equals(platform.getPlatform().getPlatformNo())) {
                return platform;
            }
        }
        return null;
    }

    private void addButtonClick(ClickEvent event) {
        // 入力値を取得
        String loadBalancerName = (String) basicForm.loadBalancerNameField.getValue();
        String comment = (String) basicForm.commentField.getValue();
        Long platformNo = basicForm.cloudTable.getValue();
        String type = basicForm.typeTable.getValue();
        Long componentNo = basicForm.serviceTable.getValue();

        // 入力チェック
        basicForm.loadBalancerNameField.validate();
        basicForm.commentField.validate();
        if (platformNo == null) {
            throw new AutoApplicationException("IUI-000023");
        }
        if (type == null || type.length() == 0) {
            throw new AutoApplicationException("IUI-000054");
        }
        if (componentNo == null) {
            throw new AutoApplicationException("IUI-000065");
        }
        if (loadBalancerName.startsWith("lb-")) {
            throw new AutoApplicationException("IUI-000084", loadBalancerName);
        }

        // ロードバランサを作成
        LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
        Long farmNo = ViewContext.getFarmNo();
        Long loadBalancerNo = null;

        // AWSロードバランサを作成
        if (PCCConstant.LOAD_BALANCER_ELB.equals(type)) {
            loadBalancerNo = loadBalancerService.createAwsLoadBalancer(farmNo, loadBalancerName, comment, platformNo,
                    componentNo, false);
        }
        // UltraMonkeyロードバランサを作成
        else if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(type)) {
            loadBalancerNo = loadBalancerService.createUltraMonkeyLoadBalancer(farmNo, loadBalancerName, comment,
                    platformNo, componentNo);
        }
        // CloudStackロードバランサを作成
        else if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(type)) {
            loadBalancerNo = loadBalancerService.createCloudstackLoadBalancer(farmNo, loadBalancerName, comment,
                    platformNo, componentNo);
        }

        // オペレーションログ
        OperationLogger.writeLoadBalancer("LOAD_BALANCER", "Make Load_Balancer", loadBalancerNo, null);

        // 画面を閉じる
        close();
    }

}
