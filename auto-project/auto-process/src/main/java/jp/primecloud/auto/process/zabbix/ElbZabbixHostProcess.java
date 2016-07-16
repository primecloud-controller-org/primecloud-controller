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
package jp.primecloud.auto.process.zabbix;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.ZabbixInstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.zabbix.model.hostgroup.Hostgroup;
import jp.primecloud.auto.zabbix.model.proxy.Proxy;
import jp.primecloud.auto.zabbix.model.template.Template;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ElbZabbixHostProcess extends ServiceSupport {

    protected ZabbixProcessClientFactory zabbixProcessClientFactory;

    protected EventLogger eventLogger;

    protected ProcessLogger processLogger;

    public void startHost(Long loadBalancerNo) {
        //ELBは全て監視対象として扱う
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100328", loadBalancerNo, awsLoadbalancer.getName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        String hostid = awsLoadbalancer.getHostid();

        //ホスト名取得
        String hostname = getHostName(loadBalancer.getFqdn());
        //ZabbixプロキシID取得
        String proxyHostid = getProxyHostid(zabbixProcessClient);

        if (StringUtils.isEmpty(hostid)) {
            List<Hostgroup> hostgroups = getInitHostgroups(zabbixProcessClient, loadBalancer.getFarmNo());

            // 監視対象の登録
            // ELBはIPでの監視をしない(DNSのみ)のでIPにはNULLを設定
            hostid = zabbixProcessClient.createHost(hostname, loadBalancer.getFqdn(), hostgroups, true, false, null, proxyHostid);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixRegist", new Object[] {loadBalancer.getFqdn(), hostid });

            // データベースの更新
            awsLoadbalancer.setHostid(hostid);
            awsLoadbalancer.setStatus(ZabbixInstanceStatus.MONITORING.toString());
            awsLoadBalancerDao.update(awsLoadbalancer);
        } else {
            // 監視対象の更新
            // ELBはIPでの監視をしない(DNSのみ)のでIPにはNULLを設定
            zabbixProcessClient.updateHost(hostid, hostname, loadBalancer.getFqdn(), null, true, false, null, proxyHostid);

            // データベースの更新
            awsLoadbalancer.setStatus(ZabbixInstanceStatus.MONITORING.toString());
            awsLoadBalancerDao.update(awsLoadbalancer);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixStart", new Object[] {loadBalancer.getFqdn(), hostid });
        }

        // 標準テンプレートの適用
        Image useImage = null;
        List<Image> images = imageDao.readByPlatformNo(loadBalancer.getPlatformNo());
        for (Image image : images) {
            if (PCCConstant.IMAGE_NAME_ELB.equals(image.getImageName())) {
                useImage = image;
                break;
            }
        }
        String templateName = useImage.getZabbixTemplate();
        if (StringUtils.isEmpty(templateName)) {
            // TODO: 互換性のためプロパティファイルから取得する方法を残している
            templateName = Config.getProperty("zabbix.basetemplate");
        }
        if (StringUtils.isNotEmpty(templateName)) {
            Template template = zabbixProcessClient.getTemplateByName(templateName);
            boolean ret = zabbixProcessClient.addTemplate(hostid, template);

            if (ret) {
                // イベントログ出力
                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixTemplateAdd", new Object[] {loadBalancer.getFqdn(), templateName });
            }
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100329", loadBalancerNo, awsLoadbalancer.getName()));
        }
    }

    public void stopHost(Long loadBalancerNo) {
        //ELBは全て監視対象として扱う
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100330", loadBalancerNo, awsLoadbalancer.getName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // 監視対象の無効化
        try {
            //ホスト名取得
            String hostname = getHostName(loadBalancer.getFqdn());
            //ZabbixプロキシID取得
            String proxyHostid = getProxyHostid(zabbixProcessClient);

            zabbixProcessClient.updateHost(awsLoadbalancer.getHostid(), hostname, loadBalancer.getFqdn(), null, false, false, null, proxyHostid);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixStop", new Object[] {loadBalancer.getFqdn(), awsLoadbalancer.getHostid() });

            // データベースの更新
            awsLoadbalancer.setStatus(ZabbixInstanceStatus.UN_MONITORING.toString());
            awsLoadBalancerDao.update(awsLoadbalancer);

        } catch (AutoException ignore) {
            // 処理に失敗した場合、警告ログを出力する
            log.warn(ignore.getMessage());
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100331", loadBalancerNo, awsLoadbalancer.getName()));
        }
    }

    public void startTemplate(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);

        if (StringUtils.isEmpty(awsLoadbalancer.getHostid())) {
            // 監視登録されていない場合
            throw new AutoException("EPROCESS-000405", loadBalancerNo);
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100332", loadBalancerNo, awsLoadbalancer.getName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // ホストグループを更新する
        startTemplateHostgroup(zabbixProcessClient, loadBalancer, awsLoadbalancer, awsLoadbalancer.getHostid());

        // テンプレートの設定
        String templateName = null;
        List<ComponentType> componentTypes = componentTypeDao.readAll();
        for (ComponentType type:componentTypes){
            if ("elb".equals(type.getComponentTypeName())) {
                templateName = type.getZabbixTemplate();
            }
        }

        if (StringUtils.isNotEmpty(templateName)) {
            // テンプレートを取得
            Template template = zabbixProcessClient.getTemplateByName(templateName);

            // テンプレートを適用
            boolean ret = zabbixProcessClient.addTemplate(awsLoadbalancer.getHostid(), template);
            if (ret) {
                // イベントログ出力
                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixTemplateAdd", new Object[] { loadBalancer.getFqdn(), templateName });
            }

            // ホストに紐づくアイテムを有効化
            boolean ret2 = zabbixProcessClient.enableItems(awsLoadbalancer.getHostid(), template.getTemplateid());
            if (ret2) {
                // イベントログ出力
                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixItemEnable", new Object[] { loadBalancer.getFqdn(), templateName });
            }
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100333", loadBalancerNo, awsLoadbalancer.getName()));
        }
    }

    public void stopTemplate(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);

        if (StringUtils.isEmpty(awsLoadbalancer.getHostid())) {
            // 監視登録されていない場合
            throw new AutoException("EPROCESS-000405", loadBalancerNo);
        }

        String templateName = null;
        List<ComponentType> componentTypes = componentTypeDao.readAll();
        for (ComponentType type:componentTypes){
            if ("elb".equals(type.getComponentTypeName())) {
                templateName = type.getZabbixTemplate();
            }
        }
        if (StringUtils.isEmpty(templateName)) {
            // テンプレートが登録されていない場合はなにもしない。
            return;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100332", loadBalancerNo, awsLoadbalancer.getName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // テンプレートを取得
        Template template = zabbixProcessClient.getTemplateByName(templateName);

        // ホストに紐づくアイテムを無効化
        boolean ret = zabbixProcessClient.disableItems(awsLoadbalancer.getHostid(), template.getTemplateid());
        if (ret) {
            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixItemDisable", new Object[] { loadBalancer.getFqdn(), templateName });
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100333", loadBalancerNo, awsLoadbalancer.getName()));
        }
    }

    public void removeTemplate(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);


        if (StringUtils.isEmpty(awsLoadbalancer.getHostid())) {
            // 監視登録されていない場合は何もしない
            return;
        }

        String templateName = null;
        List<ComponentType> componentTypes = componentTypeDao.readAll();
        for (ComponentType type:componentTypes){
            if ("elb".equals(type.getComponentTypeName())) {
                templateName = type.getZabbixTemplate();
            }
        }
        if (StringUtils.isEmpty(templateName)) {
            // テンプレートが設定されていない場合はスキップ
            return;
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100332", loadBalancerNo, awsLoadbalancer.getName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // テンプレートを取得
        Template template = zabbixProcessClient.getTemplateByName(templateName);

        // テンプレートを除去
        boolean ret = zabbixProcessClient.removeTemplate(awsLoadbalancer.getHostid(), template);
        if (ret) {
            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixTemplateRemove", new Object[] { loadBalancer.getFqdn(), templateName });
        }

        // アイテムを削除
        boolean ret2 = zabbixProcessClient.deleteItems(awsLoadbalancer.getHostid(), template.getTemplateid());
        if (ret2) {
            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, null, "ZabbixItemDelete", new Object[] { loadBalancer.getFqdn(), templateName });
        }

        // ホストグループを更新する
        removeTemplateHostgroup(zabbixProcessClient, loadBalancer, awsLoadbalancer.getHostid());

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100333", loadBalancerNo, awsLoadbalancer.getName()));
        }
    }

    public void createElbHostgroup(Long loadBalancerNo) {
        LoadBalancer loadBalancer = loadBalancerDao.read(loadBalancerNo);
        AwsLoadBalancer awsLoadbalancer = awsLoadBalancerDao.read(loadBalancerNo);
        Farm farm = farmDao.read(loadBalancer.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        String hostgroupName = getHostgroupName(user, farm, awsLoadbalancer);

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        Hostgroup hostgroup = zabbixProcessClient.getHostgroupByName(hostgroupName);
        if (hostgroup != null) {
            // すでにホストグループが作られている場合は何もしない
            return;
        }

        zabbixProcessClient.createHostgroup(hostgroupName);
    }

    public void createFarmHostgroup(Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        User user = userDao.read(farm.getUserNo());
        String hostgroupName = getHostgroupName(user, farm);
        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        Hostgroup hostgroup = zabbixProcessClient.getHostgroupByName(hostgroupName);
        if (hostgroup != null) {
            // すでにホストグループが作られている場合は何もしない
            return;
        }
        zabbixProcessClient.createHostgroup(hostgroupName);
    }

    public void deleteFarmHostgroup(Long farmNo) {
        Farm farm = farmDao.read(farmNo);
        User user = userDao.read(farm.getUserNo());
        String hostgroupName = user.getUsername() + "_" + farm.getFarmName();

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        Hostgroup hostgroup = zabbixProcessClient.getHostgroupByName(hostgroupName);
        if (hostgroup != null) {
            // ホストグループがあれば削除する
            zabbixProcessClient.deleteHostgroup(hostgroup.getGroupid(), hostgroupName);
        }

        // コンポーネントごとのホストグループを削除
        String prefix = hostgroupName + "_";
        List<Hostgroup> hostgroups = zabbixProcessClient.getHostgroups();
        for (Hostgroup hostgroup2 : hostgroups) {
            if (StringUtils.startsWith(hostgroup2.getName(), prefix)) {
                zabbixProcessClient.deleteHostgroup(hostgroup2.getGroupid(), hostgroup2.getName());
            }
        }
    }

    protected String getHostgroupName(User user) {
        return getHostgroupName(user, null);
    }

    protected String getHostgroupName(User user, Farm farm) {
        return getHostgroupName(user, farm, null);
    }

    protected String getHostgroupName(User user, Farm farm, AwsLoadBalancer awsLoadbalancer) {
        String delimiter = "_";
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUsername());
        if (farm != null) {
            sb.append(delimiter);
            sb.append(farm.getFarmName());
        }
        if (awsLoadbalancer != null) {
            sb.append(delimiter);
            sb.append(awsLoadbalancer.getName());
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

    protected void startTemplateHostgroup(ZabbixProcessClient zabbixProcessClient, LoadBalancer loadbalancer, AwsLoadBalancer awsLoadbalancer, String hostid) {
        // ホストが含まれるホストグループを取得
        List<Hostgroup> hostgroups = zabbixProcessClient.getHostgroupsByHostid(hostid);
        List<String> groupids = new ArrayList<String>();
        for (Hostgroup hostgroup : hostgroups) {
            groupids.add(hostgroup.getGroupid());
        }

        // コンポーネントごとのホストグループを取得
        Farm farm = farmDao.read(loadbalancer.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        String hostgroupName = getHostgroupName(user, farm, awsLoadbalancer);
        Hostgroup hostgroup = zabbixProcessClient.getHostgroupByName(hostgroupName);

        // ホストがホストグループに含まれない場合、ホストグループに含める
        if (hostgroup != null && !groupids.contains(hostgroup.getGroupid())) {
            hostgroups.add(hostgroup);
            //ホスト名取得
            String hostname = getHostName(loadbalancer.getFqdn());
            //ZabbixプロキシID取得
            String proxyHostid = getProxyHostid(zabbixProcessClient);

            zabbixProcessClient.updateHost(hostid, hostname, loadbalancer.getFqdn(), hostgroups, null, false, null, proxyHostid);
        }
    }

    protected void removeTemplateHostgroup(ZabbixProcessClient zabbixProcessClient, LoadBalancer loadbalancer, String hostid) {
        // ホストが含まれるホストグループを取得
        List<Hostgroup> hostgroups = zabbixProcessClient.getHostgroupsByHostid(hostid);
        List<String> groupids = new ArrayList<String>();
        for (Hostgroup hostgroup : hostgroups) {
            groupids.add(hostgroup.getGroupid());
        }

        // コンポーネントごとのホストグループ名
        Farm farm = farmDao.read(loadbalancer.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        String hostgroupName = getHostgroupName(user, farm);
        // ホストグループを取得
        Hostgroup hostgroup = zabbixProcessClient.getHostgroupByName(hostgroupName);

        // ホストがホストグループに含まれる場合、ホストグループから除去する
        if (hostgroup != null && groupids.contains(hostgroup.getGroupid())) {
            for (int i = 0; i < hostgroups.size(); i++) {
                if (StringUtils.equals(hostgroup.getGroupid(), hostgroups.get(i).getGroupid())) {
                    hostgroups.remove(i);
                    break;
                }
            }
            //ホスト名取得
            String hostname = getHostName(loadbalancer.getFqdn());
            //ZabbixプロキシID取得
            String proxyHostid = getProxyHostid(zabbixProcessClient);

            zabbixProcessClient.updateHost(hostid, hostname, loadbalancer.getFqdn(), hostgroups, null, false, null, proxyHostid);
        }
    }

    private String getHostName(String fqdn) {
        // Zabbixのホストの「名前」を prefix + _ + FQDN で設定する
        // prefix値はconfig.propertiesから取得
        String hostname = fqdn;
        if (StringUtils.isNotEmpty(Config.getProperty("zabbix.prefix"))) {
            //pretixが設定されている場合のみ設定
            //設定されていない場合は通常通り、FQDNを設定
            hostname = Config.getProperty("zabbix.prefix") + "-" + fqdn;
        }
        return hostname;
    }

    private String getProxyHostid(ZabbixProcessClient zabbixProcessClient) {
        String proxyName = Config.getProperty("zabbix.proxy");
        String proxyHostid = null;
        if (StringUtils.isNotEmpty(proxyName)) {
            Proxy proxy = zabbixProcessClient.getProxy(proxyName);
            if (proxy != null) {
                proxyHostid = proxy.getProxyid();
            }
        }
        return proxyHostid;
    }

    /**
     * zabbixProcessClientFactoryを設定します。
     *
     * @param zabbixProcessClientFactory zabbixProcessClientFactory
     */
    public void setZabbixProcessClientFactory(ZabbixProcessClientFactory zabbixProcessClientFactory) {
        this.zabbixProcessClientFactory = zabbixProcessClientFactory;
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    /**
     * processLoggerを設定します。
     *
     * @param processLogger processLogger
     */
    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }
}
