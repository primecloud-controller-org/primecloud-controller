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

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ServerButtonsTop extends CssLayout {

    private MainView sender;

    private Button startAllButton;

    private Button stopAllButton;

    public ServerButtonsTop(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setWidth("100%");
        setMargin(true);
        addStyleName("server-buttons");
        addStyleName("server-table-label");

        Label labeal = new Label(ViewProperties.getCaption("label.server"), Label.CONTENT_XHTML);
        labeal.setWidth("200px");
        addComponent(labeal);

        // StopAllボタン
        stopAllButton = new Button(ViewProperties.getCaption("button.stopAllServers"));
        stopAllButton.setDescription(ViewProperties.getCaption("description.stopAllServers"));
        stopAllButton.setIcon(Icons.STOPMINI.resource());
        stopAllButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                stopAllButtonClick(event);
            }
        });
        stopAllButton.addStyleName("right");
        addComponent(stopAllButton);

        // StartAllボタン
        startAllButton = new Button(ViewProperties.getCaption("button.startAllServers"));
        startAllButton.setDescription(ViewProperties.getCaption("description.startAllServers"));
        startAllButton.setIcon(Icons.PLAYMINI.resource());
        startAllButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                startAllButtonClick(event);
            }
        });
        startAllButton.addStyleName("right");
        addComponent(startAllButton);

        initialize();
    }

    public void initialize() {
        stopAllButton.setEnabled(true);
        startAllButton.setEnabled(true);

        // 権限がなければボタンを無効にする
        UserAuthDto auth = ViewContext.getAuthority();
        if (!auth.isServerOperate()) {
            stopAllButton.setEnabled(false);
            startAllButton.setEnabled(false);
        }
    }

    private void startAllButtonClick(ClickEvent event) {
        // ダイアログの表示オプション
        VerticalLayout optionLayout = null;
        final CheckBox checkBox;
        String enableService = Config.getProperty("ui.enableService");
        if (enableService == null || BooleanUtils.toBoolean(enableService)) {
            optionLayout = new VerticalLayout();
            checkBox = new CheckBox(ViewMessages.getMessage("IUI-000035"), false);
            checkBox.setImmediate(true);
            optionLayout.addComponent(checkBox);
            optionLayout.setComponentAlignment(checkBox, Alignment.MIDDLE_CENTER);
        } else {
            checkBox = null;
        }

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000011");
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancelConfirm, optionLayout);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                boolean startService = (checkBox == null) ? false : (Boolean) checkBox.getValue();
                startAll(startService);
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void startAll(boolean startService) {
        // 選択されているサーバを保持する
        Long selectedInstanceNo = null;
        if (sender.serverPanel.serverTable.getValue() != null) {
            InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();
            selectedInstanceNo = instance.getInstance().getInstanceNo();
        }

        // オペレーションログ
        AutoApplication apl = (AutoApplication) getApplication();
        apl.doOpLog("SERVER", "All Server Start", null, null, null, String.valueOf(startService));

        // 全てのサーバを起動
        List<Long> instanceNos = new ArrayList<Long>();
        for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
            instanceNos.add(((InstanceDto) itemId).getInstance().getInstanceNo());
        }

        List<Long> componentNos = new ArrayList<Long>();
        for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
            componentNos.add(((ComponentDto) itemId).getComponent().getComponentNo());
        }

        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.startInstances(ViewContext.getFarmNo(), instanceNos, startService);

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサーバを選択し直す
        if (selectedInstanceNo != null) {
            for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                InstanceDto instance = (InstanceDto) itemId;
                if (selectedInstanceNo.equals(instance.getInstance().getInstanceNo())) {
                    sender.serverPanel.serverTable.select(itemId);
                    break;
                }
            }
        }
    }

    private void stopAllButtonClick(ClickEvent event) {
        // 確認ダイアログの表示
        String message = ViewMessages.getMessage("IUI-000012");
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancelConfirm);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                stopAll();
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void stopAll() {
        // 選択されているサーバを保持する
        Long selectedInstanceNo = null;
        if (sender.serverPanel.serverTable.getValue() != null) {
            InstanceDto instance = (InstanceDto) sender.serverPanel.serverTable.getValue();
            selectedInstanceNo = instance.getInstance().getInstanceNo();
        }

        // オペレーションログ
        AutoApplication apl = (AutoApplication) getApplication();
        apl.doOpLog("SERVER", "All Server Stop", null, null, null, null);

        // 全てのサーバを停止
        List<Long> instanceNos = new ArrayList<Long>();
        for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
            instanceNos.add(((InstanceDto) itemId).getInstance().getInstanceNo());
        }

        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.stopInstances(ViewContext.getFarmNo(), instanceNos);

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサーバを選択し直す
        if (selectedInstanceNo != null) {
            for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                InstanceDto instance = (InstanceDto) itemId;
                if (selectedInstanceNo.equals(instance.getInstance().getInstanceNo())) {
                    sender.serverPanel.serverTable.select(itemId);
                    break;
                }
            }
        }
    }

}
