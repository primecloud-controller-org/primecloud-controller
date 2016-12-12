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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;

import jp.primecloud.auto.util.JSchUtils;
import jp.primecloud.auto.util.JSchUtils.JSchResult;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;

import com.jcraft.jsch.Session;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class JSchUtilsTest {

    @Test
    @Ignore
    public void testExecuteCommand1() {
        // パスワード認証
        Session session = JSchUtils.createSession("user01", "passw0rd", "172.22.0.23");

        JSchResult result = JSchUtils.executeCommand(session, "env", "UTF-8");
        assertEquals(0, result.getExitStatus());

        result = JSchUtils.executeCommand(session, "ls -l /etc", "UTF-8");
        assertEquals(0, result.getExitStatus());

        session.disconnect();
    }

    @Test
    @Ignore
    public void testExecuteCommand2() throws Exception {
        // 鍵認証（パスフレーズなし）
        File privateKeyFile = new ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "key01.pem"))
                .getFile();
        String privateKey = FileUtils.readFileToString(privateKeyFile, "UTF-8");

        Session session = JSchUtils.createSessionByPrivateKey("user01", privateKey, "172.22.0.23");

        JSchResult result = JSchUtils.executeCommand(session, "env", "UTF-8");
        assertEquals(0, result.getExitStatus());

        result = JSchUtils.executeCommand(session, "ls -l /etc", "UTF-8");
        assertEquals(0, result.getExitStatus());

        session.disconnect();
    }

    @Test
    @Ignore
    public void testExecuteCommand3() throws Exception {
        // 鍵認証（パスフレーズあり）
        File privateKeyFile = new ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "key02.pem"))
                .getFile();
        String privateKey = FileUtils.readFileToString(privateKeyFile, "UTF-8");

        Session session = JSchUtils.createSessionByPrivateKey("user01", privateKey, "passw0rd", "172.22.0.23");

        JSchResult result = JSchUtils.executeCommand(session, "env", "UTF-8");
        assertEquals(0, result.getExitStatus());

        result = JSchUtils.executeCommand(session, "ls -l /etc", "UTF-8");
        assertEquals(0, result.getExitStatus());

        session.disconnect();
    }

    @Test
    @Ignore
    public void testSftpPut() throws Exception {
        Session session = JSchUtils.createSession("user01", "passw0rd", "172.22.0.23");

        ByteArrayInputStream input = new ByteArrayInputStream("abcde".getBytes());
        JSchUtils.sftpPut(session, input, "/home/user01/test");

        session.disconnect();
    }

}
