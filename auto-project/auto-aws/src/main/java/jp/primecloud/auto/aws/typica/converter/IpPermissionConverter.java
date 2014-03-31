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

import com.amazonaws.services.ec2.model.IpPermission;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class IpPermissionConverter extends
        AbstractConverter<com.xerox.amazonws.ec2.GroupDescription.IpPermission, IpPermission> {

    @Override
    protected IpPermission convertObject(com.xerox.amazonws.ec2.GroupDescription.IpPermission from) {
        IpPermission to = new IpPermission();

        to.setIpProtocol(from.getProtocol());
        to.setFromPort(from.getFromPort());
        to.setToPort(from.getToPort());
        to.setUserIdGroupPairs(new UserIdGroupPairConverter().convert(from.getUidGroupPairs()));
        to.setIpRanges(from.getIpRanges());

        return to;
    }

}
