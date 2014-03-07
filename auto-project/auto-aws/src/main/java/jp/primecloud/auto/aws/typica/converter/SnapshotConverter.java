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

import com.amazonaws.services.ec2.model.Snapshot;
import com.xerox.amazonws.ec2.SnapshotInfo;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class SnapshotConverter extends AbstractConverter<SnapshotInfo, Snapshot> {

    @Override
    protected Snapshot convertObject(SnapshotInfo from) {
        Snapshot to = new Snapshot();

        to.setSnapshotId(from.getSnapshotId());
        to.setVolumeId(from.getVolumeId());
        to.setState(from.getStatus());
        to.setStartTime(from.getStartTime().getTime());
        to.setProgress(from.getProgress());

        // 未実装        
        to.setOwnerId(null);
        to.setDescription(null);
        to.setVolumeSize(null);
        to.setOwnerAlias(null);
        to.setTags(null);

        return to;
    }

}
