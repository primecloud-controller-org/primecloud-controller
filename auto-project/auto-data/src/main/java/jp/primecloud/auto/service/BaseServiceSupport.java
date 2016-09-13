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
package jp.primecloud.auto.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.dao.crud.ApiCertificateDao;
import jp.primecloud.auto.dao.crud.AuthoritySetDao;
import jp.primecloud.auto.dao.crud.AutoScalingConfDao;
import jp.primecloud.auto.dao.crud.AwsAddressDao;
import jp.primecloud.auto.dao.crud.AwsCertificateDao;
import jp.primecloud.auto.dao.crud.AwsInstanceDao;
import jp.primecloud.auto.dao.crud.AwsLoadBalancerDao;
import jp.primecloud.auto.dao.crud.AwsSnapshotDao;
import jp.primecloud.auto.dao.crud.AwsSslKeyDao;
import jp.primecloud.auto.dao.crud.AwsVolumeDao;
import jp.primecloud.auto.dao.crud.AzureCertificateDao;
import jp.primecloud.auto.dao.crud.AzureDiskDao;
import jp.primecloud.auto.dao.crud.AzureInstanceDao;
import jp.primecloud.auto.dao.crud.AzureSubnetDao;
import jp.primecloud.auto.dao.crud.CloudstackAddressDao;
import jp.primecloud.auto.dao.crud.CloudstackCertificateDao;
import jp.primecloud.auto.dao.crud.CloudstackInstanceDao;
import jp.primecloud.auto.dao.crud.CloudstackLoadBalancerDao;
import jp.primecloud.auto.dao.crud.CloudstackSnapshotDao;
import jp.primecloud.auto.dao.crud.CloudstackVolumeDao;
import jp.primecloud.auto.dao.crud.ComponentDao;
import jp.primecloud.auto.dao.crud.ComponentConfigDao;
import jp.primecloud.auto.dao.crud.ComponentInstanceDao;
import jp.primecloud.auto.dao.crud.ComponentLoadBalancerDao;
import jp.primecloud.auto.dao.crud.ComponentTypeDao;
import jp.primecloud.auto.dao.crud.FarmDao;
import jp.primecloud.auto.dao.crud.IaasInfoDao;
import jp.primecloud.auto.dao.crud.ImageDao;
import jp.primecloud.auto.dao.crud.ImageAwsDao;
import jp.primecloud.auto.dao.crud.ImageAzureDao;
import jp.primecloud.auto.dao.crud.ImageCloudstackDao;
import jp.primecloud.auto.dao.crud.ImageNiftyDao;
import jp.primecloud.auto.dao.crud.ImageOpenstackDao;
import jp.primecloud.auto.dao.crud.ImageVcloudDao;
import jp.primecloud.auto.dao.crud.ImageVmwareDao;
import jp.primecloud.auto.dao.crud.InstanceDao;
import jp.primecloud.auto.dao.crud.InstanceConfigDao;
import jp.primecloud.auto.dao.crud.LoadBalancerDao;
import jp.primecloud.auto.dao.crud.LoadBalancerHealthCheckDao;
import jp.primecloud.auto.dao.crud.LoadBalancerInstanceDao;
import jp.primecloud.auto.dao.crud.LoadBalancerListenerDao;
import jp.primecloud.auto.dao.crud.NiftyCertificateDao;
import jp.primecloud.auto.dao.crud.NiftyInstanceDao;
import jp.primecloud.auto.dao.crud.NiftyKeyPairDao;
import jp.primecloud.auto.dao.crud.NiftyVolumeDao;
import jp.primecloud.auto.dao.crud.OpenstackCertificateDao;
import jp.primecloud.auto.dao.crud.OpenstackInstanceDao;
import jp.primecloud.auto.dao.crud.OpenstackSslKeyDao;
import jp.primecloud.auto.dao.crud.OpenstackVolumeDao;
import jp.primecloud.auto.dao.crud.PccSystemInfoDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.PlatformAwsDao;
import jp.primecloud.auto.dao.crud.PlatformAzureDao;
import jp.primecloud.auto.dao.crud.PlatformCloudstackDao;
import jp.primecloud.auto.dao.crud.PlatformNiftyDao;
import jp.primecloud.auto.dao.crud.PlatformOpenstackDao;
import jp.primecloud.auto.dao.crud.PlatformVcloudDao;
import jp.primecloud.auto.dao.crud.PlatformVcloudInstanceTypeDao;
import jp.primecloud.auto.dao.crud.PlatformVcloudStorageTypeDao;
import jp.primecloud.auto.dao.crud.PlatformVmwareDao;
import jp.primecloud.auto.dao.crud.PlatformVmwareInstanceTypeDao;
import jp.primecloud.auto.dao.crud.ProxyDao;
import jp.primecloud.auto.dao.crud.PuppetInstanceDao;
import jp.primecloud.auto.dao.crud.TemplateDao;
import jp.primecloud.auto.dao.crud.TemplateComponentDao;
import jp.primecloud.auto.dao.crud.TemplateInstanceDao;
import jp.primecloud.auto.dao.crud.UserDao;
import jp.primecloud.auto.dao.crud.UserAuthDao;
import jp.primecloud.auto.dao.crud.VcloudCertificateDao;
import jp.primecloud.auto.dao.crud.VcloudDiskDao;
import jp.primecloud.auto.dao.crud.VcloudInstanceDao;
import jp.primecloud.auto.dao.crud.VcloudInstanceNetworkDao;
import jp.primecloud.auto.dao.crud.VcloudKeyPairDao;
import jp.primecloud.auto.dao.crud.VcloudTaskDao;
import jp.primecloud.auto.dao.crud.VmwareAddressDao;
import jp.primecloud.auto.dao.crud.VmwareDiskDao;
import jp.primecloud.auto.dao.crud.VmwareInstanceDao;
import jp.primecloud.auto.dao.crud.VmwareKeyPairDao;
import jp.primecloud.auto.dao.crud.VmwareNetworkDao;
import jp.primecloud.auto.dao.crud.ZabbixDataDao;
import jp.primecloud.auto.dao.crud.ZabbixInstanceDao;

