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

import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.UserService;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.service.dto.UserDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * ログイン画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinLogin extends Window {

    Form form;

    TextField usernameField;

    TextField passwordField;

    WinLogin() {
        // Window
        setIcon(Icons.LOGIN.resource());
        setCaption(ViewProperties.getCaption("window.login"));
        setModal(true);
        setWidth("300px");
        setResizable(false);
        setClosable(false);

        // Layout
        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        // Form
        form = new Form();
        form.addStyleName("form-login");
        usernameField = new TextField(ViewProperties.getCaption("field.userName"));
        usernameField.setWidth("90%");
        passwordField = new TextField(ViewProperties.getCaption("field.password"));
        passwordField.setSecret(true);
        passwordField.setWidth("90%");

        form.getLayout().addComponent(usernameField);
        form.getLayout().addComponent(passwordField);
        layout.addComponent(form);

        //フォーカスを設定
        usernameField.focus();

        // Button
        Button ok = new Button(ViewProperties.getCaption("button.login"));
        ok.setDescription(ViewProperties.getCaption("description.login"));

        ok.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                loginButtonClick(event);
                    }
                });

        // [Enter]でOKボタンクリック
        ok.setClickShortcut(KeyCode.ENTER);

        layout.addComponent(ok);
        layout.setComponentAlignment(ok, "right");

        // 入力チェックの設定
        initValidation();
    }

    private void initValidation() {
        String message = ViewMessages.getMessage("IUI-000019");
        usernameField.setRequired(true);
        usernameField.setRequiredError(message);

        message = ViewMessages.getMessage("IUI-000020");
        passwordField.setRequired(true);
        passwordField.setRequiredError(message);
    }

    private void loginButtonClick(ClickEvent event) {
        // 入力値を取得
        String username = (String) usernameField.getValue();
        String password = (String) passwordField.getValue();

        // 入力チェック
        try {
            usernameField.validate();
            passwordField.validate();
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // ロジックを実行
        UserService userService = BeanContext.getBean(UserService.class);
        UserDto userDto;
        try {
            userDto = userService.authenticate(username, password);
        } catch (AutoApplicationException e) {
            // TODO: 認証情報が間違っている場合の処理
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), ViewMessages.getMessage("IUI-000021"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // ユーザ情報をセッションに格納
        User user = userDto.getUser();
        ViewContext.setUserNo(user.getMasterUser());
        ViewContext.setUsername(user.getUsername());
        ViewContext.setLoginUser(user.getUserNo());
        ViewContext.setPowerUser(user.getPowerUser());
        ViewContext.setPowerDefaultMaster(user.getMasterUser());
        if (user.getPowerUser() || user.getMasterUser().equals(user.getUserNo())){
            ViewContext.setAuthority(new UserAuthDto(true));
        }else{
            ViewContext.setAuthority(new UserAuthDto(false));
        }

        // ログイン画面を閉じる
        close();
    }

}
