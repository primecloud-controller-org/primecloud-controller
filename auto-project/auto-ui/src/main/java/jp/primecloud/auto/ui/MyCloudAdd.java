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

import java.util.List;

import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.TemplateService;
import jp.primecloud.auto.service.dto.TemplateDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * myCloud新規作成画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class MyCloudAdd extends Window {

    final String COLUMN_HEIGHT = "30px";

    private BasicTab basicTab;

    private List<TemplateDto> templates;

    @Override
    public void attach() {
        //モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.myCloudAdd"));
        setModal(true);
        setWidth("550px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        // 基本情報
        basicTab = new BasicTab();
        layout.addComponent(basicTab);

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.setMargin(false, false, true, false);
        //okbar.setWidth("100%");
        layout.addComponent(bottomLayout);
        layout.setComponentAlignment(bottomLayout, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.mycloud.add"));
        addButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
        bottomLayout.addComponent(addButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cansel"));
        cancelButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        bottomLayout.addComponent(cancelButton);

        // テンプレート情報を表示
        showTemplates();
    }

    private class BasicTab extends Form {

        private TextField cloudNameField;

        private TextField commentField;

        private SelectTemplateTable templateTable;

        @Override
        public void attach() {
            // myCloud名
            cloudNameField = new TextField(ViewProperties.getCaption("field.cloudName"));
            getLayout().addComponent(cloudNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("100%");
            getLayout().addComponent(commentField);

            // テンプレート選択テーブル
            templateTable = new SelectTemplateTable();
            getLayout().addComponent(templateTable);

            // テンプレート説明欄
            final Label nameLabel = new Label();
            final Label descriptionLabel = new Label();

            Panel descriptionPanel = new Panel();
            CssLayout layout = new CssLayout();
            layout.addStyleName("template-desc");
            descriptionPanel.setHeight("80px");
            descriptionPanel.setContent(layout);
            layout.setSizeFull();
            layout.addComponent(nameLabel);
            layout.addComponent(descriptionLabel);
            getLayout().addComponent(descriptionPanel);

            templateTable.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    TemplateDto template = templateTable.getValue();
                    nameLabel.setValue(template.getTemplate().getTemplateNameDisp() + "：");
                    descriptionLabel.setValue(template.getTemplate().getTemplateDescriptionDisp());
                }
            });

            cloudNameField.focus();

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000002");
            cloudNameField.setRequired(true);
            cloudNameField.setRequiredError(message);
            cloudNameField.addValidator(new StringLengthValidator(message, 1, 15, false));
            cloudNameField.addValidator(new RegexpValidator("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", true, message));

            message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }

    }

    private class SelectTemplateTable extends Table {

        @Override
        public void attach() {
            // テーブル基本設定
            setWidth("100%");
            addStyleName("win-mycloud-add-temp");
            setCaption(ViewProperties.getCaption("table.selectTemplate"));
            setPageLength(4);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);

            // カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Name", Label.class, new Label());
            setColumnExpandRatio("Name", 100);

            // テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<TemplateDto> templates) {
            removeAllItems();
            for (int i = 0; i < templates.size(); i++) {
                TemplateDto template = templates.get(i);

                // テンプレート名
                String name = template.getTemplate().getTemplateNameDisp();
                Icons nameIcon = Icons.CUSTOM;
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), nameIcon, name), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                addItem(new Object[] { (i + 1), slbl }, template);
            }
        }

        @Override
        public TemplateDto getValue() {
            return (TemplateDto) super.getValue();
        }

        public void selectFirst() {
            if (size() > 0) {
                select(firstItemId());
            }
        }

    }

    private void showTemplates() {
        // テンプレート情報を取得
        TemplateService templateService = BeanContext.getBean(TemplateService.class);
        templates = templateService.getTemplates(ViewContext.getUserNo());

        // 取得したテンプレートをテーブルに追加
        basicTab.templateTable.show(templates);

        // 先頭のテンプレートを選択する
        basicTab.templateTable.selectFirst();
    }

    private void addButtonClick(ClickEvent event) {
        // 入力値を取得
        final String cloudName = (String) basicTab.cloudNameField.getValue();
        final String comment = (String) basicTab.commentField.getValue();
        final TemplateDto template = basicTab.templateTable.getValue();

        // 入力チェック
        try {
            basicTab.cloudNameField.validate();
            basicTab.commentField.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (template == null) {
            // テンプレートが選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000004"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // myCloud作成の確認ダイアログを表示
        String diagMessage = ViewMessages.getMessage("IUI-000040", cloudName);
        DialogConfirm dialogConfirm = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), diagMessage,
                Buttons.OKCancel);
        dialogConfirm.setCallback(new Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                // パワーユーザ兼任の場合は自分の管理下で作成するためUserNoを元に戻す
                if (ViewContext.getPowerUser()) {
                    ViewContext.setUserNo(ViewContext.getPowerDefaultMaster());
                }

                // myCloudを作成
                FarmService farmService = BeanContext.getBean(FarmService.class);
                Long farmNo;
                try {
                    Long userNo = ViewContext.getUserNo();
                    farmNo = farmService.createFarm(userNo, cloudName, comment);
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }

                // オペレーションログ
                AutoApplication aapl = (AutoApplication) getApplication();
                aapl.doOpLog("CLOUD", "Make Cloud", farmNo, null);

                // テンプレートを適用
                TemplateService templateService = BeanContext.getBean(TemplateService.class);
                try {
                    templateService.applyTemplate(farmNo, template.getTemplate().getTemplateNo());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }

                // 追加したmyCloudの情報をセッションに格納
                ContextUtils.setAttribute("newfarmNo", farmNo);
                ContextUtils.setAttribute("newfarmName", cloudName);

                // 画面を閉じる
                close();
            }
        });

        getApplication().getMainWindow().addWindow(dialogConfirm);
    }

}
