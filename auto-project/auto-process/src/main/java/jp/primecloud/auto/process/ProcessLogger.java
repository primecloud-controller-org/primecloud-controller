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
package jp.primecloud.auto.process;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AzureInstance;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.OpenstackInstance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.VcloudInstance;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.log.EventLogLevel;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ProcessLogger extends ServiceSupport {

    protected EventLogger eventLogger;

    public void info(Component component, Instance instance, String code, Object[] additions) {
        log(EventLogLevel.INFO, component, instance, code, additions);
    }

    public void debug(Component component, Instance instance, String code, Object[] additions) {
        log(EventLogLevel.DEBUG, component, instance, code, additions);
    }

    protected void log(EventLogLevel level, Component component, Instance instance, String code, Object[] additions) {
        Long componentNo = (component == null) ? null : component.getComponentNo();
        String componentName = (component == null) ? null : component.getComponentName();
        Long instanceNo = (instance == null) ? null : instance.getInstanceNo();
        String instanceName = (instance == null) ? null : instance.getInstanceName();
        Long platformNo = (instance == null) ? null : instance.getPlatformNo();
        String instanceType = (instance == null) ? null : getInstanceType(instanceNo, platformNo);

        eventLogger.log(level, componentNo, componentName, instanceNo, instanceName, code, instanceType, platformNo,
                additions);
    }

    public String getInstanceType(Long instanceNo, Long platformNo) {
        Platform platform = platformDao.read(platformNo);

        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
            if (awsInstance != null) {
                return awsInstance.getInstanceType();
            }
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
            if (vmwareInstance != null) {
                return vmwareInstance.getInstanceType();
            }
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);
            if (niftyInstance != null) {
                return niftyInstance.getInstanceType();
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            CloudstackInstance cloudstackInstance = cloudstackInstanceDao.read(instanceNo);
            if (cloudstackInstance != null) {
                return cloudstackInstance.getInstanceType();
            }
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            VcloudInstance vcloudInstance = vcloudInstanceDao.read(instanceNo);
            if (vcloudInstance != null) {
                return vcloudInstance.getInstanceType();
            }
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            AzureInstance azureInstance = azureInstanceDao.read(instanceNo);
            if (azureInstance != null) {
                return azureInstance.getInstanceType();
            }
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            OpenstackInstance openstackInstance = openstackInstanceDao.read(instanceNo);
            if (openstackInstance != null) {
                return openstackInstance.getInstanceType();
            }
        }

        return null;
    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
