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
package jp.primecloud.auto.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.exception.AutoException;

/**
 * <p>
 * OSのコマンドを実行するためのユーティリティClassです。
 * </p>
 *
 */
public class CommandUtils {

    private CommandUtils() {
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param commands
     * @return
     */
    public static CommandResult execute(List<String> commands) {
        return execute(commands, 10000);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param commands
     * @param timeout
     * @return
     */
    public static CommandResult execute(List<String> commands, long timeout) {
        return execute(commands, null, timeout);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param commands
     * @param stdins
     * @return
     */
    public static CommandResult execute(List<String> commands, List<String> stdins) {
        return execute(commands, stdins, 10000);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param commands
     * @param stdins
     * @param timeout
     * @return
     */
    public static CommandResult execute(List<String> commands, List<String> stdins, long timeout) {
        try {
            return executeCommand(commands, stdins, timeout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static CommandResult executeCommand(List<String> commands, List<String> stdins, long timeout)
            throws IOException {
        // Process開始
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        // WatchDogタイマー実行
        WatchDog watchDog = new WatchDog(process, timeout);
        Thread thread = new Thread(watchDog);
        thread.start();

        // 標準入力へ書き込み
        if (stdins != null && stdins.size() > 0) {
            OutputStream out = process.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            for (String stdin : stdins) {
                writer.write(stdin);
                writer.newLine();
            }
            closeQuietly(writer);
            closeQuietly(out);
        }

        // プロセス終了まで待機
        try {
            process.waitFor();
            thread.interrupt();
        } catch (InterruptedException ignore) {
        }

        // タイムアウト発生時
        if (watchDog.isTimeout) {
            AutoException exception = new AutoException("ECOMMON-000301", commands);
            exception.addDetailInfo("stdins=" + stdins);
            throw exception;
        }

        // 標準出力の読み込み
        InputStream in = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> stdouts = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            stdouts.add(line);
        }
        closeQuietly(reader);
        closeQuietly(in);

        // 実行結果
        CommandResult result = new CommandResult();
        result.setExitValue(process.exitValue());
        result.setStdouts(stdouts);

        return result;
    }

    protected static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static class CommandResult {

        private List<String> stdouts;

        private int exitValue;

        public List<String> getStdouts() {
            return stdouts;
        }

        public void setStdouts(List<String> stdouts) {
            this.stdouts = stdouts;
        }

        public int getExitValue() {
            return exitValue;
        }

        public void setExitValue(int exitValue) {
            this.exitValue = exitValue;
        }

    }

    protected static class WatchDog implements Runnable {

        protected Process process;

        protected long timeout;

        protected boolean isTimeout;

        public WatchDog(Process process, long timeout) {
            this.process = process;
            this.timeout = timeout;
            isTimeout = false;
        }

        public void run() {
            try {
                Thread.sleep(timeout);

                // タイムアウト時
                process.destroy();
                isTimeout = true;
            } catch (InterruptedException ignore) {
            }
        }

    }

}
