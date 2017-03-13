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
package jp.primecloud.auto.process;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.common.status.InstanceCoodinateStatus;
import jp.primecloud.auto.common.status.InstanceStatus;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.process.hook.ProcessHook;
import jp.primecloud.auto.process.puppet.PuppetNodesProcess;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class InstancesProcess extends ServiceSupport {

    protected PuppetNodesProcess puppetNodesProcess;

    protected ProcessLogger processLogger;

    protected ProcessHook processHook;

    public void start(Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        if (BooleanUtils.isTrue(farm.getComponentProcessing())) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                String message = MessageUtils.format("Component is being configured.(farmNo={0})", farmNo);
                log.debug(message);
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100021", farmNo));
        }

        // フック処理の実行
        processHook.execute("pre-start-instances", farm.getUserNo(), farm.getFarmNo());

        // ステータス変更
        farm.setComponentProcessing(true);
        farmDao.update(farm);

        //イベントログ出力
        processLogger.debug(null, null, "InstanceCoordinate", null);

        try {
            // 協調処理対象のインスタンスを取得
            List<Long> startInstanceNos = new ArrayList<Long>();
            List<Instance> allInstances = instanceDao.readByFarmNo(farmNo);
            for (Instance instance : allInstances) {
                // 起動しているインスタンスのうち、有効なものと、無効でも既に協調されているものを開始対象とする
                if (InstanceStatus.fromStatus(instance.getStatus()) == InstanceStatus.RUNNING) {
                    if (BooleanUtils.isTrue(instance.getEnabled())
                            || InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus()) == InstanceCoodinateStatus.COODINATED) {
                        startInstanceNos.add(instance.getInstanceNo());
                    }
                }
            }

            InstancesProcessContext context = new InstancesProcessContext();
            context.setFarmNo(farmNo);
            context.setStartInstanceNos(startInstanceNos);

            // 協調処理を実行
            puppetNodesProcess.configureNodes(context);

        } finally {
            // ステータス変更
            farm = farmDao.read(farmNo);
            farm.setComponentProcessing(false);
            farmDao.update(farm);
        }

        //イベントログ出力
        processLogger.debug(null, null, "InstanceCoordinateFinish", null);

        // フック処理の実行
        processHook.execute("post-start-instances", farm.getUserNo(), farm.getFarmNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100022", farmNo));
        }
    }

    public void stop(Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        if (BooleanUtils.isTrue(farm.getComponentProcessing())) {
            // 処理中のため実行できない場合
            if (log.isDebugEnabled()) {
                String message = MessageUtils.format("Component is being configured.(farmNo={0})", farmNo);
                log.debug(message);
            }
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100021", farmNo));
        }

        // フック処理の実行
        processHook.execute("pre-stop-instances", farm.getUserNo(), farm.getFarmNo());

        // ステータス変更
        farm.setComponentProcessing(true);
        farmDao.update(farm);

        //イベントログ出力
        processLogger.debug(null, null, "InstanceCoordinate", null);

        try {
            // 協調処理対象のインスタンスを取得
            List<Long> startInstanceNos = new ArrayList<Long>();
            List<Long> stopInstanceNos = new ArrayList<Long>();
            List<Instance> allInstances = instanceDao.readByFarmNo(farmNo);
            for (Instance instance : allInstances) {
                // 起動しているインスタンスのうち、有効なものを開始対象、無効で既に協調されているものを終了対象とする
                if (InstanceStatus.fromStatus(instance.getStatus()) == InstanceStatus.RUNNING) {
                    if (BooleanUtils.isTrue(instance.getEnabled())) {
                        startInstanceNos.add(instance.getInstanceNo());
                    } else if (InstanceCoodinateStatus.fromStatus(instance.getCoodinateStatus()) == InstanceCoodinateStatus.COODINATED) {
                        stopInstanceNos.add(instance.getInstanceNo());
                    }
                }
            }

            InstancesProcessContext context = new InstancesProcessContext();
            context.setFarmNo(farmNo);
            context.setStartInstanceNos(startInstanceNos);
            context.setStopInstanceNos(stopInstanceNos);

            // 協調処理を実行
            puppetNodesProcess.configureNodes(context);

        } finally {
            // ステータス変更
            farm = farmDao.read(farmNo);
            farm.setComponentProcessing(false);
            farmDao.update(farm);
        }

        //イベントログ出力
        processLogger.debug(null, null, "InstanceCoordinateFinish", null);

        // フック処理の実行
        processHook.execute("post-stop-instances", farm.getUserNo(), farm.getFarmNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100022", farmNo));
        }
    }

    /**
     * puppetNodesProcessを設定します。
     *
     * @param puppetNodesProcess puppetNodesProcess
     */
    public void setPuppetNodesProcess(PuppetNodesProcess puppetNodesProcess) {
        this.puppetNodesProcess = puppetNodesProcess;
    }

    /**
     * processLoggerを設定します。
     *
     * @param processLogger processLogger
     */
    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
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
