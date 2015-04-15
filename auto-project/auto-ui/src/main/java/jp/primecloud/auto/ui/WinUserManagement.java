package jp.primecloud.auto.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.UserAuth;
import jp.primecloud.auto.service.UserManagementService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.ManagementUserDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ConvertUtil;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewProperties;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * Userマネージメント画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinUserManagement extends Window {

    //Application apl;

    TopTitle topTitle = new TopTitle();

    UserButtonsTop userButtonsTop = new UserButtonsTop();

    //UserButtonsBottom userButtonsBottom  = new UserButtonsBottom();

    Table userTable;

    List<FarmDto> farmlist;

    List<ManagementUserDto> mUsers;

    WinUserManagement(){

        setCaption(ViewProperties.getCaption("window.winUserManagement"));
        setTheme("classy");

        loadData();

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setSizeFull();
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.addStyleName("win-user-view");

        layout.addComponent(topTitle);

        //上部ボタンフィールド
        layout.addComponent(userButtonsTop);
        //ユーザテーブル
        userTable = new Table();
        //userTable.setHeight("100%");
        userTable.setSizeFull();
        userTable.setSelectable(true);
        userTable.setMultiSelect(false);
        userTable.setImmediate(true);
        userTable.setColumnReorderingAllowed(true);
        userTable.setColumnCollapsingAllowed(true);
        userTable.addStyleName("win-user-auth-table");

        //コンテナをセット
        userTable.setContainerDataSource(getContainer());
        //ヘッダーをセット
        userTable.setColumnHeaders(getHeaders());
        setHeaderWidth();
        //データをセット
        Container c = userTable.getContainerDataSource();
        c.removeAllItems();
        fillContainer(c);
        userTable.sort();

        layout.addComponent(userTable);
        layout.setExpandRatio(userTable, 1);

        //下部ボタンフィールド
        //layout.addComponent(userButtonsBottom);

    }

    private void refresh() {
        loadData();
        //データをクリア
        userTable.getContainerDataSource().removeAllItems();
        //コンテナをセット
        userTable.setContainerDataSource(getContainer());
        //ヘッダーをセット
        userTable.setColumnHeaders(getHeaders());
        setHeaderWidth();
        //データをセット
        Container c = userTable.getContainerDataSource();
        fillContainer(c);
        userTable.sort();
    }


    private void loadData() {
        // ユーザ番号
        // ユーザ番号
        Long loginUserNo = ViewContext.getLoginUser();

        // クラウド情報を取得
        UserManagementService userManagementService = BeanContext.getBean(UserManagementService.class);
        farmlist = userManagementService.getFarms(loginUserNo);

        // 管理ユーザー情報を取得
        mUsers = userManagementService.getManagementUsers(loginUserNo);

    }

    public String[] getHeaders() {
        ArrayList<String> headerList = new ArrayList<String>();
        headerList.add(ViewProperties.getCaption("label.userName"));
        headerList.add(ViewProperties.getCaption("label.userEdit"));
        for (FarmDto farm :farmlist){
            headerList.add(farm.getFarm().getFarmName());
        }

        String[] headers=(String[])headerList.toArray(new String[0]);

        return headers;
    }

    public void setHeaderWidth() {
        userTable.setColumnWidth(ViewProperties.getCaption("label.userName"), 100);
        userTable.setColumnWidth(ViewProperties.getCaption("label.userEdit"), 50);
        for (FarmDto farm :farmlist){
            userTable.setColumnWidth(farm.getFarm().getFarmName(), 100);
        }
    }


    public IndexedContainer getContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(ViewProperties.getCaption("label.userName"), String.class, null);
        container.addContainerProperty(ViewProperties.getCaption("label.userEdit"), Button.class, null);
        for (FarmDto farm :farmlist){
            container.addContainerProperty(farm.getFarm().getFarmName(), String.class, null);
        }
        return container;
    }


    private void fillContainer(Container container) {

        for (ManagementUserDto userAuth: mUsers) {

            User user = userAuth.getUser();
            Map<Long, UserAuth> authMap = userAuth.getAuthMap();
            Item item = container.addItem(user.getUserNo());



            Button editButton = new Button();
            editButton.addStyleName("borderless");
            editButton.setIcon(Icons.EDITMINI.resource());
            editButton.setVisible(true);
            editButton.setData(user);
            editButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    editButtonClick(event);
                    //WinLogView window = new WinLogView();
                    //getApplication().getMainWindow().addWindow(window);
                }
            });
            //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
            editButton.setErrorHandler(new ComponentsErrorHandler(this));

            item.getItemProperty(ViewProperties.getCaption("label.userName")).setValue(user.getUsername());
            item.getItemProperty(ViewProperties.getCaption("label.userEdit")).setValue(editButton);

            ConvertUtil cUtil = new ConvertUtil();
            for (FarmDto farm :farmlist){
                String viewString = cUtil.ConvertAuthToName(authMap.get(farm.getFarm().getFarmNo()));
                item.getItemProperty(farm.getFarm().getFarmName()).setValue(viewString);
            }

        }
    }

    public void editButtonClick(Button.ClickEvent event) {
        User user = (User) event.getButton().getData();
        final Long selectUserNo = user.getUserNo();

        WinUserAuthAddEdit win = new WinUserAuthAddEdit(user.getMasterUser(), user.getUserNo());
        win.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent e) {
                refresh();

                // 選択されていたユーザを選択し直す
                if (mUsers != null) {
                    for (Object itemId : userTable.getItemIds()) {
                        Long userNo = (Long) itemId;
                        if (selectUserNo.equals(userNo)) {
                            userTable.select(itemId);
                            break;
                        }
                    }
                }
            }
        });
        this.addWindow(win);
    }


    private class TopTitle extends CssLayout {

        TopTitle() {
            addStyleName("UserTopTitle");
            setWidth("100%");
            setHeight("30px");
            setMargin(false);

            // タイトルラベル
            Label title = new Label(ViewProperties.getCaption("label.mainTitle"), Label.CONTENT_XHTML);
            title.addStyleName("title");
            addComponent(title);
        }
    }



    private class UserButtonsTop extends CssLayout implements Button.ClickListener {
        Button btnNewUser;

        UserButtonsTop() {
            //テーブル下ボタンの配置
            setWidth("100%");
            setMargin(false);
            addStyleName("user-buttons");
            addStyleName("user-table-label");
            Label luser = new Label(ViewProperties.getCaption("label.user"),Label.CONTENT_XHTML);
            luser.setWidth("200px");
            addComponent(luser);

            btnNewUser = new Button(ViewProperties.getCaption("button.newUser"));
            btnNewUser.setDescription(ViewProperties.getCaption("description.newUser"));
            btnNewUser.setIcon(Icons.ATTACH_MINI.resource());
            btnNewUser.addListener(this);
            btnNewUser.addStyleName("right");
            //※予期しないエラーのダイアログがPCCのメイン画面側に表示されることに対する処理
            btnNewUser.setErrorHandler(new ComponentsErrorHandler(WinUserManagement.this));
            addComponent(btnNewUser);
        }


        public void buttonClick(ClickEvent event) {
            WinUserAuthAddEdit win = new WinUserAuthAddEdit(ViewContext.getUserNo(), null);
            win.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    refresh();
                }
            });
            getWindow().addWindow(win);
        }

    }



//    private class UserButtonsBottom extends CssLayout implements Button.ClickListener {
//        Button okButton;
//
//        UserButtonsBottom() {
//            //テーブル下ボタンの配置
//            setWidth("100%");
//            setMargin(false);
//            addStyleName("user-buttons");
//            addStyleName("user-table-label");
//
//            // OKボタン
//            okButton = new Button(ViewProperties.getCaption("button.ok"));
//            okButton.setDescription(ViewProperties.getCaption("description.editUser.ok"));
//            okButton.setWidth("100px");
//            okButton.addListener(this);
//            okButton.addStyleName("right");
//            addComponent(okButton);
//
//       }
//
//
//        public void buttonClick(ClickEvent event) {
//            // 画面を閉じる
//            WinUserManagement.this.close();
//        }
//
//    }
}
