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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.entity.crud.AuthoritySet;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.UserAuth;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.UserManagementService;
import jp.primecloud.auto.service.UserService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.UserDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ConvertUtil;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * 新規ユーザー追加/ユーザ編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinUserAuthAddEdit extends Window {

    //ユーザ番号(ログインユーザ)
    private Long masterUserNo;

    //ユーザ番号(ユーザ編集画面、編集対象ユーザ)
    private Long userNo;

    //ユーザ情報(編集対象のユーザ情報)
    private UserDto userDto;

    //ファーム情報
    //(新規ユーザ追加画面:マスタユーザに紐づくファーム、編集画面:対象ユーザに紐づく権限)
    private List<FarmDto> farmDtos;

    //権限情報マップ<ファーム番号, UserAuth>
    private Map<Long, UserAuth> userAuthMap;

    //権限セット情報
    private List<AuthoritySet> authoritySets;

    //新規ユーザー追加/ユーザ編集画面 判定フラグ
    private Boolean isAddUser;

    //権限セット変換ユーティリティクラス
    private ConvertUtil convertUtil;

    ///// vaadin component /////
    //ユーザ名 テキストフィールド
    private TextField userNameField;

    //ユーザ名 テキストフィールド
    private TextField passwordField;

    //ユーザ権限テーブル
    private UserAuthTable userAuthTable;

    WinUserAuthAddEdit(Long masterUserNo, Long userNo) {
        this.masterUserNo = masterUserNo;
        this.userNo = userNo;

        //画面判定フラグ設定
        isAddUser = (userNo == null) ? true : false;

        // 初期データの取得
        initData();

        //ウインドウ設定
        //画面名、アイコン設定
        if (isAddUser) {
            //新規ユーザ追加画面
            setIcon(Icons.ADD.resource());
            setCaption(ViewProperties.getCaption("window.winUserAdd"));
        } else {
            //ユーザ編集画面
            setIcon(Icons.EDIT.resource());
            setCaption(ViewProperties.getCaption("window.winUserEdit"));
        }
        //モーダル
        setModal(true);
        setWidth("450px");
        //リサイズ
        setResizable(false);

        //レイアウト
        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(false, true, false, true);
        layout.setSpacing(false);

        // フォーム
        layout.addComponent(new AuthAddForm());

        // ユーザ権限テーブル
        userAuthTable = new UserAuthTable();
        layout.addComponent(userAuthTable);

        // 下部のバー
        HorizontalLayout okbar = new HorizontalLayout();
        okbar.setSpacing(true);
        okbar.setMargin(true, false, true, false);
        layout.addComponent(okbar);
        layout.setComponentAlignment(okbar, Alignment.BOTTOM_RIGHT);

        // OKボタン
        Button okButton = new Button(ViewProperties.getCaption("button.ok"));
        if (isAddUser) {
            //新規ユーザ追加画面
            okButton.setDescription(ViewProperties.getCaption("description.addUserAuth.ok"));
        } else {
            //ユーザ編集画面
            okButton.setDescription(ViewProperties.getCaption("description.editUserAuth.ok"));
        }
        okButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinUserAuthAddEdit.this.okButtonClick(event);
            }
        });
        okbar.addComponent(okButton);

        //Deleteボタン
        if (!isAddUser) {
            //ユーザ編集画面の場合のみ表示
            Button deleteButton = new Button(ViewProperties.getCaption("button.delete"));
            deleteButton.setDescription(ViewProperties.getCaption("description.editUserAuth.delete"));
            deleteButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    WinUserAuthAddEdit.this.deleteButtonClick(event);
                }
            });
            okbar.addComponent(deleteButton);
        }

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinUserAuthAddEdit.this.cancelButtonClick(event);
            }
        });
        okbar.addComponent(cancelButton);

        // ユーザ権限テーブルの表示
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

    private class AuthAddForm extends Form {

        AuthAddForm() {
            setImmediate(true);

            //ユーザ名
            userNameField = new TextField(ViewProperties.getCaption("field.userName"));
            userNameField.setImmediate(true);
            getLayout().addComponent(userNameField);

            //ユーザパスワード
            passwordField = new TextField(ViewProperties.getCaption("field.password"));
            passwordField.setSecret(true);
            passwordField.setImmediate(true);
            getLayout().addComponent(passwordField);
        }

    }

    private class UserAuthTable extends Table {

        private final String[] HEADER_NAMES_ADD = { ViewProperties.getCaption("field.couldname"),
                ViewProperties.getCaption("label.userAuth.authority") };

        private final String[] HEADER_NAMES_EDIT = { ViewProperties.getCaption("field.couldname"),
                ViewProperties.getCaption("label.userAuth.authority"), ViewProperties.getCaption("label.userAuth.edit") };

        private final String PID_TABLE_CLOUD_NAME = "CloudName";

        private final String PID_TABLE_AUTHORITY_SET = "AuthoritySet";

        private final String PID_TABLE_EDIT = "Edit";

        private final String COLUMN_HEIGHT = "28px";

        private final String PID_COMBOX_AUTHORITY_SET = "AuthoritySet";

        private final String AUTHORITY_SET_NOTHING = ViewProperties.getCaption("label.nothing");

        private final String AUTHORITY_SET_CUSTOM = ViewProperties.getCaption("label.custom");

        private final Long AUTHORITY_SET_SETNO_CUSTOM = new Long(-1);

        private final Long AUTHORITY_SET_SETNO_NOTHING = new Long(0);

        UserAuthTable() {
            //テーブル基本設定
            setWidth("100%");
            setHeight("363px");
            setPageLength(10);
            setSortDisabled(true);
            setColumnCollapsingAllowed(false);
            setColumnReorderingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("user-table");

            //カラム設定
            addContainerProperty(PID_TABLE_CLOUD_NAME, Label.class, null);
            addContainerProperty(PID_TABLE_AUTHORITY_SET, ComboBox.class, null);
            if (!isAddUser) {
                //ユーザ編集画面の場合
                addContainerProperty(PID_TABLE_EDIT, Button.class, null);
            }
            setColumnHeaders((isAddUser) ? HEADER_NAMES_ADD : HEADER_NAMES_EDIT);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        private void showUserAuths() {
            userAuthTable.removeAllItems();

            // ファーム情報をテーブルに追加
            for (FarmDto farmDto : farmDtos) {
                final Farm farm = farmDto.getFarm();

                //「マイクラウド名」カラム
                Label myCloudLbl = new Label(farm.getFarmName());
                myCloudLbl.setHeight(COLUMN_HEIGHT);

                //「権限」カラム
                ComboBox authoritySetSelect = new ComboBox();
                authoritySetSelect.setItemCaptionPropertyId(PID_COMBOX_AUTHORITY_SET);
                authoritySetSelect.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
                authoritySetSelect.setNullSelectionAllowed(false);
                //コンボボックスアイテム設定
                authoritySetSelect.setContainerDataSource(createAuthoritySetContainer());
                //初期選択設定
                if (isAddUser) {
                    //新規ユーザ追加画面 → 「権限無し」
                    authoritySetSelect.select(userAuthTable.AUTHORITY_SET_SETNO_NOTHING);
                } else {
                    //ユーザ編集画面 → ユーザ権限に合致する権限セットを選択
                    UserAuth userAuth = userAuthMap.get(farm.getFarmNo());
                    authoritySetSelect.select(convertUtil.ConvertAuthToSetNo(userAuth));
                }

                if (isAddUser) {
                    //新規ユーザ追加画面の場合
                    userAuthTable.addItem(new Object[] { myCloudLbl, authoritySetSelect }, farm.getFarmNo());
                } else {
                    //ユーザ編集画面の場合のみ設定
                    //「編集」カラム
                    Button btnEdit = new Button(ViewProperties.getCaption("button.editUserAuthDetail"));
                    btnEdit.setDescription(ViewProperties.getCaption("description.editUserAuthDetail"));
                    btnEdit.addStyleName("borderless");
                    btnEdit.addListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            editButtonClick(event, farm.getFarmNo());
                        }
                    });
                    userAuthTable.addItem(new Object[] { myCloudLbl, authoritySetSelect, btnEdit }, farm.getFarmNo());
                }
            }

            Long farmNo = null;
            if (userAuthTable.getItemIds().size() > 0) {
                farmNo = (Long) userAuthTable.getItemIds().toArray()[0];
            }

            // 先頭のクラウド情報を選択する
            userAuthTable.select(farmNo);
        }

        private IndexedContainer createAuthoritySetContainer() {
            IndexedContainer authoritySetContainer = new IndexedContainer();
            authoritySetContainer.addContainerProperty(PID_COMBOX_AUTHORITY_SET, String.class, null);

            for (AuthoritySet authoritySet : authoritySets) {
                Item item = authoritySetContainer.addItem(authoritySet.getSetNo());
                item.getItemProperty(PID_COMBOX_AUTHORITY_SET).setValue(authoritySet.getSetName());
            }

            if (!isAddUser) {
                //新規ユーザ追加画面の場合のみ、「カスタム」を選択できる
                //権限、「カスタム」
                Item itemCustom = authoritySetContainer.addItem(AUTHORITY_SET_SETNO_CUSTOM);
                itemCustom.getItemProperty(PID_COMBOX_AUTHORITY_SET).setValue(AUTHORITY_SET_CUSTOM);
            }
            //権限、「権限無し」
            Item itemNothing = authoritySetContainer.addItem(AUTHORITY_SET_SETNO_NOTHING);
            itemNothing.getItemProperty(PID_COMBOX_AUTHORITY_SET).setValue(AUTHORITY_SET_NOTHING);

            return authoritySetContainer;
        }

        private void editButtonClick(ClickEvent event, final Long farmNo) {
            //編集中データ破棄確認
            String message = ViewMessages.getMessage("IUI-000119");
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                    Buttons.OKCancel);
            //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
            setCustomErrorHandler(dialog.okButton);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    if (result != Result.OK) {
                        //OKボタンが押されていない場合
                        return;
                    }

                    //ユーザ詳細編集画面表示
                    WinUserAuthEditDetail winUserAuthDetailEdit = new WinUserAuthEditDetail(userNo, farmNo);
                    winUserAuthDetailEdit.addListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent e) {
                            //戻ってきたらリフレッシュ
                            initData();
                            showData();
                        }
                    });
                    WinUserAuthAddEdit.this.getParent().addWindow(winUserAuthDetailEdit);
                }
            });
            WinUserAuthAddEdit.this.getParent().addWindow(dialog);
        }

    }

    private void initValidation() {
        //ユーザ名
        String message = ViewMessages.getMessage("IUI-000117");
        userNameField.setRequired(true);
        userNameField.setRequiredError(message);
        userNameField.addValidator(new StringLengthValidator(message, -1, 15, false));
        userNameField.addValidator(new RegexpValidator("^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", true, message));

        //パスワード
        message = ViewMessages.getMessage("IUI-000118");
        passwordField.setRequired(true);
        passwordField.setRequiredError(message);
        passwordField.addValidator(new StringLengthValidator(message, 1, 15, false));
        passwordField.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (isValid(value)) {
                    throw new InvalidValueException(ViewMessages.getMessage("IUI-000118"));
                }
            }

            @Override
            public boolean isValid(Object value) {
                if (value == null || !(value instanceof String)) {
                    return false;
                }
                return ((String) value).matches(".*&.*|.*\".*|.*'.*|.*<.*|.*>.*");
            }
        });
    }

    private void initData() {
        // ユーザ情報取得
        if (!isAddUser) {
            //ユーザ編集画面の場合
            UserService userService = BeanContext.getBean(UserService.class);
            this.userDto = userService.getUser(userNo);
        }

        // ファーム情報取得(マスタユーザに紐づくファーム)
        UserManagementService userManagementService = BeanContext.getBean(UserManagementService.class);
        this.farmDtos = userManagementService.getFarms(masterUserNo);

        //権限セット情報取得(全件取得)
        this.authoritySets = userManagementService.getAuthoritySet();

        if (!isAddUser) {
            //ユーザ編集画面の場合
            //ユーザ権限情報取得
            this.userAuthMap = userManagementService.getUserAuthMap(userNo);
            //権限セット変換クラス
            this.convertUtil = new ConvertUtil();
        }
    }

    private void showData() {
        if (!isAddUser) {
            //ユーザ編集画面の場合
            //ユーザ名表示
            userNameField.setValue(userDto.getUser().getUsername());
            //パスワード表示
            passwordField.setValue(userDto.getUser().getPassword());
        }

        //権限テーブルの表示
        userAuthTable.showUserAuths();
    }

    private void okButtonClick(ClickEvent event) {
        //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
        //初期化時はParentWindowが設定されない為(NULL)、ここで設定する。
        setCustomErrorHandler(event.getButton());

        //入力チェック
        try {
            //ユーザ名
            userNameField.validate();
            //パスワード
            passwordField.validate();
        } catch (InvalidValueException e) {
            String errMes = e.getMessage();
            if (null == errMes) {
                //メッセージが取得できない場合は複合エラー 先頭を表示する
                InvalidValueException[] exceptions = e.getCauses();
                errMes = exceptions[0].getMessage();
            }

            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), errMes);
            getParent().addWindow(dialog);
            return;
        }

        // 入力値の取得
        //ユーザ名
        String userName = (String) userNameField.getValue();
        //パスワード
        String password = (String) passwordField.getValue();

        //権限テーブル
        Map<Long, Long> authMap = new HashMap<Long, Long>();
        for (int i = 0; i < userAuthTable.getItemIds().size(); i++) {
            Long farmNo = (Long) userAuthTable.getItemIds().toArray()[i];
            Item item = userAuthTable.getItem(farmNo);
            ComboBox comBox = (ComboBox) item.getItemProperty(userAuthTable.PID_TABLE_AUTHORITY_SET).getValue();
            Long setNo = (Long) comBox.getValue();
            if (userAuthTable.AUTHORITY_SET_SETNO_CUSTOM.equals(setNo)) {
                //「権限」が「カスタム」のデータはこの画面では更新しない
                continue;
            } else {
                //※「権限」が「権限無し」のデータは、setNoに「0」が設定されている
                authMap.put(farmNo, setNo);
            }
        }

        //更新処理
        try {
            UserManagementService userManagementService = BeanContext.getBean(UserManagementService.class);
            if (isAddUser) {
                //新規ユーザ追加画面
                userManagementService.createUserAndUserAuth(masterUserNo, userName, password, authMap);
            } else {
                //ユーザ編集画面
                userManagementService.updateUserAndUserAuth(userNo, userName, password, authMap);
            }
        } catch (AutoApplicationException e) {
            //更新エラーの場合
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getParent().addWindow(dialog);
            return;
        }

        // 画面を閉じる
        close();
    }

    private void deleteButtonClick(ClickEvent event) {
        //編集中データ破棄確認
        String message = ViewMessages.getMessage("IUI-000120");
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
        setCustomErrorHandler(dialog.okButton);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    //OKボタンが押されていない場合
                    return;
                }

                //更新処理
                try {
                    UserManagementService userManagementService = BeanContext.getBean(UserManagementService.class);
                    userManagementService.deleteUser(userNo);
                } catch (AutoApplicationException e) {
                    //更新エラーの場合
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getParent().addWindow(dialog);
                    return;
                }

                // 画面を閉じる
                close();
            }
        });
        getParent().addWindow(dialog);
    }

    private void cancelButtonClick(ClickEvent event) {
        //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
        setCustomErrorHandler(event.getButton());
        WinUserAuthAddEdit.this.close();
    }

}
