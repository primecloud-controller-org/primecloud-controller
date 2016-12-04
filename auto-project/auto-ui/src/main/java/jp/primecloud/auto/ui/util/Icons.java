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
package jp.primecloud.auto.ui.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

/**
 * <p>
 * アイコン定義クラス
 * </p>
 *
 */
public class Icons {

    public static Icons NONE = new Icons("none");

    public static Icons SPACER = new Icons("spacer.png");

    public static Icons ADD = new Icons("add.png");

    public static Icons APACHE = new Icons("apache.png");

    public static Icons ATTACH_MINI = new Icons("attachmini.png");

    public static Icons AWS = new Icons("aws.png");

    /** PLATFORM.PLATFORM_TYPEの文字列が "azure" の時に表示されるアイコン  */
    public static Icons AZURE = new Icons("azure.png");

    public static Icons BASIC = new Icons("basic.png");

    public static Icons CENTOS = new Icons("centos.png");

    public static Icons CHECKON = new Icons("checkon.png");

    public static Icons CLOUD = new Icons("cloud.png");

    public static Icons CLOUDBIG = new Icons("cloudbig.png");

    public static Icons CLOUD_STACK = new Icons("cloudstack.png");

    public static Icons CONFIGURING = new Icons("Configuring.png");

    public static Icons CUSTOM = new Icons("custom.png");

    public static Icons DELETE = new Icons("Delete.png");

    public static Icons DELETEMINI = new Icons("deletemini.png");

    public static Icons DETACH_MINI = new Icons("detachmini.png");

    public static Icons DETAIL = new Icons("detail.png");

    public static Icons DISABLE_MINI = new Icons("disablemini.png");

    public static Icons DISABLE = new Icons("disable.png");

    public static Icons DISABLE_WITH_ATTENTION = new Icons("disable_attention.png");

    public static Icons DLGWARNING = new Icons("dlgwarning.png");

    public static Icons EDIT = new Icons("Edit.png");

    public static Icons EDITMINI = new Icons("editmini.png");

    public static Icons ENABLE_MINI = new Icons("enablemini.png");

    public static Icons ENABLE = new Icons("enable.png");

    public static Icons ENABLE_WITH_ATTENTION = new Icons("enable_attention.png");

    public static Icons EUCALYPTUS = new Icons("eucalyptus.png");

    public static Icons EXTERNAL_MINI = new Icons("externalmini.png");

    public static Icons GERONIMO = new Icons("geronimo.png");

    public static Icons INFO = new Icons("info.png");

    public static Icons LOADBALANCER_TAB = new Icons("loadbalancertab.png");

    public static Icons LINUX = new Icons("linux.png");

    public static Icons LISTENER_MINI = new Icons("listenermini.png");

    public static Icons LOGIN = new Icons("login.png");

    public static Icons LOGOUT = new Icons("logout.png");

    public static Icons MNGSYSTEM = new Icons("mngsystem.png");

    public static Icons PAYSYSTEM = new Icons("paysystem.png");

    public static Icons MYCLOUD = new Icons("mycloud.png");

    public static Icons MYSQL = new Icons("mysql.png");

    public static Icons MYSQL_MASTER = new Icons("mysql_master.png");

    public static Icons MYSQL_SLAVE = new Icons("mysql_slave.png");

    public static Icons NIFTY = new Icons("nifty.png");

    /** PLATFORM.PLATFORM_TYPEの文字列が "openstack" の時に表示されるアイコン  */
    public static Icons OPENSTACK = new Icons("openstack.png");

    public static Icons PAAS = new Icons("paas.png");

    public static Icons PCCLOGO = new Icons("PCCLogo.png");

    public static Icons PLAY = new Icons("Play.png");

    public static Icons PLAYMINI = new Icons("Playmini.png");

    public static Icons PRJSERVER = new Icons("prjserver.png");

    public static Icons REDHAT = new Icons("redhat.png");

    public static Icons RELOAD = new Icons("Reload.png");

    public static Icons RUNNING = new Icons("Running.png");

    /** インスタンスは起動しているが設定確認が必要<BR>
     *  ロードバランサの場合：リスナーが存在しない */
    public static Icons RUN_WARNING = new Icons("RunWarning.png");

    public static Icons SELECTMINI = new Icons("selectmini.png");

    public static Icons SERVER = new Icons("server.png");

    public static Icons SERVERTAB = new Icons("servertab.png");

    public static Icons SERVICETAB = new Icons("servicetab.png");

    public static Icons SHORTCUT = new Icons("shortcut.png");

    public static Icons STARTING = new Icons("Starting.png");

    public static Icons STOP = new Icons("Stop.png");

    public static Icons STOPMINI = new Icons("Stopmini.png");

    public static Icons STOPPED = new Icons("Stopped.png");

    public static Icons STOPPING = new Icons("Stopping.png");

    public static Icons SYNC = new Icons("sync.png");

    public static Icons TOMCAT = new Icons("tomcat.png");

    /** OS_TYPEの文字列が "ubuntu" の時に表示されるアイコン  */
    public static Icons UBUNTU = new Icons("ubuntu.png");

    public static Icons USER = new Icons("user.png");

    public static Icons VCLOUD = new Icons("vcloud.png");

    public static Icons VMWARE = new Icons("vmware.png");

    public static Icons WINDOWS = new Icons("windowsos.png");

    public static Icons WINDOWS_APP = new Icons("windowsapp.png");

    public static Icons WARNING = new Icons("Warning.png");

    public static Icons START_MONITORING = new Icons("Startmonitoring.png");

    public static Icons STOP_MONITORING = new Icons("Stopmonitoring.png");

    public static Icons MONITORING = new Icons("Monitoring.png");

    public static Icons UN_MONITORING = new Icons("UnMonitoring.png");

    protected static Map<String, Icons> map;

    protected static String dir = "icons/";

    protected static String ext = ".png";

    private String file;

    private Icons(String file) {
        this.file = file;
    }

    static {
        map = new HashMap<String, Icons>();
        Field[] fields = Icons.class.getFields();
        for (Field field : fields) {
            if (Icons.class.equals(field.getType()) && Modifier.isStatic(field.getModifiers())) {
                try {
                    Icons value = Icons.class.cast(field.get(null));
                    map.put(field.getName().toLowerCase(Locale.ENGLISH), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Icons fromName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null.");
        }

        Icons icons = map.get(name.toLowerCase(Locale.ENGLISH));
        if (icons == null) {
            icons = new Icons(name + ext);
        }
        return icons;
    }

    public String file() {
        return file;
    }

    public String path() {
        return dir + file;
    }

    public Resource resource() {
        return new ThemeResource(path());
    }

}