/**
 * <p>
 * Serviceのサポートクラスのベースクラスです。
 * </p>
 *
 */
public abstract class BaseServiceSupport {

    protected Log log = LogFactory.getLog(getClass());

    protected ApiCertificateDao apiCertificateDao;

    protected AuthoritySetDao authoritySetDao;

    protected AutoScalingConfDao autoScalingConfDao;

    protected AwsAddressDao awsAddressDao;

    protected AwsCertificateDao awsCertificateDao;

    protected AwsInstanceDao awsInstanceDao;

    protected AwsLoadBalancerDao awsLoadBalancerDao;

    protected AwsSnapshotDao awsSnapshotDao;

    protected AwsSslKeyDao awsSslKeyDao;

    protected AwsVolumeDao awsVolumeDao;

    protected AzureCertificateDao azureCertificateDao;

    protected AzureDiskDao azureDiskDao;

    protected AzureInstanceDao azureInstanceDao;

    protected AzureSubnetDao azureSubnetDao;

    protected CloudstackAddressDao cloudstackAddressDao;

    protected CloudstackCertificateDao cloudstackCertificateDao;

    protected CloudstackInstanceDao cloudstackInstanceDao;

    protected CloudstackLoadBalancerDao cloudstackLoadBalancerDao;

    protected CloudstackSnapshotDao cloudstackSnapshotDao;

    protected CloudstackVolumeDao cloudstackVolumeDao;

    protected ComponentDao componentDao;

    protected ComponentConfigDao componentConfigDao;

    protected ComponentInstanceDao componentInstanceDao;

    protected ComponentLoadBalancerDao componentLoadBalancerDao;

    protected ComponentTypeDao componentTypeDao;

    protected FarmDao farmDao;

    protected IaasInfoDao iaasInfoDao;

    protected ImageDao imageDao;

    protected ImageAwsDao imageAwsDao;

    protected ImageAzureDao imageAzureDao;

    protected ImageCloudstackDao imageCloudstackDao;

    protected ImageNiftyDao imageNiftyDao;

    protected ImageOpenstackDao imageOpenstackDao;

    protected ImageVcloudDao imageVcloudDao;

    protected ImageVmwareDao imageVmwareDao;

    protected InstanceDao instanceDao;

    protected InstanceConfigDao instanceConfigDao;

    protected LoadBalancerDao loadBalancerDao;

