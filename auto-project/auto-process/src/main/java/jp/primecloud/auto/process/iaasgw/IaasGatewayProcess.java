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
package jp.primecloud.auto.process.iaasgw;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.process.azure.AzureDnsProcess;
import jp.primecloud.auto.process.cloudstack.CloudstackDnsProcess;
import jp.primecloud.auto.process.openstack.OpenstackDnsProcess;
import jp.primecloud.auto.process.vcloud.VcloudDnsProcess;
import jp.primecloud.auto.service.ServiceSupport;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class IaasGatewayProcess extends ServiceSupport {

    protected IaasGatewayFactory iaasGatewayFactory;

    protected CloudstackDnsProcess cloudstackDnsProcess;

    protected VcloudDnsProcess vcloudDnsProcess;

    protected AzureDnsProcess azureDnsProcess;

    protected OpenstackDnsProcess openstackDnsProcess;

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void start(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        // 起動処理
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), instance.getPlatformNo());
        gateway.startInstance(instanceNo);

        // DNSに関する処理
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            cloudstackDnsProcess.startDns(instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            vcloudDnsProcess.startDns(instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            azureDnsProcess.startDns(instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            openstackDnsProcess.startDns(instanceNo);
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

        // DNSに関する処理
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            cloudstackDnsProcess.stopDns(instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            vcloudDnsProcess.stopDns(instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            azureDnsProcess.stopDns(instanceNo);
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            openstackDnsProcess.stopDns(instanceNo);
        }

        // 停止処理
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), instance.getPlatformNo());
        gateway.stopInstance(instanceNo);
    }

    public void setIaasGatewayFactory(IaasGatewayFactory iaasGatewayFactory) {
        this.iaasGatewayFactory = iaasGatewayFactory;
    }

    public void setCloudstackDnsProcess(CloudstackDnsProcess cloudstackDnsProcess) {
        this.cloudstackDnsProcess = cloudstackDnsProcess;
    }

    public void setVcloudDnsProcess(VcloudDnsProcess vcloudDnsProcess) {
        this.vcloudDnsProcess = vcloudDnsProcess;
    }

    public void setAzureDnsProcess(AzureDnsProcess azureDnsProcess) {
        this.azureDnsProcess = azureDnsProcess;
    }

    public void setOpenstackDnsProcess(OpenstackDnsProcess openstackDnsProcess) {
        this.openstackDnsProcess = openstackDnsProcess;
    }

}
