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

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.AutoScalingConf;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.CloudstackLoadBalancer;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PlatformCloudstack;
import jp.primecloud.auto.entity.crud.PlatformNifty;
import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.AutoScalingConfDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.SslKeyDto;
import jp.primecloud.auto.ui.mock.XmlDataLoader;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockLoadBalancerService implements LoadBalancerService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LoadBalancerDto> getLoadBalancers(Long farmNo) {
        List<LoadBalancer> loadBalancers = XmlDataLoader.getData("loadBalancer.xml", LoadBalancer.class);

        // Platform
        //LinkedHashMap<Long, Platform> platformMap = getPlatformMap();
        List<Platform> platforms = XmlDataLoader.getData("platform.xml", Platform.class);
        LinkedHashMap<Long, PlatformAws> platformAwsMap = getPlatformAwsMap();
        LinkedHashMap<Long, PlatformCloudstack> platformCloudstackMap = getPlatformCloudstackMap();
        LinkedHashMap<Long, PlatformVmware>  platformVmwareMap = getPlatformVmwareMap();
        LinkedHashMap<Long, PlatformNifty> platformNiftyMap = getPlatformNiftyMap();
        LinkedHashMap<Long, PlatformDto> platformDtoMap = new LinkedHashMap<Long, PlatformDto>();
        for (Platform platform: platforms) {
            PlatformDto platformDto = new PlatformDto();
            platformDto.setPlatform(platform);
            platformDto.setPlatformAws(platformAwsMap.get(platform.getPlatformNo()));
            platformDto.setPlatformCloudstack(platformCloudstackMap.get(platform.getPlatformNo()));
            platformDto.setPlatformVmware(platformVmwareMap.get(platform.getPlatformNo()));
            platformDto.setPlatformNifty(platformNiftyMap.get(platform.getPlatformNo()));
        }

        // Image
        List<Image> images = XmlDataLoader.getData("image.xml", Image.class);
        LinkedHashMap<Long, ImageAws> imageAwsMap = getImageAwsMap();
        LinkedHashMap<Long, ImageCloudstack> imageCloudstackMap = getImageCloudstackMap();
        LinkedHashMap<Long, ImageVmware> imageVmwareMap = getImageVmwareMap();
        LinkedHashMap<Long, ImageNifty> imageNiftyMap = getImageNiftyMap();
        LinkedHashMap<Long, ImageDto> imageDtoMap = new LinkedHashMap<Long, ImageDto>();
        for (Image image: images) {
            ImageDto imageDto = new ImageDto();
            imageDto.setImage(image);
            imageDto.setImageAws(imageAwsMap.get(image.getImageNo()));
            imageDto.setImageCloudstack(imageCloudstackMap.get(image.getImageNo()));
            imageDto.setImageVmware(imageVmwareMap.get(image.getImageNo()));
            imageDto.setImageNifty(imageNiftyMap.get(image.getImageNo()));
            imageDtoMap.put(image.getImageNo(), imageDto);
        }

        List<LoadBalancerDto> dtos = new ArrayList<LoadBalancerDto>();
        for (LoadBalancer loadBalancer : loadBalancers) {

            // AwsLoadBalancer
            AwsLoadBalancer awsLoadBalancer = null;
            if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
                awsLoadBalancer = new AwsLoadBalancer();
                awsLoadBalancer.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
                awsLoadBalancer.setDnsName(loadBalancer.getCanonicalName());
                awsLoadBalancer.setSubnetId("subnet-00000001");
                awsLoadBalancer.setSecurityGroups("default");
            }

            CloudstackLoadBalancer cloudstackLoadBalancer = null;
            if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(loadBalancer.getType())) {
                cloudstackLoadBalancer = new CloudstackLoadBalancer();
                cloudstackLoadBalancer.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
            }

            // LoadBalancerListener
            List<LoadBalancerListener> listeners = new ArrayList<LoadBalancerListener>();

            if (loadBalancer.getLoadBalancerNo().equals(Long.valueOf("4")) == false) {
                LoadBalancerListener listener = new LoadBalancerListener();
                listener.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
                listener.setLoadBalancerPort(80);
                listener.setServicePort(80);
                listener.setProtocol("HTTP");
                listener.setStatus(loadBalancer.getStatus());
                listener.setEnabled(loadBalancer.getEnabled());
                listeners.add(listener);

                listener = new LoadBalancerListener();
                listener.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
                listener.setLoadBalancerPort(8000);
                listener.setServicePort(80);
                listener.setProtocol("HTTP");
                listener.setStatus(loadBalancer.getStatus());
                listener.setEnabled(loadBalancer.getEnabled());
                listeners.add(listener);
            }

            // LoadBalancerHealthCheck
            LoadBalancerHealthCheck healthCheck = new LoadBalancerHealthCheck();
            healthCheck.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
            healthCheck.setCheckProtocol("HTTP");
            healthCheck.setCheckPort(80);
            healthCheck.setCheckPath("/healthcheck");
            healthCheck.setCheckTimeout(10);
            healthCheck.setCheckInterval(60);
            healthCheck.setHealthyThreshold(3);
            healthCheck.setUnhealthyThreshold(2);

            // LoadBalancerInstance
            List<LoadBalancerInstance> lbInstances = new ArrayList<LoadBalancerInstance>();

            LoadBalancerInstance lbInstance = new LoadBalancerInstance();
            lbInstance.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
            lbInstance.setInstanceNo(1L);
            lbInstance.setEnabled(true);
            lbInstance.setStatus("RUNNING");
            lbInstances.add(lbInstance);

            LoadBalancerInstance lbInstance2 = new LoadBalancerInstance();
            lbInstance2.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
            lbInstance2.setInstanceNo(2L);
            lbInstance2.setEnabled(false);
            lbInstance2.setStatus("STOPPED");
            lbInstances.add(lbInstance2);

            //AutoScalingConf
            AutoScalingConfDto autoScalingConfDto = new AutoScalingConfDto();
            AutoScalingConf autoScalingConf = new AutoScalingConf();
            autoScalingConf.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
            autoScalingConf.setPlatformNo(1L);
            autoScalingConf.setImageNo(100L);
            autoScalingConf.setIdleTimeMax(0L);
            autoScalingConf.setIdleTimeMin(0L);
            autoScalingConf.setContinueLimit(0L);
            autoScalingConf.setAddCount(0L);
            autoScalingConf.setDelCount(0L);
            autoScalingConf.setEnabled(false);

            autoScalingConfDto.setAutoScalingConf(autoScalingConf);
            autoScalingConfDto.setPlatform(platformDtoMap.get(autoScalingConf.getPlatformNo()));
            autoScalingConfDto.setImage(imageDtoMap.get(autoScalingConf.getImageNo()));

            // DTO
            LoadBalancerDto dto = new LoadBalancerDto();
            dto.setLoadBalancer(loadBalancer);
            dto.setPlatform(platformDtoMap.get(loadBalancer.getPlatformNo()));
            dto.setAutoScalingConf(autoScalingConfDto);
            dto.setAwsLoadBalancer(awsLoadBalancer);
            dto.setCloudstackLoadBalancer(cloudstackLoadBalancer);
            dto.setLoadBalancerListeners(listeners);
            dto.setLoadBalancerHealthCheck(healthCheck);
            dto.setLoadBalancerInstances(lbInstances);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public Long createAwsLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo,
            Long componentNo) {
        System.out.println("createAwsLoadBalancer: loadBalancerName=" + loadBalancerName + ", componentNo="
                + componentNo);
        return null;
    }

    @Override
    public Long createCloudstackLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo, Long componentNo) {
        System.out.println("createAwsLoadBalancer: loadBalancerName=" + loadBalancerName + ", componentNo="
                + componentNo);
        return null;
    }

    @Override
    public Long createUltraMonkeyLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo,
            Long componentNo) {
        System.out.println("createUltraMonkeyLoadBalancer: loadBalancerName=" + loadBalancerName + ", componentNo="
                + componentNo);
        return null;
    }

    @Override
    public void updateAwsLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment,
            Long componentNo, String subnetId, String securityGroupName, String availabilityZone) {
        System.out.println("updateAwsLoadBalancer: loadBalancerName=" + loadBalancerName + ", componentNo="
                + componentNo);
    }

    @Override
    public void updateCloudstackLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment, Long componentNo,
            String algorithm, String pubricPort, String privatePort) {
        System.out.println("updateAwsLoadBalancer: loadBalancerName=" + loadBalancerName + ", componentNo="
                + componentNo);
    }
    @Override
    public void updateUltraMonkeyLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment,
            Long componentNo) {
        System.out.println("updateUltraMonkeyLoadBalancer: loadBalancerName=" + loadBalancerName + ", componentNo="
                + componentNo);
    }

    @Override
    public void deleteLoadBalancer(Long loadBalancerNo) {
        System.out.println("deleteLoadBalancer: loadBalancerNo=" + loadBalancerNo);
    }

    @Override
    public void createListener(Long loadBalancerNo, Integer loadBalancerPort, Integer servicePort, String protocol, Long sslKeyNo) {
        System.out.println("createLoadBalancerListener: loadBalancerNo=" + loadBalancerNo + ", loadBalancerPort="
                + loadBalancerPort);
    }

    @Override
    public void updateListener(Long loadBalancerNo, Integer originalLoadBalancerPort, Integer loadBalancerPort,
            Integer servicePort, String protocol, Long sslKeyNo) {
        System.out.println("createLoadBalancerListener: loadBalancerNo=" + loadBalancerNo
                + ", originalLoadBalancerPort=" + originalLoadBalancerPort + ", loadBalancerPort=" + loadBalancerPort);
    }

    @Override
    public void deleteListener(Long loadBalancerNo, Integer loadBalancerPort) {
        System.out.println("deleteLoadBalancerListener: loadBalancerNo=" + loadBalancerNo + ", loadBalancerPort="
                + loadBalancerPort);
    }

    @Override
    public void configureHealthCheck(Long loadBalancerNo, String checkProtocol, Integer checkPort, String checkPath,
            Integer checkTimeout, Integer checkInterval, Integer healthyThreshold, Integer unhealthyThreshold) {
        System.out.println("configureLoadBalancerHealthCheck: loadBalancerNo=" + loadBalancerNo);
    }

    @Override
    public void enableInstances(Long loadBalancerNo, List<Long> instanceNos) {
        System.out.println("enableInstances: loadBalancerNo=" + loadBalancerNo + ", instanceNos=" + instanceNos);
    }

    @Override
    public void disableInstances(Long loadBalancerNo, List<Long> instanceNos) {
        System.out.println("disableInstances: loadBalancerNo=" + loadBalancerNo + ", instanceNos=" + instanceNos);
    }

    @Override
    public List<LoadBalancerPlatformDto> getPlatforms(Long userNo) {
        // 引数チェック
        if (userNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "userNo");
        }

        List<LoadBalancerPlatformDto> dtos = new ArrayList<LoadBalancerPlatformDto>();
        List<Platform> platforms = XmlDataLoader.getData("platform.xml", Platform.class);
        LinkedHashMap<Long, PlatformAws> platformAwsMap = getPlatformAwsMap();
        LinkedHashMap<Long, PlatformVmware> platformVmwareMap = getPlatformVmwareMap();
        LinkedHashMap<Long, PlatformNifty> platformNiftyMap = getPlatformNiftyMap();
        LinkedHashMap<Long, PlatformCloudstack> platformCloudstackMap = getPlatformCloudstackMap();
        List<Image> images = XmlDataLoader.getData("image.xml", Image.class);
        for (Platform platform : platforms) {
            List<String> types = new ArrayList<String>();

            // AmazonEC2の場合、AWSロードバランサを利用可能とする
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                PlatformAws platformAws = platformAwsMap.get(platform.getPlatformNo());
                if (platformAws.getEuca() == false) {
                    types.add("aws"); // TODO: 文字列定数化
                }
            }

            // UltraMonkeyイメージの利用可否チェック
            for (Image image : images) {
                if (image.getPlatformNo().equals(platform.getPlatformNo()) && PCCConstant.IMAGE_NAME_ULTRAMONKEY.equals(image.getImageName())) {
                    types.add(PCCConstant.LOAD_BALANCER_ULTRAMONKEY);
                }
            }

            // 利用可能なロードバランサがない場合はスキップ
            if (types.isEmpty()) {
                continue;
            }

            List<ImageDto> imageDtos = getImages(platform, images);

            LoadBalancerPlatformDto dto = new LoadBalancerPlatformDto();
            dto.setPlatform(platform);
            dto.setPlatformAws(platformAwsMap.get(platform.getPlatformNo()));
            dto.setPlatformVmware(platformVmwareMap.get(platform.getPlatformNo()));
            dto.setPlatformNifty(platformNiftyMap.get(platform.getPlatformNo()));
            dto.setPlatformCloudstack(platformCloudstackMap.get(platform.getPlatformNo()));
            dto.setImages(imageDtos);
            dto.setTypes(types);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public void updateAutoScalingConf(Long farmNo, Long loadBalancerNo, Long platformNo, Long imageNo,
            String instanceType, Integer enabled, String namingRule, Long idleTimeMax, Long idleTimeMin,
            Long continueLimit, Long addCount, Long delCount) {
        // TODO 自動生成されたメソッド・スタブ

    }


    private List<ImageDto> getImages(Platform platform, List<Image> images) {
        // イメージを取得
        List<ImageDto> imageDtos = new ArrayList<ImageDto>();
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

            ImageDto imageDto = new ImageDto();
            imageDto.setImage(image);
            imageDto.setImageAws(imageAwsMap.get(image.getImageNo()));
            imageDto.setImageCloudstack(imageCloudstackMap.get(image.getImageNo()));
            imageDto.setImageVmware(imageVmwareMap.get(image.getImageNo()));
            imageDto.setImageNifty(imageNiftyMap.get(image.getImageNo()));
            imageDtos.add(imageDto);
        }

        return imageDtos;
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
        List<PlatformCloudstack> platformCloudstacks = XmlDataLoader.getData("platformNifty.xml", PlatformCloudstack.class);
        LinkedHashMap<Long, PlatformCloudstack> map = new LinkedHashMap<Long, PlatformCloudstack>();
        for (PlatformCloudstack platformCloudstack: platformCloudstacks) {
            map.put(platformCloudstack.getPlatformNo(), platformCloudstack);
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

    @Override
    public Long getLoadBalancerInstance(Long loadBalancerNo) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<SslKeyDto> getSSLKey(Long loadBalancerNo) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

}
