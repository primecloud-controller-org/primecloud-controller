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

import jp.primecloud.auto.api.util.BeanContext;
import jp.primecloud.auto.dao.crud.ApiCertificateDao;
import jp.primecloud.auto.dao.crud.AutoScalingConfDao;
import jp.primecloud.auto.dao.crud.AwsAddressDao;
import jp.primecloud.auto.dao.crud.AwsCertificateDao;
import jp.primecloud.auto.dao.crud.AwsInstanceDao;
import jp.primecloud.auto.dao.crud.AwsLoadBalancerDao;
import jp.primecloud.auto.dao.crud.AwsSnapshotDao;
import jp.primecloud.auto.dao.crud.AwsSslKeyDao;
import jp.primecloud.auto.dao.crud.AwsVolumeDao;
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
import jp.primecloud.auto.dao.crud.ImageCloudstackDao;
import jp.primecloud.auto.dao.crud.ImageDao;
import jp.primecloud.auto.dao.crud.ImageNiftyDao;
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
import jp.primecloud.auto.dao.crud.PccSystemInfoDao;
import jp.primecloud.auto.dao.crud.PlatformAwsDao;
import jp.primecloud.auto.dao.crud.PlatformCloudstackDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.PlatformNiftyDao;
import jp.primecloud.auto.dao.crud.PlatformVmwareDao;
import jp.primecloud.auto.dao.crud.PlatformVmwareInstanceTypeDao;
import jp.primecloud.auto.dao.crud.ProxyDao;
import jp.primecloud.auto.dao.crud.PuppetInstanceDao;
import jp.primecloud.auto.dao.crud.TemplateComponentDao;
import jp.primecloud.auto.dao.crud.TemplateDao;
import jp.primecloud.auto.dao.crud.TemplateInstanceDao;
import jp.primecloud.auto.dao.crud.UserDao;
import jp.primecloud.auto.dao.crud.VmwareAddressDao;
import jp.primecloud.auto.dao.crud.VmwareDiskDao;
import jp.primecloud.auto.dao.crud.VmwareInstanceDao;
import jp.primecloud.auto.dao.crud.VmwareKeyPairDao;
import jp.primecloud.auto.dao.crud.VmwareNetworkDao;
import jp.primecloud.auto.dao.crud.ZabbixDataDao;
import jp.primecloud.auto.dao.crud.ZabbixInstanceDao;
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
import jp.primecloud.auto.service.VmwareDescribeService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ApiSupport extends ApiConstants {

    protected Log log = LogFactory.getLog(getClass());

    //auto-data
    protected ApiCertificateDao apiCertificateDao = BeanContext.getBean(ApiCertificateDao.class);

    protected AutoScalingConfDao autoScalingConfDao = BeanContext.getBean(AutoScalingConfDao.class);

    protected AwsAddressDao awsAddressDao = BeanContext.getBean(AwsAddressDao.class);

    protected AwsCertificateDao awsCertificateDao = BeanContext.getBean(AwsCertificateDao.class);

    protected AwsInstanceDao awsInstanceDao = BeanContext.getBean(AwsInstanceDao.class);

    protected AwsLoadBalancerDao awsLoadBalancerDao = BeanContext.getBean(AwsLoadBalancerDao.class);

    protected AwsSnapshotDao awsSnapshotDao = BeanContext.getBean(AwsSnapshotDao.class);

    protected AwsVolumeDao awsVolumeDao = BeanContext.getBean(AwsVolumeDao.class);

    protected CloudstackAddressDao cloudstackAddressDao = BeanContext.getBean(CloudstackAddressDao.class);

    protected CloudstackCertificateDao cloudstackCertificateDao = BeanContext.getBean(CloudstackCertificateDao.class);

    protected CloudstackInstanceDao cloudstackInstanceDao = BeanContext.getBean(CloudstackInstanceDao.class);

    protected CloudstackLoadBalancerDao cloudstackLoadBalancerDao = BeanContext.getBean(CloudstackLoadBalancerDao.class);

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

    protected ImageVmwareDao imageVmwareDao = BeanContext.getBean(ImageVmwareDao.class);

    protected InstanceDao instanceDao = BeanContext.getBean(InstanceDao.class);

    protected InstanceConfigDao instanceConfigDao = BeanContext.getBean(InstanceConfigDao.class);

    protected LoadBalancerDao loadBalancerDao = BeanContext.getBean(LoadBalancerDao.class);

    protected LoadBalancerHealthCheckDao loadBalancerHealthCheckDao = BeanContext.getBean(LoadBalancerHealthCheckDao.class);

    protected LoadBalancerInstanceDao loadBalancerInstanceDao = BeanContext.getBean(LoadBalancerInstanceDao.class);

    protected LoadBalancerListenerDao loadBalancerListenerDao = BeanContext.getBean(LoadBalancerListenerDao.class);

    protected NiftyCertificateDao niftyCertificateDao = BeanContext.getBean(NiftyCertificateDao.class);

    protected NiftyInstanceDao niftyInstanceDao = BeanContext.getBean(NiftyInstanceDao.class);

    protected NiftyKeyPairDao niftyKeyPairDao = BeanContext.getBean(NiftyKeyPairDao.class);

    protected PccSystemInfoDao pccSystemInfoDao = BeanContext.getBean(PccSystemInfoDao.class);

    protected PlatformDao platformDao = BeanContext.getBean(PlatformDao.class);

    protected PlatformAwsDao platformAwsDao = BeanContext.getBean(PlatformAwsDao.class);

    protected PlatformCloudstackDao platformCloudstackDao = BeanContext.getBean(PlatformCloudstackDao.class);

    protected PlatformNiftyDao platformNiftyDao = BeanContext.getBean(PlatformNiftyDao.class);

    protected PlatformVmwareDao platformVmwareDao = BeanContext.getBean(PlatformVmwareDao.class);

    protected PlatformVmwareInstanceTypeDao platformVmwareInstanceTypeDao = BeanContext.getBean(PlatformVmwareInstanceTypeDao.class);

    protected ProxyDao proxyDao = BeanContext.getBean(ProxyDao.class);

    protected PuppetInstanceDao puppetInstanceDao = BeanContext.getBean(PuppetInstanceDao.class);

    protected TemplateDao templateDao = BeanContext.getBean(TemplateDao.class);

    protected TemplateComponentDao templateComponentDao = BeanContext.getBean(TemplateComponentDao.class);

    protected TemplateInstanceDao templateInstanceDao = BeanContext.getBean(TemplateInstanceDao.class);

    protected UserDao userDao = BeanContext.getBean(UserDao.class);

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
}