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

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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

    private TextField usernameField;

    private TextField passwordField;

    @Override
    public void attach() {
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
        Form form = new Form();
        form.addStyleName("form-login");

        // ユーザ名
        usernameField = new TextField(ViewProperties.getCaption("field.userName"));
        usernameField.setWidth("90%");
        usernameField.focus(); // フォーカスを設定
        usernameField.setRequired(true);
        usernameField.setRequiredError(ViewMessages.getMessage("IUI-000019"));
        form.getLayout().addComponent(usernameField);

        // パスワード
        passwordField = new TextField(ViewProperties.getCaption("field.password"));
        passwordField.setSecret(true);
        passwordField.setWidth("90%");
        passwordField.setRequired(true);
        passwordField.setRequiredError(ViewMessages.getMessage("IUI-000020"));
        form.getLayout().addComponent(passwordField);

        layout.addComponent(form);

        // ログインボタン
        Button loginButton = new Button(ViewProperties.getCaption("button.login"));
        loginButton.setDescription(ViewProperties.getCaption("description.login"));
        loginButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                loginButtonClick(event);
            }
        });
        loginButton.setClickShortcut(KeyCode.ENTER);

        layout.addComponent(loginButton);
        layout.setComponentAlignment(loginButton, "right");
    }

    private void loginButtonClick(ClickEvent event) {
        // 入力値を取得
        String username = (String) usernameField.getValue();
        String password = (String) passwordField.getValue();

        // 入力チェック
        usernameField.validate();
        passwordField.validate();

        // ログイン処理
        UserService userService = BeanContext.getBean(UserService.class);
        UserDto userDto;
        try {
            userDto = userService.authenticate(username, password);
        } catch (AutoApplicationException e) {
            // 認証情報が間違っている場合の処理
            throw new AutoApplicationException("IUI-000021");
        }

        // ユーザ情報をセッションに格納
        User user = userDto.getUser();
        ViewContext.setUserNo(user.getMasterUser());
        ViewContext.setUsername(user.getUsername());
        ViewContext.setLoginUser(user.getUserNo());
        ViewContext.setPowerUser(user.getPowerUser());
        ViewContext.setPowerDefaultMaster(user.getMasterUser());
        if (user.getPowerUser() || user.getMasterUser().equals(user.getUserNo())) {
            ViewContext.setAuthority(new UserAuthDto(true));
        } else {
            ViewContext.setAuthority(new UserAuthDto(false));
        }

        // ログイン画面を閉じる
        close();
    }

}
