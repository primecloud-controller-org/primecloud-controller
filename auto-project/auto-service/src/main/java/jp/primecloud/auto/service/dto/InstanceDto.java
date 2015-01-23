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
package jp.primecloud.auto.service.dto;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.AzureCertificate;
import jp.primecloud.auto.entity.crud.AzureDisk;
import jp.primecloud.auto.entity.crud.AzureInstance;
import jp.primecloud.auto.entity.crud.AzureSubnet;
import jp.primecloud.auto.entity.crud.CloudstackAddress;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.NiftyVolume;
import jp.primecloud.auto.entity.crud.OpenstackCertificate;
import jp.primecloud.auto.entity.crud.OpenstackInstance;
import jp.primecloud.auto.entity.crud.OpenstackVolume;
import jp.primecloud.auto.entity.crud.PlatformVcloudStorageType;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VcloudInstance;
import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;
import jp.primecloud.auto.entity.crud.VcloudKeyPair;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.entity.crud.ZabbixInstance;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class InstanceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Instance instance;

    private ZabbixInstance zabbixInstance;

    private PlatformDto platform;

    private ImageDto image;

    private List<InstanceConfig> instanceConfigs;

    private List<ComponentInstanceDto> componentInstances;

    private AwsInstance awsInstance;

    private AwsAddress awsAddress;

    private List<AwsVolume> awsVolumes;

    private AwsCertificate awsCertificate;

    private CloudstackInstance cloudstackInstance;

    private CloudstackAddress cloudstackAddress;

    private List<CloudstackVolume> cloudstackVolumes;

    private VmwareInstance vmwareInstance;

    private VmwareAddress vmwareAddress;

    private VmwareKeyPair vmwareKeyPair;

    private List<VmwareDisk> vmwareDisks;

    private NiftyInstance niftyInstance;

    private NiftyKeyPair niftyKeyPair;

    private VcloudInstance vcloudInstance;

    private VcloudKeyPair vcloudKeyPair;

    private List<VcloudDisk> vcloudDisks;

    private List<VcloudInstanceNetwork> vcloudInstanceNetworks;

    private PlatformVcloudStorageType platformVcloudStorageType;

    private AzureInstance azureInstance;

    private AzureCertificate azureCertificate;

    private List<AzureDisk> azureDisks;

    private AzureSubnet azureSubnet;

    private OpenstackInstance openstackInstance;

    private OpenstackCertificate openstackCertificate;

    private List<OpenstackVolume> openstackVolumes;

    private List<NiftyVolume> niftyVolumes;

    /**
     * instanceを取得します。
     *
     * @return instance
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * instanceを設定します。
     *
     * @param instance instance
     */
    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    /**
     * zabbixInstanceを取得します。
     *
     * @return zabbixInstance
     */
    public ZabbixInstance getZabbixInstance() {
        return zabbixInstance;
    }

    /**
     * zabbixInstanceを設定します。
     *
     * @param zabbixInstance zabbixInstance
     */
    public void setZabbixInstance(ZabbixInstance zabbixInstance) {
        this.zabbixInstance = zabbixInstance;
    }

    /**
     * platformを取得します。
     *
     * @return platform
     */
    public PlatformDto getPlatform() {
        return platform;
    }

    /**
     * platformを設定します。
     *
     * @param platform platform
     */
    public void setPlatform(PlatformDto platform) {
        this.platform = platform;
    }

    /**
     * imageを取得します。
     *
     * @return image
     */
    public ImageDto getImage() {
        return image;
    }

    /**
     * imageを設定します。
     *
     * @param image image
     */
    public void setImage(ImageDto image) {
        this.image = image;
    }

    /**
     * instanceConfigsを取得します。
     *
     * @return instanceConfigs
     */
    public List<InstanceConfig> getInstanceConfigs() {
        return instanceConfigs;
    }

    /**
     * instanceConfigsを設定します。
     *
     * @param instanceConfigs instanceConfigs
     */
    public void setInstanceConfigs(List<InstanceConfig> instanceConfigs) {
        this.instanceConfigs = instanceConfigs;
    }

    /**
     * componentInstancesを取得します。
     *
     * @return componentInstances
     */
    public List<ComponentInstanceDto> getComponentInstances() {
        return componentInstances;
    }

    /**
     * componentInstancesを設定します。
     *
     * @param componentInstances componentInstances
     */
    public void setComponentInstances(List<ComponentInstanceDto> componentInstances) {
        this.componentInstances = componentInstances;
    }

    /**
     * awsInstanceを取得します。
     *
     * @return awsInstance
     */
    public AwsInstance getAwsInstance() {
        return awsInstance;
    }

    /**
     * awsInstanceを設定します。
     *
     * @param awsInstance awsInstance
     */
    public void setAwsInstance(AwsInstance awsInstance) {
        this.awsInstance = awsInstance;
    }

    /**
     * awsAddressを取得します。
     *
     * @return awsAddress
     */
    public AwsAddress getAwsAddress() {
        return awsAddress;
    }

    /**
     * awsAddressを設定します。
     *
     * @param awsAddress awsAddress
     */
    public void setAwsAddress(AwsAddress awsAddress) {
        this.awsAddress = awsAddress;
    }

    /**
     * awsVolumesを取得します。
     *
     * @return awsVolumes
     */
    public List<AwsVolume> getAwsVolumes() {
        return awsVolumes;
    }

    /**
     * awsVolumesを設定します。
     *
     * @param awsVolumes awsVolumes
     */
    public void setAwsVolumes(List<AwsVolume> awsVolumes) {
        this.awsVolumes = awsVolumes;
    }

    /**
     * awsCertificateを取得します。
     *
     * @return awsCertificate
     */
    public AwsCertificate getAwsCertificate() {
        return awsCertificate;
    }

    /**
     * awsCertificateを設定します。
     *
     * @param awsCertificate awsCertificate
     */
    public void setAwsCertificate(AwsCertificate awsCertificate) {
        this.awsCertificate = awsCertificate;
    }

    /**
     * vmwareInstanceを取得します。
     *
     * @return vmwareInstance
     */
    public VmwareInstance getVmwareInstance() {
        return vmwareInstance;
    }

    /**
     * vmwareInstanceを設定します。
     *
     * @param vmwareInstance vmwareInstance
     */
    public void setVmwareInstance(VmwareInstance vmwareInstance) {
        this.vmwareInstance = vmwareInstance;
    }

    /**
     * vmwareAddressを取得します。
     *
     * @return vmwareAddress
     */
    public VmwareAddress getVmwareAddress() {
        return vmwareAddress;
    }

    /**
     * vmwareAddressを設定します。
     *
     * @param vmwareAddress vmwareAddress
     */
    public void setVmwareAddress(VmwareAddress vmwareAddress) {
        this.vmwareAddress = vmwareAddress;
    }

    /**
     * vmwareKeyPairを取得します。
     *
     * @return vmwareKeyPair
     */
    public VmwareKeyPair getVmwareKeyPair() {
        return vmwareKeyPair;
    }

    /**
     * vmwareKeyPairを設定します。
     *
     * @param vmwareKeyPair vmwareKeyPair
     */
    public void setVmwareKeyPair(VmwareKeyPair vmwareKeyPair) {
        this.vmwareKeyPair = vmwareKeyPair;
    }

    /**
     * vmwareDisksを取得します。
     *
     * @return vmwareDisks
     */
    public List<VmwareDisk> getVmwareDisks() {
        return vmwareDisks;
    }

    /**
     * vmwareDisksを設定します。
     *
     * @param vmwareDisks vmwareDisks
     */
    public void setVmwareDisks(List<VmwareDisk> vmwareDisks) {
        this.vmwareDisks = vmwareDisks;
    }

    /**
     * niftyInstanceを取得します。
     *
     * @return niftyInstance
     */
    public NiftyInstance getNiftyInstance() {
        return niftyInstance;
    }

    /**
     * niftyInstanceを設定します。
     *
     * @param niftyInstance niftyInstance
     */
    public void setNiftyInstance(NiftyInstance niftyInstance) {
        this.niftyInstance = niftyInstance;
    }

    /**
     * niftyKeyPairを取得します。
     *
     * @return niftyKeyPair
     */
    public NiftyKeyPair getNiftyKeyPair() {
        return niftyKeyPair;
    }

    /**
     * niftyKeyPairを設定します。
     *
     * @param niftyKeyPair niftyKeyPair
     */
    public void setNiftyKeyPair(NiftyKeyPair niftyKeyPair) {
        this.niftyKeyPair = niftyKeyPair;
    }

    /**
     * cloudstackInstanceを取得します。
     *
     * @return cloudstackInstance
     */
    public CloudstackInstance getCloudstackInstance() {
        return cloudstackInstance;
    }

    /**
     * cloudstackInstanceを設定します。
     *
     * @param cloudstackInstance cloudstackInstance
     */
    public void setCloudstackInstance(CloudstackInstance cloudstackInstance) {
        this.cloudstackInstance = cloudstackInstance;
    }

    /**
     * cloudstackAddressを取得します。
     *
     * @return cloudstackAddress
     */
    public CloudstackAddress getCloudstackAddress() {
        return cloudstackAddress;
    }

    /**
     * cloudstackAddressを設定します。
     *
     * @param cloudstackAddress cloudstackAddress
     */
    public void setCloudstackAddress(CloudstackAddress cloudstackAddress) {
        this.cloudstackAddress = cloudstackAddress;
    }

    /**
     * cloudstackVolumesを取得します。
     *
     * @return cloudstackVolumes
     */
    public List<CloudstackVolume> getCloudstackVolumes() {
        return cloudstackVolumes;
    }

    /**
     * cloudstackVolumesを設定します。
     *
     * @param cloudstackVolumes cloudstackVolumes
     */
    public void setCloudstackVolumes(List<CloudstackVolume> cloudstackVolumes) {
        this.cloudstackVolumes = cloudstackVolumes;
    }

    /**
     * vcloudInstanceを取得します。
     *
     * @return vcloudInstance
     */
    public VcloudInstance getVcloudInstance() {
        return vcloudInstance;
    }

    /**
     * vcloudInstanceを設定します。
     *
     * @param vcloudInstance vcloudInstance
     */
    public void setVcloudInstance(VcloudInstance vcloudInstance) {
        this.vcloudInstance = vcloudInstance;
    }

    /**
     * vcloudKeyPairを取得します。
     *
     * @return vcloudKeyPair
     */
    public VcloudKeyPair getVcloudKeyPair() {
        return vcloudKeyPair;
    }

    /**
     * vcloudKeyPairを設定します。
     *
     * @param vcloudKeyPair vcloudKeyPair
     */
    public void setVcloudKeyPair(VcloudKeyPair vcloudKeyPair) {
        this.vcloudKeyPair = vcloudKeyPair;
    }
    /**
     * vcloudDisksを取得します。
     *
     * @return vcloudDisks
     */
    public List<VcloudDisk> getVcloudDisks() {
        return vcloudDisks;
    }

    /**
     * vcloudDisksを設定します。
     *
     * @param vcloudDisks vcloudDisks
     */
    public void setVcloudDisks(List<VcloudDisk> vcloudDisks) {
        this.vcloudDisks = vcloudDisks;
    }

    /**
     * vcloudInstanceNetworksを取得します。
     *
     * @return vcloudInstanceNetworks
     */
    public List<VcloudInstanceNetwork> getVcloudInstanceNetworks() {
        return vcloudInstanceNetworks;
    }

    /**
     * vcloudInstanceNetworksを設定します。
     *
     * @param vcloudInstanceNetworks vcloudInstanceNetworks
     */
    public void setVcloudInstanceNetworks(List<VcloudInstanceNetwork> vcloudInstanceNetworks) {
        this.vcloudInstanceNetworks = vcloudInstanceNetworks;
    }

    /**
     * platformVcloudStorageTypeを取得します。
     *
     * @return platformVcloudStorageType
     */
    public PlatformVcloudStorageType getPlatformVcloudStorageType() {
        return platformVcloudStorageType;
    }

    /**
     * platformVcloudStorageTypeを設定します。
     *
     * @param platformVcloudStorageType platformVcloudStorageType
     */
    public void setPlatformVcloudStorageType(PlatformVcloudStorageType platformVcloudStorageType) {
        this.platformVcloudStorageType = platformVcloudStorageType;
    }

    /**
     * azureInstanceを取得します。
     *
     * @return azureInstance
     */
    public AzureInstance getAzureInstance() {
        return azureInstance;
    }

    /**
     * azureInstanceを設定します。
     *
     * @return azureInstance azureInstance
     */
    public void setAzureInstance(AzureInstance azureInstance) {
        this.azureInstance = azureInstance;
    }

    /**
     * azureCertificateを取得します。
     *
     * @return azureCertificate
     */
    public AzureCertificate getAzureCertificate() {
        return azureCertificate;
    }

    /**
     * azureCertificateを設定します。
     *
     * @return azureCertificate azureCertificate
     */
    public void setAzureCertificate(AzureCertificate azureCertificate) {
        this.azureCertificate = azureCertificate;
    }

    /**
     * azureDisksを取得します。
     *
     * @return azureDisks
     */
    public List<AzureDisk> getAzureDisks() {
        return azureDisks;
    }

    /**
     * azureDisksを設定します。
     *
     * @return azureDisks azureDisks
     */
    public void setAzureDisks(List<AzureDisk> azureDisks) {
        this.azureDisks = azureDisks;
    }

    /**
     * azureInstanceNetworksを取得します。
     *
     * @return azureSubnet
     */
    public AzureSubnet getAzureSubnet() {
        return azureSubnet;
    }

    /**
     * azureInstanceNetworksを設定します。
     *
     * @return AzureSubnet azureSubnet
     */
    public void setAzureSubnet(AzureSubnet azureInstanceNetworks) {
        this.azureSubnet = azureInstanceNetworks;
    }

    /**
     * openstackInstanceを取得します。
     *
     * @return openstackInstance
     */
    public OpenstackInstance getOpenstackInstance() {
        return openstackInstance;
    }

    /**
     * openstackInstanceを設定します。
     *
     * @return openstackInstance openstackInstance
     */
    public void setOpenstackInstance(OpenstackInstance openstackInstance) {
        this.openstackInstance = openstackInstance;
    }

    /**
     * openstackCertificateを取得します。
     *
     * @return openstackCertificate
     */
    public OpenstackCertificate getOpenstackCertificate() {
        return openstackCertificate;
    }

    /**
     * openstackCertificateを設定します。
     *
     * @return openstackCertificate openstackCertificate
     */
    public void setOpenstackCertificate(OpenstackCertificate openstackCertificate) {
        this.openstackCertificate = openstackCertificate;
    }

    /**
     * openstackVolumesを取得します。
     * @return openstackVolumes
     */
    public List<OpenstackVolume> getOpenstackVolumes() {
        return openstackVolumes;
    }

    /**
     * openstackVolumesを設定します。
     * @param openstackVolumes openstackVolumes
     */
    public void setOpenstackVolumes(List<OpenstackVolume> openstackVolumes) {
        this.openstackVolumes = openstackVolumes;
    }

    /**
     * niftyVolumesを取得します。
     * @return niftyVolumes
     */
    public List<NiftyVolume> getNiftyVolumes() {
        return niftyVolumes;
    }

    /**
     * niftyVolumesを設定します。
     * @param niftyVolumes niftyVolumes
     */
    public void setNiftyVolumes(List<NiftyVolume> niftyVolumes) {
        this.niftyVolumes = niftyVolumes;
    }

}
