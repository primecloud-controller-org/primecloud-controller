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
package jp.primecloud.auto.iaasgw;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.dao.crud.AwsCertificateDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;

public class IaasGatewayFactory {

    protected AwsCertificateDao awsCertificateDao;

    protected PlatformDao platformDao;

    protected EventLogger eventLogger;

    public IaasGatewayWrapper createIaasGateway(Long userNo, Long platformNo) {
        Platform platform = platformDao.read(platformNo);
        if (platform == null) {
            throw new AutoException("EPROCESS-000004", platformNo);
        }

        if (!PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())
                && !PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())
                && !PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())
                && !PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())
                && !PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            throw new AutoException("EPROCESS-000005", platform.getPlatformNo());
        }

        return new IaasGatewayWrapper(userNo, platform.getPlatformNo(), eventLogger);
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    /**
     * awsCertificateDaoを設定します。
     *
     * @param awsCertificateDao awsCertificateDao
     */
    public void setAwsCertificateDao(AwsCertificateDao awsCertificateDao) {
        this.awsCertificateDao = awsCertificateDao;
    }

    /**
     * platformDaoを設定します。
     *
     * @param platformDao platformDao
     */
    public void setPlatformDao(PlatformDao platformDao) {
        this.platformDao = platformDao;
    }
}
