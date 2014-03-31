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
package jp.primecloud.auto.common.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * サブネットを扱うクラス
 * </p>
 *
 */
public class Subnet {

    // =============================================
    // 定数
    // ================
    /** 制限ブロードキャストアドレス */
    private static final String LIMIT_BROADCAST_ADDR = "255.255.255.255";

    /** 有効アドレス数配列 */
    private static final long[] IPADDRSU_ARRAY = { 2147483648L, 1073741824L, 536870912L, 268435456L, 134217728L,
            67108864L, 33554432L, 16777216L, 8388608L, 4194304L, 2097152L, 1048576L, 524288L, 262144L, 131072L, 65536L,
            32768L, 16384L, 8192L, 4096L, 2048L, 1024L, 512L, 256L, 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L };

    /** マスク長Min */
    private static final int MASK_MIN = 1;

    /** マスク長Max */
    private static final int MASK_MAX = 32;

    // =============================================
    // ローカル変数
    // ================
    private String netWorkAddr;

    private String broadCastAddr;

    private String availableMinIp;

    private String availableMaxIp;

    private List<String> reservedIps = new ArrayList<String>();

    /**
     *
     * コンストラクタ
     *
     * @param subnetIp サブネット
     * @param maskLen マスク長(1～32)
     */
    public Subnet(String subnetIp, int maskLen) {
        // IPアドレスをバイト配列に設定
        byte[] ipArray = addressTobyte(subnetIp);
        // サブネットマスク長をバイト配列に設定
        byte[] maskArray = getMask(maskLen);
        // IPアドレスとサブネットマスク長をAND演算する
        byte[] calcAndArray = new byte[ipArray.length];
        for (int i = 0; i < calcAndArray.length; i++) {
            calcAndArray[i] = (byte) (ipArray[i] & maskArray[i]);
        }
        // ネットワークアドレス(サブネットマスク検査でも使用)
        netWorkAddr = byteToAddress(calcAndArray);

        // 制限ブロードキャストアドレスをバイト配列に設定
        byte[] limitBroadCastArray = addressTobyte(LIMIT_BROADCAST_ADDR);
        // サブネットマスクと制限ブロードキャストアドレスをXOR演算する
        byte[] calcXorArray = new byte[maskArray.length];
        for (int k = 0; k < calcXorArray.length; k++) {
            calcXorArray[k] = (byte) (maskArray[k] ^ limitBroadCastArray[k]);
        }
        // ホストアドレスを設定
        byte[] hostAddrArray = calcXorArray;
        // IPアドレスとホストアドレスをOR演算する
        byte[] calcOrArray = new byte[ipArray.length];
        for (int j = 0; j < calcOrArray.length; j++) {
            calcOrArray[j] = (byte) (ipArray[j] | hostAddrArray[j]);
        }

        // ブロードキャストアドレス(サブネットマスク検査でも使用)
        broadCastAddr = byteToAddress(calcOrArray);

        // 使用可能な最小IPアドレスを設定
        availableMinIp = getNextAddress(netWorkAddr);
        // 使用可能な最大IPアドレスを設定
        availableMaxIp = getBeforeAddress(broadCastAddr);
    }

    /**
     *
     * 予約済みIPを追加する
     *
     * @param reservedIp 予約済みIP、"xxx.xxx.xxx.xxx"形式
     */
    public void addReservedIp(String reservedIp) {
        reservedIps.add(reservedIp);
        if (availableMinIp.equals(reservedIp)) {
            availableMinIp = getNextAddress(reservedIp);
        }
        if (availableMaxIp.equals(reservedIp)) {
            availableMaxIp = getBeforeAddress(reservedIp);
        }
    }

    /**
     *
     * 使用可能なIP(最小)、"xxx.xxx.xxx.xxx"形式 を取得
     *
     * @return availableMinIp
     */
    public String getAvailableMinIp() {
        return availableMinIp;
    }

    /**
     *
     * 使用可能なIP(最大)、"xxx.xxx.xxx.xxx"形式 を取得
     *
     * @return availableMaxIp
     */
    public String getAvailableMaxIp() {
        return availableMaxIp;
    }

    /**
     *
     * 予約済みIPを取得
     *
     * @return reservedIps
     */
    public List<String> getReservedIps() {
        return reservedIps;
    }

