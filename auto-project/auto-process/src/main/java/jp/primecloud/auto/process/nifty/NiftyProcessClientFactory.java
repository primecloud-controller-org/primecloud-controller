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
package jp.primecloud.auto.process.nifty;

import jp.primecloud.auto.dao.crud.NiftyCertificateDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.PlatformNiftyDao;
import jp.primecloud.auto.entity.crud.NiftyCertificate;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformNifty;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.nifty.soap.NiftyCloudPortTypeFactory;
import jp.primecloud.auto.nifty.soap.jaxws.NiftyCloudPortType;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyProcessClientFactory {

    protected Integer describeInterval;

    protected Long apiTimeout;

    protected NiftyCertificateDao niftyCertificateDao;

    protected PlatformDao platformDao;

    protected PlatformNiftyDao platformNiftyDao;


    public NiftyProcessClient createNiftyProcessClient(Long userNo, Long platformNo) {
        Platform platform =platformDao.read(platformNo);
        if (platform == null) {
            throw new AutoException("EPROCESS-000004", platformNo);
        }

        return createNiftyProcessClient(userNo, platform);
    }

    protected NiftyProcessClient createNiftyProcessClient(Long userNo, Platform platform) {
        PlatformNifty platformNifty = platformNiftyDao.read(platform.getPlatformNo());
        if ("nifty".equals(platform.getPlatformType()) == false || platformNifty == null) {
            throw new AutoException("EPROCESS-000008", platform.getPlatformNo());
        }

        // NiftyCertificateを取得
        NiftyCertificate niftyCertificate = niftyCertificateDao.read(userNo, platform.getPlatformNo());
        if (niftyCertificate == null) {
            throw new AutoException("EPROCESS-000009", userNo, platform.getPlatformNo());
        }

        NiftyCloudPortType niftyCloud = createNiftyCloudPortType(platformNifty, niftyCertificate);

        return new NiftyProcessClient(niftyCloud, platform.getPlatformNo(), describeInterval);
    }

    protected NiftyCloudPortType createNiftyCloudPortType(PlatformNifty nifty, NiftyCertificate niftyCertificate) {
        NiftyCloudPortTypeFactory factory = new NiftyCloudPortTypeFactory();
        factory.setWsdlDocumentUrl(nifty.getWsdl());
        factory.setTimeout(apiTimeout);
        return factory.createNiftyCloudPortType(niftyCertificate.getCertificate(), niftyCertificate.getPrivateKey());
    }

    /**
     * describeIntervalを設定します。
     *
     * @param describeInterval describeInterval
     */
    public void setDescribeInterval(Integer describeInterval) {
        this.describeInterval = describeInterval;
    }

    /**
     * apiTimeoutを設定します。
     *
     * @param apiTimeout apiTimeout
     */
    public void setApiTimeout(Long apiTimeout) {
        this.apiTimeout = apiTimeout;
    }

    /**
     * niftyCertificateDaoを設定します。
     *
     * @param niftyCertificateDao niftyCertificateDao
     */
    public void setNiftyCertificateDao(NiftyCertificateDao niftyCertificateDao) {
        this.niftyCertificateDao = niftyCertificateDao;
    }

    /**
     * platformDaoを設定します。
     *
     * @param platformDao platformDao
     */
    public void setPlatformDao(PlatformDao platformDao) {
        this.platformDao = platformDao;
    }

    /**
     * platformNiftyDaoを設定します。
     *
     * @param platformNiftyDao platformNiftyDao
     */
    public void setPlatformNiftyDao(PlatformNiftyDao platformNiftyDao) {
        this.platformNiftyDao = platformNiftyDao;
    }
}
