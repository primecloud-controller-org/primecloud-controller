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
 * COMPONENT_TYPEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseComponentType implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** COMPONENT_TYPE_NO [BIGINT(19,0)] */
    private Long componentTypeNo;

    /** COMPONENT_TYPE_NAME [VARCHAR(100,0)] */
    private String componentTypeName;

    /** COMPONENT_TYPE_NAME_DISP [VARCHAR(300,0)] */
    private String componentTypeNameDisp;

    /** LAYER [VARCHAR(100,0)] */
    private String layer;

    /** LAYER_DISP [VARCHAR(300,0)] */
    private String layerDisp;

    /** RUN_ORDER [INT(10,0)] */
    private Integer runOrder;

    /** SELECTABLE [BIT(0,0)] */
    private Boolean selectable;

    /** ZABBIX_TEMPLATE [VARCHAR(100,0)] */
    private String zabbixTemplate;

    /** ADDRESS_URL [VARCHAR(100,0)] */
    private String addressUrl;

    /**
     * componentTypeNoを取得します。
     *
     * @return componentTypeNo
     */
    public Long getComponentTypeNo() {
        return componentTypeNo;
    }

    /**
     * componentTypeNoを設定します。
     *
     * @param componentTypeNo componentTypeNo
     */
    public void setComponentTypeNo(Long componentTypeNo) {
        this.componentTypeNo = componentTypeNo;
    }

    /**
     * componentTypeNameを取得します。
     *
     * @return componentTypeName
     */
    public String getComponentTypeName() {
        return componentTypeName;
    }

    /**
     * componentTypeNameを設定します。
     *
     * @param componentTypeName componentTypeName
     */
    public void setComponentTypeName(String componentTypeName) {
        this.componentTypeName = componentTypeName;
    }

    /**
     * componentTypeNameDispを取得します。
     *
     * @return componentTypeNameDisp
     */
    public String getComponentTypeNameDisp() {
        return componentTypeNameDisp;
    }

    /**
     * componentTypeNameDispを設定します。
     *
     * @param componentTypeNameDisp componentTypeNameDisp
     */
    public void setComponentTypeNameDisp(String componentTypeNameDisp) {
        this.componentTypeNameDisp = componentTypeNameDisp;
    }

    /**
     * layerを取得します。
     *
     * @return layer
     */
    public String getLayer() {
        return layer;
    }

    /**
     * layerを設定します。
     *
     * @param layer layer
     */
    public void setLayer(String layer) {
        this.layer = layer;
    }

    /**
     * layerDispを取得します。
     *
     * @return layerDisp
     */
    public String getLayerDisp() {
        return layerDisp;
    }

    /**
     * layerDispを設定します。
     *
     * @param layerDisp layerDisp
     */
    public void setLayerDisp(String layerDisp) {
        this.layerDisp = layerDisp;
    }

    /**
     * runOrderを取得します。
     *
     * @return runOrder
     */
    public Integer getRunOrder() {
        return runOrder;
    }

    /**
     * runOrderを設定します。
     *
     * @param runOrder runOrder
     */
    public void setRunOrder(Integer runOrder) {
        this.runOrder = runOrder;
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
     * addressUrlを取得します。
     *
     * @return addressUrl
     */
    public String getAddressUrl() {
        return addressUrl;
    }

    /**
     * addressUrlを設定します。
     *
     * @param addressUrl addressUrl
     */
    public void setAddressUrl(String addressUrl) {
        this.addressUrl = addressUrl;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((componentTypeNo == null) ? 0 : componentTypeNo.hashCode());
        result = prime * result + ((componentTypeName == null) ? 0 : componentTypeName.hashCode());
        result = prime * result + ((componentTypeNameDisp == null) ? 0 : componentTypeNameDisp.hashCode());
        result = prime * result + ((layer == null) ? 0 : layer.hashCode());
        result = prime * result + ((layerDisp == null) ? 0 : layerDisp.hashCode());
        result = prime * result + ((runOrder == null) ? 0 : runOrder.hashCode());
        result = prime * result + ((selectable == null) ? 0 : selectable.hashCode());
        result = prime * result + ((zabbixTemplate == null) ? 0 : zabbixTemplate.hashCode());
        result = prime * result + ((addressUrl == null) ? 0 : addressUrl.hashCode());

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

        final BaseComponentType other = (BaseComponentType) obj;
        if (componentTypeNo == null) {
            if (other.componentTypeNo != null) { return false; }
        } else if (!componentTypeNo.equals(other.componentTypeNo)) {
            return false;
        }
        if (componentTypeName == null) {
            if (other.componentTypeName != null) { return false; }
        } else if (!componentTypeName.equals(other.componentTypeName)) {
            return false;
        }
        if (componentTypeNameDisp == null) {
            if (other.componentTypeNameDisp != null) { return false; }
        } else if (!componentTypeNameDisp.equals(other.componentTypeNameDisp)) {
            return false;
        }
        if (layer == null) {
            if (other.layer != null) { return false; }
        } else if (!layer.equals(other.layer)) {
            return false;
        }
        if (layerDisp == null) {
            if (other.layerDisp != null) { return false; }
        } else if (!layerDisp.equals(other.layerDisp)) {
            return false;
        }
        if (runOrder == null) {
            if (other.runOrder != null) { return false; }
        } else if (!runOrder.equals(other.runOrder)) {
            return false;
        }
        if (selectable == null) {
            if (other.selectable != null) { return false; }
        } else if (!selectable.equals(other.selectable)) {
            return false;
        }
        if (zabbixTemplate == null) {
            if (other.zabbixTemplate != null) { return false; }
        } else if (!zabbixTemplate.equals(other.zabbixTemplate)) {
            return false;
        }
        if (addressUrl == null) {
            if (other.addressUrl != null) { return false; }
        } else if (!addressUrl.equals(other.addressUrl)) {
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
        sb.append("ComponentType").append(" [");
        sb.append("componentTypeNo=").append(componentTypeNo).append(", ");
        sb.append("componentTypeName=").append(componentTypeName).append(", ");
        sb.append("componentTypeNameDisp=").append(componentTypeNameDisp).append(", ");
        sb.append("layer=").append(layer).append(", ");
        sb.append("layerDisp=").append(layerDisp).append(", ");
        sb.append("runOrder=").append(runOrder).append(", ");
        sb.append("selectable=").append(selectable).append(", ");
        sb.append("zabbixTemplate=").append(zabbixTemplate).append(", ");
        sb.append("addressUrl=").append(addressUrl);
        sb.append("]");
        return sb.toString();
    }

}
