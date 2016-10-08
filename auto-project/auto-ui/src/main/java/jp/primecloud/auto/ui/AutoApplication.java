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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.log.service.OperationLogService;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
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
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
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

    Window mainWindow;

    GridLayout grid;

    VerticalLayout main;

    TopBar top;

    MyCloud myCloud;

    @Override
    public void init() {
        //初期レイアウト
        initMainLayout();

        // ログイン画面
        initLogin();

        // ログアウト後の画面設定
        setLogoutURL("../../../");
    }

    private void initMainLayout() {
        main = new VerticalLayout();
        main.setSizeFull();

        mainWindow = new Window(ViewProperties.getCaption("window.main"));
        setMainWindow(mainWindow);

        mainWindow.setContent(main);
        mainWindow.setWidth("960px");
        mainWindow.setHeight("100%");
        setTheme("classy");

        //画面上部
        top = new TopBar();
        main.addComponent(top);

        // エラーハンドリング
        setErrorHandler(new ErrorHandler(mainWindow));
    }

    private void initLogin() {
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
        //クラウド表示
        myCloud = new MyCloud();
        main.addComponent(myCloud);
        main.setExpandRatio(myCloud, 100);

        // ログインユーザ名表示
        top.btnAccount.setCaption(ViewContext.getUsername());
        top.btnAccount.setVisible(true);

//        //イベントログ インスタンス作成,URL設定
//        WinLogView window = new WinLogView();
//        addWindow(window);
//        top.logView.setResource(new ExternalResource(window.getURL()));



        // クラウドが一件もない場合は、ダイヤログを表示する。
        FarmService farmService = BeanContext.getBean(FarmService.class);
        List<FarmDto> farms = farmService.getFarms(ViewContext.getUserNo(), ViewContext.getLoginUser());
        if (farms.size() < 1 ){
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), ViewMessages.getMessage("IUI-000038"));
            dialog.setCallback(new Callback() {
                @Override
                public void onDialogResult(Result result) {
                    // クラウド選択画面を表示
                    showCloudEditWindow();
                }
            });

            getMainWindow().addWindow(dialog);
        }else{
            // クラウド選択画面を表示
            showCloudEditWindow();
        }


    }

    private class TopBar extends CssLayout {

        Button btnAccount;

        Button btnLogout;

        Button btnMyCloud;

        Button btnLogView;

//        Link logView;

        TopBar() {
            addStyleName("TopBar");
            setWidth("100%");
            setHeight("30px");
            setMargin(false, true, false, false);

            // PrimeCloudラベル
            Label plbl = new Label("<img src=\"" + VaadinUtils.getIconPath(mainWindow.getApplication(), Icons.PCCLOGO ) + "\">",
                    Label.CONTENT_XHTML);
            plbl.addStyleName("logo");
            addComponent(plbl);

            // バージョン番号表示
            String versionProp = Config.getVersionProperty("version");
            if( StringUtils.isNotEmpty(versionProp)){
                StringBuilder version = new StringBuilder();
                version.append("ver").append(versionProp);
                //リビジョン非表示へ変更
                //String buildNumberProp = Config.getVersionProperty("buildNumber");
                //if(StringUtils.isNotEmpty(buildNumberProp) && buildNumberProp.matches("^[0-9]+")){
                //   version.append("-").append(buildNumberProp);
                //}
                Label versionNo = new Label("<p>" + version.toString() + "</p>", Label.CONTENT_XHTML);
                versionNo.addStyleName("versionNo");
                addComponent(versionNo);
            } else {
                Label versionNo = new Label("<p></p>", Label.CONTENT_XHTML);
                versionNo.addStyleName("versionNoNone");
                addComponent(versionNo);
            }

            //クラウド管理ボタン
            btnMyCloud = new Button(ViewProperties.getCaption("button.myCloudManage"));
            btnMyCloud.setDescription(ViewProperties.getCaption("description.myCloudManage"));
            btnMyCloud.addStyleName("borderless");
            btnMyCloud.addStyleName("mycloud");
            btnMyCloud.setIcon(Icons.CLOUDBIG.resource());
            btnMyCloud.setVisible(true);
            btnMyCloud.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    showCloudEditWindow();
                }
            });
            addComponent(btnMyCloud);

            //監視システム(Zabbix)リンク
            String url =  Config.getProperty("zabbix.display");
            Link zabbix = new Link(ViewProperties.getCaption("link.zabbix"), new ExternalResource(url));
            zabbix.setDescription(ViewProperties.getCaption("description.link.zabbix"));
            zabbix.setIcon(Icons.MNGSYSTEM.resource());
            zabbix.setTargetName("_blank");
            zabbix.addStyleName("zabbix");
            addComponent(zabbix);

