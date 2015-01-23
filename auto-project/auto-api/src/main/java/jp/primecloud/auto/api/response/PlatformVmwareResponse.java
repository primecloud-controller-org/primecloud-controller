package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.PlatformVmware;


@XmlRootElement(name="PlatformVmwareResponse")
@XmlType(propOrder = {"url","datacenter","publicNetwork","privateNetwork","computresource"})
public class PlatformVmwareResponse {

    /**
     * ホストURL
     */
    private String url;

    /**
     * データセンター
     */
    private String datacenter;

    /**
     * パブリックネットワーク
     */
    private String publicNetwork;

    /**
     * プライベートネットワーク
     */
    private String privateNetwork;

    /**
     * コンピュートリソース
     */
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
    @XmlElement(name="Url")
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
    @XmlElement(name="Datacenter")
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
    @XmlElement(name="PublicNetwork")
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
    @XmlElement(name="PrivateNetwork")
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
    @XmlElement(name="Computresource")
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