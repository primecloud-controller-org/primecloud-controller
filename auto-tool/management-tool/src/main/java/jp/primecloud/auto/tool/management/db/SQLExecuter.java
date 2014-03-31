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
package jp.primecloud.auto.tool.management.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SQLExecuter {

    private DBConnector dbConnector;

    protected static Log log = LogFactory.getLog(SQLExecuter.class);

    public SQLExecuter(String url, String username, String password) {
        if (dbConnector == null) {
            this.dbConnector = new DBConnector(url, username, password);
        }
    }

    private static String passwordMask(String message) {
        Pattern pattern = Pattern.compile("PASSWORD='\\w*'");
        return pattern.matcher(message).replaceAll("PASSWORD='\\*\\*\\*\\*\\*'");

    }

    static public String escape(String input) {
        input = substitute(input, "'", "''");
        input = substitute(input, "\\", "\\\\");
        return input;
    }

    public static String substitute(String input, String pattern, String replacement) {
        // 置換対象文字列が存在する場所を取得
        int index = input.indexOf(pattern);

        // 置換対象文字列が存在しなければ終了
        if (index == -1) {
            return input;
        }

        // 処理を行うための StringBuffer
        StringBuffer buffer = new StringBuffer();

        buffer.append(input.substring(0, index) + replacement);

        if (index + pattern.length() < input.length()) {
            // 残りの文字列を再帰的に置換
            String rest = input.substring(index + pattern.length(), input.length());
            buffer.append(substitute(rest, pattern, replacement));
        }
        return buffer.toString();
    }

    public void execute(String sql) throws SQLException, Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String logSQL = "";
        // パスワードのマスク処理
        logSQL = passwordMask(sql);
        log.info("[" + logSQL + "] を実行します");
        try {
            con = dbConnector.getConnection();
            stmt = con.createStatement();
            stmt.execute(sql);
            log.info("[" + logSQL + "] を実行しました");
        } catch (SQLException e) {
            log.error(passwordMask(e.getMessage()), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(passwordMask(e.getMessage()), e);
            throw new Exception(e);
        } finally {
            try {
                dbConnector.closeConnection(con, stmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void executePrepared(String sql, String... params) throws SQLException, Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        // パスワードのマスク処理
        String logSQL = passwordMask(sql);
        log.info("[" + logSQL + "] を実行します");
        try {
            con = dbConnector.getConnection();
            ps = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            ps.execute();
            log.info("[" + logSQL + "] を実行しました");
        } catch (SQLException e) {
            log.error(passwordMask(e.getMessage()), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(passwordMask(e.getMessage()), e);
            throw new Exception(e);
        } finally {
            try {
                dbConnector.closeConnection(con, ps, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public int getNextid(String sql) throws SQLException, Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int nextid = 0;
        log.info("[" + sql + "] を実行します");
        try {
            con = dbConnector.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                nextid = rs.getInt("nextid");
            }
            log.info("[" + sql + "] を実行しました");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                dbConnector.closeConnection(con, stmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return nextid;
    }

    public int getGroupid(String sql) throws SQLException, Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int groupid = 0;
        log.info("[" + sql + "] を実行します");
        try {
            con = dbConnector.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                groupid = rs.getInt("groupid");
            }
            log.info("[" + sql + "] を実行しました");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);

            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                dbConnector.closeConnection(con, stmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return groupid;
    }

    public Object getColumn(String sql, String columnName, String columnType) throws SQLException, Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Object object = null;
        log.info("[" + sql + "] を実行します");
        try {
            con = dbConnector.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (columnType.equals("string")) {
                    object = rs.getString(columnName);
                } else if (columnType.equals("int")) {
                    object = rs.getInt(columnName);
                }
            }
            log.info("[" + sql + "] を実行しました");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                dbConnector.closeConnection(con, stmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return object;
    }

    public int getColumnAsInt(String sql, String columnName) throws SQLException, Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int number = 0;
        log.info("[" + sql + "] を実行します");
        try {
            con = dbConnector.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                number = rs.getInt(columnName);
            }
            log.info("[" + sql + "] を実行しました");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                dbConnector.closeConnection(con, stmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return number;
    }

    public List<List<Object>> showColumn(String sql) throws SQLException, Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        log.info("[" + sql + "] を実行します");
        List<List<Object>> results = new ArrayList<List<Object>>();
        try {
            con = dbConnector.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsMetaData = rs.getMetaData();

            int size = rsMetaData.getColumnCount();
            List<Object> columnNames = new ArrayList<Object>();
            for (int n = 1; n <= size; n++) {
                columnNames.add(rsMetaData.getColumnName(n));
            }
            results.add(columnNames);
            while (rs.next()) {
                List<Object> columns = new ArrayList<Object>();
                for (int i = 1; i <= size; i++) {
                    columns.add(rs.getObject(i));
                }
                results.add(columns);
            }
            log.info("[" + sql + "] を実行しました");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);

            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                dbConnector.closeConnection(con, stmt, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return results;
    }

}
