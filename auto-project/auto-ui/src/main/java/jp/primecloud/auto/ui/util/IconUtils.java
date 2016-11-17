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

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.service.dto.PlatformDto;

import org.apache.commons.lang.StringUtils;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class IconUtils {

    private static final String FILE_SEPARATOR = "/";

    private static final String DEFAULT_THEME = "default";

    private static final String VAADIN_THEMES_PATH = "./VAADIN/themes/";

    private IconUtils() {
    }

    public static Icons getPlatformIcon(PlatformDto platformDto) {
        String platformType = platformDto.getPlatform().getPlatformType();
        boolean isEuca = false;
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformType)) {
            if (platformDto.getPlatformAws().getEuca()) {
                isEuca = true;
            }
        }

        return getPlatformIcon(platformType, isEuca);

    }

    public static Icons getPlatformIcon(LoadBalancerPlatformDto platformDto) {
        String platformType = platformDto.getPlatform().getPlatformType();
        boolean isEuca = false;
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformType)) {
            if (platformDto.getPlatformAws().getEuca()) {
                isEuca = true;
            }
        }

        return getPlatformIcon(platformType, isEuca);

    }

    public static Icons getPlatformIcon(String platformType, boolean isEuca) {
        Icons rtIcon = Icons.NONE;

        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platformType)) {
            if (isEuca) {
                rtIcon = Icons.EUCALYPTUS;
            } else {
                rtIcon = Icons.AWS;
            }
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platformType)) {
            rtIcon = Icons.VMWARE;
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platformType)) {
            rtIcon = Icons.NIFTY;
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platformType)) {
            rtIcon = Icons.CLOUD_STACK;
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platformType)) {
            rtIcon = Icons.VCLOUD;
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platformType)) {
            rtIcon = Icons.AZURE;
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platformType)) {
            rtIcon = Icons.OPENSTACK;
        }

        return rtIcon;
    }

    public static Icons getOsIcon(ImageDto imageDto) {
        Icons rtIcon = Icons.NONE;
        if (imageDto.getImage().getOs().startsWith(PCCConstant.OS_NAME_CENTOS)) {
            rtIcon = Icons.CENTOS;
        } else if (imageDto.getImage().getOs().startsWith(PCCConstant.OS_NAME_REDHAT)) {
            rtIcon = Icons.REDHAT;
        } else if (imageDto.getImage().getOs().startsWith(PCCConstant.OS_NAME_UBUNTU)) {
            rtIcon = Icons.UBUNTU;
        } else if (imageDto.getImage().getOs().startsWith(PCCConstant.OS_NAME_WIN)) {
            rtIcon = Icons.WINDOWS;
        }
        return rtIcon;
    }

    public static Icons getImageIcon(ImageDto imageDto) {
        String iconName = StringUtils.substringBefore(imageDto.getImage().getImageName(), "_");
        Icons rtIcon = Icons.NONE;
        if (PCCConstant.IMAGE_NAME_APPLICATION.equals(iconName)) {
            rtIcon = Icons.PAAS;
        } else if (PCCConstant.IMAGE_NAME_PRJSERVER.equals(iconName)) {
            rtIcon = Icons.PRJSERVER;
        } else if (PCCConstant.IMAGE_NAME_WINDOWS.equals(iconName)) {
            rtIcon = Icons.WINDOWS_APP;
        } else if ("cloudstack".equals(iconName)) {
            rtIcon = Icons.CLOUD_STACK;
        } else {
            rtIcon = Icons.fromName(iconName);
        }
        return rtIcon;
    }

    private static Application getApplication(Component component) {
        Application application = null;
        while (component != null) {
            if (component instanceof Window) {
                application = ((Window) component).getApplication();
                break;
            }
            component = component.getParent();
        }
        return application;
    }

    public static String getIconPath(Application application, Icons icon) {
        if (application != null) {
            String theme = application.getTheme();
            if (theme == null) {
                theme = DEFAULT_THEME;
            }
            return VAADIN_THEMES_PATH + theme + FILE_SEPARATOR + icon.path();
        } else {
            return VAADIN_THEMES_PATH + DEFAULT_THEME + FILE_SEPARATOR + icon.path();
        }
    }

    public static String getIconPath(Component component, Icons icon) {
        return getIconPath(getApplication(component), icon);
    }

    public static String createImageTag(Component component, Icons icon, String text) {
        return createImageTag(getApplication(component), icon, text);
    }

    public static String createImageTag(Application application, Icons icon) {
        return createImageTag(application, icon, null);
    }

    public static String createImageTag(Application application, Icons icon, String text) {
        String tag = "<img src=\"" + getIconPath(application, icon) + "\">";
        if (StringUtils.isNotEmpty(text)) {
            tag = tag += "<div>" + text + "</div>";
        }
        return tag;
    }

}
