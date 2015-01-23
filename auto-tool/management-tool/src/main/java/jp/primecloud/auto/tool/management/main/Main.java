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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jp.primecloud.auto.tool.management.pccapi.PccApiGenerateService;
import jp.primecloud.auto.tool.management.service.UserService;
import jp.primecloud.auto.tool.management.util.ManagementConfigLoader;

/**
 *
 */
public class Main {

    public static void main(String args[]) {
        Options options = new Options();
        options.addOption("Z", false, "Zabbix mode");
        options.addOption("U", false, "UPDATE mode");
        options.addOption("S", false, "SELECT mode");
        options.addOption("C", false, "Create Mode");
        options.addOption("P", false, "Show Platform");
        options.addOption("L", false, "Show Users");
        options.addOption("E", false, "Ecrypt UserPassword");
        options.addOption("I", false, "IaasGateway Mode");
        options.addOption("A", false, "PCC-API Genarate ID or Key Mode");
        options.addOption("W", false, "Decrypt UserPassword");

        options.addOption("username", true, "Create the username");
        options.addOption("password", true, "Create the password");
        options.addOption("firstname", true, "Create the firstname");
        options.addOption("familyname", true, "Create the familyname");
        options.addOption("userno", true, "Create the userno");

        options.addOption("dburl", "connectionurl", true, "PrimeCloud Controller database url");
        options.addOption("dbuser", "username", true, "PrimeCloud Controller database username");
        options.addOption("dbpass", "password", true, "PrimeCloud Controller database password");

        options.addOption("sql", true, "SQL");
        options.addOption("columnname", true, "columnName");
        options.addOption("columntype", true, "columnType");
        options.addOption("salt", true, "Salt");

        OptionBuilder.withLongOpt("prepared");
        OptionBuilder.hasArgs();
        OptionBuilder.withDescription("execute as PreparedStatement");
        OptionBuilder.withArgName("params");
        Option optionPrepared = OptionBuilder.create();
        options.addOption(optionPrepared);

        // for Zabbix
        options.addOption("enable", false, "enable");
        options.addOption("disable", false, "disable");
        options.addOption("get", false, "getUser from zabbix");
        options.addOption("check", false, "API setting check for zabbix");

        options.addOption("config", true, "Property can obtain from management-config.properties");
        options.addOption("platformkind", true, "Platform kind. e.g. ec2 and ec2_vpc or vmware");
        options.addOption("platformname", true, "Platform can obtain from auto-config.xml");
        options.addOption("platformno", true, "Platform can obtain from auto-config.xml");

        // for IaasGateway(AWS, Cloudstack)
        options.addOption("keyname", true, "import your key pair as keyName");
        options.addOption("publickey", true, "import your public key");

        // for PCC
        options.addOption("accessid", true, "accessid for PCC-API");
        options.addOption("secretkey", true, "secretkey for PCC-API");
        options.addOption("generatetype", true, "genarateType for PCC-API");

        options.addOption("h", "help", false, "help");

        CommandLineParser parser = new BasicParser();

        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("引数の指定が間違っています -hコマンドで確認してください。");
            return;
        }

        if (commandLine.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("PCC script ", options);
        }

        ManagementConfigLoader.init();
        //コマンドの実行

        //Zabbixユーザ作成モード
        if (commandLine.hasOption("Z")) {
            if (commandLine.hasOption("C")) {
                //Zabbixユーザ作成
                ZabbixMain.createExecute(commandLine);
            } else if (commandLine.hasOption("U")) {
                //Zabbixユーザ更新
                ZabbixMain.updateExecute(commandLine);
            } else if (commandLine.hasOption("disable")) {
                //Zabbixユーザ無効化
                ZabbixMain.disableExecute(commandLine);
            } else if (commandLine.hasOption("enable")) {
                //Zabbixユーザ有効化
                ZabbixMain.enableExecute(commandLine);
            } else if (commandLine.hasOption("get")) {
                //Zabbixユーザ取得
                ZabbixMain.getUser(commandLine);
            } else if (commandLine.hasOption("check")) {
                //Zabbixバージョン取得
                ZabbixMain.checkApiVersion();
            }
        //PCCユーザ作成
        } else if (commandLine.hasOption("U")) {
            if (commandLine.hasOption("prepared")) {
                SQLMain.updateExecutePrepared(commandLine);
            } else {
                //Update文の実行
                SQLMain.updateExecute(commandLine);
            }
        } else if (commandLine.hasOption("S")) {
            //Select文の実行
            SQLMain.selectExecute(commandLine);
        } else if (commandLine.hasOption("P")) {
            //プラットフォームの表示
            ConfigMain.showPlatforms();
        } else if (commandLine.hasOption("L")) {
            //PCCユーザ使用可能プラットフォーム表示
            UserService.showUserPlatform();
        } else if (commandLine.hasOption("config")) {
            //設定ファイルから値の取得
            ConfigMain.getProperty(commandLine.getOptionValue("config"));
        } else if (commandLine.hasOption("platformname") && commandLine.hasOption("platformkind")) {
            //プラットフォーム名からプラットフォーム番号の取得
            ConfigMain.getPlatformNo(commandLine.getOptionValue("platformname"),
                    commandLine.getOptionValue("platformkind"));
        } else if (commandLine.hasOption("E")) {
            //PCCユーザ暗号化パスワード取得
            UserService.encryptUserPassword(commandLine.getOptionValue("password"));
        } else if (commandLine.hasOption("I")) {
            //IaasGatewayをCallしてAWS or Cloudstackへのキーペアインポート
            IaasGatewayMain.importExecute(commandLine);
        } else if (commandLine.hasOption("A")) {
            PccApiGenerateService.genarate(commandLine);
        } else if (commandLine.hasOption("W")) {
            //PCCユーザ復号化パスワード取得
            UserService.decryptUserPassword(commandLine.getOptionValue("password"),
                    commandLine.getOptionValue("salt"));
        }
    }
}
