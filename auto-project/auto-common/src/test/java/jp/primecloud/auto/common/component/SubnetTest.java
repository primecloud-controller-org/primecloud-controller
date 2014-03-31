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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * <p>
 * {@link Subnet}のテストクラスです。
 * </p>
 *
 */
public class SubnetTest {

    /**
     * テスト：正常系
     * 使用可能なIP(最小)、"xxx.xxx.xxx.xxx"形式 を取得確認
     *
     */
    @Test
    public void Subnet_test1() {
        Subnet subnet = new Subnet("192.168.1.1", 24);
        assertEquals("192.168.1.1", subnet.getAvailableMinIp());
    }

    /**
     * テスト：正常系
     * 使用可能なIP(最大)、"xxx.xxx.xxx.xxx"形式 を取得確認
     *
     */
    @Test
    public void Subnet_test2() {
        Subnet subnet = new Subnet("192.168.1.1", 24);
        assertEquals("192.168.1.254", subnet.getAvailableMaxIp());
    }

    /**
     * テスト：正常系(予約済みIP≠コンストラクタ取得使用可能なIP(最小)、予約済みIP≠コンストラクタ取得使用可能なIP(最大))
     * 引数の予約済みIPが予約済みIP配列に追加され取得できることを確認
     * 使用可能なIP(最小)が更新されていないことを確認
     * 使用可能なIP(最大)が更新されていないことを確認
     *
     */
    @Test
    public void Subnet_test3() {
        Subnet subnet = new Subnet("192.168.1.1", 24);
        subnet.addReservedIp("192.168.1.11");
        List<String> resultArray = new ArrayList<String>();
        resultArray.add("192.168.1.11");

        assertEquals(resultArray, subnet.getReservedIps());
        assertEquals("192.168.1.1", subnet.getAvailableMinIp());
        assertEquals("192.168.1.254", subnet.getAvailableMaxIp());
    }

    /**
     * テスト：正常系(予約済みIP＝コンストラクタ取得使用可能なIP(最小)、予約済みIP≠コンストラクタ取得使用可能なIP(最大))
     * 使用可能なIP(最小)が更新されていることを確認
     * 使用可能なIP(最大)が更新されていないことを確認
     *
     */
    @Test
    public void Subnet_test4() {
        Subnet subnet = new Subnet("192.168.1.1", 24);
        subnet.addReservedIp("192.168.1.1");
        assertEquals("192.168.1.2", subnet.getAvailableMinIp());
        assertEquals("192.168.1.254", subnet.getAvailableMaxIp());
    }

    /**
     * テスト：正常系(予約済みIP≠コンストラクタ取得使用可能なIP(最小)、予約済みIP＝コンストラクタ取得使用可能なIP(最大))
     * 使用可能なIP(最小)が更新されていないことを確認
     * 使用可能なIP(最大)が更新されていることを確認
     *
     */
    @Test
    public void Subnet_test5() {
        Subnet subnet = new Subnet("192.168.1.1", 24);
        subnet.addReservedIp("192.168.1.254");
        assertEquals("192.168.1.1", subnet.getAvailableMinIp());
        assertEquals("192.168.1.253", subnet.getAvailableMaxIp());
    }

    /**
     * テスト：正常系(IPアドレスがサブネットマスクしたIPアドレスの範囲内かつ
     * 予約IPアドレス配列に同一のIPアドレスが存在しない場合かつ予約IPアドレス配列に値がない場合)
     * 境界値MIN
     * チェックOKになることを確認
     *
     */
    @Test
    public void Subnet_test6() {
        Subnet subnet = new Subnet("192.168.1.1", 4);
        assertEquals(true, subnet.isScorp("192.0.0.1"));
    }

    /**
     * テスト：正常系(IPアドレスがサブネットマスクしたIPアドレスの範囲内かつ
     * 予約IPアドレス配列に同一のIPアドレスが存在しない場合かつ予約IPアドレス配列に値がない場合)
     * 境界値MAX
     * チェックOKになることを確認
     *
     */
    @Test
    public void Subnet_test7() {
        Subnet subnet = new Subnet("192.168.1.1", 4);
        assertEquals(true, subnet.isScorp("207.255.255.254"));
    }

