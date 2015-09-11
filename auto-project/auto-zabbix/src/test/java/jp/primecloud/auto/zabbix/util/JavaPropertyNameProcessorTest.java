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
package jp.primecloud.auto.zabbix.util;

import static org.junit.Assert.assertEquals;
import jp.primecloud.auto.zabbix.util.JavaPropertyNameProcessor;

import org.junit.Test;

/**
 * <p>
 * {@link JavaPropertyNameProcessor}のテストクラスです。
 * </p>
 *
 */
public class JavaPropertyNameProcessorTest {

    private JavaPropertyNameProcessor processor = new JavaPropertyNameProcessor();

    @Test
    public void testProcessPropertyName() {
        assertEquals(null, processor.processPropertyName(null, null));
        assertEquals("", processor.processPropertyName(null, ""));

        assertEquals("aaa", processor.processPropertyName(null, "aaa"));
        assertEquals("aaaBbb", processor.processPropertyName(null, "aaa_bbb"));
        assertEquals("aaaBbbCcc", processor.processPropertyName(null, "aaa_bbb_ccc"));

        assertEquals("ABC", processor.processPropertyName(null, "a_b_c"));
        assertEquals("aaBC", processor.processPropertyName(null, "aa_b_c"));
    }

}
