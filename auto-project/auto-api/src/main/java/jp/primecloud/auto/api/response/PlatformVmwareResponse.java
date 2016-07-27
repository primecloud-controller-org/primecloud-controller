package jp.primecloud.auto.api.response;

import jp.primecloud.auto.entity.crud.PlatformVmware;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlatformVmwareResponse {

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Datacenter")
    private String datacenter;

    @JsonProperty("PublicNetwork")
    private String publicNetwork;

    @JsonProperty("PrivateNetwork")
    private String privateNetwork;

    @JsonProperty("Computresource")
    private String computresource;

    public PlatformVmwareResponse() {
    }

    public PlatformVmwareResponse(PlatformVmware vmware) {
        this.url = vmware.getUrl();
        this.datacenter = vmware.getDatacenter();
        this.publicNetwork = vmware.getPublicNetwork();
        this.privateNetwork = vmware.getPrivateNetwork();
        this.computresource = vmware.getComputeResource();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    public String getPublicNetwork() {
        return publicNetwork;
    }

    public void setPublicNetwork(String publicNetwork) {
        this.publicNetwork = publicNetwork;
    }

    public String getPrivateNetwork() {
        return privateNetwork;
    }

    public void setPrivateNetwork(String privateNetwork) {
        this.privateNetwork = privateNetwork;
    }

    public String getComputresource() {
        return computresource;
    }

    public void setComputresource(String computresource) {
        this.computresource = computresource;
    }

}
