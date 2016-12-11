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
package jp.primecloud.auto.entity.crud;

import java.io.Serializable;

/**
 * <p>
 * IMAGE_AWSに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseImageAws implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** IMAGE_NO [BIGINT(19,0)] */
    private Long imageNo;

    /** IMAGE_ID [VARCHAR(100,0)] */
    private String imageId;

    /** KERNEL_ID [VARCHAR(100,0)] */
    private String kernelId;

    /** RAMDISK_ID [VARCHAR(100,0)] */
    private String ramdiskId;

    /** INSTANCE_TYPES [VARCHAR(500,0)] */
    private String instanceTypes;

    /** EBS_IMAGE [BIT(0,0)] */
    private Boolean ebsImage;

    /** ROOT_SIZE [INT(10,0)] */
    private Integer rootSize;

    /**
     * imageNoを取得します。
     *
     * @return imageNo
     */
    public Long getImageNo() {
        return imageNo;
    }

    /**
     * imageNoを設定します。
     *
     * @param imageNo imageNo
     */
    public void setImageNo(Long imageNo) {
        this.imageNo = imageNo;
    }

    /**
     * imageIdを取得します。
     *
     * @return imageId
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * imageIdを設定します。
     *
     * @param imageId imageId
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * kernelIdを取得します。
     *
     * @return kernelId
     */
    public String getKernelId() {
        return kernelId;
    }

    /**
     * kernelIdを設定します。
     *
     * @param kernelId kernelId
     */
    public void setKernelId(String kernelId) {
        this.kernelId = kernelId;
    }

    /**
     * ramdiskIdを取得します。
     *
     * @return ramdiskId
     */
    public String getRamdiskId() {
        return ramdiskId;
    }

    /**
     * ramdiskIdを設定します。
     *
     * @param ramdiskId ramdiskId
     */
    public void setRamdiskId(String ramdiskId) {
        this.ramdiskId = ramdiskId;
    }

    /**
     * instanceTypesを取得します。
     *
     * @return instanceTypes
     */
    public String getInstanceTypes() {
        return instanceTypes;
    }

    /**
     * instanceTypesを設定します。
     *
     * @param instanceTypes instanceTypes
     */
    public void setInstanceTypes(String instanceTypes) {
        this.instanceTypes = instanceTypes;
    }

    /**
     * ebsImageを取得します。
     *
     * @return ebsImage
     */
    public Boolean getEbsImage() {
        return ebsImage;
    }

    /**
     * ebsImageを設定します。
     *
     * @param ebsImage ebsImage
     */
    public void setEbsImage(Boolean ebsImage) {
        this.ebsImage = ebsImage;
    }

    /**
     * rootSizeを取得します。
     *
     * @return rootSize
     */
    public Integer getRootSize() {
        return rootSize;
    }

    /**
     * rootSizeを設定します。
     *
     * @param rootSize rootSize
     */
    public void setRootSize(Integer rootSize) {
        this.rootSize = rootSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((imageNo == null) ? 0 : imageNo.hashCode());
        result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
        result = prime * result + ((kernelId == null) ? 0 : kernelId.hashCode());
        result = prime * result + ((ramdiskId == null) ? 0 : ramdiskId.hashCode());
        result = prime * result + ((instanceTypes == null) ? 0 : instanceTypes.hashCode());
        result = prime * result + ((ebsImage == null) ? 0 : ebsImage.hashCode());
        result = prime * result + ((rootSize == null) ? 0 : rootSize.hashCode());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }

        final BaseImageAws other = (BaseImageAws) obj;
        if (imageNo == null) {
            if (other.imageNo != null) { return false; }
        } else if (!imageNo.equals(other.imageNo)) {
            return false;
        }
        if (imageId == null) {
            if (other.imageId != null) { return false; }
        } else if (!imageId.equals(other.imageId)) {
            return false;
        }
        if (kernelId == null) {
            if (other.kernelId != null) { return false; }
        } else if (!kernelId.equals(other.kernelId)) {
            return false;
        }
        if (ramdiskId == null) {
            if (other.ramdiskId != null) { return false; }
        } else if (!ramdiskId.equals(other.ramdiskId)) {
            return false;
        }
        if (instanceTypes == null) {
            if (other.instanceTypes != null) { return false; }
        } else if (!instanceTypes.equals(other.instanceTypes)) {
            return false;
        }
        if (ebsImage == null) {
            if (other.ebsImage != null) { return false; }
        } else if (!ebsImage.equals(other.ebsImage)) {
            return false;
        }
        if (rootSize == null) {
            if (other.rootSize != null) { return false; }
        } else if (!rootSize.equals(other.rootSize)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ImageAws").append(" [");
        sb.append("imageNo=").append(imageNo).append(", ");
        sb.append("imageId=").append(imageId).append(", ");
        sb.append("kernelId=").append(kernelId).append(", ");
        sb.append("ramdiskId=").append(ramdiskId).append(", ");
        sb.append("instanceTypes=").append(instanceTypes).append(", ");
        sb.append("ebsImage=").append(ebsImage).append(", ");
        sb.append("rootSize=").append(rootSize);
        sb.append("]");
        return sb.toString();
    }

}
