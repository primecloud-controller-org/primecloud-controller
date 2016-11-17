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

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

@SuppressWarnings("serial")
public class TopBar extends CssLayout {

    private Button btnAccount;

    @Override
    public void attach() {
        addStyleName("TopBar");
        setWidth("100%");
        setHeight("30px");
        setMargin(false, true, false, false);

        // PrimeCloudラベル
        Label plbl = new Label(IconUtils.createImageTag(getApplication(), Icons.PCCLOGO), Label.CONTENT_XHTML);
        plbl.addStyleName("logo");
        addComponent(plbl);

        // バージョン番号表示
        String versionProp = Config.getVersionProperty("version");
        if (StringUtils.isNotEmpty(versionProp)) {
            StringBuilder version = new StringBuilder();
            version.append("ver").append(versionProp);
            Label versionNo = new Label("<p>" + version.toString() + "</p>", Label.CONTENT_XHTML);
            versionNo.addStyleName("versionNo");
            addComponent(versionNo);
        } else {
            Label versionNo = new Label("<p></p>", Label.CONTENT_XHTML);
            versionNo.addStyleName("versionNoNone");
            addComponent(versionNo);
        }

        // myCloud管理ボタン
        Button btnMyCloud = new Button(ViewProperties.getCaption("button.myCloudManage"));
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

        // 監視システム(Zabbix)リンク
        String url = Config.getProperty("zabbix.display");
        Link zabbix = new Link(ViewProperties.getCaption("link.zabbix"), new ExternalResource(url));
        zabbix.setDescription(ViewProperties.getCaption("description.link.zabbix"));
        zabbix.setIcon(Icons.MNGSYSTEM.resource());
        zabbix.setTargetName("_blank");
        zabbix.addStyleName("zabbix");
        addComponent(zabbix);

        // イベントログ表示ボタン
        Button btnLogView = new Button(ViewProperties.getCaption("link.eventlog"));
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
                getApplication().getMainWindow().open(new ExternalResource(window.getURL()), "_blank");
            }
        });
        addComponent(btnLogView);

        // 課金システムリンク
        Boolean usePayment = BooleanUtils.toBooleanObject(Config.getProperty("payment.usePayment"));
        if (BooleanUtils.isTrue(usePayment)) {
            String url2 = Config.getProperty("payment.display");
            Link payment = new Link(ViewProperties.getCaption("link.payment"), new ExternalResource(url2));
            payment.setDescription(ViewProperties.getCaption("description.link.payment"));
            payment.setIcon(Icons.PAYSYSTEM.resource());
            payment.setTargetName("_payment");
            payment.addStyleName("payment");
            addComponent(payment);
        }

        // ログアウトボタン
        Button btnLogout = new Button(ViewProperties.getCaption("button.logout"));
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
                            getApplication().close();
                        }
                    }
                };
                dialog.setCallback(callback);
                getApplication().getMainWindow().addWindow(dialog);
            }
        });
        addComponent(btnLogout);

        // ログインアカウント管理ボタン
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
    };

    public void showUserName(String userName) {
        // ログインユーザ名表示
        btnAccount.setCaption(userName);
        btnAccount.setVisible(true);
    }

    public void showCloudEditWindow() {
        MyCloudManage window = new MyCloudManage(getApplication());
        window.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                ((AutoApplication) getApplication()).myCloud.hide();
                ((AutoApplication) getApplication()).myCloud.refresh();
            }
        });
        getApplication().getMainWindow().addWindow(window);
    }

}
