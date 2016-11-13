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
package jp.primecloud.auto.common.component;

/**
 * <p>
 * DNSサーバのレコードを制御するインタフェースです。
 * </p>
 *
 */
public interface DnsStrategyInterface {

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     * @param ipAddress
     */
    public void addForward(String fqdn, String ipAddress);

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     * @param ipAddress
     */
    public void addReverse(String fqdn, String ipAddress);

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     * @param canonicalName
     */
    public void addCanonicalName(String fqdn, String canonicalName);

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     */
    public void deleteForward(String fqdn);

    /**
     * TODO: メソッドコメントを記述
     *
     * @param ipAddress
     */
    public void deleteReverse(String ipAddress);

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     */
    public void deleteCanonicalName(String fqdn);

}
