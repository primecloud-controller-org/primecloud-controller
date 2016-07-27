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
package jp.primecloud.auto.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import jp.primecloud.auto.api.util.BeanContext;
import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.dao.crud.ApiCertificateDao;
import jp.primecloud.auto.dao.crud.AutoScalingConfDao;
import jp.primecloud.auto.dao.crud.AwsAddressDao;
import jp.primecloud.auto.dao.crud.AwsCertificateDao;
import jp.primecloud.auto.dao.crud.AwsInstanceDao;
import jp.primecloud.auto.dao.crud.AwsLoadBalancerDao;
import jp.primecloud.auto.dao.crud.AwsSnapshotDao;
import jp.primecloud.auto.dao.crud.AwsSslKeyDao;
import jp.primecloud.auto.dao.crud.AwsVolumeDao;
import jp.primecloud.auto.dao.crud.AzureInstanceDao;
import jp.primecloud.auto.dao.crud.CloudstackAddressDao;
import jp.primecloud.auto.dao.crud.CloudstackCertificateDao;
import jp.primecloud.auto.dao.crud.CloudstackInstanceDao;
import jp.primecloud.auto.dao.crud.CloudstackLoadBalancerDao;
import jp.primecloud.auto.dao.crud.CloudstackSnapshotDao;
import jp.primecloud.auto.dao.crud.CloudstackVolumeDao;
import jp.primecloud.auto.dao.crud.ComponentConfigDao;
import jp.primecloud.auto.dao.crud.ComponentDao;
import jp.primecloud.auto.dao.crud.ComponentInstanceDao;
import jp.primecloud.auto.dao.crud.ComponentLoadBalancerDao;
import jp.primecloud.auto.dao.crud.ComponentTypeDao;
import jp.primecloud.auto.dao.crud.FarmDao;
import jp.primecloud.auto.dao.crud.ImageAwsDao;
import jp.primecloud.auto.dao.crud.ImageAzureDao;
import jp.primecloud.auto.dao.crud.ImageCloudstackDao;
import jp.primecloud.auto.dao.crud.ImageDao;
import jp.primecloud.auto.dao.crud.ImageNiftyDao;
import jp.primecloud.auto.dao.crud.ImageOpenstackDao;
import jp.primecloud.auto.dao.crud.ImageVcloudDao;
import jp.primecloud.auto.dao.crud.ImageVmwareDao;
import jp.primecloud.auto.dao.crud.InstanceConfigDao;
import jp.primecloud.auto.dao.crud.InstanceDao;
import jp.primecloud.auto.dao.crud.LoadBalancerDao;
import jp.primecloud.auto.dao.crud.LoadBalancerHealthCheckDao;
import jp.primecloud.auto.dao.crud.LoadBalancerInstanceDao;
import jp.primecloud.auto.dao.crud.LoadBalancerListenerDao;
import jp.primecloud.auto.dao.crud.NiftyCertificateDao;
import jp.primecloud.auto.dao.crud.NiftyInstanceDao;
import jp.primecloud.auto.dao.crud.NiftyKeyPairDao;
import jp.primecloud.auto.dao.crud.OpenstackInstanceDao;
import jp.primecloud.auto.dao.crud.PccSystemInfoDao;
import jp.primecloud.auto.dao.crud.PlatformAwsDao;
import jp.primecloud.auto.dao.crud.PlatformAzureDao;
import jp.primecloud.auto.dao.crud.PlatformCloudstackDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.PlatformNiftyDao;
import jp.primecloud.auto.dao.crud.PlatformOpenstackDao;
import jp.primecloud.auto.dao.crud.PlatformVcloudDao;
import jp.primecloud.auto.dao.crud.PlatformVcloudInstanceTypeDao;
import jp.primecloud.auto.dao.crud.PlatformVcloudStorageTypeDao;
import jp.primecloud.auto.dao.crud.PlatformVmwareDao;
import jp.primecloud.auto.dao.crud.PlatformVmwareInstanceTypeDao;
import jp.primecloud.auto.dao.crud.ProxyDao;
import jp.primecloud.auto.dao.crud.PuppetInstanceDao;
import jp.primecloud.auto.dao.crud.TemplateComponentDao;
import jp.primecloud.auto.dao.crud.TemplateDao;
import jp.primecloud.auto.dao.crud.TemplateInstanceDao;
import jp.primecloud.auto.dao.crud.UserDao;
import jp.primecloud.auto.dao.crud.VcloudDiskDao;
import jp.primecloud.auto.dao.crud.VcloudInstanceDao;
import jp.primecloud.auto.dao.crud.VcloudInstanceNetworkDao;
import jp.primecloud.auto.dao.crud.VcloudKeyPairDao;
import jp.primecloud.auto.dao.crud.VmwareAddressDao;
import jp.primecloud.auto.dao.crud.VmwareDiskDao;
import jp.primecloud.auto.dao.crud.VmwareInstanceDao;
import jp.primecloud.auto.dao.crud.VmwareKeyPairDao;
import jp.primecloud.auto.dao.crud.VmwareNetworkDao;
import jp.primecloud.auto.dao.crud.ZabbixDataDao;
import jp.primecloud.auto.dao.crud.ZabbixInstanceDao;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.log.service.EventLogService;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.NiftyDescribeService;
import jp.primecloud.auto.service.PlatformService;
import jp.primecloud.auto.service.ProcessService;
import jp.primecloud.auto.service.TemplateService;
import jp.primecloud.auto.service.UserService;
import jp.primecloud.auto.service.VmwareDescribeService;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApiSupport extends ApiConstants {

    //オートスケール用ユーザ
    private static final String AUTO_SCALING_USER = Config.getProperty("autoScaling.username");

    protected Log log = LogFactory.getLog(getClass());

    @Context
    protected HttpServletRequest request;

    protected User checkAndGetUser() {
        User user = (User) request.getAttribute(PARAM_NAME_USER);

        // マスターユーザでない場合、マスターユーザに置き換える
        if (!user.getUserNo().equals(user.getMasterUser())) {
            user = userDao.read(user.getMasterUser());

            LoggingUtils.setUserNo(user.getUserNo());
            LoggingUtils.setUserName(user.getUsername());
        }

        return user;
    }

    protected User checkAndGetUser(Farm farm) {
        User user = checkAndGetUser();

        if (!user.getUserNo().equals(farm.getUserNo())) {
            if (user.getUsername().equals(AUTO_SCALING_USER) || BooleanUtils.isTrue(user.getPowerUser())) {
                // オートスケーリング用ユーザ、またはPOWER USERからのアクセスの場合、ファームに紐付くユーザに置き換える
                user = userDao.read(farm.getUserNo());

                LoggingUtils.setUserNo(user.getUserNo());
                LoggingUtils.setUserName(user.getUsername());
            } else {
                // ファームを操作する権限がない場合
                throw new AutoApplicationException("EAPI-100042", farm.getFarmNo());
            }
        }

        LoggingUtils.setFarmNo(farm.getFarmNo());
        LoggingUtils.setFarmName(farm.getFarmName());

        return user;
    }

    protected User checkAndGetUser(Instance instance) {
        Farm farm = farmDao.read(instance.getFarmNo());

        try {
            User user = checkAndGetUser(farm);

            LoggingUtils.setInstanceNo(instance.getInstanceNo());
            LoggingUtils.setInstanceName(instance.getInstanceName());

            return user;
        } catch (AutoApplicationException e) {
            // インスタンスを操作する権限がない場合
            throw new AutoApplicationException("EAPI-100043", instance.getInstanceNo());
        }
    }

    protected User checkAndGetUser(Component component) {
        Farm farm = farmDao.read(component.getFarmNo());

        try {
            User user = checkAndGetUser(farm);

            LoggingUtils.setComponentNo(component.getComponentNo());
            LoggingUtils.setComponentName(component.getComponentName());

            return user;
        } catch (AutoApplicationException e) {
            // コンポーネントを操作する権限がない場合
            throw new AutoApplicationException("EAPI-100044", component.getComponentNo());
        }
    }

    protected User checkAndGetUser(LoadBalancer loadBalancer) {
        Farm farm = farmDao.read(loadBalancer.getFarmNo());

        try {
            User user = checkAndGetUser(farm);

            return user;
        } catch (AutoApplicationException e) {
            // ロードバランサを操作する権限がない場合
            throw new AutoApplicationException("EAPI-100045", loadBalancer.getLoadBalancerNo());
        }
    }

    protected Farm getFarm(Long farmNo) {
        Farm farm = farmDao.read(farmNo);

        // ファームが存在しない
        if (farm == null) {
            throw new AutoApplicationException("EAPI-100000", "Farm", PARAM_NAME_FARM_NO, farmNo);
        }

        return farm;
    }

    protected Instance getInstance(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // インスタンスが存在しない
        if (instance == null) {
            throw new AutoApplicationException("EAPI-100000", "Instance", PARAM_NAME_INSTANCE_NO, instanceNo);
        }

        // インスタンスがロードバランサ
        if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
            throw new AutoApplicationException("EAPI-100000", "Instance", PARAM_NAME_INSTANCE_NO, instanceNo);
        }

        return instance;
    }

    protected Component getComponent(Long componentNo) {
        Component component = componentDao.read(componentNo);

        // コンポーネントが存在しない
        if (component == null) {
            throw new AutoApplicationException("EAPI-100000", "Component", PARAM_NAME_COMPONENT_NO, componentNo);
        }

        // コンポーネントがロードバランサ
        if (BooleanUtils.isTrue(component.getLoadBalancer())) {
            throw new AutoApplicationException("EAPI-100000", "Component", PARAM_NAME_COMPONENT_NO, componentNo);
        }

        return component;
    }

    protected LoadBalancer getLoadBalancer(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        // ロードバランサが存在しない
        if (loadBalancer == null) {
            throw new AutoApplicationException("EAPI-100000", "LoadBalancer", PARAM_NAME_LOAD_BALANCER_NO,
                    loadBalancerNo);
        }

        return loadBalancer;
    }

    //auto-data
    protected ApiCertificateDao apiCertificateDao = BeanContext.getBean(ApiCertificateDao.class);

    protected AutoScalingConfDao autoScalingConfDao = BeanContext.getBean(AutoScalingConfDao.class);

    protected AwsAddressDao awsAddressDao = BeanContext.getBean(AwsAddressDao.class);

    protected AwsCertificateDao awsCertificateDao = BeanContext.getBean(AwsCertificateDao.class);

    protected AwsInstanceDao awsInstanceDao = BeanContext.getBean(AwsInstanceDao.class);

    protected AwsLoadBalancerDao awsLoadBalancerDao = BeanContext.getBean(AwsLoadBalancerDao.class);

    protected AwsSnapshotDao awsSnapshotDao = BeanContext.getBean(AwsSnapshotDao.class);

    protected AwsVolumeDao awsVolumeDao = BeanContext.getBean(AwsVolumeDao.class);

    protected AzureInstanceDao azureInstanceDao = BeanContext.getBean(AzureInstanceDao.class);

    protected CloudstackAddressDao cloudstackAddressDao = BeanContext.getBean(CloudstackAddressDao.class);

    protected CloudstackCertificateDao cloudstackCertificateDao = BeanContext.getBean(CloudstackCertificateDao.class);

    protected CloudstackInstanceDao cloudstackInstanceDao = BeanContext.getBean(CloudstackInstanceDao.class);

    protected CloudstackLoadBalancerDao cloudstackLoadBalancerDao = BeanContext
            .getBean(CloudstackLoadBalancerDao.class);

    protected CloudstackSnapshotDao cloudstackSnapshotDao = BeanContext.getBean(CloudstackSnapshotDao.class);

    protected CloudstackVolumeDao cloudstackVolumeDao = BeanContext.getBean(CloudstackVolumeDao.class);

    protected ComponentDao componentDao = BeanContext.getBean(ComponentDao.class);

    protected ComponentConfigDao componentConfigDao = BeanContext.getBean(ComponentConfigDao.class);

    protected ComponentInstanceDao componentInstanceDao = BeanContext.getBean(ComponentInstanceDao.class);

    protected ComponentLoadBalancerDao componentLoadBalancerDao = BeanContext.getBean(ComponentLoadBalancerDao.class);

    protected ComponentTypeDao componentTypeDao = BeanContext.getBean(ComponentTypeDao.class);

    protected FarmDao farmDao = BeanContext.getBean(FarmDao.class);

    protected ImageDao imageDao = BeanContext.getBean(ImageDao.class);

    protected ImageAwsDao imageAwsDao = BeanContext.getBean(ImageAwsDao.class);

    protected ImageCloudstackDao imageCloudstackDao = BeanContext.getBean(ImageCloudstackDao.class);

    protected ImageNiftyDao imageNiftyDao = BeanContext.getBean(ImageNiftyDao.class);

    protected ImageVcloudDao imageVcloudDao = BeanContext.getBean(ImageVcloudDao.class);

    protected ImageVmwareDao imageVmwareDao = BeanContext.getBean(ImageVmwareDao.class);

    protected ImageOpenstackDao imageOpenstackDao = BeanContext.getBean(ImageOpenstackDao.class);

    protected ImageAzureDao imageAzureDao = BeanContext.getBean(ImageAzureDao.class);

    protected InstanceDao instanceDao = BeanContext.getBean(InstanceDao.class);

    protected InstanceConfigDao instanceConfigDao = BeanContext.getBean(InstanceConfigDao.class);

    protected LoadBalancerDao loadBalancerDao = BeanContext.getBean(LoadBalancerDao.class);

    protected LoadBalancerHealthCheckDao loadBalancerHealthCheckDao = BeanContext
            .getBean(LoadBalancerHealthCheckDao.class);

    protected LoadBalancerInstanceDao loadBalancerInstanceDao = BeanContext.getBean(LoadBalancerInstanceDao.class);

    protected LoadBalancerListenerDao loadBalancerListenerDao = BeanContext.getBean(LoadBalancerListenerDao.class);

    protected NiftyCertificateDao niftyCertificateDao = BeanContext.getBean(NiftyCertificateDao.class);

    protected NiftyInstanceDao niftyInstanceDao = BeanContext.getBean(NiftyInstanceDao.class);

    protected NiftyKeyPairDao niftyKeyPairDao = BeanContext.getBean(NiftyKeyPairDao.class);

    protected OpenstackInstanceDao openstackInstanceDao = BeanContext.getBean(OpenstackInstanceDao.class);

    protected PccSystemInfoDao pccSystemInfoDao = BeanContext.getBean(PccSystemInfoDao.class);

    protected PlatformDao platformDao = BeanContext.getBean(PlatformDao.class);

    protected PlatformAwsDao platformAwsDao = BeanContext.getBean(PlatformAwsDao.class);

    protected PlatformCloudstackDao platformCloudstackDao = BeanContext.getBean(PlatformCloudstackDao.class);

    protected PlatformNiftyDao platformNiftyDao = BeanContext.getBean(PlatformNiftyDao.class);

    protected PlatformVcloudDao platformVcloudDao = BeanContext.getBean(PlatformVcloudDao.class);

    protected PlatformVcloudInstanceTypeDao platformVcloudInstanceTypeDao = BeanContext
            .getBean(PlatformVcloudInstanceTypeDao.class);

    protected PlatformVcloudStorageTypeDao platformVcloudStorageTypeDao = BeanContext
            .getBean(PlatformVcloudStorageTypeDao.class);

    protected PlatformVmwareDao platformVmwareDao = BeanContext.getBean(PlatformVmwareDao.class);

    protected PlatformVmwareInstanceTypeDao platformVmwareInstanceTypeDao = BeanContext
            .getBean(PlatformVmwareInstanceTypeDao.class);

    protected PlatformOpenstackDao platformOpenstackDao = BeanContext.getBean(PlatformOpenstackDao.class);

    protected PlatformAzureDao platformAzureDao = BeanContext.getBean(PlatformAzureDao.class);

    protected ProxyDao proxyDao = BeanContext.getBean(ProxyDao.class);

    protected PuppetInstanceDao puppetInstanceDao = BeanContext.getBean(PuppetInstanceDao.class);

    protected TemplateDao templateDao = BeanContext.getBean(TemplateDao.class);

    protected TemplateComponentDao templateComponentDao = BeanContext.getBean(TemplateComponentDao.class);

    protected TemplateInstanceDao templateInstanceDao = BeanContext.getBean(TemplateInstanceDao.class);

    protected UserDao userDao = BeanContext.getBean(UserDao.class);

    protected VcloudDiskDao vcloudDiskDao = BeanContext.getBean(VcloudDiskDao.class);

    protected VcloudInstanceDao vcloudInstanceDao = BeanContext.getBean(VcloudInstanceDao.class);

    protected VcloudKeyPairDao vcloudKeyPairDao = BeanContext.getBean(VcloudKeyPairDao.class);

    protected VcloudInstanceNetworkDao vcloudInstanceNetworkDao = BeanContext.getBean(VcloudInstanceNetworkDao.class);

    protected VmwareAddressDao vmwareAddressDao = BeanContext.getBean(VmwareAddressDao.class);

    protected VmwareDiskDao vmwareDiskDao = BeanContext.getBean(VmwareDiskDao.class);

    protected VmwareInstanceDao vmwareInstanceDao = BeanContext.getBean(VmwareInstanceDao.class);

    protected VmwareKeyPairDao vmwareKeyPairDao = BeanContext.getBean(VmwareKeyPairDao.class);

    protected VmwareNetworkDao vmwareNetworkDao = BeanContext.getBean(VmwareNetworkDao.class);

    protected ZabbixDataDao zabbixDataDao = BeanContext.getBean(ZabbixDataDao.class);

    protected ZabbixInstanceDao zabbixInstanceDao = BeanContext.getBean(ZabbixInstanceDao.class);

    protected AwsSslKeyDao awsSslKeyDao = BeanContext.getBean(AwsSslKeyDao.class);

    //auto-service
    protected FarmService farmService = BeanContext.getBean(FarmService.class);

    protected InstanceService instanceService = BeanContext.getBean(InstanceService.class);

    protected ComponentService componentService = BeanContext.getBean(ComponentService.class);

    protected LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);

    protected TemplateService templateService = BeanContext.getBean(TemplateService.class);

    protected IaasDescribeService iaasDescribeService = BeanContext.getBean(IaasDescribeService.class);

    protected VmwareDescribeService vmwareDescribeService = BeanContext.getBean(VmwareDescribeService.class);

    protected NiftyDescribeService niftyDescribeService = BeanContext.getBean(NiftyDescribeService.class);

    protected ProcessService processService = BeanContext.getBean(ProcessService.class);

    protected EventLogService eventLogService = BeanContext.getBean(EventLogService.class);

    protected PlatformService platformService = BeanContext.getBean(PlatformService.class);

    protected UserService userService = BeanContext.getBean(UserService.class);
}