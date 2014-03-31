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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.dao.crud.PlatformAwsDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;

public class ConfigMain {

    protected static Log log = LogFactory.getLog(ConfigMain.class);

    public static final int PAD_SIZE = 20;

    public static void showPlatforms() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        try {
            PlatformDao platformDao = (PlatformDao) context.getBean("platformDao");
            List<Platform> platforms = platformDao.readAll();
            if (platforms == null || platforms.isEmpty()) {
                System.out.println("使用可能なプラットフォームがありません");
                return;
            }
            StringBuilder titles = new StringBuilder();
            titles.append(StringUtils.rightPad("PlatformName", PAD_SIZE, " "));
            titles.append(StringUtils.rightPad("Status", PAD_SIZE, " "));
            System.out.println(titles.toString());
            for (Platform platform : platforms) {
                List<String> columns = new ArrayList<String>();
                columns.add(platform.getPlatformName());
                // プラットフォームの無効化チェック
                if(BooleanUtils.isTrue(platform.getSelectable())) {
                    columns.add("enable");
                }else{
                    columns.add("disable");
                }

                for (String column : columns) {
                    System.out.print(StringUtils.rightPad(column, PAD_SIZE, " "));
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            context.destroy();
        }
    }

//    public static boolean hasPlatform(Long platformNo) {
//        try  {
//            List<Platform> platforms = Config.getPlatforms();
//            if (platforms == null || platforms.isEmpty()) {
//                return false;
//            }
//            for (Platform platform : platforms) {
//                if (platform.getNo() == platformNo.longValue()) {
//                    return true;
//                }
//            }
//        } catch {
//
//        }
//        return false;
//    }

    public static void getPlatformNo(String platformName, String platformKind) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        try {
            PlatformDao platformDao = (PlatformDao) context.getBean("platformDao");
            PlatformAwsDao platformAwsDao = (PlatformAwsDao) context.getBean("platformAwsDao");

            List<Platform> platforms = platformDao.readAll();
            if (platforms == null || platforms.isEmpty()) {
                System.out.println("NULL");
            }
            for (Platform platform : platforms) {
                if (platform.getPlatformName().equals(platformName)) {
                    if (BooleanUtils.isNotTrue(platform.getSelectable())) {
                        //使用不可なプラットフォーム
                        System.out.println("DISABLE");
                        return;
                    }

                    PlatformAws platformAws = platformAwsDao.read(platform.getPlatformNo());
                    if (platformKind.equals("ec2") && "aws".equals(platform.getPlatformType()) && platformAws.getEuca() == false) {
                        System.out.println(Long.toString(platform.getPlatformNo()));
                        return;
                    } else if (platformKind.equals("vmware") && "vmware".equals(platform.getPlatformType())) {
                        System.out.println(Long.toString(platform.getPlatformNo()));
                        return;
                    } else if (platformKind.equals("eucalyptus") && "aws".equals(platform.getPlatformType()) && platformAws.getEuca()) {
                        System.out.println(Long.toString(platform.getPlatformNo()));
                        return;
                    } else if (platformKind.equals("nifty") && "nifty".equals(platform.getPlatformType())) {
                        System.out.println(Long.toString(platform.getPlatformNo()));
                        return;
                    } else if (platformKind.equals("cloudstack") && "cloudstack".equals(platform.getPlatformType())) {
                        System.out.println(Long.toString(platform.getPlatformNo()));
                        return;
                    }
                }
            }
            System.out.println("OTHER");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            context.destroy();
        }
    }

    public static void getProperty(String key) {
        String property = Config.getProperty(key);
        if (StringUtils.isEmpty(property)) {
            System.out.println("NULL");
        } else {
            System.out.println(property);
        }
    }

}
