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
package jp.primecloud.auto.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.CloudstackAddress;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.dto.AddressDto;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.ZoneDto;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class IaasDescribeServiceImpl extends ServiceSupport implements IaasDescribeService {

    protected IaasGatewayFactory iaasGatewayFactory;

    protected EventLogger eventLogger;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ZoneDto> getAvailabilityZones(Long userNo, Long platformNo) {
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
        String result = gateway.describeAvailabilityZones();

        ArrayList<ZoneDto> retArray = new  ArrayList<ZoneDto>();
        if(!"".equals(result)){
            String[] resultArray = result.split("##");
            for (String item:resultArray){
                String[] resultArray2 = item.split("#");
                ZoneDto zone = new ZoneDto();
                //今はこれしか利用されていないので
                zone.setZoneName(resultArray2[0]);
                zone.setZoneId(resultArray2[1]);
                retArray.add(zone);
            }
        }
        return retArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KeyPairDto> getKeyPairs(Long userNo, Long platformNo) {
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
        String result = gateway.describeKeyPairs();

        ArrayList<KeyPairDto> retArray = new  ArrayList<KeyPairDto>();
        if(!"".equals(result)){
            String[] resultArray = result.split("##");
            for (String item:resultArray){
                String[] resultArray2 = item.split("#");
                KeyPairDto pair = new KeyPairDto();
                //今はこれしか利用されていないので
                pair.setKeyName(resultArray2[0]);
                retArray.add(pair);
            }
        }
        return retArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SecurityGroupDto> getSecurityGroups(Long userNo, Long platformNo, String vpcId) {
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
        String result = gateway.describeSecurityGroups(vpcId);

        ArrayList<SecurityGroupDto> retArray = new  ArrayList<SecurityGroupDto>();
        if(!"".equals(result)){
            String[] resultArray = result.split("##");
            for (String item:resultArray){
                String[] resultArray2 = item.split("#");
                SecurityGroupDto group = new SecurityGroupDto();
                //今はこれしか利用されていないので
                group.setGroupName(resultArray2[0]);
                retArray.add(group);
            }
        }
        return retArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SubnetDto> getSubnets(Long userNo, Long platformNo, String vpcId) {
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
        String result = gateway.describeSubnets(vpcId);

        ArrayList<SubnetDto> retArray = new  ArrayList<SubnetDto>();
        if(!"".equals(result)){
            String[] resultArray = result.split("##");
            for (String item:resultArray){
                String[] resultArray2 = item.split("#");
                SubnetDto subnet = new SubnetDto();
                //今はこれしか利用されていないので
                subnet.setSubnetId(resultArray2[0]);
                subnet.setZoneid(resultArray2[1]);
                subnet.setCidrBlock(resultArray2[2]);
                retArray.add(subnet);
            }
        }
        // VPCをサポートしていない場合は無視する
        return retArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AddressDto> getAddresses(Long userNo, Long platformNo) {
        // 引数チェック
        if (userNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "userNo");
        }
        if (platformNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "platformNo");
        }

        Platform platform = platformDao.read(platformNo);

        List<AddressDto> addresses = new ArrayList<AddressDto>();
        if ("aws".equals(platform.getPlatformType())) {
            // ユーザに紐づくAWSアドレス情報を取得
            List<AwsAddress> allAddresses = awsAddressDao.readByUserNo(userNo);
            for (AwsAddress address : allAddresses) {
                if (platformNo.equals(address.getPlatformNo())) {
                    addresses.add(new AddressDto(address));
                }
            }
        } else if ("cloudstack".equals(platform.getPlatformType())){
            // ユーザに紐づくCloudstackアドレス情報を取得
            List<CloudstackAddress> allAddresses = cloudstackAddressDao.readByAccount(userNo);
            for (CloudstackAddress address : allAddresses) {
                if (platformNo.equals(address.getPlatformNo())) {
                    addresses.add(new AddressDto(address));
                }
            }

        }
        return addresses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createAddress(Long userNo, Long platformNo) {
        // 引数チェック
        if (userNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "userNo");
        }
        if (platformNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "platformNo");
        }

        // プラットフォームのチェック
        Platform platform = platformDao.read(platformNo);
        if ("aws".equals(platform.getPlatformType()) == false && "cloudstack".equals(platform.getPlatformType()) == false) {
            throw new AutoApplicationException("ESERVICE-000702", platformNo);
        }

        // ユーザの存在チェック
        User user = userDao.read(userNo);
        if (user == null) {
            throw new AutoApplicationException("ESERVICE-000701", userNo);
        }

        // Elastic IPの確保
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
        String addressNo;
        try {
            addressNo = gateway.allocateAddress();
        } catch (AutoException e) {
            // Elastic IPの上限オーバーの場合
            if ("EAWS-000101".equals(e.getCode())) {
                throw new AutoApplicationException("ESERVICE-000703");
            }

            throw e;
        }
        return Long.valueOf(addressNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAddress(Long userNo, Long platformNo, Long addressNo) {
        // 引数チェック
        if (addressNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "addressNo");
        }

        // Elastic IPの解放
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
        gateway.releaseAddress(String.valueOf(addressNo));

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
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(farm.getUserNo(), instance.getPlatformNo());

        // パスワードデータの取得
        try {
            String passwordData = gateway.getPasswordData(String.valueOf(instanceNo));
            return passwordData;
        } catch (AutoException e) {
            if ("EPROCESS-000133".equals(e.getCode())) {
                // パスワードを取得できなかった場合
                throw new AutoApplicationException("ESERVICE-000704", e);
            }
            throw e;
        }
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

    /**
     * iaasGatewayFactoryを設定します。
     *
     * @param iaasGatewayFactory iaasGatewayFactory
     */
    public void setIaasGatewayFactory(IaasGatewayFactory iaasGatewayFactory) {
        this.iaasGatewayFactory = iaasGatewayFactory;
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
