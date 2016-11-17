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

import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.UserService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.UserAuthDto;
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

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * myCloud管理画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class MyCloudManage extends Window {

    private final String COLUMN_HEIGHT = "30px";

    private CloudTable cloudTable;

    private List<FarmDto> farms;

    @Override
    public void attach() {
        //モーダルウインドウ
        setIcon(Icons.EDITMINI.resource());
        setCaption(ViewProperties.getCaption("window.myCloudManage"));
        setModal(true);
        setWidth("550px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true);

        // myCloud情報テーブルの上部
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.setSpacing(true);

        // テーブルのキャプション
        Label topCaption = new Label(ViewProperties.getCaption("table.cloud"));
        topCaption.setWidth("300px");
        topLayout.addComponent(topCaption);
        topLayout.setComponentAlignment(topCaption, Alignment.MIDDLE_LEFT);

        // Editボタン
        Button editButton = new Button(ViewProperties.getCaption("button.editCloud"));
        editButton.setDescription(ViewProperties.getCaption("description.editCloud"));
        editButton.setIcon(Icons.EDITMINI.resource());
        editButton.setWidth("85px");
        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                editButtonClick(event);
            }
        });
        topLayout.addComponent(editButton);
        topLayout.setComponentAlignment(editButton, Alignment.BOTTOM_RIGHT);

        // Deleteボタン
        Button deleteButton = new Button(ViewProperties.getCaption("button.delete"));
        deleteButton.setDescription(ViewProperties.getCaption("description.delete"));
        deleteButton.setWidth("85px");
        deleteButton.setIcon(Icons.DELETEMINI.resource());
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                deleteButtonClick(event);
            }
        });
        topLayout.addComponent(deleteButton);
        topLayout.setComponentAlignment(deleteButton, Alignment.BOTTOM_RIGHT);
        topLayout.setExpandRatio(topCaption, 10);
        layout.addComponent(topLayout);

        // スペースを空ける
        Label spacer1 = new Label("");
        spacer1.setHeight("5px");
        layout.addComponent(spacer1);

        // myCloud情報テーブル
        cloudTable = new CloudTable();
        layout.addComponent(cloudTable);

        // スペースを空ける
        Label spacer2 = new Label("");
        spacer2.setHeight("7px");
        layout.addComponent(spacer2);

        // 下部のバー
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setWidth("100%");
        HorizontalLayout bottomRightLayout = new HorizontalLayout();
        bottomRightLayout.setSpacing(true);

        // Newボタン
        Button addButton = new Button(ViewProperties.getCaption("button.newCloud"));
        addButton.setDescription(ViewProperties.getCaption("description.newCloud"));
        addButton.setIcon(Icons.ADD.resource());
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClick(event);
            }
        });
        bottomLayout.addComponent(addButton);
        bottomLayout.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT);

        // Selectボタン
        Button switchButton = new Button(ViewProperties.getCaption("button.switch"));
        switchButton.setDescription(ViewProperties.getCaption("description.mycloud.switch"));
        switchButton.setWidth("85px");
        switchButton.setIcon(Icons.SELECTMINI.resource());
        switchButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                switchButtonClick(event);
            }
        });
        switchButton.setClickShortcut(KeyCode.ENTER); // [Enter]でswitchButtonクリック
        switchButton.focus();
        bottomRightLayout.addComponent(switchButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.setWidth("85px");
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        bottomRightLayout.addComponent(cancelButton);

        bottomLayout.addComponent(bottomRightLayout);
        bottomLayout.setComponentAlignment(bottomRightLayout, Alignment.MIDDLE_RIGHT);
        layout.addComponent(bottomLayout);

        // 追加、編集、削除はマスターユーザ/パワーユーザに開放
        if (ViewContext.getPowerUser()) {
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            editButton.setEnabled(true);
            // ただしマスター権限を持たないパワーユーザは新規作成は不可能
            if (ViewContext.getPowerUser() && !ViewContext.getPowerDefaultMaster().equals(ViewContext.getLoginUser())) {
                addButton.setEnabled(false);
            }
        } else if (ViewContext.getUserNo().equals(ViewContext.getLoginUser())) {
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            editButton.setEnabled(true);
        } else {
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
        }

        // myCloud情報を表示
        showClouds();
    }

    private class CloudTable extends Table {

        @Override
        public void attach() {
            //テーブル基本設定
            setWidth("100%");

            setPageLength(4);
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            setSortDisabled(true);
            setColumnReorderingAllowed(false);
            setColumnCollapsingAllowed(false);
            setSelectable(true);
            setMultiSelect(false);
            setNullSelectionAllowed(false);
            setImmediate(true);
            addStyleName("win-mycloud-edit-table");

            //カラム設定
            addContainerProperty("No", Integer.class, null);
            addContainerProperty("Name", Label.class, new Label());
            addContainerProperty("Description", String.class, null);
            setColumnExpandRatio("Name", 40);
            setColumnExpandRatio("Description", 60);

            //テーブルのカラムに対してStyleNameを設定
            setCellStyleGenerator(new StandardCellStyleGenerator());
        }

        public void show(List<FarmDto> farms) {
            removeAllItems();
            for (int i = 0; i < farms.size(); i++) {
                FarmDto farm = farms.get(i);

                // myCloud名
                Icons nameIcon = Icons.CLOUD;
                Label slbl = new Label(IconUtils.createImageTag(getApplication(), nameIcon, farm.getFarm()
                        .getFarmName()), Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);

                // コメント
                String comment = farm.getFarm().getComment();

                addItem(new Object[] { (i + 1), slbl, comment }, farm);
            }
        }

        @Override
        public FarmDto getValue() {
            return (FarmDto) super.getValue();
        }

        public void select(Long farmNo) {
            if (farmNo == null) {
                return;
            }

            for (Object itemId : getItemIds()) {
                FarmDto farm = (FarmDto) itemId;
                if (farm.getFarm().getFarmNo().longValue() == farmNo.longValue()) {
                    select(itemId);
                    break;
                }
            }
        }

    }

    private void showClouds() {
        // myCloud情報を取得
        FarmService farmService = BeanContext.getBean(FarmService.class);
        farms = farmService.getFarms(ViewContext.getUserNo(), ViewContext.getLoginUser());

        // 取得したmyCloud情報をテーブルに追加
        cloudTable.show(farms);

        // すでにmyCloudを開いていたら、その行を選択する
        cloudTable.select(ViewContext.getFarmNo());
    }

    private void deleteButtonClick(ClickEvent event) {
        final FarmDto farm = cloudTable.getValue();
        if (farm == null) {
            // myCloudが選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000005"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        String message = ViewMessages.getMessage("IUI-000006", farm.getFarm().getFarmName());
        DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.confirm"), message, Buttons.OKCancel);
        dialog.setCallback(new Callback() {
            @Override
            public void onDialogResult(Result result) {
                if (result != Result.OK) {
                    return;
                }

                // オペレーションログ
                AutoApplication aapl = (AutoApplication) getApplication();
                aapl.doOpLog("CLOUD", "Delete Cloud", farm.getFarm().getFarmNo(), null);

                // myCloudの削除
                FarmService farmService = BeanContext.getBean(FarmService.class);
                try {
                    farmService.deleteFarm(farm.getFarm().getFarmNo());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }

                // 削除したmyCloud情報をテーブルから除去
                cloudTable.removeItem(cloudTable.removeItem(farm));

                // 削除完了メッセージの表示
                String message = ViewMessages.getMessage("IUI-000007", farm.getFarm().getFarmName());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.normal"), message);
                getApplication().getMainWindow().addWindow(dialog);

                // 表示中のmyCloudを削除した場合、表示を解除
                if (farm.getFarm().getFarmNo().equals(ViewContext.getFarmNo())) {
                    ViewContext.setFarmNo(null);
                    ViewContext.setFarmName(null);
                    ViewContext.setAuthority(new UserAuthDto(false));
                }
            }
        });

        getApplication().getMainWindow().addWindow(dialog);
    }

    private void switchButtonClick(ClickEvent event) {
        FarmDto farm = cloudTable.getValue();
        if (farm == null) {
            // myCloudが選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000008"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 権限検索ロジックを実行
        UserService userService = BeanContext.getBean(UserService.class);
        UserAuthDto userAuthDto = userService.getUserAuth(ViewContext.getLoginUser(), farm.getFarm().getFarmNo());

        // 選択したmyCloudをセッションに格納
        ViewContext.setFarmNo(farm.getFarm().getFarmNo());
        ViewContext.setFarmName(farm.getFarm().getFarmName());
        ViewContext.setAuthority(userAuthDto);

        // パワーユーザは各MyCloudのマスターに成りすます
        if (ViewContext.getPowerUser()) {
            ViewContext.setUserNo(farm.getFarm().getUserNo());
        }

        // 画面を閉じる
        close();
    }

    private void addButtonClick(ClickEvent event) {
        //myCloud作成画面
        MyCloudAdd window = new MyCloudAdd();
        window.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                Long farmNo = (Long) ContextUtils.getAttribute("newfarmNo");
                if (farmNo != null) {
                    String farmName = (String) ContextUtils.getAttribute("newfarmName");
                    ContextUtils.removeAttribute("newfarmNo");
                    ContextUtils.removeAttribute("newfarmName");

                    // 新規追加したmyCloudを選択する
                    ViewContext.setFarmNo(farmNo);
                    ViewContext.setFarmName(farmName);
                    ViewContext.setAuthority(new UserAuthDto(true));

                    // 画面を閉じる
                    close();
                }
            }
        });

        getWindow().getApplication().getMainWindow().addWindow(window);
    }

    private void editButtonClick(ClickEvent event) {
        FarmDto farm = cloudTable.getValue();
        if (farm == null) {
            // myCloudが選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000047"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // myCloud編集画面
        MyCloudEdit myCloudEdit = new MyCloudEdit(farm.getFarm().getFarmNo());
        myCloudEdit.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                Long farmNo = (Long) ContextUtils.getAttribute("editFarmNo");
                if (farmNo != null) {
                    ContextUtils.removeAttribute("editFarmNo");

                    // 表示の更新
                    showClouds();
                }
            }
        });

        getWindow().getApplication().getMainWindow().addWindow(myCloudEdit);
    }

}
