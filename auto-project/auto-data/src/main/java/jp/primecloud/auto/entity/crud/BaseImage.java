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
 * IMAGEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseImage implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** IMAGE_NO [BIGINT(19,0)] */
    private Long imageNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** IMAGE_NAME [VARCHAR(100,0)] */
    private String imageName;

    /** IMAGE_NAME_DISP [VARCHAR(300,0)] */
    private String imageNameDisp;

    /** OS [VARCHAR(100,0)] */
    private String os;

    /** OS_DISP [VARCHAR(300,0)] */
    private String osDisp;

    /** SELECTABLE [BIT(0,0)] */
    private Boolean selectable;

    /** COMPONENT_TYPE_NOS [VARCHAR(500,0)] */
    private String componentTypeNos;

    /** ZABBIX_TEMPLATE [VARCHAR(100,0)] */
    private String zabbixTemplate;

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
     * platformNoを取得します。
     *
     * @return platformNo
     */
    public Long getPlatformNo() {
        return platformNo;
    }

    /**
     * platformNoを設定します。
     *
     * @param platformNo platformNo
     */
    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    /**
     * imageNameを取得します。
     *
     * @return imageName
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * imageNameを設定します。
     *
     * @param imageName imageName
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * imageNameDispを取得します。
     *
     * @return imageNameDisp
     */
    public String getImageNameDisp() {
        return imageNameDisp;
    }

    /**
     * imageNameDispを設定します。
     *
     * @param imageNameDisp imageNameDisp
     */
    public void setImageNameDisp(String imageNameDisp) {
        this.imageNameDisp = imageNameDisp;
    }

    /**
     * osを取得します。
     *
     * @return os
     */
    public String getOs() {
        return os;
    }

    /**
     * osを設定します。
     *
     * @param os os
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * osDispを取得します。
     *
     * @return osDisp
     */
    public String getOsDisp() {
        return osDisp;
    }

    /**
     * osDispを設定します。
     *
     * @param osDisp osDisp
     */
    public void setOsDisp(String osDisp) {
        this.osDisp = osDisp;
    }

    /**
     * selectableを取得します。
     *
     * @return selectable
     */
    public Boolean getSelectable() {
        return selectable;
    }

    /**
     * selectableを設定します。
     *
     * @param selectable selectable
     */
    public void setSelectable(Boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * componentTypeNosを取得します。
     *
     * @return componentTypeNos
     */
    public String getComponentTypeNos() {
        return componentTypeNos;
    }

    /**
     * componentTypeNosを設定します。
     *
     * @param componentTypeNos componentTypeNos
     */
    public void setComponentTypeNos(String componentTypeNos) {
        this.componentTypeNos = componentTypeNos;
    }

    /**
     * zabbixTemplateを取得します。
     *
     * @return zabbixTemplate
     */
    public String getZabbixTemplate() {
        return zabbixTemplate;
    }

    /**
     * zabbixTemplateを設定します。
     *
     * @param zabbixTemplate zabbixTemplate
     */
    public void setZabbixTemplate(String zabbixTemplate) {
        this.zabbixTemplate = zabbixTemplate;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((imageNo == null) ? 0 : imageNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((imageName == null) ? 0 : imageName.hashCode());
        result = prime * result + ((imageNameDisp == null) ? 0 : imageNameDisp.hashCode());
        result = prime * result + ((os == null) ? 0 : os.hashCode());
        result = prime * result + ((osDisp == null) ? 0 : osDisp.hashCode());
        result = prime * result + ((selectable == null) ? 0 : selectable.hashCode());
        result = prime * result + ((componentTypeNos == null) ? 0 : componentTypeNos.hashCode());
        result = prime * result + ((zabbixTemplate == null) ? 0 : zabbixTemplate.hashCode());

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

        final BaseImage other = (BaseImage) obj;
        if (imageNo == null) {
            if (other.imageNo != null) { return false; }
        } else if (!imageNo.equals(other.imageNo)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (imageName == null) {
            if (other.imageName != null) { return false; }
        } else if (!imageName.equals(other.imageName)) {
            return false;
        }
        if (imageNameDisp == null) {
            if (other.imageNameDisp != null) { return false; }
        } else if (!imageNameDisp.equals(other.imageNameDisp)) {
            return false;
        }
        if (os == null) {
            if (other.os != null) { return false; }
        } else if (!os.equals(other.os)) {
            return false;
        }
        if (osDisp == null) {
            if (other.osDisp != null) { return false; }
        } else if (!osDisp.equals(other.osDisp)) {
            return false;
        }
        if (selectable == null) {
            if (other.selectable != null) { return false; }
        } else if (!selectable.equals(other.selectable)) {
            return false;
        }
        if (componentTypeNos == null) {
            if (other.componentTypeNos != null) { return false; }
        } else if (!componentTypeNos.equals(other.componentTypeNos)) {
            return false;
        }
        if (zabbixTemplate == null) {
            if (other.zabbixTemplate != null) { return false; }
        } else if (!zabbixTemplate.equals(other.zabbixTemplate)) {
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
        sb.append("Image").append(" [");
        sb.append("imageNo=").append(imageNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("imageName=").append(imageName).append(", ");
        sb.append("imageNameDisp=").append(imageNameDisp).append(", ");
        sb.append("os=").append(os).append(", ");
        sb.append("osDisp=").append(osDisp).append(", ");
        sb.append("selectable=").append(selectable).append(", ");
        sb.append("componentTypeNos=").append(componentTypeNos).append(", ");
        sb.append("zabbixTemplate=").append(zabbixTemplate);
        sb.append("]");
        return sb.toString();
    }

}
