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
package jp.primecloud.auto.service.impl;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.PlatformService;
import jp.primecloud.auto.service.ServiceSupport;

/**
 * <p>
 * PlatformServiceImplインターフェースの実装クラス
 * </p>
 *
 */
public class PlatformServiceImpl extends ServiceSupport implements PlatformService {

    protected EventLogger eventLogger;

    @Override
    public boolean isUsablePlatform(Long userNo, Platform platform) {
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            // AWS認証情報のチェック
            if (awsCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            // CloudStack認証情報のチェック
            if (cloudstackCertificateDao.countByAccountAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            // VMWareキーペアのチェック
            if (vmwareKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            // Nifty認証情報のチェック
            if (niftyCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
            // Niftyキーペアのチェック
            if (niftyKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            // VCloud認証情報のチェック
            if (vcloudCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
            // VCloudキーペアのチェック
            if (vcloudKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            // Azure認証情報のチェック
            if (azureCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            // OpenStack認証情報のチェック
            if (openstackCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public boolean isUseablePlatforms(Long userNo, Platform platform) {
        return isUsablePlatform(userNo, platform);
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