    protected LoadBalancerHealthCheckDao loadBalancerHealthCheckDao;

    protected LoadBalancerInstanceDao loadBalancerInstanceDao;

    protected LoadBalancerListenerDao loadBalancerListenerDao;

    protected NiftyCertificateDao niftyCertificateDao;

    protected NiftyInstanceDao niftyInstanceDao;

    protected NiftyKeyPairDao niftyKeyPairDao;

    protected NiftyVolumeDao niftyVolumeDao;

    protected OpenstackCertificateDao openstackCertificateDao;

    protected OpenstackInstanceDao openstackInstanceDao;

    protected OpenstackSslKeyDao openstackSslKeyDao;

    protected OpenstackVolumeDao openstackVolumeDao;

    protected PccSystemInfoDao pccSystemInfoDao;

    protected PlatformDao platformDao;

    protected PlatformAwsDao platformAwsDao;

    protected PlatformAzureDao platformAzureDao;

    protected PlatformCloudstackDao platformCloudstackDao;

    protected PlatformNiftyDao platformNiftyDao;

    protected PlatformOpenstackDao platformOpenstackDao;

    protected PlatformVcloudDao platformVcloudDao;

    protected PlatformVcloudInstanceTypeDao platformVcloudInstanceTypeDao;

    protected PlatformVcloudStorageTypeDao platformVcloudStorageTypeDao;

    protected PlatformVmwareDao platformVmwareDao;

    protected PlatformVmwareInstanceTypeDao platformVmwareInstanceTypeDao;

    protected ProxyDao proxyDao;

    protected PuppetInstanceDao puppetInstanceDao;

    protected TemplateDao templateDao;

    protected TemplateComponentDao templateComponentDao;

    protected TemplateInstanceDao templateInstanceDao;

    protected UserDao userDao;

    protected UserAuthDao userAuthDao;

    protected VcloudCertificateDao vcloudCertificateDao;

    protected VcloudDiskDao vcloudDiskDao;

    protected VcloudInstanceDao vcloudInstanceDao;

    protected VcloudInstanceNetworkDao vcloudInstanceNetworkDao;

    protected VcloudKeyPairDao vcloudKeyPairDao;

    protected VcloudTaskDao vcloudTaskDao;

    protected VmwareAddressDao vmwareAddressDao;

    protected VmwareDiskDao vmwareDiskDao;

    protected VmwareInstanceDao vmwareInstanceDao;

    protected VmwareKeyPairDao vmwareKeyPairDao;

    protected VmwareNetworkDao vmwareNetworkDao;

    protected ZabbixDataDao zabbixDataDao;

    protected ZabbixInstanceDao zabbixInstanceDao;

    /**
     * apiCertificateDaoを設定します。
     *
     * @param apiCertificateDao apiCertificateDao
     */
    public void setApiCertificateDao(ApiCertificateDao apiCertificateDao) {
        this.apiCertificateDao = apiCertificateDao;
    }

    /**
     * authoritySetDaoを設定します。
     *
     * @param authoritySetDao authoritySetDao
     */
    public void setAuthoritySetDao(AuthoritySetDao authoritySetDao) {
        this.authoritySetDao = authoritySetDao;
    }

    /**
     * autoScalingConfDaoを設定します。
     *
     * @param autoScalingConfDao autoScalingConfDao
     */
    public void setAutoScalingConfDao(AutoScalingConfDao autoScalingConfDao) {
        this.autoScalingConfDao = autoScalingConfDao;
    }

    /**
     * awsAddressDaoを設定します。
     *
     * @param awsAddressDao awsAddressDao
     */
    public void setAwsAddressDao(AwsAddressDao awsAddressDao) {
        this.awsAddressDao = awsAddressDao;
    }

    /**
     * awsCertificateDaoを設定します。
     *
     * @param awsCertificateDao awsCertificateDao
     */
    public void setAwsCertificateDao(AwsCertificateDao awsCertificateDao) {
        this.awsCertificateDao = awsCertificateDao;
    }

    /**
     * awsInstanceDaoを設定します。
     *
     * @param awsInstanceDao awsInstanceDao
     */
    public void setAwsInstanceDao(AwsInstanceDao awsInstanceDao) {
        this.awsInstanceDao = awsInstanceDao;
    }

