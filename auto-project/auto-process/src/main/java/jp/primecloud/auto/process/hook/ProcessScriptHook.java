/*
 * Copyright 2016 by SCSK Corporation.
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
package jp.primecloud.auto.process.hook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.CommandUtils;
import jp.primecloud.auto.util.CommandUtils.CommandResult;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * <p>
 * 各種操作のフック処理のスクリプトを実行するクラスです。
 * </p>
 */
public class ProcessScriptHook extends ServiceSupport implements ProcessHook {

    protected String scriptExt;

    public ProcessScriptHook() {
        // OSによってフックスクリプトファイルの拡張子を決める
        String osName = System.getProperty("os.name");
        if ("Windows".equalsIgnoreCase(osName)) {
            scriptExt = ".bat";
        } else {
            scriptExt = ".sh";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String hookName, Object... args) {
        String scriptDir = Config.getProperty("hook.scriptDir");
        File scriptFile = new File(scriptDir, hookName + scriptExt);

        // スクリプトファイルが存在しなければ何もしない
        if (!scriptFile.exists()) {
            return;
        }

        List<String> commands = new ArrayList<String>();
        commands.add(scriptFile.getAbsolutePath());
        if (args != null) {
            for (Object arg : args) {
                String str = (arg == null) ? "" : arg.toString();
                commands.add(str);
            }
        }

        try {
            // フック処理のスクリプト実行
            CommandResult result = CommandUtils.execute(commands, 60 * 1000L);

            if (result.getExitValue() != 0) {
                // フック処理のスクリプト実行に失敗
                AutoException exception = new AutoException("EPROCESS-000701", hookName);
                exception.addDetailInfo("commands=" + commands);
                exception.addDetailInfo(
                        "result=" + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
                throw exception;
            }
        } catch (Exception e) {
            // XXX: フック処理におけるエラーはPCCの範囲外であるため、エラーが発生しても例外をスローしない
            log.error(e.getMessage(), e);
        }
    }

    public void setScriptExt(String scriptExt) {
        this.scriptExt = scriptExt;
    }

}
