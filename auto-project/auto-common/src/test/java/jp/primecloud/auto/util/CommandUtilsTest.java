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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.util.CommandUtils.CommandResult;

import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class CommandUtilsTest {

    @Test
    @Ignore
    public void testExecute() {
        List<String> commands = new ArrayList<String>();
        commands.add("C:\\WINDOWS\\system32\\ping.exe");
        commands.add("localhost");

        CommandResult result = CommandUtils.execute(commands);

        assertEquals(0, result.getExitValue());
        assertTrue(result.getStdouts().size() > 0);
    }

    @Test
    @Ignore
    public void testExecute2() {
        List<String> commands = new ArrayList<String>();
        commands.add("C:\\WINDOWS\\system32\\ping.exe");
        commands.add("localhost");

        try {
            CommandUtils.execute(commands, 1000);
            fail();
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("timeout"));
        }
    }

    @Test
    @Ignore
    public void testExecute3() {
        List<String> commands = new ArrayList<String>();
        commands.add("C:\\WINDOWS\\system32\\nslookup.exe");

        List<String> stdins = new ArrayList<String>();
        stdins.add("exit");

        CommandResult result = CommandUtils.execute(commands, stdins);

        assertEquals(0, result.getExitValue());
        assertTrue(result.getStdouts().size() > 0);
    }

}
