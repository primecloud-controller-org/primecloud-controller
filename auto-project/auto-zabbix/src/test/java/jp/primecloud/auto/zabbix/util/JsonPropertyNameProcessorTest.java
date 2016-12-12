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
import jp.primecloud.auto.zabbix.util.JsonPropertyNameProcessor;

import org.junit.Test;

/**
 * <p>
 * {@link JsonPropertyNameProcessor}のテストクラスです。
 * </p>
 *
 */
public class JsonPropertyNameProcessorTest {

    private JsonPropertyNameProcessor processor = new JsonPropertyNameProcessor();

    @Test
    public void testProcessPropertyName() {
        assertEquals(null, processor.processPropertyName(null, null));
        assertEquals("", processor.processPropertyName(null, ""));

        assertEquals("aaa", processor.processPropertyName(null, "aaa"));
        assertEquals("aaa_bbb", processor.processPropertyName(null, "aaaBbb"));
        assertEquals("aaa_bbb_ccc", processor.processPropertyName(null, "aaaBbbCcc"));

        assertEquals("a_b_c", processor.processPropertyName(null, "ABC"));
        assertEquals("aa_b_c", processor.processPropertyName(null, "aaBC"));
    }

}
