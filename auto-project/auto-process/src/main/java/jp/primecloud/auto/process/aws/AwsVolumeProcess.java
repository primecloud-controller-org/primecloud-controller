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
import java.util.List;

import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.AttachVolumeResult;
import com.amazonaws.services.ec2.model.CreateVolumeRequest;
import com.amazonaws.services.ec2.model.CreateVolumeResult;
import com.amazonaws.services.ec2.model.DeleteVolumeRequest;
import com.amazonaws.services.ec2.model.DetachVolumeRequest;
import com.amazonaws.services.ec2.model.DetachVolumeResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.VolumeAttachment;
import com.amazonaws.services.ec2.model.VolumeState;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class AwsVolumeProcess extends ServiceSupport {

    protected AwsCommonProcess awsCommonProcess;

    protected EventLogger eventLogger;

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void startVolume(AwsProcessClient awsProcessClient, Long instanceNo) {
        // ボリューム情報の取得
        List<AwsVolume> awsVolumes = awsVolumeDao.readByInstanceNo(instanceNo);
        if (awsVolumes.isEmpty()) {
            return;
        }

        for (AwsVolume awsVolume : awsVolumes) {
            startVolume(awsProcessClient, instanceNo, awsVolume.getVolumeNo());
        }
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     * @param volumeNo
     */
    public void startVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);

        // ボリュームIDがない場合、ボリュームを作成する
        if (StringUtils.isEmpty(awsVolume.getVolumeId())) {
            // ボリュームの作成
            createVolume(awsProcessClient, instanceNo, volumeNo);

            // ボリュームの作成待ち
            waitCreateVolume(awsProcessClient, instanceNo, volumeNo);

            // ボリュームにタグを付ける
            createTag(awsProcessClient, volumeNo);
        }

        // インスタンスIDがない場合、インスタンスにアタッチする
        if (StringUtils.isEmpty(awsVolume.getInstanceId())) {
            // ボリュームのアタッチ
            attachVolume(awsProcessClient, instanceNo, volumeNo);

            // ボリュームのアタッチ待ち
            waitAttachVolume(awsProcessClient, instanceNo, volumeNo);
        }
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void stopVolume(AwsProcessClient awsProcessClient, Long instanceNo) {
        // ボリューム情報の取得
        List<AwsVolume> awsVolumes = awsVolumeDao.readByInstanceNo(instanceNo);
        if (awsVolumes.isEmpty()) {
            return;
        }

        for (AwsVolume awsVolume : awsVolumes) {
            stopVolume(awsProcessClient, instanceNo, awsVolume.getVolumeNo());
        }
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     * @param volumeNo
     */
    public void stopVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);

        // インスタンスIDがある場合、インスタンスからデタッチする
        if (StringUtils.isNotEmpty(awsVolume.getInstanceId())) {
            try {
                // ボリュームのデタッチ
                detachVolume(awsProcessClient, instanceNo, volumeNo);

                // ボリュームのデタッチ待ち
                waitDetachVolume(awsProcessClient, instanceNo, volumeNo);

            } catch (AutoException ignore) {
                // 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
                log.warn(ignore.getMessage());

                awsVolume = awsVolumeDao.read(volumeNo);
                awsVolume.setStatus(VolumeState.Error.toString());
                awsVolume.setInstanceId(null);
                awsVolumeDao.update(awsVolume);
            }
        }
    }

    public void createVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);

        // ボリュームの作成
        CreateVolumeRequest request = new CreateVolumeRequest();
        request.withSize(awsVolume.getSize());
        request.withSnapshotId(awsVolume.getSnapshotId());
        request.withAvailabilityZone(awsVolume.getAvailabilityZone());
        CreateVolumeResult result = awsProcessClient.getEc2Client().createVolume(request);
        Volume volume = result.getVolume();

        String volumeId = volume.getVolumeId();

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100121", volumeId));
        }

        //イベントログ出力
        Component component = componentDao.read(awsVolume.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(awsVolume.getComponentNo(), component.getComponentName(), instanceNo,
                instance.getInstanceName(), "AwsEbsCreate", null, platform.getPlatformNo(),
                new Object[] { platform.getPlatformName() });

        // データベース更新
        awsVolume.setVolumeId(volume.getVolumeId());
        awsVolume.setStatus(volume.getState());
        awsVolumeDao.update(awsVolume);
    }

    public void waitCreateVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        String volumeId = awsVolume.getVolumeId();

        // ボリュームの作成待ち
        Volume volume;
        try {
            volume = awsCommonProcess.waitVolume(awsProcessClient, volumeId);

            if (!StringUtils.equals(volume.getState(), VolumeState.Available.toString())) {
                // ボリューム作成失敗時
                AutoException exception = new AutoException("EPROCESS-000113", volumeId, volume.getState());
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
                throw exception;
            }

            // ログ出力
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100122", volumeId));
            }
        } catch (AutoException e) {
            // ボリューム作成失敗時
            awsVolume = awsVolumeDao.read(volumeNo);
            awsVolume.setVolumeId(null);
            awsVolume.setStatus(null);
            awsVolumeDao.update(awsVolume);

            throw e;
        }

        //イベントログ出力
        Component component = componentDao.read(awsVolume.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = awsProcessClient.getPlatform();
        eventLogger.debug(awsVolume.getComponentNo(), component.getComponentName(), instanceNo,
                instance.getInstanceName(), "AwsEbsCreateFinish", null, platform.getPlatformNo(), new Object[] {
                        platform.getPlatformName(), awsVolume.getVolumeId(), awsVolume.getSize() });

        // データベース更新
        awsVolume = awsVolumeDao.read(volumeNo);
        awsVolume.setStatus(volume.getState());
        awsVolumeDao.update(awsVolume);
    }

    public void attachVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        String volumeId = awsVolume.getVolumeId();

        //イベントログ出力
        Component component = componentDao.read(awsVolume.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        eventLogger.debug(awsVolume.getComponentNo(), component.getComponentName(), instanceNo,
                instance.getInstanceName(), "AwsEbsAttach", null, awsProcessClient.getPlatform().getPlatformNo(),
                new Object[] { instance.getInstanceName(), awsVolume.getVolumeId(), awsVolume.getDevice() });

        // ボリュームのアタッチ
        // awsProcessClient.attachVolume(volumeId, instanceId, awsVolume.getDevice());
        // ボリュームのアタッチ
        AttachVolumeRequest request = new AttachVolumeRequest();
        request.withVolumeId(volumeId);
        request.withInstanceId(awsInstance.getInstanceId());
        request.withDevice(awsVolume.getDevice());
        AttachVolumeResult result = awsProcessClient.getEc2Client().attachVolume(request);
        VolumeAttachment attachment = result.getAttachment();

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100123", volumeId, attachment.getInstanceId()));
        }

        // データベースの更新
        awsVolume.setInstanceId(attachment.getInstanceId());
        awsVolumeDao.update(awsVolume);
    }

    public void waitAttachVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        String volumeId = awsVolume.getVolumeId();

        Volume volume = null;
        try {
            // volume = awsProcessClient.waitAttachVolume(volumeId, instanceId);
            // TODO: アタッチ情報がすぐに更新されない問題に暫定的に対応
            int retry = 6;
            for (int i = 0; i < retry; i++) {
                volume = awsCommonProcess.waitVolume(awsProcessClient, volumeId);
                if (StringUtils.equals(volume.getState(), VolumeState.InUse.toString())) {
                    break;
                }
            }

            if (!StringUtils.equals(volume.getState(), VolumeState.InUse.toString())) {
                // アタッチに失敗した場合
                AutoException exception = new AutoException("EPROCESS-000115", awsVolume.getInstanceId(), volumeId,
                        volume.getState());
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
                throw exception;
            }

            // ログ出力
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100124", volumeId, awsVolume.getInstanceId()));
            }
        } catch (AutoException e) {
            // アタッチに失敗した場合
            awsVolume = awsVolumeDao.read(volumeNo);
            awsVolume.setStatus(VolumeState.Error.toString());
            awsVolume.setInstanceId(null);
            awsVolumeDao.update(awsVolume);

            throw e;
        }

        //イベントログ出力
        Component component = componentDao.read(awsVolume.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        eventLogger.debug(awsVolume.getComponentNo(), component.getComponentName(), instanceNo,
                instance.getInstanceName(), "AwsEbsAttachFinish", null, awsProcessClient.getPlatform().getPlatformNo(),
                new Object[] { instance.getInstanceName(), awsVolume.getVolumeId(), awsVolume.getDevice() });

        // データベースの更新
        awsVolume = awsVolumeDao.read(volumeNo);
        awsVolume.setStatus(volume.getState());
        awsVolumeDao.update(awsVolume);
    }

    public void detachVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        String volumeId = awsVolume.getVolumeId();

        //イベントログ出力
        Component component = componentDao.read(awsVolume.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        eventLogger.debug(awsVolume.getComponentNo(), component.getComponentName(), instanceNo,
                instance.getInstanceName(), "AwsEbsDetach", null, awsProcessClient.getPlatform().getPlatformNo(),
                new Object[] { instance.getInstanceName(), awsVolume.getVolumeId(), awsVolume.getDevice() });

        // ボリュームのデタッチ
        DetachVolumeRequest request = new DetachVolumeRequest();
        request.withVolumeId(volumeId);
        request.withInstanceId(awsVolume.getInstanceId());
        request.withDevice(awsVolume.getDevice());
        DetachVolumeResult result = awsProcessClient.getEc2Client().detachVolume(request);
        VolumeAttachment attachment = result.getAttachment();

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100125", volumeId, attachment.getInstanceId()));
        }
    }

    public void waitDetachVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        String volumeId = awsVolume.getVolumeId();
        String instanceId = awsVolume.getInstanceId();

        Volume volume = null;
        try {
            // TODO: アタッチ情報がすぐに更新されない問題に暫定的に対応
            int retry = 6;
            for (int i = 0; i < retry; i++) {
                volume = awsCommonProcess.waitVolume(awsProcessClient, volumeId);
                if (StringUtils.equals(volume.getState(), VolumeState.Available.toString())) {
                    break;
                }
            }

            if (!StringUtils.equals(volume.getState(), VolumeState.Available.toString())) {
                // デタッチに失敗した場合
                AutoException exception = new AutoException("EPROCESS-000116", instanceId, volumeId, volume.getState());
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
                throw exception;
            }

            // ログ出力
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100126", volumeId, instanceId));
            }
        } catch (AutoException e) {
            // デタッチに失敗した場合
            awsVolume = awsVolumeDao.read(volumeNo);
            awsVolume.setStatus(VolumeState.Error.toString());
            awsVolume.setInstanceId(null);
            awsVolumeDao.update(awsVolume);

            throw e;
        }

        //イベントログ出力
        Component component = componentDao.read(awsVolume.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        eventLogger.debug(awsVolume.getComponentNo(), component.getComponentName(), instanceNo,
                instance.getInstanceName(), "AwsEbsDetachFinish", null, awsProcessClient.getPlatform().getPlatformNo(),
                new Object[] { instance.getInstanceName(), awsVolume.getVolumeId(), awsVolume.getDevice() });

        // データベースの更新
        awsVolume = awsVolumeDao.read(volumeNo);
        awsVolume.setStatus(volume.getState());
        awsVolume.setInstanceId(null);
        awsVolumeDao.update(awsVolume);
    }

    public void deleteVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        String volumeId = awsVolume.getVolumeId();

        // イベントログ出力
        String componentName = null;
        if (awsVolume.getComponentNo() != null) {
            Component component = componentDao.read(awsVolume.getComponentNo());
            componentName = component.getComponentName();
        }
        String instanceName = null;
        if (instanceNo != null) {
            Instance instance = instanceDao.read(instanceNo);
            instanceName = instance.getInstanceName();
        }
        eventLogger.debug(awsVolume.getComponentNo(), componentName, instanceNo, instanceName, "AwsEbsDelete", null,
                awsProcessClient.getPlatform().getPlatformNo(), new Object[] {
                        awsProcessClient.getPlatform().getPlatformName(), awsVolume.getVolumeId() });

        // ボリュームの削除
        DeleteVolumeRequest request = new DeleteVolumeRequest();
        request.withVolumeId(volumeId);
        awsProcessClient.getEc2Client().deleteVolume(request);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100127", volumeId));
        }
    }

    public void waitDeleteVolume(AwsProcessClient awsProcessClient, Long instanceNo, Long volumeNo) {
        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        String volumeId = awsVolume.getVolumeId();

        // ボリュームの削除待ち
        while (true) {
            try {
                Thread.sleep(1000L * awsProcessClient.getDescribeInterval());
            } catch (InterruptedException ignore) {
            }

            try {
                awsCommonProcess.describeVolume(awsProcessClient, volumeId);
            } catch (AutoException ignore) {
                // 例外が発生したらボリュームが削除されて参照できなくなったものとみなす
                break;
            }
        }

        // イベントログ出力
        String componentName = null;
        if (awsVolume.getComponentNo() != null) {
            Component component = componentDao.read(awsVolume.getComponentNo());
            componentName = component.getComponentName();
        }
        String instanceName = null;
        if (instanceNo != null) {
            Instance instance = instanceDao.read(instanceNo);
            instanceName = instance.getInstanceName();
        }
        eventLogger.debug(awsVolume.getComponentNo(), componentName, instanceNo, instanceName, "AwsEbsDeleteFinish",
                null, awsProcessClient.getPlatform().getPlatformNo(), new Object[] {
                        awsProcessClient.getPlatform().getPlatformName(), awsVolume.getVolumeId() });

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100128", volumeId));
        }

        // データベースの更新
        awsVolume = awsVolumeDao.read(volumeNo);
        awsVolume.setVolumeId(null);
        awsVolume.setStatus(null);
        awsVolumeDao.update(awsVolume);
    }

    public void createTag(AwsProcessClient awsProcessClient, Long volumeNo) {
        // Eucalyptusの場合はタグを付けない
        PlatformAws platformAws = awsProcessClient.getPlatformAws();
        if (BooleanUtils.isTrue(platformAws.getEuca())) {
            return;
        }

        AwsVolume awsVolume = awsVolumeDao.read(volumeNo);
        Component component = componentDao.read(awsVolume.getComponentNo());
        Instance instance = instanceDao.read(awsVolume.getInstanceNo());
        User user = userDao.read(awsProcessClient.getUserNo());
        Farm farm = farmDao.read(instance.getFarmNo());

        // タグを追加する
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("Name", instance.getFqdn() + "_" + component.getComponentName()));
        tags.add(new Tag("UserName", user.getUsername()));
        tags.add(new Tag("CloudName", farm.getDomainName()));
        tags.add(new Tag("ServerName", instance.getFqdn()));
        tags.add(new Tag("ServiceName", component.getComponentName()));
        awsCommonProcess.createTag(awsProcessClient, awsVolume.getVolumeId(), tags);
    }

    public void setAwsCommonProcess(AwsCommonProcess awsCommonProcess) {
        this.awsCommonProcess = awsCommonProcess;
    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
