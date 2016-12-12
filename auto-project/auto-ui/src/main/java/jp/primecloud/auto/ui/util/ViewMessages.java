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

import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ViewMessages {

    /**
     * TODO: メソッドコメントを記述
     *
     * @param code
     * @param additions
     * @return
     */
    public static String getMessage(String code, Object... additions) {
        String pattern = ViewProperties.get(code);
        if (StringUtils.isNotEmpty(pattern)) {
            return MessageUtils.format(pattern, additions);
        }

        return MessageUtils.getMessage(code, additions);
    }

}