    /**
     * awsLoadBalancerDaoを設定します。
     *
     * @param awsLoadBalancerDao awsLoadBalancerDao
     */
    public void setAwsLoadBalancerDao(AwsLoadBalancerDao awsLoadBalancerDao) {
        this.awsLoadBalancerDao = awsLoadBalancerDao;
    }

    /**
     * awsSnapshotDaoを設定します。
     *
     * @param awsSnapshotDao awsSnapshotDao
     */
    public void setAwsSnapshotDao(AwsSnapshotDao awsSnapshotDao) {
        this.awsSnapshotDao = awsSnapshotDao;
    }

    /**
     * awsSslKeyDaoを設定します。
     *
     * @param awsSslKeyDao awsSslKeyDao
     */
    public void setAwsSslKeyDao(AwsSslKeyDao awsSslKeyDao) {
        this.awsSslKeyDao = awsSslKeyDao;
    }

    /**
     * awsVolumeDaoを設定します。
     *
     * @param awsVolumeDao awsVolumeDao
     */
    public void setAwsVolumeDao(AwsVolumeDao awsVolumeDao) {
        this.awsVolumeDao = awsVolumeDao;
    }

    /**
     * azureCertificateDaoを設定します。
     *
     * @param azureCertificateDao azureCertificateDao
     */
    public void setAzureCertificateDao(AzureCertificateDao azureCertificateDao) {
        this.azureCertificateDao = azureCertificateDao;
    }

    /**
     * azureDiskDaoを設定します。
     *
     * @param azureDiskDao azureDiskDao
     */
    public void setAzureDiskDao(AzureDiskDao azureDiskDao) {
        this.azureDiskDao = azureDiskDao;
    }

    /**
     * azureInstanceDaoを設定します。
     *
     * @param azureInstanceDao azureInstanceDao
     */
    public void setAzureInstanceDao(AzureInstanceDao azureInstanceDao) {
        this.azureInstanceDao = azureInstanceDao;
    }

    /**
     * azureSubnetDaoを設定します。
     *
     * @param azureSubnetDao azureSubnetDao
     */
    public void setAzureSubnetDao(AzureSubnetDao azureSubnetDao) {
        this.azureSubnetDao = azureSubnetDao;
    }

    /**
     * cloudstackAddressDaoを設定します。
     *
     * @param cloudstackAddressDao cloudstackAddressDao
     */
    public void setCloudstackAddressDao(CloudstackAddressDao cloudstackAddressDao) {
        this.cloudstackAddressDao = cloudstackAddressDao;
    }

    /**
     * cloudstackCertificateDaoを設定します。
     *
     * @param cloudstackCertificateDao cloudstackCertificateDao
     */
    public void setCloudstackCertificateDao(CloudstackCertificateDao cloudstackCertificateDao) {
        this.cloudstackCertificateDao = cloudstackCertificateDao;
    }

    /**
     * cloudstackInstanceDaoを設定します。
     *
     * @param cloudstackInstanceDao cloudstackInstanceDao
     */
    public void setCloudstackInstanceDao(CloudstackInstanceDao cloudstackInstanceDao) {
        this.cloudstackInstanceDao = cloudstackInstanceDao;
    }

    /**
     * cloudstackLoadBalancerDaoを設定します。
     *
     * @param cloudstackLoadBalancerDao cloudstackLoadBalancerDao
     */
    public void setCloudstackLoadBalancerDao(CloudstackLoadBalancerDao cloudstackLoadBalancerDao) {
        this.cloudstackLoadBalancerDao = cloudstackLoadBalancerDao;
    }

    /**
     * cloudstackSnapshotDaoを設定します。
     *
     * @param cloudstackSnapshotDao cloudstackSnapshotDao
     */
    public void setCloudstackSnapshotDao(CloudstackSnapshotDao cloudstackSnapshotDao) {
        this.cloudstackSnapshotDao = cloudstackSnapshotDao;
    }

    /**
     * cloudstackVolumeDaoを設定します。
     *
     * @param cloudstackVolumeDao cloudstackVolumeDao
     */
    public void setCloudstackVolumeDao(CloudstackVolumeDao cloudstackVolumeDao) {
        this.cloudstackVolumeDao = cloudstackVolumeDao;
    }

