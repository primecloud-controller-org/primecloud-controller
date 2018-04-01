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
package jp.primecloud.auto.puppet;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.util.CommandUtils.CommandResult;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockPuppetClient extends PuppetClient {

    @Override
    protected CommandResult execute(List<String> commands) {
        for (String command : commands) {
            System.out.println("Command: " + command);
        }
        CommandResult result = new CommandResult();
        result.setExitValue(0);
        result.setStdouts(new ArrayList<String>());
        return result;
    }

}
