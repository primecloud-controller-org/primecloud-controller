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

import jp.primecloud.auto.log.service.OperationLogService;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.Application;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * <p>
 * 一番最初に表示される画面を生成します。</br>
 * PCCの画面上部にあるPCCロゴ画像～ログアウトリンクまでを生成しています。</br>
 * </p>
 *
 */
@SuppressWarnings("serial")
public class AutoApplication extends Application {

    private VerticalLayout main;

    private TopBar top;

    MyCloud myCloud;

    @Override
    public void init() {
        // 初期レイアウト
        main = new VerticalLayout();
        main.setSizeFull();

        Window mainWindow = new Window(ViewProperties.getCaption("window.main"));
        mainWindow.setContent(main);
        mainWindow.setWidth("960px");
        mainWindow.setHeight("100%");
        setTheme("classy");
        setMainWindow(mainWindow);

        // エラーハンドリング
        setErrorHandler(new ErrorHandler(this));

        // ログアウト後の画面設定
        setLogoutURL("../../../");

        // 画面上部
        top = new TopBar();
        main.addComponent(top);

        // ログイン画面
        WinLogin winLogin = new WinLogin();
        winLogin.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                // ログインに成功した場合
                Long loginUserNo = ViewContext.getLoginUser();
                if (loginUserNo != null) {
                    loginSuccess();
                }
            }
        });
        mainWindow.addWindow(winLogin);
    }

    private void loginSuccess() {
        // myCloud表示
        myCloud = new MyCloud();
        main.addComponent(myCloud);
        main.setExpandRatio(myCloud, 100);

        // ログインユーザ名表示
        top.showUserName(ViewContext.getUsername());

        // myCloud一件もない場合は、ダイアログを表示する。
        FarmService farmService = BeanContext.getBean(FarmService.class);
        List<FarmDto> farms = farmService.getFarms(ViewContext.getUserNo(), ViewContext.getLoginUser());
        if (farms.size() < 1) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"),
                    ViewMessages.getMessage("IUI-000038"));
            dialog.setCallback(new Callback() {
                @Override
                public void onDialogResult(Result result) {
                    // myCloud管理画面を表示
                    top.showCloudEditWindow();
                }
            });

            getMainWindow().addWindow(dialog);
        } else {
            // myCloud管理画面を表示
            top.showCloudEditWindow();
        }
    }

    public void doOpLog(String screen, String operation, Long farmNo, String memo) {
        doOpLog(ViewContext.getUserNo(), ViewContext.getUsername(), farmNo, screen, operation, null, null, null, memo);
    }

    //通常処理
    public void doOpLog(String screen, String operation, Long instanceNo, Long componentNo, Long loadBalancerNo,
            String memo) {
        doOpLog(ViewContext.getUserNo(), ViewContext.getUsername(), ViewContext.getFarmNo(), screen, operation,
                instanceNo, componentNo, loadBalancerNo, memo);
    }

    public void doOpLog(Long userNo, String userName, Long farmNo, String screen, String operation, Long instanceNo,
            Long componentNo, Long loadBalancerNo, String memo) {
        OperationLogService orerationLogService = BeanContext.getBean(OperationLogService.class);
        orerationLogService.writeOperationLog(userNo, userName, farmNo, screen, operation, instanceNo, componentNo,
                loadBalancerNo, memo);
    }

}
