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
package jp.primecloud.auto.process.aws;

import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.process.DnsProcessClient;
import jp.primecloud.auto.process.DnsProcessClientFactory;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class AwsDnsProcess extends ServiceSupport {

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

        if (BooleanUtils.isTrue(platform.getInternal())) {
            // 内部のプラットフォームの場合
            PlatformAws platformAws = platformAwsDao.read(platform.getPlatformNo());

            if (BooleanUtils.isTrue(platformAws.getEuca())) {
                // Eucalyptusの場合
                startDnsEuca(instanceNo);
            } else {
                // Amazon EC2の場合
                if (BooleanUtils.isTrue(platformAws.getVpc())) {
                    // VPC環境の場合
                    startDnsNormal(instanceNo);
                } else {
                    // 非VPC環境の場合
                    startDnsClassic(instanceNo);
                }
            }
        } else {
            // 外部のプラットフォームの場合
            Image image = imageDao.read(instance.getImageNo());
            if (StringUtils.startsWithIgnoreCase(image.getOs(), "windows")) {
                // Windowsイメージの場合、VPNを使用していないものとする
                startDnsClassic(instanceNo);
            } else {
                // VPNを使用している場合
                startDnsVpn(instanceNo);
            }
        }

        // イベントログ出力
        instance = instanceDao.read(instanceNo);
        processLogger.debug(null, instance, "DnsRegist", new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param instanceNo
     */
    public void stopDns(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Platform platform = platformDao.read(instance.getPlatformNo());

        if (BooleanUtils.isTrue(platform.getInternal())) {
            // 内部のプラットフォームの場合
            PlatformAws platformAws = platformAwsDao.read(platform.getPlatformNo());

            if (BooleanUtils.isTrue(platformAws.getEuca())) {
                // Eucalyptusの場合
                stopDnsNormal(instanceNo);
            } else {
                // Amazon EC2の場合
                if (BooleanUtils.isTrue(platformAws.getVpc())) {
                    // VPC環境の場合
                    stopDnsNormal(instanceNo);
                } else {
                    // 非VPC環境の場合
                    stopDnsClassic(instanceNo);
                }
            }
        } else {
            // 外部のプラットフォームの場合
            Image image = imageDao.read(instance.getImageNo());
            if (StringUtils.startsWithIgnoreCase(image.getOs(), "windows")) {
                // Windowsイメージの場合、VPNを使用していないものとする
                stopDnsClassic(instanceNo);
            } else {
                // VPNを使用している場合
                stopDnsNormal(instanceNo);
            }
        }

        // イベントログ出力
        processLogger.debug(null, instance, "DnsUnregist", new Object[] { instance.getFqdn(), instance.getPublicIp() });
    }

    protected void startDnsNormal(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // InstanceにIPアドレスが登録済みの場合はスキップする
        if (!StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        // PrivateIpAddressをIPアドレスとする
        String fqdn = instance.getFqdn();
        String publicIp = awsInstance.getPrivateIpAddress();
        String privateIp = awsInstance.getPrivateIpAddress();

        // 正引きの追加
        dnsProcessClient.addForward(fqdn, publicIp);

        // 逆引きの追加
        dnsProcessClient.addReverse(fqdn, publicIp);

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void startDnsClassic(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // 最新のAwsInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), awsInstance.getDnsName())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        String fqdn = instance.getFqdn();
        String publicIp = awsInstance.getIpAddress();
        String privateIp = awsInstance.getPrivateIpAddress();

        // CNAMEの追加
        dnsProcessClient.addCanonicalName(fqdn, awsInstance.getDnsName());

        // データベースの更新
        instance.setPublicIp(publicIp);
        instance.setPrivateIp(privateIp);
        instanceDao.update(instance);
    }

    protected void startDnsEuca(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // 最新のAwsInstance情報がInstanceに登録されている場合はスキップする
        if (StringUtils.equals(instance.getPublicIp(), awsInstance.getDnsName())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        // DnsName, PrivateDnsNameをそのままIPアドレスとして使用する
        String fqdn = instance.getFqdn();
        String publicIp = awsInstance.getDnsName();
        String privateIp = awsInstance.getPrivateDnsName();

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
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // InstanceにIPアドレスが登録済みの場合はスキップする
        if (!StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        // IPアドレスを正引きにより取得する（正引きの追加はインスタンス内で行う）
        String fqdn = instance.getFqdn();
        String publicIp = dnsProcessClient.resolveHost(fqdn); // VPNインタフェースのIPアドレス
        String privateIp = awsInstance.getPrivateIpAddress();

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

    protected void stopDnsClassic(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);

        // IPアドレスがない場合はスキップ
        if (StringUtils.isEmpty(instance.getPublicIp())) {
            return;
        }

        DnsProcessClient dnsProcessClient = dnsProcessClientFactory.createDnsProcessClient();

        String fqdn = instance.getFqdn();

        // CNAMEの削除
        dnsProcessClient.deleteCanonicalName(fqdn);

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
