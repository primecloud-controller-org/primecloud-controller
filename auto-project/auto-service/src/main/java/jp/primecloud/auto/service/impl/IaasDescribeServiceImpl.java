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
import java.util.Collections;
import java.util.List;

import javax.crypto.Cipher;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.CloudstackAddress;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformVcloudStorageType;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;
import jp.primecloud.auto.entity.crud.VcloudKeyPair;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.IaasDescribeService;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.dto.AddressDto;
import jp.primecloud.auto.service.dto.KeyPairDto;
import jp.primecloud.auto.service.dto.NetworkDto;
import jp.primecloud.auto.service.dto.SecurityGroupDto;
import jp.primecloud.auto.service.dto.StorageTypeDto;
import jp.primecloud.auto.service.dto.SubnetDto;
import jp.primecloud.auto.service.dto.ZoneDto;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

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

        ArrayList<ZoneDto> retArray = new ArrayList<ZoneDto>();
        if (!"".equals(result)) {
            String[] resultArray = result.split("##");
            for (String item : resultArray) {
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
        ArrayList<KeyPairDto> retArray = new ArrayList<KeyPairDto>();
        Platform platform = platformDao.read(platformNo);

        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())
                || PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())
                || PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
            String result = gateway.describeKeyPairs();
            if (!"".equals(result)) {
                String[] resultArray = result.split("##");
                for (String item : resultArray) {
                    String[] resultArray2 = item.split("#");
                    KeyPairDto pair = new KeyPairDto();
                    //今はこれしか利用されていないので
                    pair.setKeyName(resultArray2[0]);
                    retArray.add(pair);
                }
            }
        } else if (PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())) {
            List<VcloudKeyPair> vcloudKeyPairs = vcloudKeyPairDao.readByUserNoAndPlatformNo(userNo, platformNo);
            // ソート
            Collections.sort(vcloudKeyPairs, Comparators.COMPARATOR_VCLOUD_KEY_PAIR);
            for (VcloudKeyPair vcloudKeyPair : vcloudKeyPairs) {
                KeyPairDto pair = new KeyPairDto();
                pair.setKeyNo(vcloudKeyPair.getKeyNo());
                pair.setKeyName(vcloudKeyPair.getKeyName());
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

        ArrayList<SecurityGroupDto> retArray = new ArrayList<SecurityGroupDto>();
        if (!"".equals(result)) {
            String[] resultArray = result.split("##");
            for (String item : resultArray) {
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

        ArrayList<SubnetDto> retArray = new ArrayList<SubnetDto>();
        if (!"".equals(result)) {
            String[] resultArray = result.split("##");
            for (String item : resultArray) {
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
    public List<StorageTypeDto> getStorageTypes(Long userNo, Long platformNo) {
        List<StorageTypeDto> retArray = new ArrayList<StorageTypeDto>();
        List<PlatformVcloudStorageType> platformVcloudStorageTypes = platformVcloudStorageTypeDao
                .readByPlatformNo(platformNo);
        Collections.sort(platformVcloudStorageTypes, Comparators.COMPARATOR_PLATFORM_VCLOUD_STORAGE_TYPE);
        for (PlatformVcloudStorageType storageType : platformVcloudStorageTypes) {
            StorageTypeDto storageTypeDto = new StorageTypeDto(storageType);
            retArray.add(storageTypeDto);
        }

        return retArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NetworkDto> getNetworks(Long userNo, Long platformNo) {
        IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
        // IaasGateWay処理
        String result = gateway.describeNetworks();
        List<NetworkDto> tmpRetArray = new ArrayList<NetworkDto>();
        List<NetworkDto> retArray = new ArrayList<NetworkDto>();
        if (!"".equals(result)) {
            //IaasGateway戻り値の例(データが存在しない場合はNone)
            //出力形式 ネットワーク名#GATEWAY=*.*.*.*,NETMASK=*.*.*.*,DNS1=*.*.*.*,DNS2=*.*.*.*,RANGEF=*.*.*.*,RANGET=*.*.*.*,PRIMARY=TrueもしくはFalse
            //例)common-internet-direct#GATEWAY=172.16.221.254,NETMASK=255.255.255.0,DNS1=172.31.12.102,DNS2=172.16.1.1,RANGEF=172.16.221.1,RANGET=172.16.221.253,PRIMARY=True
            String[] resultArray = result.split("##");
            for (String item : resultArray) {
                String[] resultArray2 = item.split("#");
                NetworkDto networkDto = new NetworkDto();
                networkDto.setNetworkName(resultArray2[0]);
                String[] keyAndValues = resultArray2[1].split(",");
                for (String keyAndValue : keyAndValues) {
                    String[] array = keyAndValue.split("=");
                    if (StringUtils.equals("GATEWAY", array[0])) {
                        networkDto.setGateWay(StringUtils.equals("None", array[1]) ? "" : array[1]);
                    } else if (StringUtils.equals("NETMASK", array[0])) {
                        networkDto.setNetmask(StringUtils.equals("None", array[1]) ? "" : array[1]);
                    } else if (StringUtils.equals("DNS1", array[0])) {
                        networkDto.setDns1(StringUtils.equals("None", array[1]) ? "" : array[1]);
                    } else if (StringUtils.equals("DNS2", array[0])) {
                        networkDto.setDns2(StringUtils.equals("None", array[1]) ? "" : array[1]);
                    } else if (StringUtils.equals("RANGEF", array[0])) {
                        networkDto.setRangeFrom(StringUtils.equals("None", array[1]) ? "" : array[1]);
                    } else if (StringUtils.equals("RANGET", array[0])) {
                        networkDto.setRangeTo(StringUtils.equals("None", array[1]) ? "" : array[1]);
                    } else if (StringUtils.equals("PRIMARY", array[0])) {
                        networkDto.setIsPcc(StringUtils.equals("True", array[1]));
                    }
                }
                tmpRetArray.add(networkDto);
            }
            //PCCネットワークを先頭で返す
            for (NetworkDto networkDto : tmpRetArray) {
                if (networkDto.isPcc()) {
                    retArray.add(networkDto);
                }
            }
            for (NetworkDto networkDto : tmpRetArray) {
                if (!networkDto.isPcc()) {
                    retArray.add(networkDto);
                }
            }
        }
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
        // TODO CLOUD BRANCHING
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())) {
            // ユーザに紐づくAWSアドレス情報を取得
            List<AwsAddress> allAddresses = awsAddressDao.readByUserNo(userNo);
            for (AwsAddress address : allAddresses) {
                if (platformNo.equals(address.getPlatformNo())) {
                    addresses.add(new AddressDto(address));
                }
            }
        } else if (PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())) {
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
    public List<SubnetDto> getAzureSubnets(Long userNo, Long platformNo, String networkName) {
        String result = "";
        if (StringUtils.isNotEmpty(networkName)) {
            IaasGatewayWrapper gateway = iaasGatewayFactory.createIaasGateway(userNo, platformNo);
            result = gateway.describeAzureSubnets(networkName);
        }
        ArrayList<SubnetDto> retArray = new ArrayList<SubnetDto>();
        if (!"".equals(result)) {
            String[] resultArray = result.split("##");
            for (String item : resultArray) {
                String[] resultArray2 = item.split("#");
                SubnetDto subnet = new SubnetDto();
                subnet.setSubnetId(resultArray2[0]);
                subnet.setCidrBlock(resultArray2[1]);
                retArray.add(subnet);
            }
        }
        return retArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hasIpAddresse(Long platformNo, Long instanceNo, String ipAddress) {
        // 引数チェック
        if (platformNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "platformNo");
        }
        if (instanceNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "instanceNo");
        }
        if (StringUtils.isEmpty(ipAddress)) {
            throw new AutoApplicationException("ECOMMON-000003", "ipAddress");
        }

        List<VcloudInstanceNetwork> vcloudInstanceNetworks = vcloudInstanceNetworkDao.readByPlatformNo(platformNo);
        for (VcloudInstanceNetwork instanceNetwork : vcloudInstanceNetworks) {
            if (!instanceNo.equals(instanceNetwork.getInstanceNo())
                    && instanceNetwork.getIpAddress().equals(ipAddress)) {
                Instance instance = instanceDao.read(instanceNetwork.getInstanceNo());
                return instance.getInstanceName();
            }
        }
        return null;
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
        if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType()) == false
                && PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType()) == false) {
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
