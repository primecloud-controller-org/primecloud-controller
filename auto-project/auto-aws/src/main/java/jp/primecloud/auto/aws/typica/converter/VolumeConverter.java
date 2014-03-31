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

import com.amazonaws.services.ec2.model.Volume;
import com.xerox.amazonws.ec2.VolumeInfo;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VolumeConverter extends AbstractConverter<VolumeInfo, Volume> {

    @Override
    protected Volume convertObject(VolumeInfo from) {
        Volume to = new Volume();

        to.setVolumeId(from.getVolumeId());

        Integer size = null;
        if (from.getSize() != null && from.getSize().length() != 0) {
            size = Integer.valueOf(from.getSize());
        }
        to.setSize(size);

        to.setSnapshotId(from.getSnapshotId());
        to.setAvailabilityZone(from.getZone());
        to.setState(from.getStatus());
        to.setCreateTime(from.getCreateTime().getTime());
        to.setAttachments(new VolumeAttachmentConverter().convert(from.getAttachmentInfo()));

        // 未実装
        to.setTags(null);

        return to;
    }

}
