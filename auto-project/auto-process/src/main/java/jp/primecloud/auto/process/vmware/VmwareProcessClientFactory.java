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
package jp.primecloud.auto.process.vmware;

import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.PlatformVmwareDao;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.vmware.VmwareClient;
import jp.primecloud.auto.vmware.VmwareClientFactory;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareProcessClientFactory {

    protected PlatformDao platformDao;

    protected PlatformVmwareDao platformVmwareDao;

    public VmwareProcessClient createVmwareProcessClient(Long platformNo) {
        Platform platform = platformDao.read(platformNo);
        if (platform == null) {
            throw new AutoException("EPROCESS-000004", platformNo);
        }

        return createVmwareProcessClient(platform);
    }

    protected VmwareProcessClient createVmwareProcessClient(Platform platform) {
        PlatformVmware platformVmware = platformVmwareDao.read(platform.getPlatformNo());
        if ("vmware".equals(platform.getPlatformType()) == false || platformVmware == null) {
            throw new AutoException("EPROCESS-000007", platform.getPlatformNo());
        }

        VmwareClient vmwareClient = createVmwareClient(platformVmware);

        return new VmwareProcessClient(vmwareClient, platform.getPlatformNo());
    }

    protected VmwareClient createVmwareClient(PlatformVmware platformVmware) {
        VmwareClientFactory factory = new VmwareClientFactory();
        factory.setUrl(platformVmware.getUrl());
        factory.setUsername(platformVmware.getUsername());
        factory.setPassword(platformVmware.getPassword());
        factory.setDatacenterName(platformVmware.getDatacenter());
        factory.setIgnoreCert(true);
        return factory.createVmwareClient();
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
     * platformVmwareDaoを設定します。
     *
     * @param platformVmwareDao platformVmwareDao
     */
    public void setPlatformVmwareDao(PlatformVmwareDao platformVmwareDao) {
        this.platformVmwareDao = platformVmwareDao;
    }
}
