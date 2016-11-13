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
package jp.primecloud.auto.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AutoScalingConf;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.AwsSslKey;
import jp.primecloud.auto.entity.crud.CloudstackLoadBalancer;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentLoadBalancer;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageAzure;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.ImageOpenstack;
import jp.primecloud.auto.entity.crud.ImageVcloud;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PlatformAzure;
import jp.primecloud.auto.entity.crud.PlatformCloudstack;
import jp.primecloud.auto.entity.crud.PlatformNifty;
import jp.primecloud.auto.entity.crud.PlatformOpenstack;
import jp.primecloud.auto.entity.crud.PlatformVcloud;
import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.log.EventLogLevel;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.hook.ProcessHook;
import jp.primecloud.auto.process.zabbix.ZabbixProcessClient;
import jp.primecloud.auto.process.zabbix.ZabbixProcessClientFactory;
import jp.primecloud.auto.service.AwsDescribeService;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.dto.AutoScalingConfDto;
import jp.primecloud.auto.service.dto.ComponentLoadBalancerDto;
import jp.primecloud.auto.service.dto.ImageDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.LoadBalancerPlatformDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.service.dto.SslKeyDto;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class LoadBalancerServiceImpl extends ServiceSupport implements LoadBalancerService {

    protected ComponentService componentService;

    protected InstanceService instanceService;

    protected AwsDescribeService awsDescribeService;

    protected EventLogger eventLogger;

    protected ZabbixProcessClientFactory zabbixProcessClientFactory;

    protected ProcessHook processHook;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LoadBalancerDto> getLoadBalancers(Long farmNo) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }

        List<LoadBalancer> loadBalancers = loadBalancerDao.readByFarmNo(farmNo);

        // ロードバランサ番号のリスト
        List<Long> loadBalancerNos = new ArrayList<Long>();
        for (LoadBalancer loadBalancer : loadBalancers) {
            loadBalancerNos.add(loadBalancer.getLoadBalancerNo());
        }

        // プラットフォーム情報(AWS)を取得
        List<PlatformAws> platformAwss = platformAwsDao.readAll();
        Map<Long, PlatformAws> platformAwsMap = new LinkedHashMap<Long, PlatformAws>();
        for (PlatformAws platformAws : platformAwss) {
            platformAwsMap.put(platformAws.getPlatformNo(), platformAws);
        }

        // プラットフォーム情報(VMWare)を取得
        List<PlatformVmware> platformVmwares = platformVmwareDao.readAll();
        Map<Long, PlatformVmware> platformVmwareMap = new LinkedHashMap<Long, PlatformVmware>();
        for (PlatformVmware platformVmware : platformVmwares) {
            platformVmwareMap.put(platformVmware.getPlatformNo(), platformVmware);
        }

        // プラットフォーム情報(Nifty)を取得
        List<PlatformNifty> platformNifties = platformNiftyDao.readAll();
        Map<Long, PlatformNifty> platformNiftyMap = new LinkedHashMap<Long, PlatformNifty>();
        for (PlatformNifty platformNifty : platformNifties) {
            platformNiftyMap.put(platformNifty.getPlatformNo(), platformNifty);
        }

        // プラットフォーム情報(CloudStack)を取得
        List<PlatformCloudstack> platformCloudstacks = platformCloudstackDao.readAll();
        Map<Long, PlatformCloudstack> platformCloudstackMap = new LinkedHashMap<Long, PlatformCloudstack>();
        for (PlatformCloudstack platformCloudstack : platformCloudstacks) {
            platformCloudstackMap.put(platformCloudstack.getPlatformNo(), platformCloudstack);
        }

        // プラットフォーム情報(Vcloud)を取得
        List<PlatformVcloud> platformVclouds = platformVcloudDao.readAll();
        Map<Long, PlatformVcloud> platformVcloudMap = new LinkedHashMap<Long, PlatformVcloud>();
        for (PlatformVcloud platformVcloud : platformVclouds) {
            platformVcloudMap.put(platformVcloud.getPlatformNo(), platformVcloud);
        }

        // プラットフォーム情報(Azure)を取得
        List<PlatformAzure> platformAzures = platformAzureDao.readAll();
        Map<Long, PlatformAzure> platformAzureMap = new LinkedHashMap<Long, PlatformAzure>();
        for (PlatformAzure platformAzure : platformAzures) {
            platformAzureMap.put(platformAzure.getPlatformNo(), platformAzure);
        }

        // プラットフォーム情報を取得
        List<Platform> platforms = platformDao.readAll();

        // PlatformDtoを作成
        Map<Long, PlatformDto> platformDtoMap = new LinkedHashMap<Long, PlatformDto>();
        for (Platform platform : platforms) {
            PlatformDto platformDto = new PlatformDto();
            platformDto.setPlatform(platform);
            platformDto.setPlatformAws(platformAwsMap.get(platform.getPlatformNo()));
            platformDto.setPlatformCloudstack(platformCloudstackMap.get(platform.getPlatformNo()));
            platformDto.setPlatformVmware(platformVmwareMap.get(platform.getPlatformNo()));
            platformDto.setPlatformNifty(platformNiftyMap.get(platform.getPlatformNo()));
            platformDto.setPlatformVcloud(platformVcloudMap.get(platform.getPlatformNo()));
            platformDto.setPlatformAzure(platformAzureMap.get(platform.getPlatformNo()));
            platformDtoMap.put(platform.getPlatformNo(), platformDto);
        }

        // イメージ情報(AWS)を取得
        List<ImageAws> imageAwses = imageAwsDao.readAll();
        Map<Long, ImageAws> imageAwsMap = new LinkedHashMap<Long, ImageAws>();
        for (ImageAws imageAws : imageAwses) {
            imageAwsMap.put(imageAws.getImageNo(), imageAws);
        }

        // イメージ情報(Cloudstack)を取得
        List<ImageCloudstack> imageCloudstacks = imageCloudstackDao.readAll();
        Map<Long, ImageCloudstack> imageCloudstackMap = new LinkedHashMap<Long, ImageCloudstack>();
        for (ImageCloudstack imageCloudstack : imageCloudstacks) {
            imageCloudstackMap.put(imageCloudstack.getImageNo(), imageCloudstack);
        }

        // イメージ情報(VMWare)を取得
        List<ImageVmware> imageVmwares = imageVmwareDao.readAll();
        Map<Long, ImageVmware> imageVmwareMap = new LinkedHashMap<Long, ImageVmware>();
        for (ImageVmware imageVmware : imageVmwares) {
            imageVmwareMap.put(imageVmware.getImageNo(), imageVmware);
        }

        // イメージ情報(Nifty)を取得
        List<ImageNifty> imageNifties = imageNiftyDao.readAll();
        Map<Long, ImageNifty> imageNiftyMap = new LinkedHashMap<Long, ImageNifty>();
        for (ImageNifty imageNifty : imageNifties) {
            imageNiftyMap.put(imageNifty.getImageNo(), imageNifty);
        }

        // イメージ情報(VCloud)を取得
        List<ImageVcloud> imageVclouds = imageVcloudDao.readAll();
        Map<Long, ImageVcloud> imageVcloudMap = new LinkedHashMap<Long, ImageVcloud>();
        for (ImageVcloud imageVcloud : imageVclouds) {
            imageVcloudMap.put(imageVcloud.getImageNo(), imageVcloud);
        }

        // イメージ情報(Azure)を取得
        List<ImageAzure> imageAzures = imageAzureDao.readAll();
        Map<Long, ImageAzure> imageAzureMap = new LinkedHashMap<Long, ImageAzure>();
        for (ImageAzure imageAzure : imageAzures) {
            imageAzureMap.put(imageAzure.getImageNo(), imageAzure);
        }

        // イメージ情報を取得
        List<Image> images = imageDao.readAll();

        // ImageDto作成
        Map<Long, ImageDto> imageDtoMap = new LinkedHashMap<Long, ImageDto>();
        for (Image image : images) {
            ImageDto imageDto = new ImageDto();
            imageDto.setImage(image);
            imageDto.setImageAws(imageAwsMap.get(image.getImageNo()));
            imageDto.setImageVmware(imageVmwareMap.get(image.getImageNo()));
            imageDto.setImageCloudstack(imageCloudstackMap.get(image.getImageNo()));
            imageDto.setImageNifty(imageNiftyMap.get(image.getImageNo()));
            imageDto.setImageVcloud(imageVcloudMap.get(image.getImageNo()));
            imageDto.setImageAzure(imageAzureMap.get(image.getImageNo()));
            imageDtoMap.put(image.getImageNo(), imageDto);
        }

        // AWSロードバランサ情報を取得
        List<AwsLoadBalancer> awsLoadBalancers = awsLoadBalancerDao.readInLoadBalancerNos(loadBalancerNos);
        Map<Long, AwsLoadBalancer> awsLoadBalancerMap = new LinkedHashMap<Long, AwsLoadBalancer>();
        for (AwsLoadBalancer awsLoadBalancer : awsLoadBalancers) {
            awsLoadBalancerMap.put(awsLoadBalancer.getLoadBalancerNo(), awsLoadBalancer);
        }

        // CloudStackロードバランサ情報を取得
        List<CloudstackLoadBalancer> cloudstackLoadBalancers = cloudstackLoadBalancerDao
                .readInLoadBalancerNos(loadBalancerNos);
        Map<Long, CloudstackLoadBalancer> cloudstackLoadBalancerMap = new LinkedHashMap<Long, CloudstackLoadBalancer>();
        for (CloudstackLoadBalancer cloudstackLoadBalancer : cloudstackLoadBalancers) {
            cloudstackLoadBalancerMap.put(cloudstackLoadBalancer.getLoadBalancerNo(), cloudstackLoadBalancer);
        }

        // コンポーネントロードバランサ情報を取得
        List<ComponentLoadBalancer> componentLoadBalancers = componentLoadBalancerDao
                .readInLoadBalancerNos(loadBalancerNos);
        Map<Long, ComponentLoadBalancer> componentLoadBalancerMap = new LinkedHashMap<Long, ComponentLoadBalancer>();
        for (ComponentLoadBalancer componentLoadBalancer : componentLoadBalancers) {
            componentLoadBalancerMap.put(componentLoadBalancer.getLoadBalancerNo(), componentLoadBalancer);
        }

        // リスナー情報を取得
        List<LoadBalancerListener> allListeners = loadBalancerListenerDao.readInLoadBalancerNos(loadBalancerNos);
        Map<Long, List<LoadBalancerListener>> listenersMap = new LinkedHashMap<Long, List<LoadBalancerListener>>();
        for (LoadBalancerListener listener : allListeners) {
            List<LoadBalancerListener> listeners = listenersMap.get(listener.getLoadBalancerNo());
            if (listeners == null) {
                listeners = new ArrayList<LoadBalancerListener>();
                listenersMap.put(listener.getLoadBalancerNo(), listeners);
            }
            listeners.add(listener);
        }

        // ヘルスチェック情報を取得
        List<LoadBalancerHealthCheck> allHealthChecks = loadBalancerHealthCheckDao
                .readInLoadBalancerNos(loadBalancerNos);
        Map<Long, LoadBalancerHealthCheck> healthCheckMap = new LinkedHashMap<Long, LoadBalancerHealthCheck>();
        for (LoadBalancerHealthCheck healthCheck : allHealthChecks) {
            healthCheckMap.put(healthCheck.getLoadBalancerNo(), healthCheck);
        }

        // オートスケーリング情報を取得
        List<AutoScalingConf> autoScalingConfs = autoScalingConfDao.readInLoadBalancerNos(loadBalancerNos);
        Map<Long, AutoScalingConf> autoScalingConfMap = new LinkedHashMap<Long, AutoScalingConf>();
        for (AutoScalingConf autoScalingConf : autoScalingConfs) {
            autoScalingConfMap.put(autoScalingConf.getLoadBalancerNo(), autoScalingConf);
        }

        // 振り分けインスタンス情報を取得
        List<LoadBalancerInstance> allLbInstances = loadBalancerInstanceDao.readInLoadBalancerNos(loadBalancerNos);
        Map<Long, List<LoadBalancerInstance>> lbInstancesMap = new LinkedHashMap<Long, List<LoadBalancerInstance>>();
        for (LoadBalancerInstance lbInstance : allLbInstances) {
            List<LoadBalancerInstance> lbInstances = lbInstancesMap.get(lbInstance.getLoadBalancerNo());
            if (lbInstances == null) {
                lbInstances = new ArrayList<LoadBalancerInstance>();
                lbInstancesMap.put(lbInstance.getLoadBalancerNo(), lbInstances);
            }
            lbInstances.add(lbInstance);
        }

        // インスタンス情報を取得
        Set<Long> targetInstanceNos = new HashSet<Long>();
        for (LoadBalancerInstance lbInstance : allLbInstances) {
            targetInstanceNos.add(lbInstance.getInstanceNo());
        }
        List<Instance> targetInstances = instanceDao.readInInstanceNos(targetInstanceNos);
        Map<Long, Instance> targetInstanceMap = new HashMap<Long, Instance>();
        for (Instance targetInstance : targetInstances) {
            targetInstanceMap.put(targetInstance.getInstanceNo(), targetInstance);
        }

        List<LoadBalancerDto> dtos = new ArrayList<LoadBalancerDto>();
        for (LoadBalancer loadBalancer : loadBalancers) {

            Long loadBalancerNo = loadBalancer.getLoadBalancerNo();
            AwsLoadBalancer awsLoadBalancer = awsLoadBalancerMap.get(loadBalancerNo);
            CloudstackLoadBalancer cloudstackLoadBalancer = cloudstackLoadBalancerMap.get(loadBalancerNo);
            ComponentLoadBalancer componentLoadBalancer = componentLoadBalancerMap
                    .get(loadBalancer.getLoadBalancerNo());

            List<LoadBalancerListener> listeners = listenersMap.get(loadBalancerNo);
            if (listeners == null) {
                listeners = new ArrayList<LoadBalancerListener>();
            }

            LoadBalancerHealthCheck healthCheck = healthCheckMap.get(loadBalancerNo);

            AutoScalingConfDto autoScalingConfDto = null;
            if (BooleanUtils.toBoolean(Config.getProperty("autoScaling.useAutoScaling"))) {
                autoScalingConfDto = new AutoScalingConfDto();
                AutoScalingConf autoScalingConf = autoScalingConfMap.get(loadBalancerNo);
                autoScalingConfDto.setAutoScalingConf(autoScalingConf);
                autoScalingConfDto.setPlatform(platformDtoMap.get(autoScalingConf.getPlatformNo()));
                autoScalingConfDto.setImage(imageDtoMap.get(autoScalingConf.getImageNo()));
            }

            List<LoadBalancerInstance> lbInstances = lbInstancesMap.get(loadBalancerNo);
            if (lbInstances == null) {
                lbInstances = new ArrayList<LoadBalancerInstance>();
            }

            // コンポーネントロードバランサの詳細情報を取得する
            ComponentLoadBalancerDto componentLoadBalancerDto = null;
            if (componentLoadBalancer != null) {
                Component component = componentDao.read(componentLoadBalancer.getComponentNo());

                List<Long> instanceNos = new ArrayList<Long>();
                List<ComponentInstance> componentInstances = componentInstanceDao
                        .readByComponentNo(componentLoadBalancer.getComponentNo());
                for (ComponentInstance componentInstance : componentInstances) {
                    instanceNos.add(componentInstance.getInstanceNo());
                }
                List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);

                // アクセス用IPアドレス
                String ipAddress = null;
                if (!instances.isEmpty()) {
                    Boolean showPublicIp = BooleanUtils.toBooleanObject(Config.getProperty("ui.showPublicIp"));
                    if (BooleanUtils.isTrue(showPublicIp)) {
                        //ui.showPublicIp = true の場合、URLにPublicIpを表示
                        ipAddress = instances.get(0).getPublicIp();
                    } else {
                        //ui.showPublicIp = false の場合、URLにPrivateIpを表示
                        ipAddress = instances.get(0).getPrivateIp();
                    }
                }

                componentLoadBalancerDto = new ComponentLoadBalancerDto();
                componentLoadBalancerDto.setComponentLoadBalancer(componentLoadBalancer);
                componentLoadBalancerDto.setComponent(component);
                componentLoadBalancerDto.setInstances(instances);
                componentLoadBalancerDto.setIpAddress(ipAddress);
            }

            // ソート
            Collections.sort(listeners, Comparators.COMPARATOR_LOAD_BALANCER_LISTENER);
            Collections.sort(lbInstances, Comparators.COMPARATOR_LOAD_BALANCER_INSTANCE);

            // TODO: 有効無効に応じてステータスを変更する（暫定処理なのでそのうちちゃんと考える）
            // ロードバランサのステータス
            LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                if (status == LoadBalancerStatus.STOPPED) {
                    status = LoadBalancerStatus.STARTING;
                } else if (status == LoadBalancerStatus.RUNNING && BooleanUtils.isTrue(loadBalancer.getConfigure())) {
                    status = LoadBalancerStatus.CONFIGURING;
                }
            } else {
                if (status == LoadBalancerStatus.RUNNING || status == LoadBalancerStatus.WARNING) {
                    status = LoadBalancerStatus.STOPPING;
                }
            }
            loadBalancer.setStatus(status.toString());

            // リスナーのステータス
            for (LoadBalancerListener listener : listeners) {
                LoadBalancerListenerStatus status2 = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
                if (BooleanUtils.isTrue(loadBalancer.getEnabled()) && BooleanUtils.isTrue(listener.getEnabled())) {
                    if (status2 == LoadBalancerListenerStatus.STOPPED) {
                        status2 = LoadBalancerListenerStatus.STARTING;
                    } else if (status2 == LoadBalancerListenerStatus.RUNNING
                            && BooleanUtils.isTrue(listener.getConfigure())) {
                        status2 = LoadBalancerListenerStatus.CONFIGURING;
                    }
                } else {
                    if (status2 == LoadBalancerListenerStatus.RUNNING || status2 == LoadBalancerListenerStatus.WARNING) {
                        status2 = LoadBalancerListenerStatus.STOPPING;
                    }
                }
                listener.setStatus(status2.toString());
            }

            LoadBalancerDto dto = new LoadBalancerDto();
            dto.setLoadBalancer(loadBalancer);
            dto.setPlatform(platformDtoMap.get(loadBalancer.getPlatformNo()));
            dto.setAwsLoadBalancer(awsLoadBalancer);
            dto.setCloudstackLoadBalancer(cloudstackLoadBalancer);
            dto.setComponentLoadBalancerDto(componentLoadBalancerDto);
            dto.setLoadBalancerListeners(listeners);
            dto.setLoadBalancerHealthCheck(healthCheck);
            dto.setLoadBalancerInstances(lbInstances);
            dto.setAutoScalingConf(autoScalingConfDto);
            dtos.add(dto);
        }

        // ソート
        Collections.sort(dtos, Comparators.COMPARATOR_LOAD_BALANCER_DTO);

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLoadBalancerInstance(Long loadBalancerNo) {
        //コンポーネント形ロードバランサのインスタンスIDを取得する
        ComponentLoadBalancer componentLoadBalancer = componentLoadBalancerDao.read(loadBalancerNo);
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentLoadBalancer
                .getComponentNo());
        if (!componentInstances.isEmpty()) {
            return componentInstances.get(0).getInstanceNo();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createAwsLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo,
            Long componentNo, boolean internal) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }
        if (loadBalancerName == null || loadBalancerName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerName");
        }
        if (platformNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "platformNo");
        }
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", loadBalancerName)) {
            throw new AutoApplicationException("ECOMMON-000012", "loadBalancerName");
        }

        // TODO: 長さチェック

        // プラットフォームのチェック
        Platform platform = platformDao.read(platformNo);
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType()) == false) {
            throw new AutoApplicationException("ESERVICE-000606", platformNo);
        }

        // ロードバランサ名の一意チェック
        LoadBalancer checkLoadBalancer = loadBalancerDao.readByFarmNoAndLoadBalancerName(farmNo, loadBalancerName);
        if (checkLoadBalancer != null) {
            // 同名のロードバランサが存在する場合
            throw new AutoApplicationException("ESERVICE-000601", loadBalancerName);
        }

        // インスタンス名のチェック
        Instance checkInstance = instanceDao.readByFarmNoAndInstanceName(farmNo, loadBalancerName);
        if (checkInstance != null) {
            // 同名のインスタンスが存在する場合
            throw new AutoApplicationException("ESERVICE-000626", loadBalancerName);
        }

        // ファームの存在チェック
        Farm farm = farmDao.read(farmNo);
        if (farm == null) {
            throw new AutoApplicationException("ESERVICE-000602", farmNo);
        }

        // コンポーネントの存在チェック
        long countComponent = componentDao.countByComponentNo(componentNo);
        if (countComponent == 0) {
            // コンポーネントが存在しない場合
            throw new AutoApplicationException("ESERVICE-000607", componentNo);
        }

        //AWS認証情報の取得
        AwsCertificate awsCertificate = awsCertificateDao.read(farm.getUserNo(), platformNo);

        //サブネット(デフォルト)の取得
        PlatformAws platformAws = platformAwsDao.read(platformNo);
        String subnetId = null;
        String availabilityZone = null;
        if (platformAws.getVpc() && StringUtils.isNotEmpty(awsCertificate.getDefLbSubnet())) {
            //VPC かつ デフォルトサブネット(ロードバランサ用)が存在する場合
            List<String> defLbSubnets = new ArrayList<String>();
            for (String lbSubnet : awsCertificate.getDefLbSubnet().split(",")) {
                defLbSubnets.add(lbSubnet.trim());
            }
            List<String> zones = new ArrayList<String>();
            StringBuffer subnetBuffer = new StringBuffer();
            StringBuffer zoneBuffer = new StringBuffer();
            List<Subnet> subnets = awsDescribeService.getSubnets(farm.getUserNo(), platformNo);
            for (Subnet subnet : subnets) {
                if (defLbSubnets.contains(subnet.getSubnetId())
                        && zones.contains(subnet.getAvailabilityZone()) == false) {
                    subnetBuffer.append(subnetBuffer.length() > 0 ? "," + subnet.getSubnetId() : subnet.getSubnetId());
                    zoneBuffer.append(zoneBuffer.length() > 0 ? "," + subnet.getAvailabilityZone() : subnet
                            .getAvailabilityZone());
                    zones.add(subnet.getAvailabilityZone());
                }
            }
            //サブネットとゾーンを設定
            subnetId = subnetBuffer.toString();
            availabilityZone = zoneBuffer.toString();
        }

        //セキュリティグループの取得
        String groupName = null;
        if (platformAws.getVpc()) {
            //VPCの場合
            List<SecurityGroup> securityGroups = awsDescribeService.getSecurityGroups(farm.getUserNo(), platformNo);
            for (SecurityGroup securityGroup : securityGroups) {
                if ("default".equals(securityGroup.getGroupName())) {
                    //「default」のセキュリティグループがあれば「default」を設定
                    groupName = securityGroup.getGroupName();
                    break;
                }
            }
            if (groupName == null && securityGroups.size() > 0) {
                //「default」が無ければ1件目
                groupName = securityGroups.get(0).getGroupName();
            }
        }

        // ロードバランサ情報の作成
        LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.setFarmNo(farmNo);
        loadBalancer.setLoadBalancerName(loadBalancerName);
        loadBalancer.setComment(comment);
        loadBalancer.setFqdn(loadBalancerName + "." + farm.getDomainName());
        loadBalancer.setPlatformNo(platformNo);
        loadBalancer.setType(PCCConstant.LOAD_BALANCER_ELB);
        loadBalancer.setEnabled(false);
        loadBalancer.setStatus(LoadBalancerStatus.STOPPED.toString());
        loadBalancer.setComponentNo(componentNo);
        loadBalancerDao.create(loadBalancer);

        Long loadBalancerNo = loadBalancer.getLoadBalancerNo();

        // AWSロードバランサ情報の作成
        AwsLoadBalancer awsLoadBalancer = new AwsLoadBalancer();
        awsLoadBalancer.setLoadBalancerNo(loadBalancerNo);
        awsLoadBalancer.setName(loadBalancerName + "-" + loadBalancerNo);
        awsLoadBalancer.setDnsName(null);
        awsLoadBalancer.setSubnetId(subnetId);
        awsLoadBalancer.setSecurityGroups(groupName);
        awsLoadBalancer.setAvailabilityZone(availabilityZone);
        awsLoadBalancer.setInternal(internal);
        awsLoadBalancerDao.create(awsLoadBalancer);

        // 標準のヘルスチェック情報を作成
        createDefaultHealthCheck(loadBalancer);

        // 標準のオートスケーリング情報を作成
        createDefaultAutoScalingConf(loadBalancer);

        // 振り分け対象のインスタンスを登録
        registerInstances(loadBalancer);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farmNo, farm.getFarmName(), null, null, null, null, "LoadBalancerCreate",
                null, null,
                new Object[] { loadBalancerName, platform.getPlatformName(), PCCConstant.LOAD_BALANCER_ELB });

        // フック処理の実行
        processHook.execute("post-create-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);

        return loadBalancerNo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createCloudstackLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo,
            Long componentNo) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }
        if (loadBalancerName == null || loadBalancerName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerName");
        }
        if (platformNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "platformNo");
        }
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", loadBalancerName)) {
            throw new AutoApplicationException("ECOMMON-000012", "loadBalancerName");
        }

        // TODO: 長さチェック

        // プラットフォームのチェック
        Platform platform = platformDao.read(platformNo);
        if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType()) == false) {
            throw new AutoApplicationException("ESERVICE-000606", platformNo);
        }

        // ロードバランサ名の一意チェック
        LoadBalancer checkLoadBalancer = loadBalancerDao.readByFarmNoAndLoadBalancerName(farmNo, loadBalancerName);
        if (checkLoadBalancer != null) {
            // 同名のロードバランサが存在する場合
            throw new AutoApplicationException("ESERVICE-000601", loadBalancerName);
        }

        // インスタンス名のチェック
        Instance checkInstance = instanceDao.readByFarmNoAndInstanceName(farmNo, loadBalancerName);
        if (checkInstance != null) {
            // 同名のインスタンスが存在する場合
            throw new AutoApplicationException("ESERVICE-000626", loadBalancerName);
        }

        // ファームの存在チェック
        Farm farm = farmDao.read(farmNo);
        if (farm == null) {
            throw new AutoApplicationException("ESERVICE-000602", farmNo);
        }

        // コンポーネントの存在チェック
        long countComponent = componentDao.countByComponentNo(componentNo);
        if (countComponent == 0) {
            // コンポーネントが存在しない場合
            throw new AutoApplicationException("ESERVICE-000607", componentNo);
        }

        // ロードバランサ情報の作成
        LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.setFarmNo(farmNo);
        loadBalancer.setLoadBalancerName(loadBalancerName);
        loadBalancer.setComment(comment);
        loadBalancer.setFqdn(loadBalancerName + "." + farm.getDomainName());
        loadBalancer.setPlatformNo(platformNo);
        loadBalancer.setType(PCCConstant.LOAD_BALANCER_CLOUDSTACK);
        loadBalancer.setEnabled(false);
        loadBalancer.setStatus(LoadBalancerStatus.STOPPED.toString());
        loadBalancer.setComponentNo(componentNo);
        loadBalancerDao.create(loadBalancer);

        Long loadBalancerNo = loadBalancer.getLoadBalancerNo();

        PlatformCloudstack platformCloudstack = platformCloudstackDao.read(platformNo);
        // Cloudstackロードバランサ情報の作成
        //"NAME", "ALGORITHM", "ZONEID", "PUBLICPORT", "PRIVATEPORT"の情報が必要
        //今は固定する

        CloudstackLoadBalancer cloudstackLoadBalancer = new CloudstackLoadBalancer();
        cloudstackLoadBalancer.setLoadBalancerNo(loadBalancerNo);
        cloudstackLoadBalancer.setName(loadBalancerName + "-" + loadBalancerNo);
        cloudstackLoadBalancer.setAlgorithm("roundrobin");
        cloudstackLoadBalancer.setZoneid(platformCloudstack.getZoneId());
        cloudstackLoadBalancer.setPublicport("80");
        cloudstackLoadBalancer.setPrivateport("80");

        cloudstackLoadBalancerDao.create(cloudstackLoadBalancer);

        // 標準のオートスケーリング情報を作成
        createDefaultAutoScalingConf(loadBalancer);

        // 振り分け対象のインスタンスを登録
        registerInstances(loadBalancer);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farmNo, farm.getFarmName(), null, null, null, null, "LoadBalancerCreate",
                null, null,
                new Object[] { loadBalancerName, platform.getPlatformName(), PCCConstant.LOAD_BALANCER_ELB });

        // フック処理の実行
        processHook.execute("post-create-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);

        return loadBalancerNo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createUltraMonkeyLoadBalancer(Long farmNo, String loadBalancerName, String comment, Long platformNo,
            Long componentNo) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }
        if (loadBalancerName == null || loadBalancerName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerName");
        }
        if (platformNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "platformNo");
        }
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", loadBalancerName)) {
            throw new AutoApplicationException("ECOMMON-000012", "loadBalancerName");
        }

        // TODO: 長さチェック

        // イメージの存在チェック
        Image image = null;
        List<Image> images = imageDao.readAll();
        for (Image image2 : images) {
            if (image2.getPlatformNo().equals(platformNo.longValue())
                    && PCCConstant.IMAGE_NAME_ULTRAMONKEY.equals(image2.getImageName())) {
                image = image2;
                break;
            }
        }
        if (image == null) {
            // UltraMonkeyイメージが存在しない場合
            throw new AutoApplicationException("ESERVICE-000625", platformNo);
        }

        // ロードバランサ名の一意チェック
        LoadBalancer checkLoadBalancer = loadBalancerDao.readByFarmNoAndLoadBalancerName(farmNo, loadBalancerName);
        if (checkLoadBalancer != null) {
            // 同名のロードバランサが存在する場合
            throw new AutoApplicationException("ESERVICE-000601", loadBalancerName);
        }

        // インスタンス名のチェック
        Instance checkInstance = instanceDao.readByFarmNoAndInstanceName(farmNo, loadBalancerName);
        if (checkInstance != null) {
            // 同名のインスタンスが存在する場合
            throw new AutoApplicationException("ESERVICE-000626", loadBalancerName);
        }

        // ファームの存在チェック
        Farm farm = farmDao.read(farmNo);
        if (farm == null) {
            throw new AutoApplicationException("ESERVICE-000602", farmNo);
        }

        // コンポーネントの存在チェック
        long countComponent = componentDao.countByComponentNo(componentNo);
        if (countComponent == 0) {
            // コンポーネントが存在しない場合
            throw new AutoApplicationException("ESERVICE-000607", componentNo);
        }

        // UltraMonkeyインスタンス情報の作成
        //String lbInstanceName = "lb-" + loadBalancerNo;
        String lbInstanceName = loadBalancerName;
        Long lbInstanceNo = null;
        Platform platform = platformDao.read(platformNo);
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            ImageAws imageAws = imageAwsDao.read(image.getImageNo());
            String[] instanceTypes = StringUtils.split(imageAws.getInstanceTypes(), ",");
            lbInstanceNo = instanceService.createIaasInstance(farmNo, lbInstanceName, platformNo, null,
                    image.getImageNo(), instanceTypes[0].trim());
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            ImageVmware imageVmware = imageVmwareDao.read(image.getImageNo());
            String[] instanceTypes = StringUtils.split(imageVmware.getInstanceTypes(), ",");
            lbInstanceNo = instanceService.createVmwareInstance(farmNo, lbInstanceName, platformNo, null,
                    image.getImageNo(), instanceTypes[0].trim());
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            ImageNifty imageNifty = imageNiftyDao.read(image.getImageNo());
            String[] instanceTypes = StringUtils.split(imageNifty.getInstanceTypes(), ",");
            lbInstanceNo = instanceService.createNiftyInstance(farmNo, lbInstanceName, platformNo, null,
                    image.getImageNo(), instanceTypes[0].trim());
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            ImageCloudstack imageCloudstack = imageCloudstackDao.read(image.getImageNo());
            String[] instanceTypes = StringUtils.split(imageCloudstack.getInstanceTypes(), ",");
            lbInstanceNo = instanceService.createIaasInstance(farmNo, lbInstanceName, platformNo, null,
                    image.getImageNo(), instanceTypes[0].trim());
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            ImageVcloud imageVcloud = imageVcloudDao.read(image.getImageNo());
            String[] instanceTypes = StringUtils.split(imageVcloud.getInstanceTypes(), ",");
            lbInstanceNo = instanceService.createIaasInstance(farmNo, lbInstanceName, platformNo, null,
                    image.getImageNo(), instanceTypes[0].trim());
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            ImageAzure imageAzure = imageAzureDao.read(image.getImageNo());
            String[] instanceTypes = StringUtils.split(imageAzure.getInstanceTypes(), ",");
            lbInstanceNo = instanceService.createIaasInstance(farmNo, lbInstanceName, platformNo, null,
                    image.getImageNo(), instanceTypes[0].trim());
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            ImageOpenstack imageOpenstack = imageOpenstackDao.read(image.getImageNo());
            String[] instanceTypes = StringUtils.split(imageOpenstack.getInstanceTypes(), ",");
            lbInstanceNo = instanceService.createIaasInstance(farmNo, lbInstanceName, platformNo, null,
                    image.getImageNo(), instanceTypes[0].trim());
        }
        // UltraMonkeyインスタンス情報のロードバランサフラグを立てる
        Instance lbInstance = instanceDao.read(lbInstanceNo);
        lbInstance.setLoadBalancer(true);
        instanceDao.update(lbInstance);

        // ロードバランサ情報の作成
        LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.setFarmNo(farmNo);
        loadBalancer.setLoadBalancerName(loadBalancerName);
        loadBalancer.setComment(comment);
        loadBalancer.setFqdn(loadBalancerName + "." + farm.getDomainName());
        loadBalancer.setPlatformNo(platformNo);
        loadBalancer.setType(PCCConstant.LOAD_BALANCER_ULTRAMONKEY);
        loadBalancer.setEnabled(false);
        loadBalancer.setStatus(LoadBalancerStatus.STOPPED.toString());
        loadBalancer.setComponentNo(componentNo);
        loadBalancerDao.create(loadBalancer);

        Long loadBalancerNo = loadBalancer.getLoadBalancerNo();

        // UltraMonkeyコンポーネント情報の作成
        String lbComponentName = "lb-" + loadBalancerNo;
        Long lbComponentTypeNo = Long.valueOf(image.getComponentTypeNos());
        Long lbComponentNo = componentService.createComponent(farmNo, lbComponentName, lbComponentTypeNo, null, null);

        // UltraMonkeyコンポーネント情報のロードバランサフラグを立てる
        Component lbComponent = componentDao.read(lbComponentNo);
        lbComponent.setLoadBalancer(true);
        componentDao.update(lbComponent);

        // コンポーネントとインスタンスの関連を作成
        componentService.associateInstances(lbComponentNo, Arrays.asList(lbInstanceNo));

        // コンポーネントロードバランサ情報の作成
        ComponentLoadBalancer componentLoadBalancer = new ComponentLoadBalancer();
        componentLoadBalancer.setLoadBalancerNo(loadBalancerNo);
        componentLoadBalancer.setComponentNo(lbComponentNo);
        componentLoadBalancerDao.create(componentLoadBalancer);

        // 標準のヘルスチェック情報を作成
        createDefaultHealthCheck(loadBalancer);

        // 標準のオートスケーリング情報を作成
        createDefaultAutoScalingConf(loadBalancer);

        // 振り分け対象のインスタンスを登録
        registerInstances(loadBalancer);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farmNo, farm.getFarmName(), null, null, null, null, "LoadBalancerCreate",
                null, null, new Object[] { loadBalancerName, platform.getPlatformName(),
                        PCCConstant.LOAD_BALANCER_ULTRAMONKEY });

        // フック処理の実行
        processHook.execute("post-create-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);

        return loadBalancerNo;
    }

    protected void createDefaultHealthCheck(LoadBalancer loadBalancer) {
        Component component = componentDao.read(loadBalancer.getComponentNo());
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());

        String checkProtocol;
        Integer checkPort;
        String checkPath = null;
        Integer checkTimeout = 5;
        Integer checkInterval = 10;
        Integer healthyThreshold = 3;
        Integer unhealthyThreshold = 2;

        if ("apache".equals(componentType.getComponentTypeName())) {
            checkProtocol = "HTTP";
            checkPort = 80;
            checkPath = "/index.html";
        } else if ("tomcat".equals(componentType.getComponentTypeName())
                || "geronimo".equals(componentType.getComponentTypeName())) {
            checkProtocol = "HTTP";
            checkPort = 8080;
            checkPath = "/index.html";
        } else if ("mysql".equals(componentType.getComponentTypeName())) {
            checkProtocol = "TCP";
            checkPort = 3306;
        } else {
            return;
        }

        configureHealthCheck(loadBalancer.getLoadBalancerNo(), checkProtocol, checkPort, checkPath, checkTimeout,
                checkInterval, healthyThreshold, unhealthyThreshold);
    }

    protected void createDefaultAutoScalingConf(LoadBalancer loadBalancer) {
        // オートスケーリング情報の登録/更新
        AutoScalingConf autoScalingConf = autoScalingConfDao.read(loadBalancer.getLoadBalancerNo());
        boolean exist = (autoScalingConf != null);
        if (!exist) {
            autoScalingConf = new AutoScalingConf();
            autoScalingConf.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
            autoScalingConf.setFarmNo(loadBalancer.getFarmNo());
        }

        autoScalingConf.setPlatformNo(0L);
        autoScalingConf.setImageNo(0L);
        //autoScalingConf.setInstanceType(null);
        //autoScalingConf.setNamingRule(null;)
        autoScalingConf.setIdleTimeMax(0L);
        autoScalingConf.setIdleTimeMin(0L);
        autoScalingConf.setContinueLimit(0L);
        autoScalingConf.setAddCount(0L);
        autoScalingConf.setDelCount(0L);
        autoScalingConf.setEnabled(false);

        autoScalingConfDao.create(autoScalingConf);

    }

    protected void registerInstances(LoadBalancer loadBalancer) {
        // コンポーネントに関連付けられたインスタンス情報を取得
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(loadBalancer
                .getComponentNo());
        List<Long> instanceNos = new ArrayList<Long>();
        for (ComponentInstance componentInstance : componentInstances) {
            if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                continue;
            }
            instanceNos.add(componentInstance.getInstanceNo());
        }
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);

        if (instanceNos.isEmpty()) {
            return;
        }

        // ロードバランサに割り当て可能なインスタンスを抽出
        List<LoadBalancer> allLoadBalancers = loadBalancerDao.readByFarmNo(loadBalancer.getFarmNo());
        List<Long> loadBalancerNos = new ArrayList<Long>();
        for (LoadBalancer tmpLoadBalancer : allLoadBalancers) {
            loadBalancerNos.add(tmpLoadBalancer.getLoadBalancerNo());
        }
        List<LoadBalancerInstance> allLbInstances = loadBalancerInstanceDao.readInLoadBalancerNos(loadBalancerNos);

        List<Instance> tmpInstances = new ArrayList<Instance>();
        for (Instance instance : instances) {
            boolean check = checkAssociate(loadBalancer, instance, allLoadBalancers, allLbInstances);
            if (check) {
                tmpInstances.add(instance);
            }
        }
        instances = tmpInstances;

        // ロードバランサとインスタンスの関連を作成
        for (Instance instance : instances) {
            LoadBalancerInstance lbInstance = new LoadBalancerInstance();
            lbInstance.setLoadBalancerNo(loadBalancer.getLoadBalancerNo());
            lbInstance.setInstanceNo(instance.getInstanceNo());
            lbInstance.setEnabled(true);
            lbInstance.setStatus(LoadBalancerInstanceStatus.STOPPED.toString());
            loadBalancerInstanceDao.create(lbInstance);
        }
    }

    protected boolean checkAssociate(LoadBalancer loadBalancer, Instance instance, List<LoadBalancer> allLoadBalancers,
            List<LoadBalancerInstance> allLbInstances) {
        Map<Long, LoadBalancer> allLoadBalancerMap = new HashMap<Long, LoadBalancer>();
        for (LoadBalancer loadBalancer2 : allLoadBalancers) {
            allLoadBalancerMap.put(loadBalancer2.getLoadBalancerNo(), loadBalancer2);
        }

        // AWSロードバランサの場合
        String type = loadBalancer.getType();
        if (PCCConstant.LOAD_BALANCER_ELB.equals(type)) {
            // ロードバランサとインスタンスのプラットフォームが異なる場合は振り分け不可
            if (!loadBalancer.getPlatformNo().equals(instance.getPlatformNo())) {
                return false;
            }

            // 他のAWSロードバランサの振り分け対象になっていないかどうか
            for (LoadBalancerInstance lbInstance : allLbInstances) {
                if (!instance.getInstanceNo().equals(lbInstance.getInstanceNo())) {
                    continue;
                }

                // 同じAWSロードバランサでの振り分けの場合はチェックしない
                if (loadBalancer.getLoadBalancerNo().equals(lbInstance.getLoadBalancerNo())) {
                    continue;
                }

                // 振り分けが無効で停止している場合はチェックしない
                if (BooleanUtils.isNotTrue(lbInstance.getEnabled())
                        && LoadBalancerInstanceStatus.fromStatus(lbInstance.getStatus()) == LoadBalancerInstanceStatus.STOPPED) {
                    continue;
                }

                LoadBalancer loadBalancer2 = allLoadBalancerMap.get(lbInstance.getLoadBalancerNo());
                String type2 = loadBalancer2.getType();
                if (PCCConstant.LOAD_BALANCER_ELB.equals(type2) || PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(type2)) {
                    // 異なるAWSロードバランサの振り分け対象になっている場合は、振り分け不可
                    return false;
                }
            }

            //VPCの場合のゾーンのチェック
            PlatformAws platformAws = platformAwsDao.read(loadBalancer.getPlatformNo());
            if (platformAws.getVpc()) {
                AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancer.getLoadBalancerNo());
                AwsInstance awsInstance = awsInstanceDao.read(instance.getInstanceNo());
                if (StringUtils.isEmpty(awsLoadBalancer.getAvailabilityZone())) {
                    //ロードバランサのゾーンが存在しない場合(デフォルトサブネットが存在しない場合)は割り当て不可(割り当てない)
                    return false;
                } else {
                    //ロードバランサのゾーンが存在する場合(デフォルトサブネットが存在する場合)
                    List<String> zones = new ArrayList<String>();
                    for (String zone : awsLoadBalancer.getAvailabilityZone().split(",")) {
                        zones.add(zone.trim());
                    }
                    if (zones.contains(awsInstance.getAvailabilityZone()) == false) {
                        //サーバのゾーンが違う場合(ロードバランサのゾーンに含まれていない場合)は割り当て不可
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAwsLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment, Long componentNo,
            String subnetId, String securityGroupName, String availabilityZone, boolean internal) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (loadBalancerName == null || loadBalancerName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerName");
        }
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", loadBalancerName)) {
            throw new AutoApplicationException("ECOMMON-000012", "loadBalancerName");
        }

        // TODO: 長さチェック

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null || awsLoadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        // ロードバランサが停止状態でない場合
        if (LoadBalancerStatus.fromStatus(loadBalancer.getStatus()) != LoadBalancerStatus.STOPPED) {
            // 停止状態でないと変更できないものを変更していないかチェック
            if (!StringUtils.equals(loadBalancer.getLoadBalancerName(), loadBalancerName)
                    || !componentNo.equals(loadBalancer.getComponentNo())) {
                throw new AutoApplicationException("ESERVICE-000604", loadBalancer.getLoadBalancerName());
            }
            if (StringUtils.isEmpty(awsLoadBalancer.getSubnetId()) ? StringUtils.isNotEmpty(subnetId) : !StringUtils
                    .equals(awsLoadBalancer.getSubnetId(), subnetId)) {
                throw new AutoApplicationException("ESERVICE-000604", loadBalancer.getLoadBalancerName());
            }
            if (StringUtils.isEmpty(awsLoadBalancer.getSecurityGroups()) ? StringUtils.isNotEmpty(securityGroupName)
                    : !StringUtils.equals(awsLoadBalancer.getSecurityGroups(), securityGroupName)) {
                throw new AutoApplicationException("ESERVICE-000604", loadBalancer.getLoadBalancerName());
            }
        }

        // ロードバランサ名を変更する場合
        if (!StringUtils.equals(loadBalancer.getLoadBalancerName(), loadBalancerName)) {
            // ロードバランサ名の一意チェック
            LoadBalancer checkLoadBalancer = loadBalancerDao.readByFarmNoAndLoadBalancerName(loadBalancer.getFarmNo(),
                    loadBalancerName);
            if (checkLoadBalancer != null && !loadBalancerNo.equals(checkLoadBalancer.getLoadBalancerNo())) {
                // 同名のロードバランサが存在する場合
                throw new AutoApplicationException("ESERVICE-000601", loadBalancerName);
            }

            // インスタンス名のチェック
            Instance checkInstance = instanceDao
                    .readByFarmNoAndInstanceName(loadBalancer.getFarmNo(), loadBalancerName);
            if (checkInstance != null) {
                // 同名のインスタンスが存在する場合
                throw new AutoApplicationException("ESERVICE-000626", loadBalancerName);
            }
        }

        // コンポーネントの存在チェック
        long countComponent = componentDao.countByComponentNo(componentNo);
        if (countComponent == 0) {
            // コンポーネントが存在しない場合
            throw new AutoApplicationException("ESERVICE-000607", componentNo);
        }

        // リスナーが存在する場合はコンポーネントを変更できない
        long countListener = loadBalancerListenerDao.countByLoadBalancerNo(loadBalancerNo);
        if (countListener > 0) {
            if (!loadBalancer.getComponentNo().equals(componentNo)) {
                // コンポーネントを変更しようとした場合
                throw new AutoApplicationException("ESERVICE-000608");
            }
        }

        // フック処理の実行
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        processHook.execute("pre-update-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);

        //ロードバランサーインスタンス更新
        if (loadBalancer.getComponentNo().equals(componentNo) == false
                || (StringUtils.isNotEmpty(awsLoadBalancer.getAvailabilityZone()) && StringUtils.equals(
                        awsLoadBalancer.getAvailabilityZone(), availabilityZone) == false)) {
            //割り当てサービス変更時、またはサブネット変更時にロードバランサインスタンスのEnableをFalseにする
            List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao
                    .readByLoadBalancerNo(loadBalancerNo);
            for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
                loadBalancerInstance.setEnabled(false);
                loadBalancerInstanceDao.update(loadBalancerInstance);
            }
        }

        //AWSロードバランサ更新
        awsLoadBalancer.setSubnetId(subnetId);
        awsLoadBalancer.setSecurityGroups(securityGroupName);
        awsLoadBalancer.setAvailabilityZone(availabilityZone);
        awsLoadBalancer.setInternal(internal);
        awsLoadBalancerDao.update(awsLoadBalancer);

        // ロードバランサの更新
        loadBalancer.setLoadBalancerName(loadBalancerName);
        loadBalancer.setComment(comment);
        loadBalancer.setFqdn(loadBalancerName + "." + farm.getDomainName());
        loadBalancer.setComponentNo(componentNo);
        loadBalancerDao.update(loadBalancer);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                "LoadBalancerUpdate", null, null, new Object[] { loadBalancerName });

        // フック処理の実行
        processHook.execute("post-update-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateCloudstackLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment,
            Long componentNo, String algorithm, String pubricPort, String privatePort) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (loadBalancerName == null || loadBalancerName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerName");
        }
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        if (algorithm == null) {
            throw new AutoApplicationException("ECOMMON-000003", "algorithm");
        }

        if (pubricPort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "pubricPort");
        }

        if (privatePort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "privatePort");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", loadBalancerName)) {
            throw new AutoApplicationException("ECOMMON-000012", "loadBalancerName");
        }

        // TODO: 長さチェック

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        CloudstackLoadBalancer csloadBalancer = cloudstackLoadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        // ロードバランサが停止状態でない場合
        if (LoadBalancerStatus.fromStatus(loadBalancer.getStatus()) != LoadBalancerStatus.STOPPED) {
            // 停止状態でないと変更できないものを変更していないかチェック
            if (!StringUtils.equals(loadBalancer.getLoadBalancerName(), loadBalancerName)
                    || !componentNo.equals(loadBalancer.getComponentNo())) {
                throw new AutoApplicationException("ESERVICE-000604", loadBalancer.getLoadBalancerName());
            }
            if (!StringUtils.equals(csloadBalancer.getAlgorithm(), algorithm)
                    || !StringUtils.equals(csloadBalancer.getPublicport(), pubricPort)
                    || !StringUtils.equals(csloadBalancer.getPrivateport(), privatePort)) {
                throw new AutoApplicationException("ESERVICE-000604", loadBalancer.getLoadBalancerName());
            }
        }

        // ロードバランサ名を変更する場合
        if (!StringUtils.equals(loadBalancer.getLoadBalancerName(), loadBalancerName)) {
            // ロードバランサ名の一意チェック
            LoadBalancer checkLoadBalancer = loadBalancerDao.readByFarmNoAndLoadBalancerName(loadBalancer.getFarmNo(),
                    loadBalancerName);
            if (checkLoadBalancer != null && !loadBalancerNo.equals(checkLoadBalancer.getLoadBalancerNo())) {
                // 同名のロードバランサが存在する場合
                throw new AutoApplicationException("ESERVICE-000601", loadBalancerName);
            }

            // インスタンス名のチェック
            Instance checkInstance = instanceDao
                    .readByFarmNoAndInstanceName(loadBalancer.getFarmNo(), loadBalancerName);
            if (checkInstance != null) {
                // 同名のインスタンスが存在する場合
                throw new AutoApplicationException("ESERVICE-000626", loadBalancerName);
            }
        }

        // コンポーネントの存在チェック
        long countComponent = componentDao.countByComponentNo(componentNo);
        if (countComponent == 0) {
            // コンポーネントが存在しない場合
            throw new AutoApplicationException("ESERVICE-000607", componentNo);
        }

        // フック処理の実行
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        processHook.execute("pre-update-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);

        // ロードバランサの更新
        loadBalancer.setLoadBalancerName(loadBalancerName);
        loadBalancer.setComment(comment);
        loadBalancer.setFqdn(loadBalancerName + "." + farm.getDomainName());
        loadBalancer.setComponentNo(componentNo);
        loadBalancerDao.update(loadBalancer);

        // ロードバランサの更新
        csloadBalancer.setAlgorithm(algorithm);
        csloadBalancer.setPublicport(pubricPort);
        csloadBalancer.setPrivateport(privatePort);
        cloudstackLoadBalancerDao.update(csloadBalancer);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                "LoadBalancerUpdate", null, null, new Object[] { loadBalancerName });

        // フック処理の実行
        processHook.execute("post-update-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUltraMonkeyLoadBalancer(Long loadBalancerNo, String loadBalancerName, String comment,
            Long componentNo) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (loadBalancerName == null || loadBalancerName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerName");
        }
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", loadBalancerName)) {
            throw new AutoApplicationException("ECOMMON-000012", "loadBalancerName");
        }

        // TODO: 長さチェック

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        // ロードバランサが停止状態でない場合
        if (LoadBalancerStatus.fromStatus(loadBalancer.getStatus()) != LoadBalancerStatus.STOPPED) {
            // 停止状態でないと変更できないものを変更していないかチェック
            if (!StringUtils.equals(loadBalancer.getLoadBalancerName(), loadBalancerName)
                    || !componentNo.equals(loadBalancer.getComponentNo())) {
                throw new AutoApplicationException("ESERVICE-000604", loadBalancer.getLoadBalancerName());
            }
        }

        // ロードバランサ名の一意チェック
        LoadBalancer checkLoadBalancer = loadBalancerDao.readByFarmNoAndLoadBalancerName(loadBalancer.getFarmNo(),
                loadBalancerName);
        if (checkLoadBalancer != null && !loadBalancerNo.equals(checkLoadBalancer.getLoadBalancerNo())) {
            // 同名のロードバランサが存在する場合
            throw new AutoApplicationException("ESERVICE-000601", loadBalancerName);
        }

        // コンポーネントの存在チェック
        long countComponent = componentDao.countByComponentNo(componentNo);
        if (countComponent == 0) {
            // コンポーネントが存在しない場合
            throw new AutoApplicationException("ESERVICE-000607", componentNo);
        }

        // リスナーが存在する場合はコンポーネントを変更できない
        long countListener = loadBalancerListenerDao.countByLoadBalancerNo(loadBalancerNo);
        if (countListener > 0) {
            if (!loadBalancer.getComponentNo().equals(componentNo)) {
                // コンポーネントを変更しようとした場合
                throw new AutoApplicationException("ESERVICE-000608");
            }
        }

        // フック処理の実行
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        processHook.execute("pre-update-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);

        // ロードバランサの更新
        loadBalancer.setLoadBalancerName(loadBalancerName);
        loadBalancer.setComment(comment);
        loadBalancer.setFqdn(loadBalancerName + "." + farm.getDomainName());
        loadBalancer.setComponentNo(componentNo);
        loadBalancerDao.update(loadBalancer);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                "LoadBalancerUpdate", null, null, new Object[] { loadBalancerName });

        // フック処理の実行
        processHook.execute("post-update-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteLoadBalancer(Long loadBalancerNo) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            return;
        }

        // ロードバランサが停止しているかどうかのチェック
        if (LoadBalancerStatus.fromStatus(loadBalancer.getStatus()) != LoadBalancerStatus.STOPPED) {
            // ロードバランサが停止状態でない場合
            throw new AutoApplicationException("ESERVICE-000605", loadBalancer.getLoadBalancerName());
        }

        // フック処理の実行
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        processHook.execute("pre-delete-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);

        // 振り分けインスタンス情報の削除処理
        loadBalancerInstanceDao.deleteByLoadBalancerNo(loadBalancerNo);

        // ヘルスチェック情報の削除処理
        loadBalancerHealthCheckDao.deleteByLoadBalancerNo(loadBalancerNo);

        // オートスケーリング情報の削除処理
        autoScalingConfDao.deleteByLoadBalancerNo(loadBalancerNo);

        // リスナー情報の削除処理
        List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerListener listener : listeners) {
            deleteListener(loadBalancerNo, listener.getLoadBalancerPort());
        }

        // ロードバランサ種別ごとの削除処理
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
            deleteAwsLoadBalancer(loadBalancerNo);
        } else if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(loadBalancer.getType())) {
            deleteCloudstackLoadBalancer(loadBalancerNo);
        } else if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancer.getType())) {
            deleteUltraMonkeyLoadBalancer(loadBalancerNo);
        }

        // ロードバランサの削除処理
        loadBalancerDao.delete(loadBalancer);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                "LoadBalancerDelete", null, null, new Object[] { loadBalancer.getLoadBalancerName() });

        // フック処理の実行
        processHook.execute("post-delete-loadbalancer", farm.getUserNo(), farm.getFarmNo(), loadBalancerNo);
    }

    protected void deleteAwsLoadBalancer(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);
        Farm farm = farmDao.read(loadBalancer.getFarmNo());

        // Zabbix関連の削除処理
        try {
            // Zabbixに登録済みの場合、登録を解除する
            ZabbixProcessClient client = zabbixProcessClientFactory.createZabbixProcessClient();
            client.deleteHost(awsLoadbalancer.getHostid());

            //イベントログ出力
            eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                    "ZabbixUnregist", null, loadBalancer.getPlatformNo(), new Object[] { loadBalancer.getFqdn(),
                            awsLoadbalancer.getHostid() });

        } catch (RuntimeException ignore) {
            // 登録解除に失敗した場合、警告ログを出してエラーを握りつぶす
            log.warn(ignore.getMessage());
        }

        // AWSロードバランサ情報の削除処理
        awsLoadBalancerDao.deleteByLoadBalancerNo(loadBalancerNo);

    }

    protected void deleteCloudstackLoadBalancer(Long loadBalancerNo) {
        // Cloudstackロードバランサ情報の削除処理
        cloudstackLoadBalancerDao.deleteByLoadBalancerNo(loadBalancerNo);
    }

    protected void deleteUltraMonkeyLoadBalancer(Long loadBalancerNo) {
        ComponentLoadBalancer componentLoadBalancer = componentLoadBalancerDao.read(loadBalancerNo);
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentLoadBalancer
                .getComponentNo());

        // コンポーネントロードバランサ情報の削除処理
        componentLoadBalancerDao.deleteByLoadBalancerNo(loadBalancerNo);

        // ロードバランサインスタンス情報の削除処理
        for (ComponentInstance componentInstance : componentInstances) {
            instanceService.deleteInstance(componentInstance.getInstanceNo());
        }

        // ロードバランサコンポーネント情報の削除処理
        componentService.deleteComponent(componentLoadBalancer.getComponentNo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createListener(Long loadBalancerNo, Integer loadBalancerPort, Integer servicePort, String protocol,
            Long sslKeyNo) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (loadBalancerPort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerPort");
        }
        if (servicePort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "servicePort");
        }
        if (protocol == null || protocol.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "protocol");
        }

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        // ロードバランサポートのチェック
        if (loadBalancerPort < 1 || 65535 < loadBalancerPort) {
            // サービスポートが範囲外の場合
            throw new AutoApplicationException("ESERVICE-000610");
        }

        // ロードバランサポートの重複チェック
        long countListener = loadBalancerListenerDao.countByLoadBalancerNoAndLoadBalancerPort(loadBalancerNo,
                loadBalancerPort);
        if (countListener != 0) {
            // ロードバランサポートが重複している場合
            throw new AutoApplicationException("ESERVICE-000609", loadBalancerPort);
        }

        // AWSロードバランサの場合のチェック
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
            // SSLキー必須チェック
            if ("HTTPS".equals(protocol) || "SSL".equals(protocol)) {
                if (sslKeyNo == null) {
                    throw new AutoApplicationException("ECOMMON-000003", "sslKey");
                }
            }

        }

        // サービスポートのチェック
        if (servicePort < 1 || 65535 < servicePort) {
            // サービスポートが範囲外の場合
            throw new AutoApplicationException("ESERVICE-000611");
        }

        // プロトコルのチェック
        List<String> protocols = Arrays.asList("TCP", "HTTP", "HTTPS", "SSL");
        if (!protocols.contains(protocol)) {
            // 使用できないプロトコルの場合
            throw new AutoApplicationException("ESERVICE-000612");
        }

        // リスナー情報の作成
        LoadBalancerListener listener = new LoadBalancerListener();
        listener.setLoadBalancerNo(loadBalancerNo);
        listener.setLoadBalancerPort(loadBalancerPort);
        listener.setServicePort(servicePort);
        listener.setProtocol(protocol);
        listener.setSslKeyNo(sslKeyNo);
        loadBalancerListenerDao.create(listener);

        // イベントログ出力
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                "LoadBalancerListenerCreate", null, null, new Object[] { loadBalancer.getLoadBalancerName(),
                        loadBalancerPort });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateListener(Long loadBalancerNo, Integer originalLoadBalancerPort, Integer loadBalancerPort,
            Integer servicePort, String protocol, Long sslKeyNo) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (originalLoadBalancerPort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "originalLoadBalancerPort");
        }
        if (loadBalancerPort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerPort");
        }
        if (servicePort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "servicePort");
        }
        if (protocol == null || protocol.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "protocol");
        }

        // ロードバランサリスナーの存在チェック
        LoadBalancerListener listener = loadBalancerListenerDao.read(loadBalancerNo, originalLoadBalancerPort);
        if (listener == null) {
            // リスナーが存在しない場合
            throw new AutoApplicationException("ESERVICE-000613", originalLoadBalancerPort);
        }

        // リスナーが停止していることのチェック
        if (LoadBalancerListenerStatus.fromStatus(listener.getStatus()) != LoadBalancerListenerStatus.STOPPED) {
            // リスナーが停止状態で無い場合
            throw new AutoApplicationException("ESERVICE-000627");
        }

        // ロードバランサポートのチェック
        if (loadBalancerPort < 1 || 65535 < loadBalancerPort) {
            // サービスポートが範囲外の場合
            throw new AutoApplicationException("ESERVICE-000610");
        }

        if (!originalLoadBalancerPort.equals(loadBalancerPort)) {
            // ロードバランサポートの重複チェック
            long countListener = loadBalancerListenerDao.countByLoadBalancerNoAndLoadBalancerPort(loadBalancerNo,
                    loadBalancerPort);
            if (countListener != 0) {
                // ロードバランサポートが重複している場合
                throw new AutoApplicationException("ESERVICE-000609", loadBalancerPort);
            }
        }

        // AWSロードバランサの場合のチェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
            // SSLキー必須チェック
            if ("HTTPS".equals(protocol) || "SSL".equals(protocol)) {
                if (sslKeyNo == null) {
                    throw new AutoApplicationException("ECOMMON-000003", "sslKey");
                }
            }
        }

        // サービスポートのチェック
        if (servicePort < 1 || 65535 < servicePort) {
            // サービスポートが範囲外の場合
            throw new AutoApplicationException("ESERVICE-000611");
        }

        // プロトコルのチェック
        List<String> protocols = Arrays.asList("TCP", "HTTP", "HTTPS");
        if (!protocols.contains(protocol)) {
            // 使用できないプロトコルの場合
            throw new AutoApplicationException("ESERVICE-000612");
        }

        // リスナー情報の変更
        if (originalLoadBalancerPort.equals(loadBalancerPort)) {
            listener.setServicePort(servicePort);
            listener.setProtocol(protocol);
            listener.setSslKeyNo(sslKeyNo);
            loadBalancerListenerDao.update(listener);
        } else {
            // ロードバランサポートを変更する場合は、追加と削除を行う
            LoadBalancerListener listener2 = new LoadBalancerListener();
            listener2.setLoadBalancerNo(loadBalancerNo);
            listener2.setLoadBalancerPort(loadBalancerPort);
            listener2.setServicePort(servicePort);
            listener2.setProtocol(protocol);
            listener2.setSslKeyNo(sslKeyNo);
            listener2.setEnabled(listener.getEnabled());
            listener2.setStatus(listener.getStatus());
            listener2.setConfigure(listener.getConfigure());
            loadBalancerListenerDao.create(listener2);

            loadBalancerListenerDao.delete(listener);
        }

        // イベントログ出力
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                "LoadBalancerListenerUpdate", null, null, new Object[] { loadBalancer.getLoadBalancerName(),
                        loadBalancerPort });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteListener(Long loadBalancerNo, Integer loadBalancerPort) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (loadBalancerPort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerPort");
        }

        // ロードバランサリスナーの存在チェック
        LoadBalancerListener listener = loadBalancerListenerDao.read(loadBalancerNo, loadBalancerPort);
        if (listener == null) {
            // リスナーが存在しない場合
            throw new AutoApplicationException("ESERVICE-000613", loadBalancerPort);
        }

        // リスナーが停止していることのチェック
        if (LoadBalancerListenerStatus.fromStatus(listener.getStatus()) != LoadBalancerListenerStatus.STOPPED) {
            // リスナーが停止状態で無い場合
            throw new AutoApplicationException("ESERVICE-000628");
        }

        // リスナー情報の削除処理
        loadBalancerListenerDao.deleteByLoadBalancerNoAndLoadBalancerPort(loadBalancerNo, loadBalancerPort);

        // イベントログ出力
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), null, null, null, null,
                "LoadBalancerListenerDelete", null, null, new Object[] { loadBalancer.getLoadBalancerName(),
                        loadBalancerPort });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureHealthCheck(Long loadBalancerNo, String checkProtocol, Integer checkPort, String checkPath,
            Integer checkTimeout, Integer checkInterval, Integer healthyThreshold, Integer unhealthyThreshold) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (checkProtocol == null || checkProtocol.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "checkProtocol");
        }
        if (checkPort == null) {
            throw new AutoApplicationException("ECOMMON-000003", "checkPort");
        }
        if (checkTimeout == null) {
            throw new AutoApplicationException("ECOMMON-000003", "checkTimeout");
        }
        if (checkInterval == null) {
            throw new AutoApplicationException("ECOMMON-000003", "checkInterval");
        }
        if (unhealthyThreshold == null) {
            throw new AutoApplicationException("ECOMMON-000003", "unhealthyThreshold");
        }

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
            if (healthyThreshold == null) {
                throw new AutoApplicationException("ECOMMON-000003", "healthyThreshold");
            }
        }

        // プロトコルのチェック
        List<String> protocols = Arrays.asList("TCP", "HTTP");
        if (!protocols.contains(checkProtocol)) {
            // チェックできないプロトコルの場合
            throw new AutoApplicationException("ESERVICE-000614");
        }

        // ポートのチェック
        if (checkPort < 1 || 65535 < checkPort) {
            // ポートが範囲外の場合
            throw new AutoApplicationException("ESERVICE-000615");
        }

        // パスのチェック
        if ("HTTP".equals(checkProtocol)) {
            if (checkPath == null || checkPath.length() == 0) {
                throw new AutoApplicationException("ESERVICE-000616");
            }
        }

        // タイムアウトのチェック
        if (checkTimeout < 2 || 60 < checkTimeout) {
            // タイムアウトが範囲外の場合
            throw new AutoApplicationException("ESERVICE-000617", 2, 60);
        }

        // インターバルのチェック
        if (checkInterval < 5 || 600 < checkInterval) {
            // インターバルが範囲外の場合
            throw new AutoApplicationException("ESERVICE-000618", 5, 600);
        }

        // 正常閾値のチェック
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
            if (healthyThreshold < 2 || 10 < healthyThreshold) {
                // 正常閾値が範囲外の場合
                throw new AutoApplicationException("ESERVICE-000619", 2, 10);
            }
        } else if (PCCConstant.LOAD_BALANCER_ULTRAMONKEY.equals(loadBalancer.getType())) {
            // UltraMonkeyの場合は常に1とする
            healthyThreshold = 1;
        }

        // 異常閾値のチェック
        if (unhealthyThreshold < 2 || 10 < unhealthyThreshold) {
            // 異常閾値が範囲外の場合
            throw new AutoApplicationException("ESERVICE-000620", 2, 10);
        }

        // ヘルスチェック情報の登録/更新
        LoadBalancerHealthCheck healthCheck = loadBalancerHealthCheckDao.read(loadBalancerNo);
        boolean exist = (healthCheck != null);
        if (!exist) {
            healthCheck = new LoadBalancerHealthCheck();
            healthCheck.setLoadBalancerNo(loadBalancerNo);
        }
        healthCheck.setCheckProtocol(checkProtocol);
        healthCheck.setCheckPort(checkPort);
        healthCheck.setCheckPath(checkPath);
        healthCheck.setCheckTimeout(checkTimeout);
        healthCheck.setCheckInterval(checkInterval);
        healthCheck.setHealthyThreshold(healthyThreshold);
        healthCheck.setUnhealthyThreshold(unhealthyThreshold);

        if (!exist) {
            loadBalancerHealthCheckDao.create(healthCheck);
        } else {
            loadBalancerHealthCheckDao.update(healthCheck);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAutoScalingConf(Long farmNo, Long loadBalancerNo, Long platformNo, Long imageNo,
            String instanceType, Integer enabled, String namingRule, Long idleTimeMax, Long idleTimeMin,
            Long continueLimit, Long addCount, Long delCount) {

        // 引数チェックは画面入力チェックで行える為省略

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        // オートスケーリング情報の更新
        AutoScalingConf autoScalingConf = autoScalingConfDao.read(loadBalancerNo);
        boolean exist = (autoScalingConf != null);
        if (!exist) {
            autoScalingConf = new AutoScalingConf();
            autoScalingConf.setLoadBalancerNo(loadBalancerNo);
            autoScalingConf.setFarmNo(farmNo);
        }
        autoScalingConf.setPlatformNo(platformNo);
        autoScalingConf.setImageNo(imageNo);
        autoScalingConf.setInstanceType(instanceType);
        autoScalingConf.setNamingRule(namingRule);
        autoScalingConf.setIdleTimeMax(idleTimeMax);
        autoScalingConf.setIdleTimeMin(idleTimeMin);
        autoScalingConf.setContinueLimit(continueLimit);
        autoScalingConf.setAddCount(addCount);
        autoScalingConf.setDelCount(delCount);
        if (enabled == 1) {
            autoScalingConf.setEnabled(true);
        } else {
            autoScalingConf.setEnabled(false);
        }

        if (!exist) {
            autoScalingConfDao.create(autoScalingConf);
        } else {
            autoScalingConfDao.update(autoScalingConf);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableInstances(Long loadBalancerNo, List<Long> instanceNos) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (instanceNos == null) {
            throw new AutoApplicationException("ECOMMON-000003", "instanceNos");
        }

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        // インスタンス番号の重複を除去
        List<Long> tmpInstanceNos = new ArrayList<Long>();
        for (Long instanceNo : instanceNos) {
            if (!tmpInstanceNos.contains(instanceNo)) {
                tmpInstanceNos.add(instanceNo);
            }
        }
        instanceNos = tmpInstanceNos;

        // インスタンスの存在チェック
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        if (instanceNos.size() != instances.size()) {
            tmpInstanceNos = new ArrayList<Long>(instanceNos);
            for (Instance instance : instances) {
                tmpInstanceNos.remove(instance.getInstanceNo());
            }
            if (tmpInstanceNos.size() > 0) {
                throw new AutoApplicationException("ESERVICE-000621", tmpInstanceNos.iterator().next());
            }
        }

        // コンポーネントのチェック
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(loadBalancer
                .getComponentNo());
        for (Instance instance : instances) {
            boolean contain = false;
            for (ComponentInstance componentInstance : componentInstances) {
                if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                    continue;
                }
                if (componentInstance.getInstanceNo().equals(instance.getInstanceNo())) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                // インスタンスがコンポーネントに含まれていない場合
                Component component = componentDao.read(loadBalancer.getComponentNo());
                throw new AutoApplicationException("ESERVICE-000622", instance.getInstanceName(),
                        component.getComponentName());
            }
        }

        // AWSロードバランサの場合のチェック
        //        if ("aws".equals(loadBalancer.getType()) || "cloudstack".equals(loadBalancer.getType())) {
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
            // プラットフォームチェック
            Long platformNo = loadBalancer.getPlatformNo();
            for (Instance instance : instances) {
                if (!platformNo.equals(instance.getPlatformNo())) {
                    throw new AutoApplicationException("ESERVICE-000623", instance.getInstanceName());
                }
            }

            // 他のAWSロードバランサの振り分け対象となっているかどうかのチェック
            List<LoadBalancerInstance> lbInstances = loadBalancerInstanceDao.readInInstanceNos(instanceNos);
            Set<Long> otherLoadBalancerNos = new HashSet<Long>();
            for (LoadBalancerInstance lbInstance : lbInstances) {
                if (!loadBalancerNo.equals(lbInstance.getLoadBalancerNo())) {
                    otherLoadBalancerNos.add(lbInstance.getLoadBalancerNo());
                }
            }

            List<LoadBalancer> otherLoadBalancers = loadBalancerDao.readInLoadBalancerNos(otherLoadBalancerNos);
            for (LoadBalancer otherLoadBalancer : otherLoadBalancers) {
                if (PCCConstant.LOAD_BALANCER_ELB.equals(otherLoadBalancer.getType())) {
                    // 他のAWSロードバランサの振り分け対象となっている場合
                    for (LoadBalancerInstance lbInstance : lbInstances) {
                        if (otherLoadBalancer.getLoadBalancerNo().equals(otherLoadBalancer.getLoadBalancerNo())) {
                            for (Instance instance : instances) {
                                if (instance.getInstanceNo().equals(lbInstance.getInstanceNo())) {
                                    throw new AutoApplicationException("ESERVICE-000624", instance.getInstanceName());
                                }
                            }
                        }
                    }
                }
            }

            //            //VPCの場合、サブネットのゾーンがロードバランサのサブネットのゾーンに含まれているかのチェック
            //            PlatformAws platformAws = platformAwsDao.read(platformNo);
            //            if (platformAws.getVpc()) {
            //                AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
            //                List<String> zones = new ArrayList<String>();
            //                if (StringUtils.isEmpty(awsLoadBalancer.getAvailabilityZone())) {
            //                    //ELBにゾーン(サブネット)が設定されていない場合→どのサーバとも紐付け不可
            //                    throw new AutoApplicationException("ESERVICE-000630", loadBalancer.getLoadBalancerName());
            //                }
            //                for (String zone: awsLoadBalancer.getAvailabilityZone().split(",")) {
            //                    zones.add(zone.trim());
            //                }
            //                for (Instance instance : instances) {
            //                    AwsInstance awsInstance = awsInstanceDao.read(instance.getInstanceNo());
            //                    if (zones.contains(awsInstance.getAvailabilityZone()) == false) {
            //                        //ロードバランサーのゾーンにサーバのゾーンが含まれていない
            //                        throw new AutoApplicationException("ESERVICE-000629", instance.getInstanceName());
            //                    }
            //                }
            //            }
        }

        // ロードバランサとインスタンスの関連を更新
        List<LoadBalancerInstance> lbInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
        Map<Long, LoadBalancerInstance> lbInstanceMap = new HashMap<Long, LoadBalancerInstance>();
        for (LoadBalancerInstance lbInstance : lbInstances) {
            lbInstanceMap.put(lbInstance.getInstanceNo(), lbInstance);
        }

        // 振り分けの有効化
        for (Instance instance : instances) {
            // インスタンスに紐づく関連付けを取得
            LoadBalancerInstance lbInstance = lbInstanceMap.remove(instance.getInstanceNo());

            // 関連付けがない場合、レコードを作成する
            if (lbInstance == null) {
                lbInstance = new LoadBalancerInstance();
                lbInstance.setLoadBalancerNo(loadBalancerNo);
                lbInstance.setInstanceNo(instance.getInstanceNo());
                lbInstance.setEnabled(true);
                lbInstance.setStatus(LoadBalancerInstanceStatus.STOPPED.toString());
                loadBalancerInstanceDao.create(lbInstance);
            }
            // 関連付けがある場合、レコードを更新する
            else {
                if (BooleanUtils.isNotTrue(lbInstance.getEnabled())) {
                    lbInstance.setEnabled(true);
                    loadBalancerInstanceDao.update(lbInstance);
                }
            }
        }

        // ロードバランサを設定変更対象にする
        if (BooleanUtils.isNotTrue(loadBalancer.getConfigure())) {
            loadBalancer.setConfigure(true);
            loadBalancerDao.update(loadBalancer);
        }

        // ファームを設定変更対象にする
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        if (BooleanUtils.isNotTrue(farm.getScheduled())) {
            farm.setScheduled(true);
            farmDao.update(farm);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableInstances(Long loadBalancerNo, List<Long> instanceNos) {
        // 引数チェック
        if (loadBalancerNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "loadBalancerNo");
        }
        if (instanceNos == null) {
            throw new AutoApplicationException("ECOMMON-000003", "instanceNos");
        }

        // ロードバランサの存在チェック
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        if (loadBalancer == null) {
            // ロードバランサが存在しない場合
            throw new AutoApplicationException("ESERVICE-000603", loadBalancerNo);
        }

        // インスタンス番号の重複を除去
        List<Long> tmpInstanceNos = new ArrayList<Long>();
        for (Long instanceNo : instanceNos) {
            if (!tmpInstanceNos.contains(instanceNo)) {
                tmpInstanceNos.add(instanceNo);
            }
        }
        instanceNos = tmpInstanceNos;

        // インスタンスの存在チェック
        List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
        if (instanceNos.size() != instances.size()) {
            tmpInstanceNos = new ArrayList<Long>(instanceNos);
            for (Instance instance : instances) {
                tmpInstanceNos.remove(instance.getInstanceNo());
            }
            if (tmpInstanceNos.size() > 0) {
                throw new AutoApplicationException("ESERVICE-000621", tmpInstanceNos.iterator().next());
            }
        }

        // コンポーネントのチェック
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(loadBalancer
                .getComponentNo());
        for (Instance instance : instances) {
            boolean contain = false;
            for (ComponentInstance componentInstance : componentInstances) {
                if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                    continue;
                }
                if (componentInstance.getInstanceNo().equals(instance.getInstanceNo())) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                // インスタンスがコンポーネントに含まれていない場合
                Component component = componentDao.read(loadBalancer.getComponentNo());
                throw new AutoApplicationException("ESERVICE-000622", instance.getInstanceName(),
                        component.getComponentName());
            }
        }

        // ロードバランサとインスタンスの関連を更新
        List<LoadBalancerInstance> lbInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
        Map<Long, LoadBalancerInstance> lbInstanceMap = new HashMap<Long, LoadBalancerInstance>();
        for (LoadBalancerInstance lbInstance : lbInstances) {
            lbInstanceMap.put(lbInstance.getInstanceNo(), lbInstance);
        }

        // 振り分けの無効化
        for (Instance instance : instances) {
            // インスタンスに紐づく関連付けを取得
            LoadBalancerInstance lbInstance = lbInstanceMap.remove(instance.getInstanceNo());

            if (lbInstance == null) {
                // 関連付けがない場合はスキップ
                continue;
            }

            // レコードを更新して無効にする
            if (BooleanUtils.isTrue(lbInstance.getEnabled())) {
                lbInstance.setEnabled(false);
                loadBalancerInstanceDao.update(lbInstance);
            }
        }

        // ロードバランサを設定変更対象にする
        if (BooleanUtils.isNotTrue(loadBalancer.getConfigure())) {
            loadBalancer.setConfigure(true);
            loadBalancerDao.update(loadBalancer);
        }

        // ファームを設定変更対象にする
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        if (BooleanUtils.isNotTrue(farm.getScheduled())) {
            farm.setScheduled(true);
            farmDao.update(farm);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LoadBalancerPlatformDto> getPlatforms(Long userNo) {
        // 引数チェック
        if (userNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "userNo");
        }

        List<LoadBalancerPlatformDto> dtos = new ArrayList<LoadBalancerPlatformDto>();
        List<Platform> platforms = platformDao.readAll();
        List<Image> images = imageDao.readAll();
        for (Platform platform : platforms) {
            PlatformAws platformAws = null;
            PlatformCloudstack platformCloudstack = null;
            PlatformVmware platformVmware = null;
            PlatformNifty platformNifty = null;
            PlatformVcloud platformVcloud = null;
            PlatformAzure platformAzure = null;
            PlatformOpenstack platformOpenstack = null;

            // TODO CLOUD BRANCHING
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                // AWSの認証情報がない場合はスキップ
                if (awsCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                platformAws = platformAwsDao.read(platform.getPlatformNo());
            } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                // CloudStackの認証情報がない場合はスキップ
                if (cloudstackCertificateDao.countByAccountAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                platformCloudstack = platformCloudstackDao.read(platform.getPlatformNo());
            } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                // キーペアがない場合はスキップ
                // TODO: 権限を別途持つ
                if (vmwareKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                platformVmware = platformVmwareDao.read(platform.getPlatformNo());
            } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                // 認証情報とキーペアがない場合はスキップ
                // TODO: 権限を別途持つ
                if (niftyCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                if (niftyKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                platformNifty = platformNiftyDao.read(platform.getPlatformNo());
            } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                // 認証情報とキーペアがない場合はスキップ
                if (vcloudCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                if (vcloudKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                platformVcloud = platformVcloudDao.read(platform.getPlatformNo());
            } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                // 認証情報とキーペアがない場合はスキップ
                if (azureCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                platformAzure = platformAzureDao.read(platform.getPlatformNo());
            } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                // 認証情報とキーペアがない場合はスキップ
                if (openstackCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                platformOpenstack = platformOpenstackDao.read(platform.getPlatformNo());
            }

            List<String> types = new ArrayList<String>();

            // AWSの場合、AWSプラットフォーム型ロードバランサを利用可能とする
            if (platformAws != null && BooleanUtils.isNotTrue(platformAws.getEuca())) {
                types.add(PCCConstant.LOAD_BALANCER_ELB);
            }

            // CloudStackの場合、IaaS Gateway型ロードバランサを利用可能とする
            if (platformCloudstack != null) {
                types.add(PCCConstant.LOAD_BALANCER_CLOUDSTACK);
            }

            // コンポーネント型のロードバランサの利用可否チェック
            for (Image image : images) {
                if (image.getPlatformNo().equals(platform.getPlatformNo())) {
                    if (PCCConstant.IMAGE_NAME_ULTRAMONKEY.equals(image.getImageName())) {
                        types.add(PCCConstant.LOAD_BALANCER_ULTRAMONKEY);
                    }
                }
            }

            // 利用可能なロードバランサがない場合はスキップ
            if (types.isEmpty()) {
                continue;
            }

            List<ImageDto> imageDtos = getImages(platform, images);

            LoadBalancerPlatformDto dto = new LoadBalancerPlatformDto();
            dto.setPlatform(platform);
            dto.setPlatformAws(platformAws);
            dto.setPlatformCloudstack(platformCloudstack);
            dto.setPlatformVmware(platformVmware);
            dto.setPlatformNifty(platformNifty);
            dto.setPlatformVcloud(platformVcloud);
            dto.setPlatformAzure(platformAzure);
            dto.setPlatformOpenstack(platformOpenstack);
            dto.setImages(imageDtos);
            dto.setTypes(types);
            dtos.add(dto);
        }

        return dtos;
    }

    private List<ImageDto> getImages(Platform platform, List<Image> images) {
        // イメージを取得
        List<ImageDto> imageDtos = new ArrayList<ImageDto>();
        for (Image image : images) {
            // プラットフォームが異なる場合はスキップ
            if (platform.getPlatformNo().equals(image.getPlatformNo()) == false) {
                continue;
            }

            ImageAws imageAws = null;
            ImageCloudstack imageCloudstack = null;
            ImageVmware imageVmware = null;
            ImageNifty imageNifty = null;
            ImageVcloud imageVcloud = null;
            ImageAzure imageAzure = null;
            ImageOpenstack imageOpenstack = null;

            // TODO CLOUD BRANCHING
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
                imageAws = imageAwsDao.read(image.getImageNo());
            } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
                imageCloudstack = imageCloudstackDao.read(image.getImageNo());
            } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                imageVmware = imageVmwareDao.read(image.getImageNo());
            } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                imageNifty = imageNiftyDao.read(image.getImageNo());
            } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                imageVcloud = imageVcloudDao.read(image.getImageNo());
            } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                imageAzure = imageAzureDao.read(image.getImageNo());
            } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                imageOpenstack = imageOpenstackDao.read(image.getImageNo());
            }

            ImageDto imageDto = new ImageDto();
            imageDto.setImage(image);
            imageDto.setImageAws(imageAws);
            imageDto.setImageCloudstack(imageCloudstack);
            imageDto.setImageVmware(imageVmware);
            imageDto.setImageNifty(imageNifty);
            imageDto.setImageVcloud(imageVcloud);
            imageDto.setImageAzure(imageAzure);
            imageDto.setImageOpenstack(imageOpenstack);
            imageDtos.add(imageDto);
        }

        return imageDtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SslKeyDto> getSSLKey(Long loadBalancerNo) {
        // イメージを取得
        List<SslKeyDto> keyDtos = new ArrayList<SslKeyDto>();
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        //当面はAWSのみ対応
        if (PCCConstant.LOAD_BALANCER_ELB.equals(loadBalancer.getType())) {
            List<AwsSslKey> keys = awsSslKeyDao.readAll();
            for (AwsSslKey key : keys) {
                if (loadBalancer.getFarmNo().equals(key.getFarmNo())
                        && loadBalancer.getPlatformNo().equals(key.getPlatformNo())) {
                    SslKeyDto sslkey = new SslKeyDto();
                    sslkey.setKeyNo(key.getKeyNo());
                    sslkey.setKeyName(key.getKeyName());
                    sslkey.setKeyId(key.getSslcertificateid());
                    keyDtos.add(sslkey);
                }
            }
        }
        return keyDtos;
    }

    /**
     * componentServiceを設定します。
     *
     * @param componentService componentService
     */
    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    /**
     * instanceServiceを設定します。
     *
     * @param instanceService instanceService
     */
    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    public void setAwsDescribeService(AwsDescribeService awsDescribeService) {
        this.awsDescribeService = awsDescribeService;
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    /**
     * zabbixProcessClientFactoryを設定します。
     *
     * @param zabbixProcessClientFactory zabbixProcessClientFactory
     */
    public void setZabbixProcessClientFactory(ZabbixProcessClientFactory zabbixProcessClientFactory) {
        this.zabbixProcessClientFactory = zabbixProcessClientFactory;
    }

    /**
     * processHookを設定します。
     *
     * @param processHook processHook
     */
    public void setProcessHook(ProcessHook processHook) {
        this.processHook = processHook;
    }

}
