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

import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class NiftyProcess extends ServiceSupport {

    protected NiftyProcessClientFactory niftyProcessClientFactory;

    protected NiftyInstanceProcess niftyInstanceProcess;

    protected NiftyDnsProcess niftyDnsProcess;

    /**
     * TODO: メソッドコメントを記述
     *
     * @param instanceNo
     */
    public void start(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100501", instanceNo, instance.getInstanceName()));
        }

        // NiftyProcessClientの作成
        NiftyProcessClient niftyProcessClient = niftyProcessClientFactory.createNiftyProcessClient(farm.getUserNo(),
                instance.getPlatformNo());

        // インスタンス作成処理
        niftyInstanceProcess.createInstance(niftyProcessClient, instanceNo);

        // インスタンス起動処理
        niftyInstanceProcess.startInstance(niftyProcessClient, instanceNo);

        // DNSに関する処理
        niftyDnsProcess.startDns(instanceNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100502", instanceNo, instance.getInstanceName()));
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param instanceNo
     */
    public void stop(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100503", instanceNo, instance.getInstanceName()));
        }

        // NiftyProcessClientの作成
        NiftyProcessClient niftyProcessClient = niftyProcessClientFactory.createNiftyProcessClient(farm.getUserNo(),
                instance.getPlatformNo());

        try {
            // DNSに関する処理
            niftyDnsProcess.stopDns(instanceNo);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        try {
            // インスタンス停止処理
            niftyInstanceProcess.stopInstance(niftyProcessClient, instanceNo);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100504", instanceNo, instance.getInstanceName()));
        }
    }

    /**
     * niftyProcessClientFactoryを設定します。
     *
     * @param niftyProcessClientFactory niftyProcessClientFactory
     */
    public void setNiftyProcessClientFactory(NiftyProcessClientFactory niftyProcessClientFactory) {
        this.niftyProcessClientFactory = niftyProcessClientFactory;
    }

    /**
     * niftyInstanceProcessを設定します。
     *
     * @param niftyInstanceProcess niftyInstanceProcess
     */
    public void setNiftyInstanceProcess(NiftyInstanceProcess niftyInstanceProcess) {
        this.niftyInstanceProcess = niftyInstanceProcess;
    }

    /**
     * niftyDnsProcessを設定します。
     *
     * @param niftyDnsProcess niftyDnsProcess
     */
    public void setNiftyDnsProcess(NiftyDnsProcess niftyDnsProcess) {
        this.niftyDnsProcess = niftyDnsProcess;
    }

}
