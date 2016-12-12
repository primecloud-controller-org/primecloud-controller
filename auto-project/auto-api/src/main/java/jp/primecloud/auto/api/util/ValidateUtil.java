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
package jp.primecloud.auto.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;

import jp.primecloud.auto.exception.AutoApplicationException;

/**
 * <p>
 * 入力チェッククラス
 * </p>
 *
 */
public class ValidateUtil {

    /**
     * 必須チェック
     *
     * @param value 入力チェック対象の値
     * @param code 入力チェックエラー時のメッセージコード
     * @param params 入力チェックエラー時のメッセージパラメータ
     */
    public static void required(String value, String code, Object[] params) {
        if (GenericValidator.isBlankOrNull(value)) {
            throw new AutoApplicationException(code, params);
        }
    }

    /**
     * 最小値、最大値チェック(int)
     *
     * @param value 入力チェック対象の値
     * @param min 最小値
     * @param max 最大値
     * @param code 入力チェックエラー時のメッセージコード
     * @param params 入力チェックエラー時のメッセージパラメータ
     */
    public static void intInRange(String value, int min, int max, String code, Object[] params) {
        if (GenericValidator.isInt(value) == false) {
            throw new AutoApplicationException(code, params);
        }
        if (GenericValidator.isInRange(Integer.valueOf(value), min, max) == false) {
            throw new AutoApplicationException(code, params);
        }
    }

    /**
     * 最小値、最大値チェック(long)
     *
     * @param value 入力チェック対象の値
     * @param min 最小値
     * @param max 最大値
     * @param code 入力チェックエラー時のメッセージコード
     * @param params 入力チェックエラー時のメッセージパラメータ
     */
    public static void longInRange(String value, long min, long max, String code, Object... params) {
        if (GenericValidator.isLong(value) == false) {
            throw new AutoApplicationException(code, params);
        }
        if (GenericValidator.isInRange(Long.parseLong(value), min, max) == false) {
            throw new AutoApplicationException(code, params);
        }
    }

    /**
     * 桁数チェック
     *
     * @param value 入力チェック対象の値
     * @param minLength 最小桁数
     * @param maxLength 最大桁数
     * @param code 入力チェックエラー時のメッセージコード
     * @param params 入力チェックエラー時のメッセージパラメータ
     */
    public static void lengthInRange(String value, int minLength, int maxLength, String code, Object[] params) {
        if (value != null) {
            if (GenericValidator.minLength(value, minLength) == false
                    || GenericValidator.maxLength(value, maxLength) == false) {
                throw new AutoApplicationException(code, params);
            }
        }
    }

    /**
     * パターンチェック
     *
     * @param value 入力チェック対象の値
     * @param regex パターン文字列
     * @param code 入力チェックエラー時のメッセージコード
     * @param params 入力チェックエラー時のメッセージパラメータ
     */
    public static void matchRegex(String value, String regex, String code, Object[] params) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches() == false) {
            throw new AutoApplicationException(code, params);
        }
    }

    /**
     * 日付チェック
     *
     * @param value 入力チェック対象の値
     * @param code 入力チェックエラー時のメッセージコード
     * @param params 入力チェックエラー時のメッセージパラメータ
     */
    public static void isDate(String value, String code, String datePattern, Object[] params) {
        if (GenericValidator.isDate(value, datePattern, false) == false) {
            throw new AutoApplicationException(code, params);
        }
    }

    /**
     *
     * boolean値チェック
     *
     * @param value 入力チェック対象の値
     * @param code 入力チェックエラー時のメッセージコード
     * @param params 入力チェックエラー時のメッセージパラメータ
     */
    public static void isBoolean(String value, String code, Object[] params) {
        if (StringUtils.equalsIgnoreCase("true", value) == false
                && StringUtils.equalsIgnoreCase("false", value) == false) {
            throw new AutoApplicationException(code, params);
        }
    }

}
