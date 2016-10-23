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

import jp.primecloud.auto.entity.crud.AutoScalingConf;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class AutoScalingConfDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private AutoScalingConf autoScalingConf;

    private PlatformDto platform;

    private ImageDto image;

    /**
     * autoScalingConfを取得します。
     *
     * @return autoScalingConf
     */
    public AutoScalingConf getAutoScalingConf() {
        return autoScalingConf;
    }

    /**
     * autoScalingConfを設定します。
     *
     * @param autoScalingConf autoScalingConf
     */
    public void setAutoScalingConf(AutoScalingConf autoScalingConf) {
        this.autoScalingConf = autoScalingConf;
    }

    /**
     * platformを取得します。
     *
     * @return platform
     */
    public PlatformDto getPlatform() {
        return platform;
    }

    /**
     * platformを設定します。
     *
     * @param platform platform
     */
    public void setPlatform(PlatformDto platform) {
        this.platform = platform;
    }

    /**
     * imageを取得します。
     *
     * @return image
     */
    public ImageDto getImage() {
        return image;
    }

    /**
     * imageを設定します。
     *
     * @param image image
     */
    public void setImage(ImageDto image) {
        this.image = image;
    }

}
