package jp.primecloud.auto.api.response.component;

import jp.primecloud.auto.entity.crud.ComponentType;

import org.codehaus.jackson.annotate.JsonProperty;

public class ComponentTypeResponse {

    @JsonProperty("ComponentTypeNo")
    private Long componentTypeNo;

    @JsonProperty("ComponentTypeName")
    private String componentTypeName;

    @JsonProperty("ComponentTypeNameDisp")
    private String componentTypeNameDisp;

    @JsonProperty("LayerDisp")
    private String layerDisp;

    @JsonProperty("AddressUrl")
    private String addressUrl;

    public ComponentTypeResponse(ComponentType componentType) {
        this.componentTypeNo = componentType.getComponentTypeNo();
        this.componentTypeName = componentType.getComponentTypeName();
        this.componentTypeNameDisp = componentType.getComponentTypeNameDisp();
        this.layerDisp = componentType.getLayerDisp();
        this.addressUrl = componentType.getAddressUrl();
    }

    public Long getComponentTypeNo() {
        return componentTypeNo;
    }

    public void setComponentTypeNo(Long componentTypeNo) {
        this.componentTypeNo = componentTypeNo;
    }

    public String getComponentTypeName() {
        return componentTypeName;
    }

    public void setComponentTypeName(String componentTypeName) {
        this.componentTypeName = componentTypeName;
    }

    public String getComponentTypeNameDisp() {
        return componentTypeNameDisp;
    }

    public void setComponentTypeNameDisp(String componentTypeNameDisp) {
        this.componentTypeNameDisp = componentTypeNameDisp;
    }

    public String getLayerDisp() {
        return layerDisp;
    }

    public void setLayerDisp(String layerDisp) {
        this.layerDisp = layerDisp;
    }

    public String getAddressUrl() {
        return addressUrl;
    }

    public void setAddressUrl(String addressUrl) {
        this.addressUrl = addressUrl;
    }

}
