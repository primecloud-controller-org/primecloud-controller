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

import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;

import com.vaadin.data.util.BeanItemContainer;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class InstanceParameterContainer extends BeanItemContainer<InstanceParameter> implements Serializable {

    /**
     * Natural property order for Farm bean. Used in tables and forms.
     */
    public static final Object[] NATURAL_COL_ORDER = new Object[] { "kind", "name", "value" };

    /**
     * "Human readable" captions for properties in same order as in
     * NATURAL_COL_ORDER.
     */
    public static final String[] COL_HEADERS_ENGLISH = new String[] { "kind", "name", "value" };

    public InstanceParameterContainer(InstanceDto instanceDto) {
        super(InstanceParameter.class);

        Instance instance = instanceDto.getInstance();
        AwsInstance awsInstance = instanceDto.getAwsInstance();
        VmwareInstance vmwareInstance = instanceDto.getVmwareInstance();
        NiftyInstance niftyInstance = instanceDto.getNiftyInstance();
        CloudstackInstance cloudStackInstance = instanceDto.getCloudstackInstance();

        Platform platform = instanceDto.getPlatform().getPlatform();
        String platformName = platform.getPlatformNameDisp();

        Image image = instanceDto.getImage().getImage();
        String imageName = image.getImageNameDisp();

        List<InstanceParameter> parameters = new ArrayList<InstanceParameter>();

        // 共通
        String captionCommon = ViewProperties.getCaption("param.instance.common");
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.name"), instance.getInstanceName()));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.hostName"), instance.getFqdn()));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.comment"), instance.getComment()));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.ipAddress"), instance.getPublicIp()));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.privateIpAddress"), instance.getPrivateIp()));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.platform"), platformName));
        parameters.add(new InstanceParameter(captionCommon, ViewProperties.getCaption("param.instance.image"), imageName));

        // Eucalyptus/EC2
        if ("aws".equals(platform.getPlatformType()) && awsInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            String elasticIp = null;
            if (instanceDto.getAwsAddress() != null) {
                elasticIp = instanceDto.getAwsAddress().getPublicIp();
            }

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.elasticIp"), elasticIp));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.dnsName"), awsInstance.getDnsName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.privateDns"), awsInstance.getPrivateDnsName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceId"), awsInstance.getInstanceId()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), awsInstance.getKeyName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"), awsInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.securityGroup"), awsInstance.getSecurityGroups()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.availabilityZone"), awsInstance.getAvailabilityZone()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.subnetId"), awsInstance.getSubnetId()));
        }
        // Vmware
        else if ("vmware".equals(platform.getPlatformType()) && vmwareInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            String keyName = null;
            if (instanceDto.getVmwareKeyPair() != null) {
                keyName = instanceDto.getVmwareKeyPair().getKeyName();
            }

            String staticIp = "";
            VmwareAddress vmwareAddress = instanceDto.getVmwareAddress();
            if (vmwareAddress != null && BooleanUtils.isTrue(instanceDto.getVmwareAddress().getEnabled())){
               staticIp = instanceDto.getVmwareAddress().getIpAddress();
            }

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.staticIp"), staticIp));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.machineName"), vmwareInstance.getMachineName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), keyName));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"), vmwareInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.cluster"), vmwareInstance.getComputeResource()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.resourcePool"), vmwareInstance.getResourcePool()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.datastore"), vmwareInstance.getDatastore()));
        }
        // Nifty
        else if ("nifty".equals(platform.getPlatformType()) && niftyInstance != null) {
            String kind = platform.getPlatformSimplenameDisp();

            String keyName = null;
            if (instanceDto.getNiftyKeyPair() != null) {
                keyName = instanceDto.getNiftyKeyPair().getKeyName();
            }

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceId"), niftyInstance.getInstanceId()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), keyName));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"), niftyInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.publicIp"), niftyInstance.getIpAddress()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.privateIp"), niftyInstance.getPrivateIpAddress()));
        }
        // CloudStack
        else if ("cloudstack".equals(platform.getPlatformType()) && cloudStackInstance != null){
            String kind = platform.getPlatformSimplenameDisp();

            String elasticIp = null;
            if (instanceDto.getCloudstackAddress() != null) {
                elasticIp = instanceDto.getCloudstackAddress().getIpaddress();
            }

            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.elasticIp"), elasticIp));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.machineName"), cloudStackInstance.getDisplayname()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceId"), cloudStackInstance.getInstanceId()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.keyPair"), cloudStackInstance.getKeyName()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.instanceType"), cloudStackInstance.getInstanceType()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.securityGroup"), cloudStackInstance.getSecuritygroup()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.zoneId"), cloudStackInstance.getZoneid()));
            parameters.add(new InstanceParameter(kind, ViewProperties.getCaption("param.instance.networkId"), cloudStackInstance.getNetworkid()));

        }

        for (InstanceParameter parameter : parameters) {
            addItem(parameter);
        }
    }

}
