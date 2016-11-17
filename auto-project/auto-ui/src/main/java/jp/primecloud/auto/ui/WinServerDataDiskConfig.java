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

import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.DataDiskDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.Application;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * データディスク追加/編集画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinServerDataDiskConfig extends Window {

    Long instanceNo;

    DataDiskDto dataDiskDto;

    Application ap;

    TextField txtDiskSize;

    Boolean isAddMode;

    WinServerDataDiskConfig(Application ap, Long instanceNo, DataDiskDto dataDiskDto) {
        this.ap = ap;
        this.instanceNo = instanceNo;
        this.dataDiskDto = dataDiskDto;

        // Window
        isAddMode = (this.dataDiskDto == null) ? true : false;
        if (isAddMode) {
            //ディスク追加画面
            setIcon(Icons.ADD.resource());
            setCaption(ViewProperties.getCaption("window.WinServerAddDataDisk"));
        } else {
            //ディスク編集画面
            setIcon(Icons.EDIT.resource());
            setCaption(ViewProperties.getCaption("window.WinServerEditDataDisk"));
        }
        setModal(true);
        setWidth("400px");
        setResizable(false);

        // Layout
        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        // Form
        Form form = new Form();
        txtDiskSize = new TextField(ViewProperties.getCaption("field.diskSize"));
        txtDiskSize.setWidth("90%");

        form.getLayout().addComponent(txtDiskSize);
        layout.addComponent(form);

        // 下部のバー
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setSpacing(true);
        buttonBar.setMargin(false, true, true, false);
        layout.addComponent(buttonBar);
        layout.setComponentAlignment(buttonBar, Alignment.BOTTOM_RIGHT);

        // OK Button
        Button btnOk = new Button(ViewProperties.getCaption("button.ok"));
        btnOk.setDescription(ViewProperties.getCaption("description.ok"));
        btnOk.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                okButtonClick(event);
            }
        });
        buttonBar.addComponent(btnOk);
        // [Enter]でOKボタンクリック
        btnOk.setClickShortcut(KeyCode.ENTER);

        //Cancel Button
        Button btnCancel = new Button(ViewProperties.getCaption("button.cancel"));
        btnCancel.setDescription(ViewProperties.getCaption("description.cancel"));
        btnCancel.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        buttonBar.addComponent(btnCancel);

        //データ表示
        showData();

        // 入力チェックの設定
        initValidation();
    }

    private void showData() {
        if (!isAddMode) {
            txtDiskSize.setValue(String.valueOf(dataDiskDto.getDiskSize()));
        }
    }

    private void initValidation() {
        // ディスクサイズ
        String message = ViewMessages.getMessage("IUI-000032");
        txtDiskSize.setRequired(true);
        txtDiskSize.setRequiredError(message);
        txtDiskSize.addValidator(new RegexpValidator("^[1-9]|[1-9][0-9]{1,2}|1000$", true, message));
    }

    private void okButtonClick(ClickEvent event) {
        // 入力値を取得
        String diskSize = (String) txtDiskSize.getValue();

        // 入力チェック
        try {
            //基本バリデーション
            txtDiskSize.validate();

            //個別バリデーション
            if (!isAddMode) {
                //編集の場合
                if (Integer.valueOf(diskSize) < dataDiskDto.getDiskSize()) {
                    //変更後のディスクサイズが変更前より小さい場合
                    throw new InvalidValueException(ViewMessages.getMessage("IUI-000125"));
                }
            }
        } catch (InvalidValueException e) {
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), e.getMessage());
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        // 更新処理
        InstanceService instanceService = BeanContext.getBean(InstanceService.class);

        // 入力値を格納
        Long diskNo;
        if (isAddMode) {
            //テーブル更新処理
            dataDiskDto = new DataDiskDto();
            dataDiskDto.setDiskSize(Integer.valueOf(diskSize));
            diskNo = instanceService.createDataDisk(instanceNo, dataDiskDto);
        } else {
            //テーブル更新処理
            dataDiskDto.setDiskSize(Integer.valueOf(diskSize));
            instanceService.updateDataDisk(instanceNo, dataDiskDto);
            diskNo = dataDiskDto.getDiskNo();
        }

        //IaasGateWay処理(ディスクアタッチ)
        instanceService.attachDataDisk(instanceNo, diskNo);

        // ログイン画面を閉じる
        close();
    }

}
