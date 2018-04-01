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

import java.util.Date;
import java.util.List;

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.AwsInstance;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.time.DateFormatUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AllocateAddressRequest;
import com.amazonaws.services.ec2.model.AllocateAddressResult;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.DisassociateAddressRequest;
import com.amazonaws.services.ec2.model.DomainType;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsAddressProcess extends ServiceSupport {

    protected AwsCommonProcess awsCommonProcess;

    protected ProcessLogger processLogger;

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @return
     */
    public AwsAddress createAddress(AwsProcessClient awsProcessClient) {
        // Elastic IPの確保
        AllocateAddressRequest request = new AllocateAddressRequest();
        if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getVpc())) {
            request.withDomain(DomainType.Vpc);
        }

        String publicIp;
        try {
            AllocateAddressResult result = awsProcessClient.getEc2Client().allocateAddress(request);
            publicIp = result.getPublicIp();

        } catch (AutoException e) {
            // Elastic IPの上限オーバーの場合
            if (e.getCause() instanceof AmazonServiceException
                    && "AddressLimitExceeded".equals(((AmazonServiceException) e.getCause()).getErrorCode())) {
                throw new AutoApplicationException("EPROCESS-000134");
            }

            throw e;
        }

        // イベントログ出力
        processLogger.debug(null, null, "AwsElasticIpAllocate", new Object[] {
                awsProcessClient.getPlatform().getPlatformName(), publicIp });

        // AWSアドレス情報を作成
        AwsAddress awsAddress = new AwsAddress();
        awsAddress.setUserNo(awsProcessClient.getUserNo());
        awsAddress.setPlatformNo(awsProcessClient.getPlatform().getPlatformNo());
        awsAddress.setPublicIp(publicIp);
        awsAddress.setComment("Allocate at " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        awsAddressDao.create(awsAddress);

        return awsAddress;
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param addressNo
     */
    public void deleteAddress(AwsProcessClient awsProcessClient, Long addressNo) {
        // AWSアドレス情報の存在チェック
        AwsAddress awsAddress = awsAddressDao.read(addressNo);
        if (awsAddress == null) {
            return;
        }

        // Elastic IPのチェック
        if (StringUtils.isEmpty(awsAddress.getPublicIp())) {
            // Elastic IPが空ならAWSアドレス情報を削除して終了
            awsAddressDao.delete(awsAddress);
            return;
        }

        // Elastic IPを解放
        try {
            ReleaseAddressRequest request = new ReleaseAddressRequest();

            // VPCの場合
            if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getVpc())) {
                // 割り当てIDを取得する
                Address address = awsCommonProcess.describeAddress(awsProcessClient, awsAddress.getPublicIp());
                request.withAllocationId(address.getAllocationId());
            }
            // 非VPCの場合
            else {
                request.withPublicIp(awsAddress.getPublicIp());
            }

            awsProcessClient.getEc2Client().releaseAddress(request);

            // イベントログ出力
            processLogger.debug(null, null, "AwsElasticIpRelease", new Object[] {
                    awsProcessClient.getPlatform().getPlatformName(), awsAddress.getPublicIp() });

        } catch (Exception ignore) {
            // Elastic IPが実際には存在しない場合などに備えて、警告ログを出力して例外を握りつぶす
            log.warn(ignore.getMessage());
        }

        // AWSアドレス情報を削除
        awsAddressDao.delete(awsAddress);
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void startAddress(AwsProcessClient awsProcessClient, Long instanceNo) {
        // アドレス情報の取得
        List<AwsAddress> awsAddresses = awsAddressDao.readByInstanceNo(instanceNo);

        if (awsAddresses.size() > 1) {
            // アドレス情報が複数ある場合
            AutoException exception = new AutoException("EPROCESS-000202", instanceNo);
            exception.addDetailInfo("result=" + awsAddresses);
            throw exception;
        }

        if (awsAddresses.isEmpty()) {
            // アドレス情報がない場合は終了
            return;
        }

        AwsAddress awsAddress = awsAddresses.get(0);

        // インスタンスIDがない場合、インスタンスに関連付ける
        if (StringUtils.isEmpty(awsAddress.getInstanceId())) {
            // アドレスのステータスチェック
            Address address = checkAvailableAddress(awsProcessClient, instanceNo, awsAddress.getAddressNo());

            // アドレスの関連付け
            associateAddress(awsProcessClient, instanceNo, awsAddress.getAddressNo(), address);

            // インスタンスのアドレス情報を更新
            updateInstanceAddress(awsProcessClient, instanceNo, awsAddress.getAddressNo());
        }
    }

    /**
     * TODO: メソッドコメント
     * 
     * @param awsProcessClient
     * @param instanceNo
     */
    public void stopAddress(AwsProcessClient awsProcessClient, Long instanceNo) {
        // アドレス情報の取得
        List<AwsAddress> awsAddresses = awsAddressDao.readByInstanceNo(instanceNo);

        if (awsAddresses.size() > 1) {
            // アドレス情報が複数ある場合
            AutoException exception = new AutoException("EPROCESS-000202", instanceNo);
            exception.addDetailInfo("result=" + awsAddresses);
            throw exception;
        }

        if (awsAddresses.isEmpty()) {
            // アドレス情報がない場合は終了
            return;
        }

        AwsAddress awsAddress = awsAddresses.get(0);

        // インスタンスIDがある場合、インスタンスから切り離す
        if (StringUtils.isNotEmpty(awsAddress.getInstanceId())) {
            try {
                // アドレスのステータスチェック
                Address address = checkAssociatedAddress(awsProcessClient, instanceNo, awsAddress.getAddressNo());

                // アドレスの切り離し
                disassociateAddress(awsProcessClient, instanceNo, awsAddress.getAddressNo(), address);

                // インスタンスのアドレス情報を更新
                updateInstanceAddress(awsProcessClient, instanceNo, awsAddress.getAddressNo());

            } catch (AutoException ignore) {
                // 情報が不整合（インスタンス異常終了時など）の場合、警告ログと後始末のみ行う
                log.warn(ignore.getMessage());

                awsAddress = awsAddressDao.read(awsAddress.getAddressNo());
                awsAddress.setInstanceId(null);
                awsAddressDao.update(awsAddress);
            }
        }
    }

    public Address checkAvailableAddress(AwsProcessClient awsProcessClient, Long instanceNo, Long addressNo) {
        AwsAddress awsAddress = awsAddressDao.read(addressNo);
        String publicIp = awsAddress.getPublicIp();

        Address address = awsCommonProcess.describeAddress(awsProcessClient, publicIp);

        if (!StringUtils.isEmpty(address.getInstanceId())) {
            // アドレスが何らかのインスタンスに関連付けられている場合
            throw new AutoException("EPROCESS-000119", publicIp, address.getInstanceId());
        }

        return address;
    }

    public void associateAddress(AwsProcessClient awsProcessClient, Long instanceNo, Long addressNo, Address address) {
        AwsAddress awsAddress = awsAddressDao.read(addressNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // アドレスの関連付け
        AssociateAddressRequest request = new AssociateAddressRequest();
        request.withInstanceId(awsInstance.getInstanceId());

        // VPCの場合
        if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getVpc())) {
            // 割り当てIDを指定する
            request.withAllocationId(address.getAllocationId());
        }
        // 非VPCの場合
        else {
            request.withPublicIp(awsAddress.getPublicIp());
        }

        awsProcessClient.getEc2Client().associateAddress(request);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100131", awsAddress.getPublicIp(), awsInstance.getInstanceId()));
        }

        // イベントログ出力
        Instance instance2 = instanceDao.read(instanceNo);
        processLogger.debug(null, instance2, "AwsElasticIpAssociate", new Object[] { awsInstance.getInstanceId(),
                awsAddress.getPublicIp() });

        // データベースの更新
        awsAddress.setInstanceId(awsInstance.getInstanceId());
        awsAddressDao.update(awsAddress);
    }

    public void updateInstanceAddress(AwsProcessClient awsProcessClient, Long instanceNo, Long addressNo) {
        AwsAddress awsAddress = awsAddressDao.read(addressNo);
        AwsInstance awsInstance = awsInstanceDao.read(instanceNo);

        // 最新のインスタンスを取得
        com.amazonaws.services.ec2.model.Instance instance = null;
        long timeout = 180 * 1000L;
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(1000L * awsProcessClient.getDescribeInterval());
            } catch (InterruptedException ignore) {
            }

            instance = awsCommonProcess.describeInstance(awsProcessClient, awsInstance.getInstanceId());

            if (!StringUtils.equals(instance.getPublicDnsName(), awsInstance.getDnsName())
                    && !StringUtils.equals(instance.getPublicDnsName(), instance.getPrivateDnsName())) {
                // DnsNameが変更されており、PrivateDnsNameと違っていれば、最新インスタンス情報を取得できたとみなす（非VPC環境用）
                break;
            }

            // アドレスを関連付けた場合
            if (StringUtils.isNotEmpty(awsInstance.getInstanceId())) {
                if (StringUtils.equals(instance.getPublicIpAddress(), awsAddress.getPublicIp())) {
                    // PublicIPが変更されており、ElasticIPと同じであれば、最新インスタンス情報を取得できたとみなす
                    break;
                }
            }
            // アドレスを切り離した場合
            else {
                if (!StringUtils.equals(instance.getPublicIpAddress(), awsAddress.getPublicIp())) {
                    // PublicIPが変更されており、ElasticIPと違っていれば、最新インスタンス情報を取得できたとみなす
                    break;
                }
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                // タイムアウト発生時
                AutoException exception = new AutoException("EPROCESS-000204", awsInstance.getInstanceId());
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
                throw exception;
            }
        }

        // データベースの更新
        awsInstance.setDnsName(instance.getPublicDnsName());
        awsInstance.setPrivateDnsName(instance.getPrivateDnsName());
        awsInstance.setIpAddress(instance.getPublicIpAddress());
        awsInstance.setPrivateIpAddress(instance.getPrivateIpAddress());
        awsInstanceDao.update(awsInstance);
    }

    public Address checkAssociatedAddress(AwsProcessClient awsProcessClient, Long instanceNo, Long addressNo) {
        AwsAddress awsAddress = awsAddressDao.read(addressNo);
        String publicIp = awsAddress.getPublicIp();
        String instanceId = awsAddress.getInstanceId();

        // アドレスが関連付けられているかどうかのチェック
        Address address = awsCommonProcess.describeAddress(awsProcessClient, publicIp);

        if (StringUtils.isEmpty(address.getInstanceId())) {
            // アドレスがどのインスタンスにも関連付けられていない場合
            throw new AutoException("EPROCESS-000120", publicIp, instanceId);

        } else if (!StringUtils.equals(instanceId, address.getInstanceId())) {
            // アドレスが他インスタンスに関連付けられている場合
            throw new AutoException("EPROCESS-000121", publicIp, instanceId, address.getInstanceId());
        }

        return address;
    }

    public void disassociateAddress(AwsProcessClient awsProcessClient, Long instanceNo, Long addressNo, Address address) {
        AwsAddress awsAddress = awsAddressDao.read(addressNo);

        // アドレスの切り離し
        DisassociateAddressRequest request = new DisassociateAddressRequest();

        // VPCの場合
        if (BooleanUtils.isTrue(awsProcessClient.getPlatformAws().getVpc())) {
            // 関連付けIDを指定する
            request.withAssociationId(address.getAssociationId());
        }
        // 非VPCの場合
        else {
            request.withPublicIp(awsAddress.getPublicIp());
        }

        awsProcessClient.getEc2Client().disassociateAddress(request);

        // ログ出力
        if (log.isInfoEnabled()) {
            log.info(MessageUtils.getMessage("IPROCESS-100132", awsAddress.getPublicIp(), awsAddress.getInstanceId()));
        }

        //イベントログ出力
        Instance instance = instanceDao.read(instanceNo);
        processLogger.debug(null, instance, "AwsElasticIpDisassociate", new Object[] { awsAddress.getInstanceId(),
                awsAddress.getPublicIp() });

        // データベースの更新
        awsAddress.setInstanceId(null);
        awsAddressDao.update(awsAddress);
    }

    public void setAwsCommonProcess(AwsCommonProcess awsCommonProcess) {
        this.awsCommonProcess = awsCommonProcess;
    }

    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }

}
