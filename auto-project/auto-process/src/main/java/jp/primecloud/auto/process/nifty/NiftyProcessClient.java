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
package jp.primecloud.auto.process.nifty;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.nifty.soap.jaxws.DescribeInstancesInfoType;
import jp.primecloud.auto.nifty.soap.jaxws.DescribeInstancesItemType;
import jp.primecloud.auto.nifty.soap.jaxws.DescribeInstancesResponseType;
import jp.primecloud.auto.nifty.soap.jaxws.DescribeInstancesType;
import jp.primecloud.auto.nifty.soap.jaxws.InstanceIdSetType;
import jp.primecloud.auto.nifty.soap.jaxws.InstanceIdType;
import jp.primecloud.auto.nifty.soap.jaxws.InstanceStateChangeType;
import jp.primecloud.auto.nifty.soap.jaxws.NiftyCloudPortType;
import jp.primecloud.auto.nifty.soap.jaxws.ReservationInfoType;
import jp.primecloud.auto.nifty.soap.jaxws.RunInstancesResponseType;
import jp.primecloud.auto.nifty.soap.jaxws.RunInstancesType;
import jp.primecloud.auto.nifty.soap.jaxws.RunningInstancesItemType;
import jp.primecloud.auto.nifty.soap.jaxws.StartInstancesResponseType;
import jp.primecloud.auto.nifty.soap.jaxws.StartInstancesType;
import jp.primecloud.auto.nifty.soap.jaxws.StopInstancesResponseType;
import jp.primecloud.auto.nifty.soap.jaxws.StopInstancesType;
import jp.primecloud.auto.nifty.soap.jaxws.TerminateInstancesResponseType;
import jp.primecloud.auto.nifty.soap.jaxws.TerminateInstancesType;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyProcessClient {

    protected Log log = LogFactory.getLog(getClass());

    protected NiftyCloudPortType niftyCloud;

    protected Long platformNo;

    protected Integer describeInterval;

    public NiftyProcessClient(NiftyCloudPortType niftyCloud, Long platformNo) {
        this.niftyCloud = niftyCloud;
        this.platformNo = platformNo;
    }

    public NiftyProcessClient(NiftyCloudPortType niftyCloud, Long platformNo, Integer describeInterval) {
        this(niftyCloud, platformNo);
        this.describeInterval = describeInterval;
    }

    public NiftyCloudPortType getNiftyCloud() {
        return niftyCloud;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public List<RunningInstancesItemType> describeAllInstances() {
        // 全インスタンスの参照
        DescribeInstancesType request = new DescribeInstancesType();

        try {
            Thread.sleep(1000 * describeInterval);
        } catch (InterruptedException ignore) {
        }

        DescribeInstancesResponseType response = niftyCloud.describeInstances(request);
        List<ReservationInfoType> reservations = response.getReservationSet().getItem();

        List<RunningInstancesItemType> instances = new ArrayList<RunningInstancesItemType>();
        for (ReservationInfoType reservation : reservations) {
            instances.addAll(reservation.getInstancesSet().getItem());
        }

        return instances;
    }

    public RunningInstancesItemType describeInstance(String instanceId) {
        // 単一インスタンスの参照
        DescribeInstancesType request = new DescribeInstancesType();
        DescribeInstancesInfoType instancesSet = new DescribeInstancesInfoType();
        instancesSet.getItem().add(new DescribeInstancesItemType().withInstanceId(instanceId));
        request.setInstancesSet(instancesSet);

        try {
            Thread.sleep(1000 * describeInterval);
        } catch (InterruptedException ignore) {
        }

        DescribeInstancesResponseType response = niftyCloud.describeInstances(request);
        List<ReservationInfoType> reservations = response.getReservationSet().getItem();

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

        List<RunningInstancesItemType> instances = reservations.get(0).getInstancesSet().getItem();

        if (instances.size() == 0) {
            // インスタンスが存在しない場合
            throw new AutoException("EPROCESS-000601", instanceId);

        } else if (instances.size() > 1) {
            // インスタンスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000603", instanceId);
            exception.addDetailInfo("result=" + instances);
            throw exception;
        }

        return instances.get(0);
    }

    public RunningInstancesItemType waitInstance(String instanceId) {
        // インスタンスの処理待ち
        String[] stableStatus = new String[] { "running", "stopped" };
        // TODO: ニフティクラウドAPIの経過観察（インスタンスのステータス warning はAPIリファレンスに記載されていない）
        String[] unstableStatus = new String[] { "pending", "warning" };// 

        RunningInstancesItemType instance;
        while (true) {
            instance = describeInstance(instanceId);
            String status = instance.getInstanceState().getName();

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

    public RunningInstancesItemType runInstance(String imageId, String keyName, String instanceType, String password) {
        // インスタンスの起動
        RunInstancesType request = new RunInstancesType();
        request.setImageId(imageId);
        request.setMinCount(1);
        request.setMaxCount(1);
        request.setKeyName(keyName);
        request.setInstanceType(instanceType);
        request.setAccountingType("2"); // 従量課金
        request.setPassword(password);
        request.setDisableApiTermination(false); // APIでインスタンスを削除できるようにする

        RunInstancesResponseType response = niftyCloud.runInstances(request);

        if (response.getInstancesSet() == null || response.getInstancesSet().getItem().size() != 1) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000605");
        }

        RunningInstancesItemType instance = response.getInstancesSet().getItem().get(0);
        String instanceId = instance.getInstanceId();

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100511", instanceId));
        }

        return instance;
    }

    public RunningInstancesItemType waitRunInstance(String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（RunInstances直後はインスタンスを参照できないことがあるので、インスタンス情報を取得できるまでは全インスタンス情報を取得する）
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            boolean exist = false;
            List<RunningInstancesItemType> instances = describeAllInstances();
            for (RunningInstancesItemType instance : instances) {
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

        RunningInstancesItemType instance = waitInstance(instanceId);

        String state = instance.getInstanceState().getName();
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

    public InstanceStateChangeType startInstance(String instanceId, String instanceType) {
        // インスタンスの起動
        StartInstancesType request = new StartInstancesType();
        InstanceIdSetType instancesSet = new InstanceIdSetType();
        request.setInstancesSet(instancesSet);
        InstanceIdType instance = new InstanceIdType();
        instance.setInstanceId(instanceId);
        instance.setInstanceType(instanceType);
        instancesSet.getItem().add(instance);

        StartInstancesResponseType response = niftyCloud.startInstances(request);

        // API実行結果チェック
        if (response.getInstancesSet() == null || response.getInstancesSet().getItem().size() == 0) {
            // インスタンス起動失敗時
            throw new AutoException("EPROCESS-000610", instanceId);

        } else if (response.getInstancesSet().getItem().size() > 1) {
            // 複数のインスタンスが起動した場合
            AutoException exception = new AutoException("EPROCESS-000612", instanceId);
            exception.addDetailInfo("result=" + response.getInstancesSet().getItem());
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100513", instanceId));
        }

        return response.getInstancesSet().getItem().get(0);
    }

    public RunningInstancesItemType waitStartInstance(String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（StartInstances直後はstoppedのステータスを返すことがあるので、stopped以外になるまで待つ）
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            RunningInstancesItemType instance = describeInstance(instanceId);
            if (!"stopped".equals(instance.getInstanceState().getName())) {
                break;
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、インスタンスの開始に失敗したものとする
                throw new AutoException("EPROCESS-000610", instanceId);
            }
        }

        RunningInstancesItemType instance = waitInstance(instanceId);

        String state = instance.getInstanceState().getName();
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

    public InstanceStateChangeType stopInstance(String instanceId) {
        // インスタンスの停止
        StopInstancesType request = new StopInstancesType();
        request.setForce(false);
        InstanceIdSetType instancesSet = new InstanceIdSetType();
        request.setInstancesSet(instancesSet);
        InstanceIdType instance = new InstanceIdType();
        instance.setInstanceId(instanceId);
        instancesSet.getItem().add(instance);

        StopInstancesResponseType response = niftyCloud.stopInstances(request);

        // API実行結果チェック
        if (response.getInstancesSet() == null || response.getInstancesSet().getItem().size() == 0) {
            // インスタンス停止失敗時
            throw new AutoException("EPROCESS-000613", instanceId);

        } else if (response.getInstancesSet().getItem().size() > 1) {
            // 複数のインスタンスが停止した場合
            AutoException exception = new AutoException("EPROCESS-000615", instanceId);
            exception.addDetailInfo("result=" + response.getInstancesSet().getItem());
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100515", instanceId));
        }

        return response.getInstancesSet().getItem().get(0);
    }

    public RunningInstancesItemType waitStopInstance(String instanceId) {
        // TODO: ニフティクラウドAPIの経過観察（StopInstances直後はrunningのステータスを返すことがあるので、running以外になるまで待つ）
        long timeout = 600 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            RunningInstancesItemType instance = describeInstance(instanceId);
            if (!"running".equals(instance.getInstanceState().getName())) {
                break;
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時、インスタンスの停止に失敗したものとする
                throw new AutoException("EPROCESS-000613", instanceId);
            }
        }

        RunningInstancesItemType instance = waitInstance(instanceId);

        String state = instance.getInstanceState().getName();
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

    public InstanceStateChangeType terminateInstance(String instanceId) {
        // インスタンスの停止
        TerminateInstancesType request = new TerminateInstancesType();
        InstanceIdSetType instancesSet = new InstanceIdSetType();
        request.setInstancesSet(instancesSet);
        InstanceIdType instance = new InstanceIdType();
        instance.setInstanceId(instanceId);
        instancesSet.getItem().add(instance);

        TerminateInstancesResponseType response = niftyCloud.terminateInstances(request);

        // API実行結果チェック
        if (response.getInstancesSet() == null || response.getInstancesSet().getItem().size() == 0) {
            // インスタンス停止失敗時
            throw new AutoException("EPROCESS-000607", instanceId);

        } else if (response.getInstancesSet().getItem().size() > 1) {
            // 複数のインスタンスが停止した場合
            AutoException exception = new AutoException("EPROCESS-000608", instanceId);
            exception.addDetailInfo("result=" + response.getInstancesSet().getItem());
            throw exception;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100517", instanceId));
        }

        return response.getInstancesSet().getItem().get(0);
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

}
