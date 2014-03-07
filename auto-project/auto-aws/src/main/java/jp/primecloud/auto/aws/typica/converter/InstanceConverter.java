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
package jp.primecloud.auto.aws.typica.converter;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Monitoring;
import com.amazonaws.services.ec2.model.Placement;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class InstanceConverter extends
        AbstractConverter<com.xerox.amazonws.ec2.ReservationDescription.Instance, Instance> {

    @Override
    protected Instance convertObject(com.xerox.amazonws.ec2.ReservationDescription.Instance from) {
        Instance to = new Instance();

        to.setInstanceId(from.getInstanceId());
        to.setImageId(from.getImageId());

        InstanceState state = new InstanceState();
        state.setCode(from.getStateCode());
        state.setName(from.getState());
        to.setState(state);

        to.setPrivateDnsName(from.getPrivateDnsName());
        to.setPublicDnsName(from.getDnsName());
        to.setStateTransitionReason(from.getReason());
        to.setKeyName(from.getKeyName());
        to.setAmiLaunchIndex(null);
        to.setProductCodes(null);
        to.setInstanceType(from.getInstanceType().name());
        to.setLaunchTime(from.getLaunchTime().getTime());

        Placement placement = new Placement();
        placement.setAvailabilityZone(from.getAvailabilityZone());
        placement.setGroupName(null); // 未実装
        to.setPlacement(placement);

        to.setKernelId(from.getKernelId());
        to.setRamdiskId(from.getRamdiskId());
        to.setPlatform(from.getPlatform());

        Monitoring monitoring = new Monitoring();
        monitoring.setState(Boolean.toString(from.isMonitoring()));
        to.setMonitoring(monitoring);

        // 未実装
        to.setSubnetId(null);
        to.setVpcId(null);
        to.setPrivateIpAddress(null);
        to.setPublicIpAddress(null);
        to.setStateReason(null);
        to.setArchitecture(null);
        to.setRootDeviceName(null);
        to.setRootDeviceName(null);
        to.setBlockDeviceMappings(null);
        to.setVirtualizationType(null);
        to.setInstanceLifecycle(null);
        to.setSpotInstanceRequestId(null);
        to.setLicense(null);
        to.setClientToken(null);
        to.setTags(null);

        return to;
    }

}
