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

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.exception.MultiCauseException;
import jp.primecloud.auto.ui.DialogConfirm.Buttons;
import jp.primecloud.auto.ui.DialogConfirm.Result;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.ViewMessages;
import jp.primecloud.auto.ui.util.ViewProperties;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vaadin.Application;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ListenerMethod;
import com.vaadin.terminal.Terminal.ErrorEvent;
import com.vaadin.terminal.Terminal.ErrorListener;

/**
 * <p>
 * エラーが発生した際に呼び出されるClassです。
 * エラーメッセージの取得とダイアログ表示、エラーログの出力などを行います。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ErrorHandler implements ErrorListener {

    protected Log log = LogFactory.getLog(ErrorHandler.class);

    protected Application application;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param application
     */
    public ErrorHandler(Application application) {
        this.application = application;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminalError(ErrorEvent event) {
        // エラーを取得
        Throwable throwable = event.getThrowable();
        if (throwable instanceof ListenerMethod.MethodException) {
            throwable = throwable.getCause();
        }

        // 予期せぬエラーの場合、エラーログを出力する
        if (!(throwable instanceof AutoException) && !(throwable instanceof MultiCauseException)
                && !(throwable instanceof InvalidValueException)) {
            String message = "[ECOMMON-000000] " + MessageUtils.getMessage("ECOMMON-000000");
            log.error(message, throwable);
        }

        String caption = ViewProperties.getCaption("dialog.error");

        // 入力チェックエラーの場合
        if (throwable instanceof InvalidValueException) {
            String message = throwable.getMessage();
            if (null == message) {
                // メッセージが取得できない場合は複合エラー 先頭を表示する
                InvalidValueException[] exceptions = ((InvalidValueException) throwable).getCauses();
                message = exceptions[0].getMessage();
            }

            DialogConfirm dialog = new DialogConfirm(caption, message);
            application.getMainWindow().addWindow(dialog);
        }
        // アプリケーションエラーの場合
        else if (throwable instanceof AutoApplicationException) {
            AutoApplicationException e = (AutoApplicationException) throwable;
            String message = ViewMessages.getMessage(e.getCode(), e.getAdditions());

            DialogConfirm dialog = new DialogConfirm(caption, message);
            application.getMainWindow().addWindow(dialog);
        }
        // 予期せぬエラーの場合
        else {
            String code;
            if (throwable instanceof AutoException) {
                code = ((AutoException) throwable).getCode();
            } else {
                code = "ECOMMON-000000";
            }
            String message = ViewMessages.getMessage("EUI-000001", code);

            DialogConfirm dialog = new DialogConfirm(caption, message, Buttons.OK);
            dialog.setCallback(new DialogConfirm.Callback() {
                @Override
                public void onDialogResult(Result result) {
                    // セッション情報を初期化
                    LoggingUtils.removeContext();
                    ContextUtils.invalidateSession();

                    application.close();
                }
            });
            application.getMainWindow().addWindow(dialog);
        }
    }

}
