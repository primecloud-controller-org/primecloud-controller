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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * <p>
 * {@link MessageUtils}のテストクラスです。
 * </p>
 *
 */
public class MessageUtilsTest {

    @Test
    public void testGetMessage() {
        String message;

        // 正常系
        message = MessageUtils.getMessage("IAAA-000001");
        assertEquals("abcdef", message);

        message = MessageUtils.getMessage("IAAA-000002", "12", 34);
        assertEquals("abc12def34ghi", message);

        message = MessageUtils.getMessage("IBBB-000001");
        assertEquals("xyz", message);

        // 不要な引数がある場合
        message = MessageUtils.getMessage("IAAA-000001", "12", 34);
        assertEquals("abcdef", message);

        // コードがない場合
        message = MessageUtils.getMessage("IBBB-000002");
        assertEquals("IBBB-000002", message);

        message = MessageUtils.getMessage("ICCC-000001");
        assertEquals("ICCC-000001", message);
    }

    @Test
    public void testFormat() {
        String message;

        // 数字が自動的にカンマ区切りや四捨五入にならないこと
        message = MessageUtils.format("{0}", 12345.6789);
        assertEquals("12345.6789", message);
    }

}
