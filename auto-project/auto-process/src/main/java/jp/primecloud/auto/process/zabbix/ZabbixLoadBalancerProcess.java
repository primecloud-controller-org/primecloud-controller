/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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
package jp.primecloud.auto.process.zabbix;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.ZabbixInstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.proxy.Proxy;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ZabbixLoadBalancerProcess extends ServiceSupport {

    protected ZabbixProcessClientFactory zabbixProcessClientFactory;

    protected EventLogger eventLogger;

    protected ProcessLogger processLogger;

    public void startHost(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        // AWSプラットフォームのロードバランサでない場合は何もしない
        String type = loadBalancer.getType();
        if (!PCCConstant.LOAD_BALANCER_ELB.equals(type)) {
            return;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200232", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);
        String hostid = awsLoadbalancer.getHostid();

        // Zabbixホスト名
        String hostname = getHostName(loadBalancer.getFqdn());

        // ZabbixプロキシID
        String proxyHostid = getProxyHostid(zabbixProcessClient);

        // 監視対象の登録
        if (StringUtils.isEmpty(hostid)) {
            List<Hostgroup> hostgroups = getInitHostgroups(zabbixProcessClient, loadBalancer.getFarmNo());

            hostid = zabbixProcessClient.createHost(hostname, loadBalancer.getFqdn(), hostgroups, true, false, null,
                    proxyHostid);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixRegist", new Object[] {
                    loadBalancer.getFqdn(), hostid });

            // データベースの更新
            awsLoadbalancer.setHostid(hostid);
            awsLoadbalancer.setStatus(ZabbixInstanceStatus.MONITORING.toString());
            awsLoadBalancerDao.update(awsLoadbalancer);
        }
        // 監視対象の有効化
        else {
            zabbixProcessClient.updateHost(hostid, hostname, loadBalancer.getFqdn(), null, true, false, null,
                    proxyHostid);

            // データベースの更新
            awsLoadbalancer.setStatus(ZabbixInstanceStatus.MONITORING.toString());
            awsLoadBalancerDao.update(awsLoadbalancer);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixStart", new Object[] {
                    loadBalancer.getFqdn(), hostid });
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200233", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    public void stopHost(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);

        // AWSプラットフォームのロードバランサでない場合は何もしない
        String type = loadBalancer.getType();
        if (!PCCConstant.LOAD_BALANCER_ELB.equals(type)) {
            return;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200234", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // 監視対象の無効化
        try {
            // Zabbixホスト名
            String hostname = getHostName(loadBalancer.getFqdn());

            // ZabbixプロキシID取得
            String proxyHostid = getProxyHostid(zabbixProcessClient);

            AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);
            zabbixProcessClient.updateHost(awsLoadbalancer.getHostid(), hostname, loadBalancer.getFqdn(), null, false,
                    false, null, proxyHostid);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixStop", new Object[] {
                    loadBalancer.getFqdn(), awsLoadbalancer.getHostid() });

            // データベースの更新
            awsLoadbalancer.setStatus(ZabbixInstanceStatus.UN_MONITORING.toString());
            awsLoadBalancerDao.update(awsLoadbalancer);

        } catch (AutoException ignore) {
            // 処理に失敗した場合、警告ログを出力する
            log.warn(ignore.getMessage());
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-200235", loadBalancerNo, loadBalancer.getLoadBalancerName()));
        }
    }

    protected String getHostgroupName(User user) {
        return getHostgroupName(user, null);
    }

    protected String getHostgroupName(User user, Farm farm) {
        String delimiter = "_";
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUsername());
        if (farm != null) {
            sb.append(delimiter);
            sb.append(farm.getFarmName());
        }

        // 64文字でカットする
        // TODO: Zabbixの制限による暫定対応
        if (sb.length() <= 64) {
            return sb.toString();
        } else {
            return sb.substring(0, 64);
        }
    }

    protected List<Hostgroup> getInitHostgroups(ZabbixProcessClient zabbixProcessClient, Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        User user = userDao.read(farm.getUserNo());

        List<Hostgroup> hostgroups = new ArrayList<Hostgroup>();

        // ユーザごとのホストグループ
        String hostgroupName = getHostgroupName(user);
        Hostgroup hostgroup = zabbixProcessClient.getHostgroupByName(hostgroupName);
        if (hostgroup != null) {
            hostgroups.add(hostgroup);
        }

        // ファームごとのホストグループ
        String hostgroupName2 = getHostgroupName(user, farm);
        Hostgroup hostgroup2 = zabbixProcessClient.getHostgroupByName(hostgroupName2);
        if (hostgroup2 != null) {
            hostgroups.add(hostgroup2);
        }

        return hostgroups;
    }

    private String getHostName(String fqdn) {
        String hostname = fqdn;
        if (StringUtils.isNotEmpty(Config.getProperty("zabbix.prefix"))) {
            hostname = Config.getProperty("zabbix.prefix") + "-" + hostname;
        }

        return hostname;
    }

    private String getProxyHostid(ZabbixProcessClient zabbixProcessClient) {
        String proxyName = Config.getProperty("zabbix.proxy");
        if (StringUtils.isEmpty(proxyName)) {
            return null;
        }

        Proxy proxy = zabbixProcessClient.getProxy(proxyName);
        if (proxy != null) {
            return proxy.getProxyid();
        }

        return null;
    }

    public void setZabbixProcessClientFactory(ZabbixProcessClientFactory zabbixProcessClientFactory) {
        this.zabbixProcessClientFactory = zabbixProcessClientFactory;
    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }

}
