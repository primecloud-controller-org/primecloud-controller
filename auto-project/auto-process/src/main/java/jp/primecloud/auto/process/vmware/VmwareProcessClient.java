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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.vmware.VmwareClient;

import com.vmware.vim25.AlreadyExists;
import com.vmware.vim25.CustomFieldDef;
import com.vmware.vim25.CustomizationSpec;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.HostNetworkPolicy;
import com.vmware.vim25.HostPortGroupConfig;
import com.vmware.vim25.HostPortGroupSpec;
import com.vmware.vim25.InvalidArgument;
import com.vmware.vim25.NetIpConfigInfoIpAddress;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDeviceConnectInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineGuestState;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineToolsRunningStatus;
import com.vmware.vim25.VirtualMachineToolsVersionStatus;
import com.vmware.vim25.VirtualSCSIController;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.CustomFieldsManager;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.FileManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostNetworkSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareProcessClient {

    protected Log log = LogFactory.getLog(getClass());

    protected VmwareClient vmwareClient;

    protected Long platformNo;

    public VmwareProcessClient(VmwareClient vmwareClient, Long platformNo) {
        this.vmwareClient = vmwareClient;
        this.platformNo = platformNo;
    }

    public VmwareClient getVmwareClient() {
        return vmwareClient;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    protected VirtualMachine getVirtualMachine(String machineName) {
        VirtualMachine machine = vmwareClient.search(VirtualMachine.class, machineName);
        if (machine == null) {
            // 仮想マシンが見つからない場合
            throw new AutoException("EPROCESS-000501", machineName);
        }

        return machine;
    }

    public void cloneVM(String machineName, String templateName, String computeResourceName, String resourcePoolName,
            String datastoreName) {
        // 仮想マシンが既に存在する場合はスキップ
        VirtualMachine machine = vmwareClient.search(VirtualMachine.class, machineName);
        if (machine != null) {
            return;
        }

        // クローン元のテンプレートを取得
        VirtualMachine templateMachine = getVirtualMachine(templateName);

        // クローン仕様を作成
        VirtualMachineCloneSpec cloneSpec = createCloneSpec(computeResourceName, resourcePoolName, datastoreName);

        // 仮想マシンフォルダ
        // TODO: ユーザごとのフォルダを作る
        Folder vmFolder = vmwareClient.search(Folder.class, "vm");

        // 仮想マシンのクローンを実行
        Task task;
        try {
            task = templateMachine.cloneVM_Task(vmFolder, machineName, cloneSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000502", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100421", machineName, templateName));
        }

        // 仮想マシンのクローンが完了するまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000502", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000502", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // 仮想マシンのクローンに失敗した場合
            AutoException exception = new AutoException("EPROCESS-000502", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100422", machineName, templateName));
        }
    }

    protected VirtualMachineCloneSpec createCloneSpec(String computeResourceName, String resourcePoolName,
            String datastoreName) {
        VirtualMachineRelocateSpec relocateSpec = new VirtualMachineRelocateSpec();

        // ComputeResource
        ComputeResource computeResource = vmwareClient.search(ComputeResource.class, computeResourceName);
        if (computeResource == null) {
            // ComputeResourceが見つからない場合
            throw new AutoException("EPROCESS-000503", computeResourceName);
        }

        // ResourcePool
        if (StringUtils.isEmpty(resourcePoolName)) {
            resourcePoolName = "Resources";
        }
        ResourcePool resourcePool = vmwareClient.search(computeResource, ResourcePool.class, resourcePoolName);
        if (resourcePool == null) {
            // ResourcePoolが見つからない場合
            throw new AutoException("EPROCESS-000504", resourcePoolName);
        }
        relocateSpec.setPool(resourcePool.getMOR());

        // Datastore
        if (StringUtils.isNotEmpty(datastoreName)) {
            // データストアが指定されている場合
            Datastore datastore = vmwareClient.search(Datastore.class, datastoreName);
            if (datastore == null) {
                // データストアが見つからない場合
                throw new AutoException("EPROCESS-000505", datastoreName);
            }
            relocateSpec.setDatastore(datastore.getMOR());
        }

        VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        cloneSpec.setLocation(relocateSpec);
        cloneSpec.setPowerOn(false);
        cloneSpec.setTemplate(false);

        return cloneSpec;
    }

    public void extendRootDisk(String machineName, Integer rootSize) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // ルートディスク
        VirtualDisk disk = getVirtualDisk(machine, 0);

        // 指定されたサイズよりも実際のサイズが大きければ、ディスクの拡張を行わない
        long newCapacityInKB = rootSize.longValue() * 1024 * 1024;
        if (disk.getCapacityInKB() >= newCapacityInKB) {
            return;
        }

        // 設定変更仕様
        disk.setCapacityInKB(newCapacityInKB);
        VirtualDeviceConfigSpec diskSpec = new VirtualDeviceConfigSpec();
        diskSpec.setOperation(VirtualDeviceConfigSpecOperation.edit);
        diskSpec.setDevice(disk);

        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setDeviceChange(new VirtualDeviceConfigSpec[] { diskSpec });

        // ディスクの設定変更
        Task task;
        try {
            task = machine.reconfigVM_Task(configSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000534", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100425", machineName, rootSize));
        }

        // 設定が変更されるまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000534", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000534", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // ディスクの設定変更に失敗した場合
            AutoException exception = new AutoException("EPROCESS-000534", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100426", machineName, rootSize));
        }
    }

    public void powerOnVM(String machineName) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // パワーオン状態の場合はスキップ
        VirtualMachineRuntimeInfo runtimeInfo = machine.getRuntime();
        if (runtimeInfo.getPowerState() == VirtualMachinePowerState.poweredOn) {
            return;
        }

        // 仮想マシンのパワーオン
        Task task;
        try {
            task = machine.powerOnVM_Task(null);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000506", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100411", machineName));
        }

        // パワーオンが完了するまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000506", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000506", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // 仮想マシンのパワーオンに失敗した場合
            AutoException exception = new AutoException("EPROCESS-000506", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100412", machineName));
        }
    }

    public void powerOffVM(String machineName) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // パワーオフ状態の場合はスキップ
        VirtualMachineRuntimeInfo runtimeInfo = machine.getRuntime();
        if (runtimeInfo.getPowerState() == VirtualMachinePowerState.poweredOff) {
            return;
        }

        // 仮想マシンのパワーオフ
        Task task;
        try {
            task = machine.powerOffVM_Task();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000507", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100413", machineName));
        }

        // パワーオフが完了するまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000507", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000507", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // 仮想マシンのパワーオフに失敗した場合
            AutoException exception = new AutoException("EPROCESS-000507", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100414", machineName));
        }
    }

    public void shutdownGuest(String machineName) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // パワーオフ状態の場合はスキップ
        VirtualMachineRuntimeInfo runtimeInfo = machine.getRuntime();
        if (runtimeInfo.getPowerState() == VirtualMachinePowerState.poweredOff) {
            return;
        }

        // 仮想マシンのシャットダウン
        try {
            machine.shutdownGuest();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000519", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100415", machineName));
        }

        // シャットダウンが完了するまで待機
        waitForStopped(machineName);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100416", machineName));
        }
    }

    public void destroyVM(String machineName) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // 仮想マシンの削除
        Task task;
        try {
            task = machine.destroy_Task();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000508", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100423", machineName));
        }

        // 仮想マシンが削除されるまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000508", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000508", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // 仮想マシンの削除に失敗した場合
            AutoException exception = new AutoException("EPROCESS-000508", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100424", machineName));
        }
    }

    public void changeResourceVM(String machineName, Integer cpu, Long memory) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // 設定変更仕様
        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setNumCPUs(cpu);
        configSpec.setMemoryMB(memory);

        // リソース設定変更
        Task task;
        try {
            task = machine.reconfigVM_Task(configSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000513", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100433", machineName));
        }

        // リソース設定が変更されるまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000513", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000513", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // 仮想マシンのリソースの変更に失敗した場合
            AutoException exception = new AutoException("EPROCESS-000513", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100434", machineName));
        }
    }

    public void setExtraConfigVM(String machineName, Map<String, Object> extraConfigs) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // ExtraConfigのリスト
        List<OptionValue> optionValues = new ArrayList<OptionValue>();
        for (Map.Entry<String, Object> entry : extraConfigs.entrySet()) {
            OptionValue optionValue = new OptionValue();
            optionValue.setKey(entry.getKey());
            optionValue.setValue(entry.getValue());
            optionValues.add(optionValue);
        }

        // インスタンス情報設定仕様
        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setExtraConfig(optionValues.toArray(new OptionValue[optionValues.size()]));

        // インスタンス情報の設定
        Task task;
        try {
            task = machine.reconfigVM_Task(configSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000523", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100435", machineName));
        }

        // インスタンス情報が設定されるまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000523", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000523", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // インスタンス情報の設定に失敗した場合
            AutoException exception = new AutoException("EPROCESS-000523", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100436", machineName));
        }
    }

    public void waitForRunning(String machineName, List<String> networkNames) {
        VirtualMachine machine = getVirtualMachine(machineName);

        // 起動判定処理
        long startTime = System.currentTimeMillis();
        while (true) {
            // 15分経過しても起動判定が終了しない場合、タイムアウトさせる
            if (System.currentTimeMillis() - startTime > 15 * 60 * 1000L) {
                throw new AutoException("EPROCESS-000531", machineName);
            }

            try {
                Thread.sleep(30 * 1000L);
            } catch (InterruptedException ignore) {
            }

            // 仮想マシンがパワーオンであること
            if (machine.getRuntime().getPowerState() != VirtualMachinePowerState.poweredOn) {
                throw new AutoException("EPROCESS-000530", machineName);
            }

            // ゲスト情報を取得
            GuestInfo guestInfo = machine.getGuest();

            if (log.isDebugEnabled()) {
                log.debug(jp.primecloud.auto.util.StringUtils.reflectToString(guestInfo));
            }

            // VMware Toolsがインストールされていること
            if (VirtualMachineToolsVersionStatus.guestToolsNotInstalled.toString().equals(
                    guestInfo.getToolsVersionStatus2())) {
                throw new AutoException("EPROCESS-000509", machineName);
            }

            // VMware Toolsが起動していること
            if (!VirtualMachineToolsRunningStatus.guestToolsRunning.toString()
                    .equals(guestInfo.getToolsRunningStatus())) {
                continue;
            }

            // ゲストが起動していること
            if (!VirtualMachineGuestState.running.toString().equals(guestInfo.getGuestState())) {
                continue;
            }

            // ネットワーク情報を取得できていること
            if (guestInfo.getNet() == null) {
                continue;
            }

            // IPv4アドレスが付いたネットワークアダプタ名を取得
            List<String> enableNetworkNames = new ArrayList<String>();
            for (GuestNicInfo nicInfo : guestInfo.getNet()) {
                // 仮想ネットワークアダプタの場合はスキップ
                if (StringUtils.isEmpty(nicInfo.getNetwork())) {
                    continue;
                }

                // NIC情報からIPv4のアドレスを取得
                NetIpConfigInfoIpAddress[] tmpAddresses = nicInfo.getIpConfig().getIpAddress();
                if (tmpAddresses == null) {
                    continue;
                }
                String ipAddress = null;
                for (NetIpConfigInfoIpAddress tmpAdress : tmpAddresses) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(tmpAdress.getIpAddress());
                        if (inetAddress instanceof Inet4Address) {
                            ipAddress = tmpAdress.getIpAddress();
                            break;
                        }
                    } catch (UnknownHostException ignore) {
                    }
                }

                // IPアドレスを取得できない、またはリンクローカルのIPアドレスの場合はスキップ
                if (ipAddress == null || StringUtils.startsWith(ipAddress, "169.254.")) {
                    continue;
                }

                enableNetworkNames.add(nicInfo.getNetwork());
            }

            // 指定された全てのネットワークアダプタにIPv4アドレスが付いていること
            if (!enableNetworkNames.containsAll(networkNames)) {
                continue;
            }

            break;
        }
    }

    public void waitForStopped(String machineName) {
        VirtualMachine machine = getVirtualMachine(machineName);

        // 停止判定処理
        while (true) {
            try {
                Thread.sleep(30 * 1000L);
            } catch (InterruptedException ignore) {
            }

            VirtualMachineRuntimeInfo runtimeInfo = machine.getRuntime();

            if (runtimeInfo.getPowerState() == VirtualMachinePowerState.poweredOff) {
                break;
            }
        }
    }

    public void addNetwork(String hostSystemName, String portGroupName, Integer vlanId, String vswitchName) {
        // HostSystem
        HostSystem hostSystem = vmwareClient.search(HostSystem.class, hostSystemName);
        if (hostSystem == null) {
            // 仮想マシンが見つからない場合
            throw new AutoException("EPROCESS-000524", hostSystem);
        }

        // HostNetworkSystem
        HostNetworkSystem networkSystem;
        try {
            networkSystem = hostSystem.getHostNetworkSystem();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // PortGroupがHostSystemに存在するかどうかのチェック
        boolean isExist = false;
        for (HostPortGroupConfig config : networkSystem.getNetworkConfig().getPortgroup()) {
            if (StringUtils.equalsIgnoreCase(portGroupName, config.getSpec().getName())) {
                isExist = true;
                break;
            }
        }

        // PortGroupが既に存在する場合はスキップ
        if (isExist) {
            return;
        }

        // PortGroupを作成する
        HostPortGroupSpec portGroupSpec = new HostPortGroupSpec();
        portGroupSpec.setName(portGroupName);
        if (vlanId != null) {
            portGroupSpec.setVlanId(vlanId);
        }
        portGroupSpec.setVswitchName(vswitchName);
        portGroupSpec.setPolicy(new HostNetworkPolicy());

        try {
            networkSystem.addPortGroup(portGroupSpec);
        } catch (AlreadyExists ignore) {
            // 他のスレッドで同時に作成された場合に備えて握りつぶす
            return;
        } catch (RemoteException e) {
            AutoException exception = new AutoException("EPROCESS-000520", e, portGroupName);
            exception.addDetailInfo(jp.primecloud.auto.util.StringUtils.reflectToString(portGroupSpec));
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100441", hostSystem.getName(), portGroupName));
        }
    }

    public void removeNetwork(String hostSystemName, String portGroupName) {
        // HostSystem
        HostSystem hostSystem = vmwareClient.search(HostSystem.class, hostSystemName);
        if (hostSystem == null) {
            // 仮想マシンが見つからない場合
            throw new AutoException("EPROCESS-000524", hostSystem);
        }

        // HostNetworkSystem
        HostNetworkSystem networkSystem;
        try {
            networkSystem = hostSystem.getHostNetworkSystem();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // PortGroupがHostSystemに存在するかどうかのチェック
        boolean isExist = false;
        for (HostPortGroupConfig config : networkSystem.getNetworkConfig().getPortgroup()) {
            if (StringUtils.equalsIgnoreCase(portGroupName, config.getSpec().getName())) {
                isExist = true;
                break;
            }
        }

        // PortGroupが存在しない場合はスキップ
        if (!isExist) {
            return;
        }

        // PortGroupを削除する
        try {
            networkSystem.removePortGroup(portGroupName);
        } catch (NotFound ignore) {
            // 他のスレッドで同時に削除された場合に備えて握りつぶす
            return;
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000521", e, portGroupName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100442", hostSystem.getName(), portGroupName));
        }
    }

    public VirtualDisk attachDisk(String machineName, Integer scsiId, Integer size, String fileName) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // ディスク既にが存在するかどうか
        boolean existDisk = StringUtils.isEmpty(fileName) ? false : true;

        // ディスク定義の作成
        VirtualDisk disk;
        if (!existDisk) {
            // ディスクが存在しない場合
            disk = newDisk(machine, scsiId, size);
        } else {
            // ディスクが存在する場合
            disk = reuseDisk(machine, scsiId, fileName);
        }

        // ディスクのアタッチ仕様
        VirtualDeviceConfigSpec diskSpec = new VirtualDeviceConfigSpec();
        diskSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
        if (!existDisk) {
            diskSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
        }
        diskSpec.setDevice(disk);

        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setDeviceChange(new VirtualDeviceConfigSpec[] { diskSpec });

        // ディスクのアタッチ
        Task task;
        try {
            task = machine.reconfigVM_Task(configSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000515", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100451", machineName));
        }

        // ディスクがアタッチされるまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000515", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000515", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // ディスクのアタッチに失敗した場合
            AutoException exception = new AutoException("EPROCESS-000515", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100452", machineName));
        }

        return getVirtualDisk(machine, scsiId);
    }

    protected VirtualDisk newDisk(VirtualMachine machine, Integer scsiId, Integer size) {
        // SCSIコントローラを取得
        VirtualSCSIController scsiController = getSCSIController(machine);

        // 仮想マシン自体のディスクを取得
        VirtualDisk machineDisk = getVirtualDisk(machine, 0);
        VirtualDiskFlatVer2BackingInfo machieBackingInfo = VirtualDiskFlatVer2BackingInfo.class.cast(machineDisk
                .getBacking());

        // VirtualDisk
        VirtualDisk disk = new VirtualDisk();
        disk.setUnitNumber(scsiId);
        disk.setCapacityInKB(size * 1024L * 1024L);
        disk.setControllerKey(scsiController.getKey());

        // VirtualDiskFlatVer2BackingInfo
        VirtualDiskFlatVer2BackingInfo backingInfo = new VirtualDiskFlatVer2BackingInfo();
        backingInfo.setDatastore(null);
        backingInfo.setFileName("");
        backingInfo.setDiskMode("persistent");
        backingInfo.setSplit(false);
        backingInfo.setEagerlyScrub(false);
        backingInfo.setThinProvisioned(machieBackingInfo.getThinProvisioned());
        backingInfo.setWriteThrough(false);
        disk.setBacking(backingInfo);

        // VirtualDeviceConnectInfo
        VirtualDeviceConnectInfo connectInfo = new VirtualDeviceConnectInfo();
        connectInfo.setAllowGuestControl(false);
        connectInfo.setStartConnected(true);
        connectInfo.setConnected(true);
        disk.setConnectable(connectInfo);

        return disk;
    }

    protected VirtualDisk reuseDisk(VirtualMachine machine, Integer scsiId, String fileName) {
        // SCSIコントローラを取得
        VirtualSCSIController scsiController = getSCSIController(machine);

        // 仮想マシン自体のディスクを取得
        VirtualDisk machineDisk = getVirtualDisk(machine, 0);
        VirtualDiskFlatVer2BackingInfo machieBackingInfo = VirtualDiskFlatVer2BackingInfo.class.cast(machineDisk
                .getBacking());

        // VirtualDisk
        VirtualDisk disk = new VirtualDisk();
        disk.setUnitNumber(scsiId);
        disk.setControllerKey(scsiController.getKey());

        // VirtualDiskFlatVer2BackingInfo
        VirtualDiskFlatVer2BackingInfo backingInfo = new VirtualDiskFlatVer2BackingInfo();
        backingInfo.setFileName(fileName);
        backingInfo.setDiskMode("persistent");
        backingInfo.setSplit(false);
        backingInfo.setEagerlyScrub(false);
        backingInfo.setThinProvisioned(machieBackingInfo.getThinProvisioned());
        backingInfo.setWriteThrough(false);
        disk.setBacking(backingInfo);

        // VirtualDeviceConnectInfo
        VirtualDeviceConnectInfo connectInfo = new VirtualDeviceConnectInfo();
        connectInfo.setAllowGuestControl(false);
        connectInfo.setStartConnected(true);
        connectInfo.setConnected(true);
        disk.setConnectable(connectInfo);

        return disk;
    }

    protected VirtualSCSIController getSCSIController(VirtualMachine machine) {
        // 仮想マシンにあるSCSIコントローラのうち、BusNumberが0のものを取得する
        VirtualSCSIController scsiController = null;
        for (VirtualDevice device : machine.getConfig().getHardware().getDevice()) {
            if (device instanceof VirtualSCSIController) {
                VirtualSCSIController scsiController2 = VirtualSCSIController.class.cast(device);
                if (scsiController2.getBusNumber() == 0) {
                    scsiController = scsiController2;
                    break;
                }
            }
        }

        if (scsiController == null) {
            // SCSIコントローラが見つからない場合
            // TODO: SCSIコントローラを作る？
            throw new AutoException("EPROCESS-000517", 0);
        }

        return scsiController;
    }

    protected VirtualDisk getVirtualDisk(VirtualMachine machine, Integer scsiId) {
        // SCSIコントローラを取得
        VirtualSCSIController scsiController = getSCSIController(machine);

        // SCSIコントローラとSCSI IDが一致するディスクを取得
        VirtualDisk disk = null;
        for (VirtualDevice device : machine.getConfig().getHardware().getDevice()) {
            if (device instanceof VirtualDisk) {
                VirtualDisk tmpDisk = VirtualDisk.class.cast(device);
                if (tmpDisk.getControllerKey() != null && tmpDisk.getControllerKey().equals(scsiController.getKey())) {
                    if (tmpDisk.getUnitNumber() != null && tmpDisk.getUnitNumber().equals(scsiId)) {
                        disk = tmpDisk;
                        break;
                    }
                }
            }
        }

        if (disk == null) {
            // VirtualDiskが見つからない場合
            throw new AutoException("EPROCESS-000518", scsiId);
        }

        return disk;
    }

    public void detachDisk(String machineName, Integer scsiId) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // デタッチ対象のディスクを取得
        VirtualDisk disk = getVirtualDisk(machine, scsiId);

        // ディスクのデタッチ仕様
        VirtualDeviceConfigSpec diskSpec = new VirtualDeviceConfigSpec();
        diskSpec.setOperation(VirtualDeviceConfigSpecOperation.remove);
        //diskSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.destroy);
        diskSpec.setDevice(disk);

        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setDeviceChange(new VirtualDeviceConfigSpec[] { diskSpec });

        // ディスクのデタッチ
        Task task;
        try {
            task = machine.reconfigVM_Task(configSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000516", e, machineName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100453", machineName));
        }

        // ディスクがデタッチされるまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000516", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000516", e, machineName);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // ディスクのアタッチに失敗した場合
            AutoException exception = new AutoException("EPROCESS-000516", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100454", machineName));
        }
    }

    public void deleteDisk(String datastoreName, String fileName) {
        // Datacenter
        ManagedEntity datacenter = vmwareClient.getRootEntity();

        // Datastore
        Datastore datastore = vmwareClient.search(Datastore.class, datastoreName);
        if (datastore == null) {
            // データストアが見つからない場合
            throw new AutoException("EPROCESS-000505", datastoreName);
        }

        // ディスクの削除
        FileManager fileManager = vmwareClient.getServiceInstance().getFileManager();
        if (fileManager == null) {
            // fileManagerが利用できない場合
            throw new AutoException("EPROCESS-000533");
        }

        try {
            // ディスク削除
            fileManager.deleteDatastoreFile_Task(fileName, (Datacenter) datacenter);
            // ディスク削除後にごみができ、再度アタッチするとエラーになるので削除
            String flatname;
            flatname = fileName.substring(0, fileName.length() - 5) + "-flat.vmdk";
            fileManager.deleteDatastoreFile_Task(flatname, (Datacenter) datacenter);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000522", e, fileName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100455", fileName));
        }
    }

    public List<CustomFieldDef> getCustomFieldDefs() {
        CustomFieldsManager manager = vmwareClient.getServiceInstance().getCustomFieldsManager();
        CustomFieldDef[] defs = manager.getField();

        List<CustomFieldDef> customFieldDefs = new ArrayList<CustomFieldDef>();
        if (defs != null) {
            for (CustomFieldDef def : defs) {
                customFieldDefs.add(def);
            }
        }
        return customFieldDefs;
    }

    public CustomFieldDef getCustomFieldDef(String name, Class<?> type) {
        CustomFieldsManager manager = vmwareClient.getServiceInstance().getCustomFieldsManager();
        CustomFieldDef[] defs = manager.getField();

        String typeName = type.getSimpleName();

        if (defs != null) {
            for (CustomFieldDef def : defs) {
                if (def.getName().equals(name) && def.getManagedObjectType().equals(typeName)) {
                    return def;
                }
            }
        }
        return null;
    }

    public void addCustomFieldDef(String name, Class<?> type) {
        CustomFieldsManager manager = vmwareClient.getServiceInstance().getCustomFieldsManager();
        CustomFieldDef[] defs = manager.getField();

        String typeName = type.getSimpleName();

        if (defs != null) {
            for (CustomFieldDef def : defs) {
                if (def.getName().equals(name) && def.getManagedObjectType().equals(typeName)) {
                    // 既にカスタムフィールドが追加されている場合はスキップ
                    return;
                }
            }
        }

        try {
            manager.addCustomFieldDef(name, typeName, null, null);
        } catch (DuplicateName ignore) {
            // 他のスレッドで同時に追加された場合に備えて握りつぶす
            return;
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000525", e, name, typeName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100461", name));
        }
    }

    public void removeCustomFieldDef(String name, Class<?> type) {
        CustomFieldsManager manager = vmwareClient.getServiceInstance().getCustomFieldsManager();
        CustomFieldDef[] defs = manager.getField();

        String typeName = type.getSimpleName();

        CustomFieldDef customFieldDef = null;
        if (defs != null) {
            for (CustomFieldDef def : defs) {
                if (def.getName().equals(name) && def.getManagedObjectType().equals(typeName)) {
                    customFieldDef = def;
                }
            }
        }

        if (customFieldDef == null) {
            // 既にカスタムフィールドが存在しない場合はスキップ
        }

        try {
            manager.removeCustomFieldDef(customFieldDef.getKey());
        } catch (InvalidArgument ignore) {
            // 他のスレッドで同時に削除された場合に備えて握りつぶす
            return;
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000526", e, name, typeName);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100462", name));
        }
    }

    public void setCustomValue(String machineName, String name, String value) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // カスタム値の設定
        try {
            machine.setCustomValue(name, value);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000527", e, machineName, name);
        }

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100463", machineName, name));
        }
    }

    public void customize(String machineName, CustomizationSpec customSpec) {
        // VirtualMachine
        VirtualMachine machine = getVirtualMachine(machineName);

        // 設定の変更
        Task task;
        try {
            task = machine.customizeVM_Task(customSpec);
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000529", e, machineName);
        }

        // 設定の変更が完了するまで待機
        try {
            task.waitForTask();
        } catch (RemoteException e) {
            throw new AutoException("EPROCESS-000529", e, machineName);
        } catch (InterruptedException ignore) {
        }

        // タスク情報の取得
        TaskInfo taskInfo;
        try {
            taskInfo = task.getTaskInfo();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        if (taskInfo.getState() != TaskInfoState.success) {
            // 仮想マシンのカスタマイズに失敗した場合
            AutoException exception = new AutoException("EPROCESS-000529", machineName);
            if (taskInfo.getError() != null) {
                exception.addDetailInfo(ReflectionToStringBuilder.toString(taskInfo.getError().getFault()));
                exception.addDetailInfo(taskInfo.getError().getLocalizedMessage());
            }
            throw exception;
        }
    }

}
