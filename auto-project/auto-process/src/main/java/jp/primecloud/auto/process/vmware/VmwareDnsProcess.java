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
package jp.primecloud.auto.process.vmware;

import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.process.DnsProcessClient;
import jp.primecloud.auto.process.DnsProcessClientFactory;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareDnsProcess extends ServiceSupport {

    protected DnsProcessClientFactory dnsProcessClientFactory;

    protected ProcessLogger processLogger;

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void startDns(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());

        if (platform.getInternal()) {
            // 内部のプラットフォームの場合
            startDnsNormal(instanceNo);
        } else {
            // 外部のプラットフォームの場合
            startDnsVpn(instanceNo);
        }

        // イベントログ出力
        instance = instanceDao.read(instanceNo);
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "DnsRegist",
                new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void stopDns(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());

        if (platform.getInternal()) {
            // 内部のプラットフォームの場合
            stopDnsNormal(instanceNo);
        } else {
            // 外部のプラットフォームの場合
            stopDnsVpn(instanceNo);
        }

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "DnsUnregist",
                new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    protected void startDnsNormal(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        String privateIp = vmwareInstance.getPrivateIpAddress();

        // VmwareInstanceにPulicIpAddressが無い場合、PrivateIpAddressをIPアドレスとする
        String publicIp;
        if (StringUtils.isEmpty(vmwareInstance.getIpAddress())) {
            publicIp = vmwareInstance.getPrivateIpAddress();
        } else {
            publicIp = vmwareInstance.getIpAddress();
        }

        // 最新のVmwareInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), publicIp)) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        String fqdn = instance.getFqdn();

        // 正引きの追加
        dnsProcessClient.addForward(fqdn, publicIp);

        // 逆引きの追加
        dnsProcessClient.addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void startDnsVpn(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // InstanceにIPアドレスが登録済みの場合はスキップする
        if (!StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // IPアドレスを正引きにより取得する（正引きの追加はインスタンス内で行う）
        String fqdn = instance.getFqdn();
        String publicIp = dnsProcessClient.resolveHost(fqdn); // VPNインタフェースのIPアドレス
        String privateIp = vmwareInstance.getPrivateIpAddress();

        // 逆引きの追加
        dnsProcessClient.addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void stopDnsNormal(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // IPアドレスがない場合はスキップ
        if (StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        String fqdn = instance.getFqdn();
        String publicIp = instance.getPublicIp();

        // 正引きの削除
        dnsProcessClient.deleteForward(fqdn);

        // 逆引きの削除
        dnsProcessClient.deleteReverse(publicIp);

        // データベースの更新
        instance.setPublicIp(null);
        instance.setPrivateIp(null);
        instanceDao.update(instance);
    }

    protected void stopDnsVpn(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // IPアドレスがない場合はスキップ
        if (StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        String fqdn = instance.getFqdn();
        String publicIp = instance.getPublicIp();

        // 正引きの削除
        dnsProcessClient.deleteForward(fqdn);

        // 逆引きの削除
        dnsProcessClient.deleteReverse(publicIp);

        // データベースの更新
        instance.setPublicIp(null);
        instance.setPrivateIp(null);
        instanceDao.update(instance);
    }

    public void setDnsProcessClientFactory(DnsProcessClientFactory dnsProcessClientFactory) {
        this.dnsProcessClientFactory = dnsProcessClientFactory;
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
