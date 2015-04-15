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
package jp.primecloud.auto.ui.mock.service;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.service.VmwareDescribeService;
import com.vmware.vim25.mo.ComputeResource;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockVmwareDescribeService implements VmwareDescribeService {

    @Override
    public List<VmwareKeyPair> getKeyPairs(Long userNo, Long platformNo) {
        List<VmwareKeyPair> vmwareKeyPairs = new ArrayList<VmwareKeyPair>();

        VmwareKeyPair vmwareKeyPair = new VmwareKeyPair();
        vmwareKeyPair.setKeyNo(1L);
        vmwareKeyPair.setPlatformNo(4L);
        vmwareKeyPair.setKeyName("key01");
        vmwareKeyPairs.add(vmwareKeyPair);

        vmwareKeyPair = new VmwareKeyPair();
        vmwareKeyPair.setKeyNo(2L);
        vmwareKeyPair.setPlatformNo(4L);
        vmwareKeyPair.setKeyName("key02");
        vmwareKeyPairs.add(vmwareKeyPair);

        return vmwareKeyPairs;
    }

    @Override
    public List<ComputeResource> getComputeResources(Long platformNo) {
        List<ComputeResource> computeResources = new ArrayList<ComputeResource>();

        computeResources.add(new ComputeResource(null, null) {
            @Override
            public String getName() {
                return "Cluster1";
            }
        });

        computeResources.add(new ComputeResource(null, null) {
            @Override
            public String getName() {
                return "Cluster2";
            }
        });

        return computeResources;
    }

}
