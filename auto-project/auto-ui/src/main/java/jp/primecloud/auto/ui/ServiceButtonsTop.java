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

import jp.primecloud.auto.common.status.ComponentStatus;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.OperationLogger;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.StringUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ServiceButtonsTop extends CssLayout {

    private MainView sender;

    private Button startAllButton;

    private Button stopAllButton;

    public ServiceButtonsTop(MainView sender) {
        this.sender = sender;
    }

    @Override
    public void attach() {
        setWidth("100%");
        setMargin(false);
        addStyleName("service-buttons");
        addStyleName("service-table-label");

        Label label = new Label(ViewProperties.getCaption("label.service"), Label.CONTENT_XHTML);
        label.setWidth("200px");
        addComponent(label);

        // StopAllボタン
        stopAllButton = new Button(ViewProperties.getCaption("button.stopAllServices"));
        stopAllButton.setDescription(ViewProperties.getCaption("description.stopAllServices"));
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
        startAllButton = new Button(ViewProperties.getCaption("button.startAllServices"));
        startAllButton.setDescription(ViewProperties.getCaption("description.startAllServices"));
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
        if (!auth.isServiceOperate()) {
            stopAllButton.setEnabled(false);
            startAllButton.setEnabled(false);
        }
    }

    private void startAllButtonClick(ClickEvent event) {
        // 変更中のサービスが存在する場合は操作させない
        for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
            ComponentDto component = (ComponentDto) itemId;
            if (component.getStatus().equals(ComponentStatus.STARTING.toString())
                    || component.getStatus().equals(ComponentStatus.STOPPING.toString())
                    || component.getStatus().equals(ComponentStatus.CONFIGURING.toString())) {
                String message = ViewMessages.getMessage("IUI-000046",
                        new Object[] { StringUtils.capitalize(component.getStatus().toLowerCase()) });
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                getApplication().getMainWindow().addWindow(dialog);
                return;
            }
        }

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000009");
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancelConfirm);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                startAll();
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void startAll() {
        // 選択されているサービスを保持する
        Long selectedComponentNo = null;
        if (sender.servicePanel.serviceTable.getValue() != null) {
            ComponentDto component = (ComponentDto) sender.servicePanel.serviceTable.getValue();
            selectedComponentNo = component.getComponent().getComponentNo();
        }

        // オペレーションログ
        OperationLogger.write("SERVICE", "All Service Start", null);

        // 全てのサービスを開始
        List<Long> componentNos = new ArrayList<Long>();
        for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
            componentNos.add(((ComponentDto) itemId).getComponent().getComponentNo());
        }

        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.startComponents(ViewContext.getFarmNo(), componentNos);

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサービスを選択し直す
        if (selectedComponentNo != null) {
            for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                ComponentDto component = (ComponentDto) itemId;
                if (selectedComponentNo.equals(component.getComponent().getComponentNo())) {
                    sender.servicePanel.serviceTable.select(itemId);
                    break;
                }
            }
        }
    }

    private void stopAllButtonClick(ClickEvent event) {
        // ダイアログの表示オプション
        HorizontalLayout optionLayout = new HorizontalLayout();
        final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000033"), false);
        checkBox.setImmediate(true);
        optionLayout.addComponent(checkBox);

        // 確認ダイアログを表示
        String message = ViewMessages.getMessage("IUI-000010");
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancelConfirm, optionLayout);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                boolean stopInstance = (Boolean) checkBox.getValue();
                stopAll(stopInstance);
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    private void stopAll(boolean stopInstance) {
        // 選択されているサービスを保持する
        Long selectedComponentNo = null;
        if (sender.servicePanel.serviceTable.getValue() != null) {
            ComponentDto component = (ComponentDto) sender.servicePanel.serviceTable.getValue();
            selectedComponentNo = component.getComponent().getComponentNo();
        }

        // オペレーションログ
        OperationLogger.write("SERVICE", "All Service Stop", String.valueOf(stopInstance));

        // 全てのサービスを停止
        List<Long> componentNos = new ArrayList<Long>();
        for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
            componentNos.add(((ComponentDto) itemId).getComponent().getComponentNo());
        }

        ProcessService processService = BeanContext.getBean(ProcessService.class);
        processService.stopComponents(ViewContext.getFarmNo(), componentNos, stopInstance);

        // 表示を更新
        sender.refreshTable();

        // 選択されていたサービスを選択し直す
        if (selectedComponentNo != null) {
            for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                ComponentDto component = (ComponentDto) itemId;
                if (selectedComponentNo.equals(component.getComponent().getComponentNo())) {
                    sender.servicePanel.serviceTable.select(itemId);
                    break;
                }
            }
        }
    }

}
