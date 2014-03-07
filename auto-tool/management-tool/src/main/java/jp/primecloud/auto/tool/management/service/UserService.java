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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.dao.crud.AwsCertificateDao;
import jp.primecloud.auto.dao.crud.CloudstackCertificateDao;
import jp.primecloud.auto.dao.crud.NiftyCertificateDao;
import jp.primecloud.auto.dao.crud.PccSystemInfoDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.UserDao;
import jp.primecloud.auto.dao.crud.VmwareKeyPairDao;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.CloudstackCertificate;
import jp.primecloud.auto.entity.crud.NiftyCertificate;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;

public class UserService {

    protected static Log log = LogFactory.getLog(UserService.class);

    public static int padSize = 20;

    public static void showUserPlatform() {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        try {
            UserDao userDao = (UserDao) context.getBean("userDao");
            List<User> users = userDao.readAll();
            AwsCertificateDao awsCertificateDao = (AwsCertificateDao) context.getBean("awsCertificateDao");
            VmwareKeyPairDao vmwareKeyPairDao = (VmwareKeyPairDao) context.getBean("vmwareKeyPairDao");
            NiftyCertificateDao niftyCertificateDao = (NiftyCertificateDao) context.getBean("niftyCertificateDao");
            CloudstackCertificateDao cloudstackCertificateDao = (CloudstackCertificateDao) context.getBean("cloudstackCertificateDao");
            PlatformDao platformDao = (PlatformDao) context.getBean("platformDao");

            StringBuilder titles = new StringBuilder();
            titles.append(StringUtils.rightPad("Username", padSize, " "));
            titles.append(StringUtils.rightPad("Status", padSize, " "));
            titles.append(StringUtils.rightPad("Platform", padSize, " "));
            System.out.println(titles.toString());
            String disablecode = Config.getProperty("DISABLE_CODE");

            for (User user : users) {
                List<String> columns = new ArrayList<String>();
                columns.add(user.getUsername());
                // アカウントの無効化チェック
                if(!StringUtils.startsWith(user.getPassword(), disablecode)){
                    columns.add("enable");
                }else{
                    columns.add("disable");
                }

                StringBuilder sb = new StringBuilder();
                List<AwsCertificate> awsCertificates = awsCertificateDao.readByUserNo(user.getUserNo());
                if (!awsCertificates.isEmpty()) {
                    for (AwsCertificate awsCertificate : awsCertificates) {
                        Platform platform = platformDao.read(awsCertificate.getPlatformNo());
                        if ("aws".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                List<VmwareKeyPair> vmwareKeyPairs = vmwareKeyPairDao.readByUserNo(user.getUserNo());
                if (!vmwareKeyPairs.isEmpty()) {
                    Set<Long> platformNos = new LinkedHashSet<Long>();
                    for (VmwareKeyPair vmwareKeyPair : vmwareKeyPairs) {
                        platformNos.add(vmwareKeyPair.getPlatformNo());
                    }
                    for (Long platformNo : platformNos) {
                        Platform platform = platformDao.read(platformNo);
                        if ("vmware".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                List<NiftyCertificate> niftyCertificates = niftyCertificateDao.readByUserNo(user.getUserNo());
                if (!niftyCertificates.isEmpty()) {
                    Set<Long> platformNos = new LinkedHashSet<Long>();
                    for (NiftyCertificate niftyCertificate : niftyCertificates) {
                        platformNos.add(niftyCertificate.getPlatformNo());
                    }
                    for (Long platformNo : platformNos) {
                        Platform platform = platformDao.read(platformNo);
                        if ("nifty".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
                            sb.append(platform.getPlatformName());
                            sb.append(" ");
                        }
                    }
                }

                List<CloudstackCertificate> cloudstackCertificates = cloudstackCertificateDao.readByAccount(user.getUserNo());
                if (!cloudstackCertificates.isEmpty()) {
                    Set<Long> platformNos = new LinkedHashSet<Long>();
                    for (CloudstackCertificate cloudstackCertificate : cloudstackCertificates) {
                        platformNos.add(cloudstackCertificate.getPlatformNo());
                    }
                    for (Long platformNo : platformNos) {
                        Platform platform = platformDao.read(platformNo);
                        if ("cloudstack".equals(platform.getPlatformType()) && BooleanUtils.isTrue(platform.getSelectable())) {
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
        } catch (BeansException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            context.destroy();
        }
    }

    public static void encryptUserPassword(String userPassword) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        try {
            PasswordEncryptor passwordEncryptor = new PasswordEncryptor();

            //PCCシステム情報取得
            PccSystemInfoDao pccSystemInfoDao = (PccSystemInfoDao) context.getBean("pccSystemInfoDao");
            PccSystemInfo pccSystemInfo = pccSystemInfoDao.read();

            // PCCシステム情報のレコードが存在しない場合
            if (pccSystemInfo == null) {
                PccSystemInfo newPccSystemInfo = new PccSystemInfo();
                newPccSystemInfo.setSecretKey(passwordEncryptor.keyGenerate());
                pccSystemInfoDao.create(newPccSystemInfo);
                log.info("PCCシステム情報を作成しました");
                pccSystemInfo = pccSystemInfoDao.read();
            }

            // ユーザパスワード暗号化
            String secretKey = pccSystemInfo.getSecretKey();
            String encryptPass = passwordEncryptor.encrypt(userPassword, secretKey);

            System.out.print(encryptPass);
            log.info("ユーザパスワードを暗号化しました");

        } catch (BeansException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            context.destroy();
        }
    }
}
