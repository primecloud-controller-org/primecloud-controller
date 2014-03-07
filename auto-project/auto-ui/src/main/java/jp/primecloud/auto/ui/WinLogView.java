package jp.primecloud.auto.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.primecloud.auto.log.EventLogLevel;
import jp.primecloud.auto.log.dao.crud.EventLogDao;
import jp.primecloud.auto.log.entity.crud.EventLog;
import jp.primecloud.auto.log.service.EventLogService;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ViewContext;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.vaadin.henrik.refresher.Refresher;
import org.vaadin.henrik.refresher.Refresher.RefreshListener;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * EventLog画面を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class WinLogView extends Window {
    final String COMBOBOX_WIDTH = "155px";

    Label userName;

    OptionGroup optDateSelect;

    ComboBox cmbDateNow;

    PopupDateField fromDate;

    PopupDateField toDate;

    ComboBox cmbMyCloud;

    ComboBox cmbLoglevel;

    ComboBox cmbService;

    ComboBox cmbServer;

    Button btnCloudRefresh;

    Button btnSearch;

    CheckBox chkAuto;

    Table logTable;

    Refresher timer;

    Label lastUpdate;

    final static String dateFormat = "yyyy/MM/dd HH:mm:ss";

    //検索結果のLimitを設定
    Integer limit = 1000;

    WinLogView() {

        setCaption(ViewProperties.getCaption("window.winLogView"));

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.addStyleName("win-log-view");
        //ユーザ名の表示
        String strlbl = ViewProperties.getCaption("field.userName") + ":" + ViewContext.getUsername();
        userName = new Label(strlbl);
        layout.addComponent(userName);
        HorizontalLayout laySelect = new HorizontalLayout();
        laySelect.setWidth("100%");
        laySelect.setSpacing(true);

        //リセット
        btnCloudRefresh = new Button();
        // btnCloudRefresh.setStyleName(BaseTheme.BUTTON_LINK);
        btnCloudRefresh.setCaption(ViewProperties.getCaption("button.reset"));
        btnCloudRefresh.setDescription(ViewProperties.getCaption("description.reset"));
        // btnCloudRefresh.addStyleName("mycloudrefresh");
        btnCloudRefresh.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Container c = cmbMyCloud.getContainerDataSource();
                c.removeAllItems();
                setMyCloudList(c);

                Long farmNo = ViewContext.getFarmNo();
                if (farmNo != null) {
                    cmbMyCloud.select((Long) farmNo);
                }
                cmbServer.select(null);
                cmbService.select(null);
                //現在時刻をリセット
                Container dateList = cmbDateNow.getContainerDataSource();
                cmbDateNow.select(((IndexedContainer) dateList).getIdByIndex(0));
                optDateSelect.select(ViewProperties.getCaption("item.now"));
                cmbLoglevel.select(cmbLoglevel.getNullSelectionItemId());
                fromDate.setValue(null);
                toDate.setValue(null);
                chkAuto.setValue(false);

            }
        });
        layout.addComponent(btnCloudRefresh);

        // 日時選択
        GridLayout layGridDate = new GridLayout(3, 2);
        layGridDate.setCaption(ViewProperties.getCaption("field.dateselect"));
        layGridDate.setSpacing(false);

        optDateSelect = new OptionGroup();
        optDateSelect.addItem(ViewProperties.getCaption("item.now"));
        optDateSelect.addItem(ViewProperties.getCaption("item.date"));
        optDateSelect.setNullSelectionAllowed(false);
        optDateSelect.select(ViewProperties.getCaption("item.now"));
        optDateSelect.setImmediate(true);

        optDateSelect.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String val = (String) event.getProperty().getValue();
                if (val.equals(ViewProperties.getCaption("item.now"))) {
                    cmbDateNow.setEnabled(true);
                    fromDate.setEnabled(false);
                    toDate.setEnabled(false);
                } else if (val.equals(ViewProperties.getCaption("item.date"))) {
                    cmbDateNow.setEnabled(false);
                    fromDate.setEnabled(true);
                    toDate.setEnabled(true);
                }
            }
        });
        layout.addComponent(optDateSelect);

        //現在からの時刻指定
        cmbDateNow = new ComboBox();
        cmbDateNow.setWidth(COMBOBOX_WIDTH);
        //LogLevel選択
        IndexedContainer dateList = getTimeList();
        cmbDateNow.setContainerDataSource(dateList);
        cmbDateNow.setItemCaptionPropertyId("caption");
        cmbDateNow.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        cmbDateNow.setNullSelectionAllowed(false);
        cmbDateNow.select(dateList.getIdByIndex(0));

        //時刻指定
        fromDate = new PopupDateField(ViewProperties.getCaption("field.fromdate"));
        fromDate.setWidth(COMBOBOX_WIDTH);
        fromDate.setResolution(PopupDateField.RESOLUTION_SEC);
        fromDate.setDateFormat(dateFormat);
        fromDate.addStyleName("date-from-to");
        toDate = new PopupDateField(ViewProperties.getCaption("field.todate"));
        toDate.setWidth(COMBOBOX_WIDTH);
        toDate.setResolution(PopupDateField.RESOLUTION_SEC);
        toDate.setDateFormat(dateFormat);
        toDate.addStyleName("date-from-to");

        layGridDate.addComponent(optDateSelect, 0, 0, 0, 1);
        layGridDate.addComponent(cmbDateNow, 1, 0);
        layGridDate.addComponent(fromDate, 1, 1);
        layGridDate.addComponent(toDate, 2, 1);
        layGridDate.setComponentAlignment(cmbDateNow, Alignment.BOTTOM_LEFT);
        layGridDate.setRowExpandRatio(0, 1);

        layout.addComponent(layGridDate);
        laySelect.addComponent(layGridDate);

        GridLayout layGridSel = new GridLayout(2, 2);
        layGridSel.setSpacing(false);

        if (optDateSelect.isSelected(ViewProperties.getCaption("item.now"))) {
            cmbDateNow.setEnabled(true);
            fromDate.setEnabled(false);
            toDate.setEnabled(false);
        } else {
            cmbDateNow.setEnabled(false);
            fromDate.setEnabled(true);
            toDate.setEnabled(true);
        }

        // 検索条件
        HorizontalLayout layButton = new HorizontalLayout();
        layButton.setSpacing(true);

        //myCloud選択
        cmbMyCloud = new ComboBox();
        cmbMyCloud.setWidth(COMBOBOX_WIDTH);
        cmbMyCloud.setCaption(ViewProperties.getCaption("field.couldname"));
        cmbMyCloud.setItemCaptionPropertyId("FarmName");
        cmbMyCloud.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        IndexedContainer myCloudContainer = new IndexedContainer();
        myCloudContainer.addContainerProperty("FarmName", String.class, null);
        setMyCloudList(myCloudContainer);
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            cmbMyCloud.select((Long) farmNo);
        }
        cmbMyCloud.setImmediate(true);
        cmbMyCloud.addStyleName("cmbmycloud");
        cmbMyCloud.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Container c = cmbMyCloud.getContainerDataSource();
                c.removeAllItems();
                setMyCloudList(c);

            }
        });

        layButton.addComponent(cmbMyCloud);

        //LogLevel選択
        cmbLoglevel = new ComboBox();
        cmbLoglevel.setWidth(COMBOBOX_WIDTH);
        cmbLoglevel.setCaption(ViewProperties.getCaption("field.loglevel"));
        cmbLoglevel.setItemCaptionPropertyId("logLevel");
        cmbLoglevel.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        cmbLoglevel.setContainerDataSource(getLogLevelList());
        layButton.addComponent(cmbLoglevel);

        //サービス選択
        cmbService = new ComboBox();
        cmbService.setWidth(COMBOBOX_WIDTH);
        cmbService.setItemCaptionPropertyId("serviceName");
        cmbService.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        cmbService.setCaption(ViewProperties.getCaption("field.servicename"));
        IndexedContainer serviceContainer = new IndexedContainer();
        serviceContainer.addContainerProperty("serviceName", String.class, null);
        cmbService.setContainerDataSource(getServiceList(serviceContainer));
        layButton.addComponent(cmbService);

        //サーバ選択
        cmbServer = new ComboBox();
        cmbServer.setWidth(COMBOBOX_WIDTH);
        cmbServer.setItemCaptionPropertyId("serverName");
        cmbServer.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        cmbServer.setCaption(ViewProperties.getCaption("field.servername"));
        IndexedContainer serverContainer = new IndexedContainer();
        serverContainer.addContainerProperty("serverName", String.class, null);
        cmbServer.setContainerDataSource(getServerList(serverContainer));
        layButton.addComponent(cmbServer);

        //検索ボタン
        btnSearch = new Button(ViewProperties.getCaption("button.search"));
        btnSearch.setDescription(ViewProperties.getCaption("description.search"));
        btnSearch.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                search();
            }
        });
        layButton.addComponent(btnSearch);
        layout.addComponent(layButton);

        //自動更新ボタン
        chkAuto = new CheckBox(ViewProperties.getCaption("button.autoRefresh"));
        chkAuto.setDescription(ViewProperties.getCaption("description.autoRefresh"));
        chkAuto.setImmediate(true);
        chkAuto.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                btnSearch.setEnabled(!(Boolean) chkAuto.getValue());
            }
        });

        layGridSel.addComponent(cmbMyCloud, 0, 0);
        layGridSel.addComponent(cmbLoglevel, 1, 0);
        layGridSel.addComponent(cmbService, 0, 1);
        layGridSel.addComponent(cmbServer, 1, 1);

        layout.addComponent(layGridSel);
        laySelect.addComponent(layGridSel);

        GridLayout layGridButtons = new GridLayout(2, 2);
        layGridButtons.setSpacing(true);

        layGridButtons.addComponent(btnCloudRefresh, 0, 1);
        layGridButtons.addComponent(chkAuto, 1, 0);
        layGridButtons.addComponent(btnSearch, 1, 1);
        layGridButtons.setComponentAlignment(btnCloudRefresh, Alignment.BOTTOM_LEFT);
        layGridButtons.setComponentAlignment(btnSearch, Alignment.BOTTOM_LEFT);
        layGridButtons.setComponentAlignment(chkAuto, Alignment.BOTTOM_LEFT);

        layout.addComponent(layGridButtons);
        laySelect.addComponent(layGridButtons);
        laySelect.setComponentAlignment(layGridButtons, Alignment.BOTTOM_LEFT);
        layout.addComponent(laySelect);

        //最終更新時刻表示
        Label lastUpdateLabel = new Label(ViewProperties.getCaption("field.lastupdate") + ":");
        lastUpdate = new Label();
        HorizontalLayout labelLayout = new HorizontalLayout();
        laySelect.setWidth("100%");
        laySelect.setSpacing(true);
        labelLayout.addComponent(lastUpdateLabel);
        labelLayout.addComponent(lastUpdate);
        layout.addComponent(labelLayout);
        layout.setComponentAlignment(labelLayout, Alignment.TOP_RIGHT);

        //ログテーブル
        logTable = new Table();
        logTable.setSizeFull();
        logTable.setColumnReorderingAllowed(true);
        logTable.setColumnCollapsingAllowed(true);

        //カラムをセットするために初期情報のみ必要
        //
        logTable.setContainerDataSource(getInitialContainer());

        logTable.setColumnHeaders(new String[] { ViewProperties.getCaption("field.datetime"),
                ViewProperties.getCaption("field.loglevel"), ViewProperties.getCaption("field.couldname"),
                ViewProperties.getCaption("field.servicename"), ViewProperties.getCaption("field.servername"),
                ViewProperties.getCaption("field.logmessage") });

        layout.addComponent(logTable);
        layout.setExpandRatio(logTable, 1);

        //自動更新
        timer = new Refresher();
        timer.setRefreshInterval(15 * 1000); //更新間隔(msec)
        timer.addListener(new RefreshListener() {

            @Override
            public void refresh(Refresher source) {
                if (BooleanUtils.isTrue((Boolean) chkAuto.getValue())) {
                    search();
                }
            }
        });
        layout.addComponent(timer);

    }

    private IndexedContainer getTimeList() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("caption", String.class, null);
        Item item;
        item = container.addItem(new Integer[] { 0, 0, 10 });
        item.getItemProperty("caption").setValue(ViewProperties.getCaption("field.10min"));
        item = container.addItem(new Integer[] { 0, 0, 30 });
        item.getItemProperty("caption").setValue(ViewProperties.getCaption("field.30min"));
        item = container.addItem(new Integer[] { 0, 1, 0 });
        item.getItemProperty("caption").setValue(ViewProperties.getCaption("field.1hour"));
        item = container.addItem(new Integer[] { 1, 0, 0 });
        item.getItemProperty("caption").setValue(ViewProperties.getCaption("field.1day"));
        return container;
    }

    private Date getTime(Calendar calendar, int date, int hour, int minute) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(calendar.getTime());
        calendar2.set(Calendar.DATE, calendar.get(Calendar.DATE) - date);
        calendar2.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
        calendar2.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minute);
        return calendar2.getTime();
    }

    private Container getServiceList(Container container) {

        ComponentService componentService = BeanContext.getBean(ComponentService.class);

        if (cmbMyCloud.getValue() != null) {
            Long farmNo = (Long) cmbMyCloud.getValue();
            List<ComponentDto> componentDtos;
            componentDtos = componentService.getComponents(farmNo);

            for (ComponentDto componentDto : componentDtos) {
                Item item;
                item = container.addItem(componentDto.getComponent().getComponentNo());
                item.getItemProperty("serviceName").setValue(componentDto.getComponent().getComponentName());
            }
        }
        return container;
    }

    private Container getServerList(Container container) {

        InstanceService instanceService = BeanContext.getBean(InstanceService.class);

        if (cmbMyCloud.getValue() != null) {
            Long farmNo = (Long) cmbMyCloud.getValue();
            List<InstanceDto> instanceDtos;
            instanceDtos = instanceService.getInstances(farmNo);

            for (InstanceDto instanceDto : instanceDtos) {
                Item item;
                item = container.addItem(instanceDto.getInstance().getInstanceNo());
                item.getItemProperty("serverName").setValue(instanceDto.getInstance().getInstanceName());
            }
        }

        return container;
    }

    private IndexedContainer getLogLevelList() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("logLevel", String.class, null);

        for (EventLogLevel eventLogLevel : EventLogLevel.values()) {
            if (eventLogLevel == EventLogLevel.ALL || eventLogLevel == EventLogLevel.OFF) {
                continue;
            }
            Item item;
            item = container.addItem(eventLogLevel.getCode());
            item.getItemProperty("logLevel").setValue(eventLogLevel.name());
        }
        return container;
    }

    private String transformLogLevel(Integer code) {
        EventLogLevel eventLogLevel = EventLogLevel.fromCode(code);
        return eventLogLevel.name();

    }

    private void setMyCloudList(Container container) {

        List<FarmDto> farms;

        // ユーザ番号
        Long userNo = ViewContext.getUserNo();
        Long lohinUserNo = ViewContext.getLoginUser();

        if (userNo != null) {
            // クラウド情報を取得
            FarmService farmService = BeanContext.getBean(FarmService.class);
            farms = farmService.getFarms(userNo, lohinUserNo);

            List<Long> farmNos = new ArrayList<Long>();
            for (int i = 0; i < farms.size(); i++) {
                FarmDto farm = farms.get(i);
                Item item = container.addItem(farm.getFarm().getFarmNo());
                item.getItemProperty("FarmName").setValue(farm.getFarm().getFarmName());
                farmNos.add(farm.getFarm().getFarmNo());
            }
            if (!farmNos.contains(cmbMyCloud.getValue())) {
                cmbMyCloud.setValue(null);
            }
            cmbMyCloud.setContainerDataSource(container);

            if (cmbService != null && cmbServer != null) {
                Container serviceContainer = cmbService.getContainerDataSource();
                serviceContainer.removeAllItems();
                cmbService.setContainerDataSource(getServiceList(serviceContainer));
                cmbService.select(null);

                Container serverContainer = cmbServer.getContainerDataSource();
                serverContainer.removeAllItems();
                cmbServer.setContainerDataSource(getServerList(serverContainer));
                cmbServer.select(null);

            }

        }
    }

    public IndexedContainer getInitialContainer() {
        IndexedContainer c = new IndexedContainer();
        addContainer(c);
        return c;
    }

    void addContainer(IndexedContainer container) {
        container.addContainerProperty("DateTime", String.class, null);
        container.addContainerProperty("LogLevel", String.class, null);
        container.addContainerProperty("myCloud", String.class, null);
        container.addContainerProperty("Service", String.class, null);
        container.addContainerProperty("Server", String.class, null);
        container.addContainerProperty("Message", String.class, null);
    }

    void fillContainer(Container container, List<EventLog> eventLogs) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        int i = 0;
        for (EventLog eventLog : eventLogs) {
            String id = String.valueOf(i++);
            Item item = container.addItem(id);
            item.getItemProperty("DateTime").setValue(simpleDateFormat.format(eventLog.getLogDate()));
            item.getItemProperty("LogLevel").setValue(transformLogLevel(eventLog.getLogLevel()));
            item.getItemProperty("myCloud").setValue(eventLog.getFarmName());
            item.getItemProperty("Service").setValue(eventLog.getComponentName());
            item.getItemProperty("Server").setValue(eventLog.getInstanceName());
            item.getItemProperty("Message").setValue(eventLog.getMessage());
        }
    }

    void search() {
        EventLogService eventLogService = BeanContext.getBean(EventLogService.class);
        EventLogDao.SearchCondition searchCondition = new EventLogDao.SearchCondition();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (cmbDateNow.getValue() != null && cmbDateNow.isEnabled()) {
            Integer[] dateValues = (Integer[]) cmbDateNow.getValue();
            searchCondition.setFromDate(getTime(calendar, dateValues[0], dateValues[1], dateValues[2]));
        }
        if (fromDate.getValue() != null && fromDate.isEnabled()) {
            searchCondition.setFromDate((Date) fromDate.getValue());
        }
        if (toDate.getValue() != null && toDate.isEnabled()) {
            searchCondition.setToDate((Date) toDate.getValue());
        }
        if (cmbMyCloud.getValue() != null) {
            searchCondition.setFarmNo((Long) cmbMyCloud.getValue());
        }
        if (cmbLoglevel.getValue() != null) {
            searchCondition.setLogLevel((Integer) cmbLoglevel.getValue());
        }
        if (cmbService.getValue() != null) {
            searchCondition.setComponentNo((Long) cmbService.getValue());
        }
        if (cmbServer.getValue() != null) {
            searchCondition.setInstanceNo((Long) cmbServer.getValue());
        }
        Long userNo = ViewContext.getUserNo();
        if (userNo != null) {
            searchCondition.setUserNo(userNo);
        }
        searchCondition.setLimit(limit);

        if (!fromDate.isValid() || !toDate.isValid()) {
            String message = ViewMessages.getMessage("IUI-000076", dateFormat);
            DialogConfirm dialog = new DialogConfirm(ViewProperties.getCaption("dialog.error"), message);
            getWindow().addWindow(dialog);
            return;
        }

        List<EventLog> eventLogs = eventLogService.readBySearchCondition(searchCondition);

        if (limit != null && eventLogs.size() >= limit) {
            String message = ViewMessages.getMessage("IUI-000048", new Object[] { limit });
            Notification notification = new Notification(message, Notification.TYPE_WARNING_MESSAGE);
            notification.setPosition(Notification.POSITION_TOP_RIGHT);
            notification.setDelayMsec(1000);
            notification.setStyleName("search");
            getWindow().showNotification(notification);

        } else if (eventLogs.isEmpty()) {
            String message = ViewMessages.getMessage("IUI-000049");
            Notification notification = new Notification(message, Notification.TYPE_WARNING_MESSAGE);
            notification.setPosition(Notification.POSITION_TOP_RIGHT);
            notification.setDelayMsec(1000);
            notification.setStyleName("search");
            getWindow().showNotification(notification);
        }

        //最終更新時刻をセット
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date lastUpdateDate = getTime(calendar, 0, 0, 0);
        lastUpdate.setCaption(simpleDateFormat.format(lastUpdateDate));

        Container c = logTable.getContainerDataSource();
        c.removeAllItems();
        fillContainer(c, eventLogs);
        logTable.sort();
    }
}