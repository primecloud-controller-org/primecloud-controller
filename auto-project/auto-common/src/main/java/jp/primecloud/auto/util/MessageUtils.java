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
package jp.primecloud.auto.util;

import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * メッセージの生成に関するユーティリティClassです。
 * </p>
 *
 */
public class MessageUtils {

    protected static ConcurrentMap<String, ResourceBundle> bundles = new ConcurrentHashMap<String, ResourceBundle>();

    private MessageUtils() {
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param pattern
     * @param arguments
     * @return
     */
    public static String format(String pattern, Object... arguments) {
        pattern = pattern.replace("'", "''");
        MessageFormat messageFormat = new MessageFormat(pattern);

        if (arguments != null && arguments.length > 0) {
            Format[] formats = messageFormat.getFormatsByArgumentIndex();
            for (int i = 0; i < arguments.length; i++) {
                if (formats.length <= i) {
                    continue;
                }

                // 引数が数値でフォーマットの指定がない場合、数値をそのまま出力するために文字列に変換する
                if (arguments[i] != null && arguments[i] instanceof Number && formats[i] == null) {
                    arguments[i] = arguments[i].toString();
                }
            }
        }

        return messageFormat.format(arguments);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param code
     * @param additions
     * @return
     */
    public static String getMessage(String code, Object... additions) {
        try {
            ResourceBundle bundle = getBundle(code);
            String resource = bundle.getString(code);
            return format(resource, (Object[]) additions);
        } catch (MissingResourceException e) {
            return code;
        }
    }

    protected static ResourceBundle getBundle(String code) {
        String module = getModule(code);
        ResourceBundle bundle = bundles.get(module);
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle(module + "-message");
            } catch (MissingResourceException e) {
                bundle = new EmptyResourceBundle();
            }
            bundles.putIfAbsent(module, bundle);
        }
        return bundle;
    }

    protected static String getModule(String code) {
        return code.substring(1, code.indexOf('-')).toLowerCase(Locale.ENGLISH);
    }

}