    /**
     * componentDaoを設定します。
     *
     * @param componentDao componentDao
     */
    public void setComponentDao(ComponentDao componentDao) {
        this.componentDao = componentDao;
    }

    /**
     * componentConfigDaoを設定します。
     *
     * @param componentConfigDao componentConfigDao
     */
    public void setComponentConfigDao(ComponentConfigDao componentConfigDao) {
        this.componentConfigDao = componentConfigDao;
    }

    /**
     * componentInstanceDaoを設定します。
     *
     * @param componentInstanceDao componentInstanceDao
     */
    public void setComponentInstanceDao(ComponentInstanceDao componentInstanceDao) {
        this.componentInstanceDao = componentInstanceDao;
    }

    /**
     * componentLoadBalancerDaoを設定します。
     *
     * @param componentLoadBalancerDao componentLoadBalancerDao
     */
    public void setComponentLoadBalancerDao(ComponentLoadBalancerDao componentLoadBalancerDao) {
        this.componentLoadBalancerDao = componentLoadBalancerDao;
    }

    /**
     * componentTypeDaoを設定します。
     *
     * @param componentTypeDao componentTypeDao
     */
    public void setComponentTypeDao(ComponentTypeDao componentTypeDao) {
        this.componentTypeDao = componentTypeDao;
    }

    /**
     * farmDaoを設定します。
     *
     * @param farmDao farmDao
     */
    public void setFarmDao(FarmDao farmDao) {
        this.farmDao = farmDao;
    }

    /**
     * iaasInfoDaoを設定します。
     *
     * @param iaasInfoDao iaasInfoDao
     */
    public void setIaasInfoDao(IaasInfoDao iaasInfoDao) {
        this.iaasInfoDao = iaasInfoDao;
    }

    /**
     * imageDaoを設定します。
     *
     * @param imageDao imageDao
     */
    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    /**
     * imageAwsDaoを設定します。
     *
     * @param imageAwsDao imageAwsDao
     */
    public void setImageAwsDao(ImageAwsDao imageAwsDao) {
        this.imageAwsDao = imageAwsDao;
    }

    /**
     * imageAzureDaoを設定します。
     *
     * @param imageAzureDao imageAzureDao
     */
    public void setImageAzureDao(ImageAzureDao imageAzureDao) {
        this.imageAzureDao = imageAzureDao;
    }

    /**
     * imageCloudstackDaoを設定します。
     *
     * @param imageCloudstackDao imageCloudstackDao
     */
    public void setImageCloudstackDao(ImageCloudstackDao imageCloudstackDao) {
        this.imageCloudstackDao = imageCloudstackDao;
    }

    /**
     * imageNiftyDaoを設定します。
     *
     * @param imageNiftyDao imageNiftyDao
     */
    public void setImageNiftyDao(ImageNiftyDao imageNiftyDao) {
        this.imageNiftyDao = imageNiftyDao;
    }

    /**
     * imageOpenstackDaoを設定します。
     *
     * @param imageOpenstackDao imageOpenstackDao
     */
    public void setImageOpenstackDao(ImageOpenstackDao imageOpenstackDao) {
        this.imageOpenstackDao = imageOpenstackDao;
    }

    /**
     * imageVcloudDaoを設定します。
     *
     * @param imageVcloudDao imageVcloudDao
     */
    public void setImageVcloudDao(ImageVcloudDao imageVcloudDao) {
        this.imageVcloudDao = imageVcloudDao;
    }

    /**
     * imageVmwareDaoを設定します。
     *
     * @param imageVmwareDao imageVmwareDao
     */
    public void setImageVmwareDao(ImageVmwareDao imageVmwareDao) {
        this.imageVmwareDao = imageVmwareDao;
    }

    /**
     * instanceDaoを設定します。
     *
     * @param instanceDao instanceDao
     */
    public void setInstanceDao(InstanceDao instanceDao) {
        this.instanceDao = instanceDao;
    }

    /**
     * instanceConfigDaoを設定します。
     *
     * @param instanceConfigDao instanceConfigDao
     */
    public void setInstanceConfigDao(InstanceConfigDao instanceConfigDao) {
        this.instanceConfigDao = instanceConfigDao;
    }

