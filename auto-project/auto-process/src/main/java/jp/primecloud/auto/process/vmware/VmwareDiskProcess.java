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

import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.VmwareDisk;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vmware.vim25.VirtualDeviceFileBackingInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.mo.Datastore;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareDiskProcess extends ServiceSupport {

    protected ProcessLogger processLogger;

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     * @param diskNo
     */
    public void attachDisk(VmwareProcessClient vmwareProcessClient, Long instanceNo, Long diskNo) {
        VmwareDisk vmwareDisk = vmwareDiskDao.read(diskNo);

        if (BooleanUtils.isTrue(vmwareDisk.getAttached())) {
            // ディスクがアタッチ済みの場合はスキップ
            return;
        }

        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        //イベントログ出力
        Component component = componentDao.read(vmwareDisk.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(vmwareProcessClient.getPlatformNo());
        if (StringUtils.isEmpty(vmwareDisk.getFileName())) {
            processLogger.debug(component, instance, "VmwareDiskCreate", new Object[] { platform.getPlatformName() });
        } else {
            processLogger.debug(component, instance, "VmwareDiskAttach", new Object[] { platform.getPlatformName(),
                    vmwareDisk.getFileName() });
        }

        // ディスクのアタッチ
        VirtualDisk disk = vmwareProcessClient.attachDisk(vmwareInstance.getMachineName(), vmwareDisk.getScsiId(),
                vmwareDisk.getSize(), vmwareDisk.getFileName());

        // ディスク情報の取得
        VirtualDeviceFileBackingInfo backingInfo = VirtualDeviceFileBackingInfo.class.cast(disk.getBacking());
        Datastore datastore = new Datastore(vmwareProcessClient.getVmwareClient().getServiceInstance()
                .getServerConnection(), backingInfo.getDatastore());

        //イベントログ出力
        if (StringUtils.isEmpty(vmwareDisk.getFileName())) {
            processLogger.debug(component, instance, "VmwareDiskCreateFinish",
                    new Object[] { platform.getPlatformName(), backingInfo.getFileName(), vmwareDisk.getSize() });
        } else {
            processLogger.debug(component, instance, "VmwareDiskAttachFinish",
                    new Object[] { platform.getPlatformName(), vmwareDisk.getFileName(), vmwareDisk.getSize() });
        }

        // データベース更新
        vmwareDisk = vmwareDiskDao.read(diskNo);
        vmwareDisk.setAttached(true);
        vmwareDisk.setDatastore(datastore.getName());
        vmwareDisk.setFileName(backingInfo.getFileName());
        vmwareDiskDao.update(vmwareDisk);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     * @param diskNo
     */
    public void detachDisk(VmwareProcessClient vmwareProcessClient, Long instanceNo, Long diskNo) {
        VmwareDisk vmwareDisk = vmwareDiskDao.read(diskNo);

        if (BooleanUtils.isNotTrue(vmwareDisk.getAttached())) {
            // ディスクがアタッチされていない場合はスキップ
            return;
        }

        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        //イベントログ出力
        Component component = componentDao.read(vmwareDisk.getComponentNo());
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(vmwareProcessClient.getPlatformNo());
        processLogger.debug(component, instance, "VmwareDiskDetach", new Object[] { platform.getPlatformName(),
                vmwareDisk.getFileName() });

        // ディスクをデタッチ
        vmwareProcessClient.detachDisk(vmwareInstance.getMachineName(), vmwareDisk.getScsiId());

        //イベントログ出力
        processLogger.debug(component, instance, "VmwareDiskDetachFinish", new Object[] { platform.getPlatformName(),
                vmwareDisk.getFileName() });

        // データベース更新
        vmwareDisk = vmwareDiskDao.read(diskNo);
        vmwareDisk.setAttached(false);
        vmwareDiskDao.update(vmwareDisk);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param diskNo
     */
    public void deleteDisk(VmwareProcessClient vmwareProcessClient, Long diskNo) {
        VmwareDisk vmwareDisk = vmwareDiskDao.read(diskNo);

        //イベントログ出力
        Component component = componentDao.read(vmwareDisk.getComponentNo());
        Instance instance = instanceDao.read(vmwareDisk.getInstanceNo());
        processLogger.debug(component, instance, "VmwareDiskDelete", new Object[] { vmwareDisk.getFileName() });

        // ディスクファイルの削除
        vmwareProcessClient.deleteDisk(vmwareDisk.getDatastore(), vmwareDisk.getFileName());

        //イベントログ出力
        processLogger.debug(component, instance, "VmwareDiskDeleteFinish", new Object[] { vmwareDisk.getFileName() });

        // データベース更新
        vmwareDisk = vmwareDiskDao.read(diskNo);
        vmwareDisk.setDatastore(null);
        vmwareDisk.setFileName(null);
        vmwareDiskDao.update(vmwareDisk);
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
