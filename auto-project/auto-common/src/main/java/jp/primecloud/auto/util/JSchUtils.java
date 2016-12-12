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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jp.primecloud.auto.exception.AutoException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * <p>
 * SSHでコマンドを実行するためのユーティリティClassです。
 * </p>
 *
 */
public class JSchUtils {

    private JSchUtils() {
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param username
     * @param password
     * @param host
     * @return
     */
    public static Session createSession(String username, String password, String host) {
        return createSession(username, password, host, 22);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param username
     * @param password
     * @param host
     * @param port
     * @return
     */
    public static Session createSession(String username, String password, String host, int port) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            return session;
        } catch (JSchException e) {
            throw new AutoException("ECOMMON-000302", username, host, port);
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param username
     * @param privateKey
     * @param host
     * @return
     */
    public static Session createSessionByPrivateKey(String username, String privateKey, String host) {
        return createSessionByPrivateKey(username, privateKey, null, host, 22);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param username
     * @param privateKey
     * @param passphrase
     * @param host
     * @return
     */
    public static Session createSessionByPrivateKey(String username, String privateKey, String passphrase, String host) {
        return createSessionByPrivateKey(username, privateKey, passphrase, host, 22);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param username
     * @param privateKey
     * @param host
     * @param port
     * @return
     */
    public static Session createSessionByPrivateKey(String username, String privateKey, String host, int port) {
        return createSessionByPrivateKey(username, privateKey, null, host, port);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param username
     * @param privateKey
     * @param passphrase
     * @param host
     * @param port
     * @return
     */
    public static Session createSessionByPrivateKey(String username, String privateKey, String passphrase, String host,
            int port) {
        Charset charset = Charset.forName("UTF-8");
        byte[] privateKeyBytes = privateKey.getBytes(charset);
        byte[] passphraseBytes = null;
        if (passphrase != null) {
            passphraseBytes = passphrase.getBytes(charset);
        }

        try {
            JSch jsch = new JSch();
            jsch.addIdentity("name", privateKeyBytes, null, passphraseBytes);
            Session session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            return session;
        } catch (JSchException e) {
            throw new AutoException("ECOMMON-000302", username, host, port);
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param session
     * @param command
     * @return
     */
    public static JSchResult executeCommand(Session session, String command) {
        return executeCommand(session, command, null);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param session
     * @param command
     * @param encoding
     * @return
     */
    public static JSchResult executeCommand(Session session, String command, String encoding) {
        return executeCommand(session, command, encoding, 10000);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param session
     * @param command
     * @param timeout
     * @return
     */
    public static JSchResult executeCommand(Session session, String command, long timeout) {
        return executeCommand(session, command, null, timeout);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param session
     * @param command
     * @param encoding
     * @param timeout
     * @return
     */
    public static JSchResult executeCommand(Session session, String command, String encoding, long timeout) {
        return executeCommand(session, command, encoding, timeout, false);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param session
     * @param command
     * @param encoding
     * @param timeout
     * @param pty
     * @return
     */
    public static JSchResult executeCommand(Session session, String command, String encoding, long timeout, boolean pty) {
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setPty(pty);

            // 出力Streamの設定
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            channel.setOutputStream(outStream);

            ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            channel.setErrStream(errStream);

            ByteArrayOutputStream extOutStream = new ByteArrayOutputStream();
            channel.setExtOutputStream(extOutStream);

            // コマンド実行
            channel.connect();

            // コマンドが終了するまで待機
            long startTime = System.currentTimeMillis();
            while (!channel.isClosed()) {
                try {
                    if (System.currentTimeMillis() - startTime > timeout) {
                        // タイムアウト発生時
                        throw new AutoException("ECOMMON-000301", command);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
            }

            // 実行結果の取得
            JSchResult result = new JSchResult();
            result.setExitStatus(channel.getExitStatus());
            if (encoding == null) {
                result.setOut(outStream.toString());
                result.setErr(errStream.toString());
                result.setExtOut(extOutStream.toString());
            } else {
                result.setOut(outStream.toString(encoding));
                result.setErr(errStream.toString(encoding));
                result.setExtOut(extOutStream.toString(encoding));
            }

            return result;
        } catch (JSchException e) {
            throw new AutoException("ECOMMON-000303", e, command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param session
     * @param input
     * @param targetFile
     */
    public static void sftpPut(Session session, InputStream input, String targetFile) {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            // Putを実行
            channel.put(input, targetFile, ChannelSftp.OVERWRITE);

            channel.quit();

        } catch (JSchException e) {
            throw new AutoException("ECOMMON-000304", e);
        } catch (SftpException e) {
            throw new AutoException("ECOMMON-000304", e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public static class JSchResult {

        private String out;

        private String err;

        private String extOut;

        private int exitStatus;

        /**
         * outを取得します。
         *
         * @return out
         */
        public String getOut() {
            return out;
        }

        /**
         * outを設定します。
         *
         * @param out out
         */
        public void setOut(String out) {
            this.out = out;
        }

        /**
         * errを取得します。
         *
         * @return err
         */
        public String getErr() {
            return err;
        }

        /**
         * errを設定します。
         *
         * @param err err
         */
        public void setErr(String err) {
            this.err = err;
        }

        /**
         * extOutを取得します。
         *
         * @return extOut
         */
        public String getExtOut() {
            return extOut;
        }

        /**
         * extOutを設定します。
         *
         * @param extOut extOut
         */
        public void setExtOut(String extOut) {
            this.extOut = extOut;
        }

        /**
         * exitStatusを取得します。
         *
         * @return exitStatus
         */
        public int getExitStatus() {
            return exitStatus;
        }

        /**
         * exitStatusを設定します。
         *
         * @param exitStatus exitStatus
         */
        public void setExitStatus(int exitStatus) {
            this.exitStatus = exitStatus;
        }

    }

}
