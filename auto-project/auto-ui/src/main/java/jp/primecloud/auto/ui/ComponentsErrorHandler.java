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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.util.MessageUtils;
import com.vaadin.event.ListenerMethod;
import com.vaadin.ui.AbstractComponent.ComponentErrorEvent;
import com.vaadin.ui.AbstractComponent.ComponentErrorHandler;
import com.vaadin.ui.Window;

/**
 * <p>
 * エラーが発生した際に呼び出されるClassです。
 * エラーメッセージの取得とダイアログ表示、エラーログの出力などを行います。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ComponentsErrorHandler implements ComponentErrorHandler {

    protected Log log = LogFactory.getLog(ComponentsErrorHandler.class);

    Window mainWindow;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param mainWindow
     */
    public ComponentsErrorHandler(Window mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public boolean handleComponentError(ComponentErrorEvent event) {
        // エラーを取得
        Throwable throwable = event.getThrowable();
        if (throwable instanceof ListenerMethod.MethodException) {
            throwable = throwable.getCause();
        }

        // 予期せぬエラーの場合、エラーログを出力する
        if (!(throwable instanceof AutoException) && !(throwable instanceof MultiCauseException)) {
            String message = "[ECOMMON-000000] " + MessageUtils.getMessage("ECOMMON-000000");
            log.error(message, throwable);
        }

        String message;
        if (throwable instanceof AutoApplicationException) {
            // アプリケーションのエラーメッセージを表示
            AutoApplicationException e = (AutoApplicationException) throwable;
            message = ViewMessages.getMessage(e.getCode(), e.getAdditions());
        } else {
            // 予期せぬエラーとしてダイアログを表示
            String code;
            if (throwable instanceof AutoException) {
                code = ((AutoException) throwable).getCode();
            } else {
                code = "ECOMMON-000000";
            }
            message = ViewMessages.getMessage("EUI-000001", code);
        }
        String caption = ViewProperties.getCaption("dialog.error");

        DialogConfirm dialog = new DialogConfirm(caption, message, Buttons.OK);
        mainWindow.addWindow(dialog);

        return true;
    }
}
