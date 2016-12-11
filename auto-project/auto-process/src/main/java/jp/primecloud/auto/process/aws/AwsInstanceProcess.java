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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.DeviceType;
import com.amazonaws.services.ec2.model.GetPasswordDataRequest;
import com.amazonaws.services.ec2.model.GetPasswordDataResult;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.PlatformValues;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class AwsInstanceProcess extends ServiceSupport {

    protected AwsCommonProcess awsCommonProcess;

    protected EventLogger eventLogger;

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void startInstance(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        Instance instance = instanceDao.read(instanceNo);
        Image image = imageDao.read(instance.getImageNo());
        ImageAws imageAws = imageAwsDao.read(instance.getImageNo());

        // インスタンスストアイメージの場合や、EBSイメージで初回起動の場合
        if (BooleanUtils.isNotTrue(imageAws.getEbsImage()) || StringUtils.isEmpty(awsInstance.getInstanceId())) {
            // インスタンスIDがある場合はスキップ
            if (!StringUtils.isEmpty(awsInstance.getInstanceId())) {
                return;
            }

            // インスタンスの作成
            run(awsProcessClient, instanceNo);

            // インスタンスの作成待ち
            waitRun(awsProcessClient, instanceNo);

            // インスタンスにタグをつける
            createTag(awsProcessClient, instanceNo);

            // Windowsの場合、パスワードデータを取得できるようになるまで待つ
            if (StringUtils.startsWithIgnoreCase(image.getOs(), "windows")) {
                waitGetPasswordData(awsProcessClient, instanceNo);
            }
        }
        // EBSイメージで2回目以降の起動の場合
        else {
            // インスタンスが停止中でない場合はスキップ
            if (!StringUtils.equals(awsInstance.getStatus(), InstanceStateName.Stopped.toString())) {
                return;
            }

            // インスタンスの設定変更
            modify(awsProcessClient, instanceNo);

            // インスタンスの起動
            start(awsProcessClient, instanceNo);

            // インスタンスの起動待ち
            waitStart(awsProcessClient, instanceNo);
        }
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void stopInstance(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // インスタンスIDがない場合はスキップ
        if (StringUtils.isEmpty(awsInstance.getInstanceId())) {
            return;
        }

        Instance instance = instanceDao.read(instanceNo);
        ImageAws imageAws = imageAwsDao.read(instance.getImageNo());

        // インスタンスストアイメージの場合
        if (BooleanUtils.isNotTrue(imageAws.getEbsImage())) {
            try {
                // インスタンスの削除
                terminate(awsProcessClient, instanceNo);

                // インスタンスの削除待ち
                waitTerminate(awsProcessClient, instanceNo);

            } catch (AutoException ignore) {
                // 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
                log.warn(ignore.getMessage());

                awsInstance = awsInstanceDao.read(instanceNo);
                awsInstance.setInstanceId(null);
                awsInstance.setStatus(null);
                awsInstance.setDnsName(null);
                awsInstance.setPrivateDnsName(null);
                awsInstance.setIpAddress(null);
                awsInstance.setPrivateIpAddress(null);
                awsInstanceDao.update(awsInstance);
            }
        }
        // EBSイメージの場合
        else {
            // インスタンスの停止
            stop(awsProcessClient, instanceNo);

            // インスタンスの停止待ち
            waitStop(awsProcessClient, instanceNo);
        }
    }

    public void run(AwsProcessClient awsProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        ImageAws imageAws = imageAwsDao.read(instance.getImageNo());
        PlatformAws platformAws = awsProcessClient.getPlatformAws();

        // インスタンス作成情報
        RunInstancesRequest request = new RunInstancesRequest();
        request.withMinCount(1);
        request.withMaxCount(1);
        request.withImageId(imageAws.getImageId());
        request.withKernelId(StringUtils.isEmpty(imageAws.getKernelId()) ? null : imageAws.getKernelId());
        request.withRamdiskId(StringUtils.isEmpty(imageAws.getRamdiskId()) ? null : imageAws.getRamdiskId());
        request.withKeyName(awsInstance.getKeyName());
        request.withInstanceType(awsInstance.getInstanceType());

        // UserData
        Map<String, String> userData = createUserData(instanceNo);
        request.withUserData(encodeUserData(userData));

        // 非VPCの場合
        if (BooleanUtils.isNotTrue(platformAws.getVpc())) {
            // AvailabilityZone
            if (StringUtils.isNotEmpty(awsInstance.getAvailabilityZone())) {
                request.withPlacement(new Placement(awsInstance.getAvailabilityZone()));
            }

            // SecurityGroup
            if (StringUtils.isNotEmpty(awsInstance.getSecurityGroups())) {
                for (String groupName : StringUtils.split(awsInstance.getSecurityGroups(), ",")) {
                    request.withSecurityGroups(groupName.trim());
                }
            }
        }
        // VPCの場合
        else {
            // Subnet
            request.withSubnetId(awsInstance.getSubnetId());

            // SecurytiGroup
            List<SecurityGroup> securityGroups = awsCommonProcess.describeSecurityGroupsByVpcId(awsProcessClient,
                    platformAws.getVpcId());
            for (String groupName : StringUtils.split(awsInstance.getSecurityGroups(), ",")) {
                groupName = groupName.trim();
                for (SecurityGroup securityGroup : securityGroups) {
                    if (StringUtils.equals(groupName, securityGroup.getGroupName())) {
                        request.withSecurityGroupIds(securityGroup.getGroupId());
                        break;
                    }
                }
            }

            // PrivateIpAddress
            if (StringUtils.isNotEmpty(awsInstance.getPrivateIpAddress())) {
                request.withPrivateIpAddress(awsInstance.getPrivateIpAddress());
            }
        }

        // BlockDeviceMapping
        List<BlockDeviceMapping> blockDeviceMappings = createBlockDeviceMappings(awsProcessClient,
                imageAws.getImageId(), awsInstance.getInstanceType());
        request.withBlockDeviceMappings(blockDeviceMappings);

        // イベントログ出力
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance.getInstanceName(), "AwsInstanceCreate",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName() });

        // インスタンスの作成
        RunInstancesResult result = awsProcessClient.getEc2Client().runInstances(request);
        Reservation reservation = result.getReservation();

        if (reservation == null || reservation.getInstances().size() != 1) {
            // インスタンス作成失敗時
            throw new AutoException("EPROCESS-000105");
        }

        com.amazonaws.services.ec2.model.Instance instance2 = reservation.getInstances().get(0);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100115", instance2.getInstanceId()));
        }

        // データベース更新
        awsInstance.setInstanceId(instance2.getInstanceId());
        awsInstance.setStatus(instance2.getState().getName());
        awsInstanceDao.update(awsInstance);
    }

    public void waitRun(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        // インスタンスの作成待ち
        com.amazonaws.services.ec2.model.Instance instance = awsCommonProcess
                .waitInstance(awsProcessClient, instanceId);

        if (!instance.getState().getName().equals(InstanceStateName.Running.toString())) {
            // インスタンス作成失敗時
            AutoException exception = new AutoException("EPROCESS-000106", instanceId, instance.getState().getName());
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100116", instanceId));
        }

        // イベントログ出力
        Instance instance2 = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance2.getInstanceName(), "AwsInstanceCreateFinish",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName(),
                        instanceId });

        // データベース更新
        awsInstance.setAvailabilityZone(instance.getPlacement().getAvailabilityZone());
        awsInstance.setStatus(instance.getState().getName());
        awsInstance.setDnsName(instance.getPublicDnsName());
        awsInstance.setPrivateDnsName(instance.getPrivateDnsName());
        awsInstance.setIpAddress(instance.getPublicIpAddress());
        awsInstance.setPrivateIpAddress(instance.getPrivateIpAddress());
        awsInstanceDao.update(awsInstance);
    }

    public void modify(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        com.amazonaws.services.ec2.model.Instance instance2 = awsCommonProcess.describeInstance(awsProcessClient,
                instanceId);

        // InstanceType
        if (!StringUtils.equals(awsInstance.getInstanceType(), instance2.getInstanceType())) {
            // InstanceTypeを変更する
            ModifyInstanceAttributeRequest request = new ModifyInstanceAttributeRequest();
            request.withInstanceId(instanceId);
            request.withInstanceType(awsInstance.getInstanceType());
            awsProcessClient.getEc2Client().modifyInstanceAttribute(request);

            // ログ出力
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100161", instanceId));
            }
        }

        // SecurityGroup
        if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getVpc())) {
            // 現在設定されているSecurityGroup
            List<String> groupNames = new ArrayList<String>();
            List<GroupIdentifier> groupIdentifiers = instance2.getSecurityGroups();
            for (GroupIdentifier groupIdentifier : groupIdentifiers) {
                groupNames.add(groupIdentifier.getGroupName());
            }

            // 新しく設定するSecurityGroup
            List<String> newGroupNames = new ArrayList<String>();
            for (String groupName : StringUtils.split(awsInstance.getSecurityGroups(), ",")) {
                newGroupNames.add(groupName.trim());
            }

            // SecurityGroupに変更があるかどうか
            if (!(groupNames.size() == newGroupNames.size()) || !groupNames.containsAll(newGroupNames)) {
                // SecurityGroupのIDを取得する
                List<String> newSecurityGroups = new ArrayList<String>();
                List<SecurityGroup> securityGroups = awsCommonProcess.describeSecurityGroupsByVpcId(awsProcessClient,
                        awsProcessClient.getPlatformAws().getVpcId());
                for (String groupName : newGroupNames) {
                    for (SecurityGroup securityGroup : securityGroups) {
                        if (StringUtils.equals(groupName, securityGroup.getGroupName())) {
                            newSecurityGroups.add(securityGroup.getGroupId());
                            break;
                        }
                    }
                }

                // SecurityGroupを変更する
                ModifyInstanceAttributeRequest request = new ModifyInstanceAttributeRequest();
                request.withInstanceId(instanceId);
                request.withGroups(newSecurityGroups);
                awsProcessClient.getEc2Client().modifyInstanceAttribute(request);

                // ログ出力
                if (log.isInfoEnabled()) {
                    log.info(MessageUtils.getMessage("IPROCESS-100162", instanceId));
                }
            }
        }
    }

    public void start(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance.getInstanceName(), "AwsInstanceStart",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName(),
                        instanceId });

        // インスタンスの起動
        StartInstancesRequest request = new StartInstancesRequest();
        request.withInstanceIds(instanceId);
        StartInstancesResult result = awsProcessClient.getEc2Client().startInstances(request);
        List<InstanceStateChange> startingInstances = result.getStartingInstances();

        // API実行結果チェック
        if (startingInstances.size() == 0) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000125", instanceId);

        } else if (startingInstances.size() > 1) {
            // 複数のインスタンスが起動した場合
            AutoException exception = new AutoException("EPROCESS-000127", instanceId);
            exception.addDetailInfo("result=" + startingInstances);
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100111", instanceId));
        }

        // データベース更新
        awsInstance.setStatus(startingInstances.get(0).getCurrentState().getName());
        awsInstanceDao.update(awsInstance);
    }

    public void waitStart(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        // インスタンスの起動待ち
        com.amazonaws.services.ec2.model.Instance instance = awsCommonProcess
                .waitInstance(awsProcessClient, instanceId);

        if (!instance.getState().getName().equals(InstanceStateName.Running.toString())) {
            // インスタンス起動失敗時
            AutoException exception = new AutoException("EPROCESS-000126", instanceId, instance.getState().getName());
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100112", instanceId));
        }

        // イベントログ出力
        Instance instance2 = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance2.getInstanceName(), "AwsInstanceStartFinish",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName(),
                        instanceId });

        // データベース更新
        awsInstance.setAvailabilityZone(instance.getPlacement().getAvailabilityZone());
        awsInstance.setStatus(instance.getState().getName());
        awsInstance.setDnsName(instance.getPublicDnsName());
        awsInstance.setPrivateDnsName(instance.getPrivateDnsName());
        awsInstance.setIpAddress(instance.getPublicIpAddress());
        awsInstance.setPrivateIpAddress(instance.getPrivateIpAddress());
        awsInstanceDao.update(awsInstance);
    }

    public void stop(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance.getInstanceName(), "AwsInstanceStop",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName(),
                        instanceId });

        // インスタンスの停止
        StopInstancesRequest request = new StopInstancesRequest();
        request.withInstanceIds(instanceId);
        StopInstancesResult result = awsProcessClient.getEc2Client().stopInstances(request);
        List<InstanceStateChange> stoppingInstances = result.getStoppingInstances();

        // API実行結果チェック
        if (stoppingInstances.size() == 0) {
            // インスタンス停止失敗時
            throw new AutoException("EPROCESS-000128", instanceId);

        } else if (stoppingInstances.size() > 1) {
            // 複数のインスタンスが停止した場合
            AutoException exception = new AutoException("EPROCESS-000130", instanceId);
            exception.addDetailInfo("result=" + stoppingInstances);
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100113", instanceId));
        }

        // データベース更新
        awsInstance.setStatus(stoppingInstances.get(0).getCurrentState().getName());
        awsInstanceDao.update(awsInstance);
    }

    public void waitStop(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        // インスタンスの停止待ち
        com.amazonaws.services.ec2.model.Instance instance = awsCommonProcess
                .waitInstance(awsProcessClient, instanceId);

        if (!instance.getState().getName().equals(InstanceStateName.Stopped.toString())) {
            // インスタンス停止失敗時
            AutoException exception = new AutoException("EPROCESS-000129", instanceId, instance.getState().getName());
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100114", instanceId));
        }

        // イベントログ出力
        Instance instance2 = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance2.getInstanceName(), "AwsInstanceStopFinish",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName(),
                        instanceId });

        // データベース更新
        awsInstance.setAvailabilityZone(instance.getPlacement().getAvailabilityZone());
        awsInstance.setStatus(instance.getState().getName());
        awsInstance.setDnsName(instance.getPublicDnsName());
        awsInstance.setPrivateDnsName(instance.getPrivateDnsName());
        awsInstance.setIpAddress(instance.getPublicIpAddress());
        awsInstance.setPrivateIpAddress(instance.getPrivateIpAddress());
        awsInstanceDao.update(awsInstance);
    }

    public void terminate(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance.getInstanceName(), "AwsInstanceDelete",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName(),
                        instanceId });

        // インスタンスの削除
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.withInstanceIds(instanceId);
        TerminateInstancesResult result = awsProcessClient.getEc2Client().terminateInstances(request);
        List<InstanceStateChange> terminatingInstances = result.getTerminatingInstances();

        // API実行結果チェック
        if (terminatingInstances.size() == 0) {
            // インスタンス削除失敗時
            throw new AutoException("EPROCESS-000107", instanceId);

        } else if (terminatingInstances.size() > 1) {
            // 複数のインスタンスが削除された場合
            AutoException exception = new AutoException("EPROCESS-000108", instanceId);
            exception.addDetailInfo("result=" + terminatingInstances);
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100117", instanceId));
        }

        // データベース更新
        awsInstance.setStatus(terminatingInstances.get(0).getCurrentState().getName());
        awsInstanceDao.update(awsInstance);
    }

    public void waitTerminate(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        // インスタンスの削除待ち
        com.amazonaws.services.ec2.model.Instance instance;
        try {
            instance = awsCommonProcess.waitInstance(awsProcessClient, instanceId);

            if (!StringUtils.equals(instance.getState().getName(), InstanceStateName.Terminated.toString())) {
                // インスタンス削除失敗時
                AutoException exception = new AutoException("EPROCESS-000109", instanceId, instance.getState()
                        .getName());
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
                throw exception;
            }
        } catch (AutoException e) {
            // インスタンス情報を参照できない場合、既に削除されたものとして例外を無視する
            if ("EPROCESS-000101".equals(e.getCode())) {
                instance = null;
            } else {
                throw e;
            }
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100118", instanceId));
        }

        // イベントログ出力
        Instance instance2 = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(null, null, instanceNo, instance2.getInstanceName(), "AwsInstanceDeleteFinish",
                awsInstance.getInstanceType(), platform.getPlatformNo(), new Object[] { platform.getPlatformName(),
                        instanceId });

        String status = null;
        if (instance != null) {
            status = instance.getState().getName();
        }

        // データベース更新
        awsInstance.setInstanceId(null);
        awsInstance.setStatus(status);
        awsInstance.setDnsName(null);
        awsInstance.setPrivateDnsName(null);
        awsInstance.setIpAddress(null);
        awsInstance.setPrivateIpAddress(null);
        awsInstanceDao.update(awsInstance);
    }

    public void waitGetPasswordData(AwsProcessClient awsProcessClient, Long instanceNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        String instanceId = awsInstance.getInstanceId();

        GetPasswordDataRequest request = new GetPasswordDataRequest();
        request.withInstanceId(instanceId);

        while (true) {
            GetPasswordDataResult result = awsProcessClient.getEc2Client().getPasswordData(request);

            if (StringUtils.isNotEmpty(result.getPasswordData())) {
                break;
            }

            try {
                Thread.sleep(1000L * awsProcessClient.getDescribeInterval());
            } catch (InterruptedException ignore) {
            }
        }
    }

    protected Map<String, String> createUserData(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        // UserDataを作成
        Map<String, String> userData = new HashMap<String, String>();

        // DB情報
        userData.put("instanceName", instance.getInstanceName());
        userData.put("farmName", farm.getFarmName());

        // FQDN
        String fqdn = instance.getFqdn();
        userData.put("hostname", fqdn);

        // 初期スクリプト情報
        userData.put("scriptserver", Config.getProperty("script.server"));

        // DNS情報
        userData.putAll(createDnsUserData(instanceNo));

        // Puppet情報
        userData.putAll(createPuppetUserData(instanceNo));

        // VPN情報
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (BooleanUtils.isNotTrue(platform.getInternal())) {
            // 外部のプラットフォームでVPCを利用しない場合、VPN情報を含める
            userData.putAll(createVpnUserData(instanceNo));
        }

        return userData;
    }

    protected Map<String, String> createDnsUserData(Long instanceNo) {
        // UserDataを作成
        Map<String, String> userData = new HashMap<String, String>();

        // Primary DNSサーバ
        userData.put("dns", Config.getProperty("dns.server"));

        // Secondry DNSサーバ
        String dns2 = Config.getProperty("dns.server2");
        if (dns2 != null && dns2.length() > 0) {
            userData.put("dns2", dns2);
        }

        // DNSドメイン
        userData.put("dnsdomain", Config.getProperty("dns.domain"));

        return userData;
    }

    protected Map<String, String> createPuppetUserData(Long instanceNo) {
        // UserDataを作成
        Map<String, String> userData = new HashMap<String, String>();

        // PuppetMaster情報
        userData.put("puppetmaster", Config.getProperty("puppet.masterHost"));

        return userData;
    }

    protected Map<String, String> createVpnUserData(Long instanceNo) {
        // UserDataを作成
        Map<String, String> userData = new HashMap<String, String>();

        // VPN情報のユーザとパスワードをセットする
        Instance instance = instanceDao.read(instanceNo);
        userData.put("vpnuser", instance.getFqdn());
        userData.put("vpnuserpass", instance.getInstanceCode());

        // VPNサーバ情報
        userData.put("vpnserver", Config.getProperty("vpn.server"));
        userData.put("vpnport", Config.getProperty("vpn.port"));

        // ZIPパスワード
        userData.put("vpnzippass", Config.getProperty("vpn.zippass"));

        // クライアント証明書ダウンロードURL
        userData.put("vpnclienturl", Config.getProperty("vpn.clienturl"));

        return userData;
    }

    protected String encodeUserData(Map<String, String> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                sb.append(key).append("=").append(value).append(";");
            }
        }
        sb.delete(sb.length() - 1, sb.length());

        // UserDataをエンコード
        String userData = sb.toString();
        userData = new String(Base64.encodeBase64(userData.getBytes()));

        return userData;
    }

    protected List<BlockDeviceMapping> createBlockDeviceMappings(AwsProcessClient awsProcessClient, String imageId,
            String instanceType) {
        // イメージの取得
        com.amazonaws.services.ec2.model.Image image = awsCommonProcess.describeImage(awsProcessClient, imageId);

        if (image == null) {
            return null;
        }

        // EBSイメージでなければBlockDeviceMappingsを設定しない
        if (!image.getRootDeviceType().equals(DeviceType.Ebs.toString())) {
            return null;
        }

        List<BlockDeviceMapping> mappings = new ArrayList<BlockDeviceMapping>();

        // イメージのBlockDeviceMappingの設定
        List<BlockDeviceMapping> imageMappings = createImageBlockDeviceMappings(awsProcessClient, image);
        if (imageMappings != null) {
            mappings.addAll(imageMappings);
        }

        // インスタンスストアのBlockDeviceMappingの設定
        List<BlockDeviceMapping> instanceStoreMappings = createInstanceStoreBlockDeviceMappings(awsProcessClient,
                image, instanceType);
        if (instanceStoreMappings != null) {
            mappings.addAll(instanceStoreMappings);
        }

        // 追加のBlockDeviceMappingの設定
        List<BlockDeviceMapping> additionalMappings = createAdditionalBlockDeviceMappings(awsProcessClient, image);
        if (additionalMappings != null) {
            mappings.addAll(additionalMappings);
        }

        return mappings;
    }

    protected List<BlockDeviceMapping> createImageBlockDeviceMappings(AwsProcessClient awsProcessClient,
            com.amazonaws.services.ec2.model.Image image) {
        // イメージのBlockDeviceMappingを複製する
        List<BlockDeviceMapping> mappings = new ArrayList<BlockDeviceMapping>();
        for (BlockDeviceMapping originalMapping : image.getBlockDeviceMappings()) {
            BlockDeviceMapping mapping = originalMapping.clone();
            if (originalMapping.getEbs() != null) {
                mapping.withEbs(originalMapping.getEbs().clone());
            }
            mappings.add(mapping);
        }

        for (BlockDeviceMapping mapping : mappings) {
            if (mapping.getEbs() == null) {
                continue;
            }

            // インスタンス削除時にEBSが削除されるようにする
            mapping.getEbs().withDeleteOnTermination(true);

            // スナップショットから作る場合、Encryptedは指定できない
            if (StringUtils.isNotEmpty(mapping.getEbs().getSnapshotId())) {
                mapping.getEbs().withEncrypted(null);
            }

            // ボリュームタイプを指定する
            String volumeType = Config.getProperty("aws.volumeType");
            if (StringUtils.isNotEmpty(volumeType)) {
                mapping.getEbs().setVolumeType(volumeType);
            }
        }

        return mappings;
    }

    protected List<BlockDeviceMapping> createInstanceStoreBlockDeviceMappings(AwsProcessClient awsProcessClient,
            com.amazonaws.services.ec2.model.Image image, String instanceType) {
        int count = AwsInstanceTypeDefinition.getInstanceStoreCount(instanceType);
        if (count == 0) {
            return null;
        }

        // BlockDeviceMappingに追加するインスタンスストアは4つに制限する
        if (count > 4) {
            count = 4;
        }

        List<BlockDeviceMapping> mappings = new ArrayList<BlockDeviceMapping>();
        for (int i = 0; i < count; i++) {
            String virtualName = "ephemeral" + i;

            // イメージのBlockDeviceMappingでインスタンスストアが指定されている場合はスキップする
            boolean exist = false;
            for (BlockDeviceMapping mapping : image.getBlockDeviceMappings()) {
                if (virtualName.equals(mapping.getVirtualName())) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                continue;
            }

            // 空いているデバイス名の識別子を取得
            String identifier = null;
            for (int j = 0; j < 25; j++) {
                char id = (char) ('b' + j);
                exist = false;
                for (BlockDeviceMapping mapping : image.getBlockDeviceMappings()) {
                    if (mapping.getDeviceName().equals("/dev/sd" + id) || mapping.getDeviceName().equals("xvd" + id)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    identifier = String.valueOf(id);
                    break;
                }
            }

            if (identifier == null) {
                // b から z までの識別子が全て使われている場合は何もしない
                continue;
            }

            BlockDeviceMapping mapping = new BlockDeviceMapping();
            mapping.withVirtualName(virtualName);
            if (StringUtils.equals(image.getPlatform(), PlatformValues.Windows.toString())) {
                mapping.withDeviceName("xvd" + identifier);
            } else {
                mapping.withDeviceName("/dev/sd" + identifier);
            }
            mappings.add(mapping);
        }

        return mappings;
    }

    // 拡張用
    protected List<BlockDeviceMapping> createAdditionalBlockDeviceMappings(AwsProcessClient awsProcessClient,
            com.amazonaws.services.ec2.model.Image image) {
        return null;
    }

    public void createTag(AwsProcessClient awsProcessClient, Long instanceNo) {
        // Eucalyptusの場合はタグを付けない
        PlatformAws platformAws = awsProcessClient.getPlatformAws();
        if (BooleanUtils.isTrue(platformAws.getEuca())) {
            return;
        }

        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        User user = userDao.read(awsProcessClient.getUserNo());
        Farm farm = farmDao.read(instance.getFarmNo());

        // インスタンスにタグを追加する
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("Name", instance.getFqdn()));
        tags.add(new Tag("UserName", user.getUsername()));
        tags.add(new Tag("CloudName", farm.getDomainName()));
        tags.add(new Tag("ServerName", instance.getFqdn()));
        awsCommonProcess.createTag(awsProcessClient, awsInstance.getInstanceId(), tags);

        com.amazonaws.services.ec2.model.Instance instance2 = awsCommonProcess.describeInstance(awsProcessClient,
                awsInstance.getInstanceId());

        // EBSにタグを追加する
        for (InstanceBlockDeviceMapping mapping : instance2.getBlockDeviceMappings()) {
            if (mapping.getEbs() == null) {
                continue;
            }

            String deviceName = mapping.getDeviceName();
            if (deviceName.lastIndexOf("/") != -1) {
                deviceName = deviceName.substring(deviceName.lastIndexOf("/") + 1);
            }

            tags = new ArrayList<Tag>();
            tags.add(new Tag("Name", instance.getFqdn() + "_" + deviceName));
            tags.add(new Tag("UserName", user.getUsername()));
            tags.add(new Tag("CloudName", farm.getDomainName()));
            tags.add(new Tag("ServerName", instance.getFqdn()));
            awsCommonProcess.createTag(awsProcessClient, mapping.getEbs().getVolumeId(), tags);
        }
    }

    public void setAwsCommonProcess(AwsCommonProcess awsCommonProcess) {
        this.awsCommonProcess = awsCommonProcess;
    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
