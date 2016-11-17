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

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VaadinUtils {

    private static final String FILE_SEPARATOR = "/";

    private static final String DEFAULT_THEME = "default";

    private static final String VAADIN_THEMES_PATH = "./VAADIN/themes/";

    private VaadinUtils() {
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param component
     * @return
     */
    public static ApplicationContext getApplicationContext(Component component) {
        Application application = getApplication(component);

        if (application != null) {
            return application.getContext();
        }
        return null;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param component
     * @return
     */
    public static Application getApplication(Component component) {
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

    /**
     * TODO: メソッドコメントを記述
     *
     * @param application
     * @param icon
     * @return
     */
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

    /**
     * TODO: メソッドコメントを記述
     *
     * @param component
     * @param icon
     * @return
     */
    public static String getIconPath(Component component, Icons icon) {
        return getIconPath(getApplication(component), icon);
    }

}