    /**
     * loadBalancerDaoを設定します。
     *
     * @param loadBalancerDao loadBalancerDao
     */
    public void setLoadBalancerDao(LoadBalancerDao loadBalancerDao) {
        this.loadBalancerDao = loadBalancerDao;
    }

    /**
     * loadBalancerHealthCheckDaoを設定します。
     *
     * @param loadBalancerHealthCheckDao loadBalancerHealthCheckDao
     */
    public void setLoadBalancerHealthCheckDao(LoadBalancerHealthCheckDao loadBalancerHealthCheckDao) {
        this.loadBalancerHealthCheckDao = loadBalancerHealthCheckDao;
    }

    /**
     * loadBalancerInstanceDaoを設定します。
     *
     * @param loadBalancerInstanceDao loadBalancerInstanceDao
     */
    public void setLoadBalancerInstanceDao(LoadBalancerInstanceDao loadBalancerInstanceDao) {
        this.loadBalancerInstanceDao = loadBalancerInstanceDao;
    }

    /**
     * loadBalancerListenerDaoを設定します。
     *
     * @param loadBalancerListenerDao loadBalancerListenerDao
     */
    public void setLoadBalancerListenerDao(LoadBalancerListenerDao loadBalancerListenerDao) {
        this.loadBalancerListenerDao = loadBalancerListenerDao;
    }

    /**
     * niftyCertificateDaoを設定します。
     *
     * @param niftyCertificateDao niftyCertificateDao
     */
    public void setNiftyCertificateDao(NiftyCertificateDao niftyCertificateDao) {
        this.niftyCertificateDao = niftyCertificateDao;
    }

    /**
     * niftyInstanceDaoを設定します。
     *
     * @param niftyInstanceDao niftyInstanceDao
     */
    public void setNiftyInstanceDao(NiftyInstanceDao niftyInstanceDao) {
        this.niftyInstanceDao = niftyInstanceDao;
    }

    /**
     * niftyKeyPairDaoを設定します。
     *
     * @param niftyKeyPairDao niftyKeyPairDao
     */
    public void setNiftyKeyPairDao(NiftyKeyPairDao niftyKeyPairDao) {
        this.niftyKeyPairDao = niftyKeyPairDao;
    }

    /**
     * niftyVolumeDaoを設定します。
     *
     * @param niftyVolumeDao niftyVolumeDao
     */
    public void setNiftyVolumeDao(NiftyVolumeDao niftyVolumeDao) {
        this.niftyVolumeDao = niftyVolumeDao;
    }

    /**
     * openstackCertificateDaoを設定します。
     *
     * @param openstackCertificateDao openstackCertificateDao
     */
    public void setOpenstackCertificateDao(OpenstackCertificateDao openstackCertificateDao) {
        this.openstackCertificateDao = openstackCertificateDao;
    }

    /**
     * openstackInstanceDaoを設定します。
     *
     * @param openstackInstanceDao openstackInstanceDao
     */
    public void setOpenstackInstanceDao(OpenstackInstanceDao openstackInstanceDao) {
        this.openstackInstanceDao = openstackInstanceDao;
    }

    /**
     * openstackSslKeyDaoを設定します。
     *
     * @param openstackSslKeyDao openstackSslKeyDao
     */
    public void setOpenstackSslKeyDao(OpenstackSslKeyDao openstackSslKeyDao) {
        this.openstackSslKeyDao = openstackSslKeyDao;
    }

    /**
     * openstackVolumeDaoを設定します。
     *
     * @param openstackVolumeDao openstackVolumeDao
     */
    public void setOpenstackVolumeDao(OpenstackVolumeDao openstackVolumeDao) {
        this.openstackVolumeDao = openstackVolumeDao;
    }

    /**
     * pccSystemInfoDaoを設定します。
     *
     * @param pccSystemInfoDao pccSystemInfoDao
     */
    public void setPccSystemInfoDao(PccSystemInfoDao pccSystemInfoDao) {
        this.pccSystemInfoDao = pccSystemInfoDao;
    }

    /**
     * platformDaoを設定します。
     *
     * @param platformDao platformDao
     */
    public void setPlatformDao(PlatformDao platformDao) {
        this.platformDao = platformDao;
    }

