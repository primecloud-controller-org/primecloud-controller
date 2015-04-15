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
package jp.primecloud.auto.ui.mock.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.common.status.InstanceCoodinateStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.CloudstackAddress;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.NiftyKeyPair;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PlatformCloudstack;
import jp.primecloud.auto.entity.crud.PlatformNifty;
import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.DataDiskDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.service.dto.InstanceNetworkDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.VmwareAddressDto;
import jp.primecloud.auto.ui.mock.XmlDataLoader;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockInstanceService implements InstanceService {

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public List<InstanceDto> getInstances(Long farmNo) {
        List<InstanceDto> result = new ArrayList<InstanceDto>();
        List<Instance> instances = XmlDataLoader.getData("instance.xml", Instance.class);
        List<ComponentInstance> componentInstances = XmlDataLoader.getData("componentInstance.xml",
                ComponentInstance.class);
        List<InstanceConfig> instanceConfigs = XmlDataLoader.getData("instanceConfig.xml", InstanceConfig.class);
        List<AwsInstance> awsInstances = XmlDataLoader.getData("awsInstance.xml", AwsInstance.class);
        List<VmwareInstance> vmwareInstances = XmlDataLoader.getData("vmwareInstance.xml", VmwareInstance.class);
        List<NiftyInstance> niftyInstances = XmlDataLoader.getData("niftyInstance.xml", NiftyInstance.class);
        List<Component> components = XmlDataLoader.getData("component.xml", Component.class);
        List<CloudstackInstance> cloudstackInstances = XmlDataLoader.getData("cloudstackInstance.xml", CloudstackInstance.class);

        //Platform
        LinkedHashMap<Long, Platform> platformMap = getPlatformMap();
        LinkedHashMap<Long, PlatformAws> platformAwsMap = getPlatformAwsMap();
        LinkedHashMap<Long, PlatformVmware> platformVmwareMap = getPlatformVmwareMap();
        LinkedHashMap<Long, PlatformNifty> platformNiftyMap = getPlatformNiftyMap();
        LinkedHashMap<Long, PlatformCloudstack> platformCloudstackMap = getPlatformCloudstackMap();

        //Image
        LinkedHashMap<Long, Image> imageMap = getImageMap();
        LinkedHashMap<Long, ImageAws> imageAwsMap = getImageAwsMap();
        LinkedHashMap<Long, ImageVmware> imageVmwareMap = getImageVmwareMap();
        LinkedHashMap<Long, ImageNifty> imageNiftyMap = getImageNiftyMap();
        LinkedHashMap<Long, ImageCloudstack> imageCloudstackMap = getImageCloudstackMap();

        for (Instance instance : instances) {
            //Platform
            PlatformDto platformDto = new PlatformDto();
            platformDto.setPlatform(platformMap.get(instance.getPlatformNo()));
            platformDto.setPlatformAws(platformAwsMap.get(instance.getPlatformNo()));
            platformDto.setPlatformVmware(platformVmwareMap.get(instance.getPlatformNo()));
            platformDto.setPlatformNifty(platformNiftyMap.get(instance.getPlatformNo()));
            platformDto.setPlatformCloudstack(platformCloudstackMap.get(instance.getPlatformNo()));

            //Image
            ImageDto imageDto = new ImageDto();
            imageDto.setImage(imageMap.get(instance.getImageNo()));
            imageDto.setImageAws(imageAwsMap.get(instance.getImageNo()));
            imageDto.setImageVmware(imageVmwareMap.get(instance.getImageNo()));
            imageDto.setImageNifty(imageNiftyMap.get(instance.getImageNo()));
            imageDto.setImageCloudstack(imageCloudstackMap.get(instance.getImageNo()));

            AwsInstance awsInstance = null;
            for (AwsInstance tmp : awsInstances) {
                if (tmp.getInstanceNo().equals(instance.getInstanceNo())) {
                    awsInstance = tmp;
                    break;
                }
            }

            AwsAddress awsAddress = null;
            //ElasticIP指定時のテストケース
            if (instance.getInstanceNo() == 6L) {
                awsAddress = new AwsAddress();
                awsAddress.setAddressNo(1L);
                awsAddress.setUserNo(1L);
                awsAddress.setPlatformNo(instance.getPlatformNo());
                awsAddress.setPublicIp(instance.getPublicIp());
                awsAddress.setComment(null);
                awsAddress.setInstanceNo(instance.getInstanceNo());
                awsAddress.setInstanceId(awsInstance.getInstanceId());
            }

            List<AwsVolume> awsVolumes = null;

            CloudstackInstance cloudstackInstance = null;
            for (CloudstackInstance tempCsInstance: cloudstackInstances) {
                if (instance.getInstanceNo().equals(tempCsInstance.getInstanceNo())) {
                    cloudstackInstance = tempCsInstance;
                }
            }

            CloudstackAddress cloudstackAddress = new CloudstackAddress();
            cloudstackAddress.setAddressId("1");
            cloudstackAddress.setIpaddress("111.222.333.444");

            List<CloudstackVolume> cloudstackVolumes = null;

            VmwareInstance vmwareInstance = null;
            for (VmwareInstance tmp : vmwareInstances) {
                if (tmp.getInstanceNo().equals(instance.getInstanceNo())) {
                    vmwareInstance = tmp;
                    break;
                }
            }

            VmwareAddress vmwareAddress = new VmwareAddress();
            if (vmwareInstance != null) {
                vmwareAddress.setIpAddress("172.0.0.1");
                vmwareAddress.setEnabled(false);
                vmwareAddress.setDefaultGateway("172.0.0.1");
                vmwareAddress.setSubnetMask("255.255.0.0");
                vmwareAddress.setInstanceNo(instance.getInstanceNo());
            }

            VmwareKeyPair vmwareKeyPair = null;
            if (vmwareInstance != null) {
                vmwareKeyPair = new VmwareKeyPair();
                vmwareKeyPair.setKeyNo(1L);
                vmwareKeyPair.setKeyName("key01");
                vmwareKeyPair.setUserNo(1L);
                vmwareKeyPair.setKeyPublic("keypublic");
            }

            List<VmwareDisk> vmwareDisks = null;

            NiftyInstance niftyInstance = null;
            for (NiftyInstance tmp : niftyInstances) {
                if (tmp.getInstanceNo().equals(instance.getInstanceNo())) {
                    niftyInstance = tmp;
                    break;
                }
            }

            NiftyKeyPair niftyKeyPair = null;
            if (niftyInstance != null) {
                niftyKeyPair = new NiftyKeyPair();
                niftyKeyPair.setKeyNo(niftyInstance.getKeyPairNo());
                niftyKeyPair.setKeyName("key0" + niftyInstance.getKeyPairNo());
                niftyKeyPair.setUserNo(1L);
            }

            InstanceDto dto = new InstanceDto();
            // テスト
            if ("STARTING".equals(instance.getStatus())) {
                instance.setProgress((int) (Math.random() * 100));
                if (instance.getProgress() > 90) {
                    instance.setProgress(100);
                    instance.setStatus("RUNNING");
                }
            }
            instance.setPublicIp(String.valueOf((int) (Math.random() * 100)));

            List<ComponentInstanceDto> componentInstanceDtos = new ArrayList<ComponentInstanceDto>();
            for (ComponentInstance componentInstance : componentInstances) {

                ComponentInstanceDto componentInstanceDto = new ComponentInstanceDto();
                componentInstanceDto.setComponentInstance(componentInstance);

                Component component = null;
                for (Component tmpComponent : components) {
                    if (componentInstance.getComponentNo().equals(tmpComponent.getComponentNo())) {
                        component = tmpComponent;
                        break;
                    }
                }

                String url = createUrl(instance.getPublicIp(), component.getComponentTypeNo());
                componentInstanceDto.setUrl(url);
                componentInstanceDtos.add(componentInstanceDto);
            }

            AwsCertificate awsCertificate = null;
            if (platformAwsMap.get(instance.getPlatformNo()) != null) {
                awsCertificate = new AwsCertificate();
                awsCertificate.setPlatformNo(instance.getPlatformNo());
                awsCertificate.setDefSubnet("subnet-00000001");
            }

            dto.setInstance(instance);
            dto.setPlatform(platformDto);
            dto.setImage(imageDto);
            dto.setInstanceConfigs(instanceConfigs);
            dto.setComponentInstances(componentInstanceDtos);
            dto.setAwsInstance(awsInstance);
            dto.setAwsAddress(awsAddress);
            dto.setAwsVolumes(awsVolumes);
            dto.setAwsCertificate(awsCertificate);
            dto.setCloudstackInstance(cloudstackInstance);
            dto.setCloudstackAddress(cloudstackAddress);
            dto.setCloudstackVolumes(cloudstackVolumes);
            dto.setVmwareInstance(vmwareInstance);
            dto.setVmwareAddress(vmwareAddress);
            dto.setVmwareKeyPair(vmwareKeyPair);
            dto.setVmwareDisks(vmwareDisks);
            dto.setNiftyInstance(niftyInstance);
            dto.setNiftyKeyPair(niftyKeyPair);
            result.add(dto);
        }
        return result;
    }

    @Override
    public Long createIaasInstance(Long farmNo, String instanceName, Long platformNo, String comment, Long imageNo,
            String instanceType) {
        System.out.println("createAwsInstance: instanceName=" + instanceName);
        return null;
    }

    @Override
    public Long createVmwareInstance(Long farmNo, String instanceName, Long platformNo, String comment, Long imageNo,
            String instanceType) {
        System.out.println("createVmwareInstance: instanceName=" + instanceName);
        return null;
    }

    @Override
    public Long createNiftyInstance(Long farmNo, String instanceName, Long platformNo, String comment, Long imageNo,
            String instanceType) {
        System.out.println("createNiftyInstance: instanceName=" + instanceName);
        return null;
    }

    @Override
    public void updateAwsInstance(Long instanceNo, String instanceName, String comment, String keyName,
            String instanceType, String securityGroupName, String availabilityZone, Long addressNo, String subnetId, String privateIpAddress) {
        System.out.println("updateAwsInstance: instanceName=" + instanceName);
    }

    @Override
    public void updateCloudstackInstance(Long instanceNo, String instanceName, String comment, String keyName,
            String instanceType, String securityGroupName, String availabilityZoneName, Long addressNo) {
        System.out.println("updateCloudstackInstance: instanceName=" + instanceName);

    }

    @Override
    public void updateVmwareInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String computeResource, String resourcePool, Long keyPairNo) {
        System.out.println("updateVmwareInstance: instanceName=" + instanceName);
    }

    @Override
    public void updateVmwareInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String computeResource, String resourcePool, Long keyPairNo, VmwareAddressDto vmwareAddressDto) {
        if(vmwareAddressDto == null){
            System.out.println("updateVmwareInstance: instanceName=" + instanceName);

        }else{
            System.out.println("updateVmwareInstance: instanceName=" + instanceName + " ipAddress="
                    + vmwareAddressDto.getIpAddress() + " subnetMsak=" + vmwareAddressDto.getSubnetMask()
                    + " defaultGateway=" + vmwareAddressDto.getDefaultGateway());
        }
    }

    @Override
    public void updateNiftyInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            Long keyPairNo) {
        System.out.println("updateNiftyInstance: instanceName=" + instanceName);
    }

    @Override
    public void deleteInstance(Long instanceNo) {
        System.out.println("deleteInstance: instanceNo=" + instanceNo);
    }

    @Override
    public void associateComponents(Long instanceNo, List<Long> componentNos) {
        log.info("associateComponents: instanceNos=" + instanceNo + ", componentNos=" + componentNos);
    }

    @Override
    public List<PlatformDto> getPlatforms(Long userNo) {
        // プラットフォームを取得
        List<PlatformDto> dtos = new ArrayList<PlatformDto>();
        List<Platform> platforms = XmlDataLoader.getData("platform.xml", Platform.class);
        LinkedHashMap<Long, ComponentType> componentTypeMap = getComponentTypeMap();
        LinkedHashMap<Long, PlatformAws> platformAwsMap = getPlatformAwsMap();
        LinkedHashMap<Long, PlatformVmware> platformVmwareMap = getPlatformVmwareMap();
        LinkedHashMap<Long, PlatformNifty> platformNiftyMap = getPlatformNiftyMap();
        LinkedHashMap<Long, PlatformCloudstack> platformCloudstackMap = getPlatformCloudstackMap();
        for (Platform platform : platforms) {
            List<ImageDto> imageDtos = getImages(platform, componentTypeMap);

            PlatformDto dto = new PlatformDto();
            dto.setPlatform(platform);
            dto.setPlatformAws(platformAwsMap.get(platform.getPlatformNo()));
            dto.setPlatformVmware(platformVmwareMap.get(platform.getPlatformNo()));
            dto.setPlatformNifty(platformNiftyMap.get(platform.getPlatformNo()));
            dto.setPlatformCloudstack(platformCloudstackMap.get(platform.getPlatformNo()));
            dto.setImages(imageDtos);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public void enableZabbixMonitoring(Long instanceNo) {
        System.out.println("enableZabbixMonitoring: instanceNo=" + instanceNo);
    }

    @Override
    public void disableZabbixMonitoring(Long instanceNo) {
        System.out.println("disableZabbixMonitoring: instanceNo=" + instanceNo);
    }

    @Override
    public InstanceStatus getInstanceStatus(Instance instance) {
        // 有効無効に応じてステータスを変更する（画面表示用）
        InstanceStatus instanceStatus = InstanceStatus.fromStatus(instance.getStatus());
        if (BooleanUtils.isTrue(instance.getEnabled())) {
            if (instanceStatus == InstanceStatus.STOPPED) {
                instance.setStatus(InstanceStatus.STARTING.toString());
            }
        } else {
            if (instanceStatus == InstanceStatus.RUNNING || instanceStatus == InstanceStatus.WARNING) {
                instance.setStatus(InstanceStatus.STOPPING.toString());
            }
        }

        // 画面表示用にステータスの変更
        //    サーバステータス 協調設定ステータス   変換後サーバステータス
        //        Running         Coodinating            Configuring
        //        Running         Warning                Warning
        instanceStatus = InstanceStatus.fromStatus(instance.getStatus());
        InstanceCoodinateStatus insCoodiStatus = InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus());
        // サーバステータス(Running)かつ協調設定ステータス(Coodinating)⇒「Configuring」
        if (instanceStatus == InstanceStatus.RUNNING && insCoodiStatus == InstanceCoodinateStatus.COODINATING) {
            instance.setStatus(InstanceStatus.CONFIGURING.toString());
        // サーバステータス(Running)かつ協調設定ステータス(Warning)⇒「Warning」
        } else if (instanceStatus == InstanceStatus.RUNNING && insCoodiStatus == InstanceCoodinateStatus.WARNING) {
            instance.setStatus(InstanceStatus.WARNING.toString());
        }

        return InstanceStatus.fromStatus(instance.getStatus());
    }

    protected List<ImageDto> getImages(Platform platform, LinkedHashMap<Long, ComponentType> componentTypeMap) {
        // イメージを取得
        List<ImageDto> imageDtos = new ArrayList<ImageDto>();
        List<Image> images = XmlDataLoader.getData("image.xml", Image.class);
        LinkedHashMap<Long, ImageAws> imageAwsMap = getImageAwsMap();
        LinkedHashMap<Long, ImageVmware> imageVmwareMap = getImageVmwareMap();
        LinkedHashMap<Long, ImageNifty> imageNiftyMap = getImageNiftyMap();
        LinkedHashMap<Long, ImageCloudstack> imageCloudstackMap = getImageCloudstackMap();
        for (Image image : images) {
            // プラットフォームが異なる場合はスキップ
            if (platform.getPlatformNo().equals(image.getPlatformNo()) == false ||
                image.getSelectable() == false) {
                continue;
            }

            // イメージに対応したコンポーネントタイプを取得
            String[] componentTypeNos = StringUtils.split(image.getComponentTypeNos(), ",");
            List<ComponentType> componentTypes = new ArrayList<ComponentType>();
            if (componentTypeNos != null) {
                for (String componentTypeNo : componentTypeNos) {
                    long no = Long.valueOf(componentTypeNo.trim());
                    ComponentType componentType = componentTypeMap.get(no);
                    componentTypes.add(componentType);
                }
            }

            ImageDto imageDto = new ImageDto();
            imageDto.setImage(image);
            imageDto.setImageAws(imageAwsMap.get(image.getImageNo()));
            imageDto.setImageVmware(imageVmwareMap.get(image.getImageNo()));
            imageDto.setImageNifty(imageNiftyMap.get(image.getImageNo()));
            imageDto.setImageCloudstack(imageCloudstackMap.get(image.getImageNo()));
            imageDto.setComponentTypes(componentTypes);
            imageDtos.add(imageDto);
        }

        return imageDtos;
    }

    protected String createUrl(String ipAddress, Long componentTypeNo) {

        String url = "http://";
        ComponentType componentType = getComponentTypeMap().get(componentTypeNo);
        if (componentType.getComponentTypeName().equals("apache")) {
            url = url + ipAddress + ":80/";
        } else if (componentType.getComponentTypeName().equals("tomcat")) {
            url = url + ipAddress + ":8080/";
        } else if (componentType.getComponentTypeName().equals("geronimo")) {
            url = url + ipAddress + ":8080/console/";
        } else if (componentType.getComponentTypeName().equals("mysql")) {
            url = url + ipAddress + ":8085/phpmyadmin/";
        } else if (componentType.getComponentTypeName().equals("prjserver")) {
            url = url + ipAddress + "/trac/prj/top/";
        }

        return url;
    }

    private LinkedHashMap<Long, Platform> getPlatformMap() {
        List<Platform> platforms = XmlDataLoader.getData("platform.xml", Platform.class);
        LinkedHashMap<Long, Platform> map = new LinkedHashMap<Long, Platform>();
        for (Platform platform: platforms) {
            map.put(platform.getPlatformNo(), platform);
        }
        return map;
    }


    private LinkedHashMap<Long, PlatformAws> getPlatformAwsMap() {
        List<PlatformAws> platformAwss = XmlDataLoader.getData("platformAws.xml", PlatformAws.class);
        LinkedHashMap<Long, PlatformAws> map = new LinkedHashMap<Long, PlatformAws>();
        for (PlatformAws platformAws: platformAwss) {
            map.put(platformAws.getPlatformNo(), platformAws);
        }
        return map;
    }

    private LinkedHashMap<Long, PlatformVmware> getPlatformVmwareMap() {
        List<PlatformVmware> platformVmwares = XmlDataLoader.getData("platformVmware.xml", PlatformVmware.class);
        LinkedHashMap<Long, PlatformVmware> map = new LinkedHashMap<Long, PlatformVmware>();
        for (PlatformVmware platformVmware: platformVmwares) {
            map.put(platformVmware.getPlatformNo(), platformVmware);
        }
        return map;
    }

    private LinkedHashMap<Long, PlatformNifty> getPlatformNiftyMap() {
        List<PlatformNifty> platformNifties = XmlDataLoader.getData("platformNifty.xml", PlatformNifty.class);
        LinkedHashMap<Long, PlatformNifty> map = new LinkedHashMap<Long, PlatformNifty>();
        for (PlatformNifty platformNifty: platformNifties) {
            map.put(platformNifty.getPlatformNo(), platformNifty);
        }
        return map;
    }

    private LinkedHashMap<Long, PlatformCloudstack> getPlatformCloudstackMap() {
        List<PlatformCloudstack> platformCloudstacks= XmlDataLoader.getData("platformCloudstack.xml", PlatformCloudstack.class);
        LinkedHashMap<Long, PlatformCloudstack> map = new LinkedHashMap<Long, PlatformCloudstack>();
        for (PlatformCloudstack platformCloudstack: platformCloudstacks) {
            map.put(platformCloudstack.getPlatformNo(), platformCloudstack);
        }
        return map;
    }

    private LinkedHashMap<Long, Image> getImageMap() {
        List<Image> images = XmlDataLoader.getData("image.xml", Image.class);
        LinkedHashMap<Long, Image> map = new LinkedHashMap<Long, Image>();
        for (Image image: images) {
            map.put(image.getImageNo(), image);
        }
        return map;
    }

    private LinkedHashMap<Long, ImageAws> getImageAwsMap() {
        List<ImageAws> imageAwss = XmlDataLoader.getData("imageAws.xml", ImageAws.class);
        LinkedHashMap<Long, ImageAws> map = new LinkedHashMap<Long, ImageAws>();
        for (ImageAws imageAws: imageAwss) {
            map.put(imageAws.getImageNo(), imageAws);
        }
        return map;
    }

    private LinkedHashMap<Long, ImageCloudstack> getImageCloudstackMap() {
        List<ImageCloudstack> imageCloudstacks = XmlDataLoader.getData("imageCloudstack.xml", ImageCloudstack.class);
        LinkedHashMap<Long, ImageCloudstack> map = new LinkedHashMap<Long, ImageCloudstack>();
        for (ImageCloudstack imageCloudstack: imageCloudstacks) {
            map.put(imageCloudstack.getImageNo(), imageCloudstack);
        }
        return map;
    }

    private LinkedHashMap<Long, ImageVmware> getImageVmwareMap() {
        List<ImageVmware> imageVmwares = XmlDataLoader.getData("imageVmware.xml", ImageVmware.class);
        LinkedHashMap<Long, ImageVmware> map = new LinkedHashMap<Long, ImageVmware>();
        for (ImageVmware imageVmware: imageVmwares) {
            map.put(imageVmware.getImageNo(), imageVmware);
        }
        return map;
    }

    private LinkedHashMap<Long, ImageNifty> getImageNiftyMap() {
        List<ImageNifty> imageNifties = XmlDataLoader.getData("imageNifty.xml", ImageNifty.class);
        LinkedHashMap<Long, ImageNifty> map = new LinkedHashMap<Long, ImageNifty>();
        for (ImageNifty imageNifty: imageNifties) {
            map.put(imageNifty.getImageNo(), imageNifty);
        }
        return map;
    }

    private LinkedHashMap<Long, ComponentType> getComponentTypeMap() {
        List<ComponentType> componentTypes = XmlDataLoader.getData("componentType.xml", ComponentType.class);
        LinkedHashMap<Long, ComponentType> map = new LinkedHashMap<Long, ComponentType>();
        for (ComponentType componentType: componentTypes) {
            map.put(componentType.getComponentTypeNo(), componentType);
        }
        return map;
    }

    @Override
    public InstanceDto getInstance(Long instanceNo) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Long createDataDisk(Long instanceNo, DataDiskDto dataDiskDto) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public void updateDataDisk(Long instanceNo, DataDiskDto dataDiskDto) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void attachDataDisk(Long instanceNo, Long diskNo) {
        // TODO 自動生成されたメソッド・スタブ

}

    @Override
    public void detachDataDisk(Long instanceNo, Long diskNo) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void updateVcloudInstance(Long instanceNo, String instanceName, String comment, Long storageTypeNo,
            Long keyPairNo, String instanceType, List<InstanceNetworkDto> instanceNetworkDtos) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void updateAzureInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String availabilitySet, String subnetId) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void updateOpenStackInstance(Long instanceNo, String instanceName, String comment, String instanceType,
            String availabilityZoneName, String securityGroupName, String keyName) {
        // TODO 自動生成されたメソッド・スタブ

    }

}
