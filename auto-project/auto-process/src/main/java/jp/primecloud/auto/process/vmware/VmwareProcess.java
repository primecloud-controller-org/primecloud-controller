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
package jp.primecloud.auto.process.vmware;

import java.util.List;

import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.entity.crud.VmwareNetwork;
import jp.primecloud.auto.puppet.PuppetClient;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.VirtualMachineToolsRunningStatus;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareProcess extends ServiceSupport {

    protected VmwareProcessClientFactory vmwareProcessClientFactory;

    protected VmwareMachineProcess vmwareMachineProcess;

    protected VmwareNetworkProcess vmwareNetworkProcess;

    protected VmwareDiskProcess vmwareDiskProcess;

    protected VmwareInitProcess vmwareInitProcess;

    protected VmwareDnsProcess vmwareDnsProcess;

    protected VmwareCustomizeProcess vmwareCustomizeProcess;

    protected PuppetClient puppetClient;

    /**
     * TODO: メソッドコメントを記述
     *
     * @param instanceNo
     */
    public void start(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100401", instanceNo, instance.getInstanceName()));
        }

        // VmwareProcessClientの作成
        VmwareProcessClient vmwareProcessClient = vmwareProcessClientFactory.createVmwareProcessClient(instance.getPlatformNo());

        try {
            // ネットワークを作成
            List<VmwareNetwork> vmwareNetworks = vmwareNetworkDao.readByFarmNo(instance.getFarmNo());
            for (VmwareNetwork vmwareNetwork : vmwareNetworks) {
                if (vmwareNetwork.getPlatformNo().equals(instance.getPlatformNo())) {
                    vmwareNetworkProcess.addNetwork(vmwareProcessClient, vmwareNetwork.getNetworkNo(), instanceNo);
                }
            }

            // テンプレートから仮想マシンをクローンで作成
            boolean clone = vmwareMachineProcess.cloneVM(vmwareProcessClient, instanceNo);

            // 仮想マシンの設定変更
            vmwareMachineProcess.changeResource(vmwareProcessClient, instanceNo);

            // カスタマイズ
            if (clone) {
                vmwareCustomizeProcess.customize(vmwareProcessClient, instanceNo);
            }

            // 仮想マシンのイーサネットカード設定を変更
            vmwareNetworkProcess.changeEthernetCard(vmwareProcessClient, instanceNo);

            // ディスクをアタッチ
            List<VmwareDisk> vmwareDisks = vmwareDiskDao.readByInstanceNo(instanceNo);
            for (VmwareDisk vmwareDisk : vmwareDisks) {
                if (vmwareDisk.getComponentNo() != null) {
                    // コンポーネント番号がある場合はスキップ
                    continue;
                }
                vmwareDiskProcess.attachDisk(vmwareProcessClient, instanceNo, vmwareDisk.getDiskNo());
            }

            // Puppet認証情報の削除
            clearPuppetCa(instanceNo);

            // 仮想マシンを起動
            vmwareMachineProcess.powerOnVM(vmwareProcessClient, instanceNo);

            // インスタンス情報の設定
            vmwareInitProcess.initialize(vmwareProcessClient, instanceNo);

            // ネットワークの設定
            vmwareInitProcess.initializeNetwork(vmwareProcessClient, instanceNo, vmwareNetworkProcess);

            // カスタム値の設定
            vmwareMachineProcess.updateCustomValue(vmwareProcessClient, instanceNo);

            // ゲストの起動待ち
            vmwareMachineProcess.waitForRunning(vmwareProcessClient, instanceNo);

            // ゲストの情報取得
            vmwareMachineProcess.getGuestInfo(vmwareProcessClient, instanceNo);

            // DNSに関する処理
            vmwareDnsProcess.startDns(instanceNo);

        } finally {
            vmwareProcessClient.getVmwareClient().logout();
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100402", instanceNo, instance.getInstanceName()));
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param instanceNo
     */
    public void stop(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100403", instanceNo, instance.getInstanceName()));
        }

        try {
            // DNSに関する処理
            vmwareDnsProcess.stopDns(instanceNo);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        // VmwareProcessClientの作成
        try {
            VmwareProcessClient vmwareProcessClient = vmwareProcessClientFactory.createVmwareProcessClient(instance.getPlatformNo());

            try {
                VmwareInstance vmInstance = vmwareInstanceDao.read(instanceNo);
                VirtualMachine machine = vmwareProcessClient.getVmwareClient().search(VirtualMachine.class,
                        vmInstance.getMachineName());

                try {
                    GuestInfo guestInfo = machine.getGuest();
                    if (VirtualMachineToolsRunningStatus.guestToolsRunning.toString()
                            .equals(guestInfo.getToolsRunningStatus())) {
                        // vmware-toolsが起動している場合、ゲストOSをシャットダウン
                        vmwareMachineProcess.shutdownGuest(vmwareProcessClient, instanceNo);
                    } else {
                        // vmware-toolsが起動していない場合、仮想マシンをパワーオフ
                        vmwareMachineProcess.powerOffVM(vmwareProcessClient, instanceNo);
                    }
                } catch (RuntimeException e) {
                    log.warn(e.getMessage());
                }

                // ディスクをデタッチ
                List<VmwareDisk> vmwareDisks = vmwareDiskDao.readByInstanceNo(instanceNo);
                for (VmwareDisk vmwareDisk : vmwareDisks) {
                    try {
                        vmwareDiskProcess.detachDisk(vmwareProcessClient, instanceNo, vmwareDisk.getDiskNo());
                    } catch (RuntimeException e) {
                        log.warn(e.getMessage());
                    }
                }

            } finally {
                vmwareProcessClient.getVmwareClient().logout();
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100404", instanceNo, instance.getInstanceName()));
        }
    }

    protected void clearPuppetCa(Long instanceNo) {
        List<String> clients = puppetClient.listClients();

        Instance instance = instanceDao.read(instanceNo);
        String fqdn = instance.getFqdn();

        if (clients.contains(fqdn)) {
            puppetClient.clearCa(fqdn);
        }
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
     * vmwareMachineProcessを設定します。
     *
     * @param vmwareMachineProcess vmwareMachineProcess
     */
    public void setVmwareMachineProcess(VmwareMachineProcess vmwareMachineProcess) {
        this.vmwareMachineProcess = vmwareMachineProcess;
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
     * vmwareDiskProcessを設定します。
     *
     * @param vmwareDiskProcess vmwareDiskProcess
     */
    public void setVmwareDiskProcess(VmwareDiskProcess vmwareDiskProcess) {
        this.vmwareDiskProcess = vmwareDiskProcess;
    }

    /**
     * vmwareInitProcessを設定します。
     *
     * @param vmwareInitProcess vmwareInitProcess
     */
    public void setVmwareInitProcess(VmwareInitProcess vmwareInitProcess) {
        this.vmwareInitProcess = vmwareInitProcess;
    }

    /**
     * vmwareDnsProcessを設定します。
     *
     * @param vmwareDnsProcess vmwareDnsProcess
     */
    public void setVmwareDnsProcess(VmwareDnsProcess vmwareDnsProcess) {
        this.vmwareDnsProcess = vmwareDnsProcess;
    }

    /**
     * vmwareCustomizeProcessを設定します。
     *
     * @param vmwareCustomizeProcess vmwareCustomizeProcess
     */
    public void setVmwareCustomizeProcess(VmwareCustomizeProcess vmwareCustomizeProcess) {
        this.vmwareCustomizeProcess = vmwareCustomizeProcess;
    }

    /**
     * puppetClientを設定します。
     *
     * @param puppetClient puppetClient
     */
    public void setPuppetClient(PuppetClient puppetClient) {
        this.puppetClient = puppetClient;
    }

}
