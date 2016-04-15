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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.ComponentStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.AzureDisk;
import jp.primecloud.auto.entity.crud.AzureInstance;
import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.CloudstackVolume;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.NiftyVolume;
import jp.primecloud.auto.entity.crud.OpenstackInstance;
import jp.primecloud.auto.entity.crud.OpenstackVolume;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.VcloudDisk;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.log.EventLogLevel;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.nifty.process.NiftyProcessClient;
import jp.primecloud.auto.nifty.process.NiftyProcessClientFactory;
import jp.primecloud.auto.process.ComponentConstants;
import jp.primecloud.auto.process.hook.ProcessHook;
import jp.primecloud.auto.process.vmware.VmwareDiskProcess;
import jp.primecloud.auto.process.vmware.VmwareProcessClient;
import jp.primecloud.auto.process.vmware.VmwareProcessClientFactory;
import jp.primecloud.auto.process.zabbix.ZabbixHostProcess;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.ComponentTypeDto;
import jp.primecloud.auto.service.dto.InstanceDto;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ComponentServiceImpl extends ServiceSupport implements ComponentService {

    protected InstanceService instanceService;

    protected IaasGatewayFactory iaasGatewayFactory;

    protected VmwareProcessClientFactory vmwareProcessClientFactory;

    protected VmwareDiskProcess vmwareDiskProcess;

    protected ZabbixHostProcess zabbixHostProcess;

    protected EventLogger eventLogger;

    protected NiftyProcessClientFactory niftyProcessClientFactory;

    protected ProcessHook processHook;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ComponentDto> getComponents(Long farmNo) {
        // コンポーネントを取得
        List<Component> components = new ArrayList<Component>();
        List<Component> allComponents = componentDao.readByFarmNo(farmNo);
        for (Component component : allComponents) {
            // ロードバランサコンポーネントを除外
            if (BooleanUtils.isTrue(component.getLoadBalancer())) {
                continue;
            }
            components.add(component);
        }

        // コンポーネント番号のリスト
        List<Long> componentNos = new ArrayList<Long>();
        for (Component component : components) {
            componentNos.add(component.getComponentNo());
        }

        // コンポーネントに関連付けられたインスタンスを取得
        Map<Long, List<ComponentInstance>> componentInstanceMap = new LinkedHashMap<Long, List<ComponentInstance>>();
        for (Long componentNo : componentNos) {
            componentInstanceMap.put(componentNo, new ArrayList<ComponentInstance>());
        }
        List<ComponentInstance> tmpComponentInstances = componentInstanceDao.readInComponentNos(componentNos);
        for (ComponentInstance componentInstance : tmpComponentInstances) {
            // 関連付けが無効で停止している場合は除外
            if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                if (status == ComponentInstanceStatus.STOPPED) {
                    continue;
                }
            }
            componentInstanceMap.get(componentInstance.getComponentNo()).add(componentInstance);
        }

        Farm farm = farmDao.read(farmNo);
        List<Instance> instances = instanceDao.readByFarmNo(farmNo);
        Map<Long, Instance> instanceMap = new HashMap<Long, Instance>();
        for (Instance instance : instances) {
            instanceMap.put(instance.getInstanceNo(), instance);
        }
        List<ComponentDto> dtos = new ArrayList<ComponentDto>();
        for (Component component : components) {
            ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
            List<InstanceConfig> instanceConfigs = instanceConfigDao.readByComponentNo(component.getComponentNo());
            List<ComponentConfig> componentConfigs = componentConfigDao.readByComponentNo(component.getComponentNo());
            List<ComponentInstance> componentInstances = componentInstanceMap.get(component.getComponentNo());
            List<ComponentInstanceDto> componentInstanceDtos = new ArrayList<ComponentInstanceDto>();

            // インスタンスごとのコンポーネントのステータスを取得
            for (ComponentInstance componentInstance : componentInstances) {
                Instance instance = instanceMap.get(componentInstance.getInstanceNo());
                ComponentInstanceStatus status = getComponentInstanceStatus(farm, componentInstance, instance);
                componentInstance.setStatus(status.toString());
            }

            // コンポーネントのステータスを取得
            ComponentStatus componentStatus = getComponentStatus(componentInstances);

            // DTO作成、URLの設定
            for (ComponentInstance componentInstance : componentInstances) {
                ComponentInstanceDto componentInstanceDto = new ComponentInstanceDto();
                componentInstanceDto.setComponentInstance(componentInstance);
                Instance instance = instanceMap.get(componentInstance.getInstanceNo());

//                for (Instance tmpInstance : instances) {
//                    if (componentInstance.getInstanceNo().equals(tmpInstance.getInstanceNo())) {
//                        instance = tmpInstance;
//                        break;
//                    }
//                }

                String url;
                Boolean showPublicIp = BooleanUtils.toBooleanObject(Config.getProperty("ui.showPublicIp"));
                if (BooleanUtils.isTrue(showPublicIp)) {
                    //ui.showPublicIp = true の場合、URLにPublicIpを表示
                    url = createUrl(instance.getPublicIp(), component.getComponentTypeNo());
                } else {
                    //ui.showPublicIp = false の場合、URLにPrivateIpを表示
                    url = createUrl(instance.getPrivateIp(), component.getComponentTypeNo());
                }

                componentInstanceDto.setUrl(url);
                componentInstanceDtos.add(componentInstanceDto);
            }
//            // TODO: インスタンスごとのコンポーネントのステータスを調整する（暫定処理）
//            for (ComponentInstanceDto componentInstanceDto : componentInstances) {
//                ComponentInstance componentInstance = componentInstanceDto.getComponentInstance();
//                ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
//                if (BooleanUtils.isTrue(componentInstance.getEnabled())) {
//                    if (status == ComponentInstanceStatus.STOPPED) {
//                        Instance instance = instanceMap.get(componentInstance.getInstanceNo());
//                        InstanceStatus instanceStatus = InstanceStatus.fromStatus(instance.getStatus());
//                        if (instanceStatus == InstanceStatus.WARNING) {
//                            // インスタンスがWaringであれば、コンポーネントもWarningとする
//                            componentInstance.setStatus(ComponentInstanceStatus.WARNING.toString());
//                        } else if (BooleanUtils.isTrue(farm.getScheduled())) {
//                            // ファームが処理対象であれば、Startingにする
//                            componentInstance.setStatus(ComponentInstanceStatus.STARTING.toString());
//                        }
//                    } else if (status == ComponentInstanceStatus.RUNNING
//                            && BooleanUtils.isTrue(componentInstance.getConfigure())) {
//                        if (BooleanUtils.isTrue(farm.getScheduled())) {
//                            // コンポーネントがRunningでも処理対象であれば、Configuringにする
//                            componentInstance.setStatus(ComponentInstanceStatus.CONFIGURING.toString());
//                        }
//                    }
//                } else {
//                    if (status == ComponentInstanceStatus.RUNNING || status == ComponentInstanceStatus.WARNING) {
//                        if (BooleanUtils.isTrue(farm.getScheduled())) {
//                            // ファームが処理対象であれば、Stoppingにする
//                            componentInstance.setStatus(ComponentInstanceStatus.STOPPING.toString());
//                        }
//                    }
//                }
//            }



//            // コンポーネントのステータスを求める
//            ComponentStatus componentStatus;
//            Set<ComponentInstanceStatus> statuses = new HashSet<ComponentInstanceStatus>();
//            for (ComponentInstanceDto componentInstanceDto : componentInstances) {
//                statuses.add(ComponentInstanceStatus
//                        .fromStatus(componentInstanceDto.getComponentInstance().getStatus()));
//            }
//            if (statuses.contains(ComponentInstanceStatus.WARNING)) {
//                componentStatus = ComponentStatus.WARNING;
//            } else if (statuses.contains(ComponentInstanceStatus.CONFIGURING)) {
//                componentStatus = ComponentStatus.CONFIGURING;
//            } else if (statuses.contains(ComponentInstanceStatus.RUNNING)) {
//                if (statuses.contains(ComponentInstanceStatus.STARTING)) {
//                    componentStatus = ComponentStatus.CONFIGURING;
//                } else if (statuses.contains(ComponentInstanceStatus.STOPPING)) {
//                    componentStatus = ComponentStatus.CONFIGURING;
//                } else {
//                    componentStatus = ComponentStatus.RUNNING;
//                }
//            } else if (statuses.contains(ComponentInstanceStatus.STARTING)) {
//                componentStatus = ComponentStatus.STARTING;
//            } else if (statuses.contains(ComponentInstanceStatus.STOPPING)) {
//                componentStatus = ComponentStatus.STOPPING;
//            } else {
//                componentStatus = ComponentStatus.STOPPED;
//            }

            // ソート
            Collections.sort(componentInstanceDtos, Comparators.COMPARATOR_COMPONENT_INSTANCE_DTO);

            ComponentDto dto = new ComponentDto();
            dto.setComponent(component);
            dto.setComponentType(componentType);
            dto.setComponentConfigs(componentConfigs);
            dto.setComponentInstances(componentInstanceDtos);
            dto.setInstanceConfigs(instanceConfigs);
            dto.setStatus(componentStatus.toString());
            dtos.add(dto);
        }

        // ソート
        Collections.sort(dtos, Comparators.COMPARATOR_COMPONENT_DTO);

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createComponent(Long farmNo, String componentName, Long componentTypeNo, String comment,
            Integer diskSize) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }
        if (componentName == null || componentName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "componentName");
        }
        if (componentTypeNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentTypeNo");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", componentName)) {
            throw new AutoApplicationException("ECOMMON-000012", "componentName");
        }

        // TODO: 長さチェック

        // コンポーネント名の一意チェック
        Component checkComponent = componentDao.readByFarmNoAndComponentName(farmNo, componentName);
        if (checkComponent != null) {
            // 同名のコンポーネントが存在する場合
            throw new AutoApplicationException("ESERVICE-000301", componentName);
        }

        // ファームの存在チェック
        Farm farm = farmDao.read(farmNo);
        if (farm == null) {
            throw new AutoApplicationException("ESERVICE-000305", farmNo);
        }

        // コンポーネントの作成
        Component component = new Component();
        component.setFarmNo(farmNo);
        component.setComponentName(componentName);
        component.setComponentTypeNo(componentTypeNo);
        component.setComment(comment);
        componentDao.create(component);

        // ディスクサイズが指定されている場合
        if (diskSize != null) {
            // ディスクサイズの設定
            ComponentConfig componentConfig = new ComponentConfig();
            componentConfig.setComponentNo(component.getComponentNo());
            componentConfig.setConfigName(ComponentConstants.CONFIG_NAME_DISK_SIZE);
            componentConfig.setConfigValue(diskSize.toString());
            componentConfigDao.create(componentConfig);
        }

        ComponentType componentType = componentTypeDao.read(componentTypeNo);

        // TODO: 将来的にphpMyAdminの設定方式を見直す
        // MySQLコンポーネントを作成する場合、phpMyAdminを有効にする
        if (MySQLConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
            ComponentConfig componentConfig = new ComponentConfig();
            componentConfig.setComponentNo(component.getComponentNo());
            componentConfig.setConfigName(MySQLConstants.CONFIG_NAME_PHP_MY_ADMIN);
            componentConfig.setConfigValue("true");
            componentConfigDao.create(componentConfig);
        }

        // ホストグループを作成する
        Boolean useZabbix = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useZabbix"));
        if (BooleanUtils.isTrue(useZabbix)) {
            zabbixHostProcess.createComponentHostgroup(component.getComponentNo());
        }

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farmNo, farm.getFarmName(), component.getComponentNo(), componentName,
                null, null, "ComponentCreate", null, null, new Object[] { componentType.getComponentTypeName() });

        // フック処理の実行
        processHook.execute("post-create-component", farm.getUserNo(), farm.getFarmNo(), component.getComponentNo());

        return component.getComponentNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associateInstances(Long componentNo, List<Long> instanceNos) {
        // 引数チェック
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }
        if (instanceNos == null) {
            throw new AutoApplicationException("ECOMMON-000003", "instanceNos");
        }

        // コンポーネントの存在チェック
        Component component = componentDao.read(componentNo);
        if (component == null) {
            throw new AutoApplicationException("ESERVICE-000303", componentNo);
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
            for (Instance instance : instances) {
                instanceNos.remove(instance.getInstanceNo());
            }
            if (instanceNos.size() > 0) {
                throw new AutoApplicationException("ESERVICE-000304", instanceNos.iterator().next());
            }
        }

        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());

        // MySQLコンポーネントの場合、Masterチェック
        if (MySQLConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
            // Masterのインスタンスを取得
            Long masterInstanceNo = null;
            List<InstanceConfig> instanceConfigs = instanceConfigDao.readByComponentNo(componentNo);
            for (InstanceConfig instanceConfig : instanceConfigs) {
                if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(instanceConfig.getConfigName())) {
                    if (StringUtils.isEmpty(instanceConfig.getConfigValue())) {
                        masterInstanceNo = instanceConfig.getInstanceNo();
                        break;
                    }
                }
            }

            // Masterのインスタンスの関連付けを外す操作はエラー
            if (masterInstanceNo != null && !instanceNos.contains(masterInstanceNo)) {
                Instance masterInstance = instanceDao.read(masterInstanceNo);
                throw new AutoApplicationException("ESERVICE-000308", masterInstance.getInstanceName());
            }
        }

        // 関連付けを実行する
        doAssociate(componentNo, tmpInstanceNos);

        // イベントログ出力
        StringBuilder names = new StringBuilder();
        for (Instance instance : instances) {
            names.append(instance.getInstanceName()).append(",");
        }
        if (names.length() > 0) {
            names.deleteCharAt(names.length() - 1);
        }
        Farm farm = farmDao.read(component.getFarmNo());
        eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), componentNo, component
                .getComponentName(), null, null, "ComponentAssociateInstance", null, null, new Object[] { names.toString() });
    }

    /**
     * {@inheritDoc}
     */
    protected void doAssociate(Long componentNo, List<Long> instanceNos) {
        Component component = componentDao.read(componentNo);

        // コンポーネントとインスタンスの関連を更新
        List<Instance> allInstances = instanceDao.readByFarmNo(component.getFarmNo());
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentNo);
        for (Instance instance : allInstances) {
            // インスタンスに紐づく関連付けを取得
            ComponentInstance componentInstance = null;
            for (ComponentInstance tmpComponentInstance : componentInstances) {
                if (instance.getInstanceNo().equals(tmpComponentInstance.getInstanceNo())) {
                    componentInstance = tmpComponentInstance;
                    break;
                }
            }

            if (instanceNos.contains(instance.getInstanceNo())) {
                // コンポーネントに関連付けるインスタンスの場合
                if (componentInstance == null) {
                    // 関連付けレコードがない場合、レコードを作成する
                    componentInstance = new ComponentInstance();
                    componentInstance.setComponentNo(componentNo);
                    componentInstance.setInstanceNo(instance.getInstanceNo());
                    componentInstance.setAssociate(true);
                    componentInstance.setEnabled(false);
                    componentInstance.setStatus(ComponentInstanceStatus.STOPPED.toString());
                    componentInstanceDao.create(componentInstance);
                } else {
                    // 関連付けレコードがある場合、関連付けを有効化する
                    if (BooleanUtils.isNotTrue(componentInstance.getAssociate())) {
                        componentInstance.setAssociate(true);
                        componentInstanceDao.update(componentInstance);
                    }
                }
            } else {
                // コンポーネントに関連付けないインスタンスの場合
                if (componentInstance != null) {
                    // 関連付けレコードがある場合
                    ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                    if (status == ComponentInstanceStatus.STOPPED) {
                        // Zabbixのテンプレートを停止する
                        if (zabbixInstanceDao.countByInstanceNo(componentInstance.getInstanceNo()) > 0) {
                            zabbixHostProcess.removeTemplate(componentInstance.getInstanceNo(), componentNo);
                        }

                        /******************************************************************
                         * 停止時ディスクデタッチを行わないクラウドに対する特殊ロジック
                         * ※現在はVCLOUD（USiZE）のみがこのタイプ
                         ******************************************************************/
                        List<VcloudDisk> vdisks = vcloudDiskDao.readByInstanceNo(instance.getInstanceNo());
                        for (VcloudDisk disk:vdisks) {
                            if (componentNo.equals(disk.getComponentNo())) {
                                //componentNoの一致するディスクが存在していれば削除する
                                Farm farm = farmDao.read(instance.getFarmNo());
                                IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), instance.getPlatformNo());
                                try {
                                    gateway.deleteVolume(String.valueOf(disk.getDiskNo()));
                                } catch (AutoException ignore) {
                                    // ディスクが存在しない場合などに備えて握りつぶす
                                }
                                //データも削除
                                vcloudDiskDao.delete(disk);
                            }
                        }

                        // コンポーネントが停止している場合、関連付けを削除する
                        componentInstanceDao.delete(componentInstance);
                    } else {
                        // 関連付けを無効化する
                        if (BooleanUtils.isTrue(componentInstance.getAssociate())) {
                            componentInstance.setAssociate(false);
                            componentInstanceDao.update(componentInstance);
                        }
                    }
                }
            }
        }

        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());

        // MySQLコンポーネントの場合、MasterとSlaveを設定する
        if (MySQLConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
            // Master/Slaveのインスタンスを取得
            Long masterInstanceNo = null;
            Set<Long> slaveInstanceNos = new LinkedHashSet<Long>();
            List<InstanceConfig> instanceConfigs = instanceConfigDao.readByComponentNo(componentNo);
            for (InstanceConfig instanceConfig : instanceConfigs) {
                if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(instanceConfig.getConfigName())) {
                    if (StringUtils.isEmpty(instanceConfig.getConfigValue())) {
                        masterInstanceNo = instanceConfig.getInstanceNo();
                    } else {
                        slaveInstanceNos.add(instanceConfig.getInstanceNo());
                    }
                }
            }

            // Masterのインスタンスが存在しない場合、１つ目のインスタンスをMasterにする
            if (masterInstanceNo == null && instanceNos.size() > 0) {
                masterInstanceNo = instanceNos.get(0);

                InstanceConfig instanceConfig = new InstanceConfig();
                instanceConfig.setInstanceNo(masterInstanceNo);
                instanceConfig.setComponentNo(componentNo);
                instanceConfig.setConfigName(MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO);
                instanceConfig.setConfigValue(null);
                instanceConfigDao.create(instanceConfig);
            }

            // Master/Slave設定がされていないインスタンスをSlaveにする
            if (masterInstanceNo != null) {
                for (Long instanceNo : instanceNos) {
                    if (!instanceNo.equals(masterInstanceNo) && !slaveInstanceNos.contains(instanceNo)) {
                        InstanceConfig instanceConfig = new InstanceConfig();
                        instanceConfig.setInstanceNo(instanceNo);
                        instanceConfig.setComponentNo(componentNo);
                        instanceConfig.setConfigName(MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO);
                        instanceConfig.setConfigValue(masterInstanceNo.toString());
                        instanceConfigDao.create(instanceConfig);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateComponent(Long componentNo, String comment, Integer diskSize,
            String customParam1, String customParam2, String customParam3) {
        // 引数チェック
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        // TODO: 長さチェック

        // コンポーネントの存在チェック
        Component component = componentDao.read(componentNo);
        if (component == null) {
            throw new AutoApplicationException("ESERVICE-000303", componentNo);
        }

        // 既存のディスクサイズ設定を取得
        Integer oldDiskSize = null;
        ComponentConfig componentConfig = componentConfigDao.readByComponentNoAndConfigName(componentNo,
                ComponentConstants.CONFIG_NAME_DISK_SIZE);
        if (componentConfig != null && StringUtils.isNotEmpty(componentConfig.getConfigValue())) {
            oldDiskSize = Integer.valueOf(componentConfig.getConfigValue());
        }

        // 既存と異なるディスクサイズ設定が与えられた場合、ボリュームの非存在チェックをする
        if (oldDiskSize != null && !oldDiskSize.equals(diskSize)) {
         // TODO CLOUD BRANCHING
            long count = awsVolumeDao.countByComponentNo(componentNo);
            count += vmwareDiskDao.countByComponentNo(componentNo);
            count += cloudstackVolumeDao.countByComponentNo(componentNo);
            count += vcloudDiskDao.countByComponentNo(componentNo);
            count += niftyVolumeDao.countByComponentNo(componentNo);
            count += azureDiskDao.countByComponentNo(componentNo);
            count += openstackVolumeDao.countByComponentNo(componentNo);
            if (count > 0) {
                throw new AutoApplicationException("ESERVICE-000307", componentNo);
            }
        }

        // ファーム取得
        Farm farm = farmDao.read(component.getFarmNo());
        // コンポーネントインスタンス取得
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentNo);
        for (ComponentInstance componentInstance: componentInstances) {
            Instance instance = instanceDao.read(componentInstance.getInstanceNo());
            componentInstance.setStatus(getComponentInstanceStatus(farm, componentInstance, instance).toString());
        }
        ComponentStatus componentStatus = getComponentStatus(componentInstances);

        // 既存のカスタムパラメータ1を取得
        ComponentConfig compConfCustomParam1 = componentConfigDao.readByComponentNoAndConfigName(componentNo,
                ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_1);
        String oldCustomParam1 = null;
        if (compConfCustomParam1 != null) {
            oldCustomParam1 = compConfCustomParam1.getConfigValue();
        }

        // 既存のカスタムパラメータ2を取得
        ComponentConfig compConfCustomParam2 = componentConfigDao.readByComponentNoAndConfigName(componentNo,
                ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_2);
        String oldCustomParam2 = null;
        if (compConfCustomParam2 != null) {
            oldCustomParam2 = compConfCustomParam2.getConfigValue();
        }

        // 既存のカスタムパラメータ3を取得
        ComponentConfig compConfCustomParam3 = componentConfigDao.readByComponentNoAndConfigName(componentNo,
                ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_3);
        String oldCustomParam3 = null;
        if (compConfCustomParam3 != null) {
            oldCustomParam3 = compConfCustomParam3.getConfigValue();
        }

        // サービスステータスがStopped以外の場合のチェック
        if (ComponentStatus.STOPPED != componentStatus) {
            // カスタムパラメータ1
            if (oldCustomParam1 != null && !oldCustomParam1.equals(customParam1)) {
                throw new AutoApplicationException("ESERVICE-000311", component.getComponentName());
            }
            // カスタムパラメータ2
            if (oldCustomParam2 != null && !oldCustomParam2.equals(customParam2)) {
                throw new AutoApplicationException("ESERVICE-000312", component.getComponentName());
            }
            // カスタムパラメータ3
            if (oldCustomParam3 != null && !oldCustomParam3.equals(customParam3)) {
                throw new AutoApplicationException("ESERVICE-000313", component.getComponentName());
            }
        }

        // フック処理の実行
        processHook.execute("pre-update-component", farm.getUserNo(), farm.getFarmNo(), componentNo);

        // コンポーネントの更新
        component.setComment(comment);
        componentDao.update(component);

        // ディスクサイズの設定
        if (diskSize != null && !diskSize.equals(oldDiskSize)) {
            if (componentConfig != null) {
                componentConfig.setConfigValue(diskSize.toString());
                componentConfigDao.update(componentConfig);
            } else {
                componentConfig = new ComponentConfig();
                componentConfig.setComponentNo(componentNo);
                componentConfig.setConfigName(ComponentConstants.CONFIG_NAME_DISK_SIZE);
                componentConfig.setConfigValue(diskSize.toString());
                componentConfigDao.create(componentConfig);
            }
        } else if (diskSize == null && oldDiskSize != null) {
            componentConfig.setConfigValue(null);
            componentConfigDao.update(componentConfig);
        }

        // カスタムパラメータ1の設定
        if (compConfCustomParam1 != null) {
            compConfCustomParam1.setConfigValue(customParam1);
            componentConfigDao.update(compConfCustomParam1);
        } else {
            compConfCustomParam1 = new ComponentConfig();
            compConfCustomParam1.setComponentNo(componentNo);
            compConfCustomParam1.setConfigName(ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_1);
            compConfCustomParam1.setConfigValue(customParam1);
            componentConfigDao.create(compConfCustomParam1);
        }

        // カスタムパラメータ2の設定
        if (compConfCustomParam2 != null) {
            compConfCustomParam2.setConfigValue(customParam2);
            componentConfigDao.update(compConfCustomParam2);
        } else {
            compConfCustomParam2 = new ComponentConfig();
            compConfCustomParam2.setComponentNo(componentNo);
            compConfCustomParam2.setConfigName(ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_2);
            compConfCustomParam2.setConfigValue(customParam2);
            componentConfigDao.create(compConfCustomParam2);
        }

        // カスタムパラメータ3の設定
        if (compConfCustomParam3 != null) {
            compConfCustomParam3.setConfigValue(customParam3);
            componentConfigDao.update(compConfCustomParam3);
        } else {
            compConfCustomParam3 = new ComponentConfig();
            compConfCustomParam3.setComponentNo(componentNo);
            compConfCustomParam3.setConfigName(ComponentConstants.CONFIG_NAME_CUSTOM_PARAM_3);
            compConfCustomParam3.setConfigValue(customParam3);
            componentConfigDao.create(compConfCustomParam3);
        }

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), componentNo, component
                .getComponentName(), null, null, "ComponentUpdate", null, null, null);

        // フック処理の実行
        processHook.execute("post-update-component", farm.getUserNo(), farm.getFarmNo(), componentNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ComponentTypeDto> getComponentTypes(Long farmNo) {
        List<ComponentTypeDto> dtos = new ArrayList<ComponentTypeDto>();

        List<ComponentType> componentTypes = componentTypeDao.readAll();

        // 既存コンポーネントの取得
        List<Component> components = componentDao.readByFarmNo(farmNo);

        // 既存インスタンスの取得
        List<Instance> instances = new ArrayList<Instance>();
        List<Instance> allInstances = instanceDao.readByFarmNo(farmNo);
        for (Instance instance : allInstances) {
            // ロードバランサインスタンスを除去
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                continue;
            }

            instances.add(instance);
        }

        for (ComponentType componentType : componentTypes) {
            // ロードバランサのコンポーネントの場合はスキップ
            if (ComponentConstants.LAYER_NAME_LB.equals(componentType.getLayer())) {
                continue;
            }

            // 利用可能インスタンス
            List<Long> availableInstanceNos = new ArrayList<Long>();

            // コンポーネントを利用可能なものを抽出する
            for (Instance instance : instances) {
                Image image = imageDao.read(instance.getImageNo());
                String[] componentTypeNos = StringUtils.split(image.getComponentTypeNos(), ",");
                boolean available = false;
                for (String componentTypeNo : componentTypeNos) {
                    if (componentType.getComponentTypeNo().equals(Long.parseLong(componentTypeNo.trim()))) {
                        available = true;
                        break;
                    }
                }
                if (available) {
                    availableInstanceNos.add(instance.getInstanceNo());
                }
            }

            // 同レイヤのコンポーネントと関連付けられている、またはコンポーネントが停止していない場合、利用可能リストから除去する
            for (Component component : components) {
                ComponentType type = componentTypeDao.read(component.getComponentTypeNo());
                if (StringUtils.equals(componentType.getLayer(), type.getLayer())) {
                    List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(component
                            .getComponentNo());
                    for (ComponentInstance componentInstance : componentInstances) {
                        ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance
                                .getStatus());
                        if (BooleanUtils.isTrue(componentInstance.getAssociate())
                                || status != ComponentInstanceStatus.STOPPED) {
                            availableInstanceNos.remove(componentInstance.getInstanceNo());
                        }
                    }
                }
            }

            // DTOの作成
            ComponentTypeDto dto = new ComponentTypeDto();
            dto.setComponentType(componentType);
            dto.setInstanceNos(availableInstanceNos);

            dtos.add(dto);
        }

        // ソート
        Collections.sort(dtos, Comparators.COMPARATOR_COMPONENT_TYPE_DTO);

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentTypeDto getComponentType(Long componentNo) {
        // コンポーネントの取得
        Component component = componentDao.read(componentNo);

        // コンポーネントタイプの取得
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());

        // 利用可能インスタンス
        List<Instance> instances = new ArrayList<Instance>();
        List<Instance> allInstances = instanceDao.readByFarmNo(component.getFarmNo());
        for (Instance instance : allInstances) {
            // ロードバランサインスタンスを除去
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                continue;
            }

            instances.add(instance);
        }

        // 既存コンポーネントの取得
        List<Component> components = componentDao.readByFarmNo(component.getFarmNo());

        // 利用可能インスタンス
        List<Long> availableInstanceNos = new ArrayList<Long>();

        // コンポーネントを利用可能なものを抽出する
        for (Instance instance : instances) {
            Image image = imageDao.read(instance.getImageNo());
            String[] componentTypeNos = StringUtils.split(image.getComponentTypeNos(), ",");
            boolean available = false;
            for (String componentTypeNo : componentTypeNos) {
                if (componentType.getComponentTypeNo().equals(Long.parseLong(componentTypeNo.trim()))) {
                    available = true;
                    break;
                }
            }
            if (available) {
                availableInstanceNos.add(instance.getInstanceNo());
            }
        }

        // 同レイヤのコンポーネントと関連付けられている、またはコンポーネントが停止していない場合、利用可能リストから除去する
        for (Component component2 : components) {
            if (componentNo.equals(component2.getComponentNo())) {
                continue;
            }
            ComponentType componentType2 = componentTypeDao.read(component2.getComponentTypeNo());
            if (StringUtils.equals(componentType.getLayer(), componentType2.getLayer())) {
                List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(component2
                        .getComponentNo());
                for (ComponentInstance componentInstance : componentInstances) {
                    ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                    if (BooleanUtils.isTrue(componentInstance.getAssociate())
                            || status != ComponentInstanceStatus.STOPPED) {
                        availableInstanceNos.remove(componentInstance.getInstanceNo());
                    }
                }
            }
        }

        // DTOの作成
        ComponentTypeDto dto = new ComponentTypeDto();
        dto.setComponentType(componentType);
        dto.setInstanceNos(availableInstanceNos);

        return dto;
    }

    protected ComponentType getComponentType(String componentTypeName) {
        List<ComponentType> componentTypes = componentTypeDao.readAll();
        for (ComponentType componentType : componentTypes) {
            if (componentType.getComponentTypeName().equals(componentTypeName)) {
                return componentType;
            }
        }
        throw new RuntimeException("No such ComponentType: " + componentTypeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComponent(Long componentNo) {
        // 引数チェック
        if (componentNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "componentNo");
        }

        // コンポーネントの存在チェック
        Component component = componentDao.read(componentNo);
        if (component == null) {
            // コンポーネントが存在しない場合
            return;
        }

        // コンポーネントが停止しているかどうかのチェック
        List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(componentNo);
        for (ComponentInstance componentInstance : componentInstances) {
            ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
            if (status != ComponentInstanceStatus.STOPPED) {
                // コンポーネントが停止状態でない場合
                throw new AutoApplicationException("ESERVICE-000302", component.getComponentName());
            }
        }

        // ディスクがデタッチされているかどうかのチェック
        List<AwsVolume> awsVolumes = awsVolumeDao.readByComponentNo(componentNo);
        for (AwsVolume awsVolume : awsVolumes) {
            if (StringUtils.isNotEmpty(awsVolume.getInstanceId())) {
                // ディスクがデタッチされていない場合
                Instance instance = instanceDao.read(awsVolume.getInstanceNo());
                throw new AutoApplicationException("ESERVICE-000310", instance.getInstanceName());
            }
        }
        // ディスクがデタッチされているかどうかのチェック
        List<CloudstackVolume> csVolumes = cloudstackVolumeDao.readByComponentNo(componentNo);
        for (CloudstackVolume csVolume : csVolumes) {
            if (StringUtils.isNotEmpty(csVolume.getInstanceId())) {
                // ディスクがデタッチされていない場合
                Instance instance = instanceDao.read(csVolume.getInstanceNo());
                throw new AutoApplicationException("ESERVICE-000310", instance.getInstanceName());
            }
        }
        List<VmwareDisk> vmwareDisks = vmwareDiskDao.readByComponentNo(componentNo);
        for (VmwareDisk vmwareDisk : vmwareDisks) {
            if (BooleanUtils.isTrue(vmwareDisk.getAttached())) {
                // ディスクがデタッチされていない場合
                Instance instance = instanceDao.read(vmwareDisk.getInstanceNo());
                throw new AutoApplicationException("ESERVICE-000310", instance.getInstanceName());
            }
        }
        List<VcloudDisk> vcloudDisks = vcloudDiskDao.readByComponentNo(componentNo);
        for (VcloudDisk vcloudDisk: vcloudDisks) {
            if (BooleanUtils.isTrue(vcloudDisk.getAttached())) {
                // Vcloudはディスクがデタッチされていない場合、サーバが停止していないと削除できない
                Instance instance = instanceDao.read(vcloudDisk.getInstanceNo());
                InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
                if (InstanceStatus.STOPPED != status) {
                    throw new AutoApplicationException("ESERVICE-000310", instance.getInstanceName());
                }
            }
        }
        // ディスクがデタッチされているかどうかのチェック
        List<AzureDisk> azureDisks = azureDiskDao.readByComponentNo(componentNo);
        for (AzureDisk azureDisk : azureDisks) {
            if (StringUtils.isNotEmpty(azureDisk.getInstanceName())) {
                // ディスクがデタッチされていない場合
                Instance instance = instanceDao.read(azureDisk.getInstanceNo());
                throw new AutoApplicationException("ESERVICE-000310", instance.getInstanceName());
            }
        }
        // ディスクがデタッチされているかどうかのチェック
        List<OpenstackVolume> osVolumes = openstackVolumeDao.readByComponentNo(componentNo);
        for (OpenstackVolume osVolume : osVolumes) {
            if (StringUtils.isNotEmpty(osVolume.getInstanceId())) {
                // ディスクがデタッチされていない場合
                Instance instance = instanceDao.read(osVolume.getInstanceNo());
                throw new AutoApplicationException("ESERVICE-000310", instance.getInstanceName());
            }
        }
        // ディスクがデタッチされているかどうかのチェック
        List<NiftyVolume> niftyVolumes = niftyVolumeDao.readByComponentNo(componentNo);
        for (NiftyVolume niftyVolume : niftyVolumes) {
            if (StringUtils.isNotEmpty(niftyVolume.getInstanceId())) {
                // ディスクがデタッチされていない場合
                Instance instance = instanceDao.read(niftyVolume.getInstanceNo());
                throw new AutoApplicationException("ESERVICE-000310", instance.getInstanceName());
            }
        }

        // 削除するコンポーネントを振り分け対象とするロードバランサがないかどかのチェック
        List<LoadBalancer> loadBalancers = loadBalancerDao.readByComponentNo(componentNo);
        if (!loadBalancers.isEmpty()) {
            throw new AutoApplicationException("ESERVICE-000309", loadBalancers.get(0).getLoadBalancerName());
        }

        // フック処理の実行
        Farm farm = farmDao.read(component.getFarmNo());
        processHook.execute("pre-delete-component", farm.getUserNo(), farm.getFarmNo(), componentNo);

        // インスタンス設定の削除処理
        instanceConfigDao.deleteByComponentNo(componentNo);

        // コンポーネントとインスタンスの関連の削除処理
        doAssociate(componentNo, new ArrayList<Long>());
        componentInstanceDao.deleteByComponentNo(componentNo);

        // コンポーネント設定の削除処理
        componentConfigDao.deleteByComponentNo(componentNo);

        // 各プラットフォームのボリューム削除処理
        // TODO CLOUD BRANCHING

        // AWSボリュームの削除処理
        // TODO: ボリューム自体の削除処理を別で行うようにする
        for (AwsVolume awsVolume : awsVolumes) {
            if (StringUtils.isEmpty(awsVolume.getVolumeId())) {
                continue;
            }

            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), awsVolume.getPlatformNo());

            //イベントログ出力
            Platform platform = platformDao.read(gateway.getPlatformNo());
            Instance instance = instanceDao.read(awsVolume.getInstanceNo());
            AwsInstance awsInstance = awsInstanceDao.read(awsVolume.getInstanceNo());
            eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), awsVolume.getComponentNo(),
                    component.getComponentName(), awsVolume.getInstanceNo(), instance.getInstanceName(),
                    "AwsEbsDelete", awsInstance.getInstanceType(),  instance.getPlatformNo(), new Object[] { platform.getPlatformName(), awsVolume.getVolumeId() });

            try {
                // ボリュームの削除
                gateway.deleteVolume(awsVolume.getVolumeId());

                //イベントログ出力
                eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), awsVolume.getComponentNo(),
                        component.getComponentName(), awsVolume.getInstanceNo(), instance.getInstanceName(),
                        "AwsEbsDeleteFinish", awsInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), awsVolume.getVolumeId() });

                // EC2ではDeleteVolumeに時間がかかるため、Waitしない
                //awsProcessClient.waitDeleteVolume(volumeId);
            } catch (AutoException ignore) {
                // ボリュームが存在しない場合などに備えて例外を握りつぶす
            }
        }
        awsVolumeDao.deleteByComponentNo(componentNo);

        // CLOUDSTACKボリュームの削除処理
        // TODO: ボリューム自体の削除処理を別で行うようにする
        for (CloudstackVolume csVolume : csVolumes) {
            if (StringUtils.isEmpty(csVolume.getVolumeId())) {
                continue;
            }

            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), csVolume.getPlatformNo());

            //イベントログ出力
            Platform platform = platformDao.read(gateway.getPlatformNo());
            Instance instance = instanceDao.read(csVolume.getInstanceNo());
            CloudstackInstance csInstance = cloudstackInstanceDao.read(csVolume.getInstanceNo());
            eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), csVolume.getComponentNo(),
                    component.getComponentName(), csVolume.getInstanceNo(), instance.getInstanceName(),
                    "CloudStackVolumeDelete", csInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), csVolume.getVolumeId() });

            try {
                // ボリュームの削除
                gateway.deleteVolume(csVolume.getVolumeId());

                //イベントログ出力
                eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), csVolume.getComponentNo(),
                        component.getComponentName(), csVolume.getInstanceNo(), instance.getInstanceName(),
                        "CloudStackVolumeDeleteFinish", csInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), csVolume.getVolumeId() });

            } catch (AutoException ignore) {
                // ボリュームが存在しない場合などに備えて例外を握りつぶす
            }
        }
        cloudstackVolumeDao.deleteByComponentNo(componentNo);


        // VMwareディスクの削除処理
        // TODO: 削除処理を別で行うようにする
        for (VmwareDisk vmwareDisk : vmwareDisks) {
            if (StringUtils.isEmpty(vmwareDisk.getFileName())) {
                continue;
            }
            VmwareProcessClient vmwareProcessClient = vmwareProcessClientFactory.createVmwareProcessClient(vmwareDisk.getPlatformNo());
            try {
                vmwareDiskProcess.deleteDisk(vmwareProcessClient, vmwareDisk.getDiskNo());
            } catch (AutoException ignore) {
                // ディスクが存在しない場合などに備えて例外を握りつぶす
            } finally {
                vmwareProcessClient.getVmwareClient().logout();
            }
        }
        vmwareDiskDao.deleteByComponentNo(componentNo);

        // VCloudディスクの削除処理
        // VCLoudの場合は関連付け解除時（doAssociate呼出）にディスクを削除する為必要なし
