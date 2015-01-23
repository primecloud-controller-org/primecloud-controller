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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.VmwareDescribeService;
import jp.primecloud.auto.vmware.VmwareClient;
import jp.primecloud.auto.vmware.VmwareClientFactory;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.ManagedEntity;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareDescribeServiceImpl extends ServiceSupport implements VmwareDescribeService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VmwareKeyPair> getKeyPairs(Long userNo, Long platformNo) {
        List<VmwareKeyPair> vmwareKeyPairs = vmwareKeyPairDao.readByUserNoAndPlatformNo(userNo, platformNo);

        // ソート
        Collections.sort(vmwareKeyPairs, Comparators.COMPARATOR_VMWARE_KEY_PAIR);

        return vmwareKeyPairs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ComputeResource> getComputeResources(Long platformNo) {
        PlatformVmware platformVmware = platformVmwareDao.read(platformNo);

        VmwareClientFactory factory = new VmwareClientFactory();
        factory.setUrl(platformVmware.getUrl());
        factory.setUsername(platformVmware.getUsername());
        factory.setPassword(platformVmware.getPassword());
        factory.setDatacenterName(platformVmware.getDatacenter());

        factory.setIgnoreCert(true);
        VmwareClient vmwareClient = factory.createVmwareClient();

        List<ComputeResource> computeResources = new ArrayList<ComputeResource>();

        ManagedEntity[] entities = vmwareClient.searchByType(ComputeResource.class);
        for (ManagedEntity entity : entities) {
            ComputeResource computeResource = ComputeResource.class.cast(entity);
            if (StringUtils.isNotEmpty(platformVmware.getComputeResource()) &&
                !StringUtils.equals(computeResource.getName(), platformVmware.getComputeResource())) {
                continue;
            }
            computeResources.add(computeResource);
        }

        // ソート
        Collections.sort(computeResources, Comparators.COMPARATOR_COMPUTE_RESOURCE);

        return computeResources;
    }

}
