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

import java.util.List;

import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.AwsVolume;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.InstanceStateName;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsProcess extends ServiceSupport {

    protected AwsProcessClientFactory awsProcessClientFactory;

    protected AwsCommonProcess awsCommonProcess;

    protected AwsInstanceProcess awsInstanceProcess;

    protected AwsVolumeProcess awsVolumeProcess;

    protected AwsAddressProcess awsAddressProcess;

    protected AwsDnsProcess awsDnsProcess;

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void start(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100101", instanceNo, instance.getInstanceName()));
        }

        // AwsProcessClientの作成
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(farm.getUserNo(),
                instance.getPlatformNo());

        // インスタンスに関する処理
        int retryCount = 1; // インスタンス起動処理の最大リトライ回数
        for (int i = 0; i <= retryCount; i++) {
            try {
                awsInstanceProcess.startInstance(awsProcessClient, instanceNo);
                break;

            } catch (AutoException e) {
                if (i < retryCount) {
                    // RunInstancesが正常に動いたが、予期せぬ異常によりインスタンスが起動しなかった場合、リトライする
                    // TODO: エラーコード、インスタンスIDの取り方を改善すべきか（例外スロー元の実装に依存しているため）
                    if ("EPROCESS-000106".equals(e.getCode())) {
                        String instanceId = (String) e.getAdditions()[0];

                        try {
                            // インスタンス情報の参照
                            com.amazonaws.services.ec2.model.Instance instance2 = awsCommonProcess.describeInstance(
                                    awsProcessClient, instanceId);

                            if (InstanceStateName.Terminated.toString().equals(instance2.getState().getName())
                                    && "Server.InternalError".equals(instance2.getStateTransitionReason())) {
                                // 警告ログ出力した後にリトライする
                                log.warn(e.getMessage());

                                // リトライ用にインスタンスIDを削除
                                AwsInstance awsInstance = awsInstanceDao.read(instanceNo);
                                awsInstance.setInstanceId(null);
                                awsInstanceDao.update(awsInstance);

                                continue;
                            }
                        } catch (Exception ignore) {
                        }
                    }
                }

                throw e;
            }
        }

        // ボリュームに関する処理
        List<AwsVolume> awsVolumes = awsVolumeDao.readByInstanceNo(instanceNo);
        for (AwsVolume awsVolume : awsVolumes) {
            if (awsVolume.getComponentNo() != null) {
                // コンポーネント番号がある場合はスキップ
                continue;
            }
            awsVolumeProcess.startVolume(awsProcessClient, instanceNo, awsVolume.getVolumeNo());
        }

        // アドレスに関する処理
        awsAddressProcess.startAddress(awsProcessClient, instanceNo);

        // DNSに関する処理
        awsDnsProcess.startDns(awsProcessClient, instanceNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100102", instanceNo, instance.getInstanceName()));
        }
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void stop(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100103", instanceNo, instance.getInstanceName()));
        }

        // AwsProcessClientの作成
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(farm.getUserNo(),
                instance.getPlatformNo());

        try {
            // DNSに関する処理
            awsDnsProcess.stopDns(awsProcessClient, instanceNo);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        try {
            // アドレスに関する処理
            awsAddressProcess.stopAddress(awsProcessClient, instanceNo);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        try {
            // インスタンスに関する処理
            awsInstanceProcess.stopInstance(awsProcessClient, instanceNo);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        try {
            // ボリュームに関する処理
            List<AwsVolume> awsVolumes = awsVolumeDao.readByInstanceNo(instanceNo);
            ImageAws imageAws = imageAwsDao.read(instance.getImageNo());
            for (AwsVolume awsVolume : awsVolumes) {
                if (StringUtils.isEmpty(awsVolume.getVolumeId()) || StringUtils.isEmpty(awsVolume.getInstanceId())) {
                    // まだ作られていない場合、またはインスタンスにアタッチされていない場合は何もしない
                    continue;
                }

                if (awsVolume.getComponentNo() != null) {
                    // コンポーネントに関するボリュームの場合、この分岐は何らかの問題でボリュームがデタッチされていない
                    if (BooleanUtils.isTrue(imageAws.getEbsImage())) {
                        // EBSイメージの場合、ここで改めてデタッチする
                        awsVolumeProcess.stopVolume(awsProcessClient, instanceNo, awsVolume.getVolumeNo());
                    } else {
                        // インスタンスストアイメージの場合、インスタンス終了に伴い自動的にデタッチされているため、ステータスをクリアする
                        awsVolume.setStatus(null);
                        awsVolume.setInstanceId(null);
                        awsVolumeDao.update(awsVolume);
                    }
                }
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100104", instanceNo, instance.getInstanceName()));
        }
    }

    public void setAwsProcessClientFactory(AwsProcessClientFactory awsProcessClientFactory) {
        this.awsProcessClientFactory = awsProcessClientFactory;
    }

    public void setAwsCommonProcess(AwsCommonProcess awsCommonProcess) {
        this.awsCommonProcess = awsCommonProcess;
    }

    public void setAwsInstanceProcess(AwsInstanceProcess awsInstanceProcess) {
        this.awsInstanceProcess = awsInstanceProcess;
    }

    public void setAwsVolumeProcess(AwsVolumeProcess awsVolumeProcess) {
        this.awsVolumeProcess = awsVolumeProcess;
    }

    public void setAwsAddressProcess(AwsAddressProcess awsAddressProcess) {
        this.awsAddressProcess = awsAddressProcess;
    }

    public void setAwsDnsProcess(AwsDnsProcess awsDnsProcess) {
        this.awsDnsProcess = awsDnsProcess;
    }

}
