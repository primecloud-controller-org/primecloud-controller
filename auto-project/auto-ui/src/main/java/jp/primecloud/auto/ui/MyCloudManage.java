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
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.Application;
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
    final String COLUMN_HEIGHT = "30px";

    Application apl;

    CloudTable cloudTable;

    List<FarmDto> farms;

    MyCloudManage(Application ap) {
        apl = ap;

        //モーダルウインドウ
        setIcon(Icons.EDITMINI.resource());

        setCaption(ViewProperties.getCaption("window.myCloudManage"));
        setModal(true);
        setWidth("550px");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true);

        // クラウド情報テーブルの上部
        HorizontalLayout tbar = new HorizontalLayout();
        tbar.setWidth("100%");
        tbar.setSpacing(true);

        // テーブルのキャプション
        Label tcaption = new Label(ViewProperties.getCaption("table.cloud"));
        tcaption.setWidth("300px");
        tbar.addComponent(tcaption);
        tbar.setComponentAlignment(tcaption, Alignment.MIDDLE_LEFT);

        // Editボタン
        Button editButton = new Button(ViewProperties.getCaption("button.editCloud"));
        editButton.setDescription(ViewProperties.getCaption("description.editCloud"));
        editButton.setIcon(Icons.EDITMINI.resource());
        editButton.setWidth("85px");
        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudManage.this.editButtonClick(event);
            }
        });
        tbar.addComponent(editButton);
        tbar.setComponentAlignment(editButton, Alignment.BOTTOM_RIGHT);

        // Deleteボタン
        Button deleteButton = new Button(ViewProperties.getCaption("button.delete"));
        deleteButton.setDescription(ViewProperties.getCaption("description.delete"));
        deleteButton.setWidth("85px");
        deleteButton.setIcon(Icons.DELETEMINI.resource());
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudManage.this.deleteButtonClick(event);
            }
        });
        tbar.addComponent(deleteButton);
        tbar.setComponentAlignment(deleteButton, Alignment.BOTTOM_RIGHT);
        tbar.setExpandRatio(tcaption, 10);
        layout.addComponent(tbar);

        // スペースを空ける
        Label spacer1 = new Label("");
        spacer1.setHeight("5px");
        layout.addComponent(spacer1);

        // クラウド情報テーブル
        cloudTable = new CloudTable();
        layout.addComponent(cloudTable);

        // スペースを空ける
        Label spacer2 = new Label("");
        spacer2.setHeight("7px");
        layout.addComponent(spacer2);

        // 下部のバー
        HorizontalLayout bbar = new HorizontalLayout();
        bbar.setWidth("100%");
        HorizontalLayout rlay = new HorizontalLayout();
        rlay.setSpacing(true);

        // Newボタン
        Button addButton = new Button(ViewProperties.getCaption("button.newCloud"));
        addButton.setDescription(ViewProperties.getCaption("description.newCloud"));
        addButton.setIcon(Icons.ADD.resource());
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudManage.this.addButtonClick(event);
            }
        });
        bbar.addComponent(addButton);
        bbar.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT);

        // Switchボタン
        Button switchButton = new Button(ViewProperties.getCaption("button.switch"));
        switchButton.setDescription(ViewProperties.getCaption("description.mycloud.switch"));
        switchButton.setWidth("85px");
        switchButton.setIcon(Icons.SELECTMINI.resource());
        switchButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudManage.this.switchButtonClick(event);
            }
        });
        // [Enter]でswitchButtonクリック
        switchButton.setClickShortcut(KeyCode.ENTER);
        switchButton.focus();
        rlay.addComponent(switchButton);

        // Cancelボタン
        Button cancelButton = new Button(ViewProperties.getCaption("button.cancel"));
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.setWidth("85px");
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MyCloudManage.this.close();
            }
        });
        rlay.addComponent(cancelButton);

        bbar.addComponent(rlay);
        bbar.setComponentAlignment(rlay, Alignment.MIDDLE_RIGHT);

        layout.addComponent(bbar);

        // 初期データの取得
        initData();

        //追加、編集、削除はマスターユーザ/パワーユーザに開放
        if (ViewContext.getPowerUser()) {
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            editButton.setEnabled(true);
            //ただしマスター権限を持たないパワーユーザは新規作成は不可能
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

        // クラウド情報を表示
        showClouds();
    }

    private class CloudTable extends Table {

        CloudTable() {
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
            setCellStyleGenerator(new Table.CellStyleGenerator() {
                @Override
                public String getStyle(Object itemId, Object propertyId) {
                    if (propertyId == null) {
                        return "";
                    } else {
                        return propertyId.toString().toLowerCase();
                    }
                }
            });
        }

    }

    private void initData() {
        // ユーザ番号
        Long userNo = ViewContext.getUserNo();
        Long lohinUserNo = ViewContext.getLoginUser();

        // クラウド情報を取得
        FarmService farmService = BeanContext.getBean(FarmService.class);
        farms = farmService.getFarms(userNo, lohinUserNo);
    }

    private void showClouds() {
        cloudTable.removeAllItems();

        // 取得したクラウド情報をテーブルに追加
        for (int i = 0; i < farms.size(); i++) {
            FarmDto farm = farms.get(i);

            // クラウド名

            Icons nameIcon = Icons.CLOUD;
            Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(apl, nameIcon) + "\"><div>"
                    + farm.getFarm().getFarmName() + "</div>", Label.CONTENT_XHTML);
            slbl.setHeight(COLUMN_HEIGHT);

            // コメント
            String comment = farm.getFarm().getComment();
            cloudTable.addItem(new Object[] { (i + 1), slbl, comment }, farm);

        }
        // すでにクラウドを開いていたら、その行を選択する
        Long curFarmNo = ViewContext.getFarmNo();
        if (curFarmNo != null) {
            for (int i = 0; i < farms.size(); i++) {
                FarmDto farm = farms.get(i);
                if (farm.getFarm().getFarmNo().longValue() == curFarmNo.longValue()) {
                    cloudTable.select(farm);
                    break;
                }
            }
        }
    }

    private void deleteButtonClick(ClickEvent event) {
        final FarmDto farm = (FarmDto) cloudTable.getValue();
        if (farm == null) {
            // クラウドが選択されていない場合
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

                //オペレーションログ
                AutoApplication aapl = (AutoApplication) apl;
                aapl.doOpLog("CLOUD", "Delete Cloud", farm.getFarm().getFarmNo(), null);

                // クラウドの削除
                FarmService farmService = BeanContext.getBean(FarmService.class);
                try {
                    farmService.deleteFarm(farm.getFarm().getFarmNo());
                } catch (AutoApplicationException e) {
                    String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
                    DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
                    getApplication().getMainWindow().addWindow(dialog);
                    return;
                }

                // 削除したクラウド情報をテーブルから除去
                cloudTable.removeItem(cloudTable.removeItem(farm));

                // 削除完了メッセージの表示
                String message = ViewMessages.getMessage("IUI-000007", farm.getFarm().getFarmName());
                DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.normal"), message);
                getApplication().getMainWindow().addWindow(dialog);

                // 表示中のクラウドを削除した場合、表示を解除
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
        FarmDto farm = (FarmDto) cloudTable.getValue();
        if (farm == null) {
            // クラウドが選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000008"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }

        // 権限検索ロジックを実行
        UserService userService = BeanContext.getBean(UserService.class);
        UserAuthDto userAuthDto = userService.getUserAuth(ViewContext.getLoginUser(), farm.getFarm().getFarmNo());

        // 選択したクラウドのfarmNoをセッションに格納
        ViewContext.setFarmNo(farm.getFarm().getFarmNo());
        ViewContext.setFarmName(farm.getFarm().getFarmName());
        ViewContext.setAuthority(userAuthDto);

        //パワーユーザは各MyCloudのマスターに成りすます
        if (ViewContext.getPowerUser()) {
            ViewContext.setUserNo(farm.getFarm().getUserNo());
        }

        // 画面を閉じる
        close();
    }

    private void addButtonClick(ClickEvent event) {
        MyCloudAdd window = new MyCloudAdd(getWindow().getApplication());
        window.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                Long farmNo = (Long) ContextUtils.getAttribute("newfarmNo");
                if (farmNo != null) {
                    String farmName = (String) ContextUtils.getAttribute("newfarmName");
                    ContextUtils.removeAttribute("newfarmNo");
                    ContextUtils.removeAttribute("newfarmName");

                    // 新規追加したクラウドを選択する
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
        final FarmDto farm = (FarmDto) cloudTable.getValue();
        if (farm == null) {
            // クラウドが選択されていない場合
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"),
                    ViewMessages.getMessage("IUI-000047"));
            getApplication().getMainWindow().addWindow(dialog);
            return;
        }
        MyCloudEdit myCloudEdit = new MyCloudEdit(getApplication(), farm.getFarm().getFarmNo());
        myCloudEdit.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                Long farmNo = (Long) ContextUtils.getAttribute("editFarmNo");
                if (farmNo != null) {
                    ContextUtils.removeAttribute("editFarmNo");

                    // 表示の更新
                    initData();
                    showClouds();
                }
            }
        });
        //MyCloud編集画面を開く
        getWindow().getApplication().getMainWindow().addWindow(myCloudEdit);
    }

}
