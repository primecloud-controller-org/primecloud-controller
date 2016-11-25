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

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * IPアドレスを扱うためのユーティリティです。
 * </p>
 *
 */
public class IpAddressUtils {

    /**
     * IPアドレスの文字列を数値（0～2^32-1）に変換します。
     *
     * @param ipAddress IPアドレス（文字列）
     * @return IPアドレス（数値）
     */
    public static long parse(String ipAddress) {
        long num = 0L;

        String[] array = StringUtils.split(ipAddress, ".", 4);
        for (int i = 0; i < 4; i++) {
            num = num * 256;
            num += Long.parseLong(array[i]);
        }

        return num;
    }

    /**
     * IPアドレスの数値（0～2^32-1）を文字列に変換します。
     *
     * @param ipAddress IPアドレス（数値）
     * @return IPアドレス（文字列）
     */
    public static String format(long ipAddress) {
        if (ipAddress < 0L || (1L << 32) - 1 < ipAddress) {
            throw new IllegalArgumentException("IpAddress number " + ipAddress + " is illegal.");
        }

        String[] octets = new String[4];

        for (int i = 0; i < 4; i++) {
            octets[3 - i] = Long.toString(ipAddress % 256);
            ipAddress = ipAddress / 256;
        }

        return StringUtils.join(octets, ".");
    }

    /**
     * CIDR記法のIPアドレスが含まれるネットワークアドレスの数値を取得します。
     *
     * @param cidr CIDR記法のIPアドレス
     * @return ネットワークアドレスの数値
     */
    public static long getNetworkAddress(String cidr) {
        String[] array = StringUtils.split(cidr, "/", 2);
        int prefix = Integer.parseInt(array[1]);

        return getNetworkAddress(array[0], prefix);
    }

    public static long getNetworkAddress(String ipAddress, int prefix) {
        return getNetworkAddress(parse(ipAddress), prefix);
    }

    public static long getNetworkAddress(long ipAddress, int prefix) {
        if (prefix < 0 || 32 < prefix) {
            throw new IllegalArgumentException("Prefix " + prefix + " is illegal.");
        }

        return (ipAddress >> (32 - prefix) << (32 - prefix));
    }

    /**
     * CIDR記法のIPアドレスが含まれるブロードキャストアドレスの数値を取得します。
     *
     * @param cidr CIDR記法のIPアドレス
     * @return ブロードキャストアドレスの数値
     */
    public static long getBroadcastAddress(String cidr) {
        String[] array = StringUtils.split(cidr, "/", 2);
        int prefix = Integer.parseInt(array[1]);

        return getBroadcastAddress(array[0], prefix);
    }

    public static long getBroadcastAddress(String ipAddress, int prefix) {
        return getBroadcastAddress(parse(ipAddress), prefix);
    }

    public static long getBroadcastAddress(long ipAddress, int prefix) {
        if (prefix < 0 || 32 < prefix) {
            throw new IllegalArgumentException("Prefix " + prefix + " is illegal.");
        }

        return (((ipAddress >> (32 - prefix)) + 1) << (32 - prefix)) - 1;
    }

}