    /**
     * テスト：正常系(IPアドレスがサブネットマスクしたIPアドレスの範囲内かつ
     * 予約IPアドレス配列に同一のIPアドレスが存在しない場合)
     * チェックOKになることを確認
     *
     */
    @Test
    public void Subnet_test8() {
        Subnet subnet = new Subnet("192.168.1.1", 24);
        subnet.addReservedIp("192.168.1.11");
        assertEquals(true, subnet.isScorp("192.168.1.12"));
    }

    /**
     * テスト：正常系(第1オクテットがネットワークアドレスの次のIPアドレス[i]＜＝IPアドレス[i]かつ
     * IPアドレス[i]＜＝ブロードキャストアドレスの前のIPアドレス[i]以外の場合)
     * 境界値MIN-1
     * チェックNGになることを確認
     *
     */
    @Test
    public void Subnet_test9() {
        Subnet subnet = new Subnet("192.168.1.1", 4);
        assertEquals(false, subnet.isScorp("191.0.0.1"));
    }

    /**
     * テスト：正常系(第1オクテットがネットワークアドレスの次のIPアドレス[i]＜＝IPアドレス[i]かつ
     * IPアドレス[i]＜＝ブロードキャストアドレスの前のIPアドレス[i]以外の場合)
     * 境界値MAX+1
     * チェックNGになることを確認
     *
     */
    @Test
    public void Subnet_test10() {
        Subnet subnet = new Subnet("192.168.1.1", 4);
        assertEquals(false, subnet.isScorp("208.255.255.254"));
    }

    /**
     * テスト：正常系(第4オクテットがネットワークアドレスの次のIPアドレス[i]＜＝IPアドレス[i]かつ
     * IPアドレス[i]＜＝ブロードキャストアドレスの前のIPアドレス[i]以外の場合)
     * 境界値MIN-1
     * チェックNGになることを確認
     *
     */
    @Test
    public void Subnet_test11() {
        Subnet subnet = new Subnet("192.168.1.1", 4);
        assertEquals(false, subnet.isScorp("192.0.0.0"));
    }

    /**
     * テスト：正常系(第4オクテットがネットワークアドレスの次のIPアドレス[i]＜＝IPアドレス[i]かつ
     * IPアドレス[i]＜＝ブロードキャストアドレスの前のIPアドレス[i]以外の場合)
     * 境界値MAX+1
     * チェックNGになることを確認
     *
     */
    @Test
    public void Subnet_test12() {
        Subnet subnet = new Subnet("192.168.1.1", 4);
        assertEquals(false, subnet.isScorp("207.255.255.255"));
    }

    /**
     * テスト：正常系(IPアドレスがサブネットマスクしたIPアドレスの範囲内かつ
     * 予約IPアドレス配列に同一のIPアドレスが存在した場合)
     * チェックNGになることを確認
     *
     */
    @Test
    public void Subnet_test13() {
        Subnet subnet = new Subnet("192.168.1.1", 24);
        subnet.addReservedIp("192.168.1.11");
        assertEquals(false, subnet.isScorp("192.168.1.11"));
    }

    /**
     * テスト：正常系(サブネットマスク長が1の場合)
     * 境界値MIN
     * 有効アドレス数が「2147483648」になることを確認
     *
     */
    @Test
    public void Subnet_test14() {
        assertEquals(2147483648L, Subnet.countSegment(1));
    }

    /**
     * テスト：正常系(サブネットマスク長が32の場合)
     * 境界値MAX
     * 有効アドレス数が「1」になることを確認
     *
     */
    @Test
    public void Subnet_test15() {
        assertEquals(1L, Subnet.countSegment(32));
    }

    /**
     * テスト：正常系(サブネットマスク長が0の場合)
     * 境界値MIN-1
     * 有効アドレス数が「0」になることを確認
     *
     */
    @Test
    public void Subnet_test16() {
        assertEquals(0, Subnet.countSegment(0));
    }

    /**
     * テスト：正常系(サブネットマスク長が33の場合)
     * 境界値MAX+1
     * 有効アドレス数が「0」になることを確認
     *
     */
    @Test
    public void Subnet_test17() {
        assertEquals(0, Subnet.countSegment(33));
    }

}
