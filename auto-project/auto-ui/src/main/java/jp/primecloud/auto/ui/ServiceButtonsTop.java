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

    private Button playButton;

    private Button stopButton;

    ServiceButtonsTop(MainView sender) {
        this.sender = sender;

        //テーブル下ボタンの配置
        setWidth("100%");
        setMargin(false);
        addStyleName("service-buttons");
        addStyleName("service-table-label");
        Label lservice = new Label(ViewProperties.getCaption("label.service"), Label.CONTENT_XHTML);
        lservice.setWidth("200px");
        addComponent(lservice);

        stopButton = new Button(ViewProperties.getCaption("button.stopAllServices"));
        stopButton.setDescription(ViewProperties.getCaption("description.stopAllServices"));
        stopButton.setIcon(Icons.STOPMINI.resource());
        stopButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                stopButtonClick(event);
            }
        });
        stopButton.addStyleName("right");
        addComponent(stopButton);

        playButton = new Button(ViewProperties.getCaption("button.startAllServices"));
        playButton.setDescription(ViewProperties.getCaption("description.startAllServices"));
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
        if (!auth.isServiceOperate()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
        }
    }

    public void playButtonClick(ClickEvent event) {
        final ComponentDto dto = (ComponentDto) sender.servicePanel.serviceTable.getValue();

        List<ComponentDto> componentDtos = new ArrayList<ComponentDto>();
        for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
            componentDtos.add((ComponentDto) itemId);
        }

        if (!componentDtos.isEmpty()) {
            for (ComponentDto componentDto : componentDtos) {
                if (componentDto.getStatus().equals(ComponentStatus.STARTING.toString())
                        || componentDto.getStatus().equals(ComponentStatus.STOPPING.toString())
                        || componentDto.getStatus().equals(ComponentStatus.CONFIGURING.toString())) {
                    String message = ViewMessages.getMessage("IUI-000046",
                            new Object[] { StringUtils.capitalize(componentDto.getStatus().toLowerCase()) });
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }
            }
        }

        String message = ViewMessages.getMessage("IUI-000009");
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
                List<Long> componentNos = new ArrayList<Long>();
                for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                    componentNos.add(((ComponentDto) itemId).getComponent().getComponentNo());
                }

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("SERVICE", "All Service Start", null, null, null, null);

                processService.startComponents(farmNo, componentNos);
                sender.refreshTable();

                // 選択されていたサービスを選択し直す
                if (dto != null) {
                    for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                        ComponentDto dto2 = (ComponentDto) itemId;
                        if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                            sender.servicePanel.serviceTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

    public void stopButtonClick(ClickEvent event) {
        final ComponentDto dto = (ComponentDto) sender.servicePanel.serviceTable.getValue();

        HorizontalLayout optionLayout = new HorizontalLayout();
        final CheckBox checkBox = new CheckBox(ViewMessages.getMessage("IUI-000033"), false);
        checkBox.setImmediate(true);
        optionLayout.addComponent(checkBox);

        String message = ViewMessages.getMessage("IUI-000010");
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
                List<Long> componentNos = new ArrayList<Long>();
                for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                    componentNos.add(((ComponentDto) itemId).getComponent().getComponentNo());
                }
                boolean stopInstance = (Boolean) checkBox.getValue();

                //オペレーションログ
                AutoApplication apl = (AutoApplication) getApplication();
                apl.doOpLog("SERVICE", "All Service Stop", null, null, null, String.valueOf(stopInstance));

                processService.stopComponents(farmNo, componentNos, stopInstance);
                sender.refreshTable();

                // 選択されていたサービスを選択し直す
                if (dto != null) {
                    for (Object itemId : sender.servicePanel.serviceTable.getItemIds()) {
                        ComponentDto dto2 = (ComponentDto) itemId;
                        if (dto.getComponent().getComponentNo().equals(dto2.getComponent().getComponentNo())) {
                            sender.servicePanel.serviceTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        getApplication().getMainWindow().addWindow(dialog);
    }

}
