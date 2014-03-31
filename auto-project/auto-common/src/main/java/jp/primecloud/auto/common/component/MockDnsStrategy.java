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

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockDnsStrategy extends DnsStrategy {

    @Override
    public void addForward(String fqdn, String ipAddress) {
    }

    @Override
    public void addReverse(String fqdn, String ipAddress) {
    }

    @Override
    public void addCanonicalName(String fqdn, String canonicalName) {
    }

    @Override
    public void deleteForward(String fqdn) {
    }

    @Override
    public void deleteReverse(String ipAddress) {
    }

    @Override
    public void deleteCanonicalName(String fqdn) {
    }

}
