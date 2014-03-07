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
package jp.primecloud.auto.puppet.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;


/**
 * <p>
 * puppetrun実行時に出力されるYAML形式のファイルを扱うためのクラス
 * </p>
 *
 */
public class ReportLoader {

    /** ローカルログオブジェクト */
    private Log log = LogFactory.getLog(ReportLoader.class);

    /** レポートディレクトリのパス */
    protected File reportDir = new File("/var/lib/puppet/reports");

    /**
     * 指定ホストのListオブジェクト化されたYAMLファイル一覧の取得
     *
     * @param host ホスト名
     * @return Listオブジェクト化されたYAMLファイル名
     */
    public List<String> listReportFiles(String host) {
        // ホストごとのディレクトリ
        File hostDir = new File(reportDir, host);

        // ディレクトリが存在しない場合
        if (!hostDir.exists()) {
            return new ArrayList<String>();
        }

        // ホストごとのディレクトリ内で、拡張子が.yamlのファイルの名前のリストを作成
        List<String> list = new ArrayList<String>();
        for (File file : hostDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yaml")) {
                list.add(file.getName());
            }
        }

        // ファイル名の昇順でソート
        Collections.sort(list);

        return list;
    }

    /**
     * 最新レポートファイル名(YAML)の取得
     *
     * @param host ホスト名
     * @return 最新レポートファイル名(YAML)
     */
    public String getLatestReportFile(String host) {
        // 最新のレポートファイルを取得
        List<String> reportFiles = listReportFiles(host);

        if (reportFiles.isEmpty()) {
            // レポートファイルが存在しない場合
            return null;
        }

        String reportFile = reportFiles.get(reportFiles.size() - 1);

        return reportFile;
    }

    /**
     * 最新のYAMLをMapオブジェクトとして取得
     *
     * @param host ホスト名
     * @return Map化されたYAML
     */
    public Map<String, Object> loadLatestReport(String host) {
        String reportFile = getLatestReportFile(host);
        return loadReport(host, reportFile);
    }

    /**
     * 読込んだYAMLをMapとして取得
     *
     * @param host ホスト名
     * @param reportFile ファイル名
     * @return Map化されたYAML
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> loadReport(String host, String reportFile) {
        File file = new File(new File(reportDir, host), reportFile);

        if (!file.exists()) {
            // レポートファイルが存在しない場合
            log.warn(MessageUtils.getMessage("EPUPPET-000008", host, reportFile));
            return null;
        }

        // YAMLを文字列として取得
        String yamlText = getYamlText(file);

        // ロードしてオブジェクトに変換
        Yaml yaml = new Yaml();
        return (Map<String, Object>) yaml.load(yamlText);
    }

    /**
     * 指定ホストのレポートディレクトリを削除
     *
     * @param host ホスト名
     */
    public void deleteReportFiles(String host) {
        // ホストごとのディレクトリ
        File hostDir = new File(reportDir, host);

        // ディレクトリが存在しない場合
        if (!hostDir.exists()) {
            return;
        }

        // ディレクトリごと削除
        FileUtils.deleteQuietly(hostDir);
    }

    /**
     * 指定ホストに存在するファイルの最新更新時刻を取得
     *
     * @param host ホスト名
     * @param reportFile ファイル名
     * @return ファイルが最後に変更された時刻を返す
     */
    public Long getLastModified(String host, String reportFile) {
        File file = new File(new File(reportDir, host), reportFile);

        if (!file.exists()) {
            // レポートファイルが存在しない場合
            log.warn(MessageUtils.getMessage("EPUPPET-000008", host, reportFile));
            return null;
        }

        return file.lastModified();
    }

    /**
     * 指定したYAMLファイルの内容(String)を取得
     *
     * @param file ファイル名
     * @return YAMLファイル内の不要な文字列を取除いたString
     */
    protected String getYamlText(File file) {
        StringBuilder yamlText = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                // レポートのYAMLにRubyデータ型のタグがあれば取り除く
                int index = line.indexOf("!ruby");
                if (index == -1) {
                    index = line.indexOf("file:");
                    if (index == -1) {
                        yamlText.append(line);
                    }
                } else {
                    yamlText.append(line.substring(0, index));

                    int index2 = line.indexOf(" ", index);
                    if (index2 != -1) {
                        yamlText.append(line.substring(index2 + 1));
                    }
                }
                yamlText.append(lineSeparator);
            }
        } catch (IOException e) {
            // レポートファイルの読み込みに失敗
            throw new AutoException("EPUPPET-000005", e, file.getAbsolutePath());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
        }

        return yamlText.toString();
    }

    /**
     * reportDirを設定
     *
     * @param reportDir ディレクトリパス
     */
    public void setReportDir(File reportDir) {
        this.reportDir = reportDir;
    }

}
