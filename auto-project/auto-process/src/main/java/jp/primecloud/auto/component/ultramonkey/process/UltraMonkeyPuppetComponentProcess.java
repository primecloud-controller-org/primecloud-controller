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
package jp.primecloud.auto.component.ultramonkey.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.component.ultramonkey.UltraMonkeyConstants;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.process.ComponentProcessContext;
import jp.primecloud.auto.process.puppet.PuppetComponentProcess;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class UltraMonkeyPuppetComponentProcess extends PuppetComponentProcess {

    /**
     * TODO: コンストラクタコメントを記述
     */
    public UltraMonkeyPuppetComponentProcess() {
        componentTypeName = UltraMonkeyConstants.COMPONENT_TYPE_NAME;
    }

    @Override
    protected Map<String, Object> createInstanceMap(Long componentNo, ComponentProcessContext context, boolean start,
            Long instanceNo, Map<String, Object> rootMap) {
        Map<String, Object> map = super.createInstanceMap(componentNo, context, start, instanceNo, rootMap);

        // 待ち受けIP
        List<String> listenIps = new ArrayList<String>();
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            // AWSの場合
            listenIps.add(instance.getPrivateIp());
            PlatformAws platformAws = platformAwsDao.read(instance.getPlatformNo());
            if (platform.getInternal() == false && platformAws.getVpc() == false) {
                // 外部のAWSプラットフォームでVPNを利用する場合、PublicIpでも待ち受ける
                // この分岐に来るパターンは、VPC+VPN もしくは 通常のVPNの場合
                listenIps.add(instance.getPublicIp());
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
            // Cloudstackの場合
            // VPN利用前提のため、PublicIpで待ち受ける
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            // VMwareの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
            // Niftyの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            // VCloudの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())) {
            // Azureの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        } else if (PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            // Openstackの場合
            listenIps.add(instance.getPublicIp());
            listenIps.add(instance.getPrivateIp());
        }

        map.put("listenIps", listenIps);

        return map;
    }

}