    /**
     * サブネットマスク検査
     *
     * サブネットマスクしたIPアドレスの範囲であるかを検査する。
     * @param ip 検査したいIPアドレス文字列、"xxx.xxx.xxx.xxx" 形式
     * @return true=OK, false=NG
     */
    public boolean isScorp(String ip) {
        boolean result = true;
        // IPアドレスを配列に設定
        int[] ipAddrArray = addressToInt(ip);
        // ネットワークアドレスの次のIPアドレスを求める
        String nextNetWorkAddr;
        nextNetWorkAddr = getNextAddress(netWorkAddr);
        // ネットワークアドレスの次のIPアドレスを配列に設定
        int[] nextNetWorkAddrArray = addressToInt(nextNetWorkAddr);
        // ブロードキャストアドレスの前のIPアドレスを求める
        String beforeBroadCastAddr;
        beforeBroadCastAddr = getBeforeAddress(broadCastAddr);
        // ブロードキャストアドレスの前のIPアドレスを配列に設定
        int[] beforeBroadCastAddrArray = addressToInt(beforeBroadCastAddr);

        // IPアドレスの範囲チェック
        for (int i = 0; i < nextNetWorkAddrArray.length; i++) {
            // ネットワークアドレスの次のIPアドレス[i]＜＝IPアドレス[i]かつ
            // IPアドレス[i]＜＝ブロードキャストアドレスの前のIPアドレス[i]以外の場合
            if ((nextNetWorkAddrArray[i] <= ipAddrArray[i] && ipAddrArray[i] <= beforeBroadCastAddrArray[i]) == false) {
                // チェックNG
                result = false;
                break;
            }
        }
        // チェックOKかつ予約済みIPアドレス配列に値があるかつ
        // 引数のIPアドレスが予約済みIPアドレス配列に存在する場合
        if (result == true && reservedIps.size() > 0 && reservedIps.contains(ip) == true) {
            // チェックNG
            result = false;
        }

        return result;
    }

    /**
     * マスク値で、有効アドレス数を取得
     * @param maskLen マスク長、1～32
     * @return 有効アドレス数
     */
    public static long countSegment(int maskLen) {
        // サブネットマスク長が1～32以外の場合
        if ((MASK_MIN <= maskLen && maskLen <= MASK_MAX) == false) {
            return 0;
        }
        // マスク長に対応した有効アドレス数を返却
        return IPADDRSU_ARRAY[maskLen - 1];
    }

    /**
     * IPアドレス文字列、"xxx.xxx.xxx.xxx" → byte[].
     * @param ip IPアドレス文字列
     * @return byte[]
     */
    private static byte[] addressTobyte(String ip) {
        String[] sp = ip.split("\\.");
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (Integer.parseInt(sp[i]));
        }
        return b;
    }

    /**
     * IPアドレス文字列、"xxx.xxx.xxx.xxx" → Int[]
     * @param ip IPアドレス文字列
     * @return Int[]
     */
    private static int[] addressToInt(String ip) {
        String[] ipArray = ip.split("\\.");
        int[] intArray = new int[4];
        for (int i = 0; i < 4; i++) {
            intArray[i] = Integer.parseInt(ipArray[i]);
        }
        return intArray;
    }

    /**
     * byte[] → IPアドレス文字列、"xxx.xxx.xxx.xxx"
     * @param bytes IPアドレス文字列のバイト配列
     * @return IPアドレス文字列
     */
    public static String byteToAddress(byte[] bytes) {
        StringBuffer b = new StringBuffer();
        String dot = "";
        for (int i = 0; i < 4; i++) {
            b.append(dot);
            b.append((int) (bytes[i] & 0xFF));
            dot = ".";
        }
        return b.toString();
    }

    /**
     *
     * 引数の次のIPアドレスを "xxx.xxx.xxx.xxx"形式で取得
     *
     * @param ip IPアドレス文字列("xxx.xxx.xxx.xxx"形式)
     * @return IPアドレス文字列
     */
    public static String getNextAddress(String ip) {
        BigInteger bi = new BigInteger(addressTobyte(ip));
        bi = bi.add(new BigInteger(addressTobyte("0.0.0.1")));
        return byteToAddress(bi.toByteArray());
    }

    /**
     *
     * 引数の前のIPアドレスを "xxx.xxx.xxx.xxx"形式で取得
     *
     * @param ip IPアドレス文字列("xxx.xxx.xxx.xxx"形式)
     * @return IPアドレス文字列
     */
    public static String getBeforeAddress(String ip) {
        BigInteger bi = new BigInteger(addressTobyte(ip));
        bi = bi.subtract(new BigInteger(addressTobyte("0.0.0.1")));
        return byteToAddress(bi.toByteArray());
    }

    /**
     * マスク長→サブネットマスク byte[]
     * @param n マスク長
     * @return サブネットマスク byte[]
     */
    private static byte[] getMask(int n) {
        byte[] b = new BigInteger(1, new byte[] { -1, -1, -1, -1 }).shiftRight(n)
                .xor(new BigInteger(1, new byte[] { -1, -1, -1, -1 })).toByteArray();
        byte[] br = new byte[4];
        for (int i = 0; i < br.length; i++) {
            br[i] = b[i + 1];
        }
        return br;
    }
}
