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
package jp.primecloud.auto.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.Cipher;

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.process.aws.AwsProcessClient;
import jp.primecloud.auto.process.aws.AwsProcessClientFactory;
import jp.primecloud.auto.service.AwsDescribeService;
import jp.primecloud.auto.service.ServiceSupport;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.openssl.PEMReader;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.GetPasswordDataRequest;
import com.amazonaws.services.ec2.model.GetPasswordDataResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsDescribeServiceImpl extends ServiceSupport implements AwsDescribeService {

    protected AwsProcessClientFactory awsProcessClientFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AvailabilityZone> getAvailabilityZones(Long userNo, Long platformNo) {
        // アベイラビリティゾーンを取得
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(userNo, platformNo);
        DescribeAvailabilityZonesRequest request = new DescribeAvailabilityZonesRequest();
        DescribeAvailabilityZonesResult result = awsProcessClient.getEc2Client().describeAvailabilityZones(request);
        List<AvailabilityZone> availabilityZones = result.getAvailabilityZones();

        // ソート
        Collections.sort(availabilityZones, Comparators.COMPARATOR_AVAILABILITY_ZONE);

        return availabilityZones;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KeyPairInfo> getKeyPairs(Long userNo, Long platformNo) {
        // キーペアを取得
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(userNo, platformNo);
        DescribeKeyPairsRequest request = new DescribeKeyPairsRequest();
        DescribeKeyPairsResult result = awsProcessClient.getEc2Client().describeKeyPairs(request);
        List<KeyPairInfo> keyPairs = result.getKeyPairs();

        // ソート
        Collections.sort(keyPairs, Comparators.COMPARATOR_KEY_PAIR_INFO);

        return keyPairs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SecurityGroup> getSecurityGroups(Long userNo, Long platformNo) {
        // セキュリティグループを取得
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(userNo, platformNo);
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        PlatformAws platformAws = platformAwsDao.read(platformNo);
        if (BooleanUtils.isTrue(platformAws.getVpc())) {
            // VPCの場合、VPC IDが同じものを抽出
            request.withFilters(new Filter().withName("vpc-id").withValues(platformAws.getVpcId()));
        } else {
            // 非VPCの場合、VPC IDが空のものを抽出
            request.withFilters(new Filter().withName("vpc-id").withValues(""));
        }
        DescribeSecurityGroupsResult result = awsProcessClient.getEc2Client().describeSecurityGroups(request);
        List<SecurityGroup> securityGroups = result.getSecurityGroups();

        // ソート
        Collections.sort(securityGroups, Comparators.COMPARATOR_SECURITY_GROUP);

        return securityGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Subnet> getSubnets(Long userNo, Long platformNo) {
        // VPCかどうかのチェック
        PlatformAws platformAws = platformAwsDao.read(platformNo);
        if (BooleanUtils.isNotTrue(platformAws.getVpc())) {
            // 非VPCの場合、サブネットはない
            return new ArrayList<Subnet>();
        }

        // サブネットを取得
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(userNo, platformNo);
        DescribeSubnetsRequest request = new DescribeSubnetsRequest();
        request.withFilters(new Filter().withName("vpc-id").withValues(platformAws.getVpcId()));
        DescribeSubnetsResult result = awsProcessClient.getEc2Client().describeSubnets(request);
        List<Subnet> subnets = result.getSubnets();

        // プラットフォームにサブネットが指定されている場合、そのサブネットのみに制限する
        if (StringUtils.isNotEmpty(awsProcessClient.getPlatformAws().getSubnetId())) {
            List<String> subnetIds = new ArrayList<String>();
            for (String subnetId : StringUtils.split(awsProcessClient.getPlatformAws().getSubnetId(), ",")) {
                subnetIds.add(subnetId.trim());
            }

            List<Subnet> subnets2 = new ArrayList<Subnet>();
            for (Subnet subnet : subnets) {
                if (subnetIds.contains(subnet.getSubnetId())) {
                    subnets2.add(subnet);
                }
            }
            subnets = subnets2;
        }

        // ソート
        Collections.sort(subnets, Comparators.COMPARATOR_SUBNET);

        return subnets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AwsAddress> getAddresses(Long userNo, Long platformNo) {
        // ユーザに紐づくAWSアドレス情報を取得
        List<AwsAddress> allAwsAddresses = awsAddressDao.readByUserNo(userNo);

        // プラットフォームが一致するものを抽出
        List<AwsAddress> awsAddresses = new ArrayList<AwsAddress>();
        for (AwsAddress awsAddress : allAwsAddresses) {
            if (platformNo.equals(awsAddress.getPlatformNo())) {
                awsAddresses.add(awsAddress);
            }
        }

        // ソート
        Collections.sort(awsAddresses, Comparators.COMPARATOR_AWS_ADDRESS);

        return awsAddresses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword(Long instanceNo, String privateKey) {
        // PrivateKeyの取得
        PrivateKey key = toPrivateKey(privateKey);

        // パスワードデータの取得
        String passwordData = getPasswordData(instanceNo);

        // パスワードデータの復号
        String password = decryptPasswordData(passwordData, key);

        return password;
    }

    protected PrivateKey toPrivateKey(String privateKey) {
        StringReader reader = new StringReader(privateKey);

        // プライベートキーを読み込み
        PEMReader pemReader = new PEMReader(reader);
        try {
            Object pemObject = pemReader.readObject();
            KeyPair keyPair = KeyPair.class.cast(pemObject);
            return keyPair.getPrivate();
        } catch (Exception e) {
            // プライベートキーの読み込みに失敗した場合
            throw new AutoApplicationException("ESERVICE-000705", e);
        } finally {
            try {
                pemReader.close();
            } catch (IOException ignore) {
            }
        }
    }

    protected String getPasswordData(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(farm.getUserNo(),
                instance.getPlatformNo());

        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // パスワードデータの取得
        GetPasswordDataRequest request = new GetPasswordDataRequest();
        request.withInstanceId(awsInstance.getInstanceId());
        GetPasswordDataResult result = awsProcessClient.getEc2Client().getPasswordData(request);
        return result.getPasswordData();
    }

    protected String decryptPasswordData(String passwordData, PrivateKey privateKey) {
        // パスワードの復号
        try {
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] binary = cipher.doFinal(Base64.decodeBase64(passwordData.getBytes()));
            return new String(binary);
        } catch (GeneralSecurityException e) {
            // パスワードを復号できなかった場合
            throw new AutoApplicationException("ESERVICE-000706", e);
        }
    }

    public void setAwsProcessClientFactory(AwsProcessClientFactory awsProcessClientFactory) {
        this.awsProcessClientFactory = awsProcessClientFactory;
    }

}
