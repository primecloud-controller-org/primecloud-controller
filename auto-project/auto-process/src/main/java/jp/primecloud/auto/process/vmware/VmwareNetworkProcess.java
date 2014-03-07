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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.entity.crud.VmwareNetwork;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.vmware.VmwareClient;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareNetworkProcess extends ServiceSupport {

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param networkNo
     */
    public void addNetwork(VmwareProcessClient vmwareProcessClient, Long networkNo) {
        addNetwork(vmwareProcessClient, networkNo, null);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param networkNo
     * @param instanceNo
     */
    public void addNetwork(VmwareProcessClient vmwareProcessClient, Long networkNo, Long instanceNo) {
        VmwareNetwork vmwareNetwork = vmwareNetworkDao.read(networkNo);

        // HostSystemを取得
        VmwareClient vmwareClient = vmwareProcessClient.getVmwareClient();
        ManagedEntity[] hostSystems;
        if (instanceNo == null) {
            hostSystems = vmwareClient.searchByType(HostSystem.class);
        } else {
            VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
            ComputeResource computeResource = vmwareClient.search(ComputeResource.class, vmwareInstance
                    .getComputeResource());
            hostSystems = vmwareClient.searchByType(computeResource, HostSystem.class);
        }

        // ネットワークを追加
        for (ManagedEntity entity : hostSystems) {
            HostSystem hostSystem = HostSystem.class.cast(entity);
            vmwareProcessClient.addNetwork(hostSystem.getName(), vmwareNetwork.getNetworkName(), vmwareNetwork
                    .getVlanId(), vmwareNetwork.getVswitchName());
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param networkNo
     */
    public void removeNetwork(VmwareProcessClient vmwareProcessClient, Long networkNo) {
        removeNetwork(vmwareProcessClient, networkNo, null);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param networkNo
     * @param instanceNo
     */
    public void removeNetwork(VmwareProcessClient vmwareProcessClient, Long networkNo, Long instanceNo) {
        VmwareNetwork vmwareNetwork = vmwareNetworkDao.read(networkNo);

        // HostSystemを取得
        VmwareClient vmwareClient = vmwareProcessClient.getVmwareClient();
        ManagedEntity[] hostSystems;
        if (instanceNo == null) {
            hostSystems = vmwareClient.searchByType(HostSystem.class);
        } else {
            VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
            ComputeResource computeResource = vmwareClient.search(ComputeResource.class, vmwareInstance
                    .getComputeResource());
            hostSystems = vmwareClient.searchByType(computeResource, HostSystem.class);
        }

        // ネットワークを除去
        for (ManagedEntity entity : hostSystems) {
            HostSystem hostSystem = HostSystem.class.cast(entity);
            vmwareProcessClient.removeNetwork(hostSystem.getName(), vmwareNetwork.getNetworkName());
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void changeEthernetCard(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // VirtualMachine
        VirtualMachine machine = vmwareProcessClient.getVirtualMachine(vmwareInstance.getMachineName());

        // VirtualEthernetCardを取得
        VirtualMachineConfigInfo configInfo = machine.getConfig();
        List<VirtualEthernetCard> ethernetCards = new ArrayList<VirtualEthernetCard>();
        for (VirtualDevice device : configInfo.getHardware().getDevice()) {
            if (device instanceof VirtualEthernetCard) {
                ethernetCards.add(VirtualEthernetCard.class.cast(device));
            }
        }

        // Keyの順でソート
        Collections.sort(ethernetCards, new Comparator<VirtualEthernetCard>() {
            @Override
            public int compare(VirtualEthernetCard o1, VirtualEthernetCard o2) {
                return o1.getKey() - o2.getKey();
            }
        });

        // 新しい設定のVirtualEthernetCardを用意
        List<VirtualEthernetCard> newEthernetCards = createEthernetCards(vmwareProcessClient, instanceNo);

        // 設定変更仕様を作成
        List<VirtualDeviceConfigSpec> deviceConfigSpecs = new ArrayList<VirtualDeviceConfigSpec>();

        for (int i = 0; i < newEthernetCards.size(); i++) {
            VirtualDeviceConfigSpec deviceConfigSpec = new VirtualDeviceConfigSpec();
            VirtualEthernetCard newEthernetCard = newEthernetCards.get(i);
            deviceConfigSpec.setDevice(newEthernetCard);

            if (i < ethernetCards.size()) {
                // 既存のVirtualEthernetCardと同じネットワークの場合は編集しない
                VirtualEthernetCard ethernetCard = ethernetCards.get(i);
                if (checkSameNetwork(ethernetCard, newEthernetCard)) {
                    continue;
                }

                // 既存VirtualEthernetCardの編集
                deviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.edit);
                newEthernetCard.setKey(ethernetCard.getKey());
            } else {
                // 新規VirtualEthernetCardの追加
                deviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
            }

            deviceConfigSpecs.add(deviceConfigSpec);
        }

        // イーサネットカード設定を変更しない場合はスキップ
        if (deviceConfigSpecs.isEmpty()) {
            return;
        }

        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setDeviceChange(deviceConfigSpecs.toArray(new VirtualDeviceConfigSpec[deviceConfigSpecs.size()]));

        // イーサネットカード設定の変更
        Task task;
        try {
            task = machine.reconfigVM_Task(configSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000514", e, vmwareInstance.getMachineName());
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100443", vmwareInstance.getMachineName()));
        }

        // イーサネットカード設定の変更が完了するまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000514", e, vmwareInstance.getMachineName());
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000514", e, vmwareInstance.getMachineName());
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // イーサネットカードの設定変更に失敗した場合
            AutoException exception = new AutoException("EPROCESS-000514", vmwareInstance.getMachineName());
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100444", vmwareInstance.getMachineName()));
        }
    }

    protected List<VirtualEthernetCard> createEthernetCards(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        List<VmwareNetwork> vmwareNetworks = vmwareNetworkDao.readByFarmNo(instance.getFarmNo());

        // ネットワーク名の取得
        PlatformVmware platformVmware = platformVmwareDao.read(instance.getPlatformNo());
        String publicNetworkName = platformVmware.getPublicNetwork();
        String privateNetworkName = platformVmware.getPrivateNetwork();
        for (VmwareNetwork vmwareNetwork : vmwareNetworks) {
            if (BooleanUtils.isTrue(vmwareNetwork.getPublicNetwork())) {
                publicNetworkName = vmwareNetwork.getNetworkName();
            } else {
                privateNetworkName = vmwareNetwork.getNetworkName();
            }
        }

        // イーサネット設定の作成
        List<VirtualEthernetCard> ethernetCards = new ArrayList<VirtualEthernetCard>();

        // Public側イーサネット設定
        VirtualEthernetCard ethernetCard = new VirtualE1000();
        VirtualEthernetCardNetworkBackingInfo backingInfo = new VirtualEthernetCardNetworkBackingInfo();
        backingInfo.setDeviceName(publicNetworkName);
        ethernetCard.setBacking(backingInfo);
        ethernetCards.add(ethernetCard);

        // Private側イーサネット設定
        VirtualEthernetCard ethernetCard2 = new VirtualE1000();
        VirtualEthernetCardNetworkBackingInfo backingInfo2 = new VirtualEthernetCardNetworkBackingInfo();
        backingInfo2.setDeviceName(privateNetworkName);
        ethernetCard2.setBacking(backingInfo2);
        ethernetCards.add(ethernetCard2);

        return ethernetCards;
    }

    protected boolean checkSameNetwork(VirtualEthernetCard ethernetCard1, VirtualEthernetCard ethernetCard2) {
        if (!(ethernetCard1.getBacking() instanceof VirtualEthernetCardNetworkBackingInfo)) {
            return false;
        }

        if (!(ethernetCard2.getBacking() instanceof VirtualEthernetCardNetworkBackingInfo)) {
            return false;
        }

        VirtualEthernetCardNetworkBackingInfo backingInfo1 = VirtualEthernetCardNetworkBackingInfo.class
                .cast(ethernetCard1.getBacking());

        VirtualEthernetCardNetworkBackingInfo backingInfo2 = VirtualEthernetCardNetworkBackingInfo.class
                .cast(ethernetCard2.getBacking());

        return StringUtils.equals(backingInfo1.getDeviceName(), backingInfo2.getDeviceName());
    }

}
