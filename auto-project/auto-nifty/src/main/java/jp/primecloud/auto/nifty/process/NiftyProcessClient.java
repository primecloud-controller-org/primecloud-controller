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
package jp.primecloud.auto.nifty.process;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.nifty.dto.InstanceDto;
import jp.primecloud.auto.nifty.dto.VolumeDto;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nifty.cloud.sdk.NiftyClientException;
import com.nifty.cloud.sdk.NiftyServiceException;
import com.nifty.cloud.sdk.disk.NiftyDiskClient;
import com.nifty.cloud.sdk.disk.model.AttachVolumeRequest;
import com.nifty.cloud.sdk.disk.model.AttachVolumeResult;
import com.nifty.cloud.sdk.disk.model.CreateVolumeRequest;
import com.nifty.cloud.sdk.disk.model.CreateVolumeResult;
import com.nifty.cloud.sdk.disk.model.DeleteVolumeRequest;
import com.nifty.cloud.sdk.disk.model.DeleteVolumeResult;
import com.nifty.cloud.sdk.disk.model.DescribeVolumesRequest;
import com.nifty.cloud.sdk.disk.model.DescribeVolumesResult;
import com.nifty.cloud.sdk.disk.model.DetachVolumeRequest;
import com.nifty.cloud.sdk.disk.model.DetachVolumeResult;
import com.nifty.cloud.sdk.server.NiftyServerClient;
import com.nifty.cloud.sdk.server.model.DescribeInstancesRequest;
import com.nifty.cloud.sdk.server.model.DescribeInstancesResult;
import com.nifty.cloud.sdk.server.model.Instance;
import com.nifty.cloud.sdk.server.model.InstanceIdSet;
import com.nifty.cloud.sdk.server.model.InstanceStateChange;
import com.nifty.cloud.sdk.server.model.Reservation;
import com.nifty.cloud.sdk.server.model.RunInstancesRequest;
import com.nifty.cloud.sdk.server.model.RunInstancesResult;
import com.nifty.cloud.sdk.server.model.StartInstancesRequest;
import com.nifty.cloud.sdk.server.model.StartInstancesResult;
import com.nifty.cloud.sdk.server.model.StopInstancesRequest;
import com.nifty.cloud.sdk.server.model.StopInstancesResult;
import com.nifty.cloud.sdk.server.model.TerminateInstancesRequest;
import com.nifty.cloud.sdk.server.model.TerminateInstancesResult;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyProcessClient {

    protected Log log = LogFactory.getLog(getClass());

    protected NiftyServerClient niftyServerClient;

    protected NiftyDiskClient niftyDiskClient;

    protected Long platformNo;

    protected int describeInterval;

    private final static Object lock = new Object();

    public NiftyProcessClient(NiftyServerClient niftyServerClient, Long platformNo) {
        this.niftyServerClient = niftyServerClient;
        this.platformNo = platformNo;
    }

    public NiftyProcessClient(NiftyDiskClient niftyDiskClient, Long platformNo) {
        this.niftyDiskClient = niftyDiskClient;
        this.platformNo = platformNo;
    }

    public NiftyServerClient getNiftyServerClient() {
        return niftyServerClient;
    }

    public NiftyDiskClient getNiftyDiskClient() {
        return niftyDiskClient;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public List<Instance> describeAllInstances() {
        // 全インスタンスの参照
        DescribeInstancesRequest request = new DescribeInstancesRequest();

        try {
            Thread.sleep(1000 * describeInterval);
        } catch (InterruptedException ignore) {
        }

        DescribeInstancesResult result = niftyServerClient.describeInstances(request);
        List<Reservation> reservations = result.getReservations();

        List<Instance> instances = new ArrayList<Instance>();
        for (Reservation reservation : reservations) {
            instances.addAll(reservation.getInstances());
        }

        return instances;
    }

    public InstanceDto describeInstance(String instanceId) {
        // 単一インスタンスの参照
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withInstanceIds(instanceId);

        try {
            Thread.sleep(1000 * describeInterval);
        } catch (InterruptedException ignore) {
        }

        DescribeInstancesResult result = niftyServerClient.describeInstances(request);
        List<Reservation> reservations = result.getReservations();

        // API実行結果チェック
        if (reservations.size() == 0) {
            // インスタンスが存在しない場合
            throw new AutoException("EPROCESS-000601", instanceId);

        } else if (reservations.size() > 1) {
            // インスタンスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000602", instanceId);
            exception.addDetailInfo("result=" + reservations);
            throw exception;
        }

        List<Instance> instances = reservations.get(0).getInstances();

        if (instances.size() == 0) {
            // インスタンスが存在しない場合
            throw new AutoException("EPROCESS-000601", instanceId);

        } else if (instances.size() > 1) {
            // インスタンスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000603", instanceId);
            exception.addDetailInfo("result=" + instances);
            throw exception;
        }

        return new InstanceDto(instances.get(0));
    }

    public InstanceDto waitInstance(String instanceId) {
        // インスタンスの処理待ち
        String[] stableStatus = new String[] { "running", "stopped" };
        // TODO: ニフティクラウドAPIの経過観察（インスタンスのステータス warning はAPIリファレンスに記載されていない）
        String[] unstableStatus = new String[] { "pending", "warning" };//

        InstanceDto instance;
        while (true) {
            instance = describeInstance(instanceId);
            String status = instance.getState().getName();

            if (ArrayUtils.contains(stableStatus, status)) {
                break;
            }

            if (!ArrayUtils.contains(unstableStatus, status)) {
                // 予期しないステータス
                AutoException exception = new AutoException("EPROCESS-000604", instanceId, status);
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
                throw exception;
            }
        }

        return instance;
    }

    public InstanceDto runInstance(String imageId, String keyName, String instanceType, String password) {
        // インスタンスの起動
        RunInstancesRequest request = new RunInstancesRequest();
        request.setImageId(imageId);
        request.setMinCount(1);
        request.setMaxCount(1);
        request.setKeyName(keyName);
        request.setInstanceType(instanceType);
        request.setAccountingType("2"); // 従量課金
        request.setPassword(password);
        request.setDisableApiTermination(false); // APIでインスタンスを削除できるようにする

        RunInstancesResult result = new RunInstancesResult();
        try {
            result = niftyServerClient.runInstances(request);
        } catch (NiftyServiceException e) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000605");
        } catch (NiftyClientException e) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000605");
        }

        if (result.getReservation() == null || result.getReservation().getInstances().size() != 1) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000605");
        }

        InstanceDto instance = new InstanceDto(result.getReservation().getInstances().get(0));
        String instanceId = instance.getInstanceId();

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100511", instanceId));
        }

        return instance;
    }

    public InstanceDto waitRunInstance(String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（RunInstances直後はインスタンスを参照できないことがあるので、インスタンス情報を取得できるまでは全インスタンス情報を取得する）
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            boolean exist = false;
            List<Instance> instances = describeAllInstances();
            for (Instance instance : instances) {
                if (StringUtils.equals(instanceId, instance.getInstanceId())) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                break;
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、インスタンスの起動に失敗したものとする
                throw new AutoException("EPROCESS-000605", instanceId);
            }
        }

        InstanceDto instance = waitInstance(instanceId);

        String state = instance.getState().getName();
        if (!"running".equals(state)) {
            // インスタンス起動失敗時
            AutoException exception = new AutoException("EPROCESS-000606", instanceId, state);
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100512", instanceId));
        }

        return instance;
    }

    public InstanceStateChange startInstance(String instanceId, String instanceType) {
        // インスタンスの起動
        StartInstancesRequest request = new StartInstancesRequest();
        List<InstanceIdSet> instances = new ArrayList<InstanceIdSet>();
        InstanceIdSet instanceIdSet = new InstanceIdSet();
        instanceIdSet.setInstanceId(instanceId);
        instanceIdSet.setInstanceType(instanceType);
        instances.add(instanceIdSet);
        request.setInstances(instances);

        StartInstancesResult result = new StartInstancesResult();
        try {
            result = niftyServerClient.startInstances(request);
        } catch (NiftyServiceException e) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000610", instanceId);
        } catch (NiftyClientException e) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000610", instanceId);
        }

        // API実行結果チェック
        if (result.getStartingInstances() == null || result.getStartingInstances().size() == 0) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000610", instanceId);

        } else if (result.getStartingInstances().size() > 1) {
            // 複数のインスタンスが起動した場合
            AutoException exception = new AutoException("EPROCESS-000612", instanceId);
            exception.addDetailInfo("result=" + result.getStartingInstances());
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100513", instanceId));
        }

        return result.getStartingInstances().get(0);
    }

    public InstanceDto waitStartInstance(String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（StartInstances直後はstoppedのステータスを返すことがあるので、stopped以外になるまで待つ）
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            InstanceDto instance = describeInstance(instanceId);
            if (!"stopped".equals(instance.getState().getName())) {
                break;
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、インスタンスの開始に失敗したものとする
                throw new AutoException("EPROCESS-000610", instanceId);
            }
        }

        InstanceDto instance = waitInstance(instanceId);

        String state = instance.getState().getName();
        if (!"running".equals(state)) {
            // インスタンス開始失敗時
            AutoException exception = new AutoException("EPROCESS-000611", instanceId, state);
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100514", instanceId));
        }

        return instance;
    }

    public InstanceStateChange stopInstance(String instanceId) {
        // インスタンスの停止
        StopInstancesRequest request = new StopInstancesRequest();
        List<String> instanceIds = new ArrayList<String>();
        instanceIds.add(instanceId);
        request.setInstanceIds(instanceIds);
        request.setForce(false);

        StopInstancesResult result = new StopInstancesResult();
        try {
            result = niftyServerClient.stopInstances(request);
        } catch (NiftyServiceException e) {
            // インスタンス停止失敗時
            throw new AutoException("EPROCESS-000613", instanceId);
        } catch (NiftyClientException e) {
            // インスタンス停止失敗時
            throw new AutoException("EPROCESS-000613", instanceId);
        }

        // API実行結果チェック
        if (result.getStoppingInstances() == null || result.getStoppingInstances().size() == 0) {
            // インスタンス停止失敗時
            throw new AutoException("EPROCESS-000613", instanceId);

        } else if (result.getStoppingInstances().size() > 1) {
            // 複数のインスタンスが停止した場合
            AutoException exception = new AutoException("EPROCESS-000615", instanceId);
            exception.addDetailInfo("result=" + result.getStoppingInstances());
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100515", instanceId));
        }

        return result.getStoppingInstances().get(0);
    }

    public InstanceDto waitStopInstance(String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（StopInstances直後はrunningのステータスを返すことがあるので、running以外になるまで待つ）
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            InstanceDto instance = describeInstance(instanceId);
            if (!"running".equals(instance.getState().getName())) {
                break;
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、インスタンスの停止に失敗したものとする
                throw new AutoException("EPROCESS-000613", instanceId);
            }
        }

        InstanceDto instance = waitInstance(instanceId);

        String state = instance.getState().getName();
        if (!"stopped".equals(state)) {
            // インスタンス停止失敗時
            AutoException exception = new AutoException("EPROCESS-000614", instanceId, state);
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100516", instanceId));
        }

        return instance;
    }

    public InstanceStateChange terminateInstance(String instanceId) {
        // インスタンスの停止
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        List<String> instanceIds = new ArrayList<String>();
        instanceIds.add(instanceId);
        request.setInstanceIds(instanceIds);

        TerminateInstancesResult result = new TerminateInstancesResult();
        try {
            result = niftyServerClient.terminateInstances(request);
        } catch (NiftyServiceException e) {
            // インスタンス削除失敗時
            throw new AutoException("EPROCESS-000607", instanceId);
        } catch (NiftyClientException e) {
            // インスタンス削除失敗時
            throw new AutoException("EPROCESS-000607", instanceId);
        }

        // API実行結果チェック
        if (result.getTerminatingInstances() == null || result.getTerminatingInstances().size() == 0) {
            // インスタンス停止失敗時
            throw new AutoException("EPROCESS-000607", instanceId);

        } else if (result.getTerminatingInstances().size() > 1) {
            // 複数のインスタンスが停止した場合
            AutoException exception = new AutoException("EPROCESS-000608", instanceId);
            exception.addDetailInfo("result=" + result.getTerminatingInstances());
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100517", instanceId));
        }

        return result.getTerminatingInstances().get(0);
    }

    public void waitTerminateInstance(String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（StopInstances直後はrunningのステータスを返すことがあるので、running以外になるまで待つ）
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                describeInstance(instanceId);
            } catch (AutoException e) {
                // インスタンス情報を参照できなくなった場合に、削除できたものとする
                if ("EPROCESS-000601".equals(e.getCode())) {
                    break;
                }
                throw e;
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、インスタンスの削除に失敗したものとする
                throw new AutoException("EPROCESS-000608", instanceId);
            }
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100518", instanceId));
        }
    }

    public VolumeDto createVolume(Integer size, String instanceId) {
        // 最適なサイズに変換する
        int editSize;
        editSize = 0;
        for (int i = 1; i < 11; i++) {
            if (size == 0) {
                editSize = 0;
                break;
            }
            if (size <= i * 100) {
                editSize = i;
                break;
            }
        }
        // サイズに1GB～1000GB以外指定した場合、エラー
        if (editSize == 0) {
            // ディスク新規作成失敗時
            throw new AutoException("EPROCESS-000617");
        }

        // ディスク新規作成
        CreateVolumeRequest request = new CreateVolumeRequest();
        request.setSize(String.valueOf(editSize));
        request.setDiskType("3"); // Disk200A
        request.setInstanceId(instanceId);
        request.setAccountingType("2"); // 従量課金

        CreateVolumeResult result = new CreateVolumeResult();
        try {
            result = niftyDiskClient.createVolume(request);
        } catch (NiftyServiceException e) {
            // ディスク新規作成失敗時
            throw new AutoException("EPROCESS-000617");
        } catch (NiftyClientException e) {
            // ディスク新規作成失敗時
            throw new AutoException("EPROCESS-000617");
        }

        if (result.getVolume() == null) {
            // ディスク新規作成失敗時
            throw new AutoException("EPROCESS-000617");
        }

        VolumeDto volume = new VolumeDto(result.getVolume());
        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100521", volume.getVolumeId()));
        }

        return volume;
    }

    public VolumeDto waitCreateVolume(String volumeId) {
        // ボリュームの作成待ち
        VolumeDto volume = null;
        volume = waitVolume(volumeId);

        String status = volume.getStatus();
        if (!"in-use".equals(status)) {
            // ボリューム作成失敗時
            AutoException exception = new AutoException("EPROCESS-000621", volumeId, status);
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100522", volumeId));
        }

        return volume;
    }

    protected VolumeDto waitVolume(String volumeId) {
        // Volumeの処理待ち
        String[] stableStatus = new String[] { "available", "in-use" };
        String[] unstableStatus = new String[] { "creating" };
        VolumeDto volume = null;
        while (true) {
            volume = describeVolume(volumeId);
            String status;
            status = volume.getStatus();

            if (ArrayUtils.contains(stableStatus, status)) {
                break;
            }

            if (!ArrayUtils.contains(unstableStatus, status)) {
                // 予期しないステータス
                AutoException exception = new AutoException("EPROCESS-000620", volumeId, status);
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
                throw exception;
            }
        }

        return volume;
    }

    protected VolumeDto describeVolume(String volumeId) {
        // ディスク情報取得
        DescribeVolumesRequest request = new DescribeVolumesRequest();
        List<String> volumeIds = new ArrayList<String>();
        volumeIds.add(volumeId);
        request.setVolumeIds(volumeIds);

        DescribeVolumesResult result = niftyDiskClient.describeVolumes(request);

        // API実行結果チェック
        if (result.getVolumes() == null || result.getVolumes().size() == 0) {
            // インスタンスが存在しない場合
            throw new AutoException("EPROCESS-000618", volumeId);

        } else if (result.getVolumes().size() > 1) {
            // インスタンスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000619", volumeId);
            exception.addDetailInfo("result=" + result.getVolumes());
            throw exception;
        }

        return new VolumeDto(result.getVolumes().get(0));
    }

    public void attachVolume(String volumeId, String instanceId) {
        // ボリュームのアタッチ
        AttachVolumeRequest request = new AttachVolumeRequest();
        request.setVolumeId(volumeId);
        request.setInstanceId(instanceId);

        AttachVolumeResult result = new AttachVolumeResult();
        try {
            result = niftyDiskClient.attachVolume(request);
        } catch (NiftyServiceException e) {
            // アタッチ失敗時
            throw new AutoException("EPROCESS-000622", instanceId, volumeId);
        } catch (NiftyClientException e) {
            // アタッチ失敗時
            throw new AutoException("EPROCESS-000622", instanceId, volumeId);
        }

        if (result.getAttachment() == null) {
            // アタッチ失敗時
            throw new AutoException("EPROCESS-000622", instanceId, volumeId);
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100523", volumeId, instanceId));
        }
    }

    public VolumeDto waitAttachVolume(String volumeId, String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（アタッチ情報がすぐに更新されない問題に暫定的に対応）
        VolumeDto volume = null;
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            volume = waitVolume(volumeId);
            if ("in-use".equals(volume.getStatus())) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、アタッチにに失敗したものとする
                throw new AutoException("EPROCESS-000623", instanceId, volumeId, volume.getStatus());
            }
        }

        if (!"in-use".equals(volume.getStatus())) {
            // アタッチ失敗時
            AutoException exception = new AutoException("EPROCESS-000623", instanceId, volumeId, volume.getStatus());
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100524", volumeId, instanceId));
        }

        return volume;
    }

    public void detachVolume(String volumeId, String instanceId) {
        // ボリュームのデタッチ
        DetachVolumeRequest request = new DetachVolumeRequest();
        request.setVolumeId(volumeId);
        request.setInstanceId(instanceId);
        request.setAgreement(true); // 解除実施

        DetachVolumeResult result = new DetachVolumeResult();
        try {
            result = niftyDiskClient.detachVolume(request);
        } catch (NiftyServiceException e) {
            // デタッチ失敗時
            throw new AutoException("EPROCESS-000624", instanceId, volumeId);
        } catch (NiftyClientException e) {
            // デタッチ失敗時
            throw new AutoException("EPROCESS-000624", instanceId, volumeId);
        }

        if (result.getAttachment() == null) {
            // デタッチ失敗時
            throw new AutoException("EPROCESS-000624", instanceId, volumeId);
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100525", volumeId, instanceId));
        }
    }

    public VolumeDto waitDetachVolume(String volumeId, String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（アタッチ情報がすぐに更新されない問題に暫定的に対応）
        VolumeDto volume = null;
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            volume = waitVolume(volumeId);
            if ("available".equals(volume.getStatus())) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、デタッチにに失敗したものとする
                throw new AutoException("EPROCESS-000625", instanceId, volumeId, volume.getStatus());
            }
        }

        if (!"available".equals(volume.getStatus())) {
            // デタッチ失敗時
            AutoException exception = new AutoException("EPROCESS-000625", instanceId, volumeId, volume.getStatus());
            exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100526", volumeId, instanceId));
        }

        return volume;
    }

    public void deleteVolume(String volumeId) {
        // 排他制御(apiを同時に実行するとエラーになる対策)
        synchronized(lock) {
            // ディスク削除
            DeleteVolumeRequest request = new DeleteVolumeRequest();
            request.setVolumeId(volumeId);

            DeleteVolumeResult result = new DeleteVolumeResult();
            try {
                result = niftyDiskClient.deleteVolume(request);
            } catch (NiftyServiceException e) {
                // ディスク削除失敗時
                throw new AutoException("EPROCESS-000626", volumeId);
            } catch (NiftyClientException e) {
                // ディスク削除失敗時
                throw new AutoException("EPROCESS-000626", volumeId);
            }

            if (result.getReturn() == null) {
                // ディスク削除失敗時
                throw new AutoException("EPROCESS-000626", volumeId);
            }

            // ログ出力
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100527", volumeId));
            }
        }
    }

    public void setDescribeInterval(int describeInterval) {
        this.describeInterval = describeInterval;
    }

}
