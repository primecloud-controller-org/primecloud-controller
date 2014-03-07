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

import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ProcessLogger extends ServiceSupport {

    public static int LOG_ERR   = 1;
    public static int LOG_WARN  = 2;
    public static int LOG_INFO  = 3;
    public static int LOG_DEBUG = 4;

    protected EventLogger eventLogger;

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    public void writeLogSupport (int logType, Component component, Instance instance, String code, Object[] additions) {

        Long componentNo = null;
        String componentName = null;

        Long instanceNo = null;
        String instanceType = null;
        String instanceName = null;
        Long platformNo = null;

        if (instance != null) {
            instanceNo = instance.getInstanceNo();
            instanceName = instance.getInstanceName();
            platformNo = instance.getPlatformNo();
            instanceType = getInstanceType(instanceNo);
        }

        if (component != null) {
            componentNo = component.getComponentNo();
            componentName = component.getComponentName();
        }

        switch(logType){
            case 1:
                eventLogger.error(componentNo, componentName, instanceNo, instanceName,
                        code, instanceType, platformNo, additions);
                break;
            case 2:
                eventLogger.warn(componentNo, componentName, instanceNo, instanceName,
                        code, instanceType, platformNo, additions);
                break;
            case 3:
                eventLogger.info(componentNo, componentName, instanceNo, instanceName,
                        code, instanceType, platformNo, additions);
                break;

            case 4:
            default:
                eventLogger.debug(componentNo, componentName, instanceNo, instanceName,
                        code, instanceType, platformNo, additions);
                break;
        }
    }

    public String getInstanceType(Long instanceNo) {

        AwsInstance awsInstance =  awsInstanceDao.read(instanceNo);
        if (awsInstance != null){
            return awsInstance.getInstanceType();
        }

        CloudstackInstance csInstance =  cloudstackInstanceDao.read(instanceNo);
        if (csInstance != null){
            return csInstance.getInstanceType();
        }

        VmwareInstance vmInstance =  vmwareInstanceDao.read(instanceNo);
        if (vmInstance != null){
            return vmInstance.getInstanceType();
        }

        NiftyInstance niftyInstance =  niftyInstanceDao.read(instanceNo);
        if (niftyInstance != null){
            return niftyInstance.getInstanceType();
        }

        return null;
    }

}
