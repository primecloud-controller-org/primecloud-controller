package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;

import org.codehaus.jackson.annotate.JsonProperty;

public class VcloudInstanceNetworkResponse {

    @JsonProperty("NetworkName")
    private String networkName;

    @JsonProperty("NetworkIndex")
    private String networkIndex;

    @JsonProperty("IpMode")
    private String ipMode;

    @JsonProperty("IpAddress")
    private String ipAddress;

    @JsonProperty("IsPrimary")
    private Boolean isPrimary;

    public VcloudInstanceNetworkResponse() {
    }

    public VcloudInstanceNetworkResponse(VcloudInstanceNetwork vcloudInstanceNetwork) {
        this.networkName = vcloudInstanceNetwork.getNetworkName();
        if (vcloudInstanceNetwork.getNetworkIndex() == null) {
            this.networkIndex = "";
        } else {
            this.networkIndex = String.valueOf(vcloudInstanceNetwork.getNetworkIndex());
        }
        this.ipMode = vcloudInstanceNetwork.getIpMode();
        this.ipAddress = vcloudInstanceNetwork.getIpAddress();
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkIndex() {
        return networkIndex;
    }

    public void setNetworkIndex(String networkIndex) {
        this.networkIndex = networkIndex;
    }

    public String getIpMode() {
        return ipMode;
    }

    public void setIpMode(String ipMode) {
        this.ipMode = ipMode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

}
