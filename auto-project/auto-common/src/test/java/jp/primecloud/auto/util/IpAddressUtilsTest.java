/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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

public class IpAddressUtilsTest {

    @Test
    public void testParse() {
        assertEquals(0L, IpAddressUtils.parse("0.0.0.0"));
        assertEquals((1L << 32) - 1, IpAddressUtils.parse("255.255.255.255"));

        long address = (1L << 24) * 192 + (1L << 16) * 168 + (1L << 8) * 1 + 1L * 5;
        assertEquals(address, IpAddressUtils.parse("192.168.1.5"));
    }

    @Test
    public void testFormat() {
        assertEquals("0.0.0.0", IpAddressUtils.format(0L));
        assertEquals("255.255.255.255", IpAddressUtils.format((1L << 32) - 1));

        long address = (1L << 24) * 192 + (1L << 16) * 168 + (1L << 8) * 1 + 1L * 5;
        assertEquals("192.168.1.5", IpAddressUtils.format(address));
    }

    @Test
    public void testGetNetworkAddress() {
        assertEquals("0.0.0.0", IpAddressUtils.format(IpAddressUtils.getNetworkAddress("192.168.1.5/0")));
        assertEquals("192.0.0.0", IpAddressUtils.format(IpAddressUtils.getNetworkAddress("192.168.1.5/8")));
        assertEquals("192.168.0.0", IpAddressUtils.format(IpAddressUtils.getNetworkAddress("192.168.1.5/16")));
        assertEquals("192.168.1.0", IpAddressUtils.format(IpAddressUtils.getNetworkAddress("192.168.1.5/24")));
        assertEquals("192.168.1.5", IpAddressUtils.format(IpAddressUtils.getNetworkAddress("192.168.1.5/32")));
    }

    @Test
    public void testGetBroadcatAddress() {
        assertEquals("255.255.255.255", IpAddressUtils.format(IpAddressUtils.getBroadcastAddress("192.168.1.5/0")));
        assertEquals("192.255.255.255", IpAddressUtils.format(IpAddressUtils.getBroadcastAddress("192.168.1.5/8")));
        assertEquals("192.168.255.255", IpAddressUtils.format(IpAddressUtils.getBroadcastAddress("192.168.1.5/16")));
        assertEquals("192.168.1.255", IpAddressUtils.format(IpAddressUtils.getBroadcastAddress("192.168.1.5/24")));
        assertEquals("192.168.1.5", IpAddressUtils.format(IpAddressUtils.getBroadcastAddress("192.168.1.5/32")));
    }

}
