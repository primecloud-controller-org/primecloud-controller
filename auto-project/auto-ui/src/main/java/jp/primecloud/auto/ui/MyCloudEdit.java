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

import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.Application;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

    Application apl;

    Long farmNo;

    TextField cloudNameField;

    TextField domainNameField;

    TextField commentField;

    FarmDto farmDto;

    MyCloudEdit(Application apl, Long farmNo) {
        this.apl = apl;
        this.farmNo = farmNo;

        //モーダルウインドウ
        setIcon(Icons.ADD.resource());
        setCaption(ViewProperties.getCaption("window.myCloudEdit"));
        setModal(true);
        setWidth("450px");

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

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editCloud.ok"));
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudEdit.this.editButtonClick(event);
            }
        });
        okbar.addComponent(okButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudEdit.this.close();
            }
        });
        okbar.addComponent(cancelButton);

        //初期データ読み込み
        initData();

        // 入力チェックの設定
        initValidation();

    }

    private class BasicTab extends Form {

        BasicTab() {
            // クラウド名
            cloudNameField = new TextField(ViewProperties.getCaption("field.cloudName"));
            getLayout().addComponent(cloudNameField);

            //ドメイン
            domainNameField = new TextField(ViewProperties.getCaption("field.domainName"));
            domainNameField.setWidth("100%");
            getLayout().addComponent(domainNameField);

            // コメント
            commentField = new TextField(ViewProperties.getCaption("field.comment"));
            commentField.setWidth("100%");
            getLayout().addComponent(commentField);

            cloudNameField.focus();

        }
    }

    private void initData() {
        try {
            // ロジックを実行
            FarmService farmService = BeanContext.getBean(FarmService.class);
            farmDto = farmService.getFarm(farmNo);
        } catch (AutoApplicationException e) {
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        cloudNameField.setReadOnly(false);
        cloudNameField.setValue(farmDto.getFarm().getFarmName());
        cloudNameField.setReadOnly(true);

        commentField.setValue(farmDto.getFarm().getComment());

        domainNameField.setReadOnly(false);
        domainNameField.setValue(farmDto.getFarm().getDomainName());
        domainNameField.setReadOnly(true);
    }

    private void initValidation() {
        String message = ViewMessages.getMessage("IUI-000003");
        commentField.addValidator(new StringLengthValidator(message, -1, 100, true));
    }

    private void editButtonClick(ClickEvent event) {

        // 入力値を取得
        String cloudName = (String) cloudNameField.getValue();

        // 入力チェック
        try {
            //  cloudNameField.validate();
            commentField.validate();
            //  domainNameField.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        //クラウド変更の確認ダイヤログを表示
        String diagMessage = ViewMessages.getMessage("IUI-000041", cloudName);
        DialogConfirm dialogConfirm = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), diagMessage,
                Buttons.OKCancel);
        dialogConfirm.setCallback(new Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                String domainName = (String) domainNameField.getValue();
                String comment = (String) commentField.getValue();

                //TODO LOG
                AutoApplication aapl =  (AutoApplication)apl;
                aapl.doOpLog("CLOUD", "Edit Cloud", farmNo, null);

                // ロジックを実行
                FarmService farmService = BeanContext.getBean(FarmService.class);
                try {
                    farmService.updateFarm(farmNo, comment, domainName);
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }

                // 追加したクラウドのfarmNoをセッションに格納
                ContextUtils.setAttribute("editFarmNo", farmNo);

                // 画面を閉じる
                close();

            }
        });
        getApplication().getMainWindow().addWindow(dialogConfirm);

    }
}
