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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.UserAuth;
import jp.primecloud.auto.entity.crud.VmwareNetwork;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.log.EventLogLevel;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.hook.ProcessHook;
import jp.primecloud.auto.process.vmware.VmwareNetworkProcess;
import jp.primecloud.auto.process.vmware.VmwareProcessClient;
import jp.primecloud.auto.process.vmware.VmwareProcessClientFactory;
import jp.primecloud.auto.process.zabbix.ZabbixHostProcess;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.dto.FarmDto;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class FarmServiceImpl extends ServiceSupport implements FarmService {

    protected IaasGatewayFactory iaasGatewayFactory;

    protected VmwareProcessClientFactory vmwareProcessClientFactory;

    protected ComponentService componentService;

    protected InstanceService instanceService;

    protected VmwareNetworkProcess vmwareNetworkProcess;

    protected ZabbixHostProcess zabbixHostProcess;

    protected EventLogger eventLogger;

    protected LoadBalancerService loadBalancerService;

    protected ProcessHook processHook;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FarmDto> getFarms(Long userNo, Long loginUserNo) {

        //ユーザー取得
        User user = userDao.read(loginUserNo);
        //検索結果ファーム
        List<Farm> farms = new ArrayList<Farm>();
        //戻り値用
        List<FarmDto> dtos = new ArrayList<FarmDto>();


        //パワーユーザは全権
        if (user.getPowerUser()) {
            farms = farmDao.readAll();
            for (Farm farm : farms) {
                FarmDto dto = new FarmDto();
                dto.setFarm(farm);
                dtos.add(dto);
            }
        }
        //マスターユーザー及び一般はマスター管理下
        else {
            //データ取得はマスターユーザ
            farms = farmDao.readByUserNo(userNo);

            //ユーザー権限取得
            List<UserAuth> userAuth = userAuthDao.readByUserNo(loginUserNo);
            HashMap<Long, Boolean> authMap = new HashMap<Long, Boolean>();
            for (UserAuth auth : userAuth){
                if (auth.getFarmUse()) {
                    authMap.put(auth.getFarmNo(), auth.getFarmUse());
                }
            }

            for (Farm farm : farms) {
                //利用可能なファームのみ登録する ※マスターユーザーは全て
                if ((loginUserNo.equals(userNo))
                        || authMap.containsKey(farm.getFarmNo())) {
                    FarmDto dto = new FarmDto();
                    dto.setFarm(farm);
                    dtos.add(dto);
                }
            }
        }

        // ソート
        Collections.sort(dtos, Comparators.COMPARATOR_FARM_DTO);

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FarmDto getFarm(Long farmNo) {
        Farm farm = farmDao.read(farmNo);

        if (farm == null) {
            // ファーム情報が存在しない場合
            return null;
        }

        FarmDto dto = new FarmDto();
        dto.setFarm(farm);

        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createFarm(Long userNo, String farmName, String comment) {
        // 引数チェック
        if (userNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "userNo");
        }
        if (farmName == null || farmName.length() == 0) {
            throw new AutoApplicationException("ECOMMON-000003", "farmName");
        }

        // 形式チェック
        if (!Pattern.matches("^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", farmName)) {
            throw new AutoApplicationException("ECOMMON-000012", "farmName");
        }

        // TODO: 長さチェック

        // ファーム名の一意チェック
        Farm checkFarm = farmDao.readByFarmName(farmName);
        if (checkFarm != null) {
            // 同名のファームが存在する場合
            throw new AutoApplicationException("ESERVICE-000201", farmName);
        }

        // ファームのドメイン名
        // TODO: 設定値の取得方法をうまくする
        String domainName = farmName + "." + Config.getProperty("dns.domain");

        // ファームの作成
        Farm farm = new Farm();
        farm.setFarmName(farmName);
        farm.setUserNo(userNo);
        farm.setComment(comment);
        farm.setDomainName(domainName);
        farm.setScheduled(false);
        farm.setComponentProcessing(false);
        farmDao.create(farm);

        List<Platform> platforms = platformDao.readAll();
        for (Platform platform : platforms) {
            // TODO CLOUD BRANCHING
            // VMware関連情報の作成
            if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                if (vmwareKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) > 0) {
                    // 空いているVLANを取得
                    VmwareNetwork publicNetwork = null;
                    VmwareNetwork privateNetwork = null;
                    List<VmwareNetwork> vmwareNetworks = vmwareNetworkDao.readByPlatformNo(platform.getPlatformNo());
                    for (VmwareNetwork vmwareNetwork : vmwareNetworks) {
                        if (vmwareNetwork.getFarmNo() != null) {
                            continue;
                        }
                        if (BooleanUtils.isTrue(vmwareNetwork.getPublicNetwork())) {
                            if (publicNetwork == null) {
                                publicNetwork = vmwareNetwork;
                            }
                        } else {
                            if (privateNetwork == null) {
                                privateNetwork = vmwareNetwork;
                            }
                        }
                    }

                    // VLANを割り当て
                    if (publicNetwork != null) {
                        publicNetwork.setFarmNo(farm.getFarmNo());
                        vmwareNetworkDao.update(publicNetwork);
                    }
                    if (privateNetwork != null) {
                        privateNetwork.setFarmNo(farm.getFarmNo());
                        vmwareNetworkDao.update(privateNetwork);
                    }
                }
            // VCloud関連情報の作成
            } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                //プラットフォームが有効で認証情報、キーペア情報を保持している場合、空のvAppを作成する
                if (BooleanUtils.isTrue(platform.getSelectable()) &&
                    vcloudCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) > 0 &&
                    vcloudKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) > 0) {
                    //vApp作成
                    IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platform.getPlatformNo());
                    gateway.createMyCloud(farmName);
                }
            // Azure関連情報の作成
            } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
                // 現状処理なし
            // OpenStack関連情報の作成
            } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                // 現状処理なし
            }
        }

        // ※VCloud対応の為(iaasGateWayの呼び出しを行うので)、最後にZabbix処理を行うように修正しました。
        // ファームごとのホストグループ作成
        Boolean useZabbix = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useZabbix"));
        if (BooleanUtils.isTrue(useZabbix)) {
            zabbixHostProcess.createFarmHostgroup(farm.getFarmNo());
        }

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farm.getFarmNo(), farmName, null, null, null, null, "FarmCreate", null, null, null);

        // フック処理の実行
        processHook.execute("post-create-farm", farm.getUserNo(), farm.getFarmNo());

        return farm.getFarmNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFarm(Long farmNo, String comment, String domainName) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }

        // TODO: 長さチェック(comment,domainName)

        // TODO: 形式チェック(domainName)

        // ファームの存在チェック
        Farm farm = farmDao.read(farmNo);
        if (farm == null) {
            // ファームが存在しない場合
            throw new AutoApplicationException("ESERVICE-000204", farmNo);
        }

        if (!farm.getDomainName().equals(domainName)) {
            // ドメイン名を変更しようとした場合

            // 全てのインスタンスが停止状態であることのチェック
            List<Instance> instances = instanceDao.readByFarmNo(farmNo);
            for (Instance instance : instances) {
                if (InstanceStatus.fromStatus(instance.getStatus()) != InstanceStatus.STOPPED) {
                    // インスタンスが停止状態でない場合
                    throw new AutoApplicationException("ESERVICE-000205", instance.getInstanceName());
                }
            }

            // ドメイン名の一意チェック
            // TODO: データベース上で一意キーを設定するべきか？
            List<Farm> checkFarms = farmDao.readAll();
            for (Farm checkFarm : checkFarms) {
                if (checkFarm.getDomainName().equals(domainName)) {
                    // 同名のドメイン名が存在する場合
                    throw new AutoApplicationException("ESERVICE-000206", domainName);
                }
            }
        }

        // フック処理の実行
        processHook.execute("pre-update-farm", farm.getUserNo(), farm.getFarmNo());

        // インスタンスの更新
        farm.setComment(comment);
        farm.setDomainName(domainName);
        farmDao.update(farm);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farmNo, farm.getFarmName(), null, null, null, null, "FarmUpdate", null, null, null);

        // フック処理の実行
        processHook.execute("post-update-farm", farm.getUserNo(), farm.getFarmNo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFarm(Long farmNo) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }

        // ファームの存在チェック
        Farm farm = farmDao.read(farmNo);
        if (farm == null) {
            // ファームが存在しない場合
            return;
        }

        // コンポーネントが停止しているかどうかのチェック
        List<Component> components = componentDao.readByFarmNo(farmNo);
        for (Component component : components) {
            // ロードバランサのコンポーネントはチェックしない
            if (BooleanUtils.isTrue(component.getLoadBalancer())) {
                continue;
            }

            List<ComponentInstance> componentInstances = componentInstanceDao.readByComponentNo(component
                    .getComponentNo());
            for (ComponentInstance componentInstance : componentInstances) {
                ComponentInstanceStatus status = ComponentInstanceStatus.fromStatus(componentInstance.getStatus());
                if (status != ComponentInstanceStatus.STOPPED) {
                    // コンポーネントが停止状態でない場合
                    throw new AutoApplicationException("ESERVICE-000202", component.getComponentName());
                }
            }
        }

        // インスタンスが停止しているかどうかのチェック
        List<Instance> instances = instanceDao.readByFarmNo(farmNo);
        for (Instance instance : instances) {
            // ロードバランサのインスタンスはチェックしない
            if (BooleanUtils.isTrue(instance.getLoadBalancer())) {
                continue;
            }

            if (InstanceStatus.fromStatus(instance.getStatus()) != InstanceStatus.STOPPED) {
                // インスタンスが停止状態でない場合
                throw new AutoApplicationException("ESERVICE-000203", instance.getInstanceName());
            }
        }

        // ロードバランサが停止しているかどうかのチェック
        List<LoadBalancer> loadBalancers = loadBalancerDao.readByFarmNo(farmNo);
        for (LoadBalancer loadBalancer : loadBalancers) {
            if (LoadBalancerStatus.fromStatus(loadBalancer.getStatus()) != LoadBalancerStatus.STOPPED) {
                // ロードバランサが停止状態でない場合
                throw new AutoApplicationException("ESERVICE-000207", loadBalancer.getLoadBalancerName());
            }
        }

        // フック処理の実行
        processHook.execute("pre-delete-farm", farm.getUserNo(), farm.getFarmNo());

        // ホストグループの削除処理
        Boolean useZabbix = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useZabbix"));
        if (BooleanUtils.isTrue(useZabbix)) {
            zabbixHostProcess.deleteFarmHostgroup(farmNo);
        }

        // ロードバランサの削除処理
        for (LoadBalancer loadBalancer : loadBalancers) {
            loadBalancerService.deleteLoadBalancer(loadBalancer.getLoadBalancerNo());
        }

        // コンポーネントの削除処理
        for (Component component : components) {
            componentService.deleteComponent(component.getComponentNo());
        }

        // インスタンスの削除処理
        for (Instance instance : instances) {
            instanceService.deleteInstance(instance.getInstanceNo());
        }

        // TODO CLOUD BRANCHING
        // AWS関連の削除処理
        // AWSボリュームの削除処理
        // TODO: ボリューム自体の削除処理を別で行うようにする
        List<AwsVolume> awsVolumes = awsVolumeDao.readByFarmNo(farmNo);
        for (AwsVolume awsVolume : awsVolumes) {
            if (StringUtils.isEmpty(awsVolume.getVolumeId())) {
                continue;
            }

            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), awsVolume.getPlatformNo());
            try {
                // ボリュームの削除
                gateway.deleteVolume(awsVolume.getVolumeId());

                // EC2ではDeleteVolumeに時間がかかるため、Waitしない
                //awsProcessClient.waitDeleteVolume(volumeId);
            } catch (AutoException ignore) {
                // ボリュームが存在しない場合などに備えて例外を握りつぶす
            }
        }
        awsVolumeDao.deleteByFarmNo(farmNo);

        // VMware関連の削除処理
        // VLANの割り当てを解除
        List<VmwareNetwork> vmwareNetworks = vmwareNetworkDao.readByFarmNo(farmNo);
        for (VmwareNetwork vmwareNetwork : vmwareNetworks) {
            // PortGroupを削除
            VmwareProcessClient vmwareProcessClient = vmwareProcessClientFactory.createVmwareProcessClient(vmwareNetwork.getPlatformNo());
            try {
                vmwareNetworkProcess.removeNetwork(vmwareProcessClient, vmwareNetwork.getNetworkNo());
            } finally {
                vmwareProcessClient.getVmwareClient().logout();
            }

            // VLAN割り当てを解除
            vmwareNetwork.setFarmNo(null);
            vmwareNetworkDao.update(vmwareNetwork);
        }

        // VCloud関連の削除処理
        // VCloud vAppの削除処理
        List<Platform> platforms = platformDao.readAll();
        for (Platform platform: platforms) {
            if (!PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
                //VCloudのみ処理を行う
                continue;
            }

            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), platform.getPlatformNo());
            try {
                if (BooleanUtils.isTrue(platform.getSelectable()) &&
                        vcloudCertificateDao.countByUserNoAndPlatformNo(farm.getUserNo(), platform.getPlatformNo()) > 0 &&
                        vcloudKeyPairDao.countByUserNoAndPlatformNo(farm.getUserNo(), platform.getPlatformNo()) > 0) {
                    // myCloud(vApp)の削除
                    gateway.deleteMyCloud(farmNo);
                }
            } catch (AutoException ignore) {
                // 全てのVCloudプラットフォームが対象なので
                // ボリュームが存在しない場合などに備えて例外を握りつぶす
            }
        }

        // Azure関連の削除処理
        // 現状処理なし

        // OpenStack関連の削除処理
        // 現状処理なし

        //ユーザ権限の削除
        userAuthDao.deleteByFarmNo(farmNo);

        // ファームの削除処理
        farmDao.deleteByFarmNo(farmNo);

        // イベントログ出力
        eventLogger.log(EventLogLevel.INFO, farmNo, farm.getFarmName(), null, null, null, null, "FarmDelete", null, null, null);

        // フック処理の実行
        processHook.execute("post-delete-farm", farm.getUserNo(), farm.getFarmNo());
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

    /**
     * vmwareNetworkProcessを設定します。
     *
     * @param vmwareNetworkProcess vmwareNetworkProcess
     */
    public void setVmwareNetworkProcess(VmwareNetworkProcess vmwareNetworkProcess) {
        this.vmwareNetworkProcess = vmwareNetworkProcess;
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
     * loadBalancerServiceを設定します。
     *
     * @param loadBalancerService loadBalancerService
     */
    public void setLoadBalancerService(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
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
