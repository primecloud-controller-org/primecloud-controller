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
package jp.primecloud.auto.ui.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AzureInstance;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.OpenstackInstance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformVcloud;
import jp.primecloud.auto.entity.crud.VcloudInstance;
import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.BeanItemContainer;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class InstanceParameterContainer extends BeanItemContainer<InstanceParameter> implements Serializable {

    public InstanceParameterContainer(InstanceDto instanceDto) {
        super(InstanceParameter.class);

        Instance instance = instanceDto.getInstance();
        AwsInstance awsInstance = instanceDto.getAwsInstance();
        VmwareInstance vmwareInstance = instanceDto.getVmwareInstance();
        NiftyInstance niftyInstance = instanceDto.getNiftyInstance();
        CloudstackInstance cloudStackInstance = instanceDto.getCloudstackInstance();
        VcloudInstance vcloudInstance = instanceDto.getVcloudInstance();
        AzureInstance azureInstance = instanceDto.getAzureInstance();
        OpenstackInstance openstackInstance = instanceDto.getOpenstackInstance();

        Platform platform = instanceDto.getPlatform().getPlatform();
        String platformName = platform.getPlatformNameDisp();

        Image image = instanceDto.getImage().getImage();
        String imageName = image.getImageNameDisp();

        List<InstanceParameter> parameters = new ArrayList<InstanceParameter>();

        // 共通
        String captionCommon = ViewProperties.getCaption("param.instance.common");
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.name"), instance
                .getInstanceName()));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.hostName"),
                instance.getFqdn()));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.comment"),
                instance.getComment()));

        Boolean showPublicIp = BooleanUtils.toBooleanObject(Config.getProperty("ui.showPublicIp"));
        if (BooleanUtils.isTrue(showPublicIp)) {
            //ui.showPublicIp = true の場合IPアドレスにPublicIp、内部IPにPrivateIpを表示
            parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.ipAddress"),
                    instance.getPublicIp()));
            parameters.add(new InstanceParameter(captionCommon, ViewProperties
                    .getCaption("param.instance.privateIpAddress"), instance.getPrivateIp()));
        } else {
            //ui.showPublicIp = false の場合 IPアドレスにPrivateIp、内部IPにPublicIpを表示
            parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.ipAddress"),
                    instance.getPrivateIp()));
            parameters.add(new InstanceParameter(captionCommon, ViewProperties
                    .getCaption("param.instance.privateIpAddress"), instance.getPublicIp()));
        }

        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.platform"),
                platformName));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.image"),
                imageName));

        // AWS
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType()) && awsInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            String elasticIp = null;
            if (instanceDto.getAwsAddress() != null) {
                elasticIp = instanceDto.getAwsAddress().getPublicIp();
            }

            parameters
                    .add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.elasticIp"), elasticIp));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.dnsName"), awsInstance
                    .getDnsName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.privateDns"),
                    awsInstance.getPrivateDnsName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceId"),
                    awsInstance.getInstanceId()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), awsInstance
                    .getKeyName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"),
                    awsInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.securityGroup"),
                    awsInstance.getSecurityGroups()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.availabilityZone"),
                    awsInstance.getAvailabilityZone()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.subnetId"),
                    awsInstance.getSubnetId()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.rootSize"),
                    ObjectUtils.toString(awsInstance.getRootSize(), "")));
        }
        // Vmware
        else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType()) && vmwareInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            String keyName = null;
            if (instanceDto.getVmwareKeyPair() != null) {
                keyName = instanceDto.getVmwareKeyPair().getKeyName();
            }

            String staticIp = "";
            VmwareAddress vmwareAddress = instanceDto.getVmwareAddress();
            if (vmwareAddress != null && BooleanUtils.isTrue(instanceDto.getVmwareAddress().getEnabled())) {
                staticIp = instanceDto.getVmwareAddress().getIpAddress();
            }

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.staticIp"), staticIp));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.machineName"),
                    vmwareInstance.getMachineName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), keyName));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"),
                    vmwareInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.cluster"),
                    vmwareInstance.getComputeResource()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.resourcePool"),
                    vmwareInstance.getResourcePool()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.datastore"),
                    vmwareInstance.getDatastore()));
        }
        // Nifty
        else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType()) && niftyInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            String keyName = null;
            if (instanceDto.getNiftyKeyPair() != null) {
                keyName = instanceDto.getNiftyKeyPair().getKeyName();
            }

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceId"),
                    niftyInstance.getInstanceId()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), keyName));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"),
                    niftyInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.publicIp"),
                    niftyInstance.getIpAddress()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.privateIp"),
                    niftyInstance.getPrivateIpAddress()));
        }
        // CloudStack
        else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType()) && cloudStackInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            String elasticIp = null;
            if (instanceDto.getCloudstackAddress() != null) {
                elasticIp = instanceDto.getCloudstackAddress().getIpaddress();
            }

            parameters
                    .add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.elasticIp"), elasticIp));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.machineName"),
                    cloudStackInstance.getDisplayname()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceId"),
                    cloudStackInstance.getInstanceId()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"),
                    cloudStackInstance.getKeyName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"),
                    cloudStackInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.securityGroup"),
                    cloudStackInstance.getSecuritygroup()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.zoneId"),
                    cloudStackInstance.getZoneid()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.networkId"),
                    cloudStackInstance.getNetworkid()));
        }
        // VCloud
        else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType()) && vcloudInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();
            PlatformVcloud platformVcloud = instanceDto.getPlatform().getPlatformVcloud();
            List<VcloudInstanceNetwork> vcloudInstanceNetworks = instanceDto.getVcloudInstanceNetworks();

            String keyName = null;
            if (instanceDto.getVcloudKeyPair() != null) {
                keyName = instanceDto.getVcloudKeyPair().getKeyName();
            }
            String storageTypeName = null;
            if (instanceDto.getPlatformVcloudStorageType() != null) {
                storageTypeName = instanceDto.getPlatformVcloudStorageType().getStorageTypeName();
            }
            for (VcloudInstanceNetwork vcloudInstanceNetwork : vcloudInstanceNetworks) {
                String ipStr = vcloudInstanceNetwork.getNetworkName();
                if (StringUtils.isNotEmpty(vcloudInstanceNetwork.getIpAddress())) {
                    ipStr = ipStr + " (IP：" + vcloudInstanceNetwork.getIpAddress() + ")";
                }
                parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.network"), ipStr));
            }
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.machineName"),
                    vcloudInstance.getVmName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), keyName));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"),
                    vcloudInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.organization"),
                    platformVcloud.getOrgName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.vdc"), platformVcloud
                    .getVdcName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.storageType"),
                    storageTypeName));
        }
        // Azure
        else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType()) && azureInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.machineName"),
                    azureInstance.getInstanceName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.affinityGroup"),
                    azureInstance.getAffinityGroupName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.cloudService"),
                    azureInstance.getCloudServiceName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.networkName"),
                    azureInstance.getNetworkName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.storageAccount"),
                    azureInstance.getStorageAccountName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"),
                    azureInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.availabilitySet"),
                    azureInstance.getAvailabilitySet()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.subnetName"),
                    azureInstance.getSubnetId()));
        }
        // OpenStack
        else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            String kind = platform.getPlatformSimplenameDisp();

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.availabilityZone"),
                    openstackInstance.getAvailabilityZone()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"),
                    openstackInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"),
                    openstackInstance.getKeyName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.securityGroup"),
                    openstackInstance.getSecurityGroups()));
        }

        for (InstanceParameter parameter : parameters) {
            addItem(parameter);
        }
    }

}
