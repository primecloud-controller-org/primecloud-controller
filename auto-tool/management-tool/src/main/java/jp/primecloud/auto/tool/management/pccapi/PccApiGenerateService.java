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
package jp.primecloud.auto.tool.management.pccapi;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.api.ApiConstants;
import jp.primecloud.auto.api.util.SecureRandamGenerator;
import jp.primecloud.auto.entity.crud.ApiCertificate;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.tool.management.main.SQLMain;

public class PccApiGenerateService extends ApiConstants {

    private static Log log = LogFactory.getLog(PccApiGenerateService.class);

    public static void genarate(CommandLine commandLine) throws AutoException {
        Long userNo = Long.parseLong(commandLine.getOptionValue("userno"));
        String generateType = commandLine.getOptionValue("generatetype");

        try {
            String userSql = "SELECT * FROM USER WHERE USER_NO =" + userNo;
            List<User> users = SQLMain.selectExecuteWithResult(userSql, User.class);
            User user = users.get(0);

            if (generateType != null && generateType.equals("accessId")) {
                String apiAccessId = "";
                while (true) {
                    apiAccessId = generateAccessId();
                    String apiSql = "SELECT * FROM API_CERTIFICATE WHERE API_ACCESS_ID ='" + apiAccessId + "'";
                    List<ApiCertificate> apiCertificates = SQLMain.selectExecuteWithResult(apiSql, ApiCertificate.class);
                    if (apiCertificates.isEmpty()) {
                        break;
                    }
                }
                log.info(user.getUsername() + " の accessId を生成しました");
                System.out.println(apiAccessId);
            } else if (generateType != null && generateType.equals("secretKey")) {
                String apiSecretKey = "";
                while (true) {
                    apiSecretKey = generateSecretKey();
                    String apiSql = "SELECT * FROM API_CERTIFICATE WHERE API_SECRET_KEY ='" + apiSecretKey + "'";
                    List<ApiCertificate> apiCertificates = SQLMain.selectExecuteWithResult(apiSql, ApiCertificate.class);
                    if (apiCertificates.isEmpty()) {
                        break;
                    }
                }
                log.info(user.getUsername() + " の secretKey を生成しました");
                System.out.println(apiSecretKey);
            } else {
                log.error("generateType が不正な値です");
                System.out.println("GENERATE_ERROR");
            }
        } catch (Exception e) {
            log.error("PCC-APIのキー生成でエラーが発生しました。userNo:" + userNo + " generateType:" + generateType, e);
            System.out.println("GENERATE_ERROR");
        }
    }

    public static String generateAccessId() throws AutoException {
        SecureRandamGenerator generator = new SecureRandamGenerator();
        return generator.generate(ACCESS_ID_LENGTH).toUpperCase();
    }

    private static String generateSecretKey() {
        SecureRandamGenerator generator = new SecureRandamGenerator();
        return generator.generate(SECRET_KEY_LENGTH);
    }
}
