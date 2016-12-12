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

import com.amazonaws.services.ec2.model.Image;
import com.xerox.amazonws.ec2.ImageDescription;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ImageConverter extends AbstractConverter<ImageDescription, Image> {

    @Override
    protected Image convertObject(ImageDescription from) {
        Image to = new Image();

        to.setImageId(from.getImageId());
        to.setImageLocation(from.getImageLocation());
        to.setState(from.getImageState());
        to.setOwnerId(from.getImageOwnerId());
        to.setPublic(from.isPublic());
        to.setProductCodes(new ProductCodeConverter().convert(from.getProductCodes()));
        to.setArchitecture(from.getArchitecture());
        to.setImageType(from.getImageType());
        to.setKernelId(from.getKernelId());
        to.setRamdiskId(from.getRamdiskId());
        to.setPlatform(from.getPlatform());

        // 未実装
        to.setStateReason(null);
        to.setImageOwnerAlias(null);
        to.setName(null);
        to.setDescription(null);
        //to.setRootDeviceType(null);
        to.setRootDeviceName(null);
        to.setBlockDeviceMappings(null);
        //to.setVirtualizationType(null);
        to.setTags(null);

        return to;
    }
}
