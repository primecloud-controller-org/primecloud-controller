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

import jp.primecloud.auto.aws.AwsClientFactory;
import jp.primecloud.auto.aws.amazon.AmazonAwsClientFactory;
import jp.primecloud.auto.aws.typica.EucaAwsClientFactory;
import jp.primecloud.auto.aws.wrapper.LoggingAwsClientWrapper;
import jp.primecloud.auto.aws.wrapper.SynchronizedAwsClientWrapper;
import jp.primecloud.auto.dao.crud.AwsCertificateDao;
import jp.primecloud.auto.dao.crud.PlatformAwsDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.dao.crud.ProxyDao;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.Proxy;
import jp.primecloud.auto.exception.AutoException;

import org.apache.commons.lang.BooleanUtils;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsProcessClientFactory {

    protected Integer describeInterval = 15;

    protected boolean logging = false;

    protected boolean sync = true;

    protected String volumeType;

    protected PlatformDao platformDao;

    protected PlatformAwsDao platformAwsDao;

    protected AwsCertificateDao awsCertificateDao;

    protected ProxyDao proxyDao;

    /**
     * TODO: メソッドコメント
     * 
     * @param userNo
     * @param platformNo
     * @return
     */
    public AwsProcessClient createAwsProcessClient(Long userNo, Long platformNo) {
        Platform platform = platformDao.read(platformNo);
        if (platform == null) {
            throw new AutoException("EPROCESS-000004", platformNo);
        }

        PlatformAws platformAws = platformAwsDao.read(platformNo);
        if (platformAws == null) {
            throw new AutoException("EPROCESS-000011", platformNo);
        }

        return createAwsProcessClient(userNo, platform, platformAws);
    }

    protected AwsProcessClient createAwsProcessClient(Long userNo, Platform platform, PlatformAws platformAws) {
        // AwsCertificateを取得
        AwsCertificate awsCertificate = awsCertificateDao.read(userNo, platformAws.getPlatformNo());
        if (awsCertificate == null) {
            throw new AutoException("EPROCESS-000006", userNo, platformAws.getPlatformNo());
        }

        return createAwsClient(platform, platformAws, awsCertificate);
    }

    protected AwsProcessClient createAwsClient(Platform platform, PlatformAws platformAws, AwsCertificate awsCertificate) {
        AwsClientFactory factory;
        if (BooleanUtils.isTrue(platformAws.getEuca())) {
            factory = new EucaAwsClientFactory();
        } else {
            factory = new AmazonAwsClientFactory();
        }

        factory.setHost(platformAws.getHost());
        factory.setPort(platformAws.getPort());
        factory.setSecure(platformAws.getSecure());

        if (BooleanUtils.isTrue(platform.getProxy())) {
            Proxy proxy = proxyDao.read();
            factory.setProxyHost(proxy.getHost());
            factory.setProxyPort(proxy.getPort());
            factory.setProxyUser(proxy.getUser());
            factory.setProxyPassword(proxy.getPassword());
        }

        // Clientの作成
        AmazonEC2 ec2Client = factory
                .createEc2Client(awsCertificate.getAwsAccessId(), awsCertificate.getAwsSecretKey());
        AmazonElasticLoadBalancing elbClient = factory.createElbClient(awsCertificate.getAwsAccessId(),
                awsCertificate.getAwsSecretKey());

        // ログ出力用Clientでラップ
        if (logging) {
            LoggingAwsClientWrapper loggingAwsClientWrapper = new LoggingAwsClientWrapper();
            ec2Client = loggingAwsClientWrapper.wrap(ec2Client);
            elbClient = loggingAwsClientWrapper.wrap(elbClient);
        }

        // 同期実行用Clientでラップ
        if (sync) {
            SynchronizedAwsClientWrapper synchronizedAwsClientWrapper = new SynchronizedAwsClientWrapper();
            ec2Client = synchronizedAwsClientWrapper.wrap(ec2Client);
            elbClient = synchronizedAwsClientWrapper.wrap(elbClient);
        }

        AwsProcessClient client = new AwsProcessClient(awsCertificate.getUserNo(), platform, platformAws, ec2Client,
                elbClient);

        client.setDescribeInterval(describeInterval);
        client.setVolumeType(volumeType);

        return client;
    }

    public void setDescribeInterval(Integer describeInterval) {
        this.describeInterval = describeInterval;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public void setVolumeType(String volumeType) {
        this.volumeType = volumeType;
    }

    public void setPlatformDao(PlatformDao platformDao) {
        this.platformDao = platformDao;
    }

    public void setPlatformAwsDao(PlatformAwsDao platformAwsDao) {
        this.platformAwsDao = platformAwsDao;
    }

    public void setAwsCertificateDao(AwsCertificateDao awsCertificateDao) {
        this.awsCertificateDao = awsCertificateDao;
    }

    public void setProxyDao(ProxyDao proxyDao) {
        this.proxyDao = proxyDao;
    }

}