//        for (VcloudDisk vcloudDisk : vcloudDisks) {
//            if (StringUtils.isEmpty(vcloudDisk.getDiskId())) {
//                continue;
//            }
//
//            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), vcloudDisk.getPlatformNo());
//
//            //イベントログ出力
//            Platform platform = platformDao.read(gateway.getPlatformNo());
//            Instance instance = instanceDao.read(vcloudDisk.getInstanceNo());
//            VcloudInstance vcloudInstance = vcloudInstanceDao.read(vcloudDisk.getInstanceNo());
//            //TODO イベントログのメッセージと引数
//            eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), vcloudDisk.getComponentNo(),
//                    component.getComponentName(), vcloudDisk.getInstanceNo(), instance.getInstanceName(),
//                    "VcloudVolumeDelete", vcloudInstance.getInstanceType(),  instance.getPlatformNo(), new Object[] { platform.getPlatformName(), vcloudDisk.getDiskId() });
//
//            try {
//                // ボリュームの削除
//                // ※VCloudの引数はDiskNo
//                gateway.deleteVolume(String.valueOf(vcloudDisk.getDiskNo()));
//
//                //イベントログ出力
//                eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), vcloudDisk.getComponentNo(),
//                        component.getComponentName(), vcloudDisk.getInstanceNo(), instance.getInstanceName(),
//                        "VcloudVolumeDeleteFinish", vcloudInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), vcloudDisk.getDiskId() });
//
//            } catch (AutoException ignore) {
//                // ボリュームが存在しない場合などに備えて例外を握りつぶす
//            }
//        }
//        vcloudDiskDao.deleteByComponentNo(componentNo);

        // Azureボリュームの削除処理
        // TODO: ボリューム自体の削除処理を別で行うようにする
        for (AzureDisk azureDisk : azureDisks) {
            if (StringUtils.isEmpty(azureDisk.getDiskName())) {
                continue;
            }

            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), azureDisk.getPlatformNo());

            //イベントログ出力
            Platform platform = platformDao.read(gateway.getPlatformNo());
            Instance instance = instanceDao.read(azureDisk.getInstanceNo());
            AzureInstance azureInstance = azureInstanceDao.read(azureDisk.getInstanceNo());
            eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), azureDisk.getComponentNo(),
                    component.getComponentName(), azureDisk.getInstanceNo(), instance.getInstanceName(),
                    "AzureDiskDelete", azureInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), azureDisk.getDiskName() });

            try {
                // ボリュームの削除
                gateway.deleteVolume(azureDisk.getDiskNo().toString());

                //イベントログ出力
                eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), azureDisk.getComponentNo(),
                        component.getComponentName(), azureDisk.getInstanceNo(), instance.getInstanceName(),
                        "AzureDiskDeleteFinish", azureInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), azureDisk.getDiskName() });

            } catch (AutoException ignore) {
                // ボリュームが存在しない場合などに備えて例外を握りつぶす
            }
        }
        azureDiskDao.deleteByComponentNo(componentNo);

        // Openstackボリュームの削除処理
        // TODO: ボリューム自体の削除処理を別で行うようにする
        for (OpenstackVolume osVolume : osVolumes) {
            if (StringUtils.isEmpty(osVolume.getVolumeId())) {
                continue;
            }

            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), osVolume.getPlatformNo());

            //イベントログ出力
            Platform platform = platformDao.read(gateway.getPlatformNo());
            Instance instance = instanceDao.read(osVolume.getInstanceNo());
            OpenstackInstance osInstance = openstackInstanceDao.read(osVolume.getInstanceNo());
            eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), osVolume.getComponentNo(),
                    component.getComponentName(), osVolume.getInstanceNo(), instance.getInstanceName(),
                    "OpenstackVolumeDelete", osInstance.getInstanceType(),  instance.getPlatformNo(), new Object[] { platform.getPlatformName(), osVolume.getVolumeId() });

            try {
                // ボリュームの削除
                gateway.deleteVolume(osVolume.getVolumeId());

                //イベントログ出力
                eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), osVolume.getComponentNo(),
                        component.getComponentName(), osVolume.getInstanceNo(), instance.getInstanceName(),
                        "OpenstackVolumeDeleteFinish", osInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), osVolume.getVolumeId() });

            } catch (AutoException ignore) {
                // ボリュームが存在しない場合などに備えて例外を握りつぶす
            }
        }
        openstackVolumeDao.deleteByComponentNo(componentNo);

        // Niftyボリュームの削除処理
        // TODO: ボリューム自体の削除処理を別で行うようにする
        for (NiftyVolume niftyVolume : niftyVolumes) {
            if (StringUtils.isEmpty(niftyVolume.getVolumeId())) {
                continue;
            }

            // NiftyProcessClientの作成
            String clientType;
            clientType = PCCConstant.NIFTYCLIENT_TYPE_DISK;
            NiftyProcessClient niftyProcessClient = niftyProcessClientFactory.createNiftyProcessClient(farm.getUserNo(),
                    niftyVolume.getPlatformNo(), clientType);

            //イベントログ出力
            Platform platform = platformDao.read(niftyProcessClient.getPlatformNo());
            Instance instance = instanceDao.read(niftyVolume.getInstanceNo());
            NiftyInstance niftyInstance = niftyInstanceDao.read(niftyVolume.getInstanceNo());
            eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), niftyVolume.getComponentNo(),
                    component.getComponentName(), niftyVolume.getInstanceNo(), instance.getInstanceName(),
                    "NiftyDiskDelete", niftyInstance.getInstanceType(),  instance.getPlatformNo(), new Object[] { platform.getPlatformName(), niftyVolume.getVolumeId() });

            try {
                // ボリュームの削除
                niftyProcessClient.deleteVolume(niftyVolume.getVolumeId());

                //イベントログ出力
                eventLogger.log(EventLogLevel.DEBUG, farm.getFarmNo(), farm.getFarmName(), niftyVolume.getComponentNo(),
                        component.getComponentName(), niftyVolume.getInstanceNo(), instance.getInstanceName(),
                        "NiftyDiskDeleteFinish", niftyInstance.getInstanceType(), instance.getPlatformNo(), new Object[] { platform.getPlatformName(), niftyVolume.getVolumeId() });

            } catch (AutoException ignore) {
                // ボリュームが存在しない場合などに備えて例外を握りつぶす
            }
        }
        niftyVolumeDao.deleteByComponentNo(componentNo);

        // コンポーネントの削除処理
        componentDao.delete(component);

        //移行ツール対応 残存ディレクトリ削除
        StringBuilder dpath = new StringBuilder("/opt/userdata/");
        dpath.append(LoggingUtils.getUserName());
        dpath.append(System.getProperty("file.separator"));
        dpath.append(LoggingUtils.getFarmName());
        dpath.append(System.getProperty("file.separator"));
        dpath.append(component.getComponentName());
        File delDir = new File(dpath.toString());
        if (delDir.exists()) {
            deleteDirectoryAndFile(delDir);
        }

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farm.getFarmName(), componentNo, component
                .getComponentName(), null, null, "ComponentDelete", null, null, null);

        // フック処理の実行
        processHook.execute("post-delete-component", farm.getUserNo(), farm.getFarmNo(), componentNo);
    }


    private void deleteDirectoryAndFile(File delFile) {
        if ( delFile == null || !delFile.exists() ) { return; }
        if ( delFile.isFile() ) {
            // ファイル削除
            if ( delFile.exists() && !delFile.delete() ) {
                delFile.deleteOnExit();
            }
        } else {
            // ディレクトリの場合、再帰する
            File[] list = delFile.listFiles();
            for ( int i = 0 ; i < list.length ; i++ ) {
                deleteDirectoryAndFile( list[i] );
            }
            if ( delFile.exists() && !delFile.delete() ) {
                delFile.deleteOnExit();
            }
        }
    }



    protected Image getImage(Long platformNo, String imageName) {
        List<Image> images = imageDao.readAll();
        for (Image image : images) {
            if (image.getPlatformNo().equals(platformNo) && image.getImageName().equals(imageName)) {
                return image;
            }
        }
        throw new RuntimeException("No such Image: " + platformNo + ", " + imageName);
    }

    protected String createUrl(String ipAddress, Long componentTypeNo) {

//        String url = "http://";
//        ComponentType componentType = componentTypeDao.read(componentTypeNo);
//        if (componentType.getComponentTypeName().equals("apache")) {
//            url = url + ipAddress + ":80/";
//        } else if (componentType.getComponentTypeName().equals("tomcat")) {
//            url = url + ipAddress + ":8080/";
//        } else if (componentType.getComponentTypeName().equals("geronimo")) {
//            url = url + ipAddress + ":8080/console/";
//        } else if (componentType.getComponentTypeName().equals("mysql")) {
//            url = url + ipAddress + ":8085/phpmyadmin/";
//        } else if (componentType.getComponentTypeName().equals("prjserver")) {
//            url = url + ipAddress + "/trac/prj/top/";
//        }

        ComponentType componentType = componentTypeDao.read(componentTypeNo);
        String url = componentType.getAddressUrl();
        url = url.replaceAll("%d", ipAddress);
        return url;
    }

    protected int createPort(Long componentTypeNo) {
        ComponentType componentType = componentTypeDao.read(componentTypeNo);
        if (componentType.getComponentTypeName().equals("apache")) {
            return 80;
        } else if (componentType.getComponentTypeName().equals("tomcat")) {
            return 8080;
        } else if (componentType.getComponentTypeName().equals("geronimo")) {
            return 8080;
        } else if (componentType.getComponentTypeName().equals("mysql")) {
            return 8085;
        } else if (componentType.getComponentTypeName().equals("prjserver")) {
            return 80;
        }
        return 0;
    }

    protected ComponentInstanceStatus getComponentInstanceStatus(Farm farm, ComponentInstance componentInstance, Instance instance) {
        // インスタンスごとのコンポーネントのステータスを調整する（暫定処理）
        ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
        if (BooleanUtils.isTrue(componentInstance.getEnabled())) {
            if (status == ComponentInstanceStatus.STOPPED) {
                InstanceStatus instanceStatus = InstanceStatus.fromStatus(instance.getStatus());
                if (instanceStatus == InstanceStatus.WARNING) {
                    // インスタンスがWaringであれば、コンポーネントもWarningとする
                    return ComponentInstanceStatus.WARNING;
                } else if (BooleanUtils.isTrue(farm.getScheduled())) {
                    // ファームが処理対象であれば、Startingにする
                    return ComponentInstanceStatus.STARTING;
                }
            } else if (status == ComponentInstanceStatus.RUNNING
                    && BooleanUtils.isTrue(componentInstance.getConfigure())) {
                if (BooleanUtils.isTrue(farm.getScheduled())) {
                    // コンポーネントがRunningでも処理対象であれば、Configuringにする
                    return ComponentInstanceStatus.CONFIGURING;
                }
            }
        } else {
            if (status == ComponentInstanceStatus.RUNNING || status == ComponentInstanceStatus.WARNING) {
                if (BooleanUtils.isTrue(farm.getScheduled())) {
                    // ファームが処理対象であれば、Stoppingにする
                    return ComponentInstanceStatus.STOPPING;
                }
            }
        }
        return status;
    }

    protected ComponentStatus getComponentStatus(List<ComponentInstance> componentInstances) {
        // コンポーネントのステータスを求める
        ComponentStatus componentStatus;
        Set<ComponentInstanceStatus> statuses = new HashSet<ComponentInstanceStatus>();
        for (ComponentInstance componentInstance : componentInstances) {
            statuses.add(ComponentInstanceStatus
                    .fromStatus(componentInstance.getStatus()));
        }
        if (statuses.contains(ComponentInstanceStatus.WARNING)) {
            componentStatus = ComponentStatus.WARNING;
        } else if (statuses.contains(ComponentInstanceStatus.CONFIGURING)) {
            componentStatus = ComponentStatus.CONFIGURING;
        } else if (statuses.contains(ComponentInstanceStatus.RUNNING)) {
            if (statuses.contains(ComponentInstanceStatus.STARTING)) {
                componentStatus = ComponentStatus.CONFIGURING;
            } else if (statuses.contains(ComponentInstanceStatus.STOPPING)) {
                componentStatus = ComponentStatus.CONFIGURING;
            } else {
                componentStatus = ComponentStatus.RUNNING;
            }
        } else if (statuses.contains(ComponentInstanceStatus.STARTING)) {
            componentStatus = ComponentStatus.STARTING;
        } else if (statuses.contains(ComponentInstanceStatus.STOPPING)) {
            componentStatus = ComponentStatus.STOPPING;
        } else {
            componentStatus = ComponentStatus.STOPPED;
        }
        return componentStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> checkAttachDisk(Long farmNo, Long componentNo, String instanceName,
            String notSelectedItem, Collection<Object> moveList) {

        List<InstanceDto> instances = instanceService.getInstances(farmNo);
        for (InstanceDto instance : instances) {
            if (StringUtils.equals(instanceName, instance.getInstance().getInstanceName())) {
                //TODO CLOUD BRANCHING
                if (instance.getAwsVolumes() != null) {
                    // AwsVolumeをチェック
                    for (AwsVolume awsVolume : instance.getAwsVolumes()) {
                        if (componentNo.equals(awsVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(awsVolume.getInstanceId())) {
                                // ディスクがアタッチされたままの場合
                                moveList.add(notSelectedItem);
                            }
                            break;
                        }
                    }
                }
                else if (instance.getVmwareDisks() != null) {
                    // VmwareDiskをチェック
                    for (VmwareDisk vmwareDisk : instance.getVmwareDisks()) {
                        if (componentNo.equals(vmwareDisk.getComponentNo())) {
                            if (BooleanUtils.isTrue(vmwareDisk.getAttached())) {
                                // ディスクがアタッチされたままの場合
                                moveList.add(notSelectedItem);
                            }
                            break;
                        }
                    }
                }
                else if (instance.getCloudstackVolumes() != null) {
                    // CloudstackVolumeをチェック
                    for (CloudstackVolume cloudstackVolume : instance.getCloudstackVolumes()) {
                        if (componentNo.equals(cloudstackVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(cloudstackVolume.getInstanceId())) {
                                // ディスクがアタッチされたままの場合
                                moveList.add(notSelectedItem);
                            }
                            break;
                        }
                    }
                }
                else if (instance.getVcloudDisks() != null) {
                    // VcloudDiskをチェック
                    for (VcloudDisk vcloudDisk : instance.getVcloudDisks()) {
                        if (componentNo.equals(vcloudDisk.getComponentNo())) {
                            if (BooleanUtils.isTrue(vcloudDisk.getAttached())) {
                                if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                    // ディスクがアタッチされたままの場合
                                    moveList.add(notSelectedItem);
                                }
                            }
                            break;
                        }
                    }
                }
                else if (instance.getAzureDisks() != null) {
                    // AzureDiskをチェック
                    for (AzureDisk azureDisk : instance.getAzureDisks()) {
                        if (componentNo.equals(azureDisk.getComponentNo())) {
                            if (StringUtils.isNotEmpty(azureDisk.getInstanceName())) {
                                if (InstanceStatus.fromStatus(instance.getInstance().getStatus()) != InstanceStatus.STOPPED) {
                                    // ディスクがアタッチされたままの場合
                                    moveList.add(notSelectedItem);
                                }
                            }
                            break;
                        }
                    }
                }
                else if (instance.getNiftyVolumes() != null) {
                    // NiftyVolumeをチェック
                    for (NiftyVolume niftyVolume : instance.getNiftyVolumes()) {
                        if (componentNo.equals(niftyVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(niftyVolume.getInstanceId())) {
                                // ディスクがアタッチされたままの場合
                                moveList.add(notSelectedItem);
                            }
                            break;
                        }
                    }
                }
                else if (instance.getOpenstackVolumes() != null) {
                    // OpenstackVolumeをチェック
                    for (OpenstackVolume openstackVolume : instance.getOpenstackVolumes()) {
                        if (componentNo.equals(openstackVolume.getComponentNo())) {
                            if (StringUtils.isNotEmpty(openstackVolume.getInstanceId())) {
                                // ディスクがアタッチされたままの場合
                                moveList.add(notSelectedItem);
                            }
                            break;
                        }
                    }
                }
            }
        }

        return moveList;
    }

    /**
     * instanceServiceを設定します。
     *
     * @param instanceService instanceService
     */
    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    /**
     * iaasGatewayFactoryを設定します。
     *
     * @param iaasGatewayFactory iaasGatewayFactory
     */
    public void setIaasGatewayFactory(IaasGatewayFactory iaasGatewayFactory) {
        this.iaasGatewayFactory = iaasGatewayFactory;
    }

    /**
     * vmwareProcessClientFactoryを設定します。
     *
     * @param vmwareProcessClientFactory vmwareProcessClientFactory
     */
    public void setVmwareProcessClientFactory(VmwareProcessClientFactory vmwareProcessClientFactory) {
        this.vmwareProcessClientFactory = vmwareProcessClientFactory;
    }

    /**
     * vmwareDiskProcessを設定します。
     *
     * @param vmwareDiskProcess vmwareDiskProcess
     */
    public void setVmwareDiskProcess(VmwareDiskProcess vmwareDiskProcess) {
        this.vmwareDiskProcess = vmwareDiskProcess;
    }

    /**
     * zabbixHostProcessを設定します。
     *
     * @param zabbixHostProcess zabbixHostProcess
     */
    public void setZabbixHostProcess(ZabbixHostProcess zabbixHostProcess) {
        this.zabbixHostProcess = zabbixHostProcess;
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
     * niftyProcessClientFactoryを設定します。
     * @param niftyProcessClientFactory niftyProcessClientFactory
     */
    public void setNiftyProcessClientFactory(NiftyProcessClientFactory niftyProcessClientFactory) {
        this.niftyProcessClientFactory = niftyProcessClientFactory;
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
