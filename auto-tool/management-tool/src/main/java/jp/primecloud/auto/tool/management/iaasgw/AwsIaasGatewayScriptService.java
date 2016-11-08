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
package jp.primecloud.auto.tool.management.iaasgw;

import java.util.List;

import jp.primecloud.auto.aws.AwsClientFactory;
import jp.primecloud.auto.aws.amazon.AmazonAwsClientFactory;
import jp.primecloud.auto.aws.typica.EucaAwsClientFactory;
import jp.primecloud.auto.aws.wrapper.SynchronizedAwsClientWrapper;
import jp.primecloud.auto.entity.crud.AwsCertificate;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.Proxy;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.tool.management.main.SQLMain;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.ImportKeyPairRequest;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.Subnet;

public class AwsIaasGatewayScriptService extends IaasGatewayScriptService {

    protected AmazonEC2 ec2Client;

    public AwsIaasGatewayScriptService(Long userNo, Platform platform) throws AutoException {
        super(userNo, platform);

        try {
            String sql = "SELECT * FROM AWS_CERTIFICATE WHERE USER_NO = " + userNo + " AND PLATFORM_NO = "
                    + platform.getPlatformNo();
            List<AwsCertificate> awsCertificates = SQLMain.selectExecuteWithResult(sql, AwsCertificate.class);
            AwsCertificate awsCertificate = awsCertificates.get(0);

            String sql2 = "SELECT * FROM PLATFORM_AWS WHERE PLATFORM_NO = " + platform.getPlatformNo();
            List<PlatformAws> platformAwses = SQLMain.selectExecuteWithResult(sql2, PlatformAws.class);
            PlatformAws platformAws = platformAwses.get(0);

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
                String sql3 = "SELECT * FROM PROXY";
                List<Proxy> proxies = SQLMain.selectExecuteWithResult(sql3, Proxy.class);
                Proxy proxy = proxies.get(0);

                factory.setProxyHost(proxy.getHost());
                factory.setProxyPort(proxy.getPort());
                factory.setProxyUser(proxy.getUser());
                factory.setProxyPassword(proxy.getPassword());
            }

            ec2Client = factory.createEc2Client(awsCertificate.getAwsAccessId(), awsCertificate.getAwsSecretKey());

            SynchronizedAwsClientWrapper synchronizedAwsClientWrapper = new SynchronizedAwsClientWrapper();
            ec2Client = synchronizedAwsClientWrapper.wrap(ec2Client);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void importKeyPair(String keyName, String publicKey) throws AutoException {
        // キーペアがすでに登録されていたら何もしない
        DescribeKeyPairsRequest request = new DescribeKeyPairsRequest();
        DescribeKeyPairsResult result = ec2Client.describeKeyPairs(request);
        List<KeyPairInfo> keyPairs = result.getKeyPairs();

        for (KeyPairInfo keyPair : keyPairs) {
            if (keyPair.getKeyName().equals(keyName)) {
                log.info(platform.getPlatformName() + " の " + keyName + " はすでに登録されている為、キーのインポートをスキップします");
                System.out.println("IMPORT_SKIPPED");
                return;
            }
        }

        // インポート
        ImportKeyPairRequest request2 = new ImportKeyPairRequest();
        request2.withKeyName(keyName);
        request2.withPublicKeyMaterial(publicKey);
        ec2Client.importKeyPair(request2);

        log.info(keyName + "のキーをインポートしました。");
    }

    @Override
    public boolean hasSubnets(String vpcId) throws AutoException {
        if (StringUtils.isEmpty(vpcId)) {
            log.info(platform.getPlatformName() + " にvpcIdが有りません");
            System.out.println("VPCID_EMPTY");
            return false;
        }

        DescribeSubnetsRequest request = new DescribeSubnetsRequest();
        request.withFilters(new Filter().withName("vpc-id").withValues(vpcId));
        DescribeSubnetsResult result = ec2Client.describeSubnets(request);
        List<Subnet> subnets = result.getSubnets();

        if (subnets.isEmpty()) {
            log.info(platform.getPlatformName() + " にサブネットが有りません");
            System.out.println("SUBNET_EMPTY");
            return false;
        }

        return true;
    }

}
