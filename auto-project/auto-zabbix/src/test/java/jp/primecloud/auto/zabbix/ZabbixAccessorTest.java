/*
 * Copyright 2015 by SCSK Corporation.
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
package jp.primecloud.auto.zabbix;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * <p>
 * {@link ZabbixAccessor}のテストクラスです。
 * </p>
 * 
 */
public class ZabbixAccessorTest {

    @Test
    public void testCompareVersion() {
        ZabbixAccessor accessor = ZabbixAccessor.getInstance(null, null, null, null);

        assertTrue(accessor.compareVersion("1.0", "1.8") == 0);
        assertTrue(accessor.compareVersion("1.1", "1.8.1") == 0);
        assertTrue(accessor.compareVersion("1.2", "1.8.2") == 0);
        assertTrue(accessor.compareVersion("1.3", "1.8.3") == 0);
        assertTrue(accessor.compareVersion("1.3", "1.8.15") == 0);
        assertTrue(accessor.compareVersion("1.4", "2.0.0") == 0);
        assertTrue(accessor.compareVersion("1.4", "2.0.3") == 0);
        assertTrue(accessor.compareVersion("2.0.4", "2.0.4") == 0);
        assertTrue(accessor.compareVersion("2.2.9", "2.2.9") == 0);

        assertTrue(accessor.compareVersion("1.3", "2.0") < 0);
        assertTrue(accessor.compareVersion("3.0.0", "2.2.9") > 0);
    }

}
