package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.PlatformVmware;


public class PlatformVmwareResponse {

    /**
     * ホストURL
     */
    @JsonProperty("Url")
    private String url;

    /**
     * データセンター
     */
    @JsonProperty("Datacenter")
    private String datacenter;

    /**
     * パブリックネットワーク
     */
    @JsonProperty("PublicNetwork")
    private String publicNetwork;

    /**
     * プライベートネットワーク
     */
    @JsonProperty("PrivateNetwork")
    private String privateNetwork;

    /**
     * コンピュートリソース
     */
    @JsonProperty("Computresource")
    private String computresource;

    public PlatformVmwareResponse() {}

    public PlatformVmwareResponse(PlatformVmware vmware) {
        this.url = vmware.getUrl();
        this.datacenter = vmware.getDatacenter();
        this.publicNetwork = vmware.getPublicNetwork();
        this.privateNetwork = vmware.getPrivateNetwork();
        this.computresource = vmware.getComputeResource();
    }

   /**
    *
    * urlを取得します。
    *
    * @return url
    */
    public String getUrl() {
        return url;
    }

   /**
    *
    * urlを設定します。
    *
    * @param url
    */
    public void setUrl(String url) {
        this.url = url;
    }

   /**
    *
    * datacenterを取得します。
    *
    * @return datacenter
    */
    public String getDatacenter() {
        return datacenter;
    }

   /**
    *
    * datacenterを設定します。
    *
    * @param datacenter
    */
    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

   /**
    *
    * publicNetworkを取得します。
    *
    * @return publicNetwork
    */
    public String getPublicNetwork() {
        return publicNetwork;
    }

   /**
    *
    * publicNetworkを設定します。
    *
    * @param publicNetwork
    */
    public void setPublicNetwork(String publicNetwork) {
        this.publicNetwork = publicNetwork;
    }

   /**
    *
    * privateNetworkを取得します。
    *
    * @return privateNetwork
    */
    public String getPrivateNetwork() {
        return privateNetwork;
    }

   /**
    *
    * privateNetworkを設定します。
    *
    * @param privateNetwork
    */
    public void setPrivateNetwork(String privateNetwork) {
        this.privateNetwork = privateNetwork;
    }

   /**
    *
    * computresourceを取得します。
    *
    * @return computresource
    */
    public String getComputresource() {
        return computresource;
    }

   /**
    *
    * computresourceを設定します。
    *
    * @param computresource
    */
    public void setComputresource(String computresource) {
        this.computresource = computresource;
    }
}