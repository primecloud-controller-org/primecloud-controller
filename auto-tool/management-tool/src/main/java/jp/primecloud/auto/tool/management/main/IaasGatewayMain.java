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
package jp.primecloud.auto.tool.management.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import jp.primecloud.auto.dao.crud.PlatformAwsDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.tool.management.iaasgw.IaasGatewayScriptFactory;
import jp.primecloud.auto.tool.management.iaasgw.IaasGatewayScriptService;

public class IaasGatewayMain {

    protected static Log log = LogFactory.getLog(IaasGatewayMain.class);

    public static void importExecute(CommandLine commandLine) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        String keyName = commandLine.getOptionValue("keyName");
        String publicKey = commandLine.getOptionValue("publicKey");

        Long platformNo = Long.parseLong(commandLine.getOptionValue("platformno"));

        String platformName = "";

        try {
            PlatformDao platformDao = (PlatformDao) context.getBean("platformDao");
            PlatformAwsDao platformAwsDao = (PlatformAwsDao) context.getBean("platformAwsDao");

            IaasGatewayScriptService scriptService = IaasGatewayScriptFactory.createIaasGatewayScriptService(commandLine);
            Platform platform = platformDao.read(platformNo);
            platformName = platform.getPlatformName();

            PlatformAws aws = platformAwsDao.read(platformNo);
            if ("aws".equals(platform.getPlatformType()) && aws.getVpc() && !scriptService.hasSubnets()) {
                // AWSでVPC利用の場合
                // Subnetが無ければVPCは使用できない
                return;
            }
            scriptService.importKeyPair(keyName, publicKey);
        } catch (AutoException e) {
            log.error("キーのインポートでエラーが発生しました。keyName:" + keyName + " plarformName:" + platformName, e);
            System.out.println("IMPORT_ERROR");
        } catch (Exception e) {
            log.error("キーのインポートでエラーが発生しました。keyName:" + keyName + " plarformName:" + platformName, e);
            System.out.println("キーのインポートでエラーが発生しました。");
        } finally {
            context.destroy();
        }
    }
}
