/*
 * Copyright 2014 by SCSK Corporation.
 * ss
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
package jp.primecloud.auto.service;

import java.util.List;

import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.service.dto.DataDiskDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.InstanceNetworkDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.VmwareAddressDto;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public interface InstanceService {

    public InstanceDto getInstance(Long instanceNo);

    public List<InstanceDto> getInstances(Long farmNo);

    public Long createIaasInstance(Long farmNo, String instanceName, Long platformNo, String comment, Long imageNo,
            String instanceType);

    public Long createVmwareInstance(Long farmNo, String instanceName, Long platformNo, String comment, Long imageNo,
            String instanceType);

    public Long createNiftyInstance(Long farmNo, String instanceName, Long platformNo, String comment, Long imageNo,
            String instanceType);

    public void updateAwsInstance(Long instanceNo, String instanceName, String comment, String keyName,
            String instanceType, String securityGroupName, String availabilityZoneName, Long addressNo, String subnetId, String privateIpAddress);

    public void updateCloudstackInstance(Long instanceNo, String instanceName, String comment, String keyName,
            String instanceType, String securityGroupName, String availabilityZoneName, Long addressNo);

    public void updateVcloudInstance(Long instanceNo, String instanceName, String comment, Long storageTypeNo, Long keyPairNo,
            String instanceType, List<InstanceNetworkDto> instanceNetworkDtos);

    public void updateAzureInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String availabilitySet, String subnetId);

    public void updateOpenStackInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String availabilityZoneName, String securityGroupName, String keyName);

    public Long createDataDisk(Long instanceNo, DataDiskDto dataDiskDto);

    public void updateDataDisk(Long instanceNo, DataDiskDto dataDiskDto);

    public void attachDataDisk(Long instanceNo, Long diskNo);

    public void detachDataDisk(Long instanceNo, Long diskNo);

    public void updateVmwareInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String computeResource, String resourcePool, Long keyPairNo);

    public void updateVmwareInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String computeResource, String resourcePool, Long keyPairNo, VmwareAddressDto vmwareAddressDto);

    public void updateNiftyInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            Long keyPairNo);

    public void deleteInstance(Long instanceNo);

    public void associateComponents(Long instanceNo, List<Long> componentNos);

    public List<PlatformDto> getPlatforms(Long userNo);

    public void enableZabbixMonitoring(Long instanceNo);

    public void disableZabbixMonitoring(Long instanceNo);

    public InstanceStatus getInstanceStatus(Instance instance);
}
