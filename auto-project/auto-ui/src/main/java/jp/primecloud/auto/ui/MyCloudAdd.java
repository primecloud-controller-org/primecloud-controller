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
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.Application;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * <p>
 * myCloud新規作成画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class MyCloudAdd extends Window {
    final String COLUMN_HEIGHT = "30px";

    Application apl;

    TextField cloudNameField;

    TextField commentField;

    Table templateTable;

    List<TemplateDto> templates;

    MyCloudAdd(Application ap) {
        apl = ap;

        //モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.myCloudAdd"));
        setModal(true);
        setWidth("550px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);

        // 基本情報
        layout.addComponent(new BasicTab());

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        //okbar.setWidth("100%");
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // Addボタン
        Button addButton = new Button(ViewProperties.getCaption("button.add"));
        addButton.setDescription(ViewProperties.getCaption("description.mycloud.add"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudAdd.this.addButtonClick(event);
            }
        });
        okbar.addComponent(addButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cansel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudAdd.this.close();
            }
        });
        okbar.addComponent(cancelButton);

        // 入力チェックの設定
        initValidation();

        // 初期データの取得
        initData();

        // テンプレート情報を表示
        showTemplates();
    }

    private class BasicTab extends Form {
        Label ldlTemplate = new Label();
        Label ldlDesciption = new Label();

        BasicTab() {
            // クラウド名
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
            Panel descTemplate= new Panel();
            CssLayout lay = new CssLayout();
            lay.addStyleName("template-desc");
            descTemplate.setHeight("80px");
            descTemplate.setContent(lay);
            lay.setSizeFull();
            lay.addComponent(ldlTemplate);
            lay.addComponent(ldlDesciption);
            getLayout().addComponent(descTemplate);

            templateTable.addListener(new Table.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                    TemplateDto template = (TemplateDto)templateTable.getValue();
                    ldlTemplate.setValue(template.getTemplate().getTemplateNameDisp()+"：");
                    ldlDesciption.setValue(template.getTemplate().getTemplateDescriptionDisp());
                }

            });

            cloudNameField.focus();

        }
    }

    private class SelectTemplateTable extends Table {
        SelectTemplateTable() {
            //テーブル基本設定
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

            //カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Name", Label.class, new Label());
            setColumnExpandRatio("Name", 100);

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
        }
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

    private void initData() {
        // テンプレート情報を取得
        Long userNo = ViewContext.getUserNo();
        TemplateService templateService = BeanContext.getBean(TemplateService.class);
        templates = templateService.getTemplates(userNo);
    }

    private void showTemplates() {
        templateTable.removeAllItems();

        // 取得したテンプレートをテーブルに追加
        for (int i = 0; i < templates.size(); i++) {
            TemplateDto template = templates.get(i);

            // テンプレート名
            String name = template.getTemplate().getTemplateNameDisp();
            Icons nameIcon = Icons.CUSTOM;
            Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl ,nameIcon) + "\"><div>" + name
                    + "</div>", Label.CONTENT_XHTML);
            slbl.setHeight(COLUMN_HEIGHT);

            templateTable.addItem(new Object[] { (i + 1), slbl }, template);
        }

        // 先頭のテンプレートを選択する
        templateTable.select(templateTable.firstItemId());
    }

    private void addButtonClick(ClickEvent event) {

        // 入力値を取得
        String cloudName = (String) cloudNameField.getValue();
//        String comment = (String) commentField.getValue();
        TemplateDto template = (TemplateDto) templateTable.getValue();

        // 入力チェック
        try {
            cloudNameField.validate();
            commentField.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        if (template == null) {
            // テンプレートが選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), ViewMessages.getMessage("IUI-000004") );
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        //クラウド作成の確認ダイヤログを表示
        String diagMessage = ViewMessages.getMessage("IUI-000040", cloudName);
        DialogConfirm dialogConfirm = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), diagMessage, Buttons.OKCancel);
        dialogConfirm.setCallback(new Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                String cloudName = (String) cloudNameField.getValue();
                String comment = (String) commentField.getValue();
                TemplateDto template = (TemplateDto) templateTable.getValue();


                //パワーユーザ兼任の場合は自分の管理下で作成するためUserNoを元に戻す
                if (ViewContext.getPowerUser()){
                    ViewContext.setUserNo(ViewContext.getPowerDefaultMaster());
                }
                // ユーザ番号
                Long userNo = ViewContext.getUserNo();

                // ロジックを実行
                FarmService farmService = BeanContext.getBean(FarmService.class);
                Long farmNo;
                try {
                    farmNo = farmService.createFarm(userNo, cloudName, comment);
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }

                //TODO LOG
                AutoApplication aapl =  (AutoApplication)apl;
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

                // 追加したクラウドの情報をセッションに格納
                ContextUtils.setAttribute("newfarmNo", farmNo);
                ContextUtils.setAttribute("newfarmName", cloudName);

                // 画面を閉じる
                close();

            }
        });
        getApplication().getMainWindow().addWindow(dialogConfirm);

    }

}
