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

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.tool.management.iaasgw.IaasGatewayScriptFactory;
import jp.primecloud.auto.tool.management.iaasgw.IaasGatewayScriptService;

public class IaasGatewayMain {

    protected static Log log = LogFactory.getLog(IaasGatewayMain.class);

    public static void importExecute(CommandLine commandLine) {
        Long userNo = Long.parseLong(commandLine.getOptionValue("userno"));
        Long platformNo = Long.parseLong(commandLine.getOptionValue("platformno"));
        String platformKind = commandLine.getOptionValue("platformkind");
        String keyName = commandLine.getOptionValue("keyname");
        String publicKey = commandLine.getOptionValue("publickey");

        try {
            log.info("IaasGatewayScriptFactory");
            String platformSql = "SELECT * FROM PLATFORM WHERE PLATFORM_NO=" + platformNo;
            List<Platform> platforms = SQLMain.selectExecuteWithResult(platformSql, Platform.class);
            Platform platform = platforms.get(0);

            IaasGatewayScriptService scriptService = IaasGatewayScriptFactory.createIaasGatewayScriptService(userNo, platformNo, platform.getPlatformName());
            if ("aws".equals(platformKind)) {
                String platformAwsSql = "SELECT * FROM PLATFORM_AWS WHERE PLATFORM_NO=" + platformNo;
                List<PlatformAws> platformAwses = SQLMain.selectExecuteWithResult(platformAwsSql, PlatformAws.class);
                PlatformAws aws = platformAwses.get(0);
                if (BooleanUtils.isTrue(aws.getVpc()) && !scriptService.hasSubnets(aws.getVpcId())) {
                // AWSでVPC利用の場合
                // Subnetが無ければVPCは使用できない
                return;
            }
            }
            scriptService.importKeyPair(keyName, publicKey);
        } catch (AutoException e) {
            log.error("キーのインポートでエラーが発生しました。keyName:" + keyName + " platformNo:" + platformNo, e);
            System.out.println("IMPORT_ERROR");
        } catch (Exception e) {
            log.error("キーのインポートでエラーが発生しました。keyName:" + keyName + " platformNo:" + platformNo, e);
            System.out.println("キーのインポートでエラーが発生しました。");
        } finally {
        }
    }
}
