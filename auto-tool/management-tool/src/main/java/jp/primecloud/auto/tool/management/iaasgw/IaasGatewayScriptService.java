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
import org.springframework.context.support.ClassPathXmlApplicationContext;

import jp.primecloud.auto.dao.crud.PlatformAwsDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;

public class IaasGatewayScriptService {

    protected static Log log = LogFactory.getLog(IaasGatewayScriptService.class);

    protected IaasGatewayWrapper gateway;

    private Platform platform;
    private PlatformAws platformAws;

    public IaasGatewayScriptService(Long userNo, Long platformNo) throws AutoException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        IaasGatewayFactory factory = (IaasGatewayFactory) context.getBean("iaasGatewayFactory");
        gateway = factory.createIaasGateway(userNo, platformNo);

        try {
            PlatformDao platformDao = (PlatformDao) context.getBean("platformDao");
            platform = platformDao.read(platformNo);

            PlatformAwsDao platformAwsDao = (PlatformAwsDao) context.getBean("platformAwsDao");
            platformAws = platformAwsDao.read(platformNo);

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            context.destroy();
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

    public boolean hasSubnets() throws AutoException {
        if (StringUtils.isEmpty(platformAws.getVpcId())){
            log.info(platform.getPlatformName() + " にvpcIdが有りません");
            System.out.println("VPCID_EMPTY");
            return false;
        }

        String subnets = gateway.describeSubnets(platformAws.getVpcId());
        if (StringUtils.isEmpty(subnets)){
            log.info(platform.getPlatformName() + " にサブネットが有りません");
            System.out.println("SUBNET_EMPTY");
            return false;
        }
        return true;
    }
}
