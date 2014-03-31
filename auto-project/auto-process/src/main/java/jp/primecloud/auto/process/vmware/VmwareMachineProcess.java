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
import java.util.List;

import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.ImageVmware;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.entity.crud.PlatformVmwareInstanceType;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.entity.crud.VmwareNetwork;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.process.ProcessLogger;
import com.vmware.vim25.CustomFieldDef;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareMachineProcess extends ServiceSupport {

    protected String fieldUserName = "UserName";

    protected EventLogger eventLogger;

    protected ProcessLogger processLogger;

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     * @return
     */
    public boolean cloneVM(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        if (StringUtils.isNotEmpty(vmwareInstance.getDatastore())) {
            // データストアが設定されている場合はクローン済みとしてスキップ
            return false;
        }

        Instance instance = instanceDao.read(instanceNo);
        ImageVmware imageVmware = imageVmwareDao.read(instance.getImageNo());

        // データストアの選択
        String datastoreName = selectDatastore(vmwareProcessClient, vmwareInstance);

        // イベントログ出力
        Platform platform = platformDao.read(vmwareProcessClient.getPlatformNo());
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceCreate", new Object[] {
                platform.getPlatformName(), vmwareInstance.getMachineName() });

        // 仮想マシンをクローン
        vmwareProcessClient.cloneVM(vmwareInstance.getMachineName(), imageVmware.getTemplateName(),
                vmwareInstance.getComputeResource(), vmwareInstance.getResourcePool(), datastoreName);

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceCreateFinish",
                new Object[] { platform.getPlatformName(), vmwareInstance.getMachineName() });

        // データベース更新
        vmwareInstance = vmwareInstanceDao.read(instanceNo);
        vmwareInstance.setDatastore(datastoreName);
        vmwareInstanceDao.update(vmwareInstance);

        return true;
    }

    protected String selectDatastore(VmwareProcessClient vmwareProcessClient, VmwareInstance vmwareInstance) {
        // データストアフォルダ内のデータストアのうち、アクセス可能で空き容量が最も大きいものを用いる
        Datastore datastore = null;
        long freeSpace = 0L;

        String datastoreFolderName = vmwareInstance.getComputeResource() + "-storage";
        Folder datastoreFolder = vmwareProcessClient.getVmwareClient().search(Folder.class, datastoreFolderName);
        if (datastoreFolder != null) {
            ManagedEntity[] entities = vmwareProcessClient.getVmwareClient().searchByType(datastoreFolder,
                    Datastore.class);
            for (ManagedEntity entity : entities) {
                Datastore datastore2 = Datastore.class.cast(entity);
                DatastoreSummary summary2 = datastore2.getSummary();

                if (summary2.isAccessible() && freeSpace < summary2.getFreeSpace()) {
                    datastore = datastore2;
                    freeSpace = summary2.getFreeSpace();
                }
            }
        }

        if (datastore == null) {
            // 利用可能なデータストアがない場合
            throw new AutoException("EPROCESS-000528", vmwareInstance.getComputeResource());
        }

        return datastore.getName();
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void powerOnVM(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(vmwareProcessClient.getPlatformNo());
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceStart", new Object[] {
                platform.getPlatformName(), vmwareInstance.getMachineName() });

        // 仮想マシンをパワーオン
        vmwareProcessClient.powerOnVM(vmwareInstance.getMachineName());
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void powerOffVM(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(vmwareProcessClient.getPlatformNo());
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceStop", new Object[] {
                platform.getPlatformName(), vmwareInstance.getMachineName() });

        // 仮想マシンをパワーオフ
        vmwareProcessClient.powerOffVM(vmwareInstance.getMachineName());

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceStopFinish", new Object[] {
                platform.getPlatformName(), vmwareInstance.getMachineName() });

        // データベースを更新
        vmwareInstance = vmwareInstanceDao.read(instanceNo);
        vmwareInstance.setIpAddress(null);
        vmwareInstance.setPrivateIpAddress(null);
        vmwareInstanceDao.update(vmwareInstance);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void shutdownGuest(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(vmwareProcessClient.getPlatformNo());
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceStop", new Object[] {
                platform.getPlatformName(), vmwareInstance.getMachineName() });

        // 仮想マシンのゲストをシャットダウン
        vmwareProcessClient.shutdownGuest(vmwareInstance.getMachineName());

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceStopFinish", new Object[] {
                platform.getPlatformName(), vmwareInstance.getMachineName() });

        // データベースを更新
        vmwareInstance = vmwareInstanceDao.read(instanceNo);
        vmwareInstance.setIpAddress(null);
        vmwareInstance.setPrivateIpAddress(null);
        vmwareInstanceDao.update(vmwareInstance);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void destroy(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(vmwareProcessClient.getPlatformNo());
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceDelete", new Object[] {
                platform.getPlatformName(), vmwareInstance.getMachineName() });

        // 仮想マシンを削除
        vmwareProcessClient.destroyVM(vmwareInstance.getMachineName());

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceDeleteFinish",
                new Object[] { platform.getPlatformName(), vmwareInstance.getMachineName() });
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void waitForRunning(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // ゲストの起動完了待ち
        vmwareProcessClient.waitForRunning(vmwareInstance.getMachineName());
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void getGuestInfo(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
        List<VmwareNetwork> vmwareNetworks = vmwareNetworkDao.readByFarmNo(instance.getFarmNo());

        // ネットワーク名の取得
        Platform platform = platformDao.read(instance.getPlatformNo());
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

        String publicIpAddress = null;
        String privateIpAddress = null;

        VirtualMachine machine = vmwareProcessClient.getVirtualMachine(vmwareInstance.getMachineName());
        for (GuestNicInfo nicInfo : machine.getGuest().getNet()) {
            // NIC情報からIPv4のアドレスを取得
            String[] tmpAddresses = nicInfo.getIpAddress();
            if (tmpAddresses == null) {
                continue;
            }

            String ipAddress = null;
            for (String tmpAdress : tmpAddresses) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(tmpAdress);
                    if (inetAddress instanceof Inet4Address) {
                        ipAddress = tmpAdress;
                        break;
                    }
                } catch (UnknownHostException ignore) {
                }
            }

            // NIC情報がPublicかPrivateかの判定
            if (publicNetworkName.equals(nicInfo.getNetwork())) {
                publicIpAddress = ipAddress;
            } else if (privateNetworkName.equals(nicInfo.getNetwork())) {
                privateIpAddress = ipAddress;
            }
        }

        if (publicIpAddress == null) {
            // パブリックIPを取得できない場合
            throw new AutoException("EPROCESS-000510", vmwareInstance.getMachineName());
        } else if (privateIpAddress == null) {
            // プライベートIPを取得できない場合
            throw new AutoException("EPROCESS-000511", vmwareInstance.getMachineName());
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "VmwareInstanceStartFinish",
                new Object[] { platform.getPlatformName(), vmwareInstance.getMachineName() });

        // データベースに格納
        vmwareInstance = vmwareInstanceDao.read(instanceNo);
        vmwareInstance.setIpAddress(publicIpAddress);
        vmwareInstance.setPrivateIpAddress(privateIpAddress);
        vmwareInstanceDao.update(vmwareInstance);

        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100432", vmwareInstance.getMachineName()));
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void changeResource(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // InstanceTypeの取得
        PlatformVmwareInstanceType instanceType = null;
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (platform != null && ("vmware".equals(platform.getPlatformType()))) {
            PlatformVmware platformVmware = platformVmwareDao.read(instance.getPlatformNo());
            List<PlatformVmwareInstanceType> instanceTypes = platformVmwareInstanceTypeDao.readByPlatformNo(instance.getPlatformNo());
            if (platformVmware != null && instanceTypes.isEmpty() == false) {
                for (PlatformVmwareInstanceType instanceType2 : instanceTypes) {
                    if (StringUtils.equals(vmwareInstance.getInstanceType(), instanceType2.getInstanceTypeName())) {
                        instanceType = instanceType2;
                        break;
                    }
                }
            }
        }
        if (instanceType == null) {
            // InstanceTypeが見つからない場合
            throw new AutoException("EPROCESS-000512", vmwareInstance.getInstanceType());
        }

        // 仮想マシンのリソースを変更
        vmwareProcessClient.changeResourceVM(vmwareInstance.getMachineName(), instanceType.getCpu(),
                instanceType.getMemory());
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void updateCustomValue(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        // カスタムフィールドがない場合は作成
        CustomFieldDef customFieldDef = vmwareProcessClient.getCustomFieldDef(fieldUserName, VirtualMachine.class);
        if (customFieldDef == null) {
            vmwareProcessClient.addCustomFieldDef(fieldUserName, VirtualMachine.class);
        }

        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());
        User user = userDao.read(farm.getUserNo());

        // カスタムフィールドの値を設定
        vmwareProcessClient.setCustomValue(vmwareInstance.getMachineName(), fieldUserName, user.getUsername());
    }

    /**
     * fieldUserNameを設定します。
     *
     * @param fieldUserName fieldUserName
     */
    public void setFieldUserName(String fieldUserName) {
        this.fieldUserName = fieldUserName;
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
