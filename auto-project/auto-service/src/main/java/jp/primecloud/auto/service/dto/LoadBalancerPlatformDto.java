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

import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.PlatformCloudstack;
import jp.primecloud.auto.entity.crud.PlatformNifty;
import jp.primecloud.auto.entity.crud.PlatformVmware;




/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class LoadBalancerPlatformDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Platform platform;

    private PlatformAws platformAws;

    private PlatformCloudstack platformCloudstack;

    private PlatformVmware platformVmware;

    private PlatformNifty platformNifty;

    private List<ImageDto> images;

    private List<String> types;

    /**
     * platformを取得します。
     *
     * @return platform
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * platformを設定します。
     *
     * @param platform platform
     */
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    /**
     * platformAwsを取得します。
     *
     * @return platformAws
     */
    public PlatformAws getPlatformAws() {
        return platformAws;
    }

    /**
     * platformAwsを設定します。
     *
     * @param platformAws platformAws
     */
    public void setPlatformAws(PlatformAws platformAws) {
        this.platformAws = platformAws;
    }

    /**
     * platformCloudstackを取得します。
     *
     * @return platformCloudstack
     */
    public PlatformCloudstack getPlatformCloudstack() {
        return platformCloudstack;
    }

    /**
     * platformCloudstackを設定します。
     *
     * @param platformCloudstack platformCloudstack
     */
    public void setPlatformCloudstack(PlatformCloudstack platformCloudstack) {
        this.platformCloudstack = platformCloudstack;
    }

    /**
     * platformVmwareを取得します。
     *
     * @return platformVmware
     */
    public PlatformVmware getPlatformVmware() {
        return platformVmware;
    }

    /**
     * platformVmwareを設定します。
     *
     * @param platformVmware platformVmware
     */
    public void setPlatformVmware(PlatformVmware platformVmware) {
        this.platformVmware = platformVmware;
    }

    /**
     * platformNiftyを取得します。
     *
     * @return platformNifty
     */
    public PlatformNifty getPlatformNifty() {
        return platformNifty;
    }

    /**
     * platformNiftyを設定します。
     *
     * @param platformNifty platformNifty
     */
    public void setPlatformNifty(PlatformNifty platformNifty) {
        this.platformNifty = platformNifty;
    }

    /**
     * imagesを取得します。
     *
     * @return images
     */
    public List<ImageDto> getImages() {
        return images;
    }

    /**
     * imagesを設定します。
     *
     * @param images images
     */
    public void setImages(List<ImageDto> images) {
        this.images = images;
    }

    /**
     * typesを取得します。
     *
     * @return types
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * typesを設定します。
     *
     * @param types types
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

}
