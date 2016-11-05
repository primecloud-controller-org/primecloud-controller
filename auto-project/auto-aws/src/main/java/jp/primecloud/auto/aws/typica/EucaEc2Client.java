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
package jp.primecloud.auto.aws.typica;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.aws.typica.converter.AddressConverter;
import jp.primecloud.auto.aws.typica.converter.AvailabilityZoneConverter;
import jp.primecloud.auto.aws.typica.converter.ImageConverter;
import jp.primecloud.auto.aws.typica.converter.InstanceStateChangeTerminateConverter;
import jp.primecloud.auto.aws.typica.converter.KeyPairConverter;
import jp.primecloud.auto.aws.typica.converter.KeyPairInfoConverter;
import jp.primecloud.auto.aws.typica.converter.RegionConverter;
import jp.primecloud.auto.aws.typica.converter.ReservationConverter;
import jp.primecloud.auto.aws.typica.converter.SecurityGroupConverter;
import jp.primecloud.auto.aws.typica.converter.SnapshotConverter;
import jp.primecloud.auto.aws.typica.converter.VolumeAttachmentConverter;
import jp.primecloud.auto.aws.typica.converter.VolumeConverter;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.waiters.AmazonEC2Waiters;
import com.xerox.amazonws.ec2.AddressInfo;
import com.xerox.amazonws.ec2.AttachmentInfo;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.GroupDescription;
import com.xerox.amazonws.ec2.ImageDescription;
import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.RegionInfo;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.SnapshotInfo;
import com.xerox.amazonws.ec2.TerminatingInstanceDescription;
import com.xerox.amazonws.ec2.VolumeInfo;

/**
 * <p>
 * TODO: クラスコメント
 * </p>
 * 
 */
public class EucaEc2Client implements AmazonEC2 {

    protected Jec2 jec2;

    public EucaEc2Client(Jec2 jec2) {
        this.jec2 = jec2;
    }

    public Jec2 getJec2() {
        return jec2;
    }

    @Override
    public void setEndpoint(String endpoint) {
    }

    @Override
    public void setRegion(com.amazonaws.regions.Region region) {
    }