    /**
     * platformAwsDaoを設定します。
     *
     * @param platformAwsDao platformAwsDao
     */
    public void setPlatformAwsDao(PlatformAwsDao platformAwsDao) {
        this.platformAwsDao = platformAwsDao;
    }

    /**
     * platformAzureDaoを設定します。
     *
     * @param platformAzureDao platformAzureDao
     */
    public void setPlatformAzureDao(PlatformAzureDao platformAzureDao) {
        this.platformAzureDao = platformAzureDao;
    }

    /**
     * platformCloudstackDaoを設定します。
     *
     * @param platformCloudstackDao platformCloudstackDao
     */
    public void setPlatformCloudstackDao(PlatformCloudstackDao platformCloudstackDao) {
        this.platformCloudstackDao = platformCloudstackDao;
    }

    /**
     * platformNiftyDaoを設定します。
     *
     * @param platformNiftyDao platformNiftyDao
     */
    public void setPlatformNiftyDao(PlatformNiftyDao platformNiftyDao) {
        this.platformNiftyDao = platformNiftyDao;
    }

    /**
     * platformOpenstackDaoを設定します。
     *
     * @param platformOpenstackDao platformOpenstackDao
     */
    public void setPlatformOpenstackDao(PlatformOpenstackDao platformOpenstackDao) {
        this.platformOpenstackDao = platformOpenstackDao;
    }

    /**
     * platformVcloudDaoを設定します。
     *
     * @param platformVcloudDao platformVcloudDao
     */
    public void setPlatformVcloudDao(PlatformVcloudDao platformVcloudDao) {
        this.platformVcloudDao = platformVcloudDao;
    }

    /**
     * platformVcloudInstanceTypeDaoを設定します。
     *
     * @param platformVcloudInstanceTypeDao platformVcloudInstanceTypeDao
     */
    public void setPlatformVcloudInstanceTypeDao(PlatformVcloudInstanceTypeDao platformVcloudInstanceTypeDao) {
        this.platformVcloudInstanceTypeDao = platformVcloudInstanceTypeDao;
    }

    /**
     * platformVcloudStorageTypeDaoを設定します。
     *
     * @param platformVcloudStorageTypeDao platformVcloudStorageTypeDao
     */
    public void setPlatformVcloudStorageTypeDao(PlatformVcloudStorageTypeDao platformVcloudStorageTypeDao) {
        this.platformVcloudStorageTypeDao = platformVcloudStorageTypeDao;
    }

    /**
     * platformVmwareDaoを設定します。
     *
     * @param platformVmwareDao platformVmwareDao
     */
    public void setPlatformVmwareDao(PlatformVmwareDao platformVmwareDao) {
        this.platformVmwareDao = platformVmwareDao;
    }

    /**
     * platformVmwareInstanceTypeDaoを設定します。
     *
     * @param platformVmwareInstanceTypeDao platformVmwareInstanceTypeDao
     */
    public void setPlatformVmwareInstanceTypeDao(PlatformVmwareInstanceTypeDao platformVmwareInstanceTypeDao) {
        this.platformVmwareInstanceTypeDao = platformVmwareInstanceTypeDao;
    }

    /**
     * proxyDaoを設定します。
     *
     * @param proxyDao proxyDao
     */
    public void setProxyDao(ProxyDao proxyDao) {
        this.proxyDao = proxyDao;
    }

    /**
     * puppetInstanceDaoを設定します。
     *
     * @param puppetInstanceDao puppetInstanceDao
     */
    public void setPuppetInstanceDao(PuppetInstanceDao puppetInstanceDao) {
        this.puppetInstanceDao = puppetInstanceDao;
    }

