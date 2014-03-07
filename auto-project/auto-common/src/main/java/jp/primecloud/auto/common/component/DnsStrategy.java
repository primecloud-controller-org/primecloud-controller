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
package jp.primecloud.auto.common.component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.CommandUtils;
import jp.primecloud.auto.util.CommandUtils.CommandResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * DNSサーバのレコードを制御するClassです。
 * </p>
 *
 */
public class DnsStrategy {

    private static final Log log = LogFactory.getLog(DnsStrategy.class);

    private String dnsServer;

    private String domainName;

    private int timeToLive = 3600;

    /**
     * TODO: メソッドコメントを記述
     *
     * @param hostName
     * @return
     */
    public String createFqdn(String hostName) {
        if (domainName == null) {
            return hostName;
        }
        return hostName + "." + domainName;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     * @param ipAddress
     */
    public void addForward(String fqdn, String ipAddress) {
        List<String> commands = createCommands();

        List<String> stdins = createStdinsCommon();
        stdins.addAll(createAddForward(fqdn, ipAddress));
        stdins.add("quit");

        CommandResult result = execute(commands, stdins);

        if (result.getExitValue() != 0) {
            // 正引きレコードの追加に失敗
            AutoException exception = new AutoException("ECOMMON-000201", fqdn, ipAddress);
            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        // 正引きの確認
        long timeout = 10000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            String hostAddress = getHostAddress(fqdn);
            if (StringUtils.equals(ipAddress, hostAddress)) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時
                throw new AutoException("ECOMMON-000202", fqdn, ipAddress, hostAddress);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     * @param ipAddress
     */
    public void addReverse(String fqdn, String ipAddress) {
        List<String> commands = createCommands();

        List<String> stdins = createStdinsCommon();
        stdins.addAll(createAddReverse(fqdn, ipAddress));
        stdins.add("quit");

        CommandResult result = execute(commands, stdins);

        if (result.getExitValue() != 0) {
            // 逆引きレコードの追加に失敗
            AutoException exception = new AutoException("ECOMMON-000203", fqdn, ipAddress);
            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        // 逆引きの確認
        long timeout = 10000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            String hostName = getHostName(ipAddress);
            if (StringUtils.equals(fqdn, hostName)) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時
                throw new AutoException("ECOMMON-000204", ipAddress, fqdn, hostName);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     * @param canonicalName
     */
    public void addCanonicalName(String fqdn, String canonicalName) {
        List<String> commands = createCommands();

        List<String> stdins = createStdinsCommon();
        stdins.addAll(createAddCanonicalName(fqdn, canonicalName));
        stdins.add("quit");

        CommandResult result = execute(commands, stdins);

        if (result.getExitValue() != 0) {
            // CNAMEレコードの追加に失敗
            AutoException exception = new AutoException("ECOMMON-000205", fqdn, canonicalName);
            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        // CNAMEの確認はしない（参照先ホスト名を解決できない場合もあるため）
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     */
    public void deleteForward(String fqdn) {
        List<String> commands = createCommands();

        List<String> stdins = createStdinsCommon();
        stdins.addAll(createDeleteForward(fqdn));
        stdins.add("quit");

        CommandResult result = execute(commands, stdins);

        if (result.getExitValue() != 0) {
            // 正引きレコードの削除に失敗
            AutoException exception = new AutoException("ECOMMON-000207", fqdn);
            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        // 正引きの確認
        long timeout = 10000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            String hostAddress = getHostAddress(fqdn);
            if (hostAddress == null) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時
                throw new AutoException("ECOMMON-000208", fqdn, hostAddress);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param ipAddress
     */
    public void deleteReverse(String ipAddress) {
        List<String> commands = createCommands();

        List<String> stdins = createStdinsCommon();
        stdins.addAll(createDeleteReverse(ipAddress));
        stdins.add("quit");

        CommandResult result = execute(commands, stdins);

        if (result.getExitValue() != 0) {
            // 逆引きレコードの削除に失敗
            AutoException exception = new AutoException("ECOMMON-000209", ipAddress);
            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        // 逆引きの確認
        long timeout = 10000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            String hostName = getHostName(ipAddress);
            if (StringUtils.equals(ipAddress, hostName)) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時
                throw new AutoException("ECOMMON-000210", ipAddress, hostName);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param fqdn
     */
    public void deleteCanonicalName(String fqdn) {
        List<String> commands = createCommands();

        List<String> stdins = createStdinsCommon();
        stdins.addAll(createDeleteCanonicalName(fqdn));
        stdins.add("quit");

        CommandResult result = execute(commands, stdins);

        if (result.getExitValue() != 0) {
            // CNAMEレコードの削除に失敗
            AutoException exception = new AutoException("ECOMMON-000211", fqdn);
            exception.addDetailInfo("result="
                    + ReflectionToStringBuilder.toString(result, ToStringStyle.SHORT_PREFIX_STYLE));
            throw exception;
        }

        // CNAMEの確認
        long timeout = 10000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            String cname = getCanonicalName(fqdn);
            if (cname == null) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時
                throw new AutoException("ECOMMON-000212", fqdn, cname);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    /**
     * 実行するコマンドを作成します。
     *
     * @return
     */
    protected List<String> createCommands() {
        List<String> commands = new ArrayList<String>();
        commands.add("/usr/bin/nsupdate");
        commands.add("-d");
        return commands;
    }

    /**
     * 標準入力に与える内容の共通部分を作成します。
     *
     * @return
     */
    protected List<String> createStdinsCommon() {
        List<String> stdins = new ArrayList<String>();
        stdins.add("server " + dnsServer);
        return stdins;
    }

    protected List<String> createAddForward(String fqdn, String ipAddress) {
        List<String> in = new ArrayList<String>();
        in.add("update delete " + fqdn + " IN A");
        in.add("update add " + fqdn + " " + timeToLive + " IN A " + ipAddress);
        in.add("send");
        return in;
    }

    protected List<String> createAddReverse(String fqdn, String ipAddress) {
        String reverseName = createReverseName(ipAddress);
        List<String> in = new ArrayList<String>();
        in.add("update delete " + reverseName + " IN PTR");
        in.add("update add " + reverseName + " " + timeToLive + " IN PTR " + fqdn);
        in.add("send");
        return in;
    }

    protected List<String> createAddCanonicalName(String fqdn, String canonicalName) {
        List<String> in = new ArrayList<String>();
        in.add("update delete " + fqdn + " IN CNAME");
        in.add("update add " + fqdn + " " + timeToLive + " IN CNAME " + canonicalName);
        in.add("send");
        return in;
    }

    protected List<String> createDeleteForward(String fqdn) {
        List<String> in = new ArrayList<String>();
        in.add("update delete " + fqdn + " IN A");
        in.add("send");
        return in;
    }

    protected List<String> createDeleteReverse(String ipAddress) {
        String reverseName = createReverseName(ipAddress);
        List<String> in = new ArrayList<String>();
        in.add("update delete " + reverseName + " IN PTR");
        in.add("send");
        return in;
    }

    protected List<String> createDeleteCanonicalName(String fqdn) {
        List<String> in = new ArrayList<String>();
        in.add("update delete " + fqdn + " IN CNAME");
        in.add("send");
        return in;
    }

    protected String createReverseName(String ipAddress) {
        // 逆引きレコード名の作成
        StringBuilder reverseName = new StringBuilder();
        String[] splits = StringUtils.split(ipAddress, '.');
        for (int i = splits.length - 1; i >= 0; i--) {
            reverseName.append(splits[i]).append('.');
        }
        reverseName.append("in-addr.arpa");
        return reverseName.toString();
    }

    protected CommandResult execute(List<String> commands, List<String> stdins) {
        if (log.isDebugEnabled()) {
            log.debug(commands);
            log.debug(stdins);
        }

        CommandResult result = CommandUtils.execute(commands, stdins);

        if (log.isDebugEnabled()) {
            log.debug(ReflectionToStringBuilder.toString(result));
        }

        return result;
    }

    protected String getHostAddress(String fqdn) {
        try {
            InetAddress address = InetAddress.getByName(fqdn);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    protected String getHostName(String ipAddress) {
        byte[] addr = new byte[4];
        String[] octets = StringUtils.split(ipAddress, ".", 4);
        for (int i = 0; i < 4; i++) {
            addr[i] = (byte) Integer.parseInt(octets[i]);
        }

        InetAddress address;
        try {
            address = InetAddress.getByAddress(addr);
            return address.getCanonicalHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    protected String getCanonicalName(String fqdn) {
        try {
            InetAddress address = InetAddress.getByName(fqdn);
            return address.getCanonicalHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * dnsServerを取得します。
     *
     * @return dnsServer
     */
    public String getDnsServer() {
        return dnsServer;
    }

    /**
     * dnsServerを設定します。
     *
     * @param dnsServer dnsServer
     */
    public void setDnsServer(String dnsServer) {
        this.dnsServer = dnsServer;
    }

    /**
     * domainNameを取得します。
     *
     * @return domainName
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * domainNameを設定します。
     *
     * @param domainName domainName
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * timeToLiveを取得します。
     *
     * @return timeToLive
     */
    public int getTimeToLive() {
        return timeToLive;
    }

    /**
     * timeToLiveを設定します。
     *
     * @param timeToLive timeToLive
     */
    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

}
