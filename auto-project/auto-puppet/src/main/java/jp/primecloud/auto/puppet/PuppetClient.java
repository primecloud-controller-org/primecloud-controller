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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.puppet.report.MetricsResource;
import jp.primecloud.auto.puppet.report.ReportAnalyzer;
import jp.primecloud.auto.puppet.report.ReportLoader;
import jp.primecloud.auto.util.CommandUtils;
import jp.primecloud.auto.util.CommandUtils.CommandResult;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class PuppetClient {

    private static final Log log = LogFactory.getLog(PuppetClient.class);

    protected String siteFile = "/etc/puppet/manifests/site.pp";

    protected Integer configTimeout;

    protected Integer delayTime = 10;

    protected File manifestDir;

    protected ReportLoader reportLoader;

    protected ReportAnalyzer reportAnalyzer;

    protected Semaphore runSemaphore = null;

    protected String puppetrunPath = "/usr/sbin/puppetrun";

    protected String puppetcaPath = "/usr/sbin/puppetca";

    public PuppetClient() {
        discover();
    }

    public void discover() {
        List<File> searchDirs = new ArrayList<File>();
        searchDirs.add(new File("/usr/sbin/"));
        searchDirs.add(new File("/usr/bin/"));

        // puppetrun
        for (File dir : searchDirs) {
            File file = new File(dir, "puppetrun");
            if (file.exists()) {
                puppetrunPath = file.getAbsolutePath();
                break;
            }
        }

        // puppetca
        for (File dir : searchDirs) {
            File file = new File(dir, "puppetca");
            if (file.exists()) {
                puppetcaPath = file.getAbsolutePath();
                break;
            }
        }
    }

    public void runClient(String fqdn) {
        // siteFileにtouchする
        touchFile(siteFile);

        // ノードのマニフェストにtouchする
        File manifestFile = new File(manifestDir, fqdn + ".pp");
        touchFile(manifestFile.getAbsolutePath());

        // キャッシュ対応ため一定時間待機する
        if (delayTime != null) {
            try {
                Thread.sleep(delayTime.intValue() * 1000);
            } catch (InterruptedException ignore) {
            }
        }

        // puppetrun実行前のレポートファイルの変更時刻を取得
        String beforeReportFile = reportLoader.getLatestReportFile(fqdn);
        Long beforeLastModified = null;
        if (beforeReportFile != null) {
            beforeLastModified = reportLoader.getLastModified(fqdn, beforeReportFile);
        }

        List<String> commands = new ArrayList<String>();
        commands.add("/usr/bin/sudo");
        commands.add(puppetrunPath);
        commands.add("--host");
        commands.add(fqdn);
        commands.add("--foreground"); // Puppetを同期的に実行する
        commands.add("--ping");

        // puppetrunのタイムアウト時間の設定
        if (configTimeout != null) {
            commands.add("--configtimeout=" + configTimeout.toString());
        }

        log.debug(commands);
        CommandResult result = executeRun(commands);

        int retryCount = 0;
        while (result.getExitValue() != 0 && retryCount < 6) {
            // リトライを行うかどうかのチェック
            boolean retry = false;
            for (String stdout : result.getStdouts()) {
                log.warn(stdout);
                if (stdout.contains("Certificates were not trusted: ")) {
                    retry = true;
                    break;
                } else if (stdout.contains("Connection reset by peer")) {
                    retry = true;
                    break;
                } else if (stdout.contains("Could not contact")) {
                    retry = true;
                    break;
                }
            }

            if (!retry) {
                // リトライしない場合
                break;
            }

            // 警告ログとして出力
            log.warn(ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));

            // 一定時間待機後に再実行
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException ignore) {
            }
            result = executeRun(commands);

            retryCount++;
        }

        if (result.getExitValue() != 0) {
            // puppetrunの実行に失敗
            AutoException exception;

            if (result.getStdouts().size() > 0 && result.getStdouts().get(0).contains("Could not contact")) {
                // 対象のホストにpingが届かない場合（ホストの異常終了時など）
                exception = new AutoException("EPUPPET-000002", fqdn);
            } else if (result.getStdouts().size() > 0 && result.getStdouts().get(0).contains("Could not connect")) {
                // パペットマスターの滞留により排他制御に不具合が生じたい場合、少し待って再実行
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException ignore) {
                }
                //マニュフェスト不正
                exception = new AutoException("EPUPPET-000007", fqdn);
            } else if (result.getStdouts().size() > 0 && result.getStdouts().get(0).contains("returned unknown answer")) {
                // パペットマスターの滞留により排他制御に不具合が生じたい場合、少し待って再実行(強調設定版)
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException ignore) {
                }
                //マニュフェスト不正
                exception = new AutoException("EPUPPET-000007", fqdn);
            } else {
                exception = new AutoException("EPUPPET-000001", fqdn);
            }

            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        // レポートファイルのチェック
        String reportFile = null;
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException ignore) {
            }

            String tmpReportFile = reportLoader.getLatestReportFile(fqdn);
            if (tmpReportFile != null) {
                if (beforeLastModified == null) {
                    reportFile = tmpReportFile;
                    break;
                } else {
                    Long lastModified = reportLoader.getLastModified(fqdn, tmpReportFile);
                    if (lastModified.longValue() != beforeLastModified.longValue()) {
                        reportFile = tmpReportFile;
                        break;
                    }
                }
            }
        }

        if (reportFile != null) {
            // レポートファイルが出力された場合
            Map<String, Object> report = reportLoader.loadReport(fqdn, reportFile);
            String status = reportAnalyzer.getStatus(report);
            if (status.equals("failed")) {
                // レポートファイルにマニフェスト適用失敗が記録されている場合(マニフェストコンパイルエラー)
                throw new AutoException("EPUPPET-000003", fqdn, reportFile);
            }
            List<MetricsResource> metricsResources = reportAnalyzer.getMetricsResources(report);
            for (MetricsResource metricsResource : metricsResources) {
                if (metricsResource.getName().startsWith("Failed") && metricsResource.getCount() > 0) {
                    // レポートファイルにマニフェスト適用失敗が記録されている場合
                    throw new AutoException("EPUPPET-000003", fqdn, reportFile);
                }
            }
        } else {
            // レポートファイルが出力されていない場合
            throw new AutoException("EPUPPET-000007", fqdn);
        }
    }

    protected CommandResult executeRun(List<String> commands) {
        if (runSemaphore == null) {
            return execute(commands);
        }

        //同時開始しないようにずらす（安全策）
        PuppetScheduler ps = PuppetScheduler.getInstance();
        ps.doStop();

        // パーミットを取得
        try {
            runSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            return execute(commands);
        } finally {
            // パーミットを解放
            runSemaphore.release();
        }
    }

    public List<String> listClients() {
        List<String> commands = new ArrayList<String>();
        commands.add("/usr/bin/sudo");
        commands.add(puppetcaPath);
        commands.add("-la");

        CommandResult result = execute(commands);

        List<String> clients = new ArrayList<String>();

        for (String stdout : result.getStdouts()) {
            if (stdout.startsWith("+ ")) {
                String host = stdout.substring(2);
                int index = host.indexOf(" ");
                if (index != -1) {
                    host = host.substring(0, index);
                }
                clients.add(host);
            }
        }

        return clients;
    }

    public void clearCa(String fqdn) {
        List<String> commands = new ArrayList<String>();
        commands.add("/usr/bin/sudo");
        commands.add(puppetcaPath);
        commands.add("-c");
        commands.add(fqdn);

        execute(commands);

        // 実行結果のチェックはしない
    }

    protected void touchFile(String file) {
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/touch");
        commands.add(file);
        CommandResult result = execute(commands);
        if (result.getExitValue() != 0) {
            // ファイルのtouchに失敗
            AutoException exception = new AutoException("EPUPPET-000004", file);
            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }
    }

    protected CommandResult execute(List<String> commands) {
        if (log.isDebugEnabled()) {
            log.debug(commands);
        }

        // タイムアウトしないようにする
        long timeout = Long.MAX_VALUE;

        CommandResult result = CommandUtils.execute(commands, timeout);

        if (log.isDebugEnabled()) {
            log.debug(ReflectionToStringBuilder.toString(result));
        }

        return result;
    }

    /**
     * siteFileを設定します。
     *
     * @param siteFile siteFile
     */
    public void setSiteFile(String siteFile) {
        this.siteFile = siteFile;
    }

    /**
     * configTimeoutを設定します。
     *
     * @param configTimeout configTimeout
     */
    public void setConfigTimeout(Integer configTimeout) {
        this.configTimeout = configTimeout;
    }

    /**
     * delayTimeを設定します。
     *
     * @param delayTime delayTime
     */
    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }

    /**
     * manifestDirを設定します。
     *
     * @param manifestDir manifestDir
     */
    public void setManifestDir(File manifestDir) {
        this.manifestDir = manifestDir;
    }

    /**
     * reportLoaderを設定します。
     *
     * @param reportLoader reportLoader
     */
    public void setReportLoader(ReportLoader reportLoader) {
        this.reportLoader = reportLoader;
    }

    /**
     * reportAnalyzerを設定します。
     *
     * @param reportAnalyzer reportAnalyzer
     */
    public void setReportAnalyzer(ReportAnalyzer reportAnalyzer) {
        this.reportAnalyzer = reportAnalyzer;
    }

    /**
     * runPermitsを設定します。
     *
     * @param runPermits runPermits
     */
    public void setRunPermits(Integer runPermits) {
        this.runSemaphore = runPermits == null ? null : new Semaphore(runPermits.intValue());
    }

    /**
     * puppetrunPathを設定します。
     *
     * @param puppetrunPath puppetrunPath
     */
    public void setPuppetrunPath(String puppetrunPath) {
        this.puppetrunPath = puppetrunPath;
    }

    /**
     * puppetcaPathを設定します。
     *
     * @param puppetcaPath puppetcaPath
     */
    public void setPuppetcaPath(String puppetcaPath) {
        this.puppetcaPath = puppetcaPath;
    }

}
