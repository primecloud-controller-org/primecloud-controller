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

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;

public class ConfigMain {

    protected static Log log = LogFactory.getLog(ConfigMain.class);

    public static final int PAD_SIZE = 20;

    public static void showPlatforms() {
        try {
            String sql = "SELECT * FROM PLATFORM";
            List<Platform> platforms = SQLMain.selectExecuteWithResult(sql, Platform.class);
            if (platforms.isEmpty()) {
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
        }
    }

    public static void getPlatformNo(String platformName, String platformKind) {
        try {
            String platformSql = "SELECT * FROM PLATFORM";
            List<Platform> platforms = SQLMain.selectExecuteWithResult(platformSql, Platform.class);
            if (platforms == null || platforms.isEmpty()) {
                System.out.println("NULL");
                return;
            }
            for (Platform platform : platforms) {
                if (StringUtils.equals(platform.getPlatformName(), platformName)) {
                    if (BooleanUtils.isNotTrue(platform.getSelectable())) {
                        //使用不可なプラットフォーム
                        System.out.println("DISABLE");
                        return;
                    }

                    String platformAwsSql = "SELECT * FROM PLATFORM_AWS WHERE PLATFORM_NO=" + platform.getPlatformNo();
                    List<PlatformAws> platformAwses = SQLMain.selectExecuteWithResult(platformAwsSql, PlatformAws.class);
                    if ("ec2".equals(platformKind) && "aws".equals(platform.getPlatformType()) &&
                        BooleanUtils.isFalse(platformAwses.get(0).getEuca())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    } else if ("vmware".equals(platformKind) && "vmware".equals(platform.getPlatformType())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    } else if ("eucalyptus".equals(platformKind) && "aws".equals(platform.getPlatformType()) &&
                               BooleanUtils.isTrue(platformAwses.get(0).getEuca())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    } else if ("nifty".equals(platformKind) && "nifty".equals(platform.getPlatformType())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    } else if ("cloudstack".equals(platformKind) && "cloudstack".equals(platform.getPlatformType())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    } else if ("vcloud".equals(platformKind) && "vcloud".equals(platform.getPlatformType())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    } else if ("azure".equals(platformKind) && "azure".equals(platform.getPlatformType())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    } else if ("openstack".equals(platformKind) && "openstack".equals(platform.getPlatformType())) {
                        System.out.println(platform.getPlatformNo().toString());
                        return;
                    }
                }
            }
            System.out.println("OTHER");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
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
