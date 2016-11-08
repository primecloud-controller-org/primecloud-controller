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

import java.util.Comparator;

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.PlatformVcloudStorageType;
import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;
import jp.primecloud.auto.entity.crud.VcloudKeyPair;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.ComponentTypeDto;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.vmware.vim25.mo.ComputeResource;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class Comparators {

    public static final Comparator<FarmDto> COMPARATOR_FARM_DTO;

    public static final Comparator<ComponentDto> COMPARATOR_COMPONENT_DTO;

    public static final Comparator<Component> COMPARATOR_COMPONENT;

    public static final Comparator<InstanceDto> COMPARATOR_INSTANCE_DTO;

    public static final Comparator<Instance> COMPARATOR_INSTANCE;

    public static final Comparator<ComponentTypeDto> COMPARATOR_COMPONENT_TYPE_DTO;

    public static final Comparator<ComponentInstance> COMPARATOR_COMPONENT_INSTANCE;

    public static final Comparator<ComponentInstanceDto> COMPARATOR_COMPONENT_INSTANCE_DTO;

    public static final Comparator<ComputeResource> COMPARATOR_COMPUTE_RESOURCE;

    public static final Comparator<VmwareKeyPair> COMPARATOR_VMWARE_KEY_PAIR;

    public static final Comparator<NiftyKeyPair> COMPARATOR_NIFTY_KEY_PAIR;

    public static final Comparator<VcloudKeyPair> COMPARATOR_VCLOUD_KEY_PAIR;

    public static final Comparator<LoadBalancerDto> COMPARATOR_LOAD_BALANCER_DTO;

    public static final Comparator<LoadBalancer> COMPARATOR_LOAD_BALANCER;

    public static final Comparator<LoadBalancerListener> COMPARATOR_LOAD_BALANCER_LISTENER;

    public static final Comparator<LoadBalancerInstance> COMPARATOR_LOAD_BALANCER_INSTANCE;

    public static final Comparator<VcloudInstanceNetwork> COMPARATOR_VCLOUD_INSTANCE_NETWORK;

    public static final Comparator<PlatformVcloudStorageType> COMPARATOR_PLATFORM_VCLOUD_STORAGE_TYPE;

    public static final Comparator<AvailabilityZone> COMPARATOR_AVAILABILITY_ZONE;

    public static final Comparator<KeyPairInfo> COMPARATOR_KEY_PAIR_INFO;

    public static final Comparator<SecurityGroup> COMPARATOR_SECURITY_GROUP;

    public static final Comparator<Subnet> COMPARATOR_SUBNET;

    public static final Comparator<AwsAddress> COMPARATOR_AWS_ADDRESS;

    static {
        COMPARATOR_FARM_DTO = new Comparator<FarmDto>() {
            @Override
            public int compare(FarmDto o1, FarmDto o2) {
                long diff = o1.getFarm().getFarmNo() - o2.getFarm().getFarmNo();
                if (diff == 0) {
                    return 0;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_COMPONENT_DTO = new Comparator<ComponentDto>() {
            @Override
            public int compare(ComponentDto o1, ComponentDto o2) {
                long diff = o1.getComponent().getComponentNo() - o2.getComponent().getComponentNo();
                if (diff == 0) {
                    return 0;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_COMPONENT = new Comparator<Component>() {
            @Override
            public int compare(Component o1, Component o2) {
                long diff = o1.getComponentNo() - o2.getComponentNo();
                if (diff == 0) {
                    return 0;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_INSTANCE_DTO = new Comparator<InstanceDto>() {
            @Override
            public int compare(InstanceDto o1, InstanceDto o2) {
                long diff = o1.getInstance().getInstanceNo() - o2.getInstance().getInstanceNo();
                if (diff == 0) {
                    return 0;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_INSTANCE = new Comparator<Instance>() {
            @Override
            public int compare(Instance o1, Instance o2) {
                long diff = o1.getInstanceNo() - o2.getInstanceNo();
                if (diff == 0) {
                    return 0;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_COMPONENT_TYPE_DTO = new Comparator<ComponentTypeDto>() {
            @Override
            public int compare(ComponentTypeDto o1, ComponentTypeDto o2) {
                long diff = o1.getComponentType().getComponentTypeNo() - o2.getComponentType().getComponentTypeNo();
                if (diff == 0) {
                    return 0;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_COMPONENT_INSTANCE = new Comparator<ComponentInstance>() {
            @Override
            public int compare(ComponentInstance o1, ComponentInstance o2) {
                long diff = o1.getComponentNo() - o2.getComponentNo();
                if (diff == 0) {
                    diff = o1.getInstanceNo() - o2.getInstanceNo();
                    if (diff == 0) {
                        return 0;
                    }
                    return diff > 0 ? 1 : -1;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_COMPONENT_INSTANCE_DTO = new Comparator<ComponentInstanceDto>() {
            @Override
            public int compare(ComponentInstanceDto o1, ComponentInstanceDto o2) {
                long diff = o1.getComponentInstance().getComponentNo() - o2.getComponentInstance().getComponentNo();
                if (diff == 0) {
                    diff = o1.getComponentInstance().getInstanceNo() - o2.getComponentInstance().getInstanceNo();
                    if (diff == 0) {
                        return 0;
                    }
                    return diff > 0 ? 1 : -1;
                }
                return diff > 0 ? 1 : -1;
            }
        };

        COMPARATOR_COMPUTE_RESOURCE = new Comparator<ComputeResource>() {
            @Override
            public int compare(ComputeResource o1, ComputeResource o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        COMPARATOR_VMWARE_KEY_PAIR = new Comparator<VmwareKeyPair>() {
            @Override
            public int compare(VmwareKeyPair o1, VmwareKeyPair o2) {
                return o1.getKeyNo().compareTo(o2.getKeyNo());
            }
        };

        COMPARATOR_NIFTY_KEY_PAIR = new Comparator<NiftyKeyPair>() {
            @Override
            public int compare(NiftyKeyPair o1, NiftyKeyPair o2) {
                return o1.getKeyNo().compareTo(o2.getKeyNo());
            }
        };

        COMPARATOR_VCLOUD_KEY_PAIR = new Comparator<VcloudKeyPair>() {
            @Override
            public int compare(VcloudKeyPair o1, VcloudKeyPair o2) {
                return o1.getKeyNo().compareTo(o2.getKeyNo());
            }
        };

        COMPARATOR_LOAD_BALANCER_DTO = new Comparator<LoadBalancerDto>() {
            @Override
            public int compare(LoadBalancerDto o1, LoadBalancerDto o2) {
                return o1.getLoadBalancer().getLoadBalancerNo().compareTo(o2.getLoadBalancer().getLoadBalancerNo());
            }
        };

        COMPARATOR_LOAD_BALANCER = new Comparator<LoadBalancer>() {
            @Override
            public int compare(LoadBalancer o1, LoadBalancer o2) {
                return o1.getLoadBalancerNo().compareTo(o2.getLoadBalancerNo());
            }
        };

        COMPARATOR_LOAD_BALANCER_LISTENER = new Comparator<LoadBalancerListener>() {
            @Override
            public int compare(LoadBalancerListener o1, LoadBalancerListener o2) {
                int comp = o1.getLoadBalancerNo().compareTo(o2.getLoadBalancerNo());
                if (comp == 0) {
                    comp = o1.getLoadBalancerPort().compareTo(o2.getLoadBalancerPort());
                }
                return comp;
            }
        };

        COMPARATOR_LOAD_BALANCER_INSTANCE = new Comparator<LoadBalancerInstance>() {
            @Override
            public int compare(LoadBalancerInstance o1, LoadBalancerInstance o2) {
                int comp = o1.getLoadBalancerNo().compareTo(o2.getLoadBalancerNo());
                if (comp == 0) {
                    comp = o1.getInstanceNo().compareTo(o2.getInstanceNo());
                }
                return comp;
            }
        };

        COMPARATOR_VCLOUD_INSTANCE_NETWORK = new Comparator<VcloudInstanceNetwork>() {
            @Override
            public int compare(VcloudInstanceNetwork o1, VcloudInstanceNetwork o2) {
                if (o1.getNetworkIndex() == null && o2.getNetworkIndex() == null) {
                    return o1.getNetworkNo().compareTo(o2.getNetworkNo());
                } else if (o1.getNetworkIndex() != null && o2.getNetworkIndex() == null) {
                    return -1;
                } else if (o1.getNetworkIndex() == null && o2.getNetworkIndex() != null) {
                    return 1;
                }
                return o1.getNetworkIndex().compareTo(o2.getNetworkIndex());
            }
        };

        COMPARATOR_PLATFORM_VCLOUD_STORAGE_TYPE = new Comparator<PlatformVcloudStorageType>() {
            @Override
            public int compare(PlatformVcloudStorageType o1, PlatformVcloudStorageType o2) {
                if (!o1.getPlatformNo().equals(o2.getPlatformNo())) {
                    return o1.getPlatformNo().compareTo(o2.getPlatformNo());
                } else {
                    return o1.getStorageTypeNo().compareTo(o2.getStorageTypeNo());
                }
            }
        };

        COMPARATOR_AVAILABILITY_ZONE = new Comparator<AvailabilityZone>() {
            @Override
            public int compare(AvailabilityZone o1, AvailabilityZone o2) {
                return o1.getZoneName().compareTo(o2.getZoneName());
            }
        };

        COMPARATOR_KEY_PAIR_INFO = new Comparator<KeyPairInfo>() {
            @Override
            public int compare(KeyPairInfo o1, KeyPairInfo o2) {
                return o1.getKeyName().compareTo(o2.getKeyName());
            }
        };

        COMPARATOR_SECURITY_GROUP = new Comparator<SecurityGroup>() {
            @Override
            public int compare(SecurityGroup o1, SecurityGroup o2) {
                return o1.getGroupName().compareTo(o2.getGroupName());
            }
        };

        COMPARATOR_SUBNET = new Comparator<Subnet>() {
            @Override
            public int compare(Subnet o1, Subnet o2) {
                long diff = toLong(o1.getCidrBlock()) - toLong(o2.getCidrBlock());
                return diff == 0 ? 0 : (diff > 0 ? 1 : -1);
            }

            private long toLong(String cidrBlock) {
                String[] array = StringUtils.split(StringUtils.split(cidrBlock, "/")[0], ".");
                return Long.parseLong(array[0]) * 256 * 256 * 256 + Long.parseLong(array[1]) * 256 * 256
                        + Long.parseLong(array[2]) * 256 + Long.parseLong(array[3]);
            }
        };

        COMPARATOR_AWS_ADDRESS = new Comparator<AwsAddress>() {
            @Override
            public int compare(AwsAddress o1, AwsAddress o2) {
                long diff = o1.getAddressNo() - o2.getAddressNo();
                if (diff == 0) {
                    return 0;
                }
                return diff > 0 ? 1 : -1;
            }
        };

    }

}