//            //イベントログ表示ボタン(Link)
//            logView = new Link("EventLog",null);
//            logView.setIcon(Icons.CUSTOM.resource());
//            logView.setTargetName("_blank");
//            logView.addStyleName("eventlog");
//            addComponent(logView);

            //イベントログ表示ボタン
            btnLogView = new Button(ViewProperties.getCaption("link.eventlog"));
            btnLogView.setDescription(ViewProperties.getCaption("description.link.eventlog"));
            btnLogView.addStyleName("borderless");
            btnLogView.addStyleName("eventlog");
            btnLogView.setIcon(Icons.CUSTOM.resource());
            btnLogView.setVisible(true);
            btnLogView.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    WinLogView window = new WinLogView();
                    getApplication().addWindow(window);
                    mainWindow.open(new ExternalResource( window.getURL()) , "_blank" );
                }
            });
            addComponent(btnLogView);

            //課金システムリンク
            Boolean usePayment = BooleanUtils.toBooleanObject(Config.getProperty("payment.usePayment"));
            if (BooleanUtils.isTrue(usePayment)) {
                String url2 =  Config.getProperty("payment.display");
                Link payment  = new Link(ViewProperties.getCaption("link.payment"), new ExternalResource(url2));
                payment.setDescription(ViewProperties.getCaption("description.link.payment"));
                payment.setIcon(Icons.PAYSYSTEM.resource());
                payment.setTargetName("_payment");
                payment.addStyleName("payment");
                addComponent(payment);
            }



            //ログアウトボタン
            btnLogout = new Button(ViewProperties.getCaption("button.logout"));
            btnLogout.setDescription(ViewProperties.getCaption("description.logout"));
            btnLogout.addStyleName("borderless");
            btnLogout.addStyleName("logout");
            btnLogout.setIcon(Icons.LOGOUT.resource());
            btnLogout.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.dialogConfirm"),
                            ViewMessages.getMessage("IUI-000001"), Buttons.OKCancel);
                    Callback callback = new Callback() {
                        @Override
                        public void onDialogResult(Result result) {
                            if (result == Result.OK) {
                                // セッション情報を初期化
                                LoggingUtils.removeContext();
                                ContextUtils.invalidateSession();
                                btnAccount.setVisible(false);

                                // このアプリケーションのインスタンスを破棄する
                                close();
                            }
                        }
                    };
                    dialog.setCallback(callback);
                    mainWindow.addWindow(dialog);
                }
            });
            addComponent(btnLogout);

            //ログインアカウント管理ボタン
            btnAccount = new Button(ViewProperties.getCaption("button.account"));
            btnAccount.setDescription(ViewProperties.getCaption("description.account"));
            btnAccount.addStyleName("borderless");
            btnAccount.addStyleName("account");
            btnAccount.setIcon(Icons.USER.resource());
            btnAccount.setVisible(false);
            btnAccount.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    /*
                    //マスターユーザーにのみ開放する
                    if (ViewContext.getPowerDefaultMaster().equals(ViewContext.getLoginUser())){
                        WinUserManagement window = new WinUserManagement();
                        getApplication().addWindow(window);
                        mainWindow.open(new ExternalResource( window.getURL()) , "_user" );
                    }
                    */
                }
            });
            addComponent(btnAccount);
        }
    }

    private void showCloudEditWindow() {

        MyCloudManage window = new MyCloudManage( mainWindow.getApplication() );
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                myCloud.hide();
                myCloud.refresh();
            }
        });
        mainWindow.addWindow(window);
    }

    //ファーム関連用
    public void doOpLog(String screen, String operation, Long farmNo, String memo){
        doOpLog(ViewContext.getUserNo(),
                ViewContext.getUsername(),
                farmNo,
                screen,
                operation,
                null,
                null,
                null,
                memo);

    }

    //通常処理
    public void doOpLog(String screen, String operation, Long instanceNo, Long componentNo, Long loadBalancerNo, String memo){
        doOpLog(ViewContext.getUserNo(),
                ViewContext.getUsername(),
                ViewContext.getFarmNo(),
                screen,
                operation,
                instanceNo,
                componentNo,
                loadBalancerNo,
                memo);

    }

    public void doOpLog(Long userNo, String userName, Long farmNo, String screen, String operation, Long instanceNo, Long componentNo, Long loadBalancerNo, String memo){
        OperationLogService orerationLogService = BeanContext.getBean(OperationLogService.class);
        orerationLogService.writeOperationLog(userNo,
                userName,
                farmNo,
                screen,
                operation,
                instanceNo,
                componentNo,
                loadBalancerNo,
                memo);

    }
}
