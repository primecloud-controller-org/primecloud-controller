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
package jp.primecloud.auto.process.aws;

import jp.primecloud.auto.util.MessageUtils;

import jp.primecloud.auto.process.DnsProcess;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockAwsDnsProcess extends DnsProcess {

    @Override
    protected void addForward(String publicIp, String fqdn) {
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100141", fqdn, publicIp));
        }
    }

    @Override
    protected void addReverse(String publicIp, String fqdn) {
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100142", publicIp, fqdn));
        }
    }

    @Override
    protected void addCanonicalName(String fqdn, String canonicalName) {
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100145", fqdn, canonicalName));
        }
    }

    @Override
    protected void deleteForward(String fqdn) {
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100143", fqdn));
        }
    }

    @Override
    protected void deleteReverse(String publicIp) {
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100144", publicIp));
        }
    }

    @Override
    protected void deleteCanonicalName(String fqdn) {
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100146", fqdn));
        }
    }

}
