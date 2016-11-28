/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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
package jp.primecloud.auto.process.aws;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerInstanceStatus;
import jp.primecloud.auto.common.status.LoadBalancerListenerStatus;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.AwsSslKey;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;
import jp.primecloud.auto.entity.crud.LoadBalancerInstance;
import jp.primecloud.auto.entity.crud.LoadBalancerListener;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.DnsProcessClient;
import jp.primecloud.auto.process.DnsProcessClientFactory;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.elasticloadbalancing.model.ApplySecurityGroupsToLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.CrossZoneLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerAttributes;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.ModifyLoadBalancerAttributesRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsLoadBalancerProcess extends ServiceSupport {

    protected AwsCommonProcess awsCommonProcess;

    protected DnsProcessClientFactory dnsProcessClientFactory;

    protected EventLogger eventLogger;

    public void start(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200101", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // ロードバランサの作成
        createLoadBalancer(awsProcessClient, loadBalancerNo);

        // DNSサーバへの追加
        addDns(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200102", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public void stop(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200103", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // DNSサーバからの削除
        deleteDns(loadBalancerNo);

        // ロードバランサの削除
        deleteLoadBalancer(awsProcessClient, loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200104", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public void configure(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200105", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        // リスナーの設定
        try {
            configureListeners(awsProcessClient, loadBalancerNo);
        } catch (RuntimeException e) {
            // ロードバランサが無効な場合は例外を握りつぶす
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                throw e;
            }
        }

        // ヘルスチェックの設定
        try {
            configureHealthCheck(awsProcessClient, loadBalancerNo);
        } catch (RuntimeException e) {
            // ロードバランサが無効な場合は例外を握りつぶす
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                throw e;
            }
        }

        // セキュリティグループの設定
        try {
            applySecurityGroups(awsProcessClient, loadBalancerNo);
        } catch (RuntimeException e) {
            // ロードバランサが無効な場合は例外を握りつぶす
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                throw e;
            }
        }

        // インスタンスの振り分け設定
        try {
            configureInstances(awsProcessClient, loadBalancerNo);
        } catch (RuntimeException e) {
            // ロードバランサが無効な場合は例外を握りつぶす
            if (BooleanUtils.isTrue(loadBalancer.getEnabled())) {
                throw e;
            }
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200106", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public String createLoadBalancer(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // ロードバランサ作成情報
        CreateLoadBalancerRequest request = new CreateLoadBalancerRequest();
        request.withLoadBalancerName(awsLoadBalancer.getName());

        // ダミーのリスナー
        Listener listener = new Listener();
        listener.withProtocol("TCP");
        listener.withLoadBalancerPort(65535);
        listener.withInstancePort(65535);
        request.withListeners(listener);

        // 非PVCの場合
        if (BooleanUtils.isNotTrue(awsProcessClient.getPlatformAws().getVpc())) {
            // AvailabilityZones
            List<AvailabilityZone> availabilityZones = awsCommonProcess.describeAvailabilityZones(awsProcessClient);
            for (AvailabilityZone availabilityZone : availabilityZones) {
                request.withAvailabilityZones(availabilityZone.getZoneName());
            }
        }
        // VPCの場合
        else {
            // Subnet
            List<Subnet> subnets = awsCommonProcess.describeSubnetsByVpcId(awsProcessClient, awsProcessClient
                    .getPlatformAws().getVpcId());
            if (StringUtils.isNotEmpty(awsLoadBalancer.getSubnetId())) {
                for (String subnetId : StringUtils.split(awsLoadBalancer.getSubnetId(), ",")) {
                    subnetId = subnetId.trim();
                    for (Subnet subnet : subnets) {
                        if (StringUtils.equals(subnetId, subnet.getSubnetId())) {
                            request.withSubnets(subnetId);
                            break;
                        }
                    }
                }
            } else {
                // Subnetが指定されていない場合、全てのAvailabilityZoneのSubnetを設定する
                Map<String, String> subnetIdMap = new LinkedHashMap<String, String>();
                for (Subnet subnet : subnets) {
                    if (BooleanUtils.isTrue(subnet.getDefaultForAz())) {
                        subnetIdMap.put(subnet.getAvailabilityZone(), subnet.getSubnetId());
                        continue;
                    }

                    if (!subnetIdMap.containsKey(subnet.getAvailabilityZone())) {
                        subnetIdMap.put(subnet.getAvailabilityZone(), subnet.getSubnetId());
                    }
                }
                request.withSubnets(subnetIdMap.values());
            }

            // SecurytiGroup
            List<SecurityGroup> securityGroups = awsCommonProcess.describeSecurityGroupsByVpcId(awsProcessClient,
                    awsProcessClient.getPlatformAws().getVpcId());
            for (String groupName : StringUtils.split(awsLoadBalancer.getSecurityGroups(), ",")) {
                groupName = groupName.trim();
                for (SecurityGroup securityGroup : securityGroups) {
                    if (StringUtils.equals(groupName, securityGroup.getGroupName())) {
                        request.withSecurityGroups(securityGroup.getGroupId());
                        break;
                    }
                }
            }

            // Internal
            if (BooleanUtils.isTrue(awsLoadBalancer.getInternal())) {
                request.withScheme("internal");
            }
        }

        // ロードバランサの作成
        CreateLoadBalancerResult result = awsProcessClient.getElbClient().createLoadBalancer(request);
        String dnsName = result.getDNSName();

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200111", awsLoadBalancer.getName()));
        }

        // イベントログ出力
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, null, null, "AwsElbCreate", null, platform.getPlatformNo(), new Object[] {
                platform.getPlatformName(), awsLoadBalancer.getName() });

        // ダミーのリスナーの削除
        DeleteLoadBalancerListenersRequest request2 = new DeleteLoadBalancerListenersRequest();
        request2.withLoadBalancerName(awsLoadBalancer.getName());
        request2.withLoadBalancerPorts(65535);
        awsProcessClient.getElbClient().deleteLoadBalancerListeners(request2);

        // クロスゾーン負荷分散を有効化
        ModifyLoadBalancerAttributesRequest request3 = new ModifyLoadBalancerAttributesRequest();
        request3.withLoadBalancerName(awsLoadBalancer.getName());
        request3.withLoadBalancerAttributes(new LoadBalancerAttributes()
                .withCrossZoneLoadBalancing(new CrossZoneLoadBalancing().withEnabled(true)));
        awsProcessClient.getElbClient().modifyLoadBalancerAttributes(request3);

        // イベントログ出力
        eventLogger.debug(null, null, null, null, "AwsCrossZoneEnabled", null, platform.getPlatformNo(), new Object[] {
                platform.getPlatformName(), awsLoadBalancer.getName() });

        // データベース更新
        awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
        awsLoadBalancer.setDnsName(dnsName);
        awsLoadBalancerDao.update(awsLoadBalancer);

        return dnsName;
    }

    public void deleteLoadBalancer(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // ロードバランサの削除
        DeleteLoadBalancerRequest request = new DeleteLoadBalancerRequest();
        request.withLoadBalancerName(awsLoadBalancer.getName());
        awsProcessClient.getElbClient().deleteLoadBalancer(request);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200112", awsLoadBalancer.getName()));
        }

        // イベントログ出力
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, null, null, "AwsElbDelete", null, platform.getPlatformNo(), new Object[] {
                platform.getPlatformName(), awsLoadBalancer.getName() });

        // データベース更新
        awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
        awsLoadBalancer.setDnsName(null);
        awsLoadBalancerDao.update(awsLoadBalancer);
    }

    public void configureListeners(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        List<LoadBalancerListener> listeners = loadBalancerListenerDao.readByLoadBalancerNo(loadBalancerNo);

        // リスナーの起動・停止処理
        for (LoadBalancerListener listener : listeners) {
            LoadBalancerListenerStatus status = LoadBalancerListenerStatus.fromStatus(listener.getStatus());
            if (BooleanUtils.isTrue(listener.getEnabled())) {
                if (status == LoadBalancerListenerStatus.STOPPED) {
                    // 有効で停止しているリスナーは処理対象
                    startListener(awsProcessClient, loadBalancerNo, listener.getLoadBalancerPort());
                } else if (status == LoadBalancerListenerStatus.RUNNING) {
                    // 有効で起動しているリスナーの場合、処理を行わずにフラグを変更する
                    if (BooleanUtils.isTrue(listener.getConfigure())) {
                        listener.setConfigure(false);
                        loadBalancerListenerDao.update(listener);
                    }
                }
            } else {
                if (status == LoadBalancerListenerStatus.RUNNING || status == LoadBalancerListenerStatus.WARNING) {
                    // 無効で起動または異常なリスナーは処理対象
                    stopListener(awsProcessClient, loadBalancerNo, listener.getLoadBalancerPort());
                } else if (status == LoadBalancerListenerStatus.STOPPED) {
                    // 無効で停止しているリスナーの場合、処理を行わずにフラグを変更する
                    if (BooleanUtils.isTrue(listener.getConfigure())) {
                        listener.setConfigure(false);
                        loadBalancerListenerDao.update(listener);
                    }
                }
            }
        }
    }

    public void startListener(AwsProcessClient awsProcessClient, Long loadBalancerNo, Integer loadBalancerPort) {
        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
        LoadBalancerListener listener = loadBalancerListenerDao.read(loadBalancerNo, loadBalancerPort);

        try {
            // リスナー作成情報
            CreateLoadBalancerListenersRequest request = new CreateLoadBalancerListenersRequest();
            request.withLoadBalancerName(awsLoadBalancer.getName());

            Listener listener2 = new Listener();
            listener2.withProtocol(listener.getProtocol());
            listener2.withLoadBalancerPort(listener.getLoadBalancerPort());
            listener2.withInstancePort(listener.getServicePort());
            if (listener.getSslKeyNo() != null) {
                AwsSslKey awsSslKey = awsSslKeyDao.read(listener.getSslKeyNo());
                listener2.withSSLCertificateId(awsSslKey.getSslcertificateid());
            }
            request.withListeners(listener2);

            // リスナーの作成
            awsProcessClient.getElbClient().createLoadBalancerListeners(request);

            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-200121", awsLoadBalancer.getName(),
                        listener.getLoadBalancerPort()));
            }

            // イベントログ出力
            Platform platform = awsProcessClient.getPlatform();
            eventLogger.debug(
                    null,
                    null,
                    null,
                    null,
                    "AwsElbListenerCreate",
                    null,
                    platform.getPlatformNo(),
                    new Object[] { platform.getPlatformName(), awsLoadBalancer.getName(),
                            listener.getLoadBalancerPort() });

        } catch (RuntimeException e) {
            // ステータスを更新
            listener = loadBalancerListenerDao.read(loadBalancerNo, loadBalancerPort);
            listener.setStatus(LoadBalancerListenerStatus.WARNING.toString());
            loadBalancerListenerDao.update(listener);

            throw e;
        }

        // ステータスを更新
        listener = loadBalancerListenerDao.read(loadBalancerNo, loadBalancerPort);
        listener.setStatus(LoadBalancerListenerStatus.RUNNING.toString());
        listener.setConfigure(false);
        loadBalancerListenerDao.update(listener);
    }

    public void stopListener(AwsProcessClient awsProcessClient, Long loadBalancerNo, Integer loadBalancerPort) {
        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
        LoadBalancerListener listener = loadBalancerListenerDao.read(loadBalancerNo, loadBalancerPort);

        try {
            // リスナーの削除
            DeleteLoadBalancerListenersRequest request = new DeleteLoadBalancerListenersRequest();
            request.withLoadBalancerName(awsLoadBalancer.getName());
            request.withLoadBalancerPorts(listener.getLoadBalancerPort());
            awsProcessClient.getElbClient().deleteLoadBalancerListeners(request);

            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-200122", awsLoadBalancer.getName(),
                        listener.getLoadBalancerPort()));
            }

            // イベントログ出力
            Platform platform = awsProcessClient.getPlatform();
            eventLogger.debug(
                    null,
                    null,
                    null,
                    null,
                    "AwsElbListenerDelete",
                    null,
                    platform.getPlatformNo(),
                    new Object[] { platform.getPlatformName(), awsLoadBalancer.getName(),
                            listener.getLoadBalancerPort() });

        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        // ステータスを更新
        listener = loadBalancerListenerDao.read(loadBalancerNo, loadBalancerPort);
        listener.setStatus(LoadBalancerListenerStatus.STOPPED.toString());
        listener.setConfigure(false);
        loadBalancerListenerDao.update(listener);
    }

    public void configureHealthCheck(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        LoadBalancerHealthCheck healthCheck = loadBalancerHealthCheckDao.read(loadBalancerNo);

        // ヘルスチェック情報がない場合はスキップ
        if (healthCheck == null) {
            return;
        }

        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // ヘルスチェック設定を作成
        HealthCheck healthCheck2 = new HealthCheck();
        String target = healthCheck.getCheckProtocol() + ":" + healthCheck.getCheckPort();
        if (StringUtils.isNotEmpty(healthCheck.getCheckPath())) {
            if (healthCheck.getCheckPath().charAt(0) != '/') {
                target = target + "/";
            }
            target = target + healthCheck.getCheckPath();
        }
        healthCheck2.withTarget(target);
        healthCheck2.withTimeout(healthCheck.getCheckTimeout());
        healthCheck2.withInterval(healthCheck.getCheckInterval());
        healthCheck2.withHealthyThreshold(healthCheck.getHealthyThreshold());
        healthCheck2.withUnhealthyThreshold(healthCheck.getUnhealthyThreshold());

        // ヘルスチェック設定に変更がない場合はスキップ
        LoadBalancerDescription description = awsCommonProcess.describeLoadBalancer(awsProcessClient,
                awsLoadBalancer.getName());
        HealthCheck healthCheck3 = description.getHealthCheck();
        if (StringUtils.equals(healthCheck2.getTarget(), healthCheck3.getTarget())
                && ObjectUtils.equals(healthCheck2.getTimeout(), healthCheck3.getTimeout())
                && ObjectUtils.equals(healthCheck2.getInterval(), healthCheck3.getInterval())
                && ObjectUtils.equals(healthCheck2.getHealthyThreshold(), healthCheck3.getHealthyThreshold())
                && ObjectUtils.equals(healthCheck2.getUnhealthyThreshold(), healthCheck3.getUnhealthyThreshold())) {
            return;
        }

        // ヘルスチェック設定を変更
        ConfigureHealthCheckRequest request = new ConfigureHealthCheckRequest();
        request.withLoadBalancerName(awsLoadBalancer.getName());
        request.withHealthCheck(healthCheck2);
        awsProcessClient.getElbClient().configureHealthCheck(request);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200131", awsLoadBalancer.getName()));
        }

        // イベントログ出力
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, null, null, "AwsElbHealthCheckConfig", null, platform.getPlatformNo(),
                new Object[] { platform.getPlatformName(), awsLoadBalancer.getName() });
    }

    public void applySecurityGroups(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        // 非VPCの場合はスキップ
        if (BooleanUtils.isNotTrue(awsProcessClient.getPlatformAws().getVpc())) {
            return;
        }

        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // 現在設定されているSecurityGroup
        LoadBalancerDescription description = awsCommonProcess.describeLoadBalancer(awsProcessClient,
                awsLoadBalancer.getName());
        List<String> groupIds = description.getSecurityGroups();

        // 新しく設定するSecurityGroup
        List<String> newGroupIds = new ArrayList<String>();
        List<SecurityGroup> securityGroups = awsCommonProcess.describeSecurityGroupsByVpcId(awsProcessClient,
                awsProcessClient.getPlatformAws().getVpcId());
        for (String groupName : StringUtils.split(awsLoadBalancer.getSecurityGroups(), ",")) {
            groupName = groupName.trim();
            for (SecurityGroup securityGroup : securityGroups) {
                if (StringUtils.equals(groupName, securityGroup.getGroupName())) {
                    newGroupIds.add(securityGroup.getGroupId());
                    break;
                }
            }
        }

        // SecurityGroupに変更がない場合はスキップ
        if (groupIds.size() == newGroupIds.size() && groupIds.containsAll(newGroupIds)) {
            return;
        }

        // セキュリティグループを変更
        ApplySecurityGroupsToLoadBalancerRequest request = new ApplySecurityGroupsToLoadBalancerRequest();
        request.withLoadBalancerName(awsLoadBalancer.getName());
        request.withSecurityGroups(newGroupIds);
        awsProcessClient.getElbClient().applySecurityGroupsToLoadBalancer(request);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200225", awsLoadBalancer.getName()));
        }

        // イベントログ出力
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, null, null, "AwsElbSecurityGroupsConfig", null, platform.getPlatformNo(),
                new Object[] { platform.getPlatformName(), awsLoadBalancer.getName() });
    }

    public void configureInstances(AwsProcessClient awsProcessClient, Long loadBalancerNo) {
        List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);

        // 振り分け設定するインスタンスがない場合はスキップ
        if (loadBalancerInstances.isEmpty()) {
            return;
        }

        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        // 振り分けを登録・解除するインスタンスを仕分けする
        List<Long> enabledInstanceNos = new ArrayList<Long>();
        List<Long> disabledInstanceNos = new ArrayList<Long>();
        for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
            Long instanceNo = loadBalancerInstance.getInstanceNo();

            // ロードバランサが無効の場合は振り分けを解除する
            if (BooleanUtils.isNotTrue(loadBalancer.getEnabled())) {
                disabledInstanceNos.add(instanceNo);
                continue;
            }

            // インスタンスが無効の場合は振り分けを解除する
            Instance instance = instanceDao.read(instanceNo);
            if (BooleanUtils.isNotTrue(instance.getEnabled())) {
                disabledInstanceNos.add(instanceNo);
                continue;
            }

            if (BooleanUtils.isTrue(loadBalancerInstance.getEnabled())) {
                enabledInstanceNos.add(instanceNo);
            } else {
                disabledInstanceNos.add(instanceNo);
            }
        }

        // 振り分けを登録する
        registerInstances(awsProcessClient, loadBalancerNo, enabledInstanceNos);

        // 振り分けを解除する
        unregisterInstances(awsProcessClient, loadBalancerNo, disabledInstanceNos);
    }

    public void registerInstances(AwsProcessClient awsProcessClient, Long loadBalancerNo, List<Long> instanceNos) {
        if (instanceNos.isEmpty()) {
            // 振り分け登録するインスタンスがない場合はスキップ
            return;
        }

        // 振り分けされていないインスタンス番号を抽出
        {
            List<Long> tmpInstanceNos = new ArrayList<Long>();
            List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao
                    .readByLoadBalancerNo(loadBalancerNo);
            for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
                if (instanceNos.contains(loadBalancerInstance.getInstanceNo())) {
                    LoadBalancerInstanceStatus status = LoadBalancerInstanceStatus.fromStatus(loadBalancerInstance
                            .getStatus());
                    if (status == LoadBalancerInstanceStatus.STOPPED) {
                        tmpInstanceNos.add(loadBalancerInstance.getInstanceNo());
                    }
                }
            }
            instanceNos = tmpInstanceNos;
        }

        if (instanceNos.isEmpty()) {
            // 振り分けされていないインスタンスがない場合はスキップ
            return;
        }

        // 起動しているインスタンス番号を抽出
        {
            List<Long> tmpInstanceNos = new ArrayList<Long>();
            List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
            for (Instance instance : instances) {
                InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
                if (status == InstanceStatus.RUNNING) {
                    tmpInstanceNos.add(instance.getInstanceNo());
                }
            }
            instanceNos = tmpInstanceNos;
        }

        if (instanceNos.isEmpty()) {
            // 起動しているインスタンスがない場合はスキップ
            return;
        }

        // インスタンスIDを取得
        List<String> instanceIds = new ArrayList<String>();
        {
            List<AwsInstance> awsInstances = awsInstanceDao.readInInstanceNos(instanceNos);
            for (AwsInstance awsInstance : awsInstances) {
                instanceIds.add(awsInstance.getInstanceId());
            }
        }

        try {
            // 振り分け登録
            AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
            RegisterInstancesWithLoadBalancerRequest request = new RegisterInstancesWithLoadBalancerRequest();
            request.withLoadBalancerName(awsLoadBalancer.getName());
            for (String instanceId : instanceIds) {
                request.withInstances(new com.amazonaws.services.elasticloadbalancing.model.Instance(instanceId));
            }
            awsProcessClient.getElbClient().registerInstancesWithLoadBalancer(request);

            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-200141", awsLoadBalancer.getName(), instanceIds));
            }

            // イベントログ出力
            Platform platform = awsProcessClient.getPlatform();
            eventLogger.debug(null, null, null, null, "AwsElbInstancesRegist", null, platform.getPlatformNo(),
                    new Object[] { platform.getPlatformName(), awsLoadBalancer.getName(), instanceIds });

        } catch (RuntimeException e) {
            // ステータスの更新
            List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao
                    .readByLoadBalancerNo(loadBalancerNo);
            for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
                if (instanceNos.contains(loadBalancerInstance.getInstanceNo())) {
                    loadBalancerInstance.setStatus(LoadBalancerInstanceStatus.WARNING.toString());
                    loadBalancerInstanceDao.update(loadBalancerInstance);
                }
            }

            throw e;
        }

        // ステータスの更新
        List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
            if (instanceNos.contains(loadBalancerInstance.getInstanceNo())) {
                loadBalancerInstance.setStatus(LoadBalancerInstanceStatus.RUNNING.toString());
                loadBalancerInstanceDao.update(loadBalancerInstance);
            }
        }
    }

    public void unregisterInstances(AwsProcessClient awsProcessClient, Long loadBalancerNo, List<Long> instanceNos) {
        if (instanceNos.isEmpty()) {
            // 振り分け解除するインスタンスがない場合はスキップ
            return;
        }

        // 振り分けされているインスタンス番号を抽出
        {
            List<Long> tmpInstanceNos = new ArrayList<Long>();
            List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao
                    .readByLoadBalancerNo(loadBalancerNo);
            for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
                if (instanceNos.contains(loadBalancerInstance.getInstanceNo())) {
                    LoadBalancerInstanceStatus status = LoadBalancerInstanceStatus.fromStatus(loadBalancerInstance
                            .getStatus());
                    if (status == LoadBalancerInstanceStatus.RUNNING) {
                        tmpInstanceNos.add(loadBalancerInstance.getInstanceNo());
                    }
                }
            }
            instanceNos = tmpInstanceNos;
        }

        if (instanceNos.isEmpty()) {
            // 振り分けされているインスタンスがない場合はスキップ
            return;
        }

        // 起動しているインスタンス番号を抽出
        {
            List<Long> tmpInstanceNos = new ArrayList<Long>();
            List<Instance> instances = instanceDao.readInInstanceNos(instanceNos);
            for (Instance instance : instances) {
                InstanceStatus status = InstanceStatus.fromStatus(instance.getStatus());
                if (status == InstanceStatus.RUNNING) {
                    tmpInstanceNos.add(instance.getInstanceNo());
                }
            }
            instanceNos = tmpInstanceNos;
        }

        if (instanceNos.isEmpty()) {
            // 起動しているインスタンスがない場合はスキップ
            return;
        }

        // インスタンスIDを取得
        List<String> instanceIds = new ArrayList<String>();
        {
            List<AwsInstance> awsInstances = awsInstanceDao.readInInstanceNos(instanceNos);
            for (AwsInstance awsInstance : awsInstances) {
                instanceIds.add(awsInstance.getInstanceId());
            }
        }

        try {
            // 振り分け解除
            AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);
            DeregisterInstancesFromLoadBalancerRequest request = new DeregisterInstancesFromLoadBalancerRequest();
            request.withLoadBalancerName(awsLoadBalancer.getName());
            for (String instanceId : instanceIds) {
                request.withInstances(new com.amazonaws.services.elasticloadbalancing.model.Instance(instanceId));
            }
            awsProcessClient.getElbClient().deregisterInstancesFromLoadBalancer(request);

            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-200142", awsLoadBalancer.getName(), instanceIds));
            }

            // イベントログ出力
            Platform platform = awsProcessClient.getPlatform();
            eventLogger.debug(null, null, null, null, "AwsElbInstancesDeregist", null, platform.getPlatformNo(),
                    new Object[] { platform.getPlatformName(), awsLoadBalancer.getName(), instanceIds });

        } catch (RuntimeException e) {
            // ステータスの更新
            List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao
                    .readByLoadBalancerNo(loadBalancerNo);
            for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
                if (instanceNos.contains(loadBalancerInstance.getInstanceNo())) {
                    loadBalancerInstance.setStatus(LoadBalancerInstanceStatus.WARNING.toString());
                    loadBalancerInstanceDao.update(loadBalancerInstance);
                }
            }

            throw e;
        }

        // ステータスの更新
        List<LoadBalancerInstance> loadBalancerInstances = loadBalancerInstanceDao.readByLoadBalancerNo(loadBalancerNo);
        for (LoadBalancerInstance loadBalancerInstance : loadBalancerInstances) {
            if (instanceNos.contains(loadBalancerInstance.getInstanceNo())) {
                loadBalancerInstance.setStatus(LoadBalancerInstanceStatus.STOPPED.toString());
                loadBalancerInstanceDao.update(loadBalancerInstance);
            }
        }
    }

    public void addDns(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // CNAMEが登録されている場合はスキップ
        if (StringUtils.equals(awsLoadBalancer.getDnsName(), loadBalancer.getCanonicalName())) {
            return;
        }

        String fqdn = loadBalancer.getFqdn();
        String canonicalName = awsLoadBalancer.getDnsName();

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        // CNAMEの追加
        dnsProcessClient.addCanonicalName(fqdn, canonicalName);

        // イベントログ出力
        eventLogger.debug(null, null, null, null, "DnsRegistCanonical", null, loadBalancer.getPlatformNo(),
                new Object[] { fqdn, canonicalName });

        // データベース更新
        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        loadBalancer.setCanonicalName(awsLoadBalancer.getDnsName());
        loadBalancerDao.update(loadBalancer);
    }

    public void deleteDns(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        // CNAMEが登録されていない場合はスキップ
        if (StringUtils.isEmpty(loadBalancer.getCanonicalName())) {
            return;
        }

        String fqdn = loadBalancer.getFqdn();
        String canonicalName = loadBalancer.getCanonicalName();

        try {
            DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

            // CNAMEの削除
            dnsProcessClient.deleteCanonicalName(fqdn);

            // イベントログ出力
            eventLogger.debug(null, null, null, null, "DnsUnregistCanonical", null, loadBalancer.getPlatformNo(),
                    new Object[] { fqdn, canonicalName });

        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        // データベース更新
        loadBalancer = loadBalancerDao.read(loadBalancerNo);
        loadBalancer.setCanonicalName(null);
        loadBalancerDao.update(loadBalancer);
    }

    public void setAwsCommonProcess(AwsCommonProcess awsCommonProcess) {
        this.awsCommonProcess = awsCommonProcess;
    }

    public void setDnsProcessClientFactory(DnsProcessClientFactory dnsProcessClientFactory) {
        this.dnsProcessClientFactory = dnsProcessClientFactory;
    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
