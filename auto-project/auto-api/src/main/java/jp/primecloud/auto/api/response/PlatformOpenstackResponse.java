package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.PlatformOpenstack;


@XmlRootElement(name="PlatformOpenstackResponse")
@XmlType(propOrder = {"url", "networkId", "tenantId" , "tenantNm", "availabilityZone" , "defKeyPair"})
public class PlatformOpenstackResponse {

    /**
     * ホストURL
     */
    private String url;

    /**
     * ネットワークID
     */
    private String networkId;

    /**
     * テナントID
     */
    private String tenantId;

    /**
     * テナント名
     */
    private String tenantNm;

    /**
     * ゾーン名
     */
    private String availabilityZone;

    /**
     * デフォルトキーペア
     */
    private String defKeyPair;


    public PlatformOpenstackResponse(){}

    public PlatformOpenstackResponse(PlatformOpenstack openstack) {
        this.url = openstack.getUrl();
        this.networkId = openstack.getNetworkId();
        this.tenantId = openstack.getTenantId();
        this.tenantNm = openstack.getTenantNm();
        this.availabilityZone = openstack.getAvailabilityZone();
    }


    /**
    *
    * URLを取得します。
    *
    * @return url
    */
    @XmlElement(name="URL")
    public String getUrl() {
        return url;
    }

    /**
    *
    * URLを設定
    *
    * @param url
    */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
    *
    * networkIdを取得します。
    *
    * @return networkId
    */
    @XmlElement(name="NetworkID")
    public String getNetworkId() {
        return networkId;
    }

    /**
    *
    * networkIdを設定
    *
    * @param networkId
    */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    /**
    *
    * tenantIdを取得します。
    *
    * @return tenantId
    */
    @XmlElement(name="TenantID")
    public String getTenantId() {
        return tenantId;
    }

    /**
    *
    * tenantIdを設定
    *
    * @param tenantId
    */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
    *
    * tenantNmを取得します。
    *
    * @return tenantNm
    */
    @XmlElement(name="TenantNm")
    public String getTenantNm() {
        return tenantNm;
    }

    /**
    *
    * tenantNmを設定
    *
    * @param tenantNm
    */
    public void setTenantNm(String tenantNm) {
        this.tenantNm = tenantNm;
    }

    /**
    *
    * AvailabilityZoneを取得します。
    *
    * @return AvailabilityZone
    */
    @XmlElement(name="AvailavilityZone")
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
    *
    * AvailabilityZoneを設定
    *
    * @param AvailabilityZone
    */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
    *
    * defKeyPairを取得します。
    *
    * @return DefKeyPair
    */
    @XmlElement(name="DefKeyPair")
    public String getDefKeyPair() {
        return defKeyPair;
    }

    /**
    *
    * defKeyPairを設定
    *
    * @param DefKeyPair
    */
    public void setDefKeyPair(String defKeyPair) {
        this.defKeyPair = defKeyPair;
    }


}