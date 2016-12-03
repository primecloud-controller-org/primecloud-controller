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

    private Button playButton;

    private Button stopButton;

    ServerButtonsTop(MainView sender) {
        this.sender = sender;

        //テーブル下ボタンの配置
        setWidth("100%");
        setMargin(true);
        addStyleName("server-buttons");
        addStyleName("server-table-label");
        Label lserver = new Label(ViewProperties.getCaption("label.server"), Label.CONTENT_XHTML);
        lserver.setWidth("200px");
        addComponent(lserver);

        stopButton = new Button(ViewProperties.getCaption("button.stopAllServers"));
        stopButton.setDescription(ViewProperties.getCaption("description.stopAllServers"));
        stopButton.setIcon(Icons.STOPMINI.resource());
        stopButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                stopButtonClick(event);
            }
        });
        stopButton.addStyleName("right");
        addComponent(stopButton);

        playButton = new Button(ViewProperties.getCaption("button.startAllServers"));
        playButton.setDescription(ViewProperties.getCaption("description.startAllServers"));
        playButton.setIcon(Icons.PLAYMINI.resource());
        playButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                playButtonClick(event);
            }
        });
        playButton.addStyleName("right");
        addComponent(playButton);

        hide();
    }

    void hide() {
        stopButton.setEnabled(true);
        playButton.setEnabled(true);
        //オペレート権限がなければ非活性
        UserAuthDto auth = ViewContext.getAuthority();
        if (!auth.isServerOperate()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
        }
    }

    public void playButtonClick(ClickEvent event) {
        final InstanceDto dto = (InstanceDto) sender.serverPanel.serverTable.getValue();

        VerticalLayout optionLayout = new VerticalLayout();
        final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000035"), false);
        checkBox.setImmediate(true);
        optionLayout.addComponent(checkBox);
        optionLayout.setComponentAlignment(checkBox, Alignment.MIDDLE_CENTER);

        // サービスを有効にするかどうか
        String enableService = Config.getProperty("ui.enableService");
        if (enableService != null && !BooleanUtils.toBoolean(enableService)) {
            optionLayout = null;
        }

        String message = ViewMessages.getMessage("IUI-000011");
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancelConfirm, optionLayout);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                ProcessService processService = BeanContext.getBean(ProcessService.class);
                Long farmNo = ViewContext.getFarmNo();
                List<Long> instanceNos = new ArrayList<Long>();
                for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                    instanceNos.add(((InstanceDto) itemId).getInstance().getInstanceNo());
                }
                boolean startService = (Boolean) checkBox.getValue();

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("SERVER", "All Server Start", null, null, null, String.valueOf(startService));

                processService.startInstances(farmNo, instanceNos, startService);
                sender.refreshTable();

                // 選択されていたサーバを選択し直す
                if (dto != null) {
                    for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                        InstanceDto dto2 = (InstanceDto) itemId;
                        if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                            sender.serverPanel.serverTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void stopButtonClick(ClickEvent event) {
        final InstanceDto dto = (InstanceDto) sender.serverPanel.serverTable.getValue();

        String message = ViewMessages.getMessage("IUI-000012");
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message,
                Buttons.OKCancelConfirm);
        dialog.setCallback(new DialogConfirm.Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }
                ProcessService processService = BeanContext.getBean(ProcessService.class);
                Long farmNo = ViewContext.getFarmNo();
                List<Long> instanceNos = new ArrayList<Long>();
                for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                    instanceNos.add(((InstanceDto) itemId).getInstance().getInstanceNo());
                }

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("SERVER", "All Server Stop", null, null, null, null);

                processService.stopInstances(farmNo, instanceNos);
                sender.refreshTable();

                // 選択されていたサーバを選択し直す
                if (dto != null) {
                    for (Object itemId : sender.serverPanel.serverTable.getItemIds()) {
                        InstanceDto dto2 = (InstanceDto) itemId;
                        if (dto.getInstance().getInstanceNo().equals(dto2.getInstance().getInstanceNo())) {
                            sender.serverPanel.serverTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

}
