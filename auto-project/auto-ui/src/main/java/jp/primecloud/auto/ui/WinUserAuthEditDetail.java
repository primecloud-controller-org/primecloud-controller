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

import jp.primecloud.auto.entity.crud.UserAuth;
import jp.primecloud.auto.service.UserManagementService;
import jp.primecloud.auto.service.UserService;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * ユーザ編集詳細設定画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinUserAuthEditDetail extends Window {

    //ユーザ番号(編集画面、編集対象ユーザ)
    private Long userNo;

    //ファーム番号
    private Long farmNo;

    //ユーザ情報(編集対象のユーザ情報)
    private UserAuthDto userAuthDto;

    //マイクラウド コンボボックス
    private ComboBox myCloudSelect;

    //サーバ作成 コンボボックス
    private ComboBox serverMakeSelect;

    //サーバ削除 コンボボックス
    private ComboBox serverDeleteSelect;

    //サーバ操作 コンボボックス
    private ComboBox serverOperateSelect;

    //サービス作成 コンボボックス
    private ComboBox serviceMakeSelect;

    //サービス削除 コンボボックス
    private ComboBox serviceDeleteSelect;

    //サービス操作 コンボボックス
    private ComboBox serviceOperateSelect;

    //ロードバランサ作成 コンボボックス
    private ComboBox lbMakeSelect;

    //ロードバランサ削除 コンボボックス
    private ComboBox lbDeleteSelect;

    //ロードバランサ操作 コンボボックス
    private ComboBox lbOperateSelect;

    WinUserAuthEditDetail(Long userNo, Long farmNo) {
        this.userNo = userNo;
        this.farmNo = farmNo;

        //ウインドウ設定
        setIcon(Icons.EDIT.resource());
        setCaption(ViewProperties.getCaption("window.winUserDetailEdit"));

        //モーダル
        setModal(true);
        setWidth("300px");
        //リサイズ
        setResizable(false);

        //レイアウト
        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(false);

        layout.addComponent(new AuthForm());

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(false, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        okButton.setDescription(ViewProperties.getCaption("description.editUserAuthDetail.ok"));
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinUserAuthEditDetail.this.okButtonClick(event);
            }
        });
        okbar.addComponent(okButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinUserAuthEditDetail.this.cancelButtonClick(event);
            }
        });
        okbar.addComponent(cancelButton);

        // 初期データ取得
        initData();

        // データの表示
        showData();

        // 入力チェックの設定
        initValidation();
    }

    private void setCustomErrorHandler(Button button) {
        //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
        if (button.getErrorHandler() == null || button.getErrorHandler().getClass() != ComponentsErrorHandler.class) {
            button.setErrorHandler(new ComponentsErrorHandler(this.getParent()));
        }
    }

    private class AuthForm extends Form {

        private static final String PID_MYCLOUD_AUTH = "MyCloudAuth";

        private static final String PID_AUTH = "Auth";

        private AuthForm() {
            //マイクラウド(ラベル)
            Label myCloudLbl = new Label(ViewProperties.getCaption("label.userAuth.myCloud"));

            //マイクラウド
            myCloudSelect = new ComboBox();
            myCloudSelect.setNullSelectionAllowed(false);
            myCloudSelect.setItemCaptionPropertyId(PID_MYCLOUD_AUTH);
            myCloudSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            myCloudSelect.setContainerDataSource(createMyCloudAuthContainer());
            myCloudSelect.setImmediate(true);
            myCloudSelect.addListener(createMyCloudChange());

            //サービス(ラベル)
            Label serverLbl = new Label(ViewProperties.getCaption("label.userAuth.server"));

            //サーバ作成(ラベル)
            Label serverMakeLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.make"));

            //サーバ作成
            serverMakeSelect = new ComboBox();
            serverMakeSelect.setNullSelectionAllowed(false);
            serverMakeSelect.setItemCaptionPropertyId(PID_AUTH);
            serverMakeSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serverMakeSelect.setContainerDataSource(createAuthContainer());
            serverMakeSelect.setImmediate(true);

            //サーバ削除(ラベル)
            Label serverDeleteLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.delete"));

            //サーバ削除
            serverDeleteSelect = new ComboBox();
            serverDeleteSelect.setNullSelectionAllowed(false);
            serverDeleteSelect.setItemCaptionPropertyId(PID_AUTH);
            serverDeleteSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serverDeleteSelect.setContainerDataSource(createAuthContainer());
            serverDeleteSelect.setImmediate(true);

            //サーバ操作(ラベル)
            Label serverOperateLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.operate"));

            //サーバ操作
            serverOperateSelect = new ComboBox();
            serverOperateSelect.setNullSelectionAllowed(false);
            serverOperateSelect.setItemCaptionPropertyId(PID_AUTH);
            serverOperateSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serverOperateSelect.setContainerDataSource(createAuthContainer());
            serverOperateSelect.setImmediate(true);

            //サービス(ラベル)
            Label serviceLbl = new Label(ViewProperties.getCaption("label.userAuth.service"));

            //サービス作成(ラベル)
            Label serviceMakeLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.make"));

            //サービス作成
            serviceMakeSelect = new ComboBox();
            serviceMakeSelect.setNullSelectionAllowed(false);
            serviceMakeSelect.setItemCaptionPropertyId(PID_AUTH);
            serviceMakeSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serviceMakeSelect.setContainerDataSource(createAuthContainer());
            serviceMakeSelect.setImmediate(true);

            //サービス削除(ラベル)
            Label serviceDeleteLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.delete"));

            //サービス削除
            serviceDeleteSelect = new ComboBox();
            serviceDeleteSelect.setNullSelectionAllowed(false);
            serviceDeleteSelect.setItemCaptionPropertyId(PID_AUTH);
            serviceDeleteSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serviceDeleteSelect.setContainerDataSource(createAuthContainer());
            serviceDeleteSelect.setImmediate(true);

            //サービス操作(ラベル)
            Label serviceOperateLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.operate"));

            //サービス操作
            serviceOperateSelect = new ComboBox();
            serviceOperateSelect.setNullSelectionAllowed(false);
            serviceOperateSelect.setItemCaptionPropertyId(PID_AUTH);
            serviceOperateSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            serviceOperateSelect.setContainerDataSource(createAuthContainer());
            serviceOperateSelect.setImmediate(true);

            //ロードバランサ(ラベル)
            Label lbLbl = new Label(ViewProperties.getCaption("label.userAuth.lb"));

            //ロードバランサ(ラベル)
            Label lbMakeLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.make"));

            //ロードバランサ作成
            lbMakeSelect = new ComboBox();
            lbMakeSelect.setNullSelectionAllowed(false);
            lbMakeSelect.setItemCaptionPropertyId(PID_AUTH);
            lbMakeSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            lbMakeSelect.setContainerDataSource(createAuthContainer());
            lbMakeSelect.setImmediate(true);

            //ロードバランサ削除(ラベル)
            Label lbDeleteLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.delete"));

            //ロードバランサ削除
            lbDeleteSelect = new ComboBox();
            lbDeleteSelect.setNullSelectionAllowed(false);
            lbDeleteSelect.setItemCaptionPropertyId(PID_AUTH);
            lbDeleteSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            lbDeleteSelect.setContainerDataSource(createAuthContainer());
            lbDeleteSelect.setImmediate(true);

            //ロードバランサ操作(ラベル)
            Label lbOperateLbl = new Label(ViewProperties.getCaption("label.userAuth.authType.operate"));

            //ロードバランサ操作
            lbOperateSelect = new ComboBox();
            lbOperateSelect.setNullSelectionAllowed(false);
            lbOperateSelect.setItemCaptionPropertyId(PID_AUTH);
            lbOperateSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            lbOperateSelect.setContainerDataSource(createAuthContainer());
            lbOperateSelect.setImmediate(true);

            /////////////// レイアウト処理 //////////////////////
            //マイクラウド
            AbsoluteLayout myCloudLayout = createAbsoluteLayout();
            myCloudLayout.addComponent(myCloudLbl, "left:0%");
            myCloudLayout.addComponent(myCloudSelect, "left:40%");
            getLayout().addComponent(myCloudLayout);

            //サーバ(ラベルのみ)
            AbsoluteLayout serverLblLayout = createAbsoluteLayout();
            serverLblLayout.addComponent(serverLbl, "left:0%");
            getLayout().addComponent(serverLblLayout);

            //サーバ作成
            AbsoluteLayout serverMakeLayout = createAbsoluteLayout();
            serverMakeLayout.addComponent(serverMakeLbl, "left:0%");
            serverMakeLayout.addComponent(serverMakeSelect, "left:40%");
            getLayout().addComponent(serverMakeLayout);

            //サーバ削除
            AbsoluteLayout serverDeleteLayout = createAbsoluteLayout();
            serverDeleteLayout.addComponent(serverDeleteLbl, "left:0%");
            serverDeleteLayout.addComponent(serverDeleteSelect, "left:40%");
            getLayout().addComponent(serverDeleteLayout);

            //サーバ操作
            AbsoluteLayout serverOperateLayout = createAbsoluteLayout();
            serverOperateLayout.addComponent(serverOperateLbl, "left:0%");
            serverOperateLayout.addComponent(serverOperateSelect, "left:40%");
            getLayout().addComponent(serverOperateLayout);

            //サーバ(ラベルのみ)
            AbsoluteLayout serviceLblLayout = createAbsoluteLayout();
            serviceLblLayout.addComponent(serviceLbl, "left:0%");
            getLayout().addComponent(serviceLblLayout);

            //サービス作成
            AbsoluteLayout serviceMakeLayout = createAbsoluteLayout();
            serviceMakeLayout.addComponent(serviceMakeLbl, "left:0%");
            serviceMakeLayout.addComponent(serviceMakeSelect, "left:40%");
            getLayout().addComponent(serviceMakeLayout);

            //サービス削除
            AbsoluteLayout serviceDeleteLayout = createAbsoluteLayout();
            serviceDeleteLayout.addComponent(serviceDeleteLbl, "left:0%");
            serviceDeleteLayout.addComponent(serviceDeleteSelect, "left:40%");
            getLayout().addComponent(serviceDeleteLayout);

            //サービス操作
            AbsoluteLayout serviceOperateLayout = createAbsoluteLayout();
            serviceOperateLayout.addComponent(serviceOperateLbl, "left:0%");
            serviceOperateLayout.addComponent(serviceOperateSelect, "left:40%");
            getLayout().addComponent(serviceOperateLayout);

            //ロードバランサ(ラベルのみ)
            AbsoluteLayout lbLblLayout = createAbsoluteLayout();
            lbLblLayout.addComponent(lbLbl, "left:0%");
            getLayout().addComponent(lbLblLayout);

            //ロードバランサ作成
            AbsoluteLayout lbMakeLayout = createAbsoluteLayout();
            lbMakeLayout.addComponent(lbMakeLbl, "left:0%");
            lbMakeLayout.addComponent(lbMakeSelect, "left:40%");
            getLayout().addComponent(lbMakeLayout);

            //ロードバランサ削除
            AbsoluteLayout lbDeleteLayout = createAbsoluteLayout();
            lbDeleteLayout.addComponent(lbDeleteLbl, "left:0%");
            lbDeleteLayout.addComponent(lbDeleteSelect, "left:40%");
            getLayout().addComponent(lbDeleteLayout);

            //ロードバランサ操作
            AbsoluteLayout lbOperateLayout = createAbsoluteLayout();
            lbOperateLayout.addComponent(lbOperateLbl, "left:0%");
            lbOperateLayout.addComponent(lbOperateSelect, "left:40%");
            getLayout().addComponent(lbOperateLayout);
        }

        private ValueChangeListener createMyCloudChange() {
            ValueChangeListener listener = new Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                    //※ファーム権限がFALSEの場合は他の権限全てを非活性
                    Boolean isFarmUse = (Boolean) myCloudSelect.getValue();

                    //サーバ権
                    serverMakeSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));
                    serverDeleteSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));
                    serverOperateSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));

                    //サービス権
                    serviceMakeSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));
                    serviceDeleteSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));
                    serviceOperateSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));

                    //ロードバランサ権
                    lbMakeSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));
                    lbDeleteSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));
                    lbOperateSelect.setEnabled(BooleanUtils.isTrue(isFarmUse));
                }
            };
            return listener;
        }

        private AbsoluteLayout createAbsoluteLayout() {
            AbsoluteLayout layout = new AbsoluteLayout();
            layout.setWidth("100%");
            layout.setHeight("27px");
            layout.setMargin(true, false, true, false);
            return layout;
        }

        private IndexedContainer createMyCloudAuthContainer() {
            IndexedContainer authContainer = new IndexedContainer();
            authContainer.addContainerProperty(PID_MYCLOUD_AUTH, String.class, null);

            //利用可
            Item itemEnable = authContainer.addItem(true);
            itemEnable.getItemProperty(PID_MYCLOUD_AUTH)
                    .setValue(ViewProperties.getCaption("label.userAuth.available"));

            //利用不可
            Item itemDisable = authContainer.addItem(false);
            itemDisable.getItemProperty(PID_MYCLOUD_AUTH).setValue(
                    ViewProperties.getCaption("label.userAuth.notAvailable"));

            return authContainer;
        }

        private IndexedContainer createAuthContainer() {
            IndexedContainer authContainer = new IndexedContainer();
            authContainer.addContainerProperty(PID_AUTH, String.class, null);

            //可能
            Item itemEnable = authContainer.addItem(true);
            itemEnable.getItemProperty(PID_AUTH).setValue(ViewProperties.getCaption("label.userAuth.possible"));

            //不可能
            Item itemDisable = authContainer.addItem(false);
            itemDisable.getItemProperty(PID_AUTH).setValue(ViewProperties.getCaption("label.userAuth.impossible"));

            return authContainer;
        }

    }

    private void initValidation() {
        //TODO
    }

    private void initData() {
        //ユーザ権限情報取得
        UserService userService = BeanContext.getBean(UserService.class);
        this.userAuthDto = userService.getUserAuth(userNo, farmNo);

        //ユーザ権限情報取得
        UserManagementService userManagementService = BeanContext.getBean(UserManagementService.class);
        UserAuth userAuth = userManagementService.getUserAuth(userNo, farmNo);
        if (userAuth == null) {
            //権限情報が存在しない
            this.userAuthDto = new UserAuthDto(false);
        } else {
            //権限情報が存在する
            this.userAuthDto = new UserAuthDto(userAuth);
        }
    }

    private void showData() {
        //myCloud
        myCloudSelect.select(BooleanUtils.isTrue(userAuthDto.isFarmUse()));

        //サーバ権
        serverMakeSelect.select(BooleanUtils.isTrue(userAuthDto.isServerMake()));
        serverDeleteSelect.select(BooleanUtils.isTrue(userAuthDto.isServerDelete()));
        serverOperateSelect.select(BooleanUtils.isTrue(userAuthDto.isServerOperate()));

        //サービス権
        serviceMakeSelect.select(BooleanUtils.isTrue(userAuthDto.isServiceMake()));
        serviceDeleteSelect.select(BooleanUtils.isTrue(userAuthDto.isServiceDelete()));
        serviceOperateSelect.select(BooleanUtils.isTrue(userAuthDto.isServiceOperate()));

        //ロードバランサ権
        lbMakeSelect.select(BooleanUtils.isTrue(userAuthDto.isLbMake()));
        lbDeleteSelect.select(BooleanUtils.isTrue(userAuthDto.isLbDelete()));
        lbOperateSelect.select(BooleanUtils.isTrue(userAuthDto.isLbOperate()));
    }

    private UserAuthDto getInputData() {
        UserAuthDto authDto = new UserAuthDto(false);
        authDto.setFarmUse((Boolean) myCloudSelect.getValue());
        authDto.setServerMake((Boolean) serverMakeSelect.getValue());
        authDto.setServerDelete((Boolean) serverDeleteSelect.getValue());
        authDto.setServerOperate((Boolean) serverOperateSelect.getValue());
        authDto.setServiceMake((Boolean) serviceMakeSelect.getValue());
        authDto.setServiceDelete((Boolean) serviceDeleteSelect.getValue());
        authDto.setServiceOperate((Boolean) serviceOperateSelect.getValue());
        authDto.setLbMake((Boolean) lbMakeSelect.getValue());
        authDto.setLbDelete((Boolean) lbDeleteSelect.getValue());
        authDto.setLbOperate((Boolean) lbOperateSelect.getValue());
        return authDto;
    }

    private void okButtonClick(ClickEvent event) {
        //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
        //初期化時はParentWindowが設定されない為(NULL)、ここで設定する。
        setCustomErrorHandler(event.getButton());

        // 入力値を取得
        UserAuthDto inputAuthDto = getInputData();

        // 入力チェック
        //TODO

        //更新処理
        UserManagementService userManagementService = BeanContext.getBean(UserManagementService.class);
        userManagementService.updateUserAuth(farmNo, userNo, inputAuthDto);

        // 画面を閉じる
        close();
    }

    private void cancelButtonClick(ClickEvent event) {
        //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
        setCustomErrorHandler(event.getButton());
        WinUserAuthEditDetail.this.close();
    }

}
