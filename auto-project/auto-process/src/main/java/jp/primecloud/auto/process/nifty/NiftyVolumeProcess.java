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

import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.NiftyInstance;
import jp.primecloud.auto.entity.crud.NiftyVolume;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;
import com.nifty.cloud.sdk.disk.model.Volume;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyVolumeProcess extends ServiceSupport {

    protected EventLogger eventLogger;

    protected ProcessLogger processLogger;

    private final static Object lock = new Object();

    /**
     * TODO: メソッドコメントを記述
     *
     * @param niftyProcessClient
     * @param instanceNo
     * @param volumeNo
     */
    public void startVoiume(NiftyProcessClient niftyProcessClient, Long instanceNo, Long volumeNo) {
        NiftyVolume niftyVolume = niftyVolumeDao.read(volumeNo);

        // インスタンスIDがある場合はスキップ
        if (StringUtils.isNotEmpty(niftyVolume.getInstanceId())) {
            return;
        }

        NiftyInstance niftyInstance = niftyInstanceDao.read(instanceNo);

        //イベントログ出力
        Component component = componentDao.read(niftyVolume.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(niftyProcessClient.getPlatformNo());

        Volume volume;
        if (StringUtils.isEmpty(niftyVolume.getVolumeId())) {
            try {
                // 排他制御(apiを同時に実行するとエラーになる対策)
                synchronized(lock) {
                    //イベントログ出力
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "NiftyDiskCreate",
                            new Object[] { platform.getPlatformName() });

                    // ボリュームIDがない場合は新規作成、アタッチを行う
                    volume = niftyProcessClient.createVolume(niftyVolume.getSize(), niftyInstance.getInstanceId());
                    // データベース更新
                    niftyVolume.setVolumeId(volume.getVolumeId());
                    niftyVolume.setStatus(volume.getStatus());
                    niftyVolumeDao.update(niftyVolume);
                    // ボリュームの作成待ち
                    volume = niftyProcessClient.waitCreateVolume(volume.getVolumeId());

                    //イベントログ出力
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "NiftyDiskCreateFinish",
                            new Object[] { platform.getPlatformName(), volume.getVolumeId(), niftyVolume.getSize() });

                    // データベース更新
                    niftyVolume.setSize(Integer.valueOf(volume.getSize()));
                    niftyVolume.setStatus(volume.getStatus());
                    niftyVolume.setInstanceId(niftyInstance.getInstanceId());
                    niftyVolumeDao.update(niftyVolume);
                }
            } catch (AutoException e) {
                // データベース更新
                niftyVolume.setVolumeId(null);
                niftyVolume.setStatus(null);
                niftyVolume.setInstanceId(null);
                niftyVolumeDao.update(niftyVolume);
                throw e;
            }
        } else {
            try {
                // 排他制御(apiを同時に実行するとエラーになる対策)
                synchronized(lock) {
                    //イベントログ出力
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "NiftyDiskAttach",
                            new Object[] { instance.getInstanceName(), niftyVolume.getVolumeId() });

                    // ボリュームのアタッチ
                    niftyProcessClient.attachVolume(niftyVolume.getVolumeId(), niftyInstance.getInstanceId());
                    // データベース更新
                    niftyVolume.setInstanceId(niftyInstance.getInstanceId());
                    niftyVolumeDao.update(niftyVolume);

                    // ボリュームのアタッチ待ち
                    volume = niftyProcessClient.waitAttachVolume(niftyVolume.getVolumeId(), niftyInstance.getInstanceId());

                    //イベントログ出力
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "NiftyDiskAttachFinish",
                            new Object[] { instance.getInstanceName(), niftyVolume.getVolumeId() });

                    // データベース更新
                    niftyVolume.setStatus(volume.getStatus());
                    niftyVolumeDao.update(niftyVolume);
                }
            } catch (AutoException e) {
                // データベース更新
                niftyVolume.setStatus("error");
                niftyVolume.setInstanceId(null);
                niftyVolumeDao.update(niftyVolume);
                throw e;
            }
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param niftyProcessClient
     * @param instanceNo
     * @param volumeNo
     */
    public void stopVolume(NiftyProcessClient niftyProcessClient, Long instanceNo, Long volumeNo) {
        NiftyVolume niftyVolume = niftyVolumeDao.read(volumeNo);

        // ボリュームIDがない場合はスキップ
        if (StringUtils.isEmpty(niftyVolume.getVolumeId())) {
            return;
        }

        // インスタンスIDがない場合はスキップ
        if (StringUtils.isEmpty(niftyVolume.getInstanceId())) {
            return;
        }

        try {
            // 排他制御(apiを同時に実行するとエラーになる対策)
            synchronized(lock) {
                //イベントログ出力
                Component component = componentDao.read(niftyVolume.getComponentNo());
                Instance instance = instanceDao.read(instanceNo);
                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "NiftyDiskDetach", new Object[] {
                        instance.getInstanceName(), niftyVolume.getVolumeId() });

                // ディスクをデタッチ
                niftyProcessClient.detachVolume(niftyVolume.getVolumeId(), niftyVolume.getInstanceId());

                Volume volume;
                // ボリュームのデタッチ待ち
                volume = niftyProcessClient.waitDetachVolume(niftyVolume.getVolumeId(), niftyVolume.getInstanceId());

                //イベントログ出力
                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "NiftyDiskDetachFinish",
                        new Object[] { instance.getInstanceName(), niftyVolume.getVolumeId() });

                // データベース更新
                niftyVolume.setStatus(volume.getStatus());
                niftyVolume.setInstanceId(null);
                niftyVolumeDao.update(niftyVolume);
            }
        } catch (AutoException e) {
            // データベース更新
            niftyVolume.setStatus("error");
            niftyVolumeDao.update(niftyVolume);
            throw e;
        }
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
     * processLoggerを設定します。
     *
     * @param processLogger processLogger
     */
    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }
}
