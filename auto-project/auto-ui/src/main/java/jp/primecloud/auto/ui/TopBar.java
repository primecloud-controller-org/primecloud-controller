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

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Callback;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.IconUtils;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
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

    private MainView sender;

    private Button accountButton;

    public TopBar(MainView sender) {
        this.sender = sender;
    }

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
        Button myCloudButton = new Button(ViewProperties.getCaption("button.myCloudManage"));
        myCloudButton.setDescription(ViewProperties.getCaption("description.myCloudManage"));
        myCloudButton.addStyleName("borderless");
        myCloudButton.addStyleName("mycloud");
        myCloudButton.setIcon(Icons.CLOUDBIG.resource());
        myCloudButton.setVisible(true);
        myCloudButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showCloudEditWindow();
            }
        });
        addComponent(myCloudButton);

        // 監視システム(Zabbix)リンク
        String useZabbix = Config.getProperty("zabbix.useZabbix");
        if (useZabbix == null || BooleanUtils.toBoolean(useZabbix)) {
            String url = Config.getProperty("zabbix.display");
            Link zabbix = new Link(ViewProperties.getCaption("link.zabbix"), new ExternalResource(url));
            zabbix.setDescription(ViewProperties.getCaption("description.link.zabbix"));
            zabbix.setIcon(Icons.MNGSYSTEM.resource());
            zabbix.setTargetName("_blank");
            zabbix.addStyleName("zabbix");
            addComponent(zabbix);
        }

        // イベントログ表示ボタン
        Button eventLogButton = new Button(ViewProperties.getCaption("link.eventlog"));
        eventLogButton.setDescription(ViewProperties.getCaption("description.link.eventlog"));
        eventLogButton.addStyleName("borderless");
        eventLogButton.addStyleName("eventlog");
        eventLogButton.setIcon(Icons.CUSTOM.resource());
        eventLogButton.setVisible(true);
        eventLogButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WinLogView window = new WinLogView();
                getApplication().addWindow(window);
                getApplication().getMainWindow().open(new ExternalResource(window.getURL()), "_blank");
            }
        });
        addComponent(eventLogButton);

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
        Button logoutButton = new Button(ViewProperties.getCaption("button.logout"));
        logoutButton.setDescription(ViewProperties.getCaption("description.logout"));
        logoutButton.addStyleName("borderless");
        logoutButton.addStyleName("logout");
        logoutButton.setIcon(Icons.LOGOUT.resource());
        logoutButton.addListener(new Button.ClickListener() {
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
                            accountButton.setVisible(false);

                            // このアプリケーションのインスタンスを破棄する
                            getApplication().close();
                        }
                    }
                };
                dialog.setCallback(callback);
                getApplication().getMainWindow().addWindow(dialog);
            }
        });
        addComponent(logoutButton);

        // ログインアカウント管理ボタン
        accountButton = new Button(ViewProperties.getCaption("button.account"));
        accountButton.setDescription(ViewProperties.getCaption("description.account"));
        accountButton.addStyleName("borderless");
        accountButton.addStyleName("account");
        accountButton.setIcon(Icons.USER.resource());
        accountButton.setVisible(false);
        accountButton.addListener(new Button.ClickListener() {
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
        addComponent(accountButton);
    };

    public void loginSuccess() {
        // ログインユーザ名を表示
        accountButton.setCaption(ViewContext.getUsername());
        accountButton.setVisible(true);

        // myCloud一件もない場合は、ダイアログを表示する
        FarmService farmService = BeanContext.getBean(FarmService.class);
        List<FarmDto> farms = farmService.getFarms(ViewContext.getUserNo(), ViewContext.getLoginUser());
        if (farms.size() < 1) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"),
                    ViewMessages.getMessage("IUI-000038"));
            dialog.setCallback(new Callback() {
                @Override
                public void onDialogResult(Result result) {
                    // myCloud管理画面を表示
                    showCloudEditWindow();
                }
            });

            getApplication().getMainWindow().addWindow(dialog);
        } else {
            // myCloud管理画面を表示
            showCloudEditWindow();
        }
    }

    public void showCloudEditWindow() {
        MyCloudManage window = new MyCloudManage();
        window.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                sender.initialize();
                sender.refresh();
            }
        });
        getApplication().getMainWindow().addWindow(window);
    }

}
