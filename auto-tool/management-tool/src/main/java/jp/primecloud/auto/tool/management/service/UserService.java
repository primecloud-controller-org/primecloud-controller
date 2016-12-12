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
package jp.primecloud.auto.tool.management.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.CloudstackCertificate;
import jp.primecloud.auto.entity.crud.NiftyCertificate;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VcloudCertificate;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.tool.management.main.SQLMain;

public class UserService {

    protected static Log log = LogFactory.getLog(UserService.class);

    public static int padSize = 20;

    public static void showUserPlatform() {
        try {
            String userSql = "SELECT * FROM USER";
            List<User> users = SQLMain.selectExecuteWithResult(userSql, User.class);

            StringBuilder titles = new StringBuilder();
            titles.append(StringUtils.rightPad("Username", padSize, " "));
            titles.append(StringUtils.rightPad("Status", padSize, " "));
            titles.append(StringUtils.rightPad("Platform", padSize, " "));
            System.out.println(titles.toString());
            String disablecode = Config.getProperty("DISABLE_CODE");

            Map<Long, Platform> platformMap = new LinkedHashMap<Long, Platform>();
            String platformSql = "SELECT * FROM PLATFORM";
            List<Platform> platforms = SQLMain.selectExecuteWithResult(platformSql, Platform.class);
            for (Platform platform: platforms) {
                platformMap.put(platform.getPlatformNo(), platform);
            }

            Map<Long, List<AwsCertificate>> awsCertificateMap = new LinkedHashMap<Long, List<AwsCertificate>>();
            String awsAql = "SELECT * FROM AWS_CERTIFICATE";
            List<AwsCertificate> tmpAwsCertificates = SQLMain.selectExecuteWithResult(awsAql, AwsCertificate.class);
            for (AwsCertificate awsCertificate: tmpAwsCertificates) {
                List<AwsCertificate> list = awsCertificateMap.get(awsCertificate.getUserNo());
                if (list == null) {
                    list = new ArrayList<AwsCertificate>();
                }
                list.add(awsCertificate);
                awsCertificateMap.put(awsCertificate.getUserNo(), list);
            }

            Map<Long, List<VmwareKeyPair>> vmwareKeyPairMap = new LinkedHashMap<Long, List<VmwareKeyPair>>();
            String vmwareSql = "SELECT * FROM VMWARE_KEY_PAIR";
            List<VmwareKeyPair> tmpVmwareKeyPairs = SQLMain.selectExecuteWithResult(vmwareSql, VmwareKeyPair.class);
            for (VmwareKeyPair vmwareKeyPair: tmpVmwareKeyPairs) {
                List<VmwareKeyPair> list = vmwareKeyPairMap.get(vmwareKeyPair.getUserNo());
                if (list == null) {
                    list = new ArrayList<VmwareKeyPair>();
                }
                list.add(vmwareKeyPair);
                vmwareKeyPairMap.put(vmwareKeyPair.getUserNo(), list);
            }

            Map<Long, List<NiftyCertificate>> niftyCertificateMap = new LinkedHashMap<Long, List<NiftyCertificate>>();
            String niftySql = "SELECT * FROM NIFTY_CERTIFICATE";
            List<NiftyCertificate> tmpNiftyCertificates = SQLMain.selectExecuteWithResult(niftySql, NiftyCertificate.class);
            for (NiftyCertificate niftyCertificate: tmpNiftyCertificates) {
                List<NiftyCertificate> list = niftyCertificateMap.get(niftyCertificate.getUserNo());
                if (list == null) {
                    list = new ArrayList<NiftyCertificate>();
                }
                list.add(niftyCertificate);
                niftyCertificateMap.put(niftyCertificate.getUserNo(), list);
            }

            Map<Long, List<CloudstackCertificate>> cloudstackCertificateMap = new LinkedHashMap<Long, List<CloudstackCertificate>>();
            String csSql = "SELECT * FROM CLOUDSTACK_CERTIFICATE";
            List<CloudstackCertificate> tmpCloudstackCertificates = SQLMain.selectExecuteWithResult(csSql, CloudstackCertificate.class);
            for (CloudstackCertificate cloudstackCertificate: tmpCloudstackCertificates) {
                List<CloudstackCertificate> list = cloudstackCertificateMap.get(cloudstackCertificate.getAccount());
                if (list == null) {
                    list = new ArrayList<CloudstackCertificate>();
                }
                list.add(cloudstackCertificate);
                cloudstackCertificateMap.put(cloudstackCertificate.getAccount(), list);
            }

            Map<Long, List<VcloudCertificate>> vcloudCertificateMap = new LinkedHashMap<Long, List<VcloudCertificate>>();
            String vcSql = "SELECT * FROM VCLOUD_CERTIFICATE";
            List<VcloudCertificate> tmpVcloudCertificates = SQLMain.selectExecuteWithResult(vcSql, VcloudCertificate.class);
            for (VcloudCertificate vcloudCertificate: tmpVcloudCertificates) {
                List<VcloudCertificate> list = vcloudCertificateMap.get(vcloudCertificate.getUserNo());
                if (list == null) {
                    list = new ArrayList<VcloudCertificate>();
                }
                list.add(vcloudCertificate);
                vcloudCertificateMap.put(vcloudCertificate.getUserNo(), list);
            }

            for (User user : users) {
                List<String> columns = new ArrayList<String>();
                columns.add(user.getUsername());
                // アカウントの無効化チェック
                if(!StringUtils.startsWith(user.getPassword(), disablecode)){
                    columns.add("enable");
                }else{
                    columns.add("disable");
                }

                // TODO CLOUD BRANCHING
                StringBuilder sb = new StringBuilder();
                List<AwsCertificate> awsCertificates = awsCertificateMap.get(user.getUserNo());
                if (awsCertificates != null && !awsCertificates.isEmpty()) {
                    for (AwsCertificate awsCertificate : awsCertificates) {
                        Platform platform = platformMap.get(awsCertificate.getPlatformNo());
                        if ("aws".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                List<VmwareKeyPair> vmwareKeyPairs = vmwareKeyPairMap.get(user.getUserNo());
                if (vmwareKeyPairs != null && !vmwareKeyPairs.isEmpty()) {
                    for (VmwareKeyPair vmwareKeyPair : vmwareKeyPairs) {
                        Platform platform = platformMap.get(vmwareKeyPair.getPlatformNo());
                        if ("vmware".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                List<NiftyCertificate> niftyCertificates = niftyCertificateMap.get(user.getUserNo());
                if (niftyCertificates != null && !niftyCertificates.isEmpty()) {
                    for (NiftyCertificate niftyCertificate : niftyCertificates) {
                        Platform platform = platformMap.get(niftyCertificate.getPlatformNo());
                        if ("nifty".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                List<CloudstackCertificate> cloudstackCertificates = cloudstackCertificateMap.get(user.getUserNo());
                if (cloudstackCertificates != null && !cloudstackCertificates.isEmpty()) {
                    for (CloudstackCertificate cloudstackCertificate : cloudstackCertificates) {
                        Platform platform = platformMap.get(cloudstackCertificate.getPlatformNo());
                        if ("cloudstack".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                List<VcloudCertificate> vcloudCertificates = vcloudCertificateMap.get(user.getUserNo());
                if (vcloudCertificates != null && !vcloudCertificates.isEmpty()) {
                    for (VcloudCertificate vcloudCertificate : vcloudCertificates) {
                        Platform platform = platformMap.get(vcloudCertificate.getPlatformNo());
                        if ("vcloud".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                columns.add(sb.toString());
                for (String column : columns) {
                    System.out.print(StringUtils.rightPad(column, padSize, " "));
                }
                System.out.println();
            }
            log.info("ユーザ一覧を出力しました");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    public static void encryptUserPassword(String userPassword) {
        try {
            PasswordEncryptor passwordEncryptor = new PasswordEncryptor();

            //PCCシステム情報取得
            String selectSql = "SELECT * FROM PCC_SYSTEM_INFO";
            List<PccSystemInfo> systemInfos = SQLMain.selectExecuteWithResult(selectSql, PccSystemInfo.class);
            // PCCシステム情報のレコードが存在しない場合
            if (systemInfos.isEmpty()) {
                String insertSql = "INSERT INTO PCC_SYSTEM_INFO VALUES (?)";
                log.info("PCCシステム情報を作成しました");
                SQLMain.updateExecutePrepared(insertSql, new String[] {passwordEncryptor.keyGenerate()});
                systemInfos = SQLMain.selectExecuteWithResult(selectSql, PccSystemInfo.class);
            }
            PccSystemInfo systemInfo = systemInfos.get(0);

            // ユーザパスワード暗号化
            String encryptPass = passwordEncryptor.encrypt(userPassword, systemInfo.getSecretKey());

            System.out.print(encryptPass);
            log.info("ユーザパスワードを暗号化しました");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    public static void decryptUserPassword(String userPassword, String salt) {
        try {
            PasswordEncryptor passwordEncryptor = new PasswordEncryptor();

            // ユーザパスワード復号化
            String decryptPass = passwordEncryptor.decrypt(userPassword, salt);

            System.out.print(decryptPass);
            log.info("ユーザパスワードを復号化しました");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }
}
