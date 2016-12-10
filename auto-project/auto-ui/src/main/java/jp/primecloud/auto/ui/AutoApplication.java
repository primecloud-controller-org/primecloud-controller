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

import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * <p>
 * 一番最初に表示される画面を生成します。</br>
 * PCCの画面上部にあるPCCロゴ画像～ログアウトリンクまでを生成しています。</br>
 * </p>
 *
 */
@SuppressWarnings("serial")
public class AutoApplication extends Application {

    @Override
    public void init() {
        // エラーハンドリング
        setErrorHandler(new ErrorHandler(this));

        // ログアウト後の画面設定
        setLogoutURL("../../../");

        ViewContext.setAuthority(new UserAuthDto(false));

        Window mainWindow = new Window(ViewProperties.getCaption("window.main"));
        mainWindow.setWidth("960px");
        mainWindow.setHeight("100%");
        setTheme("classy");
        setMainWindow(mainWindow);

        MainView mainView = new MainView();
        mainWindow.setContent(mainView);
    }

}
