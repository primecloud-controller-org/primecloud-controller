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
package jp.primecloud.auto.process.vcloud;

import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.VcloudInstance;
import jp.primecloud.auto.process.DnsProcessClient;
import jp.primecloud.auto.process.DnsProcessClientFactory;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class VcloudDnsProcess extends ServiceSupport {

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
            //内部プラットフォーム(VPNなし)
            startDnsVcloud(instanceNo);
        } else {
            //外部プラットフォーム(VPNあり)
            startDnsVcloudVPN(instanceNo);
        }

        // イベントログ出力
        processLogger.debug(null, instance, "DnsRegist", new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void stopDns(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        stopDnsNormal(instanceNo);

        // イベントログ出力
        processLogger.debug(null, instance, "DnsUnregist", new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    //VPNを使用しないVCloudDNS設定
    //PublicIP  → VCloudで外部ネットワークに接続されているネットワークのIPアドレス
    //PrivateIP → VCloudで外部ネットワークに接続されていないネットワークのIPアドレス
    protected void startDnsVcloud(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        VcloudInstance vcloudInstance = vcloudInstanceDao.read(instanceNo);

        // 最新のVcloudInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), vcloudInstance.getIpAddress())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        String fqdn = instance.getFqdn();
        String publicIp = vcloudInstance.getIpAddress();
        String privateIp = vcloudInstance.getPrivateIpAddress();

        // 正引きの追加
        dnsProcessClient.addForward(fqdn, publicIp);

        // 逆引きの追加
        dnsProcessClient.addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    //VPNを使用するVCloudDNS設定
    //PublicIP  → PCC(OpenVPN)で払い出されたIPアドレス
    //PrivateIP → VCloudで外部ネットワークに接続されていないネットワークのIPアドレス
    protected void startDnsVcloudVPN(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        VcloudInstance vcloudInstance = vcloudInstanceDao.read(instanceNo);

        // 最新のVcloudInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), vcloudInstance.getIpAddress())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        // IPアドレスを正引きにより取得する（正引きの追加はインスタンス側(OpenVPNの実行)で行う）
        String fqdn = instance.getFqdn();
        String publicIp = dnsProcessClient.resolveHost(fqdn); // VPNインタフェースのIPアドレス
        String privateIp = vcloudInstance.getPrivateIpAddress();

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

    public void setDnsProcessClientFactory(DnsProcessClientFactory dnsProcessClientFactory) {
        this.dnsProcessClientFactory = dnsProcessClientFactory;
    }

    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }

}
