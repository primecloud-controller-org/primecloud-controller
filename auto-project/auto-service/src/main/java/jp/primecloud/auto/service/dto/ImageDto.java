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
package jp.primecloud.auto.service.dto;

import java.io.Serializable;
import java.util.List;

import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.ImageAws;
import jp.primecloud.auto.entity.crud.ImageCloudstack;
import jp.primecloud.auto.entity.crud.ImageNifty;
import jp.primecloud.auto.entity.crud.ImageVmware;



/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ImageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Image image;

    private ImageAws imageAws;

    private ImageCloudstack imageCloudstack;

    private ImageVmware imageVmware;

    private ImageNifty imageNifty;

    private List<ComponentType> componentTypes;

    /**
     * imageを取得します。
     *
     * @return image
     */
    public Image getImage() {
        return image;
    }

    /**
     * imageを設定します。
     *
     * @param image image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * imageAwsを取得します。
     *
     * @return imageAws
     */
    public ImageAws getImageAws() {
        return imageAws;
    }

    /**
     * imageAwsを設定します。
     *
     * @param imageAws imageAws
     */
    public void setImageAws(ImageAws imageAws) {
        this.imageAws = imageAws;
    }

    /**
     * imageCloudstackを取得します。
     *
     * @return imageCloudstack
     */
    public ImageCloudstack getImageCloudstack() {
        return imageCloudstack;
    }

    /**
     * imageCloudstackを設定します。
     *
     * @param imageCloudstack imageCloudstack
     */
    public void setImageCloudstack(ImageCloudstack imageCloudstack) {
        this.imageCloudstack = imageCloudstack;
    }

    /**
     * imageVmwareを取得します。
     *
     * @return imageVmware
     */
    public ImageVmware getImageVmware() {
        return imageVmware;
    }

    /**
     * imageVmwareを設定します。
     *
     * @param imageVmware imageVmware
     */
    public void setImageVmware(ImageVmware imageVmware) {
        this.imageVmware = imageVmware;
    }

    /**
     * imageNiftyを取得します。
     *
     * @return imageNifty
     */
    public ImageNifty getImageNifty() {
        return imageNifty;
    }

    /**
     * imageNiftyを設定します。
     *
     * @param imageNifty imageNifty
     */
    public void setImageNifty(ImageNifty imageNifty) {
        this.imageNifty = imageNifty;
    }

    /**
     * componentTypesを取得します。
     *
     * @return componentTypes
     */
    public List<ComponentType> getComponentTypes() {
        return componentTypes;
    }

    /**
     * componentTypesを設定します。
     *
     * @param componentTypes componentTypes
     */
    public void setComponentTypes(List<ComponentType> componentTypes) {
        this.componentTypes = componentTypes;
    }

}
