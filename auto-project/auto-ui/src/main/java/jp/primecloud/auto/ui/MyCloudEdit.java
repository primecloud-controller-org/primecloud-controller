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

import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * myCloudの編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class MyCloudEdit extends Window {

    final String COLUMN_HEIGHT = "30px";

    private Long farmNo;

    private BasicTab basicTab;

    private FarmDto farm;

    public MyCloudEdit(Long farmNo) {
        this.farmNo = farmNo;
    }

    @Override
    public void attach() {
        // モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.myCloudEdit"));
        setModal(true);
        setWidth("450px");

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

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editCloud.ok"));
        okButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                editButtonClick(event);
            }
        });
        bottomLayout.addComponent(okButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        bottomLayout.addComponent(cancelButton);

        // myCloud情報を表示
        loadData();
        basicTab.show(farm);
    }

    private class BasicTab extends Form {

        private TextField cloudNameField;

        private TextField domainNameField;

        private TextField commentField;

        @Override
        public void attach() {
            // myCloud名
            cloudNameField = new TextField(ViewProperties.getCaption("field.cloudName"));
            getLayout().addComponent(cloudNameField);

            // ドメイン名
            domainNameField = new TextField(ViewProperties.getCaption("field.domainName"));
            domainNameField.setWidth("100%");
            getLayout().addComponent(domainNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("100%");
            getLayout().addComponent(commentField);

            cloudNameField.focus();

            initValidation();
        }

        private void initValidation() {
            String message = ViewMessages.getMessage("IUI-000003");
            commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
        }

        public void show(FarmDto farm) {
            cloudNameField.setReadOnly(false);
            cloudNameField.setValue(farm.getFarm().getFarmName());
            cloudNameField.setReadOnly(true);

            commentField.setValue(farm.getFarm().getComment());

            domainNameField.setReadOnly(false);
            domainNameField.setValue(farm.getFarm().getDomainName());
            domainNameField.setReadOnly(true);
        }

    }

    private void loadData() {
        // myCloud情報を取得
        FarmService farmService = BeanContext.getBean(FarmService.class);
        farm = farmService.getFarm(farmNo);
    }

    private void editButtonClick(ClickEvent event) {
        // 入力値を取得
        final String cloudName = (String) basicTab.cloudNameField.getValue();
        final String domainName = (String) basicTab.domainNameField.getValue();
        final String comment = (String) basicTab.commentField.getValue();

        // 入力チェック
        basicTab.commentField.validate();

        // myCloud編集の確認ダイアログを表示
        String diagMessage = ViewMessages.getMessage("IUI-000041", cloudName);
        DialogConfirm dialogConfirm = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), diagMessage,
                Buttons.OKCancel);
        dialogConfirm.setCallback(new Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                OperationLogger.writeFarm("CLOUD", "Edit Cloud", farmNo, null);

                // myCloudを編集
                FarmService farmService = BeanContext.getBean(FarmService.class);
                farmService.updateFarm(farmNo, comment, domainName);

                // 編集したmyCloudのfarmNoをセッションに格納
                ContextUtils.setAttribute("editFarmNo", farmNo);

                // 画面を閉じる
                close();
            }
        });

        getApplication().getMainWindow().addWindow(dialogConfirm);
    }

}
