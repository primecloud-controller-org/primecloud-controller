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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.tool.management.db.SQLExecuter;
import jp.primecloud.auto.tool.management.db.SQLExecuterFactory;

public class SQLMain {

    protected static Log log = LogFactory.getLog(SQLMain.class);

    public static void selectExecute(CommandLine commandLine) {
        String sql = commandLine.getOptionValue("sql");

        SQLExecuter sqlExecuter = new SQLExecuterFactory().createPCCSQLExecuter();

        if (commandLine.hasOption("columntype") && commandLine.hasOption("columnname")) {
            String columnName = commandLine.getOptionValue("columnname");
            String columnType = commandLine.getOptionValue("columntype");

            try {
                Object result = sqlExecuter.getColumn(sql, columnName, columnType);
                if (result == null) {
                    System.out.println("NULL");
                } else {
                    System.out.print(result.toString());
                }
            } catch (Exception e) {
                log.error("[" + sql + "] の実行に失敗しました", e);
                System.out.println("[" + sql + "] の実行に失敗しました");
                return;
            }
        } else {
            try {
                List<List<Object>> results = sqlExecuter.showColumn(sql);
                for (List<Object> columns : results) {
                    for (Object object : columns) {
                        if (object == null) {
                            object = "NULL";
                        }
                        System.out.print(object.toString() + " ");
                    }
                    System.out.println();
                }
            } catch (Exception e) {
                log.error("[" + sql + "] の実行に失敗しました", e);
                System.out.println("[" + sql + "] の実行に失敗しました");
                return;
            }
        }
    }

    public static void updateExecute(CommandLine commandLine) {

        String sql = commandLine.getOptionValue("sql");
        SQLExecuter sqlExecuter = new SQLExecuterFactory().createPCCSQLExecuter();

        try {
            sqlExecuter.execute(sql);
        } catch (Exception e) {
            log.error("[" + sql + "] の実行に失敗しました", e);
            System.out.println("[" + sql + "] の実行に失敗しました");
            return;
        }

    }

    public static void updateExecutePrepared(CommandLine commandLine) {
        String sql = commandLine.getOptionValue("sql");

        SQLExecuter sqlExecuter = new SQLExecuterFactory().createPCCSQLExecuter();
        String[] params = commandLine.getOptionValues("prepared");

        try {
            sqlExecuter.executePrepared(sql, params);
        } catch (Exception e) {
            log.error("[" + sql + "] の実行に失敗しました", e);
            System.out.println("[" + sql + "] の実行に失敗しました");
            return;
        }

    }
}