    @Override
    public AcceptReservedInstancesExchangeQuoteResult acceptReservedInstancesExchangeQuote(
            AcceptReservedInstancesExchangeQuoteRequest acceptReservedInstancesExchangeQuoteRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AcceptVpcPeeringConnectionResult acceptVpcPeeringConnection(
            AcceptVpcPeeringConnectionRequest acceptVpcPeeringConnectionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AcceptVpcPeeringConnectionResult acceptVpcPeeringConnection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AllocateAddressResult allocateAddress(AllocateAddressRequest allocateAddressRequest) {
        try {
            String publicIp = jec2.allocateAddress();
            return new AllocateAddressResult().withPublicIp(publicIp);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public AllocateAddressResult allocateAddress() {
        return allocateAddress(new AllocateAddressRequest());
    }

    @Override
    public AllocateHostsResult allocateHosts(AllocateHostsRequest allocateHostsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AssignPrivateIpAddressesResult assignPrivateIpAddresses(
            AssignPrivateIpAddressesRequest assignPrivateIpAddressesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AssociateAddressResult associateAddress(AssociateAddressRequest associateAddressRequest) {
        try {
            jec2.associateAddress(associateAddressRequest.getInstanceId(), associateAddressRequest.getPublicIp());
            return new AssociateAddressResult();
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public AssociateDhcpOptionsResult associateDhcpOptions(AssociateDhcpOptionsRequest associateDhcpOptionsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AssociateRouteTableResult associateRouteTable(AssociateRouteTableRequest associateRouteTableRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttachClassicLinkVpcResult attachClassicLinkVpc(AttachClassicLinkVpcRequest attachClassicLinkVpcRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttachInternetGatewayResult attachInternetGateway(AttachInternetGatewayRequest attachInternetGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttachNetworkInterfaceResult attachNetworkInterface(
            AttachNetworkInterfaceRequest attachNetworkInterfaceRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttachVolumeResult attachVolume(AttachVolumeRequest attachVolumeRequest) {
        try {
            AttachmentInfo info = jec2.attachVolume(attachVolumeRequest.getVolumeId(),
                    attachVolumeRequest.getInstanceId(), attachVolumeRequest.getDevice());
            VolumeAttachment attachment = new VolumeAttachmentConverter().convert(info);
            return new AttachVolumeResult().withAttachment(attachment);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public AttachVpnGatewayResult attachVpnGateway(AttachVpnGatewayRequest attachVpnGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthorizeSecurityGroupEgressResult authorizeSecurityGroupEgress(
            AuthorizeSecurityGroupEgressRequest authorizeSecurityGroupEgressRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthorizeSecurityGroupIngressResult authorizeSecurityGroupIngress(
            AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BundleInstanceResult bundleInstance(BundleInstanceRequest bundleInstanceRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelBundleTaskResult cancelBundleTask(CancelBundleTaskRequest cancelBundleTaskRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelConversionTaskResult cancelConversionTask(CancelConversionTaskRequest cancelConversionTaskRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelExportTaskResult cancelExportTask(CancelExportTaskRequest cancelExportTaskRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelImportTaskResult cancelImportTask(CancelImportTaskRequest cancelImportTaskRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelImportTaskResult cancelImportTask() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelReservedInstancesListingResult cancelReservedInstancesListing(
            CancelReservedInstancesListingRequest cancelReservedInstancesListingRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelSpotFleetRequestsResult cancelSpotFleetRequests(
            CancelSpotFleetRequestsRequest cancelSpotFleetRequestsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelSpotInstanceRequestsResult cancelSpotInstanceRequests(
            CancelSpotInstanceRequestsRequest cancelSpotInstanceRequestsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmProductInstanceResult confirmProductInstance(
            ConfirmProductInstanceRequest confirmProductInstanceRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CopyImageResult copyImage(CopyImageRequest copyImageRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CopySnapshotResult copySnapshot(CopySnapshotRequest copySnapshotRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateCustomerGatewayResult createCustomerGateway(CreateCustomerGatewayRequest createCustomerGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateDhcpOptionsResult createDhcpOptions(CreateDhcpOptionsRequest createDhcpOptionsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateFlowLogsResult createFlowLogs(CreateFlowLogsRequest createFlowLogsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateImageResult createImage(CreateImageRequest createImageRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateInstanceExportTaskResult createInstanceExportTask(
            CreateInstanceExportTaskRequest createInstanceExportTaskRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateInternetGatewayResult createInternetGateway(CreateInternetGatewayRequest createInternetGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateInternetGatewayResult createInternetGateway() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateKeyPairResult createKeyPair(CreateKeyPairRequest createKeyPairRequest) {
        try {
            com.xerox.amazonws.ec2.KeyPairInfo info = jec2.createKeyPair(createKeyPairRequest.getKeyName());
            KeyPair keyPair = new KeyPairConverter().convert(info);
            return new CreateKeyPairResult().withKeyPair(keyPair);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public CreateNatGatewayResult createNatGateway(CreateNatGatewayRequest createNatGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateNetworkAclResult createNetworkAcl(CreateNetworkAclRequest createNetworkAclRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateNetworkAclEntryResult createNetworkAclEntry(CreateNetworkAclEntryRequest createNetworkAclEntryRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateNetworkInterfaceResult createNetworkInterface(
            CreateNetworkInterfaceRequest createNetworkInterfaceRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreatePlacementGroupResult createPlacementGroup(CreatePlacementGroupRequest createPlacementGroupRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateReservedInstancesListingResult createReservedInstancesListing(
            CreateReservedInstancesListingRequest createReservedInstancesListingRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateRouteResult createRoute(CreateRouteRequest createRouteRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateRouteTableResult createRouteTable(CreateRouteTableRequest createRouteTableRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateSecurityGroupResult createSecurityGroup(CreateSecurityGroupRequest createSecurityGroupRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateSnapshotResult createSnapshot(CreateSnapshotRequest createSnapshotRequest) {
        try {
            SnapshotInfo info = jec2.createSnapshot(createSnapshotRequest.getVolumeId());
            Snapshot snapshot = new SnapshotConverter().convert(info);
            return new CreateSnapshotResult().withSnapshot(snapshot);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public CreateSpotDatafeedSubscriptionResult createSpotDatafeedSubscription(
            CreateSpotDatafeedSubscriptionRequest createSpotDatafeedSubscriptionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateSubnetResult createSubnet(CreateSubnetRequest createSubnetRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateTagsResult createTags(CreateTagsRequest createTagsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateVolumeResult createVolume(CreateVolumeRequest createVolumeRequest) {
        String size = createVolumeRequest.getSize() != null ? createVolumeRequest.getSize().toString() : null;

        try {
            VolumeInfo info = jec2.createVolume(size, createVolumeRequest.getSnapshotId(),
                    createVolumeRequest.getAvailabilityZone());
            Volume volume = new VolumeConverter().convert(info);
            return new CreateVolumeResult().withVolume(volume);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public CreateVpcResult createVpc(CreateVpcRequest createVpcRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateVpcEndpointResult createVpcEndpoint(CreateVpcEndpointRequest createVpcEndpointRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateVpcPeeringConnectionResult createVpcPeeringConnection(
            CreateVpcPeeringConnectionRequest createVpcPeeringConnectionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateVpcPeeringConnectionResult createVpcPeeringConnection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateVpnConnectionResult createVpnConnection(CreateVpnConnectionRequest createVpnConnectionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateVpnConnectionRouteResult createVpnConnectionRoute(
            CreateVpnConnectionRouteRequest createVpnConnectionRouteRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateVpnGatewayResult createVpnGateway(CreateVpnGatewayRequest createVpnGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteCustomerGatewayResult deleteCustomerGateway(DeleteCustomerGatewayRequest deleteCustomerGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteDhcpOptionsResult deleteDhcpOptions(DeleteDhcpOptionsRequest deleteDhcpOptionsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteFlowLogsResult deleteFlowLogs(DeleteFlowLogsRequest deleteFlowLogsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteInternetGatewayResult deleteInternetGateway(DeleteInternetGatewayRequest deleteInternetGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteKeyPairResult deleteKeyPair(DeleteKeyPairRequest deleteKeyPairRequest) {
        try {
            jec2.deleteKeyPair(deleteKeyPairRequest.getKeyName());
            return new DeleteKeyPairResult();
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DeleteNatGatewayResult deleteNatGateway(DeleteNatGatewayRequest deleteNatGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteNetworkAclResult deleteNetworkAcl(DeleteNetworkAclRequest deleteNetworkAclRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteNetworkAclEntryResult deleteNetworkAclEntry(DeleteNetworkAclEntryRequest deleteNetworkAclEntryRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteNetworkInterfaceResult deleteNetworkInterface(
            DeleteNetworkInterfaceRequest deleteNetworkInterfaceRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeletePlacementGroupResult deletePlacementGroup(DeletePlacementGroupRequest deletePlacementGroupRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteRouteResult deleteRoute(DeleteRouteRequest deleteRouteRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteRouteTableResult deleteRouteTable(DeleteRouteTableRequest deleteRouteTableRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteSecurityGroupResult deleteSecurityGroup(DeleteSecurityGroupRequest deleteSecurityGroupRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteSnapshotResult deleteSnapshot(DeleteSnapshotRequest deleteSnapshotRequest) {
        try {
            jec2.deleteSnapshot(deleteSnapshotRequest.getSnapshotId());
            return new DeleteSnapshotResult();
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DeleteSpotDatafeedSubscriptionResult deleteSpotDatafeedSubscription(
            DeleteSpotDatafeedSubscriptionRequest deleteSpotDatafeedSubscriptionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteSpotDatafeedSubscriptionResult deleteSpotDatafeedSubscription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteSubnetResult deleteSubnet(DeleteSubnetRequest deleteSubnetRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteTagsResult deleteTags(DeleteTagsRequest deleteTagsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteVolumeResult deleteVolume(DeleteVolumeRequest deleteVolumeRequest) {
        try {
            jec2.deleteVolume(deleteVolumeRequest.getVolumeId());
            return new DeleteVolumeResult();
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DeleteVpcResult deleteVpc(DeleteVpcRequest deleteVpcRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteVpcEndpointsResult deleteVpcEndpoints(DeleteVpcEndpointsRequest deleteVpcEndpointsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteVpcPeeringConnectionResult deleteVpcPeeringConnection(
            DeleteVpcPeeringConnectionRequest deleteVpcPeeringConnectionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteVpnConnectionResult deleteVpnConnection(DeleteVpnConnectionRequest deleteVpnConnectionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteVpnConnectionRouteResult deleteVpnConnectionRoute(
            DeleteVpnConnectionRouteRequest deleteVpnConnectionRouteRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteVpnGatewayResult deleteVpnGateway(DeleteVpnGatewayRequest deleteVpnGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeregisterImageResult deregisterImage(DeregisterImageRequest deregisterImageRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeAccountAttributesResult describeAccountAttributes(
            DescribeAccountAttributesRequest describeAccountAttributesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeAccountAttributesResult describeAccountAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeAddressesResult describeAddresses(DescribeAddressesRequest describeAddressesRequest) {
        List<String> publicIps = describeAddressesRequest.getPublicIps();

        try {
            List<AddressInfo> infos = jec2.describeAddresses(publicIps);

            if (publicIps != null && !publicIps.isEmpty()) {
                // アドレスが指定されている場合、指定されたものを抽出する
                // （Eucalyptusでは、アドレスを指定しても全てのアドレス情報が返される）
                List<AddressInfo> newInfos = new ArrayList<AddressInfo>();
                for (AddressInfo info : infos) {
                    if (publicIps.contains(info.getPublicIp())) {
                        newInfos.add(info);
                    }
                }
                infos = newInfos;
            }

            // 関連付けられていないアドレスのinstanceIdを空文字にする
            // （EC2では空文字になるが、Eucalyptusでは"available"が設定されて返される）
            for (int i = 0; i < infos.size(); i++) {
                AddressInfo info = infos.get(i);
                if ("available".equals(info.getInstanceId())) {
                    infos.set(i, new AddressInfo(info.getPublicIp(), ""));
                }
            }

            List<Address> addresses = new AddressConverter().convert(infos);
            return new DescribeAddressesResult().withAddresses(addresses);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeAddressesResult describeAddresses() {
        return describeAddresses(new DescribeAddressesRequest());
    }

    @Override
    public DescribeAvailabilityZonesResult describeAvailabilityZones(
            DescribeAvailabilityZonesRequest describeAvailabilityZonesRequest) {
        try {
            List<com.xerox.amazonws.ec2.AvailabilityZone> zones = jec2
                    .describeAvailabilityZones(describeAvailabilityZonesRequest.getZoneNames());
            List<AvailabilityZone> availabilityZones = new AvailabilityZoneConverter().convert(zones);
            return new DescribeAvailabilityZonesResult().withAvailabilityZones(availabilityZones);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeAvailabilityZonesResult describeAvailabilityZones() {
        return describeAvailabilityZones(new DescribeAvailabilityZonesRequest());
    }

    @Override
    public DescribeBundleTasksResult describeBundleTasks(DescribeBundleTasksRequest describeBundleTasksRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeBundleTasksResult describeBundleTasks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeClassicLinkInstancesResult describeClassicLinkInstances(
            DescribeClassicLinkInstancesRequest describeClassicLinkInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeClassicLinkInstancesResult describeClassicLinkInstances() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeConversionTasksResult describeConversionTasks(
            DescribeConversionTasksRequest describeConversionTasksRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeConversionTasksResult describeConversionTasks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeCustomerGatewaysResult describeCustomerGateways(
            DescribeCustomerGatewaysRequest describeCustomerGatewaysRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeCustomerGatewaysResult describeCustomerGateways() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeDhcpOptionsResult describeDhcpOptions(DescribeDhcpOptionsRequest describeDhcpOptionsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeDhcpOptionsResult describeDhcpOptions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeExportTasksResult describeExportTasks(DescribeExportTasksRequest describeExportTasksRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeExportTasksResult describeExportTasks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeFlowLogsResult describeFlowLogs(DescribeFlowLogsRequest describeFlowLogsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeFlowLogsResult describeFlowLogs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeHostReservationOfferingsResult describeHostReservationOfferings(
            DescribeHostReservationOfferingsRequest describeHostReservationOfferingsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeHostReservationsResult describeHostReservations(
            DescribeHostReservationsRequest describeHostReservationsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeHostsResult describeHosts(DescribeHostsRequest describeHostsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeHostsResult describeHosts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeIdFormatResult describeIdFormat(DescribeIdFormatRequest describeIdFormatRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeIdFormatResult describeIdFormat() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeIdentityIdFormatResult describeIdentityIdFormat(
            DescribeIdentityIdFormatRequest describeIdentityIdFormatRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeImageAttributeResult describeImageAttribute(
            DescribeImageAttributeRequest describeImageAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeImagesResult describeImages(DescribeImagesRequest describeImagesRequest) {
        try {
            List<ImageDescription> descriptions = jec2.describeImages(describeImagesRequest.getImageIds(),
                    describeImagesRequest.getOwners(), describeImagesRequest.getExecutableUsers(), null);
            List<Image> images = new ImageConverter().convert(descriptions);
            return new DescribeImagesResult().withImages(images);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeImagesResult describeImages() {
        return describeImages(new DescribeImagesRequest());
    }

    @Override
    public DescribeImportImageTasksResult describeImportImageTasks(
            DescribeImportImageTasksRequest describeImportImageTasksRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeImportImageTasksResult describeImportImageTasks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeImportSnapshotTasksResult describeImportSnapshotTasks(
            DescribeImportSnapshotTasksRequest describeImportSnapshotTasksRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeImportSnapshotTasksResult describeImportSnapshotTasks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeInstanceAttributeResult describeInstanceAttribute(
            DescribeInstanceAttributeRequest describeInstanceAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeInstanceStatusResult describeInstanceStatus(
            DescribeInstanceStatusRequest describeInstanceStatusRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeInstanceStatusResult describeInstanceStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeInstancesResult describeInstances(DescribeInstancesRequest describeInstancesRequest) {
        try {
            List<ReservationDescription> descriptions = jec2.describeInstances(describeInstancesRequest
                    .getInstanceIds());
            List<Reservation> reservations = new ReservationConverter().convert(descriptions);
            return new DescribeInstancesResult().withReservations(reservations);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeInstancesResult describeInstances() {
        return describeInstances(new DescribeInstancesRequest());
    }

    @Override
    public DescribeInternetGatewaysResult describeInternetGateways(
            DescribeInternetGatewaysRequest describeInternetGatewaysRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeInternetGatewaysResult describeInternetGateways() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeKeyPairsResult describeKeyPairs(DescribeKeyPairsRequest describeKeyPairsRequest) {
        try {
            List<com.xerox.amazonws.ec2.KeyPairInfo> infos = jec2.describeKeyPairs(describeKeyPairsRequest
                    .getKeyNames());
            List<KeyPairInfo> keyPairs = new KeyPairInfoConverter().convert(infos);
            return new DescribeKeyPairsResult().withKeyPairs(keyPairs);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeKeyPairsResult describeKeyPairs() {
        return describeKeyPairs(new DescribeKeyPairsRequest());
    }

    @Override
    public DescribeMovingAddressesResult describeMovingAddresses(
            DescribeMovingAddressesRequest describeMovingAddressesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeMovingAddressesResult describeMovingAddresses() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeNatGatewaysResult describeNatGateways(DescribeNatGatewaysRequest describeNatGatewaysRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeNetworkAclsResult describeNetworkAcls(DescribeNetworkAclsRequest describeNetworkAclsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeNetworkAclsResult describeNetworkAcls() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeNetworkInterfaceAttributeResult describeNetworkInterfaceAttribute(
            DescribeNetworkInterfaceAttributeRequest describeNetworkInterfaceAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeNetworkInterfacesResult describeNetworkInterfaces(
            DescribeNetworkInterfacesRequest describeNetworkInterfacesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeNetworkInterfacesResult describeNetworkInterfaces() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribePlacementGroupsResult describePlacementGroups(
            DescribePlacementGroupsRequest describePlacementGroupsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribePlacementGroupsResult describePlacementGroups() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribePrefixListsResult describePrefixLists(DescribePrefixListsRequest describePrefixListsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribePrefixListsResult describePrefixLists() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeRegionsResult describeRegions(DescribeRegionsRequest describeRegionsRequest) {
        try {
            List<RegionInfo> infos = jec2.describeRegions(describeRegionsRequest.getRegionNames());
            List<Region> regions = new RegionConverter().convert(infos);
            return new DescribeRegionsResult().withRegions(regions);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeRegionsResult describeRegions() {
        return describeRegions(new DescribeRegionsRequest());
    }

    @Override
    public DescribeReservedInstancesResult describeReservedInstances(
            DescribeReservedInstancesRequest describeReservedInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeReservedInstancesResult describeReservedInstances() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeReservedInstancesListingsResult describeReservedInstancesListings(
            DescribeReservedInstancesListingsRequest describeReservedInstancesListingsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeReservedInstancesListingsResult describeReservedInstancesListings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeReservedInstancesModificationsResult describeReservedInstancesModifications(
            DescribeReservedInstancesModificationsRequest describeReservedInstancesModificationsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeReservedInstancesModificationsResult describeReservedInstancesModifications() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeReservedInstancesOfferingsResult describeReservedInstancesOfferings(
            DescribeReservedInstancesOfferingsRequest describeReservedInstancesOfferingsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeReservedInstancesOfferingsResult describeReservedInstancesOfferings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeRouteTablesResult describeRouteTables(DescribeRouteTablesRequest describeRouteTablesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeRouteTablesResult describeRouteTables() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeScheduledInstanceAvailabilityResult describeScheduledInstanceAvailability(
            DescribeScheduledInstanceAvailabilityRequest describeScheduledInstanceAvailabilityRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeScheduledInstancesResult describeScheduledInstances(
            DescribeScheduledInstancesRequest describeScheduledInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSecurityGroupReferencesResult describeSecurityGroupReferences(
            DescribeSecurityGroupReferencesRequest describeSecurityGroupReferencesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSecurityGroupsResult describeSecurityGroups(
            DescribeSecurityGroupsRequest describeSecurityGroupsRequest) {
        try {
            List<GroupDescription> descriptions = jec2.describeSecurityGroups(describeSecurityGroupsRequest
                    .getGroupNames());
            List<SecurityGroup> securityGroups = new SecurityGroupConverter().convert(descriptions);
            return new DescribeSecurityGroupsResult().withSecurityGroups(securityGroups);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeSecurityGroupsResult describeSecurityGroups() {
        return new DescribeSecurityGroupsResult();
    }

    @Override
    public DescribeSnapshotAttributeResult describeSnapshotAttribute(
            DescribeSnapshotAttributeRequest describeSnapshotAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSnapshotsResult describeSnapshots(DescribeSnapshotsRequest describeSnapshotsRequest) {
        try {
            List<SnapshotInfo> infos = jec2.describeSnapshots(describeSnapshotsRequest.getSnapshotIds());
            List<Snapshot> snapshots = new SnapshotConverter().convert(infos);
            return new DescribeSnapshotsResult().withSnapshots(snapshots);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeSnapshotsResult describeSnapshots() {
        return describeSnapshots(new DescribeSnapshotsRequest());
    }

    @Override
    public DescribeSpotDatafeedSubscriptionResult describeSpotDatafeedSubscription(
            DescribeSpotDatafeedSubscriptionRequest describeSpotDatafeedSubscriptionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotDatafeedSubscriptionResult describeSpotDatafeedSubscription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotFleetInstancesResult describeSpotFleetInstances(
            DescribeSpotFleetInstancesRequest describeSpotFleetInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotFleetRequestHistoryResult describeSpotFleetRequestHistory(
            DescribeSpotFleetRequestHistoryRequest describeSpotFleetRequestHistoryRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotFleetRequestsResult describeSpotFleetRequests(
            DescribeSpotFleetRequestsRequest describeSpotFleetRequestsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotFleetRequestsResult describeSpotFleetRequests() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotInstanceRequestsResult describeSpotInstanceRequests(
            DescribeSpotInstanceRequestsRequest describeSpotInstanceRequestsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotInstanceRequestsResult describeSpotInstanceRequests() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotPriceHistoryResult describeSpotPriceHistory(
            DescribeSpotPriceHistoryRequest describeSpotPriceHistoryRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSpotPriceHistoryResult describeSpotPriceHistory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeStaleSecurityGroupsResult describeStaleSecurityGroups(
            DescribeStaleSecurityGroupsRequest describeStaleSecurityGroupsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSubnetsResult describeSubnets(DescribeSubnetsRequest describeSubnetsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeSubnetsResult describeSubnets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeTagsResult describeTags(DescribeTagsRequest describeTagsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeTagsResult describeTags() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVolumeAttributeResult describeVolumeAttribute(
            DescribeVolumeAttributeRequest describeVolumeAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVolumeStatusResult describeVolumeStatus(DescribeVolumeStatusRequest describeVolumeStatusRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVolumeStatusResult describeVolumeStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVolumesResult describeVolumes(DescribeVolumesRequest describeVolumesRequest) {
        try {
            List<VolumeInfo> infos = jec2.describeVolumes(describeVolumesRequest.getVolumeIds());
            List<Volume> volumes = new VolumeConverter().convert(infos);
            return new DescribeVolumesResult().withVolumes(volumes);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DescribeVolumesResult describeVolumes() {
        return describeVolumes(new DescribeVolumesRequest());
    }

    @Override
    public DescribeVpcAttributeResult describeVpcAttribute(DescribeVpcAttributeRequest describeVpcAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcClassicLinkResult describeVpcClassicLink(
            DescribeVpcClassicLinkRequest describeVpcClassicLinkRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcClassicLinkResult describeVpcClassicLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcClassicLinkDnsSupportResult describeVpcClassicLinkDnsSupport(
            DescribeVpcClassicLinkDnsSupportRequest describeVpcClassicLinkDnsSupportRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcEndpointServicesResult describeVpcEndpointServices(
            DescribeVpcEndpointServicesRequest describeVpcEndpointServicesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcEndpointServicesResult describeVpcEndpointServices() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcEndpointsResult describeVpcEndpoints(DescribeVpcEndpointsRequest describeVpcEndpointsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcEndpointsResult describeVpcEndpoints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcPeeringConnectionsResult describeVpcPeeringConnections(
            DescribeVpcPeeringConnectionsRequest describeVpcPeeringConnectionsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcPeeringConnectionsResult describeVpcPeeringConnections() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcsResult describeVpcs(DescribeVpcsRequest describeVpcsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpcsResult describeVpcs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpnConnectionsResult describeVpnConnections(
            DescribeVpnConnectionsRequest describeVpnConnectionsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpnConnectionsResult describeVpnConnections() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpnGatewaysResult describeVpnGateways(DescribeVpnGatewaysRequest describeVpnGatewaysRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeVpnGatewaysResult describeVpnGateways() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetachClassicLinkVpcResult detachClassicLinkVpc(DetachClassicLinkVpcRequest detachClassicLinkVpcRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetachInternetGatewayResult detachInternetGateway(DetachInternetGatewayRequest detachInternetGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetachNetworkInterfaceResult detachNetworkInterface(
            DetachNetworkInterfaceRequest detachNetworkInterfaceRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetachVolumeResult detachVolume(DetachVolumeRequest detachVolumeRequest) {
        // Deviceから先頭の /dev/ を除去する
        String device = detachVolumeRequest.getDevice();
        if (device != null && device.startsWith("/dev/")) {
            device = device.substring("/dev/".length());
        }

        boolean force = detachVolumeRequest.isForce() != null ? detachVolumeRequest.isForce().booleanValue() : false;

        try {
            AttachmentInfo info = jec2.detachVolume(detachVolumeRequest.getVolumeId(),
                    detachVolumeRequest.getInstanceId(), device, force);
            VolumeAttachment attachment = new VolumeAttachmentConverter().convert(info);
            return new DetachVolumeResult().withAttachment(attachment);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DetachVpnGatewayResult detachVpnGateway(DetachVpnGatewayRequest detachVpnGatewayRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisableVgwRoutePropagationResult disableVgwRoutePropagation(
            DisableVgwRoutePropagationRequest disableVgwRoutePropagationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisableVpcClassicLinkResult disableVpcClassicLink(DisableVpcClassicLinkRequest disableVpcClassicLinkRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisableVpcClassicLinkDnsSupportResult disableVpcClassicLinkDnsSupport(
            DisableVpcClassicLinkDnsSupportRequest disableVpcClassicLinkDnsSupportRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisassociateAddressResult disassociateAddress(DisassociateAddressRequest disassociateAddressRequest) {
        try {
            jec2.disassociateAddress(disassociateAddressRequest.getPublicIp());
            return new DisassociateAddressResult();
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public DisassociateRouteTableResult disassociateRouteTable(
            DisassociateRouteTableRequest disassociateRouteTableRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnableVgwRoutePropagationResult enableVgwRoutePropagation(
            EnableVgwRoutePropagationRequest enableVgwRoutePropagationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnableVolumeIOResult enableVolumeIO(EnableVolumeIORequest enableVolumeIORequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnableVpcClassicLinkResult enableVpcClassicLink(EnableVpcClassicLinkRequest enableVpcClassicLinkRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnableVpcClassicLinkDnsSupportResult enableVpcClassicLinkDnsSupport(
            EnableVpcClassicLinkDnsSupportRequest enableVpcClassicLinkDnsSupportRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetConsoleOutputResult getConsoleOutput(GetConsoleOutputRequest getConsoleOutputRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetConsoleScreenshotResult getConsoleScreenshot(GetConsoleScreenshotRequest getConsoleScreenshotRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetHostReservationPurchasePreviewResult getHostReservationPurchasePreview(
            GetHostReservationPurchasePreviewRequest getHostReservationPurchasePreviewRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetPasswordDataResult getPasswordData(GetPasswordDataRequest getPasswordDataRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetReservedInstancesExchangeQuoteResult getReservedInstancesExchangeQuote(
            GetReservedInstancesExchangeQuoteRequest getReservedInstancesExchangeQuoteRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportImageResult importImage(ImportImageRequest importImageRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportImageResult importImage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportInstanceResult importInstance(ImportInstanceRequest importInstanceRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportKeyPairResult importKeyPair(ImportKeyPairRequest importKeyPairRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportSnapshotResult importSnapshot(ImportSnapshotRequest importSnapshotRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportSnapshotResult importSnapshot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportVolumeResult importVolume(ImportVolumeRequest importVolumeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyHostsResult modifyHosts(ModifyHostsRequest modifyHostsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyIdFormatResult modifyIdFormat(ModifyIdFormatRequest modifyIdFormatRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyIdentityIdFormatResult modifyIdentityIdFormat(
            ModifyIdentityIdFormatRequest modifyIdentityIdFormatRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyImageAttributeResult modifyImageAttribute(ModifyImageAttributeRequest modifyImageAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyInstanceAttributeResult modifyInstanceAttribute(
            ModifyInstanceAttributeRequest modifyInstanceAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyInstancePlacementResult modifyInstancePlacement(
            ModifyInstancePlacementRequest modifyInstancePlacementRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyNetworkInterfaceAttributeResult modifyNetworkInterfaceAttribute(
            ModifyNetworkInterfaceAttributeRequest modifyNetworkInterfaceAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyReservedInstancesResult modifyReservedInstances(
            ModifyReservedInstancesRequest modifyReservedInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifySnapshotAttributeResult modifySnapshotAttribute(
            ModifySnapshotAttributeRequest modifySnapshotAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifySpotFleetRequestResult modifySpotFleetRequest(
            ModifySpotFleetRequestRequest modifySpotFleetRequestRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifySubnetAttributeResult modifySubnetAttribute(ModifySubnetAttributeRequest modifySubnetAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyVolumeAttributeResult modifyVolumeAttribute(ModifyVolumeAttributeRequest modifyVolumeAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyVpcAttributeResult modifyVpcAttribute(ModifyVpcAttributeRequest modifyVpcAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyVpcEndpointResult modifyVpcEndpoint(ModifyVpcEndpointRequest modifyVpcEndpointRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModifyVpcPeeringConnectionOptionsResult modifyVpcPeeringConnectionOptions(
            ModifyVpcPeeringConnectionOptionsRequest modifyVpcPeeringConnectionOptionsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MonitorInstancesResult monitorInstances(MonitorInstancesRequest monitorInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MoveAddressToVpcResult moveAddressToVpc(MoveAddressToVpcRequest moveAddressToVpcRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PurchaseHostReservationResult purchaseHostReservation(
            PurchaseHostReservationRequest purchaseHostReservationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PurchaseReservedInstancesOfferingResult purchaseReservedInstancesOffering(
            PurchaseReservedInstancesOfferingRequest purchaseReservedInstancesOfferingRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PurchaseScheduledInstancesResult purchaseScheduledInstances(
            PurchaseScheduledInstancesRequest purchaseScheduledInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RebootInstancesResult rebootInstances(RebootInstancesRequest rebootInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RegisterImageResult registerImage(RegisterImageRequest registerImageRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RejectVpcPeeringConnectionResult rejectVpcPeeringConnection(
            RejectVpcPeeringConnectionRequest rejectVpcPeeringConnectionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReleaseAddressResult releaseAddress(ReleaseAddressRequest releaseAddressRequest) {
        try {
            jec2.releaseAddress(releaseAddressRequest.getPublicIp());
            return new ReleaseAddressResult();
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public ReleaseHostsResult releaseHosts(ReleaseHostsRequest releaseHostsRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplaceNetworkAclAssociationResult replaceNetworkAclAssociation(
            ReplaceNetworkAclAssociationRequest replaceNetworkAclAssociationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplaceNetworkAclEntryResult replaceNetworkAclEntry(
            ReplaceNetworkAclEntryRequest replaceNetworkAclEntryRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplaceRouteResult replaceRoute(ReplaceRouteRequest replaceRouteRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplaceRouteTableAssociationResult replaceRouteTableAssociation(
            ReplaceRouteTableAssociationRequest replaceRouteTableAssociationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReportInstanceStatusResult reportInstanceStatus(ReportInstanceStatusRequest reportInstanceStatusRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestSpotFleetResult requestSpotFleet(RequestSpotFleetRequest requestSpotFleetRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestSpotInstancesResult requestSpotInstances(RequestSpotInstancesRequest requestSpotInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResetImageAttributeResult resetImageAttribute(ResetImageAttributeRequest resetImageAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResetInstanceAttributeResult resetInstanceAttribute(
            ResetInstanceAttributeRequest resetInstanceAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResetNetworkInterfaceAttributeResult resetNetworkInterfaceAttribute(
            ResetNetworkInterfaceAttributeRequest resetNetworkInterfaceAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResetSnapshotAttributeResult resetSnapshotAttribute(
            ResetSnapshotAttributeRequest resetSnapshotAttributeRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RestoreAddressToClassicResult restoreAddressToClassic(
            RestoreAddressToClassicRequest restoreAddressToClassicRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RevokeSecurityGroupEgressResult revokeSecurityGroupEgress(
            RevokeSecurityGroupEgressRequest revokeSecurityGroupEgressRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RevokeSecurityGroupIngressResult revokeSecurityGroupIngress(
            RevokeSecurityGroupIngressRequest revokeSecurityGroupIngressRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RevokeSecurityGroupIngressResult revokeSecurityGroupIngress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RunInstancesResult runInstances(RunInstancesRequest runInstancesRequest) {
        // InstanceType
        InstanceType type = InstanceType.getTypeFromString(runInstancesRequest.getInstanceType());

        // AvailabilityZone
        String availabilityZone = null;
        if (runInstancesRequest.getPlacement() != null) {
            availabilityZone = runInstancesRequest.getPlacement().getAvailabilityZone();
        }

        // UserDataをデコード
        String userData = runInstancesRequest.getUserData();
        if (userData != null) {
            userData = new String(Base64.decodeBase64(userData.getBytes()));

            // バイト数が3の倍数になるように調整する
            int len = userData.getBytes().length;
            int mod = len % 3;
            if (mod != 0) {
                for (int i = 0; i < 3 - mod; i++) {
                    userData += ';';
                }
            }
        }

        try {
            ReservationDescription description = jec2.runInstances(runInstancesRequest.getImageId(),
                    runInstancesRequest.getMinCount(), runInstancesRequest.getMaxCount(),
                    runInstancesRequest.getSecurityGroups(), userData, runInstancesRequest.getKeyName(), true, type,
                    availabilityZone, runInstancesRequest.getKernelId(), runInstancesRequest.getRamdiskId(), null);
            Reservation reservation = new ReservationConverter().convert(description);
            return new RunInstancesResult().withReservation(reservation);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public RunScheduledInstancesResult runScheduledInstances(RunScheduledInstancesRequest runScheduledInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StartInstancesResult startInstances(StartInstancesRequest startInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StopInstancesResult stopInstances(StopInstancesRequest stopInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TerminateInstancesResult terminateInstances(TerminateInstancesRequest terminateInstancesRequest) {
        try {
            List<TerminatingInstanceDescription> descriptions = jec2.terminateInstances(terminateInstancesRequest
                    .getInstanceIds());
            List<InstanceStateChange> terminatingInstances = new InstanceStateChangeTerminateConverter()
                    .convert(descriptions);
            return new TerminateInstancesResult().withTerminatingInstances(terminatingInstances);
        } catch (EC2Exception e) {
            throw new AmazonClientException(e);
        }
    }

    @Override
    public UnassignPrivateIpAddressesResult unassignPrivateIpAddresses(
            UnassignPrivateIpAddressesRequest unassignPrivateIpAddressesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnmonitorInstancesResult unmonitorInstances(UnmonitorInstancesRequest unmonitorInstancesRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X extends AmazonWebServiceRequest> DryRunResult<X> dryRun(DryRunSupportedRequest<X> request)
            throws AmazonServiceException, AmazonClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return null;
    }

    @Override
    public AmazonEC2Waiters waiters() {
        return null;
    }

}
