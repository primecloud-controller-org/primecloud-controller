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
package jp.primecloud.auto.process.cloudstack;

import jp.primecloud.auto.entity.crud.CloudstackInstance;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.process.DnsProcessClient;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class CloudstackDnsProcess extends ServiceSupport {

    protected DnsProcessClient dnsProcessClient;

    protected ProcessLogger processLogger;

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void startDns(Long instanceNo) {
        startDnsCloudstack(instanceNo);

        // イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
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

        stopDnsNormal(instanceNo);

        // イベントログ出力
        processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance, "DnsUnregist",
                new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    protected void startDnsCloudstack(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        CloudstackInstance csInstance = cloudstackInstanceDao.read(instanceNo);

        // 最新のCloudStackInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), csInstance.getIpaddress())) {
            return;
        }

        String fqdn = instance.getFqdn();
        String publicIp = dnsProcessClient.resolveHost(fqdn);
        String privateIp = csInstance.getIpaddress();

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

    public void setDnsProcessClient(DnsProcessClient dnsProcessClient) {
        this.dnsProcessClient = dnsProcessClient;
    }

    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }

}
