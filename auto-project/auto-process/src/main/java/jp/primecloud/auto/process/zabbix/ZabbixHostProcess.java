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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.ZabbixInstanceStatus;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.ZabbixInstance;
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
public class ZabbixHostProcess extends ServiceSupport {

    protected ZabbixProcessClientFactory zabbixProcessClientFactory;

    protected EventLogger eventLogger;

    protected ProcessLogger processLogger;

    public void startHost(Long instanceNo) {
        ZabbixInstance zabbixInstance = zabbixInstanceDao.read(instanceNo);
        if (zabbixInstance == null) {
            // Zabbix監視対象でない
            throw new AutoException("EPROCESS-000401", instanceNo);
        }

        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100301", instanceNo, instance.getInstanceName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        String hostid = zabbixInstance.getHostid();
        log.info("********hostid:"+hostid);

        //ホスト名取得
        String hostname = getHostName(instance.getFqdn());
        //IP/DNS使用フラグ取得
        Boolean useIp = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useIp"));
        //ZabbixプロキシID取得
        String proxyHostid = getProxyHostid(zabbixProcessClient);
        //IP設定
        String zabbixListenIp = getZabbixListenIp(zabbixProcessClient, instance, platform);

        if (StringUtils.isEmpty(hostid)) {
            List<Hostgroup> hostgroups = getInitHostgroups(zabbixProcessClient, instance);

            // 監視対象の登録
            hostid = zabbixProcessClient.createHost(hostname, instance.getFqdn(), hostgroups, true, useIp, zabbixListenIp, proxyHostid);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null,instance, "ZabbixRegist", new Object[] {instance.getFqdn(), hostid });

            // データベースの更新
            zabbixInstance.setHostid(hostid);
            zabbixInstance.setStatus(ZabbixInstanceStatus.MONITORING.toString());
            zabbixInstanceDao.update(zabbixInstance);
        } else {
            // 監視対象の更新
            zabbixProcessClient.updateHost(hostid, hostname, instance.getFqdn(), null, true, useIp, zabbixListenIp, proxyHostid);

            // データベースの更新
            zabbixInstance.setStatus(ZabbixInstanceStatus.MONITORING.toString());
            zabbixInstanceDao.update(zabbixInstance);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null,instance, "ZabbixStart", new Object[] {instance.getFqdn(), hostid });
        }

        // 標準テンプレートの適用
        Image image = imageDao.read(instance.getImageNo());
        String templateName = image.getZabbixTemplate();
        if (StringUtils.isEmpty(templateName)) {
            // TODO: 互換性のためプロパティファイルから取得する方法を残している
            templateName = Config.getProperty("zabbix.basetemplate");
        }
        Template template = zabbixProcessClient.getTemplateByName(templateName);
        boolean ret = zabbixProcessClient.addTemplate(hostid, template);

        if (ret) {
            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null,instance, "ZabbixTemplateAdd", new Object[] {instance.getFqdn(), templateName });
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100302", instanceNo, instance.getInstanceName()));
        }
    }

    public void stopHost(Long instanceNo) {
        ZabbixInstance zabbixInstance = zabbixInstanceDao.read(instanceNo);
        if (zabbixInstance == null) {
            // Zabbix監視対象でない
            throw new AutoException("EPROCESS-000401", instanceNo);
        }

        if (StringUtils.isEmpty(zabbixInstance.getHostid())) {
            // 監視登録されていない場合はスキップ
            return;
        }

        Instance instance = instanceDao.read(instanceNo);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100303", instanceNo, instance.getInstanceName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // 監視対象の無効化
        try {
            //ホスト名取得
            String hostname = getHostName(instance.getFqdn());
            //IP/DNS使用フラグ取得
            Boolean useIp = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useIp"));
            //ZabbixプロキシID取得
            String proxyHostid = getProxyHostid(zabbixProcessClient);

            zabbixProcessClient.updateHost(zabbixInstance.getHostid(), hostname, instance.getFqdn(), null, false, useIp, null, proxyHostid);

            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null,instance, "ZabbixStop", new Object[] {instance.getFqdn(), zabbixInstance.getHostid() });

            // データベースの更新
            zabbixInstance.setStatus(ZabbixInstanceStatus.UN_MONITORING.toString());
            zabbixInstanceDao.update(zabbixInstance);

        } catch (AutoException ignore) {
            // 処理に失敗した場合、警告ログを出力する
            log.warn(ignore.getMessage());
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100304", instanceNo, instance.getInstanceName()));
        }
    }

    public void startTemplate(Long instanceNo, Long componentNo) {
        ZabbixInstance zabbixInstance = zabbixInstanceDao.read(instanceNo);
        if (zabbixInstance == null) {
            // Zabbix監視対象でない
            throw new AutoException("EPROCESS-000401", instanceNo);
        }

        if (StringUtils.isEmpty(zabbixInstance.getHostid())) {
            // 監視登録されていない場合
            throw new AutoException("EPROCESS-000404", instanceNo);
        }

        Instance instance = instanceDao.read(instanceNo);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100305", instanceNo, instance.getInstanceName()));
        }

        Component component = componentDao.read(componentNo);
        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // ホストグループを更新する
        startTemplateHostgroup(zabbixProcessClient, instance, component, zabbixInstance.getHostid());

        // テンプレートの設定
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        String templateName = componentType.getZabbixTemplate();
        if (StringUtils.isNotEmpty(templateName)) {
            // テンプレートを取得
            Template template = zabbixProcessClient.getTemplateByName(templateName);

            // テンプレートを適用
            boolean ret = zabbixProcessClient.addTemplate(zabbixInstance.getHostid(), template);
            if (ret) {
                // イベントログ出力
                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "ZabbixTemplateAdd", new Object[] { instance.getFqdn(), templateName });
            }

            // ホストに紐づくアイテムを有効化
            boolean ret2 = zabbixProcessClient.enableItems(zabbixInstance.getHostid(), template.getTemplateid());
            if (ret2) {
                // イベントログ出力
                processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "ZabbixItemEnable", new Object[] { instance.getFqdn(), templateName });
            }
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100306", instanceNo, instance.getInstanceName()));
        }
    }

    public void stopTemplate(Long instanceNo, Long componentNo) {
        ZabbixInstance zabbixInstance = zabbixInstanceDao.read(instanceNo);
        if (zabbixInstance == null) {
            // Zabbix監視対象でない
            throw new AutoException("EPROCESS-000401", instanceNo);
        }

        if (StringUtils.isEmpty(zabbixInstance.getHostid())) {
            // 監視登録されていない場合
            throw new AutoException("EPROCESS-000404", instanceNo);
        }

        Component component = componentDao.read(componentNo);
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        String templateName = componentType.getZabbixTemplate();
        if (StringUtils.isEmpty(templateName)) {
            // テンプレートが登録されていない場合はなにもしない。
            return;
        }

        Instance instance = instanceDao.read(instanceNo);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100305", instanceNo, instance.getInstanceName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // テンプレートを取得
        Template template = zabbixProcessClient.getTemplateByName(templateName);

        // ホストに紐づくアイテムを無効化
        boolean ret = zabbixProcessClient.disableItems(zabbixInstance.getHostid(), template.getTemplateid());
        if (ret) {
            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "ZabbixItemDisable", new Object[] { instance.getFqdn(), templateName });
        }

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100306", instanceNo, instance.getInstanceName()));
        }
    }

    public void removeTemplate(Long instanceNo, Long componentNo) {
        ZabbixInstance zabbixInstance = zabbixInstanceDao.read(instanceNo);
        if (zabbixInstance == null) {
            // Zabbix監視対象でない
            throw new AutoException("EPROCESS-000401", instanceNo);
        }

        if (StringUtils.isEmpty(zabbixInstance.getHostid())) {
            // 監視登録されていない場合は何もしない
            return;
        }

        Component component = componentDao.read(componentNo);
        ComponentType componentType = componentTypeDao.read(component.getComponentTypeNo());
        String templateName = componentType.getZabbixTemplate();
        if (StringUtils.isEmpty(templateName)) {
            // テンプレートが設定されていない場合はスキップ
            return;
        }

        Instance instance = instanceDao.read(instanceNo);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100305", instanceNo, instance.getInstanceName()));
        }

        ZabbixProcessClient zabbixProcessClient = zabbixProcessClientFactory.createZabbixProcessClient();

        // テンプレートを取得
        Template template = zabbixProcessClient.getTemplateByName(templateName);

        // テンプレートを除去
        boolean ret = zabbixProcessClient.removeTemplate(zabbixInstance.getHostid(), template);
        if (ret) {
            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "ZabbixTemplateRemove", new Object[] { instance.getFqdn(), templateName });
        }

        // アイテムを削除
        boolean ret2 = zabbixProcessClient.deleteItems(zabbixInstance.getHostid(), template.getTemplateid());
        if (ret2) {
            // イベントログ出力
            processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, component, instance, "ZabbixItemDelete", new Object[] { instance.getFqdn(), templateName });
        }

        // ホストグループを更新する
        removeTemplateHostgroup(zabbixProcessClient, instance, component, zabbixInstance.getHostid());

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100306", instanceNo, instance.getInstanceName()));
        }
    }

    public void createComponentHostgroup(Long componentNo) {
        Component component = componentDao.read(componentNo);
        Farm farm = farmDao.read(component.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        String hostgroupName = getHostgroupName(user, farm, component);

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

    protected String getHostgroupName(User user, Farm farm, Component component) {
        String delimiter = "_";
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUsername());
        if (farm != null) {
            sb.append(delimiter);
            sb.append(farm.getFarmName());
        }
        if (component != null) {
            sb.append(delimiter);
            sb.append(component.getComponentName());
        }

        // 64文字でカットする
        // TODO: Zabbixの制限による暫定対応
        if (sb.length() <= 64) {
            return sb.toString();
        } else {
            return sb.substring(0, 64);
        }
    }

    protected List<Hostgroup> getInitHostgroups(ZabbixProcessClient zabbixProcessClient, Instance instance) {
        Farm farm = farmDao.read(instance.getFarmNo());
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

    protected void startTemplateHostgroup(ZabbixProcessClient zabbixProcessClient, Instance instance,
            Component component, String hostid) {
        // ホストが含まれるホストグループを取得
        List<Hostgroup> hostgroups = zabbixProcessClient.getHostgroupsByHostid(hostid);
        List<String> groupids = new ArrayList<String>();
        for (Hostgroup hostgroup : hostgroups) {
            groupids.add(hostgroup.getGroupid());
        }

        // コンポーネントごとのホストグループを取得
        Farm farm = farmDao.read(instance.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        Platform platform = platformDao.read(instance.getPlatformNo());
        String hostgroupName = getHostgroupName(user, farm, component);
        Hostgroup hostgroup = zabbixProcessClient.getHostgroupByName(hostgroupName);

        // ホストがホストグループに含まれない場合、ホストグループに含める
        if (hostgroup != null && !groupids.contains(hostgroup.getGroupid())) {
            hostgroups.add(hostgroup);
            //ホスト名取得
            String hostname = getHostName(instance.getFqdn());
            //IP/DNS使用フラグ取得
            Boolean useIp = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useIp"));
            //ZabbixプロキシID取得
            String proxyHostid = getProxyHostid(zabbixProcessClient);
            //IP設定
            String zabbixListenIp = getZabbixListenIp(zabbixProcessClient, instance, platform);
            zabbixProcessClient.updateHost(hostid, hostname, instance.getFqdn(), hostgroups, null, useIp, zabbixListenIp, proxyHostid);
        }
    }

    protected void removeTemplateHostgroup(ZabbixProcessClient zabbixProcessClient, Instance instance,
            Component component, String hostid) {
        // ホストが含まれるホストグループを取得
        List<Hostgroup> hostgroups = zabbixProcessClient.getHostgroupsByHostid(hostid);
        List<String> groupids = new ArrayList<String>();
        for (Hostgroup hostgroup : hostgroups) {
            groupids.add(hostgroup.getGroupid());
        }

        // コンポーネントごとのホストグループ名
        Farm farm = farmDao.read(instance.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        Platform platform = platformDao.read(instance.getPlatformNo());
        String hostgroupName = getHostgroupName(user, farm, component);

        // 対象のインスタンスに関連付けられたコンポーネントの中に、同名のホストグループになるものがあればスキップする
        // TODO: ホストグループ名の最大長が64文字であるZabbixの制限に伴う暫定対応
        List<ComponentInstance> componentInstances = componentInstanceDao.readByInstanceNo(instance.getInstanceNo());
        for (ComponentInstance componentInstance : componentInstances) {
            if (component.getComponentNo().equals(componentInstance.getComponentNo())) {
                continue;
            }

            Component component2 = componentDao.read(componentInstance.getComponentNo());
            String hostgroupName2 = getHostgroupName(user, farm, component2);

            if (StringUtils.equals(hostgroupName, hostgroupName2)) {
                return;
            }
        }

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
            String hostname = getHostName(instance.getFqdn());
            //IP/DNS使用フラグ取得
            Boolean useIp = BooleanUtils.toBooleanObject(Config.getProperty("zabbix.useIp"));
            //ZabbixプロキシID取得
            String proxyHostid = getProxyHostid(zabbixProcessClient);
            //IP設定
            String zabbixListenIp = getZabbixListenIp(zabbixProcessClient, instance, platform);
            zabbixProcessClient.updateHost(hostid, hostname, instance.getFqdn(), hostgroups, null, useIp, zabbixListenIp, proxyHostid);
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

    private String getZabbixListenIp(ZabbixProcessClient zabbixProcessClient, Instance instance, Platform platform) {
        String zabbixListenIp = instance.getPublicIp();
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            if (BooleanUtils.isTrue(platform.getInternal())) {
                // 内部のAWSプラットフォームの場合はprivateIpで待ち受ける
                // この分岐に来るパターンは以下の場合
                // Eucalyptus
                // 内部AWS
                // 通常のVPC(VPC+VPNでは無い)
                zabbixListenIp = instance.getPrivateIp();
            }
        }
        return zabbixListenIp;
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
