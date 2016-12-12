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
package jp.primecloud.auto.tool.management.iaasgw;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;

public class IaasGatewayScriptService {

    protected static Log log = LogFactory.getLog(IaasGatewayScriptService.class);

    protected IaasGatewayWrapper gateway;

    protected Platform platform;

    public IaasGatewayScriptService(Long userNo, Platform platform) throws AutoException {
        try {
            log.info("IaasGatewayScriptService before context.getBean(iaasGatewayFactory)");
            IaasGatewayFactory factory = new IaasGatewayFactory();
            gateway = factory.createIaasGateway(userNo, platform.getPlatformNo());
            this.platform = platform;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    public void importKeyPair(String keyName, String publicKey) throws AutoException {

        // キーペアがすでに登録されていたら何もしない
        String keyPairs = gateway.describeKeyPairs();
        if (StringUtils.isNotEmpty(keyPairs)) {
            for (String keyPair: keyPairs.split("##")) {
                if (StringUtils.equals(keyName, keyPair)) {
                    log.info(platform.getPlatformName() + " の " + keyName + " はすでに登録されている為、キーのインポートをスキップします");
                    System.out.println("IMPORT_SKIPPED");
                    return;
                }
            }
        }

        gateway.importKeyPair(keyName, publicKey);
        log.info(keyName + "のキーをインポートしました。");
    }

    public boolean hasSubnets(String vpcId) throws AutoException {
        if (StringUtils.isEmpty(vpcId)){
            log.info(platform.getPlatformName() + " にvpcIdが有りません");
            System.out.println("VPCID_EMPTY");
            return false;
        }

        String subnets = gateway.describeSubnets(vpcId);
        if (StringUtils.isEmpty(subnets)){
            log.info(platform.getPlatformName() + " にサブネットが有りません");
            System.out.println("SUBNET_EMPTY");
            return false;
        }
        return true;
    }
}