    /**
     * templateDaoを設定します。
     *
     * @param templateDao templateDao
     */
    public void setTemplateDao(TemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * templateComponentDaoを設定します。
     *
     * @param templateComponentDao templateComponentDao
     */
    public void setTemplateComponentDao(TemplateComponentDao templateComponentDao) {
        this.templateComponentDao = templateComponentDao;
    }

    /**
     * templateInstanceDaoを設定します。
     *
     * @param templateInstanceDao templateInstanceDao
     */
    public void setTemplateInstanceDao(TemplateInstanceDao templateInstanceDao) {
        this.templateInstanceDao = templateInstanceDao;
    }

    /**
     * userDaoを設定します。
     *
     * @param userDao userDao
     */
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * userAuthDaoを設定します。
     *
     * @param userAuthDao userAuthDao
     */
    public void setUserAuthDao(UserAuthDao userAuthDao) {
        this.userAuthDao = userAuthDao;
    }

    /**
     * vcloudCertificateDaoを設定します。
     *
     * @param vcloudCertificateDao vcloudCertificateDao
     */
    public void setVcloudCertificateDao(VcloudCertificateDao vcloudCertificateDao) {
        this.vcloudCertificateDao = vcloudCertificateDao;
    }

    /**
     * vcloudDiskDaoを設定します。
     *
     * @param vcloudDiskDao vcloudDiskDao
     */
    public void setVcloudDiskDao(VcloudDiskDao vcloudDiskDao) {
        this.vcloudDiskDao = vcloudDiskDao;
    }

    /**
     * vcloudInstanceDaoを設定します。
     *
     * @param vcloudInstanceDao vcloudInstanceDao
     */
    public void setVcloudInstanceDao(VcloudInstanceDao vcloudInstanceDao) {
        this.vcloudInstanceDao = vcloudInstanceDao;
    }

    /**
     * vcloudInstanceNetworkDaoを設定します。
     *
     * @param vcloudInstanceNetworkDao vcloudInstanceNetworkDao
     */
    public void setVcloudInstanceNetworkDao(VcloudInstanceNetworkDao vcloudInstanceNetworkDao) {
        this.vcloudInstanceNetworkDao = vcloudInstanceNetworkDao;
    }

    /**
     * vcloudKeyPairDaoを設定します。
     *
     * @param vcloudKeyPairDao vcloudKeyPairDao
     */
    public void setVcloudKeyPairDao(VcloudKeyPairDao vcloudKeyPairDao) {
        this.vcloudKeyPairDao = vcloudKeyPairDao;
    }

    /**
     * vcloudTaskDaoを設定します。
     *
     * @param vcloudTaskDao vcloudTaskDao
     */
    public void setVcloudTaskDao(VcloudTaskDao vcloudTaskDao) {
        this.vcloudTaskDao = vcloudTaskDao;
    }

    /**
     * vmwareAddressDaoを設定します。
     *
     * @param vmwareAddressDao vmwareAddressDao
     */
    public void setVmwareAddressDao(VmwareAddressDao vmwareAddressDao) {
        this.vmwareAddressDao = vmwareAddressDao;
    }

    /**
     * vmwareDiskDaoを設定します。
     *
     * @param vmwareDiskDao vmwareDiskDao
     */
    public void setVmwareDiskDao(VmwareDiskDao vmwareDiskDao) {
        this.vmwareDiskDao = vmwareDiskDao;
    }

    /**
     * vmwareInstanceDaoを設定します。
     *
     * @param vmwareInstanceDao vmwareInstanceDao
     */
    public void setVmwareInstanceDao(VmwareInstanceDao vmwareInstanceDao) {
        this.vmwareInstanceDao = vmwareInstanceDao;
    }

    /**
     * vmwareKeyPairDaoを設定します。
     *
     * @param vmwareKeyPairDao vmwareKeyPairDao
     */
    public void setVmwareKeyPairDao(VmwareKeyPairDao vmwareKeyPairDao) {
        this.vmwareKeyPairDao = vmwareKeyPairDao;
    }

    /**
     * vmwareNetworkDaoを設定します。
     *
     * @param vmwareNetworkDao vmwareNetworkDao
     */
    public void setVmwareNetworkDao(VmwareNetworkDao vmwareNetworkDao) {
        this.vmwareNetworkDao = vmwareNetworkDao;
    }

    /**
     * zabbixDataDaoを設定します。
     *
     * @param zabbixDataDao zabbixDataDao
     */
    public void setZabbixDataDao(ZabbixDataDao zabbixDataDao) {
        this.zabbixDataDao = zabbixDataDao;
    }

    /**
     * zabbixInstanceDaoを設定します。
     *
     * @param zabbixInstanceDao zabbixInstanceDao
     */
    public void setZabbixInstanceDao(ZabbixInstanceDao zabbixInstanceDao) {
        this.zabbixInstanceDao = zabbixInstanceDao;
    }

}
