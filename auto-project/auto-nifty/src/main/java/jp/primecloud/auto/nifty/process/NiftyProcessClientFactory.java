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
package jp.primecloud.auto.nifty.process;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.dao.crud.NiftyCertificateDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.PlatformNiftyDao;
import jp.primecloud.auto.entity.crud.NiftyCertificate;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformNifty;
import jp.primecloud.auto.exception.AutoException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.nifty.cloud.sdk.ClientConfiguration;
import com.nifty.cloud.sdk.auth.BasicCredentials;
import com.nifty.cloud.sdk.auth.Credentials;
import com.nifty.cloud.sdk.disk.NiftyDiskClient;
import com.nifty.cloud.sdk.server.NiftyServerClient;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyProcessClientFactory {

    protected NiftyCertificateDao niftyCertificateDao;

    protected PlatformNiftyDao platformNiftyDao;

    protected PlatformDao platformDao;

    public NiftyProcessClient createNiftyProcessClient(Long userNo, Long platformNo, String clientType) {
        Platform platform = platformDao.read(platformNo);
        if (platform == null) {
            throw new AutoException("EPROCESS-000004", platformNo);
        }

        return createNiftyProcessClient(userNo, platform, clientType);
    }

    protected NiftyProcessClient createNiftyProcessClient(Long userNo, Platform platform, String clientType) {
        if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType()) == false) {
            throw new AutoException("EPROCESS-000008", platform.getPlatformNo());
        }

        // NiftyCertificateを取得
        NiftyCertificate niftyCertificate = niftyCertificateDao.read(userNo, platform.getPlatformNo());
        if (niftyCertificate == null) {
            throw new AutoException("EPROCESS-000009", userNo, platform.getPlatformNo());
        }

        // 指定されたAccessKeyとSecretAccessKeyから認証情報インスタンスを生成します。
        Credentials credential = new BasicCredentials(niftyCertificate.getNiftyAccessId(),
                niftyCertificate.getNiftySecretKey());

        // 設定ファイル
        ClientConfiguration config = new ClientConfiguration();

        PlatformNifty niftyPlatform = platformNiftyDao.read(platform.getPlatformNo());
        String endpoint = niftyPlatform.getUrl();
        if (StringUtils.isNotBlank(endpoint)) {
            config.setConfigEndpoint(endpoint);
        }

        NiftyProcessClient niftyProcessClient = null;
        if (PCCConstant.NIFTYCLIENT_TYPE_SERVER.equals(clientType)) {
            NiftyServerClient niftyServerClient = new NiftyServerClient(credential, config);
            niftyProcessClient = new NiftyProcessClient(niftyServerClient, platform.getPlatformNo());
        } else if (PCCConstant.NIFTYCLIENT_TYPE_DISK.equals(clientType)) {
            NiftyDiskClient niftyDiskClient = new NiftyDiskClient(credential, config);
            niftyProcessClient = new NiftyProcessClient(niftyDiskClient, platform.getPlatformNo());
        } else {
            return null;
        }

        // describeInterval
        String describeInterval = Config.getProperty("aws.describeInterval");
        niftyProcessClient.setDescribeInterval(NumberUtils.toInt(describeInterval, 15));

        return niftyProcessClient;
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
     * platformNiftyDaoを設定します。
     *
     * @param platformNiftyDao platformNiftyDao
     */
    public void setPlatformNiftyDao(PlatformNiftyDao platformNiftyDao) {
        this.platformNiftyDao = platformNiftyDao;
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
