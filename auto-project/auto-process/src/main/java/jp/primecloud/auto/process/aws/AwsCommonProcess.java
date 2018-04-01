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

import java.util.List;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.VolumeState;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class AwsCommonProcess extends ServiceSupport {

    public Instance waitInstance(AwsProcessClient awsProcessClient, String instanceId) {
        // インスタンスの処理待ち
        Instance instance;
        while (true) {
            try {
                Thread.sleep(1000L * awsProcessClient.getDescribeInterval());
            } catch (InterruptedException ignore) {
            }

            instance = describeInstance(awsProcessClient, instanceId);
            InstanceStateName state;
            try {
                state = InstanceStateName.fromValue(instance.getState().getName());
            } catch (IllegalArgumentException e) {
                // 予期しないステータス
                AutoException exception = new AutoException("EPROCESS-000104", instanceId,
                        instance.getState().getName());
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(instance));
                throw exception;
            }

            // 安定状態のステータスになったら終了
            if (state == InstanceStateName.Running || state == InstanceStateName.Terminated
                    || state == InstanceStateName.Stopped) {
                break;
            }
        }

        return instance;
    }

    public Instance describeInstance(AwsProcessClient awsProcessClient, String instanceId) {
        // 単一インスタンスの参照
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withInstanceIds(instanceId);
        DescribeInstancesResult result = awsProcessClient.getEc2Client().describeInstances(request);
        List<Reservation> reservations = result.getReservations();

        // API実行結果チェック
        if (reservations.size() == 0) {
            // インスタンスが存在しない場合
            throw new AutoException("EPROCESS-000101", instanceId);

        } else if (reservations.size() > 1) {
            // インスタンスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000102", instanceId);
            exception.addDetailInfo("result=" + reservations);
            throw exception;
        }

        List<com.amazonaws.services.ec2.model.Instance> instances = reservations.get(0).getInstances();

        if (instances.size() == 0) {
            // インスタンスが存在しない場合
            throw new AutoException("EPROCESS-000101", instanceId);

        } else if (instances.size() > 1) {
            // インスタンスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000103", instanceId);
            exception.addDetailInfo("result=" + instances);
            throw exception;
        }

        return instances.get(0);
    }

    public Volume waitVolume(AwsProcessClient awsProcessClient, String volumeId) {
        // ボリュームの処理待ち
        Volume volume;
        while (true) {
            try {
                Thread.sleep(1000L * awsProcessClient.getDescribeInterval());
            } catch (InterruptedException ignore) {
            }

            volume = describeVolume(awsProcessClient, volumeId);
            VolumeState state;
            try {
                state = VolumeState.fromValue(volume.getState());
            } catch (IllegalArgumentException e) {
                // 予期しないステータス
                AutoException exception = new AutoException("EPROCESS-000112", volume, volume.getState());
                exception.addDetailInfo("result=" + ReflectionToStringBuilder.toString(volume));
                throw exception;
            }

            // 安定状態のステータスになったら終了
            if (state == VolumeState.Available || state == VolumeState.InUse || state == VolumeState.Deleted
                    || state == VolumeState.Error) {
                break;
            }
        }

        return volume;
    }

    public Volume describeVolume(AwsProcessClient awsProcessClient, String volumeId) {
        // 単一ボリュームの参照
        DescribeVolumesRequest request = new DescribeVolumesRequest();
        request.withVolumeIds(volumeId);
        DescribeVolumesResult result = awsProcessClient.getEc2Client().describeVolumes(request);
        List<Volume> volumes = result.getVolumes();

        // API実行結果チェック
        if (volumes.size() == 0) {
            // ボリュームが存在しない場合
            throw new AutoException("EPROCESS-000110", volumeId);

        } else if (volumes.size() > 1) {
            // ボリュームを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000111", volumeId);
            exception.addDetailInfo("result=" + volumes);
            throw exception;
        }

        return volumes.get(0);
    }

    public Address describeAddress(AwsProcessClient awsProcessClient, String publicIp) {
        // 単一アドレスの参照
        DescribeAddressesRequest request = new DescribeAddressesRequest();
        request.withPublicIps(publicIp);
        DescribeAddressesResult result = awsProcessClient.getEc2Client().describeAddresses(request);
        List<Address> addresses = result.getAddresses();

        // API実行結果チェック
        if (addresses.size() == 0) {
            // アドレスが存在しない場合
            throw new AutoException("EPROCESS-000117", publicIp);

        } else if (addresses.size() > 1) {
            // アドレスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000118", publicIp);
            exception.addDetailInfo("result=" + addresses);
            throw exception;
        }

        return addresses.get(0);
    }

    public LoadBalancerDescription describeLoadBalancer(AwsProcessClient awsProcessClient, String loadBalancerName) {
        // 単一ロードバランサの参照
        DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
        request.withLoadBalancerNames(loadBalancerName);
        DescribeLoadBalancersResult result = awsProcessClient.getElbClient().describeLoadBalancers(request);
        List<LoadBalancerDescription> descriptions = result.getLoadBalancerDescriptions();

        // API実行結果チェック
        if (descriptions.size() == 0) {
            // アドレスが存在しない場合
            throw new AutoException("EPROCESS-000131", loadBalancerName);

        } else if (descriptions.size() > 1) {
            // アドレスを複数参照できた場合
            AutoException exception = new AutoException("EPROCESS-000132", loadBalancerName);
            exception.addDetailInfo("result=" + descriptions);
            throw exception;
        }

        return descriptions.get(0);
    }

    public Image describeImage(AwsProcessClient awsProcessClient, String imageId) {
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.withImageIds(imageId);
        DescribeImagesResult result = awsProcessClient.getEc2Client().describeImages(request);
        List<Image> images = result.getImages();

        if (images.isEmpty()) {
            return null;
        }

        return images.get(0);
    }

    public List<AvailabilityZone> describeAvailabilityZones(AwsProcessClient awsProcessClient) {
        DescribeAvailabilityZonesRequest request = new DescribeAvailabilityZonesRequest();
        DescribeAvailabilityZonesResult result = awsProcessClient.getEc2Client().describeAvailabilityZones(request);
        List<AvailabilityZone> availabilityZones = result.getAvailabilityZones();

        return availabilityZones;
    }

    public List<SecurityGroup> describeSecurityGroupsByVpcId(AwsProcessClient awsProcessClient, String vpcId) {
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        request.withFilters(new Filter().withName("vpc-id").withValues(vpcId));
        DescribeSecurityGroupsResult result = awsProcessClient.getEc2Client().describeSecurityGroups(request);
        List<SecurityGroup> securityGroups = result.getSecurityGroups();

        return securityGroups;
    }

    public List<Subnet> describeSubnetsByVpcId(AwsProcessClient awsProcessClient, String vpcId) {
        DescribeSubnetsRequest request = new DescribeSubnetsRequest();
        request.withFilters(new Filter().withName("vpc-id").withValues(vpcId));
        DescribeSubnetsResult result = awsProcessClient.getEc2Client().describeSubnets(request);
        List<Subnet> subnets = result.getSubnets();

        return subnets;
    }

    public void createTag(AwsProcessClient awsProcessClient, String resourceId, List<Tag> tags) {
        CreateTagsRequest request = new CreateTagsRequest();
        request.withResources(resourceId);
        request.withTags(tags);
        awsProcessClient.getEc2Client().createTags(request);

        // ログ出力
        if (log.isInfoEnabled()) {
            for (Tag tag : tags) {
                log.info(MessageUtils.getMessage("IPROCESS-100154", resourceId, tag.getKey(), tag.getValue()));
            }
        }
    }

}
